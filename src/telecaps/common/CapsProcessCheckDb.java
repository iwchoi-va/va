package telecaps.common;

import java.io.IOException;
import java.util.TimerTask;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class CapsProcessCheckDb extends TimerTask {
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
	
	String authname = null;
	String authpass = null;

	public CapsProcessCheckDb() {
		
	}
	
	public void run() {  //device_id , device_ip , msg1, msg2, etc1, etc2 , etc3, last_updated, send_yn,
	       //use_yn , device_seq, sms_yn , device_gu1 , device_nm1 , device_local ,device_local_nm , process_nm , chk_yn
		String[] a_device_id = null;
		String[] a_device_ip = null;
		String[] a_msg1 = null;   // �˶��޼���
		String[] a_etc1 = null;  //�˶����
		String[] a_etc2 = null;  //value_id
		String[] a_etc3 = null;  //01
		String[] a_last_updated = null;
		String[] a_use_yn = null;
		String[] a_device_seq = null;
		String[] a_sms_yn = null;
		String[] a_device_gu1 = null;
		String[] a_device_nm1 = null;
		String[] a_device_local = null;
		String[] a_device_local_nm = null;
		String[] a_process_nm = null;
		String[] a_chk_yn = null;
		
		//ErrorLogger.debug(Thread.currentThread().getName() + " process check start");
		//Logger.write("processCheckdb", Thread.currentThread().getName(), "process check ����", "", "", "", "");

		SQLParam sqlParam = new SQLParam();
		try{
			sqlParam.clear();
			//ErrorLogger.info("telecaps.sql.processalarm.db.list");
			sqlParam.setSqlName("telecaps.sql.processalarm.db.list");  // �˶���� ���� ���μ������ �ִ��� Ȯ��
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			if (sqlResult.getCount() > 0) {
				a_device_id = new String[sqlResult.getCount()];
				a_process_nm = new String[sqlResult.getCount()];
				a_device_ip = new String[sqlResult.getCount()];
				a_msg1 =new String[sqlResult.getCount()];
				a_etc1 = new String[sqlResult.getCount()];
				a_etc2 = new String[sqlResult.getCount()];
				a_etc3 = new String[sqlResult.getCount()];
				a_last_updated = new String[sqlResult.getCount()];
				a_use_yn = new String[sqlResult.getCount()];
				a_device_seq = new String[sqlResult.getCount()];
				a_sms_yn = new String[sqlResult.getCount()];
				a_device_gu1 = new String[sqlResult.getCount()];
				a_device_nm1 = new String[sqlResult.getCount()];
				a_device_local = new String[sqlResult.getCount()];
				a_device_local_nm = new String[sqlResult.getCount()];
				a_chk_yn = new String[sqlResult.getCount()];
				for (int i = 0; i < sqlResult.getCount(); i++) {  // ���μ��� �˶���� ���̺� ����
					a_device_id[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_ID");
					a_process_nm[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("PROCESS_NM");
					a_device_ip[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_IP");
					a_msg1[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("MSG1");
					a_etc1[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("ETC1");
					a_etc2[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("ETC2");
					a_etc3[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("ETC3");
					a_last_updated[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("LAST_UPDATED");
					a_use_yn[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("USE_YN");
					a_device_seq[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_SEQ");
					a_sms_yn[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("SMS_YN");
					a_device_gu1[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_GU1");
					a_device_nm1[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_NM1");
					a_device_local[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_LOCAL");
					a_device_local_nm[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("DEVICE_LOCAL_NM");
					a_chk_yn[i] = sqlResult.getListParam("telecaps.res.processalarm.db.list").getParam(i).getString("CHK_YN");
					
					//--------------------------------------------------------------------------------------------------
					device_id = a_device_id[i];
					device_ip = a_device_ip[i];
					alarm_msg = a_msg1[i];
					alarm_grade = a_etc1[i];
					sms_yn = a_sms_yn[i];
					device_gu1 = a_device_gu1[i];
					device_nm1 = a_device_nm1[i];
					device_gu2 = "";
					device_nm2 = "";
					device_local = a_device_local[i];
					device_local_nm = a_device_local_nm[i];
					
					getDeviceInfo(a_device_id[i]);  //----- ����̽� �������� ��������
					
					SQLParam sqlParamOid = new SQLParam();
					sqlParamOid.clear();
					sqlParamOid.addValue("device_id", a_device_id[i]);
					sqlParamOid.addValue("process_nm", a_process_nm[i]);
					//ErrorLogger.info("telecaps.sql.processalarm.oid.list");
					sqlParamOid.setSqlName("telecaps.sql.processalarm.oid.list");  // �˶���� ���� ���μ����� ���� oid�� ��������
					SQLParam sqlResultOid = SQLServiceManager.getInstance().execute(sqlParamOid);
					
					String[] b_oid = null;
					String[] b_device_id = null;
					String[] b_process_nm =null;
					String[] b_sub_oid = null;
					if(sqlResultOid.getCount() >0) {
						b_oid=new String[sqlResultOid.getCount()];
						b_device_id=new String[sqlResultOid.getCount()];
						b_process_nm=new String[sqlResultOid.getCount()];
						b_sub_oid=new String[sqlResultOid.getCount()];
						
						// ------------------ GET������� �� OID���� snmp��� ������ --------------------
						OctetString AUTH=new OctetString(authname);  // Ÿ����� ���Ӹ�
		            	OctetString AUTH_PASS=new OctetString(authpass); // Ÿ����� ����PASS
			       	   	OctetString securityName =null;
			       	   	securityName=AUTH;
			       	   	int securityLevel = SecurityLevel.AUTH_NOPRIV;
			       	   	String IP=a_device_ip[i];
			       	   	//int PORT=deviceInfo.getPort();
			       	   	Address address = GenericAddress.parse("udp:"+IP+"/161");
			       		UserTarget myTarget = new UserTarget();
				 	    myTarget.setAddress(address);
				 	    myTarget.setVersion(SnmpConstants.version3);// org.snmp4j.mp.*;
				 	    myTarget.setSecurityLevel(securityLevel);
				 	    myTarget.setSecurityName(securityName);
						
						for(int j =0; j<sqlResultOid.getCount(); j++) {  // ���μ����� �ش��ϴ� OID����
							b_oid[j] = sqlResultOid.getListParam("telecaps.res.processalarm.oid.list").getParam(j).getString("oid");
							b_device_id[j] = sqlResultOid.getListParam("telecaps.res.processalarm.oid.list").getParam(j).getString("device_id");
							b_process_nm[j] = sqlResultOid.getListParam("telecaps.res.processalarm.oid.list").getParam(j).getString("process_nm");
							b_sub_oid[j] = sqlResultOid.getListParam("telecaps.res.processalarm.oid.list").getParam(j).getString("sub_oid");
							
							
					 	    
					 	    ScopedPDU pdu = new ScopedPDU();
						    pdu.setType(PDU.GET);
						    // PDU
						    OID rootOID = new OID(b_oid[j]);
						    pdu.add( new VariableBinding(rootOID) );
						    Snmp snmp = null;
						    DefaultUdpTransportMapping utm = null;
						    try {
							    
							    //utm = new DefaultUdpTransportMapping();
							    snmp = createSnmpSession(AUTH, AuthMD5.ID, AUTH_PASS, null, null);
							    snmp.listen();
							    //ErrorLogger.info("=========== snmp get listen start ========");
							    ResponseEvent response = snmp.get(pdu, myTarget);
							    PDU resPdu = response.getResponse();
							    VariableBinding var = resPdu.get(0);
						        
						        OID currentOID = var.getOid();
						        //ErrorLogger.info("get ��Ű� )"+currentOID + " ---> " + var.getVariable().toString());
						        if(var.getVariable().toString() == "Null" || var.getVariable().toString()== "noSuchInstance" || var.getVariable().toString()== "" ) {  // �ش� OID���� ���� ���� ��� ���
						        	insertAlarmMsg();
						        	updateChkYN(a_device_seq[i]);  // üũ�ϰ� t_alarm_msg_process ���̺? chk_yn ������Ʈ
						        }else {
						        	updateChkYN(a_device_seq[i]);  // üũ�ϰ� t_alarm_msg_process ���̺? chk_yn ������Ʈ
						        }
						        
						        //snmp.close();
						    } catch (IOException e) {
						      e.printStackTrace();
						    }finally {
						    	try{
						    		if(snmp != null) {
						    			snmp.close();
						    		}
						    	}catch (Exception e) {
						    		
						        }
						    }
						    
						}  // End for
						
					}  // End if
					
					
				}   // End for
			}  // End if
			
		}catch(Exception e) {
			ErrorLogger.error(e);
		}
		//ErrorLogger.debug(Thread.currentThread().getName() + " process check end");
		
	}  // End run method
	
	public void getDeviceInfo(String getdevice_id) {
		
		SQLParam sqlParam = new SQLParam();
		SQLParam sqlResult = new SQLParam();
		
		try{
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.deviceinfo.select");
			sqlParam.addValue("device_id", getdevice_id);
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if(sqlResult.getCount() > 0) {
				this.authname = sqlResult.getListParam("telecaps.res.deviceinfo.select").getParam(0).getString("auth_name");
				this.authpass = sqlResult.getListParam("telecaps.res.deviceinfo.select").getParam(0).getString("auth_pass");
				
			}
			
		}catch (Exception e) {
			ErrorLogger.error("getDeviceInfo " + e);
		}
		
	}
	
	public void insertAlarmMsg() {
		String same_msg_yn = "N";
		String full_alarm_msg = "";
		String use_yn = "N";

		SQLParam sqlParam = new SQLParam();
		SQLParam sqlResult = new SQLParam();

		full_alarm_msg = alarm_msg;

		try {
			ListParam lpSchValueAlarm = new ListParam(new String[] {
					"device_id", "device_ip", "alarm_msg", "alarm_grade",
					"maxnum", "sms_yn", "s2o_yn", "device_gu1",
					"device_nm1", "device_gu2", "device_nm2",
					"device_local", "device_local_nm" , "use_yn" });

			lpSchValueAlarm.addRow(new Object[] { device_id, device_ip,
					full_alarm_msg, alarm_grade, null, sms_yn, "N",
					 device_gu1, device_nm1, device_gu2,
					device_nm2, device_local, device_local_nm , use_yn });

			sqlParam.clear();
			//ErrorLogger.info("=========== Process Check telecaps.sql.tcagnt.alarm.insert.add USE_YN : "+use_yn);
			//ErrorLogger.info("Process Check telecaps.sql.tcagnt.alarm.insert.add");
			sqlParam.setSqlName("telecaps.sql.tcagnt.alarm.insert.add");
			sqlParam.addValue("telecaps.sql.tcagnt.alarm.insert.add", lpSchValueAlarm);
			SQLServiceManager.getInstance().execute(sqlParam);

		} catch (Exception e) {
			ErrorLogger.error("[insertAlarmMsg] " + e);
		}
		
		
	}
	
	public  Snmp createSnmpSession(OctetString securityName, OID authProtocol, OctetString authPass,OID privacyProtocol, OctetString privacyPass) throws IOException {
		
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
		   
		 try {
			   
			   //USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
			 USM usm = USMFactory.getInstance().getUSM();
			 
			 usm.setLocalEngine(new OctetString(MPv3.createLocalEngineID()), 0, usm.getEngineTime());
			   SecurityModels.getInstance().addSecurityModel(usm);
			   UsmUser user = new UsmUser(securityName,authProtocol, authPass,privacyProtocol, privacyPass);
			   snmp.getUSM().addUser(securityName, user);
			   
			   return snmp;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("err : "+e);
			return snmp;
		} 
	}
	
	public void updateChkYN(String device_seq) {
		SQLParam sqlParam = new SQLParam();
		
		try{
			sqlParam.clear();
			//ErrorLogger.info("telecaps.sql.processalarm.upd");
			sqlParam.setSqlName("telecaps.sql.processalarm.upd");
			sqlParam.addValue("device_seq", device_seq);
			SQLServiceManager.getInstance().execute(sqlParam);
		}catch(Exception e) {
			ErrorLogger.error("[telecaps.sql.processalarm.upd] " + e);
		}
		
	}
	
	// �˶� ����
	public void insertAlarmBackMsg(String t_device_id) {
			
			
			
	}
	
}
