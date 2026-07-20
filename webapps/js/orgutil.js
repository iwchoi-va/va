var _obj; // 조직 콤보를 담을 Panel
var _org_cnt = 3; // 보여질 조직 개수
var org_muti_yn = "N"; // multiselect Y/N
var _user_yn = "N"; // user view Y/N
var _first_code = "%"; // first code
var _first_text = "전체"; // first codename
var _org_all_yn = "N"; //조직전체..
var _first_code_yn = "N";
var authFlag = "N"; //권한에 따른 사용자 ID 세팅 구분값

var _comboObjects = []; // org combo objects
var _datasetObjects = []; // org dataset objects (Filter)
var _datasetObjects_o = []; // org dataset objects (full)
var _ds_dept; // parent dept dataset
var _ds_user; // user dataset
var _cmb_user; // user combo

var sel_user; //user값 설정

var _arr_org_dept_name = [];
var _arr_user_dept = [];
var ahthLevel ="",authMuti = "", mutiCntr="";


/*********************************************************************
 * Vsens용 소속설정 컴포넌트 생성 js
 * 소속 label 별도로 생성해야함.
 * component : panel(id:pan_org, width:420, height: 25, top: 15, left: 80)
 * element 생성: g_createOrg("pan_org", 3, "N", "Y", "Y", "", "전체");
 *********************************************************************/
//위젯 그리기용
var _left = 0;
var defalut_left = 0;
var _top = 15;
var _height = 25;
var _width = 100;
var _space = 5;

/**********************************Add Variable**********************************/
var _b_forced = false; // 특정화면에서 조회번위가 다른 경우 DB조회를 새로 해야함. 조회후 Global에 저장하지 않음.
var _s_org_sch_yn = ""; // 스케줄링 화면일 경우 정해진 로직에 의해 default 조직구조 세팅을 한다.
var _s_org_user = "N"; // 'Y'인 경우 사용자목록에 자신만 조회
var _org_all_user_yn = "N"; // 'Y'로 설정시 조직정보 전체를 조회함. 초기 1회만 사용하는 필터링 조건
var _org_all_user_yn2 = "N"; // 매회 콤보 클릭시 사용하는 필터링 조건
var _userdef_chk_yn = "N"; // 소스상 정의한 조직정보를 설정하는 기능관련 컬럼...(전체 제거 안함)
var _org_grade_order_yn = "N"; // 휴가신청 화면에서 user_combo 구성시 팀장이 먼저 세팅되고 그 뒤에 이름별 사원이 세팅 된다...
var _s_org_qa_chg_yn = "N"; // 품질관리 -> 코칭 -> 코칭결과등록[qms063M.xml] 에서 조직구조를 변경하면 조직조건에 맞는 QA만 조회되게..
var _gorg_yn = true; //global userid 값 셋팅여부
var _org_filter = false; //센터값 필터링(TM 본부만 조회되도록 설정)
var _auth_filter = true; //권한 필터링
/*********************************************************************************/
global = window.opener==null?global:window.opener.global;
/*********************************************************************************/

function g_createOrg(obj, org_cnt, muti_yn, user_yn, org_all_yn, first_code, first_text, gorg_yn, org_filter, auth_filter) {
	//alert(_global("G_USER_GRADE"));
	_obj = Xwing.getPanel(obj);
	_org_cnt = _nvl(org_cnt, _org_cnt);
	org_muti_yn = _nvl(muti_yn, org_muti_yn);
	_user_yn = _nvl(user_yn, _user_yn);
	_org_all_yn = _nvl(org_all_yn, _org_all_yn);
	_first_code = _nvl(first_code, _first_code);
	_first_code_yn = _decode(_isNull(first_code), true, "N", "Y"); //fisrt_code가 널이면 N, 널이아니면 Y
	_first_text = _nvl(first_text, _first_text);
	_gorg_yn = _nvl(gorg_yn, _gorg_yn);
	_org_filter = _nvl(org_filter, _org_filter);
	_auth_filter = _nvl(auth_filter, _auth_filter);
	var _use_all = "NOT_ALL";
	var _deptcode_colid = "DEPT";
	var _user_except_deptcd = ""; // 사용자의 의미상 deptcode
	var _user_except_deptcd2 = ""; // ALL인 경우

	if(_org_all_yn == "Y"){
		_deptcode_colid = "ALL";
		_ds_dept = _isNull(_global("$DS_DEPT_LIST_ALL"))?window.opener.$DS_DEPT_LIST_ALL:_global("$DS_DEPT_LIST_ALL");
	}else{
		_ds_dept = _isNull(_global("$DS_DEPT_LIST"))?window.opener.$DS_DEPT_LIST:_global("$DS_DEPT_LIST");
	}

	for ( var i = 0; i <= _org_cnt; i++) {
		if (i < _org_cnt) {
			_arr_org_dept_name[i] = "org" + (i + 1);
			_arr_user_dept[i] = _decode((i + 1), 1, _global("G_ORG1_CD"), 2,
					_global("G_ORG2_CD"), 3, _global("G_ORG3_CD"), 4,
					_global("G_ORG4_CD"), 5, _global("G_ORG5_CD"), 6,
					_global("G_ORG6_CD"));
		}

		if (i == _org_cnt) {
			_arr_org_dept_name[i] = "user";
		}
	}

	// 특정 화면에서 조직정보 범위가 예외적인 경우는
	// 조직정보를 새로 조회해야하고, 이를 Global에 저장하지 않아야함.
	if (_isEmpty(_deptcode_colid)) {
		_deptcode_colid = "DEPT_CD";
	} else {
		_deptcode_colid += "_CD";
	}
	var v_filter = "";
	if(_org_filter){
		v_filter = "Y";
		//_b_forced = true; // 특정화면에서만 사용하기위한 DB조회
	}

	var dept_cd = [];
	
	var tmp_authMuti = top.$DS_GRADEAUTH.getValue(top.$DS_GRADEAUTH.indexOfRow("CODEID",global("G_USER_GRADE")),"ETC2");
	var tmp_org2 = global("G_ORG2_CD");
	var tt = top.$DS_MUTICNTR.indexOfRow("CODEID",tmp_org2);
	if(tt > -1 && tmp_authMuti == "Y" && _auth_filter){
		authMuti = "Y";
	}
	
	if (_ds_dept.size() != 0 && _b_forced == false) {
		_callback();
	} else {
		var _param = {
			_sqlName : "cs.com.deptlist.sel",
			dept_code : _global("G_DEPT_CD"),
			group_code : _global("G_ORG1_CD"),
			empno : _global("G_EMP_NO"),
			use_all : _use_all,
			org_user : _s_org_user,
			deptcode_colid : _deptcode_colid,
			filter : v_filter,
			filter2 : authMuti
		};

		var opt = {
			sReqId : "_orgutil",
			url : "service::cs.com.orgutil.do",
			param : _param,
			indicator : false
			//indicator : org_muti_yn == "Y" ? true : false
		};

		Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {
			_ds_dept.setData(oRes.__DEPT_LIST.column, oRes.__DEPT_LIST.record);
			_callback();
		});
	}
}

function _callback() {
	FUNC_XOBJECT = [];

	var _dataset_o, _datasetName_o;
	var _combo, _comboName, _dataset, _datasetName;

	/*
	var _height = parseInt(_obj.getHeight(), 10) - 2;

	var _width = Math.floor((parseInt(_obj.getWidth(), 10) - 10)
			/ (_user_yn == "Y" ? (_org_cnt + 1) : _org_cnt));
	var _left = 0;
	*/

	var _left = 0;
	var _height = 25;
	var _width = 100;

	for ( var i = 0; i < _org_cnt; i++) {
		_comboName = "cmb_org" + (i + 1);

		_datasetName_o = "_DS_DEPT" + (i + 1) + "_ORG";
		_dataset_o = Xwing.createDataset(_datasetName_o);
		_dataset_o.setColumnInfo([ 'CODEID', 'CODENAME', 'ETC1', 'ORDER' ]);
		_datasetObjects_o[i] = _dataset_o;
		FUNC_XOBJECT.push(eval("$" + _datasetName_o + "= Xwing.getDataset('" + _datasetName_o + "')"));

		_datasetName = "_DS_DEPT" + (i + 1);
		_dataset = Xwing.createDataset(_datasetName);
		_dataset.setColumnInfo([ 'CODEID', 'CODENAME', 'ETC1', 'ORDER' ]);
		_datasetObjects[i] = _dataset;
		FUNC_XOBJECT.push(eval("$" + _datasetName + "= Xwing.getDataset('" + _datasetName + "')"));

		_combo = Xwing.createCombo({
			id : _comboName,
			width : _width,
			height : _height,
			top : 0,
			left : _left,
			domaindataset : _datasetName,
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 20,
			change : "_dept_change" + i,
			multiselectable : "false",
			itemall : "false",
			ns : "xwing",
			flexibleitem : (_comboName == "cmb_org1" ? "true" : "false"),
			xw_type : "combo",
			itempadding: "0 10 0 5"
		});
		_left = (_left + _space) + _width;
		_obj.appendChild(_combo);

		_comboObjects[i] = _combo;
		FUNC_XOBJECT.push(eval("$" + _comboName + "= Xwing.getCombo('" + _comboName + "')"));
	}

	if (_user_yn == "Y") {
		_datasetName = "_DS_USER";
		_ds_user = Xwing.createDataset(_datasetName);
		_ds_user.setColumnInfo([ 'CODEID', 'CODENAME', 'ETC1', 'DEPT_CODE', 'USER_GRADE', 'GRADE_ORDER', 'EMP_NO']);
		FUNC_XOBJECT.push(eval("$" + _datasetName + "= Xwing.getDataset('"
				+ _datasetName + "')"));

		_cmb_user = Xwing.createCombo({
			id : "cmb_user",
			width : _width,
			height : _height,
			top : 0,
			left : _left,
			domaindataset : "_DS_USER",
			domaincodecolumn : "CODEID",
			domaintextcolumn : "CODENAME",
			size : 20,
			change : "_user_change",
			multiselectable : (org_muti_yn == "Y" ? "true" : "false"),
			itemall : (org_muti_yn == "Y" ? "true" : "false"),
			ns : "xwing",
			flexibleitem : "true",
			xw_type : "combo",
			itempadding: "0 15 0 5"
		});
		_obj.appendChild(_cmb_user);
		FUNC_XOBJECT.push(eval("$cmb_user= Xwing.getCombo('cmb_user')"));
	}

	for ( var i = 0 ; i < _ds_dept.size(); i++) {
		var _dept_cd = [];
		var _dept_name = [];
		var _dept_order = [];
		for ( var j = 0; j < _org_cnt; j++) {
			_dept_cd[j] = _ds_dept.getValue(i, "DEPT" + j + "_CD");
			_dept_name[j] = _ds_dept.getValue(i, "DEPT" + j + "_NAME");
			_dept_order[j] = (j + 1);
			;
			if (!_isNull(_dept_cd[j]) && !_isEmpty(_dept_cd[j])) {
				if (_datasetObjects_o[j].indexOfRow("CODEID", _dept_cd[j]) == -1) {
					var etc1 = "";
					if (j > 0) {
						etc1 = _dept_cd[j - 1];
					}
					// _datasetObjects[j].addRow([ _dept_cd[j], _dept_name[j],
					// etc1, _dept_order[j] ]);
					_datasetObjects_o[j].addRow([ _dept_cd[j], _dept_name[j], etc1, _dept_order[j] ]);
				}
			}
		}
	}
	// 콤보 일반일 때, 각 콤보 최상위 목록 추가
	//if(org_muti_yn!="Y"){

		for(var i = 0; i < _datasetObjects_o.length; i++) {
			if(_s_org_user != "Y"){ _datasetObjects_o[i].insertRow(0, [ _first_code, _first_text, '%', 0 ]); }
		}
	//}

	_datasetObjects[0].copyFrom(_datasetObjects_o[0]);

	//권한정의
	if(_auth_filter){
		authLevel = top.$DS_GRADEAUTH.getValue(top.$DS_GRADEAUTH.indexOfRow("CODEID",global("G_USER_GRADE")),"ETC1");

		if(authLevel != 1){
			if(authMuti == "Y"){// && authLevel == 2 팀만 조회하게 되는 경우 없음.
				g_setAuthObj(authLevel, _global("G_ORG1_CD"));
			}else{
				g_setAuthObj(authLevel, _global("G_ORG1_CD"),_global("G_ORG2_CD"),_global("G_ORG3_CD"),_global("G_USER_ID"));
			}

		}else{
		  for ( var i = 0, j = 0; i < _comboObjects.length; i++) {
			j = i + 1;

			if (!_isNull(_global("G_ORG" + j + "_CD"))) {
				if(_gorg_yn){
					_comboObjects[i].setValue(_isEmpty(_global("G_ORG" + j + "_CD"))?'%':_global("G_ORG" + j + "_CD"));
				}else{
					_comboObjects[i].setValue(_first_code);
				}
				if(i==0 && _org_filter){
					eval("$cmb_org1.setValue('11002')");
				}
				eval("_common_dept_change(" + i + ")");
			} else {
				_comboObjects[i].setValue(_first_code);
				eval("_common_dept_change(" + i + ")");
			}
		}
		}
	}




	/*
	if(typeof(f_org_callback) == "function"){
		f_org_callback();
	}
	*/
}
function _common_dept_change(iLevel, event) {
	//alert(iLevel);
	for ( var idx = iLevel; idx < _datasetObjects_o.length; idx++) {

		if ((idx + 1) > (_datasetObjects_o.length - 1)) break;

		var _dataset_parent = _datasetObjects_o[idx];
		var _combo_parent = _comboObjects[idx];

		var _dataset_child = _datasetObjects_o[idx + 1];
		var _combo_child = _comboObjects[idx + 1];

		var _sfilter = "";

		_sfilter += "," + _combo_parent.getValue().replace(eval("/" + _first_code + "/gi"), "");

		if (_combo_parent.getValue() == null || _combo_parent.getValue() == _first_code ||
			_combo_parent.getValue().indexOf(_first_code) > -1 || _org_all_user_yn == "Y") {
			_sfilter = ",%" + _sfilter;
		}

		var _filter = _substr(_sfilter, 1).split(",");
		var _ds_temp;
		_datasetObjects[(idx + 1)].clearData();
		for ( var i = 0; i < _filter.length; i++) {
			_ds_temp = _dataset_child.getData("[ETC1=='" + _filter[i] + "']");
			for ( var j = 0; j < _ds_temp.length; j++) {
				_datasetObjects[idx + 1].addRow(_ds_temp[j]);
//				if (org_muti_yn == "Y") {   -- MULTI_YN 하면 상담사만 체크. 주석처리. 20180320
//					_comboObjects[idx + 1].setMultiall("true"); //setValue("");
//				} else {

					_comboObjects[idx + 1].setValue(_first_code);
//				}
			}
		}

			_combo_child.setValue(_first_code);


		/*
		if(_multi_yn!="Y"){
			_combo_child.setValue(_first_code);
		} else {
			_combo_child.setMultiall(true);
		}
		/*
		_comboObjects[idx + 1].setSize(20);
		if(_datasetObjects[idx + 1].size() < 20){

			_comboObjects[idx + 1].setSize(_datasetObjects[idx + 1].size());
		}else{
			_comboObjects[idx + 1].setSize(20);
		}
		*/
	}


	/*for ( var i in _datasetObjects) {
		if (_datasetObjects[i].size() == 2 && _datasetObjects[i].getValue(0, "CODEID") == _first_code) {
			_datasetObjects[i].removeRow(0);
			_comboObjects[i].setValue(_datasetObjects[i].getValue(0, "CODEID"));
			eval("_common_dept_change(" + i + ")");
		}
	}*/


	if (_user_yn == "Y" && iLevel == (_datasetObjects_o.length - 1)){
		if(authFlag == "N"){
			sel_user = "";
			_getUserList(iLevel);
		}
	}else if(_user_yn == "Y"){
		if(authFlag == "N"){
			sel_user = "";
			_ds_user.clearData();
		}

		if (org_muti_yn == "Y") {
			_cmb_user.setMultiall("true");
		} else {
			_ds_user.addRow([ _first_code, _first_text, _first_code, '', '', '','']); //위치변경 //20180512
			_cmb_user.setValue(_first_code);
		}

	}
}

function _user_change(event){
//해당 기능 넣었더니 첫번째 선택시 전체선택처럼 동작.
//	if (org_muti_yn == "Y") { // 추후 Xwing 업데이트 후에 반영...20130219
//		var o = _cmb_user;
//		if (event.index == 0 && event.selected == "true" &&
//		    (o.getValue().indexOf(_first_code) > -1 || o.getValue() == _first_code)) {
//			var _str = "";
//			for ( var i = 0; i <= event.source._maxidx; i++) {
//				_str += "," + i;
//			}
//			o.setItemindex(_substr(_str, 1));
//		} else if (event.index == 0 && event.selected == "false" &&
//				   (o.getValue().indexOf(_first_code) == -1 || o.getValue() != _first_code)) {
//			o.setValue("");
//		} else {
//
//		}
//	}
}

function _getUserList(iLevel) {
	//alert("GETUSERLIST"+sel_user);

	var _doExit = false;

	var _p_dept = "";

	//본부,센터,팀밑에 바로 사원이 있는 경우가 있어서 주석처리함...20180528
	//if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[5]) ? "" : _comboObjects[5].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	//if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[4]) ? "" : _comboObjects[4].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	//if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[3]) ? "" : _comboObjects[3].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[2]) ? "" : _comboObjects[2].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	//if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[1]) ? "" : _comboObjects[1].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	//if (_isEmpty(_p_dept)) _p_dept = (_isNull(_comboObjects[0]) ? "" : _comboObjects[0].getValue().replace(eval("/" + _first_code + "/gi"), ""));
	if (_isEmpty(_p_dept)) _doExit = true;

	if (_doExit) {
		_ds_user.clearData();
		//_ds_user.addRow([ _first_code, _first_text, '%', '', '', '','']);		//20180525 multiyn 두개되는거 '전체' 세팅 막음
		if (org_muti_yn == "Y") {
			_cmb_user.setMultiall("true");
		} else {
			_cmb_user.setValue(_first_code);
		}
		return;
	}

	var _p_user = "";
	if(_user_yn == "Y" && (!_isNull(_cmb_user)) == true && _s_org_user == "Y"){
		_p_user = _global("G_USER_ID");
	}

	var _p_orderby = "USER_NAME";

	if (_org_grade_order_yn == "Y")
		_p_orderby = "GRADE_ORDER," + _p_orderby;

	var _param = {
		_sqlName : "cs.com.org_user",
		deptcd : _p_dept,
		userid : _p_user,
		orderby : _p_orderby
	};

	var opt = {
		sReqId : "_orgutil",
		url : "service::cs.com.orgutil.do",
		param : _param,
		indicator : org_muti_yn == "Y" ? true : false
	};

	Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {
		_ds_user.setData(oRes.__USER_LIST.column, oRes.__USER_LIST.record);
		if (oRes.__USER_LIST.record.length == 0 && org_muti_yn != "Y") {
			_ds_user.addRow([ _first_code, _first_text, '%', '', '', '', '' ]);
		} else if (_s_org_user != "Y") {
			if(org_muti_yn != "Y"){
				_ds_user.insertRow(0, [ _first_code, _first_text, '%', '', '', '','' ]);}
		}

		if(_ds_user.size() < 20){
			_cmb_user.setSize(_ds_user.size());
		}else{
			_cmb_user.setSize(20);
		}

		if (org_muti_yn == "Y") {
			_cmb_user.setMultiall("true");	//setValue("");
		} else {
			_cmb_user.setValue(_first_code);
		}

		if (_s_org_user == "Y") {
			if(_ds_user.size() == 2 && _ds_user.getValue(0, "CODEID") == _first_code){
				_ds_user.removeRow(0);
			}
			//_cmb_user.setItemindex(0);
			if(org_muti_yn != "Y") _cmb_user.setValue(global("G_USER_ID"));
		} else {
			if (org_muti_yn != "Y")_cmb_user.setValue(_first_code);
			else _cmb_user.setMultiall("true");
		}
		//alert("_getUserList");
		if( sel_user != ""){
			_cmb_user.setMultiall("false");
			_cmb_user.setValue(sel_user);

		}
		if(authFlag == "Y"){
			_cmb_user.setEnabled(false);
		}
	});

}

function _dept_change0(e) {
	_common_dept_change(0, e);
	if (typeof (cmb_org0_change) == "function") {
		cmb_org0_change(e);
	}
}
function _dept_change1(e) {
	_common_dept_change(1, e);
	if (typeof (cmb_org1_change) == "function") {
		cmb_org1_change(e);
	}
}
function _dept_change2(e) {
	_common_dept_change(2, e);
	if (typeof (cmb_org2_change) == "function") {
		cmb_org2_change(e);
	}
}
function _dept_change3(e) {
	_common_dept_change(3, e);
	if (typeof (cmb_org3_change) == "function") {
		cmb_org3_change(e);
	}
}
function _dept_change4(e) {
	_common_dept_change(4, e);
	if (typeof (cmb_org4_change) == "function") {
		cmb_org4_change(e);
	}
}
function _dept_change5(e) {
	_common_dept_change(5, e);
	if (typeof (cmb_org5_change) == "function") {
		cmb_org5_change(e);
	}
}

function g_setOrg(org1,org2,org3,userid){
	if(!_isNull(org1)){
		$cmb_org1.setValue(org1);
		_dept_change0();
	}

	if(!_isNull(org2)){
		$cmb_org2.setValue(org2);
		_dept_change1();
	}

	if(!_isNull(org3)){
		$cmb_org3.setValue(org3);
		//_dept_change2();
			_getUserList(2);
	}

	if(!_isNull(userid)){
		//$cmb_user.setValue(userid);
		sel_user = userid;
	}


}

function g_orgInfo(bQuote) {
	bQuote = _nvl(bQuote, false);
	var org = {
		org1_cd : g_orgValue(0, bQuote),
		org2_cd : g_orgValue(1, bQuote),
		org3_cd : g_orgValue(2, bQuote),
		org4_cd : g_orgValue(3, bQuote),
		org5_cd : g_orgValue(4, bQuote),
		org6_cd : g_orgValue(5, bQuote),
		user_id : g_userValue(bQuote)
	};
	return org;
}

function g_orgToParam(sPattern, oData, bQuote) {
	bQuote = _nvl(bQuote, false);
	oData[(sPattern + "org1_cd").toUpperCase()] = g_orgValue(0, bQuote);
	oData[(sPattern + "org2_cd").toUpperCase()] = g_orgValue(1, bQuote);
	oData[(sPattern + "org2_flag").toUpperCase()] = g_orgFlag(oData[(sPattern + "org2_cd").toUpperCase()]);
	oData[(sPattern + "org3_cd").toUpperCase()] = g_orgValue(2, bQuote);
	oData[(sPattern + "org4_cd").toUpperCase()] = g_orgValue(3, bQuote);
	oData[(sPattern + "org5_cd").toUpperCase()] = g_orgValue(4, bQuote);
	oData[(sPattern + "org6_cd").toUpperCase()] = g_orgValue(5, bQuote);
	oData[(sPattern + "user_id").toUpperCase()] = g_userValue(bQuote);

}

function g_orgFlag(org2Value){
	if(org2Value != ""){
		return "O"; //one
	}else if(authMuti == "Y"){
		return "M"; //multi center
	}else{//authMutil == "" && org2Value == ""
		return "A"; //all
	}
}

function g_orgLevel(){
	var bQuote = false;
	var level = "";
	if(g_orgValue(0, bQuote) != ""){ level = 1; }
	if(g_orgValue(1, bQuote) != ""){ level = 2; }
	if(g_orgValue(2, bQuote) != ""){ level = 3; }
	if(g_userValue(bQuote) != ""){ level = 4;}
	return level;
}



function g_orgObj(iLevel) {
	iLevel = _nvl(iLevel, -1);
	var org = {
		org1 : _nvl(_comboObjects[0], null),
		org2 : _nvl(_comboObjects[1], null),
		org3 : _nvl(_comboObjects[2], null),
		org4 : _nvl(_comboObjects[3], null),
		org5 : _nvl(_comboObjects[4], null),
		org6 : _nvl(_comboObjects[5], null),
		user : _cmb_user
	};
	return iLevel == -1 ? org :
		   iLevel == "user" ? org.user :
		   iLevel > 6 ? org : eval("org.org" + iLevel);
}

function g_orgValue(iLevel, bQuote) {
	bQuote = _nvl(bQuote, false);
	var val = _undefined_replace((_isNull(_comboObjects[iLevel])||_comboObjects[iLevel].getAllCheck()) ? "" : _decode(_first_code_yn, "Y", _comboObjects[iLevel].getValue(), _comboObjects[iLevel].getValue().replace(eval("/" + _first_code + "/gi"))));
	//점프
	/*if(_auth_filter){
		if(iLevel == 1 && authMuti =='Y' && _comboObjects[1].getValue() == ''){
			val = authMuti;
		}
	}*/
	return _isEmpty(val)?val:_decode(bQuote, true, _quote(val), val);
}

function g_userValue(oData,sPattern,bQuote) {

	bQuote = _nvl(bQuote, false);
	var val = _undefined_replace(((_user_yn == "Y") && (!_isNull(_cmb_user)&&!_cmb_user.getAllCheck())) ? _decode(_first_code_yn, "Y", _cmb_user.getValue(), _cmb_user.getValue().replace(eval("/" + _first_code + "/gi"), "")) : "");
	//val = "AND Z.USER_ID in ('" + val + "')";
	//alert(val);
	return _isEmpty(val)?val:_decode(bQuote, true, _quote(val), val);
	
}

function g_setAuthObj(iLevel, org1_cd, org2_cd, org3_cd, user_id){
//alert("g_setAuthObj");

	if(iLevel == 1) return; // iLevel이 1인 사용자는 본부까지 모두 조회가능하므로 별로 세팅 필요x

	//QC권한자들의 경우 배정 센터가 따로 있기 때문에 별도로 조회가 필요함 //현재 다른용도로 쓰고있음. 필요시 코드북에 새로 코드따야함
//	if(authExcept == "Y"){
//		var _param = {
//				_sqlName : "cs.com.getqcinfo.sel",
//				user_id : user_id
//			};
//
//			var opt = {
//				sReqId : "_getqcinfo",
//				url : "service::cs.com.orgutil.do",
//				param : _param,
//				indicator : false
//			};
//
//			Xwing.request(opt, function(sReqId, oRes, oErr, oXhr) {
//				var _ds_qc_list = Xwing.createDataset("DS_QC_LIST");
//				_ds_qc_list.setData(oRes.DS_QC_LIST.column, oRes.DS_QC_LIST.record);
//
//				org1_cd = _ds_qc_list.getValue(0, "ORG1_CD");
//				org2_cd = _ds_qc_list.getValue(0, "ORG2_CD");
//
//				for(var i=0; i< iLevel;i++){
//					var orgs = eval("org"+(i+1)+"_cd");
//					var dataset = eval("$_DS_DEPT"+(i+1)+"_ORG");
//
//					if(dataset.indexOfRow("CODEID", orgs) != -1){
//						eval("$cmb_org"+(i+1)+".setValue('"+orgs+"')");
//						_common_dept_change(i);
//
//						//만약에 자기 부서말고도 다 볼수 있게 해야한다면..
//						if(i == iLevel-1) eval("$cmb_org"+(i+1)+".setEnabled('true')");
//						else eval("$cmb_org"+(i+1)+".setEnabled('false')");
//
//						//만약에 자기 부서만 볼수 있게 해야한다면...
//						//eval("$cmb_org"+(i+1)+".setEnabled('false')");
//					}
//				}
//			});
//	}else{
	//점프
		for(var i=0; i< iLevel;i++){

			if(i == 3) break;

			var orgs = eval("org"+(i+1)+"_cd");
			var dataset = eval("$_DS_DEPT"+(i+1)+"_ORG");
			//만약에 자기 부서만 볼수 있게 해야한다면...
			if(iLevel == 4) authFlag = "Y";
			//_common_dept_change(i);
			//eval("$cmb_org"+(i+1)+".setEnabled('false')");

			if(dataset.indexOfRow("CODEID", orgs) != -1){
				eval("$cmb_org"+(i+1)+".setValue('"+orgs+"')");
				_common_dept_change(i);
				eval("$cmb_org"+(i+1)+".setEnabled('false')");
				if(i == 2 && iLevel == 4){
					//alert(user_id);
					sel_user = user_id;
					//eval("$cmb_user.setValue('"+sel_user+"')");
					org_muti_yn = 'N';
					_getUserList(iLevel);
					//_cmb_user.setValue(sel_user);

					return;
					//alert("완료");
				}
				//만약에 자기 부서말고도 다 볼수 있게 해야한다면..
				//if(i == iLevel-1) eval("$cmb_org"+(i+1)+".setEnabled('true')");
				//else eval("$cmb_org"+(i+1)+".setEnabled('false')");


			}else{
				if(_auth_filter){
					if(authMuti != "Y"){
						eval("$cmb_org"+(i+1)+".setEnabled('false')"); //TM조직이 아닌 사용자의 경우 해당 처럼 사용 --> TM조직은 아니면서 TM 하위 센터부터 조회해야하는 권한자들의 경우
					}
				}
				break;
			}

		}
	//}


}

/*******************************************************************************
 * UTIL
 ******************************************************************************/
//첫번째 인수 null이 아니면 첫번째 인수반환, 첫번째 인수 null이면 두번째 인수 반환 (null체크후 null아닐경우 값, null체크후 null일경우 값)
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

//첫번째인수=두번째인수 =>세번째인수 else 네번째인수
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
