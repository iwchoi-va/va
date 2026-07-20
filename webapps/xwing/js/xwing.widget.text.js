Class.define({
	Text : {
		alias : '#text',
		namespace : 'xwing.widget',
		extend : xwing.widget.Widget,
		Text : function(obj){
			if(obj && obj.nodeType && typeof(obj.nodeType) == "number" && obj.nodeName &&typeof(obj.nodeName) == "string" ){
				this.shell = obj.cloneNode(false);
				/*debug Xwing.debug(this.alias+" widget created by Element : " + obj); */				
			}else{
				var json = obj;
				this._init(json);
				/*debug Xwing.debug("Widget("+ this._opt.xw_type+") created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			}			
		},
		statics:{
			create : function(obj){
				return new xwing.widget.Text(obj);
			},
			parse : function(xNode){
				var xJson = xwing.widget.Widget.parseXJson(xNode);
				xJson.text = xNode.nodeValue;
				var aEl = new xwing.widget.Text(xJson);
			}			
		},
		prototypes : {
			_createShell : function(){
				this.shell = document.createTextNode(this._opt.text);
				return this.shell;
			},
			_createPart : function(){

			},
			setText : function(text){
				this.getShell().nodeValue = text;				
			}
		}	
	}
});
