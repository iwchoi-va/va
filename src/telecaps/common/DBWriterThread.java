package telecaps.common;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class DBWriterThread implements Runnable {
	private final CapsDBValue dbValue;

	public DBWriterThread(CapsDBValue dbValue) {
		this.dbValue = dbValue;
	}

	public void run() {
		try {

			DBWrite(dbValue);

		} catch (Exception e) {
			ErrorLogger.error(e);
		}
	}

	void DBWrite(CapsDBValue dbValue) {
		int dbValueType = dbValue.getType();

		//ErrorLogger.debug("DBWrite!dbValueType:" + dbValue.getType());

		if (dbValueType == CapsDBValue.DB_VALUE_OID) {
			SnmpValue snmpValue = (SnmpValue) dbValue.getValue();
			
			String oidType = snmpValue.getOidType();
			
			//if(snmpValue.getDeviceId().equals("hpsi-rec02"))ErrorLogger.info("TEST!!!!!!!!!!!!!!!!!!!!!!!!!!"+Thread.currentThread().activeCount()+":"+Thread.currentThread().getName() + " "
				//	+ snmpValue.getDeviceId() + " DB VALUE ����  ����");

			if (oidType.equals("C") || oidType.equals("H")
					|| oidType.equals("M") || oidType.equals("P")
					|| oidType.equals("S")) {
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
						//ErrorLogger.info("============ first valueId : "+valueId);
					}

					// MIB ID
					sqlParam.clear();
					sqlParam.setSqlName("telecaps.srhMibId");
					sqlParam.addValue("device_id", snmpValue.getDeviceId());
					sqlParam.addValue("oid_type", snmpValue.getOidType());
					sqlParam.addValue("oid_group", snmpValue.getOidGroup());
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

					if (sqlResult.getCount() > 0) {
						mibId = sqlResult.getListParam("telecaps.srhMibId").getParam(0).getString("MIB_ID");
						checkNumOids = sqlResult.getListParam("telecaps.srhMibId").getParam(0).getString("CHECK_NUM_OIDS");
						//ErrorLogger.info("=================== first mibId : "+mibId);
					}

					if (valueId != null && mibId != null) {
						//Logger.write(snmpValue.getDeviceId(),Thread.currentThread().getName(),"DBWriter ����  OidGroup:" + snmpValue.getOidGroup() + " value_id:" + valueId, "", "", "", "");

						if (checkNumOids == null || Integer.parseInt(checkNumOids) <= snmpValue.getOidValueListSize()) {
							JediTransaction tran = JediTransactionManager.getJediTransaction();

							try {
								ListParam lpSchValue = null;
								
								tran.begin();

								if (oidType.equals("M") && snmpValue.getOidGroup().equals("hrSWRunName")) {
									// T_RT_PROCESS - 기존 데이터 삭제
									//ErrorLogger.info("telecaps.delRTValue.process");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.delRTValue.process");
									sqlParam.addValue("device_id",snmpValue.getDeviceId());
									sqlParam.addValue("oid_group",snmpValue.getOidGroup());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();

									// T_RT_PROCESS
									lpSchValue = new ListParam(new String[] {
											"device_id", "oid_group",
											"oid_name", "sub_oid", "value_id",
											"value" });

									for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
										SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

										if (oidValueList != null) {
											for (int j = 0; j < oidValueList.getSize(); j++) {

												if (isHexcode(oidValueList.getValue(j))) {
													lpSchValue.addRow(new Object[] {
															snmpValue.getDeviceId(),
															snmpValue.getOidGroup(),
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															valueId,
															new String(hexToByte(oidValueList.getValue(j).replaceAll(" ", "")))});
													
												} else {
													lpSchValue.addRow(new Object[] {
															snmpValue.getDeviceId(),
															snmpValue.getOidGroup(),
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															valueId,
															oidValueList.getValue(j) });
												}
											}
										}
									}

									//ErrorLogger.info(snmpValue.getDeviceId()+" : telecaps.insRTValue.process");
									//if(sqlParam.getValue("device_id").equals("hpsi-rec02"))ErrorLogger.info("======== hrSWRunName DEVICE_ID : "+snmpValue.getDeviceId()+" / "+"=========OidGroup : "+snmpValue.getOidGroup()+" / ");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insRTValue.process");
									sqlParam.addValue("telecaps.insRTValue.process", lpSchValue);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
								} else if (oidType.equals("M") && snmpValue.getOidGroup().equals("Usage")) {
									// T_VALUE_USAGE_ET �ӽ� ����
									lpSchValue = new ListParam(new String[] {
											"value_id", "oid_name", "sub_oid",
											"value" });
									for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
										SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

										if (oidValueList != null) {
											for (int j = 0; j < oidValueList.getSize(); j++) {
												if (isHexcode(oidValueList.getValue(j))) {
													
													lpSchValue.addRow(new Object[] {
															valueId,
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															new String(hexToByte(oidValueList.getValue(j).replaceAll(" ", "")))});
	
												}else{
												
													lpSchValue.addRow(new Object[] {
															valueId,
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															oidValueList.getValue(j) });
												}
											}
										}
									}
									//ErrorLogger.info(snmpValue.getDeviceId() + "telecaps.insSchValue.usage.et");
									sqlParam.clear();
									//if(sqlParam.getValue("device_id").equals("hpsi-rec02"))ErrorLogger.info("======== Usage DEVICE_ID : "+valueId+" / "+"=========OidGroup : "+" / ");
									sqlParam.setSqlName("telecaps.insSchValue.usage.et");
									sqlParam.addValue("telecaps.insSchValue.usage.et", lpSchValue);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();

									// T_RT_USAGE - ���� ������ ����
									//ErrorLogger.info(snmpValue.getDeviceId() + "telecaps.delRTValue.usage");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.delRTValue.usage");
									sqlParam.addValue("device_id", snmpValue.getDeviceId());
									sqlParam.addValue("oid_group", snmpValue.getOidGroup());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									// T_RT_USAGE
									//ErrorLogger.info(snmpValue.getDeviceId() + "telecaps.insRTValue.usage");
									sqlParam.clear();
									//if(sqlParam.getValue("device_id").equals("hpsi-rec02"))ErrorLogger.info("======= Usage DEVICE_ID : "+snmpValue.getDeviceId()+" / "+"====== OID_GROUP : "+snmpValue.getOidGroup());
									sqlParam.setSqlName("telecaps.insRTValue.usage");
									sqlParam.addValue("device_id", snmpValue.getDeviceId());
									sqlParam.addValue("oid_group", snmpValue.getOidGroup());
									sqlParam.addValue("value_id", valueId);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									// T_VALUE_USAGE
									//ErrorLogger.info("telecaps.insSchValue.usage");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insSchValue.usage");
									sqlParam.addValue("value_id", valueId);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									// T_VALUE
									//ErrorLogger.info("telecaps.insValue");
									//ErrorLogger.info("================= 1.valueID : "+valueId);
									//ErrorLogger.info("================= 1.mibId "+mibId);
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insValue");
									sqlParam.addValue("value_id", valueId);
									sqlParam.addValue("mib_id", mibId);
									sqlParam.addValue("num_values", lpSchValue.rowSize());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									
									// T_VALUE_USAGE_ET - ������ ����
									//ErrorLogger.info(snmpValue.getDeviceId() + "telecaps.del.usage.et");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.del.usage.et");
									sqlParam.addValue("value_id", valueId);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
								} else if (oidType.equals("M") && (snmpValue.getOidGroup().equals("hrProcessorLoad") || snmpValue.getOidGroup().equals("MemoryUsage") || snmpValue.getOidGroup().equals("systemStats"))) {
									// T_RT_PROCESS - 기존 데이터 삭제
									//ErrorLogger.info("telecaps.delRTValue.process");
									
									//if(snmpValue.getOidGroup().equals("systemStats"))
									//{
									//	ErrorLogger.info("1111111111111111111111111111111111telecaps.delRTValue.process");
									//}
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.delRTValue.process");
									sqlParam.addValue("device_id", snmpValue.getDeviceId());
									sqlParam.addValue("oid_group", snmpValue.getOidGroup());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									// T_RT_PROCESS
									lpSchValue = new ListParam(new String[] {
											"device_id", "oid_group",
											"oid_name", "sub_oid", "value_id",
											"value" });
									for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
										SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

										if (oidValueList != null) {
											for (int j = 0; j < oidValueList.getSize(); j++) {
												lpSchValue.addRow(new Object[] {
																snmpValue.getDeviceId(),
																snmpValue.getOidGroup(),
																oidValueList.getOidName(),
																oidValueList.getSubOidv3(j),
																valueId,
																oidValueList.getValue(j) });
											}
										}
									}
									//ErrorLogger.info("telecaps.insRTValue.process");
									//if(sqlParam.getValue("device_id").equals("hpsi-rec02"))ErrorLogger.info("======= DEVICE_ID : "+snmpValue.getDeviceId()+" / "+"====== OID_GROUP : "+snmpValue.getOidGroup());
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insRTValue.process");
									sqlParam.addValue("telecaps.insRTValue.process", lpSchValue);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									
									lpSchValue = new ListParam(
											new String[] { "value_id",
													"oid_name",
													"sub_oid", "value" });
									for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
										SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

										if (oidValueList != null) {
											for (int j = 0; j < oidValueList.getSize(); j++) {
												lpSchValue.addRow(new Object[] {
																valueId,
																oidValueList.getOidName(),
																oidValueList.getSubOidv3(j),
																oidValueList.getValue(j) });
											}
										}
									}
									//ErrorLogger.info("telecaps.insSchValue.min");
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insSchValue.min");
									sqlParam.addValue("telecaps.insSchValue.min", lpSchValue);
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									
									// T_VALUE
									//ErrorLogger.info("telecaps.insValue");
									//ErrorLogger.info("================= 2.valueId : "+valueId);
									//ErrorLogger.info("================= 2.mibId : "+mibId);
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insValue");
									sqlParam.addValue("value_id", valueId);
									sqlParam.addValue("mib_id", mibId);
									sqlParam.addValue("num_values", lpSchValue.rowSize());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									
									// 이력 데이터 저장 여부 체크
									/*sqlParam.clear();
									sqlParam.setSqlName("telecaps.srhNextInsTime");
									sqlParam.addValue("min_interval", 5);
									sqlParam.addValue("device_id", snmpValue.getDeviceId());
									sqlParam.addValue("oid_group", snmpValue.getOidGroup());
									sqlResult = SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									if (sqlResult.getCount() > 0) {
										String insertYN = sqlResult.getListParam("telecaps.srhNextInsTime").getParam(0).getString("INSERT_YN");

										if (insertYN.equals("Y")) {
											// T_VALUE_MIN
											lpSchValue = new ListParam(
													new String[] { "value_id",
															"oid_name",
															"sub_oid", "value" });
											for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
												SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

												if (oidValueList != null) {
													for (int j = 0; j < oidValueList.getSize(); j++) {
														lpSchValue.addRow(new Object[] {
																		valueId,
																		oidValueList.getOidName(),
																		oidValueList.getSubOidv3(j),
																		oidValueList.getValue(j) });
													}
												}
											}
											ErrorLogger.info("telecaps.insSchValue.min");
											sqlParam.clear();
											sqlParam.setSqlName("telecaps.insSchValue.min");
											sqlParam.addValue("telecaps.insSchValue.min", lpSchValue);
											SQLServiceManager.getInstance().execute(sqlParam, tran);
											tran.commit();
											// T_VALUE
											ErrorLogger.info("telecaps.insValue1");
											sqlParam.clear();
											sqlParam.setSqlName("telecaps.insValue");
											sqlParam.addValue("value_id", valueId);
											sqlParam.addValue("mib_id", mibId);
											sqlParam.addValue("num_values", lpSchValue.rowSize());
											SQLServiceManager.getInstance().execute(sqlParam, tran);
											tran.commit();
										}
									}*/
								} else {
									// T_VALUE_HOUR, T_VALUE_MIN, T_VALUE_CALL,
									// T_VALUE_PHONE
									lpSchValue = new ListParam(new String[] {
											"value_id", "oid_name", "sub_oid",
											"value" });

									for (int i = 0; i < snmpValue.getOidValueListSize(); i++) {
										SnmpOidValueList oidValueList = snmpValue.getOidValueList(i);

										if (oidValueList != null) {
											for (int j = 0; j < oidValueList.getSize(); j++) {
												if(isHexcode(oidValueList.getValue(j)) && !oidValueList.getOidName().equals("ifPhysAddress")){
													lpSchValue.addRow(new Object[] {
															valueId,
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															new String(hexToByte(oidValueList.getValue(j).replaceAll(" ", "")))});
												} else {
													lpSchValue.addRow(new Object[] {
															valueId,
															oidValueList.getOidName(),
															oidValueList.getSubOidv3(j),
															oidValueList.getValue(j) });
												}
											}
										}
									}

									sqlParam.clear();

									if (oidType.equals("C") == true) {
										//ErrorLogger.info("telecaps.insSchValue.call");
										sqlParam.setSqlName("telecaps.insSchValue.call");
										sqlParam.addValue("telecaps.insSchValue.call", lpSchValue);									
									} else if (oidType.equals("H") == true) {
										//ErrorLogger.info("telecaps.insSchValue.hour");
										sqlParam.setSqlName("telecaps.insSchValue.hour");
										sqlParam.addValue("telecaps.insSchValue.hour", lpSchValue);
									} else if (oidType.equals("M") == true) {
										//ErrorLogger.info("telecaps.insSchValue.min");
										sqlParam.setSqlName("telecaps.insSchValue.min");
										sqlParam.addValue("telecaps.insSchValue.min", lpSchValue);
									} 

									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
									// T_VALUE
									//ErrorLogger.info("telecaps.insValue");
									//ErrorLogger.info("============ 3.valueId : "+valueId);
									//ErrorLogger.info("============ mibId : "+mibId);
									sqlParam.clear();
									sqlParam.setSqlName("telecaps.insValue");
									sqlParam.addValue("value_id", valueId);
									sqlParam.addValue("mib_id", mibId);
									sqlParam.addValue("num_values",lpSchValue.rowSize());
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									tran.commit();
								}

								// T_MIB ������Ʈ
								//ErrorLogger.info("telecaps.updMibDate");
								sqlParam.clear();
								sqlParam.setSqlName("telecaps.updMibDate");
								sqlParam.addValue("mib_id", mibId);
								sqlParam.addValue("value_id", valueId);
								SQLServiceManager.getInstance().execute(sqlParam, tran);
								tran.commit();

							} catch (Exception e) {
								tran.rollback();
								ErrorLogger.error(e);
							}

							/*Logger.write(snmpValue.getDeviceId(), Thread
									.currentThread().getName(), "DBWriter ����",
									"", "", "", "");
							Logger.write(
									"dbwriter",
									snmpValue.getDeviceId(),
									Thread.currentThread().getName(),
									"DBWriter ����  OidGroup:"
											+ snmpValue.getOidGroup()
											+ " value_id:" + valueId, "", "",
									"");*/
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

			ErrorLogger.debug(Thread.currentThread().getName() + " "
					+ snmpValue.getDeviceId() + " DB VALUE ����  ����");

		} 
	}

	public String getGenericString(int t_vlue) {
		String generictrap = "";
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
				|| value.indexOf("Z") != -1 || value.indexOf("-") != -1
				|| value.length() < 2) {
			return false;
		}
		return true;
	}

	public byte[] hexToByte(String hex) {
		byte bts[] = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}

}
