package sens.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.transfer.ParamException;
import com.locus.jedi.waf.action.WebActionException;

public class ExcelControl {
	
	private final static String SEPARATOR 	= File.separator;
	public static final String DEFAULT_DIR 	= ".."+ SEPARATOR +".."+ SEPARATOR +"webapps"+ SEPARATOR +"excel"+ SEPARATOR;
//	public static final String DEFAULT_DIR 	= "/project/is_tm/webapps/excel/";
	
	public ExcelControl() {
		
	}
	
	
	public static Param readExcel(String dir) throws Exception{

		return readSheet(DEFAULT_DIR+dir, 0);
	}
	
	
	public static int writeExcel(Param dataset, String dir) throws IOException, RowsExceededException, WriteException, ParamException {
		
		return writeSheet(DEFAULT_DIR+dir, dataset);
	}
	
	
	public static void expireExcel(int minutes) {
		
		File f 				= new File(DEFAULT_DIR);
		File[] ef 			= f.listFiles();
		
		ArrayList<String> r = new ArrayList<String>();

		for(File file : ef) {

			try{ 
			
				if(Long.parseLong(file.getName().split("_")[0]) < System.currentTimeMillis() - minutes * 1000 * 60) {
	
					r.add(file.getName());
					file.delete();
				}
				
			} catch(Exception e) {
				
				System.out.println("������ �ㅽ�: "+file.getName());
			}
		}
		
		System.out.println("___________________________________________________________________________");
		System.out.println(minutes + "minutes 珥�낵�������� ��굅寃곌낵.");
		System.out.println("��� 嫄댁�: "+ r.size());
		
		for(String s : r) {
			
			System.out.println(s);
		}
		
		System.out.println("___________________________________________________________________________");
	}
	
	
	private static int writeSheet(String dir, Param dataset) throws IOException, RowsExceededException, WriteException, ParamException {
	// �곗��곗�怨�寃쎈�瑜����諛�� �대� �곗��곗���寃쎈���excel���濡���
		
		File f 					= new File(dir);
		
		WritableWorkbook ww 	= null;
		ww 						= Workbook.createWorkbook(f);
		ww.createSheet("OUTPUT", 0);
		
		WritableSheet ws 		= null;
		ws 						= ww.getSheet(0);
		
		ListParam lp 			= null;
		lp 						= (ListParam) dataset.getValue(dataset.keys()[0]);
		// 臾댁“嫄�0踰��몃������留��쎌�!

		String cols[] 			= null;
		cols 					= lp.getColumns();
		
		for(int i = 0; i < cols.length; i++) {
			
			ws.addCell(new Label(i, 0, cols[i]));
		}

		for(int i = 0; i < lp.rowSize(); i++) {
		// ��㉧吏���!0)瑜���
		
			for(int j = 0; j < cols.length; j++) {
				
				ws.addCell(new Label(j, ++i, String.valueOf(lp.getValue(--i, j))));
			}
		}
		
		ww.write();
		ww.close();
		
		System.out.println("___________________________________________________________________________");
		System.out.println(f.getAbsolutePath()+" 寃쎈����������");
		System.out.println("���紐�: "+ f.getName());
		System.out.println("___________________________________________________________________________");
		
		return 1;
	}
	
	private static Param readSheet(String dir, int ...sheetIdx) throws BiffException, IOException, Exception {
	// ��� �몃��ㅺ���諛�� ���(���)瑜��쎄� 寃곌낵瑜�諛���댁�.

		System.out.println(dir);
		
		Workbook w 			= null;
		w 					= Workbook.getWorkbook(new File(dir));
		
		
		Sheet sheet 		= null;

		String[] col 		= null;
		
		Param r 			= null;
		Param p 			= null;
		
		ListParam lp 		= null;
		
		int ROWS 			= 0;
		int COLS 			= 0;
		
		r 					= new Param();
		p 					= new Param();
		
		for(int i = 0; i < sheetIdx.length; i++) {
			
			if(w.getSheets().length <= sheetIdx[i]) {
				
				errorLog("���瑜�李얠������.");
			}
			
			sheet 	= w.getSheet(sheetIdx[i]); 
			
			ROWS 	= sheet.getRows();
			COLS 	= sheet.getColumns();
			
			col 	= new String[COLS];
			
			if(COLS == 0) {
				
				errorLog("而щ� ��� ��� 李얠������.");
			}
			
			for(int j = 0; j < COLS; j++) {
			// 而щ���낫瑜��쎌�(0踰�Row)
				
				col[j] = sheet.getRow(0)[j].getContents();
			}
			
			lp 	= new ListParam(col);
			// 而щ���낫瑜�媛��怨�ListParam��留��.
			
			for(int j = 1; j < ROWS; j++) {
				
				for(int k = 0; k < COLS; k++) {
					
					p.addValue(col[k], sheet.getRow(j)[k].getContents());
					// ������ �쎌� ���.
				}
				
				lp.addParam(p);
				// ListParam�������.
				p.clear();
			}
			
			r.addValue("EXCEL", lp);
			// Param��Object���濡�ListParam���댁�.
		}
		
		return r;
	}
	
	private static void errorLog(String message) throws Exception {
		
		System.out.println("������������������������������������������������������������������������������������������������������������������������");
		System.out.println("		"+message);
		System.out.println("������������������������������������������������������������������������������������������������������������������������");
		throw new Exception();
	}
}

