<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ page import="java.io.*"%>
<%@ page import="com.locus.jedi.waf.*"%>
<%@ page import="com.locus.jedi.waf.controller.*"%>
<%

   Throwable t = exception;
   String message = t.getMessage();
   if(exception instanceof ServletException){
	   ServletException e = (ServletException)exception;
	   if(e.getRootCause() == null) t = e;
	   else t = e.getRootCause();
   }
   RequestSpec spec = (RequestSpec)request.getAttribute(WebKeys.CURRENT_REQUEST_KEY); 
   String temp="";
   if(spec != null) temp = spec.getDescription();
   
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>무제 문서</title>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<script language="JavaScript" type="text/JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
//-->
</script>
<style type="text/css">
<!--
td {
	font-family: "굴림", "굴림체", "돋움", "돋움체";
	font-size: 12px;
}

.bd {
	border: 1px solid #dddddd;
	background-color: #EFF3F6;
}
-->
</style>
</head>

<body leftmargin="5" topmargin="5">
	<table width="400" border="0" align="center" cellpadding="0"
		cellspacing="0">
		<tr>
			<td><img
				src="<%=request.getContextPath() %>/images/error-4.gif"
				width="400" height="108"></td>
		</tr>
		<tr>
			<td
				background="<%=request.getContextPath() %>/images/error-2.gif">
				<table width="370" border="0" align="center" cellpadding="3"
					cellspacing="1" class="bd" style="table-layout: fixed">
					<tr bgcolor="#FFFFFF">
						<td align="center" bgcolor="#f9f9f9"><font color="#006699">Exception
								Type</font></td>
						<td><%=t.getClass().getName()%></td>
					</tr>
					<tr bgcolor="#FFFFFF">
						<td align="center" bgcolor="#f9f9f9"><font color="#006699">Exception
								Message</font></td>
						<td><%=message%></td>
					</tr>
					<tr bgcolor="#f9f9f9">
						<td colspan="2" align="center"><font color="#006699">Exception
								trace</font></td>
					</tr>
				</table> <br>
			</td>
		</tr>
		<tr>
			<td><img
				src="<%=request.getContextPath() %>/images/error-3.gif"
				width="400" height="8"></td>
		</tr>
	</table>
</body>
</html>
