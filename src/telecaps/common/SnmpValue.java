package telecaps.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

	
public class SnmpValue
{
	private String m_deviceId     = null;
	private String m_oidType      = null;
	private String m_oidGroup     = null;
	private Vector m_oidValueList = null;  // SnmpOidValueList vector
	
	public SnmpValue()
	{
		m_oidValueList = new Vector();
	}
	
	public SnmpValue(SnmpValue snmpValue)
	{
		m_deviceId     = snmpValue.getDeviceId();
		m_oidType      = snmpValue.getOidType();
		m_oidGroup     = snmpValue.getOidGroup();
		m_oidValueList = (Vector)snmpValue.getOidValueList().clone();
	}
	
	public String getDeviceId()
	{
		return m_deviceId;
	}

	public String getOidType()
	{
		return m_oidType;
	}
	
	public String getOidGroup()
	{
		return m_oidGroup;
	}

	public int getOidValueListSize()
	{
		return m_oidValueList.size();
	}
	
	public Vector getOidValueList()
	{
		return m_oidValueList;
	}
	
	public SnmpOidValueList getOidValueList(int index)
	{
		if( index >= 0 && index < getOidValueListSize() )
			return (SnmpOidValueList)m_oidValueList.get(index);
		else
			return null;
	}
	
	public void setDeviceId(String deviceId)
	{
		m_deviceId = deviceId;
	}
	
	public void setOidType(String oidType)
	{
		m_oidType = oidType;
	}
	
	public void setOidGroup(String oidGroup)
	{
		m_oidGroup = oidGroup;
	}
	
	public boolean addOidValueList(SnmpOidValueList oidValueList)
	{
		return m_oidValueList.add( new SnmpOidValueList(oidValueList) );
	}

}

