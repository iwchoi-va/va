package xcron.com.webaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import cs.com.util.DateUtil;

public class SttBulkBatchWebAction extends XwingWebAction {
	/**
	 * 일요일 하루만 일괄 암호화 수행할 수 있는 java
	 */
	private static final long serialVersionUID = 1L;
//	final static String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
	final static String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
	private String key = "";
    private String sdate = "";
    private String edate = "";
    
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.info("SttBulkBatchWebAction Start!!");

		sdate = req.param.getString("from_date","");
		edate = req.param.getString("to_date","");
	
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
			
			IVRLogger.debug("sdate = " + sdate  + "// edate = " + edate + "// " + DateUtil.getDayAdd(DateUtil.getCurrentDate(),"yyyyMMdd" , -7));
			if(sdate != null && !"".equals(sdate)) sdate = sdate + "000000";
			else sdate = DateUtil.getDayAdd(DateUtil.getCurrentDate(),"yyyyMMdd" , -7)+"000000";
			if(edate != null && !"".equals(edate)) edate = edate + "235959";
			else edate = DateUtil.getDayAdd(DateUtil.getCurrentDate(),"yyyyMMdd" , -1) + "235959";
			
			setSttEncrypt();
			
			
			IVRLogger.info("SttBulkBatchWebAction End!!");
			
		} catch (Exception e) {
			e.getStackTrace();
			IVRLogger.error("#####SttBatchWebAction Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
		
	}

	private void setSttEncrypt(){
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		
		ListParam SttEnctyptContent = new ListParam(new String[] {"UCID", "CONTENT"});
		ListParam SttEncryptSent = new ListParam(new String[] {"UCID", "STT_SENT_ID","STT_SENT"});
		ListParam EncryptRollback = null;
		
		try {
				
				SQLParam sqlParam2 = new SQLParam();
				sqlParam2.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_2");
				sqlParam2.addValue("sdate", sdate);
				sqlParam2.addValue("edate", edate);
				SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
				
				EncryptRollback = sqlResult2.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_2");
				
				tran.begin();
				
				//enc_flag를 S로 업데이트
				SQLParam sqlParam3 = new SQLParam();
				sqlParam3.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_3");
				sqlParam3.addValue("SttEnctyptContent", EncryptRollback);
				
				SQLServiceManager.getInstance().execute(sqlParam3, tran);
				
				tran.commit();
				
				AES256Cipher aes_cipher = AES256Cipher.getInstance(key); 
				
				IVRLogger.debug("###########count :: " + sqlResult2.getCount() + "############");
				if(sqlResult2.getCount() > 0){

					tran.begin();
					
					for(int j =0; j< sqlResult2.getCount(); j++){
						SttEnctyptContent.clear();
						SttEncryptSent.clear();
						
						IVRLogger.info(sqlResult2.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_2").getParam(j).getString("UCID"));
						
						String v_content = sqlResult2.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_2").getParam(j).getString("CONTENT");
						String enc_content = aes_cipher.encrypt(v_content);
						
						SttEnctyptContent.addRow(new Object[] {
								sqlResult2.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_2").getParam(j).getString("UCID"),
								enc_content
						});
						
							
						String ucid = sqlResult2.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_2").getParam(j).getString("UCID");
						IVRLogger.debug("msens.xcron.hansol.setbulkbatchencwebaction_4 START");
						
						sqlParam3.clear();
						sqlParam3.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_4");
						sqlParam3.addValue("ucid", ucid);
						SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
						
						//IVRLogger.debug("SQL3 COUNT =  "+ sqlResult3.getCount());
						if(sqlResult3.getCount() > 0){
							for(int k=0; k< sqlResult3.getCount(); k++){
								String v_sent = sqlResult3.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_4").getParam(k).getString("STT_SENT");
								String enc_sent = aes_cipher.encrypt(v_sent);
									
								SttEncryptSent.addRow(new Object[] {
										sqlResult3.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_4").getParam(k).getString("UCID"),
										sqlResult3.getListParam("msens.xcron.hansol.setbulkbatchencwebaction_4").getParam(k).getString("STT_SENT_ID"),
										enc_sent
								});
									
							}
						}	
						
						
						SQLParam sqlParam4 = new SQLParam();
							
						if(SttEncryptSent.rowSize()>0){
							IVRLogger.info("msens.xcron.hansol.setbulkbatchencwebaction_5 :: count ::" + SttEncryptSent.rowSize());
							
							//sqlParam4.clear();
							sqlParam4.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_5");
							sqlParam4.addValue("SttEncryptSent", SttEncryptSent);
							
							SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
						}
						
						if(SttEnctyptContent.rowSize()>0){
								IVRLogger.info("msens.xcron.hansol.setbulkbatchencwebaction_6 :: count ::" + SttEnctyptContent.rowSize());
								
								sqlParam4.clear();
								sqlParam4.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_6");
								sqlParam4.addValue("SttEnctyptContent", SttEnctyptContent);
								
								SQLServiceManager.getInstance().execute(sqlParam4, tran);
					
						}
						
					
					}
					
					tran.commit(); 

				}

		} catch (Exception e) {
			tran.rollback();
			
			if(EncryptRollback != null){
				if(EncryptRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setscriptresultwebaction_12
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.setbulkbatchencwebaction_7");
						sqlParam5.addValue("EncryptRollback", EncryptRollback);
						SQLServiceManager.getInstance().execute(sqlParam5, tran);
						
						tran.commit();
					} catch (SQLServiceException e1) {
						// TODO Auto-generated catch block
						tran.rollback();
						e1.printStackTrace();
					}
									
				}
			}
			
			e.getStackTrace();
			IVRLogger.error("#####SttBatchWebAction Encrypt Error Message = "+e.getMessage());
			IVRLogger.error(e.getStackTrace());
		}
	}
	
}

