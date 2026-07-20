package sens.src.estimate;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class EstimationWebAction extends XwingWebAction {

	HttpServletRequest request = null;
	public void perform(JediRequest req, JediResponse res)
			throws WebActionException {
		request = req.getHttpServletRequest();
		ErrorLogger.debug("############################## EstimateWebactionWebAction.java ############################");
		
		String cmd = req.param.getString("cmd", "");

		if("initParam".equals(cmd)){  // 기본 파라미터 세팅
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String user_id = req.param.getString("user_id", "");
			
			getBasicParameter(res, ent_dgn_no, user_id);
		}else if("getCodes".equals(cmd)){ // aigen codebook 호출
			String codeType = req.param.getString("code_type");
			getTMCodeInfo(res, codeType);
		}else if("getWorkDate".equals(cmd)){ // 당월인지 체크, 마감일자 체크
			String app_date = req.param.getString("app_date", "");
			String clse_day = req.param.getString("clse_day", "");
			getWorkDate(res,app_date, clse_day);
		}else if("DmboStatus".equals(cmd)){ // 담보 코드 조회
			String tm_no = req.param.getString("tm_no", "");
			getDmboStatus(res, tm_no);
		}else if("QAStatus".equals(cmd)){ // QC 심사 상태 조회
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String qacd = req.param.getString("qacd", "");
			getQAEvaluateStatus(res, ent_dgn_no, qacd);
		}else if("ViewStatus".equals(cmd)){ // 타인이 화면 조회하는지 체크
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			getViewStatus(res, ent_dgn_no);
		}else if("UpdViewStatus".equals(cmd)){ //  화면 오픈 정보 업데이트
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String user_id = req.param.getString("user_id", "");
			String type = req.param.getString("type", "");

			setViewStatus(res, ent_dgn_no, user_id, type);
		}else if("PoaInterface".equals(cmd)){ // 사망담보 인터페이스 --> CORE 데이터라서 DB 조회가 아님
			String server_gb = req.param.getString("server_gb", "");
			String prod_cd = req.param.getString("prod_cd", "");
			String dmbo_cd = req.param.getString("dmbo_cd", "");
			String service_name = req.param.getString("service_name", "");
			String attr_type = req.param.getString("attr_type", "");
			
			getPoaValue(res, service_name, server_gb, attr_type, prod_cd, dmbo_cd);

		}else if("opinionbyqc".equals(cmd)){
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String qacd = req.param.getString("qacd", "");
			String htid = req.param.getString("htid", "");
			
			getOpinionDetailQC(res, ent_dgn_no, qacd, htid);
		}else if("getEvalCode".equals(cmd)){
			String code_depth = req.param.getString("code_depth", "");
			getEstimateCode(res, code_depth);
		}else if("getEstList".equals(cmd)){
			String sql = req.param.getString("sql", "");
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String qacd = req.param.getString("qacd", "");
			String htid = req.param.getString("htid", "");
			
			getEstimateList(res, sql,ent_dgn_no, qacd, htid);
			
		}else if("SavePreJudgeInfo".equals(cmd)){

			EstSaveWebaction est_save = new EstSaveWebaction(req.getCommonDTO(), req.param, res);
			
		}else if("estmodHistory".equals(cmd)){
			String est_item_ver = req.param.getString("est_item_ver", "");
			String htid = req.param.getString("htid", "");
			String htdgree = req.param.getString("htdgree", "");
			
			getEstModHistory(res, est_item_ver,htid,htdgree);
		}else if("saveActHist".equals(cmd)){
			String con_ent_dgn_no = req.param.getString("con_ent_dgn_no", "");
			String cust_no = req.param.getString("cust_no", "");
			String prt_cd = req.param.getString("prt_cd", "");
			String con_ip_psn_name = req.param.getString("con_ip_psn_name", "");
			String rel_cd = req.param.getString("rel_cd", "");
			String app_act = req.param.getString("app_act", "");
			String act_hist = req.param.getString("act_hist", "");
			String center_cd = req.param.getString("center_cd", "");
			String team_cd = req.param.getString("team_cd", "");
			String agent_id = req.param.getString("agent_id", "");
			String agent_nm = req.param.getString("agent_nm", "");
			String call_id = req.param.getString("call_id", "");
			String ucid = req.param.getString("ucid", "");
			
			setSaveActHist(res, con_ent_dgn_no, cust_no, prt_cd, con_ip_psn_name, rel_cd, app_act, act_hist, center_cd, team_cd, agent_id, agent_nm, call_id, ucid);

		}else if("getEstListExcel".equals(cmd)){
			String ent_dgn_no = req.param.getString("ent_dgn_no", "");
			String qacd = req.param.getString("qacd", "");
			String htid = req.param.getString("htid", "");
			
			getEstListExcel(res, ent_dgn_no, qacd, htid);
		}
	}

	/* 
	 * 초기에 필요한 파리미터 정보들 조회해서 세팅해주기 위해
	 * */
	private void getBasicParameter(JediResponse res, String ent_dgn_no, String user_id){
		
		ErrorLogger.debug("#####getBasicParameter Function#####");
		IVRLogger.info("#######basic parameter search :: con_ent_dgn_no ::" + ent_dgn_no +"#########");
		
		ListParam DS_PARAM_01 = null;
		ListParam DS_PARAM_02 = null;
		
		try{
			JSONArray arr = new JSONArray();
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getInitParameter_1");
			sqlParam.addValue("ENT_DGN_NO", ent_dgn_no);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if(sqlResult.getCount() > 0){
				DS_PARAM_01 = sqlResult.getListParam("DS_PARAM");
				arr = convertJson(DS_PARAM_01);	
			}
			
			res.param.addValue("DS_PARAM_01", arr);
			
			sqlParam.clear();
			sqlParam.setSqlName("msens.est.hansol.getInitParameter_2");
			sqlParam.addValue("USER_ID", user_id);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			
			if(sqlResult.getCount() > 0){
				DS_PARAM_02 = sqlResult.getListParam("DS_PARAM");
				arr = convertJson(DS_PARAM_02);	
			}
			
			res.param.addValue("DS_PARAM_02", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "초기 파라미터 조회 과정에서 오류가 발생했습니다. \n사유 : "  + e.getMessage() );
			
			IVRLogger.info("##### getBasicParameter Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getBasicParamter Error Message = " + e.getMessage());
		}
	}
	
	/*
	 * AIGEN 코드북 조회 함수
	 * */
	private void getTMCodeInfo(JediResponse res, String codeType){
		
		IVRLogger.info("##############tm codebook search ####################");
		
		try{
			StringTokenizer st = new StringTokenizer(codeType, ",");
			ListParam DS_TM_CODE = null;
			//JSONObject obj = new JSONObject();
			
			while (st.hasMoreTokens()) {
				String type = st.nextToken();
				
				SQLParam sqlParam = new SQLParam();
				sqlParam.setSqlName("msens.est.hansol.getTMCodes.sel");
				sqlParam.addValue("CODETYPE", type);
				SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
				
				JSONArray arr= new JSONArray();
				
				if(sqlResult.getCount() > 0){
					DS_TM_CODE = sqlResult.getListParam("DS_TM_CODE");				
					arr = convertJson(DS_TM_CODE);	
				}
	
				res.param.addValue("DS_TM_CODE", arr);
			}
			
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "TM 코드북 조회 과정에서 발생했습니다. \n사유 : "  + e.getMessage() );
			
			IVRLogger.info("##### getTMCodeInfo Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getTMCodeInfo Error Message = " + e.getMessage());
		}
		
	}
	
	/*
	 * 영업일자 기준으로 수정여부 FALG를 가져오는 함수 
	 * */
	private void getWorkDate(JediResponse res, String app_date, String clse_day){
		
		IVRLogger.info("############## getWorkDate ####################");
		
		ListParam DS_WORKDATE = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getWorkDate.sel");
			sqlParam.addValue("APP_DATE", app_date);
			sqlParam.addValue("CLSE_DAY", clse_day);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_WORKDATE = sqlResult.getListParam("DS_WORKDATE");
				arr = convertJson(DS_WORKDATE);	
			}
			
			res.param.addValue("DS_WORKDATE", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "영업일자 기준 수정 여부 조회 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getWorkDate Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getWorkDate Error Message = " + e.getMessage());
		}
	
	}
	
	/*
	 * 현재 설계번호에 대한 평가표 오픈 여부를 조회하는 함수
	 * */
	private void getViewStatus(JediResponse res, String ent_dgn_no) {
		
		IVRLogger.info("############## getViewStatus :: con_ent_dgn_no ::  "+ent_dgn_no+" ####################");
		
		ListParam DS_VIEW_STATUS = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getViewStatus.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_VIEW_STATUS = sqlResult.getListParam("DS_VIEW_STATUS");
				arr = convertJson(DS_VIEW_STATUS);	
				
			}
			
			res.param.addValue("DS_VIEW_STATUS", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "평가표 오픈 정보를 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getViewStatus Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getViewStatus Error Message = " + e.getMessage());
		}
	}
	
	/*
	 * 평가표 화면 오픈 정보 저장 함수
	 * */
	private void setViewStatus(JediResponse res, String ent_dgn_no, String user_id, String type) {
		
		IVRLogger.info("############## setViewStatus :: con_ent_dgn_no :: "+ent_dgn_no + " :: user_id :: "+user_id+" ####################");
		
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		try{
			tran.begin();
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.setViewStatus.upd");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("USER_ID", user_id);
			sqlParam.addValue("TYPE", type);
			
			SQLServiceManager.getInstance().execute(sqlParam, tran);
			
			tran.commit();
			
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
			//res.param.addValue("DS_VIEW_STATUS", arr);
			
		}catch(Exception e){
			tran.rollback();
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "평가표 오픈 정보를 저장하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### setViewStatus Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction setViewStatus Error Message = " + e.getMessage());
		}
	}
	
	/*
	 * 사망담보 존재 여부 조회하기 위해 해당 설계번호에 등록된 담보 정보를 조회하는 함수
	 * */
	private void getDmboStatus(JediResponse res, String tm_no){
		
		IVRLogger.info("##############getDmboStatus##################");
		
		ListParam DS_DMBO_STATUS = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getDmboStatus.sel");
			sqlParam.addValue("TM_NO", tm_no);
			//ErrorLogger.debug("tm_no " + tm_no);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_DMBO_STATUS = sqlResult.getListParam("DS_DMBO_STATUS");
				arr = convertJson(DS_DMBO_STATUS);
				
			}
			
			res.param.addValue("DS_DMBO_STATUS", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "담보정보를 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getDmboStatus Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getDmboStatus Error Message = " + e.getMessage());
		}
	
	}
	
	/*
	 * QA평가의 심사 상태 조회하는 함수
	 * */
	private void getQAEvaluateStatus(JediResponse res, String ent_dgn_no, String qacd) {
		ListParam DS_QA_STATUS = null;
		
		IVRLogger.info("##############getQAEvaluateStatus :: con_ent_dgn_no :: "+ent_dgn_no +" ##################");
		 
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getQAEvaluateStatus.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("QACD", qacd);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_QA_STATUS = sqlResult.getListParam("DS_QA_STATUS");
				arr = convertJson(DS_QA_STATUS);	
			}
			
			res.param.addValue("DS_QA_STATUS", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "심사상태를 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getQAEvaluateStatus Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getQAEvaluateStatus Error Message = " + e.getMessage());
		}
	}
	
	/*
	 * 사망담보 조회 하는 부분 -->  core interface 
	 * */
	private void getPoaValue(JediResponse res, String service_name, String server_gb, String attr_type, String prod_cd, String dmbo_cd) {
		
		IVRLogger.info("##############getPoaValue##################");
		
		try{
			TmInterWebaction tm_inter = new TmInterWebaction(server_gb);
			
			
			JSONObject obj = tm_inter.get_LTTMINFR0128(service_name, prod_cd, dmbo_cd, attr_type);
			
			ErrorLogger.debug("###결과 확인");
			ErrorLogger.debug(obj.toString());
			
			String res_cd = obj.get("_ResultCode").toString();
			
			if(!"0".equals(res_cd)){
				Object res_msg = "null".equals(obj.get("_ResultMessage")) ? "" : obj.get("_ResultMessage");
				
				res.param.addValue( "result", "fail" );
				res.param.addValue( "reason", res_msg );
			}else{
			
				JSONArray DS_POA_VALUE = (JSONArray) obj.get("pdCvrAttTypeVOList");
				
				res.param.addValue("DS_POA_VALUE", DS_POA_VALUE);
				res.param.addValue( "result", "success" );
				res.param.addValue( "reason", "" );
			}
		}catch(Exception e){
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "사망담보 조회 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
		}
		
	}
	
	/*
	 * QA 심사결과 이력을 조회하는 함수
	 * */
	private void getOpinionDetailQC(JediResponse res, String ent_dgn_no, String qacd, String htid) {
		
		IVRLogger.info("##############getOpinionDetailQC :: con_ent_dgn_no :: "+ent_dgn_no+"##################");
		
		
		ListParam DS_QC_OPINION = new ListParam(new String[] {"NO", "ESTRESULTCDGD","ESTRESULTCD", "ESTREGISTDT", "PROCESS_DATE", "RT_PROCESS_DATE"
			   , "CON_ENT_DGN_NO", "QACD","HTDGREE","HTID", "ESTOPINION", "QAOPINION", "RTOPINION", "ESTREGISTID", "EST_ITEM_VER", "RTOPINION1", "MODYN"});
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getOpinionDetailQc.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("QACD", qacd);
			sqlParam.addValue("HTID", htid);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_QC_OPINION = sqlResult.getListParam("DS_QC_OPINION");
				arr = convertJson(DS_QC_OPINION);	
			}
			
		
			res.param.addValue("DS_QC_OPINION", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "심사이력을 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getOpinionDetailQC Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getOpinionDetailQC Error Message = " + e.getMessage());
		}
	}

	/*
	 * QA평가항목 관리 코드 조회
	 * */
	private void getEstimateCode(JediResponse res, String code_depth){
		
		IVRLogger.info("##############getEstimateCode##################");
		
		ListParam DS_EST_CODE = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getEvaluateCodes.sel");
			sqlParam.addValue("CODE_DEPTH", code_depth);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr= new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_EST_CODE = sqlResult.getListParam("DS_EST_CODE");
				arr = convertJson(DS_EST_CODE);	
			}
			
			res.param.addValue("DS_EST_CODE", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "평가항목 관리코드를 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getEstimateCode Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getEstimateCode Error Message = " + e.getMessage());
		}
	} 
	
	/*
	 * 평가항목 리스트 조회 함수
	 * */
	private void getEstimateList(JediResponse res, String sql, String ent_dgn_no, String qacd, String htid){
		
		IVRLogger.info("##############getEstimateList :: con_ent_dgn_no :: "+ent_dgn_no+" ##################");
		
		ListParam DS_EST_LIST = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName(sql);
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("QACD", qacd);
			sqlParam.addValue("HTID", htid);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr = new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_EST_LIST = sqlResult.getListParam("DS_EST_LIST");
				arr = convertJson(DS_EST_LIST);	
			}
			
			res.param.addValue("DS_EST_LIST", arr);
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "평가항목 리스트를 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getEstimateList Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getEstimateList Error Message = " + e.getMessage());
		}
	} 
	
	/*
	 * 심사이력 변경 정보를 조회하는 함수
	 * */
	private void getEstModHistory(JediResponse res, String est_item_ver, String htid, String htdgree){
		
		IVRLogger.info("######################getEstModHistory######################");
		
		ListParam DS_MOD_HIST = null;
		
		String sql = "";
		
		if("1".equals(est_item_ver)) sql = "msens.est.hansol.getPreEstItemModList.sel"; //este0402.srhPreEstItemModList
		else sql = "msens.est.hansol.getPreEstItemModListNew.sel"; // este0402.srhPreEstItemModListNew
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName(sql);
			sqlParam.addValue("HTID", htid);
			sqlParam.addValue("HTDGREE", htdgree);
			sqlParam.addValue("EST_ITEM_VER", est_item_ver);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr = new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_MOD_HIST = sqlResult.getListParam("DS_MOD_HIST");
				arr = convertJson(DS_MOD_HIST);	
			}
			
			res.param.addValue("DS_MOD_HIST", arr);
			
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "심사결과 변경이력을 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getEstModHistory Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getEstModHistory Error Message = " + e.getMessage());
		}
		
	}
	
	/*
	 * 활동이력을 등록하는 함수
	 * */
	private void setSaveActHist(JediResponse res, String con_ent_dgn_no, String cust_no, String prt_cd, String con_ip_psn_name, String rel_cd, String app_act, 
								String act_hist, String center_cd, String team_cd, String agent_id, String agent_nm, String call_id, String ucid){
		
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		IVRLogger.info("######################setSaveActHist######################");
		
		try{
			
			//※ TB_APP_ACT_HIST 테이블 저장 권한 생기고 나서 처리할것! 
			tran.begin();
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.saveTbAppActHist.ins");
			sqlParam.addValue("CON_ENT_DGN_NO", con_ent_dgn_no);
			sqlParam.addValue("CUST_NO", cust_no);
			sqlParam.addValue("PRT_CD", prt_cd);
			sqlParam.addValue("CON_IP_PSN_NAME", con_ip_psn_name);
			sqlParam.addValue("REL_CD", rel_cd);
			sqlParam.addValue("APP_ACT", app_act);
			sqlParam.addValue("ACT_HIST", act_hist);
			sqlParam.addValue("CENTER_CD", center_cd);
			sqlParam.addValue("TEAM_CD", team_cd);
			sqlParam.addValue("AGENT_ID", agent_id);
			sqlParam.addValue("AGENT_NM", agent_nm);
			sqlParam.addValue("CALL_ID", call_id);
			sqlParam.addValue("UCID", ucid);
			
			SQLServiceManager.getInstance().execute(sqlParam, tran);

			tran.commit();
			
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );

			
		}catch(Exception e){
			
			tran.rollback();
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "활동이력을 저장하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### setSaveActHist Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction setSaveActHist Error Message = " + e.getMessage());
		}
		
	}
	
	/*
	 * 심사리스트 엑셀 다운로드 형태로 조회하는 함수
	 * */
	private void getEstListExcel(JediResponse res,String ent_dgn_no, String qacd, String htid ){
		
		IVRLogger.info("######################getEstListExcel :: con_ent_dgn_no :: "+ent_dgn_no+"######################");
		
		ListParam DS_EST_LIST_EXCEL = null;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getEstListExcel.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("QACD", qacd);
			sqlParam.addValue("HTID", htid);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			JSONArray arr = new JSONArray();
			
			if(sqlResult.getCount() > 0){
				DS_EST_LIST_EXCEL = sqlResult.getListParam("DS_EST_LIST_EXCEL");
				arr = convertJson(DS_EST_LIST_EXCEL);	
			}
			
			res.param.addValue("DS_EST_LIST_EXCEL", arr);
			
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			e.printStackTrace();
			
			res.param.addValue( "result", "fail" );
			res.param.addValue( "reason", "평가리스트_엑셀을 조회하는 과정에서 오류가 발생했습니다. \n사유 : " + e.getMessage() );
			
			IVRLogger.info("##### getEstListExcel Funtion에서 오류 발생####");
			IVRLogger.info("EstimationWebAction getEstListExcel Error Message = " + e.getMessage());
		}
		
	}
	
	/*
	 * ListParam을 JSON 형태로 변환
	 * */
	private JSONArray convertJson(ListParam param){
		JSONArray jsonArray = new JSONArray();
		
		//ErrorLogger.debug("");
		//ErrorLogger.debug(param.toString());
		try{
			
			if(param.rowSize() > 0){
				for(int i =0; i< param.rowSize(); i++){
					JSONObject obj = new JSONObject();
					
					for(int j=0; j< param.colSize(); j++){
		
						obj.put( param.getColumnName(j), param.getValue(i, j));
					}
					
					jsonArray.add(obj);
				}
			}
		}catch(Exception e){
			
		}
		
		return jsonArray;
	}
	
};

