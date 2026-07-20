package sens.player;

 
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class getSTT_smi extends XwingWebAction {

	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {
		
		 
		HttpServletRequest request = req.getHttpServletRequest();
		String file_id = req.param.getString("file_id"); 
		
		CommonDTO common = new CommonDTO(file_id);

		try{	
			
			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName("getContent.sel_js");
			sqlparam.setResultName("data");
			sqlparam.addValue("file_id", file_id);
			sqlparam = (SQLParam)BizDelegate.getInstance().execute("sqlService",common,sqlparam);

			Param result = sqlparam.getListParam("data").getParam(0);
			
			// FILE Info
			res.param.addValue( "file_id", result.getString( "FILE_ID", "" ) );
			res.param.addValue( "file_len", result.getString( "FILE_LEN", "" ) );
			
			// CONTENT JsonArray
			JSONArray jArray = new JSONArray();			
			
			String tmp = result.getString( "CONTENT", "" );
			String[] tmpArray = tmp.split("<SYNC Start=");
			
			for(int i=1; i<tmpArray.length; i++){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("SYNC", tmpArray[i].split(">")[0]);
				
				if(i < tmpArray.length-1){
					String contStr = tmpArray[i].split(">")[1].toString().replaceAll("\n", "");
					jsonObject.put("CONTENT",  contStr);
				}else{
					String contStr = (tmpArray[i].split(">")[1]).split("<")[0].toString().replaceAll("\n", "");
					jsonObject.put("CONTENT",  contStr);
				}
				
				
				jArray.add(jsonObject);
				
			}
			
//			System.out.println("#############jArray############# \n " + jArray.toString()); 

			res.param.addValue( "data", jArray );
			
			
		} catch(Exception e){
			throw new WebActionException("fail",e);
		}
	}
};
