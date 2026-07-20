package jedix.xwing.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;  

import org.json.simple.*;

import com.locus.jedi.transfer.ListParam; 
import com.locus.jedi.waf.action.WebActionException; 
import com.locus.jedi.waf.controller.JediRequest; 
import com.locus.jedi.waf.controller.JediResponse;  
 
public class GetStatWebAction extends XwingWebAction { 
	 
	public void perform(JediRequest req, JediResponse res) throws WebActionException{		

		System.out.println("#############################GetStatWebAction###########################");
		URL url = null;
		URLConnection uc = null; 
		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer(); 
		      

		try{
			// 1. get Parameters
			//String sqlName = req.param.getString("_sqlName", null);

			String stturl = req.param.getString("stturl", null);
			String start_date = req.param.getString("start_date", null);
			String end_date = req.param.getString("end_date", null);
			String category = req.param.getString("category", null);
			
			String _url = stturl;
			String tmp = _url;
			String op = "";
			
			
			if(!start_date.isEmpty()) {
				op = (tmp.length() == _url.length())?"?":"&";
				_url += op + "start_date=" + start_date;
			}
			if(!end_date.isEmpty()) {
				op = (tmp.length() == _url.length())?"?":"&";
				_url += op + "end_date=" + end_date;
			}
			
			
			System.out.println("#####GetStatWebAction#####_url: " + _url);
			
			/*
			// 2. connect MindsLab server
			url = new URL(_url);
			
			uc = url.openConnection();
			uc.setDoOutput(true);
			uc.setDoInput(true);
			
			
			// 3. set Columns
			ListParam dataName = req.param.getListParam("DS_STAT_VAL");
			String[] columns = new String[dataName.getColumns().length];
			
			for(int k=0; k<dataName.getColumns().length; k++){
				columns[k] = dataName.getColumnName(k);
			}
			ListParam list = new ListParam(columns);
			
			
			// 4. read Return value
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
			String line = null; 

			
			while((line = reader.readLine()) != null){ 
				buffer.append(line);
			}
			
			
			Object obj = JSONValue.parse(buffer.toString());
			JSONObject jObj = (JSONObject)obj;

			JSONArray arr = (JSONArray)jObj.get("data");
			
			for(int i=0; i<arr.size(); i++){
				
				JSONObject aObj = (JSONObject)arr.get(i);
				
				Iterator it = aObj.entrySet().iterator();
				dataName.createRow();
				
				int row = list.createRow();
				while(it.hasNext()){
					
					Map.Entry entry = (Map.Entry)it.next();

					list.setValue(row, entry.getKey().toString().toUpperCase(), entry.getValue().toString());
				}
				
				
			}
			
			res.param.addValue("DS_STAT_VAL", list);
			*/
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try{if(reader != null)	reader.close();}catch(Exception e1){}
		}
		
	}
};
