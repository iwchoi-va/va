<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.io.File"%>
<%@page import="java.net.*" %>
<%
	try {

		final int MAX_SIZE 			= 1024*1024*100; // 최대 파일 사이즈.
		//final String FILE_DIRECTORY = "/home/hansol/dev/engine/LA/source/lang/lang_test/docs"; ///upload_file"; // 파일 저장 경로.
		final String FILE_DIRECTORY = "/uploadFiles"; // 파일 저장 경로.
	
		System.out.println("####fileUpload2.jsp####");
		// 업로드 경로(realPath는 서버의 절대경로를 읽어옴)
		System.out.println(FILE_DIRECTORY);
		//String archive 			= FILE_DIRECTORY; //request.getRealPath(FILE_DIRECTORY); 
		String archive 			= request.getSession().getServletContext().getRealPath(FILE_DIRECTORY);
		String result 			= null; // 결과값.
		System.out.println("####real_path####"+archive);
		
		// 객체 생성과 동시에 파일업로드가됨. DefaultFileRenamePolicy객체에 의해 파일이름 중복시 자동변경.
		MultipartRequest mr 	= new MultipartRequest(request, archive, MAX_SIZE, "UTF-8", new DefaultFileRenamePolicy());;
		
		System.out.println("####success####");
		
		// 변경되었을수도 있는 파일 이름을 저장하기위해 mr인스턴스에서 업로드된 파일 이름+경로를 가져옴.
		String fileName = URLEncoder.encode( mr.getFilesystemName("files") );
		result = archive + "\\" + mr.getFilesystemName("files");
		result= result.replace("\\", "/");
		System.out.println("####Uploaded Path####"+result);
		
		// 정상적으로 업로드 되었을경우 파일경로와 에러코드 0번을 JSON형태로 뿌림.
		System.out.println("{\"ResultCode\":\"0\",\"ResultMessage\":\"no message\", \"FilePath\":\""+ result +"\", \"Error\":\"0\"}");
		out.println("{\"ResultCode\":\"0\",\"ResultMessage\":\"no message\", \"FilePath\":\""+ result +"\", \"Error\":\"0\"}");
		
		//System.out.println("{\"ResultCode\":\"0\",\"ResultMessage\":\"no message\", \"FilePath\":\""+ result +"\"}");
		//out.println("{\"ResultCode\":\"0\",\"ResultMessage\":\"no message\", \"FilePath\":\""+ result +"\"}");
		
	} catch (Exception e) {
		// 업로드중 에러 발생시 에러코드 1번과  에러메세지를 JSON형태로  뿌림.
		out.println("{\"ResultCode\":\"1\",\"ResultMessage\":\"파일 업로드중 에러 발생!\", \"Error\":\"0\"}");
		e.printStackTrace();
	}
%>