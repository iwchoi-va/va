package xcron.com.webaction;

import java.text.SimpleDateFormat;

import jedix.xwing.action.XwingWebAction;

import com.ibm.icu.util.Calendar;
import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;

import cs.com.util.DateUtil;

import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class GetBefMrLsWebAction extends XwingWebAction {
	/**
	 * MR/LS/청약거절 콜을 가져와서 ms_bef_mrls 테이블에 넣는다.
	 */
	SQLParam sqlParam = new SQLParam();
	SQLParam sqlParam2 = new SQLParam();
	SQLParam sqlResult = new SQLParam();
	ListParam tmMrLsList = new ListParam(new String[] {"CON_ENT_DGN_NO", "REG_DT", "REC_ID", "MR_CODE", "LS_CODE", 
			"CENTER_CD", "CENTER_NM", "AGENT_ID", "AGENT_NM", "CUST_ID", "CON_IP_PSN_NAME", "BIRTH_DT","VA_FLAG"});
	ListParam befMrLsList = new ListParam(new String[] {"CON_ENT_DGN_NO", "REG_DT", "REC_ID", "MR_CODE", "LS_CODE", 
			"CENTER_CD", "CENTER_NM", "AGENT_ID", "AGENT_NM", "CUST_ID", "CON_IP_PSN_NAME", "BIRTH_DT","VA_FLAG"});
	
	JediTransaction tran = JediTransactionManager.getJediTransaction();
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		/*ListParam DS_SPO = null;
		int pageSize = 1000;
		int spage = 0;
		int totalCnt = 0;
		int epage = 1;*/
		
		IVRLogger.info("##GetBefMrLlWebAction start##");
		
		String from_date = req.param.getString("from_date");
		String to_date = req.param.getString("to_date");
		DateUtil dateutil = new DateUtil();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		String sdate = "";
		String edate = "";

		if("".equals(from_date)){
			cal.add(Calendar.MONTH, -1);	//전월 달 첫 일자로 세팅
			from_date = df.format(cal.getTime());
			sdate = from_date + "01";
		}else{
			sdate = from_date;
		}
		
		if("".equals(to_date)){
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);	//전월 달 31일로 세팅
			to_date = df.format(cal.getTime());
			to_date = dateutil.getLastDate(to_date).replaceAll("-","");
		}else{
			String to_date2 = dateutil.getLastDate(to_date.substring(0, 6)).replaceAll("-",""); //혹시 해당월의 마지막날짜가 잘못들어왔으면 마지막날짜 수정 
			if(Integer.parseInt(to_date) > Integer.parseInt(to_date2)){
				to_date = to_date2;
			}
			
		}
		IVRLogger.info("sdate = "+sdate+"/////to_date = "+to_date);
		try{
			
			IVRLogger.info("####1. TM DATA 일주일치씩 수집####");
			//1. TM으로 부터 MRLS 리스트 GET
			int weekCnt = (int) Math.ceil((dateutil.getDates(sdate, to_date).length)/(7*1.0)); //두 날짜 사이 일수 구해서 주 갯수 구하기.
			tran.begin();
			for(int w = 0; w<weekCnt; w++){
				if(w != 0) sdate = dateutil.getDayAdd(edate,"yyyyMMdd",1);
				edate = dateutil.getDayAdd(sdate,"yyyyMMdd",7);
						
				if(Integer.parseInt(edate) >= Integer.parseInt(to_date)){
					edate =  to_date;
					w = weekCnt;
				}
				IVRLogger.info("조회 w="+w+" ::weekCnt="+weekCnt+"|||"+sdate +" , " + edate);
				
				sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_1"); 
				sqlParam.addValue("SDATE", sdate);
				sqlParam.addValue("EDATE", edate);

				sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
				
				tmMrLsList = sqlResult.getListParam("DS_LIST");
				
				IVRLogger.info("####1-1. 일주일씩 끊어서 가져온거 BEF MRLS 에 INSERT####");
				
				sqlParam2.setSqlName("msens.xcron.hansol.getmrlscallwebaction_2"); 
				sqlParam2.addValue("DS_LIST", tmMrLsList);
				SQLServiceManager.getInstance().execute(sqlParam2, tran);
				
			}
			tran.commit();
			
			IVRLogger.info("####1. TM DATA 수집 성공####");
			
		}catch(Exception e){
					// TODO: handle exception
					tran.rollback();
					//ErrorLogger.debug();
					IVRLogger.info("####TM DATA insert 하면서 에러"+e.getMessage());
					//e.printStackTrace();
					IVRLogger.info(e.getMessage());
		}	
	}
}

