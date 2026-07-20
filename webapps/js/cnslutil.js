/*********************************************************************
 * Vsens용 상담유형설정 컴포넌트 생성 js
 * 상담유형 label 별도로 생성해야함.
 * component : panel(pan_cnsl, width:370, height: 25, top: , left: )
 * element 생성: g_createcnsl("pan_cnsl", 3, "N");;
 *********************************************************************/

var _CNSLobj; // 상담유형 콤보를 담을 Panel
var _cnsl_cnt = 3; // 보여질 상담유형 개수
var _muti_yn = "N"; // multiselect Y/N
var _first_code_cnsl = "%"; // first code
var _first_text = "전체"; // first codename

var _cmbCNSLObj = []; // cons combo objects
var _dsCnslObj = []; // cons dataset objects (Filter)
var _dsCnslObj_o = []; // cons dataset objects (full)
var _ds_cnsl; // parent dept dataset
var _ds_cnslName = "DS_CNSL";

var _add_fn = "";
/*********************************************************************************/
global = window.opener==null?global:window.opener.global;
/*********************************************************************************/


function g_createcnsl(obj, cnsl_cnt, add_fn, first_code, first_text) {
	_CNSLobj = Xwing.getPanel(obj);
	_cnsl_cnt = _nvl(cnsl_cnt, _cnsl_cnt);
	_add_fn = _nvl(add_fn, _add_fn);
	_first_code_cnsl = _nvl(first_code, _first_code_cnsl);
	_first_text = _nvl(first_text, _first_text);


	_ds_cnsl = Xwing.createDataset(_ds_cnslName);
	FUNC_XOBJECT.push(eval("$" + _ds_cnslName + "= Xwing.getDataset('" + _ds_cnslName + "')"));

	var param = {
			_sqlName	: "cs.com.get.cnsl"
		};
	var opt = {
		reqId : 'getCnsl',
		url : 'service::rec.rec010.get.cnsl.do',
		param : param
	};
	Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {
		_ds_cnsl.setData(oRes.DS_CNSL.column, oRes.DS_CNSL.record);
		_callback_cnsl();
	});

}

function _callback_cnsl() {
	FUNC_XOBJECT = [];

	var _dataset_o, _datasetName_o;
	var _combo, _comboName, _dataset, _datasetName;

	//위젯 그리기용
	var _left = 0;
	var _height = 25;
	var _width = 120;
	var _space = 5;

	for ( var i = 0; i < _cnsl_cnt; i++) {
		_comboName = "cmb_cnsl" + (i + 1);

		_datasetName_o = "DS_CNSL" + (i + 1) + "_O";
		_dataset_o = Xwing.createDataset(_datasetName_o);
		_dataset_o.setColumnInfo([ 'id', 'value', 'upper' ]);
		_dsCnslObj_o[i] = _dataset_o;
		FUNC_XOBJECT.push(eval("$" + _datasetName_o + "= Xwing.getDataset('" + _datasetName_o + "')"));

		_datasetName = "DS_CNSL" + (i + 1);
		_dataset = Xwing.createDataset(_datasetName);
		_dataset.setColumnInfo([ 'id', 'value', 'upper' ]);
		_dsCnslObj[i] = _dataset;
		FUNC_XOBJECT.push(eval("$" + _datasetName + "= Xwing.getDataset('" + _datasetName + "')"));

		_combo = Xwing.createCombo({
			id : _comboName,
			width : _width,
			height : _height,
			top : 0,
			left : _left,
			domaindataset : _datasetName,
			domaincodecolumn : "id",
			domaintextcolumn : "value",
			size : 20,
			change : "_cnsl_change" + i,
			multiselectable : (_muti_yn == "Y" ? "true" : "false"),
			itemall : (_muti_yn == "Y" ? "true" : "false"),
			ns : "xwing",
			xw_type : "combo",
			flexibleitem : (_comboName == "cmb_cnsl3" ? "true" : "false"),
			value: 0,
			itempadding: "0 10 0 5"
		});
		_left = (_left + _space) + _width;
		_CNSLobj.appendChild(_combo);
		_cmbCNSLObj[i] = _combo;
		FUNC_XOBJECT.push(eval("$" + _comboName + "= Xwing.getCombo('" + _comboName + "')"));
	}

	for ( var i = 0 ; i < _ds_cnsl.size(); i++) {
		var _cnsl_id = [];
		var _cnsl_value = [];
		var _cnsl_upper = [];

		var _level = _ds_cnsl.getValue(i,'CNSL_LVL');

		for ( var j = 0; j < _cnsl_cnt; j++) {
			if(_level==(j+1)){
				_cnsl_id[j] = _ds_cnsl.getValue(i, "CNSL" + (j+1) + "_CD");
				_cnsl_value[j] = _ds_cnsl.getValue(i, "CNSL" + (j+1) + "_NM");
				if(_level == 3){
					_cnsl_value[j] += "(" + _ds_cnsl.getValue(i, "CNSL" + (j+1) + "_CD") + ")";
				}
				_cnsl_upper[j] = _ds_cnsl.getValue(i, "CNSL" + (j == 0 ? 0 : j) + "_CD");
				if (!_isNull(_cnsl_id[j]) && !_isEmpty(_cnsl_id[j])) {
					_dsCnslObj_o[j].addRow([ _cnsl_id[j], _cnsl_value[j], _cnsl_upper[j] ]);
				}
			}
		}
	}

	_dsCnslObj_o[0].insertRow(0, [ _first_code_cnsl, _first_text, _first_code_cnsl ]);
	_dsCnslObj[0].copyFrom(_dsCnslObj_o[0]);

	//_common_cnsl_change(0);
	//_cmbCNSLObj[0].setValue(_first_code_cnsl);
	_cmbCNSLObj[0].setItemindex("0");
	//특정화면에서 호출시.
	if(_add_fn == "msa060"){
		fn_cnslutilCall();
	}

}

function _common_cnsl_change(iLevel, event) {
	//0,1,2
	var _dataset_parent = _dsCnslObj_o[iLevel];
	var _combo_parent = _cmbCNSLObj[iLevel];

	var _dataset_child = _dsCnslObj_o[iLevel + 1];
	var _combo_child = _cmbCNSLObj[iLevel + 1];

	//하위 콤보 데이터셋은 초기화
	for(var idx =iLevel; idx < (_cnsl_cnt-1); idx++ ){
		_dsCnslObj[(idx + 1)].clearData();
	}

	if(iLevel<2){
		_dsCnslObj[(iLevel + 1)].setData(_dataset_child.getColumnInfo(),_dataset_child.getData("[upper=='"+_combo_parent.getValue()+"']"));
		//_dataset_child.clearData();

		_dsCnslObj[(iLevel + 1)].insertRow(0, [ _first_code_cnsl, _first_text, _first_code_cnsl ]);
	}

	for(var idx =iLevel; idx < (_cnsl_cnt-1); idx++ ){
		_cmbCNSLObj[(idx + 1)].setValue(_first_code_cnsl);
	}
}

function _cnsl_change0(e) {
	_common_cnsl_change(0, e);
	if (typeof (cmb_cnsl0_change) == "function") {
		cmb_cnsl0_change(e);
	}
}
function _cnsl_change1(e) {
	_common_cnsl_change(1, e);
	if (typeof (cmb_cnsl1_change) == "function") {
		cmb_cnsl1_change(e);
	}
}
function _cnsl_change2(e) {
	_common_cnsl_change(2, e);
	if (typeof (cmb_cnsl2_change) == "function") {
		cmb_cnsl2_change(e);
	}
}
function _cnsl_change3(e) {
	_common_cnsl_change(3, e);
	if (typeof (cmb_cnsl3_change) == "function") {
		cmb_cnsl3_change(e);
	}
}
function _cnsl_change4(e) {
	_common_cnsl_change(4, e);
	if (typeof (cmb_cnsl4_change) == "function") {
		cmb_cnsl4_change(e);
	}
}

function g_cnsl_cd(){
	var cnsl = "";
	if(!_isEmpty(_cmbCNSLObj[2].getValue()) & _first_code_cnsl !=_cmbCNSLObj[2].getValue() ){
		cnsl = _cmbCNSLObj[2].getValue();
	}else if(!_isEmpty(_cmbCNSLObj[1].getValue()) & _first_code_cnsl !=_cmbCNSLObj[1].getValue() ){
		cnsl = _cmbCNSLObj[1].getValue();
	}else if(!_isEmpty(_cmbCNSLObj[0].getValue()) & _first_code_cnsl !=_cmbCNSLObj[0].getValue() ){
		cnsl = _cmbCNSLObj[0].getValue();
	}

	return cnsl;
}

function g_cnslInfo(bQuote) {
	bQuote = _nvl(bQuote, false);
	var cnsl = {
		cnsl1_cd : g_consValue(0, bQuote),
		cnsl2_cd : g_consValue(1, bQuote),
		cnsl3_cd : g_consValue(2, bQuote),
		cnsl4_cd : g_consValue(3, bQuote),
		cnsl5_cd : g_consValue(4, bQuote)
	};
	return cnsl;
}

function g_cnslSet(REGU_SET_CD) {
	for(var i=0;i<REGU_SET_CD.length;i++){
		_comboName = "cmb_cnsl" + (i + 1);
		Xwing.getCombo(_comboName).setValue(REGU_SET_CD.substr(0,i+1));
		if(i <= REGU_SET_CD.length-1){
			_common_cnsl_change(i);
		}else{
			Xwing.getCombo(_comboName).setValue(_first_code_cnsl);
		}
	}
	/*
	if(REGU_SET_CD.length == 3){
	$cmb_cnsl1.setValue(REGU_SET_CD.substr(0,1));
	cmb_cnsl1_change();
	$cmb_cnsl2.setValue(REGU_SET_CD.substr(0,2));
	cmb_cnsl2_change();
	$cmb_cnsl3.setValue(REGU_SET_CD);
	}
	*/
}

function g_cnslnmSet(REGU_SET_NM){
	var nm1 = REGU_SET_NM.substring(0,REGU_SET_NM.indexOf("^"));
	var nm2 = REGU_SET_NM.substring(REGU_SET_NM.indexOf("^")+1,REGU_SET_NM.lastIndexOf("^"));
	var nm3 = REGU_SET_NM.substring(REGU_SET_NM.lastIndexOf("^")+1);

	var obj1 = Xwing.getDataset("DS_CNSL1_O");
	var cd1 = obj1.getValue(obj1.indexOfRow("value",nm1),"id");
	$cmb_cnsl1.setValue(cd1);
	_common_cnsl_change(0);

	var obj2 = Xwing.getDataset("DS_CNSL2");
	var cd2 = obj2.getData("[value*='"+nm2+"']")[0][0];
	$cmb_cnsl2.setValue(cd2);
	_common_cnsl_change(1);

	var obj3 = Xwing.getDataset("DS_CNSL3");
	var cd3 = obj3.getData("[value*='"+nm3+"']")[0][0];
	$cmb_cnsl3.setValue(cd3);
	_common_cnsl_change(2);
}


function g_cnslToParam(sPattern, oData, bQuote) {
	bQuote = _nvl(bQuote, false);
	oData[(sPattern + "cnsl1_cd").toUpperCase()] = g_cnslValue(0, bQuote);
	oData[(sPattern + "cnsl2_cd").toUpperCase()] = g_cnslValue(1, bQuote);
	oData[(sPattern + "cnsl3_cd").toUpperCase()] = g_cnslValue(2, bQuote);
	oData[(sPattern + "cnsl4_cd").toUpperCase()] = g_cnslValue(3, bQuote);
	oData[(sPattern + "cnsl5_cd").toUpperCase()] = g_cnslValue(4, bQuote);
}

function g_cnslObj(iLevel) {
	iLevel = _nvl(iLevel, -1);
	var cnsl = {
		cnsl1 : _nvl(_cmbCNSLObj[0], null),
		cnsl2 : _nvl(_cmbCNSLObj[1], null),
		cnsl3 : _nvl(_cmbCNSLObj[2], null),
		cnsl4 : _nvl(_cmbCNSLObj[3], null),
		cnsl5 : _nvl(_cmbCNSLObj[4], null)
	};
	return iLevel == -1 ? cnsl :
		   iLevel > 6 ? cnsl : eval("cnsl" + iLevel);
}

function g_cnslValue(iLevel, bQuote) {
	bQuote = _nvl(bQuote, false);
	var val = _undefined_replace((_isNull(_cmbCNSLObj[iLevel])||_cmbCNSLObj[iLevel].getAllCheck()) ? "" : _decode(_first_code_yn, "Y", _cmbCNSLObj[iLevel].getValue(), _cmbCNSLObj[iLevel].getValue().replace(eval("/" + _first_code_cnsl + "/gi"))));
	return _isEmpty(val)?val:_decode(bQuote, true, _quote(val), val);
}


/*******************************************************************************
 * UTIL
 ******************************************************************************/
function _nvl() {
	if (_isNull(arguments) == true)
		return null;
	for ( var i = 0; i < arguments.length; i++) {
		if (_isNull(arguments[i]) == false)
			return arguments[i];
	}
	return null;
}

function _evl() {
	if (_isNull(arguments) == true)
		return "";
	for ( var i = 0; i < arguments.length; i++) {
		if (_isEmpty(arguments[i]) == false)
			return arguments[i];
	}

	return "";
}

function _decode() {
	var i = 1;
	for (; i < arguments.length - 1;) {
		if (arguments[0] == arguments[i])
			return arguments[i + 1];
		i += 2;
	}
	return arguments[i];
}

function _substr() {
	var arg = arguments;

	var val = arg[0];
	if (typeof (val) != "string") {
		val = val.toString();
	}

	var s = arg[1];
	var e = _nvl(arg[2], val.length);
	return val.substr(s, e);
}

function _isNull(obj) {
	return (obj == null || typeof (obj) == "undefined");
}

function _isEmpty(obj) {
	return (_isNull(obj) == true || (typeof (obj) == "string" && obj.length == 0));
}

function _global(obj) {
	var MAX_DEPTH = 10;
	var o = obj;
	var p = "parent.";
	var g = "global.";
	var r = null;
	for ( var i = 0; i < MAX_DEPTH; i++) {
		if ((r = eval(p + o)) != undefined) {
			break;
		}

		if ((r = (eval(p+'global') ? eval(p + g + o) : undefined ) ) != undefined) {
			break;
		}
		p += "parent.";
	}
	return r;
}

function _quote(sVal) {
	var o = sVal.replace(/ /gim, "");
	o = o.split(",");

	for(var i = 0; i < o.length; i++) {
		if (typeof(o[i]) == "string" && o[i] == "undefined") {
			o[i] = "";
		}
		o[i] = "'" + o[i] + "'";
	}

	return o.join(",");
}

function _undefined_replace(sVal) {
	var o = sVal.replace(/ /gim, "");
	o = o.split(",");

	for(var i = 0; i < o.length; i++) {
		if (typeof (o[i]) == "string" && o[i] == "undefined") {
			o[i] = "";
		}
	}

	return (_substr(o.join(","), 0, 1) == ",") ? _substr(o.join(","), 1) : o.join(",");
}
