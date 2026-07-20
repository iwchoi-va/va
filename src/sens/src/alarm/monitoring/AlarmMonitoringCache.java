package sens.src.alarm.monitoring;

import java.text.SimpleDateFormat;
import java.util.Date;

import sens.src.systeminfo.SystemInfoCache;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class AlarmMonitoringCache {
	private static AlarmMonitoringCache instance = null;
	
	private static ListParam alarmmonitoringList = new ListParam(new String[] { "ALARM_GUBUN", "ALARM_MSG", "NOTI_YN", "SMS_YN", 
			"ETC1", "ETC2", "ETC3", "ETC4", "ETC5", "ETC6", "ETC7", "SMS_MSG"
	});
	
	private static ListParam alarmmonitoringList_temp = new ListParam(new String[] { "ALARM_GUBUN", "ALARM_MSG", "NOTI_YN", "SMS_YN", 
			"ETC1", "ETC2", "ETC3", "ETC4", "ETC5", "ETC6", "ETC7", "SMS_MSG"
	});
	
	private static ListParam alarmmonitoringList_system = new ListParam(new String[] { "ALARM_GUBUN", "ALARM_MSG", "NOTI_YN", "SMS_YN", 
			"ETC1", "ETC2", "ETC3", "ETC4", "ETC5", "ETC6", "ETC7", "SMS_MSG"
	});
	
	private static String g_rec_latest_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_meta_latest_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_stt_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_ta_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_src_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_grd_local_time = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
	private static String g_rec_latest_time = "";
	private static String g_meta_latest_time = "";
	private static String g_stt_latest_time = "";
	private static String g_ta_latest_time = "";
	private static String g_src_latest_time = "";
	private static String g_grd_latest_time = "";
	
	public static synchronized AlarmMonitoringCache getInstance() throws Exception {
		if (instance == null) {
			synchronized (AlarmMonitoringCache.class) {
				if (instance == null) {
					instance = new AlarmMonitoringCache();
				}
			}
		}

		return instance;
	}
	
	public AlarmMonitoringCache() {
		
	}
	
	public void AlarmMonitoringCacheCall() {
		try {
			JediTransaction tran = JediTransactionManager.getJediTransaction();
			alarmmonitoringList_temp.clear();
			
			SQLParam sqlParam5 = new SQLParam();
			sqlParam5.setSqlName("msens.xcron.hansol.alarmmonitoring.day.select");
			
			SQLParam sqlResult5 = SQLServiceManager.getInstance().execute(sqlParam5);
			
			boolean is_Holiday = false;
			
			if(sqlResult5.getCount() > 0) {
				String v_week = sqlResult5.getListParam("msens.xcron.hansol.alarmmonitoring.day.select").getParam(0).getString("WEEK1");
				String v_holiday1 = sqlResult5.getListParam("msens.xcron.hansol.alarmmonitoring.day.select").getParam(0).getString("HOLIDAY1");
				String v_holiday2 = sqlResult5.getListParam("msens.xcron.hansol.alarmmonitoring.day.select").getParam(0).getString("HOLIDAY2");
				
				if("0".equals(v_week) || "*".equals(v_holiday1) || "*".equals(v_holiday2)) {
					is_Holiday = true;
				}
			}
			
			String inTime = new java.text.SimpleDateFormat("HH").format(new java.util.Date());
			
			SystemInfoCache SystemInfo = SystemInfoCache.getInstance();
			
			ListParam systemdataList = SystemInfo.getSystemdataList();
			IVRLogger.debug("######system Info = ");
			IVRLogger.debug(systemdataList.toString());
			IVRLogger.debug(systemdataList.rowSize() );
			
			if(Integer.parseInt(inTime) > 8 && Integer.parseInt(inTime) < 18 && !is_Holiday) {
				if(systemdataList.rowSize() > 0) {

					int v_rec_cnt = Integer.parseInt(systemdataList.getValue(0, "REC_CNT").toString());
					int v_stt_cnt = Integer.parseInt(systemdataList.getValue(0, "STT_CNT").toString());
					int v_meta_cnt = Integer.parseInt(systemdataList.getValue(0, "META_CNT").toString());
					int v_anl_n_cnt = Integer.parseInt(systemdataList.getValue(0, "ANL_N_CNT").toString());
					int v_ta_cnt = Integer.parseInt(systemdataList.getValue(0, "TA_CNT").toString());
					int v_src_cnt = Integer.parseInt(systemdataList.getValue(0, "SRC_CNT").toString());
					int v_grd_cnt = Integer.parseInt(systemdataList.getValue(0, "GRD_CNT").toString());
					
					String v_rec_last_time = systemdataList.getValue(0, "REC_LAST_TIME").toString();
					String v_stt_last_time = systemdataList.getValue(0, "STT_LAST_TIME").toString();
					String v_meta_last_time = systemdataList.getValue(0, "META_LAST_TIME").toString();
					String v_ta_last_time = systemdataList.getValue(0, "TA_LAST_TIME").toString();
					String v_src_last_time = systemdataList.getValue(0, "SRC_LAST_TIME").toString();
					String v_grd_last_time = systemdataList.getValue(0, "GRD_LAST_TIME").toString();
					
					int v_rec_latest_time_gap_min = getTimediffMin(g_rec_latest_local_time);
					int v_meta_latest_time_gap_min = getTimediffMin(g_meta_latest_local_time);
					int v_stt_latest_time_gap_min = getTimediffMin(g_stt_local_time);
					int v_ta_latest_time_gap_min = getTimediffMin(g_ta_local_time);
					int v_src_latest_time_gap_min = getTimediffMin(g_src_local_time);
					int v_grd_latest_time_gap_min = getTimediffMin(g_grd_local_time);
					
					int v_rec_latest_time_gap_min_db = getTimediffMin(v_rec_last_time);
					int v_meta_latest_time_gap_min_db = getTimediffMin(v_meta_last_time);
					int v_stt_latest_time_gap_min_db = getTimediffMin(v_stt_last_time);
					int v_ta_latest_time_gap_min_db = getTimediffMin(v_ta_last_time);
					int v_src_latest_time_gap_min_db = getTimediffMin(v_src_last_time);
					int v_grd_latest_time_gap_min_db = getTimediffMin(v_grd_last_time);
					
					IVRLogger.debug("v_rec_last_time = " +v_rec_last_time +" v_rec_latest_time_gap_min_db == " + v_rec_latest_time_gap_min_db);
					
					//IVRLogger.debug("v_rec_cnt = " + v_rec_cnt + "// v_rec_latest_time_gap_min_db = " + v_rec_latest_time_gap_min_db + "// v_latest_time_gap_min = " + v_rec_latest_time_gap_min + "//g_rec_latest_time = " + g_rec_latest_time + "// v_rec_last_time = " + v_rec_last_time);
					if(v_rec_cnt > 0 && v_rec_latest_time_gap_min_db > 180) {
						
						if(v_rec_latest_time_gap_min > 2 && g_rec_latest_time.equals(v_rec_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"녹취최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"REC_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"녹취최근시간 이상발생"
							});
						} else {
							g_rec_latest_time = v_rec_last_time;
						}
					} 
					
					
					if(v_stt_cnt > 0 && v_stt_latest_time_gap_min_db > 180) {
						
						if(v_stt_latest_time_gap_min > 2 && g_stt_latest_time.equals(v_stt_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"STT최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"STT_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"STT최근시간 이상발생"
							});
						} else {
							g_stt_latest_time = v_stt_last_time;
						}
					} 


					if(v_meta_cnt > 0 && v_meta_latest_time_gap_min_db > 180) {
						
						if(v_meta_latest_time_gap_min > 2 && g_meta_latest_time.equals(v_meta_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"TM연동 최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"META_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"TM연동 최근시간 이상발생"
							});
						} else {
							g_meta_latest_time = v_meta_last_time;
						}
					} 
					
					if(v_ta_cnt > 0 && v_ta_latest_time_gap_min_db > 180) {
						
						if(v_ta_latest_time_gap_min > 2 && g_ta_latest_time.equals(v_ta_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"TA최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"TA_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"TA최근시간 이상발생"
							});
						} else {
							g_ta_latest_time = v_ta_last_time;
						}
					}
					
					if(v_src_cnt > 0 && v_src_latest_time_gap_min_db > 180) {
						
						if(v_src_latest_time_gap_min > 2 && g_src_latest_time.equals(v_src_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"검색최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"SRC_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"검색최근시간 이상발생"
							});
						} else {
							g_src_latest_time = v_src_last_time;
						}
					}
					
					if(v_grd_cnt > 0 && v_grd_latest_time_gap_min_db > 180) {
						
						if(v_grd_latest_time_gap_min > 2 && g_grd_latest_time.equals(v_grd_last_time)) {
							alarmmonitoringList_temp.addRow(new Object[] {
									"SYSTEM",
									"Grade분석최근시간 이상발생",
									"Y",
									"Y",
									"BATCH",
									"GRD_LATEST_TIME",
									"",
									"",
									"",
									"",
									"",
									"Grade분석최근시간 이상발생"
							});
						} else {
							g_grd_latest_time = v_grd_last_time;
						}
					}
					
					
					
				}
			}
			
			IVRLogger.debug("############분석프로세스모니터링#############");
			IVRLogger.debug(alarmmonitoringList_temp.toString());
			
			alarmmonitoringList.clear();
			
			for(int i=0; i<alarmmonitoringList_temp.rowSize(); i++) {
				alarmmonitoringList.addRow(alarmmonitoringList_temp.getRow(i));
			}
			
			IVRLogger.debug("############분석프로세스모니터링#############");
			IVRLogger.debug(alarmmonitoringList.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getTimediffMin(String v_time) {
		int return_min = -1;
		
		try {
			SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
			Date startday = sf.parse(v_time);

			long startTime=startday.getTime();

			String nowTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
			Date endDate = sf.parse(nowTime);
			long endTime = endDate.getTime();
				
			long mills = endTime-startTime;
			long min=mills/60000;
			return_min = (int) min;

		} catch (Exception e) {
			e.printStackTrace();
			return return_min;
		}
		return return_min;
	}
	
	public ListParam getAlarmmonitoringList() {
		return alarmmonitoringList;
	}
}
