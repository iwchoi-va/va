<%@ page language="java" contentType="text/html;charset=euc-kr" %>
<%@ page import="com.locus.jedi.log.*" %>
<%@ page import="com.locus.jedi.service.sql.*" %>
<%@ page import="com.locus.jedi.biz.*" %>
<%@ page import="com.locus.jedi.waf.controller.*" %>
<%@ page import="com.locus.jedi.transfer.*" %>
<%@ page import="com.locus.jedi.waf.*" %>
<%@ page import="jedix.xwing.util.*" %>
<%@ page import="org.json.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*"%>
<%@ page import="java.util.regex.*" %>
<jsp:useBean id="jediReq" class="com.locus.jedi.waf.controller.JediRequest" scope="request"/>
<jsp:useBean id="jediRes" class="com.locus.jedi.waf.controller.JediResponse" scope="request"/>
<%
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	ArrayList sessions = SessionManager.getInstance().getAllHttpSessions();
	Iterator iterator = sessions.iterator();
	
	ListParam listParam = new ListParam(new String[]{
	        "user_id", 
			"emp_no", 
			"user_name", 
			"clientIp",
			"creationTime",
			"lastAccessedTime"
		}
	);

	while(iterator.hasNext()) {
		Map.Entry entry = (Map.Entry)iterator.next();
		String key = (String)entry.getKey();
		HttpSession hs = (HttpSession)entry.getValue();

		try{
			BaseEntity be = (BaseEntity)hs.getAttribute(WebKeys.SESSION_KEY);
			CommonDTO common = be.getCommonDTO();

				listParam.addRow(new String[] {
				    common.getUserId(),
					common.getString("emp_no"),
					common.getUserName(),
					common.getClientIp(),
					sdf.format(new Date(hs.getCreationTime())),
					sdf.format(new Date(hs.getLastAccessedTime()))
				}
			);
		} catch(Exception ex) {}
	}
	jediRes.param.addValue("sessioninfo", listParam);
	
	/*for character injection -> XSS 유효성 체크 추가(2018.07.27)*/
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