package sens.src.search          ;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class SearchResultWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		ErrorLogger.debug("SearchResultWebaction  Start!!");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam SearchRollback = null;
		
		try {
			ListParam SearchRsl = new ListParam(new String[] {"BASE_DATE", "TYPE", "MENT_CD", "CON_ENT_DGN_NO", "COUNT", "USER_ID", "TREATYHQCD","TREATYHQNM","TREATYBRHCD","TREATYBRHNM","TREATYDEPTCD","TREATYDEPTNM", "ISTP_CD", "ISTP_NM","PROD_CD","PROD_NM"});
			
			
			//금칙어/필수멘트 분석결과 설계번호별, 유형별로 데이터 넣어주기
			IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_1");
			
			SQLParam sqlParam = new SQLParam();
			
			sqlParam.setSqlName("msens.xcron.hansol.setsearchresultwebaction_5");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			SearchRollback = sqlResult.getListParam("msens.xcron.hansol.setsearchresultwebaction_5");
			
			IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_5::count::"+sqlResult.getListParam("msens.xcron.hansol.setsearchresultwebaction_5").rowSize());

			if(sqlResult.getCount() > 0){

				tran.begin();
				
				IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_3::" + sqlResult.getCount());
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam4 =  new SQLParam();  
				sqlParam4.setSqlName("msens.xcron.hansol.setsearchresultwebaction_3");
				sqlParam4.addValue("SearchRsl", sqlResult.getListParam("msens.xcron.hansol.setsearchresultwebaction_5")); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
				tran.commit();
				
				for(int i =0; i< sqlResult.getCount(); i++){
					
					String v_ced_no =sqlResult.getListParam("msens.xcron.hansol.setsearchresultwebaction_5").getParam(i).getString("CON_ENT_DGN_NO");
					
					//sqlParam.clear();
					SQLParam sqlParam1 =  new SQLParam();  
					sqlParam1.setSqlName("msens.xcron.hansol.setsearchresultwebaction_1");
					sqlParam1.addValue("CON_ENT_DGN_NO",v_ced_no);
					SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
		
					IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_1::sqlResult.getCount()::" + sqlResult1.getCount());
					
					if(sqlResult1.getCount() >0){
						
						for(int j=0; j<sqlResult1.getCount(); j++) {
							SearchRsl.addRow(new Object[] {
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("BASE_DATE"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TYPE"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("MENT_CD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("CON_ENT_DGN_NO"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("COUNT"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("USER_ID"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYHQCD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYHQNM"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYBRHCD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYBRHNM"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYDEPTCD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("TREATYDEPTNM"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("ISTP_CD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("ISTP_NM"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("PROD_CD"),
									sqlResult1.getListParam("msens.xcron.hansol.setsearchresultwebaction_1").getParam(j).getString("PROD_NM")
							});
						}
					}
					
				}
				
				tran.begin();
				
				if(SearchRsl.rowSize() > 0){
					
					IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_2::"+SearchRsl.rowSize());
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.setsearchresultwebaction_2");
					sqlParam.addValue("SearchRsl", SearchRsl);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);
				}
				
				IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_4" + SearchRollback.rowSize());
				
				if(SearchRollback != null){
					if(SearchRollback.rowSize() > 0){
						//IVRLogger.debug("##########con_ent_dgn_no : " + v_ced_no_group);
						sqlParam.clear();
						sqlParam.setSqlName("msens.xcron.hansol.setsearchresultwebaction_4");
						sqlParam.addValue("SearchRsl", SearchRollback); //call_info에 VA_FLAG Y로 업데이트
						SQLServiceManager.getInstance().execute(sqlParam, tran);
					}
				}
				tran.commit();
			}
			IVRLogger.debug("SearchResultWebaction END!!");
		} catch (Exception e) {
			tran.rollback();
			IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_6" + SearchRollback.rowSize());
			
			if(SearchRollback != null){
				if(SearchRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction_6");
						
						//에러난 경우 flag = 'N'으로 다시 변경
						//msens.xcron.hansol.setsearchresultwebaction_6
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.setsearchresultwebaction_6");
						sqlParam5.addValue("SearchRollback", SearchRollback);
						
						SQLServiceManager.getInstance().execute(sqlParam5, tran);
						
						tran.commit();
					} catch (SQLServiceException e1) {
						// TODO Auto-generated catch block
						tran.rollback();
						e1.printStackTrace();
					}
					
					
				}
			}
			
			e.getStackTrace();
			IVRLogger.info("msens.xcron.hansol.setsearchresultwebaction error :: " + e.getMessage());
			ErrorLogger.error(e.getMessage());
		}
		
	}	
}

