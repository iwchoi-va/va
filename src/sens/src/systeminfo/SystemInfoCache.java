package sens.src.systeminfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

public class SystemInfoCache {
	private static SystemInfoCache instance = null;
	
	private static ListParam systemdataList = new ListParam(new String[] { "REC_CNT", "REC_LAST_TIME", "STT_CNT", "STT_LAST_TIME", 
			"META_CNT", "META_LAST_TIME", "ANL_Y_CNT","ANL_LAST_TIME","ANL_N_CNT", "TA_CNT", "TA_LAST_TIME", "SRC_CNT", "SRC_LAST_TIME", "GRD_CNT","GRD_LAST_TIME"
	});
	
	private static ListParam systemdataList_temp = new ListParam(new String[] { "REC_CNT", "REC_LAST_TIME", "STT_CNT", "STT_LAST_TIME", 
			"META_CNT", "META_LAST_TIME", "ANL_Y_CNT","ANL_LAST_TIME","ANL_N_CNT", "TA_CNT", "TA_LAST_TIME", "SRC_CNT", "SRC_LAST_TIME", "GRD_CNT","GRD_LAST_TIME"
	});
	
	private static ListParam systemdatatimeList = new ListParam(new String[] { "DATETIME", "REC_CNT", "STT_CNT", "ANL_N_CNT", "META_CNT", 
			"TA_CNT", "SRC_CNT", "GRD_CNT"
	});
	
	
	
	public static synchronized SystemInfoCache getInstance() throws Exception {
		if (instance == null) {
			synchronized (SystemInfoCache.class) {
				if (instance == null) {
					instance = new SystemInfoCache();
				}
			}
		}

		return instance;
	}
	
	public SystemInfoCache() {
		
	}
	
	public void SystemInfoCacheCall() {
		try {
			
			getvsensdbserver();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private NodeList getXMLParsing(String xml, String tagname) {
		NodeList list = null;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentbuilder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			Document doc = documentbuilder.parse(is);
			
	        Element element = doc.getDocumentElement();
	        list = element.getElementsByTagName(tagname);
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void getvsensdbserver() throws Exception {
		IVRLogger.debug("########getvsebsserver################");
		systemdataList_temp.clear();

		systemdataList_temp.addRow(new Object[] {
				"0",
				"000000",
				"0",
				"000000",
				"0",
				"000000",
				"0",
				"000000",
				"0",
				"0",
				"000000",
				"0",
				"000000",
				"0",
				"000000"
		});
		
		SQLParam sqlParam = new SQLParam();
		sqlParam.setSqlName("msens.xcron.hansol.systeminfocache_1");
		SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
		
		if (sqlResult.getCount() > 0) {
			String v_rec_cnt = sqlResult.getListParam("msens.xcron.hansol.systeminfocache_1").getParam(0).getString("REC_CNT");
			String v_rec_last_time = sqlResult.getListParam("msens.xcron.hansol.systeminfocache_1").getParam(0).getString("REC_LAST_TIME", "000000");
			String v_stt_cnt = sqlResult.getListParam("msens.xcron.hansol.systeminfocache_1").getParam(0).getString("STT_CNT");
			String v_stt_last_time = sqlResult.getListParam("msens.xcron.hansol.systeminfocache_1").getParam(0).getString("STT_LAST_TIME", "000000");
			
			systemdataList_temp.setValue(0, "REC_CNT", v_rec_cnt);
			systemdataList_temp.setValue(0, "REC_LAST_TIME", v_rec_last_time);
			systemdataList_temp.setValue(0, "STT_CNT", v_stt_cnt);
			systemdataList_temp.setValue(0, "STT_LAST_TIME", v_stt_last_time);
			
		}
		
		SQLParam sqlParam1 = new SQLParam();
		sqlParam1.setSqlName("msens.xcron.hansol.systeminfocache_2");
		SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
		
		if (sqlResult1.getCount() > 0) {
			String v_meta_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("META_CNT");
			String v_meta_last_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("META_LAST_TIME","000000");
			String v_ta_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("TA_CNT");
			String v_ta_last_time = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("TA_LAST_TIME", "000000");
			String v_src_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("SRC_CNT");
			String v_src_last_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("SRC_LAST_TIME", "000000");
			String v_anl_n_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("ANL_N_CNT");
			String v_anl_y_cnt = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("ANL_Y_CNT");
			String v_anl_last_time = sqlResult1.getListParam("msens.xcron.hansol.systeminfocache_2").getParam(0).getString("ANL_LAST_TIME", "000000");
			
			
			systemdataList_temp.setValue(0, "META_CNT", v_meta_cnt);
			systemdataList_temp.setValue(0, "META_LAST_TIME", v_meta_last_cnt);
			systemdataList_temp.setValue(0, "TA_CNT", v_ta_cnt);
			systemdataList_temp.setValue(0, "TA_LAST_TIME", v_ta_last_time);
			systemdataList_temp.setValue(0, "SRC_CNT", v_src_cnt);
			systemdataList_temp.setValue(0, "SRC_LAST_TIME", v_src_last_cnt);
			systemdataList_temp.setValue(0, "ANL_N_CNT", v_anl_n_cnt);
			systemdataList_temp.setValue(0, "ANL_Y_CNT", v_anl_y_cnt);
			systemdataList_temp.setValue(0, "ANL_LAST_TIME", v_anl_last_time);
		}
		
		systemdataList.clear();
		
		for(int i=0; i<systemdataList_temp.rowSize(); i++) {
			systemdataList.addRow(systemdataList_temp.getRow(i));
		}
		
		systemdatatimeList.clear();
		
		SQLParam sqlParam2 = new SQLParam();
		sqlParam2.setSqlName("msens.xcron.hansol.systeminfocache_3");
		SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
		
		if (sqlResult2.getCount() > 0) {
			for(int i=0; i<sqlResult2.getCount(); i++) {
				String v_datetime= sqlResult2.getListParam("msens.xcron.hansol.systeminfocache_3").getParam(i).getString("DATETIME");
				String v_rec_cnt = sqlResult2.getListParam("msens.xcron.hansol.systeminfocache_3").getParam(i).getString("REC_CNT");
				String v_stt_cnt = sqlResult2.getListParam("msens.xcron.hansol.systeminfocache_3").getParam(i).getString("STT_CNT");
			
				systemdatatimeList.addRow(new Object[] {
						v_datetime,
						v_rec_cnt,
						v_stt_cnt,
						"0",
						"0",
						"0",
						"0",
						"0"
				});
			}
		}
		
		SQLParam sqlParam3 = new SQLParam();
		sqlParam3.setSqlName("msens.xcron.hansol.systeminfocache_4");
		SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
		
		if (sqlResult3.getCount() > 0) {
			for(int i=0; i<sqlResult3.getCount(); i++) {
				String v_datetime= sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("DATETIME");
				String v_meta_cnt = sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("META_CNT");
				String v_ta_cnt = sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("TA_CNT");
				String v_src_cnt = sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("SRC_CNT");
				String v_grd_cnt = sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("GRD_CNT");
				String v_anl_n_cnt = sqlResult3.getListParam("msens.xcron.hansol.systeminfocache_4").getParam(i).getString("ANL_N_CNT");
		
				int idx = systemdatatimeList.findRow("DATETIME", v_datetime);
				if(idx > -1){
					systemdatatimeList.setValue(idx, "META_CNT", v_meta_cnt);
					systemdatatimeList.setValue(idx, "TA_CNT", v_ta_cnt);
					systemdatatimeList.setValue(idx, "SRC_CNT", v_src_cnt);
					systemdatatimeList.setValue(idx, "GRD_CNT", v_grd_cnt);
					systemdatatimeList.setValue(idx, "ANL_N_CNT", v_anl_n_cnt);
				}else{
					systemdatatimeList.addRow(new Object[] {
							v_datetime,
							"0",
							"0",
							v_anl_n_cnt,
							v_meta_cnt,
							v_ta_cnt,
							v_src_cnt,
							v_grd_cnt
							
					});
				}
			}
		}
		
		IVRLogger.debug("#############systemdataList##############");
		IVRLogger.debug(systemdataList.toString());
		IVRLogger.debug("#############systemdatatimeList################");
		IVRLogger.debug(systemdatatimeList.toString());
		
		
	}
	
	
	public ListParam getSystemdataList() {
		return systemdataList;
	}
	
	public ListParam getSystemdatatimeList() {
		return systemdatatimeList;
	}
	
}
