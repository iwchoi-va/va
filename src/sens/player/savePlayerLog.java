package sens.player;

 
import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.util.DateUtil;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class savePlayerLog extends XwingWebAction {

	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {

		try{	
			
			SQLParam sqlparam = new SQLParam();
			
			HttpServletRequest request = req.getHttpServletRequest();
			String  play_user_id = req.param.getString("play_user_id", "");
			String  rec_user_id = req.param.getString("rec_user_id", "");
			String  rec_cust_nm = req.param.getString("rec_cust_nm", "");
			String  rec_key = req.param.getString("rec_key", "");
			String  rec_start_time = req.param.getString("rec_start_time", "");
			String  log_gb = req.param.getString("log_gb", "");
			String  ced_no = req.param.getString("ced_no", "");
			System.out.println("##########savePlayerLog############# "+ced_no);
			String  contact_user_ip = request.getRemoteAddr();

			sqlparam.clear();
			sqlparam.setSqlName("rec.rec010.savePlayLog");	
			sqlparam.addValue("play_date", DateUtil.getTime("yyyyMMdd"));
			sqlparam.addValue("play_time", DateUtil.getTime("HHmmss"));
			sqlparam.addValue("play_user_id", play_user_id);			
			sqlparam.addValue("rec_user_id", rec_user_id);
			sqlparam.addValue("rec_cust_nm", rec_cust_nm);
			sqlparam.addValue("rec_key", rec_key);			
			sqlparam.addValue("rec_start_time", rec_start_time);
			sqlparam.addValue("contact_user_ip", contact_user_ip);
			sqlparam.addValue("ced_no", ced_no);
			sqlparam.addValue("log_gb", log_gb);
			
			CommonDTO common = new CommonDTO(play_user_id);
			BizDelegate.getInstance().execute("sqlService", common, sqlparam);
			
			
		} catch(Exception e){
			throw new WebActionException("fail",e);
		}
	}
};
