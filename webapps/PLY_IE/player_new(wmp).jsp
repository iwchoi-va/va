<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="viewport" content="width=device-width">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        
        <link href="data:image/gif;" rel="icon" type="image/x-icon" />
        <link rel="shortcut icon" href="../msens_icon.ico" />
        <link rel="stylesheet" type="text/css" href="css/XEIcon/xeicon.min.css"/>
        <link rel="stylesheet" href="css/waveform.css" />
        <link rel="stylesheet" href="css/control.css" />
        <link rel="stylesheet" href="css/sttarea.css" />
 		<link rel="stylesheet" href="css/sub-menu.css" />
 		
 		<!-- jQuery -->
		<script type='text/javascript' src='../chart/lib/jquery-1.11.1.js'></script>  
<!--		<script type="text/javascript" src="../js/json2.js"></script> -->
		<script type='text/javascript' src='./src/util.js'></script> 
    	<script type='text/javascript' src='./src/wmp.js'></script>  
		<script type='text/javascript' src='./src/content.js'></script> 
		<script type='text/javascript' src='./src/waveform.js'></script> 
		
		<style>
			/*#passage-text, #passage-text-tx, #passage-text-rx {
			   -ms-user-select: none; 
			   -moz-user-select: -moz-none;
			   -khtml-user-select: none;
			   -webkit-user-select: none;
			   user-select: none;
			 }*/
			.visibleNone{
			  display:none;
		   }
		</style>
	</head>
	
    <body>
    	<div id="container" class="container">
			<div id="wmp">
			</div>
			
            <div id="demo">

            	<!-- waveform -->
                <div id='waveform' >
                </div>
                
           	
				<!-- timer area -->
				<div class="timer">
					<span class="track-start-time" ></span>
					<span class="track-end-time" ></span>
				</div>

				
				<!-- control button area -->
				<div class="control">
	                <div class="button">
						<!-- data-action: example/trivia.js,  class: bootstrap.min.css -->
	                    <button title="키보드 숫자 단축키: 1" class="btn-back" style="cursor:pointer" ></button>
	                    <button title="키보드 숫자 단축키: 2" class="btn-pause" style="cursor:pointer"></button>
	                    <button title="키보드 숫자 단축키: 3" class="btn-play" style="cursor:pointer"></button>
	                    <button title="키보드 숫자 단축키: 4" class="btn-stop" style="cursor:pointer"></button>
	                    <button title="키보드 숫자 단축키: 5" class="btn-forth" style="cursor:pointer"></button>
	                    <div class="volume">
	                    	<div class="img"><img src="images/volume.png" alt="volum-up icon" /></div>
	                    	<input type="range" id="volume-slider" class="slider" min="-4500" max="0" value="0" step="100">
		                </div>
		                <div class="speed-mode" style="height:40px;">
		              			<div style="" class="speedTitle">SP</div>
		                    	<div id="speedValue" class="speed-num" style="">1.0</div>
								<div class="rotateTitle" id="rotateA">A</div>
								<div style="left:395px;" class="rotateTitle" id="rotateB">B</div>
								<div style="top:77px;left:410px;cursor:pointer;" class="speedTitle" id="rotateBtn" onclick="rotBtnClick()" ><i class="xi-repeat xi-2x"></i></div>
		                    	<input type="range" class="slider" name="slider" id="speed-slider" min="0.5" max="3.0" value="1.0" step="0.1" onchange="updateSpeed(this.value);" />
		                    	
		               </div>
	                </div>
		            <!--     
		            <div class="button2" style="display:none">
	                	 <button class="btn-plus" title="+0.1배속"></button> 
	                	<span class="speed-num" title="정배속으로">1</span>
		            	<button class="btn-minus" title="-0.1배속"></button> 
		            </div> 
		             --> 
		           
		            <div class="setting">   
		                <img src="images/setting.png" class="btn_setting" onclick = "document.getElementById('light').style.display='block';document.getElementById('fade').style.display='block'" alt="설정" />
		                <div id="light" class="setting_content"> 
		                	<div class="font-mode">
			                	<font>글자 크기</font>
			                	<input type="button" class="button" id="font-size-down" style="background:#5b6172 url(images/font-size-down.png) 50% 50% no-repeat;" onclick="resizeFont('down')" />
              					<input type="button" class="button" id="font-size-up" style="background:#5b6172 url(images/font-size-up.png) 50% 50% no-repeat;"  onclick="resizeFont('up')" />
		                	</div>
		                </div>
		                <div id="fade" class="black_overlay" onclick = "document.getElementById('light').style.display='none';document.getElementById('fade').style.display='none'"></div>
		            </div>  
		             <!--
		             <div id="tuning" class="tuning">
				            
						    	<img src="../images/icon_typelist.png" class="btn_tuning" onclick="openTunning('02')" alt="tuning" />
						     
				    </div>--> 

				    </div>
				     
			    </div> 
			    

			    <div class="sub-menu">
			    	<input type="button" class="btn_info" id="callinfo" title="콜 정보" style="background:url('./images/callinfo.png') 50% 50% no-repeat;" onclick=openLayer(this.id) />
			    	<!-- <input type="button" class="btn_rank" id="rank" title="TOP keyword" style="background:url('./images/rank.png') 50% 50% no-repeat;" onclick=openLayer(this.id) /> -->
			   	    <!-- <input type="button" class="btn_summary" id="summary" title="script memo" style="background:url('./images/summary.png') 50% 50% no-repeat;" onclick=openLayer(this.id) /> --> 
			    	<input type="button" class="btn_history" id="history" title="연계 콜" style="background:url('./images/history.png') 50% 50% no-repeat;" onclick=openLayer(this.id) />
			    	<input type="button" class="btn_memo" id="memo" title="QC메모" style="display:none;background:url('./images/memo.png') 50% 50% no-repeat;" onclick=openLayer(this.id) />
			    	<input type="button" class="btn_close" id="history" title="close" style="background:url('./images/close.png') 50% 50% no-repeat;" onclick=closeLayer() />
			    </div>
			    
			    <div id="search" class="search">
                    <div class="img-wrapper"><img src="images/search.png" alt="search icon" /></div>
		            <input type="text" id="srcKey" class="srcKey" style="ime-mode:active;font-family: 'Malgun Gothic';" onkeydown="" />
		             <div class="browser-view"><img id="toggleBtn" src="images/split.png" style="cursor: pointer;display:none;" alt="" onclick=toggle() /></div> 
				</div>
            </div>
		
		<script>
			
			if(window.location.protocol == "http:"){
				if(window.location.hostname.indexOf("127") != -1){
					var host = window.location.protocol+"//"+window.location.hostname+":8080/msens/";
				}
			}else{
				var host = window.location.protocol+"//"+window.location.hostname+"/msens/"; 
			}
			
			if(window.location.port == "4443"){
				var host = window.location.protocol+"//"+window.location.hostname+":"+window.location.port+"/msens/";//->third party
			}
			
			var path = "temp/";
			var file_name = "";
			var rtx = "N";
			var rec_keyword = "";
			var auth_callmemo="24,27,28,29,35,36"; //임시로 24도 넣어둠.
			var sp_rate=0.1, sp_min=0.5, sp_max=3.0, sp_num=1.0;
			var tm_yn = false; // tm_청취여부 flag 추가
			var A_position = 0;
			var B_position = 0;
			var rotateFlag = 0; // 0이면 A 구간 지정, 1이면 B구간 지정, 2면 구간반복 종료
			
			var meta = {
					rec_key: "",
					file_len: 0, 
					regist_no: 0,
					data: "",
					start_time: "", 
					keyword: "", 
					user_grade: "",
					play_user_id: "", 
					cust_name: "",
					system_gb: "",
					incall_no: "",
					log_gb:"",
					file_path : "",
					agent_id : "",
					ced_no : "",
					batch_yn : "N"
				};
			
			var controls =  {
		    		btnPlay: document.querySelector('.btn-play'),
		    		btnPause: document.querySelector('.btn-pause'),
		    		btnStop: document.querySelector('.btn-stop'),
		    	    timeElapsed: document.querySelector('.track-start-time'),
		    	    timeTotal: document.querySelector('.track-end-time'),
		    	    btnPrevious: document.querySelector('.btn-back'),
		    	    btnNext: document.querySelector('.btn-forth'),
		    	    btnVolumeDown: document.querySelector('.btn-volume-down'),
		    	    btnVolumeUp: document.querySelector('.btn-volume-up'),
		    	    btnPlus: document.querySelector('.btn-plus'),
		    	    btnMinus: document.querySelector('.btn-minus')
		    	};
			
			var width=500, height=650;
			var open_stt_top='355px', open_src_top='360px'; //445
			var close_stt_top='160px', close_src_top='170px'; //261
			
			$( window ).bind("resize", function(){

				if(parent.$pan_player != undefined){
					width= parent.$pan_player.getAttribute("width");
					height= parent.$pan_player.getAttribute("height");
				}
				
				var temp_height = 400;
				var minus_height = 225;	//0이었는데 변경
				var temp_top = close_stt_top;

				if(document.getElementById('sub-container') != null){
					minus_height = 480;
					temp_top = open_stt_top;
				}
				
				if(document.getElementById('passage-text')!= null){
					document.getElementById('passage-text').style.height = (height-minus_height)+'px';
					document.getElementById('passage-text').style.top = temp_top;
				}
				if(rtx=="Y"){
					if(document.getElementById('passage-text-tx') != null){
						document.getElementById('passage-text-tx').style.height = (height-minus_height)+'px'; 
						document.getElementById('passage-text-tx').style.top = temp_top;
					}
					if(document.getElementById('passage-text-rx') != null){
						document.getElementById('passage-text-rx').style.height = (height-minus_height)+'px'; 
						document.getElementById('passage-text-rx').style.top = temp_top;
					}
				}				
			});
			
			$(document).ready(function(){
				document.body.oncontextmenu = function(){return false;};
				
				wmp.empty();
				
				if(parent.$pan_player != undefined){
					width= parent.$pan_player.getAttribute("width");
					height= parent.$pan_player.getAttribute("height");
				}
				var query = location.search.substring(1);
				
				var parameters = {};
				var keyValues = query.split(/&/);
				for (var prop in keyValues) {
				    var keyValuePairs = keyValues[prop].split(/=/);
				    var key = keyValuePairs[0];
				    var value = keyValuePairs[1];
				    parameters[key] = value;
				}
				meta.start_time   = parameters['start_time']; //parent.start_time;
				meta.rec_key      = parameters['rec_key']; //parent.rec_key;
				meta.keyword      = parameters['keyword'];
				meta.regist_no 	  = parameters['regist_no'];
				meta.play_user_id = parameters['play_user_id'];
				meta.system_gb	  = parameters['system_gb'];
				rec_keyword		  = parameters['rec_keyword'];
				meta.log_gb    = parameters['log_gb'];
				meta.file_path = parameters['file_path'] == undefined ? '' : parameters['file_path'];
				meta.agent_id = parameters['agent_id'];
				meta.ced_no = parameters['ced_no'] == undefined ? '' : parameters['ced_no'];
				meta.user_grade	  = parameters['user_grade'];
				meta.batch_yn	  = parameters['batch_yn'] == undefined ? meta.batch_yn : parameters['batch_yn'];
				
				//파일패스가 없으면 tm에서 별도로 플레이어를 호출한거임
				if(meta.file_path == undefined || meta.file_path == ""){
					tm_yn = true;
					meta.log_gb= "L";					
				}
				
				// Windows Media Player object create
				//file_name = host+path+meta.rec_key+".wav";
				//file_name = host + path + "20170609_140433_01062068163-8k-16bit-mono-pcm.wav"
				// control buttons event
				if(meta.log_gb == 'S'){
					controls.btnPlay.addEventListener('click', function() {
						loadAudio(); //file download
						saveLog('L'); //listen log
						this.removeEventListener('click',arguments.callee,false);
						controls.btnPlay.addEventListener('click', function() { wmp.play(); });
					});
				}else if(meta.log_gb == 'L'){
					controls.btnPlay.addEventListener('click', function() {
						if(wmp.getAudio() != null) wmp.play();
					});
				}
				controls.btnPause.addEventListener('click', function() {
					if(wmp.getAudio() != null) wmp.pause();
				});
				controls.btnStop.addEventListener('click', function() {
					if(wmp.getAudio() != null) wmp.stop();
				});
				controls.btnPrevious.addEventListener('click', function() {
					if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()-3);
				});
				controls.btnNext.addEventListener('click', function() {
					if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()+3);
				});
				
			/*	
				controls.btnPlus.addEventListener('click', function() {
					if(wmp.getAudio() != null) {
						sp_num = parseFloat(wmp.getPlayBackRate().toFixed(1)) + parseFloat(sp_rate.toFixed(1)); 
						
						if(sp_num <= sp_max){
							document.querySelector('.speed-num').textContent = sp_num;
							wmp.setPlaybackRate(sp_num);
						}
					}
				});
				controls.btnMinus.addEventListener('click', function() {
					if(wmp.getAudio() != null) {
						sp_num = parseFloat(wmp.getPlayBackRate().toFixed(1)) - parseFloat(sp_rate.toFixed(1)); 
						
						if(sp_num >= sp_min){
							document.querySelector('.speed-num').textContent = sp_num;
							wmp.setPlaybackRate(sp_num);
						}
					}
				});
				
			*/	
				
				/*
				var img = document.createElement('img');
				img.src = "../images/icon_typelist.png";
				img.className = "btn_tuning";
				img.onclick = function() { openTunning('02'); };
				img.alt="tuning";
				
				document.getElementById("tuning").appendChild(img);
				document.getElementById("tuning").style.display = "none"; //튜닝화면 없으므로 display:none 처리(튜닝화면 필요하면 해당 부분 제거)
				*/
		
				if(meta.file_path != undefined && meta.file_path != ''){
					file_name = meta.file_path.substring(meta.file_path.lastIndexOf("/")+1);
					file_name = host+path+file_name;
				}

				setAuth();
				
				//20190415 TEST
				//meta.rec_key ='SMC-00003000581530690363';
				
				stt.loadText(host, meta.rec_key, meta.start_time);
				
			 	if(meta.log_gb != 'S') loadAudio();
				if(!tm_yn) saveLog(meta.log_gb);
	
			});
			
		    function setAuth(){
		    	if(auth_callmemo.indexOf(meta.user_grade) != -1){
					document.getElementById("memo").style.display = "inline";
				}
		    }
			
			/*******************************************************************************
			** 녹취플레이어 파형보이게 하는 방법
			1. downloadFile.java에 WaveData.extractData 호출하는 부분 주석 풀기
			2. wmp.js의 generateWindowsMediaPlayer 함수에서  wmp.progBar(); 주석 처리
			3. content.js의 mark 함수의 주석풀기
			********************************************************************************/
			function createWaveform(){
			
				var hostname = window.location.hostname.split(".");
				if(window.location.port != "4443"){
						file_name = window.location.protocol+"//"+window.location.hostname+"/audio/temp/"+meta.file_path.substring(meta.file_path.lastIndexOf("/")+1);
				}else{
				  if(hostname[0] != "va"){
					file_name = window.location.protocol+"//"+window.location.hostname+":"+window.location.port+"/audio/temp/"+meta.file_path.substring(meta.file_path.lastIndexOf("/")+1);
				  }else{
					file_name = window.location.protocol+"//"+window.location.hostname+"/audio/temp/"+meta.file_path.substring(meta.file_path.lastIndexOf("/")+1);
				  }
				}
				//01_9124_010XXXX2057_2018_09_19_17_46_05_002.wav
				//file_name = 'http://127.0.0.1:8080/msens/temp/00003000581530690363.wav';
				//console.log("%%%%%%%%오디오 호출경로");
				//console.log(file_name);
				
				generateWindowsMediaPlayer("wmp","MediaPlayer", file_name);
				
        		document.getElementById("waveform").addEventListener("click", function(e){
         			//var perc = e.offsetX / $(this).width();
         			var perc = e.clientX / $(this).width();
         		  	var pos = wmp.getDuration() * perc;
         		  	wmp.seekTo(pos);
				});
         		
         	
         		

				// timelaps
				//window.setInterval("whilePlaying()",500);
			}
			

		/* 	window.addEventListener('focus',function(){
				console.log("focus!");
				tm_yn = false;
			});		
			window.addEventListener('focusout',function(){
				if(document.hasFocus()){
				
				}else{
					console.log("focusout!");
					tm_yn = true;
				}
			});			
		

		 document.getElementById("container").addEventListener("blur",function(event){
			 console.log("window lost Focus");
		 },false);
		 document.getElementById("container").addEventListener('focus',function(){
				console.log("focus!");
			});		
		 */
		 
			$(document).keydown(function(zEvent){
				
				var johap = -1;
				
				//shift + 조합키zEvent.shiftKey && 
				 if(zEvent.keyCode != "" || zEvent.keyCode == 13){ 
					johap = zEvent.keyCode;
				}
				
				switch (johap) {
				
				 case 49 : //숫자 1
					 if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()-3);	
					break;
			 	case 97 : //숫자 NumLock 1
			 		if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()-3);
					break;	
			 	case 50 : //숫자 2
			 		if(wmp.getAudio() != null) wmp.pause();
					break;
			 	case 98 : //숫자 NumLock 2
			 		if(wmp.getAudio() != null) wmp.pause();
					break;	
			 	case 51 : //숫자 3
			 		if(wmp.getAudio() != null){
				 		if(!$("#srcKey").is(":focus"))
							wmp.play(); 
			 		}
					break;
			 	case 99 : //숫자 NumLock 3
			 		if(wmp.getAudio() != null){
				 		if(!$("#srcKey").is(":focus"))
							wmp.play(); 
			 		}
					break;	
			 	case 52 : //숫자 4
			 		if(wmp.getAudio() != null) wmp.stop();	
					break;
			 	case 100 : //숫자 NumLock 4
			 		if(wmp.getAudio() != null) wmp.stop();
					break; 
				case 53 : //숫자 5
					if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()+3);
					break;
			 	case 101 : //숫자 NumLock 5
			 		if(wmp.getAudio() != null) wmp.seekTo(wmp.getCurrentTime()+3);
					break;  
				 
				 case 13 : //enter key 
					search();
					break;	
					
					
					
				/* case 104: //up arrow
					var v = wmp.getVolume();
					var max = 0; //wmp max volume
					var rate = 500;
					if(v+rate <= 0){ 
						document.getElementById('volume-slider').value = (v+rate); //화면 control
						wmp.setVolume(v+rate);
					}else{
						document.getElementById('volume-slider').value = (max); //화면 control
						wmp.setVolume(max);
					}
					
					break;
				case 98: 
					var v = wmp.getVolume();
					var min = -4500; //wmp min volume
					var rate = 500;
					if(v-rate > min){
						document.getElementById('volume-slider').value = (v-rate); //화면 control
						wmp.setVolume(v-rate);
					}else{
						document.getElementById('volume-slider').value = (min); //화면 control
						wmp.setVolume(min);
					}
					
					break;
				case 102 : 
					if(wmp.getAudio() != null) {
						sp_num =  (wmp.getPlayBackRate() + sp_rate).toFixed(1); 
						if(sp_num <= sp_max){
							updateSpeed(sp_num); 
							document.getElementById('speed-slider').value = (sp_num); 
							wmp.setPlaybackRate(sp_num);
						}else{
							updateSpeed(sp_max);
							document.getElementById('speed-slider').value = (sp_max); 
							wmp.setPlaybackRate(sp_max);
						}
					}
					
				break;		
				case 100 : 
				if(wmp.getAudio() != null) {
					sp_num = (wmp.getPlayBackRate() - sp_rate).toFixed(1); 
					if(sp_num >= sp_min){
						updateSpeed(sp_num);
						document.getElementById('speed-slider').value = (sp_num); 
						wmp.setPlaybackRate(sp_num);
					}else{
						updateSpeed(sp_min); 
						document.getElementById('speed-slider').value = (sp_min);  
						wmp.setPlaybackRate(sp_min);
					}
				} */
				 
				break;		
				default:
					break;
				}
				
	        });
			
			// move by click STT
			function sttJump(start_point){
				//seekTo() : sec
				if(wmp.getAudio() != null) wmp.seekTo( start_point/100 );
			}
			
			//키워드 bold 기능은 띄어쓰기까지 100% 매칭일때만 가능
			//backgroudcolor Highlight 기능은 띄어쓰기와 관련없이 가능
			function search(){
				var keyword = document.getElementById('srcKey').value;
				keyword_noSpace = keyword.replace(/\s+/g, '');
				stt.removeMark();
				var element = document.getElementById('passage-text');
				var elms = element.querySelectorAll('[data-start]');
				var str = "", current_span = "", str_noSpace="";
	
				for (var i=0; i<elms.length; i++) {
					str_noSpace = (elms[i].innerHTML).replace(/\s+/g, '');
					str = (elms[i].innerHTML);
					current_span = stt.words[i];
					current_span.element.innerHTML = current_span.element.innerHTML.replace(/(<([^>]+)>)/gi,''); //span 태그 제거
					current_span.element.style.backgroundColor = "#F8F8F8";
					if(keyword != '' && (str.indexOf(keyword) > -1 || str_noSpace.indexOf(keyword_noSpace) > -1) ){
						stt.mark(current_span.begin/meta.file_len*100); // 10ms 
						var highlightTag = '<span style="color:#D14A0E; font-weight:600" >'+keyword+'</span>';
						current_span.element.innerHTML = (stt.words[i].element.innerHTML).replace(keyword,highlightTag);
						current_span.element.style.backgroundColor = "#FFE3B5";
					};
				}
				
				if(rtx=="Y"){
					var element_tx = document.getElementById('passage-text-tx');
					var elms_tx = element_tx.querySelectorAll('[data-start]');
					for (var i=0; i<elms_tx.length; i++) {
						str_noSpace = (elms_tx[i].innerHTML).replace(/\s+/g, '');
						str = (elms_tx[i].innerHTML);
						current_span = stt.words_tx[i];
						current_span.element.innerHTML = current_span.element.innerHTML.replace(/(<([^>]+)>)/gi,''); //span 태그 제거
						current_span.element.style.backgroundColor = "#F8F8F8";
						if(keyword != '' && str.indexOf(keyword) > -1 || str_noSpace.indexOf(keyword_noSpace) > -1 ){
							stt.mark(current_span.begin/meta.file_len*100); // 10ms
							var highlightTag = '<span style="color:#D14A0E; font-weight:600" >'+keyword+'</span>';
							current_span.element.innerHTML =(stt.words_tx[i].element.innerHTML).replace(keyword,highlightTag);
							current_span.element.style.backgroundColor = "#FFE3B5";
						};
					}
					
					var element_rx = document.getElementById('passage-text-rx');
					var elms_rx = element_rx.querySelectorAll('[data-start]');
					for (var i=0; i<elms_rx.length; i++) {
						str_noSpace = (elms_rx[i].innerHTML).replace(/\s+/g, '');
						str = (elms_rx[i].innerHTML);
						current_span = stt.words_rx[i];
						current_span.element.innerHTML = current_span.element.innerHTML.replace(/(<([^>]+)>)/gi,''); //span 태그 제거
						current_span.element.style.backgroundColor = "#F8F8F8";
						if(keyword != '' && str.indexOf(keyword) > -1 || str_noSpace.indexOf(keyword_noSpace) > -1 ){
							stt.mark(current_span.begin/meta.file_len*100); // ms 
							var highlightTag = '<span style="color:#D14A0E; font-weight:600" >'+keyword+'</span>';
							current_span.element.innerHTML =(stt.words_rx[i].element.innerHTML).replace(keyword,highlightTag);
							current_span.element.style.backgroundColor = "#FFE3B5";
						};
					}
				}
				
			}
			
			
			function updateSpeed(sv){ // 화면 맨첨에 보이는용임. wmp.안에 또있어욤
				document.getElementById('speedValue').innerHTML=sv;
			}
			//	 font setting
			function resizeFont(gubun){
				var el = document.getElementById("passage-text");
				var els = document.querySelectorAll("#passage-text span");
				var style = window.getComputedStyle(el, null).getPropertyValue('font-size');
				var fontSize = parseFloat(style); 

				if(gubun=='up' && fontSize<30){
					el.style.fontSize = (fontSize+1) + 'px'; //기준폰트설정
					for(var i =0, len = els.length; i<len; i++)
						els[i].style.fontSize = (fontSize+1) + 'px'; //span에 각각 적용
				}else if(gubun=='down' && fontSize>10){
					el.style.fontSize = (fontSize-1) + 'px';
					for(var i =0, len = els.length; i<len; i++)
						els[i].style.fontSize = (fontSize-1) + 'px';
				}
				
				if(rtx=="Y"){
					var el_rx = document.getElementById('passage-text-rx');
					var els_rx = document.querySelectorAll("#passage-text-rx span");
					var el_tx = document.getElementById('passage-text-tx');
					var els_tx = document.querySelectorAll("#passage-text-tx span");
					
					var style_rx = window.getComputedStyle(el_rx, null).getPropertyValue('font-size');
					var style_tx = window.getComputedStyle(el_tx, null).getPropertyValue('font-size');
					var fontSize_rx = parseFloat(style_rx);
					var fontSize_tx = parseFloat(style_tx); 

					if(gubun=='up' && fontSize_rx<30){
						el_rx.style.fontSize = (fontSize_rx+1) + 'px';
						for(var i =0, len = els_rx.length; i<len; i++)
							els_rx[i].style.fontSize = (fontSize_rx+1) + 'px';
					}else if(gubun=='down' && fontSize_rx>10){
						el_rx.style.fontSize = (fontSize_rx-1) + 'px';
						for(var i =0, len = els_rx.length; i<len; i++)
							els_rx[i].style.fontSize = (fontSize_rx-1) + 'px';
					}
					
					if(gubun=='up' && fontSize_tx<30){
						el_tx.style.fontSize = (fontSize_tx+1) + 'px';
						for(var i =0, len = els_tx.length; i<len; i++)
							els_tx[i].style.fontSize = (fontSize_tx+1) + 'px';
					}else if(gubun=='down' && fontSize_tx>10){
						el_tx.style.fontSize = (fontSize_tx-1) + 'px';
						for(var i =0, len = els_tx.length; i<len; i++)
							els_tx[i].style.fontSize = (fontSize_tx-1) + 'px';
					}
				}
				
			}
			
			
			// click event for sub-menu
			function openLayer(id){
				
				//10.26 tm에서 별도로 연동된 경우 layer 안열리도록 처리
				if(tm_yn){
					return;
				}
				
				if(document.getElementById('passage-text')!= null){
					document.getElementById('passage-text').style.height = (height-420)+'px'; 
					document.getElementById('passage-text').style.top = open_stt_top;
				}
				if(rtx=="Y"){
					if(document.getElementById('passage-text-tx') != null){
						document.getElementById('passage-text-tx').style.height = (height-420)+'px'; 
						document.getElementById('passage-text-tx').style.top = open_stt_top;
					}
					if(document.getElementById('passage-text-rx') != null){
						document.getElementById('passage-text-rx').style.height = (height-420)+'px'; 
						document.getElementById('passage-text-rx').style.top = open_stt_top;
					}
				}
				
				document.getElementById('search').style.top = open_src_top;
				
				//iframe src url 
				var url;
				switch(id){
					case 'callinfo':
						url = './call_info.html';
						break;
					case 'rank':
						url = './top_keyword.html';
						break;
					case 'summary':
						url = './summary.html';
						break;
					case 'memo':
						url = './memo.html';
						break;
					case 'history':
						url = './history_chart.html';
						break;
				}
				
				if(document.getElementById('sub-container') == null){
					// div create
					var container = document.createElement('div');
					container.setAttribute('id', 'sub-container');
					container.style.height = '180px';
					document.getElementById('container').appendChild(container);
					// iframe create
					var iframe = document.createElement('iframe');
					iframe.frameBorder=0;
					iframe.height='180px';
					iframe.id="if-sub";
					
					document.getElementById('sub-container').appendChild(iframe);
				}
				var frame = document.getElementById('if-sub');
				frame.src = url;
				var frameDoc = frame.contentWindow.document;
				//frameDoc.calling();
			}
			
			function closeLayer(){
				if(document.getElementById('sub-container') != null){
					$('#sub-container').remove();
					$('#if-sub').remove();
				}
				document.getElementById('passage-text').style.height = (height-225)+'px';
				document.getElementById('passage-text').style.top = close_stt_top;
				document.getElementById('search').style.top = close_src_top;
				if(rtx=="Y"){
					document.getElementById('passage-text-tx').style.height = (height-225)+'px';
					document.getElementById('passage-text-tx').style.top = close_stt_top;
					document.getElementById('passage-text-rx').style.height = (height-225)+'px';
					document.getElementById('passage-text-rx').style.top = close_stt_top;
				}
			}
			
			// history_chart.html click
			function reloadPlayer(newrec_key, newfile_path, newdate){

				wmp.empty();
				//stt.removeWordSelection();
				document.getElementById('srcKey').value = "";
				stt.removeMark();
				
				closeLayer(); //close sub-menu
				
				document.getElementById("passage-text").remove(); // remove stt-area
				if(rtx == 'Y'){
					document.getElementById("passage-text-tx").remove(); // remove stt-area
					document.getElementById("passage-text-rx").remove(); // remove stt-area
				}
				//alert("newrec_key:"+newrec_key+",newcontact_id:"+newcontact_id+",newstart_time:"+newstart_time+",newmedia_gb:"+newmedia_gb);

				meta.rec_key = newrec_key;
				meta.file_path = newfile_path;
				meta.start_time = newdate;
				
				file_name = meta.file_path.substring(meta.file_path.lastIndexOf("/")+1);
				file_name = host+path+file_name;

				setAuth();
				stt.loadText(host, meta.rec_key, meta.start_time);
				loadAudio();
			}
			
			
			function toggle(){
		    	var img = document.getElementById('toggleBtn');
		    	
		    	if(rtx=="Y"){
		    		if(img.src.indexOf('browser.png')>-1) { //1->2
			    		img.src = img.src.replace('browser.png','split.png');
			    		document.getElementById("passage-text").style.visibility = "visible";
			    		document.getElementById("passage-text-tx").style.visibility = "hidden";
			    		document.getElementById("passage-text-rx").style.visibility = "hidden";
			    	} else { //2->1
			    		img.src = img.src.replace('split.png','browser.png');
			    		document.getElementById("passage-text").style.visibility = "hidden";
			    		document.getElementById("passage-text-tx").style.visibility = "visible";
			    		document.getElementById("passage-text-rx").style.visibility = "visible";
			    		stt.selectCurrentWord();
			    	}
		    	}
		    	
		    }
			
			function openTunning(type){
				var param={	
						gubun_tunning : type,
						rec_key_tunning: meta.rec_key,
						contact_id_tunning: meta.contact_id,
						start_time_tunning : meta.start_time,
						user_id_tunning : meta.agent_id
					};
				
				if(window.opener != null) window.opener.openTunning(param); //popup
				else top.tunning_popup(param); //embed
			}
			
			
			
			function loadAudio(play_reason,play_sayu, loadAudioYN){
	
				// loading image added
				var elem = document.getElementById('waveform');
				var img = document.createElement('img');
				img.setAttribute('id','loading');
				img.setAttribute('src','./images/loading.gif');
				img.setAttribute('style','display:none; z-index:1000; position:absolute;top:5%;left:35%;transform:translate(-13%,-35%);');
				elem.appendChild(img);
				
				this.loadAudioYN = loadAudioYN;
				wmp.loadAudio(host, meta.rec_key, meta.start_time, meta.file_path, play_reason, play_sayu,meta.batch_yn);
			
			}

			function saveLog(log_gb){
				var xhr = new XMLHttpRequest();
				var paramStr = "rec_key="+meta.rec_key+"&rec_start_time="+meta.start_time+"&play_user_id="+meta.play_user_id+"&rec_user_id="+meta.agent_id+"&log_gb="+log_gb+"&ced_no="+meta.ced_no;
				
				xhr.open("GET" , host + "player.savePlayerLog.do?"+paramStr, true);
			    xhr.onreadystatechange = function() {
			        if(xhr.readyState == 4 && xhr.status == 200) {
		
			        }
			    }
			    xhr.send();
			}
			
			function rotBtnClick(){
				if(!wmp.isPlaying() && rotateFlag != 2) return;
				
				if(rotateFlag == 0){
					A_position = wmp.getCurrentTime();
					$("#rotateBtn").removeClass("rotateOut").addClass("rotateBtnIn");
					$("#rotateA").removeClass("rotateOut").addClass("rotateIn");
					$("#rotateB").addClass("rotateOut");
					
					var wgWidth = document.getElementById('waveform').offsetWidth;
					var soFar = parseFloat(A_position / wmp.getDuration()).toFixed(3);
					var left_position = wgWidth * soFar;
						
					var playingBar = document.createElement('div');
					playingBar.id = "barA";
					playingBar.style.cssText = 'position:absolute;z-index:1;top:0px;left:'+left_position+'px;width:3px;height:50px;background-color:#24C89F;border-right:solid 1px #009494'; //#FC9E49
					document.getElementById('waveform').appendChild(playingBar);

					
				}else if(rotateFlag == 1){
					B_position = wmp.getCurrentTime();
				
					var wgWidth = document.getElementById('waveform').offsetWidth;
					var soFar = parseFloat(B_position / wmp.getDuration()).toFixed(3);
					var left_position = wgWidth * soFar;

					var playingBar = document.createElement('div');
					playingBar.id = "barB";
					playingBar.style.cssText = 'position:absolute;z-index:1;top:0px;left:'+left_position+'px;width:3px;height:50px;background-color:#24C89F;border-right:solid 1px #009494'; //#FC9E49
					document.getElementById('waveform').appendChild(playingBar);

					
					$("#rotateB").removeClass("rotateOut").addClass("rotateIn");
					
					wmp.seekTo(A_position);
				}else if(rotateFlag == 2){
					rotateFlag = -1; //초기화
					A_position = 0;
					B_position = 0;
					
					$("#rotateBtn").removeClass("rotateBtnIn").addClass("rotateOut");
					$("#rotateA").removeClass("rotateIn").addClass("rotateOut");
					$("#rotateB").removeClass("rotateIn").addClass("rotateOut");
					
					$("#barA").remove();
					$("#barB").remove();
					
				}
				rotateFlag ++;
				
			}
			
		</script>
		
		
    </body>
    
</html>    