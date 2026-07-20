package sens.src.estimate;

import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediResponse;
 
public class EstSaveWebaction extends Thread { 
	 
    private CommonDTO common    = null;
    private Param param         = null;
    
    private JSONArray DS_EST_RES_HIST_TEMP = null;  // TB_PRE_EST_RESULT_HIST(심사결과이력데이터) 
    private JSONArray DS_EST_HIST_LOG_TEMP = null; // TB_PRE_EST_HISTORY_LOG(심사이력로그)
    private JSONArray DS_EST_RES_HIST_LOG_TEMP = null; // TB_PRE_EST_RESULT_HIST_LOG(심사결과이력로그)
    private JSONArray DS_EST_HIST_TEMP = null; // TB_PRE_EST_HISTORY(심사이력)
    private JSONArray DS_INSU_PLAN_MAST_TEMP = null; // TB_INSU_PLAN_MAST(가입설계메인테이블)
    private JSONArray DS_LTTMINFR0124 = null; // 사망동의서 관련 정보 저장필요 데이터
    private JSONArray DS_EST_LIST_IN_TEMP = null; // 저장시 평가리스트 결과 쪽에 넣어야하는 데이터들 넣는 곳
    
    private ListParam DS_EST_RES_HIST = null;
    private ListParam DS_EST_HIST_LOG = null;
    private ListParam DS_EST_RES_HIST_LOG = null;
    private ListParam DS_EST_HIST = null;
    private ListParam DS_INSU_PLAN_MAST = null;
    private ListParam DS_EST_LIST_IN = null;
    
    private String ent_dgn_no = ""; // 가입설계번호
    private String save_gb = ""; // 저장 구분
    private String cur_htdgree = ""; // 현재 dgree 
    private String cur_htid = ""; // 현 최종 HTID
    private String state_cd = ""; //청약 진행상태
    private boolean mod_yn = false; // 설계번호
    private String pre_tm_pro_gb= ""; // 현재 청약진행상태
    private String psn_yn= ""; // 계피상이 여부
    private String poa_yn= ""; // 사망담보존재여부
    private String simsa_cnt= ""; // 최종통과 건수
    private String qacd = "";
    
    
    private String server_gb = "";
    
    private boolean isCommit = false;
   
    private JediResponse v_res = null;
    
	public EstSaveWebaction(CommonDTO common, Param param ,  JediResponse res) {		
		v_res = res;
		ErrorLogger.debug("############################## EstSaveWebaction ############################");
		IVRLogger.info("############################## EstSaveWebaction start ############################");
		
		this.common = common;
        this.param = param;
        
        JSONParser parser = new JSONParser();
        
		try{
			
			DS_EST_RES_HIST_TEMP = (JSONArray) parser.parse(param.getString("DS_EST_RES_HIST"));
			DS_EST_HIST_LOG_TEMP = (JSONArray) parser.parse(param.getString("DS_EST_HIST_LOG"));
			DS_EST_RES_HIST_LOG_TEMP = (JSONArray) parser.parse(param.getString("DS_EST_RES_HIST_LOG"));
			DS_EST_HIST_TEMP = (JSONArray) parser.parse(param.getString("DS_EST_HIST"));
			DS_INSU_PLAN_MAST_TEMP = (JSONArray) parser.parse(param.getString("DS_INSU_PLAN_MAST"));
			DS_LTTMINFR0124 = (JSONArray) parser.parse(param.getString("DS_LTTMINFR0124"));
			DS_EST_LIST_IN_TEMP = (JSONArray) parser.parse(param.getString("DS_EST_LIST_IN"));
			
			ent_dgn_no = param.getString("CON_ENT_DGN_NO", "");
			save_gb = param.getString("SAVE_GB", "");
			cur_htdgree = param.getString("CUR_HTDGREE", "");
			cur_htid = param.getString("CUR_HTID", "");
			state_cd = param.getString("STATE_CD", "");
			mod_yn = param.getBoolean("MOD_YN", false);
			pre_tm_pro_gb = param.getString("PRE_TM_PROCESSING_GB", "");
			psn_yn = param.getString("PSN_YN", "");
			poa_yn = param.getString("POA_YN", "");
			simsa_cnt = param.getString("SIMSA_CNT", "");
			qacd = param.getString("QACD", "");
			
			
			server_gb = param.getString("SERVER_GB", "");
			
			IVRLogger.info("설계번호 :: " + ent_dgn_no + " :: 저장 구분 :: "+ save_gb);
			
			ErrorLogger.debug("#########데이터 가져온 정보들 확인하기!!");
			
			ErrorLogger.debug("####DS_EST_RES_HIST_TEMP");
			ErrorLogger.debug(DS_EST_HIST_TEMP.toString());
			
			ErrorLogger.debug("####DS_EST_HIST_LOG_TEMP");
			ErrorLogger.debug(DS_EST_HIST_LOG_TEMP.toString());
			
			ErrorLogger.debug("####DS_EST_RES_HIST_LOG_TEMP");
			ErrorLogger.debug(DS_EST_RES_HIST_LOG_TEMP.toString());

			ErrorLogger.debug("####DS_EST_HIST_TEMP");
			ErrorLogger.debug(DS_EST_HIST_TEMP.toString());
			
			ErrorLogger.debug("####DS_INSU_PLAN_MAST_TEMP");
			ErrorLogger.debug(DS_INSU_PLAN_MAST_TEMP.toString());
			
			ErrorLogger.debug("####DS_EST_LIST_IN_TEMP");
			ErrorLogger.debug(DS_EST_LIST_IN_TEMP.toString());
			
			ErrorLogger.debug("####DS_LTTMINFR0124");
			ErrorLogger.debug(DS_LTTMINFR0124.toString());
			
			ErrorLogger.debug("CON_ENT_DGN_NO::"+ent_dgn_no + " || SAVE_GB:: "+save_gb + " || CUR_HTDGREE:: " + cur_htdgree
					+" || CUR_HTID :: " + cur_htid + " || STATE_CD :: "+ state_cd + " || MOD_YN :: "+ mod_yn + " || PRE_TM_PROCESSING_GB :: " + pre_tm_pro_gb
					+ " || PSN_YN :: "+ psn_yn + " || POA_YN :: " + poa_yn + " || SIMSA_CNT :: " + simsa_cnt);
			
			
			
			savePreJudgeInfo(res);
			
			
		}catch(Exception e){
			e.printStackTrace();
		} 
	
	}
	
	/**
	 * @param res
	 */
	/**
	 * @param res
	 */
	public void savePreJudgeInfo(JediResponse res){
		
		IVRLogger.info("############# savePreJudgeInfo start ########################");
		JediTransaction tran = JediTransactionManager.getJediTransaction();

		String htid = "0";
		String log_seq_id = "0";
		
		String errMsg = "QA통판 심사저장시 오류가 발생했습니다.";
		
		String dgn_user_id = "";
		
		JSONObject obj = null;
		Set set = null;
		Iterator<String> iterator = null;
		String [] columns = null;
		String [] values = null;
		int k =0;
		
		try{
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.est.hansol.getTmProcessingGB.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if(sqlResult.getCount() > 0){
				String tm_processing_gb = sqlResult.getListParam("DS_PROCESSING_GB").getParam(0).getString("TM_PROCESSING_GB");
				dgn_user_id = sqlResult.getListParam("DS_PROCESSING_GB").getParam(0).getString("DGN_USER_ID");
				if("53".equals(pre_tm_pro_gb) && !tm_processing_gb.equals(pre_tm_pro_gb)){
					errMsg = "QC통과로 진행할 수 없는 상태입니다.";
					res.param.addValue( "result", "fail" );
					res.param.addValue( "reason", "QC통과로 진행할 수 없는 상태입니다." );
					
					throw new WebActionException("QA SAVE FAILED", errMsg);
				}
			}
			
			
			sqlParam.clear();
			sqlParam.setSqlName("msens.est.hansol.getQAEvaluateStatus.sel");
			sqlParam.addValue("CON_ENT_DGN_NO", ent_dgn_no);
			sqlParam.addValue("QACD", qacd);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if(sqlResult.getCount() > 0){
				String now_curdgree = sqlResult.getListParam("DS_QA_STATUS").getParam(0).getString("CUR_HTDGREE");
				if(Integer.parseInt(now_curdgree) > Integer.parseInt(cur_htdgree)){
					res.param.addValue( "result", "fail" );
					res.param.addValue( "reason", "이미 심사를 진행하였거나 심사결과가 변경되었습니다." );
					
					throw new WebActionException("QA SAVE FAILED", errMsg);
				}
			}
			
			/****************************************************
			 * Case.1) 최초 심사결과 이력을 저장한다. TB_PRE_EST_RESULT insert TB_PRE_EST_HISTORY insert : 보완차수 0,1 각각 2개의 row 저장 TB_PRE_EST_RESULT_HIST insert
			 * TB_PRE_EST_RESULT_HIST_LOG insert TB_PRE_EST_HISTORY_LOG insert
			 ****************************************************/
			tran.begin();
	
			
			IVRLogger.info("######## con_ent_dgn_no :: " + ent_dgn_no + " :: save_gb :: " + save_gb + " ################");
			
			if("INSERT2".equals(save_gb)){
				ErrorLogger.debug("###INSERT2 ::  최초 저장 시 ####");
				
				IVRLogger.info("##################msens.est.hansol.getMaxSEQ_HOLI_SEQ.sel :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
				// TB_PRE_EST_HISTORY MAX(HTID) 조회
				sqlParam.clear();
				sqlParam.setSqlName("msens.est.hansol.getMaxSEQ_HOLI_SEQ.sel");
				sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
				
				if(sqlResult.getCount() > 0 ){
					htid = sqlResult.getListParam("SEQ_HOLI_SEQ").getParam(0).getString("HTID");
				}
			
				
				obj = (JSONObject) DS_EST_HIST_TEMP.get(0);
				set = obj.keySet();

				columns = new String[set.size()];
				
				iterator = set.iterator();
				values = new String[set.size()];
				
				k =0;
				while(iterator.hasNext()){
					columns[k] = iterator.next();
					if("HTID".equals(columns[k])){ // TB_PRE_EST_HISTORY에 HTID 값 셋팅
						values[k] = htid;
					}else{
						Object val = obj.get(columns[k]);
						values[k] = val.toString();
					}
					k++;
				}
				
				DS_EST_HIST = new ListParam(columns);
				DS_EST_HIST.addRow(values);
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST.toString());
				
				
				if(DS_EST_HIST.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistory.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistory.ins");
					sqlParam2.addValue("DS_EST_HIST", DS_EST_HIST); //TB_PRE_EST_HISTORY 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HISTORY Insert
				for(int i=0; i< DS_EST_RES_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_RES_HIST = new ListParam(columns);
					DS_EST_RES_HIST.addRow(values);
					
				}

				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST.toString());
				
				
				if(DS_EST_RES_HIST.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHist.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHist.ins");
					sqlParam2.addValue("DS_EST_RES_HIST", DS_EST_RES_HIST); //TB_PRE_EST_RESULT_HIST 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				IVRLogger.info("##################msens.est.hansol.getMaxLogSeqId.sel :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
				
				// TB_PRE_EST_RESULT_HIST_LOG Insert
				sqlParam.clear();
				sqlParam.setSqlName("msens.est.hansol.getMaxLogSeqId.sel");
				sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
				if(sqlResult.getCount() > 0 ){
					log_seq_id = sqlResult.getListParam("LOGSEQID").getParam(0).getString("LOGSEQID");
				}
				
				for(int i =0; i< DS_EST_RES_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()+1];
					values = new String[set.size()+1];
					
					if(i ==0 ) columns[0] = "LOGSEQID";
					
					k = 0;
					values[k] = log_seq_id;
					k++;
					
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i == 0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					
					if(i ==0) DS_EST_RES_HIST_LOG = new ListParam(columns);
					DS_EST_RES_HIST_LOG.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST_LOG.toString());
				
				
				if(DS_EST_RES_HIST_LOG.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHistLog.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHistLog.ins");
					sqlParam2.addValue("DS_EST_RES_HIST_LOG", DS_EST_RES_HIST_LOG); //TB_PRE_EST_RESULT_HIST_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HISTORY_LOG Insert
				obj = (JSONObject) DS_EST_HIST_LOG_TEMP.get(0);
				set = obj.keySet();
				
				
				columns = new String[set.size()];
				
				iterator = set.iterator();
				values = new String[set.size()];
				
				k =0;
				while(iterator.hasNext()){
					columns[k] = iterator.next();
					if("HTID".equals(columns[k])){ // TB_PRE_EST_HISTORY에 HTID 값 셋팅
						values[k] = htid;
					}else{
						Object val = obj.get(columns[k]);
						values[k] = val.toString();
					}
					k++;
				}
				
				DS_EST_HIST_LOG = new ListParam(columns);
				DS_EST_HIST_LOG.addRow(values);
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST_LOG.toString());
				
				//※ TB_PRE_EST_HISTORY_LOG 테이블의 업데이트 권한을 받은 후에 살릴 것
				// 해당 테이블 저장 권한이 없어서 임시로 주석함
				if(DS_EST_HIST_LOG.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistoryLog.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistoryLog.ins");
					sqlParam2.addValue("DS_EST_HIST_LOG", DS_EST_HIST_LOG); //TB_PRE_EST_HISTORY_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
							
			/****************************************************
			* Case.2) 새로운 보완차수를 INSERT 한다. TB_PRE_EST_HISTORY insert TB_PRE_EST_RESULT_HIST insert
			* TB_PRE_EST_RESULT_HIST_LOG insert TB_PRE_EST_HISTORY_LOG insert
			****************************************************/	
			}else if("INSERT".equals(save_gb)){
				ErrorLogger.debug("새로운 보완차수를 INSERT ############");
				
				IVRLogger.info("##################msens.est.hansol.getMaxSEQ_HOLI_SEQ.sel :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
				
				
				// TB_PRE_EST_HISTORY MAX(HTID) 조회
				sqlParam.clear();
				sqlParam.setSqlName("msens.est.hansol.getMaxSEQ_HOLI_SEQ.sel");
				sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
				if(sqlResult.getCount() > 0 ){
					htid = sqlResult.getListParam("SEQ_HOLI_SEQ").getParam(0).getString("HTID");
				}

				
				obj = (JSONObject) DS_EST_HIST_TEMP.get(0);
				set = obj.keySet();
				
				columns = new String[set.size()];
				
				iterator = set.iterator();
				values = new String[set.size()];
				
				k =0;
				while(iterator.hasNext()){
					columns[k] = iterator.next();
					if("HTID".equals(columns[k])){ // TB_PRE_EST_HISTORY에 HTID 값 셋팅
						values[k] = htid;
					}else{
						Object val = obj.get(columns[k]);
						values[k] = val.toString();
					}
					k++;
				}
				
				DS_EST_HIST = new ListParam(columns);
				DS_EST_HIST.addRow(values);
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST.toString());

				if(DS_EST_HIST.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistory2.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistory2.ins");
					sqlParam2.addValue("DS_EST_HIST", DS_EST_HIST); //TB_PRE_EST_HISTORY 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_RESULT_HIST Insert
				for(int i=0; i< DS_EST_RES_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_RES_HIST = new ListParam(columns);
					DS_EST_RES_HIST.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST.toString());
				
				if(DS_EST_RES_HIST.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHist.ins :: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHist.ins");
					sqlParam2.addValue("DS_EST_RES_HIST", DS_EST_RES_HIST); //TB_PRE_EST_RESULT_HIST 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				IVRLogger.info("##################msens.est.hansol.getMaxLogSeqId.sel:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
				
				// TB_PRE_EST_RESULT_HIST_LOG Insert
				sqlParam.clear();
				sqlParam.setSqlName("msens.est.hansol.getMaxLogSeqId.sel");
				sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
				if(sqlResult.getCount() > 0 ){
					log_seq_id = sqlResult.getListParam("LOGSEQID").getParam(0).getString("LOGSEQID");
				}

				
				for(int i =0; i< DS_EST_RES_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()+1];
					values = new String[set.size()+1];
					
					if(i ==0 ) columns[0] = "LOGSEQID";
					
					k = 0;
					values[k] = log_seq_id;
					k++;
					
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i == 0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					
					if(i ==0) DS_EST_RES_HIST_LOG = new ListParam(columns);
					DS_EST_RES_HIST_LOG.addRow(values);
					
				}

				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST_LOG.toString());
				
				if(DS_EST_RES_HIST_LOG.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHistLog.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHistLog.ins");
					sqlParam2.addValue("DS_EST_RES_HIST_LOG", DS_EST_RES_HIST_LOG); //TB_PRE_EST_RESULT_HIST_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HISTORY_LOG Insert
				obj = (JSONObject) DS_EST_HIST_LOG_TEMP.get(0);
				set = obj.keySet();
				
				
				columns = new String[set.size()];
				
				iterator = set.iterator();
				values = new String[set.size()];
				
				k =0;
				while(iterator.hasNext()){
					columns[k] = iterator.next();
					if("HTID".equals(columns[k])){ // TB_PRE_EST_HISTORY에 HTID 값 셋팅
						values[k] = htid;
					}else{
						Object val = obj.get(columns[k]);
						values[k] = val.toString();
					}
					k++;
				}
				
				DS_EST_HIST_LOG = new ListParam(columns);
				DS_EST_HIST_LOG.addRow(values);
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST_LOG.toString());
				
				
				//※ TB_PRE_EST_HISTORY_LOG 테이블의 업데이트 권한을 받은 후에 살릴 것
				if(DS_EST_HIST_LOG.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistoryLog2.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistoryLog2.ins");
					sqlParam2.addValue("DS_EST_HIST_LOG", DS_EST_HIST_LOG); //TB_PRE_EST_HISTORY_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				
				// TB_PRE_EST_HIST_DT INSERT
				for(int i=0; i< DS_EST_LIST_IN_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_LIST_IN_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ 
							values[k] = htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_LIST_IN = new ListParam(columns);
					DS_EST_LIST_IN.addRow(values);
				}
				
				
								
				if(DS_EST_LIST_IN != null && DS_EST_LIST_IN.rowSize() > 0){
					ErrorLogger.debug("TB_PRE_EST_HIST_DT :: 테이블 저장 :: ");
					ErrorLogger.debug(DS_EST_LIST_IN.toString());
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistDT.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistDT.ins");
					sqlParam2.addValue("DS_EST_LIST_IN", DS_EST_LIST_IN); //TB_PRE_EST_HIST_DT 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
			/****************************************************
			* Case.3) 현 보완차수(HTDGREE)에 UPDATE한다. TB_PRE_EST_RESULT update TB_PRE_EST_HISTORY update TB_PRE_EST_RESULT_HIST update
			* TB_PRE_EST_RESULT_HIST_LOG insert TB_PRE_EST_HISTORY_LOG insert
			****************************************************/
			}else if("UPDATE".equals(save_gb)){
				ErrorLogger.debug("######UPDATE로 진입");
				
				for(int i=0; i< DS_EST_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_HIST = new ListParam(columns);
					DS_EST_HIST.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST.toString());
				
				if(DS_EST_HIST.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.updatetbPreEstHistory.upd:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.updatetbPreEstHistory.upd");
					sqlParam2.addValue("DS_EST_HIST", DS_EST_HIST); //TB_PRE_EST_HISTORY 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				
				// TB_PRE_EST_RESULT_HIST Update
				for(int i=0; i< DS_EST_RES_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_PRE_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_RES_HIST = new ListParam(columns);
					DS_EST_RES_HIST.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST.toString());
				
				if(DS_EST_RES_HIST.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.updatetbPreEstResultHist.upd:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.updatetbPreEstResultHist.upd");
					sqlParam2.addValue("DS_EST_RES_HIST", DS_EST_RES_HIST); //TB_PRE_EST_RESULT_HIST 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_RESULT_HIST_LOG Insert
				for(int i =0; i< DS_EST_RES_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()+1];
					values = new String[set.size()+1];
					
					if(i ==0 ) columns[0] = "LOGSEQID";
					
					k = 0;
					values[k] = log_seq_id;
					k++;
					
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i == 0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					
					if(i ==0) DS_EST_RES_HIST_LOG = new ListParam(columns);
					DS_EST_RES_HIST_LOG.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST_LOG.toString());
				
				if(DS_EST_RES_HIST_LOG.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHistLog.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHistLog.ins");
					sqlParam2.addValue("DS_EST_RES_HIST_LOG", DS_EST_RES_HIST_LOG); //TB_PRE_EST_RESULT_HIST_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HISTORY_LOG Insert
				
				for(int i=0; i< DS_EST_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_HIST_LOG_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ 
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_HIST_LOG = new ListParam(columns);
					DS_EST_HIST_LOG.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST_LOG.toString());
				
				//※ TB_PRE_EST_HISTORY_LOG 테이블의 업데이트 권한을 받은 후에 살릴 것
				if(DS_EST_HIST_LOG.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistoryLog3.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistoryLog3.ins");
					sqlParam2.addValue("DS_EST_HIST_LOG", DS_EST_HIST_LOG); //TB_PRE_EST_HISTORY_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HIST_DT INSERT
				for(int i=0; i< DS_EST_LIST_IN_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_LIST_IN_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						
						Object val = obj.get(key);
						values[k] = val == null ? "" : val.toString();
						
						k++;
					}
					
					if(i==0) DS_EST_LIST_IN = new ListParam(columns);
					DS_EST_LIST_IN.addRow(values);
				}
				
				
				
				if(DS_EST_LIST_IN != null && DS_EST_LIST_IN.rowSize() > 0){
					ErrorLogger.debug("TB_PRE_EST_HIST_DT :: 테이블 저장 :: ");
					ErrorLogger.debug(DS_EST_LIST_IN.toString());
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistDT.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistDT.ins");
					sqlParam2.addValue("DS_EST_LIST_IN", DS_EST_LIST_IN); //TB_PRE_EST_HIST_DT 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
			}else if("UPDATE2".equals(save_gb)){ //선택없음 선택 후 상태 변경을 진행한 경우
				
				ErrorLogger.debug("########UPDATE2 진입");
			
				for(int i=0; i< DS_EST_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_HIST = new ListParam(columns);
					DS_EST_HIST.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST.toString());
				
				if(DS_EST_HIST.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.updatetbPreEstHistory2.upd:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.updatetbPreEstHistory2.upd");
					sqlParam2.addValue("DS_EST_HIST", DS_EST_HIST); //TB_PRE_EST_HISTORY 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_RESULT_HIST Update
				for(int i=0; i< DS_EST_RES_HIST_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ // TB_PRE_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_RES_HIST = new ListParam(columns);
					DS_EST_RES_HIST.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST.toString());
				
				if(DS_EST_RES_HIST.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.updatetbPreEstResultHist.upd:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.updatetbPreEstResultHist.upd");
					sqlParam2.addValue("DS_EST_RES_HIST", DS_EST_RES_HIST); //TB_PRE_EST_RESULT_HIST 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_RESULT_HIST_LOG Insert
				for(int i =0; i< DS_EST_RES_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_RES_HIST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()+1];
					values = new String[set.size()+1];
					
					if(i ==0 ) columns[0] = "LOGSEQID";
					
					k = 0;
					values[k] = log_seq_id;
					k++;
					
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i == 0) columns[k] = key;
						if("HTID".equals(key)){ // TB_EST_RESULT_HIST의 이력ID를 TB_PRE_EST_HISTORY의 이력ID로 셋팅한다. 단 최초이력(심사의뢰) 상태값은 항목별 심사결과값이 없으므로 입력하지 않는다.
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					
					if(i ==0) DS_EST_RES_HIST_LOG = new ListParam(columns);
					DS_EST_RES_HIST_LOG.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_RESULT_HIST_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_RES_HIST_LOG.toString());
				
				if(DS_EST_RES_HIST_LOG.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstResultHistLog.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstResultHistLog.ins");
					sqlParam2.addValue("DS_EST_RES_HIST_LOG", DS_EST_RES_HIST_LOG); //TB_PRE_EST_RESULT_HIST_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HISTORY_LOG Insert
				for(int i=0; i< DS_EST_HIST_LOG_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_HIST_LOG_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						if("HTID".equals(key)){ 
							values[k] = cur_htid;
						}else{
							Object val = obj.get(key);
							values[k] = val == null ? "" : val.toString();
						}
						k++;
					}
					
					if(i==0) DS_EST_HIST_LOG = new ListParam(columns);
					DS_EST_HIST_LOG.addRow(values);
					
				}
				
				ErrorLogger.debug("TB_PRE_EST_HISTORY_LOG :: 테이블 저장 :: ");
				ErrorLogger.debug(DS_EST_HIST_LOG.toString());
				
				//※ TB_PRE_EST_HISTORY_LOG 테이블의 업데이트 권한을 받은 후에 살릴 것
				if(DS_EST_HIST_LOG.rowSize() > 0){
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistoryLog3.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistoryLog3.ins");
					sqlParam2.addValue("DS_EST_HIST_LOG", DS_EST_HIST_LOG); //TB_PRE_EST_HISTORY_LOG 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				// TB_PRE_EST_HIST_DT INSERT
				for(int i=0; i< DS_EST_LIST_IN_TEMP.size(); i++){
					obj = (JSONObject) DS_EST_LIST_IN_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						
						Object val = obj.get(key);
						values[k] = val == null ? "" : val.toString();
						
						k++;
					}
					
					if(i==0) DS_EST_LIST_IN = new ListParam(columns);
					DS_EST_LIST_IN.addRow(values);
				}
				
				
				
				if(DS_EST_LIST_IN != null && DS_EST_LIST_IN.rowSize() > 0){
					
					ErrorLogger.debug("TB_PRE_EST_HIST_DT :: 테이블 저장 :: ");
					ErrorLogger.debug(DS_EST_LIST_IN.toString());
					
					IVRLogger.info("##################msens.est.hansol.savetbPreEstHistDT.ins:: con_ent_dgn_no :: "+ent_dgn_no+" #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.savetbPreEstHistDT.ins");
					sqlParam2.addValue("DS_EST_LIST_IN", DS_EST_LIST_IN); //TB_PRE_EST_HIST_DT 테이블에 이력 저장
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
			}
	
			// ※stateCd.equals("0") 부분이 선택없을 선택한걸 제외한다는 건지, 아니면 전송이 안된 경우를 말하는 건지 확인이 필요함
			// 우선은 NONE이 아닌경우로 판단하고 개발함
			if(!"".equals(state_cd) && !"UPDATE".equals(save_gb) && mod_yn){
				// TB_INSU_PLAN_MAST 테이블의 최종상태 UPDATE
				
				for(int i =0; i< DS_INSU_PLAN_MAST_TEMP.size(); i++){
					obj = (JSONObject) DS_INSU_PLAN_MAST_TEMP.get(i);
					set = obj.keySet();
					iterator = set.iterator();
					
					columns = new String[set.size()];
					values = new String[set.size()];
					
					k =0;
					
					while(iterator.hasNext()){
						String key = iterator.next();
						if(i ==0) columns[k] = key;
						
						Object val = obj.get(key);
						values[k] = val == null ? "" : val.toString();
						
						k++;
					}
					
					if(i==0) DS_INSU_PLAN_MAST = new ListParam(columns);
					DS_INSU_PLAN_MAST.addRow(values);
					
				}
				
				ErrorLogger.debug("#### TB_INSU_PLAN_MAST 테이블의 최종상태 UPDATE");
				ErrorLogger.debug(DS_INSU_PLAN_MAST.toString());
				
				
				// ※ BATCH의 경우에도 PARAMETER 전송으로 가능한지 테스트 필요함 --> 가능
				if(DS_INSU_PLAN_MAST.rowSize() > 0){
					
					IVRLogger.info("##################msens.est.hansol.updatetbInsuPlanMast.upd:: con_ent_dgn_no :: "+ent_dgn_no+ " #########################");
					
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.est.hansol.updatetbInsuPlanMast.upd");
					sqlParam2.addValue("DS_INSU_PLAN_MAST", DS_INSU_PLAN_MAST); //TB_INSU_PLAN_MAST 테이블에 업데이트
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
					
					//tran.commit();
				}
				
				IVRLogger.info("################## con_ent_dgn_no :: "+ent_dgn_no+ " :: state_cd :: "+state_cd + " :: psn_yn :: " +psn_yn + " :: poa_yn :: " +poa_yn+" #########################");
			 	
				if("62".equals(state_cd) && "N".equals(psn_yn) && "Y".equals(poa_yn)){ // 계≠피 || 사망담보존재여부(Y) 일경우------------------
					TmInterWebaction tm_inter = new TmInterWebaction(server_gb);
					
					String v_url = "";
					String v_reqJson = "";
					
					// 청약상태변경
					JSONObject result =  tm_inter.set_LTTMINFR0096(ent_dgn_no, "03");
					
					Object res_cd = result.get("ResultCode");
					Object res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
					
					// 에러가 발생하면 화면에 오류 보여줌
					if(res_cd != null && !"0".equals(res_cd)){
						v_res.param.addValue( "result", "fail" );
						v_res.param.addValue( "reason",  "청약상태변경 과정에 오류가 발생했습니다.");
						
						throw new WebActionException("fail", res_msg.toString());
					}
					
					IVRLogger.info("########con_ent_dgn_no :: " + ent_dgn_no + " :: simsa_cnt :: " + simsa_cnt + " ###############");
					// 최종통과가 하루에 1건만 사망동의서 I/F호출
					if(Integer.parseInt(simsa_cnt) == 0){
						result = tm_inter.set_LTTMINFR0124(DS_LTTMINFR0124); // 사망동의서 우편발송 I/F 호출
						
						res_cd = result.get("ResultCode");
						res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
						
						// 에러가 발생하면 화면에 오류 보여줌
						if(res_cd != null && !"0".equals(res_cd)){
							v_res.param.addValue( "result", "fail" );
							v_res.param.addValue( "reason", "사망동의서 우편 발송 정보를 등록하는 과정에 오류가 발생했습니다.");
							
							throw new WebActionException("fail", res_msg.toString());
						}
						
					}
					
					// 개인고객 정보활용동의등록
					result = tm_inter.set_LTTMINFR0127(DS_INSU_PLAN_MAST, ent_dgn_no);
					
					res_cd = result.get("ResultCode");
					res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
					
					// 에러가 발생하면 화면에 오류 보여줌
					if(res_cd != null && !"0".equals(res_cd)){
						v_res.param.addValue( "result", "fail" );
						v_res.param.addValue( "reason", "개인고객 정보활용동의를 등록하는 과정에 오류가 발생했습니다.");
						
						throw new WebActionException("fail", res_msg.toString());
					}
					
					
					// 청약일자변경
					result = tm_inter.set_LTTMINFR0073(DS_INSU_PLAN_MAST, ent_dgn_no, dgn_user_id);
					
					res_cd = result.get("ResultCode");
					res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
					
					// 에러가 발생하면 화면에 오류 보여줌
					if(res_cd != null && !"0".equals(res_cd)){
						v_res.param.addValue( "result", "fail" );
						v_res.param.addValue( "reason", "청약일자를 변경하는 과정에서 오류가 발생했습니다.");
						
						throw new WebActionException("fail", res_msg.toString());
					}
					
				}else{ // 기존 정상심사------------------
					
					ErrorLogger.debug("####정상심사 진입!! = " + state_cd);
					if("62".equals(state_cd) || "65".equals(state_cd) || "46".equals(state_cd)){
						
						TmInterWebaction tm_inter = new TmInterWebaction(server_gb);
						
						String v_state_cd = "";
						
						switch(state_cd){
							case "62" : v_state_cd = "23"; break;
							case "65" : 
								if("48".equals(pre_tm_pro_gb)) v_state_cd = "09";
								else v_state_cd = "01";
							break;
							case "46" : v_state_cd = "37";
							break;
						}
						
						// 청약상태변경
						JSONObject result = tm_inter.set_LTTMINFR0096(ent_dgn_no, v_state_cd);
						
						ErrorLogger.debug("######청약상태 테스트");
						ErrorLogger.debug(result.toString());
						
						Object res_cd = result.get("ResultCode");
						Object res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
						
						// 에러가 발생하면 화면에 오류 보여줌
						if(res_cd != null && !"0".equals(res_cd)){
							v_res.param.addValue( "result", "fail" );
							v_res.param.addValue( "reason", "청약상태를 변경하는 과정에서 오류가 발생했습니다.");
							
							throw new WebActionException("fail", res_msg.toString());
						}
						
						// 심사의견반영
						result = tm_inter.set_LTTMINFR0097(DS_EST_HIST_TEMP, ent_dgn_no);
						
						ErrorLogger.debug("######심사의견 반영 테스트");
						ErrorLogger.debug(result.toString());
						
						res_cd = result.get("ResultCode");
						res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
						String normProcYn = result.get("normProcYn") != "null" ? "" : result.get("normProcYn").toString();
						
						// 에러가 발생하면 화면에 오류 보여줌
						if(res_cd != null && !"0".equals(res_cd)){
							if("N".equals(normProcYn)) {
								v_res.param.addValue( "result", "fail" );
								v_res.param.addValue( "reason", "심사의견을 반영하는 과정에서 오류가 발생했습니다. " + result.get("procMsg"));
							}
							
							throw new WebActionException("fail", res_msg.toString());
						}
						
						if("62".equals(state_cd)){
							// 개인고객 정보활용동의등록
							result = tm_inter.set_LTTMINFR0127(DS_INSU_PLAN_MAST, ent_dgn_no);
							
							res_cd = result.get("ResultCode");
							res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
							
							// 에러가 발생하면 화면에 오류 보여줌
							if(res_cd != null && !"0".equals(res_cd)){
								v_res.param.addValue( "result", "fail" );
								v_res.param.addValue( "reason", "개인정보 활용동의 등록 과정에서 오류가 발생했습니다.");
								
								throw new WebActionException("fail", res_msg.toString());
							}
							
							// 청약일자변경
							result =  tm_inter.set_LTTMINFR0073(DS_INSU_PLAN_MAST, ent_dgn_no, dgn_user_id);
							
							res_cd = result.get("ResultCode");
							res_msg = "null".equals(result.get("ResultMessage")) ? "" : result.get("ResultMessage");
							
							// 에러가 발생하면 화면에 오류 보여줌
							if(res_cd != null && !"0".equals(res_cd)){
								v_res.param.addValue( "result", "fail" );
								v_res.param.addValue( "reason", "청약일자 변경 과정에서 오류가 발생했습니다.");
								
								throw new WebActionException("fail", res_msg.toString());
							}
							
						}
					}
				}
			}
		
			tran.commit(); // 모든 로직 다 태우고 commit 하기
			isCommit = true;
			
			IVRLogger.info("##################### savePreJudgeInfo end :: "+ ent_dgn_no +" ########################");
			res.param.addValue( "result", "success" );
			res.param.addValue( "reason", "" );
			
		}catch(Exception e){
			tran.rollback();
			
			e.printStackTrace();
			
			v_res.param.addValue( "result", "fail" );
			v_res.param.addValue( "reason", e.getMessage());
			
			IVRLogger.info("#####QA평가표 저장 과정에서 오류가 발생했습니다. :: 설계번호 :: "+ ent_dgn_no+ " :: 에러사유 : " + e.getMessage());
			
			try {
				throw new WebActionException("fail",e);
			} catch (WebActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}finally{
			if(!isCommit) tran.rollback(); // 혹시 비정상적으로 돌았을 경우 ROLLBACK 시킴
		}
	}

	
};
