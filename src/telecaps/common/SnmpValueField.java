package telecaps.common;

import java.util.HashMap;
import java.util.Map;

public class SnmpValueField
{
	private String	m_name = null;
	private String  m_rootId = null;
	private Map 	m_subIdValue = null;
	
	public SnmpValueField()
	{
		m_subIdValue = new HashMap();
	}
	
	public SnmpValueField(SnmpValueField valueField)
	{
		m_name = valueField.getName();
		m_rootId = valueField.getRootid();
		m_subIdValue = (Map)valueField.getSubIdValue().clone();
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String getRootid()
	{
		return m_rootId;
	}
	
	public String getValue(String subId)
	{
		if (m_subIdValue.containsKey(subId))
			return (String)m_subIdValue.get(subId);
		else
			return null;
	}

	public int getSize()
	{
		return m_subIdValue.size();
	}
	
	public HashMap getSubIdValue()
	{
		return (HashMap)m_subIdValue;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public void setRootId(String rootId)
	{
		m_rootId = rootId;
	}

	public void setValue(String id, String value)
	{
		if( m_rootId != null )
		{
			if( id.startsWith(m_rootId) )
				setSubidValue(id.substring(m_rootId.length()), value);
			else
				setSubidValue(id, value);
		}
		else
			setSubidValue(id, value);
	}
	
	public void setSubidValue(String subId, String value)
	{
		m_subIdValue.put(subId, value);
	}
	
	public void removeValue(String subId)
	{
		if (m_subIdValue.containsKey(subId))
			m_subIdValue.remove(subId);
	}
}
