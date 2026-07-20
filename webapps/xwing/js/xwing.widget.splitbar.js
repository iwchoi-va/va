Class.define({
	Splitbar : {
		alias : 'splitbar',
		namespace : 'xwing.widget',
		extend : xwing.widget.Widget,
		Splitbar : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Splitbar(json);
			}
		},
		prototypes : {			
			_createPart : function(){
				this._splitInfo = [];
				this._jBar = jQuery("<div class='xw-splitbar xw-mod xw-mod-border xw-mod-background' unselectable='on' />").appendTo(this._getJShell());
			},
			_render : function(){
				xwing.widget.Widget.prototype._render.call(this);
				this._doBartype();
				this._doBindwidget();
				this._doIcon();
				this._doIconcursor();
			},
			_splitChildren : function(direction,bound,interval){
				for(var i=0, l=this._splitInfo.length; i < l; i++){
					var wdgObj = Xwing.getWidget(this._splitInfo[i].wdgId);
					if(!wdgObj) continue;
					
					var splitLT = parseInt(this.getAttribute(direction));
					var widgetLT = parseInt(wdgObj.getAttribute(direction));
					var widgetWH = parseInt(wdgObj.getAttribute(bound));
					if(this._splitInfo[i].type.toUpperCase() == 'M')
						wdgObj.setAttribute(direction,interval + widgetLT);
					else if(this._splitInfo[i].type.toUpperCase() == 'R'){
						var gab = 0;
						var fixed = widgetLT;
						if(splitLT > widgetLT ){ // left or top
							gab = splitLT - widgetLT - widgetWH;
						}else { // right or bottom
							gab = widgetLT - splitLT;
							fixed = widgetLT + widgetWH;
						}
						
						if((splitLT + interval) >= fixed){ // left or top
							wdgObj.setAttribute(bound,(splitLT + interval - widgetLT - gab));
						}else{ // right or bottom
							var directionValue =splitLT + interval + gab;
							var boundValue = fixed - directionValue;
							wdgObj.setAttribute(direction,boundValue < 0 ? directionValue+boundValue : directionValue);
							wdgObj.setAttribute(bound,boundValue);
						}
					}
				}
			},
			_doBounds:function(){
				xwing.widget.Widget.prototype._doBounds.call(this);
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
			},
			setBindwidget : function(v){
				this._opt.bindwidget = v;
				this._doBindwidget();
			},
			getBindwidget : function(){
				return this._opt.bindwidget;
			},
			_doBindwidget : function(){
				if(!this.getBindwidget()) return;
				
				var arr = this.getBindwidget().replace(/\s/g,'').split(',');
				var info;
				
				for(var i=0, l=arr.length; i < l ; i++){
					info = arr[i].split(':');
					if(info.length != 2) continue;
					this._splitInfo.push({
						type : info[0],
						wdgId : info[1]
					});
				}
			},
			setBartype : function(v){
				this._opt.bartype = v;
				this._doBartype();
			},
			getBartype : function(){
				return this._opt.bartype;
			},
			_doBartype : function(){
				var thisObj = this;
				this._jBar.draggable('destroy');
				this._jBar.draggable({
					opacity : '0.5',
					cursor : thisObj.getBartype() == 'horizontal' ? 'e-resize' : 'n-resize',
					axis : thisObj.getBartype() == 'horizontal' ? 'x' : 'y',
					iframeFix: true, 
					helper : function() {
						return jQuery("<div class='xw-splitbar-resizer-ghost'/>").height(thisObj.getHeight()).width(thisObj.getWidth());
					},
					start : function(event, ui) {
						jQuery('div.xw-page').addClass('xw-unselectable');
						thisObj._fire('dragstart', {type:'dragstart',source:this,event:event,position : ui.position, offset : ui.offset});
					},
					drag : function(event,ui){
						thisObj._fire('drag', {type:'drag',source:this,event:event,position : ui.position, offset : ui.offset});
					},
					stop : function(event, ui) {
						try {
							if (thisObj.getBartype() == 'horizontal') {
								thisObj._splitChildren('left', 'width', ui.position.left);
								thisObj.setLeft(thisObj.getLeft() + ui.position.left);
							} else {
								thisObj._splitChildren('top', 'height', ui.position.top);
								thisObj.setTop(thisObj.getTop() + ui.position.top);
							}
							thisObj._fire('dragstop', {type:'dragstop',source:this,event:event,position : ui.position, offset : ui.offset});
						} catch (e) {}
						jQuery('div.xw-page').removeClass('xw-unselectable');
					}
				});
			},	
			setIcon : function(v){
				this._opt.icon = v;
				this._doIcon();
			},
			getIcon : function(){
				return this._opt.icon;
			},
			_doIcon : function(){
				if(this.getIcon()){
					var icon = jQuery('div.xw-split-icon',this.getShell());
					if(icon.length == 0) {
						icon = jQuery("<div class='xw-split-icon xw-mod' _xid='"+this.getXId()+"' />").appendTo(this._jBar);
						this._bind(icon,'click',function(event){
							this._fire('select', {type:'select',source:this,event:event});
						});
					}
					var image = new Image();
					image.onload = function() {
						icon.width(this.width);
						icon.height(this.height);
						icon.css('margin-top',this.height/2*-1);
						icon.css('margin-left',this.width/2*-1);
						image = null;
					};
					image.src = this._opt.icon;
					icon.css('background-image','url('+this.getIcon()+')');
				}else{
					jQuery('div.xw-split-icon',this.getShell()).empty().remove();
				}
			},
			setIconcursor : function(v){
				this._opt.iconcursor = v;
				this._doIconcursor();
			},
			getIconcursor : function(){
				return this._opt.iconcursor;
			},
			_doIconcursor : function(){
				var icon = jQuery('div.xw-split-icon',this.getShell());
				if(icon.length == 0) return;
				icon.css('cursor',this.getIconcursor() == 'default' ? '' : this.getIconcursor());
			},
			setGutter : function(v){
				this._opt.gutter = v;
				this._doGutter();
			},
			getGutter : function(){
				return this._opt.gutter;
			},
			_doGutter : function(){
				if(this.getParentWidget() ){
					var gutter = this.getGutter().trim().split(/\s+/);
					var result = '';
					for(var i=0,l=gutter.length; i< l ; i++)
						result += gutter[i]+'px ';
					jQuery(this.getParentWidget().getContainer()).css('padding',result);
				}
			},
			setParentWidget : function(w){
				this._parentWidget = w;
				this._jBar.draggable("option", "containment", w.getContainer());
				this._doGutter();
			}
		}	
	}
});