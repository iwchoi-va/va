Class.define({
	Tabpage : {
		alias : "tab-page",
		namespace : "xwing.widget",
		extend : xwing.widget.Panel,
		Tabpage : function(json) {
			this._init(json);
			this.containerWidget = this;
			/*debug Xwing.debug("Widget(Tab-page) created : "); */
		},
		statics : {
			create : function(json){
				return new xwing.widget.Tabpage(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this._page = jQuery("<div class='xw-tab-page'/>").css('display', 'none');
				this.container = jQuery("<div class='xw-container xw-mod-background'/>")[0];
				this._page.append(this.container);
				
				this.handleDiv = jQuery("<div class='xw-tab-handle xw-mod-font xw-mod-border' unselectable='on'/>");
				this._handle = jQuery("<li/>").append(this.handleDiv)[0];
				this._jclose = jQuery('<span class="xw-tab-handle-close" />');
				this._jicon = jQuery('<span class="xw-tab-handle-icon xw-mod" _xid="' + this.getXId() + '" />');

				var thisObj = this;
				this._bind(this._jclose, 'click', function(e) {
					if (thisObj._parentWidget) {
						thisObj._parentWidget.removeTab(thisObj);
					}
				});
			},
			_getModule : function(mod) {
				if ("border" == mod) {
					return this.handleDiv;
				} else if ("background" == mod) {
					return jQuery(this.container);
				} else {
					return jQuery(".xw-mod-" + mod + "[_xid=" + this.getXId() + "]", this.getShell());
				}
			},
			_branding : function() {
				jQuery("*", this._page).andSelf().addClass("xw-mod");
				jQuery("*", this._handle).andSelf().addClass("xw-mod");
				
				if (this._opt._xid) {
					jQuery("*", this._page).andSelf().attr("_xid", this._opt._xid);
					jQuery("*", this._handle).andSelf().attr("_xid", this._opt._xid);
				}
			},
			setTitle : function(title) {
				this._opt.title = title;
				this._doTitle();
			},
			getTitle : function() {
				return this._opt.title;
			},
			_doTitle : function() {
				this._jclose = this._jclose.detach();
				this._jicon = this._jicon.remove();
				this.handleDiv.text(this._opt.title).prepend(this._jclose).prepend(this._jicon);
				if (this._parentWidget && !this._parentWidget.getMultiline()) {
					this._parentWidget._resetHandlewidth();
				}
			},
			_doEnabled : function() {
				if (this.getEnabled()) {
					jQuery(".xw-tab-handle", this._handle).removeClass("xw-tab-handle-disabled").addClass("xw-tab-handle-off");
					jQuery(this._handle).css('height', '');
					if (this._parentWidget) {
						if (this._parentWidget.getActivePageWidget() != this)
							jQuery(".xw-tab-handle", this._handle).css('border-bottom-width', this._parentWidget.getBorderwidth());
					}
				} else {
					jQuery(".xw-tab-handle", this._handle).addClass("xw-tab-handle-disabled").removeClass("xw-tab-handle-off").removeClass("xw-tab-handle-on");
					jQuery(".xw-tab-handle", this._handle).css('border-bottom-width', 0);
					if (this._parentWidget) {
						var h = this._parentWidget.getHandleheight() - this._parentWidget.getBorderwidth();
						jQuery(this._handle).height(h + 'px');
					}
				}
			},
			getHandle : function() {
				return this._handle;
			},
			activate : function() {
				jQuery(".xw-tab-handle", this._handle).removeClass("xw-tab-handle-off").addClass("xw-tab-handle-on");
				if (this._parentWidget) {
					if (this._parentWidget.getBorderstyle() == 'none') {
						jQuery(".xw-tab-handle", this._handle).css('border-color', 'transparent');
						jQuery('.xw-tab-handle', this._handle).css('border-bottom-width', '0px');
					} else {
						jQuery('.xw-tab-handle', this._handle).css('border-color', this._parentWidget.getBordercolor());
						jQuery('.xw-tab-handle', this._handle).css('border-style', this._parentWidget.getBorderstyle());
						jQuery('.xw-tab-handle', this._handle).css('border-width', this._parentWidget.getBorderwidth() + 'px');
						jQuery('.xw-tab-handle', this._handle).css('border-bottom-width', '0px');
					}
				}
				jQuery(this._page).css('display', 'block');
				
				if (!this._pageLoaded) {
					this._doUrl();
					this._pageLoaded = true;
				}
			},
			deactivate : function() {
				jQuery(".xw-tab-handle", this._handle).removeClass("xw-tab-handle-on").addClass("xw-tab-handle-off");
				if (this._parentWidget) {
					if (this._parentWidget.getBorderstyle() == 'none') {
						jQuery('.xw-tab-handle', this._handle).css('border-color', 'transparent');
						jQuery('.xw-tab-handle', this._handle).css('border-bottom-width', '0px');
					} else {
						jQuery('.xw-tab-handle', this._handle).css('border-bottom-width', this._parentWidget.getBorderwidth() + 'px');
					}
				}
				jQuery(this._page).css('display', 'none');
			},
			setUrl : function(url) {
				this._opt.url = url;
				this._doUrl();
			},
			getUrl : function() {
				return this._opt.url;
			},
			_doUrl : function() {
				if (this._opt.url) {
					jQuery(".xw-tab-url-indicator", this._page).remove();
					jQuery('iframe', this._page).remove();
					
					var indicator;
					if (window.xwingIDE) {
						indicator = jQuery("<div class='xw-tab-url-indicator xw-mod' style='width:100%; height:100%;margin:0 auto;'>" +
							" <div style='width:100%;margin:auto;position:relative;top:50%;text-align:center;' class='xw-mod' >" + this._opt.url + " </div>" +
							"</div>").appendTo(this._page).css('display','block');
					} else {
						indicator = jQuery("<div class='xw-tab-url-indicator' style='width:100%;height:100%;margin:0 auto;'>" +
							" <div style='width:160px;margin:auto;position:relative;top:40%'>"+
							" <div style='position:relative;top:40%;margin-top:10px;font-size:12px;text-align:center'>Loading...</div>"+	
							"	<img src='"+Xwing.XWING_HOME+"css/themes/silver/img/progress.gif' style='margin:0 auto;height:20px;width:100%;'/>" + " </div>" +
							"</div>").appendTo(this._page).css('display','block');
						
						var iframe = jQuery('<iframe tabindex="-1" width="100%" height="100%" frameBorder="0" class="xw-dialog-iframe" id="if::'+this.getId()+'" name="if::'+this.getId()+'" />');
						iframe.attr('src', Xwing._getURL(this._opt.url));
						iframe.appendTo(this._page);
						jQuery(this.container).css('overflow','hidden');

						if (this.getScroll()) {
							iframe.css('overflow', 'auto');
						} else {
							iframe.css('overflow', 'hidden');
						}
						
				  		iframe.bind('load', function(){
				  			jQuery(indicator).css('display','none');
				  		});
					}
					
					this._branding();					
					jQuery(this.container).css('display','none');
				} else {
					this._page.find(".xw-tab-url-indicator").remove();
					this._page.find('iframe').remove();
					jQuery(this.container).css('display', 'block');
				}
			},
			setClosable : function(v) {
				this._opt.closable = v;
				this._doClosable();
			},
			getClosable : function() {
				return xwing.Util.parseBoolean(this._opt.closable, false);
			},
			_doClosable : function() {
				if (this.getClosable())
					this._jclose.show();
				else
					this._jclose.hide();
			},
			setIcon : function(v) {
				this._opt.icon = v;
				this._doIcon();
			},
			getIcon : function() {
				return this._opt.icon;
			},
			_doIcon : function() {
				if (this._opt.icon) {
					var image = new Image();
					var thisObj = this;
					image.onload = function() {
						thisObj._jicon.width(this.width + 'px');
						if (thisObj._parentWidget && thisObj._parentWidget._handlewidth != 0 && !thisObj._parentWidget.getHandlewidth()) {
							thisObj._parentWidget._handlewidth += +this.width;
							thisObj._parentWidget._curright += this.width;
							thisObj._parentWidget._jul.width(thisObj._parentWidget._handlewidth + 'px');
							thisObj._parentWidget._moveHandle();
						}
					};
					image.src = this._opt.icon;
					this._jicon.css('background-image', 'url(' + this._opt.icon + ')');
					this._jicon.show();
				} else {
					this._jicon.hide();
				}
			},
			getShell : function() {
				return this._parentWidget ? this._parentWidget.getShell() : null;
			},
			remove : function() {
				this._parentWidget.removeTab(this);
			},
			_render : function() {
				this._doTitle();
				this._doEnabled();
				this._doScroll();
//				this._doUrl();
				this._doBackground();
				this._doTextpadding();
				this._doClosable();
				this._doIcon();
			},
			_doScroll : function() {
				if (this.getScroll()) {
					jQuery(".xw-container", this._page).css('overflow', 'auto');
				} else {
					jQuery(".xw-container", this._page).css('overflow', 'hidden');
				}
			},
			_doBounds : function() {
			},
			_doEvent : function() {
			},
			getPage : function() {
				return this._page;
			},
			_doBorder : function() {
				if (this._parentWidget) {
					if (this._parentWidget.getBorderstyle() == 'none') {
						this.handleDiv.find('.xw-tab-handle-on').css('border-width', '0');
					} else {
						this.handleDiv.css('border-color', this._parentWidget.getBordercolor());
						this.handleDiv.css('border-style', this._parentWidget.getBorderstyle());
						this.handleDiv.css('border-width', this._parentWidget.getBorderwidth() + 'px');
						if (this.handleDiv.hasClass('xw-tab-handle-on') || this.handleDiv.hasClass('xw-tab-handle-disabled'))
							this.handleDiv.css('border-bottom-width', '0px');
					}
				}
			}
		}
	}
});
