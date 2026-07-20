package sens.service.webaction;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
 
public class record_list extends Thread { 
	 
    private CommonDTO common    = null;
    private Param param         = null;
    
    private JSONObject jObj     = null;
    private JSONArray dataset   = null;
    private Long totalcount   = null;
    
    private ListParam list      = null; 
    private String[] columns	= null;
    
    private String CONTACT_ANL_ID= "";
    private String CUST_ANL_ID = "";
    
	public record_list(CommonDTO common, Param param, StringBuffer buffer) {		

		System.out.println("############################## record_list ############################");
		
		this.common = common;
        this.param = param;
        
		try{
				// 4. set Columns
				ListParam ds_res = param.getListParam("DS_RES");
				columns = new String[ds_res.getColumns().length];

				for(int k=0; k<ds_res.getColumns().length; k++){
					columns[k] = ds_res.getColumnName(k);
				}
				list = new ListParam(columns);
				
				
				
				//API 호출로 변경되면 해당부분 주석 풀것
				// 5. convert to JSON
				Object obj = JSONValue.parse(buffer.toString());
				jObj = (JSONObject)obj;
				
				//샘플로 호출하는 부분
				//JSONParser parser = new JSONParser();
				//Object obj = parser.parse(new FileReader("D:\\workspace\\MSENS_OB_AIG\\MSENS_OB\\MSENS_OB\\webapps\\temp\\search.json")); 
				//jObj = (JSONObject) obj;

		}catch(Exception e){
			e.printStackTrace();
		} 
	
	}
	
	public Long getTotalCount(){
		totalcount = (Long) ((JSONObject)jObj.get("result")).get("total_count");
		if(totalcount != null){
			return totalcount;
		}
		return totalcount;
	}
	
	public ListParam getrecordList(){

		//dataset = (JSONArray)((JSONObject)((JSONArray)jObj.get("result")).get(0)).get("Result");
		
		dataset = (JSONArray)((JSONObject)jObj.get("result")).get("rows");
		
		if(dataset != null){
			// 6. create dataset
			for(int i=0; i<dataset.size(); i++){ 
				JSONObject aObj = (JSONObject)((JSONObject)dataset.get(i)).get("fields");
				
				Iterator it = aObj.entrySet().iterator();
				
				int row = list.createRow();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry)it.next();
					for(int j=0; j<columns.length; j++){
						if(columns[j].equals(entry.getKey().toString())) list.setValue(row, columns[j], entry.getValue().toString());
					}
				}
			}
		}else{
			int row = list.createRow();
			for(int j=0; j<columns.length; j++){
				list.setValue(row, columns[j], "");
			}
		}
		
		return list;
	}
	
	
};
