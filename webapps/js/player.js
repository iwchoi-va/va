/*
 * file_id: rec_key.wav
 * obj: 플레이어 담을 element
 * popup_yn: 팝업여부
 */


/*st*/
var rec_key,
	contact_id,
	start_time, //지금 화면상에서 UCID_DATE로 보내고있는데 쓰이고 있지 않다. 20180418 
	keyword,
	server_ip,
	play_user_id,
	log_gb,
	file_path,
	agent_id,
	user_grade,
	ced_no;
var dialog_player = null;
var browser = "";

function play(param, obj, popup_yn){
	this.rec_key = param.rec_key;
	this.contact_id = param.contact_id== null? "" : param.contact_id;
	this.start_time = param.start_time;
	this.keyword = param.keyword == null? "" : param.keyword;
	this.play_user_id =  window.user_id == "" || window.user_id == undefined? global("G_USER_ID") :  window.user_id ;
	this.log_gb = param.log_gb == null? "L" : param.log_gb;
	this.file_path = param.file_path == null? "" : param.file_path;
	this.agent_id = param.agent_id == null? "" : param.agent_id;
	this.ced_no = param.ced_no == null? "" : param.ced_no;
	this.user_grade = window.user_grade == "" || window.user_grade == undefined? global("G_USER_GRADE") :  window.user_grade;
	/*var preElem = eval("top.window.document.getElementById('V-SENS PLAYER')");
	if(!g_isNull(preElem)) player_unLoad();
	
	var idx = eval("top.Xwing.getDataset('DS_SERVER').indexOfRow('CODEID','02')");
	server_ip = eval("top.Xwing.getDataset('DS_SERVER').getValue("+idx+",'ETC3')");*/
	var paramStr = "";
	//var url = global("G_DOMAINURL") == null? opener.top.global.G_DOMAINURL : global("G_DOMAINURL");
	//var url = "https://vadev.aig.co.kr/msens";
	
	
	if(window.location.protocol == "http:"){
		if(window.location.hostname.indexOf("127") != -1){
			var url = window.location.protocol+"//"+window.location.hostname+":8080/msens";//->local
		}
	}else{
		var url = window.location.protocol+"//"+window.location.hostname+"/msens"; //https-:uat
	}
	
	if(window.location.port == "4443"){
			var url = window.location.protocol+"//"+window.location.hostname+":"+window.location.port+"/msens";//->third party
	}
	
	
	
	//(window);
	//(url);

	browserCheck();
	
	//("RUDFH");
	//(window.location.href);
	
	if(browser == "ie"){
		url += "/PLY_IE/player_new(wmp).jsp?";
	}else{
		url += "/player_html5/player.html?";
	}
	
	
	
	if( popup_yn=='Y' ){ //popup
//		setTimeout(function(){
//			var opt2 = {
//					modal: false
//					, resizable: false
//					, draggable:true
//					, toogleicon: false //최대화 버튼
//					, id : 'V-SENS PLAYER'
//					, url : '../REC/PLAYER.xhtml'
//					, width : 510
//					, height : 740
//					, title : "V-SENS PLAYER"
//					, param : {   rec_key: rec_key
//								, contact_id: contact_id
//								, start_time: start_time
//								, keyword: keyword
//							    , server_ip: server_ip
//							  }
//					, close : player_unLoad
//				};
//	
//			eval("dialog_player = new top.xwing.Dialog(opt2)");
//			eval("dialog_player.open()");
//		},100);
		paramStr = "rec_key="+rec_key+"&start_time="+start_time+"&keyword="+keyword+"&play_user_id="+play_user_id + "&user_grade=" + user_grade+"&log_gb="+log_gb+"&file_path="+file_path+"&agent_id="+agent_id+"&ced_no="+ced_no;
//		window.open(global("G_DOMAINURL")+"/player_html5/player.html?"+paramStr, "V-SENS PLAYER", "width=500, height=740, left=0, top=0, location=no, resizable=no, menubar=no, status=no");
		window.open(url+paramStr, "M-SENS PLAYER", "width=500, height=677, left=0, top=0, location=no, resizable=no, menubar=no, status=no");
	
	} else if( popup_yn=='N' ){ //embed(녹취리스트)
		//paramStr = "server_ip="+server_ip+"&rec_key="+rec_key+"&contact_id="+contact_id+"&start_time="+start_time+"&keyword="+keyword+"&play_user_id="+play_user_id + "&user_grade=" + top.getGradeAuth(global("G_USER_GRADE"));
		//Xwing.getPanel(obj).setUrl(global("G_DOMAINURL")+"/player_html5/player.html?"+paramStr);
		paramStr = "rec_key="+rec_key+"&start_time="+start_time+"&keyword="+keyword+"&play_user_id="+play_user_id + "&user_grade=" + user_grade+"&log_gb="+log_gb+"&file_path="+file_path+"&agent_id="+agent_id+"&ced_no="+ced_no;
		//(url+paramStr);
		Xwing.getPanel(obj).setUrl(url+paramStr);
//		Xwing.getPanel(obj).setUrl("http://102.90.1.176:8090/MSENS/player_html5/player.html?"+paramStr);
	}
}

function browserCheck(){

	var agent = window.navigator.userAgent.toLowerCase();
    if ( (window.navigator.appName == 'Netscape' && window.navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ) {
        browser = "ie";
        //$scope.ie_f = true;
    }
    else {
        if (agent.indexOf("chrome") != -1)  browser = "chrome";
        else if (agent.indexOf("safari") != -1)  browser = "safari";
        else if (agent.indexOf("firefox") != -1)  browser = "firefox";
    }

}


function player_unLoad(){
	var v_body = eval("top.window.document.body");
	v_body.removeChild(eval("top.window.document.getElementById('M-SENS PLAYER')"));
	dialog_player = null;
}

function openTunning(paramSet) {
	top.tunning_popup(paramSet);
}
