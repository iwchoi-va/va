// SERVER URL 셋팅
var FILE_KEYBOARD = {
		//TAB : "        "
		TAB : "\t"
};
var tess = "";
var fileConfig = {
		test : {
			// edit100m.xhtml
			lists_url : "Tlists" // server list active 여부 확인
			, etc_url : "Tetc" // host name 위치
			, callsel_url : "Tcallsel" // 상세 탭
			, listsapp_url : "Tlistsapp " // 1차 전화탭에서 각 code 들
			, config_url : "Tconfig" // 재시도의 리스트 조회
			, job_url : "Tjob" // 재시도의 리스트 조회
			, scripts_url : "Tscripts" // 작업의 스크립트 라벨 조회
			, reports_url : "Treports" // 작업의 스크립트 라벨 조회
			, norm_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, mrp_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, rm_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, regex_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, root_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, pre_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, ne_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, post_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, dic_exe_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, dic_la_url : "dic_la.exe" //"/home/msens/engine/LA/source/lang/lang_test/dic_la.exe" // 데이터 추출에서 조건 리스트들.
			, dic_ma_url : "dic_ma.exe"
			, go_custom_url : "C:/web/IS_VOC/webapps/testcall.cmd"
			, dic_url : "C:/web/IS_VOC/webapps/custom_dic"
			, ner_url : "C:/web/IS_VOC/webapps/custom_dic"
			, sa_url : "C:/web/IS_VOC/webapps/custom_dic"
			, normal_url : "C:/web/IS_VOC/webapps/custom_dic"
			, base_file_name : "custom.txt"		// 사용자기본사전
			, complex_file_name : "custom_cn.txt"	//사용자복합명사사전
			, pre_file : "at_pre_dic.txt" // 전처리사전
			, ne_file : "at_ne.txt" // 개체명사전
			, post_file : "at_post_dic.txt" // 후처리사전
			, mrp_file : "anchors-MRP.txt" //  중심형태소
			, rm_file : "anchors-RM.txt" //  마지막형태소
			, regex_file : "anchors-REGEX.txt" //  다어절정규표현
			, root_file : "anchors-root.txt"	// 어절어휘
			, normal_file : "WikiTitle_Normalization.txt"	//이형태사전
		},
		real : {
			// edit100m.xhtml
			lists_url : "lists" // server list active 여부 확인
			, etc_url : "etc" // host name 위치
			, callsel_url : "callsel" // edit100m의 상세 탭
			, listsapp_url : "listsapp/" // 1차 전화탭에서 각 code 들
			, config_url : "config" // 재시도의 리스트 조회
			// shell url
			, norm_url : "/home/msens/engine/rsc/Normalization/cp_norm.sh"
			, mrp_url : "/home/msens/engine/rsc/SA/cp_mrp.sh"
			, rm_url : "/home/msens/engine/rsc/SA/cp_rm.sh"
			, regex_url : "/home/msens/engine/rsc/SA/cp_regex.sh"
			, root_url : "/home/msens/engine/rsc/SA/cp_root.sh"
			, pre_url : "/home/msens/engine/rsc/ner/cp_ner_pre.sh"
			, ne_url : "/home/msens/engine/rsc/ner/cp_ner_ne.sh"
			, post_url : "/home/msens/engine/rsc/ner/cp_ner_post.sh"
			, dic_exe_url : "/home/msens/web/MSENS/webapps/operation/test_exe_operation.sh"
			, dic_la_url : "dic_la.exe" //"/home/msens/engine/LA/source/lang/lang_test/dic_la.exe" // 데이터 추출에서 조건 리스트들.
			, dic_ma_url : "dic_ma.exe"
			, go_custom_url : "/home/msens/engine/LA/custom_dic/go_custom.sh"
			// directory url
			, dic_url : "/home/msens/engine/LA/custom_dic"
			, ner_url : "/home/msens/engine/rsc/ner"
			, sa_url : "/home/msens/engine/rsc/SA/tweet"
			, normal_url : "/home/msens/engine/rsc/Normalization"
			// file name
			, base_file_name : "custom.txt"
			, complex_file_name : "custom_cn.txt"
			, pre_file : "at_pre_dic.txt" // 전처리사전
			, ne_file : "at_ne.txt" // 개체명사전
			, post_file : "at_post_dic.txt" // 후처리사전
			, mrp_file : "anchors-MRP.txt" //  중심형태소
			, rm_file : "anchors-RM.txt" //  마지막형태소
			, regex_file : "anchors-REGEX.txt" //  다어절정규표현
			, root_file : "anchors-root.txt"	// 어절어휘
			, normal_file : "WikiTitle_Normalization.txt"	//이형태사전
			//이하, 키워드 통계용 url
			, upload_list_url: "/home/msens/engine/LA/source/lang/lang_test/docs" //list.txt, 업로드된 파일 위치
			, morph_url : "/home/msens/web/MSENS/webapps/operation/morp_operation.sh" // "/home/msens/engine/LA/source/lang/lang_test/get_morph_cnt.sh" //형태소 통계
			, ne_url : "/home/msens/web/MSENS/webapps/operation/ne_operation.sh" // "/home/msens/engine/LA/source/lang/lang_test/get_ne_cnt.sh" //개체명 통계
		}
};
var HIDDEN = ['RECLEN','ACCTNUM','PHONE1','PHONE2','PHONE3','PHONE4','PHONE5'];
/***************************************************
 *  전략 리스트 조회, (전략, 선택, 작업 화면에서 다 사용함으로... )
 *  그리고 다 화면을 끄지 않는 이상 재 조회되지 않음으로 문제가 될 수 있기에...
 *  공통으로 해야 한다.
 *   주의 점 전략파일은 무조건 로딩 때 새로 로깅 해야 한다.
 ***************************************************/
function g_loadStrategyFile(){
	var main  = Xwing.parent;
	if( !main ) return;
	while(true){
		if( /\/main.xhtml|\/main.html/.test(main.location.pathname) ) break;
		else main = main.Xwing.parent;
	}

	// main에 전략 파일 있는지 확인.
	if( !main.Xwing.getDataset('DS_G_STRG') ){
		// DB와 sync를 맞춰야 하기 때문에 우선 file 읽는게 중요
		var opt = {
				reqId : "initServer",
				url : "service::cdw.edit.filemanager.do",
				param : {
					fileType : 'RF',
					fileURL : fileUrl.callsel_url,
					fileName : 'G',
					fileDirectory : false
				}
			};
		Xwing.request(opt, f_callback);
	}else if($DS_G_LIST){
		if( main.Xwing.getDataset('DS_G_STRG').size() == 0 ) $DS_G_LIST.clearData();
		else{
			var arr = [];
			for( var i = 0; i < main.Xwing.getDataset('DS_G_STRG').size() ; i++ ) {
				arr.push(main.Xwing.getDataset('DS_G_STRG').getRow(i));
			}
			$DS_G_LIST.setData(arr);arr = null;
		}
		$DS_G_LIST.sort("G_CODE");
		var path = location.pathname.split('/');
		path = path[path.length - 1 ];
		if( !main.Xwing.getDataset('DS_G_STRG')._path )main.Xwing.getDataset('DS_G_STRG')._path= {path :  window};
		else main.Xwing.getDataset('DS_G_STRG')._path[path] = window;
	}
}

function f_callback(reqId, res, err, xhr){
	if (res.Error != '0') {
		var resultMsg = res.ResultMessage;
		xwing.Dialog.alert('REASON : ' + resultMsg);
		return;
	}

	var main  = Xwing.parent;
	if( !main ) return;
	while(true){
		if( /\/main.xhtml|\/main.html/.test(main.location.pathname) ) break;
		else main = main.Xwing.parent;
	}

	switch(reqId){
	case 'initServer' :
		var data = parsingTransData(res.CONTENT);
		if( data ){
			var ds = (!main.Xwing.getDataset('DS_G_STRG') ? main.Xwing.createDataset({id : 'DS_G_STRG'}) : main.Xwing.getDataset('DS_G_STRG'));
			ds._filenm = data.file0.LIST;
			var opt = {
					reqId : "searchFileDB",
					url : "service::cdw.edit.do",
					param : {
						_sqlName : "cdw.edit.filelist.search",
						FILE_TYPE : 'S'
					}
				};
			Xwing.request(opt, f_callback);
		}
		break;
	case 'searchFileDB':
		var ds = main.Xwing.getDataset('DS_G_STRG');
		// parsing  부서 별로 함...
		if( ds ){
			var arr = [], arr2 = []
				, ds_rows = res.DS_CALLLIST_SEARCH.record
				, dept = global('G_ORG2_CD')
				, filenm = "";

			// DB에 있는 내용만...
			for( var i=0, l = ds_rows.length ; i < l; i++){
				filenm = ds_rows[i][0];
				if( ds._filenm.indexOf(filenm+".G") != -1 ) {
					if( ds_rows[i][4] == dept){
						arr.push([filenm,filenm]);
						arr2.push([filenm,filenm]);
					}
				}
			}

			var path = location.pathname.split('/');
			path = path[path.length - 1 ];
			if( !ds._path ){
				ds._path= {};
				ds._path[path] = window;
			}else ds._path[path] = window;

			ds.setData(['G_CODE', 'G_NAME'],arr);
			$DS_G_LIST && $DS_G_LIST.setData(arr2);
			$DS_G_LIST.sort("G_CODE");
		}
		break;
	}
}
/***************************************************
 *  전략 리스트 조회, (전략, 선택, 작업 화면에서 다 사용함으로... )
 *  전략에서 신규 만들거나, 삭제 할 때...
 *   @param type // C : 신규, D : 수정
 ***************************************************/
function g_updateStragegyFile(){
	// 1. update
	// 2. 선택이나, 작업 쪽에 update 치기
	var ds = Xwing.parent.Xwing.getDataset('DS_G_STRG');
	if( ds && ds._path){
		for(var i in ds._path){
			var wn = ds._path[i];
			if( wn.$DS_G_LIST ){
				wn.$DS_G_LIST.setData(ds.getData());
				wn.$DS_G_LIST.sort("G_CODE");
			}
		}
	}
}

/***************************************************
 * 초반 리스트 조회 시, 조건에 따라 전략명과 리스트 설정한다.
 * @param ds_file // 저장되어 있는 file List
 * @param record // db에서 가져온 file List
 * @param org1 // 조회 소속 1
 * @param org2 // 조회 소속 2
 * @param org3 // 조회 소속 3
 * @param all // server의 file까지 조회 할 지 안 할지 결정
 * @param type // S : 전략, C : 선택, W : 작업
 * @param work // 작업을 뜻함
 * @param jobtype // work가 true 일 떄, 잡업 유형이 조건에 들어간다.
 * @param jobname // work가 true 일 떄, 작업명이 조건에 들어간다.
 */
function makeFileList(ds_file, record, org1, org2, org3, all, type, work, jobtype, jobname){
	var result = [];
	// DB data 부터 가공
	var notreadindex = [];

	for(var i=0, l = record.length ; i < l ;i++){
		var row = record[i].concat([]);
		if( work ){
			var data = $DS_FILE_LIST.getData('[FILE_NM == "'+row[0]+'"]');
			if( data.length > 0 ){
				row[7] = (data[0][2].indexOf("-") > -1 || !data[0][2] ? data[0][2] : 'server1-'+data[0][2]);
				row[18] = data[0][1];
				row[19] = data[0][3];
			}
		}
		if( org1 == '%' ){
			result.push(row);
		}else{
			if( row[3] == org1 ){
				if( org2 == '%' ){
					result.push(row);
				}else{
					if( row[4] == org2 ){
						result.push(row);
//						if( org3 == '%' ||  row[5] == org3){
//							result.push(row);
//						}
					}
				}
			}
		}
		notreadindex.push(row[0]);
	}
	// SERVER File 읽기
	if( all  ){
		for(var i = 0, l = ds_file.size(); i < l ; i++){
			if( notreadindex.indexOf(ds_file.getValue(i,"FILE_NM")) == -1 ) {
				// 작업 일 때, 조건에 따라 만들어야 할 필요 있음
				if( work && !((jobtype == 'ALL' && jobname == '') // 둘 다 조건 없을 경우
				    || (jobtype == 'ALL' && ds_file.getValue(i,"FILE_NM").indexOf(jobname) > -1 ) // 작업유형은 조건이 없고, 작업명만 있을 경우
					|| (jobname == '' && ds_file.getValue(i,"WORK_TYPE") == jobtype ) // 작업명은 조건이 없고, 작업유형만 있는 경우
					|| ( ds_file.getValue(i,"WORK_TYPE") == jobtype && ds_file.getValue(i,"FILE_NM").indexOf(jobname) > -1 )) ) // 둘 다 조건이 있는 경우
					continue;


				var arr = new Array((!work ? 19 : 21));
				arr[0] = ds_file.getValue(i,"FILE_NM");
				arr[1] = type;
				arr[arr.length - 1] = "N";
				if( work ){
					arr[6] = "N";
					arr[7] = (ds_file.getValue(i,"LIST_OUT").indexOf("-") > -1 || !ds_file.getValue(i,"LIST_OUT")  ? ds_file.getValue(i,"LIST_OUT") : 'server1-'+ds_file.getValue(i,"LIST_OUT")); //(data[0][2].indexOf("-") > -1 ? data[0][2] : 'server1-'+data[0][2]);
					arr[18] = ds_file.getValue(i,"WORK_TYPE");
					arr[19] = (ds_file.getValue(i,"LIST_IN").indexOf("-") > -1 || !ds_file.getValue(i,"LIST_IN")  ? ds_file.getValue(i,"LIST_IN") : 'server1-'+ds_file.getValue(i,"LIST_IN"));
				}
				result.push(arr);
			}
		}
	}
	return result;
}
/****************************************
 * 읽어온 파일은 내부에서 파싱함.
 * @param res
 * @returns
 *****************************************/
function parsingTransData(res){
	try{
		res = res.replace(/"\{/g,"{");
		res = res.replace(/\}"/g,"}");
		res = res.replace(/"\[/g,"[");
		res = res.replace(/\]"/g,"]");
		res = res.replace(/\\\\'/g,"\\'");
		res = res.replace(/\\"/g,"\"");

		tess = res;
		res = eval('('+res+')');
//		Xwing.log(res);
		return res;
	}catch(e){
		Xwing.log("Error Parsing Data :");
		Xwing.log(e);
	}
}

/****************************************
 * 수정한 파일들을 다시 파일로 저장하기 위해 데이터를 파싱함
 * @param res
 * @returns
 *****************************************/
function parsingServerData(ds_server, ds_initial, ds_progress, ds_recall){
	var result = "";
	//1. 상세 의 Server List -> 이건 수정,new 모두 필수임 따라서 없으면 저장 못함..
	if(!ds_server || ds_server.size() == 0 ){
		xwing.Dialog.alert("Server을 선택해주세요.");
		$tab.activate(0);
		return "";
	}
	result = "LIST:"+ds_server.getValue(0,"SERVER")+":";
	//2. 1차 전화  -> 선택(값이 있어야지만 됨)
	if( ds_initial && ds_initial.size() > 0 ){
		result +="\nINITIAL:\n";
		for(var i=0, l = ds_initial.size(); i < l ; i++){
			var row = ds_initial.getRow(i);
			// 감지모드에 check 되어 있는 애들 중 값이 존재하는 애 있는지 체크해야 함.. 만약 없으면 error 메세지 보여줘야 함  TODO
			var prog_row = null;

			if( ds_progress && ds_progress.size() > 0 ){
				var phone = $DS_PHONE_CODE.lookUp('CODE',$DS_INITIAL.getValue(i, "PHONE"),'VALUE');
				prog_row = $DS_PROGRESS.getData("[PHONE == '"+phone+"']");
				// 전화벨 수 있는지 체크
				if(prog_row.length > 0 && ( !prog_row[0][1] || prog_row[0][1] == "" )){
					CUR_STATE = "R";
					xwing.Dialog.alert("감지모드의 "+$DS_PHONE_CODE.lookUp('CODE',$DS_INITIAL.getValue(i, "PHONE"),'DESC')+" 값에 전화벨 수가 존재하지 않습니다. \n전화벨 수를 입력해주세요.");
					$tab.activate(2);
					return "";
				}

				// 상담원 연결 체크
				if( prog_row.length > 0 ){
					prog_row = $DS_PROGRESS.getData("[PHONE == '"+phone+"'][CHECK == 'true']");
					if( prog_row.length == 0 ){
						CUR_STATE = "R";
						xwing.Dialog.alert("감지모드의 "+$DS_PHONE_CODE.lookUp('CODE',$DS_INITIAL.getValue(i, "PHONE"),'DESC')+" 값에 상담원 연결 값이 존재하지 않습니다. \n 상담원 연결 값을 체크해주세요.");
						$tab.activate(2);
						return "";
					}
				}
			}
			if( row.length != ds_initial.getColumnInfo().length ) continue;
			result +=FILE_KEYBOARD.TAB;
			for(var j=0, l2 = row.length; j < l2; j++){
				if( ( j == 0 || j == 1) && !row[j] ){
					var message = (j == 0 ? "전화 값":( j == 1 ? "필드 값" : ""));
					CUR_STATE = "R";
					xwing.Dialog.alert("1차 전화의 "+message+"이 존재하지 않습니다."+message+"을 입력하세요.");
					$tab.activate(1);
					return "";
				}
				if( j == (l2 -1)) result += (!row[j]? "" : row[j]);
				else result +=(!row[j]? "" : row[j])+":";
			}
			result += (i == ( l - 1) ? '' : '\n');
		}
	}

	//3 감지모드  -> 선택(값이 있어야지만 됨)
	if( ds_progress && ds_progress.size() > 0 ){
		var rows = $DS_PROGRESS.getData("[CHECK == 'true']");
		if( rows.length != 0){
			result +="\nPROGRESS:";
			var row = ''
				, phone = ''
				, pre_phone = '';
			for( var i=0, l = rows.length ; i < l ; i++){
				if( !rows[i][1]){
					xwing.Dialog.alert("감지모드[ "+$DS_PHONE_CODE.lookUp("VALUE",rows[i][0],"DESC")+" ]의 전화벨 수를 연결하지 않았습니다. \n 전화벨 수를 연결해주세요.");
					$tab.activate(2);
					CUR_STATE = "R";
					return "";
				}
				phone = $DS_PHONE_CODE.lookUp("VALUE",rows[i][0],"CODE");
				if( pre_phone != phone ){
					if( i != 0 ){
						result += "\n";
					}
					if( row != '' ) result +=  FILE_KEYBOARD.TAB+row;
					row = phone+':'+rows[i][1]+":"+rows[i][3];//+(i == (l - 1 ) ? '' : '\n');
				}else row += ","+rows[i][3];
				pre_phone = phone;
			}
			if( row != '' ) result += "\n"+FILE_KEYBOARD.TAB+row;
		}
	}
	//4 재시도 -> 선택 (값이 있어야지만 가능)
	if( ds_recall && ds_recall.size() > 0){
		var recall = ""
			, phone_code , comp_code;
		for( var i = 0 , l = ds_recall.size() ; i < l ; i++){
			var check = xwing.Util.parseBoolean(ds_recall.getValue(i,'CHECK'))
				, recall_gap = (ds_recall.getValue(i,'RECALL_GAP') == "" && check? '0' : ds_recall.getValue(i,'RECALL_GAP'))
				, exe_cnt = (ds_recall.getValue(i, 'EXE_CNT') == "" && check ? '0' : ds_recall.getValue(i, 'EXE_CNT'));

			if( check && recall_gap && exe_cnt){
				// 값 있는 케이스
				if( recall == "") recall = "\nRECALL:";
				recall +=  "\n"+FILE_KEYBOARD.TAB+ds_recall.getValue(i,'PHONE_CODE')+":"+ds_recall.getValue(i,'COMP_CODE')+":"
						+ recall_gap + ":"+ exe_cnt +":"+ ds_recall.getValue(i,'NEXT_CALL');
			}else if( check || recall_gap || exe_cnt){
				xwing.Dialog.alert("PHONE"+ds_recall.getValue(i,'PHONE_CODE')+"의 ["+ds_recall.getValue(i,'COMP_DESC')+"]에 대한 재시도 설정이 완료되지 않았습니다.");
				$tab.activate(3);
				CUR_STATE = "R";
				return "";
			}else if( !check && ds_recall.getValue(i,'NEXT_CALL')){
				xwing.Dialog.alert("PHONE"+ds_recall.getValue(i,'PHONE_CODE')+"의 ["+ds_recall.getValue(i,'COMP_DESC')+"]에 대한 재시도 설정이 완료되지 않았습니다.");
				$tab.activate(3);
				CUR_STATE = "R";
				return "";
			}
		}
		result += recall;
	}

	Xwing.log(result);
	return result;
//	return "";
}

function parsingSelectData(ds_list, ds_record, ds_result, ds_recall, ds_sort){
	var result = "";
	//1. 상세 의 Server List -> 이건 수정,new 모두 필수임 따라서 없으면 저장 못함..
	if(!ds_list || ds_list.size() == 0 || !ds_list.getValue(0,'LIST_NM') ){
		xwing.Dialog.alert("Server을 선택해주세요.");
		return "";
	}

	if( !$DS_LIST.getValue(0,'STGYFILE')){
		xwing.Dialog.alert("전략 파일이 존재하지 않습니다. 전략 파일을 선택해 주세요.");
		$tab.activate(0);
		return "";
	}

	result = "LIST:"+ds_list.getValue(0,'LIST_NM')+":"
			+'\nIGNORETZ:'+ds_list.getValue(0,'IGNORETZ')
			+'\nSELECTTYPE:'+(ds_list.getValue(0,'SELECTTYPE') == 'true' ? 'infinity' : "")
			+'\nUNITFIELD:'+$DS_LIST.getValue(0,'UNITFIELD')
			+'\nSTGYFILE:'+$DS_LIST.getValue(0,'STGYFILE');

	// 2. 레코드 선택
	var cnt = 1;
	if( ds_record && ds_record.size() != 0 ){
		for(var i=0 ; i < ds_record.size() ; i++){
			result += '\nSELECT:'+ds_record.getValue(i,'FEILD')+':'+ds_record.getValue(i,'VALUE')+':'+ds_record.getValue(i,'LOGIC');
			if( i != 0 && $DS_RECORD.getValue(i,'CHECK') ) cnt++;
			result += ":"+ cnt;
		}
	}
	result += '\nTZONE:*';

	// 3. 결과
	if( ds_result ){
		cnt = 0;
		var rows = ds_result.getData('[CHECK == "true"]');
		if( rows.length == 0 ) {
			xwing.Dialog.alert("결과 코드를 체크해 주세요.");
			$tab.activate(2);
			return "";
		}
		for( var i = 0 ; i < rows.length ; i++){
			if( cnt == 0 ) result += '\nRCODE:'+rows[i][1];
			else {
				result += ','+rows[i][1];
				if( cnt == 14 ) cnt = -1;
			}
			cnt++;
		}
	}

	// 4. 재통화
	if( ds_recall && ds_recall.size() != 0 ){
		cnt = 1;
		for(var i=0 ; i < ds_recall.size() ; i++){
			result += '\nRECALL:'+ds_recall.getValue(i,'FEILD')+':'+ds_recall.getValue(i,'VALUE')+':'+ds_recall.getValue(i,'LOGIC');
			if( i != 0 && $DS_RECORD.getValue(0,'CHECK') ) cnt++;
			result += ":"+cnt;
		}
	}

	// 5. 정렬
	if( ds_sort && ds_sort.size() != 0 ){
		for(var i=0 ; i < ds_sort.size() ; i++){
			result += '\nSORT:'+ds_sort.getValue(i,'STANDARD')+':'+ds_sort.getValue(i,'RANGE');
		}
	}

	result += '\nDISPLAY:YES';
	Xwing.log(result);
	return result;

}

function parsingData(ds_list,delimiter,key,value){
	var result = "";

	// 2. 레코드 선택
	var cnt = 1;
	if( ds_list.size() != 0 ){
		for(var i=0 ; i < ds_list.size() ; i++){
			result += ds_list.getValue(i,key)+delimiter+ds_list.getValue(i,value)+'\n';

		}
	}
//	Xwing.log(result);
	return result;
}

function parsingDataset(ds_list, delimiter, columns){
	var result = "";
	var cols = ds_list.getColumnInfo();
	var row = "";

	Xwing.log("column : " + columns);
	if(typeof columns != 'undefined'){
		cols = columns;
	}

	if(cols.length<=0) return;
	if( ds_list.size() != 0 ){
		for(var i=0 ; i < ds_list.size() ; i++){
			for(var col in cols){
				row += ds_list.getValue(i,cols[col]) + delimiter;
			}
			result += row.substring(0,row.length-delimiter.length) +'\n';
			row = "";
		}
	}
	return result;
}
/******************************************
* file Downalod
********************************************/
function dowanloadFile(URL,PARAMS){
	var temp;
	if(jQuery('form',document.body).length != 0) tmp = jQuery('form',document.body)[0];
	else temp = document.createElement("form");
	temp.action=URL;
	temp.method="POST";
	temp.acceptCharset = "utf-8";
	temp.style.display="none";
	for(var x in PARAMS) {
		var opt=document.createElement("textarea");
		opt.name=x;
		opt.value=PARAMS[x];
		temp.appendChild(opt);
	}
	document.body.appendChild(temp);
	temp.submit();
	if( jQuery.browser.msie && parseFloat(jQuery.browser.version) == 8 ) jQuery(temp).empty().remove();
	else delete jQuery(temp).empty().remove();
}

/**************************************************
 * 조직에 따른 리스트 조회
 **************************************************/
function f_loadList(){
	var opt = {
			reqId : "readListDB",
			url : "service::cdw.edit.do",
			param : {
				_sqlName : "cdw.edit.org.list.search",
				ORG1_CD : global('G_ORG1_CD'),
				ORG2_CD : global('G_ORG2_CD'),
				ORG3_CD : global('G_ORG3_CD')
			}
		};
		Xwing.request(opt, org_callback);
}

function f_loadWorkORG(){
	var opt = {
			reqId : "readORGDB",
			url : "service::cdw.edit.do",
			param : {
				_sqlName : "cdw.edit.org.list.search,cdw.edit.org.line.search,cdw.edit.org.ani.search",
				ORG1_CD : global('G_ORG1_CD'),
				ORG2_CD : global('G_ORG2_CD'),
				ORG3_CD : global('G_ORG3_CD')
			}
		};
		Xwing.request(opt, org_callback);
}
function org_callback(reqId, res, err, xhr){
	if (res.Error != '0') {
		var resultMsg = res.ResultMessage;
		xwing.Dialog.alert('REASON : ' + resultMsg);
		return;
	}

	switch(reqId){
	case 'readListDB':
		// 잃었음....
		var rows = res.DS_LIST.record
		    , cols = res.DS_LIST.column
			, dept = 3, org, colIdx;
		var arr = [], tmp = [];
		for(var i = dept; i > 0; i--){
			org = global("G_ORG"+i+"_CD");
			colIdx = cols.indexOf("ORG"+i+"_CD");
			if( colIdx == -1) continue;

			tmp = [];
			for(var j = 0 , l = rows.length ; j < l ; j++){
				var val = rows[j][colIdx];
				if( val == org){
					var key = rows[j][1].split('-')[1];
					arr.push([key,key,key]);
				}else if( !val ) tmp.push(rows[j]);

			}
			if( arr.length > 0 ) break;
			else rows = tmp;
		}
		$DS_SERVERLIST.setData(arr);
		var opt = {
				reqId : "readListFile",
				url : "service::cdw.edit.filemanager.do",
				param : {
					fileType : 'R',
					fileType1 : 'RK',
					fileURL : fileUrl.lists_url + ',' + fileUrl.etc_url ,
					fileName : 'calllistapp.tbl,master.cfg',
					fileKeyword : 'NAMESERVICEHOST',
					fileDirectory : false
				}
			};
		Xwing.request(opt, org_callback);
		break;
	case 'readListFile' :
		var data = parsingTransData(res.CONTENT);
		if( data ){
			var arr = [];
			for(var i = 0, l = $DS_SERVERLIST.size(); i < l ; i++){
				var key = $DS_SERVERLIST.getRow(i)[0];
				if( data.file0[key]) arr.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
										data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
			}
			$DS_SERVERLIST.setData(arr);
			arr = [];
			for(var key in data.file0){
				if( key.indexOf("#") ==  -1 ) arr.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
							data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
			}
			$DS_SERVERLIST_COMMON.setData(arr);
		}
		break;
	case 'readORGDB' :
		// 작업에서 회선, 애니, 조직 조회하는 것
		var rows1 = (res.DS_LIST ? res.DS_LIST.record : []), cols1 = (res.DS_LIST ? res.DS_LIST.column : []) // DS_LIST
		    , rows2 = (res.DS_LINE ? res.DS_LINE.record : []), cols2 = (res.DS_LINE ? res.DS_LINE.column : [])// DS_LINE
		    , rows3 = (res.DS_ANI ? res.DS_ANI.record : []), cols3 = (res.DS_ANI ? res.DS_ANI.column : []) // DS_ANI
			, dept = 3, org, colIdx;
		var arr1 = [], tmp1 = []
			, arr2 = [], tmp2 = []
			, arr3 = [], tmp3 = [], arr3_comm = [];
		for(var i = dept; i > 0; i--){
			org = global("G_ORG"+i+"_CD");
			colIdx = cols1.indexOf("ORG"+i+"_CD");
			if( colIdx == -1) continue;
			tmp1 = []; // 리스트
			for(var j = 0 , l = rows1.length ; j < l ; j++){
				var val = rows1[j][colIdx];
				if( val == org){
					var key = rows1[j][1].split('-')[1];
					arr1.push([key,key,key]);
				}else if( !val ) tmp1.push(rows1[j]);

			}
			if( arr1.length > 0 ) rows1 = [];
			else rows1 = tmp1;

			tmp2 = []; //회선
			for(var j = 0 , l = rows2.length ; j < l ; j++){
				var val = rows2[j][colIdx];
				if( val == org){
					var key = rows2[j][1];
					arr2.push([key,key,key]);
				}else if( !val ) tmp2.push(rows2[j]);

			}
			if( arr2.length > 0 ) rows2 = [];
			else rows2 = tmp2;

			tmp3 = []; // ani
			for(var j = 0 , l = rows3.length ; j < l ; j++){
				arr3_comm.push([rows3[j][1].replace(/-/g,''),rows3[j][1].replace(/-/g,'')])
				var val = rows3[j][colIdx];
				if( val == org){
					var key = rows3[j][1];
					if( key.indexOf("-") > -1) key = key.replace(/-/g,'');
					arr3.push([key,key,key]);
				}else if( !val ) tmp3.push(rows3[j]);

			}
			if( arr3.length > 0 ) rows3 = [];
			else rows3 = tmp3;
		}
		$DS_SERVERLIST.setData(arr1); // 리스트
		$DS_PORTS.setData(arr2); // 회선
		$DS_ANI.setData(arr3); // ani
		$DS_ANI_COMMON.setData(arr3_comm);

		// READ FILE
		var opt = {
				reqId : "readLPFile",
				url : "service::cdw.edit.filemanager.do",
				param : {
					fileType : 'R',
					fileType1 : 'RK',
					fileURL : fileUrl.lists_url + ',' + fileUrl.etc_url ,
					fileName : 'calllistapp.tbl,master.cfg',
					fileKeyword : 'NAMESERVICEHOST,LINEASSIGN',
					fileDirectory : false
				}
			};
		Xwing.request(opt, org_callback);
		break;
	case 'readLPFile':
		var data = parsingTransData(res.CONTENT);
		if( data ){
			// LIST
			var arr = [], arr1= [];
			for(var i = 0, l = $DS_SERVERLIST.size(); i < l ; i++){
				var key = $DS_SERVERLIST.getRow(i)[0];
				if( data.file0[key]){
					if( data.file0[key][0][0] == "OUTBOUND")
						arr.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
					          data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
					else if( data.file0[key][0][0] == "INBOUND")
						arr1.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
						          data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
				}

			}
			$DS_SERVERLIST.setData(arr);
			$DS_SERVERLIST_INBOUND.setData(arr1);
			arr = [], arr1= [];
			for(var key in data.file0){
				if( data.file0[key][0][0] == "OUTBOUND")
					arr.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
				          data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
				else if( data.file0[key][0][0] == "INBOUND")
					arr1.push([key,data.file1.NAMESERVICEHOST[0] + '-' + key,
					          data.file1.NAMESERVICEHOST[0] + '-' + key +"      : "+data.file0[key][0][2]]);
			}
			$DS_SERVERLIST_COMMON.setData(arr);
			$DS_SERVERLIST_INBOUND_COMMON.setData(arr1);

			// PORTS
			if ( data.file1.LINEASSIGN[0] && data.file1.LINEASSIGN[0][0]) { //"REG,O=1-60;INB1,I=61-70"
				arr = [], arr1= [], LINEASSIGN = {
						'OUTBOUND' : [],
						'OUTBOUND_COMMON' : [],
						'INBOUND' : [],
						'INBOUND_COMMON' : []
				};
				var tmp = data.file1.LINEASSIGN[0][0].split(';');
				for(var i = 0, l = tmp.length ; i < l ; i++){
					var data = tmp[i].split(',');
					if( data[1].indexOf("O") > -1){
						// OUTBOUND
						if( $DS_PORTS.indexOfRow("CODEID",data[0]) > -1){
							LINEASSIGN["OUTBOUND"].push([data[0],data[0]]);
						}
						// OUTBOUND 공용
						LINEASSIGN["OUTBOUND_COMMON"].push([data[0],data[0]]);
					}else if( data[1].indexOf("I") > -1 ){
						// INBOUND
						if( $DS_PORTS.indexOfRow("CODEID",data[0]) > -1){
							LINEASSIGN["INBOUND"].push([data[0],data[0]]);
						}
						// INBOUND 공용
						LINEASSIGN["INBOUND_COMMON"].push([data[0],data[0]]);
					}
				}
			}
		}
		break;
	}
}
/**************************************************
 *  Show INDICATOR
***************************************************/
function showIndicator(){
	xwing.widget.Widget.showIndicator("작업 실행 중입니다.");
}
/**************************************************
 *  Hide INDICATOR
***************************************************/
function hideIndicator(){
	xwing.widget.Widget.hideIndicator();
}
/**************************************************
 *  Start Check Modify State
***************************************************/
// 수정 상태 확인
var m_ds_list = "";
function checkModifyState(filename, file_type, ds){
	m_ds_list = ds ; // 수정 하는 애들 ds 넣어서 반영하기 위해 필요

	var opt = {
			reqId : "checkModifyState",
			url : "service::cdw.edit.do",
			param : {
				_sqlName : "cdw.edit.modify.search",
				FILE_NM : filename,
				FILE_TYPE : file_type
			}
		};
	Xwing.request(opt, m_callback);
}

// 수정 중 db 업데이트
function updateModifyState(filename, file_type, update, f_cb){
	var opt = {
			reqId : "updateModifyState",
			url : "service::cdw.edit.do",
			param : {
				_sqlName : "cdw.edit.modify.update",
				FILE_NM : filename,
				FILE_TYPE : file_type,
				CHG_FLAG : update,
				CHG_ID : (update == 'Y' ? global("G_USER_ID") : '')
			}
		};
	Xwing.request(opt, ( !f_cb ? m_callback : f_cb));
}

function m_callback(reqId, res, err, xhr){
	if (res.Error != '0') {
		xwing.Dialog.alert('REASON : ' + res.ResultMessage);
		return;
	}

	switch(reqId){
	case 'checkModifyState':
		var idx = m_ds_list.getCursor();
		if( res.DS_MODIFY_SEARCH.record[0][0] == 'Y'){
			// 수정 중
			m_ds_list.setValue(idx,'CHG_FLAG',"Y");
			m_ds_list.setValue(idx,'CHG_ID',res.DS_MODIFY_SEARCH.record[0][1]);
			m_ds_list.setValue(idx,'CHG_NAME',res.DS_MODIFY_SEARCH.record[0][2]);
		}else{
			m_ds_list.setValue(idx,'CHG_FLAG',"N");
			m_ds_list.setValue(idx,'CHG_ID',"");
			m_ds_list.setValue(idx,'CHG_NAME',"");
		}
		break;
	case 'updateModifyState':
		var idx = m_ds_list.getCursor();
		// 수정 중
		m_ds_list.setValue(idx,'CHG_FLAG',"Y");
		m_ds_list.setValue(idx,'CHG_ID',global("G_USER_ID"));
		m_ds_list.setValue(idx,'CHG_NAME',global("G_USER_NAME"));
		break;
	}
}
/**************************************************
 *  End Check Change State
***************************************************/

function hangul_to_jaso(text){
    //초성(19자) ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
    var ChoSeong = new Array (0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
                0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
                0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e );
        //중성(21자) ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ(9) ㅙ(10) ㅚ(11) ㅛ ㅜ ㅝ(14) ㅞ(15) ㅟ(16) ㅠ ㅡ ㅢ(19) ㅣ
    var JungSeong = new Array (0x314f, 0x3150, 0x3151, 0x3152, 0x3153,
                0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b,
                0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 );
        //종성(28자) <없음> ㄱ ㄲ ㄳ(3) ㄴ ㄵ(5) ㄶ(6) ㄷ ㄹ ㄺ(9) ㄻ(10) ㄼ(11) ㄽ(12) ㄾ(13) ㄿ(14) ㅀ(15) ㅁ ㅂ ㅄ(18) ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
    var JongSeong = new Array (0x0000, 0x3131, 0x3132, 0x3133, 0x3134,
                0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d,
                0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146,
                0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e );
    var chars = new Array()
    var v = new Array();
    for (var i = 0; i < text.length; i++){
        chars[i] = text.charCodeAt(i);
        //// "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
        if (chars[i] >= 0xAC00 && chars[i] <= 0xD7A3) {
            var i1, i2, i3;

            i3 = chars[i] - 0xAC00;
            i1 = i3 / (21 * 28);
            i3 = i3 % (21 * 28);

            i2 = i3 / 28;
            i3 = i3 % 28;

            v.push(String.fromCharCode(ChoSeong[parseInt(i1)]));

            //복모음 분리
            switch(parseInt(i2)){
                case 9 : v.push('ㅗㅏ'); break;
                case 10 : v.push('ㅗㅐ'); break;
                case 11 : v.push('ㅗㅣ'); break;
                case 14 : v.push('ㅜㅓ'); break;
                case 15 : v.push('ㅜㅔ'); break;
                case 16 : v.push('ㅜㅣ'); break;
                case 19 : v.push('ㅡㅣ'); break;

                default : v.push(String.fromCharCode(JungSeong[parseInt(i2)]));
            }

            // c가 0이 아니면, 즉 받침이 있으면
            if (i3 != 0x0000) {
                //복자음 분리
                switch(parseInt(i3)){
                    case 3 : v.push('ㄱㅅ'); break;
                    case 5 : v.push('ㄴㅈ'); break;
                    case 6 : v.push('ㄴㅎ'); break;
                    case 9 : v.push('ㄹㄱ'); break;
                    case 10 : v.push('ㄹㅁ'); break;
                    case 11 : v.push('ㄹㅂ'); break;
                    case 12 : v.push('ㄹㅅ'); break;
                    case 13 : v.push('ㄹㅌ'); break;
                    case 14 : v.push('ㄹㅍ'); break;
                    case 15 : v.push('ㄹㅎ'); break;
                    case 18 : v.push('ㅂㅅ'); break;

                    default : v.push(String.fromCharCode(JongSeong[parseInt(i3)]));
                }
            }

        }else {
            v.push(String.fromCharCode(chars[i] ));
        }
    }

    var return_str = v.join('');
    return return_str;
}
