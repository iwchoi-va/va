<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.portlet.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.fileupload.util.*" %>
<%@ page import="xwing.importExcel.*" %>
<%
String result = "";
try{
    boolean isMultipart=ServletFileUpload.isMultipartContent(request);
    if(isMultipart){
        FileItemFactory item=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(item);
        List items=null;
        try{
            items=upload.parseRequest(request);
        }catch(FileUploadException e){
            e.getMessage();
        }
        
        Iterator itr=items.iterator();
        ImportControl ic = new ImportControl();
        while(itr.hasNext()){
            FileItem itemname=(FileItem) itr.next();
            if( itemname.getName() != null ){
            	ic.init(itemname.getName(), itemname.getInputStream());
                result = ic.parseRequest();
            }else if( "config".equals(itemname.getFieldName()) ) {
            	String confs[] = new String(itemname.getString().getBytes("8859_1"),"utf-8").split(":");
            	ic.setConfig(confs);
            }
       	}
    }
   }catch(Exception e){

   }
%>
<%=result%>
