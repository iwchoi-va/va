/**
 * 현재 Binding 된 Dataset의 Data Column 정보로 새로운 Format을 생성하는 Method 입니다. 
 * Format은 Grid의 표현형태로 Column, Head, Body의 구성정보를 가집니다.
 * 
 * @param opt
 *            {colgroup : colgroup option, head : head option, body : body option}
 * @returns {Boolean}
 */
xwing.widget.DataGrid.prototype.createFormat = function(opt) {
	var _ds = Xwing.getDataset(this._opt.binddataset);
	var _cols = _ds._cols;
	if (_datagrid_isNull(_ds))
		return false;
	if (_cols.length == 0)
		return false;

	var optcol = "";
	var opthead = "";
	var optbody = "";

	if (!_datagrid_isNull(opt)) {
		var __colgroup = opt.colgroup;
		if (!_datagrid_isNull(__colgroup)) {
			for ( var i in __colgroup) {
				optcol += i + '="' + __colgroup[i] + '" ';
			}
		}
		var __head = opt.head;
		if (!_datagrid_isNull(__head)) {
			for ( var i in __head) {
				opthead += i + '="' + __head[i] + '" ';
			}
		}
		var __body = opt.body;
		if (!_datagrid_isNull(__body)) {
			for ( var i in __body) {
				optbody += i + '="' + __body[i] + '" ';
			}
		}
	}

	var _colgroup = '<xwing:datagrid-colgroup>';
	var _head = '<xwing:datagrid-head><xwing:datagrid-row>';
	var _body = '<xwing:datagrid-body><xwing:datagrid-row>';
	for ( var i = 0; i < _cols.length; i++) {
		optcol = optcol.toLowerCase().indexOf("width") == -1 ? (optcol + ' width="100"') : optcol;
		_colgroup += '<xwing:datagrid-column ' + optcol + '></xwing:datagrid-column>';
		_head += '<xwing:datagrid-cell text="' + _cols[i] + '" ' + opthead + '></xwing:datagrid-cell>';
		_body += '<xwing:datagrid-cell bindcolumn="' + _cols[i] + '" ' + optbody + '></xwing:datagrid-cell>';
	}
	_colgroup += '</xwing:datagrid-colgroup>';
	_head += '</xwing:datagrid-row></xwing:datagrid-head>';
	_body += '</xwing:datagrid-row></xwing:datagrid-body>';

	var content = _colgroup + "" + _head + "" + _body;
	this.addContent(content, true);
};

/*******************************************************************************
 * UTIL
 ******************************************************************************/
function _datagrid_nvl() {
	if (_datagrid_isNull(arguments) == true)
		return null;
	for ( var i = 0; i < arguments.length; i++) {
		if (_datagrid_isNull(arguments[i]) == false)
			return arguments[i];
	}
	return null;
}

function _datagrid_evl() {
	if (_datagrid_isNull(arguments) == true)
		return "";
	for ( var i = 0; i < arguments.length; i++) {
		if (_datagrid_isEmpty(arguments[i]) == false)
			return arguments[i];
	}

	return "";
}

function _datagrid_decode() {
	var i = 1;
	for (; i < arguments.length - 1;) {
		if (arguments[0] == arguments[i])
			return arguments[i + 1];
		i += 2;
	}
	return arguments[i];
}

function _datagrid_substr() {
	var arg = arguments;

	var val = arg[0];
	if (typeof (val) != "string") {
		val = val.toString();
	}

	var s = arg[1];
	var e = _nvl(arg[2], val.length);
	return val.substr(s, e);
}

function _datagrid_isNull(obj) {
	return (obj == null || typeof (obj) == "undefined");
}

function _datagrid_isEmpty(obj) {
	return (_datagrid_isNull(obj) == true || (typeof (obj) == "string" && obj.length == 0));
}