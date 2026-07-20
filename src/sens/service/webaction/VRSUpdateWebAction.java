package sens.service.webaction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import jedix.xwing.action.XwingWebAction;


/*
 * 2020.04.13 수정 체크 사항
 * 1) MNG에서 수정된 QST의 SENT를 지운다. 이부분 추가하기
 * 2) //SENT 운영에 기존 데이터 delete --> 여기서 지워버리면 문장라인별로 지우기때문에 몇개가 안지워지는 문제가 발생한다. // 2020.04.09 이부분 주석 하기
 * 3) sql_msa.xml 파일 보면 200410 으로 되어있는게 수정된 쿼리니까 이부분 영향도 체크하기
 * */
public class VRSUpdateWebAction extends XwingWebAction {
	ListParam DS_MNG = null;
	ListParam DS_SENT = null;
	int start = 0;
	int pageSize = 1000;
	int page = 1;
	JediTransaction tran = JediTransactionManager.getJediTransaction();
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		String sdate = req.param.getString("sdate"); 
		String edate = req.param.getString("edate"); 
		
		
		try {
			//uat에서 mng 데이터 건수 가져옴.(페이징처리)
			SQLParam sqlparam_ex = new SQLParam();
			
			sqlparam_ex.setSqlName("msa.msa040T2.getUpdatedDataMngCnt.sel");			
			sqlparam_ex.addValue("sdate",  sdate);
			sqlparam_ex.addValue("edate",  edate);
			SQLParam sqlresult_ex = SQLServiceManager.getInstance().execute(sqlparam_ex);
			int total_mng_cnt = Integer.parseInt(sqlresult_ex.getListParam("DS_MNG_CNT").getParam(0).getString("CNT","0"));
			int mng_end_page = (int) Math.ceil(total_mng_cnt*1.0/pageSize*1.0);
			mng_end_page = mng_end_page < 1 ? 1: mng_end_page;
			IVRLogger.info("total_mng_cnt >> "+total_mng_cnt+ " <<");
			IVRLogger.info("mng_end_page >> "+mng_end_page+ " <<");
			
			SQLParam sqlparam = new SQLParam();
			SQLParam sqlparam2 = new SQLParam();
			tran.begin();
			//★★1000건씩 나눠서 진행
			//UAT MNG -> PROD MNG UPDATE
			for(int page = 1; page <= mng_end_page ; page ++){
				start =(page-1)*pageSize;
			
				sqlparam.clear();
				sqlparam.setSqlName("msa.msa040T2.getUpdatedDataMng.sel");			
				sqlparam.addValue("sdate",  sdate);
				sqlparam.addValue("edate",  edate);
				sqlparam.addValue("start",  start);
				sqlparam.addValue("end",  pageSize);
				
				try {
					IVRLogger.info("MNG SCRIPT DELETE & INSERT START!!! PAGE >> "+page+" <<");
					SQLParam sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
					DS_MNG = sqlresult.getListParam("DS_MNG");
					//IVRLogger.debug("DS_MNG 출력 ↓");
					//IVRLogger.debug(DS_MNG);
					
					//MNG 운영에 기존 데이터 겹치는 값 delete
					sqlparam2.clear();
					sqlparam2.setSqlName("msa.msa040T2.prodMng.del");			
					sqlparam2.addValue("DS_MNG",  DS_MNG);
					SQLServiceManager.getInstance().execute(sqlparam2,tran);
					
					IVRLogger.debug("MNG SCRIPT DELETE IS SUCCESSFUL!!! ^^");
					
					//MNG에서 수정된 QST의 SENT를 지운다.
					sqlparam2.clear();
					sqlparam2.setSqlName("msa.msa040T2.prodSent.del");			
					sqlparam2.addValue("DS_SENT",  DS_MNG);
					SQLServiceManager.getInstance().execute(sqlparam2,tran);
					
					IVRLogger.debug("SENT SCRIPT DELETE IS SUCCESSFUL!!! ^^");
					
					//UAT값 insert
					sqlparam2.clear();
					sqlparam2.setSqlName("msa.msa040T2.prodMng.ins");			
					sqlparam2.addValue("DS_MNG",  DS_MNG);
					SQLServiceManager.getInstance().execute(sqlparam2,tran);
					IVRLogger.debug("MNG SCRIPT INSERT IS SUCCESSFUL!!! ^^");
					
					tran.commit();
				} catch (Exception e) {
					// TODO: handle exception
					tran.rollback();
					IVRLogger.info("MNG SCRIPT DELETE & INSERT IS FAILED!!! T.T PAGE >> "+page+" <<");
					IVRLogger.info(e.getMessage());
					e.printStackTrace();
				}
			
			}
			
			//uat에서 sent 데이터 건수 가져옴.(페이징처리)
			sqlparam_ex.clear();
			sqlparam_ex.setSqlName("msa.msa040T2.getUpdatedDataSentCnt.sel");			
			sqlparam_ex.addValue("sdate",  sdate);
			sqlparam_ex.addValue("edate",  edate);
			sqlresult_ex = SQLServiceManager.getInstance().execute(sqlparam_ex);
			int total_sent_cnt = Integer.parseInt(sqlresult_ex.getListParam("DS_SENT_CNT").getParam(0).getString("CNT","0"));
			int sent_end_Page = (int) Math.ceil(total_sent_cnt*1.0/pageSize*1.0);
			sent_end_Page = sent_end_Page < 1 ? 1: sent_end_Page;
			
			IVRLogger.info("total_sent_cnt >> "+total_sent_cnt+ " <<");
			IVRLogger.info("sent_end_Page >> "+sent_end_Page+ " <<");
			
			//★★1000건씩 나눠서 진행
			//UAT SENT -> PROD SENT UPDATE
			tran.begin();
		
			for(int page = 1; page <= sent_end_Page ; page ++){
					start =(page-1)*pageSize;
	
					SQLParam sqlparam3 = new SQLParam();
					if(page != 1) DS_SENT.clear();
					sqlparam3.setSqlName("msa.msa040T2.getUpdatedDataSent.sel");			
					sqlparam3.addValue("sdate",  sdate);
					sqlparam3.addValue("edate",  edate);
					sqlparam3.addValue("start",  start);
					sqlparam3.addValue("end",  pageSize);
					
					SQLParam sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam3);
					DS_SENT = sqlresult2.getListParam("DS_SENT");
					
				try {
					IVRLogger.info("SENT SCRIPT DELETE & INSERT START!!! PAGE >> "+page+" << DS_SENT.size() >>"+DS_SENT.rowSize()+"<<");
					SQLParam sqlparam31 = new SQLParam();
					
					//SENT 운영에 기존 데이터 delete --> 여기서 지워버리면 문장라인별로 지우기때문에 몇개가 안지워지는 문제가 발생한다. // 2020.04.09
					/*SQLParam sqlparam31 = new SQLParam();
					sqlparam31.setSqlName("msa.msa040T2.prodSent.del");			
					sqlparam31.addValue("DS_SENT",  DS_SENT);
					SQLServiceManager.getInstance().execute(sqlparam31,tran);
					
					IVRLogger.debug("SENT SCRIPT DELETE IS SUCCESSFUL!!! ^^");*/
					
					//UAT 값 insert
					sqlparam31.clear();
					sqlparam31.setSqlName("msa.msa040T2.prodSent.ins");			
					sqlparam31.addValue("DS_SENT",  DS_SENT);
					SQLServiceManager.getInstance().execute(sqlparam31,tran);
					IVRLogger.debug("SENT SCRIPT INSERT IS SUCCESSFUL!!! ^^");
					tran.commit();
							
					
				} catch (Exception e) {
					// TODO: handle exception
					tran.rollback();
					IVRLogger.info("SENT SCRIPT DELETE & INSERT IS FAILED!!! T.T PAGE >> "+page+" <<");
					IVRLogger.info(e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			IVRLogger.info("VRS UPDATE WEBACTION IS COMPLETED!!! /(^0^)/");
			
		} catch (Exception e) {
			// TODO: handle exception
			tran.rollback();
			IVRLogger.info("VRS UPDATE WEBACTION IS ERROR! T.T");
			IVRLogger.info(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
};
