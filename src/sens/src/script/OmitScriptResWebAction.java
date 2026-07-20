package sens.src.script;

import java.util.ArrayList;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class OmitScriptResWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String type = "";
	private String sdate = "";  //시작일자
	private String edate = ""; //종료일자
	private String ced_no = ""; //증권번호
	private String org1_cd = ""; // 조직 1레벨
	private String org2_cd = ""; //조직 2레벨
	private String org2_flag = ""; //조직 2 flag
	private String org3_cd = ""; //조직 3레벨
	private String user_id = ""; //상담사 id
	private String istp_cd = ""; //보험종목 코드
	private String prod_cd = ""; //상품코드
	private String grade = ""; //grade 등급
	private boolean new_user = false; //신규상담사 조회 여부
	private String clicked_dept = ""; //선택된 조직코드
	private String anl_yn = ""; //설계완료여부
	private ListParam DS_DEPT = null;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		ErrorLogger.debug("OmitScriptResWebAction  Start!!");
		
		type = req.param.getString("type", "");
		sdate = req.param.getString("sdate", "");
		edate = req.param.getString("edate", "");
		ced_no = req.param.getString("ced_no", "");
		org1_cd = req.param.getString("ORG1_CD", "");
		org2_cd = req.param.getString("ORG2_CD", "");
		org2_flag = req.param.getString("ORG2_FLAG", "");
		org3_cd = req.param.getString("ORG3_CD", "");
		user_id = req.param.getString("USER_ID", "");
		istp_cd =  req.param.getString("istp_cd", "");
		prod_cd =req.param.getString("prod_cd", "");
		grade =req.param.getString("grade", "");
		anl_yn =req.param.getString("anl_yn", "");
		clicked_dept =req.param.getString("clicked_dept", "");
		new_user = req.param.getBoolean("new_user", false);
		DS_DEPT = req.param.getListParam("DS_PARAM");
		ListParam DS_RES = null;
		
	
		//if("DEPT1".equals(type)){
			DS_RES = ScriptByORG1();
		//}else if("DEPT2".equals(type)){
		//	DS_RES = ScriptByORG2();
		//}
			
		res.param.addValue("DS_RES",DS_RES);
		IVRLogger.debug("OmitScriptResWebAction END!!");

	}	
	
	
	public ListParam ScriptByORG1(){
		ErrorLogger.debug("########################ScriptByORG1");
		ErrorLogger.debug(DS_DEPT.toString());
		
		ListParam ORG1_RES = null;
		
		String sql = "";
		String sql2 = "";
		String groupby = "";
		String new_user_sql = "";
		String new_user_sql1= "";
		
		
		ArrayList<String> arr = new ArrayList<String>();
		
		
		
		//컬럼 정의하기(소속별)
		if("USER".equals(type)){
			for(int i =0; i< DS_DEPT.rowSize(); i++){
				//params[i+3] = "COL_"+DS_DEPT.getValue(i, "USER_ID").toString();
				arr.add("COL_"+DS_DEPT.getValue(i, "USER_ID").toString());
				
				sql += "SUM(CASE WHEN AA.USER_ID = '" + DS_DEPT.getValue(i,  "USER_ID").toString() + "' THEN 1 ELSE 0 END) AS 'COL_" +   DS_DEPT.getValue(i,  "USER_ID").toString() + "',";
				groupby = "BB.SC_SCLF_NM";
				org3_cd = clicked_dept;
			}
		}else{
			
			if("DEPT1".equals(type)) sql2 = "AND TREATYBRHCD IN (";
			else if("DEPT2".equals(type)) sql2 = "AND TREATYDEPTCD IN (";
			
			for(int i =0; i< DS_DEPT.rowSize(); i++){
				if("N".equals(DS_DEPT.getValue(i, "ANL_USE_YN"))) {
					continue;
				}
				
				arr.add("COL_"+DS_DEPT.getValue(i, type+"_CD").toString());
				//params[i+3] = ;
				
				if("DEPT1".equals(type)){ 
					sql += "SUM(CASE WHEN AA.TREATYBRHCD = '" + DS_DEPT.getValue(i,  type+"_CD").toString() + "' THEN 1 ELSE 0 END) AS 'COL_" +   DS_DEPT.getValue(i,  type+"_CD").toString() + "',";
					groupby = "BB.SC_SCLF_NM";
					sql2 = sql2 + "'" + DS_DEPT.getValue(i,  type+"_CD").toString() + "',";
				}else if("DEPT2".equals(type)){ 
					sql += "SUM(CASE WHEN AA.TREATYDEPTCD = '" + DS_DEPT.getValue(i,  type+"_CD").toString() + "' THEN 1 ELSE 0 END) AS 'COL_" +   DS_DEPT.getValue(i,  type+"_CD").toString() + "',";
					groupby = "BB.SC_SCLF_NM";
					org2_cd = clicked_dept;
					sql2 = sql2 + "'" + DS_DEPT.getValue(i,  type+"_CD").toString() + "',";
				}
			}
		}
		
		
		String [] params = new String[arr.size()+2];
		
		params[0] = "SC_SCLF_NM";
		params[1] = "CED_CNT";
		
		for(int i = 0; i< arr.size();i++){
			params[i+2] = arr.get(i);
		}
		
		sql = sql.substring(0, sql.length()-1);
		if(sql2.length() > 0) sql2 = sql2.substring(0, sql2.length()-1) + ")";
		
		ORG1_RES = new ListParam(params);


		try {
			
			SQLParam sqlParam = new SQLParam();

			new_user_sql = new_user == true ? "true" : "";
			
			IVRLogger.debug("ORG1 - " + org1_cd);
			sqlParam.setSqlName("bis.bis030.omitScriptbyDept.sel");
			sqlParam.addValue("sql", sql);
			sqlParam.addValue("sql2", sql2);
			sqlParam.addValue("new_user_id", new_user_sql);
			sqlParam.addValue("CED_NO", ced_no);
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			sqlParam.addValue("ISTP_CD", istp_cd);
			sqlParam.addValue("PROD_CD", prod_cd);
			sqlParam.addValue("ORG1_CD", org1_cd);
			sqlParam.addValue("ORG2_CD", org2_cd);
			sqlParam.addValue("ORG2_FLAG", org2_flag);
			sqlParam.addValue("ORG3_CD", org3_cd);
			sqlParam.addValue("USER_ID", user_id);
			sqlParam.addValue("GRADE", grade);
			sqlParam.addValue("ANL_YN", anl_yn);
			
			
			ErrorLogger.debug(sqlParam.toString());
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			ListParam DS_RES = sqlResult.getListParam("bis.bis030.omitScriptbyDept.sel");
			
			SQLParam sqlParam1 = new SQLParam();
			
			new_user_sql1 = new_user == true ? "true" : "";
			
			ErrorLogger.debug("######NEW_USER = " + new_user_sql1);
			sqlParam1.setSqlName("bis.bis030.getsclfcd.sel");
			sqlParam1.addValue("sql", sql);
			sqlParam1.addValue("sql2", sql2);
			sqlParam1.addValue("new_user_id", new_user_sql1);
			sqlParam1.addValue("CED_NO", ced_no);
			sqlParam1.addValue("SDATE", sdate);
			sqlParam1.addValue("EDATE", edate);
			sqlParam1.addValue("ISTP_CD", istp_cd);
			sqlParam1.addValue("PROD_CD", prod_cd);
			sqlParam1.addValue("ORG1_CD", org1_cd);
			sqlParam1.addValue("ORG2_FLAG", org2_flag);
			sqlParam1.addValue("ORG2_CD", org2_cd);
			sqlParam1.addValue("ORG3_CD", org3_cd);
			sqlParam1.addValue("USER_ID", user_id);
			sqlParam1.addValue("GRADE", grade);
			sqlParam1.addValue("ANL_YN", anl_yn);
			sqlParam1.addValue("groupby", groupby);
			
			
			ErrorLogger.debug(sqlParam1.toString());
			
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			ListParam DS_SCLF_CD = sqlResult1.getListParam("bis.bis030.getsclfcd.sel");
			
			Object obj[] = new Object[params.length];
			
			if(sqlResult.getCount() >0){
				for(int i =0; i< DS_RES.rowSize();i++){			
					for(int j=0; j< obj.length;j++){
						if( j == 1) obj[j] = DS_SCLF_CD.getValue(i, j);
						else if(j < 2) obj[j] = DS_RES.getValue(i, j);
						else{
							String tot = DS_SCLF_CD.getValue(i, j).toString();
							String cnt = DS_RES.getValue(i, j).toString();
							obj[j] = cnt + "/" + tot;
						}
					}
					
					ORG1_RES.addRow(obj);
					obj = new Object[params.length];
				}
				
			}
			
			
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ORG1_RES;
	}
	
}

