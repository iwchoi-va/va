package sens.src.script;

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
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class ScriptResultWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		ErrorLogger.debug("ScriptResultWebaction  Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam cedNoList = null;
		
		try {
			ListParam ScriptRsl = new ListParam(new String[] {"CON_ENT_DGN_NO", "CTRA_INSA_CLCD", "USER_ID", "ISTP_CD", "ISTP_NM", "PROD_CD", "PROD_NM","TREATYHQCD","TREATYHQNM","TREATYBRHCD","TREATYBRHNM","TREATYDEPTCD","TREATYDEPTNM","SCORE1", "SCORE2"});
			ListParam ScriptOmit = new ListParam(new String[] {"CON_ENT_DGN_NO", "SC_SNO", "SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","CTRA_INSA_CLCD","SCRIPT_SENT_ID","SENT_SCORE","QST_FOCUS", "QST_SECRET"});
			ListParam ScriptSecret = new ListParam(new String[] {"CON_ENT_DGN_NO", "SC_SNO", "SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","CTRA_INSA_CLCD","SCRIPT_SENT_ID","SENT_SCORE","SECRET_YN"});
			ListParam ScriptFocus = new ListParam(new String[] {"CON_ENT_DGN_NO", "SC_SNO", "SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","CTRA_INSA_CLCD","SCRIPT_SENT_ID","SENT_SCORE","FOCUS_YN"});
			
			ListParam ScriptScore = new ListParam(new String[] {"CON_ENT_DGN_NO", "UCID", "SC_SNO", "SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","CTRA_INSA_CLCD","SCRIPT_SENT_ID","START_TIME","TA_SCORE","SCH_SCORE","QST_KEYWORD", "CHG_DATE", "QST_RATE"});
			ListParam finalScriptScore = new ListParam(new String[] {"CON_ENT_DGN_NO", "UCID", "SC_SNO", "SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","CTRA_INSA_CLCD","SCRIPT_SENT_ID","START_TIME","SENT_SCORE"});
			
			
			IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_11");
			SQLParam sqlParam1 = new SQLParam();
			sqlParam1.setSqlName("msens.xcron.hansol.setscriptresultwebaction_11");
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			//String v_dgn_no_group = "";
			
			
			IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_11::count::"+sqlResult1.getCount());
			
			if(sqlResult1.getCount() > 0 ){
				cedNoList = sqlResult1.getListParam("msens.xcron.hansol.setscriptresultwebaction_11");
				
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam4 =  new SQLParam();  
				sqlParam4.setSqlName("msens.xcron.hansol.setscriptresultwebaction_9");
				sqlParam4.addValue("ScriptRsl", cedNoList); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
				tran.commit();
				

				SQLParam sqlParam = new SQLParam();
				SQLParam sqlResult = new SQLParam();
				
				for(int k=0; k<cedNoList.rowSize();k++){
					String v_ced_no = cedNoList.getValue(k, "CON_ENT_DGN_NO").toString();
					
					//v_dgn_no_group = v_dgn_no_group.substring(1);
				
					//스크립트 TA분석 준수율, 필수검출어 준수율 계산해서 업데이트 하기
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_1");
					//설계번호로 Sponsor_Callinfo에 STT_FLAG = 'Y' AND TA_FLAG = 'N'인 콜 조회
					//SQLParam sqlParam = new SQLParam();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_1");
					sqlParam.addValue("con_ent_dgn_no", v_ced_no);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
					
					ErrorLogger.debug(sqlResult.getCount());
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_1::sqlResult.getCount()::" + sqlResult.getCount());
					
					if(sqlResult.getCount() >0){
						
						//msens.xcron.hansol.setscriptresultwebaction_8
						
						
						for(int i=0; i<sqlResult.getCount(); i++) {
							
							ScriptScore.addRow(new Object[] {
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("CON_ENT_DGN_NO"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("UCID"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("SC_SNO"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("SC_LCLF_CD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("SC_SCLF_CD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("QST_CD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("CNTR_CD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("CTRA_INSA_CLCD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("SCRIPT_SENT_ID"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("START_TIME"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("TA_SCORE"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("SCH_SCORE"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("QST_KEYWORD"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("CHG_DATE"),
									sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_1").getParam(i).getString("QST_RATE")
									
							});
						}	
					}
				}
				
			
			
			
				//SQLParam sqlParam = new SQLParam();
				//SQLParam sqlResult = new SQLParam();
				
				Code[] code_rate = CodeUtil.getCodes("VSS190");
				double score_rate = 1;
				
				for (int i = 0; code_rate != null && i < code_rate.length; i++) {
					if (!"Y".equalsIgnoreCase(code_rate[i].getUseYn())) {
						continue;
					}
					
					if("01".equals(code_rate[i].getCodeId())) {
						score_rate = Double.parseDouble(code_rate[i].getEtc1());
					}
				}
				
				for(int i=0; i< ScriptScore.rowSize();i++){
					
					double ta_score = Double.parseDouble(ScriptScore.getValue(i, "TA_SCORE").toString());
					double sch_score = Double.parseDouble(ScriptScore.getValue(i, "SCH_SCORE").toString());
					double sent_score = 0;
					double qst_rate = Double.parseDouble(ScriptScore.getValue(i, "QST_RATE").toString());
					
					
					if(!"".equals(ScriptScore.getValue(i, "QST_KEYWORD").toString().trim())){
						if(sch_score == 0){
							sent_score = 0;
						}else if(sch_score == 100){
							sent_score = Math.round(ta_score * score_rate);
							
							if(sent_score > 100) sent_score = 100;
						}
					}else{
						if(qst_rate > ta_score) sent_score = 0;
						else sent_score = ta_score;
					}
					
					finalScriptScore.addRow(new Object[] {
							ScriptScore.getValue(i, "CON_ENT_DGN_NO").toString(),
							ScriptScore.getValue(i, "UCID").toString(),
							ScriptScore.getValue(i, "SC_SNO").toString(),
							ScriptScore.getValue(i, "SC_LCLF_CD").toString(),
							ScriptScore.getValue(i, "SC_SCLF_CD").toString(),
							ScriptScore.getValue(i, "QST_CD").toString(),
							ScriptScore.getValue(i, "CNTR_CD").toString(),
							ScriptScore.getValue(i, "CTRA_INSA_CLCD").toString(),
							ScriptScore.getValue(i, "SCRIPT_SENT_ID").toString(),
							ScriptScore.getValue(i, "START_TIME").toString(),
							sent_score
					});
					
				}
				
				
				if(finalScriptScore.rowSize() > 0){
					tran.begin();
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_2");
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_2");
					sqlParam.addValue("finalScriptScore", finalScriptScore);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
					tran.commit();
				}
				
				
				//cedNoList
				for(int k=0; k< cedNoList.rowSize(); k++){
					String v_ced_no = cedNoList.getValue(k, "CON_ENT_DGN_NO").toString();
					//설계번호별 조회 쿼리 수행
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_3");
					
					SQLParam sqlParam2 = new SQLParam();
					//sqlParam2.clear();
					sqlParam2.setSqlName("msens.xcron.hansol.setscriptresultwebaction_3");
					sqlParam2.addValue("CON_ENT_DGN_NO", v_ced_no);
					SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
					
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_3::sqlResult.getCount()::" + sqlResult2.getCount());
	
					int tot_cnt = 0;
					int regu_cnt = 0;
					double regu_rate = 0;
					
					if(sqlResult2.getCount() >0){
						for(int i=0; i<sqlResult2.getCount(); i++) {
							String v_con_ent_dgn_no1 = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CON_ENT_DGN_NO");
							String v_ctra_insa_cncl1 = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("S_CTRA_INSA_CLCD");
							double regu_score = Double.parseDouble(sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("REGU_SCORE"));
							String qst_secret = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_SECRET");
							String qst_focus = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_FOCUS");
							
							if(i== sqlResult2.getCount()-1){
								tot_cnt++;
								if(regu_score != 0) regu_cnt++;
								
								
								int row = ScriptRsl.findRow("CON_ENT_DGN_NO", v_con_ent_dgn_no1);
								//IVRLogger.debug("regu_cnt = " + regu_cnt + "// tot_cnt = " + tot_cnt);
								
								regu_rate = (double) regu_cnt / (double) tot_cnt * 100;
								//IVRLogger.debug("rate = " + regu_rate);
								
								if(row > -1){
									IVRLogger.debug("###########regu_cnt = " + regu_cnt + "// tot_cnt = " + tot_cnt + "// 계피구분 = " + v_ctra_insa_cncl1);
									
									if("E".equals(v_ctra_insa_cncl1)){
										//IVRLogger.debug("#############계피구분 코드 ::" + v_ctra_insa_cncl1);
										String v_score = ScriptRsl.getValue(row, "SCORE1").toString();
										//ScriptRsl.setValue(row, "SCORE1", Math.round(regu_rate*100d) / 100d);
										ScriptRsl.setValue(row, "SCORE1", Math.round(regu_rate));
										ScriptRsl.setValue(row, "SCORE2", v_score);
									}else{
										ScriptRsl.setValue(row, "SCORE2",  Math.round(regu_rate));
										//ScriptRsl.setValue(row, "SCORE2",  Math.round(regu_rate*100d) / 100d);
									}
									
								}else{
									ScriptRsl.addRow(new Object[] {
											v_con_ent_dgn_no1,
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CTRA_INSA_CLCD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("USER_ID"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_CD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_NM"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_CD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_NM"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQCD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQNM"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHCD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHNM"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTCD"),
											sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTNM"),
											 Math.round(regu_rate),
											""
									});
								}
	
								tot_cnt = 0;
								regu_cnt = 0;
								regu_rate = 0;
	
							}else{
								String v_con_ent_dgn_no2 = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i+1).getString("CON_ENT_DGN_NO");
								String v_ctra_insa_cncl2 = sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i+1).getString("S_CTRA_INSA_CLCD");
								
								//IVRLogger.debug("###########regu_cnt = " + regu_cnt + "// tot_cnt = " + tot_cnt + "// 계피구분 = " + v_ctra_insa_cncl1 + "// 계피구분2 = " + v_ctra_insa_cncl2);
								
								if(v_con_ent_dgn_no1.equals(v_con_ent_dgn_no2) && v_ctra_insa_cncl1.equals(v_ctra_insa_cncl2)){
									tot_cnt++;
									if(regu_score != 0) regu_cnt++;
								}else{
									regu_rate = (double) regu_cnt / (double) tot_cnt * 100;
									
									int row = ScriptRsl.findRow("CON_ENT_DGN_NO", v_con_ent_dgn_no1);
									if(row > -1){
										IVRLogger.debug("###########regu_cnt = " + regu_cnt + "// tot_cnt = " + tot_cnt + "// 계피구분 = " + v_ctra_insa_cncl1 + "// 계피구분2 = " + v_ctra_insa_cncl2);
										
										if("E".equals(v_ctra_insa_cncl1)){
											//IVRLogger.debug("#############계피구분 코드 ::" + v_ctra_insa_cncl1);
											String v_score = ScriptRsl.getValue(row, "SCORE1").toString();
											ScriptRsl.setValue(row, "SCORE1", Math.round(regu_rate));
											ScriptRsl.setValue(row, "SCORE2", v_score);
										}else{
											ScriptRsl.setValue(row, "SCORE2",  Math.round(regu_rate));
										}
									}else{
										ScriptRsl.addRow(new Object[] {
												v_con_ent_dgn_no1,
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CTRA_INSA_CLCD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("USER_ID"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_CD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_NM"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_CD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_NM"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQCD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQNM"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHCD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHNM"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTCD"),
												sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTNM"),
												 Math.round(regu_rate),
												""
										});
									}
									
									tot_cnt = 0;
									regu_cnt = 0;
									regu_rate = 0;
	
									
								}
								
							}
							
							if(regu_score == 0){
	
								ScriptOmit.addRow(new Object[] {
										v_con_ent_dgn_no1,
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SNO"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_LCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CNTR_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("S_CTRA_INSA_CLCD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SCRIPT_SENT_ID"),
										regu_score,
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_FOCUS"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_SECRET")
								});
								
							}
							
							if("Y".equals(qst_focus)){
								String focus_yn = "Y";
								
								//IVRLogger.debug("######집중관리항목 설계번호 ::" + v_con_ent_dgn_no1);
								
								if(regu_score == 0) focus_yn = "N";
								else focus_yn = "Y";
								
								ScriptFocus.addRow(new Object[] {
										v_con_ent_dgn_no1,
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SNO"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_LCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CNTR_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("S_CTRA_INSA_CLCD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SCRIPT_SENT_ID"),
										regu_score,
										focus_yn
								});
							}
							
							if("Y".equals(qst_secret)){
								//IVRLogger.debug("######개인정보항목 설계번호 ::" + v_con_ent_dgn_no1 + " :: score = " + regu_score + " :::: "+ i);
								
								String secret_yn = "Y";
								
								if(regu_score == 0) secret_yn = "N";
								else secret_yn = "Y";
								
								ScriptSecret.addRow(new Object[] {
										v_con_ent_dgn_no1,
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SNO"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_LCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SC_SCLF_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("QST_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CNTR_CD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("S_CTRA_INSA_CLCD"),
										sqlResult2.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("SCRIPT_SENT_ID"),
										regu_score,
										secret_yn
								});
							}
							
							
							
							
							
							/*String v_con_ent_dgn_no = sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CON_ENT_DGN_NO");
							 
							int row = ScriptRsl.findRow("CON_ENT_DGN_NO", v_con_ent_dgn_no);
							if(row > -1){
								ScriptRsl.setValue(row, "SCORE2", sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("REGU_SCORE"));
							}else{
								ScriptRsl.addRow(new Object[] {
										v_con_ent_dgn_no,
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("CTRA_INSA_CLCD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("USER_ID"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_CD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("ISTP_NM"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_CD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("PROD_NM"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQCD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYHQNM"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHCD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYBRHNM"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTCD"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("TREATYDEPTNM"),
										sqlResult.getListParam("msens.xcron.hansol.setscriptresultwebaction_3").getParam(i).getString("REGU_SCORE"),
										""
								});
							}*/
							
							
						}
					}
	
				}
				
				
				tran.begin();
				
				if(ScriptRsl.rowSize() > 0){
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_4");
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_4");
					sqlParam.addValue("ScriptRsl", ScriptRsl);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_10");
					sqlParam.addValue("ScriptRsl", ScriptRsl); //STT_META에 VRS_S_FLAG Y로 업데이트
					SQLServiceManager.getInstance().execute(sqlParam, tran);
		
				}
				
				
				if(ScriptOmit.rowSize() > 0){
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_6");
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_6");
					sqlParam.addValue("ScriptOmit", ScriptOmit);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
		
				}
				
				if(ScriptSecret.rowSize() > 0){
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_7");
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_7");
					sqlParam.addValue("ScriptSecret", ScriptSecret);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
		
				}
				
				if(ScriptFocus.rowSize() > 0){
					IVRLogger.info("msens.xcron.hansol.setscriptresultwebaction_8");
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setscriptresultwebaction_8");
					sqlParam.addValue("ScriptFocus", ScriptFocus);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
		
				}
	
				tran.commit();
			
			}
			
			IVRLogger.debug("ScriptResultWebaction END!!");
		} catch (Exception e) {
			tran.rollback();
			
			
			if(cedNoList != null){
				if(cedNoList.rowSize() > 0){
					
					try {
						tran.begin();
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setscriptresultwebaction_12
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.setscriptresultwebaction_12");
						sqlParam5.addValue("ScriptRollback", cedNoList);
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
			//ErrorLogger.error(e.getMessage());
			IVRLogger.error("####ScriptResultWebAction Error :: "+e.getMessage());
			IVRLogger.info("####ScriptResultWebAction Error :: "+e.getMessage());
		
		}
		
	}	
}

