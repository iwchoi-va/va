package sens.player;

 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import org.json.simple.JSONArray;

import wfm.com.util.AES256Cipher;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.hansol.audio.util.PropertyUtil;
import com.initech.core.util.URLDecoder;
import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class getCallInfo extends XwingWebAction {

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		
		HttpServletRequest request = req.getHttpServletRequest();
		String ucid = req.param.getString("rec_key"); 
		String ced_no = req.param.getString("ced_no"); 
		String cmd = req.param.getString("cmd"); 
		Map svc_map = req.param.getMap();
		String aes_key = "";

		Properties prop2 = new Properties();
		
		//prop2 = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/webapps/WEB-INF/conf.properties");
		prop2 = PropertyUtil.loadProperty(System.getProperty("jedi.home")+ "/WEB-INF/conf.properties");
		aes_key = prop2.getProperty("cipher_key");
		
		CommonDTO common = new CommonDTO(ucid);
		ErrorLogger.debug(common);

		try{	
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setResultName("data");
			
			ErrorLogger.error("########getCallInfo.java##########"+cmd);
			
			if(cmd.equals("call_info")){
				sqlparam.setSqlName("rec.player.getCallInfo.sel");
				sqlparam.addValue("UCID", ucid);
				sqlparam.addValue("CED_NO", ced_no);
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
				ErrorLogger.error("########sqlparam##########"+sqlparam.getListParam("data"));
			//	SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				
				if(sqlparam.getCount() > 0){
					Param result = sqlparam.getListParam("data").getParam(0);
					ErrorLogger.error("########RESULT##########"+result);	
					res.param.addValue( "user_nm", result.getString( "USER_NM", "" ) );
					res.param.addValue( "prod_nm", result.getString( "PROD_NM", "" ) );
					res.param.addValue( "dmbo_nm", result.getString( "DMBO_NM", "" ) );
					res.param.addValue( "tot_prem", result.getString( "TOT_PREM", "" ) );
					res.param.addValue( "cust_nm", result.getString( "CUST_NM", "" ) );
					res.param.addValue( "ucid_date", result.getString( "UCID_DATE", "" ) );
				}else{
					res.param.addValue( "user_nm", "" );
					res.param.addValue( "prod_nm", "" );
					res.param.addValue( "dmbo_nm", "" );
					res.param.addValue( "tot_prem", "" );
					res.param.addValue( "cust_nm", "" );
					res.param.addValue( "ucid_date", "" );
				}
			}
			
			else if(cmd.equals("script_memo")){
				ErrorLogger.debug("########SCRIPT_MEMO##########");	
				sqlparam.setSqlName("rec.player.getScriptmemo.sel");
				sqlparam.addValue("CED_NO", ced_no);
				//SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
				ErrorLogger.error("$$$$$$$$$$sqlParam"+sqlparam.getCount());
			//	ErrorLogger.error("$$$$$$$$$$sqlParam"+sqlparam.getListParam("data").getParam(0));
				
				
			//	SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				
			if(sqlparam.getCount() > 0){
					Param result = sqlparam.getListParam("data").getParam(0);
					res.param.addValue( "memo", result.getString( "MEMO", "" ) );
					res.param.addValue( "user_nm", result.getString( "USER_NM", "" ) );
					res.param.addValue( "altr_dt", result.getString( "ALTR_DT", "" ) );
			}else{
				res.param.addValue( "memo", "등록된 메모가 없습니다" );
				res.param.addValue( "user_nm", "" );
				res.param.addValue( "altr_dt", "" );
			}
		 }
			else if(cmd.equals("callmemo_sel")){
				ErrorLogger.debug("########callmemo_sel##########");	
				sqlparam.setSqlName("rec.player.callMemo.sel");
				sqlparam.addValue("ucid", ucid);
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);

				if(sqlparam.getCount() > 0){
					AES256Cipher aes256 = AES256Cipher.getInstance(aes_key);
					ListParam result = sqlparam.getListParam("data");
					
					for(int i=0; i<sqlparam.getCount(); i++){
						Param memo = result.getParam(i);
						String v_memo = memo.getString( "MEMO", "" );
						v_memo = aes256.decrypt(v_memo);
						System.out.println("DECRYPT :::" + v_memo);
						ErrorLogger.debug("####CALL_MEMO - " + v_memo);
						result.setValue(i,"MEMO", v_memo );
					}
					res.param.addValue("DATA",result);
				}
			}
			else if(cmd.equals("callmemo_ins")){
				//메모 인서트
				String user_id = req.param.getString("user_id");
				String memo = req.param.getString("memo");
				
				memo = URLDecoder.decode(memo,"UTF-8");
				
				sqlparam.setSqlName("rec.player.callMemo.ins");
				sqlparam.addValue("UCID", ucid);
				sqlparam.addValue("CED_NO", ced_no);
				sqlparam.addValue("REG_ID", user_id);
				AES256Cipher aes256 = AES256Cipher.getInstance(aes_key);
				String v_memo = aes256.encrypt(memo);
				
				
				System.out.println("ECRYPT :::" + v_memo);
				
				sqlparam.addValue("MEMO", v_memo);
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
				ListParam data = new ListParam(new String[] { "TYPE"});
				data.addRow(new Object[] {"ins"});
				res.param.addValue( "DATA", data );
			}
			else if(cmd.equals("callmemo_del")){
				String seq_id = req.param.getString("seq_id");
				sqlparam.setSqlName("rec.player.callMemo.del");
				sqlparam.addValue("SEQ_ID", seq_id);
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
				ListParam data = new ListParam(new String[] { "TYPE"});
				data.addRow(new Object[] {"del"});
				res.param.addValue( "DATA", data );
			}
			
			else if(cmd.equals("contact_history")){
				ErrorLogger.debug("########contact_history##########");	
				sqlparam.setSqlName("rec.player.getContactHistory.sel");
				sqlparam.addValue("CED_NO", ced_no);
				//SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				
				sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);
				ErrorLogger.error("$$$$$$$$$$sqlParam"+sqlparam.getCount());
			//	ErrorLogger.error("$$$$$$$$$$sqlParam"+sqlparam.getListParam("data").getParam(0));
				
				
			//	SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				
			if(sqlparam.getCount() > 0){
					res.param.addValue("DATA",sqlparam.getListParam("data"));
			}else{
				res.param.addValue("DATA","");
			}
		}
			
			
		} catch(Exception e){
			throw new WebActionException("fail",e);
		}
	}
	
};
