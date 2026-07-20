package xcron.com.webaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class AutoAssignWebAction extends XwingWebAction {
	/*
	 * 자동배분 시간 : 평일 09:00 ~ 19:30 (토요일은 수동배분 처리 되므로 제외)
	 * 19:30분 이후에는 한 센터당 10건씩 넘어가면 다른 팀으로 넘기는 걸로 --> 계속 돌아가게 처리할것(다음 대상 소속을 기억해둘수 있도록 합시다!)
	 * 
	 * 자동배분 규칙
	 * 1순위 : 30일 이내에 동일 고객번호로 설계가 된 경우 배정받은 QC에게로 배정
	 * 		   (예외 : QC가 휴일인 경우엔 해당 QC가 속한 팀의 우선순위 높은 QC에게 배정되도록 처리) -> 해당 팀이 근무를 하지 않아도 배정함
	 * 2순위 : 미처리 건수가 적은 QC로 배정(전영업일/당일의 배분일자 기준)
	 * 3순위 : 처리 건수가 적은 QC로 배정(당일/전일 배분일자 and 심사일자가 오늘 and 심사상태가 uw통과심사, uw보완qc심사가 아닌 경우)
	 * 4순위 : 총 배정 건수가 적은 QC로 배정(당일 배분일자 기준)
	 * 5순위 : 배정건수가 동일한 경우, 현재 배당할 설계의 grade 건수가 적은 상담사에게로 배정(당일 배분일자 기준)
	 * 6순위 : 사번순 
	*/
	
	/*
	 * 수정사항
	 * 1) 현재 배분 추이 보려고 전체 다 보이게 해놨는데,  반영시에는 msens.xcron.hansol.setautoassignwebaction_12_bak로 반영
	 * 2) 
	 * */
	private static final long serialVersionUID = 1L;

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.info("#################AutoAssignWebAction Start!!#########################");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam AutoRollback = null;
		
		try {
			ListParam alloc_List = new ListParam(new String[] {"CON_ENT_DGN_NO", "ASS_USER_ID", "ASS_R_TIME"});
			
			// STEP1. 배분 대상자 추출하기
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.setautoassignwebaction_1");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			AutoRollback = sqlResult.getListParam("msens.xcron.hansol.setautoassignwebaction_1");
			
			IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_1::sqlResult.getCount()::" + sqlResult.getCount());
			
			if(sqlResult.getCount() > 0){
				
				tran.begin();
				
				IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_2 :: COUNT :: " + AutoRollback.rowSize());
				
				// STEP 2. ASS_FLAG = 'S'로 업데이트
				SQLParam sqlParam8 =  new SQLParam();  
				sqlParam8.setSqlName("msens.xcron.hansol.setautoassignwebaction_2");
				sqlParam8.addValue("AutoAssign", sqlResult.getListParam("msens.xcron.hansol.setautoassignwebaction_1")); 
				SQLServiceManager.getInstance().execute(sqlParam8, tran);
				
				tran.commit();
				
				for(int i =0; i< sqlResult.getCount(); i++){
					
					alloc_List.clear(); // 설계번호별 자동배분 정보 초기화
					
					String auto_yn = sqlResult.getListParam("msens.xcron.hansol.setautoassignwebaction_1").getParam(i).getString("AUTO_YN");
					String ced_no = sqlResult.getListParam("msens.xcron.hansol.setautoassignwebaction_1").getParam(i).getString("CON_ENT_DGN_NO");
					String v_grade = sqlResult.getListParam("msens.xcron.hansol.setautoassignwebaction_1").getParam(i).getString("GRADE");
					
					boolean v_auto_yn = true;
					
					// STEP3. 해당 설계번호가 이미 AGIEN에서 배정되었는지 체크한다.
					IVRLogger.info("auto_yn :: "+auto_yn+" :: msens.xcron.hansol.setautoassignwebaction_3 :: con_ent_dgn_no :: " + ced_no);

					SQLParam sqlParam2 =  new SQLParam();
					sqlParam2.setSqlName("msens.xcron.hansol.setautoassignwebaction_3");
					sqlParam2.addValue("CON_ENT_DGN_NO", ced_no);
					SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
					
					String ass_user_id = "";
					String ass_datetime = "";

					if(sqlResult2.getCount() > 0){

						ass_user_id = sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_3").getParam(0).getString("TM_UPDATE_PRE_QA", "");
						
						if(!"".equals(ass_user_id)){
							
							IVRLogger.info("########AIGEN 내 배정정보 존재 :: CON_ENT_DGN_NO :: " + ced_no);
							
							ass_datetime = sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_3").getParam(0).getString("TM_UPDATE_PRE_JUDGE_DATE", "");
							
							v_auto_yn = false; 
							
							alloc_List.addRow(new Object[] {
								ced_no,
								ass_user_id,
								ass_datetime
							});
						}
					}
				
					// AIGEN에서 배분을 수행한 이력이 없는경우
					if("".equals(ass_user_id) && "".equals(ass_datetime)){
						// STEP4-1. 토,일, 휴일인 경우는 자동배분을 수행하지 않는다. --> aigen에서 수동배분을 수행하므로 ass_flag = 'Y'로 만들고 패스한다.
						if("N".equals(auto_yn)){
							v_auto_yn = false; 
							
							alloc_List.addRow(new Object[] {
								ced_no,
								"",
								"",
							});
							
						}else if("Y".equals(auto_yn)){
							// STEP4-2 자동 배분 우선순위에 따라 배정 수행
							/*
							 * 1순위 : 30일 이내 동일 고객은 이전 검수 담당자인 QC에게 배분
							 * -> 만약 해당 QC가 휴일이면, 해당 팀에서 우선순위가 가장 높은 QC에게 배분
							 * 2순위 : 미처리건수(UW통과QC심사,UW보완QC심사인 건수)가 적은 QC(배정일자가 전일,당일)
							 * 3순위 : 처리건수(UW통과QC심사,UW보완QC심사가 아닌 건수)가 적은 QC(배정일자가 전일,당일이면서 심사일자가 당일인 건수)
							 * 4순위 : 배정건수(배정일자가 당일)
							 * 5순위 : 해당 GRADE 배정 건수가 적은 QC(배정일자가 당일)
							 * 6순위 : 사번
							 * 
							 * 마지막 근무 종료 이후에는 해당 로직을 수행하면서 각 팀당 배정 건수가 10건이 넘으면 순환 하도록 처리
							 * */
							
							IVRLogger.info("#############msens.xcron.hansol.setautoassignwebaction_4 :: ced_no :: "+ ced_no);
							
							// STEP 4-2-1. 동일 고객 번호로 30일 이내에 검수가 QC가 있는 지 확인
							sqlParam2.clear();
							sqlParam2.setSqlName("msens.xcron.hansol.setautoassignwebaction_4");
							sqlParam2.addValue("CON_ENT_DGN_NO", ced_no);
							sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
							
							boolean qc_yn = false; // 배분된 QC가 있는지 체크
							String qc_use_yn = ""; // QC 근무 여부
							String qc_org_cd = ""; // QC 배분 조직
							
							IVRLogger.info("#############msens.xcron.hansol.setautoassignwebaction_4 :: count :: "+ sqlResult2.getCount());
							
							if(sqlResult2.getCount() > 0){
								
								IVRLogger.debug("###### 30일 이내 동일 고객번호로 설계한 이력 존재함 #########");
								qc_use_yn = sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_4").getParam(0).getString("USE_YN");
								String bef_qc_id = sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_4").getParam(0).getString("TM_UPDATE_PRE_QA","");

								qc_yn = true;
								
								IVRLogger.info("#####30일 이내 동일 고객 번호 설계이력 존재 :: CON_ENT_DGN_NO :: " + ced_no + " :: QC_USER_ID :: " + bef_qc_id + " :: QC_WORK_YN :: " + qc_use_yn);
								
								if("Y".equals(qc_use_yn) && !"".equals(bef_qc_id)){ // 30일 이전에 검수를  진행한 QC가 휴일이 아닌 경우
									
									IVRLogger.debug("###### 30일 이내 동일 고객번호로 설계한 이력 존재하지만, QC 휴무아님#########");
									alloc_List.addRow(new Object[] {
											ced_no,
											bef_qc_id,
											""
									});
									
								}else{ // 30일 이전에 검수를  진행한 QC가 휴일인 경우 -> 해당 QC 팀의 우선순위 높은 상담사에게 배분되도록 처리
									//IVRLogger.debug("###### 30일 이내 동일 고객번호로 설계한 이력 존재하지만, QC 휴무임#########");
									qc_org_cd = "," + sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_4").getParam(0).getString("TREATYDEPTCD","");
									
									IVRLogger.debug("###### 30일 이내 동일 고객번호로 설계한 이력 존재하지만, QC휴무 중 :: 해당 QC의 조직명 :: " +qc_org_cd + "#########");
								}

							}
							
							
							
							// STEP 6. 전영업일과 당일 날짜 구해오기(미처리건수/처리건수 계산을 위해)
							IVRLogger.info("##############msens.xcron.hansol.setautoassignwebaction_11");
							
							Calendar cal = new GregorianCalendar();
							cal.add(Calendar.DATE, -1);
							SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
							
							String now_work_date = format.format(cal.getTime());
							String bef_work_date = DateUtil.getCurrentDate();
							
							SQLParam sqlParam6 =  new SQLParam();
							sqlParam6.setSqlName("msens.xcron.hansol.setautoassignwebaction_11");
							SQLParam sqlResult6 = SQLServiceManager.getInstance().execute(sqlParam6);
							
							if(sqlResult6.getCount() > 0){
								bef_work_date = sqlResult6.getListParam("msens.xcron.hansol.setautoassignwebaction_11").getParam(0).getString("BEF_WORK_DATE",format.format(cal.getTime()));
								now_work_date = sqlResult6.getListParam("msens.xcron.hansol.setautoassignwebaction_11").getParam(0).getString("NOW_WORK_DATE", DateUtil.getCurrentDate());
							}
						
							// ※ 테스트를 위한 임시 데이터
							//bef_work_date = "20180101";
							//now_work_date = "20181106";
							
							
							IVRLogger.info("######전영업일 :: " + bef_work_date + " 당일 영업일 :: " + now_work_date);
							
							// STEP 4-2-2. 동일 고객 번호로 30일 이내에 검수한 QC가 없는 경우
							if(!qc_yn && "".equals(qc_use_yn)){
							
								IVRLogger.debug("#############30일내 설계한 이력 없으므로 우선순위 조회 수행");
								
								IVRLogger.info("#############msens.xcron.hansol.setautoassignwebaction_5 :: ced_no  :: "+ ced_no);
								
								// STEP 5. INSU_PLAN_MAST TABLE에서 관리자영업코드 조회
								sqlParam2.clear();
								sqlParam2.setSqlName("msens.xcron.hansol.setautoassignwebaction_5");
								sqlParam2.addValue("CON_ENT_DGN_NO", ced_no);
								sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
								
								IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_5 :: COUNT ::" + sqlResult2.getCount()); 
								
								if(sqlResult2.getCount() > 0){
									String org_cd = sqlResult2.getListParam("msens.xcron.hansol.setautoassignwebaction_5").getParam(0).getString("ORG_CD");
									
									IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_6 :: 센터코드 :: " + org_cd);
									
									// STEP 6. 해당 관리점을 담당하는 QC 그룹들을 조회해오기(QC센터의 업무 시간도 같이 체크함)
									SQLParam sqlParam3 =  new SQLParam();
									sqlParam3.setSqlName("msens.xcron.hansol.setautoassignwebaction_6");
									sqlParam3.addValue("ORG_CD", org_cd);
									sqlParam3.addValue("WORK_YN", "Y");
									SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
									
									IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_6 :: COUNT ::" + sqlResult3.getCount());
									
									if(sqlResult3.getCount() > 0){ // 현재 업무 중인 센터가 존재하는 경우에는 해당 조직 정보를 바로 넣는다.
										
										// 평일 업무가 시작되면 야간 근무 FLAG를 초기 상태로 변경해둔다.
										tran.begin();
										
										IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_7");
										SQLParam sqlParam5 =  new SQLParam();
										sqlParam5.setSqlName("msens.xcron.hansol.setautoassignwebaction_7");
										SQLServiceManager.getInstance().execute(sqlParam5, tran);
										
										tran.commit();
										
										ListParam ass_qcList = sqlResult3.getListParam("msens.xcron.hansol.setautoassignwebaction_6");
										
										for(int k=0; k< ass_qcList.rowSize(); k++){
											qc_org_cd += ",'" + ass_qcList.getValue(k, "QC_ORG_CD").toString()+"'";
										}
										
										
										IVRLogger.info("##########업무 중 배정 QC 센터 :: " + qc_org_cd);
										
									}else{
										
										ErrorLogger.debug("##########모든 센터의 업무가 종료되었습니다####################");
										IVRLogger.info("################모든 QC센터 업무 종료#########################");
										// 배분해야할 센터가 모두 업무가 종료된 경우 -> 야간에 배정할 센터 정보를 조회해 온다.
										
										
										//int max_ass_cnt = 10; // 최대 배정 건수
										
										//QC센터 중에 야간배정 flag = 'Y'인 대상을 조회한다.
										sqlParam3.clear();   
										sqlParam3.setSqlName("msens.xcron.hansol.setautoassignwebaction_6");
										sqlParam3.addValue("ORG_CD", org_cd);
										//sqlParam3.addValue("WORK_YN", "N");
										sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
										
										
										if(sqlResult3.getCount() > 0){
											
											int qc_org_cnt = Integer.parseInt(sqlResult3.getListParam("msens.xcron.hansol.setautoassignwebaction_6").getParam(0).getString("QC_CNT"));
											
											if(qc_org_cnt == 1){ // 애초에 센터가 하나인 경우에는 체크하지 않고 바로 정보를 넣어준다.
												ErrorLogger.debug("#####센터가 하나인 경우에는 체크하지 않는다!");
												qc_org_cd += ",'" + sqlResult3.getListParam("msens.xcron.hansol.setautoassignwebaction_6").getParam(0).getString("QC_ORG_CD")+"'";
											}else{
												String work_end_qc_id = ""; // 업무 종료 후 배정자
												Code[] code = CodeUtil.getCodes("SYS142"); // 야간의 최대 배정 건수를 조회
												
												for (int j = 0; code != null && j < code.length; j++) { 
													if (!"Y".equalsIgnoreCase(code[j].getUseYn())) {
														continue;
													}
												
													if("01".equals(code[j].getCodeId())) work_end_qc_id  = code[j].getEtc1();
												}
												
												alloc_List.addRow(new Object[] {
														ced_no,
														work_end_qc_id,
														"",
												});
												
												IVRLogger.info("###############"+org_cd+"를 담당하는 QC센터 업무 종료 :: 배정 qc :: "+work_end_qc_id+"#########################");
												
											}
										}
										IVRLogger.info("##########업무 종료 후 배정 QC 센터 :: " + qc_org_cd); 
									} 
								}else{
									// 혹시나 tm에 관리자영업 코드가 없는 경우가 존재한다면..
									// 배분 로직 돌리지 않고, 그냥 우리 db에 공백으로 넣는다
									v_auto_yn = false;
									
									IVRLogger.info("########AIGEN 내에 영업점 코드가 존재하지않음############### :: con_ent_dgn_no :: " + ced_no );
									
									alloc_List.addRow(new Object[] {
										ced_no,
										"",
										"",
									});
								}
							}
							
							// 위에서 조회한 QC팀을 기준으로 우선순위 2~6순위를 체크
							if(qc_org_cd.length() > 0){
								
								qc_org_cd = qc_org_cd.substring(1);
			
								SQLParam sqlParam4 =  new SQLParam();
								SQLParam sqlResult4 = null;
	
								
								IVRLogger.info("##############msens.xcron.hansol.setautoassignwebaction_12 :: 배정 QC 팀 :: " + qc_org_cd);
			
								// STEP 7. 상단에서 구한 QC리스트 대상자들의 중 현재 시점에 배분 개수가 가장 적은 상담사를 조회한다
								sqlParam4.clear();
								sqlParam4.setSqlName("msens.xcron.hansol.setautoassignwebaction_12");
								sqlParam4.addValue("CON_ENT_DGN_NO", ced_no);
								sqlParam4.addValue("QC_ORG_CD", qc_org_cd);
								sqlParam4.addValue("GRADE", v_grade);
								sqlParam4.addValue("BEF_WORK_DATE", bef_work_date);
								sqlParam4.addValue("NOW_WORK_DATE", now_work_date);
								sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
								
								ErrorLogger.debug("###########QC 우선순위 배정자 결과 :: ");
								
								IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_12 :: COUNT :: " + sqlResult4.getCount() + " :: ced_no " + ced_no); 
								// 해당부분은 나중에 주석을 제거할것
								IVRLogger.info(sqlResult4.getListParam("msens.xcron.hansol.setautoassignwebaction_12").toString());
								
								if(sqlResult4.getCount() > 0){
									String final_ass_user_id = sqlResult4.getListParam("msens.xcron.hansol.setautoassignwebaction_12").getParam(0).getString("USERID");
									IVRLogger.info("#######QC 우선순위 배정 결과 :: USERID :: " + final_ass_user_id);
									
									alloc_List.addRow(new Object[] {
											ced_no,
											final_ass_user_id,
											""
									});
									
								}
							}
						}
					}
					
					
					if(alloc_List.rowSize() > 0){
						
						IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_13,14 :: ced_no :: " + alloc_List.getValue(0, "CON_ENT_DGN_NO"));
						
						tran.begin();
						
						SQLParam sqlParam9 = new SQLParam();
						
						if(v_auto_yn){ // AIGEN에서 배정이력이 없을 때만 tm에 업데이트를 수행한다.
							
							IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_13");
							
							sqlParam9.clear();
							sqlParam9.setSqlName("msens.xcron.hansol.setautoassignwebaction_13");
							sqlParam9.addValue("alloc_List", alloc_List);
								
							SQLServiceManager.getInstance().execute(sqlParam9, tran);
						}
						
						
						// META 테이블에 배정 정보 저장
						sqlParam9.clear();
						sqlParam9.setSqlName("msens.xcron.hansol.setautoassignwebaction_14");
						sqlParam9.addValue("alloc_List", alloc_List);
							
						SQLServiceManager.getInstance().execute(sqlParam9, tran);

						tran.commit();
					}else{
						//throw new WebActionException("fail", "배정할 QC가 존재하지 않습니다. :: 설계번호 :: " + ced_no);
						//※ 30일 이내 동일 계약자를 평가한 QC의 부서 이동이 된 경우, 오류가 발생하여 우선 아래 형태로 반영해논상태(2020-02-04)
						String work_end_qc_id = ""; // 업무 종료 후 배정자
						Code[] code = CodeUtil.getCodes("SYS142"); // 야간의 최대 배정 건수를 조회
						
						for (int j = 0; code != null && j < code.length; j++) { 
							if (!"Y".equalsIgnoreCase(code[j].getUseYn())) {
								continue;
							}
						
							if("01".equals(code[j].getCodeId())) work_end_qc_id  = code[j].getEtc1();
						}
						
						alloc_List.addRow(new Object[] {
								ced_no,
								work_end_qc_id,
								"",
						});
						
						
						tran.begin();
						
						SQLParam sqlParam9 = new SQLParam();
						
						if(v_auto_yn){ // AIGEN에서 배정이력이 없을 때만 tm에 업데이트를 수행한다.
							
							IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_13");
							
							sqlParam9.clear();
							sqlParam9.setSqlName("msens.xcron.hansol.setautoassignwebaction_13");
							sqlParam9.addValue("alloc_List", alloc_List);
								
							SQLServiceManager.getInstance().execute(sqlParam9, tran);
						}
						
						
						// META 테이블에 배정 정보 저장
						sqlParam9.clear();
						sqlParam9.setSqlName("msens.xcron.hansol.setautoassignwebaction_14");
						sqlParam9.addValue("alloc_List", alloc_List);
							
						SQLServiceManager.getInstance().execute(sqlParam9, tran);

						tran.commit();
					}
				}
			}
			
			IVRLogger.info("AutoAssignWebAction Stop!!");
			
		} catch (Exception e) {
			tran.rollback();
			e.printStackTrace();
			IVRLogger.info("AutoAssignWebAction Exception ##########");
			IVRLogger.info("AutoAssignWebAction Exception REASON =" + e.getMessage());
			
			
			if(AutoRollback != null){
				if(AutoRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						IVRLogger.info("msens.xcron.hansol.setautoassignwebaction_15");
						
						//에러난 경우 ass_flag = 'N'으로 다시 변경
						SQLParam sqlParam10 =  new SQLParam();  
						sqlParam10.setSqlName("msens.xcron.hansol.setautoassignwebaction_15");
						sqlParam10.addValue("AutoRollback", AutoRollback);
						
						SQLServiceManager.getInstance().execute(sqlParam10, tran);
						
						tran.commit();
						
					} catch (SQLServiceException e1) {
						// TODO Auto-generated catch block
						tran.rollback();
						e1.printStackTrace();
					}	
				}
			
			}
			
			e.getStackTrace();
			//IVRLogger.error("#####AutoAssignWebAction Error Message = "+e.getMessage());
		}
		
	}
	
}

