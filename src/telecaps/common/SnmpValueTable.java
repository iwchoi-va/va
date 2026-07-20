package telecaps.common;

import java.util.Iterator;
import java.util.Vector;

public class SnmpValueTable
{
	private String m_deviceId = null;
	private Vector m_recordIds = null;
	private Vector m_fields = null;
	
	public SnmpValueTable()
	{
		m_recordIds = new Vector();
		m_fields = new Vector();
	}
	
	private boolean isValidRecordIndex(int index)
	{
		return (index >= 0 && index < getRecordIdSize() );
	}
	
	private boolean isValidFieldIndex(int index)
	{
		return (index >= 0 && index < getFieldSize() );
	}
	
	public String getDeviceId()
	{
		return m_deviceId;
	}
	
	public int getFieldSize()
	{
		return m_fields.size();
	}
	
	public int getRecordIdSize()
	{
		return m_recordIds.size();
	}
	
	public String getRecordId(int index)
	{
		if( isValidRecordIndex(index) )
			return (String)m_recordIds.get(index);
		else
			return null;
	}
	
	public SnmpValueField getField(int index)
	{
		if( isValidFieldIndex(index) )
			return (SnmpValueField)m_fields.get(index);
		else
			return null;
	}

	public String getFieldName(int index)
	{
		SnmpValueField valueField = getField(index);
		if( valueField != null )
			return valueField.getName();
		else
			return null;
	}
	
	public String getFieldValue(int recordIndex, int fieldIndex)
	{
		String recordId = getRecordId(recordIndex);
		SnmpValueField valueField = getField(fieldIndex);
		
		if( recordId != null && valueField != null )
			return valueField.getValue(recordId);
		else
			return null;
	}
	
	public void addField(SnmpValueField valueField)
	{
		m_fields.add( valueField );
		
		for(Iterator it = valueField.getSubIdValue().keySet().iterator(); it.hasNext(); )
		{
			String subId = (String)it.next();
			
			if (m_recordIds.contains(subId) == false)
				m_recordIds.add(subId);
		}
	}
	
	public void setDeviceId(String deviceId)
	{
		m_deviceId = deviceId;
	}
}
