<!--
// -----------------------------------------------------------------------------
// Globals
// Major version of Flash required
var requiredMajorVersion = 10;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 46;

rMateMapChartJsReady = false;

function rMateMapChartIsReady() {
	return rMateMapChartJsReady;
}

function rMateMapChartInit() {
	rMateMapChartJsReady = true;
}

function rMateMapChartOnLoadInit() {
	try {
		rMateMapChartOnLoad();
	} catch(e) {
   		;
	}
}
function getRMateMapChartLicense() {
	try {
		return rMateMapChartLicense;
	} catch(e) {
		alert("rMate MapChart license is required.");
	}
}
// -----------------------------------------------------------------------------
// -->


function rMateMapChartCreate(id, fileName, flashVars, w, h, bgColor, target){

	// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
	var hasProductInstall = DetectFlashVer(6, 0, 65);

	// Version check based upon the values defined in globals
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

	if ( hasProductInstall && !hasRequestedVersion ) {
		// DO NOT MODIFY THE FOLLOWING FOUR LINES
		// Location visited after installation is complete if installation is required
		var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
		var MMredirectURL = window.location;
		document.title = document.title.slice(0, 47) + " - Flash Player Installation";
		var MMdoctitle = document.title;

		var pathName = fileName.split("/");
		var playerProductInstall = "";

		for (var i = 0; i < pathName.length-1; ++i) {
			playerProductInstall += pathName[i] + "/";
		}
		playerProductInstall += "playerProductInstall";

		return AC_FL_RunContent(
			"src", playerProductInstall,
			"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
			"width", w,
			"height", h,
			"align", "middle",
			"id", id,
			"wmode", "window",
			"quality", "high",
			"bgcolor", bgColor,
			"name", fileName,
			"allowScriptAccess","sameDomain",
			"type", "application/x-shockwave-flash",
			"pluginspage", "http://www.adobe.com/go/getflashplayer",
			"target",target
		);
	} else if (hasRequestedVersion) {
		// if we've detected an acceptable version
		// embed the Flash Content SWF when all tests are passed
		return AC_FL_RunContent(
				"src", fileName,
				"flashVars", flashVars,
				"width", w,
				"height", h,
				"align", "middle",
				"id", id,
				"wmode", "window",
				"quality", "high",
				"bgcolor", bgColor,
				"name", fileName,
				"allowScriptAccess","sameDomain",
				"type", "application/x-shockwave-flash",
				"pluginspage", "http://www.adobe.com/go/getflashplayer",
				"target",target
		);
	  } else {  // flash is too old or we can't detect the plugin
		var alternateContent = 'Alternate HTML content should be placed here. '
		+ 'This content requires the Adobe Flash Player. '
		+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
		document.write(alternateContent);  // insert non-flash content
	  }
}