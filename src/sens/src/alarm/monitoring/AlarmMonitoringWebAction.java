package sens.src.alarm.monitoring;

import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;

public class AlarmMonitoringWebAction extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			
			String result_name = req.param.getString("result_name","DS_ALARMLIST");
			AlarmMonitoringCache alarmMonitoring = AlarmMonitoringCache.getInstance();
			ListParam list = alarmMonitoring.getAlarmmonitoringList();
			res.param.addValue(result_name, list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
