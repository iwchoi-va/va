package sens.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.log.ErrorLogger;

public class XlsxConvertToLP {
	
	private ListParam lp = null;
	private Param domain = null;
	
	public XlsxConvertToLP(String filePath) {
		readXlsx(filePath, null);
	}

	public XlsxConvertToLP(String filePath, Param domainData) {
		readXlsx(filePath, domainData);
	}
	
	private void readXlsx(String filePath, Param domainData) {
		XSSFWorkbook workbook = null;
		
		XSSFSheet sheet = null;
		XSSFCell cell    =  null;
		XSSFRow row	= null;
		
		String[] headers 	= null;
		String[] cols 	= null;
		String[] rows = null;
		//String[][] rowcols = null;
		//new String[sheet.getRows()+1][cols.length];
		ArrayList<String[]> rowcols = new ArrayList<String[]>(); 
		
		
		filePath= filePath.replace("\\", "//");
		//System.out.println(":"+filePath);
		filePath = cleanString(filePath);
		ErrorLogger.debug("▶▶▶▶▶▶▶[readXlsx]▶▶▶▶▶▶▶"+filePath);
		
		try {
			//workbook = XSSFWorkbook.getWorkbook(new File(filePath));
			workbook  =  new XSSFWorkbook(new FileInputStream(new File(filePath)));

			sheet = workbook.getSheetAt(0);

			int rowCount = sheet.getPhysicalNumberOfRows();
			//rows = new String[rowCount];
			for(int i = 0; i < rowCount+1; i++) {
				row = sheet.getRow(i);
				if(row != null){
					int cells = row.getPhysicalNumberOfCells();					
					rows 	= new String[cells+1];
					if(i==0){
						cols 	= new String[cells+1];
					}
				    for(int j = 0; j < cells+1; j++) {
				    	if(i == 0) {
				    		
							if(j == 0) {
								cols[j] = "A";
							}else if (j==1){
								cols[j] = "B";
							}else if (j==2){
								cols[j] = "C";
							}else if (j==3){
								cols[j] = "D";
							}else if (j==4){
								cols[j] = "E";
							}else if (j==5){
								cols[j] = "F";
							}else if (j==6){
								cols[j] = "G";
							}else if (j==7){
								cols[j] = "H";
							}else if (j==8){
								cols[j] = "I";
							}else if (j==9){
								cols[j] = "J";
							}else if (j==10){
								cols[j] = "K";
							}else if (j==11){
								cols[j] = "L";
							}else if (j==12){
								cols[j] = "M";
							}else if (j==13){
								cols[j] = "N";
							}else if (j==14){
								cols[j] = "O";
							}else if (j==15){
								cols[j] = "P";
							}else if (j==16){
								cols[j] = "Q";
							}else if (j==17){
								cols[j] = "R";
							}else if (j==18){
								cols[j] = "S";
							}else if (j==19){
								cols[j] = "T";
							}else if (j==20){
								cols[j] = "U";
							}else if (j==21){
								cols[j] = "V";
							}else if (j==22){
								cols[j] = "W";
							}else if (j==23){
								cols[j] = "X";
							}else if (j==24){
								cols[j] = "Y";
							}else if (j==25){
								cols[j] = "Z";
							}
							//ErrorLogger.debug("▶▶▶▶▶▶▶[cols[j]]▶▶▶▶▶▶▶"+ cols[j]);
						} 
				    	
			    		cell = row.getCell(j);
			    		String temp = "";
						if(cell != null){
							//ErrorLogger.debug("▶▶▶▶▶▶▶[cell.getCellType()]▶▶▶▶▶▶▶"+ cell.getCellType());
							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_FORMULA:
								temp=cell.getCellFormula();
			                    break;
			                case XSSFCell.CELL_TYPE_NUMERIC:
			                	temp=(int)cell.getNumericCellValue()+""; //정수형이면 '숫자.0'이 뒤에 붙기에 int로 형변환. 20180402
			                    break;
			                case XSSFCell.CELL_TYPE_STRING:
			                	temp=cell.getStringCellValue()+"";
			                    break;
			                case XSSFCell.CELL_TYPE_BLANK:
			                	temp=cell.getBooleanCellValue()+"";
			                    break;
			                case XSSFCell.CELL_TYPE_ERROR:
			                	temp=cell.getErrorCellValue()+"";
			                    break;
			                default :
			                	temp = "";
								break;
							}
							//ErrorLogger.debug("▶▶▶▶▶▶▶[temp]▶▶▶▶▶▶▶"+ temp);
						}						
				    	   	
				    	
						rows[j] = temp.replace("-", "");
						//ErrorLogger.debug("▶▶▶▶▶▶▶[cols[j]]▶▶▶▶▶▶▶"+ cols[j]);
				    	
					}
				    //rows[i] = cols;
				    rowcols.add(rows);
				    //ErrorLogger.debug("▶▶▶▶▶▶▶[rowcols.add]▶▶▶▶▶▶▶");
				}
				//rowcols.add(rows);
			    
			}
			lp = new ListParam(cols);
			//ErrorLogger.debug("▶▶▶▶▶▶▶[rowcols.length]▶▶▶▶▶▶▶"+ rowcols.size());
			for(int i = 0; i < rowcols.size(); i++) {
					lp.addRow((Object[])rowcols.get(i));
 			}
			//domain.addValue("rowCnt", rowCount);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			ErrorLogger.debug("▶▶▶▶▶▶▶7[IOException]▶▶▶▶▶▶▶"+e);
			e.printStackTrace();
		}
	}

	public ListParam getListParam() {
		
		return lp;
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
