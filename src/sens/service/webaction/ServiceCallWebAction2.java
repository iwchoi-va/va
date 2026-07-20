package sens.service.webaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import jedix.xwing.action.XwingWebAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class ServiceCallWebAction2 extends XwingWebAction{
	public void perform(JediRequest req, JediResponse res)
			throws WebActionException {
		
		
		ErrorLogger
		.error("############################## ServiceCallWebAction ############################");
		
		URL url = null;
		//URLConnection uc = null;
		HttpsURLConnection uc = null;
		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
		JSONArray dataset   = null;
	    ListParam list      = null; 
	    String[] columns	= null;
	    ListParam ds_res 	= null;
	    
		try {
			
			String serviceUrl 		= req.param.getString("serviceUrl", null);
			String serviceName 		= req.param.getString("serviceName", null); //"LTTMINFR0090";
			String reqJson 			= req.param.getString("reqJson", null); //"{\"planNo\":\"941802602612\"}";
			String serviceResult 	= req.param.getString("serviceResult", null);//uwHistoryDetailIvstInfoVOList
			
			serviceUrl = serviceUrl +"?_serviceName="+ serviceName+ "&_reqJson="+ reqJson;
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			
			url = new URL(serviceUrl);
			ErrorLogger.error("##result##:" + url);

			uc = (HttpsURLConnection)url.openConnection();
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(5000);
			uc.setDoOutput(true);
			uc.setDoInput(true);
			
			reader = new BufferedReader(new InputStreamReader(
					uc.getInputStream(), "UTF-8"));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
	
			
			Object obj = JSONValue.parse(buffer.toString());
			JSONObject jObj = (JSONObject)obj;
			
			/**
			 * Local test 용
			 * */
			//JSONParser parser = new JSONParser();
			//Object obj = parser.parse(new FileReader("C:\\test1.txt")); 
			//JSONObject jObj = (JSONObject) obj;
			/** Local test*/
			
		    ds_res = req.param.getListParam("DS_RES");
			columns = new String[ds_res.getColumns().length];

			for(int k=0; k<ds_res.getColumns().length; k++){
				columns[k] = ds_res.getColumnName(k);
			}
			list = new ListParam(columns);
			
			dataset = (JSONArray)jObj.get(serviceResult);//"uwHistoryDetailIvstInfoVOList"
		    
			if(dataset != null){
				
				for(int i=0; i<dataset.size(); i++){ 
					JSONObject aObj = (JSONObject)dataset.get(i);
					Iterator it = aObj.entrySet().iterator();
					
					int row = list.createRow();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						for(int j=0; j<columns.length; j++){
							if(columns[j].equalsIgnoreCase(entry.getKey().toString())) {
								list.setValue(row, columns[j], entry.getValue().toString());
								
							}
						}
					}
				}
			}else{
				int row = list.createRow();
				for(int j=0; j<columns.length; j++){
					list.setValue(row, columns[j], "");
				}
			}

			if (list != null) res.param.addValue("DS_RES", list);
			

		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (Exception e1) {
			}
		}
	}
	
}