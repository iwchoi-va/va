package sens.util;

import java.io.File;
import java.io.IOException;


//import jxl.Sheet;
//import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.*;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.log.ErrorLogger;

public class XlsConvertToLP {
	
	private ListParam lp = null;
	private Param domain = null;
	
	public XlsConvertToLP(String filePath) {
		readXls(filePath, null);
	}

	public XlsConvertToLP(String filePath, Param domainData) {
		readXls(filePath, domainData);
	}
	
	private void readXls(String filePath, Param domainData) {
		Workbook workbook = null;
		filePath= filePath.replace("\\", "//");
		//System.out.println(":"+filePath);
		filePath = cleanString(filePath);
		try {
			workbook = Workbook.getWorkbook(new File(filePath));
			Sheet sheet = workbook.getSheet(0);
			
			String[] cols 	= new String[sheet.getColumns()];
			String[] row = new String[cols.length];
			String[][] rows = new String[sheet.getRows()+1][cols.length];
		
			int rowCount = sheet.getRows();
			for(int i = 0; i < rowCount+1; i++) {
				for(int j = 0; j < cols.length; j++) {
					if(i == 0) {
						//cols[j] = sheet.getCell(j, i).getContents();
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
					} 
					else {
						row[j] = sheet.getCell(j, i-1).getContents().replace("-", "");
						rows[i][j] = row[j].replace(" ", "");
					}
				}
			}
			
			lp = new ListParam(cols);
			//ErrorLogger.debug("▶▶▶▶▶▶▶[rows.length]▶▶▶▶▶▶▶"+ rows.length);
			for(int i = 1; i < rows.length; i++) {
					lp.addRow((Object[])rows[i]);
 			}
			//domain.addValue("rowCnt", rowCount);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			ErrorLogger.debug("▶▶▶▶▶▶▶6[BiffException]▶▶▶▶▶▶▶"+e);
			e.printStackTrace();
		} catch (IOException e) {
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
