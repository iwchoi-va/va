package sens.src.search ;

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
import com.locus.jedi.log.IVRLogger;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.sql.*;

import java.util.*;


public class OmitMentResWebAction extends XwingWebAction {
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
	private String istp_cd = "";
	private String prod_cd = "";
	private String anl_stat = "";
	private String grade = "";
	private boolean new_user_id = false; //신규상담사 조회 여부
	private String type = ""; // 금지어 조회 구분(C : 콜별, U : 소속별, S : 증권번호별)
	private Code[] code = null;
	private String clicked_dept = "";
	private String selected_user = "";
	private ListParam ds_vss140 = null;
	
	public void perform(JediRequest req, JediResponse res) 
	throws WebActionException {
		ErrorLogger.debug("#############################GET ESSENTAIL MENT TERMS WEBACTION ############################");
		
		type = req.param.getString("type", "");
		sdate = req.param.getString("sdate", "");
		edate = req.param.getString("edate", "");
		ced_no = req.param.getString("ced_no", "");
		org1_cd = req.param.getString("org1_cd", "");
		org2_cd = req.param.getString("org2_cd", "");
		org3_cd = req.param.getString("org3_cd", "");
		user_id = req.param.getString("user_id", "");
		istp_cd = req.param.getString("istp_cd", "");
		prod_cd = req.param.getString("prod_cd", "");
		grade = req.param.getString("grade", "");
		anl_stat = req.param.getString("anl_stat", "");
		clicked_dept = req.param.getString("clicked_dept", "");
		selected_user = req.param.getString("selected_user", "");
		new_user_id = req.param.getBoolean("new_user", false);
		ds_vss140 = req.param.getListParam("DS_VSS140");
		
		ErrorLogger.debug(ds_vss140.toString());
		
		code = CodeUtil.getCodes("VSS140");
		
		ListParam DS_RES = null;
		
		if("DEPT1".equals(type)) DS_RES = getOmitMentByDept1();
		else if("DEPT2".equals(type)) DS_RES = getOmitMentByDept2();
		else if("DEPT3".equals(type)) DS_RES = getOmitMentByUser();
		else if("DETAIL".equals(type)) DS_RES = getOmitMentDetail();
		
		res.param.addValue("DS_RES",DS_RES);
		IVRLogger.debug("OmitScriptResWebAction END!!");
		
			
	}
	
	/*
	 * 금지어 결과조회(설계번호 기준/소속별 기준)
	 * */
	public ListParam getOmitMentByDept1(){
		
		ListParam DS_DEPT_RES = null;
		
		String sql1 = "";
		String sql2 = "";
		String groupby1 = "";
		String groupby2 = "";
		String join_sql = "";
		String [] columns = null;
		int col_idx = 0;
		String new_user_sql = "";
		
			sql1 += "D.ORG1_CD, D.ORG2_CD,(SELECT DEPT_NAME FROM MS_DEPT WHERE DEPT_CD = D.ORG1_CD) AS ORG1_NM, (SELECT DEPT_NAME FROM MS_DEPT WHERE DEPT_CD = D.ORG2_CD) AS ORG2_NM,";	
			sql2 += "TREATYBRHNM, TREATYBRHCD,";		
			groupby1 = "TREATYBRHNM, TREATYBRHCD, CON_ENT_DGN_NO";
			groupby2 = "D.ORG1_CD, D.ORG2_CD";
			join_sql = "D.ORG2_CD = R.TREATYBRHCD WHERE DEPT_LEVEL = 2";
			
			columns = new String[ds_vss140.rowSize()+5];
			columns[0] = "ORG1_CD";
			columns[1] = "ORG2_CD";
			columns[2] = "ORG1_NM";
			columns[3] = "ORG2_NM";
			columns[4] = "CED_NO_CNT";
			col_idx = 5;

			if(new_user_id) new_user_sql = "AND U.WORK_MONTH <= (SELECT ETC1 FROM ms_codebook where code_type='SYS103' and code_id != 00)";
			else new_user_sql = "";
		
		for(int i=0; i< ds_vss140.rowSize(); i++){
			
				//sql1 += "CASE WHEN TERMS_"+(i+1)+"_CNT IS NULL THEN 0 ELSE TERMS_1_CNT END AS TERMS_"+(i+1)+"_CNT,";
				//sql2 += "SUM(CASE WHEN MENT_CD = '"+code[i].getCodeId()+"' THEN 0 ELSE 1 END) AS TERMS_"+(i+1)+"_CNT,";
				sql1 += "ISNULL(SUM(TERMS_"+(i+1)+"_CNT),0) AS TERMS_"+(i+1)+"_CNT,";
				sql2 += "(CASE WHEN SUM(CASE WHEN MENT_CD = '"+ds_vss140.getValue(i, "CODEID")+"' THEN 1 ELSE 0 END) > 0 THEN 0 ELSE 1 END) AS TERMS_"+(i+1)+"_CNT,";
				
				columns[col_idx+i] = "TERMS_"+(i+1)+"_CNT";
			
		}
		
		sql1 = sql1.substring(0, sql1.length()-1);
		sql2 = sql2.substring(0, sql2.length()-1);
		
		try {
			
			ErrorLogger.debug("oba.oba070.getOmitMentRes.sel");
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("oba.oba070.getOmitMentRes.sel");
			sqlParam.addValue("sql1", sql1);
			sqlParam.addValue("sql2", sql2);
			sqlParam.addValue("join_sql", join_sql);
			sqlParam.addValue("groupby1", groupby1);
			sqlParam.addValue("groupby2", groupby2);
			sqlParam.addValue("ORG1_CD",  org1_cd);
			sqlParam.addValue("ORG2_CD",  org2_cd);
			sqlParam.addValue("ORG3_CD",  org3_cd);
			sqlParam.addValue("USER_ID",  user_id);
			sqlParam.addValue("new_user_sql", new_user_sql);
			sqlParam.addValue("CED_NO", ced_no);
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			sqlParam.addValue("ISTP_CD", istp_cd);
			sqlParam.addValue("PROD_CD", prod_cd);
			sqlParam.addValue("GRADE", grade);
			sqlParam.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			ListParam DS_OMIT_RES = null;
			
			if(sqlResult.getCount() > 0){
				DS_OMIT_RES = sqlResult.getListParam("oba.oba070.getOmitMentRes.sel");
			}
			
			
			SQLParam sqlParam1 = new SQLParam();
			sqlParam1.setSqlName("oba.oba070.getCountByDept1.sel");
			sqlParam1.addValue("ORG1_CD",  org1_cd);
			sqlParam1.addValue("ORG2_CD",  org2_cd);
			sqlParam1.addValue("ORG3_CD",  org3_cd);
			sqlParam1.addValue("USER_ID",  user_id);
			sqlParam1.addValue("new_user_sql", new_user_sql);
			sqlParam1.addValue("CED_NO", ced_no);
			sqlParam1.addValue("SDATE", sdate);
			sqlParam1.addValue("EDATE", edate);
			sqlParam1.addValue("ISTP_CD", istp_cd);
			sqlParam1.addValue("PROD_CD", prod_cd);
			sqlParam1.addValue("GRADE", grade);
			sqlParam1.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			ListParam DS_OMIT_CNT = null;
			
			if(sqlResult.getCount() > 0){
				DS_OMIT_CNT = sqlResult1.getListParam("oba.oba070.getCountByDept1.sel");
			}
			
			//데이터 넣기
			
			DS_DEPT_RES = new ListParam(columns);
			Object obj[] = new Object[columns.length];
			
			for(int i=0; i< DS_OMIT_RES.rowSize() ;i++){
				String dept_cd2 = DS_OMIT_RES.getValue(i, "ORG2_CD").toString();
				for(int j=0; j< obj.length; j++){
					if(j == 4){
						int idx = DS_OMIT_CNT.findRow("ORG2_CD", dept_cd2);
						
						if(idx > -1) obj[j] = DS_OMIT_CNT.getValue(idx, "DEPT_CNT").toString();
						else  obj[j] =0;
					}else if(j > 4){
						obj[j] = DS_OMIT_RES.getValue(i, j-1).toString();
					}else{
						obj[j] = DS_OMIT_RES.getValue(i, j).toString();
					}
				}
				
				DS_DEPT_RES.addRow(obj);
				obj = new Object[columns.length];
			}
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return DS_DEPT_RES;
	}
	
	/*
	 * 금지어 결과조회(설계번호 기준/소속별 기준)
	 * */
	public ListParam getOmitMentByDept2(){
		
		ListParam DS_DEPT_RES = null;
		
		String sql1 = "";
		String sql2 = "";
		String groupby1 = "";
		String groupby2 = "";
		String join_sql = "";
		String [] columns = null;
		String new_user_sql = "";
		
		org2_cd = clicked_dept;
		
		
		
		int col_idx = 0;

			sql1 += "D.ORG1_CD, D.ORG2_CD, D.ORG3_CD, "
					+ "(SELECT DEPT_NAME FROM MS_DEPT WHERE DEPT_CD = D.ORG1_CD) AS ORG1_NM, "
					+ "(SELECT DEPT_NAME FROM MS_DEPT WHERE DEPT_CD = D.ORG2_CD) AS ORG2_NM,"
					+ "(SELECT DEPT_NAME FROM MS_DEPT WHERE DEPT_CD = D.ORG3_CD) AS ORG3_NM,";	
			sql2 += "TREATYDEPTCD, TREATYDEPTNM,";		
			groupby1= "TREATYDEPTCD, TREATYDEPTNM, CON_ENT_DGN_NO";
			groupby2 = "D.ORG1_CD, D.ORG2_CD, D.ORG3_CD";
			join_sql = "D.ORG3_CD = R.TREATYDEPTCD WHERE DEPT_LEVEL = 3";
			
			if(new_user_id) new_user_sql = "AND U.WORK_MONTH <= (SELECT ETC1 FROM ms_codebook where code_type='SYS103' and code_id != 00)";
			else new_user_sql = "";
			
			columns = new String[ds_vss140.rowSize()+7];
			columns[0] = "ORG1_CD";
			columns[1] = "ORG2_CD";
			columns[2] = "ORG3_CD";
			columns[3] = "ORG1_NM";
			columns[4] = "ORG2_NM";
			columns[5] = "ORG3_NM";
			columns[6] = "CED_NO_CNT";
			col_idx = 7;

		for(int i=0; i< ds_vss140.rowSize(); i++){
			
			//if("Y".equals(code[i].getUseYn())){
				//sql1 += "CASE WHEN TERMS_"+(i+1)+"_CNT IS NULL THEN 0 ELSE TERMS_1_CNT END AS TERMS_"+(i+1)+"_CNT,";
				
				//,CASE WHEN SUM(CASE WHEN MENT_CD = '0001' THEN 0 ELSE 1 END) >0 THEN 0 ELSE 1 END AS TERMS_1_CNT
				//sql2 += "SUM(CASE WHEN MENT_CD = '"+code[i].getCodeId()+"' THEN 0 ELSE 1 END) AS TERMS_"+(i+1)+"_CNT,";
				
				sql1 += "ISNULL(SUM(TERMS_"+(i+1)+"_CNT),0) AS TERMS_"+(i+1)+"_CNT,";
				sql2 += "(CASE WHEN SUM(CASE WHEN MENT_CD = '"+ds_vss140.getValue(i, "CODEID")+"' THEN 1 ELSE 0 END) > 0 THEN 0 ELSE 1 END) AS TERMS_"+(i+1)+"_CNT,";
				
				
				columns[col_idx+i] = "TERMS_"+(i+1)+"_CNT";
			//}
			
		}
		
		sql1 = sql1.substring(0, sql1.length()-1);
		sql2 = sql2.substring(0, sql2.length()-1);
		
		try {
			
			ErrorLogger.debug("oba.oba070.getOmitMentRes.sel");
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("oba.oba070.getOmitMentRes.sel");
			sqlParam.addValue("sql1", sql1);
			sqlParam.addValue("sql2", sql2);
			sqlParam.addValue("join_sql", join_sql);
			sqlParam.addValue("groupby1", groupby1);
			sqlParam.addValue("groupby2", groupby2);
			sqlParam.addValue("ORG1_CD",  org1_cd);
			sqlParam.addValue("ORG2_CD",  org2_cd);
			sqlParam.addValue("ORG3_CD",  org3_cd);
			sqlParam.addValue("USER_ID",  user_id);
			sqlParam.addValue("new_user_sql", new_user_sql);
			sqlParam.addValue("CED_NO", ced_no);
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			sqlParam.addValue("ISTP_CD", istp_cd);
			sqlParam.addValue("PROD_CD", prod_cd);
			sqlParam.addValue("GRADE", grade);
			sqlParam.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			ListParam DS_OMIT_RES = null;
			
			if(sqlResult.getCount() > 0){
				DS_OMIT_RES = sqlResult.getListParam("oba.oba070.getOmitMentRes.sel");
			}
			
			
			SQLParam sqlParam1 = new SQLParam();
			sqlParam1.setSqlName("oba.oba070.getCountByDept2.sel");
			sqlParam1.addValue("ORG1_CD",  org1_cd);
			sqlParam1.addValue("ORG2_CD",  org2_cd);
			sqlParam1.addValue("ORG3_CD",  org3_cd);
			sqlParam1.addValue("USER_ID",  user_id);
			sqlParam1.addValue("new_user_sql", new_user_sql);
			sqlParam1.addValue("CED_NO", ced_no);
			sqlParam1.addValue("SDATE", sdate);
			sqlParam1.addValue("EDATE", edate);
			sqlParam1.addValue("ISTP_CD", istp_cd);
			sqlParam1.addValue("PROD_CD", prod_cd);
			sqlParam1.addValue("GRADE", grade);
			sqlParam1.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			ListParam DS_OMIT_CNT = null;
			
			if(sqlResult.getCount() > 0){
				DS_OMIT_CNT = sqlResult1.getListParam("oba.oba070.getCountByDept2.sel");
			}
			
			//데이터 넣기
			
			DS_DEPT_RES = new ListParam(columns);
			Object obj[] = new Object[columns.length];
			
			for(int i=0; i< DS_OMIT_RES.rowSize() ;i++){
				String dept_cd3 = DS_OMIT_RES.getValue(i, "ORG3_CD").toString();
				for(int j=0; j< obj.length; j++){
					if(j == 6){
						int idx = DS_OMIT_CNT.findRow("ORG3_CD", dept_cd3);
						
						if(idx > -1) obj[j] = DS_OMIT_CNT.getValue(idx, "DEPT_CNT").toString();
						else  obj[j] =0;
					}else if(j > 6){
						obj[j] = DS_OMIT_RES.getValue(i, j-1).toString();
					}else{
						obj[j] = DS_OMIT_RES.getValue(i, j).toString();
					}
				}
				
				DS_DEPT_RES.addRow(obj);
				obj = new Object[columns.length];
			}
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return DS_DEPT_RES;
	}
	
	/*
	 * 금지어 결과조회(설계번호 기준/소속별 기준)
	 * */
	public ListParam getOmitMentByUser(){
		
		ListParam DS_DEPT_RES = null;
		String [] columns = null;
		String new_user_sql = "";
		
		org3_cd = clicked_dept;
		
		int col_idx = 0;
		String sql1 = "";
		String sql2 = "";
		
		if(new_user_id) new_user_sql = "AND U.WORK_MONTH <= (SELECT ETC1 FROM ms_codebook where code_type='SYS103' and code_id != 00)";
		else new_user_sql = "";
			
			columns = new String[ds_vss140.rowSize()+9];
			columns[0] = "ORG1_CD";
			columns[1] = "ORG2_CD";
			columns[2] = "ORG3_CD";
			columns[3] = "USER_ID";
			columns[4] = "ORG1_NM";
			columns[5] = "ORG2_NM";
			columns[6] = "ORG3_NM";
			columns[7] = "USER_NAME";
			columns[8] = "CED_NO_CNT";
			col_idx = 9;
		
		for(int i=0; i< ds_vss140.rowSize(); i++){			
			
			//if("Y".equals(code[i].getUseYn())){
				columns[col_idx+i] = "TERMS_"+(i+1)+"_CNT";
				
				//,SUM(CASE WHEN MENT_CD = '0001' THEN 0 ELSE 1 END) AS TERMS_1_CNT
				
				//CASE WHEN SUM(CASE WHEN MENT_CD='0001' THEN 0 ELSE 1 END) > 0 THEN 1 ELSE 0 END AS TERMS_1_CNT
				
				//ISNULL(SUM(TERMS_1_CNT),0)
				sql1 += "ISNULL(SUM(TERMS_"+(i+1)+"_CNT),0) AS TERMS_"+(i+1)+"_CNT,";
				sql2 += "CASE WHEN SUM(CASE WHEN MENT_CD='"+ds_vss140.getValue(i, "CODEID")+"' THEN 1 ELSE 0 END) > 0 THEN 0 ELSE 1 END AS TERMS_"+(i+1)+"_CNT,";
			//}
		}
		
		
		try {
			
			ErrorLogger.debug("oba.oba070.getOmitMentByUserRes.sel");
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("oba.oba070.getOmitMentByUserRes.sel");
			sqlParam.addValue("sql1",  sql1.substring(0, sql1.length()-1));
			sqlParam.addValue("sql2",  sql2.substring(0, sql2.length()-1));
			sqlParam.addValue("ORG1_CD",  org1_cd);
			sqlParam.addValue("ORG2_CD",  org2_cd);
			sqlParam.addValue("ORG3_CD",  org3_cd);
			sqlParam.addValue("USER_ID",  user_id);
			sqlParam.addValue("new_user_sql", new_user_sql);
			sqlParam.addValue("CED_NO", ced_no);
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			sqlParam.addValue("ISTP_CD", istp_cd);
			sqlParam.addValue("PROD_CD", prod_cd);
			sqlParam.addValue("GRADE", grade);
			sqlParam.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			ListParam DS_OMIT_RES = null;
			
			ErrorLogger.debug("######sqlResult");
			ErrorLogger.debug(sqlResult.toString());
			if(sqlResult.getCount() > 0){
				DS_OMIT_RES = sqlResult.getListParam("oba.oba070.getOmitMentByUserRes.sel");
			}
			
			
			SQLParam sqlParam1 = new SQLParam();
			sqlParam1.setSqlName("oba.oba070.getCountByUser.sel");
			sqlParam1.addValue("ORG1_CD",  org1_cd);
			sqlParam1.addValue("ORG2_CD",  org2_cd);
			sqlParam1.addValue("ORG3_CD",  org3_cd);
			sqlParam1.addValue("USER_ID",  user_id);
			sqlParam1.addValue("new_user_sql", new_user_sql);
			sqlParam1.addValue("CED_NO", ced_no);
			sqlParam1.addValue("SDATE", sdate);
			sqlParam1.addValue("EDATE", edate);
			sqlParam1.addValue("ISTP_CD", istp_cd);
			sqlParam1.addValue("PROD_CD", prod_cd);
			sqlParam1.addValue("GRADE", grade);
			sqlParam1.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			ListParam DS_OMIT_CNT = null;
			
			if(sqlResult.getCount() > 0){
				DS_OMIT_CNT = sqlResult1.getListParam("oba.oba070.getCountByUser.sel");
			}
			
			//데이터 넣기
			DS_DEPT_RES = new ListParam(columns);
			Object obj[] = new Object[columns.length];
			
			ErrorLogger.debug(columns.length);
			
			for(int i=0; i< DS_OMIT_RES.rowSize() ;i++){
				String user_id = DS_OMIT_RES.getValue(i, "USER_ID").toString();
				for(int j=0; j< obj.length; j++){
					if(j == 8){
						int idx = DS_OMIT_CNT.findRow("USER_ID", user_id);
						
						if(idx > -1) obj[j] = DS_OMIT_CNT.getValue(idx, "DEPT_CNT").toString();
						else  obj[j] =0;
					}else if(j > 8){
						obj[j] = DS_OMIT_RES.getValue(i, j-1).toString();
					}else{
						obj[j] = DS_OMIT_RES.getValue(i, j).toString();
					}
				}
				
				DS_DEPT_RES.addRow(obj);
				obj = new Object[columns.length];
			}
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return DS_DEPT_RES;
	}
	
public ListParam getOmitMentDetail(){
		
		ListParam DS_OMIT_DETAIL = null;
		String new_user_sql = "";
		String sql1 = "";
		
		if(new_user_id) new_user_sql = "AND U.WORK_MONTH <= (SELECT ETC1 FROM ms_codebook where code_type='SYS103' and code_id != 00)";
		else new_user_sql = "";
		
		for(int i=0; i< ds_vss140.rowSize(); i++){			
			
			//if("Y".equals(code[i].getUseYn())){
				sql1 += "(CASE WHEN SUM(CASE WHEN MENT_CD = '"+ ds_vss140.getValue(i, "CODEID") + "' THEN 1 ELSE 0 END) > 0 THEN '-' ELSE 'N' END) AS TERMS_"+(i+1)+"_CNT ,";
			//}
			//,SUM(CASE WHEN MENT_CD = '0001' THEN 0 ELSE 1 END) AS TERMS_1_CNT
			//(CASE WHEN SUM(CASE WHEN MENT_CD = '0001' THEN 1 ELSE 0 END) > 0 THEN '-' ELSE 'N' END)
			
			//sql1 += "(CASE WHEN MENT_CD = '" + code[i].getCodeId()+"' THEN '-' ELSE 'N' END) AS TERMS_"+(i+1)+"_CNT,";
		}
		
		
		try {
			
			ErrorLogger.debug("oba.oba070.getOmitMentDetailRes.sel");
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("oba.oba070.getOmitMentDetailRes.sel");
			sqlParam.addValue("sql1",  sql1.substring(0, sql1.length()-1));
			sqlParam.addValue("USER_ID",  selected_user);
			sqlParam.addValue("new_user_sql", new_user_sql);
			sqlParam.addValue("CED_NO", ced_no);
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			sqlParam.addValue("ISTP_CD", istp_cd);
			sqlParam.addValue("PROD_CD", prod_cd);
			sqlParam.addValue("GRADE", grade);
			sqlParam.addValue("ANL_YN", anl_stat);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			

			if(sqlResult.getCount() > 0){
				DS_OMIT_DETAIL = sqlResult.getListParam("oba.oba070.getOmitMentDetailRes.sel");
			}
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return DS_OMIT_DETAIL;
	}
	

}


