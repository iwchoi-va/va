<%@page import="com.sshtools.j2ssh.net.HttpRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="cs.com.login.*"%>
<%@ page import="com.locus.jedi.waf.controller.*" %>	
<%@ page import="com.locus.jedi.transfer.*" %>	
<%@ page import="com.locus.jedi.log.*" %>
<%@ page import="com.locus.jedi.util.*" %>	
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<link rel="shortcut icon" href="msens_icon.ico" />
<link rel="icon" href="msens_icon.ico" />

<script>

<%
		String errMsg = "";
		try{
			String userid 	= request.getHeader("USERID");
			
			Param param = new Param();
			JediRequest req = new JediRequest(request, null,null,null,null,param);
			JediResponse res = new JediResponse(response, null);
			
			req.param.addValue("userid", userid);
			req.param.addValue("sso_yn", "Y");
			req.param.addValue("session_yn", "Y");
			
			LoginWebAction login = new LoginWebAction();
			login.perform(req, res);
			response.sendRedirect("../common/main.xhtml");
		}catch(Exception  e){
			errMsg = e.getMessage();
			if(!"".equals(errMsg)){
%>
				errorMsg();
<%			}
		}
		
		
%>

function errorMsg(){
	alert('<%=errMsg%>');
}
</script>
<title>M-SENS</title>
</head>
<body>

</body>
</html>