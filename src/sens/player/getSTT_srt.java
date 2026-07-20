package sens.player;

 
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import wfm.com.util.AES256Cipher;

import com.hansol.audio.util.PropertyUtil;
import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class getSTT_srt extends XwingWebAction {
	
	private String ucid = "";
	private String start_time = "";
	private String type = "";
	private Param result = null;
	private String aes_key = "";
	
	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {
		
		 
		HttpServletRequest request = req.getHttpServletRequest();
		ucid = req.param.getString("rec_key"); 
		start_time = req.param.getString("start_time");  
		type = req.param.getString("type", "");
		
		CommonDTO common = new CommonDTO(ucid);
		
		try{	
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("getContent.sel_js");
			sqlparam.setResultName("data");
			sqlparam.addValue("ucid", ucid);
			//sqlparam.addValue("start_time", start_time);
			sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
			
			if(sqlparam.getListParam("data").rowSize() > 0) result = sqlparam.getListParam("data").getParam(0);
			else {
				// STT 데이터가 없는 경우는 대비하여
				JSONParser parser = new JSONParser();
				JSONArray jArray = (JSONArray) parser.parse("[{\"SYNC\":\"0\",\"DUR\":\"0\",\"CONTENT\":\"변환된 텍스트가 존재하지 않습니다.\"}]");
				
				res.param.addValue( "data",  jArray);
				
			}
			
			Properties prop = new Properties();
			
			//prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/webapps/WEB-INF/conf.properties");
			prop = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/WEB-INF/conf.properties");
			aes_key = prop.getProperty("cipher_key");
			
			
		} catch(Exception e){
			throw new WebActionException("fail",e);
		}
		
		if(result != null){
			if("".equals(type)){
				getPlayerSTT(res);
			}else{
				getSciptSTT(res);
			}
		}
	}
	
	public void getPlayerSTT(JediResponse res)  throws WebActionException {
		// FILE Info
					res.param.addValue( "rec_key", result.getString( "doc_id", "" ) );
					res.param.addValue( "duration", result.getInt( "duration", 0 ) );
					res.param.addValue( "agent_id", result.getString( "user_id", "" ) );
					res.param.addValue( "regist_no", result.getString( "regist_no", "" ) );
					res.param.addValue( "cust_name", result.getString( "cust_name", "" ) );
					res.param.addValue( "contact_id", result.getString( "contact_id", "" ) );
					
					
					//주민번호 복호화
					String encRegNo = result.getString( "regist_no", "" );
					//String decRegNo = SafeDBUtil.sdbDecrypt("ENC_REG_NO", encRegNo);
					String decRegNo = encRegNo;
					res.param.addValue( "regist_no", decRegNo );
					ErrorLogger.debug("(0)decRegNo:"+decRegNo);
					//convert clob to string
					//Clob tmp = (Clob) result.getValue( "content", "" );
					String tmp = result.getString( "content", "" );
					String enc_flag = result.getString( "enc_flag", "N" );
					String rtx_gb = result.getString( "rxtx_gb", "N" );
					
					AES256Cipher aes256;
					try {
						aes256 = AES256Cipher.getInstance(aes_key);
						if("Y".equals(enc_flag)) tmp = aes256.decrypt(tmp);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ErrorLogger.debug("(1)=======================================");
					// STT 복호화
					String encContent = "";
					String decContent = "";
					/*if(tmp.length() > 0){
					 encContent = tmp.substring(1, (int) tmp.length());
					//encContent = tmp.getSubString(1, (int) tmp.length());
					//decContent = SafeDBUtil.sdbDecrypt("ENC_ADDR", encContent);
					ErrorLogger.debug("(2)=======================================");
//					System.out.println(decContent);
					}*/
					
					// CONTENT JsonArray
					JSONArray jArray = new JSONArray();	
					//String[] tmpArray = decContent.split("\t");
					String[] tmpArray = tmp.split("\n");
					ErrorLogger.debug("(3)=======================================");

					for(int i =0 ; i< tmpArray.length; i++){
						JSONObject jsonObject = new JSONObject();
						
						/*if(tmpArray[i].indexOf("|") > -1 ){ //화자분리인 경우
							res.param.addValue("RTX","Y");
							String[] sent = tmpArray[i].split("\\|");
							
							String[] x = sent[1].split("\t");
							jsonObject.put( "GB", sent[0]);
							jsonObject.put( "SYNC", x[0] );
							jsonObject.put( "DUR", Integer.parseInt(x[1].trim())-Integer.parseInt(x[0].trim()) );
							jsonObject.put( "CONTENT",  x[2] );
						}else{ //화자분리 아닌 경우
							res.param.addValue("RTX","N");
							String[] x = tmpArray[i].split("\t");
							jsonObject.put( "SYNC", x[0] );
							jsonObject.put( "DUR", Integer.parseInt(x[1].trim())-Integer.parseInt(x[0].trim()) );
							jsonObject.put( "CONTENT",  x[2].trim() );
						}*/
						
						
						String[] sent = tmpArray[i].split("\\|");
						
						String[] x = sent[1].split("\t");
						jsonObject.put( "GB", sent[0]);
						jsonObject.put( "SYNC", x[0] );
						jsonObject.put( "DUR", Integer.parseInt(x[1].trim())-Integer.parseInt(x[0].trim()) );
						jsonObject.put( "CONTENT",  x[2] );
						
						if("Y".equals(rtx_gb)) res.param.addValue("RTX","Y");
						else res.param.addValue("RTX","N");
						
						jArray.add(jsonObject);
					}
					/*int i=0;
					while(i<tmpArray.length-1){
						JSONObject jsonObject = new JSONObject();
						
						String[] x = tmpArray[i].split("\t");
						
						jsonObject.put( "SYNC", x[i] );
						jsonObject.put( "DUR", Integer.parseInt(x[i+1])-Integer.parseInt(x[i]) );
						jsonObject.put( "CONTENT",  x[i+2] );
						
						jArray.add(jsonObject);
						i += 2;
					}*/
					res.param.addValue( "data", jArray );
					
	}
	
	public void getSciptSTT(JediResponse res) throws WebActionException {
		
		 ListParam list  =  new ListParam(new String[] {"SYNC", "CONTENT"}); 
		 
		 String tmp = result.getString( "content", "" );
		 tmp =  tmp.substring(1, (int) tmp.length());
		 String[] tmpArray = tmp.split("\n");
		 
		 for(int i =0 ; i< tmpArray.length; i++){
			 String[] x = tmpArray[i].split("\t");
			 list.addRow(new Object[] {
					 x[0],
					 x[2].trim()
			 });
				
		}
		 
		res.param.addValue("DS_SCRIPT", list);
		 
	}
	
};
