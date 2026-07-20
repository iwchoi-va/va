Class.define({
	HTML : {
		alias : 'html',
		namespace : 'xwing.widget',
		extend : xwing.widget.Widget,
		HTML : function(obj){
			if(obj && obj.nodeType && typeof(obj.nodeType) == "number" && obj.nodeName &&typeof(obj.nodeName) == "string" ){
				this.shell = obj.cloneNode(false);	
				jQuery(this.shell).addClass("xw-shell");
				/*debug Xwing.debug(this.alias+" widget created by Element : " + obj); */
				var id = obj.getAttribute("id"); 
				if(id)	Xwing.addWidget(id, this);
				var xid = obj.getAttribute("_xid"); 
				if(xid && xwingIDE && xwingIDE.addWidget){
					xwingIDE.addWidget(xid, this);
				}
				var xJson = xwing.widget.Widget._parseXJson(obj);
				this._opt = xJson;		
			}else{
				var json = obj;
				/*debug Xwing.debug(this.alias+" widget created by JSON : " + xwing.util.Util.obj2json(json)); */
				this._init(json);
			}
		},
		statics:{
			create : function(obj){
				return new xwing.widget.HTML(obj);
			},
			parse : function(xNode){
				var xJson = xwing.widget.Widget.parseXJson(xNode);
				var aEl = new xwing.widget.HTML(xJson);
			}
		},
		prototypes : {
			isAppendable : function(){
				return true;
			},
			appendChild : function(child){
				this.getShell().appendChild(child.getShell());
				return true;
			},
			_createShell : function(){
				var shell = jQuery("<"+this._opt.xw_type + "/>");
				this.shell = shell[0];
				for(var att in this._opt){
					shell[0].setAttribute(att, this._opt[att]);
				}
				if(this._opt._xid) shell.attr("_xid", this._opt._xid);
				return this.shell;
			},
			_createPart : function(){
				/*debug Xwing.debug("Widget(HTML "+ this._opt.xw_type+") created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			setAttribute : function(key, value){
				this.getShell().setAttribute(key, value);
			},
			getAttribute : function(key){
				return this.getShell().getAttribute(key);
			},
			hasAttribute : function(key){
				return this.getShell().hasAttribute(key);
			},
			removeAttribute : function(key){
				return this.getShell().removeAttribute(key);
			}
		}	
	}
});
