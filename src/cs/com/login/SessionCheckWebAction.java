package cs.com.login;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.LoginException;
import com.locus.jedi.waf.UserSession;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.waf.controller.WebInitializer;

import jedix.xwing.action.XwingWebAction;

public class SessionCheckWebAction extends XwingWebAction
{
	public void perform(JediRequest req, JediResponse res) throws WebActionException
	{
		try {
			HttpServletRequest request = req.getHttpServletRequest();
			
			/*String strEmpNo = (String)req.getCommonDTO().getAttribute("emp_no");
			*/
			String strEmpNo = (String)req.getCommonDTO().getAttribute("user_id");
			
			if( strEmpNo == null )
			{
				throw new WebActionException("Session Expired", 
						"세션이 만료되었습니다."
				);				
			}
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("cs.com.user_select");
			sqlparam.setResultName("user");
			sqlparam.addValue("userid", strEmpNo);
			sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",req.getCommonDTO(),sqlparam);
			
			if(sqlparam.getCount() <= 0)
			{
				throw new WebActionException(LoginException.getReason(
					LoginException.NO_SUCH_USER), 
					"등록되지 않은 사용자입니다."
				);
			}

			Param aUser = sqlparam.getListParam("user").getParam(0);
			
			if("".equals(aUser.getString("user_grade_cd", "").trim())) {
				throw new WebActionException(LoginException.getReason(
					LoginException.DENIED_USER), 
					"사용자 등급을 설정하십시오."
				);				
			}
			
			 
			
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
			res.param.addValue( "g_job_nm"				, aUser.getString( "job_name"		, "" ) );
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
			res.param.addValue( "g_time_interval_sch"	, aUser.getString( "time_interval_sch", ""));
			res.param.addValue( "g_work_stime"			, aUser.getString( "work_stime"		, "" ) );
			res.param.addValue( "g_work_etime"			, aUser.getString( "work_etime"		, "" ) );
			res.param.addValue( "g_currdate"			, aUser.getString( "currdate"		, "" ) );
			res.param.addValue( "g_url"					, aUser.getString( "g_url"			, "" ) );
			res.param.addValue( "g_file_url"			, aUser.getString( "g_file_url"		, "" ) );
			res.param.addValue( "g_gpa_gbn"		       	, aUser.getString( "gpa_gbn"		, "" ) );
			res.param.addValue( "g_object_id"		   	, aUser.getString( "object_id"		, "" ) );

			res.param.addValue( "g_clientip"			, request.getRemoteAddr()                  );
			res.param.addValue( "g_wasid"				, InetAddress.getLocalHost().getHostName() );
			res.param.addValue( "g_wasip"				, InetAddress.getLocalHost().getHostAddress());
			res.param.addValue( "g_wasport"				, request.getServerPort()                  );
			res.param.addValue( "g_contextRoot"			, request.getContextPath()                 );
		//	res.param.addValue( "g_serverUrl"			, "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + request.getServerPort() + request.getContextPath());
			res.param.addValue( "g_serverUrl"            , "http://" + getLocalIpAddress() + ":" + request.getServerPort() + request.getContextPath());
			res.param.addValue( "g_login_time"			, DateUtil.getTime("HH:mm:ss")             );
			res.param.addValue( "g_jedi_home"			, WebInitializer.JEDI_HOME                 );

			res.param.addValue( "g_last_connect_ip"		, aUser.getString( "REG_IP", "" ) );		// 통화이력 청취 가능 IP용으로 사용 2011.12.05
			res.param.addValue( "g_last_login_date"		, aUser.getString( "LAST_LOGIN_DATE", "" ) );
			res.param.addValue( "g_last_login_time"		, aUser.getString( "LAST_LOGIN_TIME", "" ) );	
			
			res.param.addValue( "g_min_sortno"			, aUser.getString( "MIN_SORTNO", "" ) );
			res.param.addValue( "g_max_sortno"			, aUser.getString( "MAX_SORTNO", "" ) );

			String domain = new URL(request.getRequestURL().toString()).getHost();
			res.param.addValue( "g_domainUrl"			, "http://" + domain + ":" + request.getServerPort() + request.getContextPath() );
		}
		catch(WebActionException e)
		{
			throw e;
		} 
		catch(Exception e)
		{
			ErrorLogger.error("Error occurred in SessionCheckWebAction : "+ e.getMessage());
			throw new WebActionException("SessionCheckWebAction : "+ e.getMessage(), e);
		}
	}
			
    public String getLocalIpAddress() throws WebActionException {
        boolean loopback = true;
        String local = "";
        try {
          Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
          for(NetworkInterface netint : Collections.list(nets))
          {
            if(netint.isLoopback())
              continue;
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for(InetAddress inetAddress : Collections.list(inetAddresses))
            {
              if(inetAddress.getHostAddress() != null && inetAddress.getHostAddress().indexOf('.') != -1)
              {
                local = inetAddress.getHostAddress();
                loopback = false;
              }
            }
            if(!loopback)
              break;
          }
          
          return local;
          
        } catch (SocketException ex) {
            ErrorLogger.error("Error occurred in SessionCheckWebAction : "+ ex.getMessage());
            throw new WebActionException("SessionCheckWebAction : "+ ex.getMessage(), ex);
        }
   }

}
