package xcron.com.webaction;

import jedix.xwing.action.XwingWebAction;
import sens.src.alarm.monitoring.AlarmMonitoringCache;

import java.util.Vector;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class AlarmSMSWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		try {
			ListParam final_alarmmonitoringList = new ListParam(new String[] { "ALARM_GUBUN", "ALARM_MSG", "NOTI_YN", "SMS_YN", 
					"ETC1", "ETC2", "ETC3", "ETC4", "ETC5", "ETC6", "ETC7", "SMS_MSG"
			});
			ListParam alarmSmsList = new ListParam(new String[] { "ALARM_GUBUN", "RCV_PHN_ID", "SND_MSG"});
			
			AlarmMonitoringCache alarmMonitoring = AlarmMonitoringCache.getInstance();
			ListParam Alarmlist = alarmMonitoring.getAlarmmonitoringList();
			
			for(int i=0; i<Alarmlist.rowSize(); i++) {
				String v_msg = Alarmlist.getValue(i, "SMS_MSG").toString();
				String v_gubun = Alarmlist.getValue(i, "ALARM_GUBUN").toString();
				
				SQLParam sqlParam2 = new SQLParam();
				sqlParam2.clear();
				sqlParam2.setSqlName("msens.xcron.hansol.vs_alarm_sms.count.select");
				sqlParam2.addValue("v_gubun", v_gubun);
				sqlParam2.addValue("v_msg", v_msg);
			
				SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
				
				if (sqlResult2.getCount() > 0) {
					int v_msg_cnt = sqlResult2.getListParam("msens.xcron.hansol.vs_alarm_sms.count.select").getParam(0).getInt("MSG_CNT");
					System.out.println("v_msg_cnt::" + v_msg_cnt);
					if(v_msg_cnt == 0) {
						final_alarmmonitoringList.addRow(Alarmlist.getRow(i));
					}
				}
			}
			
			//System.out.println("final_alarmmonitoringList::" + final_alarmmonitoringList.rowSize());
			
			if(final_alarmmonitoringList.rowSize() > 0) {
				Code[] code = CodeUtil.getCodes("SYS006");
				Vector<String> SMSTel = new Vector<String>();
				Vector<String> Keyword = new Vector<String>();
				Vector<String> Customer = new Vector<String>();
				Vector<String> SystemGroup = new Vector<String>();
				
				for (int i = 0; code != null && i < code.length; i++) {
					if (!"Y".equalsIgnoreCase(code[i].getUseYn())) {
						continue;
					}
					
					String v_tel = code[i].getEtc1();
					String v_keyword = code[i].getEtc2();
					String v_cust = code[i].getEtc3();
					String v_system = code[i].getEtc4();
					
					//System.out.println(v_tel + "::" + v_keyword + "::" + v_cust + "::" + v_system);
					
					SMSTel.addElement(v_tel);
					Keyword.addElement(v_keyword);
					Customer.addElement(v_cust);
					SystemGroup.addElement(v_system);
				}
				
				if(SMSTel.size() > 0) {
					for(int i=0; i<final_alarmmonitoringList.rowSize(); i++) {
						String v_msg = final_alarmmonitoringList.getValue(i, "SMS_MSG").toString();
						String v_gubun = final_alarmmonitoringList.getValue(i, "ALARM_GUBUN").toString();
						
						for(int j=0; j<SMSTel.size(); j++) {
							boolean v_check = false;
							
							System.out.println(SMSTel.elementAt(j) + "::" + v_gubun);
							
							if("KEYWORD".equals(v_gubun)) {
								if("Y".equals(Keyword.elementAt(j))){
									v_check = true;
								}
							} else if ("CUSTOMER".equals(v_gubun)) {
								if("Y".equals(Customer.elementAt(j))){
									v_check = true;
								}
							} else {
								if("Y".equals(SystemGroup.elementAt(j))){
									v_check = true;
								}
							}
							
							System.out.println("v_check::" + v_check);
							
							if(v_check) {
								String v_telnum = SMSTel.elementAt(j);
								alarmSmsList.addRow(new Object[] {
										v_gubun,
										v_telnum,
										v_msg
								});
							}
						}
					}
				}
				
				if(alarmSmsList.rowSize() > 0) {
					tran.begin();
					
					SQLParam sqlParam = new SQLParam();
					sqlParam.setSqlName("msens.xcron.hansol.app.arreo_sms.insert");
					sqlParam.addValue("alarmSmsList", alarmSmsList);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
					
					SQLParam sqlParam1 = new SQLParam();
					sqlParam1.setSqlName("msens.xcron.hansol.vs_alarm_sms.insert");
					sqlParam1.addValue("alarmSmsList", alarmSmsList);
					
					SQLServiceManager.getInstance().execute(sqlParam1, tran);
					
					tran.commit();
				}
			}
			
			//System.out.println("alarmSmsList::" + alarmSmsList.rowSize());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			tran.rollback();
			e.printStackTrace();
		}
	}
}

