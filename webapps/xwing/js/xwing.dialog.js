Class.define({
	Dialog : {
		alias : 'dialog',
		namespace : 'xwing',
		extend : xwing.widget.Widget,
		Dialog : function(json) {
			if (!arguments.length) return;
			if( json.id ){
				var preshell = jQuery(document.body).children('#'+json.id+'.xw-dialog-shell');
				if( preshell.length > 0 ){
					preshell.remove();
				}
			}
			this._init(json);
			this.state = xwing.Dialog.STATE_INIT;
			/*debug Xwing.debug("Dialog("+this.alias+") created : "+ this.getId() +", " + xwing.util.Util.obj2json(json)); */
		},
		statics : {
			create : function(json) {
				return new xwing.Dialog(json);
			},
			INITIAL_Z : 160000,
			STATE_INIT : 0,
			STATE_OPEN : 1,
			STATE_MIN : 2,
			STATE_NORMAL : 3,
			STATE_MAX : 4,
			STATE_CLOSED : -1,
			activeWin : null,
			wins : [],
			alert : function(msg, title, callback) {
				var opt = {};
				opt.id = 'dialog_alert';
				opt.width = 300;
				opt.height = 120;
				opt.resizable = false;
				opt.modal = true;
				opt.title = title ? title : 'Alert';
				var win = new xwing.Dialog(opt);
				var callbackFunc = callback;
				if (typeof (callback) == "string") {
					callbackFunc = eval(callback);
				}

				win.addButton("OK", function() {
					if (callbackFunc)
						callbackFunc.call(win);
					win.close(true);
				});
				var panel = win.getContentPanel();
				panel.append(jQuery("<span class='xw-dialog-alert'/>").html(msg));
				win.open();
				
				setTimeout(function(){
					var height = panel.find('span.xw-dialog-alert')[0].clientHeight + 70;
					win._opt.height = height;
					win._getJShell().height(height + "px");
				},0);
				
			},
			confirm : function(msg, title, callback) {
				var opt = {};
				opt.id = 'dialog_confirm';
				opt.width = 300;
				opt.height = 120;
				opt.resizable = false;
				opt.modal = true;
				opt.title = title ? title : 'Confirm';
				var win = new xwing.Dialog(opt);
				var callbackFunc = callback;
				if (typeof (callback) == "string") {
					callbackFunc = eval(callback);
				}

				win.addButton("OK", function() {
					callbackFunc.call(win, true);
					win.close(true);
				});
				win.addButton("Cancel", function() {
					callbackFunc.call(win, false);
					win.close(true);
				});
				var panel = win.getContentPanel();
				panel.append(jQuery("<span class='xw-dialog-confirm'/>").html(msg));
				win.open();
				
//				setTimeout(function(){
//					var height = panel.find('span.xw-dialog-confirm')[0].clientHeight + 70;
//					Xwing.log(height);
//					win.setHeight(height);
//				},0);
			},
			prompt : function() { },
			wait : function() { }
		},
		prototypes : {
			_createPart : function() {
				var thisObj = this;
				this._iframeLoad = false;
				this._getJShell().appendTo(document.body).css('display', 'none');
				this._getJShell().css("position", "absolute");
				this.win = jQuery('<div class="xw-dialog"/>');
				this._getJShell().append(this.win);

				this.head = jQuery('<div class="xw-dialog-area-head"/>').appendTo(this.win);
				this.title = jQuery('<span class="xw-dialog-title"/>').appendTo(this.head);
				this.icons = jQuery("<div class='xw-dialog-icons'/>").appendTo(this.head);
				this.minIcon = jQuery("<div class='xw-dialog-icon xw-dialog-icon-min'/>");
				if( this.getMinicon() ) this.minIcon.appendTo(this.head);
				this.toogleIcon = jQuery('<div class="xw-dialog-icon xw-dialog-icon-toogle xw-dialog-icon-max"/>');
				if( this.getToogleicon() ) this.toogleIcon.appendTo(this.icons);
				this.closeIcon = jQuery('<div class="xw-dialog-icon xw-dialog-icon-close" />').appendTo(this.icons);

				this.body = jQuery('<div class="xw-dialog-area-body"/>').appendTo(this.win);
				this.content = jQuery('<div class="xw-dialog-content"/>').appendTo(this.body);
				this.buttonbar = jQuery('<div class="xw-dialog-buttonbar"/>').appendTo(this.win);

				
				this.minIcon.bind('click', function() {
					if (thisObj.state != xwing.Dialog.STATE_CLOSED) {
						if (thisObj.state == xwing.Dialog.STATE_MIN) {
							thisObj.restore();
						} else {
							thisObj.minimize();
						}
					}
				});
				this.closeIcon.bind('click', function() {
					if( thisObj.getId() == 'dialog_alert' || thisObj.getId() == 'dialog_confirm' ) thisObj.close(true);
					else thisObj.close();
				});
				this.toogleIcon.bind('click', function() {
					if (thisObj.state != xwing.Dialog.STATE_CLOSED) {
						if (thisObj.state == xwing.Dialog.STATE_MIN || thisObj.state == xwing.Dialog.STATE_NORMAL) {
							thisObj.maximize();
						} else {
							thisObj.restore();
						}
					}
				});
				this.head.bind('dblclick', function() {
					if (thisObj.state != xwing.Dialog.STATE_CLOSED) {
						if (thisObj.state != xwing.Dialog.STATE_MAX) {
							thisObj.maximize();
						} else {
							thisObj.restore();
						}
					}
				});
				this.head.bind('mousedown', function(event) {
					if (event.target == thisObj.closeIcon[0])
						return;
					thisObj.activate();
				});

				window._param = window._param || {};
				window._param[this.getId()] = this._opt.param;
			},
			_render : function() {
				this._doTitle();
				this._doBounds();
				this._doButtonbarheight();
				this._doDraggable();
				this._doResizable();
				this._doModal();
				this._doUrl();
			},
			_doModal : function() {
				if (this.getModal()) {
					var thisObj = this;
					this.overlay = jQuery("<div class='xw-dialog-overlay'/>").appendTo(jQuery(document.body));
					this.overlay.height(jQuery(document).height());
					this.overlay.css({
						"background-color" : this._opt.overlaycolor || '#fff',
						"opacity" : this._opt.overlayopacity || 0.5
					});
					this.minIcon.css('display', 'none');
				}
			},
			setModal : function(v) {
				this._opt.modal = v;
			},
			getModal : function() {
				return xwing.Util.parseBoolean(this._opt.modal);
			},
			_doBounds : function() {
				var shell = this._getJShell();
				shell.width(this.getWidth() + "px");
				shell.height(this.getHeight() + "px");

				var top = this.getTop();
				if (top == -1) {
					top = xwing.Util.parseInt(jQuery(document.body).scrollTop()) + ((xwing.Util.parseInt(jQuery(window).height()) - this._getJShell().outerHeight()) / 2);
				}
				var left = this.getLeft();
				if (left == -1) {
					left = xwing.Util.parseInt(jQuery(document.body).scrollLeft()) + ((xwing.Util.parseInt(jQuery(window).width()) - this._getJShell().outerWidth()) / 2);

				}
				if (xwing.Dialog.activeWin) {
					var activeOffset = xwing.Dialog.activeWin._getJShell().offset();
					if (top == xwing.Util.parseInt(activeOffset.top) || left == xwing.Util.parseInt(activeOffset.left)) {
						top = xwing.Util.parseInt(activeOffset.top) + 20;
						left = xwing.Util.parseInt(activeOffset.left) + 20;
						if (top >= xwing.Util.parseInt(jQuery(window).height()) || left >= xwing.Util.parseInt(jQuery(window).width())) {
							top = 10;
							left = 10;
						}
					}
				}

				shell.css({
					"left" : left,
					"top" : top
				});
			},
			setButtonbarheight : function(h) {
				this._opt.buttonbarheight = h;
			},
			_doButtonbarheight : function() {
				this.buttonbar.height(this.getButtonbarheight());
				this.buttonbar.css('display', 'none');
				this.body.css("padding-bottom", "10px");
			},
			getButtonbarheight : function() {
				return xwing.Util.parseInt(this._opt.buttonbarheight);
			},
			_doUrl : function() {
				if (this._opt.url) {
					var indicator = this.content.find(".xw-dialog-indicator");
					if (indicator.size() <= 0) {
						indicator = jQuery("<div class='xw-dialog-indicator'/>").append(
								jQuery("<div/>").append(jQuery("<span class='xw-dialog-indicator-icon' />")).append(jQuery('<br/>')).append(
										jQuery("<span class='xw-dialog-indicator-text' />").text("please wait while loading..."))).appendTo(this.content);
					} else {
						indicator.css('display', 'block');
					}

					var iframe = this.content.find('iframe');

					if (iframe.size() <= 0) {
						iframe = jQuery('<iframe tabindex="-1" width="100%" height="100%" frameBorder="0" class="xw-dialog-iframe" id="if::' + this.getId() + '" name="if::' + this.getId() + '" />');
						iframe.attr('src', this._opt.url);
						iframe.appendTo(this.content);
						var thisObj = this;
						iframe.bind('load', function() {
							thisObj._iframeLoad = true;
							jQuery(this).css('display', 'block');
							jQuery(indicator).css('display', 'none');
						});
						iframe.bind('error', function() {
							alert("error");
						});
					} else {
						iframe.css('display', 'none');
					}
					iframe.css('overflow', 'hidden');
				}
			},
			open : function(opt) {
				if( opt ){
					opt['width'] && (this._opt.width = opt['width']);
					opt['height'] && (this._opt.height = opt['height']);
					opt['left'] && (this._opt.left = opt['left']);
					opt['top'] && (this._opt.top = opt['top']);
					
					this._doBounds();
						this._opt.param = opt['param'];
						window._param[this.getId()] = opt['param'];
				}
				this.activate();
				this.body.css('display','block');
				this._getJShell().css('display', 'block');
				
				if( this.content.find('iframe') && Xwing.children[this.getId()]){
					Xwing.children[this.getId()].Xwing.param = this._opt.param;
					var pageLoad = Xwing.children[this.getId()]._pageLoad;
					if (pageLoad && this._iframeLoad) {
						try {
							var funcObj = pageLoad.func;
							if (typeof (funcObj) != "function") {
								funcObj = eval("Xwing.children['"+this.getId()+"']."+funcObj);
							}
							var event = {
								type : 'load',
								source : pageLoad
							};
							if (pageLoad.obj) {
								funcObj.call(pageLoad.obj, event);
							} else {
								funcObj.call(pageLoad.obj, event);
							}	
						} catch (e) {
							/*debug Xwing.debug("event fire call error : widget(page) event type:load,"+funcObj.func + "\n"+ e); */	
//							return false;
						}
					}
				}
				xwing.Dialog.wins.push(this);
				this.state = xwing.Dialog.STATE_NORMAL;
			},
			_doDraggable : function() {
				if (this.getDraggable()) {
					var thisObj = this;
					this._getJShell().draggable({
						opacity : '0.2',
						handle : this.head,
						cursor : 'move',
						helper : function() {
							return jQuery("<div/>").css({
								"width" : thisObj._getJShell().outerWidth(),
								"height" : thisObj._getJShell().outerHeight(),
								"z-index" : thisObj._getJShell().css("z-index")
							}).addClass("xw-dialog-helper").appendTo(jQuery(document.documentElement));
						},
						stop : function(event, ui) {
							/*debug Xwing.debug(ui.offset.left); */
							thisObj._getJShell().css({
								left : ui.offset.left,
								top : ui.offset.top
							});
						}
					});
				} else {
					try{this._getJShell().draggable("destroy");}catch(e){}
				}
			},
			setDraggable : function(v) {
				this._opt.draggable = v;
			},
			getDraggable : function() {
				return xwing.Util.parseBoolean(this._opt.draggable);
			},
			_doResizable : function() {
				if (this.getResizable()) {
					var thisObj = this;
					this._getJShell().resizable({
						start : function(event, ui) {
							thisObj.setEnabled(false);
						},
						stop : function(event, ui) {
							thisObj.setEnabled(true);
						}
					});
				} else {
						try{this._getJShell().resizable("destroy");}catch(e){}
				}
			},
			setResizable : function(v) {
				this._opt.resizable = v;
			},
			getResizable : function() {
				return xwing.Util.parseBoolean(this._opt.resizable);
			},
			close : function(basic) {
				for ( var i = 0; i < xwing.Dialog.wins.length; i++) {
					if (this == xwing.Dialog.wins[i]) {
						xwing.Dialog.wins.splice(i, 1);
						break;
					}
				}

				if (xwing.Dialog.activeWin == this) {
					if (xwing.Dialog.wins.length > 0) {
						xwing.Dialog.wins[xwing.Dialog.wins.length - 1].activate();
					} else {
						xwing.Dialog.activeWin = null;
					}
				}
				if (this.overlay) {
					this.overlay.remove();
					this.overlay = null;
				}
				
				if( (jQuery.browser.msie || (jQuery.browser.mozilla && parseFloat(jQuery.browser.version) >= 11)) && window)
					window.focus();
					
				if( basic ) this._getJShell().css('display', 'none').remove();
				else this._getJShell().css('display', 'none');
				this.state = xwing.Dialog.CLOSED;
				
				// unload event 만들�?
				this._fire('close',  {
						type : 'close',
						source : this,
						event : null
					});
				
			},
			getZ : function() {
				return parseFloat(this._getJShell().css("z-index"));
			},
			_doZ : function(z) {
				if (this.overlay) {
					z = z < xwing.Dialog.INITIAL_Z ? xwing.Dialog.INITIAL_Z : z;
					this.overlay.css("z-index", z);
					z++;
				}
				this._getJShell().css("z-index", z < xwing.Dialog.INITIAL_Z ? xwing.Dialog.INITIAL_Z : z);
			},
			activate : function() {
				if (xwing.Dialog.activeWin == this)
					return;
				for ( var i = 0; i < xwing.Dialog.wins.length; i++) {
					xwing.Dialog.wins[i].inactivate();
				}
				var z = xwing.Dialog.INITIAL_Z;
				if (xwing.Dialog.activeWin) {
					z = xwing.Dialog.activeWin.getZ() + 1;
				} else if (xwing.Dialog.wins.length > 0) {
					z = xwing.Dialog.wins[xwing.Dialog.wins.length - 1].getZ() + 1;
				}
				this._doZ(z);
				xwing.Dialog.activeWin = this;
				this._getJShell().removeClass("xw-dialog-shell-inactive").addClass("xw-dialog-shell-active");
				this.win.removeClass("xw-dialog-inactive").addClass("xw-dialog-active");
				this.head.removeClass("xw-dialog-area-head-inactive").addClass("xw-dialog-area-head-active");
			},
			inactivate : function() {
				this._getJShell().removeClass("xw-dialog-shell-active").addClass("xw-dialog-shell-inactive");
				this.win.removeClass("xw-dialog-active").addClass("xw-dialog-inactive");
				this.head.removeClass("xw-dialog-area-head-active").addClass("xw-dialog-area-head-inactive");
			},
			setTitle : function(title) {
				this._opt.title = title;
			},
			_doTitle : function() {
				this.title.text(this._opt.title);
			},
			getTitle : function() {
				return this._opt.title;
			},
			addButton : function(text, onclick) {
				var button = new xwing.widget.Button({
					value : text,
					click : onclick
				});
				this.buttonbar.append(button._getJShell());
				button._getJShell().css("display", "inline-block").css("position", "static");
				this.body.css("padding-bottom", (this.getButtonbarheight() + 10) + "px");
				this.buttonbar.css('display', 'block');
			},
			maximize : function() {
				if (this.state == xwing.Dialog.STATE_MAX)
					return;

				if (this.state == xwing.Dialog.STATE_NORMAL) {
					this.preTop = this._getJShell().offset().top;
					this.preLeft = this._getJShell().offset().left;
					this.preWidth = this._getJShell().width();
					this.preHeight = this._getJShell().height();
				}
				var maxTop = parseInt(jQuery(document).scrollTop());
				var maxLeft = parseInt(jQuery(document).scrollLeft());
				var maxWidth = parseInt(jQuery(window).width());
				var maxHeight = parseInt(jQuery(window).height());

				if (this.getAnimation()) {
					var dummy = jQuery('<div/>').width(maxWidth).height(maxHeight).css({
						'position' : 'absolute',
						'top' : maxTop,
						'left' : maxLeft
					}).appendTo(jQuery(document.body));

					this._getJShell().effect("transfer", {
						to : dummy,
						className : 'xw-dialog-transferer'
					}, 400);
					
					dummy.detach();
				}
				this._getJShell().css({
					'position' : 'absolute',
					'top' : maxTop,
					'left' : maxLeft
				});
				this._getJShell().height("100%");
				this._getJShell().width("100%");
				if (this.state == xwing.Dialog.STATE_MIN) {
					this.minIcon.removeClass("xw-dialog-icon-restore").addClass("xw-dialog-icon-min");
					this.buttonbar.css('display', 'block');
					this.body.css('display', 'block');
				}
				this.toogleIcon.removeClass("xw-dialog-icon-max").addClass("xw-dialog-icon-restore");
				dummy && dummy.remove();
				this._getJShell().draggable("disable");
				this.state = xwing.Dialog.STATE_MAX;
			},
			minimize : function() {
				if (this.state == xwing.Dialog.STATE_MIN)
					return;

				this.preTop = this._getJShell().offset().top;
				this.preLeft = this._getJShell().offset().left;
				this.preWidth = this._getJShell().width();
				this.preHeight = this._getJShell().height();

				this.buttonbar.css('display', 'none');
				this.body.css('display', 'none');
				var minWidth = 150;
				var minHeight = 35;
				if (this.getAnimation()) {
					var dummy = jQuery('<div/>').width(minWidth).height(minHeight).css({
						'position' : 'absolute',
						'top' : this.preTop,
						'left' : this.preLeft
					}).appendTo(jQuery(document.body));

					this._getJShell().effect("transfer", {
						to : dummy,
						className : 'xw-dialog-transferer'
					}, 400);
				}
				this._getJShell().height(minHeight);
				this._getJShell().width(minWidth);
				if (dummy)
					dummy.remove();
				this.minIcon.removeClass("xw-dialog-icon-min").addClass("xw-dialog-icon-restore");
				this.toogleIcon.removeClass("xw-dialog-icon-restore").addClass("xw-dialog-icon-max");
				if (this._opt.draggable)
					this._getJShell().draggable("enable");
				this._getJShell().resizable("disable");
				this.state = xwing.Dialog.STATE_MIN;
			},
			restore : function() {
				var top = this.preTop ? this.preTop : this.getTop();
				var left = this.preLeft ? this.preLeft : this.getLeft();
				var width = this.preWidth ? this.preWidth : this.getWidth();
				var height = this.preHeight ? this.preHeight : this.getHeight();
				if (this.state == xwing.Dialog.STATE_MAX) {
					this.toogleIcon.removeClass("xw-dialog-icon-restore").addClass("xw-dialog-icon-max");
					if (this.getAnimation()) {
						jQuery(document.body).effect("transfer", {
							to : this._getJShell(),
							className : 'xw-dialog-transferer'
						}, 400);
					}
				} else if (this.state == xwing.Dialog.STATE_MIN) {
					this.minIcon.removeClass("xw-dialog-icon-restore").addClass("xw-dialog-icon-min");
					if (this.getAnimation()) {
						var dummy = jQuery('<div/>').width(width).height(height).css({
							'position' : 'absolute',
							'top' : top,
							'left' : left
						}).appendTo(jQuery(document.body));

						this._getJShell().effect("transfer", {
							to : dummy,
							className : 'xw-dialog-transferer'
						}, 400);
					}
				}
				if (this._opt.draggable)
					this._getJShell().draggable("enable");
				if (this._opt.resizable)
					this._getJShell().resizable("enable");
				this._getJShell().css({
					'top' : top,
					'left' : left
				});
				dummy && dummy.remove();
				this._getJShell().width(width);
				this._getJShell().height(height);
				this.body.css('display', 'block');
				this.buttonbar.css('display', 'block');

				this.state = xwing.Dialog.STATE_NORMAL;
			},
			getAnimation : function() {
				return xwing.Util.parseBoolean(this._opt.animation);
			},
			setAnimation : function(v) {
				this._opt.animation = v;
			},
			getContentPanel : function() {
				return this.content;
			},getMinicon : function() {
				return xwing.Util.parseBoolean(this._opt.minicon);
			},
			setMinicon : function(v) {
				this._opt.minicon = v;
			},getToogleicon : function() {
				return xwing.Util.parseBoolean(this._opt.toogleicon);
			},
			setToogleicon : function(v) {
				this._opt.toogleicon = v;
			}
		}
	}
});