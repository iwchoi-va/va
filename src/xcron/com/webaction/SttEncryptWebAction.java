package xcron.com.webaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class SttEncryptWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type = ""; // (D: stt_rslt 테이블에서 조회하는경우, M : stt_merge 테이블에서 조회하는 경우, 그외 : stt 암호화하는 경우)
	private String ced_no = "";
	private String ucid = "";
	private String sclf_cd = "";
	private String ctra_insa_clcd = "";
	private String ban_cd = "";
	private String prod_cd = "";
	private String dmbo_cd = "";
	private String _sqlName = "";
	private ListParam DS_RES = null;
	//final static String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
	final static String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
	
	String key = "";
    
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.error("SttDecryptWebAction Start!!");
		type = req.param.getString("type", "E");
		_sqlName = req.param.getString("_sqlName", "");
		ced_no = req.param.getString("CED_NO", "");
		ucid = req.param.getString("UCID", "");
		sclf_cd = req.param.getString("SCLF_CD", "");
		ban_cd = req.param.getString("BAN_CD", "");
		ctra_insa_clcd = req.param.getString("CTRA_INSA_CLCD", "");
		prod_cd = req.param.getString("PROD_CD", "");
		dmbo_cd = req.param.getString("DMBO_CD", "");
		
		//DS_RES = req.param.getListParam("DS_RES");
		
		
		Properties prop = new Properties();
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(PROP_DIR);
			prop.load(new java.io.BufferedInputStream(fis));
			key = prop.getProperty("cipher_key");
			//key256 = "LGUplus_1544ARS_WebService000001";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			if("D".equals(type)) getDecryptStt(res);
			else if("M".equals(type)) getDecryptMergeStt(res);
			
			IVRLogger.error("SttDecryptWebAction Stop!!");
			
		} catch (Exception e) {
			e.getStackTrace();
			IVRLogger.error("#####SttDescryption Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
		
	}
	
	private void getDecryptStt(JediResponse res){
		
		try {
			
			AES256Cipher aes_cipher = AES256Cipher.getInstance(key); 
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName(_sqlName);
			
		
			if(!"".equals(ced_no)) sqlParam.addValue("CED_NO", ced_no);
			if(!"".equals(sclf_cd)) sqlParam.addValue("SCLF_CD", sclf_cd);
			if(!"".equals(ctra_insa_clcd)) sqlParam.addValue("CTRA_INSA_CLCD", ctra_insa_clcd);
			if(!"".equals(ban_cd)) sqlParam.addValue("BAN_CD", ban_cd);
			if(!"".equals(prod_cd)) sqlParam.addValue("PROD_CD", prod_cd);
			if(!"".equals(dmbo_cd)) sqlParam.addValue("DMBO_CD", dmbo_cd);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			
			IVRLogger.info("sttDecryption::sqlResult.getCount()::" + sqlResult.getCount());
			//Aes256 aes256 = new Aes256();
			
			if(sqlResult.getCount() > 0){
				DS_RES = sqlResult.getListParam("DS_RES");
				
				for(int i=0; i< DS_RES.rowSize();i++){
					if(DS_RES.getValue(i, "STT_SENT") != null){
					String stt_sent = DS_RES.getValue(i, "STT_SENT").toString();
					
					ErrorLogger.debug("######STT_SENT = " + DS_RES.getValue(i, "STT_SENT").toString());
					if("Y".equals(DS_RES.getValue(i, "ENC_FLAG").toString())){
						DS_RES.setValue(i, "STT_SENT", aes_cipher.decrypt(stt_sent).split("\t")[2].trim());
					}else{
						DS_RES.setValue(i, "STT_SENT", stt_sent.split("\t")[2].trim());
					}
					}
				}
				
			}
			
			res.param.addValue("DS_RES", DS_RES);
		
		} catch (Exception e) {
			e.getStackTrace();
			IVRLogger.error("#####Sttdecryption Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
	}
	
	private void getDecryptMergeStt(JediResponse res){
		
		try {
				
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName(_sqlName);
			if(!"".equals(ced_no)) sqlParam.addValue("CED_NO", ced_no);
			if(!"".equals(ucid)) sqlParam.addValue("UCID", ucid);
			
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			
			IVRLogger.info("sttDecryption Merge STT::sqlResult.getCount()::" + sqlResult.getCount());
			AES256Cipher aes_cipher = AES256Cipher.getInstance(key); 
			
			if(sqlResult.getCount() > 0){
				DS_RES = sqlResult.getListParam("DS_LIST");
				
				for(int i=0; i< DS_RES.rowSize();i++){
					String stt_content = DS_RES.getValue(i, "CONTENT").toString();
					
					//ErrorLogger.debug("######stt_sent = " + stt_content);
					
					if("Y".equals(DS_RES.getValue(i, "ENC_FLAG").toString())){
						DS_RES.setValue(i, "CONTENT", aes_cipher.decrypt(stt_content));
					}else{
						DS_RES.setValue(i, "CONTENT", stt_content);
					}
				}
				
			}
			
			res.param.addValue("DS_RES", DS_RES);
		
		} catch (Exception e) {
			e.getStackTrace();
			IVRLogger.error("#####Sttdecryption Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
	}
	
}

