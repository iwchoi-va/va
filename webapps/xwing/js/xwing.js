var xwing = {
 	Xwing : {
 		version : '1.0.8',
 		config : {
 			isDesignMode : false,
			isDebug : true,
			theme : "silver"	
 		},
 		XWING_HOME : '',
		_requiredLib : [
            "/lib/lib.core-all-min.js"
		],
		_requiredJs : [ 
			"/js/class.js",
			"/js/xwing.util.js",
			"/js/xwing.core.js",
			"/js/xwing.dataset.js", "/js/xwing.dataset.model.js",
			"/js/xwing.ajax.js",
			"/js/xwing.dragdrop.js",
			"/js/xwing.widget.js", "/js/xwing.widget.model.js",
			"/js/xwing.widget.panel.js", "/js/xwing.widget.panel.model.js",
			"/js/xwing.widget.page.js", "/js/xwing.widget.page.model.js",
			"/js/xwing.widget.databindable.js", "/js/xwing.widget.databindable.model.js",
			"/js/xwing.widget.html.js",
			"/js/xwing.widget.text.js",
			"/js/xwing.widget.button.js", "/js/xwing.widget.button.model.js",
			"/js/xwing.widget.edit.js", "/js/xwing.widget.edit.model.js",
			"/js/xwing.widget.tab.js", "/js/xwing.widget.tab.model.js",
			"/js/xwing.widget.tab-page.js", "/js/xwing.widget.tab-page.model.js",
			"/js/xwing.widget.combo.js", "/js/xwing.widget.combo.model.js",
			"/js/xwing.widget.list.js", "/js/xwing.widget.list.model.js",
			"/js/xwing.widget.tree.js", "/js/xwing.widget.tree.model.js",
			"/js/xwing.widget.group.js", "/js/xwing.widget.group.model.js",
			"/js/xwing.widget.checkbox.js", "/js/xwing.widget.checkbox.model.js",
			"/js/xwing.widget.radio.js", "/js/xwing.widget.radio.model.js",
			"/js/xwing.widget.richtext.js", "/js/xwing.widget.richtext.model.js",
			"/js/xwing.widget.label.js", "/js/xwing.widget.label.model.js",
			"/js/xwing.widget.datepicker.js" ,"/js/xwing.widget.datepicker.model.js",				                
			"/js/xwing.widget.grid.js" ,"/js/xwing.widget.grid.model.js",		                		                
			"/js/xwing.widget.grid-column.js" ,"/js/xwing.widget.grid-column.model.js",
			"/js/xwing.dialog.js", "/js/xwing.dialog.model.js",		      
			"/js/xwing.widget.textarea.js", "/js/xwing.widget.textarea.model.js",
			"/js/xwing.notify.js",
			"/js/xwing.menu.contextmenu.js",
			"/js/xwing.widget.menu.js", "/js/xwing.widget.menu.model.js",
			"/js/xwing.widget.file.js", "/js/xwing.widget.file.model.js",
			"/js/xwing.widget.embed.js", "/js/xwing.widget.embed.model.js",
			"/js/xwing.widget.chart.js", "/js/xwing.widget.chart.model.js",
			"/js/xwing.widget.progressbar.js", "/js/xwing.widget.progressbar.model.js",
			"/js/xwing.widget.spin.js", "/js/xwing.widget.spin.model.js",
			"/js/xwing.widget.splitbar.js", "/js/xwing.widget.splitbar.model.js",
			"/js/xwing.widget.slider.js", "/js/xwing.widget.slider.model.js",
			"/js/xwing.widget.datagrid.js", "/js/xwing.widget.datagrid.model.js",
			"/js/xwing.widget.datagrid-column.js" ,"/js/xwing.widget.datagrid-column.model.js",
			"/js/xwing.widget.datagrid-row.js" ,"/js/xwing.widget.datagrid-row.model.js",
			"/js/xwing.widget.datagrid-cell.js" ,"/js/xwing.widget.datagrid-cell.model.js",
			"/js/xwing.widget.datagrid-group.js"
		],
		_requiredCss : [ 
            "/lib/jquery/css/redmond/jquery-ui-1.8.21.custom.css",
			"/lib/jquery/css/contextmenu/jquery.contextmenu.css"						
		],
 		isIE : function() {
 			return !!window.ActiveXObject;
		},
		_traceQue : [],
		_logQue : [],
		debug : function(msg) {
			if (xwing.Xwing.config.isDebug) {
				if (window.console && console.debug) {
					for ( var i = 0, l = xwing.Xwing._logQue.length; i < l; i++)
						console.debug(xwing.Xwing._logQue[i]);
					xwing.Xwing._logQue = [];
					//console.debug(msg);
				} else {
					xwing.Xwing._logQue.length > 200 && xwing.Xwing._logQue.pop();
					xwing.Xwing._logQue.push(msg);
				}
			}			
			if (Xwing.config.isDesignMode && xwingIDE && xwingIDE.debug) {
				try { xwingIDE.debug(msg); } catch (e) { alert(e); }
			}
		},
		log : function(msg){
			if (xwing.Xwing.config.isDebug) {
				if (window.console && console.log) {
					for ( var i = 0, l = xwing.Xwing._traceQue.length; i < l; i++) {
						console.log(xwing.Xwing._traceQue[i]);
						this._trace(xwing.Xwing._traceQue[i]);
					}
					xwing.Xwing._traceQue = [];
					console.log(msg);
					this._trace(msg);
				} else {
					xwing.Xwing._traceQue.length > 200 && xwing.Xwing._traceQue.pop();
					xwing.Xwing._traceQue.push(msg);
				}
			}
			if (Xwing.config.isDesignMode && xwingIDE && xwingIDE.info) {
				xwingIDE.info(msg);
			}
		},
		_trace : function(log) {
			if (window.top.isTraceMode) {
				try {
					var cmd = {
						type : 'TRACE',
						msg : log
					};
					var evt = document.createEvent("CommandEvent");
					evt.initCommandEvent("xwing.logging", true, true, JSON.stringify(cmd));
					window.top.dispatchEvent(evt);
				} catch (e) {
					Xwing.debug(e);
				}
			} 
		},
		warn : function(msg){
			if (xwing.Xwing.config.isDebug) {
				if (window.console && console.warn) {
					for ( var i = 0, l = xwing.Xwing._logQue.length; i < l; i++) 
						console.debug(xwing.Xwing._logQue[i]);
					xwing.Xwing._logQue = [];
					console.warn(msg);					
				} else {
					xwing.Xwing._logQue.length > 200 && xwing.Xwing._logQue.pop();
					xwing.Xwing._logQue.push(msg);	
				}
			}
			if (Xwing.config.isDesignMode && xwingIDE && xwingIDE.warn) {
				xwingIDE.warn(msg);
			}
		},
		error : function(msg) {
			if (xwing.Xwing.config.isDebug) {
				try {
					for ( var i = 0, l = xwing.Xwing._logQue.length; i < l; i++)
						console.debug(xwing.Xwing._logQue[i]);
					xwing.Xwing._logQue = [];
					if (console.error) console.error(msg);
					else console.debug(msg);
				} catch(e) {
					xwing.Xwing._logQue.length > 200 && xwing.Xwing._logQue.pop();
					xwing.Xwing._logQue.push(msg);
					alert("[XwingErr] " + msg);
				}
			}
			if(Xwing.config.isDesignMode && xwingIDE && xwingIDE.error){
				xwingIDE.error(msg);
			}
		},		
		globalEval : function(src) {
			if (this.isIE()) {
				window.exec(src);
			} else {
				window.eval ? window.eval(src) : eval(src);
			}
		}
	}
};
 
var Xwing = xwing.Xwing;

Xwing._init = function(){
	Xwing.debug("Xwing "+ Xwing.version +" initilazing...");
	var jsPath = Xwing._getJsPath();
	if(jsPath){
		 Xwing.XWING_HOME = jsPath.replace(/js(\/|\\)$/, '');
		 Xwing.XWING_ROOT = Xwing.XWING_HOME.replace(/xwing\/$/,'');
		 Xwing.debug("Xwing.XWING_HOME : "+Xwing.XWING_HOME);
	}else{
		Xwing.warn("Xwing.XWING_HOME can't set.");		
	} 
	Xwing._loadConfig();
};

Xwing._loadConfig = function(){
	var hostname = window.location.hostname.split(".");
	var lbl="";
		if(hostname[0] == "vadev"){
			Xwing._loadJs("/config/xwing_dev.config", Xwing._loadedConfig);	
		}else if(hostname[0] == "vauat"){
			Xwing._loadJs("/config/xwing_uat.config", Xwing._loadedConfig);	
		}else if(hostname[0] == "va"){
			Xwing._loadJs("/config/xwing.config", Xwing._loadedConfig);	
		}else{
			Xwing._loadJs("/config/xwing_local.config", Xwing._loadedConfig);	
		}
};

Xwing._loadedConfig = function(){
//	window.xwingIDE && (Xwing.config.setup = 'compress');
	Xwing.debug(Xwing.config);	

	Xwing._loadCss();
	Xwing._loadLib();
};

Xwing._loadLib = function(){
	Xwing.debug("required library loading..(" + Xwing._requiredLib.length + ") ");
	
	var libs = Xwing._requiredLib.slice();
	var libloader = function(){
		if(libs.length){
			Xwing._loadJs(libs.shift(), libloader);
		}else{
			Xwing._loadedLib();
		}
	};
	libloader();
};

Xwing._loadContextLib = function() {
	Xwing.debug("context library loading ... ");
	
	jQuery.xmlns["xwing"] = "xwing.net";
	var libs = [];
	
	jQuery("xwing|chart").length && libs.push('/lib/vchart/lib.chart-all-min.js');
	!!('ontouchstart' in window) && libs.push('/lib/utils/iscroll.min.js');
	
	var libloader = function(){
		if (libs.length) {
			Xwing._loadJs(libs.shift(), libloader);
		} else {
			Xwing._start();		
		}
	};
	
	libloader();	
};

Xwing._loadedLib = function(){
	Xwing.debug("required library loaded..");	
	Xwing._loadXwing();
};

Xwing._loadXwing = function(){
	var setupMode = Xwing.config.setup || 'debug';
	if (setupMode == "debug") { 
		Xwing.debug("xwing js loading..("+Xwing._requiredJs.length+") " );
		var xwingjs = Xwing._requiredJs.slice();
		var xwingjsLoader = function(){
			if(xwingjs.length){
				Xwing._loadJs(xwingjs.shift(), xwingjsLoader);
			}else{
				Xwing._loadedXwing();
			}
		};
		xwingjsLoader();
	} else if(setupMode == "combine") {
		Xwing.debug("xwing js loading.. (xwing-all.js)" );
		Xwing._loadJs("/js/xwing-all.js", Xwing._loadedXwing);		
	} else if(setupMode == "compress") {
		Xwing.debug("xwing js loading..(xwing-min.js)" );
		Xwing._loadJs("/js/xwing-all-min.js", Xwing._loadedXwing);		
	}
};

Xwing._loadedXwing = function(){
	Xwing.debug("xwing js is loaded.");
	Xwing._postRequiredJs();
};

Xwing._loadJs = function(js, callback, args){
	if (document.location.protocol == "http:") {
		Xwing._loadJsByXHR(js + '?v' + Xwing.version, callback, args);			
	} else if (document.location.protocol == "https:") {
		Xwing._loadJsByDOMs(js, callback, args); 		
	} else {		
		Xwing._loadJsByDOM(js, callback, args);  
	}
};

Xwing._loadJsByDOMs = function(js, callback){
	var url = Xwing.XWING_HOME + js;
	var loaded = function(){
		Xwing.debug(url + " loaded.");
		callback();
	};
	var script = document.createElement("script");
	script.onerror = function(){
		Xwing.debug("error loading "+js);
	};
	var loadFlag = false;
	if(Xwing.isIE()){
		script.onreadystatechange =  function(){
			Xwing.debug(url + " onreadystatechange(" + script.readyState + ") on IE");
			if(script.readyState && (script.readyState =="loaded" || script.readyState == "complete")){
				if (loadFlag){
					return;
				}else{
	       			loaded();
				}
			}
		};
	}else{
		script.onload = function(){
			loaded();
		};
	}
	script.type = "text/javascript";
	script.charset="UTF-8";
	//SSSSS
	//script.src = url+"?"+((new Date()).getSeconds()).toString(16);
	script.src = "/msens/xwing"+js;
	var head = document.getElementsByTagName('head')[0];
	if (head) {
		head.appendChild(script);
	}else{
		document.body.appendChild(script);
	}
};

Xwing._filterScript = function(js, source) {
	var setupMode = Xwing.config.setup || 'debug';
	if (setupMode != "compress" && window.JSDEV) {
		js = js.split('?')[0];
		if ((Xwing._requiredJs.indexOf(js) != -1) || (js == "/js/xwing-all.js")) {
			try {
				var rules = [ "debug", "startwatch:Xwing._startWatch", "stopwatch:Xwing._stopWatch" ];
				source = JSDEV(source, rules, [ "Setup Debug Mode." ]);
			} catch (e) {
				Xwing.error("[Debug filter]" + e);
			}
		}
	}
	return source;
};

Xwing._loadJsByXHR = function(js, callback){
	var url = Xwing.XWING_HOME + js;
	var xmlhttp = null;
	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();  
	} else if (window.ActiveXObject) { 
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");   
		if(xmlhttp == null)	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	if (!xmlhttp) throw new Error("Xwing error : XHR Not Suppoted.");   

	xmlhttp.open('GET', url, false); 
	xmlhttp.setRequestHeader("Accept-Language","ko");
	xmlhttp.setRequestHeader("Content-Type", "text/plain;charset=UTF-8");
	xmlhttp.send(null);
	
	var source = xmlhttp.responseText;
	
	if(xmlhttp.status == 200 || xmlhttp.status == 304){
		if ( ( source != null ) && ( !document.getElementById( js ) ) ){  
			var oHead = document.getElementsByTagName('head').item(0); 
			var oScript = document.createElement( "script" ); 
			oScript.language = "javascript"; 
			oScript.type = "text/javascript"; 
			oScript.charset = "UTF-8";
			oScript.id = js; 
			oScript.text = Xwing._filterScript(js, source);			
			oHead.appendChild( oScript );
		}
		Xwing.debug("load success : "+url );		
	}else{
		Xwing.debug("load fail : " +url );
	}
	callback();
};

Xwing._loadJsByDOM = function(js, callback){
	var url = Xwing.XWING_HOME + js;
	var loaded = function(){
		Xwing.debug(url + " loaded.");
		callback();
	};
	var script = document.createElement("script");
	script.onerror = function(){
		Xwing.debug("error loading "+js);
	};
	var loadFlag = false;
	if(Xwing.isIE()){
		script.onreadystatechange =  function(){
			Xwing.debug(url + " onreadystatechange(" + script.readyState + ") on IE");
			if(script.readyState && (script.readyState =="loaded" || script.readyState == "complete")){
				if (loadFlag){
					return;
				}else{
	       			loaded();
				}
			}
		};
	}else{
		script.onload = function(){
			loaded();
		};
	}
	script.type = "text/javascript";
	script.charset="UTF-8";
	//SSSSS
	script.src = url+"?"+((new Date()).getSeconds()).toString(16);
	//script.src = "/xwing"+js;
	var head = document.getElementsByTagName('head')[0];
	if (head) {
		head.appendChild(script);
	}else{
		document.body.appendChild(script);
	}
};

Xwing._getJsPath = function(){
	var allScripts = document.getElementsByTagName("script");
	var path = '';
	for(var i=0; i < allScripts.length; i++){
		var curr = allScripts[i];
		if(curr.src && curr.src.match(/xwing\.js(\?.*)?$/)){
			path = curr.src.replace(/xwing\.js(\?.*)?$/,'');
			break;
		}
	}
	return path;
};

Xwing._postRequiredJs = function(){
	Xwing.debug("start xwing runtime ...");
	
	Xwing._loadCreator();
	Xwing._loadMasks();
	
	if (!window.xwingIDE) {
		try {
			jQuery(Xwing._loadContextLib);
		} catch (e) {
			Xwing.error(e);
		}
	}
};

Xwing._loadCss = function(){
	var setupMode = Xwing.config.setup || 'debug';
	var requiredCss = new Array();

	if (setupMode == "debug")
		Xwing._requiredCss.push("/css/themes/" + xwing.Xwing.config.theme + "/xwing.css");
	else
		Xwing._requiredCss.push("/css/themes/" + xwing.Xwing.config.theme + "/xwing-all.css");

	for(var i in Xwing._requiredCss){
		// SSSSS
		//requiredCss[i] = Xwing._getJsPath() + "../" + Xwing._requiredCss[i];
		requiredCss[i] = Xwing._getJsPath().replace('/js/', Xwing._requiredCss[i]);
	}

	Xwing._importCss(requiredCss);	
};

Xwing._importCss = function(src){
	var load = function(href){
		var css = document.createElement('link');
		css.setAttribute('rel', 'stylesheet');
		css.setAttribute('type', 'text/css');
		css.setAttribute('media', 'screen');
		css.setAttribute('href', href + '?v' + Xwing.version);
		css.setAttribute('charset', 'UTF-8');
		var head = document.getElementsByTagName('head')[0];
		if (head) { 
			head.appendChild(css);
		} else {
			document.body.appendChild(css);
		}
	};
	if(src instanceof Array && src.length){
		while(src.length) load(src.pop());
	}else{
		load(src);
	}
};

Xwing._init();
