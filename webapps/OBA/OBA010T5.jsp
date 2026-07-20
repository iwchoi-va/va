<%@page import="com.locus.jedi.transfer.ListParam"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>QC 평가표</title>
<script type='text/javascript' src='../chart/lib/jquery-1.11.1.js'></script>
<link rel="icon" href="../msens_icon.ico" />
<link rel="stylesheet" type="text/css" href="../css/oba010t5.css">
<script src='../js/scriptutil.js' type="text/javascript" charset="UTF-8"></script>
<script src='../js/oba010p5_util.js' type="text/javascript" charset="UTF-8"></script>
<script src="../js/jqgrid/js/trirand/i18n/grid.locale-kr.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/trirand/src/jquery.jqGrid.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jszip.min.js" type="text/javascript"></script>
<!-- <script src="../js/grid/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script> --> 
<script src="../js/ageutil.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/css/trirand/ui.jqgrid.css" />

<script type="text/javascript">
	/****
	** ※ 으로 되어있는게 확인/특이사항들 표시해 놓은것!! 
	******/
	
	/*
	* ※ 케이스 별로 테스트 해보기!
	*/

	var tm_no = ""; //TM_NO : TM설계번호
	var ent_dgn_no = ""; // con_ent_dgn_no : 설계번호
	var app_date=""; // CON_APP_DATE : 청약일자
	var prod_cd = ""; // CON_I_KIND_CD : 상품코드
	var view_gb = ""; // 평가표 화면에서는 조회용이 아니므로 view값을 공백으로 넘겨주면됨
	var qacd = "01"; //default : 01
	var user_id = ""; // 현재 접속 중인 user 정보
	
	var clse_yn = ""; // 마감일 여부(청약일자 기준 다음달 13일)
	var mod_yn = ""; // 당월 여부
	
	var host = ""; // host 경로
	
	var open_userId = ""; // 현재 화면을 열고 있는 user id
	var est_item_ver = ""; // 평가표버전
	
	var poa_yn = ""; // 사망담보 존재 여부 flag
	var psn_yn = "" // 계약자&피보험자 동일 여부
	var chk_insur_age = "N"; // 보험나이 체크 flag
	
	var save_gb     = "";	//저장구분(INSERT2 : 최초심사시, INSERT : 보완차수 증가시 , UPDATE : 수정시)
	var save_gb_sub = ""; 
	var btn_save_yn = false; // 저장 버튼 활성화 여부
	var bIsMod = true;
	
	var now_date = ""; // 오늘일자
	
	var opi_max_len = 4000; // tsr 의견, uw 의견 바이트 제한수
	
	//DATASET들
	var DS_CMP025 = []; // 증권번호별 심사결과 코드값
	var DS_SYS130 = []; // 평가 점수 계산을 위해 필요한 항목
	var DS_EST201 = []; // 심사항목별 심사결과 코드값
	
	var DS_WORKDATE = []; // 영업일 여부 조회
	
	var DS_DMBO_STATUS = []; // 사망담보존재여부 조회 데이터
	var DS_QA_STATUS = []; // 심사상태 조회데이터
	var DS_VIEW_STATUS = []; // 화면 열고 있는지 조회 데이터
	
	var DS_POA_VALUE = []; // 사망담보 존재 여부
	var DS_QC_OPINION = []; // 심사결과 이력 조회 데이터
	var DS_EST_CODE = []; //평가항목 코드 리스트
	var DS_EST_LIST = []; // 평가항목 리스트
	var DS_EST_LIST_COPY = []; // 평가항목 리스트_COPY

	/* 결과 저장 테이블들 */	
	var DS_EST_RES_HIST = [] ; // TB_PRE_EST_RESULT_HIST(심사결과이력데이터) 
	var DS_EST_HIST = []; // TB_PRE_EST_HISTORY(심사이력)
	var DS_EST_RES_HIST_LOG = [] ;// TB_PRE_EST_RESULT_HIST_LOG(심사결과이력로그)
	var DS_EST_HIST_LOG = []; // TB_PRE_EST_HISTORY_LOG(심사이력로그)
	var DS_INSU_PLAN_MAST = []; // TB_INSU_PLAN_MAST(가입설계메인테이블)
	var DS_EST_LIST_IN = []; // 변경이력 저장하는 데이터셋
	
	var DS_LTTMINFR0124 = []; // 벤더사 출력정보 저장 데이터
	var DS_USER_INFO = []; // 기본 유저 정보 저장
	var DS_RDO_STATE = []; //radio button state
	
	var DS_EST_LIST_EXCEL = []; // 평가표 엑셀 다운로드 용
	
	function page_load(){
		// ※ 현재 조회 기능만 사용하게 될거라서 저장버튼을 안보이로도록 처리하고, 상태값 변경 불가능하게 처리함
		$("#btn_save").css("visibility", "hidden");
		$("input[name=rdo_state]").attr("disabled", true);
			
		var query = location.search.substring(1);
		
		var parameters = {};
		var keyValues = query.split(/&/);
		for (var prop in keyValues) {
		    var keyValuePairs = keyValues[prop].split(/=/);
		    var key = keyValuePairs[0];
		    var value = keyValuePairs[1];
		    parameters[key] = value;
		}
		
		ent_dgn_no = parameters['ENTDGNNO'];
		user_id = parameters['USERID'];
		
		// ※ 임시 : VA 테스트 계정으로 테스트 시에 오류가 발생하므로 va 테스트시에는 나홍선차장님 계정으로 저장되도록 한다. --> 이거 운영반영시에 빼야됨
		//if(user_id.length < 8) user_id = "23921376";
	
		//※ 임시 : 나중에 삭제해야합니다!!
		//ent_dgn_no = "941906065716";
		
		
		document.getElementById("con_ent_dgn_no").setAttribute("value", ent_dgn_no);
		
		if(window.location.protocol == "http:"){
			if(window.location.hostname.indexOf("127") != -1){
				host = window.location.protocol+"//"+window.location.hostname+":8080/msens/";
			}
		}else{
			host = window.location.protocol+"//"+window.location.hostname+"/msens/"; 
		}
		
		if(window.location.port == "4443"){
			host = window.location.protocol+"//"+window.location.hostname+":"+window.location.port+"/msens/";//->third party
		}
		
		//※ 크롬 테스트용임
		//host = "http://10.51.206.59:8090/MSENS/";
		//host = "http://127.0.0.1:8090/MSENS/";
	
		f_initParameter(); //va에 필요한 파라미터 정보가 다 없어서 aigen db에서 가져와서 세팅하기 위해
	}
	
	/*
	  * [OPEN]시 이벤트 처리
	  STEP1. VA에 필요한 파라미터 정보가 없어서 AGIEN DB에서 조회 하는 함수
	  PARAMETER : 
		  ENT_DGN_NO : 설계번호
		  USER_ID : 해당 화면을 연 상담사 ID
	*/
	function f_initParameter(){
		
		var xhr = new XMLHttpRequest();
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do" , true);
		var param = "cmd=initParam&ent_dgn_no="+ent_dgn_no+"&user_id="+user_id;
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		
		xhr.onreadystatechange = function() {
	        if(xhr.readyState == 4) {
	        	if(xhr.status == 200) {
	        		f_callback("f_initParameter", xhr);
	        	}
	        }
	    };
	    xhr.send(param);
	}
		
		
	/*
		* [OPEN]시 이벤트 처리
		STEP2. 필요한 코드 정보들을 조회해 오는 함수 
		PARAMETER : 
			INITCODE : 초기화 해야하는 구분 값 
				- INIT : 초기정보 조회
				- BTN_INIT : QC 평가이력 정보 조회전 초기화
				- SAVE : 저장 전 기본 정보 초기화
	*/
	function f_init(initCode){	
		switch(initCode){
			case "init" :
				now_date = g_toDate();
				
				f_getTMCodes("DS_CMP025","CMP025"); // 증권번호별 심사결과 코드값을 불러온다.
				f_getTMCodes("DS_SYS130","SYS130"); // 평가점수 계산을 위한 항목
				
				f_getWorkDate(app_date, "13");
				
				f_SubPopOnLoadCompleted();

				if(view_gb == "view"){
					document.getElementById("btn_save").style.visibility = "hidden";
				}
	
				break;
			case "btn_init" : 
				
				DS_EST_HIST = []; // TB_EST_HISTORY DataSet 초기화
			
				break;
			case "save" :
				// 저장전에 데이터 값 초기화
				
				DS_EST_RES_HIST = [] ; // TB_PRE_EST_RESULT_HIST(심사결과이력데이터) 
				DS_EST_HIST = []; // TB_PRE_EST_HISTORY(심사이력)
				DS_EST_RES_HIST_LOG = [] ;// TB_PRE_EST_RESULT_HIST_LOG(심사결과이력로그)
				DS_EST_HIST_LOG = []; // TB_PRE_EST_HISTORY_LOG(심사이력로그)
				DS_INSU_PLAN_MAST = [{
	 				"CON_ENT_DGN_NO" : "",
	 				"TM_PROCESSING_GB" : "",
	 				"TM_UPDATE_PRE_JUDGE_DATE" : "",
	 				"TM_UPDATE_PRE_QA" : "",
	 				"CON_CONT_STATE" : "",
	 				"CON_P_PSN_NO" : "",
	 				"CON_APP_DATE" : "",
	 				"TREATYCD" : "",
	 				"CON_ETC_FIELD_3" : "",
	 				"CON_ISTAR_CONT_DATE" : "",
	 				"CON_IEND_CONT_DATE" : "",
	 				"ESTRESULTCD" : ""
	 			}]; // TB_INSU_PLAN_MAST(가입설계메인테이블)
				DS_EST_LIST_IN = []; // 변경이력 저장하는 데이터셋

				break;
				
		}
	}
	
	/*
		TM의 CODEBOOK을 조회하는 함수
	*/
	function f_getTMCodes(dataset,codeType){
		var xhr = new XMLHttpRequest();
		var ds_tm_code = "";
		
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do" , true);
		var param = "cmd=getCodes&code_type="+codeType;
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		//xhr.setRequestHeader("Content-Type", "application/json");
		
		xhr.onreadystatechange = function() {			
	        if(xhr.readyState == 4) {
	        	if(xhr.status == 200) {	
					var responseText = JSON.parse(xhr.responseText);
					ds_tm_code = JSON.parse(responseText.DS_TM_CODE);
	    	        eval(dataset + "= ds_tm_code");
	
	    	        if(dataset == "DS_CMP025"){
	    	        	DS_CMP025.push({
	    	        		CODENAME : "계좌변경",
	    	        		ETCCODE1 : "",
	    	        		ETCCODE2 : "",
	    	        		CODETYPE : "CMP025",
	    	        		CODE : "67"
	    	        	});
	    	        	
	    	        	DS_CMP025.push({
	    	        		CODENAME : "선택없음",
	    	        		ETCCODE1 : "",
	    	        		ETCCODE2 : "",
	    	        		CODETYPE : "CMP025",
	    	        		CODE : ""
	    	        	});
	    	        }
		       	}        	
	        }
	    };
	    xhr.send(param);
	}
	
	/*
	 * [OPEN]시 이벤트 처리
	 * 청약일자 기준으로 다음달 13일이 마감일인데, 마감일자인지의 여부와 
	   청약된 일자가 당월인지 체크하는 함수
	 */
	function f_getWorkDate(p_app_date, p_clse_day){ 
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do" , true);
	
		var param = "cmd=getWorkDate&app_date="+p_app_date+"&clse_day="+p_clse_day;
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		
		xhr.onreadystatechange = function() {			
	        if(xhr.readyState == 4) {
	        	if(xhr.status == 200) {
	        		f_callback("f_getWorkDate", xhr);
	        		
	        	}        	
	        }
	    };
	    xhr.send(param);	
		
	}
	
	/**
	 * [OPEN]시 이벤트 처리
	 * STEP3 . 페이지가 오픈 되면서 처리되는 함수
	 * @param :
	 * @return :
	 */
	function f_SubPopOnLoadCompleted(){
		if(!g_isEmpty(ent_dgn_no)){
			//f_init("load"); // 필요없어서 제거함
			
			// view_gb = 'view'인 경우는 VA에 없기 때문에 신경 쓸 필요 X
			if(view_gb == "view"){
				f_getPoaStatusQm();		//사망담보존재여부 조회
				f_getEstStatusQm();		//QA통판 심사상태 조회
			}else{
				//f_getViewStatus("init"); // ※ 원래버전
				f_getViewStatus("srh"); // ※ 동시 접속 가능하게 처리하면서 바로 정보 조회되도록 처리함
			}
			
		}else{
			alert("가입설계번호가 없습니다.");
		}
	}
	 
	 /**
	  * [OPEN]시 이벤트 처리
	  * STEP4. 다른 QC가 평가표를 열고 있는지 확인(view_id = 'INIT')
	  * @param : CON_ENT_DGN_NO (설계번호)
	  * @return : 화면오픈여부 조회
	  */
	 function f_getViewStatus(view_id){
		  var xhr = new XMLHttpRequest();
			
			xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
			var param = "cmd=ViewStatus&ent_dgn_no="+ent_dgn_no;
			
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			
			xhr.onreadystatechange = function() {
		        if(xhr.readyState == 4) {
		        	if(xhr.status == 200) {
		        		
		        		if(view_id == "init") f_callback("f_getViewStatus_init", xhr);
		        		else if(view_id == "srh") f_callback("f_getViewStatus_srh", xhr);
		        		
		        	}        	
		        }
		    };
		    xhr.send(param);	
	 }
			
	 /**
	  * [OPEN]시 이벤트 처리
	  * STEP5. 다른 QC에 현재 설계에 대해 평가를 하고 있지 않다면, 다른 QC가 평가표를 열지 못하도록 막기
	  * @param : CON_ENT_DGN_NO : 설계번호
	  			 USER_ID : 평가표를 연 USER ID
	  			 TYPE : OPEN / CLOSE
	  * @return : 화면오픈 업데이트
	  */
	  function f_updateViewStatus(type){
			 var xhr = new XMLHttpRequest();
			 xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
			 var param = "cmd=UpdViewStatus&ent_dgn_no="+ent_dgn_no+"&user_id="+user_id+"&type="+type;
				
			 xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
				
			 xhr.onreadystatechange = function() {			
			        if(xhr.readyState == 4) {
			        	if(xhr.status == 200) {
			        		if(type != "close") f_getViewStatus("srh");
			        	}        	
			        }
			 };
			  
			xhr.send(param);	
	 }
	
	 
	 /**
	  * [OPEN]시 이벤트 처리
	  * STEP6. 해당 설계에서 사망담보가 있는 체크하기 전에 설계된 담보 조회
	  * @param : TM_NO
	  * @return : 사망담보존재여부 조회
	  */
	 function f_getPoaStatusQm(){
		  var xhr = new XMLHttpRequest();
			
			xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
			
			var param = "cmd=DmboStatus&tm_no="+tm_no;
			
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			 
			xhr.onreadystatechange = function() {			
		        if(xhr.readyState == 4) {
		        	if(xhr.status == 200) {
		        		f_callback("f_getPoaStatusQm", xhr);
		        		
		        	}        	
		        }
		    };
		    xhr.send(param);	
	 }
	 
	  /**
	   * [OPEN]시 이벤트 처리
	   * STEP7. 설계된 담보가 사망담보 속성이 있는게 있는 조회 --> CORE 인터페이스 호출
	   * @param : SERVER_GB : 현재 접속중인 서버
	   			  ATTR_TYPE : DEFAULT : Z0207
	   			  PROD_CD : 상품코드
	   			  DMBO_CD : 담보코드
	   * @return : 사망담보 속성값 인터페이스
	   */
	  function f_getPoaValueCd(){ 
		  var xhr = new XMLHttpRequest();
		
			xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
			
			var hostname = window.location.hostname.split(".");
			var server_gb = hostname[0];
			
			if(server_gb == "127") server_gb = "vauat";
			
			//※ 로컬 버전
			//server_gb = "vauat";
			
			var dmbo_cd = "";
			for(var i= 0; i< DS_DMBO_STATUS.length;i++){
				dmbo_cd += DS_DMBO_STATUS[i].DMBO_CD + ",";
			}
			dmbo_cd = dmbo_cd.slice(0,-1);
			
			var param = "cmd=PoaInterface&service_name=LTTMINFR0128&server_gb="+server_gb+"&attr_type=Z0207&prod_cd="+prod_cd+"&dmbo_cd="+dmbo_cd;
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			
			xhr.onreadystatechange = function() {			
		        if(xhr.readyState == 4) {
		        	if(xhr.status == 200) {
		        		f_callback("f_getPoaValueCd", xhr);
		        			        		
		        	}        	
		        }
		    };
		    
		    
		    xhr.send(param);	
	 }
	  
	  
	  /**
	   * [OPEN]시 이벤트 처리
	   * STEP8. QC 평가표의 심사 상태를 조회
	   * @param : CON_ENT_DGN_NO : 설계번호
	   			  QACD : 심사유형코드 (DEFAULT : 01)
	   * @return : 심사상태 조회
	   */
	  function f_getEstStatusQm(){
		
		   var xhr = new XMLHttpRequest();
			
			xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
			var param = "cmd=QAStatus&ent_dgn_no="+ent_dgn_no+"&qacd="+qacd;
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			
			xhr.onreadystatechange = function() {			
		        if(xhr.readyState == 4) {
		        	if(xhr.status == 200) {
		        		f_callback("f_getEstStatusQm", xhr);
		        		
		        		
		        	}        	
		        }
		    };
		    xhr.send(param);	
	  }
		   
	/**
	     * [OPEN]시 이벤트 처리
		 * STEP9. QC 심사결과 이력 조회 함수
		 * @param : CON_ENT_DGN_NO : 설계번호
		 			CUR_QACD : 최종 심사단계 
		 			CUR_HTID : 최종 HTID
		 * @return : 심사결과 이력 조회
	*/
	
	function f_opnionDetailQm(cur_qacd, cur_htid){ 
		
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
					
		var param = "cmd=opinionbyqc&ent_dgn_no="+ent_dgn_no+"&qacd="+cur_qacd+"&htid="+cur_htid;
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
					
		xhr.onreadystatechange = function() {			
			if(xhr.readyState == 4) {
				   if(xhr.status == 200) {
					   f_callback("f_opnionDetailQm", xhr);
			 	}
			}
		};
		
		xhr.send(param);	
		
	}
	
	  
	/**
	 * [OPEN]시 이벤트 처리
	 * [OPEN]시 이벤트 처리
	 * STEP 11. QC 평가표 리스트 조회(실질적으로 VERSION 1을 쓰지 않기 때문에 ELSE 문만 주로 보면된다)
	 * @param : SQL - 버전에 따라 다른 쿼리 전송(VA에서는 최신 버전만 쓰기때문에 msens.est.hansol.getEvalListNew.sel 쿼리만 탐 )
	  			CON_ENT_DGN_NO : 설계번호
	  			CUR_QACD : 최종 심사 상태
	  			CUR_HTID : 최종 HTID
	 * @return : 평가항목 조회
	 */
	function f_getEstItemList(cur_qacd, cur_htid){
		 if(DS_QC_OPINION.length == 0) est_item_ver = "";
		 else est_item_ver = DS_QC_OPINION[0].EST_ITEM_VER;
		 
		 var sqlName = "";

		 if(g_isEmpty(est_item_ver)) {
			 if(now_date < "20160801"){
				// 심사항목별 심사결과 코드값을 불러온다.
				f_getTMCodes("DS_EST201","EST201"); // 증권번호별 심사결과 코드값을 불러온다.
						
				sqlName = "msens.est.hansol.getEvalList.sel";
					
			 }else{			
				// 심사항목별 심사결과 코드값을 불러온다.
				 f_detail_combo_list();
				
				 sqlName = "msens.est.hansol.getEvalListNew.sel";
				
			 }
		 }else{
			 if(est_item_ver == 1){
					
				// 심사항목별 심사결과 코드값을 불러온다.
				f_getTMCodes("DS_EST201","EST201"); // 증권번호별 심사결과 코드값을 불러온다.
				
				sqlName = "msens.est.hansol.getEvalList.sel"; // 해당 설계번호 당시의 평가표가 1인 경우에만 해당 쿼리를 타도록 --> 버전1만 형태가 조금 다름
					
			 }else{
					//grd_itemResult.visible = false;
					//grd_itemResultNew.visible = true;
					
					// 심사항목별 심사결과 코드값을 불러온다.
					f_detail_combo_list();
						
					sqlName = "msens.est.hansol.getEvalListNew.sel";
			 }
			 
		 }
		 
		 var xhr = new XMLHttpRequest();
			
		 xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
								
		 var param = "cmd=getEstList&sql="+sqlName+"&ent_dgn_no="+ent_dgn_no+"&qacd="+cur_qacd+"&htid="+cur_htid;
		 xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
								
		 xhr.onreadystatechange = function() {			
			if(xhr.readyState == 4) {
				if(xhr.status == 200) {
					f_callback("f_getEstItemList", xhr);
				}
			}
		 };
					
		xhr.send(param);	
	 
	}
	 
	 /**
		 * [OPEN]시 이벤트 처리
		 * STEP12, 심사항목별 심사결과 코드값 조회--> 평가항목을 매핑하는데 필요한 정보들
		 * @param : TM_CD
		 * @return : 심사항목별 심사결과 코드값 조회
	 */
		 
	function f_detail_combo_list(){
		var xhr = new XMLHttpRequest();
				
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
							
		var param = "cmd=getEvalCode&code_depth=3";
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
							
		xhr.onreadystatechange = function() {			
			if(xhr.readyState == 4) {
				if(xhr.status == 200) {
					f_callback("f_detail_combo_list", xhr);
				}
			}
		};
				
		xhr.send(param);	
				
	}
	
	/**
	 * STEP10. 심사자 의견 상세 조회
	 * 좌측의 QC평가이력에서 선택한 ROW에 따라 QC-TSR의견, QC-UW의견, 청약진행 상태 값을 변경해준다
	 * @return :
	*/	 
	function f_opnionDetailInfoQm(nRow){
		// 심사자 의견 관련 초기화
		f_init("btn_init"); 
		
		var tsr_opinion = DS_QC_OPINION[nRow].ESTOPINION;
		var qc_opinion = DS_QC_OPINION[nRow].QAOPINION;
		
		
		document.getElementById("tsr_opinion").value = (tsr_opinion == null || tsr_opinion == "undefined") ? "" : tsr_opinion;
		
		document.getElementById("qc_opinion").value = (qc_opinion == null || qc_opinion == "undefined") ? "" : qc_opinion;

		if(!g_isEmpty(DS_QC_OPINION[nRow].ESTRESULTCD)) $("input:radio[name=rdo_state]:radio[value="+DS_QC_OPINION[nRow].ESTRESULTCD+"]").prop("checked", true);

	}

	/*
	 * CALLBACK  FUNCTION
	*/
	function f_callback(reqId, xhr){
		var responseText = JSON.parse(xhr.responseText);
		
		if(responseText.RESULT == "fail"){
			alert(responseText.REASON);
			return;
		}
		
		switch(reqId){
			case "f_initParameter" :
	        	var ds_param = JSON.parse(responseText.DS_PARAM_01);				
	        	if(ds_param.length == 0){
	        		alert("가입설계번호가 없습니다.");
	        		return;
	        	}
	        	
	        	/*tm_no = ds_param.record[0][0];
	        	app_date = ds_param.record[0][2];
	        	prod_cd= ds_param.record[0][3];*/
	        	
	        	tm_no = ds_param[0].TM_NO;
	        	app_date = ds_param[0].CON_APP_DATE;
	        	prod_cd = ds_param[0].CON_I_KIND_CD;
	        	
	        	// VA에 없는 USER 정보
	        	DS_USER_INFO = JSON.parse(responseText.DS_PARAM_02); 
	        	
	        	// 청약 진행 상태 코드 정보를 필요로 하는 경우가 있어서 저장해둠
	        	DS_RDO_STATE.push({ "CODE_ID" : "none", "CODE_NM" : "선택없음" });
	        	DS_RDO_STATE.push({ "CODE_ID" : "62", "CODE_NM" : "QC최종통과" });
	        	DS_RDO_STATE.push({ "CODE_ID" : "53", "CODE_NM" : "QC보완" });
	        	DS_RDO_STATE.push({ "CODE_ID" : "65", "CODE_NM" : "QC-UW보완" });
	        	DS_RDO_STATE.push({ "CODE_ID" : "46", "CODE_NM" : "UW심사의뢰" });
	        	
	        	f_init("init"); //페이지 로드 후 초기화
			break;
			case "f_getWorkDate" :
        		DS_WORKDATE = JSON.parse(responseText.DS_WORKDATE); 
        		
        		clse_yn = DS_WORKDATE[0].CLSE_YN;
        		mod_yn = DS_WORKDATE[0].MOD_YN;
        		
				break;
			case "f_getViewStatus_init":
        		DS_VIEW_STATUS = JSON.parse(responseText.DS_VIEW_STATUS); 
        		
        		if(DS_VIEW_STATUS[0].OPEN_YN == "Y" && DS_VIEW_STATUS[0].USERID != user_id ){
				
        			alert("현재 다른 사용자가 열람 중 입니다. \n사용자ID\n→"+ DS_VIEW_STATUS[0].USERID + "_" + DS_VIEW_STATUS[0].USERNM + "("
        				 + DS_VIEW_STATUS[0].USER_TEAM + "_" + DS_VIEW_STATUS[0].USER_LEVEL + ")\n열람개시시간 : " + DS_VIEW_STATUS[0].OPEN_DATE);
        				
					self.close();
					//return;
        				
        		}else{
	        		f_updateViewStatus("open");	//화면오픈 여부 업데이트
	        	}
        		
				break;
			case "f_getViewStatus_srh" :
				open_userId = g_nvl(user_id, "").trim();
    			
    			f_getPoaStatusQm();	//사망담보존재여부 조회
    			f_getEstStatusQm();	//QA통판 심사상태 조회
				
				break;
			case "f_getPoaStatusQm":
				
        		DS_DMBO_STATUS = JSON.parse(responseText.DS_DMBO_STATUS); 
        				        		
        		if(DS_DMBO_STATUS.length > 0 ){
        			f_getPoaValueCd();
        		}else{
        			poa_yn = "N";
        		}
				break;
				
			case "f_getEstStatusQm" :
				
        		DS_QA_STATUS = JSON.parse(responseText.DS_QA_STATUS);
        		
        		//console.log("평가진행현황####");
        		//console.log(DS_QA_STATUS);
        		
        		/* TM 설계상태 
        		 PRE_TM_PROCESSING_GB(TM설계 상태 값)
        		 47/55/53 - 이전에 심사 이력이 있으면 INSERT (보완 차수 증가)
        		 		  - 최초 심사면 INSERT2
        		 48		  - 이전에 심사 이력이 있으면 INSERT (보완 차수 증가)
		 		  		  - 최초 심사면 INSERT2
		 		 그외 	  - UPDATE
		 		 		  - 청약진행 상태 변경 불가능 / QC평가표의 수정만 가능함
        		*/
        		switch(DS_QA_STATUS[0].PRE_TM_PROCESSING_GB){
        			case "47" :
        			case "55" :
        			case "53" :
        				$("input[name=rdo_state]").attr("disabled", false);
        				
        				if(DS_QA_STATUS[0].RESULT_YN == "Y"){
	        				save_gb = "INSERT"; // 보완차수 증가
	        			}else{ 
	        				save_gb = "INSERT2"; // 최초 심사
	        			}
        			break;
        			case "48":
        				if(DS_QA_STATUS[0].RESULT_YN == "Y"){
	        				save_gb = "INSERT";
	        			}else{
	        				save_gb = "INSERT2";
	        			}
        			break;
        			default : 
        				save_gb = "UPDATE";
        				$("input[name=rdo_state]").attr("disabled", true);
        				$("#btn_save").prop("disabled", false);
	        		
        		}
        		
        		//청약일자 기준 다음달 13일 이상이면 마감일을 넘긴 것이므로 수정이 불가능함
        		if(clse_yn == "Y"){	     			
        			$("input[name=rdo_state]").attr("disabled", true);
    				$("#btn_save").prop("disabled", true);
        		
        		// 청약일자가 마감일을 지나지는 않았으나,당월이 아닌 경우에는 청약진행상태만 변경이 불가능함
        		}else if(mod_yn == "N"){  
        			$("input[name=rdo_state]").attr("disabled", true);
        			bIsMod = false;
        			save_gb = "UPDATE";
        		}
        		
        		
        		
        		// 상령일 체크
        		var insur_age = DS_QA_STATUS[0].CON_IP_CONT_AGE // 계약 시 나이
        		var app_inusr_age = f_getInsurAge(2, DS_QA_STATUS[0].CON_APP_DATE, DS_QA_STATUS[0].CON_IP_ISD_CD); 
        		
        		// 계약시 나이와 청약시점의 보험 나이가 달라진 경우 알림을 띄워주고, 보험나이를 체크하는 FLAG를 변경한다.
        		if(insur_age != app_inusr_age){ 
        			alert("보장개시일 기준으로 보험나이가 변경됩니다.\nQC-UW보완으로 처리 진행해주세요.");
        			chk_insur_age = "Y";
        		}
        		
        		 // 최초 심사가 아닌데, 47,48 코드가 아니면 버튼 활성화 안되게
        		if(save_gb == "INSERT2"){
        			if(DS_QA_STATUS[0].PRE_TM_PROCESSING_GB != "47" && DS_QA_STATUS[0].PRE_TM_PROCESSING_GB != "48"){
        				$("#btn_save").prop("disabled", true);
        			}
        		}
        		
        		// 만료된 설계건은 수정만 가능하도록 단, 만료된 설계 중 심사 이력이 없으면 저장/청약 진행 상태 변경이 모두 불가능함
        		if(DS_QA_STATUS[0].EXPIRED_YN == "Y"){	
        			$("#lbl_notice").text("만료된 설계입니다.");
        			$("#lbl_notice").css("visibility", "visible");
        			
        			if(DS_QA_STATUS[0].RESULT_YN == "Y"){
        				save_gb = "UPDATE";
        				$("input[name=rdo_state]").attr("disabled", true); 
        			}else{ 
        				$("input[name=rdo_state]").attr("disabled", true);
        				$("#btn_save").prop("disabled", true);
        			}
        		}
        		
        		// 만료, 삭제여부
        		// 삭제/만료된 설계인데, 심사이력이 있으면 수정만 가능하고, 그렇지 않으면 둘다 불가능
        		if(DS_QA_STATUS[0].DEL_YN == "Y" && DS_QA_STATUS[0].EXPIRED_YN == "Y"){
        			$("#lbl_notice").text("만료, 기삭제된 설계입니다.");
        			$("#lbl_notice").css("visibility", "visible");
        			
        			if(DS_QA_STATUS[0].RESULT_YN == "Y"){
        				save_gb = "UPDATE";
        				$("input[name=rdo_state]").attr("disabled", true);
        			}else{
        				$("input[name=rdo_state]").attr("disabled", true);
        				$("#btn_save").prop("disabled", true);
        			}
        		}else if(DS_QA_STATUS[0].DEL_YN == "Y"){
        			$("#lbl_notice").text("기삭제된 설계입니다.");
        			$("#lbl_notice").css("visibility", "visible");
        			
        			if(DS_QA_STATUS[0].RESULT_YN == "Y"){
        				save_gb = "UPDATE";
        				$("input[name=rdo_state]").attr("disabled", true);
        			}else{
        				$("input[name=rdo_state]").attr("disabled", true);
        				$("#btn_save").prop("disabled", true);
        			}
        		}else if(DS_QA_STATUS[0].EXPIRED_YN == "Y"){
        			alert("만료된 설계입니다.");
        			
        			$("#lbl_notice").text("만료된 설계입니다.");
        			$("#lbl_notice").css("visibility", "visible");
        			
        			if(DS_QA_STATUS[0].RESULT_YN == "Y"){
        				save_gb = "UPDATE";
        				$("input[name=rdo_state]").attr("disabled", true);
        			}else{
        				$("input[name=rdo_state]").attr("disabled", true);
        				$("#btn_save").prop("disabled", true);
        			}
        		}
        		
        		var cur_qacd = (g_isEmpty(DS_QA_STATUS[0].CUR_QACD) == true) ? "" : DS_QA_STATUS[0].CUR_QACD; //최종심사단계
        		var cur_htid = (g_isEmpty(DS_QA_STATUS[0].CUR_HTID) == true) ? "" : DS_QA_STATUS[0].CUR_HTID; // 최종HTID값
        		
        		psn_yn = (g_isEmpty(DS_QA_STATUS[0].PSN_YN) == true) ? "" : DS_QA_STATUS[0].PSN_YN; // 계약자&피보험자 동일 여부
        		
        		f_opnionDetailQm(cur_qacd, cur_htid);		// QC평가이력 조회
        		f_getEstItemList(cur_qacd, cur_htid);		// 항목리스트
        		
        		btn_save_yn = $("#btn_save").attr("disabled") == "disabled" ? false : true;
        		
        		
        		// ※ 현재 조회 기능만 사용하게 될거라서 저장버튼을 안보이로도록 처리하고, 상태값 변경 불가능하게 처리함
        		$("#btn_save").css("visibility", "hidden");
        		$("input[name=rdo_state]").attr("disabled", true);

        		
				break;
			case "f_getPoaValueCd" :
        		DS_POA_VALUE = JSON.parse(responseText.DS_POA_VALUE); 
        		// 사망담보가 있으면 Y, 없으면 N
        		if(DS_POA_VALUE.length > 0){
        			poa_yn = "Y"; 
        		}else{
        			poa_yn = "N";
        		}

				
				break;
			case "f_opnionDetailQm" : 
				DS_QC_OPINION = JSON.parse(responseText.DS_QC_OPINION); 
				
				f_draw_opinionTable(); // QC평가이력 테이블 그리기
				
		        if(DS_QC_OPINION.length > 0)  f_opnionDetailInfoQm(0);  
		        
				break;
			case "f_detail_combo_list" :
				DS_EST201 = JSON.parse(responseText.DS_EST_CODE);  // 버전1이 아닐때만 여기에서 코드 정보를 가져오기때문에 같은 dataset에 넣어야함-
				
				break;
			case "f_getEstItemList" :
				DS_EST_LIST = JSON.parse(responseText.DS_EST_LIST);
				
				//console.log("##평가항목");
				//console.log(DS_EST_LIST);
				
				if(g_isEmpty(est_item_ver)){
					if(now_date < "20160801"){
						if(DS_EST_LIST.length > 0){
							if(DS_EST_LIST[0].CNTYN == "N"){ // 기존에 설계했던 이력이 없는 경우
								for(var i =0; i< DS_EST_LIST.length; i++){
									if(DS_EST_LIST[i].ESTETC == "99"){
										DS_EST_LIST[i].ETCCD = "0";
									}else{
										if(DS_EST_LIST[i].ESTETC != null){
											var arr = DS_EST_LIST[i].ESTETC.split(",");
											DS_EST_LIST[i].ETCCD = arr[0];
										}
									}
								}
							}
						}
					}else{
						// 
						if(DS_EST_LIST.length > 0){
							if(DS_EST_LIST[0].CNTYN == "N"){
								for(var i=0; i< DS_EST_LIST.length; i++){
									if(DS_EST_LIST[i].ESTETC == "RST999"){ // CHECKBOX인 경우
										DS_EST_LIST[i].ETCCD = "0";
									}else{
										if(DS_EST_LIST[i].ESTETC != null){ // COMBOBOX인 경우고, 평가 항목을 지정해주는 부분
											var cd_type = DS_EST_LIST[i].ESTETC;
											var temp = DS_EST201.filter(function(item){
												return item.CODETYPE == cd_type
											});

											if(temp.length > 0) DS_EST_LIST[i].ETCCD = temp[0].CODE;
										}
									}
								}
							}
						}
					}
					
				}else{
					if(est_item_ver == "1"){
						if(DS_EST_LIST.length > 0){
							if(DS_EST_LIST[0].CNTYN == "N"){
								for(var i =0; i< DS_EST_LIST.length; i++){
									if(DS_EST_LIST[i].ESTETC == "99"){
										DS_EST_LIST[i].ETCCD = "0";
									}else{
										if(DS_EST_LIST[i].ESTETC != null){
											var arr = DS_EST_LIST[i].ESTETC.split(",");
											DS_EST_LIST[i].ETCCD = arr[0];
										}
									}
								}
							}
						}
					}else{
						if(DS_EST_LIST.length > 0){
							if(DS_EST_LIST[0].CNTYN == "N"){
								for(var i=0; i<DS_EST_LIST.length; i++ ){
									if(DS_EST_LIST[i].ESTETC == "RST999"){ // CHECKBOX인 경우
										DS_EST_LIST[i].ETCCD = "0";
									}else{
										if(DS_EST_LIST[i].ESTETC != null){ // COMBOBOX인 경우고, 평가 항목을 지정해주는 부분
											var cd_type = DS_EST_LIST[i].ESTETC;
											
											var temp = DS_EST201.filter(function(item){
												return item.CODETYPE == cd_type
											});

											if(temp.length > 0) DS_EST_LIST[i].ETCCD = temp[0].CODE;
										}
										
									}
								}
							}
						}
					}
				}
				
				DS_EST_LIST_COPY = JSON.parse(JSON.stringify(DS_EST_LIST)); // 저장 시 수정 사항 체크시 사용하기 위해 COPY 해둠
				
				f_draw_estTable();
				
				// ※ 테스트를 위해 임시로..
				//$("#btn_save").prop("disabled", false);
				//$("input[name=rdo_state]").attr("disabled", false);
				//save_gb = "INSERT2";
				
				break;
				
			case "btn_save_event" :
				
				if(responseText.RESULT == "success" && responseText.REASON == ""){
					var v_rdo_state = $("input:radio[name=rdo_state]:checked").val();
					
					// 사망동의서 우편발송 I/F 호출완료시.. --> 활동 이력을 추가로 또 저장한다
					if(v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y"){
						var cur_datetime = getNow();
						var con_p_name = DS_QA_STATUS[0].CON_P_NAME;
						var kind_cd = DS_QA_STATUS[0].CON_I_KIND_CD;
						var cust_no = DS_QA_STATUS[0].CUSTNO;
						
						// 활동이력 저장
						f_saveActHist("170", cur_datetime, con_p_name, kind_cd, ent_dgn_no, cust_no);
					}
					
					alert("정상적으로 처리되었습니다.");
				}else{
					alert(responseText.REASON);
				}
				
				f_getEstStatusQm();
				break;
			case "f_saveActHist" :
				if(responseText.RESULT != "success"){
					alert("####활동이력 저장 과정 중에 오류 발생 : " + responseText.REASON);
				}
				break;
			case "btn_excel_down" :
				DS_EST_LIST_EXCEL = JSON.parse(responseText.DS_EST_LIST_EXCEL);
				
				f_draw_est_item_excel();
				
				break;
		}
	}
	
	/*****************************************************************************************
	 * 함  수  명 :  f_saveActHist
	 * 입      력 :  p_app_act   : 활동코드(SYS054)
	     p_prt_cd   : 상품코드
	     p_act_hist  : 활동결과
	     p_con_ip_psn_name : 고객명
	     p_con_ent_dgn_no : 가입설계번호
	     p_cust_no   : 고객ID
	     p_rel_cd   : 관계코드(EST205)
	 * 반      환 :  
	 * 기      능 :  설계 활동이력을 등록처리함.
	 *****************************************************************************************/
	function f_saveActHist(p_app_act, p_act_hist, p_con_ip_psn_name, p_prt_cd, p_con_ent_dgn_no, p_cust_no, p_rel_cd ){
		var str_center_cd = DS_USER_INFO[0].ORG2_CD;
		var str_team_cd = DS_USER_INFO[0].ORG3_CD;
		var str_user_id = DS_USER_INFO[0].USERID;
		var str_user_nm = DS_USER_INFO[0].USERNAME;
		var str_callid = "";
		var str_ucid = "";
		
		// ※ 해당 정보 받은 다음에 추가 구현 필요함 --> TM CTI 연결 정보라 VA에서는 확인이 어려워, 그냥 공백으로 전달함
		/*
			if(gds_calllog.getRowCount() > 0){
			  strCallId = gfn_setNullToString(gds_calllog.getColumn(0, ""CALLID""));
			  strUcId  = gfn_setNullToString(gds_calllog.getColumn(0, ""UCID""));
			 }
		*/
		
		if(g_isEmpty(p_rel_cd)) p_rel_cd = "2";
		
		var xhr = new XMLHttpRequest();
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do" , true);
		var param = "cmd=saveActHist";
		
		param += "&con_ent_dgn_no="+p_con_ent_dgn_no+"&cust_no="+p_cust_no+"&prt_cd="+p_prt_cd+"&con_ip_psn_name="+p_con_ip_psn_name
			  + "&rel_cd="+p_rel_cd+"&app_act="+p_app_act+"&act_hist="+p_act_hist+"&center_cd="+str_center_cd+"&team_cd="+str_team_cd
			  + "&agent_id="+str_user_id+"&agent_nm="+str_user_nm+"&call_id="+str_callid+"&ucid="+str_ucid;
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		
		xhr.onreadystatechange = function() {
	        if(xhr.readyState == 4) {
	        	if(xhr.status == 200) {
	        		f_callback("f_saveActHist", xhr);
	        	}
	        }
	    };
	    xhr.send(param);
	    
		
	}
	
	/*  
	* [OPEN]시 이벤트 처리
	*	STEP 11. 평가표 테이블 그리기
	*/
	 function f_draw_estTable(){

		 var prevCellVal = [{ cellId: undefined, value: undefined },{ cellId: undefined, value: undefined }];
		 var idx = 0;
		 
		 // 같은 데이터의 경우 rowspan 속성을 주기 위해 
		 var attrCell = function (rowId, val, rawObject, cm, rdata) {
			 var result;
			 
			 switch(cm.name){
				 case "ESTSCATNM" :
					 idx = 0;
					 break;
				 case "ESTTCATNM":
					 idx = 1;
					 break;
			 }
 
             if (prevCellVal[idx].value == val) {
                 result = ' style="display: none" rowspanid="' + prevCellVal[idx].cellId + '"';
             }else {
                 var cellId = this.id + '_row_' + rowId + '_' + cm.name;

                 result = ' rowspan="1" id="' + cellId + '"';
                 prevCellVal[idx] = { cellId: cellId, value: val };
             }
             
             return result;
         };
         
         $("#tbl_est_items").jqGrid('setGridParam', {data : DS_EST_LIST}).trigger('reloadGrid');

		 $("#tbl_est_items").jqGrid({
			 datatype : "local",
			 data : DS_EST_LIST,
			 colNames : ['TYPE1', 'TYPE2', 'No.', 'Question', '평가', 'Feedback Comment', '보완 Comment'],
			 colModel : [{name : 'ESTSCATNM', width: 80, align: 'center', cellattr: attrCell}, 
			             {name : "ESTTCATNM", width: 82, align: 'center',cellattr: attrCell},
			             {name : "TOT_NO", align: 'center',width: 30},
			             {name : "QST_CONT", width: 250}, 
			             {name : "ETCGB",  align: 'center', width: 100, formatter : setRowFormatter}, 
			             {name : "QUEST_MEMO", width: 170, formatter : setEditFormatter},
			             {name : "RCV_MEMO", width: 170, formatter : setEditFormatter}],
			 rowNum : 10000,
	 	     height: "100%",
	 	     //cellEdit : true,
	 	     footerrow:true, // footer 생성 가능하도록 --> ※ footer는 필요하면 추가해야됨 --> footer 불필요로 구현 안함
	 	     userDataOnFooter:true, // footer 생성 가능하도록 
			 gridComplete: function () {
			        var grid = this;

			        $('td[rowspan="1"]', grid).each(function () {
			            var spans = $('td[rowspanid="' + this.id + '"]', grid).length + 1;

			            if (spans > 1) {
			                $(this).attr('rowspan', spans);
			            }
			        });
			        
			       
			       var ds_qc_ratio = DS_EST_LIST.filter(function(item){
						return item.ETCGB == "combo" && (item.ETCCD == '4' || item.ETCCD == '5');
			    	   //return item.ETCGB == "combo";
				   });
			       var v_ratio = ds_qc_ratio.length > 0 ? "0%" : "100%";
			    	 
			       $(grid).jqGrid('footerData', 'set', {ESTSCATNM : "Total", 
			    	   									QST_CONT : "<div>QC Pass Ratio</div><div>"+v_ratio+"</div>",
			    	   									ETCGB : "<div>Pass</div><div>0</div>",
			    	   									QUEST_MEMO : "<div>Feedback</div><div>0</div>",
			    	   									RCV_MEMO : "<div>녹취보완</div><div>0</div>" }
			       				 );

				      
				    var $footer = $(grid).closest(".ui-jqgrid-bdiv").next(".ui-jqgrid-sdiv").find(".footrow");
				    var $estscatnm = $footer.find("td[aria-describedby='tbl_est_items_ESTSCATNM']");
				    var $esttcatnm = $footer.find("td[aria-describedby='tbl_est_items_ESTTCATNM']");
				    var $tot_no = $footer.find("td[aria-describedby='tbl_est_items_TOT_NO']");
				    
				    if($estscatnm.attr("colspan") == undefined){
					    var cols_width = $estscatnm.width() + $esttcatnm.outerWidth() + $tot_no.outerWidth();
					    $esttcatnm.css("display", "none");
					    $tot_no.css("display", "none");
					    $estscatnm.attr("colspan",3).width(cols_width);    
					    
					    $("#div_estTable .ui-jqgrid-bdiv").width($("#div_estTable .ui-jqgrid-bdiv").width()+18); // 스크롤 때문에 height 늘임
					    $("#div_estTable .ui-jqgrid-hdiv").width($("#div_estTable .ui-jqgrid-hdiv").width()+1);
					    $("#div_estTable .ui-jqgrid-sdiv").width($("#div_estTable .ui-jqgrid-sdiv").width()+1);
				    }
			        
			    }
		 });
 
	 }


	 /********************* QC 평가표 포맷을 customizing 하는데 필요한 함수들 ****************************************/
	 
     /*
      * 평가영역에서 항목에 따라 combo/text/checkbox 형태로 변경해주는 로직을 추가한 함수
     */
     function setRowFormatter(cellValue, options, rowdata, action){
    	 var result ="";

    	 switch(cellValue){
    	 	case "normal" :
    	 		if(now_date > "20160801"){
    	 			var v_etc_cd = DS_EST_LIST[options.rowId-1].ETCCD;
    	 			if(!g_isEmpty(v_etc_cd)) {
    	 				result = "<input type='text' id='edt_etc_cd_"+options.rowId+"' name='edt_etc_cd_"+options.rowId+"' value='"+ v_etc_cd +"' style='width:95px;' onfocusin='edt_focusin_event();' onfocusout='edt_focutout_event();'>";
    	 			}else{
    	 				result = "<input type='text' id='edt_etc_cd_"+options.rowId+"' name='edt_etc_cd_"+options.rowId+"' value='' style='width:95px;' onfocusin='edt_focusin_event();' onfocusout='edt_focutout_event();'>";
    	 			}
    	 		}
    	 		
    	 		break;
    	 	case "checkbox":
    	 		var v_etc_cd = DS_EST_LIST[options.rowId-1].ETCCD;
    	 		
    	 		if(v_etc_cd == "0") result = "<input type='checkbox' id='chk_etc_cd_"+options.rowId+"' name='chk_etc_cd_"+options.rowId+"' onclick='chk_click_event();'>";
    	 		else result = "<input type='checkbox' id='chk_etc_cd_"+options.rowId+"' name='chk_etc_cd_"+options.rowId+"' checked='true' onclick='chk_click_event();'>";
    
    	 		break;
    	 	case "combo" :
    	 		var v_etc_cd = DS_EST_LIST[options.rowId-1].ETCCD;
    	 		
    	 		result = "<select style='width:80px;' id='cmb_etc_cd_"+options.rowId+"' onchange='cmb_chage_event()'>";

    	 		if(g_isEmpty(est_item_ver)){
    	 			if(now_date < "20160801"){
    	 				var str = DS_EST_LIST[options.rowId-1].ESTETC;
    	 				var arr = str.split(",");
    	 				
    	 				for(var i=0; i< arr.length; i++){
    	 					for(var j=0; j< DS_EST201.length; j++){
    	 						if(arr[i] == DS_EST201[j].CODE){
    	 							if(v_etc_cd == DS_EST201[j].CODE ) result += "<option value='"+DS_EST201[j].CODE+"' selected='true'>"+DS_EST201[j].CODENAME+"</option>";
    	 							else result += "<option value='"+DS_EST201[j].CODE+"'>"+DS_EST201[j].CODENAME+"</option>";
    	 						}
    	 					}
    	 					
    	 				}
    	 			}else{
        	 			var codeType = DS_EST_LIST[options.rowId-1].ESTETC;
        	 			for(var j=0; j< DS_EST201.length; j++){
	 						if(codeType == DS_EST201[j].CODETYPE){
	 							if(v_etc_cd == DS_EST201[j].CODE ) result += "<option value='"+DS_EST201[j].CODE+"' selected='true'>"+DS_EST201[j].CODENAME+"</option>";
	 							else result += "<option value='"+DS_EST201[j].CODE+"'>"+DS_EST201[j].CODENAME+"</option>";
	 						}
	 					}
        	 		}
    	 		}else{
    	 			if(est_item_ver == "1"){
    	 				var str = DS_EST_LIST[options.rowId-1].ESTETC;
    	 				var arr = str.split(",");
    	 				
    	 				for(var i=0; i< arr.length; i++){
    	 					for(var j=0; j< DS_EST201.length; j++){
    	 						if(arr[i] == DS_EST201[j].CODE){
    	 							if(v_etc_cd == DS_EST201[j].CODE ) result += "<option value='"+DS_EST201[j].CODE+"' selected='true'>"+DS_EST201[j].CODENAME+"</option>";
    	 							else result += "<option value='"+DS_EST201[j].CODE+"'>"+DS_EST201[j].CODENAME+"</option>";
    	 						}
    	 					}
    	 					
    	 				}
    	 			}else{
        	 			var codeType = DS_EST_LIST[options.rowId-1].ESTETC;
        	 			for(var j=0; j< DS_EST201.length; j++){
	 						if(codeType == DS_EST201[j].CODETYPE){
	 							if(v_etc_cd == DS_EST201[j].CODE ) result += "<option value='"+DS_EST201[j].CODE+"' selected='true'>"+DS_EST201[j].CODENAME+"</option>";
	 							else result += "<option value='"+DS_EST201[j].CODE+"'>"+DS_EST201[j].CODENAME+"</option>";
	 						}
	 					}
    	 			}
    	 		}
    	 		
    	 		result += "</select>";
    	 		break;
    	 		default : 
    	 			// footer 에 pass 개수 보여주기
	    	 		var ds_pass = DS_EST_LIST.filter(function(item){
						return item.ETCGB == "combo" && (item.ETCCD == '1');
				   });
    	 		
    	 			result += "<div>Pass</div><div>"+ds_pass.length+"</div>";
    	 			break;
    	 	
    	 }
 
    	 
    	 return result;
     }
     
     /* QC평가표의 question, 피드백 항목에 textarea를 넣어주는 함수*/
	 function setEditFormatter(cellValue, options, rowdata, action){
		 var result ="";
		
		 
		 if(cellValue != null && cellValue.indexOf("div") > 0){ // footer 만들기
			 if(options.colModel.name == "QUEST_MEMO"){
				 var ds_feedback = DS_EST_LIST.filter(function(item){ // 패스 개수 조회
					return item.ETCGB == "combo" && (item.ETCCD == '2');
				 });
		 
				 result += "<div>Feedback</div><div>"+ds_feedback.length+"</div>";
			 }else if(options.colModel.name == "RCV_MEMO"){
				 var ds_supp = DS_EST_LIST.filter(function(item){ //녹취보완 개수 조회
					return item.ETCGB == "combo" && (item.ETCCD == '3' || item.ETCCD == '4' || item.ETCCD == '5');
				 });
		 		
				 result += "<div>녹취보완</div><div>"+ds_supp.length+"</div>";
			 }
		 }else{
			 if(g_isEmpty(est_item_ver)){
				 if(now_date < "20160801"){
					if(options.colModel.name == "QUEST_MEMO"){ 
						var v_qst_memo = DS_EST_LIST[options.rowId-1].QUEST_MEMO;
		 	 			
						if(!g_isEmpty(v_qst_memo)) result = "<textarea id='txt_qst_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_qst_memo + "</textarea>";
		 	 			else result = "<textarea id='txt_qst_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
		 	 			
	 	 			}else if(options.colModel.name == "RCV_MEMO"){
	 	 				var v_rcv_memo = DS_EST_LIST[options.rowId-1].RCV_MEMO;
	 	 				
	 	 				if(!g_isEmpty(v_rcv_memo)) result = "<textarea id='txt_rcv_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_rcv_memo + "</textarea>";
		 	 			else result = "<textarea id='txt_rcv_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
		 	 				 	 			
	 	 			}
				 }else{
					 if(DS_EST_LIST[options.rowId-1].ETCGB != 'checkbox'){
						 if(options.colModel.name == "QUEST_MEMO"){ 
							var v_qst_memo = DS_EST_LIST[options.rowId-1].QUEST_MEMO;
				 	 		
							if(!g_isEmpty(v_qst_memo)) result = "<textarea id='txt_qst_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_qst_memo + "</textarea>";
				 	 		else result = "<textarea id='txt_qst_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
				 	 		
			 	 		 }else if(options.colModel.name == "RCV_MEMO"){
			 	 			var v_rcv_memo = DS_EST_LIST[options.rowId-1].RCV_MEMO;
			 	 				
			 	 			if(!g_isEmpty(v_rcv_memo)) result = "<textarea id='txt_rcv_memo_"+options.rowId+"' onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_rcv_memo + "</textarea>";
				 	 		else result = "<textarea id='txt_rcv_memo_"+options.rowId+"'   onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
			 	 		}
					 }
				 }
			 }else{
				 if(est_item_ver == "1"){
					 if(options.colModel.name == "QUEST_MEMO"){ 
							var v_qst_memo = DS_EST_LIST[options.rowId-1].QUEST_MEMO;
			 	 			
							if(!g_isEmpty(v_qst_memo)) result = "<textarea id='txt_qst_memo_"+options.rowId+"'   onclick='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_qst_memo + "</textarea>";
			 	 			else result = "<textarea id='txt_qst_memo_"+options.rowId+"'   onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
			 	 			
		 	 		 }else if(options.colModel.name == "RCV_MEMO"){
		 	 				var v_rcv_memo = DS_EST_LIST[options.rowId-1].RCV_MEMO;
		 	 				
		 	 				if(!g_isEmpty(v_rcv_memo)) result = "<textarea id='txt_rcv_memo_"+options.rowId+"'   onclick='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_rcv_memo + "</textarea>";
			 	 			else result = "<textarea id='txt_rcv_memo_"+options.rowId+"'   onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
		 	 		 }
				 }else{
					 if(DS_EST_LIST[options.rowId-1].ETCGB != 'checkbox'){
						 if(options.colModel.name == "QUEST_MEMO"){ 
							var v_qst_memo = DS_EST_LIST[options.rowId-1].QUEST_MEMO;
				 	 		
							if(!g_isEmpty(v_qst_memo)) result = "<textarea id='txt_qst_memo_"+options.rowId+"'   onclick='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_qst_memo + "</textarea>";
				 	 		else result = "<textarea id='txt_qst_memo_"+options.rowId+"'   onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
				 	 			
			 	 		 }else if(options.colModel.name == "RCV_MEMO"){
			 	 			var v_rcv_memo = DS_EST_LIST[options.rowId-1].RCV_MEMO;
			 	 				
			 	 			if(!g_isEmpty(v_rcv_memo)) result = "<textarea id='txt_rcv_memo_"+options.rowId+"'   onclick='txt_focusin_event();' onfocusout='txt_focutout_event();'>" + v_rcv_memo + "</textarea>";
				 	 		else result = "<textarea id='txt_rcv_memo_"+options.rowId+"'   onfocusin='txt_focusin_event();' onfocusout='txt_focutout_event();'></textarea>";
			 	 		}
					 }
				 }
			 }
		 }
		 
		 return result;
	 }
     
	 /* textarea 수정 이벤트 활성화 시키는거 */
	 function txt_focusin_event(){
		 var eventId = event.target.id;

		 if(event.target.localName == "textarea"){
			$("#"+eventId).addClass('selected'); // readonly 이벤트의 버그로 인해 css로 선택과 미선택영역을 구분하기로함
			 
		 }
	 }
	 
	 function txt_focutout_event(){
		 var eventId = event.target.id;
		 
		 if(event.target.localName == "textarea"){
			var idx = eventId.substr(eventId.lastIndexOf("_")+1,eventId.length)-1;
			var v_txt =  document.getElementById(eventId).value;
			
			if(eventId.indexOf("qst_memo") > -1){ //focus out시 dataset 다시 저장해줌
                 if(v_txt.trim() != DS_EST_LIST[idx].QUEST_MEMO) DS_EST_LIST[idx].QUEST_MEMO = v_txt;
         	}else if(eventId.indexOf("rcv_memo") > -1){
                 if(v_txt.trim() != DS_EST_LIST[idx].RCV_MEMO) DS_EST_LIST[idx].RCV_MEMO = v_txt;
         	}
			
			$("#"+eventId).removeClass('selected');
			
			$("#tbl_est_items").trigger('reloadGrid');
	 	}
	}
	 
	 /*평가 영역에 edit box 이벤트 */
	 function edt_focusin_event(){
		 var eventId = event.target.id;
		
		 if(event.target.type == "text"){
			 $("#"+eventId).addClass('selected');
		 }
	 }
	 
	 function edt_focutout_event(){
		 var eventId = event.target.id;

		 if(event.target.type == "text"){
			var idx = eventId.substr(eventId.lastIndexOf("_")+1,eventId.length)-1;
			var v_txt =  document.getElementById(eventId).value;
				 
			DS_EST_LIST[idx].ETCCD = v_txt; // focus out 될때 입력된 정보를 dataset에 저장해둔다.
				 
			$("#"+eventId).removeClass('selected');
			 
		 }
		 
	 }
	 
	 /*평가의 체크박스 영역 값 세팅*/
	 function chk_click_event(){
		 var eventId = event.target.id;		 
		 var idx = eventId.substr(eventId.lastIndexOf("_")+1,eventId.length)-1;

		 // 체크되면 1, 체크가 안되어있으면 0인거 같음
		 if(event.target.checked) DS_EST_LIST[idx].ETCCD = "1";
		 else DS_EST_LIST[idx].ETCCD = "0";
		
	 }
	 
	 /* 평가 컬럼의 combobox의 값 변경시 데이터 변경 데이터로 세팅해주는 event*/
	 function cmb_chage_event(){
		 var eventId = event.target.id;		 
		 var idx = eventId.substr(eventId.lastIndexOf("_")+1,eventId.length)-1;
		 
		 DS_EST_LIST[idx].ETCCD = event.target.value;
	 }
	 
	 /************************************* QC 평가표 customizing 함수 종료 ***********************************/
	  
	 /*
	 * [OPEN]시 이벤트 처리
	 * STEP10. QC평가이력 테이블을 그리는 함수 
	 */
	 function f_draw_opinionTable(){
		
		 $("#tbl_qc_opinion").jqGrid('setGridParam', {data : DS_QC_OPINION}).trigger('reloadGrid');
		
		 $("#tbl_qc_opinion").jqGrid({
			 datatype : "local",
			 data : DS_QC_OPINION,
			 colNames : ['심사회차', '청약진행상태', '심사일시', '수정일시', 'QC평가자', '변경이력'],
			 colModel : [{name : 'HTDGREE', width: 50, align: 'center'}, 
			             {name : "ESTRESULTCDGD", width: 70, align: 'center', formatter : expr_result_state},
			             {name : "ESTREGISTDT", align: 'center',width: 100, formatter : expr_date_format},
			             {name : "PROCESS_DATE", width: 100, align: 'center', formatter : expr_date_format}, 
			             {name : "ESTREGISTID",  align: 'center', width: 60}, 
			             {name : "MODYN", width: 50, align: 'center'}],
			 rowNum : 10000,
	 	     height: "auto",
	 	     emptyrecords : "조회된 데이터가 없습니다.",
	 	     viewrecords: true,
	 	     //   cellEdit: true,
	 	     //  autowidth: true,
	 	     beforeSelectRow : reset_highlight_row,
			 onCellSelect : opinion_tbl_click,
			 gridComplete: function () {
			        
			        if(DS_QC_OPINION.length == 0){
			        	$("#tbl_qc_opinion tbody").append("<tr><td colspan='6' style='border-right : 1px solid #acacac;border-bottom : 1px solid #acacac;height:25px;text-align:center;'>조회된 심사항목 데이터가 없습니다.</td></tr>")
			        }else{		        	
			        	$("#tbl_qc_opinion tbody tr[id=1]").addClass("ui-state-highlight");
			        }

			        //$("#tbl_qc_opinion .ui-jqgrid-hdiv").width($(".ui-jqgrid-hdiv").width()+1);
			        $("#div_history .ui-jqgrid-bdiv").width($("#div_history .ui-jqgrid-bdiv").width()+1);
				    $("#div_history .ui-jqgrid-hdiv").width($("#div_history .ui-jqgrid-hdiv").width()+1);
				       
			        
			    }
		 });
		 
		 
		 
	 }
	 
	 /****************************** QC 평가이력 CUSTOMIZING 해주는 함수 *****************************************/
	 
	 /* QC 평가이력의 청약진행 상태 값 매핑해주는 함수*/
	 function expr_result_state(cellValue, options, rowdata, action){
	 	var result ="";
	 		
	 	cellValue = cellValue == null ? "" : cellValue;

		if(DS_CMP025.length > 0){
			var temp = DS_CMP025.filter(function(item){
				return item.CODE == cellValue
			});
		}
		
		if(temp.length > 0) result = temp[0].CODENAME;
		
		return result;
	 }
	 
	 /* QC평가이력의 심사일시, 수정일시 포맷 변경해주는 함수 */
	 function expr_date_format(cellValue, options, rowdata, action){
		 var result = "";
	
		 
		 if(cellValue != null){
			 result = cellValue.substr(0,4) + "-" + cellValue.substr(4,2) + "-" + cellValue.substr(6,2) 
			  + " " + cellValue.substr(8,2) + ":" + cellValue.substr(10,2) + ":"+ cellValue.substr(12,2); 
		 }
		 
		 return result;
		 
	 }
	 
	 /****************************** QC 평가이력 CUSTOMIZING 해주는 함수 종료 *****************************************/
	 
	 /* 테이블 하이라이트 초기화 시키는 함수*/
	 function reset_highlight_row(rowid, e){
		 var high =  document.querySelectorAll("#tbl_qc_opinion tbody .ui-state-highlight");
		 
		 for(var i=0, len=high.length; i<len; i ++){
			 high[i].classList.remove("ui-state-highlight");
		 }
	 }
	 
	 /*
	  * QC 평가 리스트에서 클릭 시 이벤트 발생 함수
	 */
	 function opinion_tbl_click(rowid,colid,value){
	
		var cur_qacd = DS_QC_OPINION[rowid-1].QACD;
		var cur_htid = DS_QC_OPINION[rowid-1].HTID;
		 
		f_getEstItemList(cur_qacd,cur_htid); // 평가리스트 재조회 함
		
		// 심사자 의견 상세조회
		f_opnionDetailInfoQm(rowid-1);
		
		if(rowid-1 == 0){
			$("#btn_save").prop("disabled", !btn_save_yn);
		}else{
			$("#btn_save").prop("disabled", true);
		}
		
		// QC 평가 이력에서 변경이력 컬럼을 클릭했고, 해당 값이 Y인 경우 변경이력 화면 팝업
		if(colid == 5 && DS_QC_OPINION[rowid-1].MODYN == "Y"){
			var htid = DS_QC_OPINION[rowid-1].HTID;
			var htdgree = DS_QC_OPINION[rowid-1].HTDGREE;
			est_item_ver = DS_QC_OPINION[rowid-1].EST_ITEM_VER; 
			
			//console.log(htid + "//" + htdgree + "//" + est_item_ver);
			
			window.open("./OBA010T5P1.jsp?HTID="+htid+"&HTDGREE="+htdgree+"&EST_ITEM_VER="+est_item_ver, "선심사_QA통판_변경이력", "width=680, height=360, left=100, top=150, location=no,resizable=no, scrollbars=no");
		}
		 
	 }
	 
	 /**
	  * [SAVE]시 이벤트 처리
	  *  STEP14. 심사자결과 및 심사자의견 저장 함수
	  * @param :
	  * @return :
	  */
	 function btn_save_event(){
		
		// 저장을 수행하기 이전에 저장할때 데이터를 담는 JSON 초기화
		f_init("save");
		
		
		save_gb_sub = save_gb ;
		
		if(f_save_validation()){
			
			
			var sel_op_tbl_row = $("#tbl_qc_opinion").jqGrid('getGridParam','selrow') == undefined ? 0 : $("#tbl_qc_opinion").jqGrid('getGridParam','selrow')-1;
			var v_rdo_state = $("input:radio[name=rdo_state]:checked").val();
			
			v_rdo_state = v_rdo_state == "none" ? "" : v_rdo_state;
			
			//console.log("before : "+save_gb_sub);
			
			// 선택없음 저장 후 다시 선택없음으로 저장시
			if(DS_QA_STATUS[0].RESULT_YN == "Y" && g_isEmpty(DS_QC_OPINION[sel_op_tbl_row].ESTRESULTCDGD) && v_rdo_state == ""){
				save_gb = "UPDATE";
				save_gb_sub = save_gb;
			// 선택없음 저장 후 진행상태 선택 후 저장시
			}else if(DS_QA_STATUS[0].RESULT_YN == "Y" && g_isEmpty(DS_QC_OPINION[sel_op_tbl_row].ESTRESULTCDGD) && v_rdo_state != ""){
				save_gb = "UPDATE";
				save_gb_sub = "UPDATE2";
			}
		
			
			DS_EST_RES_HIST = [];
			for(var i =0; i< DS_EST_LIST.length; i++){
				var temp = {
					"HTID" : "",
					"ESTTCATCD" : DS_EST_LIST[i].ESTTCATCD,
					"ESTRESULTCD" : DS_EST_LIST[i].ETCCD,
					"QUEST_MEMO" : DS_EST_LIST[i].QUEST_MEMO,
					"RCV_MEMO" : DS_EST_LIST[i].RCV_MEMO
				}
				
				DS_EST_RES_HIST.push(temp);
				
			}
			
			//TB_EST_RESULT_HIST_LOG 백업
			DS_EST_RES_HIST_LOG = JSON.parse(JSON.stringify(DS_EST_RES_HIST));
			
			// 구분선 앞에 평가일시 추가
			var v_qc_opinion = document.getElementById("qc_opinion").value;
			var v_tsr_opinion = document.getElementById("tsr_opinion").value;
			
			
			var now_datetiem = g_getNow();
			
			var strByteLength = function(s,b,i,c){
				for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1);
				return b
		 	};
		 	
		 	if(opi_max_len - strByteLength(v_qc_opinion) > 74 ){
		 		v_qc_opinion = now_datetiem + " ------------------------------------------------------\n\n" + document.getElementById("qc_opinion").value;
		 	}
		 	
		 	// update가 아닌 경우에는 bIsmod는 true
		 	if(!bIsMod){ //설계가 만료된 경우에는 정보만 업데이트하고, 변경이력은 저장하지 않는다!
		 		if(DS_QA_STATUS[0].RESULT_YN == "Y"){
		 			
		 			//====================================================
					// TB_PRE_EST_HISTORY(심사이력)에 Update - 심사결과 값을 Setting 한다.
					//====================================================
		 			
		 			DS_EST_HIST.push({
		 				"HTID" : 0,
		 				"CON_ENT_DGN_NO" : ent_dgn_no,
		 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
		 				"QACD" :  qacd,
		 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state, // QC최종 통과이면서 계≠피, 사망담보 있는 경우 -> 56으로 처리
		 				"ESTOPINION" : v_tsr_opinion,
		 				"QAOPINION" : v_qc_opinion,
		 				"RTOPINION" : "",
		 				"ESTREGISTDT" : "",
		 				"RTREGISTDT" : user_id,
		 				"ESTREGISTID" : user_id,
		 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
		 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
		 				"EVAL_SCOR" : ""
		 			});
		 		}else{
		 			//==================================================== 
					// TB_EST_HISTORY(심사이력)에 보완차수 Insert --> 심사결과 값을 Setting 한다.
					//====================================================
					DS_EST_HIST.push({
		 				"HTID" : 0,
		 				"CON_ENT_DGN_NO" : ent_dgn_no,
		 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
		 				"QACD" :  qacd,
		 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
		 				"ESTOPINION" : v_tsr_opinion,
		 				"QAOPINION" : v_qc_opinion,
		 				"RTOPINION" : "",
		 				"ESTREGISTDT" : "",
		 				"RTREGISTDT" : "",
		 				"ESTREGISTID" : user_id,
		 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
		 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
		 				"EVAL_SCOR" : ""
		 			});
		 		}
		 		
		 	}else if(v_rdo_state == ""){ // 청약 진행상태를 선택안함을 택했을 때
		 		if(DS_QA_STATUS[0].RESULT_YN == "Y"){
		 			if(save_gb == "UPDATE"){
		 				//====================================================
						// TB_PRE_EST_HISTORY(심사이력)에 Update
						//====================================================
						DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : user_id,
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 				});
						
		 			}else{
		 				//==================================================== 
						// TB_EST_HISTORY(심사이력)에 보완차수 Insert
						//====================================================
							
						DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : "",
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 				});
		 				
		 			}
		 			
		 			
		 			for(var i=0; i< DS_EST_LIST.length; i++){
		 				if(DS_EST_LIST[i].ETCCD != DS_EST_LIST_COPY[i].ETCCD){ // 이전 버전에서 수정이 일어난 경우에 변경이력을 저장하기 위해
		 					DS_EST_LIST_IN.push({
		 						"HTID" : DS_QA_STATUS[0].CUR_HTID,
		 						"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE,
		 						"PRE_RST_CD" : DS_EST_LIST_COPY[i].ETCCD,
		 						"MOD_RST_CD" : DS_EST_LIST[i].ETCCD,
		 						"ESTTCATCD" : DS_EST_LIST_COPY[i].ESTTCATCD,
		 						"EST_CD_ID" : DS_EST_HIST[0].SC_CD_ID,
		 						"EST_ITEM_VER" : DS_EST_HIST[0].EST_ITEM_VER
		 					});	 						
		 				}
		 			}
		 		}else{
		 			
		 			//==================================================== 
					// TB_EST_HISTORY(심사이력)에 보완차수 Insert
					//====================================================
					
					DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : 0, 
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : "",
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 			});
		 		}
		 	}else {
		 		if(DS_QA_STATUS[0].RESULT_YN == "Y"){
		 			if(save_gb == "UPDATE"){
		 				//==================================================== 
						// TB_PRE_EST_HISTORY(심사이력)에 Update
						//====================================================
		 				DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : user_id,
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 				});

		 			}else if(save_gb == "INSERT"){
		 				//==================================================== 
						// TB_EST_HISTORY(심사이력)에 보완차수+1 Insert
						//====================================================
						
						DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE, // 현 보완 차수
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : "",
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 				});
		 			}
		 			
		 			DS_INSU_PLAN_MAST[0].TM_PROCESSING_GB = (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state; // TM설계상태
		 			
		 			if(v_rdo_state == "62"){
		 				if(psn_yn == "Y" || poa_yn == "N"){ 
		 					DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "23"; // 가입설계상태 : 계≠피, 사망담보= Y
		 				}
		 			}else if(v_rdo_state == "53"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "12"; // QC보완
		 			}else if(v_rdo_state == "65"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "01"; // QC-UW보완
		 			}else if(v_rdo_state == "46"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "37"; //UW-심사의뢰
		 				DS_INSU_PLAN_MAST[0].CON_ETC_FIELD_3 = "Y";
		 			}
		 			
		 			for(var i=0; i< DS_EST_LIST.length; i++){
		 				if(DS_EST_LIST[i].ETCCD != DS_EST_LIST_COPY[i].ETCCD){ // 이전 버전에서 수정이 일어난 경우에 변경이력을 저장하기 위해
		 					DS_EST_LIST_IN.push({
		 						"HTID" : DS_QA_STATUS[0].CUR_HTID,
		 						"HTDGREE" : DS_QA_STATUS[0].CUR_HTDGREE,
		 						"PRE_RST_CD" : DS_EST_LIST_COPY[i].ETCCD,
		 						"MOD_RST_CD" : DS_EST_LIST[i].ETCCD,
		 						"ESTTCATCD" : DS_EST_LIST_COPY[i].ESTTCATCD,
		 						"EST_CD_ID" : DS_EST_HIST[0].SC_CD_ID,
		 						"EST_ITEM_VER" : DS_EST_HIST[0].EST_ITEM_VER
		 					});	 						
		 				}
		 			}
		 			
		 		}else{
		 			//==================================================== 
					// TB_EST_HISTORY(심사이력)에 보완차수 1차수 Insert
					//====================================================
					
					DS_EST_HIST.push({
			 				"HTID" : 0,
			 				"CON_ENT_DGN_NO" : ent_dgn_no,
			 				"HTDGREE" : 1,
			 				"QACD" :  qacd,
			 				"ESTRESULTCD" : (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state,
			 				"ESTOPINION" : v_tsr_opinion,
			 				"QAOPINION" : v_qc_opinion,
			 				"RTOPINION" : "",
			 				"ESTREGISTDT" : "",
			 				"RTREGISTDT" : "",
			 				"ESTREGISTID" : user_id,
			 				"EST_ITEM_VER" : DS_EST_LIST[0].EST_ITEM_VER,
			 				"SC_CD_ID" : DS_EST_LIST[0].EST_CD_ID,
			 				"EVAL_SCOR" : ""
		 			});
		 			
					DS_INSU_PLAN_MAST[0].TM_PROCESSING_GB = (v_rdo_state == "62" && psn_yn == "N" && poa_yn == "Y") ? "56" : v_rdo_state;
					
					if(v_rdo_state == "62"){
		 				if(psn_yn == "Y" || poa_yn == "N"){
		 					DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "23";
		 				}
		 			}else if(v_rdo_state == "53"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "12";
		 			}else if(v_rdo_state == "65"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "01";
		 			}else if(v_rdo_state == "46"){
		 				DS_INSU_PLAN_MAST[0].CON_CONT_STATE = "37";
		 				DS_INSU_PLAN_MAST[0].CON_ETC_FIELD_3 = "Y";
		 			}
		 		}
		 		
		 		DS_INSU_PLAN_MAST[0].TM_UPDATE_PRE_JUDGE_DATE = ""; //심사일자
		 		DS_INSU_PLAN_MAST[0].TM_UPDATE_PRE_QA = user_id; //할당된 심사자 상관없이 실제 심사한 심사자ID로 업데이트 함.
		 		DS_INSU_PLAN_MAST[0].CON_ENT_DGN_NO = ent_dgn_no;
		 		DS_INSU_PLAN_MAST[0].CON_P_PSN_NO = DS_QA_STATUS[0].CON_P_PSN_NO;
		 		DS_INSU_PLAN_MAST[0].CON_APP_DATE = DS_QA_STATUS[0].CON_APP_DATE;
		 		DS_INSU_PLAN_MAST[0].CON_ISTAR_CONT_DATE = DS_QA_STATUS[0].CON_APP_DATE;
		 		DS_INSU_PLAN_MAST[0].CON_IEND_CONT_DATE = f_getConIEndContDate(DS_QA_STATUS[0].CON_APP_DATE);
		 		DS_INSU_PLAN_MAST[0].TREATYCD = DS_USER_INFO[0].TREATYCD;
		 		DS_INSU_PLAN_MAST[0].ESTRESULTCD = DS_QA_STATUS[0].v_rdo_state;
		 		
		 	}
		 	
		 	// 당일 사망담보건 중 최종통과가 몇건인지 체크
		 	var temp = DS_QC_OPINION.filter(function(item){
				return item.ESTRESULTCD == "56"
			});
		 	
		 	var now_pass_cnt = 0;
		 	
		 	for(var i=0; i< temp.length; i++){
		 		var sdate = DS_QC_OPINION[i].ESTREGISTDT.substr(0,8);
		 		if(now_date = sdate) now_pass_cnt++;
		 	}
		 	
		 	// 평가점수계산
		 	DS_EST_HIST[0].EVAL_SCOR = "100";
		 	
		 	for(var i =0;i<DS_EST_LIST.length;i++){
		 		if(DS_EST_LIST[i].ETCGB == "combo"){
		 			var v_etccd = DS_SYS130.filter(function(item){
						return item.CODE_ID == DS_EST_LIST[i].ETCCD
					});
		 			
		 			if(v_etccd.length > 0){
		 				DS_EST_HIST[0].EVAL_SCOR = 0;
		 				break;
		 			}
		 		}
		 	}
		 	
		   // 사망담보 정보 전송에 필요한 정보 저장 data set
		 	f_set_DS_LTTMINFR0124();
		   
		   
		 	DS_EST_HIST_LOG = JSON.parse(JSON.stringify(DS_EST_HIST));

		 	//DS_RDO_STATE
		 	var temp = DS_RDO_STATE.filter(function(item){
				return item.CODE_ID == v_rdo_state;
			});
		 	
		 	var confirm_msg = ""; 
		 	if(v_rdo_state == "" || save_gb_sub == "UPDATE" || !bIsMod){
		 		confirm_msg = "저장하시겠습니까?";
		 	}else{
		 		confirm_msg = "설계상태가 " + temp[0].CODE_NM + "(으)로 변경됩니다. 진행하시겠습니까?";
		 	}
		 	
		 	/*******여기부터 해야됨!!!*******/
		 	/*console.log(confirm_msg);
		 	
		 	console.log("save_gb = " + save_gb_sub);
		   
		 	console.log("######저장 대상 데이터들");
		 	console.log("#DS_EST_RES_HIST :: ");
		 	console.log(DS_EST_RES_HIST);
		 	
		 	console.log("#DS_EST_HIST_LOG :: ");
		 	console.log(DS_EST_HIST_LOG);
		 	
		 	console.log("#DS_EST_HIST :: ");
		 	console.log(DS_EST_HIST);
		 	
		 	console.log("#DS_EST_HIST_LOG :: ");
		 	console.log(DS_EST_HIST_LOG);
		 	
		 	console.log("#DS_INSU_PLAN_MAST :: ");
		 	console.log(DS_INSU_PLAN_MAST);
		 	
		 	console.log("#DS_LTTMINFR0124 :: ");
		 	console.log(DS_LTTMINFR0124);
		 	
		 	console.log("#DS_EST_LIST_IN :: ");
		 	console.log(DS_EST_LIST_IN);
		 	
		 	console.log("#DS_EST_RES_HIST_LOG :: ");
		 	console.log(DS_EST_RES_HIST_LOG);*/
		 	
		 	if(confirm(confirm_msg)){
		 		
		 		   document.getElementById("qc_opinion").value = v_qc_opinion;

				   var xhr = new XMLHttpRequest();
					
					xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
					var hostname = window.location.hostname.split(".");
					var server_gb = hostname[0];
					
					if(server_gb == "127") server_gb = "vauat";
					
					//※ 현재 테스트라 
					//server_gb = "vauat";
					   
					var param = "cmd=SavePreJudgeInfo&DS_EST_RES_HIST="+JSON.stringify(DS_EST_RES_HIST)+"&DS_EST_RES_HIST_LOG="+JSON.stringify(DS_EST_RES_HIST_LOG)+"&DS_EST_HIST="+JSON.stringify(DS_EST_HIST)
								+"&DS_EST_HIST_LOG="+JSON.stringify(DS_EST_HIST_LOG)+"&DS_INSU_PLAN_MAST="+JSON.stringify(DS_INSU_PLAN_MAST)+"&DS_LTTMINFR0124="+JSON.stringify(DS_LTTMINFR0124) +"&DS_EST_LIST_IN="+JSON.stringify(DS_EST_LIST_IN);
					
					param += "&CON_ENT_DGN_NO="+ent_dgn_no+"&SAVE_GB="+save_gb_sub+"&CUR_HTDGREE="+DS_QA_STATUS[0].CUR_HTDGREE+"&CUR_HTID="+DS_QA_STATUS[0].CUR_HTID
						  +"&STATE_CD="+v_rdo_state+"&MOD_YN="+bIsMod+"&PRE_TM_PROCESSING_GB="+DS_QA_STATUS[0].PRE_TM_PROCESSING_GB+"&PSN_YN="+psn_yn+"&POA_YN="+poa_yn
						  +"&SIMSA_CNT="+now_pass_cnt+"&QACD="+qacd+"&SERVER_GB="+server_gb;
					
					xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
					 
					xhr.onreadystatechange = function() {				
				        if(xhr.readyState == 4) {
				        	if(xhr.status == 200) {
				        		f_callback("btn_save_event", xhr);
				        		
				        	}        	
				        }
				    };
				    xhr.send(param);	
		 	}
		 	
		 	
		 	
		}
	 }
	 
	 /* 
	  * [SAVE]시 이벤트 처리
	  * STEP15. 저장 전 validation check
	  */
	 function f_save_validation(){
		 /*radio 버튼 state 상태 : NONE : 선택없음, 62 : qc 최종 통과,  53:qc보완 65:qc-uw보완, 46:uw심사의뢰*/
		 var result_cd = !g_isEmpty(DS_QA_STATUS[0].RESULT_CD) ? DS_QA_STATUS[0].RESULT_CD : "";
		 
		 // 문자 BYTE 계산해주는 함수
		 var strByteLength = function(s,b,i,c){
				for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1);
				return b
		 };
			 
		 var tsr_opinion = document.getElementById("tsr_opinion").value;
		 var qc_opinion = document.getElementById("qc_opinion").value;
		 
		 
		 if(strByteLength(tsr_opinion) > opi_max_len){
			 alert("QC-TSR의견 " + opi_max_len + "Byte를 초과할 수 없습니다.\n" + opi_max_len + "Byte에 맞게 QC-TSR의견을 수정하세요.");
			 return false;
		 }else if(strByteLength(qc_opinion) > opi_max_len){
			 alert("QC-UW의견 " + opi_max_len + "Byte를 초과할 수 없습니다.\n" + opi_max_len + "Byte에 맞게 QC-UW의견을 수정하세요.");
			 return false;
		 }
		
		 var v_rdo_state = $("input:radio[name=rdo_state]:checked").val();
		 
		 if(result_cd != '56'){
			 if(v_rdo_state == undefined){ 
				 alert("청약 진행 상태를 선택하세요.");
				 return false;
			 }
			 
		 }
	
		 if(v_rdo_state == "none" && result_cd != ""){
			 alert("선택없음은 선택할 수 없습니다.");
			 return false;
		 }
	
		 if(!g_isEmpty(v_rdo_state) && DS_QA_STATUS[0].PRE_TM_PROCESSING_GB == "48"){
			// UW심사의뢰, QC-UW보완만 선택가능
			if(v_rdo_state == "46" || v_rdo_state == "65" ){
				alert("QC-UW보완, UW심사의뢰만 선택가능합니다.");
				return false;
			}
			
			
		 }else if(!g_isEmpty(v_rdo_state) && result_cd == "67"){ // 계좌변경시에는 못함
			// UW심사의뢰, QC-UW보완만 선택불가능
			if(v_rdo_state == "46"){
				//UW심사의뢰
				alert("UW심사의뢰는 선택할 수 없습니다.");
				return false;
			}else if(v_rdo_state == "65" ){
				alert("QC-UW보완은 선택할 수 없습니다.");
				return false;
			}
		 }
		 
		 
		// QC통과 선택시
		if(v_rdo_state == "62"){
		
		// QC보완 선택시	
		}else if(v_rdo_state == "53"){

			// QC-TSR 의견
			if (tsr_opinion.trim() == '' || g_isEmpty(tsr_opinion.trim())) {
				alert("QC-TSR 의견을 입력하세요.");
				return false;
			}	
			
		// QC-UW보완 선택시
		}else if(v_rdo_state == "65"){
			
			// QC-TSR 의견
			if (tsr_opinion.trim() == '' || g_isEmpty(tsr_opinion.trim())) {
				alert("QC-TSR 의견을 입력하세요.");
				return false;
			}
				
			// QC-UW 의견
			if (qc_opinion.trim() == '' || g_isEmpty(qc_opinion.trim())) {
				alert("QC-UW 의견을 입력하세요.");
				return false;
			}
		
		// UW심사의뢰 선택시	
		}else if(v_rdo_state == "46"){
			if(DS_QA_STATUS[0].AUTO_YN == "Y"){
				alert("자동심사건은 UW심사의뢰 상태로 변경이 불가합니다.");
				return false;
			}else if(g_isEmpty(qc_opinion.trim())){
				alert("QC-UW 의견을 입력하세요.");
				return false;
			}
		}
		
		
		// 상령일 체크 (보장 나이가 달라진 경우)
		if(chk_insur_age == "Y"){
			if(v_rdo_state != "none" && v_rdo_state != "65"){
				alert("보장개시일 기준으로 보험나이가 변경됩니다.\nQC-UW보완으로 처리 진행해주세요.");
				return false;
			}
		}
		
		 
		if(DS_QA_STATUS[0].RESULT_YN == "Y" && v_rdo_state == result_cd){
			if(DS_QA_STATUS[0].PRE_TM_PROCESSING_GB == "47" || DS_QA_STATUS[0].PRE_TM_PROCESSING_GB == "48" || DS_QA_STATUS[0].PRE_TM_PROCESSING_GB  == "55"){
				save_gb_sub = "INSERT";
			}else{
				save_gb_sub = "UPDATE";
			}		
		}
		
		return true;
	 }
	 
	 
	 function f_set_DS_LTTMINFR0124(){
		 var callauth = DS_USER_INFO[0].CALLAUTH;
		 
		 DS_LTTMINFR0124 = [];
		 
		 var v_issuBrkdClcd = ["0012","0013","0014"];
		 for(var i= 0; i< 3;i++){
			 var temp = {
					 	"rltNo" : ent_dgn_no,  										// 가입설계번호
						"issuBrkdClcd" : v_issuBrkdClcd[i], 						// 청약서
						"issReqDt" : now_date,										// 발행요청일자
						"rqsqCrno" : user_id,										// 요청자사번
						"prodCd" : DS_QA_STATUS[0].CON_I_KIND_CD,					// 상품코드
						"istpCd" : "4",												// 보험종목코드
						"actlCrno" : user_id,										// 취급자or사용인
						"newYn" : "Y",												// 신규여부
						"ctraId" : DS_QA_STATUS[0].CUSTNO, 							// 고객번호
						"insaId" : DS_QA_STATUS[0].CON_IP_CUST_NO,					// 피보험자 고객번호
						"startYm" : "",												// 손금산입시작일자
						"issuClcd" : "1",											// 발행구분코드
			};
				 
			DS_LTTMINFR0124.push(temp);
		 }
	 }
	 
	 /*===============================================================
	 *  보험종기일자 조회
	 *===============================================================*/
	function f_getConIEndContDate(strDate) {

		var conIEndContDate = "";
		var startYear  = strDate.substr(0,4);
		var startMonth = strDate.substr(4,4);
		var yCnt       = DS_QA_STATUS[0].CON_CON_EXP_Y_CNT; // 보험 만료 기간(ex) 10)

		var vv_s = parseInt(startYear) + parseInt(yCnt);

		if (startMonth == "0229") {
			conIEndContDate = vv_s + "0228";
		} else {
			conIEndContDate = vv_s + startMonth;
		}

		return conIEndContDate;
	}
	 
	 /* 
	  * 심사 진행 상태 라디오 버튼 변경 이벤트 
	 */
	 function rdo_state_change(){
		 var result_cd = !g_isEmpty(DS_QA_STATUS[0].RESULT_CD) ? DS_QA_STATUS[0].RESULT_CD : "";
		 var selected_rdo = event.target.value;
		 
		// 선택없음은 선택할 수 없음	
		 if(selected_rdo == "none" && result_cd != ''){
			 alert("선택없음은 선택할 수 없습니다.");
			 return false;
		 }
		
		// TM진행상태(UW보완QC심사 48) 일때
		if(selected_rdo != 'none' && DS_QA_STATUS[0].PRE_TM_PROCESSING_GB == "48"){
			if(selected_rdo == "46" || selected_rdo == "65"){
				alert("QC-UW보완, UW심사의뢰만 선택가능합니다.");
				return false;
			}
		}else if(selected_rdo != 'none' && result_cd == "67"){ // 계좌변경 일때
			
			if(selected_rdo == "65" || selected_rdo == "46"){
				var temp = DS_RDO_STATE.filter(function(item){
					return item.CODE_ID == selected_rdo;
				});
				
				alert(temp[0].CODE_NM + "는 선택할 수 없습니다.");
				return false;
			}
		}
		
		// UW심사의뢰 선택시
		if(selected_rdo == "46"){
			if(DS_QA_STATUS[0].AUTO_YN == "Y"){
				alert("자동심사건은 UW심사의뢰 상태로 변경이 불가합니다.");
				return;
			}
		}
		
		// 상령일 체크
		if(chk_insur_age == "Y"){
			if(selected_rdo != "none" && selected_rdo != '65'){
				alert("보장개시일 기준으로 보험나이가 변경됩니다.\nQC-UW보완으로 처리 진행해주세요.");
				return false;
			}
		}
	 }
	 
	 /*
	  * 평가표 엑셀 다운로드 클릭 시
	 */
	 function btn_excel_down(){
		 var sel_est_hist = $("#tbl_qc_opinion").jqGrid('getGridParam', 'selrow')-1;
 
		 var cur_qacd = "";
 		 var cur_htid = "";
 		 
 		 if(DS_QC_OPINION.length > 0){ // 최초에 저장하고 싶은 경우에는 이력정보가 없기때문에 공백으로 전달!
 			sel_est_hist = sel_est_hist == -1 ? 0 : sel_est_hist;
 			cur_qacd = (g_isEmpty(DS_QC_OPINION[sel_est_hist].QACD) == true) ? "" : DS_QC_OPINION[sel_est_hist].QACD; //최종심사단계
 			cur_htid = (g_isEmpty(DS_QC_OPINION[sel_est_hist].HTID) == true) ? "" : DS_QC_OPINION[sel_est_hist].HTID; // 최종HTID값
 		 }
 		
		 var xhr = new XMLHttpRequest();
			
		 xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do", true);
								
		 var param = "cmd=getEstListExcel&ent_dgn_no="+ent_dgn_no+"&qacd="+cur_qacd+"&htid="+cur_htid;
		 xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
								
		 xhr.onreadystatechange = function() {			
			if(xhr.readyState == 4) {
				if(xhr.status == 200) {
					f_callback("btn_excel_down", xhr);
				}
			}
		 };
					
		xhr.send(param);	
		
	 }
	 
	 /*
	  * 엑셀 다운로드 용 테이블 그리는 함수
	 */
	 function f_draw_est_item_excel(){
		 $("#tbl_est_items_excel").jqGrid('setGridParam', {data : DS_EST_LIST_EXCEL}).trigger('reloadGrid');
		 
		
		 $("#tbl_est_items_excel").jqGrid({
			 datatype : "local",
			 data : DS_EST_LIST_EXCEL,
			 colNames : ['TYPE1', 'TYPE2', 'No.', 'Question', '평가', 'Feedback Comment', '보완 Comment'],
			 colModel : [{name : 'ESTSCATNM', width: 80, align: 'center'}, 
			             {name : "ESTTCATNM", width: 82, align: 'center'},
			             {name : "TOT_NO", align: 'center',width: 30},
			             {name : "QST_CONT", width: 250}, 
			             {name : "ETCGB_NM",  align: 'center', width: 100}, 
			             {name : "QUEST_MEMO", width: 170},
			             {name : "RCV_MEMO", width: 170}],
			 rowNum : 10000,
	 	     height: "100%",
	 	     footerrow:true, // footer 생성 가능하도록 --> ※ footer는 필요하면 추가해야됨 --> footer 불필요로 구현 안함
	 	     userDataOnFooter:true, // footer 생성 가능하도록 
			 gridComplete: function () {
				 var grid = this;
				 
				 var ds_qc_ratio = DS_EST_LIST_EXCEL.filter(function(item){
						return item.ETCGB == "combo" && (item.ETCCD == '4' || item.ETCCD == '5');
			    	   //return item.ETCGB == "combo";
				 });
			     
				 var v_ratio = ds_qc_ratio.length > 0 ? "0%" : "100%";
				 
				 var ds_pass = DS_EST_LIST_EXCEL.filter(function(item){
						return item.ETCGB == "combo" && (item.ETCCD == '1');
				 });
				 
				 var ds_feedback = DS_EST_LIST.filter(function(item){ // 패스 개수 조회
						return item.ETCGB == "combo" && (item.ETCCD == '2');
					 });
			 
				 
				 var ds_supp = DS_EST_LIST.filter(function(item){ //녹취보완 개수 조회
						return item.ETCGB == "combo" && (item.ETCCD == '3' || item.ETCCD == '4' || item.ETCCD == '5');
				 });
				 
				$(grid).jqGrid('footerData', 'set', {ESTSCATNM : "Total", 
						QST_CONT : "QC Pass Ratio : "+ v_ratio,
						ETCGB_NM : "Pass : "+ds_pass.length,
						QUEST_MEMO : "Feedback : "+ds_feedback.length,
						RCV_MEMO : "녹취보완 : "+ds_supp.length }
 				 );
			  }
		 });

		 $("#tbl_est_items_excel").jqGrid("exportToExcel",{
			 includeLabels : true,
			 includeGroupHeader : true,
			 includeFooter : true,
			 fileName : "QC평가표다운로드_"+getNow()+".xlsx",
			 maxLength : 40
		 });
	 }
	
	/*
	 * PAGE 종료 시, 평가표 상태 업데이트 수행
	*/
	function page_unload(){
		if(open_userId == user_id && view_gb != "view"){
			f_updateViewStatus("close");
			
			return null;
		} 
	}
	
	/*// ※ 업데이트가 불필요해서 주석 처리함
	window.onbeforeunload = function(){
		if(open_userId == user_id && view_gb != "view"){
			if(typeof window.opener.close_est_popup == "function"){
				window.opener.close_est_popup();
			}else{
				f_updateViewStatus("close");
			}
			//f_updateViewStatus("close");
			//console.log(parent);
			//parent.test();
			
			//(e || window.event).retrunValue = null;
			//return "이 페이지를 종료하시겠습니까?";
		} 
	};*/
	
	function screenBottom() {
	    return $(window).scrollTop() + $(window).height();
	}

</script>

</head>
<body onload="page_load()">
	<div id="div_title">
		<div id="title">QC 평가표</div>
		<!-- <div id="btn_exit">
			<button class="btn" style="background-color: #ffffff; color: #4F4F4F; border:1px solid #c6c6c6;" onclick="page_unload()">닫 기</button>
		</div>  -->
	</div>
	<div id="div_search">
		<div class="search_lbl" style="width:75px;"><label>가입설계번호</label></div>
		<div class="search_edt edt_readonly"><input id="con_ent_dgn_no" type="text" value="" readonly="readonly"></div>
		<div class="search_lbl"><label id="lbl_notice" style="visibility: hidden;">기삭제된 설계입니다.</label></div>
	</div>
	<div>
		<div><button id="btn_excel" onclick="btn_excel_down()"></button></div>
		<div style="display: none;"><table id="tbl_est_items_excel"></table></div>
		
	</div>
	<div id="div_estTable">
		<table id="tbl_est_items"></table>
	</div>
	<div id="div_history">
		<div class="lbl_subtitle">QC 평가이력</div>
		<table id="tbl_qc_opinion"></table>
		<div id="his_notice">
			<label>※ QC평가이력 클릭시 평가한 항목과 의견을 보실 수 있습니다.</label>
			<br>
			<label>변경이력이 'Y'일때 클릭하면 변경이력 리스트를 보실 수 있습니다.</label>
		</div>
	</div>
	<div id="div_status">
		<div class="div_status_sub">
			<div class="lbl_subtitle">QC-TSR 의견</div>
			<div><textarea id="tsr_opinion" class="txt_option"></textarea></div>
		</div>
		<div class="div_status_sub">
			<div class="lbl_subtitle">QC-UW 의견</div>
			<div><textarea id="qc_opinion" class="txt_option"></textarea></div>
		</div>
		<div class="div_status_sub">
			<div class="lbl_subtitle">청약 진행상태</div>
			<div>
				<ul class="qc_status">
					<li><label for="radio_state_01"><input type="radio" id="radio_state_01" name="rdo_state" checked value="none" onchange="rdo_state_change();">선택없음</label></li>
					<li><label for="radio_state_02"><input type="radio" id="radio_state_02" name="rdo_state" value="62" onchange="rdo_state_change();">QC최초통과</label></li>
					<li><label for="radio_state_03"><input type="radio" id="radio_state_03" name="rdo_state" value="53" onchange="rdo_state_change();">QC보완</label></li>
					<li><label for="radio_state_04"><input type="radio" id="radio_state_04" name="rdo_state" value="65" onchange="rdo_state_change();">QC-UW보완</label></li>
					<li><label for="radio_state_05"><input type="radio" id="radio_state_05" name="rdo_state" value="46" onchange="rdo_state_change();">UW심사의뢰</label></li>
				</ul>
			</div>
		</div>
		<div>
			<button id="btn_save" class="btn" style="float:right;background-color: rgb(37, 121, 209);border:none;color:#ffffff;margin-top:10px;cursor:pointer;" onclick="btn_save_event();">저 장</button>
		</div>
	</div>
</body>
</html>