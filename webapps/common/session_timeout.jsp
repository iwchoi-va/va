<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ page import="java.io.*"%>
<%@ page import="com.locus.jedi.service.sql.*" %>
<%@ page import="com.locus.jedi.biz.*" %>
<%@ page import="com.locus.jedi.waf.controller.*" %>
<%@ page import="com.locus.jedi.transfer.*" %>
<%@ page import="jedix.xwing.util.*" %>
<%@ page import="org.json.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="sens.service.webaction.Timestamp" %>
<jsp:useBean id="jediReq" class="com.locus.jedi.waf.controller.JediRequest" scope="request"/>
<jsp:useBean id="jediRes" class="com.locus.jedi.waf.controller.JediResponse" scope="request"/>
<%
	Timestamp timestamp = null;
	HttpServletRequest req = jediReq.getHttpServletRequest();

	timestamp = new Timestamp();
	String connect_ip = req.getRemoteAddr();
	
	try{
		timestamp.STimeoutInsertTimeStamp(connect_ip);
		timestamp.STimeoutInsertWorkStat(connect_ip);
	}catch(Exception e){
		e.printStackTrace();
	}


	jediRes.setResultCode("SessionTimeOut");
	jediRes.setResultMessage("다른 PC에서 동시접속으로 인해 세션이 만료되었습니다. 다시 로그인 해주세요.");
	jediRes.setError(true);
	
	/* for character injection -> XSS 유효성 체크 추가(2018.07.27)*/

	String pattern = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
	Pattern p = Pattern.compile(pattern);
	String [] keysRes = jediRes.param.keys();
	String newKey = "";
	String newVal = "";
	
	for(int i =0; i< keysRes.length; i++){
		Matcher m = p.matcher(keysRes[i]);
		
		if(m.find()){ //key에서 걸릴 때
			newKey = keysRes[i].replaceAll(pattern, "").replaceAll("script", "");
			newVal = jediRes.param.getValue(keysRes[i]).toString();
			
			jediRes.param.remove(keysRes[i]);
			jediRes.param.addValue(newKey, newVal);
			
		}
		
		String val = jediRes.param.getValue(keysRes[i],"").toString();
		Matcher m_val = p.matcher(val);
		
		if(m_val.find() && val.indexOf("_QUERY") == -1){ //VAL에서 걸릴 때
			newKey = keysRes[i];
			newVal = (jediRes.param.getValue(keysRes[i]).toString()).replaceAll(pattern, "").replaceAll("script", "");
			
			jediRes.param.addValue(newKey, newVal);
					
		}
		
		if(jediRes.getResultMessage() != null) jediRes.setResultMessage(jediRes.getResultMessage().replaceAll(pattern, "").replaceAll("script", ""));
	}	
	
	JSONObject jsonStr = XwingProcessor.sendResponse(jediRes,jediRes.param);
%>
<%=jsonStr.toString()%>
<%out.flush();%>