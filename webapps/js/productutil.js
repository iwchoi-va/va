/*********************************************************************
 * Vsens용 상품군설정 컴포넌트 생성 js 
 * 상품군 label 별도로 생성해야함. 
 * component : panel(pan_product, width:205, height: 25, top: , left: )
 * element 생성: g_createproduct("pan_product", 3, "N");;
 *********************************************************************/

var _PRODUCTobj; // 상품 콤보를 담을 Panel
var _product_cnt = 2; // 보여질 상품 개수
var _muti_yn = "N"; // multiselect Y/N
var _first_code_prod = "%"; // first code
var _first_text = "전체"; // first codename

var _cmbPRODUCTObj = []; // cons combo objects
var _dsPRODUCTObj = []; // cons dataset objects (Filter)
var _dsPRODUCTObj_o = []; // cons dataset objects (full)
var _ds_product; // parent dept dataset
var _ds_istp; // parent dept dataset
var _ds_productName = "DS_PRODUCT";
var _ds_istpName = "DS_ISTP";

var _add_fn = "";
/*********************************************************************************/
global = window.opener==null?global:window.opener.global;
/*********************************************************************************/


function g_createproduct(obj, product_cnt, add_fn, first_code, first_text,multi_yn) {
	_PRODUCTobj = Xwing.getPanel(obj);
	_product_cnt = _nvl(product_cnt, _product_cnt);
	_add_fn = _nvl(add_fn, _add_fn);
	_first_code_prod = _nvl(first_code, _first_code_prod);
	_first_text = _nvl(first_text, _first_text);
	_muti_yn = _nvl(multi_yn, _muti_yn);
	

	_ds_product = _isNull(_global("$DS_PRODUCT"))?window.opener.$DS_PRODUCT:_global("$DS_PRODUCT");	
	_ds_istp = _isNull(_global("$DS_ISTP"))?window.opener.$DS_ISTP:_global("$DS_ISTP");
	
	if(_ds_product.size() > 0 && _ds_istp.size() > 0){
		_callback_product();
	}else{
		
		//_ds_product = Xwing.createDataset(_ds_productName);
		//FUNC_XOBJECT.push(eval("$" + _ds_productName + "= Xwing.getDataset('" + _ds_productName + "')"));
		
		//_ds_istp = Xwing.createDataset(_ds_istpName);
		//FUNC_XOBJECT.push(eval("$" + _ds_istpName + "= Xwing.getDataset('" + _ds_istpName + "')"));
		
		var param = {
				_sqlName	: "cs.com.getProduct.sel, cs.com.getistpcd.sel"
			};
		var opt = {
			reqId : 'getproduct',
			url : 'service::com.common.product.sel.do',
			param : param
		};
		Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {		
			_ds_product.setData(oRes.DS_PRODUCT.column, oRes.DS_PRODUCT.record);
			_ds_istp.setData(oRes.DS_ISTP.column, oRes.DS_ISTP.record);
			
			//var _top_product = _isNull(_global("$DS_PRODUCT"))?window.opener.$DS_PRODUCT:_global("$DS_PRODUCT");
			//var _top_istp = _isNull(_global("$DS_ISTP"))?window.opener.$DS_ISTP:_global("$DS_ISTP");
			
			//_top_product.copyFrom(_ds_product); //최초 load시에 main쪽 dataset에 세팅
			//_top_istp.copyFrom(_ds_istp);
			
			_callback_product();
		});
	}
	
	
	
}

function _callback_product() {
	FUNC_XOBJECT = [];
	
	var _combo, _comboName, _dataset, _datasetName;
	
	//위젯 그리기용
	var _left = 0; 
	var _height = 25; 
	var _space = 5;
	
	var label = Xwing.createLabel({
		id : 'lbl_istp_cd',
		width : 55,
		height : _height,
		top : 0, 
		left : _left,
		styles : 'label_subtitle',
		valign : 'middle',
		halign : 'right',
		value : '보험종목'
	});
	_PRODUCTobj.appendChild(label);
	_left += (eval("Xwing.getLabel('lbl_istp_cd').getWidth()") + _space);
	
	
	_datasetName = "DS_ISTP";
	_comboName = "cmb_istp_cd";
	_dataset = Xwing.createDataset(_datasetName);
	_dataset.setColumnInfo([ 'ISTP_CD', 'ISTP_NM']);
	_dataset.copyFrom(_ds_istp);
	
	_combo = Xwing.createCombo({
		id : _comboName,
		width : 100,
		height : _height,
		top : 0,
		left : _left,
		domaindataset : _datasetName,
		domaincodecolumn : "ISTP_CD",
		domaintextcolumn : "ISTP_NM",
		size : 20,
		change : "_istp_change",
		multiselectable : (_muti_yn == "Y" ? "true" : "false"),
		itemall : (_muti_yn == "Y" ? "true" : "false"),
		ns : "xwing",
		xw_type : "combo",
		value: 0
	});
	
	_left = (_left + _space) + eval("Xwing.getLabel('cmb_istp_cd').getWidth()");
	_PRODUCTobj.appendChild(_combo);
	FUNC_XOBJECT.push(eval("$" + _comboName + "= Xwing.getCombo('" + _comboName + "')"));
	
	_dataset.insertRow(0, [ _first_code_prod, _first_text ]);
	_combo.setValue(_first_code_prod);
	
	var label = Xwing.createLabel({
		id : 'lbl_prod_cd',
		width : 30,
		height : _height,
		top : 0, 
		left : _left,
		styles : 'label_subtitle',
		valign : 'middle',
		halign : 'right',
		value : '상품'
	});
	_PRODUCTobj.appendChild(label);
	_left += (eval("Xwing.getLabel('lbl_prod_cd').getWidth()") + _space);
	
	
	_datasetName = "DS_PRODUCT";
	_comboName = "cmb_prod_cd";
	
	_dataset = Xwing.createDataset(_datasetName);
	_dataset.copyFrom(_ds_product);
	_dataset.setColumnInfo([ 'ISTP_CD', 'ISTP_NM', 'PROD_CD', 'PROD_NM' ]);
	FUNC_XOBJECT.push(eval("$" + _datasetName + "= Xwing.getDataset('" + _datasetName + "')"));
	
	_combo = Xwing.createCombo({
		id : _comboName,
		width : 270,
		height : _height,
		top : 0,
		left : _left,
		domaindataset : _datasetName,
		domaincodecolumn : "PROD_CD",
		domaintextcolumn : "PROD_NM",
		size : 20,
		multiselectable : (_muti_yn == "Y" ? "true" : "false"),
		itemall : (_muti_yn == "Y" ? "true" : "false"),
		flexibleitem : "false",
		ns : "xwing",
		xw_type : "combo",
		value: 0
	});
	
	_left = (_left + _space) + eval("Xwing.getLabel('cmb_prod_cd').getWidth()");
	_dataset.insertRow(0, [ _first_code_prod, _first_text,  _first_code_prod, _first_text]);
	_combo.setValue(_first_code_prod);
	_PRODUCTobj.appendChild(_combo);
	
	FUNC_XOBJECT.push(eval("$" + _comboName + "= Xwing.getCombo('" + _comboName + "')"));
	
}


function _istp_change(e) {
	var v_ds_istp = eval("Xwing.getDataset('DS_ISTP')");
	var v_ds_prod_cd = eval("Xwing.getDataset('DS_PRODUCT')");
	
	var v_istp_cd = eval("Xwing.getCombo('cmb_istp_cd')");
	var v_prod_cd = eval("Xwing.getCombo('cmb_prod_cd')");
	
	v_ds_prod_cd.clearData();
	
	if(v_istp_cd.getValue() != _first_code_prod){
		v_ds_prod_cd.setData(_ds_product.getData("[ISTP_CD=='"+v_istp_cd.getValue()+"']"));
	}else{
		v_ds_prod_cd.copyFrom(_ds_product);
	}
	
	v_ds_prod_cd.insertRow(0, [ _first_code_prod, _first_text,  _first_code_prod, _first_text]);
	v_prod_cd.setValue(_first_code_prod);

}


function g_productToParam(sPattern, oData, bQuote) {
	bQuote = _nvl(bQuote, false);
	oData[(sPattern + "istp_cd").toUpperCase()] = eval("$cmb_istp_cd.getValue()");
	oData[(sPattern + "prod_cd").toUpperCase()] = eval("$cmb_prod_cd.getValue()");

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