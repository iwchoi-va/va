package telecaps.common;

import org.snmp4j.smi.OID;


public class SnmpOidValue
{
	private String m_oid;
	private String m_value;
	private OID m_oid_v3;
	
	public SnmpOidValue()
	{
		m_oid = null;
		m_value = null;
		m_oid_v3=null;
	}
	
	public SnmpOidValue(OID oidv3, String value)
	{
		
		m_value = value;
		m_oid_v3=oidv3;
	}
	
	public SnmpOidValue(String oid, String value)
	{
		m_oid = oid;
		m_value = value;
		
	}
	
	public SnmpOidValue(SnmpOidValue oidValue)
	{
		m_oid = oidValue.getOid();
		m_value = oidValue.getValue();
		m_oid_v3=oidValue.getOidv3();
	}
	
	public String getOid()
	{
		return m_oid;
	}
	
	public OID getOidv3()
	{
		return m_oid_v3;
	}
	
	public String getValue()
	{
		return m_value;
	}

	public void setOid(String oid)
	{
		m_oid = oid;
	}
	
	public void setOidv3(OID oid)
	{
		m_oid_v3 = oid;
	}
	
	public void setValue(String value)
	{
		m_value = value;
	}
	
	public void setOidValue(String oid, String value)
	{
		m_oid = oid;
		m_value = value;
	}
	
	public void setOidValuev3(OID oid, String value)
	{
		m_oid_v3 = oid;
		m_value = value;
	}
	
	public void setOidValue(SnmpOidValue oidValue)
	{
		m_oid = oidValue.getOid();
		m_value = oidValue.getValue();
	}
	
	public void setOidValuev3(SnmpOidValue oidValue)
	{
		m_oid_v3 = oidValue.getOidv3();
		m_value = oidValue.getValue();
	}
	
}
