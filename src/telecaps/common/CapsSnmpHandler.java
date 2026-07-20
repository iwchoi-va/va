package telecaps.common;

import org.opennms.protocols.snmp.SnmpEndOfMibView;
import org.opennms.protocols.snmp.SnmpHandler;
import org.opennms.protocols.snmp.SnmpObjectId;
import org.opennms.protocols.snmp.SnmpPduPacket;
import org.opennms.protocols.snmp.SnmpPduRequest;
import org.opennms.protocols.snmp.SnmpSession;
import org.opennms.protocols.snmp.SnmpSyntax;
import org.opennms.protocols.snmp.SnmpVarBind;

import com.locus.jedi.log.ErrorLogger;

public class CapsSnmpHandler implements SnmpHandler {
	private final SnmpOidValueList m_snmpOidValueList;

	/**
	 * The object identifier where the walk of the tree should stop.
	 */
	SnmpObjectId m_stopAt = null;

	public CapsSnmpHandler(SnmpOidValueList snmpOidValueList) {
		m_snmpOidValueList = snmpOidValueList;
	}

	/**
	 * Defined by the SnmpHandler interface. Used to process internal session
	 * errors.
	 * 
	 * @param session
	 *            The SNMP session in error.
	 * @param err
	 *            The Error condition
	 * @param pdu
	 *            The pdu associated with this error condition
	 * 
	 */
	public void snmpInternalError(SnmpSession session, int err, SnmpSyntax pdu) {
		ErrorLogger.error(session.getPeer().getPeer().getHostAddress()
				+ " InternalError(" + err + ")");

		synchronized (session) {
			session.notify();
		}
	}

	/**
	 * This method is define by the SnmpHandler interface and invoked if an
	 * agent fails to respond.
	 * 
	 * @param session
	 *            The SNMP session in error.
	 * @param pdu
	 *            The PDU that timedout.
	 * 
	 */
	public void snmpTimeoutError(SnmpSession session, SnmpSyntax pdu) {
		//ErrorLogger.error(session.getPeer().getPeer().getHostAddress()
		//		+ " SnmpTimeout");
		//Logger.write("snmpget", Thread.currentThread().getName(), session
		//		.getPeer().getPeer().getHostAddress()
		//		+ " SnmpTimeout", "", "", "", "");

		synchronized (session) {
			session.notify();
		}
	}

	/**
	 * This method is defined by the SnmpHandler interface and invoked when the
	 * agent responds to the management application.
	 * 
	 * @param session
	 *            The session receiving the pdu.
	 * @param cmd
	 *            The command from the pdu.
	 * @param pdu
	 *            The received pdu.
	 * 
	 * @see org.opennms.protocols.snmp.SnmpPduPacket#getCommand
	 */
	public void snmpReceivedPdu(SnmpSession session, int cmd, SnmpPduPacket pdu) {
		SnmpPduRequest req = null;
		if (pdu instanceof SnmpPduRequest) {
			req = (SnmpPduRequest) pdu;
		}

		if (pdu.getCommand() != SnmpPduPacket.RESPONSE) {
			ErrorLogger.error(session.getPeer().getPeer().getHostAddress()
					+ " Error: Received non-response command "
					+ pdu.getCommand());

			synchronized (session) {
				session.notify();
			}
			return;
		}

		if (req.getErrorStatus() != 0) {
			synchronized (session) {
				session.notify();
			}
			return;
		}

		//
		// Passed the checks so lets get the first varbind and
		// print out it's value
		//
		SnmpVarBind vb = pdu.getVarBindAt(0);
		if (vb.getValue().typeId() == SnmpEndOfMibView.ASNTYPE
				|| (m_stopAt != null && m_stopAt.compare(vb.getName()) < 0)) {
			synchronized (session) {
				session.notify();
			}
			return;
		}

		// System.out.println(vb.getName().toString() + ": " +
		// vb.getValue().toString());
		m_snmpOidValueList.addOidValue(vb.getName().toString(), vb.getValue()
				.toString());

		//
		// make the next pdu
		//
		SnmpVarBind[] vblist = { new SnmpVarBind(vb.getName()) };
		SnmpPduRequest newReq = new SnmpPduRequest(SnmpPduPacket.GETNEXT,
				vblist);
		newReq.setRequestId(SnmpPduPacket.nextSequence());

		session.send(newReq);
	}

}
