package telecaps.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

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

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;


public class SnmpGetThread implements Runnable
{
	private static final int TIME_OUT = 40000;
	//private static final int TIME_OUT = 10000;
	private static final int RETRY    = 1;

	//private final Blocking_queue m_queue;
	private final BlockingQueue<CapsDBValue> m_queue;

	String strDeviceId   = null;
	String strOidType    = null;
	String strOidGroup   = null;

	public SnmpGetThread(BlockingQueue<CapsDBValue> q, String strDeviceId, String strOidType, String strOidGroup)
    {
    	m_queue = q;
    	this.strDeviceId  = strDeviceId;
    	this.strOidType   = strOidType;
    	this.strOidGroup  = strOidGroup;
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
			
			sqlParam.setSqlName("telecaps.srhDeviceInfo.2");
			sqlParam.addValue("device_id", aDeviceId);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			if( sqlResult.getCount() > 0 )
			{
				Param param = sqlResult.getListParam("telecaps.srhDeviceInfo.2").getParam(0);
				
				deviceInfo = new CapsDeviceInfo();
				
				deviceInfo.setHost( param.getString("DEVICE_IP") );
				deviceInfo.setPort( param.getInt("DEVICE_PORT")  );
				deviceInfo.setCommunity( param.getString("READ_COMMUNITY") );
				deviceInfo.setAuthname(param.getString("AUTH_NAME"));
				deviceInfo.setAuthpass(param.getString("AUTH_PASS"));
				String version = param.getString("SNMP_VERSION");
				deviceInfo.setVersion( (version.equals("v2") || version.equals("v2c")) ? SnmpSMI.SNMPV2 : SnmpConstants.version3);
			}
		}
        catch(SQLServiceException e)
        {
        	ErrorLogger.error(e);
        	//Logger.write(aDeviceId,Thread.currentThread().getName(),"getDeviceInfo",Logger.getStackTrace(e),"","","");
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	//Logger.write(aDeviceId,Thread.currentThread().getName(),"getDeviceInfo",Logger.getStackTrace(ex),"","","");
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
        	//Logger.write(aDeviceId,Thread.currentThread().getName(),"getOidList",Logger.getStackTrace(e),"","","");
        }
        catch(Exception ex)
        {
        	ErrorLogger.error(ex);
        	//Logger.write(aDeviceId,Thread.currentThread().getName(),"getOidList",Logger.getStackTrace(ex),"","","");
        }

		return oidList;
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
	/**
	 * Device ������ OID ����Ʈ�� ��ȸ�Ͽ� OID ���� ��� DB�� �����Ѵ�
     *
	 * @param strDeviceId   Device ID
     * @param strOidType    OID Ÿ��
     * @param strOidGroup   OID �׷�
	 * @return boolean      �����
	 */
    public void run()
    {
    	 	
    	boolean success = true;
		
		try
		{
            // Device ����
            CapsDeviceInfo deviceInfo = null;
            
            deviceInfo = getDeviceInfo(strDeviceId);
            
            //ErrorLogger.error( " strDeviceIdstrDeviceIdstrDeviceId ::: " + strDeviceId  );

            // OID ����Ʈ
            SnmpOidList oidList = getOidList(strDeviceId, strOidType, strOidGroup);
            
            if( deviceInfo == null )
            {
                ErrorLogger.error( strDeviceId + " info not found" );
                //return false;
            }
            
            if( oidList == null || oidList.getCount() == 0 )
            {
                ErrorLogger.error( strDeviceId + " oid not found" );
                //return false;
            }
            
            InetAddress remote = null;
            
            try
            {
                remote = InetAddress.getByName(deviceInfo.getHost());
            }
            catch (UnknownHostException e)
            {
                ErrorLogger.error(e);
                //return false;
            }
            //ErrorLogger.debug("=======================version : "+deviceInfo.getVersion());
            //  SNMP V3�� ���
            if(deviceInfo.getVersion() == 3) {
            	//ErrorLogger.debug("========================== SNMP V3 START ==========================");
            	
            	OctetString AUTH=new OctetString(deviceInfo.getAuthname());  // Ÿ����� ���Ӹ�
            	OctetString AUTH_PASS=new OctetString(deviceInfo.getAuthpass()); // Ÿ����� ����PASS
	       	   	OctetString securityName =null;
	       	   	securityName=AUTH;
	       	   	int securityLevel = SecurityLevel.AUTH_NOPRIV;
	       	   	String IP=deviceInfo.getHost();
	       	   	int PORT=deviceInfo.getPort();
	       	   	Address address = GenericAddress.parse("udp:"+IP+"/"+PORT);
	       	   	
	        	 //ErrorLogger.debug("address         ::::::::::::::::::::: " + address);
	        	 //ErrorLogger.debug("securityLevel         ::::::::::::::::::::: " + securityLevel);
	        	 //ErrorLogger.debug("securityName         ::::::::::::::::::::: " + securityName);
	       	 
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
		 	    snmp = createSnmpSession(AUTH, AuthMD5.ID, AUTH_PASS, null, null);
	 		    snmp.listen();
	 		    //ErrorLogger.info(Thread.currentThread().getName() +" "+strDeviceId+" SnmpV3Get");
		 	    try{
		 	    	//ErrorLogger.debug("========== SNMP CREATE ==============");
		 	    	//ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpV3Get OID oidList.getCount():"+oidList.getCount() + " OidGroup:" +strOidGroup);
		 	    	
		 	    	for(int k=0; k<oidList.getCount(); k++) {
		 	    		
		 	    		//ErrorLogger.debug("====================OIDLIST COUNT : "+oidList.getCount() + " strDeviceId :: " + strDeviceId);
		 	    		
		 	    		ScopedPDU pdu = new ScopedPDU();
				 	    pdu.setType(PDU.GETBULK);
		 	    		OID rootOID = new OID(oidList.getOid(k));
		 	    		pdu.add( new VariableBinding(rootOID) );
		 	    		//ErrorLogger.debug("================RootOID : "+rootOID);
		 	    		ResponseEvent response = null;
		 	    		PDU resPdu=null;
		 	    		VariableBinding var = null;
		 	    		OID currentOID = null;
		 	    		int z=1;
		 	    		while(true){
			 	    		response = snmp.getNext(pdu, myTarget);
			 		        // PDU
			 		        resPdu = response.getResponse();
			 		        if ( resPdu != null && resPdu.getErrorStatus() == SnmpConstants.SNMP_ERROR_SUCCESS ) {
			 		        	 var = resPdu.get(0);
			 		        	//ErrorLogger.debug("================== var result : "+var.getVariable().toString() + " : " + strDeviceId);
			 			         currentOID = var.getOid();
			 			         
			 			         if( !var.isException()) {
			 			        	//ErrorLogger.debug("================= rootOID : "+rootOID+" / currentOID : "+currentOID  + " : " + strDeviceId);
			 			        	if ( isSubTree(rootOID, currentOID) ) {
			 			        		 //ErrorLogger.info("��ȣ "+z+")"+currentOID + " =>>>>>>>>> " + var.getVariable().toString());
			 				             snmpOidValueList.addOidValue2(currentOID, var.getVariable().toString());
			 				             //ErrorLogger.debug("=================== addOID after =======================");
			 				             pdu = new ScopedPDU();
			 				             pdu.setType(PDU.GETBULK);
			 				             pdu.addOID( new VariableBinding(currentOID) );
			 				             z++;
			 				        }else {
			 				        	//ErrorLogger.debug("================ SubTree END ================");
			 				        	break;
			 				        }
			 			        	
			 			         }else{
			 			        	 ErrorLogger.error(currentOID + " ->>> " + "has exception syntax!");
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
		 	    	   
		 	    		//ErrorLogger.info(Thread.currentThread().getName() +" "+strDeviceId+" SnmpV3Get OID �� ���� snmpOidValueList.getSize() : "+snmpOidValueList.getSize());
	                    //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpV3Get OID "+oidList.getOid(k)+" ���� �� ����:"+snmpOidValueList.getSize(),"","","","");
		 	    		
		 		       //ErrorLogger.debug("================= BEFORE addOIDLIST ==================");
		 		       if(snmpOidValueList.getSize() > 0){
		 		    	 // if(snmpValue.getDeviceId().equals("hpsi-rec02"))ErrorLogger.info("hpsi-rec02 COUNT:"+k+":"+oidList.getOidName(k));
			 	    		snmpOidValueList.setRootOid(rootOID);
				            snmpOidValueList.setOidName(oidList.getOidName(k));
				            snmpValue.addOidValueList(snmpOidValueList);
			 	    	}
			 	    	snmpOidValueList.clear();
		 		       
			 	    	
		 	    	}  // for End
		 	    	snmp.close();
		 	    	 if( snmpValue.getOidValueListSize() > 0 ) {
		 	    		
		 	    		//if(snmpValue.getDeviceId().equals("hpsi-rec02"))ErrorLogger.info("========== BEFORE CapsDBValue =========="+snmpValue.getDeviceId()+":"+snmpValue.getOidValueListSize() );
		 	    		 m_queue.put( new CapsDBValue(CapsDBValue.DB_VALUE_OID, new SnmpValue(snmpValue)) );
		 	    	 }
		 	    	
		 	    }catch(IOException e) {
		 	    	success=false;
		 	    	e.printStackTrace();
		 	    }
		 	    
            }else { // ���⼭���ʹ� SNMP V1,V2c 
            	ErrorLogger.debug("===================== SNMP V1,V2c START =========================");
		            // Initialize the peer
		            SnmpPeer peer = new SnmpPeer(remote);
		            
		            peer.setPort(deviceInfo.getPort());
		            
		            //ErrorLogger.error("getTimeout:"+deviceInfo.getTimeout()+"!getRetry:"+deviceInfo.getRetry());
		            
		            peer.setTimeout(TIME_OUT);
		            peer.setRetries(RETRY); 
		
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
		                //Logger.write(strDeviceId,Thread.currentThread().getName(),Logger.getStackTrace(e),"","","","");
		                //return false;
		            }        
		
		            SnmpValue snmpValue = new SnmpValue();
		            SnmpOidValueList snmpOidValueList = new SnmpOidValueList();
		            CapsSnmpHandler snmpHandler = new CapsSnmpHandler(snmpOidValueList);			        
		            session.setDefaultHandler(snmpHandler);
		
		            snmpValue.setDeviceId(strDeviceId);
		            snmpValue.setOidType(strOidType);
		            snmpValue.setOidGroup(strOidGroup);
	            
	            //ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpGet ���� ����");
	            try
	            {
	                //ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpGet OID ���� oidList.getCount():"+oidList.getCount());
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet ����  OidGroup:"+strOidGroup,"","","","");
	                //Logger.write("snmpget",strDeviceId,Thread.currentThread().getName(),"SnmpGet ����  OidGroup:"+strOidGroup,"","","");
	                for( int j = 0; j < oidList.getCount(); j++ )
	                {
	                	
	                    // set the stop point
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
	                    //ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpGet OID ��ȸ");
	
	                    synchronized (session)
	                    {
	                        session.send(pdu);
	                        session.wait();
	                    }
	                    //ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpGet OID �� ���� snmpOidValueList.getSize():"+snmpOidValueList.getSize());
	                    //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet OID "+oidList.getOid(j)+" ���� �� ����:"+snmpOidValueList.getSize(),"","","","");
	                    //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet OID "+oidList.getOid(j)+" ����Ʈ��:"+snmpOidValueList.getValues(),"","","","");
	                    if( snmpOidValueList.getSize() > 0 )
	                    {
	                        snmpOidValueList.setOidName(oidList.getOidName(j));
	                        snmpOidValueList.setRootOid(oidList.getOid(j));
	                        snmpValue.addOidValueList(snmpOidValueList);
	                    }
	                    snmpOidValueList.clear();

	                }
	                
	                if( snmpValue.getOidValueListSize() > 0 ) {
	                    m_queue.put( new CapsDBValue(CapsDBValue.DB_VALUE_OID, new SnmpValue(snmpValue)) );
	                }
	                //ErrorLogger.debug(Thread.currentThread().getName() +" "+strDeviceId+" SnmpGet ���� ����");
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),"SnmpGet ����","","","","");
	                //Logger.write("snmpget",strDeviceId,Thread.currentThread().getName(),"SnmpGet ����","","","");
	            
	            }
	             
	            catch (InterruptedException e)
	            {
	                success = false;
	                ErrorLogger.error(strDeviceId+"::"+e);
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),Logger.getStackTrace(e),"","","","");
	            }
	            catch(Exception e)
	            {
	                success = false;
	                ErrorLogger.error(e);
	                //Logger.write(strDeviceId,Thread.currentThread().getName(),Logger.getStackTrace(e),"","","","");
	            }
	            finally
	            {
	                session.close();
	            }
            }
		}
        catch(Exception e)
        {
            success = false;
        	ErrorLogger.error(e);
        	//Logger.write(strDeviceId,Thread.currentThread().getName(),Logger.getStackTrace(e),"","","","");
        }		
		
		
		//return success;
    }
	 
	
}
