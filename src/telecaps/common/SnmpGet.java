package telecaps.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.opennms.protocols.snmp.SnmpObjectId;
import org.opennms.protocols.snmp.SnmpParameters;
import org.opennms.protocols.snmp.SnmpPduPacket;
import org.opennms.protocols.snmp.SnmpPduRequest;
import org.opennms.protocols.snmp.SnmpPeer;
import org.opennms.protocols.snmp.SnmpSMI;
import org.opennms.protocols.snmp.SnmpSession;
import org.opennms.protocols.snmp.SnmpVarBind;
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
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;



public class SnmpGet
{
	//private static final int TIME_OUT = 5000;
	private static final int TIME_OUT = 40000;
	private static final int RETRY    = 1;

	//private final Blocking_queue m_queue;
	private final BlockingQueue<CapsDBValue> m_queue;
	private final ExecutorService threadExecutor;

	public SnmpGet(BlockingQueue<CapsDBValue> q)
    {
    	m_queue = q;
    	threadExecutor = null;
    }

	//������ �и�
	public SnmpGet(BlockingQueue<CapsDBValue> q, ExecutorService threadEx)
    {
    	m_queue = q;
    	threadExecutor = threadEx;
    }

	
    /**
     * Device ������ ��ȸ�Ѵ�.
     *
     * @param aDeviceId         ��� ID
     * @return CapsDeviceInfo   ��� IP, ��� ��Ʈ, Read community, SNMP ����
     */
	private CapsDeviceInfo getDeviceInfo(String aDeviceId)
	{
		CapsDeviceInfo deviceInfo = null;
		
		try
		{
			SQLParam sqlParam = new SQLParam();
			
			sqlParam.setSqlName("telecaps.srhDeviceInfo");
			sqlParam.addValue("device_id", aDeviceId);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if( sqlResult.getCount() > 0 )
			{
				Param param = sqlResult.getListParam("telecaps.srhDeviceInfo").getParam(0);
				
				deviceInfo = new CapsDeviceInfo();
				
				deviceInfo.setHost( param.getString("DEVICE_IP") );
				deviceInfo.setPort( param.getInt("DEVICE_PORT")  );
				deviceInfo.setCommunity( param.getString("READ_COMMUNITY") );
				String version = param.getString("SNMP_VERSION");
				deviceInfo.setVersion( (version.equals("v2") || version.equals("v2c")) ? SnmpSMI.SNMPV2 :  SnmpSMI.SNMPV1 );
			}
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	ErrorLogger.error("getDeviceInfo ::: " + aDeviceId + ":::" + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(e));
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	ErrorLogger.error("getDeviceInfo ::: " + aDeviceId + ":::" + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(ex));
        }

		return deviceInfo;
	}


    /**
     * Phone Device ������ ��ȸ�Ѵ�.
     *
     * @param aDeviceId         ��� ID
     * @return CapsDeviceInfo   ��� IP, ��� ��Ʈ, Read community, SNMP ����
     */
	
	private CapsDeviceInfo getPhoneDeviceInfo(String aDeviceId)
	{
		CapsDeviceInfo deviceInfo = null;
		
		try
		{
			SQLParam sqlParam = new SQLParam();
			
			sqlParam.setSqlName("telecaps.srhPhoneDeviceInfo");
			sqlParam.addValue("device_id", aDeviceId);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if( sqlResult.getCount() > 0 )
			{
				Param param = sqlResult.getListParam("telecaps.srhPhoneDeviceInfo").getParam(0);
				
				deviceInfo = new CapsDeviceInfo();
				
				deviceInfo.setHost( param.getString("DEVICE_IP") );
				deviceInfo.setPort( param.getInt("DEVICE_PORT")  );
				deviceInfo.setCommunity( param.getString("READ_COMMUNITY") );
				String version = param.getString("SNMP_VERSION");
				deviceInfo.setVersion( (version.equals("v2") || version.equals("v2c")) ? SnmpSMI.SNMPV2 : SnmpSMI.SNMPV1 );
			}
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }

		return deviceInfo;
	}

	/**
	 * OID ����Ʈ�� ��ȸ�Ѵ�
     *
	 * @param aDeviceId     Device ID
	 * @param aOidType      OID Ÿ��
	 * @param aOidGroup     OID �׷�
	 * @return SnmpOidList  OID ����Ʈ (OID �̸�, OID)
	 */

	private SnmpOidList getOidList(String aDeviceId, String aOidType, String aOidGroup)
	{
		SnmpOidList oidList = null;
		
		try
		{
			SQLParam sqlParam = new SQLParam();
			
			sqlParam.setSqlName("telecaps.srhOid");
			sqlParam.addValue("device_id", aDeviceId);
			sqlParam.addValue("oid_type",  aOidType);
			sqlParam.addValue("oid_group", aOidGroup);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if( sqlResult.getCount() > 0 )
			{
				oidList = new SnmpOidList();
				ListParam listParam = sqlResult.getListParam("telecaps.srhOid");
				
				for( int i = 0; i < listParam.rowSize(); i++ )
				{
					Param param = listParam.getParam( i );
					oidList.add( param.getString("OID_NAME"), param.getString("OID") );
				}
			}
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	ErrorLogger.error("getOidList ::: " + aDeviceId + ":::" + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(e));
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	ErrorLogger.error("getOidList ::: " + aDeviceId + ":::" + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(ex));
        }

		return oidList;
	}

	
	
    /**
     * (�����ٿ�) ��� ����Ʈ�� ��ȸ�Ѵ�.
     *
     * @return CapsDeviceList ��� ����Ʈ
     */
    private CapsDeviceList getScheduledDeviceList()
    {
    	CapsDeviceList deviceList = null;
    	
    	try
    	{
    		SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.srhScheduledDevice");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		deviceList = new CapsDeviceList();
	    		ListParam listParam = sqlResult.getListParam("telecaps.srhScheduledDevice");
	    		
	    		for( int i = 0; i < sqlResult.getCount(); i++ )
	    		{
	    			Param param = listParam.getParam( i );
	    			deviceList.add( param.getString("DEVICE_ID"), param.getString("OID_TYPE"), param.getString("OID_GROUP") );
	    		}
    		}

        	//ErrorLogger.debug("snmpget getScheduledDeviceList ::: " + Thread.currentThread().getName() );
    	}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	ErrorLogger.error("snmpget getScheduledDeviceList ::: " + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(e));
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	ErrorLogger.error("snmpget getScheduledDeviceList ::: " + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(ex));
        }
		
		return deviceList;
    }
	

    /**
     * (�����ٿ�) ��� ����Ʈ�� ��ȸ�Ѵ�.
     *
     * @return CapsDeviceList ��� ����Ʈ
     */
    private CapsDeviceList getRealScheduledDeviceList()
    {
    	CapsDeviceList deviceList = null;
    	
    	try
    	{
    		SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.sql.srhRealScheduledDevice");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		deviceList = new CapsDeviceList();
	    		ListParam listParam = sqlResult.getListParam("telecaps.res.srhRealScheduledDevice");
	    		
	    		for( int i = 0; i < sqlResult.getCount(); i++ )
	    		{
	    			Param param = listParam.getParam( i );
	    			deviceList.add( param.getString("DEVICE_ID"), param.getString("OID_TYPE"), param.getString("OID_GROUP") );
	    		}
    		}
    		
    		//ErrorLogger.debug("snmpget getRealScheduledDeviceList ::: " + Thread.currentThread().getName());
    	}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	ErrorLogger.error("snmpget getRealScheduledDeviceList ::: " + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(e));
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	ErrorLogger.error("snmpget getRealScheduledDeviceList ::: " + Thread.currentThread().getName() + ":::" + Logger.getStackTrace(ex));
        }
		
		return deviceList;
    }
    
    //��ȯ�� ���-C
    private CapsDeviceList getCmScheduledDeviceList()
    {
    	CapsDeviceList deviceList = null;
    	
    	try
    	{
    		SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.sql.srhCmScheduledDevice");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		deviceList = new CapsDeviceList();
	    		ListParam listParam = sqlResult.getListParam("telecaps.res.srhCmScheduledDevice");
	    		
	    		for( int i = 0; i < sqlResult.getCount(); i++ )
	    		{
	    			Param param = listParam.getParam( i );
	    			deviceList.add( param.getString("DEVICE_ID"), param.getString("OID_TYPE"), param.getString("OID_GROUP") );
	    		}
    		}
    		
    		//Logger.write("snmpget",Thread.currentThread().getName(),"getCmScheduledDeviceList ����","","","","");
    	}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	//Logger.write("snmpget",Thread.currentThread().getName(),Logger.getStackTrace(e),"","","","");
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	//Logger.write("snmpget",Thread.currentThread().getName(),Logger.getStackTrace(ex),"","","","");
        }
		
		return deviceList;
    }

  //��ȯ�� ���-M,H,P
    private CapsDeviceList getCmNonCScheduledDeviceList()
    {
    	CapsDeviceList deviceList = null;
    	
    	try
    	{
    		SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.sql.srhCmNonCScheduledDevice");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		deviceList = new CapsDeviceList();
	    		ListParam listParam = sqlResult.getListParam("telecaps.res.srhCmNonCScheduledDevice");
	    		
	    		for( int i = 0; i < sqlResult.getCount(); i++ )
	    		{
	    			Param param = listParam.getParam( i );
	    			deviceList.add( param.getString("DEVICE_ID"), param.getString("OID_TYPE"), param.getString("OID_GROUP") );
	    		}
    		}
    	}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
		
		return deviceList;
    }
    
    //��ȯ�� ���-M,H,P
    private CapsDeviceList getAesScheduledDeviceList()
    {
    	CapsDeviceList deviceList = null;
    	
    	try
    	{
    		SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.sql.srhAesScheduledDevice");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		deviceList = new CapsDeviceList();
	    		ListParam listParam = sqlResult.getListParam("telecaps.sql.srhAesScheduledDevice");
	    		
	    		for( int i = 0; i < sqlResult.getCount(); i++ )
	    		{
	    			Param param = listParam.getParam( i );
	    			deviceList.add( param.getString("DEVICE_ID"), param.getString("OID_TYPE"), param.getString("OID_GROUP") );
	    		}
    		}
    	}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
		
		return deviceList;
    }
    
	/**
	 * (�����ٿ�) �����͸� �����Ѵ�
     *
	 * @return boolean �����
	 */
    
	public boolean getScheduled()
	{
		boolean success = false;
		
		try
		{
			// DeviceId, OidType, OidGroup ����Ʈ
			CapsDeviceList deviceList = getScheduledDeviceList();
			
			if( deviceList != null && deviceList.getSize() > 0 )
			{
				for( int i = 0; i < deviceList.getSize(); i++ )
				{
					String strDeviceId = deviceList.getDeviceId(i);
					String strOidType  = deviceList.getOidType(i);
					String strOidGroup = deviceList.getOidGroup(i);
					
                    //if( (success = get(strDeviceId, strOidType, strOidGroup)) == false )
                    //    break;
				}
			}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		
		return success;
	}
	
    
	public boolean getRealScheduled()
	{
		boolean success = false;
		
		try
		{
			// DeviceId, OidType, OidGroup ����Ʈ
			CapsDeviceList deviceList = getRealScheduledDeviceList();
			
			if( deviceList != null && deviceList.getSize() > 0 )
			{
				for( int i = 0; i < deviceList.getSize(); i++ )
				{
					String strDeviceId = deviceList.getDeviceId(i);
					String strOidType  = deviceList.getOidType(i);
					String strOidGroup = deviceList.getOidGroup(i);
					
                    //if( (success = get(strDeviceId, strOidType, strOidGroup)) == false )
                    //    break;
			        threadExecutor.execute(new SnmpGetThread(m_queue, strDeviceId, strOidType, strOidGroup)); // start task3
					
				}
				
			}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		
		return success;
	}
	
	public boolean getCmScheduled()
	{
		boolean success = false;
		
		try
		{
			// DeviceId, OidType, OidGroup ����Ʈ
			CapsDeviceList deviceList = getCmScheduledDeviceList();
			
			if( deviceList != null && deviceList.getSize() > 0 )
			{
				for( int i = 0; i < deviceList.getSize(); i++ )
				{
					String strDeviceId = deviceList.getDeviceId(i);
					String strOidType  = deviceList.getOidType(i);
					String strOidGroup = deviceList.getOidGroup(i);
					
					success = get(strDeviceId, strOidType, strOidGroup);
                    //if( (success = get(strDeviceId, strOidType, strOidGroup)) == false )
                    //    break;
				}
			}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		
		return success;
	}

	public boolean getCmNonCScheduled()
	{
		boolean success = false;
		
		try
		{
			// DeviceId, OidType, OidGroup ����Ʈ
			CapsDeviceList deviceList = getCmNonCScheduledDeviceList();
			
			if( deviceList != null && deviceList.getSize() > 0 )
			{
				for( int i = 0; i < deviceList.getSize(); i++ )
				{
					String strDeviceId = deviceList.getDeviceId(i);
					String strOidType  = deviceList.getOidType(i);
					String strOidGroup = deviceList.getOidGroup(i);
					
					success = get(strDeviceId, strOidType, strOidGroup);
                    //if( (success = get(strDeviceId, strOidType, strOidGroup)) == false )
                    //    break;
				}
			}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		
		return success;
	}
	
	public boolean getAesScheduled()
	{
		boolean success = false;
		
		try
		{
			// DeviceId, OidType, OidGroup ����Ʈ
			CapsDeviceList deviceList = getAesScheduledDeviceList();
			
			if( deviceList != null && deviceList.getSize() > 0 )
			{
				for( int i = 0; i < deviceList.getSize(); i++ )
				{
					String strDeviceId = deviceList.getDeviceId(i);
					String strOidType  = deviceList.getOidType(i);
					String strOidGroup = deviceList.getOidGroup(i);
					
					success = get(strDeviceId, strOidType, strOidGroup);
					
                    //if( (success = get(strDeviceId, strOidType, strOidGroup)) == false )
                    //    break;
				}
			}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		
		return success;
	}
	
	
	public boolean isCheckSummary()
	{
		boolean ischeck = false;
		
		try
		{
			SQLParam sqlParam = new SQLParam();
  			sqlParam.setSqlName("telecaps.sql.isCheckSummay");
    		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
    		
    		if( sqlResult.getCount() > 0)
    		{
	    		ListParam listParam = sqlResult.getListParam("telecaps.sql.isCheckSummay");
	    		if(listParam.getParam(0).getInt("CNT") > 0){
	    			ischeck = true;
	    		}
    		}
		}
        catch(Exception e)
        {
        	ErrorLogger.error(e);
        }		
		return ischeck;
	}
	
	public void insSummary()
	{
		try
		{
			JediTransaction tran = JediTransactionManager.getJediTransaction();
			SQLParam sqlParam = new SQLParam();
			
			// T_SUM_CPU_HOUR
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValue.cpu");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();

            // T_SUM_USAGE_HOUR
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValue.usage");
            SQLServiceManager.getInstance().execute(sqlParam);			
            tran.commit();
            
            // T_SUM_AGNT_HOUR
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValue.agent");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
            
            // T_SUM_CPU_DAY
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValueDay.cpu");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
            
            // T_SUM_USAGE_DAY
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValueDay.usage");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
            
            // T_SUM_AGNT_DAY
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.insSumValueDay.agent");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
	}
	
	public void delOldValue()
	{
		try
		{
			JediTransaction tran = JediTransactionManager.getJediTransaction();
			SQLParam sqlParam = new SQLParam();
			
			// T_VALUE, T_VALUE_CALL, T_VALUE_HOUR, T_VALUE_MIN, T_VALUE_USAGE 2���� ������ ����
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.sql.sp_del_old_value.proc");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
	}

	public void delOldAgntValue()
	{
		try
		{
			JediTransaction tran = JediTransactionManager.getJediTransaction();
			SQLParam sqlParam = new SQLParam();
			
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.sql.sp_del_old_agnt_value.proc");
            SQLServiceManager.getInstance().execute(sqlParam);
            tran.commit();
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
	}

	public void syncUserInfo(){
		try
		{
			String[] r_usr_id = null;
			String[] r_usr_nm = null;
			String[] r_center_id = null;
			String[] r_group_id = null;
			String[] r_team_id = null;
			String[] r_part_id = null;
			String[] auth_menu = null;
			
			ListParam lpSchValue = null;
			
			JediTransaction tran = JediTransactionManager.getJediTransaction();
			SQLParam sqlParam = new SQLParam();
			SQLParam sqlResult = new SQLParam();
			
            sqlParam.clear();
            sqlParam.setSqlName("telecaps.sql.rec.syncUserInfo.del");
            SQLServiceManager.getInstance().execute(sqlParam);
            
            tran.commit();
            
            sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.rec.syncUserInfo.sel");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			tran.commit();
			
			if (sqlResult.getCount() > 0) {
				r_usr_id = new String[sqlResult.getCount()];
				r_usr_nm = new String[sqlResult.getCount()];
				r_center_id = new String[sqlResult.getCount()];
				r_group_id = new String[sqlResult.getCount()];
				r_team_id = new String[sqlResult.getCount()];
				r_part_id = new String[sqlResult.getCount()];
				auth_menu = new String[sqlResult.getCount()];
				
				lpSchValue = new ListParam(new String[] { "r_usr_id", "r_usr_nm",
						"r_center_id", "r_group_id", "r_team_id",
						"r_part_id", "auth_menu"});
				
				for (int i = 0; i < sqlResult.getCount(); i++) {
					r_usr_id[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_usr_id").trim();
					
					if(r_usr_id[i].indexOf("0") == 0){
						r_usr_id[i] = r_usr_id[i].substring(1);
					}
					
					r_usr_nm[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_usr_nm").trim();
					r_center_id[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_center_id").trim();
					r_group_id[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_group_id").trim();
					r_team_id[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_team_id").trim();
					r_part_id[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("r_part_id").trim();
					auth_menu[i] = sqlResult.getListParam("telecaps.sql.rec.syncUserInfo.sel").getParam(i).getString("auth_menu").trim();
					
					lpSchValue.addRow(new Object[] { r_usr_id[i], r_usr_nm[i],
							r_center_id[i], r_group_id[i],
							r_team_id[i], r_part_id[i],
							auth_menu[i] });
				}
				
				sqlParam.clear();
				sqlParam.setSqlName("telecaps.sql.rec.syncUserInfo.insert");
				sqlParam.addValue("telecaps.sql.rec.syncUserInfo.insert", lpSchValue);
				SQLServiceManager.getInstance().execute(sqlParam);
				
				tran.commit();
				
				sqlParam.clear();
	            sqlParam.setSqlName("telecaps.sql.rec.syncUserInfo.proc");
	            SQLServiceManager.getInstance().execute(sqlParam);
	            
	            tran.commit();
			}
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        }
	}
	
	public static boolean isSubTree(OID rootOID, OID currentOID) {
	    boolean b = true;
	    /**
	     * Compares the n leftmost sub-identifiers with the given OID in left-to-right direction.
	     * 0 if the first n sub-identifiers are the same.
	     * <0 if the first n sub-identifiers of this OID are lexicographic less than those of the comparand.
	     * >0 if the first n sub-identifiers of this OID are lexicographic greater than those of the comparand
	     */
	    if ( rootOID.leftMostCompare(rootOID.size(), currentOID) != 0 ) {
	      b = false;
	    }
	    return b;
	  }
	public static Snmp createSnmpSession(OctetString securityName,
			   OID authProtocol, OctetString authPass,
			   OID privacyProtocol, OctetString privacyPass) throws IOException {
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
		   
		 try {
			   
			   USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
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
	
	/**
	 * Device ������ OID ����Ʈ�� ��ȸ�Ͽ� OID ���� ��� DB�� �����Ѵ�
     *
	 * @param strDeviceId   Device ID
     * @param strOidType    OID Ÿ��
     * @param strOidGroup   OID �׷�
	 * @return boolean      �����
	 */

    public boolean get(String strDeviceId, String strOidType, String strOidGroup)
    {
		boolean success = true;
		
		try
		{
            // Device ����
            CapsDeviceInfo deviceInfo = null;
            
            if( strOidType.equals("P") == true )
                deviceInfo = getPhoneDeviceInfo(strDeviceId);
            else
                deviceInfo = getDeviceInfo(strDeviceId);

            // OID ����Ʈ
            SnmpOidList oidList = getOidList(strDeviceId, strOidType, strOidGroup);
            
            if( deviceInfo == null )
            {
                ErrorLogger.error( strDeviceId + " info not found" );
                return false;
            }
            
            if( oidList == null || oidList.getCount() == 0 )
            {
                ErrorLogger.error( strDeviceId + " oid not found" );
                return false;
            }
            
            InetAddress remote = null;
            
            try
            {
                remote = InetAddress.getByName(deviceInfo.getHost());
            }
            catch (UnknownHostException e)
            {
                ErrorLogger.error(e);
                return false;
            }
            if(deviceInfo.getVersion() == 3) {
            	ErrorLogger.debug("========================== SNMP V3 START ==========================");
            	OctetString AUTH=new OctetString(deviceInfo.getAuthname());  // Ÿ����� ���Ӹ�
            	OctetString AUTH_PASS=new OctetString(deviceInfo.getAuthpass()); // Ÿ����� ����PASS
	       	   	OctetString securityName =null;
	       	   	securityName=AUTH;
	       	   	int securityLevel = SecurityLevel.AUTH_NOPRIV;
	       	   	String IP=deviceInfo.getHost();
	       	   	int PORT=deviceInfo.getPort();
	       	   	Address address = GenericAddress.parse("udp:"+IP+"/"+PORT);
	       	 
	       	   	UserTarget myTarget = new UserTarget();
		 	    myTarget.setAddress(address);
		 	    myTarget.setVersion(SnmpConstants.version3);// org.snmp4j.mp.*;
		 	    myTarget.setSecurityLevel(securityLevel);
		 	    myTarget.setSecurityName(securityName);
		 	    // PDU
		 	    
		 	    
		 	    SnmpValue snmpValue = new SnmpValue();
		 	    SnmpOidValueList snmpOidValueList = new SnmpOidValueList();
		 	   	snmpValue.setDeviceId(strDeviceId);
	            snmpValue.setOidType(strOidType);
	            snmpValue.setOidGroup(strOidGroup);
	           
		 	    Snmp snmp=null;
		 	    
		 	    try{
		 	    	//ErrorLogger.debug("========== SNMP CREATE ==============");
		 		    snmp = createSnmpSession(AUTH, AuthMD5.ID, AUTH_PASS, null, null);
		 		    snmp.listen();
		 	    	for(int k=0; k<oidList.getCount(); k++) {
		 	    		//ErrorLogger.debug("====================OIDLIST COUNT : "+oidList.getCount());
		 	    		ScopedPDU pdu = new ScopedPDU();
				 	    pdu.setType(PDU.GETNEXT);
		 	    		OID rootOID = new OID(oidList.getOid(k));
		 	    		pdu.add( new VariableBinding(rootOID) );
		 	    		//ErrorLogger.debug("================RootOID : "+rootOID);
		 	    		ResponseEvent response = null;
		 	    		PDU resPdu=null;
		 	    		VariableBinding var = null;
		 	    		OID currentOID = null;
		 	    		while(true){
			 	    		response = snmp.getNext(pdu, myTarget);
			 		        // PDU
			 		        resPdu = response.getResponse();
			 		        if ( resPdu != null && resPdu.getErrorStatus() == SnmpConstants.SNMP_ERROR_SUCCESS ) {
			 		        	 var = resPdu.get(0);
			 		        	//ErrorLogger.debug("================== var result : "+var.getVariable().toString());
			 			         currentOID = var.getOid();
			 			         
			 			         if( !var.isException()) {
			 			        	 //ErrorLogger.debug("================= rootOID : "+rootOID+" / currentOID : "+currentOID);
			 			        	if ( isSubTree(rootOID, currentOID) ) {
			 				             //System.out.println(currentOID + " -> " + var.getVariable().toString());
			 			        		//ErrorLogger.debug(currentOID + " =>>>>>>>>> " + var.getVariable().toString());
			 				             snmpOidValueList.addOidValue2(currentOID, var.getVariable().toString());
			 				             //ErrorLogger.debug("=================== addOID after =======================");
			 				             pdu = new ScopedPDU();
			 				             pdu.setType(PDU.GETNEXT);
			 				             pdu.addOID( new VariableBinding(currentOID) );
			 				        }else {
			 				        	ErrorLogger.error("================ SubTree END ================");
			 				        	break;
			 				        }
			 			        	
			 			         }else{
			 			        	 ErrorLogger.error(currentOID + " ->>> " + "has exception syntax!");
			 			        	success=false;
			 			        	 break;
			 			         }
			 		        }else {
			 		        	//break;
			 		        	if (resPdu == null) {
						            
					 	    		ErrorLogger.error(">>>>>>>request timeout<<<<<<<<<");
						        } else {
						        	ErrorLogger.error(resPdu.getErrorStatus()+" // "+resPdu.getErrorStatusText());
						        }
			 		        	success=false;	
						          break;
			 		        }
		 	    	   }   // while End 
		 	    		
		 		       //ErrorLogger.debug("================= BEFORE addOIDLIST ==================");
		 		       if(snmpOidValueList.getSize() > 0){
			 	    		snmpOidValueList.setRootOid(rootOID);
				            snmpOidValueList.setOidName(oidList.getOidName(k));
				            snmpValue.addOidValueList(snmpOidValueList);
			 	    	}
			 	    	snmpOidValueList.clear();
		 		       
			 	    	
		 	    	}  // for End
		 	    	snmp.close();
		 	    	 if( snmpValue.getOidValueListSize() > 0 ) {
		 	    		//ErrorLogger.debug("========== BEFORE CapsDBValue ==========");
		 	    		 m_queue.put( new CapsDBValue(CapsDBValue.DB_VALUE_OID, new SnmpValue(snmpValue)) );
		 	    	 }
		 	    	
		 	    }catch(IOException e) {
		 	    	success=false;
		 	    	e.printStackTrace();
		 	    }
            	
            }else {  //================================= SNMP V1, V2c �� ��� 
	            // Initialize the peer
	            SnmpPeer peer = new SnmpPeer(remote);
	            
	            peer.setPort(deviceInfo.getPort());
	            
	            //ErrorLogger.error("getTimeout:"+deviceInfo.getTimeout()+"!getRetry:"+deviceInfo.getRetry());
	            
	            if( deviceInfo.getTimeout() == -1 )
	                peer.setTimeout(TIME_OUT);
	            else
	                peer.setTimeout(deviceInfo.getTimeout());
	            
	            if( deviceInfo.getRetry() == -1 )
	                peer.setRetries(RETRY);
	            else
	                peer.setRetries(deviceInfo.getRetry());        
	
	            SnmpParameters parms = peer.getParameters();
	            parms.setVersion(deviceInfo.getVersion());
	            parms.setReadCommunity(deviceInfo.getCommunity()); 
	            
	            SnmpSession session = null;
	            try
	            {
	                session = new SnmpSession(peer);
	            }
	            catch (SocketException e)
	            {
	                ErrorLogger.error(e);
	                return false;
	            }        
	
	            SnmpValue snmpValue = new SnmpValue();
	            SnmpOidValueList snmpOidValueList = new SnmpOidValueList();
	            CapsSnmpHandler snmpHandler = new CapsSnmpHandler(snmpOidValueList);			        
	            session.setDefaultHandler(snmpHandler);
	
	            snmpValue.setDeviceId(strDeviceId);
	            snmpValue.setOidType(strOidType);
	            snmpValue.setOidGroup(strOidGroup);
	            //ErrorLogger.debug("SnmpGet 1-------------------------");
	            
	            try
	            {
	                //ErrorLogger.debug("SnmpGet oidList.getCount():"+oidList.getCount());
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet ����  OidGroup:"+strOidGroup,"","","","");
	                //Logger.write("snmpget",strDeviceId,Thread.currentThread().getName(),"SnmpGet ����  OidGroup:"+strOidGroup,"","","");
	                for( int j = 0; j < oidList.getCount(); j++ )
	                {
	                    //
	                    // set the stop point
	                    //
	                    SnmpObjectId id = new SnmpObjectId(oidList.getOid(j));
	                    int[] ids = id.getIdentifiers();
	                    ++ids[ids.length - 1];
	                    id.setIdentifiers(ids);
	                    snmpHandler.m_stopAt = id;
	                    
	                    //
	                    // send the first request
	                    //
	                    SnmpVarBind[] vblist = {new SnmpVarBind(oidList.getOid(j))};
	                    SnmpPduRequest pdu = new SnmpPduRequest(oidList.getOid(j).endsWith(".0") ? SnmpPduPacket.GET : SnmpPduPacket.GETNEXT, vblist);
	                    pdu.setRequestId(SnmpPduPacket.nextSequence());
	                    //ErrorLogger.debug("SnmpGet 1:1-------------------------");            
	                    
	                    synchronized (session)
	                    {
	                        session.send(pdu);
	                        session.wait();
	                    }
	                    
	                    //ErrorLogger.debug("SnmpGet snmpOidValueList.getSize():"+snmpOidValueList.getSize());
	                    //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet OID "+oidList.getOid(j)+" ���� �� ����:"+snmpOidValueList.getSize(),"","","","");
	                    if( snmpOidValueList.getSize() > 0 )
	                    {
	                        snmpOidValueList.setOidName(oidList.getOidName(j));
	                        snmpOidValueList.setRootOid(oidList.getOid(j));
	                        snmpValue.addOidValueList(snmpOidValueList);
	                    }
	                    snmpOidValueList.clear();
	                    
	                    Thread.sleep(3000);
	                }
	                
	                if( snmpValue.getOidValueListSize() > 0 )
	                    m_queue.put( new CapsDBValue(CapsDBValue.DB_VALUE_OID, new SnmpValue(snmpValue)) );
	
	                //ErrorLogger.debug("SnmpGet 2-------------------------");
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet ����","","","","");
	                //Logger.write("snmpget",strDeviceId,Thread.currentThread().getName(),"SnmpGet ����","","","");
	                
	                //Thread.sleep(3000);
	            }
	            catch (InterruptedException e)
	            {
	                success = false;
	                ErrorLogger.error(strDeviceId + "::" + e);
	            }
	            catch(Exception e)
	            {
	                success = false;
	                ErrorLogger.error(e);
	            }
	            finally
	            {
	                session.close();
	            }
			}  // SNMP V1, V2c END
		}
        catch(Exception e)
        {
            success = false;
        	ErrorLogger.error(e);
        }		
		
		return success;
    }

	
	/**
	 * Device ������ OID ����Ʈ�� ��ȸ�Ͽ� OID �����͸� ��´�
     *
	 * @param aDeviceInfo   Device ����
     * @param aOidList      OID ����Ʈ
	 * @return SnmpValue    OID ������
	 */
	public static SnmpValue get(CapsDeviceInfo aDeviceInfo, SnmpOidList aOidList)
	{
		SnmpValue snmpValue = new SnmpValue();
		SnmpOidValueList oidValueList = new SnmpOidValueList();
		CapsSnmpHandler snmpHandler = new CapsSnmpHandler(oidValueList);
        InetAddress remote = null;
        
        try
        {
            remote = InetAddress.getByName(aDeviceInfo.getHost());
        }
        catch (UnknownHostException e)
        {
        	ErrorLogger.error(e);
            return null;
        }
		
        //
        // Initialize the peer
        //
        SnmpPeer peer = new SnmpPeer(remote);
        
        peer.setPort(aDeviceInfo.getPort());
        if( aDeviceInfo.getTimeout() == -1 )
        	peer.setTimeout(TIME_OUT);
        else
        	peer.setTimeout(aDeviceInfo.getTimeout());
        
        if( aDeviceInfo.getRetry() == -1 )
        	peer.setRetries(RETRY);
        else
        	peer.setRetries(aDeviceInfo.getRetry());        

		SnmpParameters parms = peer.getParameters();
		parms.setVersion(aDeviceInfo.getVersion());
		parms.setReadCommunity(aDeviceInfo.getCommunity()); 
		
        SnmpSession session = null;
        try
        {
            session = new SnmpSession(peer);
        }
        catch (SocketException e)
        {
        	ErrorLogger.error(e);
            return null;
        }        
        
    	session.setDefaultHandler(snmpHandler);
        
        try
        {
	        for( int i = 0; i < aOidList.getCount(); i++ )
	        {
	        	if( aOidList.getOid(i) == null )
	        		continue;
	        	
		        //
		        // set the stop point
		        //
		        SnmpObjectId id = new SnmpObjectId(aOidList.getOid(i));
		        int[] ids = id.getIdentifiers();
		        ++ids[ids.length - 1];
		        id.setIdentifiers(ids);
		        snmpHandler.m_stopAt = id;

		        //
		        // send the first request
		        //
		        SnmpVarBind[] vblist = {new SnmpVarBind(aOidList.getOid(i))};
		        SnmpPduRequest pdu = new SnmpPduRequest(aOidList.getOid(i).endsWith(".0") ? SnmpPduPacket.GET : SnmpPduPacket.GETNEXT, vblist);
		        pdu.setRequestId(SnmpPduPacket.nextSequence());

	            synchronized (session)
	            {
	                session.send(pdu);
	                session.wait();
	            }
	        }
        }
        catch (InterruptedException e)
        {
        	ErrorLogger.error(e);
            return null;
        }
        finally
        {
            session.close();
        }
        
        snmpValue.addOidValueList(oidValueList);
        return snmpValue;
	}

	/**
	 * Device ������ OID ����Ʈ�� ��ȸ�Ͽ� OID �����͸� ��� ���̺� ���·� ��ȯ�Ѵ�
     *
	 * @param aDeviceInfo       Device ����
     * @param aOidList          OID ����Ʈ
	 * @return SnmpValueTable   OID ������ ���̺�
	 */
	public static SnmpValueTable getTable(CapsDeviceInfo aDeviceInfo, SnmpOidList aOidList)
	{
		/*ErrorLogger.debug("���������������������������������������������");
		ErrorLogger.debug("SnmpGet.getTable("+ aDeviceInfo +", "+ aOidList +")");
		ErrorLogger.debug("aOidList.getCount() :"+aOidList.getCount());
		ErrorLogger.debug("���������������������������������������������");*/
		
		SnmpValue		 snmpValue   	= new SnmpValue();
		SnmpValueTable	 snmpValueTable = new SnmpValueTable();
		SnmpOidValueList oidValueList   = new SnmpOidValueList();
		CapsSnmpHandler	 snmpHandler	= new CapsSnmpHandler(oidValueList);
        InetAddress		 remote			= null;
        
        try
        {
            remote = InetAddress.getByName(aDeviceInfo.getHost());
        }
        catch (UnknownHostException e)
        {
        	ErrorLogger.error(e);
            return null;
        }
		/*ErrorLogger.debug("���������������������������������������������");
		ErrorLogger.debug("remote :"+remote);
		ErrorLogger.debug("���������������������������������������������");*/
		
        //
        // Initialize the peer
        //
        SnmpPeer peer = new SnmpPeer(remote);
        
        peer.setPort(aDeviceInfo.getPort());
        if( aDeviceInfo.getTimeout() == -1 )
        	peer.setTimeout(TIME_OUT);
        else
        	peer.setTimeout(aDeviceInfo.getTimeout());
        
        if( aDeviceInfo.getRetry() == -1 )
        	peer.setRetries(RETRY);
        else
        	peer.setRetries(aDeviceInfo.getRetry());        

		SnmpParameters parms = peer.getParameters();
		parms.setVersion(aDeviceInfo.getVersion());
		parms.setReadCommunity(aDeviceInfo.getCommunity());        
        
        SnmpSession session = null;
        try
        {
            session = new SnmpSession(peer);
        }
        catch (SocketException e)
        {
        	ErrorLogger.error(e);
            return null;
        }        
        
    	session.setDefaultHandler(snmpHandler);
        
        try
        {
	        for( int i = 0; i < aOidList.getCount(); i++ )
	        {
	    		/*ErrorLogger.debug("���������������������������������������������");
	    		ErrorLogger.debug("aOidList.getOid("+i+") :"+aOidList.getOid(i));
	    		ErrorLogger.debug("���������������������������������������������");*/
	        	if( aOidList.getOid(i) == null )
	        		continue;
	        	
		        //
		        // set the stop point
		        //
		        SnmpObjectId id = new SnmpObjectId(aOidList.getOid(i));
		        int[] ids = id.getIdentifiers();
		        ++ids[ids.length - 1];
		        id.setIdentifiers(ids);
		        snmpHandler.m_stopAt = id;

		        //
		        // send the first request
		        //
		        SnmpVarBind[] vblist = {new SnmpVarBind(aOidList.getOid(i))};
		        SnmpPduRequest pdu = new SnmpPduRequest(aOidList.getOid(i).endsWith(".0") ? SnmpPduPacket.GET : SnmpPduPacket.GETNEXT, vblist);
		        pdu.setRequestId(SnmpPduPacket.nextSequence());

	            synchronized (session)
	            {
	                session.send(pdu);
	                session.wait();
	            }
	            
	            SnmpValueField snmpValueField = new SnmpValueField();
	            snmpValueField.setRootId(aOidList.getOid(i));
	            snmpValueField.setName(aOidList.getOidName(i));
				for( int j = 0; j < oidValueList.getSize(); j++ )
				{
					SnmpOidValue oidValue = oidValueList.getOidValue(j);
					snmpValueField.setValue( oidValue.getOid(), oidValue.getValue() );
				}
				
				snmpValueTable.addField( snmpValueField );
				snmpValue.addOidValueList(oidValueList);
				oidValueList.clear();
	        }
        }
        catch (InterruptedException e)
        {
        	ErrorLogger.error("SnmpValueTable"+"::"+e);
        	//ErrorLogger.error(e);
            return null;
        }
        finally
        {
            session.close();
        }
        
        return snmpValueTable;
	}
}
