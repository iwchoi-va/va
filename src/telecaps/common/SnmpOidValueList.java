package telecaps.common;

import java.util.Vector;

import org.snmp4j.smi.OID;


public class SnmpOidValueList
{
	private String 	m_oidName   = null;
	private String	m_rootOid   = null;
	private Vector	m_value     = null;	// SnmpOidValue Vector
	private OID m_rootOIDv3=null;
	
	public SnmpOidValueList()
	{
		m_value = new Vector();
	}
	
	public SnmpOidValueList(String oidName, String rootOid)
	{
		m_oidName = oidName;
		m_rootOid = rootOid;
	}
	
	public SnmpOidValueList(String oidName, OID rootOidv3)
	{
		m_oidName = oidName;
		m_rootOIDv3 = rootOidv3;
	}
	
	public SnmpOidValueList(SnmpOidValueList oidValueList)
	{
		m_oidName = oidValueList.getOidName();
		//m_rootOid = oidValueList.getRootOid();		
		m_value   = (Vector)oidValueList.getValues().clone();
		m_rootOIDv3=oidValueList.getRootOidv3();
	}
	
	private boolean isValidIndex(int index)
	{
		return (index >= 0 && index < m_value.size() );
	}
	
	public int getSize()
	{
		return m_value.size();
	}
	
	public String getOidName()
	{
		return m_oidName;
	}
	
	public String getRootOid()
	{
		return m_rootOid;
	}	
	
	public OID getRootOidv3()
	{
		return m_rootOIDv3;
	}
	
	public Vector getValues()
	{
		return m_value;
	}
	
	public SnmpOidValue getOidValue(int index)
	{
		if( isValidIndex(index) )
			return (SnmpOidValue)m_value.get(index);
		else
			return null;
	}

	public String getOid(int index)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
			return oidValue.getOid();
		else
			return null;
	}
	
	public OID getOidv3(int index)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
			return oidValue.getOidv3();
		else
			return null;
	}
	
	public String getValue(int index)
	{
		SnmpOidValue value = getOidValue( index );
		
		if( value != null )
			return value.getValue();
		else
			return null;
	}	
	
	public String getSubOid(int index)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
		{
			String oid = oidValue.getOid();
		
			if( oid != null )
			{
				if( m_rootOid != null && oid.startsWith(m_rootOid) == true ){
					return oid.substring( m_rootOid.length() );
				}else
					return oid;
			}
			else
				return null;
		}
		else
			return null;
	}
	
	public String getSubOidv3(int index)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
		{
			OID oid = oidValue.getOidv3();
		
			if( oid != null )
			{
				if( m_rootOIDv3 != null && oid.startsWith(m_rootOIDv3) == true ){
					//return oid.substring( m_rootOid.length() );
					return (oid.toString()).substring(m_rootOIDv3.toString().length());
				}else
					return oid.toString();
			}
			else
				return null;
		}
		else
			return null;
	}
	
	public boolean addOidValue(SnmpOidValue oidValue)
	{
		return m_value.add( oidValue );
	}
	
	public boolean addOidValue(String oid, String value)
	{
		return m_value.add( new SnmpOidValue(oid, value) );
	}
	
	public boolean addOidValue2(OID oidv3, String value)
	{
		return m_value.add( new SnmpOidValue(oidv3, value) );
	}

	public void setOidValue(int index, String oid, String value)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
		{
			oidValue.setOidValue( oid, value );
		}
	}
	
	public void setOidValue(int index, SnmpOidValue aOidValue)
	{
		SnmpOidValue oidValue = getOidValue( index );
		
		if( oidValue != null )
		{
			oidValue.setOidValue( aOidValue );
		}
	}	
	
	public void setOidName(String oidName)
	{
		m_oidName = oidName;
	}
	
	public void setRootOid(String rootOid)
	{
		m_rootOid = rootOid;
	}
	
	public void setRootOid(OID rootOidv3)
	{
		m_rootOIDv3 = rootOidv3;
	}
	
	public void clear()
	{
		m_rootOIDv3=null;
		m_oidName = null;
		m_rootOid = null;		
		m_value.clear();
	}
}

