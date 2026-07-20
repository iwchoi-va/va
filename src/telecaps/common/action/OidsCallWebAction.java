package telecaps.common.action;

import com.locus.jedi.service.sql.*;
import com.locus.jedi.waf.action.*;
import com.locus.jedi.waf.*;
import com.locus.jedi.biz.*;
import com.locus.jedi.biz.delegate.*;
import com.locus.jedi.waf.controller.*;
import com.locus.jedi.transfer.*;
import com.locus.jedi.log.*;

import org.opennms.protocols.snmp.SnmpSMI;
import org.opennms.protocols.snmp.SnmpTrapSession;

import telecaps.common.*;

public class OidsCallWebAction extends WebActionSupport
{
    public void perform(JediRequest req, JediResponse res) throws WebActionException
    {
        //ErrorLogger.debug("���������������������������������������������");
        //ErrorLogger.debug("req.param :" + req.param);
        //ErrorLogger.debug("���������������������������������������������");
        try
        {
            SQLParam sqlparam = new SQLParam();
            SnmpOidList oidList = new SnmpOidList();
            CapsDeviceInfo deviceInfo = new CapsDeviceInfo();

            // Request parameter
            String deviceId = req.param.getString("deviceId");
            String oidGroup = req.param.getString("oidGroup");
            String outDataset = req.param.getString("outDataset");

            //ErrorLogger.debug("���������������������������������������������");
            //ErrorLogger.debug("deviceId   :" + deviceId);
            //ErrorLogger.debug("oidGroup   :" + oidGroup);
            //ErrorLogger.debug("outDataset :" + outDataset);
            //ErrorLogger.debug("���������������������������������������������");

            if( deviceId != null && !deviceId.equals("") &&
                oidGroup != null && !oidGroup.equals("") &&
                outDataset != null && !outDataset.equals("") )
            {
                // Device info
                sqlparam.setSqlName("telecaps.sql.device.info");
                sqlparam.addValue("device_id", deviceId);
                sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);
                ListParam deviceParam = (ListParam)sqlparam.getValue("telecaps.res.device.info");
                
                if( deviceParam.rowSize() > 0 )
                {
                    deviceInfo.setHost(deviceParam.getParam(0).getString("DEVICE_IP"));
                    deviceInfo.setPort(deviceParam.getParam(0).getInt("DEVICE_PORT"));
                    deviceInfo.setCommunity(deviceParam.getParam(0).getString("READ_COMMUNITY"));
                    String version = deviceParam.getParam(0).getString("SNMP_VERSION");
                    deviceInfo.setVersion((version.equals("v2") || version.equals("v2c")) ? SnmpSMI.SNMPV2 : SnmpSMI.SNMPV1);

                    String[] oidGroups = oidGroup.split(" ");
                    String[] outDatasets = outDataset.split(" ");

                    for( int k = 0; k < oidGroups.length; k++ )
                    {
                        // OID list
                        sqlparam.clear();
                        sqlparam.setSqlName("telecaps.sql.oid.list");
                        sqlparam.addValue("oid_group", oidGroups[k]);
                        sqlparam.addValue("deviceId", deviceId);
                        sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService", req.getCommonDTO(), sqlparam);
                        ListParam oidListParam = (ListParam)sqlparam.getValue("telecaps.res.oid.list");
                        
                        if( oidListParam.rowSize() > 0 )
                        {
                            String[] fieldNameList = new String[oidListParam.rowSize()];
                            oidList.clear();
                            for( int i = 0; i < oidListParam.rowSize(); i++ )
                            {
                                oidList.add(oidListParam.getParam(i).getString("OID_NAME","0"), oidListParam.getParam(i).getString("OID","0"));
                                fieldNameList[i] = oidListParam.getParam(i).getString("OID_NAME","0");
                            }
                            
                            // SNMP ȣ��
                            SnmpValueTable valueTable = SnmpGet.getTable(deviceInfo, oidList);

                            // ��� ����
                            ListParam listParam = new ListParam(fieldNameList);
                                        
                            if( valueTable != null )
                            {
                                //ErrorLogger.debug("���������������������������������������������");
                                //ErrorLogger.debug("SnmpGet.getTable("+deviceInfo+", "+oidList+").getRecordIdSize():"+valueTable.getRecordIdSize());
                                //ErrorLogger.debug("���������������������������������������������");
                                for( int i = 0; i < valueTable.getRecordIdSize(); i++ )
                                {
                                    listParam.createRow(); 
                                    for( int j = 0; j < valueTable.getFieldSize(); j++ )
                                    {
                                        listParam.setValue(i, j, valueTable.getFieldValue(i, j));
                                    }
                                }
                            }
                            res.param.addValue(outDatasets[k], listParam);
                        }
                    }
                }
            }
        }
        catch(BizAppException e)
        {
            System.out.println(e);
            throw new WebActionException("SQLServiceWebAction : " + e.getMessage(), e);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
