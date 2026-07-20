<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="sens.service.webaction.*"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN " "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html>
<head>
	<meta http-equiv="Content-Type" content="application/vnd.ms-excel;charset=utf-8">
</head>
<body>

	<%
		 request.setCharacterEncoding("UTF-8");
		 String preName = "STT원문다운_";
		 String ucid = request.getParameter("ucid");
		 
		//String filename = new String(preName.getBytes(), "UTF-8");
		 String filename = URLEncoder.encode(preName, "UTF-8");
		 
		 if(filename == null){
			 filename = "Excel_";
		 }
		 DateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 Date nowDate = new Date();
		 String tempDate = sdFormat.format(nowDate);
		 filename = filename + "_" + tempDate;
		 
		 SttExcelExport export = new SttExcelExport();
		 File file = new File(filename); 
		 export.Export(request, file, ucid);
		 
		//response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
		response.setHeader("Content-Description", "JSP Generated Data");
		
		response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Transfer-Encoding", "binary;");
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");
        
		byte b[]=new byte[(int)file.length()];

		 if(file.isFile()){
			 BufferedInputStream fin=new BufferedInputStream(new FileInputStream(file));
			 BufferedOutputStream outs=new BufferedOutputStream(response.getOutputStream());
			 
			 
			 int read=0;
			 while((read=fin.read(b))!=-1){
				 outs.write(b,0,read);
			 }
			 	 
			 outs.close();
			 fin.close();
			 file.delete();
			}
		 
	%>
</body>
</html>