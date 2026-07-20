package telecaps.common;

import java.util.Vector;

public class CapsDeviceList {
	private Vector m_deviceId = null;
	private Vector m_oidType = null;
	private Vector m_oidGroup = null;

	public CapsDeviceList() {
		m_deviceId = new Vector();
		m_oidType = new Vector();
		m_oidGroup = new Vector();
	}

	private boolean isValidIndex(int index) {
		return (index >= 0 && index < getSize());
	}

	public int getSize() {
		return m_deviceId.size();
	}

	public void add(String aDeviceId, String aOidType, String aOidGroup) {
		m_deviceId.add(aDeviceId);
		m_oidType.add(aOidType);
		m_oidGroup.add(aOidGroup);
	}

	public String getDeviceId(int index) {
		if (isValidIndex(index) == true)
			return (String) m_deviceId.get(index);
		else
			return null;
	}

	public String getOidType(int index) {
		if (isValidIndex(index) == true)
			return (String) m_oidType.get(index);
		else
			return null;
	}

	public String getOidGroup(int index) {
		if (isValidIndex(index) == true)
			return (String) m_oidGroup.get(index);
		else
			return null;
	}
}
