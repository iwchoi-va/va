package xcron.com.webaction;

 
import java.util.Calendar;
import java.util.Date;

import jedix.xwing.action.XwingWebAction;

import com.ibm.icu.text.SimpleDateFormat;
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

public class SttContentSplitWebaction extends XwingWebAction {
	
	public void perform(JediRequest req, JediResponse res) 
		throws WebActionException {
		
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam sttContents = new ListParam(new String[] {"UCID", "DURATION", "RXTX_GB", "STT_SENT_ID", "START_TIME", "END_TIME","STT_SENT"});
		ListParam sttSplitList = new ListParam(new String[] {"UCID"});
		ListParam DS_STT_SENT = new ListParam(new String[] {"UCID", "RXTX_GB", "STT_LEN", "MIN_TIME"});
		ListParam splitRollback = null;
		
		IVRLogger.info("###############STT contents split WebAction###################");
		
		try{	
			SQLParam sqlParam = new SQLParam();
			//sqlParam.setSqlName("msens.xcron.hansol.getSttContents.sel");
			sqlParam.setSqlName("msens.xcron.hansol.getSttContents.sel");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			splitRollback = sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel");
			
			ErrorLogger.debug(sqlResult.toString());
			
			if(sqlResult.getCount() > 0) {
				
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam4 =  new SQLParam();  
				sqlParam4.setSqlName("msens.xcron.hansol.getSttContens.flag.upd");
				sqlParam4.addValue("sttContents", sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel")); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
				tran.commit();
				
				for(int i = 0;i< sqlResult.getCount(); i++){
					String content = sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("CONTENT");
					String ucid = sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("UCID");
					
					
					// SPDB에서 녹취시작시간을 조회해 온다 --> META에 모든 녹취정보가 없을 수 있기때문
					SQLParam sqlParam2 =  new SQLParam();
					sqlParam2.setSqlName("msens.xcron.hansol.getRecStartTime.sel"); 
					sqlParam2.addValue("UCID", ucid);
					SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
					
					
					String rec_start_time = "";
					
					if(sqlResult2.getCount() > 0){
						rec_start_time =  sqlResult2.getListParam("msens.xcron.hansol.getRecStartTime.sel").getParam(0).getString("REC_START_TIME");
					}
					
					//테스트용 : 
					//rec_start_time = "20190404173113";
					
					String[] tmpArray = content.split("\n");
					
					
					int rx_time = 0; // 상담사 발화 시간
					int rx_spk_length = 0;
					int tx_time = 0; // 고객 발화 시간
					int tx_spk_length = 0;
					
					String rx_datetime = rec_start_time; // 상담사 발화 그룹
					String tx_datetime = rec_start_time; // 고객 발화 그룹
					
					
					sttSplitList.addRow(new Object[] {
							sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("UCID")
					});
					
					
					for(int j =0; j< tmpArray.length; j++){
						String [] rxtx = tmpArray[j].split("\\|");
						String[] time;
						if(rxtx.length > 1){
							time = rxtx[1].split("\t");
						}else{
							time = rxtx[0].split("\t");
						}
						
						if(!"".equals(rec_start_time)){ //SPDB에 있는거만 발화속도 체크함
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
							if("TX".equals(rxtx[0])){ 
								tx_time += (Integer.parseInt(time[1])-Integer.parseInt(time[0]));
								tx_spk_length += time[2].trim().replaceAll(" ", "").length();
	
								if(tx_time/100 >= 60){
	
									DS_STT_SENT.addRow(new Object[] {
											ucid,
											rxtx[0],
											tx_spk_length,
											tx_datetime
									});
									
									Date date = format.parse(tx_datetime);
									cal.setTime(date);
									cal.add(Calendar.MINUTE, 1);
									tx_datetime = format.format(cal.getTime()); // 1분 다 채워지면 1분 더하기
									
									tx_time = 0;
									tx_spk_length = 0;
								}
	
							}else if("RX".equals(rxtx[0])){ 
								rx_time += Integer.parseInt(time[1])-Integer.parseInt(time[0]);
								rx_spk_length += time[2].trim().replaceAll(" ", "").length();
								
								//ErrorLogger.debug("####문장 start time = " + Integer.parseInt(time[0]) + "//문장 : " + time[2].trim().replaceAll(" ", "") + "//길이 : " + time[2].trim().replaceAll("(^\\p{Z}+|\\p{Z}+$)", "").length());
								if(rx_time/100 >= 60){
									//ErrorLogger.debug("##1분집입!! = "+Integer.parseInt(time[0])+" // 문장길이:: - " + time[2].trim().replaceAll("(^\\p{Z}+|\\p{Z}+$)", "").length() + "// 최종 길이 = " + rx_spk_length);
									
									DS_STT_SENT.addRow(new Object[] {
											ucid,
											rxtx[0],
											rx_spk_length,
											rx_datetime
									});
									
									Date date = format.parse(rx_datetime);
									cal.setTime(date);
									cal.add(Calendar.MINUTE, 1);
									rx_datetime = format.format(cal.getTime());
									
									rx_time = 0;
									rx_spk_length = 0;
								}  	
								
							}
						}
						
						sttContents.addRow(new Object[] {
								sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("UCID"),
								sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("DURATION"),
								sqlResult.getListParam("msens.xcron.hansol.getSttContents.sel").getParam(i).getString("RXTX_GB"),
								(j+1),
								time[0],
								time[1],
								tmpArray[j].trim()
						});
						
						
						
					}
			
					boolean rx_flag = false; // 마지막에 1분이 안되서 남아있는거 처리하기위해
					boolean tx_flag = false;
					for(int k =0 ;k< DS_STT_SENT.rowSize(); k++){
						if("RX".equals(DS_STT_SENT.getValue(k, "RXTX_GB"))){
							if(rx_datetime.equals(DS_STT_SENT.getValue(k, "MIN_TIME"))) rx_flag = true;
						}else if("TX".equals(DS_STT_SENT.getValue(k, "RXTX_GB"))){
							if(tx_datetime.equals(DS_STT_SENT.getValue(k, "MIN_TIME"))) tx_flag = true;
						}
					}
					
					
					if(!tx_flag && tx_spk_length != 0){
						DS_STT_SENT.addRow(new Object[] {
								ucid,
								"TX",
								tx_spk_length,
								tx_datetime
						});
					}
					
					if(!rx_flag && rx_spk_length != 0){
						DS_STT_SENT.addRow(new Object[] {
								ucid,
								"RX",
								rx_spk_length,
								rx_datetime
						});
					}
					
				}
				
				
				//ErrorLogger.debug(sttContents.toString());
				
				tran.begin();
				
				if(sttContents.rowSize() > 0){
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getSttSplitContent.ins");
					sqlParam.addValue("sttContents", sttContents);
				
					SQLServiceManager.getInstance().execute(sqlParam, tran);
				}
				
				/* 현재 수집한 UCID 모두 일단 발화테이블에서 지운다. (원래 발화테이블에 있었다가 재분석했더니 발화데이터가 없을 수 있기 때문)
				 * STT재분석 후 분석값이 달라질 수 있기 때문에 테이블 키로 지우지 않고 해당 UCID 통째로 지운다. */
				if(sttSplitList.rowSize() > 0){ 
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getSpkStatics.del");
					sqlParam.addValue("DS_STT_SENT", sttSplitList);
				
					SQLServiceManager.getInstance().execute(sqlParam, tran);
				}
				
				if(DS_STT_SENT.rowSize() > 0){  
					sqlParam.clear();	
					sqlParam.setSqlName("msens.xcron.hansol.getSpkStatics.ins");
					sqlParam.addValue("DS_STT_SENT", DS_STT_SENT);
				
					SQLServiceManager.getInstance().execute(sqlParam, tran);
				}
				
				if(sttSplitList.rowSize() > 0){
					sqlParam4.clear();
					sqlParam4.setSqlName("msens.xcron.hansol.getSttContens.endFlag.upd");
					sqlParam4.addValue("sttContents", sttSplitList); // merge 테이블에 split Y로 업데이트
					SQLServiceManager.getInstance().execute(sqlParam4, tran);
				}
				tran.commit();
				
				IVRLogger.info("###############STT contents split WebAction Success###################");
				
			}

		} catch(Exception e){
			tran.rollback();
			IVRLogger.info("###############STT CONTENT SPLIT ERROR :: " + e.getMessage());
			
			if(splitRollback != null){
				if(splitRollback.rowSize() > 0){
					
					try {
						tran.begin();
						
						//FLAG 값 S로 업데이트 수행
						SQLParam sqlParam4 =  new SQLParam();  
						sqlParam4.setSqlName("msens.xcron.hansol.getSttContens.error.flag.upd");
						sqlParam4.addValue("splitRollback", splitRollback); //call_info에 VA_FLAG Y로 업데이트
						
						SQLServiceManager.getInstance().execute(sqlParam4, tran);
	
						tran.commit();
					} catch (SQLServiceException e1) {
						// TODO Auto-generated catch block
						
						tran.rollback();
						e1.printStackTrace();
					}
					
				}
			}
			
			e.printStackTrace();
			throw new WebActionException("fail",e);
		}
		
	}
	
	
	
};
