package xcron.com.webaction;


import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;


import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class MrLsListWebAction extends XwingWebAction {
	/**
	 * MR/LS/청약거절 콜을 가져와서 ms_mrls_list 테이블에 넣는다.
	 */
	SQLParam sqlParam = new SQLParam();
	SQLParam sqlParam2 = new SQLParam();
	SQLParam sqlResult = new SQLParam();
	ListParam befMrLsList = new ListParam(new String[] {"CON_ENT_DGN_NO", "REG_DT", "REC_ID", "MR_CODE", "LS_CODE", 
			"CENTER_CD", "CENTER_NM", "AGENT_ID", "AGENT_NM", "CUST_ID", "CON_IP_PSN_NAME", "BIRTH_DT","VA_FLAG"});
	ListParam mrLsList = new ListParam(new String[] {"CON_ENT_DGN_NO", "UCID", "REG_DT", "MR_CODE", "LS_CODE", 
			"CENTER_CD", "CENTER_NM", "AGENT_ID", "AGENT_NM", "CUST_ID", "CON_IP_PSN_NAME", "BIRTH_DT","REC_YN","PASS_YN"});
	ListParam DS_UCID = null;

	static JediTransaction tran = JediTransactionManager.getJediTransaction();
	ListParam mrlsRollback = null;
	int s_flag = 0; 
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		IVRLogger.info("####2. BEF MRLS에서 미분석 데이터 500건 수집##");
		try {
			
			sqlParam.clear();
			sqlParam2.clear();
			
			sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_3"); 
				
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
			befMrLsList = sqlResult.getListParam("DS_LIST");
			
			if(befMrLsList.rowSize() > 0){
				tran.begin();
				mrlsRollback = befMrLsList;
				IVRLogger.info("####2-1. S 플래그 업데이트###");
				sqlParam2.setSqlName("msens.xcron.hansol.getmrlscallwebaction_4"); 
				sqlParam2.addValue("DS_LIST", befMrLsList);
				SQLServiceManager.getInstance().execute(sqlParam2, tran);
				tran.commit();
			}else{

				IVRLogger.info("####2-1. 데이터 없어서 return###");
				return;
			}

			IVRLogger.info("####2. MRLS LIST 넣을 데이터 수집 성공###");
			
			IVRLogger.info("####3,4. MRLS LIST 넣을 데이터 조합###");
			String center_cd = ""; String rec_id = "";
			String mr_cd = ""; String ls_cd = ""; String ced_no = "";
			String reg_dt = "";
			Code[] code = CodeUtil.getCodes("SYS002"); //직영센터관리
			boolean multi_yn = false; //직영여부 
			String rec_yn = "N";   //녹취여부
				
				IVRLogger.info("####3-1. 센터로 구분###");
				for(int i =0; i< befMrLsList.rowSize();i++){
					if(s_flag == 2){ 
						IVRLogger.info("#### 오류나서 종료 ####");
						return;
					} 
					center_cd = befMrLsList.getValue(i, "CENTER_CD").toString();
					ced_no = befMrLsList.getValue(i, "CON_ENT_DGN_NO").toString();
					rec_id = befMrLsList.getValue(i, "REC_ID").toString();
					mr_cd = befMrLsList.getValue(i, "MR_CODE").toString();
					ls_cd = befMrLsList.getValue(i, "LS_CODE").toString();
					reg_dt = befMrLsList.getValue(i,"REG_DT").toString();
					
					mrLsList.addRow(new Object[] {
							ced_no,
							"",
							reg_dt,
							"",
							"",
							center_cd,
							befMrLsList.getValue(i,"CENTER_NM").toString(),
							befMrLsList.getValue(i,"AGENT_ID").toString(),
							befMrLsList.getValue(i,"AGENT_NM").toString(),
							befMrLsList.getValue(i,"CUST_ID").toString(),
							befMrLsList.getValue(i,"CON_IP_PSN_NAME").toString(),
							befMrLsList.getValue(i,"BIRTH_DT").toString(),
							"",
							""
					});
					
					for(int l = 0; code != null && l < code.length; l++){
						if (!"Y".equalsIgnoreCase(code[l].getUseYn())) {
							continue;
						}
						
						if(center_cd.equals(code[l].getCodeId())){
							multi_yn = true;
							break;
						}else{
							multi_yn = false;
						}
					}	
					
					if(multi_yn){
						getSPDBData(rec_id,ced_no,mr_cd,ls_cd,reg_dt);
					}else{
						getSPDBData(mr_cd,ls_cd,reg_dt,"");
					}
				}
				IVRLogger.info("####3. SPDB 데이터 조합 완료###");

				if(s_flag == 3){
					IVRLogger.info("####4. 평가표에서 사전동의 여부 확인###");
					getEstimateFlag();
				}
				if(s_flag == 4){
					IVRLogger.info("####5. 최종데이터 저장###");
					insertMrLsList();
				}
				
				if(s_flag == 5){
					IVRLogger.info("####6. Bef 에서 분석한 데이터 지우기###");
					deleteBefList();
				}
				
		} catch (Exception e) {
			// TODO: handle exception
			tran.rollback();
			
			//callInfoRollback
			mrLsListRollback();
			
			/*e.getStackTrace();*/
			IVRLogger.info("MrLsCallWebAction Error :: "+e.getMessage());
		}
		
		
		}
		
		//직영 : REC_ID를 던져서 매칭되는 콜 조회
		public void getSPDBData(String rec_id, String ced_no,String mr_cd,String ls_cd,String reg_dt){
			sqlParam.clear();
			DS_UCID = null;
			
			try {
				if(!"".equals(rec_id)){
					sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_5"); 
					sqlParam.addValue("REC_ID","SMC-"+rec_id);
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					DS_UCID= sqlResult.getListParam("DS_UCID");
				}
				
				
				if(DS_UCID != null){
					if(DS_UCID.rowSize() > 0){
						/*for(int i = 0 ; i<DS_UCID.rowSize(); i++){ // REC_ID 키라서 ucid가 여러개 나올 수 가 없음.
							if(i != 0){
								mrLsList.addRow(new Object[] { 
												mrLsList.getValue(mrLsList.rowSize()-1,"CON_ENT_DGN_NO").toString(),
												"",
												mrLsList.getValue(mrLsList.rowSize()-1,"REG_DT").toString(),
												"",
												"",
												mrLsList.getValue(mrLsList.rowSize()-1,"CENTER_CD").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CENTER_NM").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_ID").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_NM").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CUST_ID").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CON_IP_PSN_NAME").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"BIRTH_DT").toString(),
												"",
												""
								});
							}*/
							IVRLogger.info("####3-2. 직영: 매칭됨###ced_no:"+ced_no +"콜갯수:"+DS_UCID.rowSize());
							mrLsList.setValue(mrLsList.rowSize()-1,"UCID",DS_UCID.getValue(0, "UCID"));
							mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",mr_cd); //sp에 있는거는 다른 설계의 mr,ls code일 수도 있기 때문에 tm에서 가져온걸 넣는다.
							mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",ls_cd);
							mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","Y");
						//}
					}else{
						/*int idx = 0;
						for(int j = 0; j<mrLsList.rowSize(); j++){
							if(ced_no.equals(mrLsList.getValue(j, "CON_ENT_DGN_NO"))){
								idx++;
							}
						}
						mrLsList.setValue(mrLsList.rowSize()-1,"UCID",idx+"-"+ced_no);
						mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","N");
						mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",mr_cd); //sp에 있는거는 다른 설계의 mr,ls code일 수도 있기 때문에 tm에서 가져온걸 넣는다.
						mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",ls_cd);*/
						
						//REG_DT와 MR,LS코드로 한번 더 조회해본다.
						//정확한 rec 매칭 안될 수 있다..
						IVRLogger.info("####3-2. 직영:### 매칭안됨...한번 더 분석해본다! ced_no:"+ced_no);
						getSPDBData(mr_cd,ls_cd,reg_dt,"Y");
						
					}
				}else{	//직영인데 REC_ID가 없는 경우
					int idx = 0;
					for(int j = 0; j<mrLsList.rowSize(); j++){
						if(ced_no.equals(mrLsList.getValue(j, "CON_ENT_DGN_NO"))){
							idx++;
						}
					}
					IVRLogger.info("####3-2. 직영: REC_ID없음### UCID:"+idx+"-"+ced_no);
					mrLsList.setValue(mrLsList.rowSize()-1,"UCID",idx+"-"+ced_no);
					mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",mr_cd); //sp에 있는거는 다른 설계의 mr,ls code일 수도 있기 때문에 tm에서 가져온걸 넣는다.
					mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",ls_cd);
					mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","N");
				}
				
				if(s_flag != 2) s_flag = 3;
				
			} catch (Exception e) {
				s_flag = 2;
				IVRLogger.info("####3-2. 직영 SPDB 데이터 조합 에러###"+s_flag);
				// TODO Auto-generated catch block
				mrLsListRollback();
				IVRLogger.info("MrLsCallWebAction Error :: "+e.getMessage());
			}
		}
		
		//스폰서 : REG_DT(협회조회일시가 녹취일시 사이에 있어야함),MR_CD,LS_CD를 던져서 매칭되는 콜 조회
		//직영 REC_ID로 매칭 안된 경우 다시 MR,LS,REG_DT로 조회
		public void getSPDBData(String mr_cd,String ls_cd,String reg_dt,String center_cd){
			sqlParam.clear();
			DS_UCID = null;
			String ced_no = mrLsList.getValue(mrLsList.rowSize()-1,"CON_ENT_DGN_NO").toString();
			
			try {
				
				if((!"".equals(ls_cd) || !"".equals(mr_cd))){
					String sql = "";
					String[] mr_cds = null;
					String[] ls_cds = null;
					if(!mr_cd.equals("")){
						mr_cds = mr_cd.split(",");
						for(int i = 0; i<mr_cds.length; i++){
							if(!"".equals(sql)) sql += " OR ";
							sql += "MR_CODE LIKE '%"+mr_cds[i]+"%'";
						}
					}
					
					if(!ls_cd.equals("")){
						ls_cds = ls_cd.split(",");
						for(int i = 0; i<ls_cds.length; i++){
							if(!"".equals(sql)) sql += " OR ";
							sql += "LS_CODE LIKE '%"+ls_cds[i]+"%'";
						}
					}
					
					
					sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_5_1"); 
					sqlParam.addValue("SQL",sql);
					sqlParam.addValue("REG_DT",reg_dt);
					
					/* 직영 재조회 시 센터와 상담사,고객명으로 조회하는 경우
					if(!"".equals(center_cd)){ //center 넘어오면 직영조회 후 한번 더 분석임.
						//※ 계피상이의 경우 피보험자 고객명과 callCustName이 다르기 때문에 매칭이 안될 수 있다.
					       sqlParam.addValue("CENTER_CD",center_cd);	
					       sqlParam.addValue("AGENT_ID", mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_ID"));
					       sqlParam.addValue("CUST_NM", mrLsList.getValue(mrLsList.rowSize()-1,"CON_IP_PSN_NAME"));
					}
					*/
					
					sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
					DS_UCID= sqlResult.getListParam("DS_UCID");
				}
				
				if(DS_UCID != null){ 	
					if(DS_UCID.rowSize() > 0){
						IVRLogger.info("####3-2. 스폰서: 매칭됨####ced_no:"+ced_no +"콜갯수:"+DS_UCID.rowSize());
						for(int i = 0 ; i<DS_UCID.rowSize(); i++){
							
							if(i != 0){
								mrLsList.addRow(new Object[] { 
												ced_no,
												"",
												mrLsList.getValue(mrLsList.rowSize()-1,"REG_DT").toString(),
												"",
												"",
												mrLsList.getValue(mrLsList.rowSize()-1,"CENTER_CD").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CENTER_NM").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_ID").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_NM").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CUST_ID").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"CON_IP_PSN_NAME").toString(),
												mrLsList.getValue(mrLsList.rowSize()-1,"BIRTH_DT").toString(),
												"",
												""
								});
							}
							
							if(!"".equals(center_cd)){
									IVRLogger.info("@@@@직영 재 조회후 매칭되는 콜 찾음@@@");	
									IVRLogger.info("설계번호 : "+ced_no + ", 매핑된 UCID:" +DS_UCID.getValue(i, "UCID"));
									IVRLogger.info(mr_cd + "//("+DS_UCID.getValue(i, "MR_CODE")+")//"+ls_cd+"("+DS_UCID.getValue(i, "LS_CODE")+")");
									IVRLogger.info("tm상담사 >> "+mrLsList.getValue(mrLsList.rowSize()-1,"AGENT_NM").toString() +"//콜상담사>>"+DS_UCID.getValue(i, "AGENTNAME"));
									IVRLogger.info("tm고객 >> "+mrLsList.getValue(mrLsList.rowSize()-1,"CON_IP_PSN_NAME").toString() +"//콜고객>>"+DS_UCID.getValue(i, "CUSTOMERNAME"));
							}
							
							
							mrLsList.setValue(mrLsList.rowSize()-1,"UCID",DS_UCID.getValue(i, "UCID"));
							//콜이 여러개 나올 수 있으므로 sp에서 조회해온 그 rec에 해당하는 mr_code, ls_code를 넣는다. 
							//다만, TM에서 말하는 mr_cd,ls_cd가 없을 수 있다.
							mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",DS_UCID.getValue(i, "MR_CODE")); 
							mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",DS_UCID.getValue(i, "LS_CODE"));
							mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","Y");
							
						}
					}else{
						int idx = 0;
						for(int j = 0; j<mrLsList.rowSize(); j++){
							if(ced_no.equals(mrLsList.getValue(j, "CON_ENT_DGN_NO"))){
								idx++;
							}
						}
						IVRLogger.info("####3-2. 스폰서:### 매칭안됨... UCID:"+idx+"-"+ced_no);
						mrLsList.setValue(mrLsList.rowSize()-1,"UCID",idx+"-"+ced_no);
						mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",mr_cd); //sp에 있는거는 다른 설계의 mr,ls code일 수도 있기 때문에 tm에서 가져온걸 넣는다.
						mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",ls_cd);
						mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","N");
					}
				}else{
					int idx = 0;
					for(int j = 0; j<mrLsList.rowSize(); j++){
						if(ced_no.equals(mrLsList.getValue(j, "CON_ENT_DGN_NO"))){
							idx++;
						}
					}
					IVRLogger.info("####3-2. 스폰서:### MRLS코드안넘어옴### UCID::"+idx+"-"+ced_no);
					mrLsList.setValue(mrLsList.rowSize()-1,"UCID",idx+"-"+ced_no);
					mrLsList.setValue(mrLsList.rowSize()-1,"MR_CODE",mr_cd); //sp에 있는거는 다른 설계의 mr,ls code일 수도 있기 때문에 tm에서 가져온걸 넣는다.
					mrLsList.setValue(mrLsList.rowSize()-1,"LS_CODE",ls_cd);
					mrLsList.setValue(mrLsList.rowSize()-1,"REC_YN","N");
				}
				s_flag = 3;
				
			} catch (Exception e) {
				s_flag = 2;
				IVRLogger.info("####3-2. 스폰서 SPDB 데이터 조합 에러###"+s_flag);
				// TODO Auto-generated catch block
				mrLsListRollback();
				IVRLogger.info("MrLsCallWebAction Error :: "+e.getMessage());
			}
		}
		
		public void getEstimateFlag(){
			sqlParam.clear();
			ListParam DS_LIST = null;
			String ced_no = "";
			int cnt = 0;
			try {
					for(int i = 0; i<mrLsList.rowSize(); i++){
						ced_no = mrLsList.getValue(i, "CON_ENT_DGN_NO").toString();
					
						sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_6"); 
						sqlParam.addValue("CED_NO",ced_no);
						
						sqlResult = SQLServiceManager.getInstance().execute(sqlParam);
						cnt = Integer.parseInt(sqlResult.getListParam("DS_LIST").getParam(0).getString("CNT","0"));
						
						if(cnt > 0){
							mrLsList.setValue(i,"PASS_YN","Y");
						}else{
							mrLsList.setValue(i,"PASS_YN","N");
						}
					}
				s_flag = 4;
				IVRLogger.info("####4. 평가표에서 사전동의 여부 확인 완료###");
			} catch (Exception e) {
				s_flag = 3;
				IVRLogger.info("####4. 평가표 데이터 가져오기 에러###");
				// TODO Auto-generated catch block
				
				mrLsListRollback();
				IVRLogger.info("GetMrLsCallWebAction Error :: "+e.getMessage());
			}
			
		}
		
		public void insertMrLsList(){
			sqlParam.clear();
			try {
				tran.begin();	
				sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_7"); 
				sqlParam.addValue("MRLSLIST",mrLsList);
				
				SQLServiceManager.getInstance().execute(sqlParam);
				
				sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_8"); 
				sqlParam.addValue("MRLSLIST",mrLsList);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				
				tran.commit();
				s_flag = 5;
				IVRLogger.info("####5. 최종데이터 저장완료###");
			} catch (Exception e) {
				IVRLogger.info("####5. 최종데이터 저장 에러###");
				// TODO Auto-generated catch block
				tran.rollback();
				mrLsListRollback();
				IVRLogger.info("MrLsCallWebAction Error :: "+e.getMessage());
			}
		}
		
		public void deleteBefList(){
			sqlParam.clear();
			try {
				tran.begin();	
				
				sqlParam.setSqlName("msens.xcron.hansol.getmrlscallwebaction_9"); 
				sqlParam.addValue("befMrLsList",befMrLsList);
				
				SQLServiceManager.getInstance().execute(sqlParam, tran);
				tran.commit();
				IVRLogger.info("####6. Bef 에서 분석한 데이터 지우기 완료###");
			} catch (Exception e) {
				IVRLogger.info("####6. BEF DELETE 에러###");
				// TODO Auto-generated catch block
				s_flag = 5;
				tran.rollback();
				mrLsListRollback();
				IVRLogger.info("MrLsCallWebAction Error :: "+e.getMessage());
			}
			
			
		}
		
		public void mrLsListRollback(){
			//callInfoRollback
			if(mrlsRollback != null){
				if(mrlsRollback.rowSize() > 0){
					
					try {
						tran.begin();
						IVRLogger.error(mrLsList.toString());
						//에러난 경우 flag = 'N'으로 다시 변경
						SQLParam sqlParam5 =  new SQLParam();  
						sqlParam5.setSqlName("msens.xcron.hansol.getmrlscallwebaction_4_1");
						sqlParam5.addValue("mrlsRollback", mrlsRollback);
						SQLServiceManager.getInstance().execute(sqlParam5, tran);
						
						tran.commit();
						IVRLogger.info("##롤백 완료##");
						mrlsRollback = null;
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						
						tran.rollback();
						IVRLogger.info("##롤백하다 에러발생##");
						IVRLogger.info("MrLsCallWebAction Error :: "+e1.getMessage());
					}
					
					
				}
			}
			
		}
}

