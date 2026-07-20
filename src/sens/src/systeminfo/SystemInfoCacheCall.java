package sens.src.systeminfo;

import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;

public class SystemInfoCacheCall extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			IVRLogger.debug("######systemMonitoring##########");
			SystemInfoCache systeminfo = SystemInfoCache.getInstance();
			systeminfo.SystemInfoCacheCall();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
