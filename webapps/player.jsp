<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="cs.com.login.*"%>
<%@ page import="com.locus.jedi.waf.*" %>	
<%@ page import="com.locus.jedi.waf.controller.*" %>	
<%@ page import="com.locus.jedi.transfer.*" %>	
<%@ page import="com.locus.jedi.log.*" %>
<%@ page import="com.locus.jedi.util.*" %>	
<%@ page import="java.util.regex.*" %>
<%@ page import="com.locus.jedi.waf.SessionManager" %>	
<%@ page import="com.locus.jedi.waf.action.WebActionException" %>	

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<link rel="shortcut icon" href="msens_icon.ico" />
<link rel="icon" href="msens_icon.ico" />

<script>
<%!
	private String valCheck(String val){	
		if(val == null) val = "";
		
		String pattern = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
		Pattern p = Pattern.compile(pattern);
		Matcher m_val = p.matcher(val);
		
		if(m_val.find()) val = val.replaceAll(pattern,"").replaceAll("script","");
			
		return val;
	}
%>

<%
		request.setCharacterEncoding("UTF-8");
		
		String userid 	= valCheck(request.getHeader("USERID")); 
		String ced_no 	= valCheck(request.getParameter("ced_no"));
		String ucid 	= valCheck(request.getParameter("ucid"));
		String agent_id = valCheck(request.getParameter("agent_id"));
		String errorMsg = "";
		String redirect_url = "";
		
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
		}
	
		BaseEntity entity = new BaseEntity(userid);
		CommonDTO common = new CommonDTO(userid);
		entity.setCommonDTO(common);
		entity.setUserSession(new UserSessionImple(entity));
		SessionManager.getInstance().login(request, entity);
		
		response.sendRedirect("./PLY_IE/player_new(wmp).jsp?rec_key="+ucid+"&ced_no="+ced_no+"&play_user_id="+userid+"&agent_id="+agent_id);
%>
	
</script>
<title>M-SENS</title>
</head>
<body>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="center">
		<img name="notice">
		</td>
	</tr>
</table>
</body>
</html>
