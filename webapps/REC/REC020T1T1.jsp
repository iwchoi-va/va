<%@page import="com.locus.jedi.transfer.ListParam"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MR/LS리스트</title>
<script type='text/javascript' src='../chart/lib/jquery-1.11.1.js'></script>
<link rel="stylesheet" type="text/css" href="../css/rec020t1t1.css">
<script src='../js/scriptutil.js' type="text/javascript" charset="UTF-8"></script>
<script src='../js/oba010p5_util.js' type="text/javascript" charset="UTF-8"></script>
<script src="../js/jqgrid/js/trirand/i18n/grid.locale-kr.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/trirand/src/jquery.jqGrid.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/jszip.min.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/css/trirand/ui.jqgrid.css" />

<style>
body{
	/*margin-top : 0px;*/
	/*margin-left : 2px;*/
}

.pointer {cursor : pointer;}

</style>
<script type="text/javascript">

$(document).ready(function(){

	f_draw_mrlsGrid();
	f_draw_excelGrid();
	
});
	
	var DS_MRLS_LIST = [];
	var DS_EXCEL = [];
	
	//rowspan에 필요한 변수
	var prevCellVal = [{ cellId: undefined, value: "" }, { cellId: undefined, value: "" }, { cellId: undefined, value: "" }
					 , { cellId: undefined, value: "" }, { cellId: undefined, value: "" }]; 
	var  idx = 0;
	

	/**********************************************
	* MR/LS용 테이블 그리는 함수
	**********************************************/
	function f_draw_mrlsGrid(){
		
		// rowspan을 위한 조건 체크하는 부분
		var attrCell = function (rowId, val, rowObject, cm, rdata) {
			var result;
			
			 switch(cm.name){
				 case "CENTER_NM" :
					 idx = 0;
					 break;
				 case "AGENT_NM" : 
					 idx = 1;
					 break;
				 case "CUST_ID":
					 idx = 2;
					 break;
				 case "CON_IP_PSN_NAME" :
					 idx = 3;
					 break;
				 case "BIRTH_DT" :
					 idx = 4;
					 break;
		 	}
			 
			var temp = "";
			var row = rowId -1;
			
			if(prevCellVal[idx].value.length > 0) temp = prevCellVal[idx].value.split("^");
			
			if(temp.length == 4){
				if(temp[0] == DS_MRLS_LIST[row].CENTER_CD && temp[1] == DS_MRLS_LIST[row].AGENT_ID && 
				   temp[2] == DS_MRLS_LIST[row].CUST_ID && temp[3] == DS_MRLS_LIST[row].CON_IP_PSN_NAME){
					result = ' style="display: none" rowspanid="' + prevCellVal[idx].cellId + '"';
				}else{
					var cellId = this.id + '_row_' + rowId + '_' + cm.name;
					var comp =  DS_MRLS_LIST[row].CENTER_CD + '^' + DS_MRLS_LIST[row].AGENT_ID + '^' + DS_MRLS_LIST[row].CUST_ID+ "^" + DS_MRLS_LIST[row].CON_IP_PSN_NAME;
					
	                result = ' rowspan="1" id="' + cellId + '"';
	                prevCellVal[idx] = { cellId: cellId, value: comp };
				}
			}else{
				var cellId = this.id + '_row_' + rowId + '_' + cm.name;
				var comp =  DS_MRLS_LIST[row].CENTER_CD + '^' + DS_MRLS_LIST[row].AGENT_ID + '^' + DS_MRLS_LIST[row].CUST_ID + "^" + DS_MRLS_LIST[row].CON_IP_PSN_NAME;
				
                result = ' rowspan="1" id="' + cellId + '"';
                prevCellVal[idx] = { cellId: cellId, value: comp };
			}
			
            return result;
        };
		 
		$("#tbl_mrls_list").jqGrid({
			 datatype : "local",
			 data : DS_MRLS_LIST,
			 colNames : ['No', '청취' ,'콜수(녹취/TM)','센터', '설계번호','MR여부','LS여부','조회일시', '상담원명', '고객번호', '고객명', '생년월일','심사상태','Comment','QC명','심사일자'],
			 colModel : [ 
						 {name : "No", width: 25, align: 'center'},
						 {name : "PLAY", width: 30, align: 'center',formatter : imgFormatter},
			             {name : "CNT_STAT", width: 80, align: 'center'},
			             {name : "CENTER_NM", align: 'center',width: 80 , cellattr: attrCell},
			             {name : "CON_ENT_DGN_NO", align: 'center',width: 80},
			             {name : "MR_YN", width: 50, align: 'center'}, 
			             {name : "LS_YN",  align: 'center', width: 50},
			             {name : "REG_DT", width: 110, align: 'center', formatter: dateFormatter},
			             {name : "AGENT_NM", width: 100, align: 'center', cellattr: attrCell},
			             {name : "CUST_ID", width: 80, align: 'center', cellattr: attrCell},
			             {name : "CON_IP_PSN_NAME", width: 60, align: 'center',  cellattr: attrCell},
			             {name : "BIRTH_DT", width: 70, align: 'center', formatter: birthFormatter, cellattr: attrCell},
			             {name : "EST_RESULT_CD", width: 75, align: 'center', formatter: estFormatter},
			             {name : "EST_COMMENT", width: 250, align: 'left'},
			             {name : "EST_QC_NM", width: 100, align: 'center'},
			             {name : "EST_DATE", width: 120, align: 'center', formatter: dateFormatter}
			             ],
			 rowNum : 10000,
		     height: "420",
		     width : "100%",
		     cmTemplate : {sortable : false},
		     emptyrecords : "조회된 데이터가 없습니다.",
		     viewrecords: true,
			 onCellSelect : mrls_tbl_click,
			 loadComplete: function () {
 				var grid = this;
				
 				//위에서 설정해둔 id에 따라 rowspan 처리
				 $('td[rowspan="1"]', grid).each(function () {
			            var spans = $('td[rowspanid="' + this.id + '"]', grid).length + 1;
						
			            if (spans > 1) {
			                $(this).attr('rowspan', spans);
			                $(this).css("background-color", "#ffffff");
			                
			            }
			      });
				
 				// 데이터가 많아서 scroll이 생기는 경우 width 사이즈 조절해주려고
				 if(DS_MRLS_LIST.length == 0){
						$("#tbl_mrls_list tbody").append("<tr><td colspan='16' style='border-right : 1px solid #acacac;border-bottom : 1px solid #acacac;height:25px;text-align:center;'>조회된 데이터가 없습니다.</td></tr>");
					}else{		        	
					}
				
				 if(DS_MRLS_LIST.length > 15){
					$("#div_mrsls .ui-jqgrid-bdiv").width($("#div_mrsls .ui-jqgrid-view").width()+15);
					$("#div_mrsls .ui-jqgrid-hdiv").width($("#div_mrsls .ui-jqgrid-view").width()+15);
				 }else{
					 $("#div_mrsls .ui-jqgrid-bdiv").width($("#div_mrsls .ui-jqgrid-view").width()+1);
					 $("#div_mrsls .ui-jqgrid-hdiv").width($("#div_mrsls .ui-jqgrid-view").width()+1);
				 }
				 setPrevEstComplete();
			 }
		
		});
		
	}
	
	/**********************************************
	* 재조회 시 처리하는 함수
	***********************************************/
	function reloadGrid(){
		
		DS_MRLS_LIST = parent.DS_MRLS_LIST;
		
		prevCellVal = [{ cellId: undefined, value: "" }, { cellId: undefined, value: "" }, { cellId: undefined, value: "" }
		 , { cellId: undefined, value: "" }, { cellId: undefined, value: "" }];
		idx = 0;


		$("#tbl_mrls_list").jqGrid('clearGridData');
		$("#tbl_mrls_list").jqGrid('setGridParam', {data :  DS_MRLS_LIST});
		
		
		$("#tbl_mrls_list").trigger('reloadGrid');

	}

	// 엑셀 다운로드 시, 동일 센터,상담사,고객번호,고객명의 경우 다른 설계라도 하나라도 검수가 되어있으면 pass_yn = 'Y'로 처리
	function setPassYN(cellValue,options,rowObject){ 
		
		var pass_yn = "";

		var center_cd = rowObject.CENTER_CD;
		var agent_id = rowObject.AGENT_ID;
		var cust_id = rowObject.CUST_ID;
		var cust_nm = rowObject.CON_IP_PSN_NAME;

		var res = DS_EXCEL.filter(function(item){
			return item.CENTER_CD == center_cd && item.AGENT_ID == agent_id && item.CUST_ID == cust_id &&  item.CON_IP_PSN_NAME == cust_nm;
					
	 	});
			
		
		for(var i=0; i< res.length;i++){
			if(res[i].PASS_YN == 'Y') {
				pass_yn = "Y";
				break;
			}else pass_yn = "N";
		}

		return pass_yn;
		
	}
	
	function f_draw_excelGrid(){

		$("#tbl_mrls_excel").jqGrid({
			 datatype : "local",
			 data : DS_EXCEL,
			 colNames : ['No.','녹취콜수','TM콜수','검수여부','센터', '설계번호','MR여부','LS여부','조회일시', '상담원명', '고객번호', '고객명', '생년월일','심사상태','Comment','QC명','심사일자'],
			 colModel : [ 
						 {name : "No", width: 30, align: 'center'},
			             {name : "REC_YN", width: 80, align: 'center'},
			             {name : "TM_CNT", width: 80, align: 'center'},
			             {name : "PASS_YN", width: 30, align: 'center', formatter: setPassYN},
			             {name : "CENTER_NM", align: 'center',width: 80},
			             {name : "CON_ENT_DGN_NO", align: 'center',width: 80},
			             {name : "MR_YN", width: 50, align: 'center'}, 
			             {name : "LS_YN",  align: 'center', width: 50},
			             {name : "REG_DT", width: 110, align: 'center', formatter: dateFormatter},
			             {name : "AGENT_NM", width: 100, align: 'center'},
			             {name : "CUST_ID", width: 80, align: 'center'},
			             {name : "CON_IP_PSN_NAME", width: 60, align: 'center'},
			             {name : "BIRTH_DT", width: 70, align: 'center', formatter: birthFormatter},
			             {name : "EST_RESULT_CD", width: 75, align: 'center', formatter: estFormatter},
			             {name : "EST_COMMENT", width: 250, align: 'left'},
			             {name : "EST_QC_NM", width: 100, align: 'center'},
			             {name : "EST_DATE", width: 120, align: 'center', formatter: dateFormatter}
			             ],
			 rowNum : 10000,
		     height: "485",
		     width : "100%",
		     cmTemplate : {sortable : false},
		     emptyrecords : "조회된 데이터가 없습니다.",
		     viewrecords: true,
			 loadComplete: function () {
				 var grid = this;
			 }
		
		});
		
	}
	
	/**********************************************
	* 엑셀 다운로드 용 테이블 생성하는 함수
	***********************************************/
	function f_exportToExcel(){
		DS_EXCEL = parent.DS_EXCEL;
		
		$("#tbl_mrls_excel").jqGrid('clearGridData');
		$("#tbl_mrls_excel").jqGrid('setGridParam', {data :  DS_EXCEL}).trigger('reloadGrid');
		
		 $("#tbl_mrls_excel").jqGrid("exportToExcel",{
			 includeLabels : true,
			 includeGroupHeader : true,
			 includeFooter : false,
			 fileName : "MRLS_녹취리스트_"+getNow()+".xlsx",
			 maxLength : 40
		 });
	}
	
	
	/**********************************************
	* 리스트 테이블에 컬럼별 Fomat 만드는 함수들
	***********************************************/
	function dateFormatter(cellValue,options,rowObject){
		var dates = cellValue;

		if(cellValue.length == 8) dates = cellValue.substr(0,4) + '-' + cellValue.substr(4,2) + '-' + cellValue.substr(6,2);
		else if(cellValue.length == 14) dates = cellValue.substr(0,4) + '-' + cellValue.substr(4,2) + '-' + cellValue.substr(6,2) + ' ' 
									   +  cellValue.substr(8,2) + ':' +  cellValue.substr(10,2) + ':' +  cellValue.substr(12,2);
		
		return dates;
	}
	
	function birthFormatter(cellValue,options,rowObject){
		var  birth = cellValue;
		
		if(cellValue.length > 0) birth = cellValue.substr(0,4) + '-' + cellValue.substr(4,2) + '-' + cellValue.substr(6,2);

		return birth;
	}
	
	function estFormatter(cellValue,options,rowObject){
		var val = cellValue;
		
		if(cellValue != ''){
			var est_idx = parent.$DS_EST_RST._rows.filter(function(item){
				return item[1] == cellValue;
		   });
			
		   if(est_idx.length > 0) val = est_idx[0][2];
		}
		
		return val;
		
	}

	function imgFormatter(cellValue,options,rowObject){
		return '<img src="../images/icon_notice.png"/>';
	}
	
	/**********************************************
	* 미리 검수 완료된 항목 색칠하도록 해주는 함수
	**********************************************/
	function setPrevEstComplete(){
		var data = $("#tbl_mrls_list").getDataIDs();
		
		$.each(data, function(idx,rowId){
			
			var pass_yn = DS_MRLS_LIST[rowId-1].PASS_YN;
			
			if(pass_yn == 'Y'){ 
				$("#tbl_mrls_list").setRowData(rowId, false, {background: "#FFCD8B"});
			}
		});
	}
	
	/**********************************************
	* 테이블 row 클릭시 발생하는 이벤트
	***********************************************/
	function mrls_tbl_click(rowId,idx,contents,event){
		
		var cm  = $(this).jqGrid('getGridParam','colModel');
		
		// ##### 녹취 call수 0건인거 클릭 안되게 막아야되는경우 주석 해제 필요
		if(cm[idx].name == 'PLAY') {
			//if($(this).jqGrid("getCell",rowId,'REC_YN') == "0" ) {
			//	parent.xwing.Dialog.alert("녹취 DB에 매칭되는 콜이 존재하지 않습니다.");
			//	return;
			//}else{
				var ced_no = DS_MRLS_LIST[rowId-1].CON_ENT_DGN_NO;
				parent.getCallList(ced_no);
			//}	
		}
		
		var param = new Object();
		param.ced_no = DS_MRLS_LIST[rowId-1].CON_ENT_DGN_NO;
		param.est_comment = DS_MRLS_LIST[rowId-1].EST_COMMENT;
		param.est_result_cd = DS_MRLS_LIST[rowId-1].EST_RESULT_CD;
		
		parent.getEstInfo(param);

	}

</script>

</head>
<body>
	<div id="div_mrsls">
		<table id="tbl_mrls_list"></table>
		<div style="display: none;" ><table id="tbl_mrls_excel"></table></div>
	</div>
	
</body>
</html>