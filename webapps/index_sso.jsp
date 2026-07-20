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
		request.setCharacterEncoding("UTF-8");
                response.setHeader("httpOnly","true");
		
		String userid 	= request.getHeader("USERID");
		String smsession 	= request.getHeader("SMSESSION");
		//String cookie 	= request.getHeader("Cookie");
		String errorMsg = "";
		String redirect_url = "";
		String session_yn = "N";
		String err = "";
		String flag = "N";
		//테스트용
		//userid = "999993";
		
		//개발 : http://ssodev.aig.co.kr/login/login.jsp
		//UAT : http://ssouat.aig.co.kr/login/login.jsp
		
		Code[] code = CodeUtil.getCodes("SYS106");
		for (int i = 0; code != null && i < code.length; i++) {
			if (!"Y".equalsIgnoreCase(code[i].getUseYn())) {
				continue;
			}
			
			if("Y".equals(code[i].getEtc2())) {
				redirect_url = code[i].getEtc1();
			}
		}
		
		
		if(userid == null || "".equals(userid)){
			response.sendRedirect(redirect_url);
			return;
		}else{
			if(userid.indexOf("?") != -1) userid = userid.substring(0,userid.length()-1);
			//response.setHeader("SMSESSION", userid);
			//session.setAttribute("SMSESSION", userid);
		}
		
		Param param = new Param();
		JediRequest req = new JediRequest(request, null,null,null,null,param);
		JediResponse res = new JediResponse(response, null);
		
		session_yn = "N";
		req.param.addValue("userid", userid);
		req.param.addValue("sso_yn", "Y");
		//req.param.addValue("smsession", smsession);
		req.param.addValue("session_yn", session_yn);
		
		try{
			LoginWebAction login = new LoginWebAction();
			login.perform(req, res);
			response.sendRedirect("common/main.xhtml");
		}catch(Exception e){
			err = e.toString();
			errorMsg = e.getMessage();
			if(!"".equals(errorMsg)){
				String[] temp = errorMsg.split("::");
				if("SESSION_CHECK".equals(temp[0])){
					errorMsg = "IP: " + temp[1] + "에서 접속중 입니다. 접속하시겠습니까?";
%>					
				login_session();
<%				
					
				}else{
%>
					errorMsg();
<%
				}
			}
				
		}
%>


function errorMsg(){	
	alert('<%=errorMsg%>');
}

function login_session(){
	if(confirm('<%=errorMsg%>')){
		window.location.href ="jsp/login_session.jsp";
	}else{
		window.close();
	}
}

</script>
<title>M-SENS</title>
</head>
<body>

</body>
</html>
