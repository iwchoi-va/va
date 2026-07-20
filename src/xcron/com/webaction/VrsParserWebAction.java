package xcron.com.webaction;

import java.sql.Clob;
import java.sql.SQLException;

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

public class VrsParserWebAction extends XwingWebAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		ListParam VRSInfo = new ListParam(new String[] {"SC_SNO", "SC_LCLF_CD","SC_SCLF_CD", "QST_CD", "CNTR_CD", "QST_CDNM", "QST_CNTS", "QST_KIND_CD","SORTKEY","DEL_YN","DEL_DATE","QST_CNTS_P1", "UPDR_CRNO"});
		ListParam VRSInfo2 = new ListParam(new String[] {"SC_SNO","SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD","QST_CDNM","QST_CNTS_P1","QST_KIND_CD", "SORTKEY","DEL_YN","DEL_DATE"});
		ListParam tempVRSInfo = new ListParam(new String[] {"SC_SNO","SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD", "QST_CDNM","SEQ","SCRIPT_SENT","QST_KIND_CD", "SORTKEY","DEL_YN","DEL_DATE"});
		ListParam finalVRSInfo = new ListParam(new String[] {"SC_SNO","SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD", "QST_CDNM","SEQ","SCRIPT_SENT","SCRIPT_TA_SENT","QST_KIND_CD", "SORTKEY","DEL_YN","DEL_DATE"});
		ListParam imsiVRSInfo = new ListParam(new String[] {"SC_SNO","SC_LCLF_CD", "SC_SCLF_CD", "QST_CD", "CNTR_CD", "QST_CDNM","SEQ","SCRIPT_SENT","QST_KIND_CD", "SORTKEY","DEL_YN","DEL_DATE"});
		
		SQLParam sqlParam = new SQLParam();

		ListParam ParsingRollback = null;
		ListParam SplitRollback = null;
		
		String sdate = req.param.getString("from_date"); 
		String edate = req.param.getString("to_date"); 
		
		//SC_SNO, SC_LCLF_CD, SC_SCLF_CD, QST_CD, CNTR_CD, QST_CDNM, QST_CNTS, UPDR_CRNO, QST_KIND_CD, SORTKEY
		try{

			sqlParam.setSqlName("msens.xcron.hansol.getVRSParser.sel"); //AiGEN에서 넘어온 스크립트 조회
			sqlParam.addValue("SDATE", sdate);
			sqlParam.addValue("EDATE", edate);
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			
			String tags = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>"; 
			String filter1 = "(&nbsp;)|(&lt;)|(&gt;)|(,)|(-)|(\\^\\^)|(!)|(/)|(■)|(▶)|(◈)|(‘)|(’)|(“)|(”)|(•)|(\\*)|(※)|(:)|(\\\")"; //특수문자
			String filter2 = "(\\d\\))|(\\d\\d\\))|(\\d\\.\\s)|(특약[0-9]{2})|(특약[0-9]{1})|(특약 [0-9]{1})|(Yes\\))|(No\\))|(① 예)|(② 아니요)"; //넘버링, 특약번호 //filter 2 변경
			String filter3 = "(or)|(변경된 내용 진행     필수 :)|(변경된 내용 진행  필수 :)|(추가된 내용 진행  필수 :)|(CT 검사비 언급시 필수멘트:)|(★ 치과  한의원  한방병원 언급시 필수멘트:)|(부담보조건부 인수스크립트 :)|(피보험자\\))|(계약자 진행\\))|(암진단비\\))|(^[\\.]*)";	 // 특정 텍스트(수많은 동일한 패턴에 의해 제거), 마침표
			
			ParsingRollback = sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel");
			
			if (sqlResult.getCount() > 0) {
				
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam4 =  new SQLParam();  
				sqlParam4.setSqlName("msens.xcron.hansol.getVRSParser.flag.upd");
				sqlParam4.addValue("VRSInfo2", sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel")); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam4, tran);
				
				tran.commit();
				
				for(int i=0; i<sqlResult.getCount(); i++) {
					VRSInfo.addRow(new Object[] {
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("SC_SNO"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("SC_LCLF_CD"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("SC_SCLF_CD"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("QST_CD"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("CNTR_CD"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("QST_CDNM"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("QST_CNTS"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("QST_KIND_CD"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("SORTKEY"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("DEL_YN"),
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("DEL_DATE"),
							"",
							sqlResult.getListParam("msens.xcron.hansol.getVRSParser.sel").getParam(i).getString("CHG_ID")
					});
				}
				
				for(int i =0; i< VRSInfo.rowSize(); i++){ //태그제거작업
					String text = VRSInfo.getValue(i, "QST_CNTS").toString();
					
					
					String filteredText = text.replaceAll(tags, ""); //html태그 제거
					filteredText = filteredText.replaceAll("\\<(.*?)>", ""); //'<>' 안에있는 데이터 제거 // 이거 추가하면 태그 제거 문제 해결될 수 있을듯! // 태그 한번 제거하는 로직 추가!
					filteredText = filteredText.replaceAll("\\[(.*?)]", ""); //대괄호'[]' 안에있는 데이터 제거
					filteredText = filteredText.replaceAll("\\((.*?)\\)", ""); // 소괄호 '()' 안에있는 데이터 제거
					filteredText = filteredText.replaceAll("\\$(.*?)\\$", ""); // 변수 '$$' 안에있는 데이터 제거
					filteredText = filteredText.replaceAll("\\?", "\\."); // 물음표를 마침표로 변경
					filteredText = filteredText.replaceAll("www.aig.co.kr", "더블유더블유쩜에이아이지쩜씨오쩜케이알"); // url 특수 처리(because of .)
					filteredText = (filteredText.replaceAll(filter1, " ")).trim();
					filteredText = (filteredText.replaceAll(filter2, " ")).trim();
					filteredText = (filteredText.replaceAll(filter3, " ")).trim();
					
					if("<SPAN".indexOf(filteredText)>0 || "xml:".indexOf(filteredText)>0) filteredText = "";
					else filteredText = filteredText.replaceAll("^\\s+", ""); //ltrim
					
					VRSInfo.setValue(i, "QST_CNTS_P1", filteredText);
					
				}
				
				tran.begin();
				
				//임시주석
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.getVRSParser.upd");
				sqlParam.addValue("VRSInfo", VRSInfo);
				
				//ErrorLogger.debug(VRSInfo.toString());
			
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				sqlParam.clear();
				sqlParam.setSqlName("msens.xcron.hansol.getVRSParser.flag_y.upd");
				sqlParam.addValue("VRSInfo", VRSInfo);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				tran.commit();
				

			}	
		}catch(Exception e){
			tran.rollback();
			e.printStackTrace();
			
			if(ParsingRollback.rowSize() > 0){
				try {
					tran.begin();
					
					//FLAG 값 S로 업데이트 수행
					SQLParam sqlParam5 =  new SQLParam();  
					sqlParam5.setSqlName("msens.xcron.hansol.getVRSParser.error.flag.upd");
					sqlParam5.addValue("ParsingRollback", ParsingRollback); //call_info에 VA_FLAG Y로 업데이트
					SQLServiceManager.getInstance().execute(sqlParam5, tran);
					
					tran.commit();
				} catch (SQLServiceException e1) {
					// TODO Auto-generated catch block
					tran.rollback();
					e1.printStackTrace();
				}
				
				
			}
			//ErrorLogger.debug();
			ErrorLogger.debug("####error"+e.getMessage());
			e.printStackTrace();
			IVRLogger.error(e.getMessage());
		}
		
		try{
				SQLParam sqlParam2 = new SQLParam();
				sqlParam2.setSqlName("msens.xcron.hansol.getVRSParser2.sel"); //
				SQLParam sqlResult2 = SQLServiceManager.getInstance().execute(sqlParam2);
				
				SplitRollback = sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel");
				
				ErrorLogger.debug("#############getCount :: "+sqlResult2.getCount());
				if (sqlResult2.getCount() > 0) {
					tran.begin();
					
					//FLAG 값 S로 업데이트 수행
					SQLParam sqlParam4 =  new SQLParam();  
					sqlParam4.setSqlName("msens.xcron.hansol.getSentSplit.flag_s.upd");
					sqlParam4.addValue("SplitRollback", SplitRollback); //call_info에 VA_FLAG Y로 업데이트
					SQLServiceManager.getInstance().execute(sqlParam4, tran);
					
					tran.commit();
					
					
					for(int i=0; i<sqlResult2.getCount(); i++) {
				
						VRSInfo2.addRow(new Object[] {
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("SC_SNO"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("SC_LCLF_CD"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("SC_SCLF_CD"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("QST_CD"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("CNTR_CD"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("QST_CDNM"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("QST_CNTS_P1"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("QST_KIND_CD"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("SORTKEY"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("DEL_YN"),
								sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("DEL_DATE")
						});
						
					/*	tran.begin();
						sqlParam.clear();
						sqlParam.setSqlName("msens.xcron.hansol.getVRSParser2.upd");
						sqlParam.addValue("QST_CD", sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("QST_CD"));
						sqlParam.addValue("CNTR_CD", sqlResult2.getListParam("msens.xcron.hansol.getVRSParser2.sel").getParam(i).getString("CNTR_CD"));
						//sqlParam.addValue("tempVRSInfo", VRSInfo2.getParam());
					
						SQLServiceManager.getInstance().execute(sqlParam, tran);
						
						tran.commit();*/
						
					}
					
					
					
					ErrorLogger.debug("#############getCount :: 중간단계");
					for(int i =0; i< VRSInfo2.rowSize(); i++){
						String[] rVRS = VRSInfo2.getValue(i, "QST_CNTS_P1").toString().trim().split("(?!(?<=\\d)\\.)(\\.)|(시고)|(하며)");
						
						ErrorLogger.debug("###########3VRS = " + i + "              "+rVRS.length);
						
						for(int j = 0; j< rVRS.length; j++){
							//ErrorLogger.debug("############j = " +j + "            " +rVRS[j].trim().length());
							
							
							if(rVRS[j].trim().length() > 1){
								tempVRSInfo.addRow(new Object[] {
										VRSInfo2.getValue(i, "SC_SNO"),
										VRSInfo2.getValue(i, "SC_LCLF_CD"),
										VRSInfo2.getValue(i, "SC_SCLF_CD"),
										VRSInfo2.getValue(i, "QST_CD"),
										VRSInfo2.getValue(i, "CNTR_CD"),
										VRSInfo2.getValue(i, "QST_CDNM"),
										(j+1),
										rVRS[j].trim(),
										VRSInfo2.getValue(i, "QST_KIND_CD"),
										VRSInfo2.getValue(i, "SORTKEY"),
										VRSInfo2.getValue(i, "DEL_YN"),
										VRSInfo2.getValue(i, "DEL_DATE")
								});
							}else continue;
						}							
					}
					
					imsiVRSInfo.clear();
					
					String bef_script_sent = "";
					
					for(int i =0; i< tempVRSInfo.rowSize(); i++){
						String rVRS = tempVRSInfo.getValue(i, "SCRIPT_SENT").toString().trim();
						String v_qst_cd1 = tempVRSInfo.getValue(i, "QST_CD").toString();
						String v_qst_cd2 =  "";
						String v_cntr_cd1 = tempVRSInfo.getValue(i, "CNTR_CD").toString();
						String v_cntr_cd2 = "";
						//IVRLogger.debug("###########CHARAT - " + i + " :::: "+rVRS.charAt(rVRS.length()-1));
						//IVRLogger.debug("###########CHARAT - " + i + " :::: LEN::  "+rVRS.substring(rVRS.length()-1, rVRS.length()));
						//IVRLogger.debug("며".equals(rVRS.charAt(rVRS.length()-1)));
						//IVRLogger.debug("며".equals(rVRS.substring(rVRS.length()-1, rVRS.length())));
						if(i == 0){
							
							if(i < tempVRSInfo.rowSize()-1){
								v_qst_cd2 = tempVRSInfo.getValue(i+1, "QST_CD").toString();
								v_cntr_cd2 = tempVRSInfo.getValue(i+1, "CNTR_CD").toString();
							}
							
							if(v_qst_cd1.equals(v_qst_cd2) && v_cntr_cd1.equals(v_cntr_cd2)){
								//bef_script_sent = rVRS;
								
								if(("리며".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("세".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("는".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("로".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("의".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("데".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("시".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("비".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("금".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("망".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("당".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("면".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("후".equals(rVRS.substring(rVRS.length()-1, rVRS.length())))){
									
									bef_script_sent = rVRS;
									
									imsiVRSInfo.addRow(new Object[] {
											tempVRSInfo.getValue(i, "SC_SNO"),
											tempVRSInfo.getValue(i, "SC_LCLF_CD"),
											tempVRSInfo.getValue(i, "SC_SCLF_CD"),
											tempVRSInfo.getValue(i, "QST_CD"),
											tempVRSInfo.getValue(i, "CNTR_CD"),
											tempVRSInfo.getValue(i, "QST_CDNM"),
											tempVRSInfo.getValue(i, "SEQ"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "QST_KIND_CD"),
											tempVRSInfo.getValue(i, "SORTKEY"),
											tempVRSInfo.getValue(i, "DEL_YN"),
											tempVRSInfo.getValue(i, "DEL_DATE")
									});
									
									
								}else{
									finalVRSInfo.addRow(new Object[] {
											tempVRSInfo.getValue(i, "SC_SNO"),
											tempVRSInfo.getValue(i, "SC_LCLF_CD"),
											tempVRSInfo.getValue(i, "SC_SCLF_CD"),
											tempVRSInfo.getValue(i, "QST_CD"),
											tempVRSInfo.getValue(i, "CNTR_CD"),
											tempVRSInfo.getValue(i, "QST_CDNM"),
											tempVRSInfo.getValue(i, "SEQ"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "QST_KIND_CD"),
											tempVRSInfo.getValue(i, "SORTKEY"),
											tempVRSInfo.getValue(i, "DEL_YN"),
											tempVRSInfo.getValue(i, "DEL_DATE")
									});
								}

							}else{
								finalVRSInfo.addRow(new Object[] {
										tempVRSInfo.getValue(i, "SC_SNO"),
										tempVRSInfo.getValue(i, "SC_LCLF_CD"),
										tempVRSInfo.getValue(i, "SC_SCLF_CD"),
										tempVRSInfo.getValue(i, "QST_CD"),
										tempVRSInfo.getValue(i, "CNTR_CD"),
										tempVRSInfo.getValue(i, "QST_CDNM"),
										tempVRSInfo.getValue(i, "SEQ"),
										tempVRSInfo.getValue(i, "SCRIPT_SENT"),
										tempVRSInfo.getValue(i, "SCRIPT_SENT"),
										tempVRSInfo.getValue(i, "QST_KIND_CD"),
										tempVRSInfo.getValue(i, "SORTKEY"),
										tempVRSInfo.getValue(i, "DEL_YN"),
										tempVRSInfo.getValue(i, "DEL_DATE")
								});
							}
							
						}else{
							v_qst_cd2 = tempVRSInfo.getValue(i-1, "QST_CD").toString();
							v_cntr_cd2 = tempVRSInfo.getValue(i-1, "CNTR_CD").toString();
							
							IVRLogger.debug("##########################################################");
							IVRLogger.debug(i +" 번째 qst_cd = " + v_qst_cd1 + "// cntr_cd =" + v_cntr_cd1);
							IVRLogger.debug((i-1)+"번째 qst_cd = " + v_qst_cd2 + "// cntr_cd =" + v_cntr_cd2);
							IVRLogger.debug("");
							
							if(v_qst_cd1.equals(v_qst_cd2) && v_cntr_cd1.equals(v_cntr_cd2)){
									if (("리며".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("세".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("는".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("로".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("의".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("데".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("시".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("비".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("금".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("망".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("당".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("면".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
										    ("후".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))))
										{
											bef_script_sent = bef_script_sent + " "+rVRS;
											
											//IVRLogger.debug("##########BEFORE_SCRIPT_SENT:: "+ i +" ### " + bef_script_sent);
											
											imsiVRSInfo.addRow(new Object[] {
													tempVRSInfo.getValue(i, "SC_SNO"),
													tempVRSInfo.getValue(i, "SC_LCLF_CD"),
													tempVRSInfo.getValue(i, "SC_SCLF_CD"),
													tempVRSInfo.getValue(i, "QST_CD"),
													tempVRSInfo.getValue(i, "CNTR_CD"),
													tempVRSInfo.getValue(i, "QST_CDNM"),
													tempVRSInfo.getValue(i, "SEQ"),
													tempVRSInfo.getValue(i, "SCRIPT_SENT"),
													tempVRSInfo.getValue(i, "QST_KIND_CD"),
													tempVRSInfo.getValue(i, "SORTKEY"),
													tempVRSInfo.getValue(i, "DEL_YN"),
													tempVRSInfo.getValue(i, "DEL_DATE")
											});
											
										} else {
											
											//IVRLogger.debug("##########BEFORE_SCRIPT_SENT:: else 일때 " + bef_script_sent);

											if("".equals(bef_script_sent))
											{
												finalVRSInfo.addRow(new Object[] {
														tempVRSInfo.getValue(i, "SC_SNO"),
														tempVRSInfo.getValue(i, "SC_LCLF_CD"),
														tempVRSInfo.getValue(i, "SC_SCLF_CD"),
														tempVRSInfo.getValue(i, "QST_CD"),
														tempVRSInfo.getValue(i, "CNTR_CD"),
														tempVRSInfo.getValue(i, "QST_CDNM"),
														tempVRSInfo.getValue(i, "SEQ"),
														tempVRSInfo.getValue(i, "SCRIPT_SENT"),
														tempVRSInfo.getValue(i, "SCRIPT_SENT"),
														tempVRSInfo.getValue(i, "QST_KIND_CD"),
														tempVRSInfo.getValue(i, "SORTKEY"),
														tempVRSInfo.getValue(i, "DEL_YN"),
														tempVRSInfo.getValue(i, "DEL_DATE")
												});
											} else {
												
												imsiVRSInfo.addRow(new Object[] {
														tempVRSInfo.getValue(i, "SC_SNO"),
														tempVRSInfo.getValue(i, "SC_LCLF_CD"),
														tempVRSInfo.getValue(i, "SC_SCLF_CD"),
														tempVRSInfo.getValue(i, "QST_CD"),
														tempVRSInfo.getValue(i, "CNTR_CD"),
														tempVRSInfo.getValue(i, "QST_CDNM"),
														tempVRSInfo.getValue(i, "SEQ"),
														tempVRSInfo.getValue(i, "SCRIPT_SENT"),
														tempVRSInfo.getValue(i, "QST_KIND_CD"),
														tempVRSInfo.getValue(i, "SORTKEY"),
														tempVRSInfo.getValue(i, "DEL_YN"),
														tempVRSInfo.getValue(i, "DEL_DATE")
												});
												
												bef_script_sent = bef_script_sent + " "+rVRS;
												
												if(imsiVRSInfo.rowSize() > 0)
												{
													for(int j=0; j<imsiVRSInfo.rowSize(); j++ )
													{
														finalVRSInfo.addRow(new Object[] {
																imsiVRSInfo.getValue(j, "SC_SNO"),
																imsiVRSInfo.getValue(j, "SC_LCLF_CD"),
																imsiVRSInfo.getValue(j, "SC_SCLF_CD"),
																imsiVRSInfo.getValue(j, "QST_CD"),
																imsiVRSInfo.getValue(j, "CNTR_CD"),
																imsiVRSInfo.getValue(j, "QST_CDNM"),
																imsiVRSInfo.getValue(j, "SEQ"),
																imsiVRSInfo.getValue(j, "SCRIPT_SENT"),
																bef_script_sent,
																imsiVRSInfo.getValue(j, "QST_KIND_CD"),
																imsiVRSInfo.getValue(j, "SORTKEY"),
																imsiVRSInfo.getValue(j, "DEL_YN"),
																imsiVRSInfo.getValue(j, "DEL_DATE")
														});
													}
												}
												
												imsiVRSInfo.clear();
											}
											
											
											//IVRLogger.debug("###########SC_SNO = " + finalVRSInfo.getValue(finalVRSInfo.rowSize()-1, "SC_SNO"));
											//IVRLogger.debug("###########QST_CD = " + finalVRSInfo.getValue(finalVRSInfo.rowSize()-1, "QST_CD"));
											//IVRLogger.debug("###########CNTR_CD = " + finalVRSInfo.getValue(finalVRSInfo.rowSize()-1, "CNTR_CD"));
											//IVRLogger.debug("###########SEQ = " + finalVRSInfo.getValue(finalVRSInfo.rowSize()-1, "SEQ"));
											//IVRLogger.debug("###########bef_script_sent = " + finalVRSInfo.getValue(finalVRSInfo.rowSize()-1, "SCRIPT_SENT"));
											
											bef_script_sent = "";
										}

								
							}else{
								
								if(imsiVRSInfo.rowSize() > 0){ //첫번째 row가 현재 row랑 다른 경우
									for(int j=0; j<imsiVRSInfo.rowSize(); j++ )
									{
										finalVRSInfo.addRow(new Object[] {
												imsiVRSInfo.getValue(j, "SC_SNO"),
												imsiVRSInfo.getValue(j, "SC_LCLF_CD"),
												imsiVRSInfo.getValue(j, "SC_SCLF_CD"),
												imsiVRSInfo.getValue(j, "QST_CD"),
												imsiVRSInfo.getValue(j, "CNTR_CD"),
												imsiVRSInfo.getValue(j, "QST_CDNM"),
												imsiVRSInfo.getValue(j, "SEQ"),
												imsiVRSInfo.getValue(j, "SCRIPT_SENT"),
												bef_script_sent,
												imsiVRSInfo.getValue(j, "QST_KIND_CD"),
												imsiVRSInfo.getValue(j, "SORTKEY"),
												imsiVRSInfo.getValue(j, "DEL_YN"),
												imsiVRSInfo.getValue(j, "DEL_DATE")
										});
									}
								}
								
								imsiVRSInfo.clear();
								
								if(("리며".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("세".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("는".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("로".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("의".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("데".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("시".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("비".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("금".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("망".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("당".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("면".equals(rVRS.substring(rVRS.length()-1, rVRS.length()))) ||
									    ("후".equals(rVRS.substring(rVRS.length()-1, rVRS.length())))){
									
									bef_script_sent = rVRS;
									
									imsiVRSInfo.addRow(new Object[] {
											tempVRSInfo.getValue(i, "SC_SNO"),
											tempVRSInfo.getValue(i, "SC_LCLF_CD"),
											tempVRSInfo.getValue(i, "SC_SCLF_CD"),
											tempVRSInfo.getValue(i, "QST_CD"),
											tempVRSInfo.getValue(i, "CNTR_CD"),
											tempVRSInfo.getValue(i, "QST_CDNM"),
											tempVRSInfo.getValue(i, "SEQ"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "QST_KIND_CD"),
											tempVRSInfo.getValue(i, "SORTKEY"),
											tempVRSInfo.getValue(i, "DEL_YN"),
											tempVRSInfo.getValue(i, "DEL_DATE")
									});
									
									
								}else{
									finalVRSInfo.addRow(new Object[] {
											tempVRSInfo.getValue(i, "SC_SNO"),
											tempVRSInfo.getValue(i, "SC_LCLF_CD"),
											tempVRSInfo.getValue(i, "SC_SCLF_CD"),
											tempVRSInfo.getValue(i, "QST_CD"),
											tempVRSInfo.getValue(i, "CNTR_CD"),
											tempVRSInfo.getValue(i, "QST_CDNM"),
											tempVRSInfo.getValue(i, "SEQ"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "SCRIPT_SENT"),
											tempVRSInfo.getValue(i, "QST_KIND_CD"),
											tempVRSInfo.getValue(i, "SORTKEY"),
											tempVRSInfo.getValue(i, "DEL_YN"),
											tempVRSInfo.getValue(i, "DEL_DATE")
									});
									
								}
								
							}
							
						}
					
					}
					
					if(imsiVRSInfo.rowSize() > 0){ //마지막 row가 남아있는 경우
						for(int j=0; j<imsiVRSInfo.rowSize(); j++ )
						{
							finalVRSInfo.addRow(new Object[] {
									imsiVRSInfo.getValue(j, "SC_SNO"),
									imsiVRSInfo.getValue(j, "SC_LCLF_CD"),
									imsiVRSInfo.getValue(j, "SC_SCLF_CD"),
									imsiVRSInfo.getValue(j, "QST_CD"),
									imsiVRSInfo.getValue(j, "CNTR_CD"),
									imsiVRSInfo.getValue(j, "QST_CDNM"),
									imsiVRSInfo.getValue(j, "SEQ"),
									imsiVRSInfo.getValue(j, "SCRIPT_SENT"),
									bef_script_sent,
									imsiVRSInfo.getValue(j, "QST_KIND_CD"),
									imsiVRSInfo.getValue(j, "SORTKEY"),
									imsiVRSInfo.getValue(j, "DEL_YN"),
									imsiVRSInfo.getValue(j, "DEL_DATE")
							});
						}
					}
					
					//ErrorLogger.debug("#############getCount :: 중간단계 :: " + tempVRSInfo.rowSize());
					
					tran.begin();
					
					/*sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getVRSParser2.del");
					sqlParam.addValue("tempVRSInfo", tempVRSInfo);
				
					SQLServiceManager.getInstance().execute(sqlParam, tran);*/
					
					//IVRLogger.debug(finalVRSInfo.toString());
					
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getVRSParser2.ins2");
					sqlParam.addValue("finalVRSInfo", finalVRSInfo);
				
					SQLServiceManager.getInstance().execute(sqlParam, tran);
					//
					//for(int j=0; j< VRSInfo2.rowSize(); j++){
					sqlParam.clear();
					sqlParam.setSqlName("msens.xcron.hansol.getSentSplit.flag_y.upd");
						//sqlParam.addValue("QST_CD", VRSInfo2.getValue(j, "QST_CD"));
						//sqlParam.addValue("CNTR_CD", VRSInfo2.getValue(j, "CNTR_CD"));
					sqlParam.addValue("VRSInfo2", VRSInfo2);
					
					SQLServiceManager.getInstance().execute(sqlParam, tran);

					//}
					tran.commit();
					
				}
	
		}catch (Exception e) {
			tran.rollback();
			
			try {
				tran.begin();
				
				//FLAG 값 S로 업데이트 수행
				SQLParam sqlParam5 =  new SQLParam();  
				sqlParam5.setSqlName("msens.xcron.hansol.getSentSplit.flag_n.upd");
				sqlParam5.addValue("ParsingRollback", ParsingRollback); //call_info에 VA_FLAG Y로 업데이트
				SQLServiceManager.getInstance().execute(sqlParam5, tran);
				
				tran.commit();
			} catch (SQLServiceException e1) {
				// TODO Auto-generated catch block
				tran.rollback();
				e1.printStackTrace();
			}
			
			
			e.printStackTrace();
			//ErrorLogger.debug();
			ErrorLogger.debug("####error"+e.getMessage());
			e.printStackTrace();
			IVRLogger.error(e.getMessage());
		}
		
	}
	
	

}

