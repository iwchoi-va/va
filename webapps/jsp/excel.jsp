<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="com.locus.jedi.transfer.*" %>
<%@ page import="com.locus.jedi.service.sql.SQLParam" %>
<%@ page import="com.locus.jedi.waf.CommonDTO" %>
<%@ page import="com.locus.jedi.util.DateUtil" %>
<%@ page import="com.locus.jedi.log.ErrorLogger" %>
<%@ page import="util.ExcelUtil" %>
<jsp:useBean id="jediReq" class="com.locus.jedi.waf.controller.JediRequest" scope="request"/>
<jsp:useBean id="jediRes" class="com.locus.jedi.waf.controller.JediResponse" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head></head>
<body>
<%	
CommonDTO common = jediReq.getCommonDTO();

ErrorLogger.debug ("************ 파일명2 :: " + new String (new String(jediReq.param.getString("filename", "NoName")).getBytes("KSC5601"), "8859_1"));

String fileName  = jediReq.param.getString("filename", "NoName") + "_" + DateUtil.getTime("yyyyMMddHHmmss");
response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls"); 
response.setHeader("Content-Description", "JSP Generated Data");
response.setContentType("application/vnd.ms-excel");

String[] keys = jediRes.param.keys();

for (int i = 0; i < keys.length; i++) {
	
	Object object = jediRes.param.getValue(keys[i]);
	
	if (object instanceof ListParam) 
	{
		out.println (ExcelUtil.makeTable((ListParam)object));
	} 
	else if (object instanceof SQLParam) 
	{
		ListParam listParam = ((SQLParam)object).getListParam(keys[i]);

		ErrorLogger.debug("★ 7. keys[i] >>>>>>>>>>>>>>>>> " + keys[i] + " <<<<<<<<<<<<<<<<<<<<<<");
		ErrorLogger.debug("★ 8. listParam >>>>>>>>>>>>>>>>> " + listParam + "<<<<<<<<<<<<<<<<<<<<<<");
		ErrorLogger.debug("★ 9. ((SQLParam)object).getListParam(keys[i]) >>>>>>>>>>>>>>>>> " + ((SQLParam)object).getListParam(keys[i]) + "<<<<<<<<<<<<<<<<<<<<<<");
	
		out.println (ExcelUtil.makeTable(listParam));					
		
	}
	
	out.println("<br><br>");
}
%>
</body>
</html>