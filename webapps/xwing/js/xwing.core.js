xwing.widget = {};
/**
 * DataSet Registry
 */
Xwing._datasetList = new xwing.util.Hash();
/**
 * Widgt Registry
 */
Xwing._widgetList = new xwing.util.Hash();

Xwing.parent;
Xwing.children = {};
Xwing.param = {};

Xwing.go = function(url, option){
	if(arguments.length == 1 || option == true || option == 'true')
		window.location.href = url;
	else
		window.location.replace(url);
};

Xwing.reload = function(optionalArg){
	window.location.reload(optionalArg);
};

Xwing.setCustomStyle = function(key){
	Xwing.config.custom = key;
	
	for(var key in Xwing.getWidgetList()._item){
		if( Xwing.getWidgetList()._item[key].getAlias() != 'html' && Xwing.getWidgetList()._item[key]._doStyles){
			Xwing.getWidgetList()._item[key]._doStyles();
		}
	}
};

var _pageLoad = null;
var _pageObj = null;

Xwing._start = function(){ 
	/*debug Xwing.debug("xwing starting.."); */
	/*startwatch 'Xwing._start' */
	
	if (Xwing.onload) {
		try {
			/*debug Xwing.debug("execute xwing.onload"); */ 
			Xwing.onload();
		} catch (e) {
			/*debug Xwing.error("error on Xwing.onload:" + e); */
		}
	}
	
	jQuery.xmlns["xwing"] = "xwing.net";
	Xwing.dom = {};
	jQuery("body").css('overflow','hidden');
	Xwing.dom.body = jQuery("body").remove()[0];
	
	if(window.xwingIDE && xwingIDE.initListener){
		xwingIDE.initListener(Xwing.dom.body);
	}
	
	if(!Xwing.dom.body) return;
	
	if(Xwing.config.isDebug){
		Xwing._addDebugger(); 
	}

	if(Xwing.dom.data = jQuery("xwing|data")[0]){
		jQuery("xwing|data").remove();
		jQuery("xwing|dataset", Xwing.dom.data).each(Xwing._loadDataset);
	}
	
	setTimeout(function() {
		Xwing._loadChildElements(Xwing.dom.body);
		
		if(window.parent != window){
			var name = window.frameElement.id;
			var p_id = name.split("::")[1];
			
			window.parent.Xwing && (window.parent.Xwing.children[p_id] = window);
			Xwing.parent = window.parent;
			
			if(!(window.parent._param == undefined || window.parent._param[p_id] == undefined)) 
				Xwing.param = window.parent._param[p_id];
			
			if(!window.xwingIDE && window.top.isTraceMode && Xwing.config.isDebug) {
				jQuery("<img/>").attr({
					src : Xwing.XWING_HOME + 'css/images/refresh.png',
					title : 'Refresh'
				}).addClass("xw-trace-refresh").click(function() {
					Xwing.reload();
				}).appendTo('body');
			}			
		}
		
		/*stopwatch 'Xwing._start' */
		/*debug
			var map = Xwing._getWatchMap();
			Xwing.debug("> StopWatch Overall Result");	

			for(var key in map) {
				var stopwatch = map[key];
				var events = stopwatch.events;
				
				Xwing.debug(">> id = " + key + ", count = " + stopwatch.count + ", avg = " + Math.round(stopwatch.total/stopwatch.count) + " m/s");
				
				for (var i = 0 ; i < events.length ; i++) {
					Xwing.debug(">>> [" + i + "].duration = " + events[i].duration + " m/s");
				}
				
				xwing.util.removeStopWatch(key);
			}		
		*/

		if (Xwing.config.isDesignMode && xwingIDE.loadCompleted) {
			xwingIDE.loadCompleted();
		}
		
		if (_pageLoad) {
			try {
				var funcObj = _pageLoad.func;
				if (typeof (funcObj) != "function") {
					funcObj = eval(funcObj);
				}
				var event = {
					type : 'load',
					source : _pageObj
				};
				if (_pageLoad.obj) {
					funcObj.call(_pageLoad.obj, event);
				} else {
					funcObj.call(null, event);
				}	
			} catch (e) {
				/*debug Xwing.debug("event fire call error : widget(page) event type:load,"+funcObj.func + "\n"+ e); */	
				return false;
			}
		}
	}, 0);
};

/**
 * Load child element TOP-DOWN recursively
 * @param mode use only appendChild
 */
Xwing._loadChildElements = function(node, mode){
	var xObj = null;

	if (jQuery(node).filter("xwing|*").size()) {
		var opt = xwing.widget.Widget._parseXJson(node);
		var nodeName = node.nodeName;
		opt.xw_type = nodeName.replace(/^xwing:/i, "");
		opt.ns = "xwing";
		xObj = Xwing._createElement(opt, node);
	} else {
		xObj = Xwing._cloneNode(node);
	}

	if (xObj) {
		if (node.nodeName != "#text")
			node._xnode = xObj;

		var parent = node.parentNode;
		if (parent && parent._xnode) {
			try {
				var nth = -1;
				var total = 0;
				if (mode && parent.childNodes.length > 1) {
					for ( var i = 0, l = parent.childNodes.length; i < l; i++) {
						if (parent.childNodes[i].nodeType == 1) {
							total++;
							if (parent.childNodes[i] === node) {
								nth = total;
							}
						}
					}
					--nth;
				}
				if (nth + 1 < total) {
					try {
						parent._xnode.insertChild(xObj, nth);
					} catch (e) {
						Xwing.error("error on insertChild():" + e);
					}
				} else {
					try {
						parent._xnode.appendChild(xObj);
					} catch (e) {
						Xwing.error("error on appendChild():" + e);
					}
				}

				xObj.setParentWidget(parent._xnode);
			} catch (e) {
				Xwing.error("error on xwing.core._loadChildElemtns():" + e);
			}
		} else {
			jQuery(document.documentElement).append(node._xnode.getShell());
		}

		if (node.hasChildNodes()) {
			for ( var i = 0, l = node.childNodes.length; i < l; i++) {
				var child = node.childNodes[i];
				Xwing._loadChildElements(child);
			}
		}

		xObj._appendCompleted();
		(xObj._doDraggable && node.nodeName != "#text" )&& xObj._doDraggable();
		(xObj._doDroppable && node.nodeName != "#text" )&& xObj._doDroppable();
		
		if (!window.xwingIDE && "#text,html".indexOf(xObj.getAlias()) == -1) {
			xObj._doAnchor();
			if (xObj.getAlias() == "page") {
				var _frame = window.frameElement ? jQuery(window.frameElement) : null;
				jQuery(window).resize(function(e) {
					var w = document.documentElement.clientWidth;
					var h = document.documentElement.clientHeight;
					if (_frame && Xwing.isIE()) {
						w = _frame.width();
						h = _frame.height();
					}
					xObj.setWidth(w);
					xObj.setHeight(h);
					xObj.fireAnchorResizeEvent(w, h);
				});
				jQuery(window).trigger('resize');
			}
		}

		if (xObj.getAlias() == "page") {
			for ( var i = 0, l = xObj._listeners.length; i < l; i++) {
				if ("load" == xObj._listeners[i].type) {
					_pageLoad = xObj._listeners[i];
					_pageObj = xObj;
				}
			}
		}
	}
	return xObj;
};

Xwing._cloneNode = function(node){
	if (node && node.nodeType && node.nodeName) {
		try {
			if (node.nodeType == 1) {
				var clazz = Class.getClass("html");
				if (!jQuery(node).attr("id")) {
					var id = clazz.getNextId();
					jQuery(node).attr("id", id);
				}
			} else if (node.nodeType == 3) {
				var clazz = Class.getClass("#text");
			} else {
				return null;
			}
			var obj = new clazz(node);
			return obj;
		} catch (e) {
			Xwing.error("error on creating Xwing Object :[" + node.nodeName + "] " + e);
		}
	}
};

Xwing._createElement = function(opt, node){
	if (opt && opt.xw_type) {
		if (opt.ns == "xwing") {
			return Xwing._createWidget(opt, node);
		} else {
			if (opt.xw_type == "#text")
				var clazz = Class.getClass(opt.xw_type);
			if (!clazz)
				clazz = xwing.widget.HTML;
		}
		if (opt.id == null || opt.id == "undefined" || opt.id == "")
			opt.id = clazz.getNextId();
		return new clazz(opt);
	}
};

Xwing._createWidget = function(opt, node){
	var clazz = Class.getClass(opt.xw_type);
	if (opt.id == null || opt.id == "undefined" || opt.id == "")
		opt.id = clazz.getNextId();
	return new clazz(opt, node);
};

Xwing._loadDataset = function(idx){
	var xwingType = this.tagName.replace(/^xwing:/i, "");
	if(/^data$/i.test(xwingType)) return true;
	if(/-/.test(xwingType)) return true;

	Xwing._parseNode(this);
};

Xwing._parseNode = function(xwingNode){
	var xType = xwingNode.tagName.replace(/^xwing:/i, "");
	try{
		Class.getClass(xType).parse(xwingNode);
	}catch(e){
		Xwing.error("xwing type :"+ xType + " is nuknown type." + e);
		Xwing.error("Available types are : ");
		for(var alias in Class._map){
			Xwing.error(alias);
		}
	}
};

Xwing._addDataset = function(dsId, dsObj){
	Xwing._datasetList.set(dsId, dsObj);
};

Xwing.hasDataset = function(dsId) {
	return Xwing._datasetList.hasKey(dsId);
};

Xwing.getDatasetList =function(){
	return Xwing._datasetList;
};

Xwing.getDataset = function(dsId){
	return Xwing._datasetList.get(dsId);
};

Xwing.getWidgetList = function(){
	return Xwing._widgetList;
};

Xwing.getWidget = function(widgetId){
	return Xwing._widgetList.get(widgetId);
};

Xwing.addWidget = function(wdgId, wdgObj){
	Xwing._widgetList.set(wdgId, wdgObj);
};

Xwing.removeWidget = function(wdgId){
	Xwing._widgetList.remove(wdgId);
};

Xwing.hasWidget = function(wdgId){
	return Xwing._widgetList.hasKey(wdgId);
};

Xwing.request = function(opt, callback){
	/*debug
	 var opts = jQuery.extend({}, opt || {}); 
	 opts['indicator'] = null;
	 Xwing.debug("Xwing.request " + xwing.util.Util.obj2json(opts)); 
	 */	

	if (opt.indicator === true) {
		xwing.widget.Widget.showIndicator(opt.message);
	} else if (opt.indicator !== false) {
		xwing.widget.Widget.showIndicator(opt.message, opt.indicator);
	}
	
	var url = Xwing._getURL(opt.url),
		timeout = Xwing._getTimeout(opt.url),
		c = (typeof callback == 'function') ? callback : eval(callback),
		dataset = [],
		data = {};
	
	for (var i in opt.param) {
		if (opt.param[i] instanceof xwing.Dataset) {
			data[i] = opt.param[i].getDataString();
			dataset.push(i);
		} else {
			data[i] = opt.param[i];
		}
	}
	
	dataset.length && (data['_dataset'] = dataset.join(','));
	data = jQuery.param(data);
	
	/*
	var data = "";
	var dataset = "";
	for(var i in opt.param){
		if(opt.param[i] instanceof xwing.Dataset){
			data += (data == "" ? i : "&"+i) + "=" + opt.param[i].getDataString();
			dataset += (dataset == "" ? i : ","+i ); 
		}else{
			data += (data == "" ? i : "&"+i) + "=" + opt.param[i];
		}
	}
	if(dataset)	data += "&_dataset="+ dataset;
	*/
	
	/*debug 
	 Xwing.debug('url:'+ url +",timeout :"+timeout); 
	 Xwing.debug('data:' + data); 
	*/
	var res = null;
	var err = {};
	jqopt = {
		timeout : timeout,
		parameters : data,
		type : 'POST',
		onFailure : function(url, xhr){
			/*debug Xwing.debug("ajax onFailure : url="+url + ", xhr.status="+xhr.status+":"+ (xhr.status ? xhr.statusText : "Unknown-status" )+", xhr.readyState="+xhr.readyState); */
			hideIndicator();
			err.code = 1;
			err.text = 'HTTP Transfer Error:'+(xhr.status ? xhr.statusText : "Unknown-status" );
			try{
				if(xhr.responseText){
					res = eval("("+xhr.responseText+")");
					if(res.ResultMessage){
						err.text += (" " + res.ResultMessage);
					}
				}			
				xwing.Dialog.alert("URL:"+url +"<br/>"+err.text, "Xwing reqeust Error");
				try {
					c.call(null, opt.reqId, res, err, xhr);
				} catch (e) {
				} finally {
					res = null;
				}
			}catch (e){
				// xwing.Dialog.alert("구문 오류", "SyntaxError");
			}finally {
				res = null;
			}		
		},
		onTimeout : function(url, xhr){
			/*debug Xwing.debug("ajax onFailure : url="+url + ", xhr.status="+xhr.status+":"+ (xhr.status ? xhr.statusText : "Unknown-status" )+", xhr.readyState="+xhr.readyState); */
			hideIndicator();
			err.code = -1;
			err.text = 'TIMEOUT';
			xwing.Dialog.alert("URL:"+url +"<br/>"+err.text+"("+timeout+")", "Xwing reqeust Error");
			
			try{
				c.call(null, opt.reqId, res, err, xhr);
			}catch(e){}			
		},
		onSuccess : function(url, xhr){
			/*debug Xwing.debug("ajax onSucces : "+url + ", "+xhr.status + ":" + xhr.statusText + ", "+ xhr.responseText); */
			hideIndicator();
			try{
				res = eval("("+xhr.responseText+")");
				err.code = 0;
				err.text = 'OK';
				try {
					c.call(null, opt.reqId, res, err, xhr);
				} catch (e) {
				} finally {
					res = null;
				}
			}catch(e){
				Xwing.error("ajax onSucces, but parse error: " +e );
				err.code = -2;
				err.text = 'Xwing Response Parse Error';				
				xwing.Dialog.alert("URL:"+url +"<br/>"+err.text+"("+e+")", "Xwing reqeust Error");
				try{
					c.call(null, opt.reqId, res, err, xhr);
				}catch(e){}
			}
		},
		onProgress : function(url, xhr, n){
			/*debug Xwing.debug("Xwing.Ajax is onProgress : "+url +", "+ xhr.readyState + ", " + n); */											
		}
	};
	
	xwing.Ajax.request(url, jqopt);

	function hideIndicator(){
		if(opt.indicator === true){
			xwing.widget.Widget.hideIndicator();
		}else if(opt.indicator !== false){
			xwing.widget.Widget.hideIndicator(opt.indicator);
		}
	}
};

Xwing._getURL = function(url){
	var baseurl = url;
	var urlArray = url.split("::");
	if (urlArray.length == 2) {
		var prefix = urlArray[0];
		if (xwing.Xwing.config.transaction.url[prefix]) {
			baseurl = "";
			baseurl += xwing.Xwing.config.transaction.url[prefix].baseUrl || document.location.pathname;
			baseurl += "/" + urlArray[1];
		}
	}
	return baseurl;
};

Xwing._getTimeout = function(url){
	var timeout = 30000;
	var urlArray = url.split("::");
	if (urlArray.length == 2 && xwing.Xwing.config.transaction.url[urlArray[0]]) {
		var prefix = urlArray[0];
		timeout = xwing.Xwing.config.transaction.url[prefix].timeout || xwing.Xwing.config.transaction.timeout || 30000;
	} else {
		timeout = xwing.Xwing.config.transaction.timeout || 30000;
	}
	return timeout;
};

// http://www.cambiaresearch.com/articles/15/javascript-char-codes-key-codes
Xwing.key = {
	isCtrl : false,
	isShift : false,
	isAlt : false,
	F12 : 123,
	TAB : 9,
	ENTER : 13,
	SHIFT : 16,
	CTRL : 17,
	ALT : 18,
	ESCAPE : 27,
	SPACE : 32,
	LEFT_ARROW : 37,
	UP_ARROW : 38,
	RIGHT_ARROW : 39,
	DOWN_ARROW : 40
};
	
Xwing._addDebugger = function(){
	if (window.console) {
		if (console.firebug) {
			if (console.debug && typeof (console.debug) == "function")
				Xwing.debug = console.debug;
			if (console.log && typeof (console.log) == "function")
				Xwing.log = console.log;
			if (console.error && typeof (console.error) == "function")
				Xwing.error;
		} else if (console.provider) {
			if (console.debug && typeof (console.debug) == "function")
				Xwing.debug = console.debug;
			if (console.log && typeof (console.log) == "function")
				Xwing.log = console.log;
			if (console.error && typeof (console.error) == "function")
				Xwing.error;
		} else {
			if (!console.debug || !console.log || !console.error) {
				Xwing.addFirebugLite();
			}
		}
	} else {
		Xwing.addFirebugLite();
	}
};

Xwing.addFirebugLite = function(){
	var F = document;
	var i = 'createElement';
	var r = 'setAttribute';
	var e = 'getElementsByTagName';
	var b = 'FirebugLite';
	var u = '4';
	var g = 'firebug-lite.js';
	var L = 'skin/xp/sprite.png';
	var I = Xwing.XWING_HOME+"lib/firebug/";
	var T = '';
	(function(F,i,r,e,b,u,g,L,I,T,E){
		if(F.getElementById(b))return;
		E=F.documentElement.namespaceURI;
		E=E?F[i+'NS'](E,'script'):F[i]('script');
		E=F[i]('script');
		E[r]('id',b);
		E[r]('src',I+g+T);
		E[r](b,u);
		(F[e]('head')[0]||F[e]('body')[0]).appendChild(E);
		E=new Image;
		E[r]('src',I+L);
	})(F,i,r,e,b,u,g,L,I,T);
};

Xwing.obj2string = function(obj){
	var str = "{";
	for ( var name in obj) {
		if (typeof (obj[name]) == "object") {
			str += "" + name + ": " + Xwing.obj2string(obj[name]);
		} else {
			str += "" + name + ": " + obj[name] + ",";
		}
	}
	return str + "}";
};

Xwing._loadCreator = function() {
	for ( var wz in Class._map) {
		var clazz = Class.getClass(wz);
		if (clazz['create']) {
			xwing.Xwing['create' + clazz.getName()] = clazz['create'];
			if(clazz.getName() != 'Dataset') xwing.Xwing['get' + clazz.getName()] = Xwing.getWidget;
		}
	}
};

Xwing._loadMasks = function() {
	/*debug Xwing.debug("required mask loading ... "); */
	if( Xwing.config.mask.fixedChars ) 
		jQuery.mask.options.fixedChars =  Xwing.config.mask.fixedChars;
	jQuery.mask.masks = Xwing.config.mask.masks;
};

Xwing._startWatch = function(id) {
	var watch = xwing.util.getStopWatch(id);
	watch.start();
};

Xwing._stopWatch = function(id) {
	var watch = xwing.util.getStopWatch(id);
	watch.stop();
	
	var event = watch.events[watch.events.length - 1];
	event && Xwing.debug('StopWatch (' + 'Xwing._start' + ') = ' + event.duration + ' m/s');	
};

Xwing._getWatch = function(id) {
	return xwing.util.getStopWatch(id);
};

Xwing._getWatchMap = function() {
	return xwing.util.stopwatch.watches;
};