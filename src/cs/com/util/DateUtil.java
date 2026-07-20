package cs.com.util;

import java.text.*;
import java.util.*;

public class DateUtil {
	/**
	 * 현재 날짜를 yyyy년 MM월 dd일 HH시 mm분의 형태로 값을 얻어낸다.
	 * 
	 * @return yyyy년 MM월 dd일 HH시 mm분의 형태로 바뀐 현재 시간값
	 */
	public static String getTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
		return sdf.format(d);
	}

	/**
	 * 현재 날짜를 입력받은 포맷의 형태로 변환하여 결과값을 리턴하도로 한다. 예) format : yyyyMMddHHmmss -->
	 * 20031130124130 로 결과값 반환
	 */
	public static String getTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 현재 날짜를 yyyy/mm/dd까지의 형태로 추출해낸다.
	 * 
	 * @return yyyy/mm/dd형태로 변경되된 문자열값
	 */
	public static String getCurrentDate() {
		return getCurrentDate("yyyyMMdd");
	}

	/**
	 * 현재 날짜를 yyyy/mm/dd까지의 형태로 추출해낸다.
	 * 
	 * @return yyyy/mm/dd형태로 변경되된 문자열값
	 */
	public static String getCurrentDate(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 현재 월을 년도와 함께 추출해낸다.
	 * 
	 * @return yyyy/mm형태로 변경되된 문자열값
	 */
	public static String getThisMonth() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return sdf.format(d);
	}

	/**
	 * 현재 년도를 추출해낸다.
	 * 
	 * @return yyyy형태로 변경되된 문자열값
	 */
	public static String getThisYear() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(d);
	}

	/**
	 * 현재 시간을 03:34 형태의 시/분으로 표시한다.
	 * 
	 * @return hh:mm형태로 변경되된 문자열값
	 */
	public static String getCurrentTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(d);
	}

	/**
	 * 오늘을 기준으로 입력받은 날자의 날짜를 알아낸다
	 * 
	 * @return yyyy/mm/dd형태로 변경되된 문자열값
	 */
	public static String getDayInterval(String format, int distance) {
		Calendar cal = getCalendar();
		cal.roll(Calendar.DATE, distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 입력받은 날자의 날짜를 기준으로 해당일의 과거나 미래일을 알아낸다
	 * 
	 * @return format형태로 변경되된 문자열값
	 */
	public static String getDayInterval(String dateString, String format,
			int distance) {
		Calendar cal = getCalendar(dateString);
		cal.roll(Calendar.DATE, distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 오늘을 기준으로 어제의 날짜를 알아낸다
	 * 
	 * @return yyyy/mm/dd형태로 변경되된 문자열값
	 */
	public static String getYesterday() {
		Calendar cal = getCalendar();
		cal.roll(Calendar.DATE, -1);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(d);
	}

	/**
	 * 현재 얻어낸 날짜의 마지막 달을 알아낸다.
	 * 
	 * @return yyyy/mm형태로 변경되된 문자열값
	 */
	public static String getLastMonth() {
		Calendar cal = getCalendar();
		cal.roll(Calendar.MONTH, -1);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
		return sdf.format(d);
	}

	/**
	 * 입력한 날짜의 마지막날 가져오기
	 * 
	 * @param ymd
	 *            - 입력한 년월일
	 * @return String 마지막 날을 YYYY-MM-DD형태로 반환
	 */
	public static String getLastDate(String ymd) {

		String[] dayAry = new String[] { "31", "28", "31", "30", "31", "30",
				"31", "31", "30", "31", "30", "31" };
		int yyyy = 0;
		String mm = "";
		String dd = "";

		if (ymd == null || "".equals(ymd))
			return "";

		if (ymd.trim().length() != 6 && ymd.trim().length() != 8)
			return "";

		yyyy = Integer.parseInt(ymd.substring(0, 4));
		mm = ymd.substring(4, 6);
		dd = ymd.trim().length() == 6 ? "01" : ymd.substring(6);

		if (!"02".equals(mm)) {
			dd = dayAry[Integer.parseInt(mm) - 1];
		} else {
			if (yyyy % 4 == 0 && yyyy % 100 != 0 || yyyy % 400 == 0)
				dd = "29";
			else
				dd = "28";
		}

		return yyyy + "-" + mm + "-" + dd;
	}

	/**
	 * 주어진 시작일과 종료일에 사이의 값들을 문자형태의 배열로 만들어낸다.
	 * 
	 * @param startDay
	 *            생성하고자 하는 값을 시작일
	 * @param endDay
	 *            생성하고자 하는 값의 종료일
	 * @return 시작일과 종료일사이의 날짜값들을 가진 문자열배열
	 */
	public static String[] getDates(String startDay, String endDay) {
		return getDates(startDay, endDay, null);
	}

	/**
	 * format은 startDay와 endDay의 포멧이다. 만약 yyyyMMdd의 포멧인 경우 String[]
	 * getDates(String startDay, String endDay) 메소드를 사용해도 된다.
	 * 
	 * @param startDay
	 *            생성하고자 하는 값을 시작일
	 * @param endDay
	 *            생성하고자 하는 값의 종료일
	 * @param format
	 *            시작일과 종료일의 날짜 포멧
	 * @return 시작일과 종료일사이의 날짜값들을 가진 문자열배열
	 */
	public static String[] getDates(String startDay, String endDay,
			String format) {
		Vector v = new Vector();
		v.addElement(startDay);
		Calendar cal = getCalendar();

		if (format == null)
			format = "yyyyMMdd";
		cal.setTime(string2Date(startDay, format));

		// System.out.println("cal : " + cal);

		String nextDay = date2String(cal.getTime(), format);
		// System.out.println("nextDay : " + nextDay);

		while (!nextDay.equals(endDay)) {
			cal.add(Calendar.DATE, 1);
			nextDay = date2String(cal.getTime(), format);
			v.addElement(nextDay);
		}

		String[] go = new String[v.size()];
		v.copyInto(go);
		return go;
	}

	/**
	 * GMT기준시간중의 한국표준시를 반환한다.
	 * 
	 * return GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar() {
		Calendar calendar = new GregorianCalendar(
				TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
		calendar.setTime(new Date());

		return calendar;
	}

	/**
	 * GMT기준시간중의 한국표준시를 반환한다.
	 * 
	 * return GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar(String dateString) {
		Calendar calendar = new GregorianCalendar(
				TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
		calendar.setTime(string2Date(dateString, "yyyyMMdd"));

		return calendar;
	}

	/**
	 * GMT기준시간중의 한국표준시를 반환한다.
	 * 
	 * return GMT+09:00형태의 대한민국표준시
	 */
	public static Calendar getCalendar(Date date) {
		Calendar calendar = new GregorianCalendar(
				TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
		calendar.setTime(date);

		return calendar;
	}

	/**
	 * 날짜형태의 데이터를 yyyy/mm/dd형태로 바꿔주는 메소드 Date -> String (2000/09/25)
	 * 
	 * @param d
	 *            설정된 날짜표시형태로 변경할 date객체
	 * @return yyyy/mm/dd형태로 변경되어진 문자열
	 */
	public static String date2String(java.util.Date d) {
		return date2String(d, "yyyyMMdd");
	}

	/**
	 * 날짜형태의 데이터를 사용자정의형태로 바꿔주는 메소드 Date -> String (2000/09/25)
	 * 
	 * @param d
	 *            설정된 날짜표시형태로 변경할 date객체
	 * @return yyyy/mm/dd형태로 변경되어진 문자열
	 */
	public static String date2String(java.util.Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	/**
	 * 문자열 데이터를 yyyy/mm/dd형태의 Date형태의 객체로 바꾸어준다 String -> Date (2000/09/25)
	 * 
	 * @param s
	 *            Date형태로 만들게 될 yyyy/mm/dd형태의 문자열
	 * @return yyyy/mm/dd형태로 변경되어진 Date객체
	 */
	public static java.util.Date string2Date(String s) {
		return string2Date(s, "yyyy/MM/dd");
	}

	/**
	 * 문자열 데이터를 사용자형태의 Date형태의 객체로 바꾸어준다 String -> Date (2000/09/25)
	 * 
	 * @param s
	 *            Date형태로 만들게 될 yyyy/mm/dd형태의 문자열
	 * @return yyyy/mm/dd형태로 변경되어진 Date객체
	 */
	public static java.util.Date string2Date(String s, String format) {
		java.util.Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			d = sdf.parse(s, new ParsePosition(0));

			// System.out.println("string2Date : " + d);

		} catch (Exception e) {
			throw new RuntimeException("Date format not valid.");
		}
		return d;
	}

	/**
	 * 두 날짜 사이의 차이
	 * 
	 * _a_t_ param startDate 시작 날짜 _a_t_ param endDate 종료 날짜 _a_t_ param format
	 * 날짜 형식 _a_t_ return long 날짜 차이
	 */
	public static long getDayDistance(String startDate, String endDate)
			throws Exception {
		return getDayDistance(startDate, endDate, null);
	}

	/**
	 * 두 날짜 사이의 차이
	 * 
	 * _a_t_ param startDate 시작 날짜 _a_t_ param endDate 종료 날짜 _a_t_ param format
	 * 날짜 형식 _a_t_ return long 날짜 차이
	 */
	public static long getDayDistance(String startDate, String endDate,
			String format) throws Exception {

		return getDayDistance(startDate, endDate, format, "U");
	}

	/**
	 * 두 날짜 사이의 차이 구하기 _a_t_ param startDate 시작 날짜 _a_t_ param endDate 종료 날짜
	 * _a_t_ param format 날짜 형식 _a_t_ param type returnType 형식(음양 구분 : "S", 음양
	 * 구분 안함 : "U") _a_t_ return long 날짜 차이
	 */
	public static long getDayDistance(String startDate, String endDate,
			String format, String returnType) throws Exception {
		if (format == null)
			format = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date sDate;
		Date eDate;
		long day2day = 0;
		try {
			sDate = sdf.parse(startDate);
			eDate = sdf.parse(endDate);
			day2day = (eDate.getTime() - sDate.getTime())
					/ (1000 * 60 * 60 * 24);
		} catch (Exception e) {
			throw new Exception("wrong format string");
		}

		if ("S".equals(returnType)) {
			return day2day;
		} else {
			return Math.abs(day2day);
		}
	}

	public static String getTrimedTime(String date, String time) {
		int minute = Integer.parseInt(time.substring(2));

		if (minute < 30)
			time = time.substring(0, 2) + "00";
		else
			time = time.substring(0, 2) + "30";

		return date + time;
	}

	public static String getLatestTime(String date, String time) {
		try {
			int minute = Integer.parseInt(time.substring(2));
			if (minute < 30)
				time = time.substring(0, 2) + "30";
			else {
				if (time.substring(0, 2).equals("23")) {
					date = DateUtil.getDayAdd(date, "yyyyMMdd", 1);
					time = "0000";
				} else {
					time = StringUtil.pad(
							Integer.parseInt(time.substring(0, 2)) + 1, 2, "0")
							+ "00";
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return date + time;
	}

	public static String getMinuteAdd(String date, String format, int minute)
			throws Exception {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+09:00"),
				Locale.KOREA);
		cal.setTime(string2Date(date, format));
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			cal.add(Calendar.MINUTE, minute);
			return sdf.format(cal.getTime());
		} catch (Exception ex) {
			throw new Exception("wrong format string");
		}
	}

	/**
	 * 입력받은 날짜에 입력받은 날수 만큼 더하기 _a_t_ param inputDate 계산할 날짜 _a_t_ param format
	 * 날짜 format _a_t_ param days add할 날 수
	 */

	public static String getDayAdd(String inputDate, String format, int days)
			throws Exception {

		String returnDate = "";

		if (inputDate == null)
			return "";
		if (format == null)
			format = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = getCalendar(inputDate);

		try {
			cal.add(Calendar.DATE, days);
			returnDate = sdf.format(cal.getTime());
		} catch (Exception e) {
			throw new Exception("wrong format string");
		}

		return returnDate;
	}

	public static String getMonthInterval(String dateString, String format,
			int distance) {
		Calendar cal = getCalendar(dateString);
		cal.roll(Calendar.MONTH, distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	public static String whichDay(String s) throws java.text.ParseException {
		String yoil;
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
		java.text.DateFormat ddf = new java.text.SimpleDateFormat("EEE");

		yoil = ddf.format(df.parse(s));

		return yoil;
	}

	/**
	 * 입력받은 날짜가 포함된 주의 mon~sun 가져오기 _a_t_ param inputDate 기준 날짜 _a_t_ return
	 * weekinfo 지난주및 금주, 내주 정보
	 */

	public static Hashtable getWeekDayInfo(String inputDate) throws Exception {

		String basedate = inputDate == null ? DateUtil.getCurrentDate()
				: inputDate;
		String prevweek = DateUtil.getDayAdd(basedate, "yyyyMMdd", -7); // 일주일전
																		// 기준일
		String nextweek = DateUtil.getDayAdd(basedate, "yyyyMMdd", 7); // 일주일후
																		// 기준일

		// 기준일의 요일
		int week = DateUtil.getCalendar(basedate).get(Calendar.DAY_OF_WEEK);

		// 이번주 월요일
		String FIRST_DAY_OF_WEEK = DateUtil.getDayAdd(basedate, "yyyyMMdd",
				2 - week);
		String FIRST_DAY_OF_WEEK_V = DateUtil.getDayAdd(basedate,
				"yyyy년 MM월 dd일", 2 - week);

		// 이번주 일요일
		String END_DAY_OF_WEEK = DateUtil.getDayAdd(basedate, "yyyyMMdd",
				8 - week);
		String END_DAY_OF_WEEK_V = DateUtil.getDayAdd(basedate,
				"yyyy년 MM월 dd일", 8 - week);

		Hashtable weekinfo = new Hashtable();
		weekinfo.put("current", basedate);
		weekinfo.put("prevweek", prevweek);
		weekinfo.put("nextweek", nextweek);
		weekinfo.put("monday", FIRST_DAY_OF_WEEK);
		weekinfo.put("monday_v", FIRST_DAY_OF_WEEK_V);
		weekinfo.put("sunday", END_DAY_OF_WEEK);
		weekinfo.put("sunday_v", END_DAY_OF_WEEK_V);

		return weekinfo;
	}

	/**
	 * 현재시간에서 입력받은 시간만큼 더한 시간 Return... int i_per_min : 분단위 (ex: 10분, 15분,
	 * 20분...) int i_add_hour : 시간 (ex: -2 --> 현재시간에서 2시간 전... )
	 */
	public static String getTimeAdd(int i_per_min, int i_add_hour)
			throws Exception {

		if (i_per_min == 0 || i_add_hour == 0)
			return "0000";

		Calendar cd = new GregorianCalendar(Locale.KOREA);
		String s_add_hour = "";

		int i_hour = cd.get(cd.HOUR_OF_DAY) + i_add_hour;
		int i_cur_minute = cd.get(cd.MINUTE);

		int i_per_hour = 60 / i_per_min;

		int[] i_arr_time = new int[i_per_hour];
		for (int i = 0; i < i_per_hour; i++) {
			i_arr_time[i] = i_per_min * (i + 1);
		}

		for (int i = 0; i < i_per_hour; i++) {
			if (i_arr_time[i] >= i_cur_minute) {
				i_cur_minute = i_arr_time[i];
				break;
			}
		}
		i_cur_minute -= i_per_min;

		if (i_hour <= 0)
			s_add_hour = "0000";
		else if (i_hour >= 24)
			s_add_hour = "23" + (i_cur_minute == 0 ? "00" : i_cur_minute + "");
		else
			s_add_hour = StringUtil.pad(i_hour + "", 2, "0")
					+ (i_cur_minute == 0 ? "00" : i_cur_minute + "");

		return s_add_hour;
	}

	public static long getMinuteDistance(String startDate, String endDate,
			String format) throws Exception {
		if (format == null)
			format = "yyyyMMddhhmm";

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date sDate, eDate;
		long minute = 0;

		try {
			sDate = sdf.parse(startDate);
			eDate = sdf.parse(endDate);
			minute = (eDate.getTime() - sDate.getTime()) / (1000 * 60);
		} catch (Exception e) {
			throw new Exception("wrong format string");
		}

		return minute;
	}
}