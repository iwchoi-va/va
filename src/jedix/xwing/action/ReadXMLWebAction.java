package jedix.xwing.action;

import java.io.File;

import jxl.Sheet;
import jxl.Workbook;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class ReadXMLWebAction extends XwingWebAction
{
	public void perform(JediRequest request, JediResponse response)
	throws WebActionException
	{ 
		try{
			
			File f 			= null;
			
			Workbook w 		= null;
			Sheet s 		= null;
			
			String[] col 	= null;
			
			ListParam lp 	= null;
			Param p			= null;
			
			int rows 		= 0;
			int cols 		= 0;
			
			String url = request.param.getString("XMLURL");
			// XMLURL 키값으로 XML경로를 받음.

			if(url == null || "".equals(url)) {
				
				throw new NullPointerException();
			}
			url = cleanString(url);
			f 		= new File(url);
			// 입력받은 주소값.
			
			w 		= Workbook.getWorkbook(f);
			s 		= w.getSheet(0);
			// 0번 시트 선택.
			
			rows 	= s.getRows();
			cols 	= s.getColumns();

			col 	= new String[cols];
			// ListParam만들기 위한 컬럼배열.
			
			for(int i = 0; i < cols; i++) {
				
				col[i] = s.getRow(0)[i].getContents();
			}
			// 컬럼정보를 얻음.
			
			lp 		= new ListParam(col);
			// Param정보를 넣기 위한 ListParam을 만듬.
			p  		= new Param();
			
			for(int i = 1; i < rows; i++) {
				
				for(int j = 0; j < cols; j++) {
					
					p.addValue(col[j], s.getRow(i)[j].getContents());
					// Param에 컬럼명(키값)과 값 추가.(HashMap)
				}
				
				lp.addParam(p);
				// ListParam에 Param을 담음. 
				p.clear();
				// p에 저장된 Param을 지움.
			}
			
			w.close();
			// 문서를 닫음.
			response.param.addValue("XMLData", lp);
			// 클라이언트로 ListParam 전송~.
			
		}catch(Exception e){
			e.printStackTrace();
			throw new WebActionException(e.getMessage(),e);
		}
	}
	public static String cleanString(String aString) { 
	    if (aString == null) { return null; } 
	    String cleanString = ""; 
	    for (int i = 0; i < aString.length(); ++i) { 
	         cleanString += cleanChar(aString.charAt(i)); } 
	    return cleanString; } 

	private static char cleanChar(char aChar) { 
	  // 0 - 9 
		for (int i = 48; i < 58; ++i) { if (aChar == i){ return (char) i; } } 
	  // 'A' - 'Z' 
		for (int i = 65; i < 91; ++i) { if (aChar == i){ return (char) i; } } 
	  // 'a' - 'z' 
		for (int i = 97; i < 123; ++i) { if (aChar == i){ return (char) i; } } 
	  // other valid characters 
		return getSpecialLetter(aChar); 
	} 

	public static char getSpecialLetter(char aChar){ 
	  switch (aChar) { case '/': return '/'; case '.': return '.'; case '-': return '-'; case '_': return '_'; case ' ': return ' '; 
	                       case ':': return ':'; case '&': return '&'; default: return '%'; }
	}
};

