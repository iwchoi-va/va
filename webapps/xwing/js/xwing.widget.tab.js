Class.define({
	Tab : {
		alias : "tab",
		extend : xwing.widget.Widget,
		namespace : "xwing.widget",
		Tab : function(json){
			this._init(json);
			/*debug Xwing.debug("Widget(Tab) created : "); */
		},
		statics : {
			create : function(json){
				return new xwing.widget.Tab(json);
			}
		},		
		prototypes :{
			_createPart : function(){
				this._pages = [];
				this._handlewidth = 0;
				this._curright = 0;
				this._areaHandle = jQuery("<div class='xw-tab-area-handle' unselectable='on'/>");
				this._jul= jQuery("<ul/>").appendTo(this._areaHandle);
				this._areaBody = jQuery("<div class='xw-tab-area-page xw-mod-border'/>");
				this._getJShell().append(this._areaHandle).append(this._areaBody);
				this._createArrow();
				
				this.container = this._areaBody;
			},
			_createArrow : function(){
				this._jArrowDiv = jQuery("<div class='xw-tab-area-arrow' />").height(this.getHandleheight()).hide();
				var pre = jQuery("<div class='xw-tab-area-arrow-pre' />");
				var next = jQuery("<div class='xw-tab-area-arrow-next' />");
				this._areaHandle.append(this._jArrowDiv.append(pre).append(next));
				
				var thisObj = this;
				this._bind(pre, 'click', function(e) {
					if (thisObj.getActive() == 0)
						return;
					var idx = thisObj.getActive() - 1;
					if (thisObj._pages[idx].getEnabled()) {
						thisObj.activate(idx);
					} else {
						thisObj.activate(idx - 1);
					}
				});
				this._bind(next, 'click', function(e) {
					if (thisObj.getActive() == (this._pages.length - 1))
						return;
					var idx = this.getActive() + 1;
					if (thisObj._pages[idx].getEnabled()) {
						thisObj.activate(idx);
					} else {
						thisObj.activate(idx + 1);
					}
				});
			},
			_appendCompleted : function(){
				this._doFont();
				this._doDeactivefont();
				this._doBackground();
				this._doDeactivebackground();
				this._doHandlealign();
				this._doChildHeight();

				this.oldact = this._opt.active;
				if (this._pages[this._opt.active].getEnabled()) {
					this._pages[this._opt.active].activate();
					this._doFont(this._pages[this._opt.active].handleDiv);
					this._doBackground(this._pages[this._opt.active].handleDiv);
				}

				var thisObj = this;
				if (this.getMultiline()) {
					window.setTimeout(function() {
						thisObj._doMultiline();
					}, 100);
				} else {
					window.setTimeout(function() {
						thisObj._resetHandlewidth();
						thisObj._moveHandle();
					}, 100);
				}
				
				/*debug Xwing.debug('tab child append completed'); */
			},
			_getModule : function(mod) {
				var modules = jQuery(".xw-mod-" + mod + "[_xid=" + this.getXId() + "]", this.getShell());
				if ('border' == mod) {
					if (this.getActivePageWidget() && this.getActivePageWidget()._getModule(mod)) {
						modules = modules.add(this.getActivePageWidget()._getModule(mod));
					}
				}
				return modules;
			},
			appendChild : function(child) {
				if (!this.isAppendable(child))
					return;
				
				child.setParentWidget(this);

				this._pages.push(child);
				this._jul.append(child.getHandle());
				this._areaBody.append(child.getPage());
				child._doEnabled();
				
				var thisTab = this;
				jQuery(child.getHandle()).bind('click', function() {
					for ( var i = 0, l = thisTab._pages.length; i < l; i++) {
						if (this == thisTab._pages[i].getHandle() && thisTab._pages[i].getEnabled()) {
							thisTab.activate(i);
							thisTab._jul.width(this._handlewidth + 'px');
							if (thisTab._opt.onclick)
								thisTab._opt.onclick.call(thisTab, i);
						}
					}
				});

				if (this.getHandlewidth())
					jQuery(child.getHandle()).width(this.getHandlewidth() + 'px');
				var top = xwing.Util.parseInt((this.getFontsize() - 11) / 2, 0);
				child._doAnchor();
				child._doBorder();
				child._doTextpadding();
				this._doCursor();
				return child;
			},
			addTab : function(tabpage) {
				if (tabpage) {
					this.appendChild(tabpage);
					this._doFont();
					this._doDeactivefont();
					this._doBackground();
					this._doDeactivebackground();
					this._doChildHeight();
					if (this.getMultiline()) {
						this._doMultiline();
					} else {
						this._resetHandlewidth();
						this._moveHandle();
					}
				}
			},
			insertTab : function(idx, tabpage) {
				if (tabpage && this.isAppendable(tabpage) && idx > -1 && idx < this._pages.length) {
					tabpage.setParentWidget(this);

					jQuery(this._pages[idx].getHandle()).before(tabpage.getHandle());
					this._pages[idx]._page.before(tabpage.getPage());
					
					var thisTab = this;
					jQuery(tabpage.getHandle()).bind('click', function() {
						for ( var i = 0, l = thisTab._pages.length; i < l; i++) {
							if (this == thisTab._pages[i].getHandle() && thisTab._pages[i].getEnabled()) {
								thisTab.activate(i);
								if (thisTab._opt.onclick)
									thisTab._opt.onclick.call(thisTab, i);
							}
						}
					});

					if (this.getHandlewidth())
						jQuery(tabpage.getHandle()).width(this.getHandlewidth() + 'px');
					
					var top = xwing.Util.parseInt((this.getFontsize() - 11) / 2, 0);
					tabpage._jclose.css('top', (top + 1));
					this._pages.splice(idx, 0, tabpage);
					this.oldact = -1;
					if (idx <= this.getActive())
						this._opt.active = this.getActive() + 1;
					this._doFont();
					this._doDeactivefont();
					this._doBackground();
					this._doDeactivebackground();

					tabpage._doAnchor();
					tabpage._doBorder();

					if (this.getMultiline()) {
						this._doMultiline();
					} else {
						this._resetHandlewidth();
						this._moveHandle();
					}
				}
			},
			removeTab : function(tab) {
				/*debug Xwing.debug("tab removeTab("+tab+")"); */
				var idx = -1;
				var type = 1;
				
				if (tab instanceof xwing.widget.Tabpage) {
					idx = this._pages.indexOf(tab);
				} else if (!isNaN(tab) && tab < this._pages.length && tab > 0) {
					idx = tab;
				}
				/*debug Xwing.debug("tab removeTab idx :" + idx); */
				if (idx == -1)
					return;
				var tab = this._pages[idx];
				Xwing.removeWidget(tab._opt.id);
				jQuery(tab.getHandle()).remove();
				jQuery(tab._page).remove();
				this._pages.splice(idx, 1);
				if (this.getActive() != idx) {
					if (this.getActive() > idx) {
						this.oldact = -1;
						this._opt.active = this.getActive() - 1;
					}
				} else {
					this.oldact = -1;
					if (this._pages.length == idx) {
						type = -1;
						this._opt.active = idx - 1;
					}

					while (true) {
						if (this._pages[this._opt.active].getEnabled() == true)
							break;
						else
							this._opt.active = this._opt.active + type;
						if (this._opt.active < 0 || this._opt.active == (this._pages.length - 1))
							break;
					}
				}
				if (!this.getMultiline())
					this._resetHandlewidth();
				this.activate(this._opt.active);
				if (this.getMultiline())
					this._doMultiline();
				else
					this._moveHandle();
			},
			moveTap : function(tabIdx, moveIdx) {
				if (moveIdx != tabIdx && (moveIdx > -1 && moveIdx < this._pages.length) && (tabIdx > -1 && tabIdx < this._pages.length))
					var curTab = this._pages[tabIdx];

				var curhandle = jQuery(curTab.getHandle()).detach();
				var curPage = curTab.getPage().detach();

				if (tabIdx < moveIdx) {
					jQuery(this._pages[moveIdx].getHandle()).after(curhandle);
					this._pages[moveIdx].getPage().after(curPage);
					this._pages.splice(moveIdx, 0, curTab);
					this._pages.splice(tabIdx, 1);
				} else {
					jQuery(this._pages[moveIdx].getHandle()).before(curhandle);
					this._pages[moveIdx].getPage().before(curPage);
					this._pages.splice(tabIdx, 1);
					this._pages.splice(moveIdx, 0, curTab);
				}
			},
			_doBorder : function() {
				xwing.widget.Widget.prototype._doBorder.call(this);
				if (this.getBorderstyle() == 'none') {
					this._areaHandle.find('.xw-tab-handle').css('border-color', 'transparent');
					this._areaHandle.find('.xw-tab-handle').css('border-style', this.getBorderstyle());
					this._areaHandle.find('.xw-tab-handle').css('border-width', '0');
					this._areaBody.css('margin-top', '0px');
				} else {
					this._areaHandle.find('.xw-tab-handle').css('border-color', this.getBordercolor());
					this._areaHandle.find('.xw-tab-handle').css('border-style', this.getBorderstyle());
					this._areaHandle.find('.xw-tab-handle').css('border-width', this.getBorderwidth() + 'px');
					this._areaBody.css('margin-top', -1 * this.getBorderwidth() + 'px');
				}
				this._areaHandle.find('.xw-tab-handle-on').css('border-bottom-width', '0px');
				this._doBounds();
			},
			_doFont : function(mod) {
				var act = (mod === undefined) ? this._areaHandle.find('.xw-tab-handle-on') : mod;
				if (act.length != 0) {
					act.css({
						'color' : this.getFontcolor(),
						'font-style' : this.getFontstyle(),
						'font-weight' : this.getFontweight(),
						'font-size' : this.getFontsize() + 'px',
						'text-decoration' : this.getFontdecoration()
					});
					
					try {
						if (this.getFontfamily && Xwing.config.fonts && Xwing.config.fonts[this.getFontfamily()]) {
							act.css("font-family", Xwing.config.fonts[this.getFontfamily()]);
						}
					} catch (e) {
						Xwing.error("err on " + this.getAlias() + ".doFontfamily" + +":" + e);
					}
				}
			},
			_doFontfamily : function() {
				this._doFont();
			},
			_doFontcolor : function() {
				this._doFont();
			},
			_doFontstyle : function() {
				this._doFont();
			},
			_doFontweight : function() {
				this._doFont();
			},
			_doFontsize : function() {
				this._doFont();
			},
			_doFontdecoration : function() {
				this._doFont();
			},
			setDeactivefontfamily : function(v) {
				this._opt.deactivefontfamily = v;
				this._doDeactivefont();
			},
			getDeactivefontfamily : function() {
				return this._opt.deactivefontfamily;
			},
			setDeactivefontcolor : function(v) {
				this._opt.deactivefontcolor = v;
				this._doDeactivefont();
			},
			getDeactivefontcolor : function() {
				return this._opt.deactivefontcolor;
			},
			setDeactivefontstyle : function(v) {
				this._opt.deactivefontstyle = v;
				this._doDeactivefont();
			},
			getDeactivefontstyle : function() {
				return this._opt.deactivefontstyle;
			},
			setDeactivefontweight : function(v) {
				this._opt.deactivefontweight = v;
				this._doDeactivefont();
			},
			getDeactivefontweight : function() {
				return this._opt.deactivefontweight;
			},
			setDeactivefontsize : function(v) {
				this._opt.deactivefontsize = v;
			},
			getDeactivefontsize : function() {
				return this._opt.deactivefontsize;
			},
			setDeactivefontdecoration : function(v) {
				this._opt.deactivefontdecoration = v;
				this._doDeactivefont();
			},
			getDeactivefontdecoration : function() {
				return this._opt.deactivefontdecoration;
			},
			_doDeactivefont : function(mod) {
				var deact = (mod === undefined) ? this._areaHandle.find('.xw-tab-handle-off') : mod;
				if (deact.length != 0) {
					deact.css({
						'color' : this.getDeactivefontcolor(),
						'font-style' : this.getDeactivefontstyle(),
						'font-weight' : this.getDeactivefontweight(),
						'font-size' : this.getDeactivefontsize() + 'px',
						'text-decoration' : this.getDeactivefontdecoration()
					});
					
					try {
						if (this.getDeactivefontfamily && Xwing.config.fonts && Xwing.config.fonts[this.getDeactivefontfamily()]) {
							deact.css("font-family", Xwing.config.fonts[this.getDeactivefontfamily()]);
						}
					} catch (e) {
						Xwing.error("err on " + this.getAlias() + ".doFontfamily" + +":" + e);
					}
				}
			},
			_doBackground : function(mod) {
				var act = (mod === undefined) ? this._areaHandle.find('.xw-tab-handle-on') : mod;
				if (act.length != 0) {
					act.css('background', '');
					if (this.getBggradientcolor() && this.getBgcolor()) {
						if (Xwing.isIE()) {
							var gradientType = (this.getBggradientdir() == "vertical" ? "GradientType=0" : "GradientType=1");
							act.css("filter", "progid:DXImageTransform.Microsoft.gradient(" + gradientType + ",startColorstr='" + this.getBgcolor() + "', endColorstr='" + this.getBggradientcolor() + "')");
						} else {
							if (this.getBggradientdir() == "vertical") {
								if (jQuery.browser.mozilla) {
									act.css("background-image", "-moz-linear-gradient(top, " + this.getBgcolor() + ", " + this.getBggradientcolor() + ")");
								} else if (jQuery.browser.opera) {
									act.css("background-image", "-o-linear-gradient(top, " + this.getBgcolor() + ", " + this.getBggradientcolor() + ")");
								} else if (jQuery.browser.webkit) {
									act.css("background-image", "-webkit-gradient(linear, left top, left bottom, from(" + this.getBgcolor() + "), to(" + this.getBggradientcolor() + ") )");
								}
							} else {
								if (jQuery.browser.mozilla) {
									act.css("background-image", "-moz-linear-gradient(left," + this.getBgcolor() + ", " + this.getBggradientcolor());
								} else if (jQuery.browser.opera) {
									act.css("background-image", "-o-linear-gradient(left," + this.getBgcolor() + ", " + this.getBggradientcolor());
								} else if (jQuery.browser.webkit) {
									act.css("background-image", "-webkit-gradient(linear, left top, right bottom, from(" + this.getBgcolor() + "), to(" + this.getBggradientcolor() + ") )");
								}
							}
						}
					} else {
						if (this.getBgcolor()) {
							act.css('background', this.getBgcolor());
						}
						if (this.getBgimage()) {
							act.css({
								'background-image' : this.getBgimage(),
								"background-repeat" : this.getBgimagerepeat(),
								"background-position" : this.getBgimagealign()
							});
						}
					}
				}
			},
			_doBgcolor : function() {
				this._doBackground();
			},
			_doBgimage : function() {
				this._doBackground();
			},
			_doBgimagerepeat : function() {
				this._doBackground();
			},
			_doBgimagealign : function() {
				this._doBackground();
			},
			setDeactivebgcolor : function(value) {
				this._opt.deactivebgcolor = value;
				this._doDeactivebackground();
			},
			getDeactivebgcolor : function() {
				return this._opt.deactivebgcolor;
			},
			setDeactivebggradientcolor : function(v) {
				this._opt.deactivebggradientcolor = v;
				this._doDeactivebackground();
			},
			getDeactivebggradientcolor : function() {
				return this._opt.deactivebggradientcolor;
			},
			setDeactivebggradientdir : function(v) {
				this._opt.deactivebggradientdir = v;
				this._doDeactivebackground();
			},
			getDeactivebggradientdir : function() {
				return this._opt.deactivebggradientdir;
			},
			setDeactivebgimage : function(value) {
				this._opt.deactivebgimage = value;
				this._doDeactivebackground();
			},
			getDeactivebgimage : function() {
				return this._opt.deactivebgimage;
			},
			setDeactivebgimagerepeat : function(v) {
				this._opt.deactivebgimagerepeat = v;
				this._doDeactivebackground();
			},
			getDeactivebgimagerepeat : function() {
				return this._opt.deactivebgimagerepeat;
			},
			setDeactivebgimagealign : function(v) {
				this._opt.deactivebgimagealign = v;
				this._doDeactivebackground();
			},
			getDeactivebgimagealign : function() {
				return this._opt.deactivebgimagealign;
			},
			_doDeactivebackground : function(mod) {
				var deact = (mod === undefined) ? this._areaHandle.find('.xw-tab-handle-off') : mod;
				if (deact.length != 0) {
					deact.css('background', '');
					if (this.getDeactivebggradientcolor() && this.getDeactivebgcolor()) {
						if (Xwing.isIE()) {
							var gradientType = (this.getDeactivebggradientdir() == "vertical" ? "GradientType=0" : "GradientType=1");
							deact.css("filter", "progid:DXImageTransform.Microsoft.gradient(" + gradientType + ",startColorstr='" + this.getDeactivebgcolor() + "', endColorstr='" + this.getDeactivebggradientcolor() + "')");
							deact.css("background-image", "linear-gradient(to "+(this.getDeactivebggradientdir() == "vertical" ? "top" : "left")+", "+this.getDeactivebgcolor()+", "+this.getDeactivebggradientcolor()+")");
						} else {
							if (this.getDeactivebggradientdir() == "vertical") {
								deact.css("background-image", "linear-gradient(to top, "+this.getDeactivebgcolor()+", "+this.getDeactivebggradientcolor()+")");
								if (jQuery.browser.mozilla) {
									deact.css("background-image", "-moz-linear-gradient(top, " + this.getDeactivebgcolor() + ", " + this.getDeactivebggradientcolor() + ")");
								} else if (jQuery.browser.opera) {
									deact.css("background-image", "-o-linear-gradient(top, " + this.getDeactivebgcolor() + ", " + this.getDeactivebggradientcolor() + ")");
								} else if (jQuery.browser.webkit) {
									deact.css("background-image", "-webkit-gradient(linear, left top, left bottom, from(" + this.getDeactivebgcolor() + "), to(" + this.getDeactivebggradientcolor() + ") )");
								}
							} else {
								deact.css("background-image", "linear-gradient(to left, "+this.getDeactivebgcolor()+", "+this.getDeactivebggradientcolor()+")");
								if (jQuery.browser.mozilla) {
									deact.css("background-image", "-moz-linear-gradient(left," + this.getDeactivebgcolor() + ", " + this.getDeactivebggradientcolor());
								} else if (jQuery.browser.opera) {
									deact.css("background-image", "-o-linear-gradient(left," + this.getDeactivebgcolor() + ", " + this.getDeactivebggradientcolor());
								} else if (jQuery.browser.webkit) {
									deact.css("background-image", "-webkit-gradient(linear, left top, right bottom, from(" + this.getDeactivebgcolor() + "), to(" + this.getDeactivebggradientcolor() + ") )");
								}
							}
						}
					} else {
						if (this.getDeactivebgcolor()) {
							deact.css('background', this.getDeactivebgcolor());
						}
						if (this.getDeactivebgimage()) {
							deact.css({
								'background-image' : 'url('+this.getDeactivebgimage()+')',
								"background-repeat" : this.getDeactivebgimagerepeat(),
								"background-position" : this.getDeactivebgimagealign()
							});
						}
					}
				}
			},
			_notify : function(dsEvent) {
				this.activate(this._opt.active);
			},
			setActive : function(idx) {
				this.oldact = this._opt.active;
				this._opt.active = idx;
				this._doActive();
			},
			_doActive : function() {
				this.activate(this.getActive());
			},
			activate : function(idx) {
				if (idx < 0 || idx >= this._pages.length || this.oldact == idx)
					return false;

				this.containerWidget = this._pages[idx];
				this._handlewidth = 0;
				this._curright = 0;
				
				for ( var j = 0; j < this._pages.length; j++) {
					if (idx == j) {
						this._opt.active = j;
						this.oldact = j;
						if (this._pages[j].getEnabled()) {
							this._pages[j].activate();
							this._doFont(this._pages[j].handleDiv);
							this._doBackground(this._pages[j].handleDiv);
						}
						this._curright += jQuery(this._pages[j].getHandle()).width();
					} else {
						if (this._pages[j].getEnabled()) {
							this._pages[j].deactivate();
							this._doDeactivefont(this._pages[j].handleDiv);
							this._doDeactivebackground(this._pages[j].handleDiv);
						}
						if (idx > j)
							this._curright += jQuery(this._pages[j].getHandle()).width();
					}
					this._handlewidth += jQuery(this._pages[j].getHandle()).width();
				}
				this._doHandlealign();
				this._jul.width(this._handlewidth + 'px');
				if (!this.getMultiline())
					this._moveHandle();
				
				this._fire("change", {
					type : 'change',
					source : this,
					event : null
				});
				
				return true;
			},
			getActive : function() {
				return xwing.Util.parseInt(this._opt.active);
			},
			size : function() {
				return this._pages.length;
			},
			setHandleheight : function(hh) {
				this._opt.handleheight = hh;
				this._doBounds();
			},
			getHandleheight : function() {
				return xwing.Util.parseInt(this._opt.handleheight, this.model.handleheight);
			},
			getActivePageWidget : function() {
				return this._pages[this.getActive()];
			},
			_doBounds : function() {
				/* debug Xwing.debug("xwing.tab doBoudns.."); */
				xwing.widget.Widget.prototype._doBounds.call(this);
				
				this._jArrowDiv.height(this.getHandleheight());
				this._areaHandle.height(this.getHandleheight());
				
				var bodyHeight = this.getHeight() - this.getHandleheight() + this.getBorderwidth();
				this._areaBody.height(bodyHeight);
				this._doChildHeight();
				
				if (this.getMultiline()) {
					this._doMultiline();
				} else {
					this._resetHandlewidth();
				}
			},
			_doChildHeight : function() {
				var children = this._jul.find('.xw-tab-handle');
				var thisObj = this;
				children.each(function(idx, el) {
					var height = (thisObj.getHandleheight() - xwing.Util.parseInt(this.style.paddingTop) - thisObj.getBorderwidth() * 2) + 'px';
					this.style.lineHeight = height;
					for ( var i = 0, l = this.children.length; i < l; i++)
						this.children[i].style.height = height;
				});
			},
			setWidth : function(width) {
				xwing.widget.Panel.prototype.setWidth.call(this, width);
			},
			setHeight : function(height) {
				xwing.widget.Panel.prototype.setHeight.call(this, height);
			},
			_doCursor : function() {
				if (window.xwingIDE)
					return;
				var handle = jQuery('li div', this._areaHandle);
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				handle.css('cursor', cursor);
			},
			setHandlewidth : function(v){
				this._opt.handlewidth = v;
				this._doHandlewidth();
			},
			getHandlewidth : function(){
				return this._opt.handlewidth;
			},
			_doHandlewidth : function() {
				if (this.getHandlewidth()) {
					this._areaHandle.find('.xw-tab-handle').css('width', this.getHandlewidth() + 'px');
				} else {
					this._areaHandle.find('.xw-tab-handle').css('width', '');
				}

				if (this.getMultiline()) {
					this._doMultiline();
				} else {
					this._resetHandlewidth();
					this._moveHandle();
				}
			},
			setHandlealign : function(v) {
				this._opt.handlealign = v;
				this._doHandlealign();
			},
			getHandlealign : function() {
				return this._opt.handlealign;
			},
			_doHandlealign : function() {
				this._jul.css('float', this.getHandlealign());
			},
			setMultiline : function(v) {
				this._opt.multiline = v;
				this._doMultiline();
			},
			getMultiline : function() {
				return xwing.Util.parseBoolean(this._opt.multiline, false);
			},
			_doMultiline : function() {
				var uls = jQuery('ul', this._areaHandle);
				if (this.getMultiline()) {
					var totwidth = 0;
					var line = 1;
					var tot_line = parseInt(this._handlewidth / (this.getWidth() - 10)) + 1;
					var totheight = this.getHandleheight();
					var top = 0;
					this._jArrowDiv.hide();
					uls.css({
						width : '',
						left : '',
						position : 'absolute'
					});
					for ( var i = 0; i < this._pages.length; i++) {
						var handle = jQuery(this._pages[i].getHandle());
						var ul;
						totwidth += handle.width();
						if (totwidth > (this.getWidth() - 10)) {
							totwidth = handle.width();
							totheight += this.getHandleheight();
							line++;
						}
						handle = handle.detach();
						if (uls.length >= line) {
							ul = jQuery(uls[uls.length - line]);
						} else {
							ul = jQuery("<ul class='xw-mod' style='position:absolute;' />").prependTo(this._areaHandle);
							uls = jQuery('ul', this._areaHandle);
						}
						ul.append(handle);
					}

					if (uls.length > line) {
						delete jQuery('ul:lt(1)', this._areaHandle).remove();
						uls = jQuery('ul', this._areaHandle);
					}

					var thisObj = this;
					var curline = line;
					uls.each(function(idx, e) {
						jQuery(this).css('top', thisObj.getHandleheight() * idx);
						jQuery(this).height(thisObj.getHandleheight() * curline + 'px');
						curline--;
					});
					this._areaHandle.height(totheight);
					var bodyHeight = this.getHeight() - totheight + this.getBorderwidth();
					this._areaBody.height(bodyHeight);
				} else {
					// ul 제거 및 append
					uls.not(this._jul[0]).detach();
					this._jul.children().detach();
					this._jul.css('position', '').css('top', '').height('');
					for ( var i = 0; i < this._pages.length; i++) {
						var child = this._pages[i];
						this._jul.append(child.getHandle());
					}
					if (this._opt.active == this._pages.length - 1)
						this.activate(this._opt.active);
					this._areaHandle.height(this.getHandleheight());
					var bodyHeight = this.getHeight() - this.getHandleheight() + this.getBorderwidth();
					this._areaBody.height(bodyHeight);
					this._resetHandlewidth();
					this._moveHandle();
				}
			},
			_resetHandlewidth : function() {
				this._handlewidth = 0;
				this._curright = 0;
				this._jul.width(this.getWidth() + 'px');
				for ( var i = 0; i < this._pages.length; i++) {
					var child = this._pages[i];
					var width = jQuery(child.getHandle()).width();
					if( child.getHandle().scrollWidth && (width < parseInt(child.getHandle().scrollWidth)) ) 
						width = parseInt(child.getHandle().scrollWidth);
					this._handlewidth += width;
					if (i <= this.getActive())
						this._curright += jQuery(child.getHandle()).width();
				}
				if (this._pages.length != 0 && this._handlewidth == 0) {
					var thisObj = this;
					window.setTimeout(function() {
						thisObj._resetHandlewidth();
						thisObj._moveHandle();
					}, 100);
					return;
				}
				this._jul.width(this._handlewidth + 'px');
				// when overflow
				if (this._handlewidth > (this.getWidth() - 10)) {
					this._jArrowDiv.show();
					if (this.getHandlealign() == 'right')
						this._jul.css('float', 'left');
					this._jArrowDiv.css('left', (this.getWidth() - 26));
					this._jArrowDiv.css('border-bottom-width', this.getBorderwidth());
					this._jArrowDiv.css('border-bottom-style', this.getBorderstyle());
					this._jArrowDiv.css('border-bottom-color', this.getBordercolor());
				} else {
					this._jul.css('float', this.getHandlealign());
					this._jArrowDiv.hide();
					this._jul.css('left', 0);
				}
			},
			_moveHandle : function() {
				if (this._handlewidth <= (this.getWidth() - 10))
					return;
				var width = this.getWidth() - 26 - 5;
				var left = parseInt(this._jul.css('left')) * -1;
				var right = left + width;
				var _curleft = this._curright - jQuery(this._pages[this.getActive()].getHandle()).width();
				if (left > _curleft || right < this._curright) {
					if (left > _curleft)
						this._jul.css('left', _curleft * -1);
					else if (this._curright <= width)
						this._jul.css('left', 0);
					else
						this._jul.css('left', (width - this._curright) + 'px');
				} else if (right > this._handlewidth) {
					this._jul.css('left', (this._handlewidth - width) * -1 + 'px');
				}
			},
			getTabPageIndex : function(obj){
				var tabPage = obj, idx = -1;
				if(!isNaN(obj)){
					tabPage = this._pages[obj];
				}else if(typeof obj == 'string'){
					tabPage = Xwing.getWidget(obj);
				}
				
				for(var i=0; i < this._pages.length ; i++){
					if(this._pages[i] == tabPage){
						idx = i;
						break;
					}
				}
				return idx;
			}
		}
	}
});
