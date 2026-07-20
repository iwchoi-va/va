package cs.com.util;

import java.math.BigDecimal;
import java.text.*;
import java.util.*;

public class StringUtil {
	public static double getDouble(Object v) {
		double result = 0;

		if (v != null) {
			try {
				if (v instanceof Number) {
					result = ((Number) v).doubleValue();
				} else {
					result = new BigDecimal(v.toString()).doubleValue();
				}
			} catch (NumberFormatException e) {
			}
		}

		return result;
	}

	/**
	 * 기능: 입력한 값을 빈 문자열로 반환한다. <BR>
	 * 
	 * @param String
	 *            null check할 문자열 <BR>
	 * @param String
	 *            null인경우 return할 문자열 <BR>
	 * @return String 결과 문자열
	 */
	public static String null2String(String inputString, String outputString) {
		String returnVal = "";

		outputString = outputString == null ? "" : outputString;
		returnVal = inputString == null ? outputString : inputString;

		return returnVal.trim();
	}

	/**
	 * 기능: 입력한 값을 빈 문자열로 반환한다. <BR>
	 * 
	 * @param String
	 *            text <BR>
	 * @return String 결과 문자열
	 */
	public static String null2String(String inputString) {
		String returnVal = "";

		returnVal = inputString == null ? "" : inputString;

		return returnVal;
	}

	/**
	 * 기능: 입력한 값을 "0"으로 반환한다. <BR>
	 * 
	 * @param String
	 *            text <BR>
	 * @return String 결과 문자열
	 */
	public static String null2Zero(String inputString) {
		String returnVal = "0";

		if (inputString != null && !"".equals(inputString))
			returnVal = inputString;

		return returnVal;
	}

	public static short parseShort(String s) {
		short result = (short) 0;
		try {
			if (s != null && !s.trim().equals("")) {
				result = Short.parseShort(s.trim());
			}
		} catch (NumberFormatException ne) {
		}
		return result;
	}

	public static int parseInt(String s) {
		int result = 0;
		try {
			if (s != null && !s.trim().equals("")) {
				result = Integer.parseInt(s.trim());
			}
		} catch (NumberFormatException ne) {
		}
		return result;
	}

	public static double parseDouble(String s) {
		double result = 0;
		try {
			if (s != null && !s.trim().equals("")) {
				result = Double.parseDouble(s.trim());
			}
		} catch (NumberFormatException ne) {
		}
		return result;
	}

	/**
	 * 기능: 문자열 replace 함수 <BR>
	 * 
	 * @param String
	 *            text <BR>
	 * @param int start 시작 index <BR>
	 * @param String
	 *            src 바뀔문자열 <BR>
	 * @param String
	 *            dest 새문자열 <BR>
	 * @return String 결과 문자열
	 */
	public static String replaceAll(String text, int start, String src,
			String dest) {
		if (text == null)
			return "";
		if (src == null || dest == null)
			return text;

		int textlen = text.length();
		int srclen = src.length();
		int diff = dest.length() - srclen;
		int d = 0;

		StringBuffer t = new StringBuffer(text);

		while (start < textlen) {
			start = text.indexOf(src, start);
			if (start < 0)
				break;
			t.replace(start + d, start + d + srclen, dest);
			start += srclen;
			d += diff;
		}
		return t.toString();
	}

	/**
	 * 기능 : 받은 값을 #,###,###,##0.#0 형식으로 바꿈 <br>
	 * 
	 * @param str
	 *            core로 부터 받은 값
	 * @param cnt
	 *            소수점 자릿수
	 * @return #,###,###,###
	 */
	public static String fFormat(String str, int cnt) {
		if (str == null)
			return "";
		if (str.length() == 0)
			return "";

		// String s_format = "#,###,###,##0.";
		StringBuffer sb_format = new StringBuffer("#,###,###,##0.");

		if (cnt <= 0) {
			sb_format.deleteCharAt(13);
		} else {
			for (int i = 0; i < cnt; i++) {
				sb_format.append("0");
			}
		}

		DecimalFormat df = new DecimalFormat(sb_format.toString());
		String retstr = null;

		try {
			retstr = df.format(Long.parseLong(str));
			return retstr;
		} catch (NumberFormatException nfe) {
			try {
				retstr = df.format(Double.valueOf(str).doubleValue());
				return retstr;
			} catch (Exception ee) {
				return "0";
			}
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 기능 : 받은 값을 #,###,###,##0 형식으로 바꿈 <br>
	 * 
	 * @param str
	 *            core로 부터 받은 값
	 * @return #,###,###,###
	 */
	public static String dFormat(String str) {
		if (str == null)
			return "";
		if (str.length() == 0)
			return "";

		NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
		Number num = null;
		String ret = "";

		try {
			num = nf.parse(str);
		} catch (java.text.ParseException e) {
		}

		ret = dFormat(num.longValue());
		return ret;
	}

	/**
	 * 기능 : 국가코드 KOREA에 기준하여 숫자를 (한국통화기준으로)변환하는 함수들입니다.
	 * 
	 * @param num
	 *            int
	 * @return 새로운 string
	 */
	public static String dFormat(int num) {
		NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
		String tempStr = nf.format(num);
		return tempStr;
	}

	/**
	 * 기능 : 국가코드 KOREA에 기준하여 숫자를 (한국통화기준으로)변환하는 함수들입니다.
	 * 
	 * @param num
	 *            long
	 * @return 새로운 string
	 */
	public static String dFormat(long num) {
		NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
		String tempStr = nf.format(num);
		return tempStr;
	}

	/**
	 * 기능 : 국가코드 KOREA에 기준하여 숫자를 (한국통화기준으로)변환하는 함수들입니다.
	 * 
	 * @param num
	 *            double
	 * @return 새로운 string
	 */
	public static String dFormat(double num) {
		NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
		String tempStr = nf.format(num);
		return tempStr;
	}

	/**
	 * 긴 내용의 텍스트를 입력한 길이만큼만 자른후 나머지는 "..." 처리를 한다.
	 * 
	 * @author Jaewhan Kim (runjava@hanmir.com)
	 * @since version 1.0
	 * @param length
	 *            화면에 보여줄 문자열의 수
	 * @param str
	 *            원본 텍스트
	 * @return String 가공된 원본 텍스트
	 */
	public static String substring(String str, int length) {
		String r1 = "";
		String r2 = "";

		if (str == null) {
			return "";
		} else if (str.trim().length() <= length) {
			return str;
		} else {
			byte[] bytes = str.trim().getBytes();

			if (length * 2 > bytes.length - 3) {
				return new String(bytes);
			} else {
				r1 = new String(bytes, 0, length * 2);
				r2 = new String(bytes, 0, length * 2 - 1);

				if ("".equals(r1))
					return r2 + "...";
				else
					return r1 + "...";
			}
		}
	}

	public static String getSplitDate(String strDate) {
		if (strDate == null)
			return "";
		else if (strDate.length() == 8) {
			return strDate.substring(0, 4) + "-" + strDate.substring(4, 6)
					+ "-" + strDate.substring(6);
		}
		return strDate;
	}

	public static String getSplitTime(String strTime) {
		if (strTime == null)
			return "";
		else if (strTime.length() == 6) {
			return strTime.substring(0, 2) + ":" + strTime.substring(2, 4)
					+ ":" + strTime.substring(4);
		} else if (strTime.length() == 4) {
			return strTime.substring(0, 2) + ":" + strTime.substring(2);
		}
		return strTime;
	}

	public static String getSocialid2Birthday(String socialid) {
		String result = "";
		if (socialid == null)
			return "";

		if (socialid.length() >= 13) {
			result = socialid.substring(0, 6);
			if (socialid.charAt(6) == '1' || socialid.charAt(6) == '2')
				result = "19" + result;
			else
				result = "20" + result;
			return getSplitDate(result);
		}
		return result;
	}

	/**
	 * 최지웅, 14
	 */
	public static String pad(String str, int width) {
		return pad(str, width, " ");
	}

	/**
	 * 입력받은 자리수 만큼 특정 입력한 문자로 padding 한다.
	 * 
	 * @param str
	 *            padding할 문자열
	 * @param width
	 *            padding할 문자열의 총 길이
	 * @param width
	 *            padding할 문자열의 총 길이
	 * @return 입력한 문자로 padding된 문자열
	 */
	public static String pad(String str, int width, String specChar) {
		if (str == null)
			str = "";

		StringBuffer buf = new StringBuffer();
		int space = width - str.length();
		while (space-- > 0) {
			buf.append(specChar);
		}
		buf.append(str);
		return buf.toString();
	}

	public static String pad(int input, int width, String specChar) {
		return pad(input + "", width, specChar);
	}

	/**
	 * 13자리의 주민등록번호를 앞의 6자리 뒤쪽의 7자리로 분리하여 중간부분에 -(hyphen)표시를 붙이도록 한다.
	 * 
	 * @param socialId
	 * @return -에 의하여 분리된 문자열값
	 */
	public static String getSocialId(String socialId) {
		if (socialId == null)
			return "";
		if (socialId.length() < 6)
			return socialId;
		if (socialId.charAt(6) == '-')
			return socialId;

		return new StringBuffer(socialId).insert(6, '-').toString();
	}

	/**
	 * 13자리의 주민등록번호를 앞의 6자리 또는 뒤의 7자리를 잘라내어 반환한다.
	 * 
	 * @param socialId
	 * @param isFirst
	 *            앞의 6자리 - true, 뒤쪽 7자리 - false
	 * @return 주민등록번호 앞6자리
	 */
	public static String getSocialId(String socialId, boolean isFirst) {
		if (isFirst) {
			return socialId.substring(0, 6);
		} else {
			return socialId.substring(6);
		}
	}

	/**
	 * S9(3)V9(4) 형식의 숫자를 포멧에 맞춰 반환한다.
	 */
	public static String getRateValue(double value) {
		DecimalFormat dFormat = new DecimalFormat("##0.0000");
		return dFormat.format(value);
	}

	public static String replace(String src, String oldstr, String newstr) {
		if (src == null)
			return null;
		String dest = "";
		int len = oldstr.length();
		int srclen = src.length();
		int pos = 0;
		int oldpos = 0;

		while ((pos = src.indexOf(oldstr, oldpos)) >= 0) {
			dest += src.substring(oldpos, pos) + newstr;
			oldpos = pos + len;
		}
		dest += src.substring(oldpos, srclen);

		return dest;
	}

	/**
	 * 문자열을 delimiter를 기준으로 분리하여 분리된 각 substring을 Vector로 리턴 [ jdk1.4
	 * java.lang.String.split(String deli)기능 ]
	 * 
	 * @param strString
	 *            - 처리대상 문자열
	 * @param strDelimeter
	 *            - Delimiter
	 * @return 분리된 substring을 담은 Vector
	 */
	public static Vector getSplit(String srcString, String strDelimeter) {
		Vector result = new Vector();
		int cnt = 0;
		int lastIndex = 0;
		try {
			lastIndex = srcString.indexOf(strDelimeter);

			if (lastIndex == -1) {
				result.add(0, srcString);
			} else {
				while ((srcString.indexOf(strDelimeter) > -1)) {
					lastIndex = srcString.indexOf(strDelimeter);
					result.add(cnt, srcString.substring(0, lastIndex));
					srcString = srcString.substring(
							lastIndex + strDelimeter.length(),
							srcString.length());
					cnt++;
				}
				result.add(cnt, srcString);
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	/**
	 * 입력받은 문자열을 00:00으로 변환한다.
	 * 
	 * @param str
	 *            convert할 문자열
	 * @return long convert된 결과
	 */
	public static String seconds2time2(long seconds) {
		String result = "0:00";
		int hour = 0;
		int min = 0;
		int sec = 0;

		if (seconds == 0)
			return result;
		hour = (int) (seconds / 3600);
		min = (int) ((seconds % 3600) / 60);
		sec = (int) ((seconds % 3600) % 60);
		if (hour > 0)
			result = hour + ":" + pad(min, 2, "0") + ":" + pad(sec, 2, "0");
		else
			result = "0:" + pad(min, 2, "0") + ":" + pad(sec, 2, "0");

		return result;
	}

	/**
	 * 입력받은 자리수 만큼 특정 입력한 문자로 right padding 한다.
	 * 
	 * @param str
	 *            padding할 문자열
	 * @param width
	 *            padding할 문자열의 총 길이
	 * @param width
	 *            padding할 문자열의 총 길이
	 * @return 입력한 문자로 padding된 문자열
	 */
	public static String Rpad(String str, int width, String specChar) {
		if (str == null)
			str = "";

		StringBuffer buf = new StringBuffer(width);
		int space = 0;

		buf.append(str);
		while (buf.length() < width) {
			buf.append(specChar);
		}
		return buf.toString();
	}

	public static String Rpad(int str, int width, String specChar) {

		return Rpad(str + "", width, specChar);
	}

	public static String toUpper(String key) {
		if (key == null)
			return null;
		return key.toUpperCase();
	}
}
