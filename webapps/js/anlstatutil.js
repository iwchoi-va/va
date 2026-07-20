﻿/*********************************************************************
 * 분석진행상태 컴포넌트 생성 js
 * ex) 
 * component : panel(id:pnl_anlstat, width:315, height: 25, top: 15)
 * element 생성: g_createAnlStat("pnl_anlstat", auth); //추후에 권한 들어가겠지?
 * 분석상태 id : Xwing.getCombo("cmb_anl_stat").getValue()
 *********************************************************************/

var _STATobj; 				  // 조회 기간을 담을 Panel
var _mode;                 // 기간구분 선택 모드
var FUNC_XOBJECT = [];    // XwingObject()

//위젯 그리기용
var _left = 0; 
var defalut_left = 0;
//var _top = 0; 
var _height = 25; 
var _space = 5;

//위젯명(접근 객체명)
var _statName = "cmb_anl_stat";

// Dataset 관련 변수
var _dsStatName = "DS_ANL_STAT"; 	// 분석상태 데이터셋 명
var _ds_stat ; 					// 분석상태 데이터셋 객체


//////////////////////////////////////////기간 구분 생성////////////////////////////////////////////////

function g_createAnlStat(obj) { 

	_STATobj = Xwing.getPanel(obj);
	
	// 분석상태 데이터셋 생성
	_ds_stat = Xwing.createDataset(_dsStatName);
	_ds_stat.setColumnInfo([ 'CODEID', 'ETC1', 'CODENAME' ]);
	_ds_stat.addRow(['','전체']);
	
	FUNC_XOBJECT.push(eval("$" + _dsStatName + "= Xwing.getDataset('" + _dsStatName + "')"));

	// 분석상태
	var label = Xwing.createLabel({
		id : 'label1',
		width : 60,
		height : _height,
		top : 0, 
		left : 0,
		styles : 'label_subtitle',
		valign : 'middle',
		halign : 'right',
		value : '분석상태'
	});
	_STATobj.appendChild(label);
	_left += (eval("Xwing.getLabel('label1').getWidth()") + _space);
	
	//분석상태 콜보박스 생성
	var _combo = Xwing.createCombo({
		id : _statName,
		width : 73,
		height : _height,
		top : 0,
		left : 65,
		domaindataset : _dsStatName,
		domaincodecolumn : "ETC1",
		domaintextcolumn : "CODENAME",
		size : 25,
		change : "_anl_stat_change",
		ns : "xwing",
		xw_type : "combo"
	});
	_STATobj.appendChild(_combo);
	
	FUNC_XOBJECT.push(eval("$" + _statName + "= Xwing.getCombo('" + _statName + "')"));
	_left += eval("$"+_statName+".getWidth()")+_space;
	defalut_left = _left;
	
	var g_tem;
	var g_tem2;
	if(opener == null){
		g_tem = global("G_USER_ID");
		g_tem2 = global("G_USER_GRADE");
	}else{
		g_tem = opener.global.G_USER_ID;
		g_tem2 = opener.global.G_USER_GRADE;
	}
	//권한, 등록자 id 
	var param = {
			cmd : "getCodes",
			codeType : "VSS180",
			useyn : "Y"
		};
		var opt = {
			reqId : "f_codebook_sel",
			url : "service::cs.com.codeutil.do",
			param : param
		};

		Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {	
			_ds_stat.setData(oRes.VSS180.column, oRes.VSS180.record);
			_ds_stat.insertRow(0, ['VSS180','','전체','','','','','','','','']);
			eval("$" + _statName + ".setItemIndex('0')");
			
		});
}
		
	
function g_setEnabledGrp(bool){
	Xwing.getWidget(_statName).setEnabled(bool);
}

function g_analStatToParam(sPattern, oData, bQuote) {
	bQuote = _nvl(bQuote, false);
	oData[(sPattern + "ANL_STAT").toUpperCase()] = eval("$cmb_anl_stat.getValue()");
}

function _anl_stat_change(){
}
