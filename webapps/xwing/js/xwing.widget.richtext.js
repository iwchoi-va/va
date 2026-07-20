Class.define({
	Richtext : {
		alias : 'richtext',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Richtext : function(json) {
			this._init(json);
		},
		statics : {
			create : function(json) {
				return new xwing.widget.Richtext(json);
			}
		},
		prototypes : {
			_createPart : function() {
				this.richtext = jQuery("<div class='xw-richtext xw-mod-border xw-mod-background' style='width:100%;height:100%;'/>");
				this.richtext.appendTo(this._getJShell());
				/*debug Xwing.debug("Widget(Richtext) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_render : function() {
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doScroll();
			},
			_doValue : function() {
				try {
					this.richtext.empty();
					this.richtext.html((this.getValue()||'').replace(/&nbsp;/gi, '&#160;'));
					
					if (window.xwingIDE) {
						jQuery('iframe', this.richtext).each(function(i, w){
							var replaceDiv = jQuery("<div class='xw-ide-iframe'/>");
							var thisObj = jQuery(this);

							var properties = [ "float", "position", "width", "height", "left", "top", "marginLeft", "marginTop" ];
							for ( var i in properties) {
								replaceDiv.css(properties[i], thisObj.css(properties[i]) || "");
							}

							thisObj.replaceWith(replaceDiv);
						});
					}
					
					this._branding();
				} catch (e) {
					Xwing.error(e);
				}
			},
			setScroll : function(v){
				this._opt.scroll = v;
				this._doScroll();
			},
			_doScroll : function() {
				this.richtext.css('overflow', this.getScroll() ? 'auto' : 'hidden');
			},
			getScroll : function(){
				return xwing.Util.parseBoolean(this._opt.scroll);
			}			
		}
	}
});
