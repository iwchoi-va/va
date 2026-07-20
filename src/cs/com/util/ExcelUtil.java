package cs.com.util;

import com.locus.jedi.transfer.Param;
import com.locus.jedi.transfer.ListParam;

public class ExcelUtil {
	public static String makeTable(ListParam listParam) {
		StringBuffer sb  = new StringBuffer();
		
		String[] columns = listParam.getColumns();
		
		sb.append("<table border='1'>");
		// Ÿ��Ʋ ����
		sb.append("<tr>");
		for (int i = 0; i < columns.length; i++) {
			String sColumn = columns[i].replace("NO$", "");
			sb.append("<td align='center' style='background-color:#666666;color:white;font-weight:bold'>" + sColumn + "</td>");
		}
		sb.append("</tr>");
		// Ÿ��Ʋ ����
		
		// ���� ����
		for (int i = 0; i < listParam.rowSize(); i++) {
			String sRowColor = "#FFFFFF";
			
			if ((i + 1) % 2 == 0) {
				sRowColor = "#DAE0E9";
			}
			
			sb.append("<tr>");
			
			Param param = listParam.getParam(i);
			
			for (int j = 0; j < columns.length; j++) {
				sb.append("<td style='text-align:left;text-valign:middle;background-color:" + sRowColor + "'>");
				sb.append(convertHtmlStr(param.getString(columns[j], "")));
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		// ���� ����
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	// �ְ��������� ����
	public static String makeWeeklyExcel(ListParam listParam) {
		StringBuffer sb  = new StringBuffer();
		
		String[] columns = listParam.getColumns();
		
		sb.append("<table border='1'>");
		// Ÿ��Ʋ ����
		sb.append("<tr>");
		for (int i = 0; i < columns.length; i++) {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+columns[i].indexOf("����")+"_"+columns[i]);
			if(columns[i].indexOf("����") >= 0){		// �ǽû��� or ��������
				sb.append("<td align='center' width='500' style='background-color:#666666;color:white;font-weight:bold'>" + columns[i] + "</td>");
			}else{
				sb.append("<td align='center' style='background-color:#666666;color:white;font-weight:bold'>" + columns[i] + "</td>");
			}
		}
		sb.append("</tr>");
		// Ÿ��Ʋ ����
		
		// ���� ����
		for (int i = 0; i < listParam.rowSize(); i++) {
			String sRowColor = "#FFFFFF";
			
			if ((i + 1) % 2 == 0) {
				sRowColor = "#DAE0E9";
			}
			
			sb.append("<tr>");
			
			Param param = listParam.getParam(i);
			
			if("X".equals(param.getString(columns[3], "")))	 sRowColor = "#FFFF96";	// �ְ����� �ۼ� ������ ��������� ǥ��	
			
			for (int j = 0; j < columns.length; j++) {				
				sb.append("<td style='text-align:left;text-valign:middle;background-color:" + sRowColor + "'>");
				sb.append(convertHtmlStr(param.getString(columns[j], "")));
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		// ���� ����
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	private static String convertHtmlStr(String str) {
		if (str == null) {
			str = "";
		} else {
			str = str.trim();
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll(" ", "&nbsp;");
			str = str.replaceAll("\"", "&quot;");
			str = str.replaceAll("'", "&#39;");
			str = str.replaceAll("\r", "<br>");
		}
		
		return str;
	}
}