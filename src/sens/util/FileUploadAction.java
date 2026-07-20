/* ******************************************************
* This software was developed and owned by Locus  
* Illegal use of this software will violate the Copy Right Law 
* ******************************************************  
* Program Name : ���� ���ε�
* Function description : ���� ���ε� ���
* Programmer Name : ȫ����
* Creation Date : 
* ******************************************************  
*                    P R O G R A M H I S T O R Y  
* ******************************************************  
* DATE	: PRGAMMER	:   REASON 
*/

package sens.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import com.locus.jedi.biz.BizDelegate;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.CommonDTO;
import com.locus.jedi.waf.controller.JediResponse;

public class FileUploadAction extends Thread{
	private CommonDTO common    = null;
	private Param param         = null;
	private SQLParam sqlparam = new SQLParam();
	private SQLParam sqlparam2 = new SQLParam();
	private SQLParam sqlparam3 = new SQLParam();
	private SQLParam sqlresult = new SQLParam();
	private ListParam sqlresultList =null;
	private Param sqlresultParam = null;
	private Param currParam = null;
	private String iDateTime = null;
	private String iDate = null;
	private String iTime = null;
	private int juminLength = 0;
	private boolean errorResult;
	
	private String key_col = "REGIST_NO";
	private String ANL_GUBUN = "C";
	private String sql_GUBUN = "list.dataupload.";
	private String key_ANL_ID = "CUST_ANL_ID";
/*===================================================
	생성. 파라미터 받아서 입력값 넣어준다.
=====================================================*/
    public FileUploadAction(CommonDTO common, Param param) {
    	this.common = common;
        this.param = param;
	}

    public String run(JediResponse res){
    	ListParam excelList = param.getListParam("excelList");
		ErrorLogger.debug("▶▶▶▶▶▶[excelList컬럼]▶"+excelList.getColumns());
		GregorianCalendar calendar = new GregorianCalendar();
		  int year  = calendar.get(Calendar.YEAR);
		  int month = calendar.get(Calendar.MONTH)+1;//특이하게 월은 0월부터 시작, +1해서 출력해야됨
		  int date  = calendar.get(Calendar.DATE);
		  int ampm  = calendar.get(Calendar.AM_PM);//0이면오전 1이면오후
		  int hour  = calendar.get(Calendar.HOUR_OF_DAY);
		  int min   = calendar.get(Calendar.MINUTE);
		  int sec   = calendar.get(Calendar.SECOND);

		  iDateTime= ""+year+""+String.format("%02d", month)+""+String.format("%02d", date)+""+String.format("%02d", hour)+""+String.format("%02d", min)+""+String.format("%02d", sec);
		  iDate=""+year+""+String.format("%02d", month)+""+String.format("%02d", date);
		  iTime=String.format("%02d", hour)+""+String.format("%02d", min)+""+String.format("%02d", sec);
		  ErrorLogger.debug("▶▶▶▶▶▶[시간]▶"+iDateTime);
		  ErrorLogger.debug("▶▶▶▶▶▶[ANL_TYPE]▶"+param.getString("ANL_TYPE", "01"));
		  if(param.getString("ANL_TYPE", "01").equals("03")){
			  
			  key_col = "CONTACT_ID";
			  ANL_GUBUN = "I";
			  sql_GUBUN = "anl.contact.";
			  key_ANL_ID = "CONTACT_ANL_ID";
			  ErrorLogger.debug("key_col:"+key_col);
		  }
		  
    	try{
			  //데이터 업로드 목록 insert
			  	sqlparam.clear();
				sqlparam.setSqlName(sql_GUBUN + "catalog.ins"); //anl.contact.catalog.ins
				param.addValue(key_ANL_ID, ANL_GUBUN+iDateTime);
				param.addValue("EXEC_DATE", iDate);
				param.addValue("EXEC_TIME", iTime);
				param.addValue("EXEC_ID", param.getString("EXEC_ID"));
				param.addValue("FILE_UPLOAD_YN", param.getString("FILE_UPLOAD_YN","Y"));
				sqlparam.addParam(param);
				sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				//주민번호 길이 가져오기 
				
				sqlparam.clear();
				sqlparam.setSqlName("list.dataupload.codebook.sch");
				param.addValue("CODE_TYPE", "EXC002");
				param.addValue("CODE_ID", "01");
				sqlparam.addParam(param);
				sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				sqlresultList = sqlresult.getListParam("DS_CODE");
				sqlresultParam = sqlresultList.getParam(0);
				juminLength= sqlresultParam.getInt("ETC1");
				
			   int li_extDataSize =  excelList.rowSize();
			   int basicLine = 500; //list 업로드에서 한번에 처리할건수(많은시간 걸리고 세션이 끊어지고 트랜젝션 수가 넘어가는 관계로 500건씩 commit)
			   int arrayCount = li_extDataSize / basicLine ; //회전 수
			   int arrayRow = 0; 
			   int j = 0;
			   
			   //ListParam 에 모든 데이타 넣기
			   ListParam[] extList    = new ListParam[arrayCount + 1];
			   ListParam[] extErrList    = new ListParam[arrayCount + 1];
			   for(arrayRow = 0; arrayRow < arrayCount; arrayRow++){
				   //ErrorLogger.debug("▶▶▶▶▶▶▶[for:arrayCount]▶▶▶▶▶▶▶"+arrayRow);
	               extList[arrayRow] = new ListParam(new String[]{"ANL_TYPE", key_ANL_ID, key_col
							            		   , "col_1", "col_2", "col_3", "col_4", "col_5", "col_6", "col_7"});
	               extErrList[arrayRow] = new ListParam(new String[]{"ANL_TYPE", "CUST_ANL_ID", key_col
	            		   , "col_1", "col_2", "col_3", "col_4", "col_5", "col_6", "col_7","error_cause"});
					ErrorLogger.debug("▶▶▶▶▶▶▶[extList111("+j+")]▶▶▶▶▶▶▶"+extList[arrayRow]);
	                for (j= arrayRow * basicLine; j < basicLine * (arrayRow + 1); j++) {
	                	 currParam = excelList.getParam(j);
	                	 currParam.addValue("ANL_TYPE", param.getString("ANL_TYPE", "01"));
	                	 currParam.addValue(key_ANL_ID, ANL_GUBUN+iDateTime);
	                	 if(!("").equals(currParam.getValue("REGIST_NO",""))){//주민번호
	                		 String checkData = checkData(currParam.getString("REGIST_NO", ""),extList);
	   				      if( "pass".equals(checkData)){
	   				    	 //주민번호 암호화처리	   				    	 
	   				    	 //String REGIST_NO_enc = SafeDBUtil.sdbEncrypt("ENC_REG_NO", currParam.getString("REGIST_NO"));
	   				    	 String REGIST_NO_enc = currParam.getString("REGIST_NO");
	   				    	 currParam.addValue("REGIST_NO", REGIST_NO_enc);
	   				    	 extList[arrayRow].addParam(currParam);
	   				      }else{
	   				    	  currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
	   				    	  extErrList[arrayRow].addParam(currParam);
	   				      }
	                	 }else if(!("").equals(currParam.getValue("CONTACT_ID",""))){//콜 id
	                		  String checkData = checkData(currParam.getString("CONTACT_ID", ""),extList);
							  if( "pass".equals(checkData)){
								 extList[arrayRow].addParam(currParam);
							  }else{
								  currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
								  extErrList[arrayRow].addParam(currParam);
							  }
	                	 }else if(!("").equals(currParam.getValue("A", ""))){ //주민번호,콜 id 필드가 없는경우 필드 A를 주민번호 필드로 본다.
		   			    	  String checkData = checkData(currParam.getString("A", ""),extList);
		   				      if( "pass".equals(checkData)){
		   				    	//주민번호 암호화처리	   				    	 
		   				    	//String REGIST_NO_enc = SafeDBUtil.sdbEncrypt("ENC_REG_NO", currParam.getString("A", ""));
		   				    	String REGIST_NO_enc = currParam.getString("A");
		   				    	currParam.addValue("REGIST_NO", REGIST_NO_enc);
			   				    extList[arrayRow].addParam(currParam);
		   				      }else{
		   				    	currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
		   				    	extErrList[arrayRow].addParam(currParam);
		   				    	
		   				     }
	                	 }
	                    ErrorLogger.debug("▶▶▶▶▶▶▶[extList222("+j+")]▶▶▶▶▶▶▶"+extList[arrayRow]);
	                }
	                ErrorLogger.debug("▶▶▶▶▶▶▶[extList333("+arrayRow+")]▶▶▶▶▶▶▶"+extList[arrayRow]);
	            }
			  
	            extList[arrayRow] = new ListParam(new String[]{"ANL_TYPE", key_ANL_ID, key_col
							         		   , "col_1", "col_2", "col_3", "col_4", "col_5", "col_6", "col_7"});
	          
	            extErrList[arrayRow] = new ListParam(new String[]{"ANL_TYPE", key_ANL_ID, key_col
	            		   , "col_1", "col_2", "col_3", "col_4", "col_5", "col_6", "col_7","error_cause"});
	            
	            for (j= arrayRow * basicLine; j < li_extDataSize; j++) {
	            	 currParam = excelList.getParam(j);
	            	 currParam.addValue("ANL_TYPE", param.getString("ANL_TYPE", "01"));
                	 currParam.addValue(key_ANL_ID, ANL_GUBUN+iDateTime);
                	 if(!("").equals(currParam.getValue("REGIST_NO", ""))){
                		  String checkData = checkData(currParam.getString("REGIST_NO", ""),extList);
	   				      if( "pass".equals(checkData)){
	   				    	//주민번호 암호화처리	   				    	 
	   				    	//String REGIST_NO_enc = SafeDBUtil.sdbEncrypt("ENC_REG_NO", currParam.getString("REGIST_NO",""));
	   				    	String REGIST_NO_enc = currParam.getString("REGIST_NO");
	   				    	currParam.addValue("REGIST_NO", REGIST_NO_enc);
		   				    extList[arrayRow].addParam(currParam);
	   				      }else{
	   				    	currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
	   				    	extErrList[arrayRow].addParam(currParam);
	   				    	
	   				     }
                	 }else if(!("").equals(currParam.getValue("CONTACT_ID", ""))){
                		  String checkData = checkData2(currParam.getString("CONTACT_ID", ""),extList);
	   				      if( "pass".equals(checkData)){
	   				    	extList[arrayRow].addParam(currParam);
	   				      }else{
	   				    	currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
	   				    	extErrList[arrayRow].addParam(currParam);
	   				    	
	   				     }
                	 }else if(!("").equals(currParam.getValue("A", ""))){ //주민번호 필드가 없는경우 필드 A를 주민번호 필드로 본다.
	   			    	  String checkData = checkData(currParam.getString("A", ""),extList);
	   				      if( "pass".equals(checkData)){
	   				    	//주민번호 암호화처리	   				    	 
	   				    	//String REGIST_NO_enc = SafeDBUtil.sdbEncrypt("ENC_REG_NO", currParam.getString("A", ""));
	   				    	String REGIST_NO_enc = currParam.getString("A", "");
	   				    	currParam.addValue("REGIST_NO", REGIST_NO_enc);
		   				    extList[arrayRow].addParam(currParam);
	   				      }else{
	   				    	currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
	   				    	extErrList[arrayRow].addParam(currParam);
	   				    	
	   				     }
                	 }
	            	  ErrorLogger.debug("▶▶▶▶▶▶▶[extList444("+j+")]▶▶▶▶▶▶▶"+extList[arrayRow]);
	            	  ErrorLogger.debug("▶▶▶▶▶▶▶[extErrList555("+j+")]▶▶▶▶▶▶▶"+extErrList[arrayRow]);
	            }
	            
	            int sqlrow = 0;        
	            int arrayTotalCount = arrayCount + 1; //전체 회전수
	            String sqlName = "";
	            if(param.getString("ANL_TYPE", "01").equals("01")){
	            	sqlName = sql_GUBUN + "ins";
	            }else{
	            	sqlName = sql_GUBUN + "list.ins";
	            }
	            try{
	            	  for(sqlrow = 0 ; sqlrow < arrayTotalCount ; sqlrow++) {
	  	            	sqlparam2.setSqlName(sqlName);
	  	            	sqlparam2.addValue(sqlName, extList[sqlrow]); //
	  	            	sqlparam2.addValue("DS_CONTACTLIST", extList[sqlrow]);
	  	              ErrorLogger.debug("▶▶▶▶▶▶▶[extErrList.sqlrow("+sqlrow+")]▶▶▶▶▶▶▶"+extList[sqlrow]);
	  	            	BizDelegate.getInstance().execute("sqlService",common,sqlparam2);
	  	            	System.gc();
	  	            }  
	            	  /* 누락건 입력안함.
	            	  for(sqlrow = 0 ; sqlrow < extErrList.length ; sqlrow++) {
	            	  		ErrorLogger.debug("▶▶▶▶▶▶▶extErrList저장");
		  	            	sqlparam3.setSqlName("list.dataupload.error.ins");
		  	            	sqlparam3.addValue("list.dataupload.error.ins", extErrList[sqlrow]); 
		  	            	BizDelegate.getInstance().execute("sqlService",common,sqlparam3);
		  	            	System.gc();
		  	            }
		  	            */  
	            }catch(Exception e){
	            	 ErrorLogger.debug("▶▶▶▶▶▶▶[SqlError]▶▶▶▶▶▶▶"+e);
	            }
			/*
			   for (int i = 0; i < excelList.rowSize(); i++) {
				currParam = excelList.getParam(i);
				currParam.addValue("UPLOAD_ID", "U"+iDateTime);
			      sqlparam.clear();
			      if(!("").equals(currParam.getValue("CUST_ID"))){
			    	  String checkData = "";
				      checkData = checkData(currParam);
				      if( checkData == "pass"){
					    	//데이터 업로드 정상 insert
							sqlparam.setSqlName("list.dataupload.ins");
							sqlparam.addParam(currParam);
							sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				      }else{
				    	 //데이터 업로드 비정상 insert
				    	  ErrorLogger.debug("▶▶▶▶▶▶▶[누락사유코드]▶▶▶▶▶▶▶"+checkData);
				    	  sqlparam.setSqlName("list.dataupload.error.ins");
				    	  currParam.addValue("ERROR_CAUSE", checkData); //누락사유코드 
				    	  sqlparam.addParam(currParam);
				    	  sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
				      }
			      }else{
						ErrorLogger.debug("▶▶▶▶▶▶▶[null인 값]▶▶▶▶▶▶▶"+i);
						//throw new WebActionException("누락된 고객KEY["+i+"] 값을 확인하세요.");
					}
				
				}
			*/
//			 데이터 업로드 목록 update(업로드실행결과 수정 Y:완료, N:실행중)
			sqlparam.clear();
			sqlparam.setSqlName(sql_GUBUN + "catalog.upd");
			param.addValue(key_ANL_ID, ANL_GUBUN+iDateTime);
			param.addValue("EXEC_RESULT", "Y");
			sqlparam.addParam(param);
			sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			errorResult=true;
	    
	    }catch(Exception e){
	    	sqlparam.clear();
			sqlparam.setSqlName(sql_GUBUN + "catalog.upd");
			param.addValue(key_ANL_ID, ANL_GUBUN+iDateTime);
			param.addValue("EXEC_RESULT", "");
			sqlparam.addParam(param);
			try {
				sqlresult = SQLServiceManager.getInstance().execute(sqlparam);
			} catch (SQLServiceException e1) {
				e1.printStackTrace();
			}
		
	    	ErrorLogger.debug("▶▶▶▶▶▶▶[Thread_ERROR]▶▶▶▶▶▶▶"+e);
	    	errorResult=false;
	    }
    	
    	ErrorLogger.debug("▶▶▶▶▶▶[errorResult]▶"+errorResult);
    	String s_CUST_ANL_ID = "";
    	if(errorResult){
    		s_CUST_ANL_ID = ANL_GUBUN+iDateTime;
    	}
    	return s_CUST_ANL_ID;
    	
    }

	//public String checkData(Param currParam, ListParam[] extList){
	public String checkData(String ls_Key, ListParam[] extList){	
		//ErrorLogger.debug("▶▶▶▶▶▶[checkData]▶"+currParam.getString("REGIST_NO"));
		ErrorLogger.debug("▶▶▶▶▶▶[checkData]▶"+ls_Key);
        if(checkKey(ls_Key) == false ) return "01"; //고객키 오류 (누락사유코드: EXC002)
        //if(socialCheck(ls_Key) == false ) return "02"; //고객키 오류 (누락사유코드: EXC002)        
        if(isRightSocialSecurityNumber(ls_Key) == false ) return "02"; //고객키 오류 (누락사유코드: EXC002)
        
        if(checkDuplication(ls_Key,extList)   == false)   return "04"; //고객키 리스트상에서 중복 (누락사유코드: EXC002)
        return "pass";
	}
	
	public String checkData2(String ls_Key, ListParam[] extList){
		ErrorLogger.debug("▶▶▶▶▶▶[checkData2]▶"+ls_Key);
        if(checkDuplication(ls_Key,extList)   == false)   return "04"; //고객키 리스트상에서 중복 (누락사유코드: EXC002)
    	return "pass";
	}
	
/*===================================================
	전화번호체크 (전화번호길이가 7에서 11사이만 허용.) 아님 ""
=====================================================*/

	public boolean changePhoneOk(String data){
		ErrorLogger.debug("▶▶▶▶▶▶▶[changePhoneOk]Start▶▶▶▶▶▶▶"+data);
        if (data == null) return false;
		if(data.length() >= 7 && data.length() <= 11){
			return true;
		}
		else {
			return false;
		}
	}
/*===================================================
	주민번호길이 체크
=====================================================*/

	public boolean checkKey(String data){
		//ErrorLogger.debug("▶▶▶▶▶▶▶[checkKey]Start▶▶▶▶▶▶▶"+data.length());
        //if (data == null) return false;
		//if(data.length()>=juminLength){
        if(data.length()==juminLength){
			return true;
		}else {
			return false;
		}
	}
/*===================================================
	주민번호 체크(제대로 있는 주민번호 벡터에 중복되는값이 있나 체크.)
=====================================================*/
	public boolean checkDuplication(String data, ListParam[] extList){
	  for(int i =0;i<extList.length; i++){
		  if(extList[i] != null && extList[i].findRow(key_col, data) != -1){  
		  	  return false;			  
		  }
	  }
		  
      return true;
	}
	
/*===================================================
	주민등록번호 체크(주민등록번호 체계 규칙) 오류나는데 ....???
=====================================================*/
    public boolean socialCheck(String as_socialId) {

        try{
            //if (as_socialId.length() != 13) return false;   //13자리가 안될때.

            //String jumin1 = as_socialId.substring(0,7); //76
            //String jumin2 = as_socialId.substring(7); //76
            int i, j;
            long ssntot = 0, ssnave = 0;

            int[] a = new int[6];
            int[] b = new int[7];
            for(i = 0; i<6; i++){
                a[i] = Integer.parseInt(as_socialId.charAt(i) + "");
            }
            for(i = 0; i<7; i++) {
                b[i] = Integer.parseInt(as_socialId.charAt(i + 6) + "");
            }
            if( !(b[0] >= 1 && b[0] <= 4 )) return false;

            ssntot = (a[0]*2)+(a[1]*3)+(a[2]*4)+(a[3]*5)+(a[4]*6)+(a[5]*7) + (b[0]*8)+(b[1]*9)+(b[2]*2)+(b[3]*3)+(b[4]*4)+(b[5]*5);

            ssnave = 11 - (ssntot%11);

            if (ssnave == 11) ssnave = 1;
            else if(ssnave == 10) ssnave = 0;

            if (b[6] == ssnave) return true;
            else return false;
        }catch(Exception e){
        //    e.getMessage();
            return false;
        }
    }
    
    public boolean isRightSocialSecurityNumber(String socialSecurityNumber) {

    	//모든 문자가 수자로 이루어졌는지 체크한다.
    		Pattern p = Pattern.compile("\\d{13}");
    		if (!p.matcher(socialSecurityNumber).matches())
    		return false;


    		int total = 0;
    		int[] ssns = transNumberStringToIntArray(socialSecurityNumber);
    		int[] ns = { 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 };

    		for (int i = 0; i < ns.length; i++) {
    			total += ssns[i] * ns[i];
    		}

    		if (ssns[ssns.length -1]  == ((11 - total % 11) % 10))
    			return true;
    		return false;
    	}
    
    	private int[] transNumberStringToIntArray(String numString){
    		int[] results = new int[numString.length()];
    		for (int i = 0; i < results.length; i++) {
    			results[i] = numString.charAt(i) - '0';
    		}
    		return results;
    	}


}

