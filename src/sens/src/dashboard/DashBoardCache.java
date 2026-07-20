package sens.src.dashboard;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;

public class DashBoardCache {
	private static DashBoardCache instance = null;
	
	private static ListParam analProcessList = null;
	private static ListParam gradeScoreList = null;
	private static ListParam scriptScoreList = null;
	private static ListParam banScoreList = null;
	private static ListParam secScoreList = null;
	private static ListParam focScoreList = null;
	private static ListParam menScoreList = null;
	private static ListParam befScoreList = null;
	

	private static ListParam analProcessList_tmp = null;
	private static ListParam gradeScoreList_tmp = null;
	private static ListParam scriptScoreList_tmp = null;
	private static ListParam banScoreList_tmp = null;
	private static ListParam secScoreList_tmp = null;
	private static ListParam focScoreList_tmp = null;
	private static ListParam menScoreList_tmp = null;
	private static ListParam befScoreList_tmp = null;
	
	
	public static synchronized DashBoardCache getInstance() throws Exception {
		if (instance == null) {
			synchronized (DashBoardCache.class) {
				if (instance == null) {
					instance = new DashBoardCache();
				}
			}
		}

		return instance;
	}
	
	public DashBoardCache() {
		
	}
	
	public void DashBoardCacheCall(String org1_cd,String org2_cd, String org3_cd,String user_id,String sel_user_id) {
		try {
			//ErrorLogger.debug("########org1cd##########"+org1_cd);
			//ErrorLogger.debug("########org2cd##########"+org2_cd);
			//ErrorLogger.debug("########org3cd##########"+org3_cd);
			IVRLogger.info("#####DASHBOARD 조회함#######+USER_ID>>>>"+sel_user_id);
			Code[] code = CodeUtil.getCodes("SYS020");
			
			getAnalProcess(org1_cd,org2_cd,org3_cd,user_id);
			getGradeCondition(org1_cd,org2_cd,org3_cd,user_id);
			
			//IVRLogger.debug("민원감지끝");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAnalProcess(String org1_cd,String org2_cd, String org3_cd,String user_id){
				//IVRLogger.info("#####DASHBOARD 조회함#######+USER_ID>>>>"+user_id);
				//ErrorLogger.debug("#####DASHBOARD 조회함#######+USER_ID>>>>"+user_id);
				//grade 분석현황 조회
				
				
				SQLParam sqlParam = new SQLParam();
				sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_1");
				sqlParam.addValue("ORG1_CD", org1_cd);
				sqlParam.addValue("ORG2_CD", org2_cd);
				sqlParam.addValue("ORG3_CD", org3_cd);
				sqlParam.addValue("USER_ID", user_id);
				try {
					SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
					if(sqlResult.getCount() > 0){
						analProcessList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_1");
						for(int i = 0; i<analProcessList_tmp.rowSize(); i++){
							if(!(analProcessList_tmp.getValue(i,"GUBUN_NM").equals("당월현황"))){
								//IVRLogger.info("############"+analProcessList_tmp.colSize()+"###########");
								for(int j = 2 ; j<analProcessList_tmp.colSize(); j++){ //1번째 인덱스 부터 마지막까지
									double a = Double.parseDouble((analProcessList_tmp.getValue(i,j).toString()));
									analProcessList_tmp.setValue(i, j, Math.round(a));	
								}								
						}
					}
						analProcessList=analProcessList_tmp;
				}
				} catch (SQLServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	private void getGradeCondition(String org1_cd,String org2_cd, String org3_cd,String user_id){
				//grade 분석 점수 조회
				//IVRLogger.info("msens.xcron.hansol.getdashboardwebaction_2");
				try {
					
					SQLParam sqlParam = new SQLParam();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_2");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0){
						gradeScoreList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_2");
						for(int i = 0; i<gradeScoreList_tmp.colSize(); i++){
							if(!(gradeScoreList_tmp.getColumnName(i).equals("GRD_MON_SCORE"))){		
									double a = Double.parseDouble((gradeScoreList_tmp.getValue(0,i).toString()));
									gradeScoreList_tmp.setValue(0, i, Math.round(a));	
								}								
	
						}
						gradeScoreList=gradeScoreList_tmp;
						//IVRLogger.info(gradeScoreList);
						
					}
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_3");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					
					if(sqlResult.getCount() > 0){
						scriptScoreList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_3");
						for(int i = 0; i<scriptScoreList_tmp.colSize(); i++){
							if(!(scriptScoreList_tmp.getColumnName(i).equals("VRS_MON_SCORE"))){		
									double a = Double.parseDouble((scriptScoreList_tmp.getValue(0,i).toString()));
									scriptScoreList_tmp.setValue(0, i, Math.round(a));	
								}								
	
						}

						scriptScoreList=scriptScoreList_tmp;
						//ErrorLogger.debug(scriptScoreList);
						
					}
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_4");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0){
						banScoreList = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_4");
						/*for(int i = 0; i<banScoreList_tmp.colSize(); i++){
							if(!(banScoreList_tmp.getColumnName(i).equals("BAN_MON_SCORE"))){		
									double a = Double.parseDouble((banScoreList_tmp.getValue(0,i).toString()));
									banScoreList_tmp.setValue(0, i, Math.round(a));	
								}								
	
						}
						banScoreList=banScoreList_tmp;
						IVRLogger.info(banScoreList);*/
					}
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_5");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0){
						menScoreList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_5");
						for(int i = 0; i<menScoreList_tmp.colSize(); i++){
							if(!(menScoreList_tmp.getColumnName(i).equals("MEN_MON_SCORE"))){		
									double a = Double.parseDouble((menScoreList_tmp.getValue(0,i).toString()));
									menScoreList_tmp.setValue(0, i, Math.round(a));	
								}								
	
						}
						menScoreList=menScoreList_tmp;
						//IVRLogger.info(menScoreList);
					}
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_6");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0){
						secScoreList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_6");
						for(int i = 0; i<secScoreList_tmp.colSize(); i++){
							if(!(secScoreList_tmp.getColumnName(i).equals("SEC_MON_SCORE"))){		
									double a = Double.parseDouble((secScoreList_tmp.getValue(0,i).toString()));
									secScoreList_tmp.setValue(0, i, Math.round(a));	
								}								
	
						}
						secScoreList=secScoreList_tmp;
						//IVRLogger.info(secScoreList);
					}
					
					//집중관리항목 임시 주석 -> 해당 항목 제외됨(추후에 추가될 가능서 있음)
					/*sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_7");
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0) focScoreList = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_7");*/
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getdashboardwebaction_8");
					sqlParam.addValue("ORG1_CD", org1_cd);
					sqlParam.addValue("ORG2_CD", org2_cd);
					sqlParam.addValue("ORG3_CD", org3_cd);
					sqlParam.addValue("USER_ID", user_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					if(sqlResult.getCount() > 0){
							befScoreList_tmp = sqlResult.getListParam("msens.xcron.hansol.getdashboardwebaction_8");
							for(int i = 0; i<befScoreList_tmp.colSize(); i++){
								if(!befScoreList_tmp.getColumnName(i).equals("BEF_MON_SCORE")){		
										double a = Double.parseDouble((befScoreList_tmp.getValue(0,i).toString()));
										befScoreList_tmp.setValue(0, i, Math.round(a));	
									}								
		
							}
							befScoreList=befScoreList_tmp;
							//IVRLogger.info(befScoreList);
						}
					
					
				} catch (SQLServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public ListParam getAnalProcessList() {
		return analProcessList;
	}
	
	public ListParam getGradeScoreList() {
		return gradeScoreList;
	}
	
	public ListParam getScriptScoreList() {
		return scriptScoreList;
	}
	
	public ListParam getBanScoreList() {
		return banScoreList;
	}
	
	public ListParam getSecScoreList() {
		return secScoreList;
	}
	
	public ListParam getFocScoreList() {
		return focScoreList;
	}
	
	public ListParam getMenScoreList() {
		return menScoreList;
	}
	
	public ListParam getBefScoreList() {
		return befScoreList;
	}

	
}
