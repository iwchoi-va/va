package cs.com.util;

import java.util.*;

import com.locus.jedi.util.*;

/**
 * WFM 에서 사용하는 유틸 시간을 정한다.
 */
public class SchTimeUtil {
	/**
	 * Time Interval에 따라 Time Array List를 생성한다.
	 * 
	 * @param timeInterval
	 * @param prefix
	 * @param preAddList
	 * @param postAddList
	 * @return
	 */
	public static String[] getTimeIntervalList(int timeInterval, String prefix,
			ArrayList preAddList, ArrayList postAddList) {
		String time[] = SchTimeUtil.makeTimeList("0000", "2400", timeInterval);
		ArrayList timeArray = new ArrayList();

		if (preAddList != null)
			timeArray.addAll(preAddList);

		for (int i = 0; i < time.length; i++) {
			timeArray.add(prefix + time[i]);
		}

		if (postAddList != null)
			timeArray.addAll(postAddList);

		return (String[]) timeArray.toArray(new String[timeArray.size()]);
	}

	public static String[] makeTimeList(String startTime, String endTime,
			int interval) {
		List keyList = new ArrayList();
		for (int i = 0; i <= 23; i++) {
			String hour = StringUtil.pad(i + "", 2, "0");
			for (int j = 0; j < 60; j += interval) {
				String time = hour + StringUtil.pad(j + "", 2, "0");
				if (startTime.compareTo(time) <= 0
						&& endTime.compareTo(time) >= 0)
					keyList.add(time);

			}
		}
		return (String[]) keyList.toArray(new String[keyList.size()]);
	}

	public static String[] makeTimeList1(String startTime, String endTime,
			int interval) {
		List keyList = new ArrayList();
		for (int i = 0; i <= 23; i++) {
			String hour = StringUtil.pad(i + "", 2, "0");
			for (int j = 0; j < 60; j += interval) {
				String time = hour + StringUtil.pad(j + "", 2, "0");
				if (startTime.compareTo(time) <= 0
						&& endTime.compareTo(time) > 0)
					keyList.add(time);

			}
		}
		return (String[]) keyList.toArray(new String[keyList.size()]);
	}

	public static String chgUniCode(String orgData) throws Exception {
		String temp = new String(orgData.getBytes("EUC_KR"), "ISO8859_1");
		return temp;
	}
}