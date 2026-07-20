package sens.service.webaction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;

import javax.servlet.http.HttpServletRequest;

public class Timestamp extends XwingWebAction {
	JediTransaction tran = JediTransactionManager.getJediTransaction();
	String connect_ip = null;
	HttpServletRequest Httpreq = null;
	String log_gb = null;
	String ced_no = null;
	String user_id = null;
	String work_gubun = null;
	String start_yn = null;
			
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		Httpreq = req.getHttpServletRequest();
		connect_ip = Httpreq.getRemoteAddr();
		log_gb = req.param.getString("log_gb","");
		ced_no = req.param.getString("ced_no",null); 
		user_id = req.param.getString("user_id",(String)req.getCommonDTO().getAttribute("user_id"));
		work_gubun = req.param.getString("work_gubun",null);
		start_yn = req.param.getString("start_yn","N");
		
		IVRLogger.debug("********LOG_GB*********"+log_gb);
		IVRLogger.debug("********start_yn*********"+start_yn);
		
		if(log_gb.equals("T") || log_gb.equals("")){ //팝업 닫을때는 log_gb가 소멸되서 안넘어옴
			InsertTimeStamp();
		}else if(log_gb.equals("B")){ //얘 닫을때는 화면에서 alert을 한번 띄우므로 무조건 넘어온다(휴식,교육은 어차피 갱신 안하는게 맞음)
				InsertWorkStat();
		}else{
			IVRLogger.info("########LOG_GB가 안넘어와서 로그 못갱신하는 에러발생!!!########");
		}
		
	}
	
	public void InsertTimeStamp(){
		try {
			SQLParam sqlparam_ex = new SQLParam();
			
			sqlparam_ex.setSqlName("oba.oba010p1.timestamp.upd");	
			
				sqlparam_ex.addValue("CED_NO",  ced_no);
				sqlparam_ex.addValue("USER_ID",  user_id);
				sqlparam_ex.addValue("WORK_GUBUN",  work_gubun);
				sqlparam_ex.addValue("CONNECT_IP", connect_ip);
			
			SQLServiceManager.getInstance().execute(sqlparam_ex);
			
			IVRLogger.debug("#########INSERT POPUP LOGOUT TIMESTAMP SUCCESS#########"+user_id+"////"+connect_ip);
		} catch (Exception e) {
			// TODO: handle exception
			IVRLogger.info("#########INSERT POPUP LOGOUT TIMESTAMP ERROR! T.T#########");
			IVRLogger.info(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void InsertWorkStat(){
		try {
			SQLParam sqlparam_ex = new SQLParam();
			
			sqlparam_ex.setSqlName("oba.oba010p1.setWorkStat.upd");	
			
			sqlparam_ex.addValue("USER_ID",user_id);
			sqlparam_ex.addValue("WORK_GUBUN",  work_gubun);
			sqlparam_ex.addValue("CONNECT_IP", connect_ip);
			
			System.out.println("#######COONECT IP#####"+connect_ip);
			
			SQLServiceManager.getInstance().execute(sqlparam_ex);
			
			IVRLogger.debug("#########INSERT POPUP LOGOUT WORKSTAT SUCCESS#########"+user_id+"////"+connect_ip);
		} catch (Exception e) {
			// TODO: handle exception
			IVRLogger.info("#########INSERT POPUP LOGOUT WORKSTAT ERROR! T.T#########"+user_id+"////"+connect_ip);
			IVRLogger.info(e.getMessage());
			e.printStackTrace();
		}
		
	}
	

	public void STimeoutInsertTimeStamp(String ip){
		try{
			if(connect_ip == null){
				connect_ip = ip;
			}
			IVRLogger.debug("@@@@@@@@@@@@@@@@@connect_ip@@@@@@@@@@@@@@@@@@@@@@@@@@@"+connect_ip);
		
			SQLParam sqlparam_ex = new SQLParam();
			
			sqlparam_ex.setSqlName("oba.oba010p1.sessionTimeout.timestamp.upd");	
			
			sqlparam_ex.addValue("CONNECT_IP", connect_ip);
			
			SQLServiceManager.getInstance().execute(sqlparam_ex);
			
			IVRLogger.debug("#########INSERT POPUP SESSION TIMEOUT SUCCESS#########"+"////"+connect_ip);
		} catch (Exception e){
			IVRLogger.info("#########INSERT SESSION TIMEOUT TIMESTAMP ERROR! T.T#########"+connect_ip);
			e.printStackTrace();
		}
		
	}
	
	public void STimeoutInsertWorkStat(String ip){
		try{
			if(connect_ip == null){
				connect_ip = ip;
			}
			IVRLogger.debug("@@@@@@@@@@@@@@@@@connect_ip@@@@@@@@@@@@@@@@@@@@@@@@@@@"+connect_ip);
		
			SQLParam sqlparam_ex = new SQLParam();
			
			sqlparam_ex.setSqlName("oba.oba010p1.sessionTimeout.workstat.upd");	
			
			sqlparam_ex.addValue("CONNECT_IP", connect_ip);
			
			SQLServiceManager.getInstance().execute(sqlparam_ex);
			
			IVRLogger.debug("#########INSERT POPUP WORKSTAT SESSION TIMEOUT SUCCESS#########"+"////"+connect_ip);
		} catch (Exception e){
			IVRLogger.info("#########INSERT SESSION TIMEOUT WORKSTAT ERROR! T.T#########"+connect_ip);
			e.printStackTrace();
		}
		
	}
};
