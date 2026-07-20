package xcron.com.webaction;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Calendar;

import jedix.xwing.action.XwingWebAction;

import com.hansol.dbenc.util.SafeDBUtil;
import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class SttIntegWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.error("SttIntegWebAction Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		try {
			ListParam SttInteInfo = new ListParam(new String[] {"REC_KEY", "FILE_LEN", "CONTENT", "START_TIME", "DURATION", "EXTENSION", 
					"AGENT_ID", "CONTACT_ID", "REGIST_NO", "INCALL_NO", "OUTCALL_NO", "OUT_GB", "CUST_NAME", "USER_ID", "USER_NAME", "DEPT_CD",
					"DEPT_NAME", "USER_WORK_MONTH", "CUST_AGE", "CUST_SEX", "SVC_GRADE", "MARK_GRADE", "TA_FLAG", "RED_YN", "ARS_CD", "CHANNEL"});
			
			ListParam Final_SttInteInfo = new ListParam(new String[] {"REC_KEY", "FILE_LEN", "CONTENT", "START_TIME", "DURATION", "EXTENSION", 
					"AGENT_ID", "CONTACT_ID", "REGIST_NO", "INCALL_NO", "OUTCALL_NO", "OUT_GB", "CUST_NAME", "USER_ID", "USER_NAME", "DEPT_CD",
					"DEPT_NAME", "USER_WORK_MONTH", "CUST_AGE", "CUST_SEX", "SVC_GRADE", "MARK_GRADE", "TA_FLAG", "RED_YN", "ARS_CD", "CHANNEL"});
			
			ListParam AppContact = new ListParam(new String[] {"CONTACT_ID", "CUST_ID", "CONTACT_PATH_CD", "CONTACT_START_DATE", "CONTACT_START_TIME",
					"CONTACT_END_DATE", "CONTACT_END_TIME", "USER_ID", "CENTER_CD", "SVC_GRADE", "MARK_GRADE", "CUST_NAME", "ARS_CD", "CHANNEL"});
			
			ListParam AppContactDetail = new ListParam(new String[] {"CONTACTINFO_ID", "CONTACT_ID", "INSURE_ID", "BOJONG_ID", "MAIN_CNSL_CD", "SUB_CNSL_CD",
					"DTL_CNSL_CD", "CNSL_MEMO", "SEQ_NO", "MAIN_CNSL_CD_NM", "SUB_CNSL_CD_NM", "DTL_CNSL_CD_NM", "PROD_ID", "PROD_NAME", "MAIN_PROD_CD", "MAIN_PROD_NM",
					"SUB_PROD_CD", "SUB_PROD_NM"});
			
			ListParam Final_AppContactDetail = new ListParam(new String[] {"CONTACTINFO_ID", "CONTACT_ID", "INSURE_ID", "BOJONG_ID", "MAIN_CNSL_CD", "SUB_CNSL_CD",
					"DTL_CNSL_CD", "CNSL_MEMO", "SEQ_NO", "MAIN_CNSL_CD_NM", "SUB_CNSL_CD_NM", "DTL_CNSL_CD_NM", "PROD_ID", "PROD_NAME", "MAIN_PROD_CD", "MAIN_PROD_NM",
					"SUB_PROD_CD", "SUB_PROD_NM"});
			
			ListParam AppContactId = new ListParam(new String[] {"CONTACT_ID"});
			
			IVRLogger.info("msens.xcron.hansol.settintegwebaction_1");
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.settintegwebaction_1");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			String v_contact_id_group = "";
			String v_contact_fp_id_group = "";
			
			IVRLogger.info("msens.xcron.hansol.settintegwebaction_1::sqlResult.getCount()::" + sqlResult.getCount());
			
			if (sqlResult.getCount() > 0) {
				for(int i=0; i<sqlResult.getCount(); i++) {
					
					String v_decrypt_regist_no = SafeDBUtil.sdbDecrypt("ENC_REG_NO", sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("REGIST_NO"));
					
					String v_age = "";
					String v_sex = "";
					
					IVRLogger.info("v_decrypt_regist_no::" + v_decrypt_regist_no);
					
					if(v_decrypt_regist_no.length() == 13 && v_decrypt_regist_no != null){
						v_age = getAnalysisRegistno(v_decrypt_regist_no, "AGE");
						v_sex = getAnalysisRegistno(v_decrypt_regist_no, "SEX");
					}

					String v_red_yn = sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("RED_YN");
					
					/*if("N".equals(v_red_yn)) {
						String v_decrypt_incall_no = SafeDBUtil.sdbDecrypt("ENC_TEL_NO", sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("INCALL_NO"));
						if(v_decrypt_incall_no.length() != 0 && v_decrypt_incall_no != null){
							IVRLogger.info("v_decrypt_incall_no::" + v_decrypt_incall_no);
							
							SQLParam sqlParam7 = new SQLParam();
							sqlParam7.clear();
							sqlParam7.setSqlName("vsens.xcron.kyobo.settintegwebaction_8");
							sqlParam7.addValue("incall_no", v_decrypt_incall_no);
							
							SQLParam sqlResult7 = SQLServiceManager.getInstance().execute(sqlParam7);
							
							if (sqlResult7.getCount() > 0) {
								v_red_yn = "Y";
							} else {
								v_red_yn = "N";
							}
						}
					}
					
					IVRLogger.info("v_red_yn::" + v_red_yn);*/
					
					/*String v_regist_no = sqlResult.getListParam("vsens.xcron.kyobo.settintegwebaction_1").getParam(i).getString("REGIST_NO");
					String v_incall_no = sqlResult.getListParam("vsens.xcron.kyobo.settintegwebaction_1").getParam(i).getString("INCALL_NO");
					String v_red_yn = "N";
					
					SQLParam sqlParam1 = new SQLParam();
					sqlParam1.clear();
					sqlParam1.setSqlName("vsens.xcron.kyobo.settintegwebaction_7");
					sqlParam1.addValue("regist_no", v_regist_no);
					
					SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
					
					if (sqlResult1.getCount() > 0) {
						v_red_yn = "Y";
					} else {
						SQLParam sqlParam2 = new SQLParam();
						sqlParam2.clear();
						sqlParam2.setSqlName("vsens.xcron.kyobo.settintegwebaction_8");
						sqlParam2.addValue("incall_no", v_incall_no);
						
						SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
						
						if (sqlResult2.getCount() > 0) {
							v_red_yn = "Y";
						} else {
							v_red_yn = "N";
						}
					}*/
					
					String v_out_gb = sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("OUT_GB");
					String r_out_gb = "";
					
					if("I".equals(v_out_gb) || "IN".equals(v_out_gb)) {
						r_out_gb = "I";
					} else if("O".equals(v_out_gb) || "OUT".equals(v_out_gb) || "PDS".equals(v_out_gb)) {
						r_out_gb = "O";
					} else {
						r_out_gb = v_out_gb;
					}
					
					SttInteInfo.addRow(new Object[] {
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("REC_KEY"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("FILE_LEN"),
						getClobConvertToStr((Clob) sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getValue("CONTENT")),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("START_TIME"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("DURATION"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("EXTENSION"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("AGENT_ID"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("CONTACT_ID"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("REGIST_NO"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("INCALL_NO"),
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("OUTCALL_NO"),
						r_out_gb,
						sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("CUST_NAME"),
						"",
						"",
						"",
						"",
						"",
						v_age,
						v_sex,
						"",
						"",
						"N",
						v_red_yn,
						"",
						""
					});
					String v_contact_id = sqlResult.getListParam("msens.xcron.hansol.settintegwebaction_1").getParam(i).getString("CONTACT_ID");
					
					if(v_contact_id.length() < 9 && v_contact_id.length() > 6) {
						v_contact_fp_id_group = v_contact_fp_id_group + ",'"+ v_contact_id + "'";
					} else {
						v_contact_id_group = v_contact_id_group + ",'"+ v_contact_id + "'";
					}
					
					if(i % 501 == 500 || i == (sqlResult.getCount()-1)) {
						if(v_contact_id_group.length() > 0) {
							v_contact_id_group = v_contact_id_group.substring(1);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_2");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_2::contact_id::" + v_contact_id_group);
							
							SQLParam sqlParam3 = new SQLParam();
							sqlParam3.clear();
							sqlParam3.setSqlName("msens.xcron.hansol.settintegwebaction_2");
							sqlParam3.addValue("contact_id", v_contact_id_group);
							
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_2::sqlResult3.getCount()::" + sqlResult3.getCount());
							
							if (sqlResult3.getCount() > 0) {
								for(int j=0; j<sqlResult3.getCount(); j++) {
									AppContact.addRow(new Object[] {
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTID"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CUSTID"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTPATHCD"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTSTARTDATE"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTSTARTTIME"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTENDDATE"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CONTACTENDTIME"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("USERID"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CENTERCD"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("SVC_GRADE"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("MARK_GRADE"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CUST_NAME"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("ARS_CD"),
										sqlResult3.getListParam("msens.xcron.hansol.settintegwebaction_2").getParam(j).getString("CHANNEL")
									});
								}
							}
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_9");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_9::contact_id::" + v_contact_id_group);
							
							SQLParam sqlParam5 = new SQLParam();
							sqlParam5.clear();
							sqlParam5.setSqlName("msens.xcron.hansol.settintegwebaction_9");
							sqlParam5.addValue("contact_id", v_contact_id_group);
							
							SQLParam sqlResult5 = SQLServiceManager.getInstance().execute(sqlParam5);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_9::sqlResult5.getCount()::" + sqlResult5.getCount());
							
							if (sqlResult5.getCount() > 0) {
								for(int j=0; j<sqlResult5.getCount(); j++) {
									AppContactDetail.addRow(new Object[] {
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("CONTACTINFO_ID"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("CONTACT_ID"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("INSURE_ID"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("BOJONG_ID"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("MAIN_CNSL_CD"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("SUB_CNSL_CD"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("DTL_CNSL_CD"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("CNSL_MEMO"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("SEQ_NO"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("MAIN_CNSL_CD_NM"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("SUB_CNSL_CD_NM"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("DTL_CNSL_CD_NM"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("PROD_ID"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("PROD_NAME"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("MAIN_PROD_CD"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("MAIN_PROD_NM"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("SUB_PROD_CD"),
										sqlResult5.getListParam("msens.xcron.hansol.settintegwebaction_9").getParam(j).getString("SUB_PROD_NM")
									});
								}
							}
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_13");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_13::contact_id::" + v_contact_id_group);
							
							SQLParam sqlParam6 = new SQLParam();
							sqlParam6.clear();
							sqlParam6.setSqlName("msens.xcron.hansol.settintegwebaction_13");
							sqlParam6.addValue("contact_id", v_contact_id_group);
							
							SQLParam sqlResult6 = SQLServiceManager.getInstance().execute(sqlParam6);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_13::sqlResult6.getCount()::" + sqlResult6.getCount());
							
							if (sqlResult6.getCount() > 0) {
								for(int j=0; j<sqlResult6.getCount(); j++) {
									AppContactId.addRow(new Object[] {
										sqlResult6.getListParam("msens.xcron.hansol.settintegwebaction_13").getParam(j).getString("CONTACT_ID")
									});
								}
							}
							
							v_contact_id_group = "";
						}
						
						if(v_contact_fp_id_group.length() > 0) {
							v_contact_fp_id_group = v_contact_fp_id_group.substring(1);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_14");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_14::fp_contact_id::" + v_contact_fp_id_group);
							
							SQLParam sqlParam14 = new SQLParam();
							sqlParam14.clear();
							sqlParam14.setSqlName("msens.xcron.hansol.settintegwebaction_14");
							sqlParam14.addValue("fp_contact_id", v_contact_fp_id_group);
							
							SQLParam sqlResult14 = SQLServiceManager.getInstance().execute(sqlParam14);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_14::sqlResult14.getCount()::" + sqlResult14.getCount());
							
							if (sqlResult14.getCount() > 0) {
								for(int j=0; j<sqlResult14.getCount(); j++) {
									AppContact.addRow(new Object[] {
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTID"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CUSTID"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTPATHCD"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTSTARTDATE"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTSTARTTIME"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTENDDATE"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CONTACTENDTIME"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("USERID"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CENTERCD"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("SVC_GRADE"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("MARK_GRADE"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CUST_NAME"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("ARS_CD"),
										sqlResult14.getListParam("msens.xcron.hansol.settintegwebaction_14").getParam(j).getString("CHANNEL")
									});
								}
							}
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_15");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_15::contact_id::" + v_contact_fp_id_group);
							
							SQLParam sqlParam15 = new SQLParam();
							sqlParam15.clear();
							sqlParam15.setSqlName("msens.xcron.hansol.settintegwebaction_15");
							sqlParam15.addValue("fp_contact_id", v_contact_fp_id_group);
							
							SQLParam sqlResult15 = SQLServiceManager.getInstance().execute(sqlParam15);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_15::sqlResult15.getCount()::" + sqlResult15.getCount());
							
							if (sqlResult15.getCount() > 0) {
								for(int j=0; j<sqlResult15.getCount(); j++) {
									AppContactDetail.addRow(new Object[] {
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("CONTACTINFO_ID"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("CONTACT_ID"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("INSURE_ID"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("BOJONG_ID"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("MAIN_CNSL_CD"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("SUB_CNSL_CD"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("DTL_CNSL_CD"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("CNSL_MEMO"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("SEQ_NO"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("MAIN_CNSL_CD_NM"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("SUB_CNSL_CD_NM"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("DTL_CNSL_CD_NM"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("PROD_ID"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("PROD_NAME"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("MAIN_PROD_CD"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("MAIN_PROD_NM"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("SUB_PROD_CD"),
										sqlResult15.getListParam("msens.xcron.hansol.settintegwebaction_15").getParam(j).getString("SUB_PROD_NM")
									});
								}
							}
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_16");
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_16::contact_id::" + v_contact_fp_id_group);
							
							SQLParam sqlParam16 = new SQLParam();
							sqlParam16.clear();
							sqlParam16.setSqlName("msens.xcron.hansol.settintegwebaction_16");
							sqlParam16.addValue("fp_contact_id", v_contact_fp_id_group);
							
							SQLParam sqlResult16 = SQLServiceManager.getInstance().execute(sqlParam16);
							
							IVRLogger.info("msens.xcron.hansol.settintegwebaction_16::sqlResult16.getCount()::" + sqlResult16.getCount());
							
							if (sqlResult16.getCount() > 0) {
								for(int j=0; j<sqlResult16.getCount(); j++) {
									AppContactId.addRow(new Object[] {
										sqlResult16.getListParam("msens.xcron.hansol.settintegwebaction_16").getParam(j).getString("CONTACT_ID")
									});
								}
							}
							
							v_contact_fp_id_group = "";
						}
					}
				}
				
				if(AppContact.rowSize() > 0) {
					for(int i=0; i<SttInteInfo.rowSize(); i++) {
						String t_contact_id = (String) SttInteInfo.getValue(i, "CONTACT_ID");
							
						for(int j=0; j<AppContact.rowSize(); j++) {
							String e_contact_id = AppContact.getValue(j, "CONTACT_ID").toString();  
							
							if(t_contact_id.equals(e_contact_id)) {
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + t_contact_id);
								
								String v_svc_grade = "";
								if(AppContact.getValue(j, "SVC_GRADE") != null) {
									v_svc_grade = AppContact.getValue(j, "SVC_GRADE").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_svc_grade);
								
								String v_mark_grade = "";
								if(AppContact.getValue(j, "MARK_GRADE") != null) {
									v_mark_grade = AppContact.getValue(j, "MARK_GRADE").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_mark_grade);
								
								String v_cust_name = "";
								if(AppContact.getValue(j, "CUST_NAME") != null) {
									v_cust_name = AppContact.getValue(j, "CUST_NAME").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_cust_name);
								
								String v_user_id = "";
								if(AppContact.getValue(j, "USER_ID") != null) {
									v_user_id = AppContact.getValue(j, "USER_ID").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_user_id);
								
								String v_ars_cd = "";
								if(AppContact.getValue(j, "ARS_CD") != null) {
									v_ars_cd = AppContact.getValue(j, "ARS_CD").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_ars_cd);
								
								String v_channel = "";
								if(AppContact.getValue(j, "CHANNEL") != null) {
									v_channel = AppContact.getValue(j, "CHANNEL").toString();
								}
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_channel);
								
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()1::" + v_svc_grade);
								SttInteInfo.setValue(i, "SVC_GRADE", v_svc_grade);
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()2::" + v_mark_grade);
								SttInteInfo.setValue(i, "MARK_GRADE", v_mark_grade);
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()3::" + v_cust_name);
								SttInteInfo.setValue(i, "CUST_NAME", v_cust_name);
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()4::" + v_user_id);
								SttInteInfo.setValue(i, "USER_ID", v_user_id);
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()5::" + v_ars_cd);
								SttInteInfo.setValue(i, "ARS_CD", v_ars_cd);
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()6::" + v_channel);
								SttInteInfo.setValue(i, "CHANNEL", v_channel);
								
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3");
								
								SQLParam sqlParam4 = new SQLParam();
								sqlParam4.clear();
								sqlParam4.setSqlName("msens.xcron.hansol.settintegwebaction_3");
								sqlParam4.addValue("user_id", v_user_id);
								
								SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
								
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_user_id + "::" + sqlResult4.getCount());
								
								if (sqlResult4.getCount() > 0) {
									SttInteInfo.setValue(i, "USER_NAME", sqlResult4.getListParam("msens.xcron.hansol.settintegwebaction_3").getParam(0).getString("USER_NAME"));
									SttInteInfo.setValue(i, "DEPT_CD", sqlResult4.getListParam("msens.xcron.hansol.settintegwebaction_3").getParam(0).getString("DEPT_CD"));
									SttInteInfo.setValue(i, "DEPT_NAME", sqlResult4.getListParam("msens.xcron.hansol.settintegwebaction_3").getParam(0).getString("DEPT_NAME"));
									SttInteInfo.setValue(i, "USER_WORK_MONTH", sqlResult4.getListParam("msens.xcron.hansol.settintegwebaction_3").getParam(0).getString("USER_WORK_MONTH"));
								}
								
								IVRLogger.info("msens.xcron.hansol.settintegwebaction_3::sqlResult4.getCount()::" + v_user_id + "::" + sqlResult4.getCount());
								break;
							}
						}
					}
				}
				
				tran.begin();
				/*IVRLogger.info("vsens.xcron.kyobo.settintegwebaction_10");
				
				sqlParam.clear();
				sqlParam.setSqlName("vsens.xcron.kyobo.settintegwebaction_10");
				sqlParam.addValue("AppContact", AppContact);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);*/
				
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_11");
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.settintegwebaction_11");
				sqlParam.addValue("AppContactDetail", AppContactDetail);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				for(int i=0; i<SttInteInfo.rowSize(); i++) {
					String t_contact_id = (String) SttInteInfo.getValue(i, "CONTACT_ID");
					
					if("0".equals(t_contact_id)) {
						Final_SttInteInfo.addParam(SttInteInfo.getParam(i));
						continue;
					}
					
					for(int j=0; j<AppContactId.rowSize(); j++) {
						String e_contact_id = AppContactId.getValue(j, "CONTACT_ID").toString(); 
						
						if(t_contact_id.equals(e_contact_id) || "0".equals(t_contact_id)) {
							Final_SttInteInfo.addParam(SttInteInfo.getParam(i));
							break;
						}
					}
				}
				
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_4");
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_4::Final_SttInteInfo.rowSize()::" + Final_SttInteInfo.rowSize());
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.settintegwebaction_4");
				sqlParam.addValue("SttInteInfo", Final_SttInteInfo);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				for(int i=0; i<AppContactDetail.rowSize(); i++) {
					String t_contact_id = (String) AppContactDetail.getValue(i, "CONTACT_ID");
					
					if("0".equals(t_contact_id)) {
						Final_AppContactDetail.addParam(AppContactDetail.getParam(i));
						continue;
					}
					
					for(int j=0; j<AppContactId.rowSize(); j++) {
						String e_contact_id = AppContactId.getValue(j, "CONTACT_ID").toString(); 
						
						if(t_contact_id.equals(e_contact_id)) {
							Final_AppContactDetail.addParam(AppContactDetail.getParam(i));
							break;
						}
					}
				}
				/*
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_12");
				IVRLogger.info("vsens.xcron.kyobo.settintegwebaction_12::Final_AppContactDetail.rowSize()::" + Final_AppContactDetail.rowSize());
				
				sqlParam.clear();
				sqlParam.setSqlName("vsens.xcron.kyobo.settintegwebaction_12");
				sqlParam.addValue("AppContactDetail", Final_AppContactDetail);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);*/
				
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_5");
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.settintegwebaction_5");
				sqlParam.addValue("SttInteInfo", Final_SttInteInfo);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				IVRLogger.info("msens.xcron.hansol.settintegwebaction_6");
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.settintegwebaction_6");
				sqlParam.addValue("SttInteInfo", Final_SttInteInfo);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				tran.commit();
			}
			IVRLogger.error("SttIntegWebAction Stop!!");
			
		} catch (Exception e) {
			tran.rollback();
			e.getStackTrace();
			IVRLogger.error(e.getMessage());
		}
		
	}
	
	public String getAnalysisRegistno(String registno, String gubun) {
		String resultValue = "";
		Calendar date = Calendar.getInstance(); 
		int age = 0;
		int sex = 0;

		char gender = registno.charAt(6); 
		int year = Integer.parseInt(registno.substring(0, 2));

		switch (gender) {         
			case '1':             
				year += 1900;             
				sex = 0;
				break;         
			case '2':             
				year += 1900;             
				sex = 1;
				break;         
			case '3':
				year += 2000;
				sex = 0;
				break;
			case '4':
				year += 2000;
				sex = 1;
				break;
			case '5':
				year += 1900;
				sex = 0;
				break;         
			case '6':             
				year += 1900;             
				sex = 1;                         
				break;         
			case '7':             
				year += 2000;             
				sex = 0;                     
				break;         
			case '8':             
				year += 2000;             
				sex = 1;                      
				break;         
			case '9':             
				year += 1800;             
				sex = 0;                        
				break;         
			case '0':             
				year += 1800;             
				sex = 1;                        
				break;         
		}
		
		if("AGE".equals(gubun)) {
			age = (date.get(Calendar.YEAR)) - year + 1;
			resultValue = Integer.toString(age);
		} else if("SEX".equals(gubun)) {
			String sexchk = (sex != 1 ? "M" : "F");
			resultValue = sexchk;
		}
		return resultValue;
	}
	
	public String getClobConvertToStr(Clob clob) {
		int size;
		String str = "";

		try {
			if (clob == null) {
				size = 0;
			} else {
				size = (int) clob.length();
			}

			if (size != 0) {
				str = clob.getSubString(1, size);
			} else {
				str = "";
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return str;
	}
}

