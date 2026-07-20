package xcron.com.webaction;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;
import sens.player.downloadFile;

/*
 * 콜 재생 속도 향상을 위해 당일 인입콜 중 STT분석이 된 콜은 미리 다운로드 해둔다.
 * */

public class AutoDownloadFile extends XwingWebAction {
	ListParam DS_CALL = null;
	ListParam DS_DOWN = new ListParam(new String[] { "UCID", "DOWN_FLAG","REASON"});
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.info("##############AUTODOWNLOADFILE##############");
		downloadFile df = new downloadFile();
		SQLParam sqlparam = new SQLParam();
		sqlparam.setSqlName("msens.xcron.hansol.autoDownloadFile_1");			

		try {
			SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			DS_CALL =  sqlresult.getListParam("DS_CALL");
			String ucid = "";
			String filePath = "";
			
			IVRLogger.info("##############콜 카운트##############"+sqlresult.getCount());
			
			if(sqlresult.getCount() > 0){
				for(int i = 0; i<sqlresult.getCount();i++){
					
					ucid = DS_CALL.getValue(i,"UCID").toString();
					filePath = DS_CALL.getValue(i,"FILE_FULL_PATH").toString();
					
					if("".equals(ucid)){
						DS_DOWN.addRow(new Object[] {ucid,"E","No Ucid"});
						continue;
					}
					
					req.param.addValue("contactId",ucid);
					req.param.addValue("file_path",filePath);
					req.param.addValue("batch_yn","N");
					df.perform(req, res);
					
					if(!"success".equals(res.param.getValue("result"))){
						DS_DOWN.addRow(new Object[] {ucid,"E",res.param.getValue("reason")});
						/*IVRLogger.info("##############AUTODOWNLOAD ERROR##############");
						IVRLogger.info("##########fileid>>>"+ucid+"#######reason>>"+res.param.getValue("reason"));
						IVRLogger.info("##########filepath>>>"+filePath);*/
					}else{
						DS_DOWN.addRow(new Object[] {ucid,"Y",""}); //성공 콜 플래그 업데이트
					}
				}
				
				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName("msens.xcron.hansol.autoDownloadFile_2");		
				sqlparam2.addValue("DS_DOWN",  DS_DOWN);
				
				SQLServiceManager.getInstance().execute(sqlparam2);
				
				
			}
			IVRLogger.info("### DS_DOWN 결과 ###"+DS_DOWN.toString());
			IVRLogger.info("##############AUTODOWNLOADFILE END##############");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			IVRLogger.info("AutoDownloadFile Error :: "+e.getMessage());
		}
		

	}
	
};
