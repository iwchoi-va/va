/**
 * Captions processing initialization
 */
var vid = document.getElementById("waveform");
var tt_number = 0;
//var replicas = vid.textTracks[tt_number].cues;
var replicasStartTime = new Array();
var replicasEnd;
var repNumber = 0;

//var config_proto = JSON.parse(json_config);

/**
 * Occurs when the video has been loaded
 */
window.onload = function(){
	//synchronizeTextVideo();
	//synchronizeVideoText();
	/*var html="";	
	html = "<div id='rep' onclick='goTo('')'>안녕하세요</div>";
	document.getElementById("captions").innerHTML = html;*/
};

/**
 * Occurs when the playing position of the video has changed
 */
vid.ontimeupdate = function(){
	updateRepNumber();
	synchronizeVideoText();
};

/**
 * Function to go to a specific time in the video
 * @param {number} seconds - Seconds
 * @param {number} i - TODO: SEE IF STILL USEFUL
 */
function goTo(seconds, i){
	vid.currentTime = seconds;
	updateRepNumber();
	synchronizeVideoText();
}

/**
 * Function to pop-up the current time
 */
function giveTime(){
	alert(vid.currentTime);
}

/**
 * Function to run the synchronization
 */
function onLoadFunction(){
//	synchronizeTextVideo();
	//synchronizeVideoText();
}

/**
 * Function to synchronize the text to the video
 */
function synchronizeTextVideo(){
/*
	replicas = vid.textTracks[0].cues;
	replicasEnd = replicas[replicas.length-1].endTime;
	html = '';
	for(i=0; i<replicas.length; i++){
		replicasStartTime[i] = replicas[i].startTime;
		html = html + '<div id="rep-' + i + '" onclick="goTo(' + replicas[i].startTime + ', ' + i + ')">' + replicas[i].text + '</div>';
	}
	document.getElementById("captions").innerHTML = html;
	*/
}

/**
 * Function to remove the classes from the replicas
 */
function removeClasses(){
	for(i=0; i<replicasStartTime.length; i++){
		document.getElementById("rep-" + i).className = '';
	}
}

/**
 * Function to synchronize the video to the text
 */
function synchronizeVideoText(){
	if(repNumber < replicasStartTime.length){
		if(vid.currentTime >= replicasStartTime[repNumber]){
			removeClasses();
			document.getElementById("rep-" + repNumber).className = 'highlighted';
			repNumber = repNumber + 1;
		}
	}
}

/**
 * Function to update the current replicas number
 */
function updateRepNumber(){
	removeClasses();
	for(i=0; i<replicasStartTime.length; i++){
		if(vid.currentTime >= replicasStartTime[i] && i+1 >= replicasStartTime.length && vid.currentTime <= replicasEnd){
			repNumber = i;
			break;
		}
		if(vid.currentTime >= replicasStartTime[i] && vid.currentTime < replicasStartTime[i+1]){
			repNumber = i;
			break;
		}
	}
}