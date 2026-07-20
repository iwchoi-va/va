package telecaps.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class CapsDBWriter extends Thread {
	// private final Blocking_queue m_queue;
	private final BlockingQueue m_queue;
	private boolean m_stop;
	private final ExecutorService threadExecutor;

	public CapsDBWriter(BlockingQueue queue) {
		m_queue = queue;
		m_stop = false;
		threadExecutor = null;
	}

	public CapsDBWriter(BlockingQueue queue, ExecutorService threadEx) {
		m_queue = queue;
		m_stop = false;
		threadExecutor = threadEx;
	}

	public void run() {

		try {
			while (true) {
				/*
				 * synchronized( m_queue ) { if( m_queue.is_empty() == false )
				 * //DBWrite( (CapsDBValue)m_queue.dequeue() );
				 * threadExecutor.execute(new DBWriterThread(
				 * (CapsDBValue)m_queue.dequeue() ) ); else if( m_stop == true )
				 * break; }
				 * 
				 * Thread.sleep(10);
				 */
				threadExecutor.execute(new DBWriterThread((CapsDBValue) m_queue
						.take()));
			}
		} catch (InterruptedException e) {
			ErrorLogger.error(e);
		} catch (Blocking_queue.Closed e) {
			/* ignore it, stop thread */
		}
	}

	void DBWrite(CapsDBValue dbValue) {
		// -- ������ ����.
		int dbValueType = dbValue.getType();

		//ErrorLogger.debug("DBWrite!dbValueType:" + dbValue.getType());

		if (dbValueType == CapsDBValue.DB_VALUE_OID) {
			SnmpValue snmpValue = (SnmpValue) dbValue.getValue();
			String oidType = snmpValue.getOidType();
			String tableName = null;

			if (oidType.equals("C") == true)
				tableName = "T_VALUE_CALL";
			else if (oidType.equals("H") == true)
				tableName = "T_VALUE_HOUR";
			else if (oidType.equals("M") == true)
				tableName = "T_VALUE_MIN";
			else if (oidType.equals("P") == true)
				tableName = "T_VALUE_PHONE";
			// else if( oidType.equals("T") == true )
			// tableName = "T_VALUE_TRAP";

			if (tableName != null) {
				JediTransaction tran = JediTransactionManager
						.getJediTransaction();

				try {
					String valueId = null;
					String mibId = null;
					String checkNumOids = null;
					SQLParam sqlParam = new SQLParam();

					// Value ID
					sqlParam.setSqlName("telecaps.srhNewValueId2");
					SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam);

					//ErrorLogger.debug("CapsDBWriter sqlResult2:" + sqlResult2);

					if (sqlResult2.getCount() > 0) {
						//ErrorLogger.debug("CapsDBWriter sqlResult2:"+ sqlResult2.getListParam("telecaps.srhNewValueId"));
					}
					
					// Value ID
					sqlParam.clear();
					sqlParam.setSqlName("telecaps.srhNewValueId");
					SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

					//ErrorLogger.debug("CapsDBWriter sqlResult:" + sqlResult);

					if (sqlResult.getCount() > 0) {
						valueId = sqlResult.getListParam("telecaps.srhNewValueId").getParam(0).getString("VALUE_ID");
						//ErrorLogger.debug("CapsDBWriter sqlResult:"+ sqlResult.getListParam("telecaps.srhNewValueId"));
					}
					
					// MIB ID
					sqlParam.clear();
					sqlParam.setSqlName("telecaps.srhMibId");
					sqlParam.addValue("device_id", snmpValue.getDeviceId());
					sqlParam.addValue("oid_type", snmpValue.getOidType());
					sqlParam.addValue("oid_group", snmpValue.getOidGroup());
					sqlResult = SQLServiceManager.getInstance().execute(
							sqlParam);

					if (sqlResult.getCount() > 0) {
						mibId = sqlResult.getListParam("telecaps.srhMibId")
								.getParam(0).getString("MIB_ID");
						checkNumOids = sqlResult
								.getListParam("telecaps.srhMibId").getParam(0)
								.getString("CHECK_NUM_OIDS");
					}

					if (valueId != null && mibId != null) {
						if (checkNumOids == null
								|| Integer.parseInt(checkNumOids) == snmpValue
										.getOidValueListSize()) {
							try {
								// T_VALUE_HOUR, T_VALUE_MIN, T_VALUE_CALL,
								// T_VALUE_PHONE
								// ListParam lpSchValue = new ListParam(new
								// String[]{"table_name", "value_id",
								// "oid_name", "sub_oid", "value"});
								ListParam lpSchValue = new ListParam(
										new String[] { "value_id", "oid_name","sub_oid", "value" });

								for (int i = 0; i < snmpValue
										.getOidValueListSize(); i++) {
									SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

									if (oidValueList != null) {
										for (int j = 0; j < oidValueList
												.getSize(); j++) {
											// lpSchValue.addRow( new
											// Object[]{tableName, valueId,
											// oidValueList.getOidName(),
											// oidValueList.getSubOid(j),
											// oidValueList.getValue(j)} );
											lpSchValue.addRow(new Object[] {
													valueId,
													oidValueList.getOidName(),
													oidValueList.getSubOid(j),
													oidValueList.getValue(j) });
										}
									}
								}

								// sqlParam.clear();
								// sqlParam.setSqlName("telecaps.insSchValue");
								// sqlParam.addValue("telecaps.insSchValue",
								// lpSchValue);
								// SQLServiceManager.getInstance().execute(sqlParam);
								sqlParam.clear();
								if (oidType.equals("C") == true)
									sqlParam.setSqlName("telecaps.insSchValue.call");
								else if (oidType.equals("H") == true)
									sqlParam.setSqlName("telecaps.insSchValue.hour");
								else if (oidType.equals("M") == true)
									sqlParam.setSqlName("telecaps.insSchValue.min");
								else if (oidType.equals("P") == true)
									sqlParam.setSqlName("telecaps.insSchValue.phone");
								// sqlParam.setSqlName("telecaps.insSchValue");
								sqlParam.addValue("telecaps.insSchValue",lpSchValue);
								SQLServiceManager.getInstance().execute(sqlParam);

								// T_VALUE
								sqlParam.clear();
								sqlParam.setSqlName("telecaps.insValue");
								sqlParam.addValue("value_id", valueId);
								sqlParam.addValue("mib_id", mibId);
								sqlParam.addValue("num_values",
										lpSchValue.rowSize());
								SQLServiceManager.getInstance().execute(sqlParam);

								// T_MIB ������Ʈ
								sqlParam.clear();
								sqlParam.setSqlName("telecaps.updMibDate");
								sqlParam.addValue("mib_id", mibId);
								sqlParam.addValue("value_id", valueId);
								SQLServiceManager.getInstance().execute(sqlParam);

								sqlParam.clear();
								//ErrorLogger.info("================ valueID :"+valueId+" telecaps.sql.alarm.proc");
								sqlParam.setSqlName("telecaps.sql.alarm.proc");
								sqlParam.addValue("value_id", valueId);
								SQLServiceManager.getInstance().execute(sqlParam);

								tran.commit();
							} catch (Exception e) {
								tran.rollback();
								ErrorLogger.error(e);
							}
						} else {
							ErrorLogger.error("Num of oids check error : ("
									+ mibId + ", "
									+ Integer.parseInt(checkNumOids) + ", "
									+ snmpValue.getOidValueListSize() + ")");
						}
					} else {
						ErrorLogger.error("valueId = null or mibId = null");
					}

				} catch (SQLServiceException e) {
					ErrorLogger.error(e);
				} catch (Exception e) {
					ErrorLogger.error(e);
				}
			}
		} 
	}

	public void complete() {
		try {
			join();
		} catch (InterruptedException e) {
			ErrorLogger.error(e);
		} catch (Exception e) {
			ErrorLogger.error(e);
		}
	}

	public String getGenericString(int t_vlue) {
		String generictrap = "";
		ErrorLogger.error("t_vlue::" + t_vlue);
		if (t_vlue == 0) {
			generictrap = "coldStart";
		} else if (t_vlue == 1) {
			generictrap = "warmStart";
		} else if (t_vlue == 2) {
			generictrap = "linkDown";
		} else if (t_vlue == 3) {
			generictrap = "linkUp";
		} else if (t_vlue == 4) {
			generictrap = "authenticationFailure";
		} else if (t_vlue == 5) {
			generictrap = "egpNeighborLoss";
		} else if (t_vlue == 6) {
			generictrap = "enterpriseSpecific";
		}
		ErrorLogger.error("generictrap::" + generictrap);
		return generictrap;
	}

	public boolean isHexcode(String t_hexcode) {
		String value = t_hexcode.toUpperCase();
		if (value.indexOf(" ") != 2 || value.indexOf("G") != -1
				|| value.indexOf("H") != -1 || value.indexOf("I") != -1
				|| value.indexOf("J") != -1 || value.indexOf("K") != -1
				|| value.indexOf("L") != -1 || value.indexOf("M") != -1
				|| value.indexOf("N") != -1 || value.indexOf("O") != -1
				|| value.indexOf("P") != -1 || value.indexOf("Q") != -1
				|| value.indexOf("R") != -1 || value.indexOf("S") != -1
				|| value.indexOf("T") != -1 || value.indexOf("U") != -1
				|| value.indexOf("V") != -1 || value.indexOf("W") != -1
				|| value.indexOf("X") != -1 || value.indexOf("Y") != -1
				|| value.indexOf("Z") != -1 || value.length() < 2) {
			return false;
		}
		return true;
	}

	public byte[] hexToByte(String hex) {
		byte bts[] = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),16);
		}
		return bts;
	}
}
