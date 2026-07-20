Class.define({
	Panel : {
		alias : 'panel',
		namespace : 'xwing.widget',
		extend : xwing.widget.Widget,
		Panel : function(json) {
			if (!arguments.length) return;
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Panel(json);
			}
		},		
		prototypes : {
			isAppendable : function(child){
				return child.getAlias().indexOf("-") == -1;
			},
			_createPart :function(){
				this.jContainer = jQuery("<div/>").addClass("xw-panel xw-container xw-mod-background");
				this.container = this.jContainer[0];
				this._getJShell().addClass('xw-mod-border').append(this.container);
				this._setPosition();
			},
			getContainer : function(){
				return this.container;
			},
			_setPosition : function(el){
				if(el){
					jQuery(el.getShell()).css({"position": "absolute"});
				}else{
					jQuery(this.getContainer()).children(".xw-shell").css({"position":"absolute"});
				}
			},
			appendChild : function(child){
				if(this.isAppendable(child)){
					var childShell = null;
					if(child instanceof xwing.widget.Widget){
						childShell = child.getShell();
					}else{
						childShell = child;
					} 
					var conWidget = this.getContainerWidget();
					var container = conWidget.getContainer();
					container.appendChild(childShell);
					child.setParentWidget(this);
					this._setPosition(child);
					return true;
				}else{
					return false;
				}
			},
			insertChild : function(child, idx){
				if(idx < 0){
					return this.appendChild(child);
				}else{					
					if(this.isAppendable(child)){
						var childShell = null;
						if(child instanceof xwing.widget.Widget){
							childShell = child.getShell();
						}else{
							childShell = child;
						}
						var conWidget = this.getContainerWidget();
						var container = conWidget.getContainer();
						var children = jQuery(container).children('.xw-shell');
						if(children.size() <= idx){
							return this.appendChild(child);
						}else{
							var sibling = children[idx];
							container.insertBefore(childShell, sibling);	
							child.setParentWidget(this);
							this._setPosition(child);
						}
						return true;
					}else{
						return false;
					}
				}
			},
			_appendCompleted : function(){
				this._setPosition();
			},
			_render : function(){
				this._doTabindex();
				this._doBounds();
				this._doEnabled();
				this._doVisible();
				this._doFont();
				this._doBackground();
				this._doBorder();
				this._doScroll();
				this._doUrl();
				this._doOpacity();
				this._doShadow();
				this._doDraggable();
				this._doDroppable();
			},
			setUrl : function(url){
				this._opt.url = url;
				this._doUrl();
			},
			getUrl : function(){
				return this._opt.url;
			},
			_doUrl : function(){
				if(this._opt.url){
					jQuery(".xw-panel-url-indicator", this.jContainer).remove();
					jQuery('iframe', this.jContainer).remove();

					var indicator;
					if(window.xwingIDE){
						indicator = jQuery("<div class='xw-panel-url-indicator xw-mod' style='width:100%; height:100%;margin:0 auto;'>" +
								" <div style='width:100%;margin:auto;position:relative;top:50%;text-align:center;' class='xw-mod' >"+this._opt.url+
								" </div>" +
								"</div>").appendTo(this.jContainer).css('display','block');	
					}else{
						indicator = jQuery("<div class='xw-panel-url-indicator' style='width:100%; height:100%;margin:0 auto;'>" +
								" <div style='width:160px;margin:auto;position:relative;top:40%'>"+
									"<div style='position:relative;top:40%;margin-top:10px;font-size:12px;text-align:center'>Loading...</div>"+		
								"	<img src='"+Xwing.XWING_HOME+"css/themes/silver/img/progress.gif' style='margin:0 auto;height:20px;width:100%;'/>"+
								" </div>"+
								"</div>").appendTo(this.jContainer).css('display','block');	
						var iframe = jQuery('<iframe tabindex="-1" width="100%" height="100%" wmode="transparent" frameBorder="0" class="xw-dialog-iframe" id="if::'+this.getId()+'" name="if::'+this.getId()+'" />');
						iframe.attr('src', Xwing._getURL(this._opt.url));
						iframe.appendTo(this.jContainer).css('display','none');
						this.jContainer.css('overflow','hidden');

						if( this.getScroll()){
							iframe.css('overflow','auto');
						}else{
							iframe.css('overflow','hidden');
						}
						
				  		iframe.bind('load', function(){
				  			jQuery(this).show();
				  			jQuery(indicator).css('display','none');
				  		});
					}
					this._branding();
					jQuery(indicator).css('display','inline');
				}else{
					jQuery(".xw-panel-url-indicator", this.jContainer).remove();
					jQuery('iframe', this.jContainer).remove();
				}
			},
			setScroll : function(v){
				this._opt.scroll = v;
				this._doScroll();
			},
			_doScroll : function() {
				if( this.getScroll()){
					jQuery(".xw-container", this.getShell()).css('overflow','auto');
				}else{
					jQuery(".xw-container", this.getShell()).css('overflow','hidden');
				}
			},
			getScroll : function(){
				return xwing.Util.parseBoolean(this._opt.scroll);
			}			
		}
	}
});