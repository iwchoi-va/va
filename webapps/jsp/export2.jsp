<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="xwing.export.*"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.locus.jedi.log.*" %>

<%
out.clear();
out=pageContext.pushBody();
%>
 <%
 request.setCharacterEncoding("UTF-8");
 
 String filename = new String(request.getParameter("filename").getBytes(), "UTF-8");
 //String filename = new String(request.getParameter("filename").getBytes("UTF-8"), "UTF-8"); 
 out.println("filename####"+filename);
 //보안 취약성으로 인한 수정(2018.07.25) -> 상대경로 유효성 체크
 filename = filename.replaceAll("/", "");
 filename = filename.replaceAll("\\\\", "");
 filename = filename.replaceAll("\\.", "");
 filename = filename.replaceAll("&", "");
 //---------------------------------------------------------------
 
 if(filename == null){
	 filename = "Excel_";
 }
 DateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
 Date nowDate = new Date();
 String tempDate = sdFormat.format(nowDate);
 filename =filename + "_" + tempDate+ ".xls";

 ExcelExport export = new ExcelExport();
 File file = new File(filename); 
 out.println("filename####"+filename);
 export.Export(request, file);
 
 response.setContentType("application/vnd.ms-excel;charset=UTF-8"); 
 response.setHeader("Content-disposition","attachment;");
 response.setHeader("Content-disposition","attachment;filename="+java.net.URLEncoder.encode(filename,"UTF-8"));
 response.setHeader("Content-Description", "JSP Generated Data");
  
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
/* 	ControlExcel excel = new ControlExcel();
	pageContext.pushBody();
	excel.Download(request,response); */
%>
