var websocket = null;
var wsUri = '';
//var wsUri = "ws://127.0.0.1:8888/admin/websocket";

var reCnt = 0;

var wsDialog;
var dialogFlag = false;

function initWebSocket() {
	var url = document.location.host;
	wsUri = 'ws://' + url + '/IS_STAT/websocket';
	websocket = new WebSocket(wsUri);
	websocket.onopen = function(evt){onOpen(evt)};
	websocket.onclose = function(evt){onClose(evt)};
	websocket.onmessage = function(evt){onMessage(evt)};
	websocket.onerror = function(evt){onError(evt)};
}

function onOpen(evt) {
	reCnt = 0;
//	Xwing.getLabel('lbl_alarm').setValue('알람받는중');
	Xwing.getLabel('btn_alarm').setIcon('../images/btn_bell_on.png');
	Xwing.getLabel('btn_alarm').setTooltiptext('장애알람받기 해제');
}

function onClose(evt) {
//		Xwing.getLabel('lbl_alarm').setValue('알람해제됨');
		Xwing.getLabel('btn_alarm').setIcon('../images/btn_bell_off.png');
		Xwing.getLabel('btn_alarm').setTooltiptext('장애알람 다시받기');
		websocket = null;
}

function onMessage(evt) {
	
	var obj = JSON.parse(evt.data);
	
	var key = 'anymore_' + global.G_USER_ID + "-" + obj.code + '-' + obj.param1 + '-' + obj.param2;
	
	
	if(localStorage.getItem(key) != "anymore") {
		if(obj.level <= global.G_ALARM_LV && global.G_ALARM_LV != 0) {
			if(dialogFlag == false && obj.f_type == 1) {
				dialogFlag = true;	// will open dialog
				
				var alarmPop = {
						id:'alarmPop',
						url:'../cm_admin/alarm/alm300m10p.xhtml',
						left: -1,
						top:-1,
						width: 398,
						height:480,
						param : {
							obj:obj
						},
						resizable:false,
						draggable:true,
							title:'알람발생',
						modal:false
					};
					
				wsDialog = new xwing.Dialog(alarmPop);
				wsDialog.closeIcon.hide();
				wsDialog.open();
			} else {
				Xwing.children.alarmPop.setData(obj);
			}
		}
	}
	
}

function onError(evt) {
//	Xwing.getLabel('lbl_alarm').setValue('알람해제됨');
//	Xwing.getLabel('lbl_alarm').setTooltiptext('알람 다시받기');
	websocket = null;
	new xwing.Dialog.alert('알람서버와 연결이 원할하지 않습니다. 알람을 받으시려면 재접속해주십시오.', '장애알람');
}

function doSend(message) {
	websocket.send(message);
}


function openWebsocket() {
	initWebSocket();
}

function closeWebsocket() {
	websocket.close();
}


//인터넷 창을 닫았을 경우
window.onbeforeunload = function() {
	// 윈도우 창을 닫을 때 로그아웃 처리
	if( event.clientY < 0 ){
        // 로그아웃 처리
		websocket.close();
     }	
};

document.onkeydown = function() {
    // 새로고침 방지 ( Ctrl+R, Ctrl+N, F5 )
    if ( event.ctrlKey == true && ( event.keyCode == 78 || event.keyCode == 82 ) || event.keyCode == 116) {
         event.keyCode = 0;
         event.cancelBubble = true;
         event.returnValue = false;
    }

    // 창 닫기( Alt+F4 ) 방지 
    if ( event.keyCode == 115) { // F4 눌렀을 시
      // 로그아웃 처리
    }

    // 윈도우 창이 닫힐 경우
    if (event.keyCode == 505) { 
        alert(document.body.onBeforeUnload);
   }	
};
