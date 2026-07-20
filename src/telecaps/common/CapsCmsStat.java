package telecaps.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.TimerTask;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.EnvironmentXmlDAO;

public class CapsCmsStat extends TimerTask {
	JediTransaction tran = JediTransactionManager.getJediTransaction();
	
	String u_device_id  = EnvironmentXmlDAO.getInstance().getProperty("cms_device_id");
	String u_file_path  = EnvironmentXmlDAO.getInstance().getProperty("cms_file_path");
	String device_id = null;
	String device_ip = null;
    String device_gu1 = null;
    String device_nm1 = null;
    String device_gu2 = null;
    String device_nm2 = null;
    String device_local = null;
    String device_local_nm = null;
    String use_yn = null;
    String alarm_yn = null;
	String file_path = null;
	
	boolean alarmMsgFlag = false;
	int agentSeq = 0;
	
	public CapsCmsStat() {
		
	}

	public void run() {
		//Logger.write("CmsStat", Thread.currentThread().getName(), "CmsStat ����", "", "", "", "");
		
		StringTokenizer t_device_id= new StringTokenizer(u_device_id, "|");
		StringTokenizer t_file_path= new StringTokenizer(u_file_path, "|");
		
		while(t_device_id.hasMoreTokens()) {
			device_id = t_device_id.nextToken();
			file_path = t_file_path.nextToken();
			
			getDeviceInfo();
			getInfoStatus();
		}
		
		//Logger.write("CmsStat", Thread.currentThread().getName(), "CmsStat ����", "", "", "", "");
	}
	
	public void getDeviceInfo() {
		SQLParam sqlParam  = new SQLParam();
	    SQLParam sqlResult = new SQLParam(); 
		
		try {
			sqlParam.clear();
	        sqlParam.setSqlName("telecaps.sql.device_id.select");
	        sqlParam.addValue("device_id", device_id);
	        sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
	        
	        for (int i=0; i<sqlResult.getCount(); i++) {
		         device_ip       = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_ip");
		         device_gu1      = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_gu1");
		         device_nm1      = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_nm1");
		         device_gu2      = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_gu2");
		         device_nm2      = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_nm2");
		         device_local    = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_local");
		         device_local_nm = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("device_local_nm");
		         use_yn			 = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("use_yn");
		         alarm_yn		 = sqlResult.getListParam("telecaps.sql.device_id.select").getParam(i).getString("alarm_yn");
	        }
	        

			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.recvcmd.maxnum.select");
			sqlParam.addValue("device_id", device_id);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			agentSeq = 0;
			if (sqlResult.getCount() > 0) {
				agentSeq = sqlResult.getListParam("telecaps.res.tcagnt.recvcmd.maxnum.select").getParam(0).getInt("maxnum");
			}
			
		} catch (Exception e) {
		    ErrorLogger.error("[CMSgetDeviceInfo] " + e);
		}
	}
	
	public void getInfoStatus() {
		int k = 0;
		String line = null;
		StringTokenizer token = null;
		BufferedReader br = null;
		FileReader f = null;
		
		try {
			f = new FileReader(file_path);
			br = new BufferedReader(f);
				
			while ((line = br.readLine()) != null) {
				
				if (k <= 29 && (line.indexOf("PROBLEM") >= 0 
						      || line.indexOf("LOW")  >= 0
						      || line.indexOf("CRITICAL")  >= 0 )) {

					line = line.replaceAll("\t", " ").trim();
					String system_stability = line.substring(0, line.indexOf(":")+1);
					String status = line.substring(line.indexOf(":")+1);
					String alarm_msg = system_stability + status.trim();
					
					String alarm_grade = "WRN";
					if(line.indexOf("PROBLEM") >= 0 || line.indexOf("CRITICAL")  >= 0){
						if(line.indexOf("PROBLEM") >= 0 && line.indexOf("ACD1") >= 0){
							alarm_grade = "MAJ";
						}
					}
					
					insertAlarmMsg("������Ϲ߻�", alarm_msg, "60", "0000", "0100", alarm_grade, "Y", "N");
					alarmMsgFlag = true;
					
				} else if(line.indexOf("Pass 30 Second Interval")  >= 0) {
					line = br.readLine();
					k++;
					
					token = new StringTokenizer(line," ");
					token.nextToken();
					token.nextToken();
					token.nextToken();
					token.nextToken();
					token.nextToken();
					token.nextToken();
					token.nextToken();
					String str_cpu = token.nextToken().trim();
					
					insertCpuInfo(str_cpu);
					
				} else if(line.indexOf("Filesystem")  >= 0) {
					
					while ((line = br.readLine()) != null) {
						k++;
						token = new StringTokenizer(line," ");
						
						if(token.countTokens() < 2){
							break;
						}
						
						token.nextToken();
						token.nextToken();
						String disk_used = token.nextToken().trim();
						String disk_free = token.nextToken().trim();
						token.nextToken();
						String disk_mount = token.nextToken().trim();
						
						insertDiskInfo(disk_mount, disk_used, disk_free);
					}
					
				} else if(line.indexOf("Swap Memory Status")  >= 0) {
					line = br.readLine();
					k++;
					line = br.readLine();
					k++;
					line = br.readLine();
					k++;
					
					token = new StringTokenizer(line," ");
					
					token.nextToken();
					token.nextToken().trim();
					String mem_used = token.nextToken().trim();
					String mem_free = token.nextToken().trim();
					
					insertMemoryInfo(mem_used, mem_free);
					
					line = br.readLine();
					k++;
					line = br.readLine();
					k++;
					
					token = new StringTokenizer(line," ");
					token.nextToken();
					token.nextToken().trim();
					String swap_used = token.nextToken().trim();
					String swap_free = token.nextToken().trim();
					
					insertSwapInfo(swap_used, swap_free);
				}
				k++;
			}
			br.close();
			f.close();
			
			if(!alarmMsgFlag){
				insertAlarmBackMsg("������Ϲ߻�", "MAJ", "Y");
			}
			
			insertAgentValue();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if (br != null) br.close();
				if (f != null) f.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}        	
		}
	}
	
	public void insertAgentValue() {
		SQLParam sqlParam  = new SQLParam();
		
	    try {
	    	sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.alarm.merge.proc");
			sqlParam.addValue("device_id", device_id);
			SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
		} catch (SQLServiceException e) {
			ErrorLogger.error("[CMSinsertAgentValue] " + e);
		}
	}
	
	public void insertSwapInfo(String swap_used, String swap_free){
		ListParam lpSchValue = new ListParam(new String[] {"device_id", "device_ip", "device_id_seq", "crit_name", "crit_sub_name", "value" });
		
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "swapUsed", "", swap_used });
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "swapFree", "", swap_free });
		
		SQLParam sqlParam  = new SQLParam();
		SQLParam sqlResult = new SQLParam(); 
		
		String t_gijun1 = "";
		String t_gijun2 = "";
		String t_gijun3 = "";
		String t_alarm_gijun = "";
		String t_alarm_msg = "";
		String t_sms_yn = "";
		String t_s2o_yn = "";
		String t_intervl = "";
		String from_time = "";
		String to_time = "";
		
		float swapUsage = (float) (Math.round((Float.parseFloat(swap_used) / (Float.parseFloat(swap_used) + Float.parseFloat(swap_free)) * 100) * 100) / 100.0);
		
	    try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.recvcmd.insert");
			sqlParam.addValue("telecaps.sql.tcagnt.recvcmd.insert", lpSchValue);
			SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			sqlParam.clear();
			sqlParam.setSqlName("w_main.sql.agent.alarm.hw.gijun.select");
			sqlParam.addValue("device_id", device_id);
			sqlParam.addValue("item_nm", "SWAP");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			for (int i = 0; i < sqlResult.getCount(); i++) {
				t_gijun1 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN1");
				t_gijun2 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN2");
				t_gijun3 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN3");
				t_alarm_gijun = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_GIJUN");
				t_alarm_msg = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_MSG");
				t_sms_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("SMS_YN");
				t_s2o_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("S2O_YN");
				t_intervl = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("INTERVL");
				from_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("FROM_TIME");
				to_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("TO_TIME");
				
				String alarm_grade = "NO";
				int alarm_gijun_flag = 0;
				String alarm_gijun_value = "0";
				
				if(Float.parseFloat(t_gijun3) <= swapUsage){
					alarm_grade = "MAJ";
					alarm_gijun_flag = 3;
					alarm_gijun_value = t_gijun3;
				}else if(Float.parseFloat(t_gijun2) <= swapUsage){
					alarm_grade = "MIN";
					alarm_gijun_flag = 2;
					alarm_gijun_value = t_gijun2;
				}else if(Float.parseFloat(t_gijun1) <= swapUsage){
					alarm_grade = "WRN";
					alarm_gijun_flag = 1;
					alarm_gijun_value = t_gijun1;
				}
				
				if(!alarm_grade.equals("NO") && (alarm_gijun_flag >= Integer.parseInt(t_alarm_gijun))){
					String v_msg = alarm_gijun_value + "!" + swapUsage;
					
					insertAlarmMsg(t_alarm_msg, v_msg, t_intervl, from_time, to_time, alarm_grade, t_sms_yn, t_s2o_yn);
				} else if(alarm_grade.equals("NO")){
					insertAlarmBackMsg(t_alarm_msg, "MAJ", t_sms_yn);
				}
			}
		} catch (SQLServiceException e) {
			ErrorLogger.error("[CMSinsertSwapInfo] " + e);
		}
	}
	
	public void insertMemoryInfo(String mem_used, String mem_free){
		ListParam lpSchValue = new ListParam(new String[] {"device_id", "device_ip", "device_id_seq", "crit_name", "crit_sub_name", "value" });
		
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "memoryUsed", "", mem_used });
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "memoryFree", "", mem_free });
		
		SQLParam sqlParam  = new SQLParam();
		SQLParam sqlResult = new SQLParam(); 
		
		String t_gijun1 = "";
		String t_gijun2 = "";
		String t_gijun3 = "";
		String t_alarm_gijun = "";
		String t_alarm_msg = "";
		String t_sms_yn = "";
		String t_s2o_yn = "";
		String t_intervl = "";
		String from_time = "";
		String to_time = "";
		
		float memUsage = (float) (Math.round((Float.parseFloat(mem_used) / (Float.parseFloat(mem_used) + Float.parseFloat(mem_free)) * 100) * 100) / 100.0);
		
	    try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.recvcmd.insert");
			sqlParam.addValue("telecaps.sql.tcagnt.recvcmd.insert", lpSchValue);
			SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			sqlParam.clear();
			sqlParam.setSqlName("w_main.sql.agent.alarm.hw.gijun.select");
			sqlParam.addValue("device_id", device_id);
			sqlParam.addValue("item_nm", "MEMORY");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			for (int i = 0; i < sqlResult.getCount(); i++) {
				t_gijun1 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN1");
				t_gijun2 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN2");
				t_gijun3 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN3");
				t_alarm_gijun = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_GIJUN");
				t_alarm_msg = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_MSG");
				t_sms_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("SMS_YN");
				t_s2o_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("S2O_YN");
				t_intervl = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("INTERVL");
				from_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("FROM_TIME");
				to_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("TO_TIME");
				
				String alarm_grade = "NO";
				int alarm_gijun_flag = 0;
				String alarm_gijun_value = "0";
				
				if(Float.parseFloat(t_gijun3) <= memUsage){
					alarm_grade = "MAJ";
					alarm_gijun_flag = 3;
					alarm_gijun_value = t_gijun3;
				}else if(Float.parseFloat(t_gijun2) <= memUsage){
					alarm_grade = "MIN";
					alarm_gijun_flag = 2;
					alarm_gijun_value = t_gijun2;
				}else if(Float.parseFloat(t_gijun1) <= memUsage){
					alarm_grade = "WRN";
					alarm_gijun_flag = 1;
					alarm_gijun_value = t_gijun1;
				}
				
				if(!alarm_grade.equals("NO") && (alarm_gijun_flag >= Integer.parseInt(t_alarm_gijun))){
					String v_msg = alarm_gijun_value + "!" + memUsage;
					
					insertAlarmMsg(t_alarm_msg, v_msg, t_intervl, from_time, to_time, alarm_grade, t_sms_yn, t_s2o_yn);
				} else if(alarm_grade.equals("NO")){
					insertAlarmBackMsg(t_alarm_msg, "MAJ", t_sms_yn);
				}
			}
		} catch (SQLServiceException e) {
			ErrorLogger.error("[CMSinsertMemInfo] " + e);
		}
	}
	
	public void insertDiskInfo(String disk_mount, String disk_used, String disk_free){
		ListParam lpSchValue = new ListParam(new String[] {"device_id", "device_ip", "device_id_seq", "crit_name", "crit_sub_name", "value" });
		
		long disk_total = Long.parseLong(disk_used) + Long.parseLong(disk_free);
		
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "diskName", disk_mount, disk_mount });
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "diskTotal", disk_mount, disk_total });
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "diskUsed", disk_mount, disk_used });
		
		SQLParam sqlParam  = new SQLParam();
		SQLParam sqlResult = new SQLParam(); 
		
		String t_gijun1 = "";
		String t_gijun2 = "";
		String t_gijun3 = "";
		String t_alarm_gijun = "";
		String t_alarm_msg = "";
		String t_sms_yn = "";
		String t_s2o_yn = "";
		String t_intervl = "";
		String from_time = "";
		String to_time = "";
		
		String v_alarm = "";
		
		float diskUsage = (float) (Math.round((Float.parseFloat(disk_used) / (Float.parseFloat(disk_used) + Float.parseFloat(disk_free)) * 100) * 100) / 100.0);
		
	    try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.recvcmd.insert");
			sqlParam.addValue("telecaps.sql.tcagnt.recvcmd.insert", lpSchValue);
			SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			sqlParam.clear();
			sqlParam.setSqlName("w_main.sql.agent.alarm.disk.gijun.select");
			sqlParam.addValue("device_id", device_id);
			sqlParam.addValue("parti_nm", disk_mount);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			for (int i = 0; i < sqlResult.getCount(); i++) {
				t_gijun1 = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("GIJUN1");
				t_gijun2 = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("GIJUN2");
				t_gijun3 = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("GIJUN3");
				t_alarm_gijun = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("ALARM_GIJUN");
				t_alarm_msg = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("ALARM_MSG");
				t_sms_yn = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("SMS_YN");
				t_s2o_yn = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("S2O_YN");
				t_intervl = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("INTERVL");
				from_time = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("FROM_TIME");
				to_time = sqlResult.getListParam("w_main.sql.agent.alarm.disk.gijun.select").getParam(i).getString("TO_TIME");
				
				String alarm_grade = "NO";
				int alarm_gijun_flag = 0;
				String alarm_gijun_value = "0";
				
				if(Float.parseFloat(t_gijun3) <= diskUsage){
					alarm_grade = "MAJ";
					alarm_gijun_flag = 3;
					alarm_gijun_value = t_gijun3;
				}else if(Float.parseFloat(t_gijun2) <= diskUsage){
					alarm_grade = "MIN";
					alarm_gijun_flag = 2;
					alarm_gijun_value = t_gijun2;
				}else if(Float.parseFloat(t_gijun1) <= diskUsage){
					alarm_grade = "WRN";
					alarm_gijun_flag = 1;
					alarm_gijun_value = t_gijun1;
				}
				
				v_alarm = t_alarm_msg + "!" + disk_mount;
				
				if(!alarm_grade.equals("NO") && (alarm_gijun_flag >= Integer.parseInt(t_alarm_gijun))){
					String v_msg = alarm_gijun_value + "!" + diskUsage;
					
					insertAlarmMsg(v_alarm, v_msg, t_intervl, from_time, to_time, alarm_grade, t_sms_yn, t_s2o_yn);
				} else if(alarm_grade.equals("NO")){
					insertAlarmBackMsg(v_alarm, "MAJ", t_sms_yn);
				}
			}
		} catch (SQLServiceException e) {
			ErrorLogger.error("[CMSinsertDiskInfo] " + e);
		}
	}
	
	public void insertCpuInfo(String value) {
		ListParam lpSchValue = new ListParam(new String[] {"device_id", "device_ip", "device_id_seq", "crit_name", "crit_sub_name", "value" });
		lpSchValue.addRow(new Object[] { device_id, device_ip, agentSeq, "cpuIdle", "", value });
		
		SQLParam sqlParam  = new SQLParam();
		SQLParam sqlResult = new SQLParam(); 
		
		String t_gijun1 = "";
		String t_gijun2 = "";
		String t_gijun3 = "";
		String t_alarm_gijun = "";
		String t_alarm_msg = "";
		String t_sms_yn = "";
		String t_s2o_yn = "";
		String t_intervl = "";
		String from_time = "";
		String to_time = "";
		
		float cpuUsage = (float) (Math.round((100 - Float.parseFloat(value)) * 100) / 100.0);
		
	    try {
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.tcagnt.recvcmd.insert");
			sqlParam.addValue("telecaps.sql.tcagnt.recvcmd.insert", lpSchValue);
			SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			sqlParam.clear();
			sqlParam.setSqlName("w_main.sql.agent.alarm.hw.gijun.select");
			sqlParam.addValue("device_id", device_id);
			sqlParam.addValue("item_nm", "CPU");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			tran.commit();
			
			for (int i = 0; i < sqlResult.getCount(); i++) {
				t_gijun1 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN1");
				t_gijun2 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN2");
				t_gijun3 = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("GIJUN3");
				t_alarm_gijun = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_GIJUN");
				t_alarm_msg = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("ALARM_MSG");
				t_sms_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("SMS_YN");
				t_s2o_yn = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("S2O_YN");
				t_intervl = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("INTERVL");
				from_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("FROM_TIME");
				to_time = sqlResult.getListParam("w_main.sql.agent.alarm.hw.gijun.select").getParam(i).getString("TO_TIME");
				
				String alarm_grade = "NO";
				int alarm_gijun_flag = 0;
				String alarm_gijun_value = "0";
				
				if(Float.parseFloat(t_gijun3) <= cpuUsage){
					alarm_grade = "MAJ";
					alarm_gijun_flag = 3;
					alarm_gijun_value = t_gijun3;
				}else if(Float.parseFloat(t_gijun2) <= cpuUsage){
					alarm_grade = "MIN";
					alarm_gijun_flag = 2;
					alarm_gijun_value = t_gijun2;
				}else if(Float.parseFloat(t_gijun1) <= cpuUsage){
					alarm_grade = "WRN";
					alarm_gijun_flag = 1;
					alarm_gijun_value = t_gijun1;
				}
				
				if(!alarm_grade.equals("NO") && (alarm_gijun_flag >= Integer.parseInt(t_alarm_gijun))){
					String v_msg = alarm_gijun_value + "!" + cpuUsage;
					
					insertAlarmMsg(t_alarm_msg, v_msg, t_intervl, from_time, to_time, alarm_grade, t_sms_yn, t_s2o_yn);
				} else if(alarm_grade.equals("NO")){
					insertAlarmBackMsg(t_alarm_msg, "MAJ", t_sms_yn);
				}
			}
			
			
		} catch (SQLServiceException e) {
			ErrorLogger.error("[CMSinsertCpuInfo] " + e);
		}
	}
	
	public void insertAlarmMsg(String alarm_msg, String msg, String intervl, String from_time, String to_time, String alarm_grade, String sms_yn, String s2o_yn) {
		String same_msg_yn = "N";
		String alarm_time_yn = "Y";
		String v_alarm_msg = alarm_msg + "!" + alarm_grade;
		String full_alarm_msg = alarm_msg + "!" + alarm_grade + "!" + msg;
		SQLParam sqlParam  = new SQLParam();
	    SQLParam sqlResult = new SQLParam(); 
		
		try {
	            sqlParam.clear();
	            sqlParam.setSqlName("telecaps.sql.tcagnt.samealarm.list");
	            sqlParam.addValue("device_id", device_id);
	            sqlParam.addValue("intervl", intervl);
	            sqlParam.addValue("alarm_msg", v_alarm_msg);
	            sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
	            tran.commit();
	            
	            for (int i=0; i<sqlResult.getCount(); i++) {
	                same_msg_yn = sqlResult.getListParam("telecaps.res.tcagnt.samealarm.list").getParam(i).getString("same_msg_yn");
	            }
	            
	            if (!"0000".equals(from_time) || !"0000".equals(to_time)) {
					sqlParam.clear();
					sqlParam.setSqlName("telecaps.sql.tcagnt.time.list");
					sqlParam.addValue("from_time", from_time);
					sqlParam.addValue("to_time", to_time);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					tran.commit();
					
					for (int i = 0; i < sqlResult.getCount(); i++) {
						alarm_time_yn = sqlResult.getListParam("telecaps.res.tcagnt.time.list").getParam(i).getString("alarm_time_yn");
					}

				}
	            
	            if ("N".equals(same_msg_yn) && "Y".equals(alarm_time_yn) && "Y".equals(alarm_yn)) {
	            	ListParam lpSchValueAlarm  = new ListParam(new String[]{"device_id", "device_ip", "alarm_msg", "alarm_grade", "maxnum", "sms_yn" ,"s2o_yn" ,"msg_cd1", "device_gu1", "device_nm1", "device_gu2", "device_nm2", "device_local", "device_local_nm" , "use_yn"});
	            	String alarm_use_yn = "N";
	                lpSchValueAlarm.addRow( new Object[]{device_id, device_ip, full_alarm_msg, alarm_grade, null,  sms_yn, s2o_yn, null, device_gu1, device_nm1, device_gu2, device_nm2, device_local, device_local_nm, alarm_use_yn} );
	                
	                sqlParam.clear();
	                sqlParam.setSqlName("telecaps.sql.tcagnt.alarm.insert");
	                ErrorLogger.info("============= CapsCmsStat AlARM_MSG USE_YN : "+alarm_use_yn);
	                sqlParam.addValue("telecaps.sql.tcagnt.alarm.insert", lpSchValueAlarm);
	                SQLServiceManager.getInstance().execute(sqlParam);
	                tran.commit();
	            }
	            
		} catch (Exception e) {
			ErrorLogger.error("[CMSinsertAlarmMsg] " + e);
		}
	}
	
	public void insertAlarmBackMsg(String alarm_msg, String alarm_grade, String sms_yn) {
		SQLParam sqlParam  = new SQLParam();
		SQLParam sqlResult  = new SQLParam();
		String same_msg_yn = "N";
	     
	    try {
	    	sqlParam.clear();
            sqlParam.setSqlName("telecaps.sql.tcagnt.samealarmback.list");
            sqlParam.addValue("device_id", device_id);
            sqlParam.addValue("alarm_msg", alarm_msg);
            sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
            
            if(sqlResult.getCount() != 0){
            	same_msg_yn = "Y";
            }
            
            if(same_msg_yn.equals("Y")){
		    	sqlParam.clear();
		    	sqlParam.setSqlName("w_main.cmsAlarm.message.update");
	            sqlParam.addValue("device_id", device_id);
	            sqlParam.addValue("alarm_msg", alarm_msg);
	            SQLServiceManager.getInstance().execute(sqlParam);
	            tran.commit();
	            
	            if("Y".equals(sms_yn)){
		            ListParam lpSchValueAlarm  = new ListParam(new String[]{"device_id", "device_ip", "sms_yn" ,"s2o_yn", "device_gu1", "device_nm1", "device_gu2", "device_nm2", "device_local", "device_local_nm", "alarm_grade", "alarm_msg" });  
		            lpSchValueAlarm.addRow( new Object[]{device_id, device_ip, "Y", "N", device_gu1, device_nm1, device_gu2, device_nm2, device_local, device_local_nm, alarm_grade, alarm_msg} );
		            
		            sqlParam.clear();
		            sqlParam.setSqlName("w_main.cmsAlarm.clear.message.insert");
		            sqlParam.addValue("w_main.cmsAlarm.clear.message.insert", lpSchValueAlarm);
		            SQLServiceManager.getInstance().execute(sqlParam);
		            tran.commit();
	            }
            }
	     } catch (Exception e) {
	      ErrorLogger.error("[CMSinsertAlarmBackMsg] " + e);
	     }
	}
}
