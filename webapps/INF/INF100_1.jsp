<%@page import="com.locus.jedi.transfer.ListParam"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>QC 평가표</title>
<script type='text/javascript' src='../chart/lib/jquery-1.11.1.js'></script>
<link rel="stylesheet" type="text/css" href="../css/oba010t5.css">
<script src='../js/scriptutil.js' type="text/javascript" charset="UTF-8"></script>
<script src="../js/ageutil.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/trirand/i18n/grid.locale-kr.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/trirand/src/jquery.jqGrid.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/css/trirand/ui.jqgrid.css" />

<style>
.pointer {cursor : pointer;}
</style>
<script type="text/javascript">

$(document).ready(function(){

	setGrid();
});

	function setGrid(){
		
		$("#tbl_qc_opinion").jqGrid({
			 datatype : "local",
			 colNames : ['청취', 'Stt완료여부','Sttflag', '녹취일시','경로', '파일명', '요청자', '업로드일시', '올린사람','Stt에러내용','파일ID'],
			 colModel : [ 
						{name : "icon", width: 70, align: 'center',formatter : imgFormatter,cellattr:pointer},
						{name : "STT_FLAG_NM", width: 70, align: 'center'},
			             {name : "STT_FLAG", width: 70, align: 'center',hidden:true},
			             {name : "REC_START_TIME", align: 'center',width: 150,formatter:"date",formatoptions:dateFormatter()},
			             {name : "ORGFILEFULLPATH", align: 'center',width: 150,hidden:true},
			             {name : "ORGFILENAME", width: 200, align: 'center'}, 
			             {name : "REQ_ID",  align: 'center', width: 100},
			             {name : "REG_DATE", width: 150, align: 'center',formatter:"date",formatoptions:dateFormatter()},
			             {name : "REG_ID", width: 100, align: 'center'},
			             {name : "STT_ERR_CD", width: 150, align: 'center'},
			             {name : "FILEID", width: 150, align: 'center',hidden:true}
			             ],
			 rowNum : 10000,
		     height: "470",
		     width : "100%",
		     emptyrecords : "조회된 데이터가 없습니다.",
		     viewrecords: true,
			 onCellSelect : opinion_tbl_click,
			 loadComplete: function (data) { 
			        //var grid = this;
			    
				 if(data.rows.length == 0){
						//$("#tbl_qc_opinion tbody").append("<tr><td colspan='6' align='center'>조회된 데이터가 없습니다.</tr>");
						$("#tbl_qc_opinion tbody").append("<tr><td colspan='8' style='border-right : 1px solid #acacac;border-bottom : 1px solid #acacac;height:25px;text-align:center;'>조회된 심사항목 데이터가 없습니다.</td></tr>");
					}else{		        	
						$("#tbl_qc_opinion tbody tr[id=1]").addClass("ui-state-highlight");
					}

					$("#div_history .ui-jqgrid-bdiv").width($("#div_history .ui-jqgrid-bdiv").width()+1);
					$("#div_history .ui-jqgrid-hdiv").width($("#div_history .ui-jqgrid-hdiv").width()+1);
			   }
		});
		
	}
	
	function pointer(cellvalue,option,rowobject){
		return "class='pointer'";
	}
	
	function dateFormatter(){
		return {srcformat:"ISO8601Long",newformat:"Y-m-d H:i:s"};
	}
	
	function imgFormatter(cellValue,options,rowObject){
		return '<img src="../images/icon_notice.png"/>';
	}
	
	function search(params){
		//console.log(params);
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
		
		var url = host + "inf.inf100.sel.do";
		//inf.inf100.getRecBatch.sel
		//alert(params.USER_ID);
		var param = {
				_sqlName : "inf.inf100.getRecBatch.sel",
				searchRecStartDateFrom : params.searchRecStartDateFrom,
				searchRecStartDateTo : params.searchRecStartDateTo,
				searchRegDateFrom : params.searchRegDateFrom ,
				searchRegDateTo : params.searchRegDateTo ,
				searchReqId  : params.USER_ID ,
				searchOrgFileNm  : params.searchOrgFileNm,
				searchChkSttFlag :params.searchChkSttFlag ,
			};
		$.ajax({
			url : url,
			type : 'POST',
			data : param,
			success: function(data){
				//console.log(data);
				var d = JSON.parse(data);
				var r = d.DS_LIST;
				
				var rows = Array();
				
				for(var i = 0 ; i < r.record.length ; i ++){
					var row = new Object();
					for(var j = 0 ; j < r.column.length ; j ++){
						
						//console.log(r.column[j] +" = "+ r.record[i][j]);
						row[r.column[j]] = r.record[i][j];
						//console.log(row);
					}
					//console.log("--------------");
					rows.push(row);
				}
				//console.log(rows);
				$("#tbl_qc_opinion").jqGrid("clearGridData");
				
				for(var i in rows) $("#tbl_qc_opinion").jqGrid('addRowData',i+1,rows[i]);
				
				$("#tbl_qc_opinion").trigger("reloadGrid");
				
				
			},
			error : function(xhr,stat,err){
				
			}
		});
		
		
	}
	
	function opinion_tbl_click(rowId,idx,contents,event){
		
		var cm  = $(this).jqGrid('getGridParam','colModel');
		
		if(cm[idx].name != 'icon') return;
		
		if($(this).jqGrid("getCell",rowId,'STT_FLAG') != "Y" ) return;
		
		var param = new Object();
		
		param.file_path = $(this).jqGrid("getCell",rowId,'ORGFILEFULLPATH');
		param.ucid = $(this).jqGrid("getCell",rowId,'FILEID');
		param.contactId = $(this).jqGrid("getCell",rowId,'FILEID');
		param.batch_yn = "Y";
		param.rec_key = $(this).jqGrid("getCell",rowId,'FILEID');
		
		parent.grid_click(param);
	}

</script>

</head>
<body>
	<div id="div_history">
		<table id="tbl_qc_opinion"></table>
	</div>
</body>
</html>