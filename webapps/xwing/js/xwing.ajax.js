xwing.Ajax = {};
xwing.Ajax._factories = [
	function(){ return new XMLHttpRequest();},
	function(){ return new ActiveXObject("Msxml2.XMLHTTP");},
	function(){ return new ActiveXObject("Microsoft.XMLHTTP");}
];

xwing.Ajax._factory = null;

xwing.Ajax.newRequest = function(){
	if (xwing.Ajax._factory != null) return xwing.Ajax._factory();
	
	for ( var i = 0, l = xwing.Ajax._factories.length; i < l; i++) {
		try {
			var factory = xwing.Ajax._factories[i];
			var request = factory();
			if (request != null) {
				xwing.Ajax._factory = factory;
				return request;
			}
		} catch (e) {
			continue;
		}
	}
	
	xwing.Ajax._factory = function(){
		throw new Error("Xwing error : This Browser does not suppot XHR.");
	};
	
	xwing.Ajax._factory();
};

xwing.Ajax.request = function(url, options){
	/*debug Xwing.debug("xwing.Ajax.request "+url+", "+ options); */
	
	var request = xwing.Ajax.newRequest();
	var n = 0;
	var timer;
	var aborted = false;
	var type = options.type || "POST";

	if (options.timeout) {
		timer = setTimeout(function() {
			aborted = true;
			request.abort();
		}, options.timeout);
	}
	
//	if (xmlHttpRequest && xmlHttpRequest.aborted==true) return;
	
	request.onreadystatechange = function(event){
		/*debug Xwing.debug("xhr.onreadystatechange readyState:"+request.readyState +", status:" +request.status); */
		if (request.readyState == 4) {
			timer && clearTimeout(timer);
			
			if (request.status == 200) {
				if (options.onSuccess) {
					options.onSuccess(url, request);
				}
			} else {
				if (aborted && options.onTimeout)
					options.onTimeout(url, request);
				else if (options.onFailure)
					options.onFailure(url, request);
			}
			
			request.onreadystatechange = new Function;
			request = null;
		} else if (options.onProgress) {
			options.onProgress(url, request, ++n);
		}	
	};
	
	var parameters = null;
	if (options.parameters) parameters = options.parameters;

	try {
		request.open(type, url, true);
	} catch(e) {
		/*debug Xwing.debug("error on xhr.open:"+e); */
	}
	
	try { 
		request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	} catch(e) {
		/*debug Xwing.debug("error on xhr.setReqeustHeader:"+e); */
	}

	try {
		request.send(parameters); 
	} catch(e) {
		/*debug Xwing.debug("error on xhr.send:"+e); */
	}	
};
