package telecaps.common;

import java.util.Vector;

public class SnmpOidList
{
	private Vector m_oidName = null;
	private Vector m_oid = null;
	
	public SnmpOidList()
	{
		m_oidName = new Vector();
		m_oid = new Vector();
	}
	
	private boolean isValidIndex(int index)
	{
		return (index >= 0 && index < m_oidName.size());
	}
	
	public void add(String aOidName, String aOid)
	{
		m_oidName.add( aOidName );
		m_oid.add( aOid );
	}
	
	public int getCount()
	{
		return m_oidName.size();
	}
	
	public String getOidName(int index)
	{
		if( isValidIndex(index) )
			return (String)m_oidName.get(index);
		else
			return null;
	}
	
	public String getOid(int index)
	{
		if( isValidIndex(index) )
			return (String)m_oid.get(index);
		else
			return null;
	}
	
	public void setOidName(int index, String aOidName)
	{
		if( isValidIndex(index) )
			m_oidName.set(index, aOidName);
	}
	
	public void setOid(int index, String aOid)
	{
		if( isValidIndex(index) )
			m_oid.set(index, aOid);
	}
	
	public void clear()
	{
		m_oidName.clear();
		m_oid.clear();
	}
	
	public void remove(int index)
	{
		if( isValidIndex(index) )
		{
			m_oidName.remove(index);
			m_oid.remove(index);
		}
	}
}
