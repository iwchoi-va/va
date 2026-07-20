package com.hansol.dbenc.util;

import com.initech.safedb.SimpleSafeDB;
import com.initech.safedb.common.SafeDBException;

public class SafeDBUtil {
	public static String sdbEncrypt(String columnName, String data) throws SafeDBException {
		SimpleSafeDB ssdb = SimpleSafeDB.getInstance();
		
		if ((ssdb.login()) && (data != null) && (!"".equals(data))) {	
			byte[] plainData = data.getBytes();
			
			byte[] encryptedData = ssdb.encrypt("SAFEDB", "SAFEDB.POLICY", columnName, plainData);

			debugLog(">> sdbEncrypt():");
			debugLog(">> \tOrignal data(plain data): " + data);
			debugLog(">> \tEncrypted data: " + new String(encryptedData));
			return new String(encryptedData);
		}
		return data;
	}
	
	public static String sdbDecrypt(String columnName, String data) throws SafeDBException {
		SimpleSafeDB ssdb = SimpleSafeDB.getInstance();
		if ((ssdb.login()) && (data != null) && (!"".equals(data))) {
			byte[] encryptedData = data.getBytes();

			byte[] decryptedData = ssdb.decrypt("SAFEDB", "SAFEDB.POLICY", columnName, encryptedData);

			debugLog(">> sdbDecrypt():");
			debugLog(">> \tOrignal data(encrypted data): " + data);
			debugLog(">> \tDecrypted data: " + new String(decryptedData));
			return new String(decryptedData);
		}

		if ((data == null) || ("".equals(data))) {
			data = "";
		}

		return data;
	}
			    
	private static void debugLog(String message) {
		if (isDebugMode())
			System.out.println(message);
	}
	
	private static boolean isDebugMode() {
		return false;
	}
}
