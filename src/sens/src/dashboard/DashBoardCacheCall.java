package sens.src.dashboard;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class DashBoardCacheCall extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			String org1_cd = req.param.getString("ORG1_CD",""); 
			String org2_cd = req.param.getString("ORG2_CD",""); 
			String org3_cd = req.param.getString("ORG3_CD","");
			String user_id = req.param.getString("USER_ID","");
			String sel_user_id = req.param.getString("SEL_USER_ID","");
			
			DashBoardCache dashboard = DashBoardCache.getInstance();
			dashboard.DashBoardCacheCall(org1_cd,org2_cd,org3_cd,user_id,sel_user_id);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
