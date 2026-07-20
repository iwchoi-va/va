package telecaps.common.cfg;

import java.util.ArrayList;
import java.util.List;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;

/**
 * TeleCaps Constants
 * @author tommy
 *
 */
public class TeleCapsConst {
	
	public static final int TRAP_SERVER_PORT = 6005;		//TeleCaps trap port
	public static final String BASIC_SERVER_PORT = "80";		//TeleCaps server basic port
	public static final String TRAP_CLIENT_PATH = "/telecaps/trap/client/";	//java policy file name
	public static final String JPOLICY_NAME = ".java.policy";	//java policy file name
	public static final String CHART_BG_IMG = "img_chart_bg.gif";	//chart bg image
	
	//create chart image file
	public static final String CHART_STATIC_OCC_IMG = "chart_static_occ.png";
	public static final String CHART_CP_OCC_IMG = "chart_cp_occ.png";
	public static final String CHART_SM_OCC_IMG = "chart_sm_occ.png";
	public static final String CHART_IDLE_OCC_IMG = "chart_idle_occ.png";
	public static final String CHART_BHCA_IMG = "chart_bhca.png";
	public static final String CHART_BHCC_IMG = "chart_bhcc.png";
	
	//chart color
	public static final String[] chartColorRGB = new String[]{"6A5ACD","C71585","9ACD32","9370DB","800000","32CD32","588526","B3AA00","008ED6","9D080D","A186BE"};
	public static final String[] chartColor = new String[]{"6970061","13047173","10145074","9662683","8388608","3329330","5801254","11774464","36566","10291213","10585790"};
		
	public static String DEVICE_ID_DESC = "";			//Device ID List (ex: 'hansol01','hansol02')
	public static List DEVICE_LIST = null;
	
	public static String getDEVICE_ID_DESC() {
		if(DEVICE_ID_DESC.length()<=0) 
			getDeviceIDs();
		return DEVICE_ID_DESC;
	}
	
	public static void setDEVICE_ID_DESC(String device_id_desc) {
		DEVICE_ID_DESC = device_id_desc;
	}
	
	public static List getDEVICE_LIST() {
		if(DEVICE_ID_DESC.length()<=0) 
			getDeviceIDs();
		return DEVICE_LIST;
	}
	
	public static void setDEVICE_LIST(List device_list) {
		DEVICE_LIST = device_list;
	}
	
	/**
	 * Device ID List
	 */
	private static void getDeviceIDs() {
		String deviceIDs = "";
		List deviceList = new ArrayList();
		
		try {
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("telecaps.sql.device.list");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			if (sqlResult.getCount() > 0){
				ListParam listData = sqlResult.getListParam("telecaps.res.device.list");
				
				if (listData != null && listData.rowSize() > 0){
					// set data ROW
					for (int iRow = 0; iRow < listData.rowSize(); iRow++) {
						if(iRow >0) deviceIDs += ",";
						deviceIDs += "'"+(String)listData.getValue(iRow,"DEVICE_ID")+"'";
						deviceList.add((String)listData.getValue(iRow,"DEVICE_ID"));
					}
				}
				setDEVICE_ID_DESC(deviceIDs);
				setDEVICE_LIST(deviceList);
			}
			
		} catch (SQLServiceException e) {
			ErrorLogger.error("TeleCapsConst getDeviceIDs SQLServiceException");
			ErrorLogger.error(e);
		} catch (Exception e) {
			ErrorLogger.error("TeleCapsConst getDeviceIDs Exception");
			ErrorLogger.error(e);
		}
	}
	
}
