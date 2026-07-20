package sens.util;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.controller.JediResponse;

public class ReadExcelFile {
	
	private static Workbook w = null;
	// ���遺����) �댁� 媛�껜
	
	public static void responseExcelData(JediResponse res, String fileDirectory) throws BiffException, IOException, Exception {
	// response媛�껜瑜��몄�濡�諛�� �⑥��ㅽ����대��댁��몄륫��Param�곗���Excel寃곌낵)瑜����以�
		
		w 					= Workbook.getWorkbook(new File(fileDirectory));
		// ���遺����.
		
		Param p 			= null;
		String[] keys 		= null;
		Sheet[] sheet 		= null;
		int[] sheetIdx 		= null;
		
		sheet 				= w.getSheets();
		sheetIdx 			= new int[sheet.length];
		
		for(int i = 0; i < sheetIdx.length; i++) {
		// ��껜 ��� 媛��瑜�援ы�.
			
			sheetIdx[i] = i;
		}
		
		p 				= readSheet(sheetIdx);
		// Param��Excel�쎌� 寃곌낵�ㅼ� �댁�.
		keys 			= p.keys();
		// �ㅺ����댁�.
		
		for(String k : keys) {
		// Param諛곗�(湲몄� = �ㅺ�湲몄�)���대��댁��명������以� 
			
			res.param.addValue(k, p.getValue(k));
		}
	}
	
	public static void responseExcelData(JediResponse res, String fileDirectory, int ...sheetIdx) throws BiffException, IOException, Exception {
	// �몄�媛��濡���� Index瑜�諛�� �뱀� ���(�몃���寃곌낵瑜��살� 蹂������.
		
		w = Workbook.getWorkbook(new File(fileDirectory));

		Param p 		= null;
		String[] keys 	= null;
		
		p 				= readSheet(sheetIdx);
		keys 			= p.keys();
		
		for(String k : keys) {
			
			res.param.addValue(k, p.getValue(k));
		}
	}
	
	private static Param readSheet(int ...sheetIdx) throws BiffException, IOException, Exception {
	// ��� �몃��ㅺ���諛�� ���(���)瑜��쎄� 寃곌낵瑜�諛���댁�.

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
			
			r.addValue(sheet.getName(), lp);
			// Param��Object���濡�ListParam���댁�.
		}
		
		w = null;
		return r;
	}
	
	private static void errorLog(String message) throws Exception {
		
		System.out.println("������������������������������������������������������������������������������������������������������������������������");
		System.out.println("		"+message);
		System.out.println("������������������������������������������������������������������������������������������������������������������������");
		throw new Exception();
	}
}

