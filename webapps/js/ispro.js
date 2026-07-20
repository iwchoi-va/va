//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
// ispro Object
// original by : YunKi
// improved by : 
// Last updated : 2013.10.16
//〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
var ispro = function() {};
ispro.statics = {
	todate : null,
	timelist : null,
	monthlist : null
};
ispro.prototype = {
	/***************************************************************************
	 * Math Util
	 **************************************************************************/
	round : function(value, precision) {
		var m, f, isHalf, sgn;
		precision |= 0;
		m = Math.pow(10, precision);
		value *= m;
		sgn = (value > 0) | -(value < 0);
		isHalf = value % 1 === 0.5 * sgn;
		f = Math.floor(value);
		if (isHalf) {
			value = f + (sgn > 0);
		}
		return (isHalf ? value : Math.round(value)) / m;
	},
	abs : function(value) {
		return Math.abs(value);
	},
	floor : function(value) {
		return Math.floor(value);
	},
	ceil : function(value) {
		return Math.ceil(value);
	},
	rand : function(min, max) {
		var argc = arguments.length;
		if (argc === 0) {
			min = 0;
			max = 2147483647;
		} else if (argc === 1) {
			throw new Error(
					'Warning: rand() expects exactly 2 parameters, 1 given');
		}
		return Math.floor(Math.random() * (max - min + 1)) + min;
	},
	truncate : function() {
		// 입력된 실수에 버림
		// * example: ispro.truncate("1234.4624", 2);
		// * returns: 1234.46
		var rtnval = null;
		var argc = arguments.length;
		if (argc == 1) {
			var n = arguments[0];
			rtnval = Math[n > 0 ? "floor" : "ceil"](n);
		} else if (argc == 2) {
			var n = arguments[0];
			var p = arguments[1];
			return Math[n > 0 ? "floor" : "ceil"](n
					* Math.pow(10, parseInt(p, 10)))
					/ Math.pow(10, parseInt(p, 10));
		}
		return rtnval;
	},
	/***************************************************************************
	 * String Util
	 **************************************************************************/
	isNull : function(value) {
		return (value == null || typeof (value) == "undefined");
	},
	isEmpty : function(value) {
		return (this.isNull(value) == true || (typeof (value) == "string" && value.length == 0));
	},
	isSpace : function(value) {
		return this.isEmpty(this.nvl(value, "").trim());
	},
	isCase : function() {
		for ( var i = 1; i < arguments.length; i++) {
			if (arguments[0] == arguments[i]) {
				return true;
			}
		}
		return false;
	},
	nvl : function() {
		if (this.isNull(arguments) == true) {
			return null;
		}
		for ( var i = 0; i < arguments.length; i++) {
			if (this.isNull(arguments[i]) == false)
				return arguments[i];
		}
		return null;
	},
	evl : function() {
		if (this.isNull(arguments) == true) {
			return "";
		}
		for ( var i = 0; i < arguments.length; i++) {
			if (this.isEmpty(arguments[i]) == false) {
				return arguments[i];
			}
		}
		return "";
	},
	length : function(value) {
		value = this.nvl(value, "");
		return value.length;
	},
	decode : function() {
		var i = 1;
		for (; i < arguments.length - 1;) {
			if (arguments[0] == arguments[i]) {
				return arguments[i + 1];
			}
			i += 2;
		}
		return arguments[i];
	},
	substr : function(varValue, nIndex, nSize) {
		if (typeof (varValue) != "string") {
			varValue = String(value);
		}
		return varValue.substr(nIndex, this.nvl(nSize, varValue.length));
	},
	toLower : function(value) {
		if (this.isNull(value) == true || this.isEmpty(value) == true) {
			return value;
		}
		if (typeof (value) == "object") {
			if (value instanceof Array) {
				var str = '';
				for ( var i in value) {
					str += ',["' + this.replace(value[i].join(), ',', '","')
							+ '"]';
				}
				str = this.substr(str, 1);
				value = (value.length == 1 ? str : '[' + str + ']');
			} else {
				value = value.getValue();
			}
		}
		return value.toLowerCase();
	},
	toUpper : function(value) {
		if (this.isNull(value) == true || this.isEmpty(value) == true) {
			return value;
		}
		if (typeof (value) == "object") {
			if (value instanceof Array) {
				var str = '';
				for ( var i in value) {
					str += ',["' + this.replace(value[i].join(), ',', '","')
							+ '"]';
				}
				str = this.substr(str, 1);
				value = (value.length == 1 ? str : '[' + str + ']');
			} else {
				value = value.getValue();
			}
		}
		return value.toUpperCase();
	},
	lpad : function(strValue, strPadChar, nCount) {
		if (!strValue || !strPadChar || strValue.length >= nCount) {
			return strValue;
		}
		var max = (nCount - strValue.length) / strPadChar.length;
		for ( var i = 0; i < max; i++) {
			strValue = strPadChar + strValue;
		}
		return strValue;
	},
	rpad : function(strValue, strPadChar, nCount) {
		if (!strValue || !strPadChar || strValue.length >= nCount) {
			return strValue;
		}
		var max = (nCount - strValue.length) / strPadChar.length;
		for ( var i = 0; i < max; i++) {
			strValue = strValue + strPadChar;
		}
		return strValue;
	},
	quote : function(value) {
		// 입력된 문자열의 양쪽에 쌍따옴표를 붙여 Return
		// * example 1: ispro.quote("a,b,c");
		// * returns 1: 'a','b','c'
		// * example 2: ispro.quote("a");
		// * returns 2: 'a'
		var o = value.replace(/ /gim, "");
		o = o.split(",");
		for ( var i in o) {
			if (typeof (o[i]) == "string" && o[i] == "undefined") {
				o[i] = "";
			}
			o[i] = "'" + o[i] + "'";
		}
		return o.join(",");
	},
	array_unique : function(array) {
		// 배열중복제거
		// * example: ispro.array_unique([ "a", "b", "c", "a" ]);
		// * returns: ["a","b","c"]
		var a = {};
		for ( var i = 0; i < array.length; i++) {
			if (typeof a[array[i]] == "undefined") {
				a[array[i]] = 1;
			}
		}
		array.length = 0;
		for ( var i in a) {
			array[array.length] = i;
		}
		return array;
	},
	nToken : function(value, delimiter, index) {
		// 입력된 문자열을 delimiter로 자른 결과 중 index 번째 결과값을 구한다.
		// * example: ispro.nToken("a,b,c,a", ",", 2);
		// * returns: "c"
		value = this.nvl(value, "");
		delimiter = this.nvl(delimiter, "");
		index = this.nvl(index, 0);
		var tokens = value.trim().split(eval("/" + delimiter + "/"));
		if (tokens.length <= 1) {
			return value;
		}
		return tokens[index];
	},
	iif : function(varValue, varTrue, varFalse) {
		// 첫 값의 True/False를 검사해 그 결과에 따라 두번째 또는 세번째 값을 Return
		// * example: ispro.iif(1==1, 1, 2);
		// * returns: 1
		var argc = arguments.length;
		if (argc != 3) {
			return false;
		}
		return (varValue | false) ? varTrue : varFalse;
	},	
	replace : function(strValue, strOld, strNew) {
		// 입력된 문자열의 일부분을 다른 문자열로 치환. sNew를 생략할 경우 sOld로 찾은 문자열이 모두 제거
		// * example: ispro.replace("_abcdabcd", "a");
		// * returns: "_bcdbcd"
		var str = this.nvl(strValue, "");
		return str.replace(eval("/" + this.nvl(strOld, "") + "/gim"), this.nvl(
				strNew, ""));
	},
	toNumber : function(value) {
		// 입력된 값을 정수/실수형으로 전환한다.
		// * example: ispro.toNumber("11.5");
		// * returns: 11.5
		value = this.evl(this.nvl(value, 0), 0);
		return isNaN(value) ? 0 : parseFloat(value);
	},
	comma : function(value) {
		var tmp = value.split('.');
		var str = new Array();
		var v = tmp[0].replace(/,/gi, '');
		for ( var i = 0; i <= v.length; i++) {
			str[str.length] = v.charAt(v.length - i);
			if (i % 3 == 0 && i != 0 && i != v.length) {
				str[str.length] = '.';
			}
		}
		str = str.reverse().join('').replace(/\./gi, ',');
		return (tmp.length == 2) ? str + '.' + tmp[1] : str;
	},
	/***************************************************************************
	 * Date Util
	 **************************************************************************/
	toDate : function() {
		var year = String(new Date().getFullYear());
		var month = this.lpad(String(new Date().getMonth() + 1), "0", 2);
		var date = this.lpad(String(new Date().getDate()), "0", 2);
		this.todate = year + month + date + "";
		return year + month + date + "";
	},
	toDay : function() {
		var year = String(new Date().getFullYear());
		var month = this.lpad(String(new Date().getMonth() + 1), "0", 2);
		var date = this.lpad(String(new Date().getDate()), "0", 2);
		var hours = this.lpad(String(new Date().getHours()), "0", 2);
		var minutes = this.lpad(String(new Date().getMinutes()), "0", 2);
		var seconds = this.lpad(String(new Date().getSeconds()), "0", 2);
		return year + month + date + hours + minutes + seconds + "";
	},
	addDate : function(gubun, add, yyyymmdd, delimiter) {
		// 입력된 날자에 add로 지정된 만큼의 날짜를 더한다.
		// * example 1: ispro.addDate("m", -3, "20130520", "");
		// * returns 1: "20130220"
		// * example 2: ispro.addDate("m", -3, "20130520", "-");
		// * returns 2: "2013-02-20"
		gubun = this.nvl(gubun, "d");
		add = this.nvl(add, 0);
		delimiter = this.nvl(delimiter, "");
		yyyymmdd = this.replace(this.nvl(yyyymmdd, this.toDate()), delimiter,
				"");
		var yyyy = yyyymmdd.substr(0, 4);
		var mm = yyyymmdd.substr(4, 2);
		var dd = yyyymmdd.substr(6, 2);
		if (gubun == "y") {
			yyyy = (yyyy * 1) + (add * 1);
		} else if (gubun == "m") {
			mm = (mm * 1) + (add * 1);
		} else if (gubun == "d") {
			dd = (dd * 1) + (add * 1);
		}
		var date = new Date(yyyy, mm - 1, dd);
		var year = date.getFullYear() + "";
		var month = this.lpad(date.getMonth() + 1 + "", "0", 2);
		var day = this.lpad(date.getDate() + "", "0", 2);
		return year + delimiter + month + delimiter + day;
	},
	lastDate : function(yyyymmdd) {		
		return this.addDate("d", -1, this.addDate("m", 1, yyyymmdd.substr(0, 6) + "01", ""), "");
	},
	getDay : function(yyyymmdd, gubun) {
		// 입력된 날자로부터 요일(default Number, en English, ko Korea)을 구한다.
		// * example 1: ispro.getDay("20130520");
		// * returns 1: 1
		// * example 2: ispro.getDay("20130520", "ko");
		// * returns 2: "월"
		gubun = this.nvl(gubun, "number");
		if (typeof (yyyymmdd) != "string") {
			return null;
		}
		if (yyyymmdd.length != 8) {
			return null;
		}
		var day = this.string2date(yyyymmdd).getDay();
		if (this.isNull(day)) {
			return null;
		}
		var weekday_en = new Array(7);
		weekday_en[0] = "Sunday";
		weekday_en[1] = "Monday";
		weekday_en[2] = "Tuesday";
		weekday_en[3] = "Wednesday";
		weekday_en[4] = "Thursday";
		weekday_en[5] = "Friday";
		weekday_en[6] = "Saturday";
		if (gubun == "en") {
			return weekday_en[day];
		}
		var weekday_ko = new Array(7);
		weekday_ko[0] = "일";
		weekday_ko[1] = "월";
		weekday_ko[2] = "화";
		weekday_ko[3] = "수";
		weekday_ko[4] = "목";
		weekday_ko[5] = "금";
		weekday_ko[6] = "토";
		if (gubun == "ko") {
			return weekday_ko[day];
		}
		return day;
	},
	string2date : function(yyyymmdd) {
		if (typeof (yyyymmdd) != "string") {
			return null;
		}
		if (yyyymmdd.length != 8) {
			return null;
		}
		var yyyy = parseInt(yyyymmdd.substr(0, 4));
		var mm = parseInt(yyyymmdd.substr(4, 2));
		var dd = parseInt(yyyymmdd.substr(6, 2));
		return new Date(yyyy, mm - 1, dd);
	},
	date2mask : function(varDate, varMask) {
		varDate = this.rpad(this.nvl(varDate, this.toDay()), "0", 14);
		varMask = this.toUpper(this.nvl(varMask, "YYYYMMDDHHMISS"));
		varMask = this.replace(varMask, "YYYY", this.substr(varDate, 0, 4));
		varMask = this.replace(varMask, "MM", this.substr(varDate, 4, 2));
		varMask = this.replace(varMask, "DD", this.substr(varDate, 6, 2));
		varMask = this.replace(varMask, "HH", this.substr(varDate, 8, 2));
		varMask = this.replace(varMask, "MI", this.substr(varDate, 10, 2));
		varMask = this.replace(varMask, "SS", this.substr(varDate, 12, 2));
		return varMask;
	},
	sec2hhmiss : function(varSec) {
		var nSec = parseFloat(varSec);
		var k = "";
		if (nSec < 0) {
			nSec = Math.abs(nSec);
			k = "-";
		}
		var hour = this.floor(nSec / 3600);
		var min = this.floor((nSec % 3600) / 60);
		var sec = this.floor((nSec % 3600) % 60);
		return k + this.lpad(hour + "", "0", 2) + ":"
				+ this.lpad(min + "", "0", 2) + ":"
				+ this.lpad(sec + "", "0", 2);
	},
	sec2miss : function(varSec) {
		psec = parseFloat(psec);
		var min = this.floor((psec) / 60);
		var sec = this.floor((psec) % 60);
		var rtn = this.lpad(min + "", "0", 2) + ":"
				+ this.lpad(sec + "", "0", 2);
		return rtn;
	},
	sec2hour : function(varSec) {
		return Math.round(parseFloat(varSec) / 3600);
	},
	sec2min : function(varSec) {
		return Math.round(parseFloat(varSec) / 60);
	},
	monthList : function() {
		var month = [];
		for ( var i = 1; i <= 12; i++) {
			var codeid = i;
			var codename = i + "월";
			var quarter = this.ceil(i / 3);
			var quartername = quarter + "분기";
			var half = this.ceil(i / 6);
			var halfname = this.iif(half == 1, "상반기", "하반기");
			month.push([ this.lpad(codeid + "", "0", 2), codename, quarter, quartername, half, halfname ]);
		}
		var monthlist = {
			column : [ "CODEID", "CODENAME", "QUARTER", "QUARTERNAME", "HALF",
					"HALFNAME" ],
			record : month,
			count : month.length
		};
		this.monthlist = monthlist;
		return monthlist;
	},
	timeList : function() {
		var quarter = [];
		var half = [];
		var full = [];
		for ( var i = 0; i <= 23; i++) {
			for ( var j = 0; j < 60; j++) {
				var code = this.lpad(i + "", "0", 2)
						+ this.lpad(j + "", "0", 2);
				var codename = this.lpad(i + "", "0", 2) + ":"
						+ this.lpad(j + "", "0", 2);
				var tmp = [ code, codename ];
				if (j % 15 == 0) {
					quarter.push(tmp);
				}
				if (j % 30 == 0) {
					half.push(tmp);
				}
				if (j % 60 == 0) {
					full.push(tmp);
				}
			}
		}
		var timelist = {
			quarter : {
				column : [ "CODEID", "CODENAME" ],
				record : quarter,
				count : quarter.length
			},
			half : {
				column : [ "CODEID", "CODENAME" ],
				record : half,
				count : half.length
			},
			full : {
				column : [ "CODEID", "CODENAME" ],
				record : full,
				count : full.length
			}
		};
		this.timelist = timelist;
		return timelist;
	},
	/***************************************************************************
	 * dataset Util
	 **************************************************************************/
	addColumn : function(datast, column) {
		// 데이터셋 컬럼값 추가.
		// * example : ispro.addColumn(DATASET, "COLUMN");
		// * returns : dataset column info
		datast.setColumnInfo(datast.getColumnInfo().concat(column));
		return datast.getColumnInfo();
	},	
	getColId : function(dataset, nIndex) {
		var columns = dataset.getColumnInfo();
		return columns[nIndex];
	},
	getColIndex : function(dataset, column) {
		var columns = dataset.getColumnInfo();
		var i = 0;
		var idx = -1;
		for (i in columns) {
			if (columns[i] == column) {
				idx = i;
				break;
			}
		}
		return idx;
	},
	count : function(dataset, filter) {
		filter = String(this.nvl(filter, "")).trim();
		var rows = this.getData(dataset, filter);
		return rows.length;
	},
	colCount : function(dataset) {
		return dataset.getColumnInfo().length;
	},
	searchRow : function(dataset, filter){
		filter = String(this.nvl(filter, "")).trim();
		var __cols = this.nvl(dataset._cols, []);
		if (__cols.length == 0){
			return -1;
		}		
		var __rows = this.nvl(dataset._rows, []);
		if (__rows.length == 0){
			return -1;
		}		
		__cols = __cols.concat("_rowidx");
		var _max_col_idx = (__cols.length - 1);
		for ( var i = 0; i < __rows.length; i++) {
			__rows[i][_max_col_idx] = i;
		}		
		var _pattern = [], _ptnExpr, v;
		_ptnExpr = xwing.Dataset._FILTER_PTN;
		while (v = filter.match(_ptnExpr)) {
			_pattern.push(v);
			filter = filter.replace(_ptnExpr, '');
		}		
		_ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?>)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
		while (v = filter.match(_ptnExpr)) {
			_pattern.push(v);
			filter = filter.replace(_ptnExpr, '');
		}
		_ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?<)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
		while (v = filter.match(_ptnExpr)) {
			_pattern.push(v);
			filter = filter.replace(_ptnExpr, '');
		}
		if (_pattern.length == 0){
			return -1;
		}
		for ( var i = 0, l = _pattern.length; i < l; i++) {
			var idx = dataset._cols.indexOf(_pattern[i][1]);
			if (idx == -1) {
				continue;
			}
			var type = _pattern[i][2], check = _pattern[i][4], value;
			__rows = jQuery.grep( __rows,
					function(v) {
						value = String(v[idx]);
						return type === '==' ? value === check : 
							   type === '*=' ? value.indexOf(check) >= 0 : 
							   type === '~=' ? (" " + value + " ").indexOf(check) >= 0 : 
							   type === '!=' ? value !== check : 
							   type === '^=' ? value.indexOf(check) === 0 : 
							   type === '$=' ? value.substr(value.length - check.length) === check : 
							   type === '<=' ? parseFloat(value) <= parseFloat(check) :
							   type === '>=' ? parseFloat(value) >= parseFloat(check) :
							   type === '>>' ? parseFloat(value) > parseFloat(check) :
							   type === '<<' ? parseFloat(value) < parseFloat(check) :
							   false;
			});
		}
		return (this.isNull(__rows) || (__rows.length == 0) || 
				this.isNull(__rows[0][_max_col_idx]) ||
				this.isEmpty(__rows[0][_max_col_idx])) ? -1 : __rows[0][_max_col_idx];
	},
	copyRow : function(toDataset, toRow, fromDataset, fromRow, colInfo) {
		if (typeof (toDataset) == "string") {
			toDataset = Xwing.getDataset(toDataset);
			if (this.isNull(toDataset)) {
				return false;
			}
		}
		if (typeof (fromDataset) == "string") {
			fromDataset = Xwing.getDataset(fromDataset);
			if (this.isNull(fromDataset)) {
				return false;
			}
		}
		var to_columns_join = toDataset.getColumnInfo().join(",");
		var from_columns = fromDataset.getColumnInfo();
		var to_columns = toDataset.getColumnInfo();
		var from_hash = new xwing.util.Hash();
		var to_hash = new xwing.util.Hash();		
		var to_rows = [];
		for ( var i = 0; i < from_columns.length; i++) {
			from_hash.set(to_columns[i], i);
			var _column = from_columns[i];
			if (to_columns_join.indexOf(_column) == -1) {
				to_columns_join += "," + _column;
			}
		}
		toDataset.setColumnInfo(to_columns_join.split(","));
		to_columns = toDataset.getColumnInfo();
		for ( var i = 0; i < to_columns.length; i++) {
			to_hash.set(to_columns[i], i);
		}
		for ( var i = 0; i < from_columns.length; i++) {
			to_rows[to_hash.get(from_columns[i])] = fromDataset.getValue(
					fromRow, from_columns[i]);
		}
		if (!this.isNull(colInfo) && !this.isEmpty(colInfo)) {
			var _coninfos = colInfo.split(",");
			for ( var i = 0; i < _coninfos.length; i++) {
				if (_coninfos[i].indexOf("=") == -1)
					continue;
				var _infos = _coninfos[i].split("=");
				for ( var j = 0; j < _infos.length; j++) {
					to_rows[to_hash.get(String(_infos[0]).trim())] = fromDataset
							.getValue(fromRow, String(_infos[1]).trim());
				}
			}
		}
		toDataset.setRow(toRow, to_rows);
		return true;
	},
	getData : function(dataset, filter) {
		// 상태[컬럼0 operator '비교값'] 상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. operator는 ==, =,
		// ~=, !=, ^=, $=, >>, >> 이있습니다.
		// == 는 비교값이 컬럼값과 일치하는 경우,
		// *= 는 비교값이 포함된 경우,
		// ~= 는 비교값이 한 단어로 분리된 경우,
		// != 는 비교값이 컬럼값과 다른 경우,
		// ^= 는컬럼값이 비교값으로 시작하는 경우,
		// $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
		// <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
		// >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다.
		// << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
		// >> 는 컬럼값이 비교값보다 큰 경우를 말합니다.
		if (!filter) {
			return this.nvl(dataset._rows, []);
		}
		if (xwing.Util.is(filter, 'function')) {
			return jQuery.grep(dataset._rows, filter);
		}
		filter = String(filter).trim();
		var row = [], state;
		if (filter.charAt(0) == ':') {
			if ((state = filter.charAt(1).toUpperCase()) == 'D') {
				for ( var i = 0, l = dataset._rowsRemoved.length; i < l; i++) {
					row.push(dataset._rowsRemoved[i].concat([]));
				}
			} else {
				for ( var i = 0, l = dataset._rows.length; i < l; i++) {
					dataset._rows[i]._ST == state
							&& row.push(dataset._rows[i].concat([]));
				}
			}
			filter = filter.substr(2);
			if (filter.length == 0) {
				return row;
			}
		} else {
			row = this.nvl(dataset._rows, []);
		}
		var pattern = [], ptnExpr, v;
		ptnExpr = xwing.Dataset._FILTER_PTN;
		while (v = filter.match(ptnExpr)) {
			pattern.push(v);
			filter = filter.replace(ptnExpr, '');
		}
		ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?>)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
		while (v = filter.match(ptnExpr)) {
			pattern.push(v);
			filter = filter.replace(ptnExpr, '');
		}
		ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?<)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
		while (v = filter.match(ptnExpr)) {
			pattern.push(v);
			filter = filter.replace(ptnExpr, '');
		}
		for ( var i = 0, l = pattern.length; i < l; i++) {
			var idx = dataset._cols.indexOf(pattern[i][1]);
			if (idx == -1) continue;
			var type = pattern[i][2], check = pattern[i][4], value;
			row = jQuery.grep( row,
					function(v) {
						value = String(v[idx]);
						return type === '==' ? value === check : 
							   type === '*=' ? value.indexOf(check) >= 0 : 
							   type === '~=' ? (" " + value + " ").indexOf(check) >= 0 : 
							   type === '!=' ? value !== check : 
							   type === '^=' ? value.indexOf(check) === 0 : 
							   type === '$=' ? value.substr(value.length - check.length) === check : 
							   type === '<=' ? parseFloat(value) <= parseFloat(check) :
							   type === '>=' ? parseFloat(value) >= parseFloat(check) :
							   type === '>>' ? parseFloat(value) > parseFloat(check) :
							   type === '<<' ? parseFloat(value) < parseFloat(check) :
							   false;
			});
		}
		return row;
	},
	min : function(dataset, column, filter) {
		column = String(column).trim();
		filter = String(this.nvl(filter, "")).trim();
		var _idx = this.getColIndex(dataset, column);
		if (_idx == -1) {
			return "";
		}
		var __rows = this.getData(dataset, filter);
		if (__rows.length == 0) {
			return "";
		}
		var _array = new Array();
		for ( var i = 0; i < __rows.length; i++) {
			_array[i] = __rows[i][_idx];
		}
		_array.sort(function(pre, next) {
			if (isNaN(parseFloat(pre)) || isNaN(parseFloat(next))) {
				return (pre < next) ? -1 : (pre > next) ? 1 : 0;
			} else {
				return ((parseFloat(pre) < parseFloat(next)) ? -1
						: ((parseFloat(pre) > parseFloat(next)) ? 1 : 0));
			}
		});
		return _array[0];
	},
	max : function(dataset, column, filter) {
		column = String(column).trim();
		filter = String(dataset.nvl(filter, "")).trim();
		var _idx = this.getColIndex(dataset, column);
		if (_idx == -1) {
			return "";
		}
		var __rows = dataset.getData(dataset, filter);
		if (__rows.length == 0) {
			return "";
		}
		var _array = new Array();
		for ( var i = 0; i < __rows.length; i++) {
			_array[i] = __rows[i][_idx];
		}
		_array.sort(function(pre, next) {
			if (isNaN(parseFloat(pre)) || isNaN(parseFloat(next))) {
				return (pre < next) ? -1 : (pre > next) ? 1 : 0;
			} else {
				return ((parseFloat(pre) < parseFloat(next)) ? -1
						: ((parseFloat(pre) > parseFloat(next)) ? 1 : 0));
			}
		});
		return _array[_array.length - 1];
	},
	sum : function(dataset, column, filter) {		
		column = String(column).trim();
		filter = String(this.nvl(filter, "")).trim();
		var _idx = this.getColIndex(dataset, column);		
		if (_idx == -1){
			return null;
		}
		var __rows = this.getData(dataset, filter);		
		if (__rows.length == 0){
			return 0;
		}			
		var _sum = 0, _val;
		var _flag = false;
		for ( var i = 0; i < __rows.length; i++) {
			_val = this.evl(this.nvl(__rows[i][_idx], 0), 0);
			if (isNaN(parseFloat(_val))) {
				_flag = true;
				break;
			}
			_sum += parseFloat(_val);
		}		
		return _flag ? null : _sum;
	},
	avg : function(dataset, column, filter) {
		column = String(column).trim();
		filter = String(this.nvl(filter, "")).trim();
		var _idx = this.getColIndex(dataset, column);
		if (_idx == -1){
			return null;
		}
		var __rows = this.getData(dataset, filter);
		if (__rows.length == 0)
			return 0;
		var _sum = 0, _val, _cnt = 0;
		var _flag = false;
		for ( var i = 0; i < __rows.length; i++) {
			_val = __rows[i][_idx];
			if (!this.isNull(_val) && !this.isEmpty(_val)) {
				if (isNaN(parseFloat(_val))) {
					_flag = true;
					break;
				}
				_sum += parseFloat(_val);
				_cnt++;
			}
		}
		return _flag ? null : _sum / _cnt;
	},
	addRow : function(dataset) {
		if (!(dataset instanceof xwing.Dataset)) {
			return -1;
		}
		var colInfo = dataset.getColumnInfo();
		var array = colInfo.join().split(",");
		for ( var i in array) {
			array[i] = "";
		}
		dataset.addRow(array);
		return dsObj.size() - 1;
	},
	getValues : function(dataset, column) {
		var rtnval = "";
		for ( var i = 0; i < dataset.size(); i++) {
			rtnval += dataset.getValue(i, column);
			rtnval += i < dataset.size() - 1 ? "," : "";
		}
		return rtnval;
	},
	setValues : function(dataset, column, value) {
		if (typeof (value) == "string") {
			for ( var i = 0; i < dataset.size(); i++) {
				dataset.setValue(i, column, value);
			}
			return true;
		} else if (typeof (value) == "object" && value instanceof Array) {
			for ( var i = 0; i < dataset.size(); i++) {
				dataset.setValue(i, column, this.nvl(value[i], ""));
			}
			return true;
		} else {
			return false;
		}
	},
	setMulitValue : function() {
		var argc = arguments.length;
		if (argc % 2 != 0) {
			return false;
		}
		if (argc < 4) {
			return false;
		}	
		var dsObj = arguments[0];	
		if (!(dsObj instanceof xwing.Dataset)) {
			return false;
		}	
		var nRow = arguments[1];
		if (typeof (nRow) != "number") {
			return false;
		}
		var colInfo = dsObj.getColumnInfo();
		if (dsObj.size() <= 0 || (dsObj.size() - 1) < nRow) {
			dsObj.addRow(new Array(colInfo.length));
			nRow = (dsObj.size()) - 1;
		} else {
			dsObj.insertRow(nRow, new Array(colInfo.length));
		}
		for ( var i = 2; i < argc - 1; i += 2) {
			var colId = arguments[i];
			if (colInfo.indexOf(colId) > 0) {
				var value = arguments[i + 1];
				value = ((value == null || value == undefined) ? "" : value);
				dsObj.setValue(nRow, colId, value);
			}
		}	
		return true;
	},
	/***************************************************************************
	 * dataGrid Util
	 **************************************************************************/
	createFormat : function(datagrid, opt) {
		var _ds = Xwing.getDataset(datagrid._opt.binddataset);
		var _cols = _ds._cols;
		if (this.isNull(_ds)){
			return false;
		}			
		if (_cols.length == 0){
			return false;
		}			
		var optcol = "";
		var opthead = "";
		var optbody = "";
		if (!this.isNull(opt)) {
			var __colgroup = opt.colgroup;
			if (!this.isNull(__colgroup)) {
				for ( var i in __colgroup) {
					optcol += i + '="' + __colgroup[i] + '" ';
				}
			}
			var __head = opt.head;
			if (!this.isNull(__head)) {
				for ( var i in __head) {
					opthead += i + '="' + __head[i] + '" ';
				}
			}
			var __body = opt.body;
			if (!this.isNull(__body)) {
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
		datagrid.addContent(content, true);
	}
};
ispro.prototype.timer = function() {
	this._interval = 1000;
	this._timer = null;
	this._function = null;
};
ispro.prototype.timer.prototype = {
	start : function() {
		this._timer = setInterval((this._function == null ? this._func
				: this._function), this._interval);
	},
	stop : function() {
		clearInterval(this._timer);
	},
	setfunction : function(func) {
		this._function = (typeof (func) == "string") ? eval(func)
				: (typeof (func) == "function") ? func : null;
	},
	setInterval : function(interval) {
		this._interval = (isNaN(interval) ? 1 : (interval == 0) ? 1 : interval) * 1000;
	},
	_func : function() {
		Xwing.notify("The function is not defined.");
	}
};
ispro.prototype.msg = function(id, param){
	if(message == null){
		return null;
	}
	var msg = message.kr[id];
	if (msg == null
			|| (typeof (msg) == "string" && msg.replace(/(^\s*)|(\s$)/g, "") == "")) {
		return null;
	}
	var idx = 0;
	var count = 0;
	if (param == null) {
		return msg;
	}
	while ((idx = msg.indexOf("@", idx)) != -1) {
		if (param[count] == null) {
			param[count] = "";
		}
		msg = msg.substr(0, idx) + String(param[count])
				+ msg.substring(idx + 1);
		idx = idx + String(param[count++]).length;
	}
	return msg;
};
var ispro = new ispro();