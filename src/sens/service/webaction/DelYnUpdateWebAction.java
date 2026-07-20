package sens.service.webaction;

import oracle.sql.CLOB;
import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class DelYnUpdateWebAction extends XwingWebAction {
	/*
	 * TM에서 삭제된 스크립트 삭제 플래그 업데이트 한다. 
	 * 우리 기존꺼에 분석된거 화면 조회하기 위해 플래그만 처리 (분석 제외) + 개정된 스크립트로 분석
	 * 삭제 스크립트 보관 주기 1년
	 */
	private static final long serialVersionUID = 1L;
	

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		ListParam TB_SC_QST_DEL_MNG = new ListParam(new String[] {"QST_CD", "CNTR_CD", "DEL_DATE"});
		JediTransaction tran = JediTransactionManager.getJediTransaction();
	
		String from_date = req.param.getString("sdate");
		String to_date = req.param.getString("edate");
		String server_gb = req.param.getString("server_gb");
		
		String sql = "";
		int pageSize = 3;
		
		IVRLogger.info("####DelYnUpdateWebAction start#### server_gb >> "+ server_gb);
		
		if(server_gb.equals("UAT")){
			sql = "msa.msa040T1.";	// TM -> UAT
			IVRLogger.info("###### TM 삭제된건 GET -> VA UAT 반영 ######");
		}else if(server_gb.equals("PROD")){
			sql = "msa.msa040T2.";	// UAT -> PROD
			IVRLogger.info("###### VA UAT 삭제 상태 데이터 GET -> VA PROD 반영 ######");
		}else{
			return;
		}
		
		
		try{
			SQLParam sqlParam = new SQLParam();
			
			sqlParam.setSqlName(sql+"delynupdatewebaction_1");  
			sqlParam.addValue("SDATE", from_date);
			sqlParam.addValue("EDATE", to_date);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			if(sqlResult.getCount() > 0){
				IVRLogger.info("####DelYnUpdateWebAction####"+sqlResult.getListParam(sql+"delynupdatewebaction_1").toString());
				
				tran.begin();
				
				SQLParam sqlParam5 = new SQLParam();
				sqlParam5.setSqlName(sql+"delynupdatewebaction_5");	//현재 UAT,PROD에서 그 삭제기간에 삭제여부  초기화
				sqlParam5.addValue("SDATE", from_date);
				sqlParam5.addValue("EDATE", to_date);
				
				SQLServiceManager.getInstance().execute(sqlParam5, tran);
			
				
				
				for(int i =0; i< sqlResult.getCount(); i++){
					String DEL_DT = sqlResult.getListParam(sql+"delynupdatewebaction_1").getParam(i).getString("DEL_DT");
					int cnt = Integer.parseInt(sqlResult.getListParam(sql+"delynupdatewebaction_1").getParam(i).getString("CNT"));
					
				//	IVRLogger.debug(cnt/500+1 + "  //  " + cnt);
					int epage = cnt%pageSize == 0? cnt/pageSize : cnt/pageSize+1;
					for(int j =0 ;j< epage; j++){

						int from_rnum = pageSize*j+1;
						int to_rnum = pageSize*(j+1);
						
						IVRLogger.info("DEL_DT = "+DEL_DT+"// FROM_RNUM = "+from_rnum + "// TO_RNUM = " + to_rnum);
						
						SQLParam sqlParam1 = new SQLParam();
						sqlParam1.setSqlName(sql+"delynupdatewebaction_2");
						sqlParam1.addValue("DEL_DT", DEL_DT);
						sqlParam1.addValue("FROM_RNUM", from_rnum);
						sqlParam1.addValue("TO_RNUM", to_rnum);
						sqlParam1.addValue("_PAGESIZE", pageSize);
						SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
						
						TB_SC_QST_DEL_MNG.clear();
						
						for(int k =0; k< sqlResult1.getCount(); k++){
							
							TB_SC_QST_DEL_MNG.addRow(new Object[] {
									sqlResult1.getListParam(sql+"delynupdatewebaction_2").getParam(k).getString("QST_CD"),
									sqlResult1.getListParam(sql+"delynupdatewebaction_2").getParam(k).getString("CNTR_CD"),
									sqlResult1.getListParam(sql+"delynupdatewebaction_2").getParam(k).getString("DEL_DATE")
							});
						}
						
						IVRLogger.info("TB_SC_QST_DEL_MNG :: SIZE ::" + TB_SC_QST_DEL_MNG.rowSize());
						
						if(TB_SC_QST_DEL_MNG.rowSize() > 0){
							SQLParam sqlParam2 = new SQLParam();
							
							sqlParam2.clear();
							sqlParam2.setSqlName(sql+"delynupdatewebaction_3");
							sqlParam2.addValue("TB_SC_QST_DEL_MNG", TB_SC_QST_DEL_MNG);
							
							SQLServiceManager.getInstance().execute(sqlParam2, tran);
							
							if(server_gb.equals("UAT")){	//tm에 가져갔다는 플래그 업데이트(날짜랑 CHG_ID만)
								sqlParam2.clear();
								sqlParam2.setSqlName("msa.msa040T1.delynupdatewebaction_4");
								sqlParam2.addValue("TB_SC_QST_DEL_MNG", TB_SC_QST_DEL_MNG);
								
								SQLServiceManager.getInstance().execute(sqlParam2, tran);
							}
							
						}
						
					}
					
					
				}
				
				tran.commit();
				IVRLogger.info("#### TB_SC_QST_DEL_MNG 플래그 업데이트 완료 ####");
			}
			

		}catch (Exception e) {
			tran.rollback();
			e.printStackTrace();
			//ErrorLogger.debug();
			ErrorLogger.debug("####error"+e.getMessage());
			e.printStackTrace();
			IVRLogger.error(e.getMessage());
		}
		
	}
	
	

}

