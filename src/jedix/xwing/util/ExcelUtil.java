/* **************************************************************
 * Program Name         : ExcelUtil
 * Function description : Excel을 읽어서 One Line 스트링(구분자포함)으로 변환
 * Programmer Name      : 신정은
 * Creation Date        : 2012.06.18
 * *************************************************************/

package jedix.xwing.util;

import jxl.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import com.locus.jedi.log.*;
import com.locus.jedi.transfer.ListParam;

public class ExcelUtil {

    public ListParam getExcelData( ListParam listParam, ListParam excel_info ) 
    {

		ListParam excel_list = null;

		try{

			if( listParam==null || listParam.rowSize() == 0 ){
				return excel_list;
			}

			String s_dir          = (String)listParam.getValue(0, "file_path"   );
			String s_file_name    = (String)listParam.getValue(0, "file_name"   );
			Vector v_column_name  = StringUtil.getSplit( (String)listParam.getValue(0, "column_name" ) , "," );
			int    i_read_row_num = StringUtil.parseInt( (String)listParam.getValue(0, "read_row_num") );

			if( v_column_name == null || v_column_name.size() == 0 ){

				excel_list = new ListParam( new String[]{ "err_msg" } );
				excel_list.addRow ( new Object[]{ "컬럼 정보가 없습니다.\n'ds_uploadfile' Dataset 'column_name'정보를 입력해주세요." } );

				return excel_list;
			}

			String[] s_column_name = new String[v_column_name.size()];
			int      i_column_cnt  = v_column_name.size();

			for( int i=0; i<i_column_cnt; i++ ){
				s_column_name[i] = (String)v_column_name.get(i);				
			}

			if( !s_dir.endsWith("/") ) s_dir += "/";

			// 파일명, 경로, 읽을컬럼수, 컬럼 index[0부터시작], row의 구분자, 삭제유무
			ArrayList arr_excel_data = access_excelFile( s_file_name, s_dir, i_column_cnt, 0, "Y");

			// 필수 파라메터가 없는 경우는 바로 Return...
			if( i_column_cnt == 0 || i_read_row_num == 0 ){

				excel_list = new ListParam( new String[]{ "err_msg" } );
				excel_list.addRow ( new Object[]{ "Excel Upload에 필요한 파라메터가 없습니다.\ncolumn_cnt=" + i_column_cnt + "    \nread_row_num=" + i_read_row_num } );

				return excel_list;
			}

			if( arr_excel_data == null || arr_excel_data.size() == 0 ){

				excel_list = new ListParam( new String[]{ "err_msg" } );
				excel_list.addRow ( new Object[]{ "Upload한 Excel 파일을 다시 확인하거나, Upload한 Excel 파일을 닫은 후 다시 Upload해 주세요." } );

				return excel_list;
			}

			String s_data[]      = null;

			if( arr_excel_data != null && arr_excel_data.size() > 0 ){

				excel_list = new ListParam( s_column_name );

				for( int i=i_read_row_num-1; i<arr_excel_data.size(); i++ ){					
					s_data = (String[])arr_excel_data.get(i);

					if( s_data != null )
						excel_list.addRow( (Object[])s_data );
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			ErrorLogger.error("Exception : " + e.getMessage(), e);
		}

		return excel_list;
	}

    public ArrayList access_excelFile( String s_file_name, String s_full_path, int count, int init_num, String del_yn ) {

		ArrayList arr_data = null;
		File   s_dir  = null;
		File   s_file = null;

		try {

			//임시로 저장할 엑셀 파일의 폴더를 생성한다.
			s_dir  = new File( s_full_path );
			s_file = new File( s_full_path + s_file_name );

			//폴더 생성 여부
			if( !s_dir.exists() ){
				s_dir.mkdirs();
			}

			arr_data = read_excel( s_full_path, s_file_name, count, init_num );

			if( del_yn != null && del_yn.equals("Y") ){
				//임시파일삭제
				boolean result_b = s_file.delete();
				//임시폴더삭제
				result_b = s_dir.delete();
			}

		} catch(Exception e) {
			e.printStackTrace();

			s_file.delete();
			s_dir.delete();
		}
		return arr_data;
	}

    private ArrayList read_excel(String s_dir, String file_name, int count, int init_num ) throws Exception {

		ArrayList arr_excel_date = null;

		try {
			//엑셀 파일을 읽는다.
			Workbook readWorkbook = Workbook.getWorkbook(new File( s_dir + file_name ));

			//테스트 시트의 갯수를 가지고 온다.
			//int num_value = readWorkbook.getNumberOfSheets();

			//첫번째 시트를 읽는다.
			Sheet readSheet = readWorkbook.getSheet(0);

			//시트의 Column수를 반환
			int excel_Col = readSheet.getColumns();

			//시트의 ROW수를 반환
			int excel_Row = readSheet.getRows();
			int num = 0;
			String contents = "";
			//임시 데이터 저장
			String data = "";
			
			int i_empty_cnt = 0;
					  arr_excel_date = new ArrayList();
			String[]  arr_date = null;
			Cell cell[] = null;

			for (int i=0; i < excel_Row; i++)  {

				num = init_num;
				cell = readSheet.getRow(i);
				//contents = "";

				arr_date = new String[ count ];
				i_empty_cnt = 0;

				//컬럼을 갯수만큼 읽는다.
				for (int j=0; j < count; j++) {
					try {
						data = (cell[num++].getContents()).trim();

						arr_date[j] = data;
						
						if( data == null || "".equals( data ) ) i_empty_cnt++;

					} catch(Exception e) {
						arr_date[j] = "";
						i_empty_cnt++;
					}
				}
				
				// row에 데이터가 없을때는 skip... 
				if( count == i_empty_cnt ) continue;
				
				arr_excel_date.add ( arr_date);

			} // end of for

		} catch(Exception e) {
			e.printStackTrace();
		}
		return arr_excel_date; //result;
	}
}