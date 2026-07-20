package xwing.importExcel;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.*;
	
public class ImportControl {
	private InputStream is;
	private String filename;
	private FileItem file;
	private Integer colIdx[] = null;
	
	private static List cols;
	private static Boolean useCol;
	private static int	sheetIdx;
	// xls, xlsx 둘 중 어떻게 할 지 결정 
	
	public void init(String filename, InputStream is, FileItem file){
		this.filename = filename;
		this.is = is;
		this.file = file;
		
//		cols = Arrays.asList(new String[]{"VDN", "VDN명", "날짜", "시간", "서비스레벨    만족도(%)"});
//		System.out.println(">>SDFSDF "+this.cols.toString());
//		System.out.println(":::"+this.cols.toString().replace(", ", "\", \"").replace("[", "[\"").replace("]", "\"]"));
	}
	
	public void setConfig(String confs[]){
		this.cols = Arrays.asList(confs[0].split(","));
		this.useCol = Boolean.valueOf(confs[1]);
		if( confs.length > 2 ) this.sheetIdx = Integer.parseInt(confs[2]);
	}
	
	public String parseRequest(){
		String rows = "";
		try{
			if( this.filename.indexOf(".xlsx") > -1 ){
				rows = parseXlxs();
			}else if( this.filename.indexOf(".xls") > -1 ){
				// 기존 그대로 
				rows = parseXls();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return "{cols:"+this.cols.toString().replace(", ", "\", \"").replace("[", "[\"").replace("]", "\"]")
			+", rows:"+rows+"}";
	}
	
	private String parseJSPExcel(String file){
		file = file.substring(file.indexOf("<table"), file.indexOf("</table>")+8);
		System.out.println(file);
		
		int first = 0;
		int sec = 0;
		if( !this.useCol ){
			// make Col.
			cols = new ArrayList<String>();
			int start = 0;
			int end = 0;
			while(file.indexOf("<th", start) != -1){
				first = file.indexOf("<th", start);
				sec = file.indexOf("</th", end);
				
				 start = first + 2;  // move start up for next iteration
				 end = sec + 2;
//				 System.out.println(first+":"+sec);
//				 System.out.println(file.substring(first+4,sec)+"::");
				 cols.add(file.substring(first+4,sec));
			}
			System.out.println(cols);
		}
		
		// make Row.
		int rowStart = 0;
		int rowEnd = 0;
		int colStart = 0;
		int colEnd = 0;
		List<String> axis2 = new ArrayList<String>();
		String result = "[";
		while( file.indexOf("<tr",rowStart) != -1){
			first = file.indexOf("<tr",rowStart);
			sec = file.indexOf("</tr",rowEnd);
			
			rowStart = first + 2;  // move start up for next iteration
			rowEnd = sec + 2;
			//System.out.println(file.substring(first+4,sec)+"::");
			
			colStart = rowStart;
			colEnd = rowStart;
			axis2 = new ArrayList<String>();
			while(true){
				int first2 = file.indexOf("<td",colStart);
				int sec2 = file.indexOf("</td",colEnd);
				
				if(rowEnd <= first2 || first2 == -1) break;
				colStart = first2 +2;
				colEnd = sec2 + 2;
				axis2.add("\""+file.substring(first2+4,sec2)+"\"");
			}
			result +=axis2.toString();
			if( file.indexOf("<tr",rowStart) != -1 ) result += ",";
		}
		return result+"]";
	}
	
	private String parseXls() {
		try{
			String result = "";
			POIFSFileSystem fs = new POIFSFileSystem(  is );
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
		
			Iterator rows = sheet.rowIterator();
			if( rows.hasNext()) makeColIndexArray("xls",rows.next());
			
			String axis1[] = new String[sheet.getLastRowNum() - sheet.getFirstRowNum()];
			List<String> axis2 = new ArrayList<String>();
			int cnt= 0;
		    while( rows.hasNext() ) {  
		    	HSSFRow row = (HSSFRow) rows.next();
		    	
		    	axis2 = new ArrayList<String>();
		        for( int i=0; i < colIdx.length; i++){
		        	
		        	//System.out.println(row.getCell(colIdx[i]).getCellType());
		        	if( row.getCell(colIdx[i]) == null){
		        		axis2.add("\"\"");
		        	}else if( row.getCell(colIdx[i]).getCellType() == 0 ){
		        		axis2.add("\""+(int)row.getCell(colIdx[i]).getNumericCellValue()+"\"");
		        	}else if( row.getCell(colIdx[i]).getCellType() == 1){
		        		axis2.add("\""+row.getCell(colIdx[i])+"\"");
		        	}
		        }
//		        System.out.println(sheet.getFirstRowNum()+"::"+sheet.getLastRowNum());
		        axis1[cnt] = axis2.toString();
				 cnt++;
		    } 
		    return Arrays.toString(axis1);
		}catch(IOException e){
			try {
				return parseJSPExcel(file.getString("utf-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "[]";
		}
		
	}
	
	private String parseXlxs() throws IOException{
		XSSFWorkbook  xwb = new XSSFWorkbook(  is );
    	XSSFSheet sheet = xwb.getSheetAt(0);

		Iterator rows = sheet.rowIterator();
		if( rows.hasNext()) makeColIndexArray("xlsx",rows.next());
//		List a[];
		String axis1[] = new String[sheet.getLastRowNum() - sheet.getFirstRowNum()];
		List<String> axis2 = new ArrayList<String>();
		int cnt= 0;
		while (rows.hasNext())
		{
			XSSFRow row=(XSSFRow) rows.next();
			int cellType = -1;
			axis2 = new ArrayList<String>();
			 for( int i=0; i < colIdx.length; i++){
	        	//System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getStringCellValue()+" , ");
				 if( row.getCell(colIdx[i]) == null){
		        		axis2.add("\"\"");
		        		continue;
				 }
				 cellType = row.getCell(colIdx[i]).getCellType();
				 switch(cellType){
				 	case 0 : // number
////				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getNumericCellValue()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getNumericCellValue()+"\"";
				 		axis2.add("\""+row.getCell(colIdx[i]).getNumericCellValue()+"\"");
					 break;
				 	case 1 : // string
//				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getStringCellValue()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getStringCellValue()+"\"";
				 		//axis2.add("\""+row.getCell(colIdx[i]).getNumericCellValue()+"\"");
				 		axis2.add( "\""+row.getCell(colIdx[i]).getStringCellValue()+"\"");
				 		break;
				 	case 2 : // folmula
////				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getCellFormula()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getCellFormula()+"\"";
				 		axis2.add("\""+row.getCell(colIdx[i]).getCellFormula()+"\"");
				 		break;
				 	case 3 : // blank
////				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getDateCellValue()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getDateCellValue()+"\"";
				 		axis2.add("\""+row.getCell(colIdx[i]).getDateCellValue()+"\"");
				 		break;
				 	case 4 : // boolean
////				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getBooleanCellValue()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getBooleanCellValue()+"\"";
				 		axis2.add("\""+row.getCell(colIdx[i]).getBooleanCellValue()+"\"");
				 		break;
				 	case 5 : // error
////				 		System.out.print(colIdx[i]+":"+row.getCell(colIdx[i]).getErrorCellValue()+" , ");
//				 		result += "\""+row.getCell(colIdx[i]).getErrorCellValue()+"\"";
				 		axis2.add("\""+row.getCell(colIdx[i]).getErrorCellValue()+"\"");
				 		break;
				 }
	        }
			 //System.out.println(axis2.toString());
//			 System.out.println(cnt+":"+sheet.getLastRowNum()+":"+row.getRowNum());
			 axis1[cnt] = axis2.toString();
			 cnt++;
		}
//		System.out.println(Arrays.toString(axis1));
		return Arrays.toString(axis1);
	}
	
	private void makeColIndexArray( String type, Object row){
		Iterator cells;
		int cnt = 0, i=0;
		colIdx = new Integer[this.cols.size()];
		if( !this.useCol ) this.cols = new ArrayList<String>();
		if( "xls".equals(type) ){
			HSSFCell cell;
			HSSFRow cols = (HSSFRow) row;
			cells = cols.cellIterator();
			if( !this.useCol ) colIdx = new Integer[cols.getLastCellNum()];
			while (cells.hasNext())
			{
				cell=(HSSFCell) cells.next();
				if( this.useCol ){
					i = this.cols.indexOf(cell.getStringCellValue());
					if( i != -1 ) colIdx[i] = cnt;
				}else{
					colIdx[cnt] = cnt;
					this.cols.add(cell.getStringCellValue());
				}
				
				cnt++;
			}
		}else{
			XSSFCell cell;
			XSSFRow cols = (XSSFRow) row;
			cells = cols.cellIterator();
			
			if( !this.useCol ) colIdx = new Integer[cols.getLastCellNum()];
			while (cells.hasNext())
			{
				cell=(XSSFCell) cells.next();
				if( this.useCol ){
					i = this.cols.indexOf(cell.getStringCellValue());
					if( i != -1 ) colIdx[i] = cnt;
				}else{
					colIdx[cnt] = cnt;
					this.cols.add(cell.getStringCellValue());
				}
				
				cnt++;
			}
		}
	}
}
