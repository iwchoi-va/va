package xwing.export;

import java.io.*;
import java.net.*;
import java.util.*;
import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import javax.servlet.*;
import javax.servlet.http.*;

public class ControlExcel {

	public void CreateData(){
		/* 
		 * 1. excel�� ������ ������ ����� 
		 */
		try {
			File excel = new File("output.xls");
			WritableWorkbook workbook = Workbook.createWorkbook(excel);
			workbook.createSheet("first",0);
			System.out.println("Sfsseesta rt");
			System.out.println(excel.exists());
			System.out.println(excel.getAbsoluteFile());
			WritableSheet sheet = workbook.getSheet(0);
			Label label = null;
			for( int j=0; j<100; j++){
				label = new Label(j,0,"Test Cell "+j);
				sheet.addCell(label);
			}
			workbook.write();
			workbook.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 /**
	   * �ش� �Է� ��Ʈ�����κ��� ���� �����͸� �ٿ�ε� �Ѵ�.
	   * 
	   * @param request
	   * @param response
	   * @param is
	   *            �Է� ��Ʈ��
	   * @param filename
	   *            ���� �̸�
	   * @param filesize
	   *            ���� ũ��
	   * @param mimetype
	   *            MIME Ÿ�� ����
	   * @throws ServletException
	   * @throws IOException
	   */
	  public static void Download(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String mime = "";
	    
	    byte[] buffer = new byte[8192];
	 
	    response.setContentType("application/octet-stream; charset=euc-kr" );
	 
	    // �Ʒ� �κп��� euc-kr �� utf-8 �� �ٲٰų� URLEncoding�� ���ϰų� ���� �׽�Ʈ��
	    // �ؼ� �ѱ��� ���������� �ٿ�ε� �Ǵ� ������ �����Ѵ�.
	    String userAgent = request.getHeader("User-Agent");
	 
	    // attachment; �� ������ IE�� ��� ������ �ٿ�ε�â�� ���. ��Ȳ�� ��� ����Ѵ�.
	    if (userAgent != null && userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 ����
	      response.setHeader("Content-Disposition", "filename=" + URLEncoder.encode("output.xls", "UTF-8") + ";");
	    } else if (userAgent != null && userAgent.indexOf("MSIE") > -1) { // MS IE (������ 6.x �̻� ����)
	      response.setHeader("Content-Disposition", "attachment; filename="
	          + java.net.URLEncoder.encode("output.xls", "UTF-8") + ";");
	    } else { // ����� �����
	      response.setHeader("Content-Disposition", "attachment; filename="
	          + new String("output.xls".getBytes("euc-kr"), "latin1") + ";");
	    }
	 
	    BufferedInputStream fin = null;
	    BufferedOutputStream outs = null;
	 
	    try {
	      InputStream is = null;
	      fin = new BufferedInputStream( new FileInputStream(new File("output.xls")));
	      outs = new BufferedOutputStream(response.getOutputStream());
	      int read = 0;
	 
	      while ((read = fin.read(buffer)) != -1) {
	        outs.write(buffer, 0, read);
	      }
	    } catch (IOException ex) {
	        // Tomcat ClientAbortException�� ��Ƽ� �����ϵ��� ó�����ִ°� ����.
	    } finally {
	      try {
	        outs.close();
	      } catch (Exception ex1) {
	      }
	 
	      try {
	        fin.close();
	      } catch (Exception ex2) {
	 
	      }
	    } // end of try/catch
	  }
}
