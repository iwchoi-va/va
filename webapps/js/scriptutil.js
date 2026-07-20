/**
 * 입력된 실수를 반올림을 하여 Return
 * 
 * @param value
 * @param precision
 * @returns {Number}
 */
function round(value, precision) {
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
}


/**
 * 입력된 정수 또는 실수의 절대값을 구하여 Return
 * 
 * @param value
 * @returns {Number}
 */
function abs(value) {
	return Math.abs(value) || 0;
}

/**
 * 입력된 실수에 내림 하여 Return
 * 
 * @param value
 * @returns
 */
function floor(value) {
	return Math.floor(value);
}

/**
 * 입력된 실수에 올림 하여 Return
 * 
 * @param value
 * @returns
 */
function ceil(value) {
	return Math.ceil(value);
}

/**
 * 일정한 정수 범위의 난수를 구하여 Return
 * 
 * @param min
 * @param max
 * @returns
 */
function rand(min, max) {
	var argc = arguments.length;
	if (argc === 0) {
		min = 0;
		max = 2147483647;
	} else if (argc === 1) {
		throw new Error('Warning: rand() expects exactly 2 parameters, 1 given');
	}
	return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * Null Check
 * 
 * @param value
 * @returns {Boolean}
 */
function g_isNull(value) {
	return (value == null || typeof (value) == "undefined");
}

/**
 * Null 값을 실제값으로 변환
 * 
 * @returns
 */
function g_nvl() {
	if (g_isNull(arguments) == true)
		return null;
	for ( var i = 0; i < arguments.length; i++) {
		if (g_isNull(arguments[i]) == false)
			return arguments[i];
	}

	return null;
}

/**
 * 빈값을 실제값으로 변환
 * 
 * @returns
 */
function g_evl() {
	if (g_isNull(arguments) == true)
		return "";
	for ( var i = 0; i < arguments.length; i++) {
		if (g_isEmpty(arguments[i]) == false)
			return arguments[i];
	}

	return "";
}
/**
 * 빈값을 Check
 * 
 * @param value
 * @returns {Boolean}
 */
function g_isEmpty(value) {
	return (g_isNull(value) == true || (typeof (value) == "string" && value.length == 0));
}

/**
 * 띄어쓰기를 Check
 * 
 * @param value
 * @returns
 */
function g_isSpace(value) {
	return g_isEmpty(g_nvl(value, "").trim());
}

/**
 * 값비교
 * 
 * @returns {Boolean}
 */
function g_isCase() {
	for ( var i = 1; i < arguments.length; i++) {
		if (arguments[0] == arguments[i])
			return true;
	}
	return false;
}

/**
 * 입력값 형태에 따라서 길이 또는 범위를 구한다.
 * 
 * @param value
 * @returns
 */
function g_length(value) {
	var value = g_nvl(value, "");
	return value.length;
}

/**
 * 입력된 값 또는 수식을 검사해 적당한 값을 Return 한다. Decode는 가변갯수 인자를 취하는 Method 로 2n+1 또는 2n
 * (n>1)개의 인자갯수를 입력할 수 있습니다.
 * 
 * @returns
 */
function g_decode() {
	var i = 1;
	for (; i < arguments.length - 1;) {
		if (arguments[0] == arguments[i])
			return arguments[i + 1];
		i += 2;
	}
	return arguments[i];
}

/**
 * 입력된 문자열에서 가운데 부분을 주어진 길이만큼 Return 한다.
 * 
 * @returns
 */
function g_substr() {
	var arg = arguments;

	var value = arg[0];
	if (typeof (value) != "string") {
		value = value.toString();
	}

	var nStart = arg[1];
	var nLength = g_nvl(arg[2], value.length);
	return value.substr(nStart, nLength);
}

/**
 * 문자열에 있는 모든 영어를 소문자로 바꾼다.
 * 
 * @param value
 * @returns
 */
function g_toLower(value) {
	if (value == null)
		return value;
	if (typeof (value) == "object") {		
		if (value instanceof Array) {
			var str = '';
			for ( var i in value) {
				str += ',["' + g_replace(value[i].join(), ',', '","') + '"]';
			}
			str = g_substr(str, 1);
			value = (value.length == 1 ? str : '[' + str + ']');			
		} else {
			value = value.getValue();
		}
	}
	return value.toLowerCase();
}

/**
 * 문자열에 있는 모든 영어를 대문자로 바꾼다.
 * 
 * @param value
 * @returns
 */
function g_toUpper(value) {
	if (value == null)
		return value;
	if (typeof (value) == "object") {		
		if (value instanceof Array) {
			var str = '';
			for ( var i in value) {
				str += ',["' + g_replace(value[i].join(), ',', '","') + '"]';
			}
			str = g_substr(str, 1);
			value = (value.length == 1 ? str : '[' + str + ']');			
		} else {
			value = value.getValue();
		}
	}
	return value.toUpperCase();
}

/**
 * 문자열이 지정된 길이가 되도록 왼쪽을 채운다.
 * 
 * @param s
 *            원본 문자열.
 * @param c
 *            왼쪽에 채울 문자. 입력된 문자열의 첫 1글자만을 사용합니다.
 * @param n
 *            출력될 문자열의 길이
 * @returns
 */
function g_lpad(s, c, n) {
	if (!s || !c || s.length >= n) {
		return s;
	}

	var max = (n - s.length) / c.length;
	for ( var i = 0; i < max; i++) {
		s = c + s;
	}
	return s;
}

/**
 * 문자열이 지정된 길이가 되도록 오른쪽을 채우다.
 * 
 * @param s
 *            원본 문자열.
 * @param c
 *            오른쪽에 채울 문자. 입력된 문자열의 첫 1글자만을 사용합니다.
 * @param n
 *            출력될 문자열의 길이.
 * @returns
 */
function g_rpad(s, c, n) {

	if (!s || !c || s.length >= n) {
		return s;
	}

	var max = (n - s.length) / c.length;
	for ( var i = 0; i < max; i++) {
		s = c + s;
	}
	return s;
}

/**
 * 입력된 문자열의 양쪽에 따옴표를 붙여 Return(undefined는 제거)
 * 
 * @param value
 * @returns
 */
function quote(value) {
	var o = value.replace(/ /gim, "");
	o = o.split(",");
	for ( var i in o) {
		if (typeof (o[i]) == "string" && o[i] == "undefined") {
			o[i] = "";
		}
		o[i] = "'" + o[i] + "'";
	}
	return o.join(",");
}

/**
 * 배열중복제거
 * 
 * @param array
 * @returns
 */
function array_unique(array) {
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
}

/**
 * 입력된 문자열을 delimiter로 자른 결과 중 index 번째 결과값을 구한다.
 * 
 * @param value
 * @param delimiter
 * @param index
 * @returns
 */
function nToken(value, delimiter, index) {
	value = value || '';
	delimiter = delimiter || '';
	index = index || 0;
	var tokens = (value || '').trim().split(eval("/" + delimiter + "/"));
	if (tokens.length <= 1) {
		return value;
	}
	return tokens[index];
}

/**
 * 첫 값의 True/False를 검사해 그 결과에 따라 두번째 또는 세번째 값을 Return
 * 
 * @returns
 */
function iif() {
	var argc = arguments.length;
	if (argc != 3) {
		return false;
	}
	return (arguments[0]|false) ? arguments[1] : arguments[2];
}

/**
 * 입력된 실수에 버림
 * 
 * @param num
 * @param place
 * @returns
 */
function truncate() {
	var rtnval = null;
	var argc = arguments.length;
	if (argc == 1) {
		var n = arguments[0];
		rtnval = Math[n > 0 ? "floor" : "ceil"](n);
	} else if (argc == 2) {
		var n = arguments[0];
		var p = arguments[1];
		return Math[n > 0 ? "floor" : "ceil"]
				(n * Math.pow(10, parseInt(p, 10)))
				/ Math.pow(10, parseInt(p, 10));
	}
	return rtnval;
}

/**
 * 입력된 문자열의 일부분을 다른 문자열로 치환. sNew를 생략할 경우 sOld로 찾은 문자열이 모두 제거됩니다.
 * 
 * @param value
 * @param delimiter
 * @param replace
 * @returns
 */
function g_replace(sStr, sOld, sNew) {
	var str = g_nvl(sStr, "");
	return str.replace(eval("/" + g_nvl(sOld, "") + "/gim"), g_nvl(sNew, ""));
}

/**
 * 입력된 값을 전수형으로 전환
 * 
 * @param value
 */
function toNumber(value) {	
	value = g_nvl(g_evl(value, 0), 0);	
	return isNaN(value) ? 0 : parseFloat(value);
}

function comma(value) {
	var number = value.toString();
	return number.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}