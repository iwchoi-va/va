<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="../msens_icon.ico" />
<script type='text/javascript' src='../chart/lib/jquery-1.11.1.js'></script>
<script src='../js/scriptutil.js' type="text/javascript" charset="UTF-8"></script>
<script src='../js/oba010p5_util.js' type="text/javascript" charset="UTF-8"></script>
<script src="../js/jqgrid/js/trirand/i18n/grid.locale-kr.js" type="text/javascript"></script>
<script src="../js/jqgrid/js/trirand/src/jquery.jqGrid.js" type="text/javascript"></script>
<!-- <script src="../js/grid/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script> -->
<link rel="stylesheet" type="text/css" href="../css/oba010t5.css">
<link rel="stylesheet" type="text/css" media="screen" href="../js/jqgrid/css/trirand/ui.jqgrid.css" />

<title>선심사_QA통판_변경이력</title>

<script type="text/javascript">
	
	var htid = "";
	var htdgree= "";
	var est_item_ver = "";
	var now_date ="";
	
	var host = "";
	
	var DS_MOD_HIST = []; // 변경이력 테이블
	
	function page_load(){
		
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
		
		var query = location.search.substring(1);
		
		var parameters = {};
		var keyValues = query.split(/&/);
		for (var prop in keyValues) {
		    var keyValuePairs = keyValues[prop].split(/=/);
		    var key = keyValuePairs[0];
		    var value = keyValuePairs[1];
		    parameters[key] = value;
		}
		
		htid = parameters['HTID'];
		htdgree = parameters['HTDGREE'];
		est_item_ver = parameters['EST_ITEM_VER'];
		
		//※ 크롬 테스트용임
		//host = " http://10.51.206.59:8090/MSENS/";
		
		//htid ='13776';
		//htdgree = '2';
		//est_item_ver = '4';
		
		now_date = g_toDate();
		
		f_getEstModHistory();
		
	}
	
	// 변경 이력 히스토리 조회
	function f_getEstModHistory(){
		
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST" , host + "oba.oba010t5.getEstimationInfo.do" , true);
	
		var param = "cmd=estmodHistory&est_item_ver="+est_item_ver+"&htid="+htid+"&htdgree="+htdgree;
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		
		xhr.onreadystatechange = function() {			
	        if(xhr.readyState == 4) {
	        	if(xhr.status == 200) {
	        		var responseText = JSON.parse(xhr.responseText);
	        		DS_MOD_HIST = JSON.parse(responseText.DS_MOD_HIST);
	        		
	        		draw_historyTable();
	        		
	        	}        	
	        }
	    };
	    xhr.send(param);	
	}
	
	function draw_historyTable(){
		 var prevCellVal = [{ cellId: undefined, value: undefined} ,{ cellId: undefined, value: undefined },{ cellId: undefined, value: undefined }];
		 var idx = 0;
		 
		 var attrCell = function (rowId, val, rawObject, cm, rdata) {
			 var result;

			 switch(cm.name){
				 case "HTDGREE" :
					 idx = 0;
					 break;
				 case "ESTSCATNM":
					 idx = 1;
					 break;
				 case "ESTTCATNM" :
					 idx = 2;
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
        
		 $("#tbl_history").jqGrid({
			 datatype : "local",
			 data : DS_MOD_HIST,
			 colNames : ['심사회차', 'TYPE1', 'TYPE2', '항목', '변경전', '변경후', '변경일시'],
			 colModel : [{name : 'HTDGREE', width: 55, align: 'center', cellattr: attrCell}, 
			             {name : "ESTSCATNM", width: 70, align: 'center', cellattr: attrCell},
			             {name : "ESTTCATNM", align: 'center', width: 100, cellattr: attrCell},
			             {name : "TOT_NO", width: 30, align: 'center'}, 
			             {name : "PRE_RST_CD",  align: 'center', width: 120}, 
			             {name : "MOD_RST_CD", width: 120, align: 'center'},
			             {name : "REG_DT", width: 120, align: 'center', formatter : expr_date_format}],
			 rowNum : 10000,
	 	     height: "250px",
	 	     emptyrecords : "조회된 데이터가 없습니다.",
	 	     viewrecords: true,
	 	     //   cellEdit: true,
	 	     //  autowidth: true,
			 //onCellSelect : opinion_tbl_click,
			 gridComplete: function () {
			      var grid = this;

			       $('td[rowspan="1"]', grid).each(function () {
			            var spans = $('td[rowspanid="' + this.id + '"]', grid).length + 1;

			            if (spans > 1) {
			                $(this).attr('rowspan', spans);
			            }
			       });
			       
			       $("#div_mod_history .ui-jqgrid-bdiv").width($("#div_mod_history .ui-jqgrid-bdiv").width()+1);
			       $("#div_mod_history .ui-jqgrid-hdiv").width($("#div_mod_history .ui-jqgrid-hdiv").width()+1);
			        
			}
		 });
		 
		// $("#tbl_history").trigger('reloadGrid');
	}
	
	 function expr_date_format(cellValue, options, rowdata, action){
		 var result = "";
		 
		 if(cellValue != null){
			 result = cellValue.substr(0,4) + "-" + cellValue.substr(4,2) + "-" + cellValue.substr(6,2) 
			  + " " + cellValue.substr(8,2) + ":" + cellValue.substr(10,2) + ":"+ cellValue.substr(12,2); 
		 }
		 
		 return result;
		 
	 }

	
</script>
</head>
<body onload="page_load()">
	<div id="div_title">
		<div id="title">선심사-QA통판_변경이력</div>
	</div>
	<div id="div_mod_history">
		<table id="tbl_history"></table>
	</div>
</body>
</html>