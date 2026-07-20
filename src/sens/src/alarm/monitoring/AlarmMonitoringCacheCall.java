package sens.src.alarm.monitoring;

import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;

public class AlarmMonitoringCacheCall extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			
			AlarmMonitoringCache alarmMonitoring = AlarmMonitoringCache.getInstance();
			alarmMonitoring.AlarmMonitoringCacheCall();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
