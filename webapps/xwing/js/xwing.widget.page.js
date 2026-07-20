try {
Class.define({
	Page : {
		alias : 'page',
		namespace : 'xwing.widget',
		extend : xwing.widget.Panel,
		Page : function(json) {
			this._init(json);
		},
		statics : {
			create : function(json) {
				return new xwing.widget.Page(json);
			}
		},		
		prototypes : {
			_createShell : function() {
				this.$ = jQuery("<div style='border:0px none;'/>").addClass("xw-shell xw-page xw-mod-background xw-container");
				return this.$[0]; 
			},
			_createPart : function() {
				this.container = this.shell;

				jQuery(document.body).css("width", "100%");
				this._getJShell().css("width", "100%");
				jQuery(document.body).css("height", "100%");
				this._getJShell().css("height", "100%");

				if (!window.xwingIDE) {
					var thisObj = this;
					
					window.onunload = function(event) {
						var opt = {
							type : 'unload',
							source : thisObj,
							event : event
						};

						thisObj._fire('unload', opt);
					};
				} else {
					if (this._opt.width && this._opt.height) {
						jQuery(document.body).css("width", this.getHeight());
						this._getJShell().width(this.getWidth());
						
						jQuery(document.body).css("height", this.getHeight());
						this._getJShell().height(this.getHeight());
					}

					this._opt.left && (this._opt.left = 0);
					this._opt.top && (this._opt.top = 0);
					
					var jContainer = jQuery("<div style='margin:0px;overflow:hidden;' class='xw-ide-page xw-container'/>");
					this._getJShell().removeClass('xw-container').append(jContainer);
					this.container = jContainer[0];
				}
				
				this._doScroll();
				this._doTitle();
				this._setPosition();
				
				/*debug Xwing.debug("Widget(Page) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_doBounds : function() {
				if (window.xwingIDE) {
					xwing.widget.Widget.prototype._doBounds.call(this);
				}
			},
			_appendCompleted : function() { },
			_doVisible : function() { },
			_doEnabled : function() { },
			getEnabled : function() {
				return true;
			},
			_doScroll : function() {
				if (!window.xwingIDE) {
					if (this.getScroll()) {
						this._getJShell().css('overflow', 'auto');
					} else {
						this._getJShell().css('overflow', 'hidden');
					}
				}
			},
			setTitle : function(v) {
				this._opt.title = v;
				this._doTitle();
			},
			getTitle : function() {
				return this._opt.title;
			},
			_doTitle : function() {
				document.title = this.getTitle();
			}
		}
	}
});
} catch(e) {
	/*debug Xwing.debug("Page class define error :"+e); */
}
