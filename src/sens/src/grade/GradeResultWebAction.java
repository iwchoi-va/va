package sens.src.grade;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

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
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;


public class GradeResultWebAction extends XwingWebAction {
	/**
	 * grade 등급 계산  
	 */
	private static final long serialVersionUID = 1L;
	private ListParam GradeRslt = new ListParam(new String[] {"CON_ENT_DGN_NO", "VERSION", "GRADE", "GRADE_SUM", "VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE","FOC_SCORE", "MEN_SCORE"}); //grade_sum 테이블에 넣는 결과 
	private ListParam Grade = new ListParam(new String[] {"VERSION", "GRADE", "GRADE_SCORE"}); //grade 항목 데이터
	private ListParam GradeWeight = new ListParam(new String[] {"VERSION", "ITEM_CD", "WEIGHT"}); //grade 산정 항목 가중치 데이터
	private ListParam GradeDetail = new ListParam(new String[] {"VERSION", "ITEM_CD", "ITEM_CNT1", "ITEM_CNT2", "ITEM_SCORE", "REG_COMMENT"}); //상세 항목 데이터
	private ListParam GradeSubject = new ListParam(new String[] {"CON_EN_DGN_NO", "USER_ID", "WORK_MONTH"}); // grade 산정 대상자 데이터
	private ListParam SubjectScore = null; ////각 설계번호별 점수 산정 결과 데이터
	private ListParam GradeTmp = new ListParam(new String[] {"CON_EN_DGN_NO","VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE","FOC_SCORE", "MEN_SCORE"}); //각 항목별 점수 산정 데이터 --> 검증에 필요한 데이터이지, db에는 넣지 않음
	private ListParam GradeRollback = null;
	//private String v_ced_no = "";
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.info("GradeResultWebAction Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
				
		try {
			SQLParam sqlParam1 = new SQLParam();
			
			//Grade 항목별 가중치 구해오기
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_3");
			sqlParam1.clear();
			sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_3");
			SQLParam  sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_3::Count::" + sqlResult1.getCount());
			
			boolean ta_yn = true;
			boolean src_yn = true;
			
			if(sqlResult1.getCount() > 0){
				for(int i=0; i<sqlResult1.getCount(); i++) {
					String item_cd = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("ITEM_CD");
					String weight = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("WEIGHT"); 
					GradeWeight.addRow(new Object[] {
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("VERSION"),
							item_cd,
							weight
					});
					
					if("01".equals(item_cd)){
						if(Integer.parseInt(weight) > 0) ta_yn = true;
						else ta_yn = false;
					}
					
					if("02".equals(item_cd)){
						if(Integer.parseInt(weight) > 0) src_yn = true;
						else src_yn = false;
					}
				}
			}
			
			// TA&금칙어 분석 대상에 따라 체크해보기 
			String v_sql = "";
			
			if(ta_yn && src_yn) v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_1";
			else if(ta_yn && !src_yn) v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_2";
			else if(!ta_yn && src_yn) v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_3";

			//v_ced_no = "";
			//Grade 대상자 조회해오기
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_1");
			
			sqlParam1.clear();
			sqlParam1.setSqlName(v_sql);
			sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_1::Count::" + sqlResult1.getCount());
			
			if(sqlResult1.getCount() > 0){
				
				GradeRollback = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1");
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam4 =  new SQLParam();  
				sqlParam4.setSqlName("msens.xcron.hansol.setgraderesultwebaction_13");
				sqlParam4.addValue("GradeRslt", sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1")); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
				tran.commit();
				
				for(int i=0; i<sqlResult1.getCount(); i++) {
					GradeSubject.addRow(new Object[] {
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("CON_EN_DGN_NO"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("USER_ID"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("WORK_MONTH")
					});
					
					//v_ced_no = v_ced_no + "'" + sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("CON_ENT_DGN_NO") + "',";
				}
				
				//v_ced_no = v_ced_no.substring(0, v_ced_no.length()-1);
			}
			
			//Grade 해당 점수 조회해오기
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_2");
			sqlParam1.clear();
			sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_2");
			sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_2::Count::" + sqlResult1.getCount());
			if(sqlResult1.getCount() > 0){
				for(int i=0; i<sqlResult1.getCount(); i++) {
					Grade.addRow(new Object[] {
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("VERSION"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("GRADE"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("GRADE_SCORE")
					});
				}
			}
			
			
			
			//Grade 항목별 항목 기준치(기준점수) 구해오기
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_4");
			sqlParam1.clear();
			sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_4");
			sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_4::Count::" + sqlResult1.getCount());
			
			if(sqlResult1.getCount() > 0){
				for(int i=0; i<sqlResult1.getCount(); i++) {
					GradeDetail.addRow(new Object[] {
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("VERSION"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CD"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CNT1"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CNT2"),
							sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_SCORE"),
							""
					});
				}
			}
			//GradeRollback
			
			if(GradeRollback != null){
				for(int z=0; z< GradeRollback.rowSize(); z++){
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5");
	
					
					String p_ced_no = GradeRollback.getValue(z, "CON_ENT_DGN_NO").toString();
					
					IVRLogger.debug(z + "번째 ced_no = " + p_ced_no);
					
					sqlParam1.clear();
					sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_5");
					sqlParam1.addValue("v_ced_no", p_ced_no);
					sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
					
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5::CON_ENT_DGN_NO::" + p_ced_no);
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5::Count::" + sqlResult1.getCount());
					
					SubjectScore = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_5");
					
					//Grade 정보를 받아올 코드북 조회해오기
					Code[] code = CodeUtil.getCodes("VSS170");
					
					for(int i =0; i< SubjectScore.rowSize(); i++){
						String ced_no = SubjectScore.getValue(i, "CON_ENT_DGN_NO").toString();
						double vrs_score_res = 0;
						double ban_score_res = 0;
						double bef_score_res = 0;
						double sec_score_res = 0;
						double foc_score_res = 0;
						double men_score_res = 0;
						double tot_score = 0;
						
						GradeTmp.addRow(new Object[] { //점수 확인용으로 넣은 것이므로 나중에 삭제해도 무방
							ced_no,
							"",
							"",
							"",
							"",
							"",
							""
						});
						
						GradeRslt.addRow(new Object[] {
								ced_no,
								Grade.getValue(0, "VERSION"),
								"",
								"",
								"",
								"",
								"",
								"",
								"",
								""
						});
						
						
						IVRLogger.debug("###########설계번호 - " + ced_no);
						for (int j = 0; code != null && j < code.length; j++) { //각 항목별 점수 계산하는 로직
							if (!"Y".equalsIgnoreCase(code[j].getUseYn()) || !"Y".equalsIgnoreCase(code[j].getEtc2())) {
								continue;
							}
							
							if("01".equals(code[j].getCodeId())){ // VRS 준수결과
								double vrs_score = Double.parseDouble(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
								int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
								double weight = 0;
								if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
								
								GradeTmp.setValue(GradeTmp.rowSize()-1, "VRS_SCORE",vrs_score);
								vrs_score_res = Math.round(vrs_score*weight/100); //소수점 두자리까지 자르기
								GradeRslt.setValue(GradeRslt.rowSize()-1, "VRS_SCORE",  vrs_score_res);
								
								tot_score += vrs_score_res;
								
								IVRLogger.debug("vrs준수결과 - " + vrs_score_res + "// 총 점수 - "+ tot_score);
								
							}else if("03".equals(code[j].getCodeId())){ //전월평가점수 쪽은 아직 스키마를 못받아서 개발이 불가능함 -> 추후 개발을 해야됨
								
								double bef_score = Double.parseDouble(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
								int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
								double weight = 0;
								if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
								
								GradeTmp.setValue(GradeTmp.rowSize()-1, "BEF_SCORE", bef_score);
								bef_score_res =  Math.round((bef_score*weight/100));
								
								GradeRslt.setValue(GradeRslt.rowSize()-1, "BEF_SCORE", bef_score_res);
								tot_score += bef_score_res;
								
								IVRLogger.debug("전월평가점수 - " + bef_score_res + "// 총 점수 - "+ tot_score);
								
							}else if("04".equals(code[j].getCodeId())){ //개인정보처리항목
								double secret_score = Double.parseDouble(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
								
								int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
								double weight = 0;
								if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
								
								GradeTmp.setValue(GradeTmp.rowSize()-1, "SEC_SCORE",secret_score);
								sec_score_res = Math.round((secret_score*weight/100)); //소수점 두자리까지 자르기
								GradeRslt.setValue(GradeRslt.rowSize()-1, "SEC_SCORE",  sec_score_res);
								tot_score += sec_score_res;
								
								IVRLogger.debug("개인정보처리항목 점수 - " + sec_score_res + "// 총 점수 - "+ tot_score);
								
							}else if("05".equals(code[j].getCodeId())){ //집중관리항목
								double focus_score = Double.parseDouble(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
								
								int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
								double weight = 0;
								if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
								
								GradeTmp.setValue(GradeTmp.rowSize()-1, "FOC_SCORE",focus_score);
								foc_score_res = Math.round((focus_score*weight/100)); //소수점 두자리까지 자르기
								GradeRslt.setValue(GradeRslt.rowSize()-1, "FOC_SCORE",  foc_score_res);
								tot_score += foc_score_res;
	
								IVRLogger.debug("집중관리항목 점수 - " + foc_score_res + "// 총 점수 - "+ tot_score);
							}else{
								for(int k =0; k < GradeDetail.rowSize(); k++){ // 항목별 상세 기준이 있는 항목들(금칙어, 개인정보처리항목, 집중관리항목, 필수멘트
									if("02".equals(GradeDetail.getValue(k, "ITEM_CD")) && "02".equals(code[j].getCodeId())){ //금칙어
										int ban_cnt = Integer.parseInt(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
										String comp_cnt1 = GradeDetail.getValue(k, "ITEM_CNT1").toString();
										String comp_cnt2 = GradeDetail.getValue(k, "ITEM_CNT2").toString();
										
										double ban_score = 0;
										
											if(!"".equals(comp_cnt2)){ 
												if(ban_cnt >= Integer.parseInt(comp_cnt1) && ban_cnt < Integer.parseInt(comp_cnt2)){
													ban_score = Double.parseDouble(GradeDetail.getValue(k, "ITEM_SCORE").toString());
													GradeTmp.setValue(GradeTmp.rowSize()-1, "BAN_SCORE", GradeDetail.getValue(k, "ITEM_SCORE"));	
													
													int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
													double weight = 0;
													if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
													
													ban_score_res = Math.round((ban_score*weight/100));
													GradeRslt.setValue(GradeRslt.rowSize()-1, "BAN_SCORE",  ban_score_res);
													
													tot_score += ban_score_res;
													
													IVRLogger.debug("금칙어 점수 - " + ban_score_res + "// 총 점수 - "+ tot_score);
												}
											}else{
												if(ban_cnt >= Integer.parseInt(comp_cnt1)){
													ban_score = Double.parseDouble(GradeDetail.getValue(k, "ITEM_SCORE").toString());
													GradeTmp.setValue(GradeTmp.rowSize()-1, "BAN_SCORE", GradeDetail.getValue(k, "ITEM_SCORE"));
													
													int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
													double weight = 0;
													if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
													
													ban_score_res = Math.round((ban_score*weight/100));
													GradeRslt.setValue(GradeRslt.rowSize()-1, "BAN_SCORE",  ban_score_res);
													
													tot_score += ban_score_res;
													
													IVRLogger.debug("금칙어 점수 - " + ban_score_res + "// 총 점수 - "+ tot_score);
												}
											}
				
									}else if("06".equals(GradeDetail.getValue(k, "ITEM_CD")) && "06".equals(code[j].getCodeId())){ //필수멘트
										int ment_cnt = Integer.parseInt(SubjectScore.getValue(i, "SCORE_"+code[j].getCodeId()).toString());
										
										String comp_cnt1 = GradeDetail.getValue(k, "ITEM_CNT1").toString();
										String comp_cnt2 = GradeDetail.getValue(k, "ITEM_CNT2").toString();
										
										double men_score = 0;
	
											if(!"".equals(comp_cnt2)){
												if(ment_cnt >= Integer.parseInt(comp_cnt1) && ment_cnt < Integer.parseInt(comp_cnt2)){
													men_score = Double.parseDouble(GradeDetail.getValue(k, "ITEM_SCORE").toString());
													GradeTmp.setValue(GradeTmp.rowSize()-1, "MEN_SCORE", GradeDetail.getValue(k, "ITEM_SCORE"));
													
													int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
													double weight = 0;
													if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
													men_score_res = Math.round((men_score*weight/100));
													GradeRslt.setValue(GradeRslt.rowSize()-1, "MEN_SCORE",  men_score_res);
													
													tot_score += men_score_res;
													
													IVRLogger.debug("필수멘트 점수 - " + men_score_res + "// 총 점수 - "+ tot_score);	
												}
											}else{
												if(ment_cnt >= Integer.parseInt(comp_cnt1)){
													men_score = Double.parseDouble(GradeDetail.getValue(k, "ITEM_SCORE").toString());
													GradeTmp.setValue(GradeTmp.rowSize()-1, "MEN_SCORE", GradeDetail.getValue(k, "ITEM_SCORE"));
													
													int idx = GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
													double weight = 0;
													if(idx > -1) weight = Double.parseDouble(GradeWeight.getValue(idx, "WEIGHT").toString());
													men_score_res = Math.round((men_score*weight/100));
													GradeRslt.setValue(GradeRslt.rowSize()-1, "MEN_SCORE",  men_score_res);
													
													tot_score += men_score_res;
													
													IVRLogger.debug("필수멘트 점수 - " + men_score_res + "// 총 점수 - "+ tot_score);	
												}
											}
											
										
									}
								}
	
							}
						}
						
						GradeRslt.setValue(GradeRslt.rowSize()-1, "GRADE_SUM",  Math.round(tot_score)); //GRADE 산정 최종 점수 저장
	
					}
				}
				
				if(GradeRslt.rowSize() > 0){
					for(int i =0; i< GradeRslt.rowSize(); i++){ 
						double grade_sum = Double.parseDouble(GradeRslt.getValue(i, "GRADE_SUM").toString());
	
						for(int j =0; j < Grade.rowSize();j++){
							double comp_score = Double.parseDouble(Grade.getValue(j, "GRADE_SCORE").toString());
							
							if(grade_sum >= comp_score){ 
								GradeRslt.setValue(i, "GRADE", Grade.getValue(j, "GRADE"));
								break;
							}
		
						}
						
					}
				}
				
				
				tran.begin();
				
				if(GradeRslt.rowSize() > 0){
					
					//GRADE 산정 기준을 기준으로 점수 저장
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_6");
					
					SQLParam sqlParam2 = new SQLParam();
					sqlParam2.setSqlName("msens.xcron.hansol.setgraderesultwebaction_6");
					sqlParam2.addValue("GradeRslt", GradeRslt);
					
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//소속 기준으로 점수 저장(GRADE 산정 기준 외 예외로 등급 지정하는 부분)
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_7");
					
					SQLParam sqlParam3 = new SQLParam();
					sqlParam3.setSqlName("msens.xcron.hansol.setgraderesultwebaction_7");
					sqlParam3.addValue("GradeRslt", GradeRslt);
					
					SQLServiceManager.getInstance().execute(sqlParam3, tran);
					
					
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_8");
					
					//근속년수 기준이 여러개일 경우를 대비해 먼저 근속년수 기준의 쿼리를 가져온다
					SQLParam sqlParam5 = new SQLParam();
					sqlParam5.setSqlName("msens.xcron.hansol.setgraderesultwebaction_8");
					sqlParam5.addValue("VERSION", GradeRslt.getValue(0, "VERSION"));
					SQLParam sqlResult5 = SQLServiceManager.getInstance().execute(sqlParam5);
					
					if(sqlResult5.getCount() > 0){
						for(int i=0; i< sqlResult5.getCount();i++){
							String smonth = "0";
							String emonth = "0";
							String grade = "";
							String reg_comment = "";
							
							if(i == 0){
								emonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_CNT1");
							}else{
								smonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i-1).getString("ITEM_CNT1");
								emonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_CNT1");
							}
							
							grade = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_SCORE");
							reg_comment = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("REG_COMMENT");
							
							//근속년수 기준으로 GRADE 산정(예외처리) --> 소속별등급산정이 없는 것에 한에 적용
							IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_9");
							
							for(int j=0; j< GradeRslt.rowSize(); j++){
								String p_ced_no = GradeRslt.getValue(j, "CON_ENT_DGN_NO").toString();
								SQLParam sqlParam4 = new SQLParam();
								sqlParam4.setSqlName("msens.xcron.hansol.setgraderesultwebaction_9");
								sqlParam4.addValue("VERSION", GradeRslt.getValue(0, "VERSION"));
								sqlParam4.addValue("CON_ENT_DGN_NO", p_ced_no);
								sqlParam4.addValue("SMONTH", smonth);
								sqlParam4.addValue("EMONTH", emonth);
								sqlParam4.addValue("GRADE", grade);
								sqlParam4.addValue("REG_COMMENT", reg_comment);
								
								SQLServiceManager.getInstance().execute(sqlParam4, tran);
							}
						}
					}
					
					//meta 테이블에 grade_flag 'y'로 변경
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_10");
					
					SQLParam sqlParam6 = new SQLParam();
					sqlParam6.setSqlName("msens.xcron.hansol.setgraderesultwebaction_10");
					sqlParam6.addValue("GradeRslt", GradeRollback);
					
					SQLServiceManager.getInstance().execute(sqlParam6, tran);			
					
					
		
				}
			
				
				tran.commit();
				
				ListParam GradeRslt_copy = new ListParam(new String[] {"CON_ENT_DGN_NO", "VERSION", "GRADE", "GRADE_NM","GRADE_SUM", "VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE","FOC_SCORE", "MEN_SCORE", "CHG_GRADE", "CHG_GRADE_NM","CHG_COMMENT"}); 
				
				//TM DB에 넣으면 안되서 임시 주석 처리한 것이므로 오픈 전에 주석 풀어야함!!
				try{
					//설정된 grade 점수 조회해오기
					for(int i =0 ;i< GradeRslt.rowSize(); i++){
						String p_ced_no = GradeRslt.getValue(i, "CON_ENT_DGN_NO").toString();
						
						IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_11");
						
						SQLParam sqlParam7 = new SQLParam();
						sqlParam7.setSqlName("msens.xcron.hansol.setgraderesultwebaction_11");
						sqlParam7.addValue("CON_ENT_DGN_NO", p_ced_no);
						SQLParam sqlResult7 = SQLServiceManager.getInstance().execute(sqlParam7);
						
						if(sqlResult7.getCount() > 0){
							for(int k=0; k< sqlResult7.getCount() ; k++){
								GradeRslt_copy.addRow(new Object[] {
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CON_ENT_DGN_NO"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("VERSION"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE_NM"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE_SUM"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("VRS_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("BAN_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("BEF_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("SEC_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("FOC_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("MEN_SCORE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_GRADE"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_GRADE_NM"),
										sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_COMMENT")
								});
							}
						}
						
					}
					
	
					
					//ListParam ds_res = sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11");
					
					//설정된 grade 점수 AiGEN에 보내기
					IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_12 :: " + GradeRslt_copy.rowSize());
					//해당 부분 임시주석 --> 해당부분 임시주석(Aigen 실테스트 시점에 주석 풀어야함
					if(GradeRslt_copy.rowSize()>0){
						tran.begin();
						
						SQLParam sqlParam8 = new SQLParam();
						sqlParam8.setSqlName("msens.xcron.hansol.setgraderesultwebaction_12");
						sqlParam8.addValue("DS_GRD_RES", GradeRslt_copy);
						
						SQLServiceManager.getInstance().execute(sqlParam8, tran);		
						
						tran.commit();
					}
					
				}catch(Exception e){
					tran.rollback();
					e.getStackTrace();
					ErrorLogger.error("#####TM DB에 Grade 넣는 과정 오류 발생 ::  = "+e.getMessage());
					IVRLogger.error("#####TM DB에 Grade 넣는 과정 오류 발생 ::  = "+e.getMessage());
				}
				
				//Encryption 대상 찾아오는 쿼리 수행하기
				try{
					if(GradeRollback != null){ 
						if(GradeRollback.rowSize() > 0) setEncrypt();
					}
				}catch(Exception e){
					ErrorLogger.error("#####Encryption 과정 중 에러발생 ::  = "+e.getMessage());
					IVRLogger.error("#####Encryption 과정 중 에러발생 ::  = "+e.getMessage());
				}
			
			}
			IVRLogger.info("GradeResultWebAction Stop!!");
					
				
		} catch (Exception e) {
			tran.rollback();
			
			if(GradeRollback != null){
				if(GradeRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setgraderesultwebaction_14
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.setgraderesultwebaction_14");
						sqlParam5.addValue("GradeRollback", GradeRollback);
						
						SQLServiceManager.getInstance().execute(sqlParam5, tran);
						
						tran.commit();
					} catch (SQLServiceException e1) {
						// TODO Auto-generated catch block
						tran.rollback();
						e1.printStackTrace();
					}		
					
					
				}
			}
			
			e.getStackTrace();
			//ErrorLogger.error("#####error = "+e.getMessage());
			IVRLogger.error("#####Grade 등급 산정 중 에러발생= "+e.getMessage());
		}
		
	}	
	
	
	public void setEncrypt(){
		IVRLogger.info("###########setEncrryption Start###################");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		ListParam SttEncryptContent = new ListParam(new String[] {"UCID", "CONTENT"});
		ListParam SttEncryptSent = new ListParam(new String[] {"UCID", "STT_SENT_ID","STT_SENT"});
		
		//String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
		String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
		
		String key = "";
	    
		Properties prop = new Properties();
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(PROP_DIR);
			prop.load(new java.io.BufferedInputStream(fis));
			key = prop.getProperty("cipher_key");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//암호화 대상자 찾아오기
		try {
			
			for(int i=0; i< GradeRollback.rowSize(); i++){
				String ced_no = GradeRollback.getValue(i, "CON_ENT_DGN_NO").toString();
				
				SQLParam sqlParam1 = new SQLParam();
				sqlParam1.setSqlName("msens.xcron.hansol.setEncryptionwebaction_1");
				sqlParam1.addValue("CON_ENT_DGN_NO", ced_no);
				SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
				
				
				
				IVRLogger.info("###########msens.xcron.hansol.setEncryptionwebaction_1::count::" + sqlResult1.getCount());
				
				ListParam res = null;
				
				//tran.begin();
				
				if(sqlResult1.getCount() > 0){
					res = sqlResult1.getListParam("msens.xcron.hansol.setEncryptionwebaction_1");
					
					AES256Cipher aes_cipher = AES256Cipher.getInstance(key);
					
					for(int j=0; j<res.rowSize(); j++){
						String ucid = res.getValue(j, "UCID").toString();
						
						SttEncryptSent.clear();
						SttEncryptContent.clear();
						
						IVRLogger.debug("###ucid ::"+ ucid +" ///// 분석개수 : " + res.getValue(j, "ANL_CNT").toString() + ", 콜개수 : " + res.getValue(j, "CALL_CNT").toString() + "// 암호화 여부 : " + res.getValue(j, "ENC_FLAG").toString());
						if((Integer.parseInt(res.getValue(j, "ANL_CNT").toString()) == Integer.parseInt(res.getValue(j, "CALL_CNT").toString())) && "N".equals(res.getValue(j, "ENC_FLAG").toString())){
							SQLParam sqlParam2 = new SQLParam();
							
							//IVRLogger.debug("##########s로 업데이트시작");
							
							tran.begin();
							
							/*//해당 UCID 암호화 작업 중임을 알리기 위해 S로 업데이트 치기
							//sqlParam2.clear();
							sqlParam2.setSqlName("msens.xcron.hansol.setEncryptionwebaction_6");
							sqlParam2.addValue("UCID", ucid);
							
							SQLServiceManager.getInstance().execute(sqlParam2, tran);*/
							
							
							//IVRLogger.debug("#############ucid :::: "+ucid);
							SQLParam sqlParam5 =  new SQLParam();  
							sqlParam5.setSqlName("msens.xcron.hansol.setEncryptionwebaction_6");
							sqlParam5.addValue("UCID", ucid);
							
							SQLServiceManager.getInstance().execute(sqlParam5, tran);
							
							
							
							tran.commit();
							
							//IVRLogger.debug("##########s로 업데이트완료");
							
							//같으면 분석이 다된거니까 암호화 돌리기
							
							SQLParam sqlParam3 = new SQLParam();
							
							//IVRLogger.debug("!!!!!!!!!!!!");
							sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_2");
							sqlParam3.addValue("UCID", ucid);
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
							
							IVRLogger.debug("######msens.xcron.hansol.setEncryptionwebaction_2 ::count:: " + sqlResult3.getCount());
							if(sqlResult3.getCount() > 0){
								String v_content = sqlResult3.getListParam("msens.xcron.hansol.setEncryptionwebaction_2").getParam(0).getString("CONTENT");
								String enc_content = aes_cipher.encrypt(v_content);
								SttEncryptContent.addRow(new Object[] {
										sqlResult3.getListParam("msens.xcron.hansol.setEncryptionwebaction_2").getParam(0).getString("UCID"),
										enc_content
								});
							}
							
							SQLParam sqlParam4 = new SQLParam();
							sqlParam4.setSqlName("msens.xcron.hansol.setEncryptionwebaction_3");
							sqlParam4.addValue("UCID", ucid);
							SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
							
							IVRLogger.debug("######msens.xcron.hansol.setEncryptionwebaction_3 ::count:: " + sqlResult4.getCount());
							
							if(sqlResult4.getCount() > 0){
								for(int k =0; k< sqlResult4.getCount(); k++){
									String v_sent = sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("STT_SENT");
									String enc_sent = aes_cipher.encrypt(v_sent);
									SttEncryptSent.addRow(new Object[] {
											sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("UCID"),
											sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("STT_SENT_ID"),
											enc_sent
									});
								}
							}
							
							//SQLParam sqlParam3 = new SQLParam();
							
							//IVRLogger.debug("###############CONTENT : :" + SttEncryptContent.rowSize());
							//IVRLogger.debug("###############SENT : :" + SttEncryptSent.rowSize());
							
							tran.begin();
							
							if(SttEncryptContent.rowSize() > 0){
								sqlParam3.clear();
								sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_4");
								sqlParam3.addValue("SttEncryptContent", SttEncryptContent);
								
								SQLServiceManager.getInstance().execute(sqlParam3, tran);
							}
							
							if(SttEncryptSent.rowSize() > 0){
								sqlParam3.clear();
								sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_5");
								sqlParam3.addValue("SttEncryptSent", SttEncryptSent);
								
								SQLServiceManager.getInstance().execute(sqlParam3, tran);
							}
							
							tran.commit();
						}
	
					}
				}
				
				

			}

			
	
		} catch (Exception e) {
			tran.rollback();
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			IVRLogger.error("##########GradeWebAction :: Encryption Error :: " + e.getMessage());
		}
		
	}
	
}

