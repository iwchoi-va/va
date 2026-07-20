package telecaps.common;

import java.util.TimerTask;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class CapsSnmpCheckDb extends TimerTask {
	String device_id = null;
	String device_ip = null;
	String device_gu1 = null;
	String device_nm1 = null;
	String device_gu2 = null;
	String device_nm2 = null;
	String device_local = null;
	String device_local_nm = null;
	String alarm_grade = null;

	String alarm_msg_cd = null;
	String alarm_msg = null;
	String interval = null;
	String sms_yn = null;
	String s2o_yn = null;

	public CapsSnmpCheckDb() {
	}

	public void run() {
		String[] a_device_id = null;
		String[] a_device_ip = null;
		String[] a_device_ip2 = null;
		String[] a_last_updated = null;
		String[] a_cur_date = null;
		String[] a_time_gap = null;
		String[] a_alarm_grade = null;

		//ErrorLogger.debug(Thread.currentThread().getName() + " snmp check ����");
		//Logger.write("snmpchekdb", Thread.currentThread().getName(), "snmp check ����", "", "", "", "");

		SQLParam sqlParam = new SQLParam();

		try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.snmp.db.list");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			//ErrorLogger.debug("telecaps.sql.snmp.db.list sqlResult.getCount " + sqlResult.getCount());

			if (sqlResult.getCount() > 0) {
				a_device_id = new String[sqlResult.getCount()];
				a_device_ip = new String[sqlResult.getCount()];
				a_device_ip2 = new String[sqlResult.getCount()];
				a_last_updated = new String[sqlResult.getCount()];
				a_cur_date = new String[sqlResult.getCount()];
				a_time_gap = new String[sqlResult.getCount()];
				a_alarm_grade = new String[sqlResult.getCount()];

				for (int i = 0; i < sqlResult.getCount(); i++) {
					a_device_id[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("DEVICE_ID");
					a_device_ip[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("DEVICE_IP");
					a_device_ip2[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("DEVICE_IP2");
					a_last_updated[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("LAST_UPDATED");
					a_cur_date[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("CUR_DATE");
					a_time_gap[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("TIME_GAP");
					a_alarm_grade[i] = sqlResult.getListParam("telecaps.res.snmp.db.list").getParam(i).getString("ALARM_GRADE");

					//ErrorLogger.debug("Snmp DB:" + a_device_id[i] + " " + a_device_ip[i] + " " + a_last_updated[i] + " " + a_cur_date[i] + " " + a_time_gap[i] + " " + a_alarm_grade[i]);

					getAlarmInfo(a_device_ip[i], a_alarm_grade[i]);
					// ��� �˶��� ���
					if (!a_alarm_grade[i].equals("NO")) {
						//ErrorLogger.error("snmp �˶�:" + a_device_id[i] + " " + a_device_ip[i] + " " + a_last_updated[i] + " " + a_cur_date[i] + " " + a_time_gap[i] + " " + a_alarm_grade[i]);
						//Logger.write("snmpchekdb", Thread.currentThread().getName(), "snmp �˶�: " + a_device_id[i] + " " + a_last_updated[i] + " " + a_cur_date[i] + " " + a_time_gap[i], "", "", "", "");
						insertAlarmMsg();
					} else {
						insertAlarmBackMsg(a_device_id[i]);
					}
				}
			}

		} catch (Exception e) {
			ErrorLogger.error(e);
		}
		//ErrorLogger.debug(Thread.currentThread().getName() + " snmp check ����");
		//Logger.write("snmpchekdb", Thread.currentThread().getName(), "snmp check ����", "", "", "", "");
	}

	public void getAlarmInfo(String device_ip, String alarm_grade) {
		SQLParam sqlParam = new SQLParam();
		SQLParam sqlResult = new SQLParam();

		try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.device.ip.select");
			sqlParam.addValue("device_ip", device_ip);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			for (int i = 0; i < sqlResult.getCount(); i++) {
				device_id = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_id");
				this.device_ip = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_ip");
				device_gu1 = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_gu1");
				device_nm1 = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_nm1");
				device_gu2 = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_gu2");
				device_nm2 = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_nm2");
				device_local = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_local");
				device_local_nm = sqlResult.getListParam("telecaps.res.device.ip.select").getParam(i).getString("device_local_nm");
			}

			/*ErrorLogger.debug("getAlarmInfo device_id:" + device_id);
			ErrorLogger.debug("getAlarmInfo device_ip:" + device_ip);
			ErrorLogger.debug("getAlarmInfo device_gu1:" + device_gu1);
			ErrorLogger.debug("getAlarmInfo device_nm1:" + device_nm1);
			ErrorLogger.debug("getAlarmInfo device_gu2:" + device_gu2);
			ErrorLogger.debug("getAlarmInfo device_nm2:" + device_nm2);
			ErrorLogger.debug("getAlarmInfo device_local:" + device_local);
			ErrorLogger.debug("getAlarmInfo device_local_nm:" + device_local_nm);*/

			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.snmp.db.gijun.select");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			for (int i = 0; i < sqlResult.getCount(); i++) {
				alarm_msg_cd = sqlResult.getListParam("telecaps.res.tcagnt.snmp.db.gijun.select").getParam(i).getString("ALARM_MSG_CD");
				alarm_msg = sqlResult.getListParam("telecaps.res.tcagnt.snmp.db.gijun.select").getParam(i).getString("ALARM_MSG");
				interval = sqlResult.getListParam("telecaps.res.tcagnt.snmp.db.gijun.select").getParam(i).getString("INTERVL");
				sms_yn = sqlResult.getListParam("telecaps.res.tcagnt.snmp.db.gijun.select").getParam(i).getString("SMS_YN");
				s2o_yn = sqlResult.getListParam("telecaps.res.tcagnt.snmp.db.gijun.select").getParam(i).getString("S2O_YN");
			}

			this.alarm_grade = alarm_grade;

			/*ErrorLogger.debug("getAlarmInfo alarm_msg:" + alarm_msg);
			ErrorLogger.debug("getAlarmInfo alarm_grade:" + alarm_grade);
			ErrorLogger.debug("getAlarmInfo alarm_interval:" + interval);*/

		} catch (Exception e) {
			ErrorLogger.error("getAlarmInfo " + e);
		}

	}

	// �˶�
	public void insertAlarmMsg() {
		String same_msg_yn = "N";
		String full_alarm_msg = "";

		SQLParam sqlParam = new SQLParam();
		SQLParam sqlResult = new SQLParam();

		full_alarm_msg = alarm_msg;

		// �ð��� �ߺ� �޼��� Ȯ��
		try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.samealarm.list");
			sqlParam.addValue("device_id", device_id);
			sqlParam.addValue("intervl", interval);
			sqlParam.addValue("alarm_msg", alarm_msg);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			for (int i = 0; i < sqlResult.getCount(); i++) {
				same_msg_yn = sqlResult.getListParam("telecaps.res.tcagnt.samealarm.list").getParam(i).getString("same_msg_yn");
			}

		} catch (Exception e) {
			ErrorLogger.error("[insertAlarmMsg] " + e);
		}
		//Logger.write("snmpchekdb", Thread.currentThread().getName(), "�ߺ� �˶�: " + same_msg_yn, "", "", "", "");
		//ErrorLogger.debug("[insertAlarmMsg] same_msg_yn:" + same_msg_yn);

		if ("N".equals(same_msg_yn)) {
			String alarm_use_yn = "N";
			try {
				ListParam lpSchValueAlarm = new ListParam(new String[] {
						"device_id", "device_ip", "alarm_msg", "alarm_grade",
						"maxnum", "sms_yn", "s2o_yn", "msg_cd1", "device_gu1",
						"device_nm1", "device_gu2", "device_nm2",
						"device_local", "device_local_nm" ,  "use_yn" });

				lpSchValueAlarm.addRow(new Object[] { device_id, device_ip,
						full_alarm_msg, alarm_grade, null, sms_yn, s2o_yn,
						alarm_msg_cd, device_gu1, device_nm1, device_gu2,
						device_nm2, device_local, device_local_nm, alarm_use_yn });

				sqlParam.clear();
				sqlParam.setSqlName("telecaps.sql.tcagnt.alarm.insert");
				//ErrorLogger.info("==================== CapsSnmpCheckDB USE_YN : "+alarm_use_yn);
				sqlParam.addValue("telecaps.sql.tcagnt.alarm.insert", lpSchValueAlarm);
				SQLServiceManager.getInstance().execute(sqlParam);

			} catch (Exception e) {
				ErrorLogger.error("[insertAlarmMsg] " + e);
			}

		}

	}

	// �˶� ����
	public void insertAlarmBackMsg(String t_device_id) {
		SQLParam sqlParam = new SQLParam();
		SQLParam sqlResult = new SQLParam();

		try {
			sqlParam.clear();
			sqlParam.setSqlName("w_main.snmpAlarm.message.cnt.check");
			sqlParam.addValue("device_id", t_device_id);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			if (sqlResult.getCount() > 0) {
				if (sqlResult.getListParam("w_main.snmpAlarm.message.cnt.check").getParam(0).getInt("ALARM_CNT") > 0) {
					sqlParam.clear();
					sqlParam.setSqlName("w_main.snmpAlarm.message.update");
					sqlParam.addValue("device_id", t_device_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

					ListParam lpSchValueAlarm = new ListParam(new String[] {
							"device_id", "device_ip", "sms_yn", "s2o_yn",
							"device_gu1", "device_nm1", "device_gu2",
							"device_nm2", "device_local", "device_local_nm",
							"alarm_grade" });
					lpSchValueAlarm.addRow(new Object[] { device_id, device_ip,
							sms_yn, s2o_yn, device_gu1, device_nm1, device_gu2,
							device_nm2, device_local, device_local_nm, "MAJ" });

					sqlParam.clear();
					sqlParam.setSqlName("w_main.snmpAlarm.clear.message.insert");
					sqlParam.addValue("w_main.snmpAlarm.clear.message.insert", lpSchValueAlarm);
					SQLServiceManager.getInstance().execute(sqlParam);
				}
			}

		} catch (Exception e) {
			ErrorLogger.error("[insertAlarmBackMsg] " + e);
		}
	}
}
