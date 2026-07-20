package sens.service.webaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import wfm.com.util.AES256Cipher;
import jedix.xwing.action.XwingWebAction;

public class MentDecryptWebAction extends XwingWebAction {
	ListParam DS_LIST = null;
	private static final long serialVersionUID = 1L;
	
	//final static String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
	final static String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
	
	String key = "";
	JediTransaction tran = JediTransactionManager.getJediTransaction();
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		
		String sdate = req.param.getString("SDATE"); 
		String edate = req.param.getString("EDATE"); 
		String ced_no = req.param.getString("CED_NO"); 
		String istp_cd = req.param.getString("ISTP_CD"); 
		String prod_cd = req.param.getString("PROD_CD"); 
		String dmbo_cd = req.param.getString("DMBO_CD"); 
		String org1_cd = req.param.getString("ORG1_CD"); 
		String org2_cd = req.param.getString("ORG2_CD"); 
		String org3_cd = req.param.getString("ORG3_CD"); 
		String user_id = req.param.getString("USER_ID"); 
		String _start = req.param.getString("_START"); 
		String _pageSize = req.param.getString("_PAGESIZE"); 
		
		
		try {
			
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
			
			AES256Cipher aes_cipher = AES256Cipher.getInstance(key); 

			
			SQLParam sqlparam = new SQLParam();
			SQLParam sqlparam2 = new SQLParam();
			
			sqlparam.setSqlName("oba.oba080.getMentDetail.cnt.sel");	
			sqlparam.addValue("SDATE",  sdate);
			sqlparam.addValue("EDATE",  edate);
			sqlparam.addValue("CED_NO",  ced_no);
			sqlparam.addValue("ISTP_CD",  istp_cd);
			sqlparam.addValue("PROD_CD",  prod_cd);
			sqlparam.addValue("DMBO_CD",  dmbo_cd);
			sqlparam.addValue("ORG1_CD",  org1_cd);
			sqlparam.addValue("ORG2_CD",  org2_cd);
			sqlparam.addValue("ORG3_CD",  org3_cd);
			sqlparam.addValue("USER_ID",  user_id);
			sqlparam.addValue("_START",  _start);
			sqlparam.addValue("_PAGESIZE",  _pageSize);
			SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			
			sqlparam2.setSqlName("oba.oba080.getMentDetail.sel");			
			sqlparam2.addValue("SDATE",  sdate);
			sqlparam2.addValue("EDATE",  edate);
			sqlparam2.addValue("CED_NO",  ced_no);
			sqlparam2.addValue("ISTP_CD",  istp_cd);
			sqlparam2.addValue("PROD_CD",  prod_cd);
			sqlparam2.addValue("DMBO_CD",  dmbo_cd);
			sqlparam2.addValue("ORG1_CD",  org1_cd);
			sqlparam2.addValue("ORG2_CD",  org2_cd);
			sqlparam2.addValue("ORG3_CD",  org3_cd);
			sqlparam2.addValue("USER_ID",  user_id);
			sqlparam2.addValue("_START",  _start);
			sqlparam2.addValue("_PAGESIZE",  _pageSize);
			SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
			DS_LIST = sqlresult2.getListParam("DS_LIST");
			String stt_sent = ""; 
			if(sqlresult2.getCount() > 0){
				for(int i = 0; i < sqlresult2.getCount() ; i ++){
					if(DS_LIST.getValue(i, "CED_NO") == null){
						DS_LIST.setValue(i, "CED_NO",ced_no);
					}
					if(DS_LIST.getValue(i, "STT_MENT") != null){
						stt_sent = DS_LIST.getValue(i, "STT_MENT").toString();
						if("Y".equals(DS_LIST.getValue(i, "ENC_FLAG").toString())){
							DS_LIST.setValue(i, "STT_MENT", aes_cipher.decrypt(stt_sent).split("\t")[2].trim());
						}else{
							DS_LIST.setValue(i, "STT_MENT",stt_sent.split("\t")[2].trim());
						}
					}
				}		
			}
			
			res.param.addValue("DS_CNT_LIST", sqlresult.getListParam("DS_CNT_LIST"));
			res.param.addValue("DS_LIST", DS_LIST);
			

				} catch (Exception e) {
					// TODO: handle exception
					tran.rollback();
					IVRLogger.info("GET MENT SCORE STT ERROR");
					IVRLogger.info(e.getMessage());
					e.printStackTrace();
				}
			
			}
	
};
