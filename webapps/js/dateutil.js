/*********************************************************************
 * 기간설정 컴포넌트 생성 js (일, 월별 단위 기준)
 * ex) 
 * component : panel(id:pnl_date, width:420, height: 25, top: 15, left: 15)
 * element 생성: g_createDate("pnl_date", day_yn, mon_yn, hour_yn);
 * parameter 호출: g_dateToParam().sdate
 *               (일별/월별: sdate, edate)
 *               (시간대별: sdate(=edate), stime, etime)
 * 제약 조건: 기간 범위 (일별: 31일, 월별: 31개월, 시간대별: 24시간)
 *********************************************************************/

var _obj; 				  // 조회 기간을 담을 Panel
var _mode;                 // 기간구분 선택 모드
var FUNC_XOBJECT = [];    // XwingObject()

//위젯 그리기용
var _left = 0; 
var defalut_left = 0;
//var _top = 0; 
var _height = 25; 
var _space = 5;

//위젯명(접근 객체명)
var _gubunName = "cmb_gubun";
var _sdateName = "sdate";
var _edateName = "edate";
var _syearName = "syear";
var _smonthName = "smonth";
var _eyearName = "eyear";
var _emonthName = "emonth";
var _dateName = "date";
var _stimeName = "stime";
var _etimeName = "etime";

var _swdateName = "swdate";
var _ewdateName = "ewdate";
var _swyearName = "swyear";
var _ewyearName = "ewyear";

var _channelName = "rdo_channel";

// Dataset 관련 변수
var _dsGubunName = "DS_DATE"; 		// 기간 구분 데이터셋명
var _dsGubunObj ; 					// 기간 구분 데이터셋 객체
var _gubun = {D:["D","일별"],W:["W","주별"],M:["M","월별"],H:["H","시간대별"]};
var _dsWeekName = "DS_WEEK"; 	    // 주간 데이터셋명
var _dsWeekObj; 					// 주간 데이터셋 객체
var _week = [["01","1월"],["02","2월"],["03","3월"],["04","4월"],["05","5월"],["06","6월"],["07","7월"],["08","8월"],["09","9월"],["10","10월"],["11","11월"],["12","12월"]];
var _dsMonthName = "DS_MONTH"; 	    // 월 데이터셋명
var _dsMonthObj; 					// 월 데이터셋 객체
var _month = [["01","1월"],["02","2월"],["03","3월"],["04","4월"],["05","5월"],["06","6월"],["07","7월"],["08","8월"],["09","9월"],["10","10월"],["11","11월"],["12","12월"]];
var _dsSTimeName = "DS_STIME"; 	    // 시간대 데이터셋명
var _dsETimeName = "DS_ETIME"; 	    // 시간대 데이터셋명
var _dsSTimeObj; 					// 시간대  데이터셋 객체
var _dsETimeObj; 					// 시간대  데이터셋 객체
var _stime = [["00","0시"],["01","1시"],["02","2시"],["03","3시"],["04","4시"],["05","5시"],["06","6시"],["07","7시"],["08","8시"],["09","9시"],["10","10시"],["11","11시"],["12","12시"],["13","13시"],["14","14시"],["15","15시"],["16","16시"],["17","17시"],["18","18시"],["19","19시"],["20","20시"],["21","21시"],["22","22시"],["23","23시"]];
var _etime = [["01","1시"],["02","2시"],["03","3시"],["04","4시"],["05","5시"],["06","6시"],["07","7시"],["08","8시"],["09","9시"],["10","10시"],["11","11시"],["12","12시"],["13","13시"],["14","14시"],["15","15시"],["16","16시"],["17","17시"],["18","18시"],["19","19시"],["20","20시"],["21","21시"],["22","22시"],["23","23시"],["24","24시"]];


//////////////////////////////////////////기간 구분 생성////////////////////////////////////////////////

function g_createDate(obj, day_yn, mon_yn, hour_yn, lbl_name, week_yn) { 
	
	if(lbl_name == undefined ) lbl_name= "녹취일시";
	var cnt = 0;
	if(day_yn=="Y") cnt++; if(mon_yn=="Y") cnt++; if(hour_yn=="Y") cnt++; if(week_yn=="Y") cnt++;
	
	if(cnt<1) return; //왜 만들려고 하니?
	
	_obj = Xwing.getPanel(obj);
	// 주 데이터셋 생성
	_dsWeekObj = Xwing.createDataset(_dsWeekName);
	_dsWeekObj.setColumnInfo([ 'CODEID', 'CODENAME' ]);
	FUNC_XOBJECT.push(eval("$" + _dsWeekName + "= Xwing.getDataset('" + _dsWeekName + "')"));
	
	for(var i=0; i<12; i++){
		$DS_WEEK.addRow();
		$DS_WEEK.setRow(i, _week[i]);
	}
	// 월 데이터셋 생성
	_dsMonthObj = Xwing.createDataset(_dsMonthName);
	_dsMonthObj.setColumnInfo([ 'CODEID', 'CODENAME' ]);
	FUNC_XOBJECT.push(eval("$" + _dsMonthName + "= Xwing.getDataset('" + _dsMonthName + "')"));
	
	for(var i=0; i<12; i++){
		$DS_MONTH.addRow();
		$DS_MONTH.setRow(i, _month[i]);
	}
	
	// 시간대 데이터셋 생성
	_dsSTimeObj = Xwing.createDataset(_dsSTimeName);
	_dsSTimeObj.setColumnInfo([ 'CODEID', 'CODENAME' ]);
	FUNC_XOBJECT.push(eval("$" + _dsSTimeName + "= Xwing.getDataset('" + _dsSTimeName + "')"));
	_dsETimeObj = Xwing.createDataset(_dsETimeName);
	_dsETimeObj.setColumnInfo([ 'CODEID', 'CODENAME' ]);
	FUNC_XOBJECT.push(eval("$" + _dsETimeName + "= Xwing.getDataset('" + _dsETimeName + "')"));
	for(var i=0; i<24; i++){
		$DS_STIME.addRow();
		$DS_STIME.setRow(i, _stime[i]);
		$DS_ETIME.addRow();
		$DS_ETIME.setRow(i, _etime[i]);
	}

	
	// 기간 구분 데이터셋 생성
	_dsGubunObj = Xwing.createDataset(_dsGubunName);
	_dsGubunObj.setColumnInfo([ 'CODEID', 'CODENAME' ]);
	FUNC_XOBJECT.push(eval("$" + _dsGubunName + "= Xwing.getDataset('" + _dsGubunName + "')"));

//	for(var i=0; i<gubun_cnt; i++){
//		$DS_DATE.addRow();
//		$DS_DATE.setRow(i, _gubun[i]);
//	}

	// 선택 타입만 datset 구성
	if(day_yn == "Y"){
		$DS_DATE.addRow();
		$DS_DATE.setRow($DS_DATE.size()-1, _gubun.D);
	}
	if(week_yn == "Y"){
		$DS_DATE.addRow();
		$DS_DATE.setRow($DS_DATE.size()-1, _gubun.W);
	}
	if(mon_yn == "Y"){
		$DS_DATE.addRow();
		$DS_DATE.setRow($DS_DATE.size()-1, _gubun.M);
	}
	if(hour_yn == "Y"){
		$DS_DATE.addRow();
		$DS_DATE.setRow($DS_DATE.size()-1, _gubun.H);
	}
	
	
	// 기간
	var label = Xwing.createLabel({
		id : 'label1',
		width : 60,
		height : _height,
		top : 0,
		left : _left,
		styles : 'label_subtitle',
		valign : 'middle',
		halign : 'right',
		value : lbl_name
	});
	_obj.appendChild(label);
	_left += (eval("Xwing.getLabel('label1').getWidth()") + _space);
	
	//기간 구분 콜보박스 생성
	var _combo = Xwing.createCombo({
		id : _gubunName,
		width : 100,
		height : _height,
		top : 0,
		left : _left,
		domaindataset : _dsGubunName,
		domaincodecolumn : "CODEID",
		domaintextcolumn : "CODENAME",
		size : 20,
		change : "_gubun_change",
		ns : "xwing",
		xw_type : "combo",
		value: $DS_DATE.getValue($DS_DATE.size()-1,"CODEID")
//		value: $DS_DATE.getValue(2,"CODEID")
//		value: _gubun[0][0]
	});
	_obj.appendChild(_combo);
	
	FUNC_XOBJECT.push(eval("$" + _gubunName + "= Xwing.getCombo('" + _gubunName + "')"));
	_left += eval("$"+_gubunName+".getWidth()")+_space;
	defalut_left = _left;
	
	
	

	// 기간구분별 panel 생성
	var _pan1 = Xwing.createPanel({
		id : 'panel1',
		width : 220,
		height : _height,
		top : 0,
		left : defalut_left,
		visible: false
	});
	_obj.appendChild(_pan1);
	var _pan2 = Xwing.createPanel({
		id : 'panel2',
		width : 250,
		height : _height,
		top : 0,
		left : defalut_left,
		visible: false
	});
	_obj.appendChild(_pan2);
	var _pan3 = Xwing.createPanel({
		id : 'panel3',
		width : 235,
		height : _height,
		top : 0,
		left : defalut_left,
		visible: false
	});
	_obj.appendChild(_pan3);
	var _pan4 = Xwing.createPanel({
		id : 'panel4',
		width : 220,
		height : _height,
		top : 0,
		left : defalut_left,
		visible: false
	});
	_obj.appendChild(_pan4);
	
	makeWidget(); 
	
}

function makeWidget(){

	var mon = String(new Date().getMonth()+1);
	mon = mon.length<2?"0"+mon:mon;

	var hour = String(new Date().getHours());
	hour = hour.length<2?"0"+hour:hour;
	//굳이 안해도되려나?
	var week =  String(new Date().getFullYear())+mon+'01';
	////////////////////////////////일별//////////////////////////////////
	_left=0;
	var _pan1 = Xwing.getPanel('panel1');
	 
	// 시작일자
	var _sDp= Xwing.createDatepicker({
			id : _sdateName,
			width : '100',
			height : _height,
			top : 0,
			left : 0,
			value: beforeToday(7)
		});
	_pan1.appendChild(_sDp);
	FUNC_XOBJECT.push(eval("$" + _sdateName + "= Xwing.getDatepicker('" + _sdateName + "')"));
	_left += (eval("$" + _sdateName + ".getWidth()") + _space);

	// ~
	var label = Xwing.createLabel({
		id : 'label2',
		width : '10',
		height : _height,
		top : 0,
		left : _left,
		value: '~'
	});
	_pan1.appendChild(label);
	_left += (eval("Xwing.getLabel('label2').getWidth()") + _space);
	
	// 종료일자
	var _eDp= Xwing.createDatepicker({
		id : _edateName,
		width : '100',
		height : _height,
		top : 0,
		left : _left,
		value: checkDateFormat()
	});
	_pan1.appendChild(_eDp);
	FUNC_XOBJECT.push(eval("$" + _edateName + "= Xwing.getDatepicker('" + _edateName + "')"));

	
	////////////////////////////////월별//////////////////////////////////
//	if(gubun_cnt > 1){
		
		_left=0;
		var _pan2 = Xwing.getPanel('panel2');
		
		// 시작년도
		var _sY= Xwing.createSpin({
			id : _syearName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			min: '2000',
			max: '9999',
			value: String(new Date().getFullYear())
		});
		_pan2.appendChild(_sY);
		FUNC_XOBJECT.push(eval("$" + _syearName + "= Xwing.getSpin('" + _syearName + "')"));
		_left += (eval("$" + _syearName + ".getWidth()") + _space);
		
		// 시작월
		var _sM= Xwing.createCombo({
			id : _smonthName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			domaindataset : _dsMonthName,
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 12,
			ns : "xwing",
			xw_type : "combo",
			value: mon
		});
		_pan2.appendChild(_sM);
		FUNC_XOBJECT.push(eval("$" + _smonthName + "= Xwing.getCombo('" + _smonthName + "')"));
		_left += (eval("$" + _smonthName + ".getWidth()") + _space);
		
		// ~
		var label = Xwing.createLabel({
			id : 'label3',
			width : '10',
			height : _height,
			top : 0,
			left : _left,
			value: '~'
		});
		_pan2.appendChild(label);
		_left += (eval("Xwing.getLabel('label3').getWidth()") + _space);
		
		// 종료년도
		var _eY= Xwing.createSpin({
			id : _eyearName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			min: '2000',
			max: '9999',
			value: String(new Date().getFullYear())
		});
		_pan2.appendChild(_eY);
		FUNC_XOBJECT.push(eval("$" + _eyearName + "= Xwing.getSpin('" + _eyearName + "')"));
		_left += (eval("$" + _eyearName + ".getWidth()") + _space);

		// 종료월
		var _eM= Xwing.createCombo({
			id : _emonthName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			valign: "middle",
			domaindataset : _dsMonthName,
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 12,
			ns : "xwing",
			xw_type : "combo",
			value: mon
		});
		_pan2.appendChild(_eM);
		FUNC_XOBJECT.push(eval("$" + _emonthName + "= Xwing.getCombo('" + _emonthName + "')"));
		_left += (eval("$" + _emonthName + ".getWidth()") + _space);
//	}
	
	////////////////////////////////시간대별//////////////////////////////////
//	if(gubun_cnt > 2){
		
		_left=0;
		var _pan3 = Xwing.getPanel('panel3');
		
		// 일자
		var _sDp= Xwing.createDatepicker({
			id : _dateName,
			width : '100',
			height : _height,
			top : 0,
			left : 0,
			value: checkDateFormat()
		});
		_pan3.appendChild(_sDp);
		FUNC_XOBJECT.push(eval("$" + _dateName + "= Xwing.getDatepicker('" + _dateName + "')"));
		_left += (eval("$" + _dateName + ".getWidth()") + _space);
		
		// 시작 시간대
		var _sT= Xwing.createCombo({
			id : _stimeName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			domaindataset : _dsSTimeName,
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 24,
			ns : "xwing",
			xw_type : "combo",
			value: "08"
		});
		_pan3.appendChild(_sT);
		FUNC_XOBJECT.push(eval("$" + _stimeName + "= Xwing.getCombo('" + _stimeName + "')"));
		_left += (eval("$" + _stimeName + ".getWidth()") + _space);
		
		// ~
		var label = Xwing.createLabel({
			id : 'label4',
			width : '10',
			height : _height,
			top : 0,
			left : _left,
			value: '~'
		});
		_pan3.appendChild(label);
		_left += (eval("Xwing.getLabel('label4').getWidth()") + _space);
		
		// 종료 시간대
		var _eT= Xwing.createCombo({
			id : _etimeName,
			width : '55',
			height : _height,
			top : 0,
			left : _left,
			valign: "middle",
			domaindataset : _dsETimeName,
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 24,
			ns : "xwing",
			xw_type : "combo",
			value: hour
		});
		_pan3.appendChild(_eT);
		FUNC_XOBJECT.push(eval("$" + _etimeName + "= Xwing.getCombo('" + _etimeName + "')"));
		_left += (eval("$" + _etimeName + ".getWidth()") + _space);
//	}
		
////////////////////////////////주간별 만들기//////////////////////////////////
//if(gubun_cnt > 1){
	_left=0;
	var _pan4 = Xwing.getPanel('panel4');
	
	// 시작년도
	// 시작일자
	var _sDp= Xwing.createDatepicker({
			id : _swdateName,
			width : '100',
			height : _height,
			top : 0,
			left : 0,
			value: week
		});
	_pan4.appendChild(_sDp);
	FUNC_XOBJECT.push(eval("$" + _swdateName + "= Xwing.getDatepicker('" + _swdateName + "')"));
	_left += (eval("$" + _swdateName + ".getWidth()") + _space);

	// ~
	var label = Xwing.createLabel({
		id : 'label4',
		width : '10',
		height : _height,
		top : 0,
		left : _left,
		value: '~'
	});
	_pan4.appendChild(label);
	_left += (eval("Xwing.getLabel('label2').getWidth()") + _space);
	
	// 종료일자
	var _eDp= Xwing.createDatepicker({
		id : _ewdateName,
		width : '100',
		height : _height,
		top : 0,
		left : _left,
		value: checkDateFormat()
	});
	_pan4.appendChild(_eDp);
	FUNC_XOBJECT.push(eval("$" + _ewdateName + "= Xwing.getDatepicker('" + _ewdateName + "')"));

	
//}
		Xwing.getCombo(_gubunName).setValue('D');
		_gubun_change(); //panel setting
}


function _gubun_change(){
	_mode = Xwing.getCombo(_gubunName).getValue();

	if(_mode=="D"){
		Xwing.getPanel('panel1').setVisible(true);
		Xwing.getPanel('panel2').setVisible(false);
		Xwing.getPanel('panel3').setVisible(false);
		Xwing.getPanel('panel4').setVisible(false);
	}else if(_mode=="M"){
		Xwing.getPanel('panel1').setVisible(false);
		Xwing.getPanel('panel2').setVisible(true);
		Xwing.getPanel('panel3').setVisible(false);
		Xwing.getPanel('panel4').setVisible(false);
	}else if(_mode=="H"){
		Xwing.getPanel('panel1').setVisible(false);
		Xwing.getPanel('panel2').setVisible(false);
		Xwing.getPanel('panel3').setVisible(true);
		Xwing.getPanel('panel4').setVisible(false);
	}
	else if(_mode=="W"){
		Xwing.getPanel('panel1').setVisible(false);
		Xwing.getPanel('panel2').setVisible(false);
		Xwing.getPanel('panel3').setVisible(false);
		Xwing.getPanel('panel4').setVisible(true);
	}
}

function g_setEnabledDate(bool){
	Xwing.getWidget(_gubunName).setEnabled(bool);
	Xwing.getWidget(_sdateName).setEnabled(bool);
	Xwing.getWidget(_edateName).setEnabled(bool);
	Xwing.getWidget(_syearName).setEnabled(bool);
	Xwing.getWidget(_smonthName).setEnabled(bool);
	Xwing.getWidget(_eyearName).setEnabled(bool);
	Xwing.getWidget(_emonthName).setEnabled(bool);
	Xwing.getWidget(_dateName).setEnabled(bool);
	Xwing.getWidget(_stimeName).setEnabled(bool);
	Xwing.getWidget(_etimeName).setEnabled(bool);
	Xwing.getWidget(_swdateName).setEnabled(bool);
	Xwing.getWidget(_swdateName).setEnabled(bool);
}

/**
 * 현재 일자를 구한다.
 * @returns
 */
function g_toDate() {
	var year = String(new Date().getFullYear());
	var mon = String(new Date().getMonth() + 1);
	var day = String(new Date().getDate());	
	return year + (mon.length < 2 ? ("0" + mon) : mon)
			+ (day.length < 2 ? ("0" + day) : day);
}

/**
 * 현재 날짜/시간 형식 출력(년-월-일 시:분:초)
 * 
 * @returns {String}
 */
function g_getNow() {
	var year = String(new Date().getFullYear());
	var mon = String(new Date().getMonth() + 1);
	var day = String(new Date().getDate());
	var hr = String(new Date().getHours());
	var min = String(new Date().getMinutes());
	var sec = String(new Date().getSeconds());

	if (mon.length == 1)
		mon = "0" + mon;
	if (day.length == 1)
		day = "0" + day;
	if (hr.length == 1)
		hr = "0" + hr;
	if (min.length == 1)
		min = "0" + min;
	if (sec.length == 1)
		sec = "0" + sec;

	return year + "-" + mon + "-" + day + " " + hr + ":" + min + ":" + sec;
}

/**
 * 현재 날짜/시간 형식 출력(년월일시분초)
 * 
 * @returns {String}
 */
function getNow(){
	var year = String(new Date().getFullYear());
	var mon = String(new Date().getMonth() + 1);
	var day = String(new Date().getDate());
	var hr = String(new Date().getHours());
	var min = String(new Date().getMinutes());
	var sec = String(new Date().getSeconds());

	if (mon.length == 1)
		mon = "0" + mon;
	if (day.length == 1)
		day = "0" + day;
	if (hr.length == 1)
		hr = "0" + hr;
	if (min.length == 1)
		min = "0" + min;
	if (sec.length == 1)
		sec = "0" + sec;

	return year + mon + day + hr +min + sec;
}

/**
 * 입력된 날자에 add로 지정된 만큼의 날짜를 더한다.
 * 
 * @param gubun
 *            year/momth/day 구분 (y/m/d) [default = d]
 * @param add
 *            날짜로부터 증가 감소값. [default Value = 1]
 * @param yyyymmdd
 *            yyyymmdd 형태로 표현된 날자. [default Value = todate]
 * @param delimiter
 *            구분문자
 * @returns Date에 add가 더해진 결과를 yyyymmdd로 표현된 날자.
 */
function g_addDate(gubun, add, yyyymmdd, delimiter) {
	gubun = ((gubun == null || typeof (gubun) == "undefined") ? "d" : gubun);
	add = ((add == null || typeof (add) == "undefined") ? "d" : add);
	yyyymmdd = ((yyyymmdd == null || typeof (yyyymmdd) == "undefined") ? g_toDate()
			: yyyymmdd);
	delimiter = ((delimiter == null || typeof (delimiter) == "undefined") ? ""
			: delimiter);

	var yyyy;
	var mm;
	var dd;
	var date;
	var year, month, day;

	if (delimiter != "") {
		yyyymmdd = yyyymmdd.replace(eval("/\\" + delimiter + "/g"), "");
	}

	yyyy = yyyymmdd.substr(0, 4);
	mm = yyyymmdd.substr(4, 2);
	dd = yyyymmdd.substr(6, 2);

	if (gubun == "y") {
		yyyy = (yyyy * 1) + (add * 1);
	} else if (gubun == "m") {
		mm = (mm * 1) + (add * 1);
	} else if (gubun == "d") {
		dd = (dd * 1) + (add * 1);
	}

	date = new Date(yyyy, mm - 1, dd) // 12월, 31일을 초과하는 입력값에 대해 자동으로 계산된 날짜가 만들어짐.
	year = date.getFullYear() + "";
	month = date.getMonth() + 1 + "";
	day = date.getDate() + "";

	month = month < 10 ? "0" + month : month;
	day = day < 10 ? "0" + day : day;

	if (delimiter != "") {
		return year + delimiter + month + delimiter + day;
	} else {
		return year + month + day;
	}
}

/**
 * Calendar Widget Setting ex) getDateFormat("month",-7);
 * getDateFormat("day",15);
 * 
 * @param gubun
 *            year/month/day
 * @param num
 *            더하거나 뺄 년,월,일 숫자
 * @returns
 */
function g_getDateFormat(gubun, num) {
	var year = new Date().getFullYear();
	var mon = new Date().getMonth() + 1;
	var day = new Date().getDate();
	var hr = new Date().getHours();
	var min = new Date().getMinutes();
	var sec = new Date().getSeconds();
	var mon_day = new Array(30, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	var gap;

	if (gubun == "year") {
		year = year + num;
	} else if (gubun == "month") {
		gap = (num + mon) % 12;
		if (num > 0) {
			if (num + mon > 12) { // '다음년'으로 넘어갈 때
				mon = gap;
				year++;
			} else {
				mon = mon + num;
			}
		} else {
			if (mon + num <= 0) { // '지난년'으로 넘어갈 때
				mon = 12 + gap;
				year--;
			} else {
				mon = mon + num;
			}

		}
	} else if (gubun == "day") {
		if (num > 0) {
			if (day + num > mon_day[mon - 1]) { // '다음월'로 넘어갈 때
				day = num - (mon_day[mon - 1] - day);
				mon++;
			} else {
				day = day + num;
			}
		} else {
			if (day + num <= 0) { // '지난월'로 넘어갈 때
				day = mon_day[mon - 2] + (day + num);
				mon--;
			} else {
				day = day + num;
			}

		}
	} else if (gubun == "") {

		return year + mon + day;
	}

	year = new String(year);
	mon = new String(mon);
	day = new String(day);
	hr = new String(hr);
	min = new String(min);
	sec = new String(sec);

	if (mon.length == 1)
		mon = "0" + mon;
	if (day.length == 1)
		day = "0" + day;
	if (hr.length == 1)
		hr = "0" + hr;
	if (min.length == 1)
		min = "0" + min;
	if (sec.length == 1)
		sec = "0" + sec;

	return year + mon + day;
}

/**
 * 입력한 시간범위에 시간간격 세팅...
 * 
 * @param strStime
 *            시작시간
 * @param strEtime
 *            종료시간
 * @param nInterval
 *            간격(분)
 * @param objDataset
 *            null
 */
function g_makeTimeList(strStime, strEtime, nInterval, objDataset) {
	if (objDataset == null) {
		alert("DataSet이 지정되지 않았습니다.\n==>makeTimeList");
		return;
	}
	objDataset.clearData();
	var v_hour = 0;
	var v_time = 0;
	var vv_time = 0;
	var v_cnt = 0;
	var temp;
	v_cnt = objDataset.size();
	var shour = g_substr(strEtime, 0, 2);
	for ( var i = 0; i <= shour; i++) {
		hour = g_lpad(i + "", "0", 2);
		for ( var j = 0; j < 60; j += eval(nInterval)) {
			v_time = hour + g_lpad(j + "", "0", 2);
			vv_time = hour + ":" + g_lpad(j + "", "0", 2);
			if (strStime <= v_time && strEtime >= v_time) {
				temp = [ v_time, vv_time ];
				objDataset.addRow(temp);
				v_cnt++;
			}
		}
	}
}

/**
 * 일자 포멧 변경 ('YYYYMMDD'와 '월' 을 변수로 넘겨받아서 'YYYY년 MM월 DD일 [월]'로 반환)
 * 
 * @param strDate
 *            날짜정보(YYYYMMDD)
 * @param strWeekName
 *            요일(월,화,수,목,금,토,일)
 * @returns {String}
 */
function g_setDateViewFormat(strDate, strWeekName) {
	return strDate.substr(0, 4) + "년 " + strDate.substr(4, 2) + "월 "
			+ strDate.substr(6, 2) + "일 [" + strWeekName + "]";
}

/**
 * 요일텍스트색상반환
 * 
 * @param strDayInfo
 *            날짜정보
 * @param strHolidayInfo
 *            휴일여부
 * @returns {String}
 */
function g_setHolidayColor_two(strDayInfo, strHolidayInfo) {
	if (strHolidayInfo == "*")
		return '#C23F6A';
	else if (strDayInfo == "토" || strDayInfo == "6")
		return '#008194';
	else if (strDayInfo == "일" || strDayInfo == "0")
		return '#C23F6A';
	else
		return 'white';
}

/**
 * 요일배경색상반환
 * 
 * @param strDayInfo
 *            날짜정보
 * @param strHolidayInfo
 *            휴일정보
 * @param strDefColor
 *            color code
 * @returns
 */
function g_setHolidayBgColor(strDayInfo, strHolidayInfo, strDefColor) {
	if (strHolidayInfo == "*")
		return '#fbe4e4';
	else if (strDayInfo == "토" || strDayInfo == "6")
		return '#dbf7fb';
	else if (strDayInfo == "일" || strDayInfo == "0")
		return '#fbe4e4';
	else
		return (strDefColor == null || typeof (strDefColor) == "undefined" || typeof (strDefColor) == "string"
				&& strDefColor.length == 0) ? 'white' : strDefColor;
}

/**
 * 입력값이 음수이면 color 를 red로 세팅...
 * 
 * @param varValue
 * @returns {String}
 */
function g_setFontStyle(varValue) {
	if (varValue.indexOf("-") > -1) {
		return "red";
	} else {
		return "black";
	}
}

/**
 * 월 Format...
 * 
 * @param strMonth
 *            년월
 * @param strDelimiter
 *            구분자(디폴트 -)
 * @returns {String}
 */
function g_getSplitMonth(strMonth, strDelimiter) {

	if (typeof (strMonth) != "string" || strMonth.length == 0) {
		return "";
	}
	if (strMonth.length < 6) {
		return strMonth;
	}

	if (strDelimiter.length == 0) {
		strDelimiter = "-";
	}

	return strMonth.substr(0, 4) + strDelimiter + strMonth.substr(4, 2);
}

/**
 * 날짜 Format...
 * 
 * @param strDate
 *            년월일
 * @param strDelimiter
 *            구분자(디폴트 -)
 * @returns {String}
 */
function g_getSplitDate(strDate, strDelimiter) {

	if (strDate == null || strDate == "") {
		return "";
	}
	if (strDate.length != 8) {
		return strDate;
	}

	if (strDelimiter == null || strDelimiter == "") {
		strDelimiter = "-";
	}

	return strDate.substr(0, 4) + strDelimiter + strDate.substr(4, 2)
			+ strDelimiter + strDate.substr(6, 2);
}

/**
 * 시간 Format...
 * 
 * @param strStime
 *            시분초
 * @param strDelimiter
 *            구분자(디폴트 -)
 * @returns {String}
 */
function g_getSplitTime(strStime, strDelimiter) {
	if (strDelimiter == null || strDelimiter == "") {
		strDelimiter = ":";
	}

	if (strStime == null || strStime == "") {
		return "";
	} else if (strStime.length == 6) {
		return strStime.substr(0, 2) + strDelimiter + strStime.substr(2, 2)
				+ strDelimiter + strStime.substr(4, 2);
	} else if (strStime.length == 4) {
		return strStime.substr(0, 2) + strDelimiter + strStime.substr(2, 2);
	}

	return strStime;
}

/**
 * 마지막 일자 세팅...
 * 
 * @param strDate
 *            입력일자
 * @returns {String}
 */
function g_getLastDate(strDate) {

	var i_lastday = "";
	var s_date = strDate.replace(/-/gim, "").replace(/\//gim, "");

	var tmp = "31,28,31,30,31,30,31,31,30,31,30,31";
	var arr_days = tmp.split(",");

	var i_year = eval(s_date.substr(0, 4));
	var i_month = eval(s_date.substr(4, 2));

	if (i_month == "2") {
		if (i_year % 4 == 0 && i_year % 100 != 0 || i_year % 400 == 0)
			i_lastday = 29;
		else
			i_lastday = 28;
	} else {
		i_lastday = arr_days[i_month - 1];
	}

	return i_lastday;
}

/**
 * 두자리로 왼쪽에 0 padding
 * 
 * @param varNum
 * @returns
 */
function g_make2No(varNum) {
	if (varNum < 10)
		varNum = "0" + varNum;
	return varNum;
}

/**
 * 초를 시분초로 변환
 * 
 * @param varSec
 *            초
 * @returns {String}
 */
function g_Sec2Time(varSec) {
	var nSec = parseFloat(varSec);
	var k = "";
	if (nSec < 0) {
		nSec = Math.abs(nSec);
		k = "-";
	}
	var hour = Math.floor(nSec / 3600);
	var min = Math.floor((nSec % 3600) / 60);
	var sec = Math.floor((nSec % 3600) % 60);

	var rtn = k + g_make2No(hour) + ":" + g_make2No(min) + ":" + g_make2No(sec);

	return rtn;
}

/**
 * 초를 시간단위로 변환
 * 
 * @param varSec
 *            초
 * @returns
 */
function g_Sec2Hour(varSec) {
	return Math.round(parseFloat(varSec) / 3600);
}

/**
 * 초를 분단위로 변환
 * 
 * @param varSec
 *            초
 * @returns
 */
function g_Sec2Min(varSec) {
	return Math.round(parseFloat(varSec) / 60);
}

/**
 * 
 * @param psec
 * @returns {String}
 */
function g_Sec2Time3(psec) {
	psec = parseFloat(psec);
	var min = Math.floor((psec) / 60);
	var sec = Math.floor((psec) % 60);

	var rtn = g_make2No(min) + ":" + g_make2No(sec);

	return rtn;
}

/**
 * 
 * @param value
 * @returns {Date}
 */
function g_String2Date(value) {
	if (typeof (value) != "string") {
		return null;
	}

	if (value.length != 8) {
		return null;
	}

	var yyyy = parseInt(value.substr(0, 4));
	var mm = parseInt(value.substr(4, 2));
	var dd = parseInt(value.substr(6, 2));
	return new Date(yyyy, mm - 1, dd);
}

/**
 * 
 * @param value
 * @param gubun
 *            default Number, en English, kr Korea
 * @returns day
 */
function g_getDay(value, gubun) {
	if (typeof (value) != "string") return null;

	if (value.length != 8) return null;

	gubun = ((gubun == null || typeof (gubun) == "undefined") ? "number" : gubun);

	var day = g_String2Date(value).getDay();

	if ((day == null || typeof (day) == "undefined")) return null;

	var weekday_en = new Array(7);
	weekday_en[0] = "Sunday";
	weekday_en[1] = "Monday";
	weekday_en[2] = "Tuesday";
	weekday_en[3] = "Wednesday";
	weekday_en[4] = "Thursday";
	weekday_en[5] = "Friday";
	weekday_en[6] = "Saturday";

	if (gubun == "en")
		return weekday_en[day];

	var weekday_kr = new Array(7);
	weekday_kr[0] = "일";
	weekday_kr[1] = "월";
	weekday_kr[2] = "화";
	weekday_kr[3] = "수";
	weekday_kr[4] = "목";
	weekday_kr[5] = "금";
	weekday_kr[6] = "토";

	if (gubun == "kr")
		return weekday_kr[day];

	return day;
}

/**
 * 해당월의 마지막일자를 가져온다.
 * @param yyyymm
 * @returns
 */
function g_getLastDay(yyyymm) {
	var v_year, v_month, v_last_day;

	v_year = parseInt(g_substr(yyyymm, 0, 4));
	v_month = parseInt(g_substr(yyyymm, 4, 2));

	if (v_month < 1 || v_month > 12) {
		return -1;
	}

	if (v_month == 2) {
		if ((v_year % 4) == 0 && (v_year % 100) != 0 || (v_year % 400) == 0) {
			v_last_day = 29;
		} else {
			v_last_day = 28;
		}
	} else if (v_month == 4 || v_month == 6 || v_month == 9 || v_month == 11) {
		v_last_day = 30;
	} else {
		v_last_day = 31;
	}

	return v_last_day;
}

/**
 * 두 일자 사이의 일자를 가져온다.
 * @param yyyymmdd, yyyymmdd
 * @returns
 */
function betweenDate(startDt, endDt) {
    var between = [];
    var currentDate = new Date(startDt.substring(0,4)+'-'+startDt.substring(4,6)+'-'+startDt.substring(6,8)), //new Date(parse(startDt)),
        end = new Date(endDt.substring(0,4)+'-'+endDt.substring(4,6)+'-'+endDt.substring(6,8)); //new Date(parse(endDt));

    while (currentDate <= end) {
    	var y = currentDate.getFullYear().toString();
    	var m = ( currentDate.getMonth()+1< 10 ? "0"+(currentDate.getMonth()+1) : ""+(currentDate.getMonth()+1) ).toString();
    	var d = ( currentDate.getDate()< 10 ? "0"+currentDate.getDate() : ""+currentDate.getDate() ).toString();
        between.push(y+m+d); //new Date(currentDate).yyyymmdd());
        currentDate.setDate(currentDate.getDate() + 1);
    }
      
    return between;
}



Date.isLeapYear = function (year) { 
    return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0)); 
};

Date.getDaysInMonth = function (year, month) {
    return [31, (Date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
};

Date.prototype.isLeapYear = function () { 
    return Date.isLeapYear(this.getFullYear()); 
};

Date.prototype.getDaysInMonth = function () { 
    return Date.getDaysInMonth(this.getFullYear(), this.getMonth());
};

Date.prototype.addMonths = function (value) {
    var n = this.getDate();
    this.setDate(1);
    this.setMonth(this.getMonth() + value);
    this.setDate(Math.min(n, this.getDaysInMonth()));
    return this;
};

function monthDiff(d1, d2) {
	var between = [];
	var start = new Date(d1.substring(0,4)+'-'+d1.substring(4,6)+'-'+d1.substring(6,8)),
	end = new Date(d2.substring(0,4)+'-'+d2.substring(4,6)+'-'+d2.substring(6,8));

	start = new Date(start.setMonth(start.getMonth()));
	end = new Date(end.setMonth(end.getMonth()));
																																																														
	while (start < end) {
		var y = start.getFullYear();
		var m = ( start.getMonth()< 9 ? "0"+(start.getMonth()+1) : ""+(start.getMonth()+1) );
		between.push( y + m );
		
		start.addMonths(1);
	}
	return between;
}

	
function parse(str) {
    if(!/^(\d){8}$/.test(str)) return "invalid date";
    var y = str.substr(0,4),
        m = str.substr(4,2),
        d = str.substr(6,2);
    return new Date(y,m,d);
}

Date.prototype.yyyymmdd = function()
{
    var yyyy = this.getFullYear().toString();
    var mm = this.getMonth().toString();
    var dd = this.getDate().toString();

    return yyyy + (mm[1] ? mm : '0'+mm[0]) + (dd[1] ? dd : '0'+dd[0]);
};

Date.prototype.yyyymm = function()
{
    var yyyy = this.getFullYear().toString();
    var mm = this.getMonth().toString();

    return yyyy + (mm[1] ? mm : '0'+mm[0]);
};

/**
 * 추이 데이터셋에 비어있는 일자와 value를 지정한다.(일자별은 휴일도 제외시킴...)
 * @param ds, yyyymmdd, yyyymmdd
 * @returns
 */
function setDate(obj, gubun, sdate, edate){
	var v_ds_holiday = global("g_ds_holiday");
	var _ds_holiday=[];

	for(var i=0; i<v_ds_holiday.length; i++) {
		if(Number(sdate) <= Number(v_ds_holiday[i]) && Number(edate) >= Number(v_ds_holiday[i])) {
			_ds_holiday.push(v_ds_holiday[i]);
		}
	}
	
	return _callBack(obj, gubun, sdate, edate,_ds_holiday);
		
}

function _callBack(obj, gubun, sdate, edate, _ds_holiday){
	var col = Xwing.getDataset(obj).getColumnInfo();
	var dateIdx = Xwing.getDataset(obj).getColumnIndex('DATE');
	var curDate = [], newDate = [];
	var week = new Array('일', '월', '화', '수', '목', '금', '토');

	if(gubun == "D"){ 
		var tmpDate = betweenDate(sdate, edate);
		for(var i=0; i<Xwing.getDataset(obj).size(); i++)
			curDate.push(Xwing.getDataset(obj).getValue(i, 'DATE'));
		
		newDate = tmpDate.diff(_ds_holiday);
		newDate = newDate.diff(curDate);

		for(var i=0; i<newDate.length; i++){ //휴일이 아닌데도 건수가 0
				Xwing.getDataset(obj).addRow();
				Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, 'DATE', newDate[i]);
				for(var c=0; c<col.length; c++){
					if(c!=dateIdx){ 
						Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, col[c], 0);
					
					}
				}
		}
		
		for(var i=0; i<Xwing.getDataset(obj).size(); i++){ //요일붙이기
			var tmp = Xwing.getDataset(obj).getValue(i, 'DATE');
			var tmp = tmp.substr(0,8);
			var yoil = new Date(tmp.substr(0,4)+"-"+tmp.substr(4,2)+"-"+tmp.substr(6,2)).getDay();
			var yoilname = week[yoil];
			Xwing.getDataset(obj).setValue(i, 'DATE', tmp+ yoilname);
		}
	}
	
	else if(gubun == "W"){
		var obj2 = Xwing.getDataset(obj);
		setAddColumn(obj2);
		obj2.addColumn("WEEK_CNT");
		obj2.addColumn("DATE_NM");
		for(var i = 0, len = obj2.size(); i<len; i++){
			var dd = obj2.getValue(i,"DATE");
			dd = dd.substring(0,6); //201801
			var week = weekFunc.getCountWeekOfMonthTsst(obj2.getValue(i,"DATE")).toString();
			obj2.setValue(i,"WEEK_CNT",week);
			obj2.setValue(i,"DATE_NM",dd + "(" + week +"주)");
		}
		
		// 주 데이터셋 생성
		var _diffWeekObj;
		var _diffWeekObj = Xwing.createDataset('DS_DIFF_WK');
		_diffWeekObj.setColumnInfo([ 'DATE_NM','SORT_KEY' ]);//월에 해당하는 마지막 주차
		FUNC_XOBJECT.push(eval("$DS_DIFF_WK= Xwing.getDataset('DS_DIFF_WK')"));
		var tmpDate =  monthDiff(sdate, edate);
		var date = "", week = "", date_nm="", sort_key = 0;
		for(var i = 0, len = tmpDate.length; i<len ; i++){
			var weekCnt = weekFunc.getWeekCountOfMonth(tmpDate[i]);
			if(tmpDate[i] == edate.substring(0,6)){
				weekCnt=weekFunc.getCountWeekOfMonthTsst(edate);			}
			
			for(var j =1; j<=weekCnt; j++){
				_diffWeekObj.addRow();
				date = tmpDate[i];
				week = j;
				
				date_nm = date + "(" + week +"주)";
				_diffWeekObj.setValue(_diffWeekObj.size()-1,"DATE_NM",date_nm);
				_diffWeekObj.setValue(_diffWeekObj.size()-1,"SORT_KEY",sort_key++);
			}
		}
	}
	
	else if(gubun == "M"){
		var tmpDate = monthDiff(sdate, edate);
		for(var i=0; i<Xwing.getDataset(obj).size(); i++) 
			curDate.push(Xwing.getDataset(obj).getValue(i, 'DATE'));
		
		newDate = tmpDate.diff(curDate);
		for(var i=0; i<newDate.length; i++){
			Xwing.getDataset(obj).addRow();
			Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, 'DATE', newDate[i]);
			for(var c=0; c<col.length; c++){
				if(c!=dateIdx) Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, col[c], 0);
			}
		}
		
	}
	
	else if(gubun == "H"){
		var date = sdate.substring(0,8); 
		var stime = sdate.substring(8,10); 
		var etime = edate.substring(8,10); 
		
		for(var i=0; i<Xwing.getDataset(obj).size(); i++) 
			curDate.push(Xwing.getDataset(obj).getValue(i, 'DATE'));

		for(var i=0; i<_stime.length; i++){
			if(stime <= _stime[i][0] && etime >= _stime[i][0]){ //시간대 들어이쒀. 활용하자!
				if(!curDate.contains(date+_stime[i][0])){
					newDate.push(date+_stime[i][0]);
				}
			} 
		}
		
		for(var i=0; i<newDate.length; i++){
			Xwing.getDataset(obj).addRow();
			Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, 'DATE', newDate[i]);
			for(var c=0; c<col.length; c++){
				if(c!=dateIdx) Xwing.getDataset(obj).setValue(Xwing.getDataset(obj).size()-1, col[c], 0);
			}
		}
	}
	Xwing.getDataset(obj).sort('DATE');
	return true; 	
}

function setAddColumn(obj){
	if(obj.addColumn == undefined){
		obj.addColumn = function(sColumn) {
				this.setColumnInfo(this.getColumnInfo().concat(sColumn));
				return this.getColumnInfo();
			};
		}
}

//데이터없는 주 데이터 0으로 삽입 
function noDataWeekInsert(tmp_obj,obj){ 
	setAddColumn(tmp_obj);
	 var tmp_date_nm = [];
	 var w_obj = $DS_DIFF_WK;
	 for(var j = 0, len = tmp_obj.size(); j<len; j++ ){ //현재 템프 데이트에 존재하는 날짜들.
		 var dd = tmp_obj.getValue(j,"DATE_NM");
		 if(tmp_date_nm.indexOf(dd) == -1) tmp_date_nm.push(dd); 
	 }
	 
	tmp_obj.addColumn("SORT_KEY");
	var col = tmp_obj.getColumnInfo();
	var dateIdx = tmp_obj.getColumnIndex('DATE');
	var dateIdx2 = tmp_obj.getColumnIndex('DATE_NM');
	
for(var i =0, len= tmp_date_nm.length; i<len; i++){
	 var idx = w_obj.indexOfRow("DATE_NM",tmp_date_nm[i]);
	 var idx2 = tmp_obj.indexOfRow("DATE",tmp_date_nm[i]);
	 
	 tmp_obj.setValue(idx2,"SORT_KEY",w_obj.getValue(idx,"SORT_KEY")); 
	}
	
	
	 for(var i =0, len = w_obj.size(); i<len; i++){
		 if(tmp_date_nm.indexOf(w_obj.getValue(i,"DATE_NM")) == -1){
			 tmp_obj.addRow();
			 for(var c=0; c<col.length; c++){
					if(c!=dateIdx&&c!=dateIdx2) tmp_obj.setValue(tmp_obj.size()-1, col[c], 0);
				}
			 tmp_obj.setValue(tmp_obj.size()-1, 'DATE', w_obj.getValue(i,"DATE_NM"));
			 tmp_obj.setValue(tmp_obj.size()-1, 'DATE_NM', w_obj.getValue(i,"DATE_NM"));
			 tmp_obj.setValue(tmp_obj.size()-1, 'SORT_KEY', w_obj.getValue(i,"SORT_KEY"));		
		 }
	 }
	 tmp_obj.sort("SORT_KEY");
	 obj.clear();
	 obj.copyFrom(tmp_obj);
}

Array.prototype.contains = function(needle){
	  for (var i=0; i<this.length; i++)
	    if (this[i] == needle) return true;

	  return false;
};

Array.prototype.diff = function(compare) {
	if( this && this.filter) return this.filter(function(elem) {return !compare.contains(elem);});
	else return [];
};

function getIntersect(arr1, arr2) {
    var temp = [];
    for(var i = 0; i < arr1.length; i++){
        for(var k = 0; k < arr2.length; k++){
            if(arr1[i] == arr2[k]){
                temp.push( arr1[i]);
                break;
            }
        }
    }
    return temp;
}

/**
 * 일자 사이의 일수를 계산한다.
 * @param yyyymmdd, yyyymmdd
 * @returns
 */
function diffDate(sdate, edate){
	var num = 0;
    var s = new Date(parse(sdate)),
        e = new Date(parse(edate));
        
    while (s <= e) {
    	num++;
        s.setDate(s.getDate() + 1);
    }
    
    return num;
}


/**
* 날짜 조회조건 체크
* @param start_date
* @param end_date
*/

function g_checkDate(start_date, end_date) {
//	if(start_date== null || end_date == null || start_date== "" || end_date == "" ){
//		alert("조회 조건 날짜를 선택해주세요.");
//		return false;
//	}
	if(start_date>end_date){
		new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');
		return false;
	}
	return true;
}

function g_dateToParam(){
	var p_date = {};
	var gubun = Xwing.getCombo('cmb_gubun').getValue();
	
	switch(gubun){
		case "H":
			var s = Xwing.getCombo('stime').getValue();
			var e = Xwing.getCombo('etime').getValue();
			
			if(e < s){
				new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');
				return false;
			}else{
				p_date = {
					gubun: 'H',
					sdate: 	Xwing.getDatepicker('date').getValue(),
					edate: 	Xwing.getDatepicker('date').getValue(),
					stime:  s,
					etime:  e
				};
			}
		break;
		
		case "D":
			var s = Xwing.getDatepicker('sdate').getValue();
			var e = Xwing.getDatepicker('edate').getValue();
			
			if(e < s){
				new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');
				return false;
			}
			
			p_date = {
				gubun: 'D', sdate: s, edate: e
			};
		break;
			

		case "W":
			var s = Xwing.getDatepicker('swdate').getValue();
			var e = Xwing.getDatepicker('ewdate').getValue();
			
			if(e < s){
				new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');
				return false;
			}
			
			p_date = {
				gubun: 'W', sdate: s, edate: e
			};
		break;
			
		
		case "M":
			var s = Xwing.getSpin('syear').getValue() + Xwing.getCombo('smonth').getValue() + '01';
			var e = Xwing.getSpin('eyear').getValue() + Xwing.getCombo('emonth').getValue();
			e += g_getLastDay(Xwing.getSpin('eyear').getValue() + Xwing.getCombo('emonth').getValue());

			if(e < s){
				new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');
				return false;
			}
			
//			if(diffDate(s,e) > 915){
//				alert("31개월을 초과할 수 없습니다.");
//				return false;
//			} else{
				p_date = {
					gubun: 'M', sdate: s, edate: e
				};
//			}
		break;
	}
	
	return p_date;
}


//단순히 datePicker로 만든 combo 선택하기.
function checkDate(sdateId,edateId,stimeId,etimeId) {
	var result = false;
	var sdate,edate;
	
	var stime = stimeId.getValue();
	var etime = etimeId.getValue();
//	if(stime.length < 6 || etime.length < 6) {
//		new xwing.Dialog.alert("시간을 다시 설정해 주세요. ex)090000 6자리", '시간 설정 오류');	
//		return true;
//	}
	
	var shour = stime.substring(0,2);
	var smin = stime.substring(2,4);
	var ssec = stime.substring(4,6);
	
	var ehour = etime.substring(0,2);
	var emin = etime.substring(2,4);
	var esec = etime.substring(4,6);
	
	sdate = new Date(sdateId.getYear(), sdateId.getMonth(), sdateId.getDate(), shour, smin, ssec);
	edate = new Date(edateId.getYear(), edateId.getMonth(), edateId.getDate(), ehour, emin, esec);
		
		if (sdate > edate) {
			new xwing.Dialog.alert("종료일자가 시작일자보다 작습니다.", '시간 설정 오류');	
			Xwing.getEdit(stimeId).focus();
			result = true;
		}

	return result;	
}


/**
 * 오늘날짜 문자(YYYYMMDD)로 리턴
 * 
 * @param 
 * @param 
 * @returns {String}
 */
function checkDateFormat(){
	
	var result;
	var now = new Date();	
	var year  = now.getFullYear();
	var month = now.getMonth() + 1;
	var day   = now.getDate();
		
	result = year + (month<10?'0'+month:month).toString() + (day<10?'0'+day:day).toString() ;
	
	return result;
}

function lastMonth() {
	var now = new Date();
	var lastDay = new Date(now);

	var year;
	var month;
	var day;
	if(now.getDate() == 31 
			|| (now.getMonth() + 1 == 3 && now.getDate() == 29)
			|| (now.getMonth() + 1 == 3 && now.getDate() == 30)) {
		year = now.getFullYear();
		month = now.getMonth() + 1;
		day = "1";
	} else {
		lastDay.setDate(0);
		lastDay.setDate(now.getDate() + 1);
		
		year  = lastDay.getFullYear();
		month = lastDay.getMonth() + 1;
		day   = lastDay.getDate();

		// 윤년의 2월 29일의 최근 1달은 2/1 일로 세팅
		if(now.getMonth() == 1) {
			if((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 4)) { 
				month = 2;
				day = 1;
			}
		}
			
	}

	if (month <10) month = "0" + month;
	if (day < 10) day = "0" + day;
	
	result = year.toString() + month.toString() + day.toString();
	
	return result;
}

/*
 * 오늘로부터 몇일전
 */
function beforeToday(day){
	var now = new Date();
    var newDt = new Date(now);
	newDt.setDate( newDt.getDate() - day);
	
	var year  = newDt.getFullYear();
	var month = newDt.getMonth() + 1;
	var day   = newDt.getDate();
	
	if (month <10) month = "0" + month;
	if (day < 10) day = "0" + day;
	
	result = year.toString() + month.toString() + day.toString();
	
	return result;
}

/*week 때문에 넣음*/
var weekFunc = {

		/**
		 * 두 날짜의 주차수 차이를 반환한다.
		 * 
		 */
		getWeekNumOfTwoDate : function( ymd1, ymd2 )
		{
			// 두 날짜의 기준 목요일을 구한다.
			var thursdayYmd1 = this.getDateThursdayThisWeek( ymd1 );
			var thursdayYmd2 = this.getDateThursdayThisWeek( ymd2 );
			
			// 날짜의 크기별로 변수에 넣는다.
			var beforeYmd = thursdayYmd1;
			var afterYmd = thursdayYmd2;
			
			if( thursdayYmd1 > thursdayYmd2 ) {
				beforeYmd = thursdayYmd2;
				afterYmd = thursdayYmd1;
			}

			var periodWeek = 0;
			while( beforeYmd <= afterYmd ) {
				var beforeObj = this.getDateObjByYmd( beforeYmd );
				var d = this.getDateObjAddDate( beforeObj, 7 );
				beforeYmd = this.getYmdByDateObj( d );
				periodWeek++;
			}
		
			return periodWeek;
		},
		
		/**
		 * 해당월의 주차수를 반환한다.
		 * 
		 */
		getWeekNumOfMonthTsst : function( ymd )
		{
			// 이번달 1일을 셋팅한다.
			var thisMonthD = new Date( this.getModeValueByYmd( ymd, "year" ), getModeValueByYmd( ymd, "month" ) - 1, 1 );

			// 다음달 1일을 셋팅한다.
			var nextMonthD = new Date( this.getModeValueByYmd( ymd, "year" ), getModeValueByYmd( ymd, "month" ), 1 );

			// 이번달 1일에 해당하는 기준 목요일을 구한다.
			var thisThursdayYmd = this.getDateThursdayThisWeek( getYmdByDateObj( thisMonthD ) );
			
			// 다음달 1일에 해당하는 기준 목요일을 구한다.
			var nextThursdayYyyyMmDd = this.getDateThursdayThisWeek( getYmdByDateObj( nextMonthD ) );

			var periodWeek = 0;
			while( thisThursdayYmd <= nextThursdayYyyyMmDd ) {
				var thisThursdayObj = this.getDateObjByYmd( thisThursdayYmd );
				var d = this.getDateObjAddDate( thisThursdayObj, 7 );
				thisThursdayYmd = this.getYmdByDateObj( d );
				periodWeek++;
			}
		
			return periodWeek;
		},

		/**
		 * 해당월의 몇번째 주인지를 반환한다.
		 * 
		 */
		getCountWeekOfMonthTsst : function( ymd )
		{
			// 해당일의 기준이 되는 목요일을 구하자.
			var thisThursdayYmd = this.getDateThursdayThisWeek( ymd );
			
			// 해당월의 첫번째 목요일을 구하자.
			var firstThursdayYmd = this.getDateFirstWeekThursdayOfMonthTsst( ymd );

			var periodWeek = 0;
			while( firstThursdayYmd < thisThursdayYmd ) {
				var firstThursdayObj = this.getDateObjByYmd( firstThursdayYmd );
				var d = this.getDateObjAddDate( firstThursdayObj, 7 );
				firstThursdayYmd = this.getYmdByDateObj( d );
				periodWeek++;
			}
			
			return periodWeek + 1;
		},

		/**
		 * 해당년도의 몇번째 주차인지를 반환한다.
		 * 
		 */
		getCountWeekOfYearTsst : function( ymd )
		{		
			// 해당일의 기준이 되는 목요일을 구하자.
			var thisThursdayYmd = this.getDateThursdayThisWeek( ymd );
			
			// 기준이 되는 목요일의 년도를 구하자.
			var yyyy = this.getModeValueByYmd( thisThursdayYmd, "year" );
			
			// 해당년도의 첫번째 목요일을 구하자.
			var firstThursdayYmd = this.getDateFirstWeekThursdayOfYearTsst( yyyy );

			var periodWeek = 0;
			while( firstThursdayYmd < thisThursdayYmd ) {
				var firstThursdayObj = this.getDateObjByYmd( firstThursdayYmd );
				var d = this.getDateObjAddDate( firstThursdayObj, 7 );
				firstThursdayYmd = this.getYmdByDateObj( d );
				periodWeek++;
			}

			return periodWeek + 1;
		},
		
		/**
		 * 해당월의 첫주 시작 목요일을 구한다.
		 * 
		 */
		getDateFirstWeekThursdayOfMonthTsst : function( ymd )
		{
			// 해당월의 시작일(1월 1일)을 셋팅하기
			var firstDateYmd = ymd.substring(0, 6) +"01";
			var mm = this.getModeValueByYmd( ymd, "month" );
			
			// 기준이 되는 목요일을 구하자.
			var firstThursdayYmd = this.getDateThursdayThisWeek( firstDateYmd );
			var firstThursdayMm = this.getModeValueByYmd( firstThursdayYmd, "month" );
			
			// 기준이 되는 목요일이 작년이라면 7일을 더하자.
			if( firstThursdayMm != mm ) {
				var firstThursdayObj = this.getDateObjByYmd( firstThursdayYmd );
				var d = this.getDateObjAddDate( firstThursdayObj, 7 );
				firstThursdayYmd = this.getYmdByDateObj( d );
			}
			
			return firstThursdayYmd;
		},
		
		/**
		 * 해당월의 마지막주 종료 목요일을 구한다.
		 * 
		 */
		getDateLastWeekThursdayOfMonthTsst : function( ymd )
		{
			// 해당월의 마지막날짜를 셋팅하자
			var lastD = new Date( this.getModeValueByYmd( ymd, "year" ), this.getModeValueByYmd( ymd, "month" ), 0 );
			
			var mm = this.getModeValueByYmd( ymd, "month" );
			var lastDateYmd = this.getYmdByDateObj( lastD );
			
			// 기준이 되는 목요일을 구하자.
			var lastThursdayYmd = this.getDateThursdayThisWeek( lastDateYmd );
			var lastThursdayMm = this.getModeValueByYmd( lastThursdayYmd, "month" );
			
			// 기준이 되는 목요일이 다음월이라면 7일을 빼자.
			if( lastThursdayMm != mm ) {
				var lastThursdayObj = this.getDateObjByYmd( lastThursdayYmd );
				var d = this.getDateObjAddDate( lastThursdayObj, -7 );
				lastThursdayYmd = this.getYmdByDateObj( d );
			}
			
			return lastThursdayYmd;
		},
		
		/**
		 * 해당년도의 첫주 시작 목요일을 구한다.
		 * 
		 */
		getDateFirstWeekThursdayOfYearTsst : function( yyyy )
		{
			// 해당년의 시작일(1월 1일)을 셋팅하기
			var firstDateYmd = yyyy +"0101";
			
			// 기준이 되는 목요일을 구하자.
			var firstThursdayYmd = this.getDateThursdayThisWeek( firstDateYmd );
			var firstThursdayYyyy = this.getModeValueByYmd( firstThursdayYmd, "year" );
			
			// 기준이 되는 목요일이 작년이라면 7일을 더하자.
			if( firstThursdayYyyy != yyyy ) {
				var firstThursdayObj = this.getDateObjByYmd( firstThursdayYmd );
				var d = this.getDateObjAddDate( firstThursdayObj, 7 );
				firstThursdayYmd = this.getYmdByDateObj( d );
			}
			
			return firstThursdayYmd;
		},
		
		/**
		 * 해당년도의 마지막주 종료 목요일을 구한다.
		 * 
		 */
		getDateLastWeekThursdayOfYearTsst : function( yyyy )
		{
			// 해당년도의 마지막날짜(12월 31일)을 셋팅하자
			var lastDateYmd = yyyy +"1231";
			
			// 기준이 되는 목요일을 구하자.
			var lastThursdayYmd = this.getDateThursdayThisWeek( lastDateYmd );
			var lastThursdayYyyy = this.getModeValueByYmd( lastThursdayYmd, "year" );
			
			// 기준이 되는 목요일이 내년이라면 7일을 빼자.
			if( lastThursdayYyyy != yyyy ) {
				var lastThursdayObj = this.getDateObjByYmd( lastThursdayYmd );
				var d = this.getDateObjAddDate( lastThursdayObj, -7 );
				lastThursdayYmd = this.getYmdByDateObj( d );
			}
			
			return lastThursdayYmd;
		},
		
		/** 
		 * 해당일의 년도를 가져오자.
		 */
		getYearByYmd : function( ymd )
		{
			var thursdayYmd = this.getDateThursdayThisWeek( ymd );
			return thursdayYmd.substring(0, 4);
		},
		
		/** 
		 * 한주의 기준이 되는 목요일을 구하자.
		 * - 목요일을 기준으로 앞뒤 3일이 한주가 된다.
		 */
		getDateThursdayThisWeek : function( ymd )
		{
			var yyyy = this.getModeValueByYmd( ymd, "year" );
			var mm = this.getModeValueByYmd( ymd, "month" ) - 1;
			var dd = this.getModeValueByYmd( ymd, "date" );

			var d = new Date(yyyy, mm, dd);

			var dayOfWeek = d.getDay();		// 일요일~토요일(0~6)
			var addDate = 0;

			// 일요일은 3일을 빼서 목요일을 구한다.
			if( dayOfWeek == 0 ) addDate = -3; 
			else addDate = 4 - dayOfWeek;

			// 구한날짜만큼 더한다.
			var thursdayD = this.getDateObjAddDate( d, addDate );
			
			return this.getYmdByDateObj( thursdayD );
		},
		
		/** 
		 * yyyyMmDd 입력받아서 년/월/일 반환하기
		 * 
		 */
		getModeValueByYmd : function( ymd, mode ) {
			var result = ymd;
			
			if( ymd && ymd.length == 8 ) {
				if( mode == "year" ) result = ymd.substring(0, 4);
				if( mode == "month" ) result = ymd.substring(4, 6);
				if( mode == "date" ) result = ymd.substring(6, 8);
				
				if( result ) result = parseInt( result );
			}
			
			return result;
		},
		
		/** 
		 * 날짜객체 입력받아서 yyyyMMdd 반환하기
		 * 
		 */
		getYmdByDateObj : function( dateObj ) {
			
			var yyyy = dateObj.getFullYear();
			var mm = dateObj.getMonth() + 1;
			var dd = dateObj.getDate();
			
			if( mm < 10 ) mm = "0"+ mm;
			if( dd < 10 ) dd = "0"+ dd;
			
			var result = yyyy +""+ mm +""+ dd;

			return result;
		},
		
		/** 
		 * yyyyMMdd 입력받아서 날짜객체 반환하기
		 * 
		 */
		getDateObjByYmd : function( ymd ) {
			
			var yyyy = this.getModeValueByYmd( ymd, "year" );
			var mm = this.getModeValueByYmd( ymd, "month" ) - 1;
			var dd = this.getModeValueByYmd( ymd, "date" );

			var resultD = new Date( yyyy, mm, dd);
			return resultD;
		},
		
		/** 
		 * 날짜객체 입력 받아서 해당날짜만큼 더해서 날짜객체 반환하기
		 * 
		 */
		getDateObjAddDate : function( dateObj, addDate ) {
			
			var resultD = new Date( dateObj.getFullYear(), dateObj.getMonth(), dateObj.getDate() + addDate );
			
			return resultD;
		},
		
		/**
		 * 해당월 주의 최대값
		 * @param dateStr       YYYYMM
		 */
		getWeekCountOfMonth : function(dateStr) {
		    var year  = Number(dateStr.substring(0, 4));
		    var month = Number(dateStr.substring(4, 6));
		     
		    var nowDate = new Date(year, month-1, 1);
		 
		    var lastDate = new Date(year, month, 0).getDate();
		    var monthSWeek = nowDate.getDay();
		 
		    var weekSeq = parseInt((parseInt(lastDate) + monthSWeek - 1)/7) + 1;
		 
		    return weekSeq;
		}

	}