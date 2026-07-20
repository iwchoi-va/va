package sens.src.systeminfo;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;

public class SystemInfoCacheWebAction extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			
			String result_name1 = req.param.getString("result_name1","DS_DATALIST");
			String result_name2 = req.param.getString("result_name2","DS_TIMELIST");
			
			SystemInfoCache SystemInfo = SystemInfoCache.getInstance();

			ListParam list1 = SystemInfo.getSystemdataList();
			res.param.addValue(result_name1, list1);
			
			ListParam list2 = SystemInfo.getSystemdatatimeList();
			res.param.addValue(result_name2, list2);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
