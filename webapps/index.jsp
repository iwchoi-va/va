<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<link rel="shortcut icon" href="msens_icon.ico" />
<link rel="icon" href="msens_icon.ico" />
<script>
function setCookie(cName, cValue, cDay){
    var expire = new Date();
    expire.setDate(expire.getDate() + cDay);
    cookies = cName + '=' + escape(cValue) + '; path=/ '; 
    if(typeof cDay != 'undefined') cookies += ';expires=' + expire.toGMTString() + ';';
    document.cookie = cookies;
}

function getCookie(cName) {
    cName = cName + '=';
    var cookieData = document.cookie;
    var start = cookieData.indexOf(cName);
    var cValue = '';
    if(start != -1){
         start += cName.length;
         var end = cookieData.indexOf(';', start);
         if(end == -1)end = cookieData.length;
         cValue = cookieData.substring(start, end);
    }
    return unescape(cValue);
}

function onload() {
	var agt = navigator.userAgent.toLowerCase();
	
	//if (agt.indexOf("chrome") != -1){
		
	//	alert("로그인을 수행해주세요");
	//	console.log("로그인 고고고");	
		var w = window.open("./common/login.xhtml", "LOGIN","width=679, height=447, left=0, top=0, location=no, resizable=0");
		
		if (w == null) {
			alert("팝업차단을 해제 후 접속 해 주세요.");
			return;
		}
		
		document.notice.src="./images/go_vsens.jpg";
		setTimeout("window.close()", 300);
		//window.open("about:_blank", "_self").self.close();
	//} else {
	//	document.notice.src="./images/brower_notice.jpg";
	//	sleep(500);
	//	document.location= location.href + "/temp/ChromeStandaloneSetup.exe";
	//}
	/*
	// 인터넷옵션 신뢰할 수 있는 사이트에 서버 IP 추가
	if(window.navigator.userAgent.indexOf("MSIE")>=0){
		//fncIconCreate();
	} 
	var w = window.open("./common/login.xhtml", "LOGIN","width=796, height=596, left=0, top=0, location=no, resizable=no");
	
	if (w == null) {
		alert("팝업차단을 해제 후 접속 해 주세요.");
		return;
	}
	//window.open("about:_blank", "_self").close(); //실제 사용시 주석 제거		
	*/
	
	/**
	* Chrome 체크 후 설치
	*/
	//var agt = navigator.userAgent.toLowerCase();
	//if (agt.indexOf("chrome") != -1){
	//	var v_flag = getCookie("make_icon");
	//	if(v_flag == "make"){
			/*  var w = window.open("./common/login.xhtml", "LOGIN","width=796, height=596, left=0, top=0, location=no, resizable=no");
			
			if (w == null) {
				alert("팝업차단을 해제 후 접속 해 주세요.");
				return;
			} */
			//window.open("about:_blank", "_self").close();
	//	} else {
	//		setCookie("make_icon", "make", 365);
	//		document.notice.src="./images/makeinfo.jpg";
	//	}
	//} else {
	//	document.notice.src="./images/brower_notice.jpg";
	//	sleep(500);
	//	document.location= location.href + "/temp/ChromeStandaloneSetup.exe";
	//}	
	
}

function fncIconCreate() {	
	var WshShell = new ActiveXObject("WScript.Shell");
	strDesktop = WshShell.SpecialFolders("Desktop");
	
     var oUrlLink = WshShell.CreateShortcut(strDesktop + "\\IS_VOC.url");
	 //if(oUrlLink.TargetPath.indexOf("http://10.1.12.6:8080/IS_CNVu") > -1)	return;
	 
	 oUrlLink.TargetPath = "http://localhost:8090/MSENS/index.jsp";
     oUrlLink.Save();
}

function sleep(milliseconds) {
	var start = new Date().getTime();
	
	for(var i = 0; i < 1e7; i++) {
		if((new Date().getTime() - start) > milliseconds){
	    	break;
	   	}
	}
}
	
</script>
<title>M-SENS</title>
</head>
<body onload="onload()">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="center">
		<img name="notice">
		</td>
	</tr>
</table>
</body>
</html>
