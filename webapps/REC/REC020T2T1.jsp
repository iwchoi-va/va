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


</style>
<script type="text/javascript">

$(document).ready(function(){

	f_draw_recGrid();
});
	
	var DS_REC_LIST = [];

	/**********************************************
	* MR/LS용 테이블 그리는 함수
	**********************************************/
	function f_draw_recGrid(){
		 
		$("#tbl_rec_list").jqGrid({
			 datatype : "local",
			 data : DS_REC_LIST,
			 colNames : ['No', '청취','센터' ,'설계번호','녹취일시','MR여부','LS여부','상담원명', '고객명'],
			 colModel : [ 
						 {name : "No", width: 35, align: 'center'},
						 {name : "PLAY", width: 30, align: 'center',formatter : imgFormatter},
			             {name : "SpCode", align: 'center',width: 70, formatter : setCenterName},
			             {name : "TPANO", align: 'center',width: 85, formatter : setTpano},
			             {name : "RecStartDT", align: 'center',width: 115, formatter: dateFormatter},
			             {name : "MR_YN", width: 45, align: 'center'}, 
			             {name : "LS_YN",  align: 'center', width: 45},
			             {name : "AgentName", width: 70, align: 'center'},
			             {name : "CustomerName", width: 60, align: 'center'},
			             ],
			 rowNum : 10000,
		     height: "620",
		     width : "100%",
		     cmTemplate : {sortable : false},
		     emptyrecords : "조회된 데이터가 없습니다.",
		     viewrecords: true,
			 onCellSelect : rec_tbl_click,
			 loadComplete: function () {
 				var grid = this;
				
				 if(DS_REC_LIST.length == 0){
						$("#tbl_rec_list tbody").append("<tr><td colspan='9' style='border-right : 1px solid #acacac;border-bottom : 1px solid #acacac;height:25px;text-align:center;'>조회된 데이터가 없습니다.</td></tr>");
				 }
				 
				 if(DS_REC_LIST.length > 15){
					$("#div_rec .ui-jqgrid-bdiv").width($("#div_rec .ui-jqgrid-view").width()+15);
					$("#div_rec .ui-jqgrid-hdiv").width($("#div_rec .ui-jqgrid-view").width()+15);
				 }else{
					 $("#div_rec .ui-jqgrid-bdiv").width($("#div_rec .ui-jqgrid-view").width()+1);
					 $("#div_rec .ui-jqgrid-hdiv").width($("#div_rec .ui-jqgrid-view").width()+1);
				 }
			 }
		
		});
		
	}
	
	/**********************************************
	* 재조회 시 처리하는 함수
	***********************************************/
	function reloadGrid(){
	
		DS_REC_LIST = parent.DS_REC_LIST;

		$("#tbl_rec_list").jqGrid('clearGridData');
		$("#tbl_rec_list").jqGrid('setGridParam', {data :  DS_REC_LIST}).trigger('reloadGrid');

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
	function imgFormatter(cellValue,options,rowObject){
		return '<img src="../images/icon_notice.png"/>';
	}
	
	function setCenterName(cellValue,options,rowObject){
		var val = cellValue;
		
		if(cellValue != ''){
			var est_idx = parent.$DS_SYS170._rows.filter(function(item){
				return item[6] == cellValue;
		   });
			
		   val = est_idx[0][2];
		}
		
		return val;
	}
	
	function setTpano(cellValue,options,rowObject){
		var val = cellValue;

		if(val.length > 0){
			var tpano = val.split(";");
			var temp = "";
			
			for(var i =0 ;i< tpano.length ; i++){
				if(tpano[i].length == 17) temp += tpano[i].substr(5) + ", ";
				else if(tpano[i].length > 0) temp += tpano + ",";
			}

			val = temp.slice(0,-2);
		}
		
		return String(val);
	}
	
	/**********************************************
	* 테이블 row 클릭시 발생하는 이벤트
	***********************************************/
	function rec_tbl_click(rowId,idx,contents,event){
		
		var cm  = $(this).jqGrid('getGridParam','colModel');
		
		if(cm[idx].name == 'PLAY') {
			
			var ucid = DS_REC_LIST[rowId-1].FILEID;
			var rec_start_time = DS_REC_LIST[rowId-1].RecStartDT;
			var file_path = DS_REC_LIST[rowId-1].FILE_PATH;
			parent.rec_tbl_click(ucid, rec_start_time, file_path);
				
		}
	}

</script>

</head>
<body>
	<div id="div_rec">
		<table id="tbl_rec_list"></table>
	</div>
	
</body>
</html>