package sens.service.webaction;

import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.sql.*;

import java.util.*;


public class GetBannedTermsWebAction extends XwingWebAction {
	/**
	 * 
	* @param req
	* @param res
	* @throws WebActionException
	*/
	private String sdate = "";  //시작일자
	private String edate = ""; //종료일자
	private String ced_no = ""; //증권번호
	private String org1_cd = ""; // 조직 1레벨
	private String org2_cd = ""; //조직 2레벨
	private String org3_cd = ""; //조직 3레벨
	private String user_id = ""; //상담사 id
	private String user_flag = ""; //소속에서 선택된 상담사 id
	private String org2_flag = "";
	private String istp_cd = "";
	private String prod_cd = "";
	private String anl_stat = "";
	private String ban_grp = "";
	private String grade = "";
	private int start = 0;
	private int pagesize = 0;
	private String new_user_id = ""; //신규상담사 조회 여부
	private String type = ""; // 금지어 조회 구분(C : 콜별, U : 소속별, S : 증권번호별)
	private String sql = ""; 
	private String sql2 = ""; 
	private String groupby = ""; //groupby 기준
	private String cntgroupby ="";
	private String cntselect ="";
	private String cntjoin ="";
	private String orderby = "";
	private boolean excel_down = false;
	
	private Code[] code = null;
	ListParam DS_CODE = null;
	
	public void perform(JediRequest req, JediResponse res) 
	throws WebActionException {
		ErrorLogger.debug("#############################GET BANNED TERMS WEBACTION ############################");
		
		type = req.param.getString("type", "");
		sdate = req.param.getString("sdate", "");
		edate = req.param.getString("edate", "");
		ced_no = req.param.getString("ced_no", "");
		org1_cd = req.param.getString("org1_cd", "");
		org2_flag = req.param.getString("org2_flag", "");
		org2_cd = req.param.getString("org2_cd", "");
		org3_cd = req.param.getString("org3_cd", "");
		user_id = req.param.getString("user_id", "");
		istp_cd = req.param.getString("istp_cd", "");
		prod_cd = req.param.getString("prod_cd", "");
		grade = req.param.getString("grade", "");
		anl_stat = req.param.getString("anl_stat", "");
		ban_grp = req.param.getString("ban_grp", "");
		start = req.param.getInt("_START", 0);
		pagesize = req.param.getInt("_PAGESIZE", 20);
		new_user_id = req.param.getString("new_user_id", "");
		//user_flag = req.param.getString("user_flag", "");
		excel_down = req.param.getBoolean("excel_down", false);
		
		sql = "";
		
		SQLParam sqlparam = new SQLParam();
		sqlparam.setSqlName("oba.oba020.getBanCode.sel");			
		sqlparam.addValue("ban_grp",  ban_grp);
		sqlparam.addValue("SDATE",  sdate);
		sqlparam.addValue("EDATE",  edate);
		sqlparam.addValue("ORG1_CD",  org1_cd);
		sqlparam.addValue("ORG2_CD",  org2_cd);
		sqlparam.addValue("ORG2_FLAG",  org2_flag);
		sqlparam.addValue("ORG3_CD",  org3_cd);
		/*sqlparam.addValue("user_flag",  user_flag);
		
		if("Y".equals(user_flag)){
			for(int i =1; i<=20; i++){
				String v_user_id = "USER_ID"+ i;
				sqlparam.addValue(v_user_id,  req.param.getString(v_user_id, ""));
			}
		}*/
		sqlparam.addValue("USER_ID",  user_id);
		sqlparam.addValue("ISTP_CD",  istp_cd);
		sqlparam.addValue("PROD_CD",  prod_cd);
		sqlparam.addValue("GRADE",  grade);
		sqlparam.addValue("ANL_STAT",  anl_stat);
		sqlparam.addValue("CED_NO",  ced_no);
		sqlparam.addValue("new_user_id",  new_user_id);

		SQLParam sqlresult;
		try {
			sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			DS_CODE = sqlresult.getListParam("DS_RES");
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(excel_down) getExcelDown(req,res);
		else{
			if("S".equals(type) || "DGN_DETAIL".equals(type)){
				sql += ",MAX(R.DURATION) AS DURATION,MAX(R.CALL_CNT) CALL_CNT,R.CED_NO,R.BASE_DATE";
				groupby = "R.CED_NO, R.USER_NAME, R.USER_ID, R.BASE_DATE";
				orderby="R.BASE_DATE DESC";
				cntgroupby = "AND S1.CON_ENT_DGN_NO = R.CED_NO AND S1.USER_ID=R.USER_ID";
				getBannedTerms(req, res);
			}else if("U".equals(type)){
				sql += ",SUM(R.DURATION) AS DURATION"
					+ ",SUM(R.CALL_CNT) CALL_CNT"
					+ ",(SELECT DEPT_NAME FROM ms_dept WHERE DEPT_CD = R.ORG2_CD) AS ORG2_NAME"
					+ ",(SELECT DEPT_NAME FROM ms_dept WHERE DEPT_CD = R.ORG3_CD) AS ORG3_NAME"
					+ "";
				
				groupby = "R.ORG1_CD, R.ORG2_CD, R.ORG3_CD, R.USER_ID, R.USER_NAME";
				orderby="R.ORG1_CD, R.ORG2_CD, R.ORG3_CD,R.USER_ID, R.USER_NAME";
				cntgroupby = "AND S2.USER_ID = R.USER_ID  GROUP BY S1.UCID";
				getBannedTerms(req, res);
			}else if("CALL_DETAIL".equals(type)){
				getBannedTermsDetail(req, res);
			}	
		}	
	}
	
	/*
	 * 금지어 결과조회(설계번호 기준/소속별 기준)
	 * */
	public void getBannedTerms(JediRequest req,JediResponse res){
		
		//ErrorLogger.debug("#########상세정보 보기??");
		for(int i =0; i< DS_CODE.rowSize(); i++){
			//sql += ",(CASE WHEN SUM(CASE WHEN B.MENT_CD = '" + code[i].getCodeId() + "' THEN 1 ELSE 0 END) = (COUNT(DISTINCT r.UCID)) THEN 'Y' ELSE 'N' END ) AS TERMS_" + (i+1);
			//sql += ",MAX(CASE WHEN B.MENT_CD = '" + code[i].getCodeId() + "'and B.COUNT =1 THEN 'Y' ELSE '-' END) AS TERMS_" + (i+1)+ "_CNT";
			if("S".equals(type) || "DGN_DETAIL".equals(type)){
				sql += ",MAX(CASE WHEN R.BAN_CD  LIKE '%" + DS_CODE.getValue(i, "CODE_ID") + "%'and BAN_CNT=1 THEN 'Y' ELSE '-' END) AS TERMS_" + (i+1)+ "_CNT";
			}else if("U".equals(type)){
				sql += ",SUM(CASE WHEN R.BAN_CD  LIKE '%" + DS_CODE.getValue(i, "CODE_ID") + "%' THEN BAN_CNT ELSE 0 END) AS TERMS_" + (i+1)+ "_CNT";
			}
		}
		
		
		try {
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("oba.oba020.getBannedTerms.cnt.sel");			
			sqlparam.addValue("sql",  sql);

			sqlparam.addValue("SDATE",  sdate);
			sqlparam.addValue("EDATE",  edate);
			sqlparam.addValue("ORG1_CD",  org1_cd);
			sqlparam.addValue("ORG2_CD",  org2_cd);
			sqlparam.addValue("ORG2_FLAG",  org2_flag);
			sqlparam.addValue("ORG3_CD",  org3_cd);
			/*sqlparam.addValue("user_flag",  user_flag);
			
			if("Y".equals(user_flag)){
				for(int i =1; i<=20; i++){
					String v_user_id = "USER_ID"+ i;
					sqlparam.addValue(v_user_id,  req.param.getString(v_user_id, ""));
				}
			}*/
			
			sqlparam.addValue("USER_ID",  user_id);
			sqlparam.addValue("ISTP_CD",  istp_cd);
			sqlparam.addValue("PROD_CD",  prod_cd);
			sqlparam.addValue("GRADE",  grade);
			sqlparam.addValue("ANL_STAT",  anl_stat);
			sqlparam.addValue("CED_NO",  ced_no);
			sqlparam.addValue("new_user_id",  new_user_id);
			sqlparam.addValue("BAN_GRP",  ban_grp);
			sqlparam.addValue("groupby",  groupby);
			sqlparam.addValue("cntgroupby",  cntgroupby);
			sqlparam.addValue("cntselect",  cntselect);
			sqlparam.addValue("cntjoin",  cntjoin);
			sqlparam.addValue("orderby",  orderby);
			
			SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			ListParam DS_CNT_LIST = sqlresult.getListParam("DS_CNT_LIST");
			
			SQLParam sqlparam2 = new SQLParam();
			sqlparam2.setSqlName("oba.oba020.getBannedTerms.sel");		
			sqlparam2.addValue("sql",  sql);
			sqlparam2.addValue("SDATE",  sdate);
			sqlparam2.addValue("EDATE",  edate);
			sqlparam2.addValue("ORG1_CD",  org1_cd);
			sqlparam2.addValue("ORG2_CD",  org2_cd);
			sqlparam2.addValue("ORG2_FLAG",  org2_flag);
			sqlparam2.addValue("ORG3_CD",  org3_cd);
			/*sqlparam2.addValue("user_flag",  user_flag);
			
			if("Y".equals(user_flag)){
				for(int i =1; i<=20; i++){
					String v_user_id = "USER_ID"+ i;
					sqlparam2.addValue(v_user_id,  req.param.getString(v_user_id, ""));
				}
			}*/
			
			sqlparam2.addValue("USER_ID",  user_id);
			sqlparam2.addValue("ISTP_CD",  istp_cd);
			sqlparam2.addValue("PROD_CD",  prod_cd);
			sqlparam2.addValue("GRADE",  grade);
			sqlparam2.addValue("ANL_STAT",  anl_stat);
			sqlparam2.addValue("CED_NO",  ced_no);
			sqlparam2.addValue("new_user_id",  new_user_id);
			sqlparam2.addValue("BAN_GRP",  ban_grp);
			sqlparam2.addValue("groupby",  groupby);
			sqlparam2.addValue("cntgroupby",  cntgroupby);
			sqlparam2.addValue("cntselect",  cntselect);
			sqlparam2.addValue("cntjoin",  cntjoin);
			sqlparam2.addValue("orderby",  orderby);
			sqlparam2.addValue("_START",  start);
			sqlparam2.addValue("_PAGESIZE",  pagesize);
			SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
			ListParam DS_RES = sqlresult2.getListParam("DS_RES");

			//ErrorLogger.debug(DS_RES.toString());
			
			res.param.addValue("DS_RES",DS_RES);
			res.param.addValue("DS_CNT_LIST",DS_CNT_LIST);
			res.param.addValue("DS_HEAD",DS_CODE);
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 금지어 상세결과 조회(콜별 기준) - CALL_DETAIL만.
	 * */
	public void getBannedTermsDetail(JediRequest req,JediResponse res){
		sql = "";
		//ErrorLogger.debug("#########Detail Info");
		
		
		for(int i =0; i< DS_CODE.rowSize(); i++){
			//sql += ",(CASE WHEN SUM(CASE WHEN r.BAN_ID = '" + code[i].getCodeId() + "' THEN 1 ELSE 0 END) = 1 THEN 'Y' ELSE 'N' END ) AS TERMS_" + (i+1); 
			//sql += ",SUM(CASE WHEN B.MENT_CD = '" + code[i].getCodeId() + "' THEN B.COUNT ELSE 0 END) AS TERMS_" + (i+1)+ "_CNT";
			sql += ",MAX(CASE WHEN S.UCID IS NOT NULL AND S.MENT_CD  = '" + DS_CODE.getValue(i, "CODE_ID") + "' THEN 'Y' ELSE '-' END) AS TERMS_" + (i+1)+ "_CNT";
		}

		
		try {
			
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("oba.oba020.getBannedTermsDetail.cnt.sel");			
			sqlparam.addValue("sql",  sql);
			//원래 user_id, ced_no만 있었음.
			sqlparam.addValue("SDATE",  sdate);
			sqlparam.addValue("EDATE",  edate);
			sqlparam.addValue("ORG1_CD",  org1_cd);
			sqlparam.addValue("ORG2_CD",  org2_cd);
			sqlparam.addValue("ORG2_FLAG",  org2_flag);
			sqlparam.addValue("ORG3_CD",  org3_cd);
			/*sqlparam.addValue("user_flag",  user_flag);
			
			if("Y".equals(user_flag)){
				for(int i =1; i<=20; i++){
					String v_user_id = "USER_ID"+ i;
					sqlparam.addValue(v_user_id,  req.param.getString(v_user_id, ""));
				}
			}*/
			
			sqlparam.addValue("USER_ID",  user_id);
			sqlparam.addValue("ISTP_CD",  istp_cd);
			sqlparam.addValue("PROD_CD",  prod_cd);
			sqlparam.addValue("BAN_GRP",  ban_grp);
			sqlparam.addValue("GRADE",  grade);
			sqlparam.addValue("ANL_STAT",  anl_stat);
			sqlparam.addValue("CED_NO",  ced_no);
			sqlparam.addValue("new_user_id",  new_user_id);

			SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			ListParam DS_CNT_LIST = sqlresult.getListParam("DS_CNT_LIST");
			res.param.addValue("DS_CNT_LIST",DS_CNT_LIST);
			
			SQLParam sqlparam2 = new SQLParam();
			sqlparam2.setSqlName("oba.oba020.getBannedTermsDetail.sel");		
			sqlparam2.addValue("sql",  sql);
			//원래 user_id, ced_no만 있었음.
			sqlparam2.addValue("SDATE",  sdate);
			sqlparam2.addValue("EDATE",  edate);
			sqlparam2.addValue("ORG1_CD",  org1_cd);
			sqlparam2.addValue("ORG2_CD",  org2_cd);
			sqlparam2.addValue("ORG2_FLAG",  org2_flag);
			sqlparam2.addValue("ORG3_CD",  org3_cd);
			/*sqlparam2.addValue("user_flag",  user_flag);
			
			if("Y".equals(user_flag)){
				for(int i =1; i<=20; i++){
					String v_user_id = "USER_ID"+ i;
					sqlparam2.addValue(v_user_id,  req.param.getString(v_user_id, ""));
				}
			}*/
			
			sqlparam2.addValue("USER_ID",  user_id);
			sqlparam2.addValue("ISTP_CD",  istp_cd);
			sqlparam2.addValue("PROD_CD",  prod_cd);
			sqlparam2.addValue("BAN_GRP",  ban_grp);
			sqlparam2.addValue("GRADE",  grade);
			sqlparam2.addValue("ANL_STAT",  anl_stat);
			sqlparam2.addValue("CED_NO",  ced_no);
			sqlparam2.addValue("new_user_id",  new_user_id);
			sqlparam2.addValue("_START",  start);
			sqlparam2.addValue("_PAGESIZE",  pagesize);
			
			SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
			ListParam DS_RES_DETAIL = sqlresult2.getListParam("DS_RES_DETAIL");
			
			res.param.addValue("DS_RES_DETAIL",DS_RES_DETAIL);
			res.param.addValue("DS_HEAD",DS_CODE);
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private void getExcelDown(JediRequest req,JediResponse res){
		
		ListParam DS_RES = null;
		
		if("CALL_DETAIL".equals(type)){
			for(int i =0; i< DS_CODE.rowSize(); i++){
				sql += ",MAX(CASE WHEN S.UCID IS NOT NULL AND S.MENT_CD  = '" + DS_CODE.getValue(i, "CODE_ID") + "' THEN 'Y' ELSE '-' END) AS TERMS_" + (i+1)+ "_CNT";
			}
			
			try{
				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName("oba.oba020.getBannedTermsDetail.all.sel");		
				sqlparam2.addValue("sql",  sql);
				//원래 user_id, ced_no만 있었음.
				sqlparam2.addValue("SDATE",  sdate);
				sqlparam2.addValue("EDATE",  edate);
				sqlparam2.addValue("ORG1_CD",  org1_cd);
				sqlparam2.addValue("ORG2_CD",  org2_cd);
				sqlparam2.addValue("ORG2_FLAG",  org2_flag);
				sqlparam2.addValue("ORG3_CD",  org3_cd);
				sqlparam2.addValue("USER_ID",  user_id);
				sqlparam2.addValue("ISTP_CD",  istp_cd);
				sqlparam2.addValue("PROD_CD",  prod_cd);
				sqlparam2.addValue("BAN_GRP",  ban_grp);
				sqlparam2.addValue("GRADE",  grade);
				sqlparam2.addValue("ANL_STAT",  anl_stat);
				sqlparam2.addValue("CED_NO",  ced_no);
				sqlparam2.addValue("new_user_id",  new_user_id);
				sqlparam2.addValue("_START",  start);
				sqlparam2.addValue("_PAGESIZE",  pagesize);
				
				SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
				DS_RES = sqlresult2.getListParam("DS_RES_DETAIL");
				
				
			}catch(Exception e){
				e.printStackTrace();
				ErrorLogger.error("getBannedTermsWebAction ExcelDown Error : "+e.getMessage());
			}
			
		}else{
			if("S".equals(type) || "DGN_DETAIL".equals(type)){
				sql += ",MAX(R.DURATION) AS DURATION,MAX(R.CALL_CNT) CALL_CNT,R.CED_NO,R.BASE_DATE";
				groupby = "R.CED_NO, R.USER_NAME, R.USER_ID, R.BASE_DATE";
				orderby="R.BASE_DATE DESC";
				cntgroupby = "AND S1.CON_ENT_DGN_NO = R.CED_NO AND S1.USER_ID=R.USER_ID";
			}else if("U".equals(type)){
				sql += ",SUM(R.DURATION) AS DURATION"
					+ ",SUM(R.CALL_CNT) CALL_CNT"
					+ ",(SELECT DEPT_NAME FROM ms_dept WHERE DEPT_CD = R.ORG2_CD) AS ORG2_NAME"
					+ ",(SELECT DEPT_NAME FROM ms_dept WHERE DEPT_CD = R.ORG3_CD) AS ORG3_NAME"
					+ "";
				groupby = "R.ORG1_CD, R.ORG2_CD, R.ORG3_CD, R.USER_ID, R.USER_NAME";
				orderby="R.ORG1_CD, R.ORG2_CD, R.ORG3_CD,R.USER_ID, R.USER_NAME";
				cntgroupby = "AND S2.USER_ID = R.USER_ID  GROUP BY S1.UCID";
			}
			
			for(int i =0; i< DS_CODE.rowSize(); i++){
				if("S".equals(type)  || "DGN_DETAIL".equals(type)){
					sql += ",MAX(CASE WHEN R.BAN_CD  LIKE '%" + DS_CODE.getValue(i, "CODE_ID") + "%' and BAN_CNT=1 THEN 'Y' ELSE '-' END) AS TERMS_" + (i+1)+ "_CNT";
				}else if("U".equals(type)){
					sql += ",SUM(CASE WHEN R.BAN_CD  LIKE '%" + DS_CODE.getValue(i, "CODE_ID") + "%' THEN BAN_CNT ELSE 0 END) AS TERMS_" + (i+1)+ "_CNT";
				}
			}
			
			try{
				
				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName("oba.oba020.getBannedTerms.all.sel");		
				sqlparam2.addValue("sql",  sql);
				sqlparam2.addValue("SDATE",  sdate);
				sqlparam2.addValue("EDATE",  edate);
				sqlparam2.addValue("ORG1_CD",  org1_cd);
				sqlparam2.addValue("ORG2_CD",  org2_cd);
				sqlparam2.addValue("ORG2_FLAG",  org2_flag);
				sqlparam2.addValue("ORG3_CD",  org3_cd);
				/*sqlparam2.addValue("user_flag",  user_flag);
				
				if("Y".equals(user_flag)){
					for(int i =1; i<=20; i++){
						String v_user_id = "USER_ID"+ i;
						sqlparam2.addValue(v_user_id,  req.param.getString(v_user_id, ""));
					}
				}*/
				
				sqlparam2.addValue("USER_ID",  user_id);
				sqlparam2.addValue("ISTP_CD",  istp_cd);
				sqlparam2.addValue("PROD_CD",  prod_cd);
				sqlparam2.addValue("GRADE",  grade);
				sqlparam2.addValue("ANL_STAT",  anl_stat);
				sqlparam2.addValue("CED_NO",  ced_no);
				sqlparam2.addValue("new_user_id",  new_user_id);
				sqlparam2.addValue("BAN_GRP",  ban_grp);
				sqlparam2.addValue("groupby",  groupby);
				sqlparam2.addValue("cntgroupby",  cntgroupby);
				sqlparam2.addValue("cntselect",  cntselect);
				sqlparam2.addValue("cntjoin",  cntjoin);
				sqlparam2.addValue("orderby",  orderby);
				SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
				DS_RES = sqlresult2.getListParam("DS_RES");

			}catch(Exception e){
				e.printStackTrace();
				ErrorLogger.error("getBannedTermsWebAction ExcelDown Error : "+e.getMessage());
			}
			
		}
		
		if(DS_RES != null ) res.param.addValue("DS_EXCEL_RES",DS_RES);
		res.param.addValue("DS_HEAD",DS_CODE);
		
		
	}
	
}


