'use strict';

var audio = null;
var si = null;
var createFlag = false;
var barId = 'progBar',
	playedId = 'played';

var autoPlay;



function generateWindowsMediaPlayer(holderId,objId,audioUrl) {
	
	var cookie = document.cookie.match('(^|;) ?volume=([^;]*)(;|$)');
	var volume = cookie? cookie[2] : -2000;
	
	document.getElementById('volume-slider').value = volume;
	
    var holder = document.getElementById(holderId);
    var player = "<object id='"+ objId +"' width='0%' height='0%'";

    audioUrl = encodeURI(audioUrl); // Encode for special characters

    if (navigator.userAgent.indexOf("MSIE") < 0) {
        // Chrome, Firefox, Opera, Safari, IE(edge)
        //player += 'type="application/x-ms-wmp" '; //Old Edition
    	 player += "type='video/x-ms-wmp' "; //New Edition, suggested by MNRSullivan (Read Comments)
         player += "data='" + audioUrl + "' >";
    }
    else {
        // Internet Explorer(<=10)
        player += "classid='CLSID:22D6F312-B0F6-11D0-94AB-0080C74C7E95' ";
        player += "codebase='http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701' ";
        player += "standby='Loading Microsoft¢ç Windows¢ç Media Player components...' type='application/x-oleobject'>";
        player += "<param name='url' value='" + audioUrl + "' />";
    }

    player += "<param name='autoStart' value='false' />";
    player += "<param name='playCount' value='1' />";
    player += "<param name='FileName' value='" + file_name + "' />";
    player += "<param name='ShowControls' value='0' />";
    player += "<param name='ShowStatusBar' value='false' />";
    player += "<param name='ShowDisplay' value='0' />";
    player += "<param name='Rate' value='1.0' />";
    player += "<param name='Volume' value='"+volume+"' />";
    player += "<param name='SendPlayStateChangeEvents' value='True' />";
    player += "<embed type='application/x-mplayer2' src='" + file_name + "' ShowControls='0' ShowStatusBar='false' ShowDisplay='0' autostart='false' Rate='1.0' Volume='0' ></<embed>";
 //   player += "<embed type='video/x-ms-wmp' src='" + file_name + "' ShowControls='0' ShowStatusBar='false' ShowDisplay='0' autostart='false' Rate='1.0' Volume='0' ></<embed>";
    player += "</object>";
    holder.innerHTML = player;

    audio = document.getElementById(objId);

    wmp.init();
    //wmp.progBar(); AIG파형제거
}

function whilePlaying(){ //흘러가는 동안 duration 표기하기 위함.
	if(audio != null){
		document.querySelector('.track-end-time').textContent = formatseconds(audio.duration);
		document.querySelector('.track-start-time').textContent = formatseconds(audio.CurrentPosition);
	}
}

function autoPlaying(){

	if(audio.duration != undefined && audio.duration > 0){ //audio.CurrentPosition != undefined &&
		//setTimeout(function(){
			wmp.play();
		//},200);
		clearInterval(autoPlay);
	}
}

var wmp = {
		init: function() {
			var that = this;
			
			document.getElementById('volume-slider').onchange=function(){ 
				that.setVolume(this.value); 
				document.cookie = "volume="+this.value;
			};
			
			document.querySelector('.speed-num').onclick=function(){ 
				that.setPlaybackRate('1');
				document.querySelector('.speed-num').textContent = 1;
			};
			
			//document.getElementById('speed-slider').onchange=function(){ that.setPlaybackRate(this.value);updateSpeed(this.value); };

			audio.playState = 1;
			if(audio != null){ //해당파일없는경우 계속 interval 하는거 방지 !!!
				autoPlay = setInterval(function(){autoPlaying();},1000);
			}
			window.setInterval("whilePlaying()",500);
		},

		loadAudio: function(host, rec_key, start_time,file_path,play_reason,play_sayu){
			var that = this;
			document.getElementById("loading").style.display = "";

			//var paramStr = "host="+host+"&rec_key="+rec_key+"&startTime="+start_time+"&play_reason="+play_reason+"&play_sayu="+play_sayu;
			//var paramStr = "&rec_key="+rec_key+"&startTime="+start_time+"&play_reason="+play_reason+"&play_sayu="+play_sayu;
			//alert("loadAudio:"+host+"PLY_IE/downloadFile_popup.jsp?"+paramStr);
			//window.open(host+"PLY_IE/downloadFile_popup.jsp?"+paramStr, "V-SENS PLAYER", "width=550, height=370, left=0, top=0, location=no, resizable=no, menubar=no, status=no");
			//window.open(host+"REC/REC010P4.xhtml?"+paramStr, "V-SENS PLAYER", "width=550, height=370, left=0, top=0, location=no, resizable=no, menubar=no, status=no");
			var paramStr = "";

		paramStr = "&contactId="+rec_key+"&file_path="+encodeURIComponent(encodeURIComponent(file_path));
			try{
				var xhr = new XMLHttpRequest();
				//xhr.open("GET" , host + "PLY_IE/downloadFile_popup.jsp?rec_key="+rec_key+"&startTime="+start_time, true);
			    xhr.open("GET" ,host + "player.downloadFile.do?cmd=CHECK"+paramStr, true); //java에서 decode하기위해 두번감쌈
			    xhr.onreadystatechange = function() {

			        if(xhr.readyState == 4 && xhr.status == 200) {

			        	document.getElementById("loading").style.display = "none";

			        	var result = JSON.parse(xhr.responseText);
			        	if(result.RESULT=="success"){
			        		var wavedata = new Array();
//			        		if(result.WAVEDATA != null){ _aig 파형제거
//			        			var wavedata_temp = result.WAVEDATA.split(",");
//
//				        		for(var i=0; i<wavedata_temp.length; i++){
//				        			wavedata[i]=Number(wavedata_temp[i]);
//				        		}
//			        		}
			        		
			        		
			        		//console.log(result);
			        		if(result.FILE_PATH != ''){
			        			meta.file_path = result.FILE_PATH;
			        			file_name = result.FILE_PATH.substring(result.FILE_PATH.lastIndexOf("/")+1);
			    				file_name = host+path+file_name;
			    				
			    				//console.log(meta.file_path);
			        		}
			        		createWaveform();

			        	} else if(result.RESULT=="failed"){
			        		//failed message
			        		alert("FAILE:"+result.REASON);
			        	}else if(result.RESULT=="download"){
			        		//failed message
			        		window.open( result.URL, "녹취청취사유", "width=550, height=370, left=0, top=0, location=no, resizable=no, menubar=no, status=no");
			        		//timer로 녹취파일다운확인 ??
			        		//getWavFile(host, rec_key, start_time);
			        	}

			        }
			    };

			    xhr.send();
				//createWaveform();
				
			}catch(e){
				alert("Exception:: "+e);
			}

		},

		getAudio: function() {
			return audio;
		},

		play: function() {
			var that = this;
			if (audio === null) return;

			that.setPlaybackRate(document.querySelector('.speed-num').textContent);

			if (audio.playState === 0 || audio.playState === 1 || audio.playState === 2 ) {
				 audio.play();
			     audio.classList.add('is-playing');
			     audio.playState = 3;
			     stt.selectCurrentWord();

			     if(rtx=="Y"){
			    	 stt.text_element_tx.classList.add('speaking_t');
				     stt.text_element_rx.classList.add('speaking_r');
			     }

			     var time = Math.ceil(this.getDuration());
			     si = setInterval(function(){
             		 if (that.progBar()){
             			 clearInterval(si);
             			 audio.stop();
	           			 audio.classList.remove('is-playing');
	           			 audio.playState = 1;
             		 }
   	    		}, 100);
			 } else if (audio.playState === 3) {
			     audio.pause();

			     audio.classList.remove('is-playing');
			     audio.playState = 2;

			     stt.selectCurrentWord();
			     stt.text_element.classList.remove('speaking');
			     if(rtx=="Y"){
			    	 stt.text_element_tx.classList.remove('speaking');
				     stt.text_element_rx.classList.remove('speaking');
			     }


			     clearInterval(si);
			  }

		},

		pause: function() {
			if (audio == null) return;
			if (audio.playState === 3){
				 audio.pause();

  			     audio.classList.remove('is-playing');
  			     audio.playState = 2;

  			     stt.selectCurrentWord();
  			     stt.text_element.classList.remove('speaking');
  			     if(rtx=="Y"){
  			    	 stt.text_element_tx.classList.remove('speaking');
  				     stt.text_element_rx.classList.remove('speaking');
  			     }

  			     clearInterval(si);
  			  }
		},

		stop: function() {
			 if (audio == null) return;
			 if (audio.playState === 3 || audio.playState == 2){
				 audio.stop();
				 audio.classList.remove('is-playing');
				 audio.playState = 1;
				 this.setInitPosition();
			 }
		},

		setInitPosition: function () {
			 stt.removeWordSelection();

	    	 document.querySelector('.track-start-time').textContent = formatseconds(0);

			 createFlag = false;

			 clearInterval(si);

			 $("#"+barId).remove();
//			 $("#"+playedId).remove();
	    },

		isPlaying: function () {
	    	return audio.classList.contains('is-playing');
	    },

		getDuration: function () {
	        return audio.duration;
	    },

	    getCurrentTime: function () {
	        return audio.CurrentPosition;
	    },

	    getPlayBackRate: function() {
	    	return audio.Rate;
	    },
	    
	    getVolume : function(){
	    	return audio.Volume;
	    },

	    setVolume: function(vol){
	    	audio.Volume = vol;
	    },

	    setPlaybackRate: function(rate){
	    	audio.Rate = rate;
	    },

//		updateSpeed : function(sv){
//			document.getElementById('speedValue').innerHTML=sv;
//		},

	    empty: function(){
	    	if (audio != null){
	    		this.stop();
//	    		audio.remove();
	    		audio=null;
//	    		document.getElementById('MediaPlayer').innerHTML="";
	    	}
//	    	$('#sub-container').remove();
//			$('#if-sub').remove();
//	    	$('#passage-text').remove();
//			document.getElementById('search').style.top=close_src_top;
			document.getElementById('waveform').querySelectorAll('canvas').remove();
	    },

	    seekTo: function (pos) {
	    	if(audio != undefined){
		    	if(this.isPlaying()){
		    		audio.pause();
					audio.CurrentPosition = pos;
					this.setPlaybackRate(document.querySelector('.speed-num').textContent);
					audio.play();
		    	}else{
		    		audio.CurrentPosition = pos;
		    		this.progBar();
		    	}
	    	}

	    	stt.removeWordSelection();
	    	stt.selectCurrentWord();

	    },

        progBar: function() {

        	var wgWidth = document.getElementById('waveform').offsetWidth;
        	var soFar = parseFloat(this.getCurrentTime() / this.getDuration()).toFixed(3);
        	var left_position = wgWidth * soFar;

        	//if(soFar == 0.000){
        	if(!createFlag){
        		var playingBar = document.createElement('div');
        		playingBar.id = barId;
        		playingBar.style.cssText = 'position:absolute;z-index:1;top:0px;left:'+left_position+'px;width:3px;height:50px;background-color:#D14A0E;border:2px;';
            	document.getElementById('waveform').appendChild(playingBar);

//            	var playedArea = document.createElement('div');
//            	playedArea.id = playedId;
//            	playedArea.style.cssText = 'position:absolute;z-index:2;top:0px;left:0px;width:0px;height:50px;background-color:#252E39;opacity: .00;';
//            	document.getElementById('waveform').appendChild(playedArea);

            	createFlag = true;
        	}else{
//        		document.getElementById(barId).style.left = left_position+"px";
//        		document.getElementById(playedId).style.width = left_position+"px";

        		document.getElementById(barId).style.cssText = 'position:absolute;z-index:1;top:0px;left:'+left_position+'px;width:3px;height:50px;background-color:#D14A0E;border:2px;';
//        		document.getElementById(playedId).style.cssText = 'position:absolute;z-index:2;top:0px;left:0px;width:'+left_position+'px;height:50px;background-color:#252E39;opacity: .80;';
        	}

        	if(left_position == wgWidth) return true; // clearInterval
       		else return false;
        }


};




/*********************************************************************************************
 * UTIL
 *********************************************************************************************/
function formatseconds(seconds) {
//	   var hours = Math.floor(seconds / 3600000);
//	   seconds = seconds % 3600000;
//	   var minutes = Math.floor(seconds / 60000);
//	   seconds = seconds % 60000;
//	   var seconds = Math.floor(seconds / 1000);
	  // milliseconds = Math.floor(milliseconds % 1000);

		var hours = Math.floor(seconds / 3600);
	    seconds = seconds % 3600;
	    var minutes = Math.floor(seconds / 60);
	    seconds = seconds % 60;
	    var seconds = Math.floor(seconds);

	    return (hours > 0 ? hours : '0') + ':' +
	           (minutes < 10 ? '0' : '') + minutes + ':' +
	           (seconds < 10 ? '0' : '') + seconds;
}


function pos(time, width){
	return Math.round((time/audio.durationEstimate)*width);
}
