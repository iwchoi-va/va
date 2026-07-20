package telecaps.common.action;

import com.locus.jedi.biz.BizAppException;
import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.action.WebActionSupport;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class SQLConversionAction extends WebActionSupport {

	public void perform(JediRequest jediReq, JediResponse jediRes)
			throws WebActionException {
		try {

			String sqlName = jediReq.param.getString("_sqlName");
			String deviceIds = jediReq.param.getString("deviceId");

			SQLParam sqlparam = new SQLParam();
			sqlparam.setSqlName(sqlName);
			sqlparam.addParam(jediReq.param);

			if (deviceIds.indexOf(":") > 0) {
				deviceIds = "'" + deviceIds.replaceAll(":", "','") + "'";
			} else {
				deviceIds = "'" + deviceIds + "'";
			}
			sqlparam.addValue("deviceId", deviceIds);

			sqlparam = (SQLParam) BizDelegate.getInstance().execute(
					"sqlService", jediReq.getCommonDTO(), sqlparam);

			jediRes.param.addValue(sqlparam.getSqlName(), sqlparam);
			jediRes.param.addParam(sqlparam);

		} catch (BizAppException e) {
			throw new WebActionException(e.getCode(), e.getMessage(), e);

		} catch (Exception e) {
			throw new WebActionException("AwardWebAction", "AwardWebAction : "
					+ e.getMessage(), e);
		}
	}
}
