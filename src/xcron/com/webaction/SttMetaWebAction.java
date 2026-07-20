package xcron.com.webaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
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


/*
 * 2020.04.13 체크사항
 * 1) 암호화 프로세스가 겹치는 경우가있어서 암호화 프로세스를 뒤로 미룰건데, 최대한 암호화 로직 영향안가게 만들었는데 어디서 겹치는 지 확인
 * 2) 개선사항이라고 써져있는 부분 변경하기
 * 
 * */
public class SttMetaWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.error("SttMetaWebAction Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam MetaRollback = null;
		
		try {
			ListParam SttInteInfo = new ListParam(new String[] {"UCID", "CON_ENT_DGN_NO", "STT_FLAG", "STT_ERR_CD", "STT_R_TIME", "TA_FLAG", "TA_R_TIME", 
					"SRC_FLAG", "SRC_R_TIME", "GRD_FLAG", "ASS_R_TIME", "ASS_USER_ID", "ASS_FLAG","DURATION", "UCID_DATE", "USER_ID", "CON_ENT_DGN_DATE","CON_P_CONT_NAME","CON_CON_MONTH_PREM","CON_CON_OLT_DEPOSIT_NM","CON_IP_ISD_NAME","CON_CON_NA_Y_CNT","CENTERCD","WORK_MONTH", "ISTP_CD", "ISTP_NM", "PROD_CD", "PROD_NM",
					"DMBO_CD", "INSUR_AMT","BASIC_PREM","CTRA_INSA_CLCD", "SC_LCLF_CD", "G_AGE", "P_AGE", "G_INSUR_AGE","P_INSUR_AGE","FILE_FULL_PATH", "BATCH_YN"});
			ListParam SttInteInfo_final = new ListParam(new String[] {"UCID", "CON_ENT_DGN_NO", "STT_FLAG", "STT_ERR_CD", "STT_R_TIME", "TA_FLAG", "TA_R_TIME", 
					"SRC_FLAG", "SRC_R_TIME", "GRD_FLAG", "ASS_R_TIME", "ASS_USER_ID", "ASS_FLAG","DURATION", "UCID_DATE", "USER_ID", "CON_ENT_DGN_DATE","CON_P_CONT_NAME","CON_CON_MONTH_PREM","CON_CON_OLT_DEPOSIT_NM","CON_IP_ISD_NAME","CON_CON_NA_Y_CNT","CENTERCD","WORK_MONTH", "ISTP_CD", "ISTP_NM", "PROD_CD", "PROD_NM",
					"DMBO_CD", "INSUR_AMT","BASIC_PREM","CTRA_INSA_CLCD", "SC_LCLF_CD", "G_AGE", "P_AGE", "G_INSUR_AGE","P_INSUR_AGE","FILE_FULL_PATH", "BATCH_YN"});
			
			
			
			ListParam PlanMastInfo = new ListParam(new String[] {"CON_ENT_DGN_NO", "CON_ENT_DGN_DATE" ,"WORK_MONTH","CON_P_CONT_NAME", "CON_CON_MONTH_PREM","CON_CON_OLT_DEPOSIT_NM","CON_IP_ISD_NAME","CON_CON_NA_Y_CNT","TM_NO","PROD_CD","PROD_NM", "DMBO_CD", "INSUR_AMT","BASIC_PREM","CTRA_INSA_CLCD" ,"G_AGE", "P_AGE","G_INSUR_AGE","P_INSUR_AGE","USER_ID","CENTERCD", "TM_UPDATE_PRE_QA", "TM_UPDATE_PRE_JUDGE_DATE"});
			
			ListParam SttIntegInfo_N =  new ListParam(new String[] {"CON_ENT_DGN_NO"});
			ListParam SttIntegInfo_Y =  new ListParam(new String[] {"TPANO"});
			ListParam SttIntegInfo_final_del =  new ListParam(new String[] {"CON_ENT_DGN_NO","UCID"});
			
			ListParam BefMetaInfo = new ListParam(new String[] {"FILEID","UCID", "SP_CODE","REC_START_DT","DURATION", "AGENT_ID","TPANO", "ORG_FILE_FULL_PATH","STT_FLAG", "STT_ERR_CD","STT_R_TIME","BATCH_YN", "VA_FLAG"});
			IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_1");
			
			//STEP1. MS_STT_BEF_META 테이블에서 80개의 설계번호 조회해오기
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_1");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			MetaRollback =  sqlResult.getListParam("msens.xcron.hansol.setsttmetawebaction_1");
			
			//ErrorLogger.debug(sqlResult.getCount());
			IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_1::sqlResult.getCount()::" + sqlResult.getCount());
			
			String v_dgn_no_group = "";
			String v_tm_no_group = "";
			if(sqlResult.getCount() > 0){
				//우선 flag를 S로 업데이트 치기
				tran.begin();
				
				//STEP2. FLAG 값 S로 업데이트 수행(수집중임을 체크하기위해)
				SQLParam sqlParam1 =  new SQLParam();  
				sqlParam1.setSqlName("msens.xcron.hansol.setsttmetawebaction_2");
				sqlParam1.addValue("SttInteInfo", sqlResult.getListParam("msens.xcron.hansol.setsttmetawebaction_1"));
				SQLServiceManager.getInstance().execute(sqlParam1, tran);
				
				tran.commit();
				
				for(int i =0; i< sqlResult.getCount(); i++){
					
					String v_tpano = sqlResult.getListParam("msens.xcron.hansol.setsttmetawebaction_1").getParam(i).getString("TPANO").trim();
					
					//spdb에서 v_tapno와 같은 설계번호가 있는 콜의 전체 갯수와 v_tpano 에 해당하는 stt flag 를 체크하여 모두 변환된 경우만 Meta 생성
					//STEP3. 해당 설계번호의 엮인 콜들에 대한 stt분석 상태를 조회(설계번호에 엮인 전체 콜수와 STT가 분석완료된 콜만 META를 생성하기 위해)
					SQLParam sqlParam2 = new SQLParam();
					sqlParam2.setSqlName("msens.xcron.hansol.setsttmetawebaction_3");
					sqlParam2.addValue("CON_ENT_DGN_NO", v_tpano);
					SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
					
					int call_cnt = 0;
					int stt_y_cnt = 0;
					//spdb에서 가져오기
					if(sqlResult2.getCount() > 0){
						call_cnt = sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").rowSize();
						for(int j =0; j< sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").rowSize(); j++){
							stt_y_cnt += Integer.parseInt(sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getValue(j, "STT_Y_CNT").toString()); //STT_FLAG = 'Y'인 건수 세기
						}
					}

					IVRLogger.info("설계번호 ::: "+v_tpano+"########stt분석 건수 : " + stt_y_cnt + " :: 콜 건수 : " + call_cnt);
					if(call_cnt == stt_y_cnt){ //STEP3-1. STT분석건수와 현재 콜 건수가 일치하는 경우
						
						String v_ced_no = "";
						v_ced_no = v_tpano.substring(5);
						//String v_fileid = sqlResult.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(i).getString("FILEID");
						
						
						for(int k =0; k< sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").rowSize(); k++){
							String v_fileid = sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("FILEID");
							SttInteInfo.addRow(new Object[] {
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("FILEID"),
									v_ced_no,
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_FLAG"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_ERR_CD"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_R_TIME"),
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("DURATION"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("UCID_DATE"),
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									"",
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("FILE_FULL_PATH"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("BATCH_YN"),
								});
							
							//IVRLogger.debug("integinfo 셍성####");
							
							/*20191217 타이밍이슈로 befmetainfo 밑에서 SttIntegInfo_Y로 다시 생성
							 * BefMetaInfo.addRow(new Object[] {
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("FILEID"),
									"",
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("SP_CODE"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("UCID_DATE"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("DURATION"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("USERID"),
									v_tpano,
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("FILE_FULL_PATH"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_FLAG"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_ERR_CD"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("STT_R_TIME"),
									sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_3").getParam(k).getString("BATCH_YN"),
									"Y"
								});*/
							
							//IVRLogger.debug("befmeta 셍성####");
							
							//STEP3-2. 연결된 콜의 텍스트가 암호화되어있을 수있으므로 암/복호화 체크
							/*SQLParam sqlParam3 = new SQLParam();
							sqlParam3.setSqlName("msens.xcron.hansol.setsttmetawebaction_11");
							sqlParam3.addValue("UCID", v_fileid);
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
							
							IVRLogger.debug("설계번호 ::: "+v_tpano+"########stt분석 건수 : " + stt_y_cnt + " :: 콜 건수 : " + call_cnt);
							if(sqlResult3.getCount() > 0){
								String enc_flag = sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_11").getParam(0).getString("ENC_FLAG");
								
								if("Y".equals(enc_flag)) setDecryption(v_fileid);
							}*/
							
							
							
	
						}
						
						//String v_ent_dgn_no = v_ced_no;
						//v_dgn_no_group = v_dgn_no_group + ",'"+ v_ent_dgn_no + "'";
						

						//if(v_dgn_no_group.length() > 0) {
							//v_dgn_no_group = v_dgn_no_group.substring(1);
							
							//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4");
							IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::ent_dgn_no::" + v_ced_no);
							
							//STEP3-3. 설계 기본 정보 조회
							SQLParam sqlParam3 = new SQLParam();
							//sqlParam2.clear();
							sqlParam3.setSqlName("msens.xcron.hansol.setsttmetawebaction_4"); //TB_INSU_PLAN_MAST, TB_PROD, TBIC063, TB_INSU_PLAN_DETAIL을 설계번호 기준으로 JOIN해서 찾기
							sqlParam3.addValue("con_ent_dgn_no", v_ced_no);
							
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
							
							IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::" + sqlResult3.getCount());
							
							if (sqlResult3.getCount() > 0) {
								
								IVRLogger.debug(sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").toString());
								for(int j=0; j<sqlResult3.getCount(); j++) {
									String v_g_age =  sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("G_AGE","");
									
									String g_age = "";
									String g_insur_age = "";
									String con_app_date = sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_APP_DATE","");
									
									IVRLogger.info("#####v_ced_no :: 청약일자 :: " +  con_app_date);
							//		IVRLogger.debug("########나이 계산 :: v_g_age ::" + v_g_age);
									//if(!"".equals(v_g_age)) g_age = getAge(v_g_age); //나이 계산해서 리턴 --> 운영계의 경우 쓰레기 값으로 넘어와서 임시 주석 처리함
									if(!"".equals(v_g_age) && !"*******".equals(v_g_age.substring(0, 7))){
										g_age = getAge(v_g_age); //나이 계산해서 리턴 --> 운영계의 경우 쓰레기 값으로 넘어와서 임시 주석 처리함
										if(!"".equals(con_app_date) && con_app_date.length() >= 8) g_insur_age = getInsurAge(v_g_age, con_app_date);
									}
									//String g_age = "";
									String v_p_age =  sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("P_AGE","");
									String p_age = "";
									String p_insur_age = "";
									
							//		IVRLogger.debug("########나이 계산 :: v_p_age ::" + v_p_age);
									//if(!"".equals(v_p_age)) p_age = getAge(v_p_age); //나이 계산해서 리턴
									if(!"".equals(v_p_age) && !"*******".equals(v_p_age.substring(0, 7))){
										p_age = getAge(v_p_age); //나이 계산해서 리턴
										if(!"".equals(con_app_date) && con_app_date.length() >= 8) p_insur_age = getInsurAge(v_p_age, con_app_date);
										//IVRLogger.info("#####v_page- " + v_p_age + "// con_app_date= " +  con_app_date + "// p_insur_age = " + p_insur_age);
									}
									
									//String p_age = "";
									String tm_no = sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("TM_NO");
									v_tm_no_group = v_tm_no_group + ",'"+ tm_no + "'";
									
									
									PlanMastInfo.addRow(new Object[] {
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_ENT_DGN_NO").trim(),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_ENT_DGN_DATE", ""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("WORK_MONTH", ""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_P_CONT_NAME", ""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_CON_MONTH_PREM", ""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_CON_OLT_DEPOSIT_NM",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_IP_ISD_NAME",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CON_CON_NA_Y_CNT",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("TM_NO",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("PROD_CD",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("PROD_NM",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("DMBO_CD",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("INSUR_AMT",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("BASIC_PREM",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CTRA_INSA_CLCD",""),
										g_age,
										p_age,
										g_insur_age,
										p_insur_age,
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("USER_ID",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("CENTERCD",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("TM_UPDATE_PRE_QA",""),
										sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_4").getParam(j).getString("TM_UPDATE_PRE_JUDGE_DATE","")
									});
								}

								
							}else{ //20191217 insu plan에 없는 경우 플래그 N으로 업데이트..
								SttIntegInfo_N.addRow(new Object[] {
										v_tpano
								});
							}

							v_dgn_no_group = "";
							v_tm_no_group = "";
						//}
						
					}else{ //STEP3-1. 콜개수와 STT분석완료 개수가 다르면 우선 META 생성에서 일단 제외
						SttIntegInfo_N.addRow(new Object[] {
								v_tpano
						});
					}	
				}
				
				//IVRLogger.debug(PlanMastInfo.rowSize());
				//IVRLogger.debug(PlanMastInfo.toString());
				
				//STEP4. META 생성을 위한 정보 세팅(설계정보 & 콜정보)
				if(PlanMastInfo.rowSize() > 0) {
					for(int i=0; i<SttInteInfo.rowSize(); i++) {
						String t_con_ent_dgn_no = (String) SttInteInfo.getValue(i, "CON_ENT_DGN_NO");
						
						for(int j=0; j<PlanMastInfo.rowSize(); j++) {
							String e_contact_id = PlanMastInfo.getValue(j, "CON_ENT_DGN_NO").toString();  
							if(t_con_ent_dgn_no.equals(e_contact_id)) {
								
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::" + t_con_ent_dgn_no);
								
								String con_ent_dgn_date = "";
								if(PlanMastInfo.getValue(j, "CON_ENT_DGN_DATE") != null && !"".equals(PlanMastInfo.getValue(j, "CON_ENT_DGN_DATE"))){
									con_ent_dgn_date = PlanMastInfo.getValue(j, "CON_ENT_DGN_DATE").toString();
								}
								
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount():: WORK_MONTH" );
								
								String work_month = "";
								if(PlanMastInfo.getValue(j, "WORK_MONTH") != null && !"".equals(PlanMastInfo.getValue(j, "WORK_MONTH"))){
									work_month = PlanMastInfo.getValue(j, "WORK_MONTH").toString();
								}
								
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount():: CON_P_CONT_NAME" );
								
								String con_p_cont_name = "";
								if(PlanMastInfo.getValue(j, "CON_P_CONT_NAME") != null && !"".equals(PlanMastInfo.getValue(j, "CON_P_CONT_NAME"))){
									con_p_cont_name = PlanMastInfo.getValue(j, "CON_P_CONT_NAME").toString();
								}
								
								String con_month_prem = "";
								if(PlanMastInfo.getValue(j, "CON_CON_MONTH_PREM") != null && !"".equals(PlanMastInfo.getValue(j, "CON_CON_MONTH_PREM"))){
									con_month_prem = PlanMastInfo.getValue(j, "CON_CON_MONTH_PREM").toString();
								}
								
								String deposit_nm = "";
								if(PlanMastInfo.getValue(j, "CON_CON_OLT_DEPOSIT_NM") != null && !"".equals(PlanMastInfo.getValue(j, "CON_CON_OLT_DEPOSIT_NM"))){
									deposit_nm = PlanMastInfo.getValue(j, "CON_CON_OLT_DEPOSIT_NM").toString();
								}
								
								String ip_isd_name = "";
								if(PlanMastInfo.getValue(j, "CON_IP_ISD_NAME") != null && !"".equals(PlanMastInfo.getValue(j, "CON_IP_ISD_NAME"))){
									ip_isd_name = PlanMastInfo.getValue(j, "CON_IP_ISD_NAME").toString();
								}
								//CON_CON_NA_Y_CNT
								String con_na_y_cnt = "";
								if(PlanMastInfo.getValue(j, "CON_CON_NA_Y_CNT") != null && !"".equals(PlanMastInfo.getValue(j, "CON_CON_NA_Y_CNT"))){
									con_na_y_cnt = PlanMastInfo.getValue(j, "CON_CON_NA_Y_CNT").toString();
								}
								
								
								String ctra_insa_clcd = "";
								String sc_lclf_cd = "";
								if(PlanMastInfo.getValue(j, "CTRA_INSA_CLCD") != null && !"".equals(PlanMastInfo.getValue(j, "CTRA_INSA_CLCD"))){
									ctra_insa_clcd =  PlanMastInfo.getValue(j, "CTRA_INSA_CLCD").toString();
									
									//IVRLogger.debug(ctra_insa_clcd + "/// 인사코드 : " + PlanMastInfo.getValue(j, "CTRA_INSA_CLCD").toString().length());
									String [] v_relation_cd = ctra_insa_clcd.split(",");
									
									Code[] code = CodeUtil.getCodes("SYS105"); //계피구분에 따른 대분류 코드 삽입
									
									for(int k = 0; k< v_relation_cd.length; k++){
										//IVRLogger.info(code.length);
										for(int l = 0; code != null && l < code.length; l++){
											if (!"Y".equalsIgnoreCase(code[l].getUseYn())) {
												continue;
											}

											if(v_relation_cd[k].equals(code[l].getCodeId())){
												sc_lclf_cd += code[l].getEtc2().replaceAll("\\|", ",");
												break;
											}
										}
									}
									
									sc_lclf_cd = sc_lclf_cd.substring(0, sc_lclf_cd.length()-1);
									
									//IVRLogger.info("######sc_lclf_cd length = " + sc_lclf_cd);
									
									
								}
								
								String prod_cd = "";
								String istp_cd = "";
								String istp_nm = ""; 
								if(PlanMastInfo.getValue(j, "PROD_CD") != null && !"".equals(PlanMastInfo.getValue(j, "PROD_CD"))){
									prod_cd = PlanMastInfo.getValue(j, "PROD_CD").toString();
									//보종코드, 보종명 매핑해주기(코드북에서 직접 조회하는 거로 수정)
									
									SQLParam sqlParam4 = new SQLParam();
									sqlParam4.clear();
									sqlParam4.setSqlName("msens.xcron.hansol.setsttmetawebaction_5");
									sqlParam4.addValue("prod_cd", prod_cd);
									
									SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
									if (sqlResult4.getCount() > 0) {
										istp_cd = sqlResult4.getListParam("msens.xcron.hansol.setsttmetawebaction_5").getParam(0).getString("INSUR_CD");
										istp_nm = sqlResult4.getListParam("msens.xcron.hansol.setsttmetawebaction_5").getParam(0).getString("INSUR_NM");
									}
									
								}
								
								String prod_nm = "";
								if(PlanMastInfo.getValue(j, "PROD_NM") != null && !"".equals(PlanMastInfo.getValue(j, "PROD_NM"))){
									prod_nm = PlanMastInfo.getValue(j, "PROD_NM").toString();
								}
								
								String dmbo_cd = "";
								if(PlanMastInfo.getValue(j, "DMBO_CD") != null && !"".equals(PlanMastInfo.getValue(j, "DMBO_CD"))){
									dmbo_cd = PlanMastInfo.getValue(j, "DMBO_CD").toString();
								}
								
								String insur_amt = "";
								if(PlanMastInfo.getValue(j, "INSUR_AMT") != null && !"".equals(PlanMastInfo.getValue(j, "INSUR_AMT"))){
									insur_amt = PlanMastInfo.getValue(j, "INSUR_AMT").toString();
								}
								
								String basic_prem = "";
								if(PlanMastInfo.getValue(j, "BASIC_PREM") != null && !"".equals(PlanMastInfo.getValue(j, "BASIC_PREM"))){
									basic_prem = PlanMastInfo.getValue(j, "BASIC_PREM").toString();
								}

								String g_age = "";
								if(PlanMastInfo.getValue(j, "G_AGE") != null && !"".equals(PlanMastInfo.getValue(j, "G_AGE"))){
									g_age = PlanMastInfo.getValue(j, "G_AGE").toString();
								}
								
								String p_age = "";
								if(PlanMastInfo.getValue(j, "P_AGE") != null && !"".equals(PlanMastInfo.getValue(j, "P_AGE"))){
									p_age = PlanMastInfo.getValue(j, "P_AGE").toString();
								}
								
								String g_insur_age = "";
								if(PlanMastInfo.getValue(j, "G_INSUR_AGE") != null && !"".equals(PlanMastInfo.getValue(j, "G_INSUR_AGE"))){
									g_insur_age = PlanMastInfo.getValue(j, "G_INSUR_AGE").toString();
								}
								
								String p_insur_age = "";
								if(PlanMastInfo.getValue(j, "P_INSUR_AGE") != null && !"".equals(PlanMastInfo.getValue(j, "P_INSUR_AGE"))){
									p_insur_age = PlanMastInfo.getValue(j, "P_INSUR_AGE").toString();
								}
								
								String user_id = "";
								if(PlanMastInfo.getValue(j, "USER_ID") != null && !"".equals(PlanMastInfo.getValue(j, "USER_ID"))){
									user_id = PlanMastInfo.getValue(j, "USER_ID").toString();
								}
								
								String centercd = "";
								if(PlanMastInfo.getValue(j, "CENTERCD") != null && !"".equals(PlanMastInfo.getValue(j, "CENTERCD"))){
									centercd = PlanMastInfo.getValue(j, "CENTERCD").toString();
								}
								
								String ass_user_id = "";
								String ass_flag = "N";
								String ass_r_date = "";
								if(PlanMastInfo.getValue(j, "TM_UPDATE_PRE_QA") != null && !"".equals(PlanMastInfo.getValue(j, "TM_UPDATE_PRE_QA"))){
									ass_user_id = PlanMastInfo.getValue(j, "TM_UPDATE_PRE_QA").toString();
									ass_flag = "Y";
									ass_r_date = PlanMastInfo.getValue(j, "TM_UPDATE_PRE_JUDGE_DATE").toString()  + "000000";									
								}
								IVRLogger.debug("ass_flag ::::::::::::::::::::::::::::::: " + ass_flag);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CON_ENT_DGN_DATE::" + con_ent_dgn_date);
								SttInteInfo.setValue(i, "CON_ENT_DGN_DATE", con_ent_dgn_date);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::WORK_MONTH::" + work_month);
								SttInteInfo.setValue(i, "WORK_MONTH", work_month);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CON_P_CONT_NAME::" + con_p_cont_name);
								SttInteInfo.setValue(i, "CON_P_CONT_NAME", con_p_cont_name);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CON_MONTH_PREM::" + con_month_prem);
								SttInteInfo.setValue(i, "CON_CON_MONTH_PREM", con_month_prem);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::DEPOSIT_NM::" + deposit_nm);
								SttInteInfo.setValue(i, "CON_CON_OLT_DEPOSIT_NM", deposit_nm);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::IP_ISD_NAME::" + ip_isd_name);
								SttInteInfo.setValue(i, "CON_IP_ISD_NAME", ip_isd_name);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CON_NA_Y_CNT::" + con_na_y_cnt);
								SttInteInfo.setValue(i, "CON_CON_NA_Y_CNT", con_na_y_cnt);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CTRA_INSA_CLCD::" + ctra_insa_clcd);
								SttInteInfo.setValue(i, "CTRA_INSA_CLCD", ctra_insa_clcd);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::SC_LCLF_CD::" + sc_lclf_cd);
								SttInteInfo.setValue(i, "SC_LCLF_CD", sc_lclf_cd);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::PROD_CD::" + prod_cd);
								SttInteInfo.setValue(i, "PROD_CD", prod_cd);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::PROD_NM::" + prod_nm);
								SttInteInfo.setValue(i, "PROD_NM", prod_nm);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::DMBO_CD::" + dmbo_cd);
								SttInteInfo.setValue(i, "DMBO_CD", dmbo_cd);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::INSUR_NM::" + insur_amt);
								SttInteInfo.setValue(i, "INSUR_AMT", insur_amt);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::BASIC_PREM::" + basic_prem);
								SttInteInfo.setValue(i, "BASIC_PREM", basic_prem);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::ISTP_CD::" + istp_cd);
								SttInteInfo.setValue(i, "ISTP_CD", istp_cd);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::ISTP_NM::" + istp_nm);
								SttInteInfo.setValue(i, "ISTP_NM", istp_nm);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::G_AGE::" + g_age);
								SttInteInfo.setValue(i, "G_AGE", g_age);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::P_PAGE::" + p_age);
								SttInteInfo.setValue(i, "P_AGE", p_age);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::G_AGE::" + g_age);
								SttInteInfo.setValue(i, "G_INSUR_AGE", g_insur_age);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::P_PAGE::" + p_age);
								SttInteInfo.setValue(i, "P_INSUR_AGE", p_insur_age);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::p_insur_age::" + p_insur_age);
								SttInteInfo.setValue(i, "USER_ID", user_id);
								//IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4::sqlResult3.getCount()::CENTER_CD::" + centercd);
								SttInteInfo.setValue(i, "CENTERCD", centercd);
								SttInteInfo.setValue(i, "ASS_USER_ID", ass_user_id);
								SttInteInfo.setValue(i, "ASS_FLAG", ass_flag);
								SttInteInfo.setValue(i, "ASS_R_TIME", ass_r_date);
								
								IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_4");
								
								break;
							}
						}
						
					}
				}
				
				IVRLogger.debug("#####INTEG "+SttInteInfo.rowSize());
				//IVRLogger.info("###stt integ info");
				//IVRLogger.info(SttInteInfo.toString());
				
				tran.begin();
			
				if(SttInteInfo.rowSize() > 0) //STEP5.타이밍이슈로 들어온 설계가 또 들어오게되면 PK 오류가 발생하기 때문에 이를 방지하기위해 한번 더 체크 
				{
					IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_6_1"); //타이밍 이슈 발생: META 넣기 바로 직전 다시 meta에 있는지 확인 후 있으면 제거 
					String ced_no = "";
					String prod_cd = "";
					String ucid = "";
					SQLParam sqlResult2 = null;
					for(int i = 0 ; i<SttInteInfo.rowSize(); i++){
						ced_no = SttInteInfo.getValue(i, "CON_ENT_DGN_NO").toString();
						prod_cd = SttInteInfo.getValue(i, "PROD_CD").toString();
						ucid = SttInteInfo.getValue(i, "UCID").toString();
						sqlResult2 = null;
						if(SttIntegInfo_N.rowSize() > 0  && SttIntegInfo_N.findRow("CON_ENT_DGN_NO",prod_cd+ced_no) > -1){ //STEP 5-1. 해당 설계가 이미 META 생성에 속하지 않으면 비교X
								IVRLogger.info("##해당 TPANO가 이미 재분석 대상이면 패스##");
								continue; //해당 TPANO가 이미 재분석 대상이면 패스
						}
						
						//STEP 5-2. 561 line 부분이 meta 테이블 생성 대상을 삭제해야 해서 삭제시켜야되는 프로세스인데, 이 작업이 수행되서 pk 오류 발생 가능성이 없으므로 meta에 넣어되는것 
						if(SttIntegInfo_final_del.rowSize() > 0 && SttIntegInfo_final_del.findRow("CON_ENT_DGN_NO",ced_no) > -1){ //테이블에 메타에 있어서 이미 지우는 과정을 했으면 관련 설계 바로 FINAL담기
							//FINAL 넣기
							IVRLogger.info("##메타에 있어서 이미 지우는 과정을 했으면 관련 설계 바로 FINAL담기##");
							
							//※ getRow 주의점 sttIntegInfo_final의 cell값이 바뀌면 sttInteInfo도 바뀐다.
							SttInteInfo_final.addRow(SttInteInfo.getRow(i));
							
							//META에 들어가는 데이터 (BEF에서 지울 데이터)
							
							SttIntegInfo_Y.addRow(new Object[] {	
									prod_cd+ced_no
							}); 
						}else{ //STEP 5-2. meta테이블 존재 여부를 체크해야 되는 경우
							//IVRLogger.info("##meta에 분석현황 조회##"+ced_no);
							sqlParam.clear();
							sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_6_1");
							sqlParam.addValue("CED_NO", ced_no);
						
							sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam);
						}
						
						//STEP 5-3. META 테이블 존재 여부를 체크
						if(sqlResult2 != null){
							
							//STEP 5-3-1. META 테이블에 존재하는 경우
							if (sqlResult2.getCount() > 0) {//메타에 있는 경우
								IVRLogger.info("##타이밍 이슈 발생:: 메타에 있는 경우##"+ced_no);
								String grd_flag =sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_6_1").getParam(0).getString("GRD_FLAG");
								String ass_flag =sqlResult2.getListParam("msens.xcron.hansol.setsttmetawebaction_6_1").getParam(0).getString("ASS_FLAG");
								
								//STEP 5-3-1-1. 분석이 완료되서 삭제해도 되는 대상이거나, 등급산정이 에러인 경우는 삭제해도 되는 것
								if("E".equals(grd_flag) || ("Y".equals(grd_flag) && "Y".equals(ass_flag))){ //에러가 났거나 분석이 완료된 설계번호가 있으면 META DELETE 후 INSERT
									IVRLogger.info("##타이밍 이슈 발생:: 분석이 완료되어 지우고 다시 insert## grd_flag >> "+grd_flag+"///ass_flag >> "+ass_flag);
									
									SttIntegInfo_final_del.addRow(new Object[]{
													SttInteInfo.getValue(i, "CON_ENT_DGN_NO").toString(),
													SttInteInfo.getValue(i, "UCID").toString()
									});
									
									//분석데이터 모두 지우기
									SQLParam sqlParam3 =  new SQLParam();  
									sqlParam3.setSqlName("msens.xcron.hansol.setsttmetawebaction_6_2");
									sqlParam3.addValue("CON_ENT_DGN_NO", ced_no);
									sqlParam3.addValue("UCID", ucid);
									
									SQLServiceManager.getInstance().execute(sqlParam3, tran);
									
									//FINAL 넣기
									SttInteInfo_final.addRow(SttInteInfo.getRow(i));
									
									//META에 들어가는 데이터 (BEF에서 지울 데이터)
									SttIntegInfo_Y.addRow(new Object[] {	
											prod_cd+ced_no
									}); 
								
								//STEP 5-3-1-2. 분석 중이므로 나중에 다시 처리하도록 대기
								}else{ //아직 분석 중이면 재수집되도록 FLAG N 처리
									IVRLogger.info("##타이밍 이슈 발생:: 아직 분석 중... 재수집되도록 FLAG N 처리##"+prod_cd+ced_no);
									SttIntegInfo_N.addRow(new Object[] {	
											prod_cd+ced_no
									}); 
								}
							}else{ //STEP 5-3-1. META 테이블에 존재하지 않음
								//META에 없는 경우 INSERT
								//IVRLogger.info("##META에 없으므로 정상 INSERT##"+ced_no);
								
								SttInteInfo_final.addRow(SttInteInfo.getRow(i));
								
								//META에 들어가는 데이터 (BEF에서 지울 데이터)
								SttIntegInfo_Y.addRow(new Object[] {	
										prod_cd+ced_no
								}); 
							}
						}
						
						// (※ 개선사항) 여기서 암호화 로직을 다시 해야됨(앞에서 하다보니, 해당 스케줄 중간에 다른 스케줄이 암호화해버리는 문제 발생)--> 마지막 STEP에서 암복호화 다시 체크 필요
						
						IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_11");
						SQLParam sqlParam3 = new SQLParam();
						sqlParam3.setSqlName("msens.xcron.hansol.setsttmetawebaction_11");
						sqlParam3.addValue("UCID", ucid);
						SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
						
						if(sqlResult3.getCount() > 0){
							String enc_flag = sqlResult3.getListParam("msens.xcron.hansol.setsttmetawebaction_11").getParam(0).getString("ENC_FLAG");
							
							if("Y".equals(enc_flag)) setDecryption(ucid);
						}
						
					}
					
					
				}
				
				IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_6");
				if(SttInteInfo_final.rowSize() > 0){
					//MS_STT_META에 데이터 넣기
					IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_6");
					//IVRLogger.debug(SttInteInfo.toString());
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_6");
					sqlParam.addValue("SttInteInfo", SttInteInfo_final);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
				}
				
				
				/*IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_9");
				String dgn_no_group = "";
				for(int i=0; i<SttInteInfo.rowSize(); i++) {
					String v_con_ent_dgn_no = SttInteInfo.getValue(i, "CON_ENT_DGN_NO").toString();
					dgn_no_group  = dgn_no_group + ",'"+ v_con_ent_dgn_no + "'";
					if(i % 501 == 500 || i == (SttInteInfo.rowSize()-1)) {
						dgn_no_group = dgn_no_group.substring(1);
						IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_9:::::DGN_NO_GROUP :: " + dgn_no_group);
						sqlParam.clear();
						sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_9"); // 설계일시 기점으로 근속년수계산한 결과 업데이트
						sqlParam.addValue("con_ent_dgn_no", dgn_no_group);
						
						SQLServiceManager.getInstance().execute(sqlParam, tran);
						
						dgn_no_group = "";
						
					}
							
				}*/
				
				
				/*IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_6"+SttInteInfo_final.toString());
				IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_7"+SttIntegInfo_N.toString());
				IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_8"+SttIntegInfo_Y.toString());
				*/
				
				//미분석 건의 VA_FLAG = 'N'으로 변경하기
				IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_7");
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_7");
				sqlParam.addValue("SttIntegInfo_N", SttIntegInfo_N); //MS_STT_BEF_META에 UPDATE 하기
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				IVRLogger.info("msens.xcron.hansol.setsttmetawebaction_8");
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.setsttmetawebaction_8");
				sqlParam.addValue("SttIntegInfo_Y",SttIntegInfo_Y); //ms_stt_bef_meta에 분석완료 DATA DELETE 하기
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				

				tran.commit();

			}

			IVRLogger.error("SttMETAWebAction Stop!!");
			
		} catch (Exception e) {
			tran.rollback();
			
			//MetaRollback
			
			if(MetaRollback != null){
				if(MetaRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setgraderesultwebaction_14
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.setsttmetawebaction_10");
						sqlParam5.addValue("MetaRollback", MetaRollback);
						SQLServiceManager.getInstance().execute(sqlParam5, tran);
						tran.commit();
						
					} catch (SQLServiceException e1) {
						tran.rollback();
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				
				}
			}
			
			e.getStackTrace();
			e.printStackTrace();
			IVRLogger.error("#####STTMETA WEBACTION message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
		
	}
	
	public String getAge(String jumin){
		String v_age = "";
		int age;
		
		//IVRLogger.debug("############jumin start:: "+jumin); 
		jumin = jumin.substring(0,6) + "-" + jumin.substring(6,jumin.length());
		/***** 주민등록번호에 - 포함인경우 ***********/
		int idx = jumin.indexOf("-");
		String birthStr = jumin.substring(0,idx);
		
		int century = Integer.parseInt(jumin.substring(idx+1, idx+2));
		
		//IVRLogger.debug("############jumin :: "+jumin);
		
		if(century == 9 || century == 0){
			birthStr = "18"+birthStr;
		}else if(century == 1 || century == 2 || century == 5 || century == 6) {
			   birthStr = "19" + birthStr;
		} else if(century == 3 || century == 4 || century == 7 || century == 8) {
		   birthStr = "20" + birthStr;
		}

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
		Date birthDay = new Date();
		
		try {
			birthDay = sdf.parse(birthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		GregorianCalendar today = new GregorianCalendar();
		GregorianCalendar birth = new GregorianCalendar();
		birth.setTime(birthDay);

		int factor = 0;
		if(today.get(Calendar.DAY_OF_YEAR)<birth.get(Calendar.DAY_OF_YEAR)) {
		   factor = -1;
		}
		 
		
		age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
		v_age = Integer.toString(age);
		
		return v_age;
		
	}
	
	public String getInsurAge(String jumin, String con_app_date){
		String insur_age = "";
		
		try{
			//IVRLogger.info("###con_app_date= " + );
			boolean bLady = false; // 오늘이 마지막날인지 체크하는 변수 
			jumin = jumin.substring(0,6) + "-" + jumin.substring(6,jumin.length());
			/***** 주민등록번호에 - 포함인경우 ***********/
			
			int idx = jumin.indexOf("-");
			
			String birthStr = jumin.substring(0,idx);
			String toDayStr = con_app_date;
			
			int century = Integer.parseInt(jumin.substring(idx+1, idx+2));
			
			//IVRLogger.debug("############jumin :: "+jumin);
			
			if(century == 9 || century == 0){
				birthStr = "18"+birthStr;
			}else if(century == 1 || century == 2 || century == 5 || century == 6) {
				   birthStr = "19" + birthStr;
			} else if(century == 3 || century == 4 || century == 7 || century == 8) {
			   birthStr = "20" + birthStr;
			}
			
			int to_year=0, to_mon=0, to_day=0;
			int bir_year=0, bir_mon=0, bir_day=0;
			int ins_year=0, ins_mon=0, ins_day=0;
			
			to_year = Integer.parseInt(toDayStr.substring(0, 4));
			to_mon = Integer.parseInt(toDayStr.substring(4, 6));
			to_day = Integer.parseInt(toDayStr.substring(6, 8));
			
			bir_year = Integer.parseInt(birthStr.substring(0, 4));
			bir_mon = Integer.parseInt(birthStr.substring(4, 6));
			bir_day = Integer.parseInt(birthStr.substring(6, 8));
			
			switch(to_mon){
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					if(to_day == 31) bLady = true;
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					if(to_day == 30) bLady = true;
					break;
				case 2:
					if(to_day == 28 || to_day == 29) bLady = true;
			}
			
			
			ins_day = to_day - bir_day;
			
			if(ins_day < 0 && bLady == false) to_mon--;
			
			ins_mon = to_mon - bir_mon;
			
			if(ins_mon < 0 ){
				to_year--;
				ins_mon = ins_mon + 12;
			}
			ins_year = to_year - bir_year;
			
			if(ins_year == -1) insur_age = Integer.toString(ins_year);
			else if(ins_mon > 5) ins_year++;
			
			insur_age = Integer.toString(ins_year);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return insur_age;
	}

	
	public boolean setDecryption(String fileid){
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		IVRLogger.info("Decryption Start");
		
		ListParam SttDecryptContent = new ListParam(new String[] {"UCID", "CONTENT"});
		ListParam SttDecryptSent = new ListParam(new String[] {"UCID", "STT_SENT_ID","STT_SENT"});
//		String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
		String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
		
		String key = "";
		
		Properties prop = new Properties();
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(PROP_DIR);
			prop.load(new java.io.BufferedInputStream(fis));
			key = prop.getProperty("cipher_key");
			//key256 = "LGUplus_1544ARS_WebService000001";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			IVRLogger.info("msens.xcron.hansol.setsttdecryptionwebaction_1");
			//모든 데이터 Decrypt 시키면서 ENC_FLAG = 'Y'로 변경
			
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.setsttdecryptionwebaction_1");
			sqlParam.addValue("ucid", fileid);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			AES256Cipher aes_cipher = AES256Cipher.getInstance(key); 
			
			if(sqlResult.getCount() > 0){
				for(int i =0; i< sqlResult.getCount(); i++){
					IVRLogger.debug(sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_1").getParam(i).getString("UCID"));
					
					String v_content = sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_1").getParam(i).getString("CONTENT");
					String enc_content = aes_cipher.decrypt(v_content);
					
					SttDecryptContent.addRow(new Object[] {
							sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_1").getParam(i).getString("UCID"),
							enc_content
					});
				}
			}
			
			sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.setsttdecryptionwebaction_2");
			sqlParam.addValue("ucid", fileid);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if(sqlResult.getCount() > 0){
				for(int i =0; i< sqlResult.getCount(); i++){
					String v_sent = sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_2").getParam(i).getString("STT_SENT");
					String enc_sent = aes_cipher.decrypt(v_sent);
					SttDecryptSent.addRow(new Object[] {
							sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_2").getParam(i).getString("UCID"),
							sqlResult.getListParam("msens.xcron.hansol.setsttdecryptionwebaction_2").getParam(i).getString("STT_SENT_ID"),
							enc_sent
					});
				}
			}
			
			tran.begin();
			
			if(SttDecryptSent.rowSize()>0){
				IVRLogger.info("msens.xcron.hansol.setsttdecryptionwebaction_4 :: count ::" + SttDecryptSent.rowSize());
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.setsttdecryptionwebaction_4");
				sqlParam.addValue("SttDecryptSent", SttDecryptSent);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
			}
			
			
			if(SttDecryptContent.rowSize()>0){
				IVRLogger.info("msens.xcron.hansol.setsttdecryptionwebaction_3 :: count ::" + SttDecryptContent.rowSize());
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.setsttdecryptionwebaction_3");
				sqlParam.addValue("SttDecryptContent", SttDecryptContent);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
			}
			
			
			
			tran.commit();
			
			
		}catch(Exception e){
			tran.rollback();
			
			e.getStackTrace();
			IVRLogger.error("#####sttmeta DecryptionWebAction Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
		
		return true;
	}
}

