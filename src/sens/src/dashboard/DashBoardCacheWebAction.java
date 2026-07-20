package sens.src.dashboard;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class DashBoardCacheWebAction extends XwingWebAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		try {
			String analProcess = req.param.getString("analProcess","DS_PROCESS");
			String gradeScore = req.param.getString("gradeScore","DS_GRD_SCORE");
			String scriptScore = req.param.getString("scriptScore","DS_VRS_SCORE");
			String banScore = req.param.getString("banScore","DS_BAN_SCORE");
			//String focScore = req.param.getString("focScore","DS_FOC_SCORE");
			String secScore = req.param.getString("secScore","DS_SEC_SCORE");
			String menScore = req.param.getString("menScore","DS_MEN_SCORE");
			String befScore = req.param.getString("befScore","DS_BEF_SCORE");
			String org1_cd = req.param.getString("ORG1_CD",""); 
			String org2_cd = req.param.getString("ORG2_CD",""); 
			String org3_cd = req.param.getString("ORG3_CD","");
			String user_id = req.param.getString("USER_ID","");
			String sel_user_id = req.param.getString("SEL_USER_ID","");
			
			//KeywordMonitoringCache keywordMonitoring = KeywordMonitoringCache.getInstance();
			//keywordMonitoring.KeywordMonitoringTA_Call();
			
			DashBoardCache dashboard = DashBoardCache.getInstance();
			dashboard.DashBoardCacheCall(org1_cd,org2_cd,org3_cd,user_id,sel_user_id);
			
			ListParam analProcessList = dashboard.getAnalProcessList();
			res.param.addValue(analProcess, analProcessList);
			
			ListParam gradeScoreList = dashboard.getGradeScoreList();
			res.param.addValue(gradeScore, gradeScoreList);
			
			ListParam scriptScoreList = dashboard.getScriptScoreList();
			res.param.addValue(scriptScore, scriptScoreList);
			
			ListParam banScoreList = dashboard.getBanScoreList();
			res.param.addValue(banScore, banScoreList);
			
			ListParam secScoreList = dashboard.getSecScoreList();
			res.param.addValue(secScore, secScoreList);
			
			//ListParam focScoreList = dashboard.getFocScoreList();
			//res.param.addValue(focScore, focScoreList);
			
			ListParam menScoreList = dashboard.getMenScoreList();
			res.param.addValue(menScore, menScoreList);
			
			ListParam befScoreList = dashboard.getBefScoreList();
			res.param.addValue(befScore, befScoreList);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
