package telecaps.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * Written by Yeon Jung Mo(2008/10/10)
 * Class Name	 	: DateTime
 * Argument 		: 없음 
 * Description		: 현재 날짜, 시간 구하는 객체
 */
public class DateTime {
	/*
	 * 현재 날짜
	 */
	public static String getDate(){
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	
	/*
	 * SMS용 현재날짜 
	 */
	public static String getSMSDate(){
		return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
	}
	
	/*
	 * 현재 시간
	 */
	public static String getTime(){
		return new SimpleDateFormat("HHmmss").format(new Date());
	}
	
	/*
	 * SMS용 현재 시간
	 */
	public static String getSMSTime(){
		return new SimpleDateFormat("hh:mm aaa").format(new Date());
	}
	
	/*
	 * 밀리 세컨드 현재 시간
	 */
	public static String getmTime(){
		return new SimpleDateFormat("HHmmssSS").format(new Date());
	}
	
	/*
	 * 현재 날짜을 기준으로 특정 날짜 구함
	 */
	public static String getSpecificDate(int changeDate) throws Exception{
		SimpleDateFormat regFormatter = new SimpleDateFormat("yyyyMMdd");
		Date trialTime = regFormatter.parse(getDate());
		Calendar cal = new GregorianCalendar();
		cal.setTime(trialTime);
		cal.add(Calendar.DATE,+changeDate); 
		Date currentTime = cal.getTime();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
		return formatter.format(currentTime);
	}
}
