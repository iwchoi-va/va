package sens.service.webaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import jedix.xwing.action.XwingWebAction;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class ServiceCallWebAction extends XwingWebAction {

	public void perform(JediRequest req, JediResponse res)
			throws WebActionException {

		ErrorLogger
				.error("############################## ServiceCallWebAction ############################");

		URL url = null;
		URLConnection uc = null;
		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
		String groupCol = "";

		try {

			// 1. get Parameters and URL
			String serviceUrl = req.param.getString("serviceUrl", null);
			String serviceType = req.param.getString("serviceType", "");
			ListParam dsObj = req.param.getListParam("DS_PARAM");
			String[][] params = new String[dsObj.getColumns().length][2];

			for (int k = 0, len = dsObj.getColumns().length; k < len; k++) {
				params[k][0] = dsObj.getColumnName(k);
				
				if (dsObj.getValue(0, params[k][0]) != null	&& !"null".equals(dsObj.getValue(0, params[k][0]).toString())) {

					if ("where".equals(params[k][0])) {
						params[k][1] = URLEncoder.encode((String) dsObj.getValue(0, params[k][0]),"UTF-8") + "&";
					} else if (k == (len-1)) {
						params[k][1] = (String) dsObj.getValue(0, params[k][0]);
					} else {
						params[k][1] = (String) dsObj.getValue(0, params[k][0])+ "&";
					}
					if (params[k][1].length() > 0) {
						if (!"orderby".equals(params[k][0]))
							serviceUrl += params[k][0] + "=" + params[k][1];
						else
							serviceUrl += params[k][1];
					}
				}
			}

			ErrorLogger.error("#serviceUrl# " + serviceUrl);

			// 2. connect service on the server
			url = new URL(serviceUrl);
			uc = url.openConnection();
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(5000);
			uc.setDoOutput(true);
			uc.setDoInput(true);

			// 3. read Return value
			reader = new BufferedReader(new InputStreamReader(
					uc.getInputStream(), "UTF-8"));
			String line = null;

			while ((line = reader.readLine()) != null) {
				line = line.replace("(WARNING: EVALUATION COPY[SEARCH])", ""); // 검색 개발용이라 마스크있음.추후제거
				buffer.append(line);
			}

			ErrorLogger.error("##result##:" + buffer.toString());

			/******************************************************************************************************************
			 * 녹취리스트
			 ******************************************************************************************************************/
			if ("recordlist".equals(serviceType)) { // 녹취리스트
				record_list rec_list = new record_list(req.getCommonDTO(),
						req.param, buffer);

				Long rs = rec_list.getTotalCount();
				if (rs != null)
					res.param.addValue("total", rs);

				ListParam result = rec_list.getrecordList();
				if (result != null)
					res.param.addValue("DS_RES", result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e1) {
			}
		}

	}
};
