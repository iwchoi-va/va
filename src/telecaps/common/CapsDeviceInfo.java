package telecaps.common;

import org.opennms.protocols.snmp.SnmpSMI;
import org.snmp4j.mp.SnmpConstants;

public class CapsDeviceInfo {
	/**
	 * The version of the SNMP protocol used to communicate
	 */
	//private int m_version = SnmpSMI.SNMPV1;
	
	private int m_version=SnmpConstants.version3;

	/**
	 * The community string used to "authenticate" the request.
	 */
	private String m_community = null;

	/**
	 * The number of retries to use.
	 */
	private int m_retries = -1;

	/**
	 * The time period to wait before considering the last transmission a
	 * failure. This should be in milliseconds.
	 */
	private int m_timeout = -1;

	/**
	 * The port where request are sent & received from.
	 */
	private int m_port = -1;

	private String m_authname=null;
	private String m_authpass=null;
	
	/**
	 * The remote agent to communicate with.
	 */
	//private String m_host = "127.0.0.1";
	private String m_host = "";

	public int getVersion() {
		return m_version;
	}

	public String getCommunity() {
		return m_community;
	}
	
	public String getAuthname() {
		return m_authname;
	}
	
	public String getAuthpass() {
		return m_authpass;
	}
	
	public int getRetry() {
		return m_retries;
	}

	public int getTimeout() {
		return m_timeout;
	}

	public int getPort() {
		return m_port;
	}

	public String getHost() {
		return m_host;
	}

	public void setVersion(int version) {
		m_version = version;
	}

	public void setCommunity(String community) {
		m_community = community;
	}
	
	public void setAuthname(String authname) {
		m_authname=authname;
	}
	
	public void setAuthpass(String authpass) {
		m_authpass=authpass;
	}

	public void setRetry(int retry) {
		m_retries = retry;
	}

	public void setTimeout(int timeout) {
		m_timeout = timeout;
	}

	public void setPort(int port) {
		m_port = port;
	}

	public void setHost(String host) {
		m_host = host;
	}

}
