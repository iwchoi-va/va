String.prototype.trim = function() {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
}

xwing.util ={};

xwing.util.Util = {
	parseInt : function(value, defaultValue){
		defaultValue = (defaultValue == undefined? 0 : defaultValue);
		var result = defaultValue; 
		if(isNaN(parseInt(value))){
		 if(typeof(value) == "string"){
		 	var temp = value.replace(/[^0-9]/g, "");
		 	try{
		 		result = parseInt(temp == "" ? defaultValue : temp);
		 	}catch(e){} 
		 }
		}else{
			try{
				result = parseInt(value);
			}catch(e){}
		}
		return result;
	},
	parseFloat : function(value, defaultValue){
		defaultValue = (defaultValue == undefined? 0 : defaultValue);
		var result = defaultValue; 
		if(isNaN(parseFloat(value))){
		 if(typeof(value) == "string"){
		 	var temp = value.replace(/[^0-9|.]/g, "");
		 	try{
		 		result = parseFloat(temp == "" ? defaultValue : temp);
		 	}catch(e){} 
		 }
		}else{
			try{
				result = parseFloat(value);
			}catch(e){}
		}
		return result;
	},
	parseBoolean : function(value){
		if(value === true || value === "true") return true;
		else return false;
	},
	toString : function(obj){
		var json = "{";
		for(var key in obj){
			json += key + ":" + (typeof(obj[key]) == "object" ? xwing.util.Util.obj2json(obj[key]) : 
									(typeof(obj[key]) == "string" ? "'"+obj[key]+"'" : obj[key]));
			json += ",";
		}
		return json += "}";
	},
	obj2json : function(obj){
		var json = "{";
		for(var key in obj){
			json += key + ":" + (typeof(obj[key]) == "object" ? 
									(obj[key] instanceof xwing.Dataset ? 
										obj[key].toString() : xwing.util.Util.obj2json(obj[key])): 
											(typeof(obj[key]) =="string" ? "'"+obj[key]+"'": obj[key]));
			json += ",";
		}
		return json += "}";
	},
	att2json : function(node){
		var attStr = "";
		var i=0;
		while(node.attributes.length){
			if(node.attributes[i].nodeValue){
				attStr += node.attributes[i].nodeName +":'"+ node.attributes[i].nodeValue+"'";
				if(i != node.attributes.length -1){
					attStr += ",";
				}else{
					break;
				}
			}
			i++;
		}
		/*debug Xwing.debug("att2json:"+attStr); */
		return eval("({"+attStr+"})");
	},
	shift : function(arry,value){
		var return_arry = arry;
		if( typeof arry == "array" || typeof arry == "object"){
			if(value == null || value == undefined ) return return_arry;
			return_arry = new Array();
			for(var i=0; i < arry.length; i++){
				if(arry[i] == value){
					return_arry = return_arry.concat(arry.slice(i+1));

					return return_arry;
				}
				return_arry.push(arry[i]);
			}
		}
		
		return return_arry;		
	},
	is : function(obj, type) {
		var stringTolowerCase = String.prototype.toLowerCase, 
			objectToString = Object.prototype.toString, 
			type = stringTolowerCase.call(type);

		return (type == "object" && obj === Object(obj)) || 
			   (type == "undefined" && typeof obj == type) || 
			   (type == "null" && obj == null) || 
			   (type == "array" && Array.isArray && Array.isArray(obj)) || 
			   stringTolowerCase.call(objectToString.call(obj).slice(8, -1)) == type;
	},
	isTouchDevice : function() {
		return !!('ontouchstart' in window);
	},
	parseShorthand : function(v) {
	    var tokens = (v || '').trim().split(/\s+/);
	    
	    for (var i = 0, l = tokens.length ; i < l ; i++) {
	        tokens[i] += 'px';
	    }
	    
	    return tokens.join(' ');
	},
	encodeHtml : function(v) {
		 /(&|>|<|"|')/.test(v) && (v = v.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;"));
		 return v;
	},
	getCellCoords : function(elem) {
		if (elem.nodeName !== 'td' && elem.nodeName !== 'th')
			return null;
	
		var node = jQuery(elem),
			result = {
				y : node.parent('tr').prevAll('tr').length,
				x : null
			};
		
		var rows = node.closest("table").find("tr"), 
			matrix = [], 
			row = null, cells = null, cell = null, 
			colspan = null, rowspan = null, 
			rowIndex = null, colIndex = null;
		
		for ( var i = 0; i < rows.length && result.x === null; i++) {
			matrix[i] = matrix[i] || [];
			row = rows[i];
			cells = jQuery(row).children('td, th');

			for ( var j = 0; j < cells.length && result.x === null; j++) {
				cell = jQuery(cells[j]);
				colspan = +cell.attr('colspan') || 1;
				rowspan = +cell.attr('rowspan') || 1;

				rowIndex = i;
				matrix[rowIndex] = matrix[rowIndex] || [];
				colIndex = null;
				
				for ( var l = 0; l <= matrix[rowIndex].length && colIndex === null; l++) {
					if (!matrix[rowIndex][l])
						colIndex = l;
				}

				if (elem === cell[0]) {
					result.x = colIndex;
					break;
				}

				for ( var k = rowIndex; k < rowIndex + rowspan; k++) {
					for ( var l = colIndex; l < colIndex + colspan; l++) {
						matrix[k] = matrix[k] || [];
						matrix[k][l] = 1;
					}
				}
			}
		}

		return result;
	}
};

xwing.util.Util.isXwing = function(element){
	var flag = false;
	if(element){
		if(element.scopeName){ //for IE
			if(/xwing/i.test(element.scopeName))
					flag = true;
		}else{
			if(element.prefix){ //for xml
				if(/xwing/i.test(element.prefix))
					flag = true;
			}else{ // for other
				if(element.tagName && /^xwing:\w+/i.test(element.tagName))
					flag = true;
			}
		}
	}
	return flag;	
};

xwing.util.Util.getXwingTagName = function(element){
	var tagName = null;
	if(element){
		if(element.scopeName){ //for IE
			if(/xwing/i.test(element.scopeName) && element.tagName)
					tagName = element.tagName.toUpperCase();
		}else{
			if(element.prefix){ //for xml
				if(/xwing/i.test(element.prefix) && element.localName)
					tagName = element.localName.toUpperCase();
			}else{ // for other
				if(element.tagName && /^xwing:\w+/i.test(element.tagName))
					tagName = element.tagName.replace(/^xwing:/i, "").toUpperCase();				
			}
		}
	}
	return tagName;	
};

xwing.util.Mask = {
	formatMask : function(originValue, mask) {
		var str = new String(originValue);
		str = str.replace(/(\,|\.|\-|\/|\:|\\)/g,'');
		var formatStr="";
		var j = 0; 
		for (var i=0; i< str.length; i++) {
			formatStr += str.charAt(i);
			j++;
			if (j<str.length && j < mask.length && mask.charAt(j) != "#") {
				formatStr += mask.charAt(j++);
			}
		}
		return formatStr;
	},
	formatUnMask : function(maskValue, mask) {
		return maskValue.replace(/(\,|\.|\-|\/|\:|\\)/g,'');		 
	}
}

xwing.util.Hash = function(){
	this._item = {};
	this._length = 0;
}
xwing.util.Hash.prototype = {
		set: function(key, value){
			if(key == undefined) return;
			if(value == undefined) value = null;
			if(this._item[key] == undefined){
				this._item[key] = value;
				this._length++;
			}else{
				this._item[key] = value;
			}
		},// end of set
		get: function(key){
			if(key == undefined) return;
			return this._item[key];
		},// end of get
		remove: function(key){
			var tmp_value;
			if(key == undefined){
				return;
			}
			if( this._item[key] !== undefined){
				tmp_value = this._item[key];
				this._length--;
				delete this._item[key];
			}
			return tmp_value;
		},
		hasKey : function(key){
			return this._item[key] != undefined;
		},
		size : function(){
			return this._length;
		},
		keys : function(){
			var keyArray = [];
			for(var i in this._item){
				keyArray[keyArray.length] = i;
			}
			return keyArray;
		},
		toString: function(){
			var str = new String();
			for(var j in this._item){
				str += "["+j + ":" + this._item[j] +"] ";
			}
			return str;
		}
}

xwing.util.Event = {
	addEvent : function(element, evtType, handler){
		if(element.addEventListener){//ff
			element.addEventListener(evtType, function(event){handler.handleEvent(event);}, false);
		}else if(element.attachEvent){//ie
			element.attachEvent("on"+evtType, function(event){handler.handleEvent(event);});
		}
	}
}

xwing.util.CSS = {
	hasClass : function(node, className){
		if(typeof(node) == "string") node = document.getElementById(node);
		var classes = node.className;
		if(!classes) return false;
		if(classes == className) return true;
		return node.className.search("\\b" + className + "\\b") != -1;	
	},
	addClass : function(node, className){
		if(typeof(node) == "string" ) node = document.getElementById(node);
			if(xwing.util.CSS.hasClass(node, className)){
				 return;
			}
			if(node.className ) className = " "+ className;
			node.className += className ;
	},
	removeClass : function(node, className){
		if(typeof(node)== "string") node = document.getElementById(node);
		if(xwing.util.CSS.hasClass(node, className))
		node.className = node.className.replace(new RegExp("\\b"+ className + "\\b\\s*", "g"), "");

	},
	getY : function (node){
		var y = 0;
		for(var n = node; n; n = n.offsetParent)
			y += n.offsetTop;
	
		for(e= node.parentNode; e && e!= document.body; e = e.parentNode)
			if(e.scrollTop) y -= e.scrollTop;
		return y;
	},
	getX : function (node){
		var x = 0;
		while(node){
			x += node.offsetLeft;
			node = node.offsetParent;
		}
		return x;
	}	
}

xwing.util.String = {
	length : function(str){
		var str = str || "" ;
		var len = 0;
		for(var i=0; i < str.length ; i++ ){
			if( escape(str.charAt(i)).length > 4 ) len += 2;
			else len +=1;
		}
		return len;
	}
}

xwing.util.BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]
};

xwing.util.getWidgetList = function(obj,regExpr){
	var reg;
	var arr = [];
	var widgets = Xwing.getWidgetList();
	
	if(arguments.length == 1){
		var first = arguments[0];
		if((first instanceof RegExp) || (typeof first == "string")){
			if(first instanceof RegExp) reg = first;
			else reg = new RegExp(first);
			
			for(var i in widgets._item){
				var widget = widgets._item[i];
				if(reg.test(i))
					arr.push(widget);
			}
		}else{
			for(var i in widgets._item){
				var widget = widgets._item[i];
				if(widget instanceof first){
					arr.push(widget);
				}
			}
		}
	}else{
		if(typeof regExpr == "object") reg = regExpr;
		else reg = new RegExp(regExpr);
		for(var i in widgets._item){
			var widget = widgets._item[i];
			if(reg.test(i) && (widget instanceof obj)){
				arr.push(widget);
			}
		}
	}
	
	return arr;	
};
xwing.util.BrowserDetect.init();
Xwing.getBrowserName = function(){
	return xwing.util.BrowserDetect.browser;
}
Xwing.getOSName = function(){
	return xwing.util.BrowserDetect.OS;
}
Xwing.getBrowserVersion = function(){
	return xwing.util.BrowserDetect.version;
}

xwing.Util = xwing.util.Util;

xwing.util.stopwatch = {};
xwing.util.stopwatch.watches = {};
xwing.util.stopwatch.StopWatch = function(id) {
	this.id = id;
	xwing.util.stopwatch.watches[id] = this;
	this.events = new Array();
	this.count = 0;
	this.total = 0;
}
xwing.util.stopwatch.StopWatch.prototype.start = function() {
	this.current = new xwing.util.stopwatch.TimedEvent();
}
xwing.util.stopwatch.StopWatch.prototype.stop = function() {
	if (this.current) {
		this.current.stop();
		this.events.push(this.current);
		this.count++;
		this.total += this.current.duration;
		this.current = null;
	}
}
xwing.util.stopwatch.TimedEvent = function() {
	this.start = new Date();
}
xwing.util.stopwatch.TimedEvent.prototype.stop = function() {
	var stop = new Date();
	this.duration = stop - this.start;
}
xwing.util.getStopWatch = function(id, startNow) {
	var watch = xwing.util.stopwatch.watches[id];
	!watch && (watch = new xwing.util.stopwatch.StopWatch(id));
	startNow && watch.start();
	return watch;
}
xwing.util.removeStopWatch = function(id) {
    delete xwing.util.stopwatch.watches[id];
}
xwing.util.Util.getByteLength = function(ch){
	 if (ch == null || ch.length == 0) return 0;
	 
	    var tot = 0;
	    for(var i=0; i< ch.length; i++){
	    	var charCode = ch.charCodeAt(i);

	        if (charCode <= 0x00007F) {
	        	tot += 1;
	        } else if (charCode <= 0x0007FF) {
	        	tot += 2;
	        } else if (charCode <= 0x00FFFF) {
	        	tot += 3;
	        } else {
	        	tot += 4;
	        }
	    }
	    return tot;
}
xwing.util._placeholder = function(widget, jEdit){
	this.widgetObj = widget;
	this.jEdit = jEdit;
	this.jSpan = undefined;
};
xwing.util._placeholder.prototype = {
		create : function(){
			this.jSpan = this.jEdit.siblings().filter('span.xw-placeholder');
			if(this.jSpan.length == 0){
				this.jSpan= jQuery("<span class='xw-placeholder xw-mod' _xid='"+this.widgetObj.getXId()+"' >"+this.widgetObj.getPlaceholder()+"</span>").css('line-height',this.widgetObj.getHeight()+'px');
				this.jEdit.before(this.jSpan);
				if(this.jEdit[0].value == "" ) this.jSpan.css("display",'inline');
				var thisObj = this;
				this.jEdit.bind('focus',function(event){
					if(thisObj.jSpan) thisObj.jSpan.hide();
				});
				this.jEdit.bind('blur',function(event){
					thisObj.confirmValue();
				});
			}else
				this.jSpan.text(message).css('line-height',height+'px');
		},
		remove : function(){
			this.jEdit.siblings().filter('span.xw-placeholder').remove();
		},
		setMessage : function(){
			if(this.jSpan) this.jSpan.text(this.widgetObj.getPlaceholder()); 
		},
		confirmValue : function(){
			if(this.jSpan && this.jEdit && this.jEdit[0].value.length == 0) this.jSpan.show();
			else this.jSpan.hide();
		}
};
xwing.util.importJSFile = function(url){
	var script = document.createElement("script");
	script.type = "text/javascript";
	script.charset="UTF-8";
	script.src = url+"?"+((new Date()).getSeconds()).toString(16);
	var head = document.getElementsByTagName('head')[0];
	if (head) {
		head.insertBefore(script);
	}else{
		document.body.insertBefore(script);
	}
};