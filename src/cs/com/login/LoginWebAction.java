package cs.com.login;
 
import java.net.InetAddress;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.BaseEntity;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.LoginException;
import com.locus.jedi.waf.SessionManager;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.waf.controller.WebInitializer;


/**
* 업무 그룹명 : common.login
* 서브 업무명 : LoginWebAction.java
* 작성자 : 공통
* 작성일 : 2009. 03. 19
* 설 명 : LoginWebAction
*/
public class LoginWebAction extends XwingWebAction {
	private static final String[] cyper = new String[] {
		"Ve7kzIeKnCMrRCti6Up+IA==",
		"JFuHGrTUBkRNum1ZrciFDg==",
		"mDtqpZw7b2wDRupd5lERRA==",
		"1LkeoU8BTuovOoZ1pv5/Iw==",
		"INsMPtPnI6f6+oY/IfvYdw==",
		"aaeez7ptSdVW35QCopuJxQ==",
		"1zTVOh1fIOCyGELYAz01jQ==",
		"r9cuX12jHVmeCAj/2sbvmA==",
		"BFzlB5UXpWwqzGi0vNcgQA==",
		"fJklkhynnTTq9NzMtbX/Mg==",
		"QJVKFWN6lL95dpsUbODJCQ==",
		"hl1cCJzJmpkPkr+4cLvShA==",
		"XgUmpKjcKcH+yLGwOwRTnA==",
		"pyBeOfYtPK1wuPkJQxd1XQ==",
		"9w/na0DEaX5kBzGk8rg29g==",
		"JiucdfaV01RVunMYTda+5g==",
		"ryzlOs4/UNBJAqtxuvTN9g==",
		"oc4NKqQ1adP04bvObO2Htw==",
		"ftjroB1rZ22RFqs3deDkiQ==",
		"T62o1tTpy5g0uukePeucwg==",
		"pgNBgGehXuopzduRXonFmg==",
		"sx9wdw31p0bvRn31iOHd1A==",
		"YEZ5xB6VtXQcp9cZf/x6Dg==",
		"RcETZoZg/dKNQx5Dxhw2AQ==",
		"GTJ6z+e4+lNu2WQEUJsFsQ==",
		"teKpFJ22X/Seo8rq60CICQ==",
		"5NHiPnyJV1fcXpC9sXu8tQ==",
		"8E1urC2RstX7VT2Lr38q1A==",
		"/WWHQbbP1fI2ezZPcecOrg==",
		"NUaokl6+dOHVm+Vu2XE8nQ==",
		"tnq8+//RNpmEzl2pzZbvIA=="		};
	/**
	 * 웹액션 메인 메서드 : 로그인 처리
	 */
	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {
	
		HttpServletRequest request = req.getHttpServletRequest();
		String userid=req.param.getString("userid");
		String password = req.param.getString("password");		
		String sign = req.param.getString("sign");
		String sso_yn = req.param.getString("sso_yn");	
		String expired_yn = req.param.getString("expired_yn");
		String pass_yn = req.param.getString("pass_yn");
		String session_yn = req.param.getString("session_yn");
		String clientIp = request.getRemoteAddr();
		//String smsession=req.param.getString("smsession");
		
		//HttpSession session1 = request.getSession(true);
			
		if(sign == null) sign = "N";
		
		if(sso_yn == null) sso_yn = "N";
		
		if(pass_yn == null) pass_yn = "N";
		
		if(session_yn == null) session_yn = "N";

		//IVRLogger.debug("##########SSO LOGIN ::: " + sso_yn + "################# " + userid);
		// SSO 로그인시 암호화 복호 및 시간 체크.
		/*if( "Y".equals(sso_yn) )
		{	

			try {
				/*	session1.setAttribute("SMSESSION", smsession);
					session1.setAttribute("USERID", userid);
					/*if(smsession != null || !"".equals(smsession)){
						Cookie cookie = new Cookie("SMSESSION", smsession);
						
					}
					
					IVRLogger.debug("##########세션정보 ###############");
					IVRLogger.debug(smsession + " // " + userid);
					IVRLogger.debug(session1.toString());
					
				} catch(Exception e) {
					throw new WebActionException("login failed", "로그인에 실패하였습니다.",e);
				}
		} */
		
		// Common DTO에 대한 설정
		BaseEntity entity = new BaseEntity(userid);
		CommonDTO common = new CommonDTO(userid);
		entity.setCommonDTO(common);
		entity.setUserSession(new UserSessionImple(entity));

		if(expired_yn == null) expired_yn = "N";
		 
//		if( "Y".equals(expired_yn) ){
//			
//			ExpiredRunWebAction expr = new ExpiredRunWebAction();
//			
//			try {
//				expr.deleteAll();   
//				
//			}  catch (ParseException e) {
////				throw new WebActionException(LoginException.getReason(LoginException.NO_SUCH_USER),"사용기간이 만료되었습니다.");
//				e.printStackTrace();
//			} catch (IOException e) {  
////				throw new WebActionException(LoginException.getReason(LoginException.NO_SUCH_USER),"사용기간이 만료되었습니다.");
//				e.printStackTrace();
//			}
//			
//		}
		
		 
		try{		
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("cs.com.user_select");
			sqlparam.setResultName("user");
			sqlparam.addValue("userid", userid);
			sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
			
			if(sqlparam.getCount() <= 0) {
				throw new WebActionException(LoginException.getReason(
					LoginException.NO_SUCH_USER), 
					"로그인에 실패하였습니다"
				);
			}
			
			

			Param aUser = sqlparam.getListParam("user").getParam(0);
			ErrorLogger.debug(aUser);
			
			//int dayIndex = Integer.parseInt(DateUtil.getCurrentDate().substring(6)) - 1;
			//if(!"Y".equals(sign)) password = SecurityUtil.getCryptoMD5String(password);			
			
			if("".equals(aUser.getString("user_grade_cd", "").trim())) {
				throw new WebActionException(LoginException.getReason(
					LoginException.DENIED_USER), 
					"사용자 등급을 설정해 주세요"
				);				
			}
			
			if("N".equals(sso_yn)){
				//System.out.println(">>>>>> sso_yn:["+sso_yn+"] password:["+password+"] cyper[dayIndex]:["+cyper[dayIndex]+"]");
				// { Master Key Start			
				//if(! cyper[dayIndex].equals(password) && ! "Y".equals(sso_yn)) {	
				String pass = aUser.getString("password", "");
				String pwchgdate = aUser.getString("pwd_chg_date", DateUtil.getCurrentDate());
				int pwd_err_cnt = aUser.getInt("pwd_err_cnt");
						
				if(pwd_err_cnt >= 3) {
					throw new WebActionException("PasswordErrorOver", 
							"비밀번호 연속 3회 이상 오류로 접속이 제한되었습니다.\n☞  비밀번호를 초기화하세요."
					);				
				}
					
					//if(pass.equals(SecurityUtil.getCryptoMD5String("9999"))) {
					//	throw new WebActionException("PasswordInitialized", "PasswordInitialized"
					//	);
					//}
	
				//System.out.println("pwchgdate::" + pwchgdate);
				//System.out.println(DateUtil.getDayDistance(pwchgdate, DateUtil.getCurrentDate()));
				/*getDayDistance 함수 오류
				if(DateUtil.getDayDistance(pwchgdate, DateUtil.getCurrentDate()) > 90) {
					throw new WebActionException("PasswordExpired", 
							"비밀번호 변경 주기 90일이 지났습니다.\n☞  비밀번호를 변경하세요."
					);	
				}
				*/
				sqlparam.clear();
				sqlparam.setSqlName("cs.com.password_errorcnt");
				sqlparam.addValue("userid", userid);
					
				if(!pass.equals(password) && pass_yn.equals("N")) {	
					pwd_err_cnt = pwd_err_cnt + 1;
					sqlparam.addValue("pwd_err_cnt", pwd_err_cnt);			
					BizDelegate.getInstance().execute("sqlService", common, sqlparam);
						
					throw new WebActionException(LoginException.getReason(
							LoginException.PWD_INCORRECT), 
							"비밀번호 " + pwd_err_cnt +"회 오류입니다.\n☞  비밀번호를 확인해 주세요."
						);
				} else {
					sqlparam.addValue("pwd_err_cnt", 0);
					BizDelegate.getInstance().execute("sqlService", common, sqlparam);
				}
			}
			
				
			ErrorLogger.debug(aUser); 
			//}
			// } Master Key
			
			String serverName = null;
			String serverIp = null;
			try{
				 serverName = InetAddress.getLocalHost().getHostName();
				 serverIp = InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e){
				serverName = "UnKnown";
			}

			common.setUserId(aUser.getString("user_id"));
			common.setUserName(aUser.getString("user_name"));
			common.setClientIp(clientIp);
			
			//SQL : cs.com.user_select
			common.setAttribute( "user_id"			, aUser.getString( "user_id"		, "" ) );
			common.setAttribute( "user_name"		, aUser.getString( "user_name"		, "" ) );
			common.setAttribute( "emp_no"			, aUser.getString( "emp_no"			, "" ) );
			common.setAttribute( "user_grade_cd"	, aUser.getString( "user_grade_cd"	, "" ) );
			common.setAttribute( "user_grade_nm"	, aUser.getString( "user_grade_nm"	, "" ) );
			common.setAttribute( "g_user_group_cd"	, aUser.getString( "user_group_cd"	, "" ) );
			common.setAttribute( "password"			, aUser.getString( "password"		, "" ) );
			common.setAttribute( "use_yn"			, aUser.getString( "use_yn"		    , "" ) );

			common.setAttribute( "social_id"		, aUser.getString( "social_id"		, "" ) );
			common.setAttribute( "enter_date"		, aUser.getString( "enter_date"		, "" ) );
			common.setAttribute( "retire_date"		, aUser.getString( "retire_date"	, "" ) );
			common.setAttribute( "level_cd"			, aUser.getString( "level_cd"		, "" ) );
			common.setAttribute( "level_nm"		    , aUser.getString( "level_nm"		, "" ) );
			common.setAttribute( "function_cd"		, aUser.getString( "function_cd"	, "" ) );
			common.setAttribute( "function_nm"	    , aUser.getString( "function_nm"	, "" ) );
			common.setAttribute( "job_cd"		    , aUser.getString( "job_cd"			, "" ) );
			common.setAttribute( "job_nm"		    , aUser.getString( "job_name"   	, "" ) );
			common.setAttribute( "work_group_cd"	, aUser.getString( "work_group_cd"	, "" ) );
			common.setAttribute( "work_group_nm"	, aUser.getString( "work_group_nm"	, "" ) );

			common.setAttribute( "dept_cd"			, aUser.getString( "dept_cd"		, "" ) );
			common.setAttribute( "dept_nm"			, aUser.getString( "dept_nm"		, "" ) );
			common.setAttribute( "org1_cd"		    , aUser.getString( "org1_cd"		, "" ) );
			common.setAttribute( "org1_nm"		    , aUser.getString( "org1_name"		, "" ) );
			common.setAttribute( "org2_cd"			, aUser.getString( "org2_cd"		, "" ) );
			common.setAttribute( "org2_nm"			, aUser.getString( "org2_name"		, "" ) );
			common.setAttribute( "org3_cd"			, aUser.getString( "org3_cd"		, "" ) );
			common.setAttribute( "org3_nm"			, aUser.getString( "org3_name"		, "" ) );
			common.setAttribute( "org4_cd"			, aUser.getString( "org4_cd"		, "" ) );
			common.setAttribute( "org4_nm"			, aUser.getString( "org4_name"		, "" ) );
			common.setAttribute( "org5_cd"			, aUser.getString( "org5_cd"		, "" ) );
			common.setAttribute( "org5_nm"			, aUser.getString( "org5_name"		, "" ) );
			common.setAttribute( "org6_cd"			, aUser.getString( "org6_cd"		, "" ) );
			common.setAttribute( "org6_nm"			, aUser.getString( "org6_name"		, "" ) );


			common.setAttribute( "pbx_id"			, aUser.getString( "pbx_id"			, "" ) );
			common.setAttribute( "tenant_id"        , aUser.getString( "tenant_id"      , "" ) );
			common.setAttribute( "a_alarm_level"    , aUser.getString( "a_alarm_level"  , "" ) );
//			common.setAttribute( "time_interval"	, aUser.getString( "time_interval"	, "" ) );
//			common.setAttribute( "time_interval_sch", aUser.getString( "time_interval_sch"	, "" ) );
//			common.setAttribute( "work_stime"		, aUser.getString( "work_stime"		, "" ) );
//			common.setAttribute( "work_etime"		, aUser.getString( "work_etime"		, "" ) );
			common.setAttribute( "currdate"			, aUser.getString( "currdate"		, "" ) );
			common.setAttribute( "g_url"			, aUser.getString( "g_url"			, "" ) );
			common.setAttribute( "g_file_url"		, aUser.getString( "g_file_url"		, "" ) );
			common.setAttribute( "skincd"			, aUser.getString( "skincd"			, "" ) );
			//common.setAttribute( "gpa_gbn"		    , aUser.getString( "gpa_gbn"		, "" ) );
			//common.setAttribute( "object_id"		, aUser.getString( "object_id"		, "" ) );

			common.setAttribute("wasid", serverName);
			common.setAttribute("wasip", serverIp);
			common.setAttribute("wasPort", request.getServerPort());
			common.setAttribute("contextRoot", request.getContextPath());
			common.setAttribute("serverUrl", "http://" + serverIp + ":" + request.getServerPort() + request.getContextPath());
			common.setAttribute( "max_sortno"		 , aUser.getString( "max_sortno"		, "" ) );
			common.setAttribute( "min_sortno"		 , aUser.getString( "min_sortno"		, "" ) );
			
			res.param.addValue( "g_user_id"				, aUser.getString( "user_id"		, "" ) );
			res.param.addValue( "g_user_name"			, aUser.getString( "user_name"		, "" ) );
			res.param.addValue( "g_emp_no"				, aUser.getString( "emp_no"			, "" ) );
			res.param.addValue( "g_user_grade"			, aUser.getString( "user_grade_cd"	, "" ) );
			res.param.addValue( "g_user_grade_nm"		, aUser.getString( "user_grade_nm"	, "" ) );
			res.param.addValue( "g_user_group_cd"		, aUser.getString( "user_group_cd"	, "" ) );
			res.param.addValue( "g_password"			, aUser.getString( "password"		, "" ) );
			res.param.addValue( "g_use_yn"			    , aUser.getString( "use_yn"			, "" ) );

			res.param.addValue( "g_social_id"			, aUser.getString( "social_id"		, "" ) );
			res.param.addValue( "g_enter_date"			, aUser.getString( "enter_date"		, "" ) );
			res.param.addValue( "g_retire_date"			, aUser.getString( "retire_date"	, "" ) );
			res.param.addValue( "g_level_cd"			, aUser.getString( "level_cd"		, "" ) );
			res.param.addValue( "g_level_nm"			, aUser.getString( "level_nm"		, "" ) );
			res.param.addValue( "g_function_cd"			, aUser.getString( "function_cd"	, "" ) );
			res.param.addValue( "g_function_nm"			, aUser.getString( "function_nm"	, "" ) );
			res.param.addValue( "g_job_cd"				, aUser.getString( "job_cd"			, "" ) );
			res.param.addValue( "g_job_nm"				, aUser.getString( "job_nm"			, "" ) );
			res.param.addValue( "g_work_group_cd"		, aUser.getString( "work_group_cd"	, "" ) );
			res.param.addValue( "g_work_group_nm"		, aUser.getString( "work_group_nm"	, "" ) );

			res.param.addValue( "g_dept_cd"				, aUser.getString( "dept_cd"		, "" ) );
			res.param.addValue( "g_dept_nm"				, aUser.getString( "dept_nm"		, "" ) );
			res.param.addValue( "g_org1_cd"				, aUser.getString( "org1_cd"		, "" ) );
			res.param.addValue( "g_org1_nm"				, aUser.getString( "org1_name"		, "" ) );
			res.param.addValue( "g_org2_cd"				, aUser.getString( "org2_cd"		, "" ) );
			res.param.addValue( "g_org2_nm"				, aUser.getString( "org2_name"		, "" ) );
			res.param.addValue( "g_org3_cd"				, aUser.getString( "org3_cd"		, "" ) );
			res.param.addValue( "g_org3_nm"				, aUser.getString( "org3_name"		, "" ) );
			res.param.addValue( "g_cell_code"			, aUser.getString( "cell_code"		, "" ) );
			res.param.addValue( "g_cell_name"			, aUser.getString( "cell_name"		, "" ) );

			res.param.addValue( "g_pbx_id"				, aUser.getString( "pbx_id"			, "" ) );
			res.param.addValue( "g_tenant_id"           , aUser.getString( "tenant_id"      , "" ) );
			res.param.addValue( "g_alarm_lv"            , aUser.getString( "a_alarm_level"  , "" ) );
			res.param.addValue( "g_time_interval"		, aUser.getString( "time_interval"	, "" ) );
			res.param.addValue( "g_time_interval_sch"	, aUser.getString( "time_interval_sch"	, "" ) );
			res.param.addValue( "g_work_stime"			, aUser.getString( "work_stime"		, "" ) );
			res.param.addValue( "g_work_etime"			, aUser.getString( "work_etime"		, "" ) );
			res.param.addValue( "g_currdate"			, aUser.getString( "currdate"		, "" ) );
			res.param.addValue( "g_url"					, aUser.getString( "g_url"			, "" ) );
			res.param.addValue( "g_file_url"			, aUser.getString( "g_file_url"		, "" ) );
			//res.param.addValue( "g_gpa_gbn"		       	, aUser.getString( "gpa_gbn"		, "" ) );
			//res.param.addValue( "g_object_id"		   	, aUser.getString( "object_id"		, "" ) );

			res.param.addValue( "g_clientip"			, clientIp                                 );
			res.param.addValue( "g_wasid"				, serverName                               );
			res.param.addValue( "g_wasip"				, serverIp                                 );
			res.param.addValue( "g_wasport"				, request.getServerPort()                  );
			res.param.addValue( "g_contextRoot"			, request.getContextPath()                 );
			//res.param.addValue( "g_serverUrl"			, "http://" + serverIp + ":" + request.getServerPort() + request.getContextPath());
			res.param.addValue( "g_serverUrl"			, "https://" + serverIp + request.getContextPath());
			res.param.addValue( "g_login_time"			, DateUtil.getTime("HH:mm:ss")             );
			res.param.addValue( "g_jedi_home"			, WebInitializer.JEDI_HOME                 );

			res.param.addValue( "g_last_connect_ip"		, aUser.getString( "REG_IP", "" ) );		// 통화이력 청취 가능 IP용으로 사용 2011.12.05
			res.param.addValue( "g_last_login_date"		, aUser.getString( "LAST_LOGIN_DATE", "" ) );
			res.param.addValue( "g_last_login_time"		, aUser.getString( "LAST_LOGIN_TIME", "" ) );			
			res.param.addValue( "g_min_sortno"			, aUser.getString( "MIN_SORTNO", "" ) );
			res.param.addValue( "g_max_sortno"			, aUser.getString( "MAX_SORTNO", "" ) );	
			
			
			String domain = new URL(request.getRequestURL().toString()).getHost();
			//res.param.addValue( "g_domainUrl"			, "http://" + domain + ":" + request.getServerPort() + request.getContextPath());
			res.param.addValue( "g_serverUrl"			, "https://" + serverIp + request.getContextPath());
			
			
			//JSONObject jsonStr =XwingProcessor.sendResponse(res, res.param); 
			//ErrorLogger.debug("Xwing.jsp out : "+ jsonStr.toString());
			//ErrorLogger.debug("####################debug : ");
			//ErrorLogger.debug(res.param.toString());
			//IVRLogger.debug("#######session_yn : "+session_yn);
			
			if("N".equals(session_yn)){
				if(SessionManager.getInstance().getHttpSessionById(userid) != null) {
					HttpSession v_session = SessionManager.getInstance().getHttpSessionById(userid);
					BaseEntity v_be = (BaseEntity)v_session.getAttribute("BASE_ENTITY_KEY");
					String before_ip= v_be.getCommonDTO().getClientIp();
					//ip정보 조회하기
					//IVRLogger.debug("clienIp ::  "+clientIp + "  beforeIp ::" + before_ip);
					if(!before_ip.equals(clientIp)) {
						//throw new WebActionException("SESSION_CHECK", "IP: " + before_ip + " 에서 접속중입니다. 로그인 하시겠습니까?");
						if("Y".equals(sso_yn)) throw new WebActionException("SESSION_CHECK", "SESSION_CHECK::" + before_ip);
						else throw new WebActionException("SESSION_CHECK", "IP: " + before_ip + " 에서 접속중입니다. 로그인 하시겠습니까?");
					}
				}
			}	
			
			//IVRLogger.debug("##########세션 매니저##########");
			SessionManager.getInstance().login(request, entity);
			
			// 로그인 로그 처리(성공)
			//setLoginLog(common, userid, clientIp, (String)entity.getCommonDTO().getAttribute("wasip"), "Y", "", "");			 
		    setLoginLog(common, aUser.getString( "user_id" , "" ), clientIp, (String)entity.getCommonDTO().getAttribute("wasip"), "Y", "", "");
		    
		/*    if("Y".equals(sso_yn)){
		    	URL url = new URL("https://vadev.aig.co.kr/msens/common/main.xhtml");
		    	HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
		    	conn.setReadTimeout(500);
		    	
		    	conn.addRequestProperty("Cookie", "SMSESSION="+smsession);
		    	
		    	IVRLogger.debug("###################로그인##############");
		    	IVRLogger.debug("cookie = " + smsession);
		    	IVRLogger.debug(conn.toString());
		    	
		    }*/
			
			
		}catch(WebActionException e){
			throw e;
		}catch(Exception e){
			throw new WebActionException("login failed","로그인에 실패하였습니다",e);
		}
	}
	/**
	* @param common
	* @param userId
	* @param connectIp
	* @param wasIp
	* @param loginYn
	* @param errorCode
	* @param errorMsg
	*/
	private void setLoginLog(CommonDTO common, String userId, String connectIp, String wasIp, String loginYn, String errorCode, String errorMsg) {
		try {
			SQLParam sqlparam = new SQLParam();
			SQLParam sqlResultparam = new SQLParam();
			
			sqlparam.setSqlName("cs.com.logout_checklog");			
			sqlparam.addValue("user_id", userId);
			sqlparam.addValue("was_ip", wasIp);
			
			sqlResultparam = (SQLParam)BizDelegate.getInstance().execute("sqlService", common, sqlparam);	

			if(sqlResultparam.getCount() > 0) {
				sqlparam.clear();
				sqlparam.setSqlName("cs.com.logout_insertlog");			
				sqlparam.addValue("user_id", userId);			
				sqlparam.addValue("was_ip", wasIp);
				sqlparam.addValue("login_yn", "N");
				
				BizDelegate.getInstance().execute("sqlService", common, sqlparam);
			}
			
			sqlparam.clear();
			sqlparam.setSqlName("cs.com.login_insertlog");			
			sqlparam.addValue("user_id", userId);
			sqlparam.addValue("connect_ip", connectIp);
			sqlparam.addValue("was_ip", wasIp);
			sqlparam.addValue("login_yn", loginYn);
			sqlparam.addValue("error_cd", errorCode);
			sqlparam.addValue("error_msg", errorMsg);
			
			BizDelegate.getInstance().execute("sqlService", common, sqlparam);	
			
			/*
			 * 로그인에 로그인 및 IP 정보 넣는 컬럼 삭제됨
			 * */
			/*sqlparam.clear();
			sqlparam.setSqlName("cs.com.login.loginchk");			
			sqlparam.addValue("user_id", userId);			
			sqlparam.addValue("connect_ip", connectIp);		
			sqlparam.addValue("was_ip", wasIp);
			sqlparam.addValue("login_yn", "Y");
			
			BizDelegate.getInstance().execute("sqlService", common, sqlparam);*/
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
};
