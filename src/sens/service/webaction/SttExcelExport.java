package sens.service.webaction;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import sun.security.krb5.internal.crypto.Aes256;
import wfm.com.util.AES256Cipher;

import com.hansol.audio.util.PropertyUtil;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;


public class SttExcelExport extends XwingWebAction {
	/**
	 * 
	* @param req
	* @param res
	* @throws WebActionException
	*/
	
	Workbook workbook = null;
	
	public void Export(HttpServletRequest req, File file, String ucid) throws Exception{

		ListParam content = new ListParam(new String[] {"UCID", "STT_SENT"});
		
		try {
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("cs.common.getContents.sel");
			sqlParam.addValue("UCID", ucid);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			//스폰서 구분에 따라 화자구분시 엑셀 다운로드에상담사/고객표시 필요!(현재는 스폰서 코드값이 없어서 추후에 수정할 예정)
			if(sqlResult.getCount() > 0){
				for(int i =0; i< sqlResult.getCount(); i++){
					String enc_flag =  sqlResult.getListParam("CS_STT_CONTENTS").getParam(i).getString("ENC_FLAG");
					String v_sent =  sqlResult.getListParam("CS_STT_CONTENTS").getParam(i).getString("STT_SENT");
					String v_rtx_yn = sqlResult.getListParam("CS_STT_CONTENTS").getParam(i).getString("RXTX_GB");
					
					Properties prop2 = new Properties();
					
					prop2 = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/webapps/WEB-INF/conf.properties");
					String aes_key = prop2.getProperty("cipher_key");
					
					AES256Cipher aes256 = AES256Cipher.getInstance(aes_key);
					
					if("Y".equals(enc_flag)){ 
						//Aes256 aes256 = new Aes256();
						v_sent = aes256.decrypt(v_sent);
					}
					
					String sent = v_sent.split("\t")[2];
					
					if("Y".equals(v_rtx_yn)){
						String rtx = v_sent.split("\t")[0];
						
						if(rtx.equals("RX")) sent = "고객 : " + sent;
						else sent = "상담사 : " + sent;
					
					}
						
					content.addRow(new Object[] {
							sqlResult.getListParam("CS_STT_CONTENTS").getParam(i).getString("UCID"),
							sent
					});
					
					
				}
			}
			
			ErrorLogger.debug(content.toString());
			
			
			buildExcelDocument(content);
			
            FileOutputStream fileOutput = new FileOutputStream(file);
		    workbook.write(fileOutput);
		    fileOutput.close();
             
		    ErrorLogger.debug("#########엑셀 다운로드 완료#########");
	
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void buildExcelDocument(ListParam content){
		workbook = new SXSSFWorkbook();

		Sheet sheet = workbook.createSheet("STT원문");
        Row row = null;
        Cell cell = null;
        int rowCount = 0;
        int cellCount = 0;

        // 첫번째 로우 폰트 설정
        Font headFont = workbook.createFont();
        headFont.setFontHeightInPoints((short) 11);
        headFont.setFontName("맑은 고딕");
        //headFont.setBoldweight(BOLDWEIGHT_BOLD);

        // 첫번째 로우 셀 스타일 설정
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
        //headStyle.setVerticalAlignment(VERTICAL_CENTER);
        headStyle.setFont(headFont);
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setBorderTop(CellStyle.BORDER_THIN);

        // 바디 폰트 설정
        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 9);
        bodyFont.setFontName("맑은 고딕");

        // 바디 스타일 설정
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setFont(bodyFont);
        bodyStyle.setWrapText(true);
       // bodyStyle.setVerticalAlignment(VERTICAL_CENTER);
       // bodyStyle.setBorderBottom(CellStyle.BORDER_THIN);
       // bodyStyle.setBorderLeft(CellStyle.BORDER_THIN);
       // bodyStyle.setBorderRight(CellStyle.BORDER_THIN);
       // bodyStyle.setBorderTop(CellStyle.BORDER_THIN);

        CellStyle bodyStyleAlign = workbook.createCellStyle();
        bodyStyleAlign.setFont(bodyFont);
        bodyStyleAlign.setWrapText(true);
      //  bodyStyleAlign.setVerticalAlignment(VERTICAL_CENTER);
       // bodyStyleAlign.setBorderBottom(CellStyle.BORDER_THIN);
       // bodyStyleAlign.setBorderLeft(CellStyle.BORDER_THIN);
       // bodyStyleAlign.setBorderRight(CellStyle.BORDER_THIN);
       // bodyStyleAlign.setBorderTop(CellStyle.BORDER_THIN);
        bodyStyleAlign.setAlignment(CellStyle.ALIGN_CENTER);

        // 제목 셀 생성
        row = sheet.createRow(rowCount++);
        cell = row.createCell(cellCount++);
        cell.setCellStyle(headStyle);
        cell.setCellValue("STT 문장");
        
        String sent = "";
        
        ErrorLogger.debug("###########SENT");
        ErrorLogger.debug(content.toString());
        // 데이터 셀 생성
        for (int i = 0; i< content.rowSize(); i++) {
        	
        	if(i== 0 || i % 30 != 0){ //30 line 씩 합쳐서 넣음
        		sent += content.getValue(i, "STT_SENT").toString() + "\n";
        	}else{
        		row = sheet.createRow(rowCount++);
                cellCount = 0;
               
                cell = row.createCell(cellCount++);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(sent);
                
                //ErrorLogger.debug(sent);
 
                sent = "";
        	}           
        }
        
        if(content.rowSize() < 30){
        	row = sheet.createRow(rowCount++);
            cellCount = 0;
           
            cell = row.createCell(cellCount++);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(sent);
            
            sent = "";
        }

        // 셀 와이드 설정
        for (int i = 0; i < 1; i++){
            sheet.autoSizeColumn(i, true);
            //sheet.setColumnWidth(i, (sheet.getColumnWidth(i))-512 ); 
            
        }

        
	}
	
}


