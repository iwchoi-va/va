package xcron.com.webaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import wfm.com.util.AES256Cipher;
import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class GetRTinfoWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.error("GetRTinfoWebAction Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam callInfoRollback = null;
		
		try {
			ListParam SttInteInfo = new ListParam(new String[] {"FILEID", "UCID", "SP_CODE", "REC_START_DT", "DURATION", 
					"AGENT_ID", "TPANO", "ORG_FILE_FULL_PATH", "STT_FLAG", "STT_ERR_CD", "STT_R_TIME", "BATCH_YN"});
			
			ListParam sttErrorInfo = new ListParam(new String[] {"TPANO", "UCID", "VA_FLAG"});
			
			//Sponsor Callinfo 에서 STT_FLAG IN ('Y', 'E') 콜 조회해오기			
			IVRLogger.info("GetRTinfoWebAction Start!!");
			IVRLogger.info("msens.xcron.hansol.getrealtimeinfo_1");
			//설계번호로 Sponsor_Callinfo에 STT_FLAG = 'Y' AND VA_FLAG = 'N'인 콜 조회
			
			
			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("msens.xcron.hansol.getrealtimeinfo_1");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			callInfoRollback = sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1");
			
			IVRLogger.info("msens.xcron.hansol.getrealtimeinfo_1::count::"+ sqlResult.getCount());
			
			if(sqlResult.getCount()> 0){
				//해당설계번호의 TPANO들 S로 업데이트 하기
				
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam1 =  new SQLParam();  
				sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo_2");
				sqlParam1.addValue("SttInteInfo", sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1")); //call_info에 VA_FLAG S로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam1, tran);
				
				tran.commit();
				
				for(int i =0; i< sqlResult.getCount();i++){
					String v_tpano = sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("TPANO").trim();
					String stt_flag = sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_FLAG").trim();
					String v_ucid   = sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("FILEID").trim();
					
					String[] tpano = v_tpano.split(";");
					
					for(int k=0; k< tpano.length; k++){
						if("".equals(tpano[k])) continue;
						if(tpano[k].length() < 17) continue; // 상품번호+가입설계번호 : 17자리이므로 해당 자리수 보다 작으면 pass
						
						//설계번호의 SPDB 콜 갯수와 Meta 콜 갯수 비교하
						SQLParam sqlParam3 =  new SQLParam();  
						sqlParam3.setSqlName("msens.xcron.hansol.getrealtimeinfo_10");
						sqlParam3.addValue("TPANO", tpano[k].substring(5)); 
						SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);						

						SQLParam sqlParam4 =  new SQLParam();  
						sqlParam4.setSqlName("msens.xcron.hansol.getrealtimeinfo_11");
						sqlParam4.addValue("TPANO", tpano[k].substring(5)); 
						SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
						
						int sp_cnt = 0;
						int meta_cnt = 0;
						String max_date = "";
						
						if (sqlResult3.getCount() > 0)
						{
							sp_cnt = Integer.parseInt(sqlResult3.getListParam("msens.xcron.hansol.getrealtimeinfo_10").getParam(0).getString("CALL_CNT","0"));
						}
						
						if (sqlResult4.getCount() > 0)
						{
							meta_cnt = Integer.parseInt(sqlResult4.getListParam("msens.xcron.hansol.getrealtimeinfo_11").getParam(0).getString("CALL_CNT","0"));
							max_date = sqlResult4.getListParam("msens.xcron.hansol.getrealtimeinfo_11").getParam(0).getString("MAX_DATE","");
						}
						IVRLogger.debug("##############msens.xcron.hansol.getrealtimeinfo_6::max_date::"+max_date +"dateutil::"+DateUtil.getCurrentDate());
						IVRLogger.debug("##############msens.xcron.hansol.getrealtimeinfo_6::max_date::"+sp_cnt +"meta_cnt::"+meta_cnt);
						
						IVRLogger.info("#######ced_no :: " + tpano[k].substring(5) + " spdb count ::" + sp_cnt + " meta count :: " + meta_cnt + "  meta_max_date :: " + max_date);
						
						if (sp_cnt != meta_cnt) 
						{
							if("".equals(max_date) || max_date.equals(DateUtil.getCurrentDate()))
							{
													
								//설계번호에 해당하는 현재 진행중인 분석건이 존재하는지 체크
								SQLParam sqlParam2 =  new SQLParam();  
								sqlParam2.setSqlName("msens.xcron.hansol.getrealtimeinfo_6");
								IVRLogger.info("##############msens.xcron.hansol.getrealtimeinfo_6::CON_ENT_DGN_NO::"+tpano[k].substring(5));
								sqlParam2.addValue("CON_ENT_DGN_NO", tpano[k].substring(5)); 
								SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
								
								String v_bef_ucid = "";
								String v_con_date = "";
								String v_ass_flag = "";
								String v_grd_flag = "";
								String v_src_flag = "";
								
								if(sqlResult2.getCount() > 0)
								{
									v_bef_ucid = sqlResult2.getListParam("msens.xcron.hansol.getrealtimeinfo_6").getParam(0).getString("UCID","");
									v_con_date = sqlResult2.getListParam("msens.xcron.hansol.getrealtimeinfo_6").getParam(0).getString("CON_ENT_DGN_DATE","");
									v_ass_flag = sqlResult2.getListParam("msens.xcron.hansol.getrealtimeinfo_6").getParam(0).getString("ASS_FLAG","");
									v_grd_flag = sqlResult2.getListParam("msens.xcron.hansol.getrealtimeinfo_6").getParam(0).getString("GRD_FLAG","");
									v_src_flag = sqlResult2.getListParam("msens.xcron.hansol.getrealtimeinfo_6").getParam(0).getString("BAN_S_FLAG","");
								}
								
								if("".equals(v_ass_flag))
								{						
									SttInteInfo.addRow(new Object[] {
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("FILEID"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("UCID"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("SP_CODE"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("REC_START_DT"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("DURATION"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("AGENT_ID"),
											tpano[k],
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("ORG_FILE_FULL_PATH"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_FLAG"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_ERR_CD"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_R_TIME"),
											sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("BATCH_YN")
									});
								} else {
									IVRLogger.info("### CON_ENT_DGN_NO :: " + tpano[k].substring(5) + " :: meta_ass_flag :: " + v_ass_flag + " :: meta_grd_flag :: " + v_grd_flag + " :: meta ban_s_flag :: " + v_src_flag);
									if("Y".equals(v_ass_flag) && "Y".equals(v_grd_flag) && "Y".equals(v_src_flag))
									{
										SttInteInfo.addRow(new Object[] {
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("FILEID"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("UCID"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("SP_CODE"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("REC_START_DT"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("DURATION"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("AGENT_ID"),
												tpano[k],
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("ORG_FILE_FULL_PATH"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_FLAG"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_ERR_CD"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("STT_R_TIME"),
												sqlResult.getListParam("msens.xcron.hansol.getrealtimeinfo_1").getParam(i).getString("BATCH_YN")
										});
									} else {
										stt_flag = "R";
									}
								}
							} else {
								stt_flag = "B";
							}
						} else {
							IVRLogger.info("### CON_ENT_DGN_NO :: " + tpano[k].substring(5) + " :: meta_max_date :: " + max_date + " :: 보완 처리");
							stt_flag = "Y";
						}
						
					}
					
					/*
					 * flag 구분항목
					 * STT_FLAG - E : STT에서 텍스트 변환 작업에서 오류 발생
					 * 			- R : 현재 META에서 분석이 진행 중이므로 수집을 임시 보류 처리
					 * 			- B : 보안콜(META 생성 시점이 오늘이 아닌 경우 보안콜 처리
					 * */
					//IVRLogger.debug("##############msens.xcron.hansol.getrealtimeinfo_1::stt_flag::"+stt_flag);	
					String v_flag = "Y";
					
					if(("E".equals(stt_flag)) || ("R".equals(stt_flag))) v_flag = "N";
					else if ("B".equals(stt_flag)) v_flag = "B";
					else v_flag = "Y";
					
					sttErrorInfo.addRow(new Object[] {
							v_tpano,
							v_ucid,
							v_flag
					});
				}
				
				IVRLogger.debug("##############msens.xcron.hansol.getrealtimeinfo_1::BEF_META::COUNT::"+SttInteInfo.rowSize());
				tran.begin();
				
				if(SttInteInfo.rowSize() > 0){
					//기존 분석데이터 삭제(나중에 들어온 정보과 함께 재분석을 진행하기 위함
					SQLParam sqlParam3 =  new SQLParam();  
					sqlParam3.setSqlName("msens.xcron.hansol.getrealtimeinfo_9");
					sqlParam3.addValue("SttInteInfo", SttInteInfo); 
					
					SQLServiceManager.getInstance().execute(sqlParam3, tran);
					
					IVRLogger.info("##############msens.xcron.hansol.getrealtimeinfo_9");
					
					//해당 데이터 MS_STT_BEF_META 테이블에 저장하기(MERGE INTO 형태로)
					SQLParam sqlParam2 =  new SQLParam();  
					sqlParam2.setSqlName("msens.xcron.hansol.getrealtimeinfo_3");
					sqlParam2.addValue("SttInteInfo", SttInteInfo); 
					
					SQLServiceManager.getInstance().execute(sqlParam2, tran);	
					
				}
				IVRLogger.info("##############msens.xcron.hansol.getrealtimeinfo_7::"+ sttErrorInfo.rowSize());
				
				//IVRLogger.info(sttErrorInfo.toString());
				if(sttErrorInfo.rowSize() > 0)
				{
					for(int j=0; j< sttErrorInfo.rowSize(); j++)
					{

						//보안 콜인 경우 stt가 암호화가 안되어있을 수도 있기때문에 암호화할지 체크해서 암호화를 진행해야함
						if("B".equals(sttErrorInfo.getValue(j, "VA_FLAG"))) 
						{	
							
							setEncrypt(sttErrorInfo.getValue(j, "UCID").toString());

							/*SttEncryptContent.clear();
							SttEncryptSent.clear();

							//암호화대상 UCID 대화내용 조회
							SQLParam sqlParam3 =  new SQLParam();  
							sqlParam3.setSqlName("msens.xcron.hansol.getrealtimeinfo_7");
							sqlParam3.addValue("UCID", sttErrorInfo.getValue(j, "UCID")); 
							
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3, tran);
							String v_content = "";
							String enc_flag = "";
							
							if(sqlResult3.getCount() > 0)
							{
								v_content= sqlResult3.getListParam("msens.xcron.hansol.getrealtimeinfo_7").getParam(0).getString("CONTENT","");
								enc_flag = sqlResult3.getListParam("msens.xcron.hansol.getrealtimeinfo_7").getParam(0).getString("ENC_FLAG","N");
								
								if(!"".equals(v_content) && "N".equals(enc_flag))
								{	
									sqlParam.clear();
									sqlParam.setSqlName("msens.xcron.hansol.setsttencryptionwebaction_6");
									sqlParam.addValue("UCID", sttErrorInfo.getValue(j, "UCID"));
									
									SQLServiceManager.getInstance().execute(sqlParam, tran);
									
									
									AES256Cipher aes_cipher = AES256Cipher.getInstance(key);
									String enc_content = aes_cipher.encrypt(v_content);
									
									SttEncryptContent.addRow(new Object[] {
											sttErrorInfo.getValue(j, "UCID"),
											enc_content
									});
								}
							}
							
							//암호화대상 UCID 대화내용 조회
							SQLParam sqlParam4 =  new SQLParam();  
							sqlParam4.setSqlName("msens.xcron.hansol.getrealtimeinfo_8");
							sqlParam4.addValue("UCID", sttErrorInfo.getValue(j, "UCID")); 
							
							SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4, tran);
							
							if(sqlResult4.getCount() > 0)
							{
								for(int k=0; k< sqlResult4.getCount(); k++)
								{
									String v_sent = sqlResult4.getListParam("msens.xcron.hansol.getrealtimeinfo_8").getParam(k).getString("STT_SENT","");									
									
									//IVRLogger.debug(enc_flag + " :: " + v_sent); 
									if(!"".equals(v_sent) && "N".equals(enc_flag))
									{	
										AES256Cipher aes_cipher = AES256Cipher.getInstance(key);
										String enc_sent = aes_cipher.encrypt(v_sent);
										
										SttEncryptSent.addRow(new Object[] {
												sqlResult4.getListParam("msens.xcron.hansol.getrealtimeinfo_8").getParam(k).getString("UCID"),
												sqlResult4.getListParam("msens.xcron.hansol.getrealtimeinfo_8").getParam(k).getString("STT_SENT_ID"),
												enc_sent
										});
									}
								}
							}
							
							// sent 정보 암호화
							if(SttEncryptSent.rowSize()>0){
								IVRLogger.info("msens.xcron.hansol.setsttencryptionwebaction_5 :: count ::" + SttEncryptSent.rowSize());
								
								sqlParam.clear();
								sqlParam.setSqlName("msens.xcron.hansol.setsttencryptionwebaction_5");
								sqlParam.addValue("SttEncryptSent", SttEncryptSent);
								
								SQLServiceManager.getInstance().execute(sqlParam, tran);
							}
							
							//IVRLogger.info("msens.xcron.hansol.setsttencryptionwebaction_4 :: count ::" + SttEncryptContent.rowSize());
							
							if(SttEncryptContent.rowSize()>0){			
								
								IVRLogger.info("msens.xcron.hansol.setsttencryptionwebaction_4 :: count ::" + SttEncryptContent.rowSize());
								
								sqlParam.clear();
								sqlParam.setSqlName("msens.xcron.hansol.setsttencryptionwebaction_4");
								sqlParam.addValue("SttEncryptContent", SttEncryptContent);
								
								SQLServiceManager.getInstance().execute(sqlParam, tran);
								
							}*/
						}						
					}
					
					//IVRLogger.info("msens.xcron.hansol.setsttencryptionwebaction_5 :: count ::" + SttEncryptSent.rowSize());
					
					//해당 데이터 중 STT_FLAG가 E인 콜은 다시 읽기위해 VA_FLAG='N'으로 Y인 경우는 VA_FLAG='Y'로 업데이트
					SQLParam sqlParam2 =  new SQLParam(); 
					sqlParam2.clear();
					sqlParam2.setSqlName("msens.xcron.hansol.getrealtimeinfo_4");
					sqlParam2.addValue("SttInteInfo", sttErrorInfo); 
					
					SQLServiceManager.getInstance().execute(sqlParam2, tran);
				}
				
				tran.commit();

			}
			
			IVRLogger.info("GetRTinfoWebAction end!!");
			
			
		} catch (Exception e) {
			tran.rollback();
			
			
			//callInfoRollback
			if(callInfoRollback != null){
				if(callInfoRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setgraderesultwebaction_14
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.getrealtimeinfo_5");
						sqlParam5.addValue("callInfoRollback", callInfoRollback);
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
			IVRLogger.error("GetRTInfoWebAction Error :: "+e.getMessage());
		}
		
	}
	
	public void setEncrypt(String ucid){
		ListParam SttEncryptContent = new ListParam(new String[] {"UCID", "CONTENT"});
		ListParam SttEncryptSent = new ListParam(new String[] {"UCID", "STT_SENT_ID","STT_SENT"});
		
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		String key = "";
		//String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
		String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
		
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
		
		try{
			
			IVRLogger.info("msens.xcron.hansol.getrealtimeinfo.encrypt_1::"+ucid);
			//암호화대상 UCID 대화내용 조회
			// 보완콜이어도 SPLIT이안된 CONTENT는 암호화를 안하기 위해 쿼리에서 SPLIT_FLAG = 'Y'인 데이터만 조회되도록 처리한다.
			SQLParam sqlParam1 =  new SQLParam();  
			sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_1");
			sqlParam1.addValue("UCID", ucid); 
			
			SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
			
			//SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1, tran);
			
			if(sqlParam1.getCount() > 0){
				
				String v_content= sqlResult1.getListParam("msens.xcron.hansol.getrealtimeinfo.encrypt_1").getParam(0).getString("CONTENT","");
				String enc_flag = sqlResult1.getListParam("msens.xcron.hansol.getrealtimeinfo.encrypt_1").getParam(0).getString("ENC_FLAG","N");
				
				IVRLogger.debug("ucid :: " + ucid + " // ENC_FLAG :: " + enc_flag);
				if(!"".equals(v_content) && "N".equals(enc_flag)){
					
					IVRLogger.info("msens.xcron.hansol.getrealtimeinfo.encrypt_2"); 
					
					tran.begin();
					//암호화 대상자 S로 업데이트
					sqlParam1.clear();
					sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_2");
					sqlParam1.addValue("UCID", ucid);
					
					SQLServiceManager.getInstance().execute(sqlParam1, tran);
					
					tran.commit();
					
					AES256Cipher aes_cipher = AES256Cipher.getInstance(key);
					String enc_content = aes_cipher.encrypt(v_content);
					
					SttEncryptContent.addRow(new Object[] {
						ucid,
						enc_content
					});
					
					IVRLogger.info("msens.xcron.hansol.getrealtimeinfo.encrypt_3");
					//암호화대상 UCID 대화내용 조회
					sqlParam1.clear();
					sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_3");
					sqlParam1.addValue("UCID", ucid); 
					
					sqlResult1 =  SQLServiceManager.getInstance().execute(sqlParam1);
					
					
					if(sqlResult1.getCount() > 0)
					{
						for(int k=0; k< sqlResult1.getCount(); k++)
						{
							String v_sent = sqlResult1.getListParam("msens.xcron.hansol.getrealtimeinfo.encrypt_3").getParam(k).getString("STT_SENT","");									
							
							//IVRLogger.debug(enc_flag + " :: " + v_sent); 
							if(!"".equals(v_sent))
							{	
								String enc_sent = aes_cipher.encrypt(v_sent);
								
								SttEncryptSent.addRow(new Object[] {
										sqlResult1.getListParam("msens.xcron.hansol.getrealtimeinfo.encrypt_3").getParam(k).getString("UCID"),
										sqlResult1.getListParam("msens.xcron.hansol.getrealtimeinfo.encrypt_3").getParam(k).getString("STT_SENT_ID"),
										enc_sent
								});
							}
						}
					}
					
					tran.begin();
					if(SttEncryptSent.rowSize()>0){
						IVRLogger.info("msens.xcron.hansol.getrealtimeinfo.encrypt_4 :: count ::" + SttEncryptSent.rowSize());
						
						sqlParam1.clear();
						sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_4");
						sqlParam1.addValue("SttEncryptSent", SttEncryptSent);
						
						SQLServiceManager.getInstance().execute(sqlParam1, tran);
					}
					
					//IVRLogger.info("msens.xcron.hansol.setsttencryptionwebaction_4 :: count ::" + SttEncryptContent.rowSize());
					
					if(SttEncryptContent.rowSize()>0){			
						
						IVRLogger.info("msens.xcron.hansol.getrealtimeinfo.encrypt_5 :: count ::" + SttEncryptContent.rowSize());
						
						sqlParam1.clear();
						sqlParam1.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_5");
						sqlParam1.addValue("SttEncryptContent", SttEncryptContent);
						
						SQLServiceManager.getInstance().execute(sqlParam1, tran);
						
					}
					
					tran.commit();
					
				}
				
			}
			
			IVRLogger.info("##########msens.xcron.hansol.getrealtimeinfo.encrypt end#################");
		}catch(Exception e){
			tran.rollback();
			
			try{
				tran.begin();
				
				SQLParam sqlParam2 =  new SQLParam();  
				sqlParam2.setSqlName("msens.xcron.hansol.getrealtimeinfo.encrypt_6");
				sqlParam2.addValue("UCID", ucid);
				
				SQLServiceManager.getInstance().execute(sqlParam2, tran);
			
				tran.commit();
			}catch(Exception e1){
				tran.rollback();
			}
			
			IVRLogger.error("getRTInfoWebAction 보완콜 Encrypt Error :: " + e.getMessage());
			IVRLogger.info("getRTInfoWebAction 보완콜 Encrypt Error :: " + e.getMessage());
		}
		
		
	}
	
}




