package sens.util;

import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import com.locus.jedi.log.ErrorLogger;

import jedix.xwing.action.XwingWebAction;

import java.io.File;



public class ExcelUtil extends XwingWebAction {
	/**
	 * 
	* @param req
	* @param res
	* @throws WebActionException
	*/
	private int rowCnt = 0;
	public void perform(JediRequest req, JediResponse res) 
	throws WebActionException {
		
		String cmd = req.param.getString("cmd", "");
		
		if ("getExcelFile".equals(cmd)) {
			getExcelFile(req, res);
		} else if ("saveExcel".equals(cmd)) {
			saveExcel(req, res);
		} else {
			
			return;
		}
	
	}
	
	/**
	 * 엑셀파일 가져오기 
	 * @param req
	 * @param res
	 * @throws WebActionException
	 */
	private void getExcelFile(JediRequest req, JediResponse res)
			throws WebActionException {
		try{
			ErrorLogger.debug("▶▶▶▶▶▶▶getExcelFile[START]▶▶▶▶▶▶▶");
			String filePath = req.param.getString("filePath");
			String fileGubun = req.param.getString("fileGubun");
			ErrorLogger.debug("▶▶▶▶▶▶▶getExcelFile[filePath]▶▶▶▶▶▶▶"+filePath);
			if("".equals(filePath)){ 
				throw new WebActionException("파일경로를 확인해주세요.");
			}else{
								
				ListParam list= new ListParam(new String[]{
						"A",
						"B",
						"C",
						"D",
						"E",
						"F",
						"G",
						"H",
						"I", 
						"J",
						"K",
						"L",
						"M",
						"N",
						"O",
						"P",
						"Q",						
						"R",
						"S"
						}
				);
				ErrorLogger.debug("▶▶▶▶▶▶▶CALL_START▶▶▶▶▶▶▶"+fileGubun);
				if("csv".equals(fileGubun)){ 
					ErrorLogger.debug("▶▶▶▶▶▶▶CALL_START[CsvConvertToLP]▶▶▶▶▶▶▶");
					CsvConvertToLP xctlp = new CsvConvertToLP(filePath);
					list= xctlp.getListParam();
				}else if("xlsx".equals(fileGubun)){ 
					ErrorLogger.debug("▶▶▶▶▶▶▶CALL_START[XlsxConvertToLP]▶▶▶▶▶▶▶");
					XlsxConvertToLP xctlp = new XlsxConvertToLP(filePath);
					list= xctlp.getListParam();
				}else{
					ErrorLogger.debug("▶▶▶▶▶▶▶CALL_START[XlsConvertToLP]▶▶▶▶▶▶▶");
					XlsConvertToLP xctlp = new XlsConvertToLP(filePath);
					list= xctlp.getListParam();
				}
				//list= xctlp.getListParam();
				rowCnt=list.rowSize();
				ErrorLogger.debug("▶▶▶▶▶▶▶CALL_END[XlsConvertToLP]▶▶▶▶▶▶▶"+list);
				res.param.addValue("DS_UPLOAD",list);
				res.param.addValue("rowCnt", rowCnt);
				ErrorLogger.debug("▶▶▶▶▶▶▶rowCnt[XlsConvertToLP]▶▶▶▶▶▶▶"+rowCnt);
				
			       //deleteDir(new File("D:\\jedi\\IS_TM\\webapps\\upload_file"));
						
				
				filePath = cleanString(filePath);
				File file = new File(filePath);
				 if(file.exists()) {
					 ErrorLogger.debug("▶▶▶▶▶▶▶[exists]▶▶▶▶▶▶▶"+file);
					 file.delete();
				 }else{
					 ErrorLogger.debug("▶▶▶▶▶▶▶[NOTexists]▶▶▶▶▶▶▶"+file);
				 }
			}
			
			ErrorLogger.debug("▶▶▶▶▶▶▶getExcelFile[END]▶▶▶▶▶▶▶");
		}catch(Exception e){
			throw new WebActionException("ExcelUitlWebAction(getExcelFile) : "+e.getMessage(),e);
		}
	}
	
	/**
	 * 엑셀파일 DB에 저장
	 * @param req
	 * @param res
	 * @throws WebActionException
	 */
	private void saveExcel(JediRequest req, JediResponse res) throws WebActionException {
		try{
			ErrorLogger.debug("▶▶▶▶▶▶▶saveExcel[START]▶▶▶▶▶▶▶"+req);
			FileUploadAction maktLstact = new FileUploadAction(req.getCommonDTO(), req.param);
			
			/*
			String errorResult = maktLstact.run(res);
			
			res.setError(!errorResult);
			*/
			
			String s_CUST_ANL_ID = maktLstact.run(res);
			//엑셀업로드 오류
			if("".equals(s_CUST_ANL_ID)){
				res.setError(true);
				res.setResultCode("99");
				res.setResultMessage("엑셀업로드 실패");
			//성공
			}else{
				res.setResultCode(s_CUST_ANL_ID);
				//res.setResultMessage("엑셀업로드 실패");
			}
			
			
			ErrorLogger.debug("▶▶▶▶▶▶[res.param]▶"+res.getResultCode());
			ErrorLogger.debug("▶▶▶▶▶▶▶saveExcel[END]▶▶▶▶▶▶▶");
			
		}catch(Exception e){
			ErrorLogger.debug("▶▶▶▶▶▶▶saveExcel[ERROR]▶▶▶▶▶▶▶"+e);
			res.setError(true);
			res.setResultCode("99");
			res.setResultMessage("엑셀업로드 실패");
			throw new WebActionException("ExcelUitlWebAction(saveExcel) : "+e.getMessage(),e);
		}
	}
	
	public static void deleteDir(File file){
		// File fileEx = new File(file);
	        File [] files = file.listFiles();
	       // if(file.exists()) 
	      // {
	        ErrorLogger.debug("▶▶▶▶▶▶▶[exists]▶▶▶▶▶▶▶"+file);
	        for(int i=0; i<files.length; i++){
	        	if(files[i].isDirectory()){
	        		deleteDir(files[i]);
	        	}else{
	        		files[i].delete();
	        	}
	        }
	        file.delete();
	       // }else{
	       // 	 ErrorLogger.debug("▶▶▶▶▶▶▶[Not exists]▶▶▶▶▶▶▶"+file);
	       // }
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
}


