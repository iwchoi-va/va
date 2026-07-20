package xcron.com.webaction;

import jedix.xwing.action.XwingWebAction;

import com.hansol.dbenc.util.SafeDBUtil;
import com.initech.safedb.common.SafeDBException;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.ParamException;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class XcronTestWebAction extends XwingWebAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			ListParam SttInteInfo = new ListParam(new String[] { "REC_KEY", "REGIST_NO", "INCALL_NO" });

			SQLParam sqlParam = new SQLParam();
			sqlParam.setSqlName("vsens.xcron.kyobo.test_1");

			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			if (sqlResult.getCount() > 0) {
				for(int i=0; i<sqlResult.getCount(); i++) {
					SttInteInfo.addRow(new Object[] {
							sqlResult.getListParam("vsens.xcron.kyobo.test_1").getParam(i).getString("REC_KEY"),
							SafeDBUtil.sdbDecrypt("ENC_REG_NO", sqlResult.getListParam("vsens.xcron.kyobo.test_1").getParam(i).getString("REGIST_NO")),
							SafeDBUtil.sdbDecrypt("ENC_TEL_NO", sqlResult.getListParam("vsens.xcron.kyobo.test_1").getParam(i).getString("INCALL_NO"))
					});
				}
			}
			
			for(int i=0; i<SttInteInfo.rowSize(); i++) {
				String v_regist_no = (String) SttInteInfo.getValue(i, "REGIST_NO");
				String v_tel_no = (String) SttInteInfo.getValue(i, "INCALL_NO");
				
				System.out.println("주민번호복호화::" + v_regist_no);
				System.out.println("전화번호복호화::" + v_tel_no);
				
				String s_regist_no = SafeDBUtil.sdbEncrypt("ENC_REG_NO", v_regist_no);
				String s_tel_no = SafeDBUtil.sdbEncrypt("ENC_TEL_NO", v_tel_no);
				
				System.out.println("주민번호암호화::" + s_regist_no);
				System.out.println("전화번호암호화::" + s_tel_no);
				
				SttInteInfo.setValue(i, "REGIST_NO", s_regist_no);
				SttInteInfo.setValue(i, "INCALL_NO", s_tel_no);
			}
			
			sqlParam.clear();
			sqlParam.setSqlName("vsens.xcron.kyobo.test_2");
			sqlParam.addValue("SttInteInfo", SttInteInfo);
			
			//SQLServiceManager.getInstance().execute(sqlParam);
			
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SafeDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
