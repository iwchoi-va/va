Class.define({
	Widget : {
		alias : 'widget',
		namespace : 'xwing.widget',
		Widget : function(json){
			if (!arguments.length) return;
			this._init(json);
			/*debug Xwing.debug("Widget("+this.alias+") created : "+ this.getId() +", " + xwing.util.Util.obj2json(json)); */
		},
		statics : {
			_EVENT_COMM : [ "click", "mouseenter", "mousemove", "mouseleave", "focusin", "focusout", "dblclick" ],
			_STYLE_NONE : [ "id", "styles", "left", "top", "value" ],
			_parseXJson : function(xNode){
				var json = {};
				if(xNode.attributes){
					for ( var i = 0, l = xNode.attributes.length; i < l; i++) {
						var item = xNode.attributes[i];
						/*debug Xwing.debug(item); */
						if (item.name && item.value) {
							json[item.name] = item.value;
						}
					}
				}
				return json;
			},
			showIndicator : function(msg, target){
				target = target ? target._getJShell() : jQuery('.xw-page');
				var w = target.width();
				var h = target.height();
				var x = target.offset().left;
				var y = target.offset().top;

				if (target[0]._indicator) {
					target[0]._indicator.find('span').text(msg);
				} else {
					var indicator = jQuery("<div class='xw-indicator'/>").append(jQuery("<div/>")).
						append(jQuery("<span/>").append(msg || "please wait...")).
						appendTo(jQuery(document.body));
					
					indicator.css({"left": x, "top":y});
					indicator.width(w);
					indicator.height(h);					
					target[0]._indicator = indicator;
				}
			},
			hideIndicator : function(target){
				target = target ? target._getJShell() : jQuery('.xw-page');
				if (target[0]._indicator) {
					target[0]._indicator.remove();
					target[0]._indicator = "";
				}
			}
		},
		prototypes : {
			_init : function(json){
				/*startwatch this.getAlias() + "._init" */
				
				this._listeners = [];
				this.model = Class.getClass(this.getAlias()).getModel();
				this._json = json;
				this._opt = {};
				this._attr = {};				
				this._anchoroffset = [];
				
				//keep the original attributes before using.
				jQuery.extend(this._attr, json || {});				 				
				jQuery.extend(this._opt,  json || {});
				
				this._initOption();
				if (this._opt._xid == undefined) this._opt._xid = this._opt.id;
				this.shell = this._createShell();
				
				this._opt.styles && this._initStyleEvent();
				this._initEvent();					
				
				this.container = this.shell;
				this._createPart();
				this._branding();
				this._render();
				this.containerWidget = this;
				Xwing.addWidget(this._opt.id, this);
				
				if (this._opt._xid && window.xwingIDE && window.xwingIDE.addWidget){
					xwingIDE.addWidget(this._opt._xid, this);
				}	
				
				/*stopwatch this.getAlias() + "._init" */
			},
			_getDefault : function(key){
				if (key && this.model.attributes[key]) {
					var style = (Xwing.config.customStyle && Xwing.config.customStyle[Xwing.config.custom]) ?  Xwing.config.customStyle[Xwing.config.custom] : Xwing.config.style
					  , model = Xwing.config.model;
					if (style) {
						if (this._opt.styles && style.classes) {
							if (style.classes[this._opt.styles] && style.classes[this._opt.styles][key]) {
								return ((key == 'bgimage' || key == 'icon')? Xwing.XWING_ROOT : '') + style.classes[this._opt.styles][key];
//								return style.classes[this._opt.styles][key];
							}
						}

						if (style.widgets) {
							if (style.widgets["all"] && style.widgets["all"][key]) {
								return ((key == 'bgimage' || key == 'icon') ? Xwing.XWING_ROOT : '') + style.widgets["all"][key];
//								return style.widgets["all"][key];
							} else if (style.widgets[this._area+"_"+this.getAlias().replace(/-/,"_")] && style.widgets[this._area+"_"+this.getAlias().replace(/-/,"_")][key]) {
								return ((key == 'bgimage' || key == 'icon') ? Xwing.XWING_ROOT : '') + style.widgets[this._area+"_"+this.getAlias().replace(/-/,"_")][key];
//								return style.widgets[this._area+"_"+this.getAlias().replace(/-/,"_")][key];
							} else if (style.widgets[this.getAlias().replace(/-/,"_")] && style.widgets[this.getAlias().replace(/-/,"_")][key]) {
								return ((key == 'bgimage' || key == 'icon') ? Xwing.XWING_ROOT : '') + style.widgets[this.getAlias().replace(/-/,"_")][key];
//								return style.widgets[this.getAlias().replace(/-/,"_")][key];
							}
						}
					}
					if(model && model["all"] && model["all"][key]) 
						return ((key == 'bgimage' || key == 'icon') ? Xwing.XWING_ROOT : '') + model["all"][key];
//						return model["all"][key];
					else if(model && model[this.getAlias()] &&  model[this.getAlias()][key])
						return ((key == 'bgimage' || key == 'icon') ? Xwing.XWING_ROOT : '') + model[this.getAlias()][key];
//						return model[this.getAlias()][key];
					
					return this.model.attributes[key].defaultValue;
				}
			},
			_initOption : function(){
				for ( var key in this.model.attributes) {
					this._opt[key] == undefined && (this._opt[key] = this._getDefault(key));
					
					/*debug
					var valueDomain = this.model.attributes[key].valueDomain;
					if (valueDomain && this._opt[key] != null) {
						var regExp = new RegExp(valueDomain);
						if (!regExp.test(this._opt[key])) {
							Xwing.warn("<xwing:" + this.getAlias() + " id=" + this.getId() + ">'s attribute, " + key + " has set '" + this._opt[key] + "', but not allowed.");
						}
					}
					*/
				} 
			},
			_createShell : function(){
				this.$ = jQuery("<div/>").addClass("xw-" + this.getAlias() + "-shell").attr('tabindex', '-1');
				this.shell = this.$[0];
				return this.shell;			
			},
			_branding : function(){
				this._getJShell().attr("id", this.getId()).addClass("xw-shell");
				if (this._opt._xid) {
					this._getJShell().attr("_xid", this._opt._xid);
					jQuery("*", this.getShell()).attr("_xid", this.getXId());
				} else {
					this._getJShell().attr("_xid", this.getId());
					jQuery("*", this.getShell()).attr("_xid", this.getId());
				}
				jQuery("*", this.getShell()).addClass("xw-mod");
			},
			getModel : function(){
				return this.model;
			},
			getId : function(){
				return this._opt.id;
			},
			getShell : function(){
				return this.shell;
			},
			_getJShell : function(){
				if(this.$) return this.$;
				else return jQuery(this.shell);
			},
			getContainer : function(){
				return this.container;				
			},
			hasAttribute : function(key){
				if (this.model.attributes[key]) {
					return true;
				} else {
					/*debug Xwing.debug("["+key+"] No such  attribute defined in " + this.getAlias() +"'s model"); */
					return false;
				}
			},
			setAttribute : function(key, value) {
				if (!key || !this.hasAttribute(key))
					return false;
				
				this._attr[key] = value;
				var funcName = "set" + key.replace(/^\w/, key.match(/^\w/)[0].toUpperCase());
				
				if (this[funcName]) {
					this[funcName](value);
				} else {
					/*debug Xwing.debug("widget's setAttribute("+ key +", "+value +") but No "+ funcName +" in "+this.getAlias()); */
				}
			},
			getAttribute : function(key) {
				if (!key) return;
				return this._opt[key];
			},
			removeAttribute : function(key) {
				if (!key || !this._opt[key]) return;
				
				delete this._attr[key];				
				var oldValue = this._opt[key];
				
				if (this.model.attributes[key]) {
					this.setAttribute(key, this._getDefault(key));
				} else {
					this.setAttribute(key, null);
				}
				
				return oldValue;
			},
			getAttributes : function(){
				return this._opt;
			},
			appendChild : function(child){
				if (this.isAppendable(child)) {
					var childShell = null;
					
					if (child instanceof xwing.widget.Widget) {
						childShell = child.getShell();
					} else {
						childShell = child;
					}
					
					var conWidget = this.getContainerWidget();
					var container = conWidget.getContainer();
					container.appendChild(childShell);
					child.setParentWidget(this);					
					return true;
				} 
				
				return false;				 
			},
			insertChild : function(child, idx){
				if (idx < 0) {
					return this.appendChild(child);
				} else {
					if (this.isAppendable(child)) {
						var childShell = null;
						
						if (child instanceof xwing.widget.Widget) {
							childShell = child.getShell();
						} else {
							childShell = child;
						}
						
						var conWidget = this.getContainerWidget();
						var container = conWidget.getContainer();
						var children = jQuery(container).children('.xw-shell');
						if (children.size() <= idx) {
							return this.appendChild(child);
						} else {
							var sibling = children[idx];
							container.insertBefore(childShell, sibling);
							child.setParentWidget(this);
						}
						return true;
					} 
					
					return false;					
				} 
			},
			_appendCompleted : function(){

			},
			insertBefore : function(el, rel){
				this.getContainer().insertBefore(el.getShell(), rel.getShell());
			},
			removeChild : function(el){
				this.getContainer().removeChild(el.getShell());
			},
			remove : function(){
				this._getJShell().remove();
				this.disconnectDataset && this.disconnectDataset("binddataset");
				this.disconnectDataset && this.disconnectDataset("domaindataset");
				Xwing.removeWidget(this._opt.id);
				if(this._opt._xid && window.xwingIDE && xwingIDE.addWidget){
					xwingIDE.removeWidget(this._opt._xid, this);
				}
			},
			isAppendable : function(child){
				if(child.getAlias().indexOf(this.getAlias()+"-") == 0) return true;
				else return false;
			},
			getContainerWidget : function(){
				return this.containerWidget;
			},
			getAlias : function(){
				return this.alias;
			},
			getXId : function(){
				return this._opt._xid;
			},
			_render : function(){
				this._doTabindex();
				this._doBounds();
				this._doFont();
				this._doBackground();
				this._doBorder();
				this._doEnabled();
				this._doVisible();
				this._doTextpadding();
				this._doCursor();
				this._doTooltiptext();
				this._doOpacity();
				this._doShadow();
				this._doDraggable();
				this._doDroppable();
			},
			_doBounds : function(){			
				this._getJShell().css({
					"left" : this.getLeft() + "px",
					"top" : this.getTop() + "px",
					"width" : (this.getWidth() < 0 ? 0 : this.getWidth()) + "px",
					"height" : (this.getHeight() < 0 ? 0 : this.getHeight()) + "px"
				});
			},
			setWidth : function(width){
				var oldvalue = this._opt.width;
				if(arguments.length){
					this._opt.width = width;
				}
				this._doBounds();
				if (this.getAlias() != 'page') {
					this.calcAnchorOffset("right left");
					this.fireAnchorResizeEvent(width, this.getHeight());
				}
				if (oldvalue != width) {
					this._fireResizeEvent();
				}				
			},
			getWidth : function(){
				return xwing.Util.parseInt(this._opt.width, 0);				
			},
			setHeight : function(height){
				var oldvalue = this._opt.height;
				if(arguments.length){
					this._opt.height = height;
				}
				this._doBounds();
				if (this.getAlias() != 'page') {
					this.calcAnchorOffset("top bottom");
					this.fireAnchorResizeEvent(this.getWidth(), height);
				}
				if (oldvalue != height) {
					this._fireResizeEvent();
				}
			},
			getHeight : function(){
				return xwing.Util.parseInt(this._opt.height, 0);
			},
			setLeft : function(l){
				if(arguments.length){
					this._opt.left = l;
				}
				this._doBounds();
				if (this.getAlias() != 'page') {
					this.calcAnchorOffset("right left");
				}
			},
			getLeft : function(){
				return xwing.Util.parseInt(this._opt.left, 0);
			},
			setTop : function(t){
				if(arguments.length){
					this._opt.top = t;
				}
				this._doBounds();
				if (this.getAlias() != 'page') {
					this.calcAnchorOffset("top bottom");
				}
			},
			getTop : function(){
				return xwing.Util.parseInt(this._opt.top, 0);
			},
			setEnabled : function(flag){
				this._opt.enabled = flag;	
				this._doEnabled();
			},
			_doEnabled : function(){
				this._getJShell().children(".xw-disabled").remove();

				if(!this.getEnabled()){
					var interceptor = jQuery("<div class='xw-disabled xw-mod'/>");
					if(this._opt._xid) interceptor.attr("_xid", this._opt._xid);
					this._getJShell().append(interceptor);
					interceptor.width("100%");
					interceptor.height("100%");
				}
				this._doTabindex();
			},
			getEnabled : function(){
				return xwing.Util.parseBoolean(this._opt.enabled);
			},
			_getModule : function(mod){
				var selector = ".xw-mod-"+mod +"[_xid="+this.getXId()+"]";
				return jQuery(selector, this.getShell()).andSelf().filter(selector);
			},
			setFontfamily : function(v){
				this._opt.fontfamily = v;
				this._doFontfamily();
			},
			getFontfamily : function(){
				return this._opt.fontfamily;				
			},
			_doFontfamily : function(){
				try {
					if (this.getFontfamily && Xwing.config.fonts && Xwing.config.fonts[this.getFontfamily()]) {
						this._getModule("font").css("font-family", Xwing.config.fonts[this.getFontfamily()]);
					}
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontfamily" + +":" + e);
				}
			},
			setFontcolor : function(v){
				this._opt.fontcolor = v;
				this._doFontcolor();
			},
			getFontcolor : function(){
				return this._opt.fontcolor || '';
			},
			_doFontcolor : function(){
				try {
					this._getModule("font").css("color", this.getFontcolor());
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".getFontcolor" + +":" + e);
				}
			},
			setFontstyle : function(v){
				this._opt.fontstyle = v;
				this._doFontstyle();
			},
			getFontstyle : function(){
				return this._opt.fontstyle;				
			},
			_doFontstyle : function(){
				try {
					this._getModule("font").css("font-style", this.getFontstyle());
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + "._doFontstyle:" + e);
				}
			},
			setFontweight : function(v){
				this._opt.fontweight = v;
				this._doFontweight();
			},
			getFontweight : function(){
				return this._opt.fontweight;
			},
			_doFontweight : function(){
				try {
					if (this.getFontweight()) {
						this._getModule("font").css("font-weight", this.getFontweight());
					}
				} catch (e) {
					Xwing.error("Error on " + this.getAlias() + ".doFontweight:" + this.getFontweight() + "," + e);
				}
			},
			setFontsize : function(v){
				this._opt.fontsize =  v;
				this._doFontsize();
			},
			getFontsize : function(){
				return this._opt.fontsize;
			},
			_doFontsize : function(){
				try {
					if (this.getFontsize()) {
						this._getModule("font").css("font-size", this.getFontsize() + 'px');
					}
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontsize:" + e);
				}
			},
			setFontdecoration : function(v){				
				this._opt.fontdecoration  = v;
				this._doFontdecoration();
			},
			getFontdecoration : function(){
				return this._opt.fontdecoration;
			},
			_doFontdecoration : function(){
				try {
					this._getModule("font").css("text-decoration", this.getFontdecoration());
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontdecoration : " + this.getFontdecoration() + " , " + e);
				}
			},
			_doFont : function(){
				this._doFontfamily();
				this._doFontcolor();
				this._doFontstyle();
				this._doFontweight();
				this._doFontsize();
				this._doFontdecoration();
			},
			_doBackground : function(module){				
				this._doBgcolor(module);
				this._doBgimage(module);
				this._doBgimagerepeat(module);
				this._doBgimagealign(module);
			},
			setBgcolor : function(value){
				this._opt.bgcolor = value;
				this._doBgcolor();
			},
			_doBgcolor : function(module){				
				var bgmod = module;
				if (bgmod === undefined)
					bgmod = this._getModule("background");
				if (this.getBgcolor()) {
					bgmod.css("background-color", this.getBgcolor());
				} else {
					bgmod.css("background-color", "");
				}
				this._doBggradientcolor(module);
			},
			getBgcolor : function(){
				return this._opt.bgcolor;
			},
			setBggradientcolor : function(v){
				this._opt.bggradientcolor = v;
				this._doBgcolor();
			},
			getBggradientcolor : function(){
				return this._opt.bggradientcolor;
			},
			_doBggradientcolor : function(module){
				var bgmod = module;
				if (bgmod === undefined)
					bgmod = this._getModule("background");
				
				if(this.getBggradientcolor() && this.getBgcolor() && !this.getBgimage()){					
					bgmod.css('background-color','');
					if(Xwing.isIE()){
						var gradientType = (this.getBggradientdir() == "vertical"? "GradientType=0" : "GradientType=1" ); 						
						var thisObj = this;
						bgmod.each(function(){
							this.style.filter = "progid:DXImageTransform.Microsoft.gradient("+gradientType+",startColorstr='"+thisObj.getBgcolor()+"', endColorstr='"+thisObj.getBggradientcolor()+"')";							
						});
						gradientType = (this.getBggradientdir() == "vertical"? "top" : "left" );
						bgmod.css("background-image", "-ms-linear-gradient("+gradientType+", "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
					}else{
						if(this.getBggradientdir() == "vertical"){
							bgmod.css("background-image", "linear-gradient(to top, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							if(jQuery.browser.mozilla){
								bgmod.css("background-image", "-moz-linear-gradient(top, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							}else if(jQuery.browser.opera){
								bgmod.css("background-image", "-o-linear-gradient(top, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							}else if(jQuery.browser.webkit){
								bgmod.css("background-image", "-webkit-gradient(linear, left top, left bottom, from("+this.getBgcolor()+"), to("+this.getBggradientcolor()+") )");
							}else if(jQuery.browser.msie){
								bgmod.css("background-image", "-ms-linear-gradient(top, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							}
						}else{
							bgmod.css("background-image", "linear-gradient(to left, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							if(jQuery.browser.mozilla){							
								bgmod.css("background-image", "-moz-linear-gradient(left,"+this.getBgcolor()+", "+this.getBggradientcolor());
							}else if(jQuery.browser.opera){							
								bgmod.css("background-image", "-o-linear-gradient(left,"+this.getBgcolor()+", "+this.getBggradientcolor());
							}else if(jQuery.browser.webkit){							
								bgmod.css("background-image", "-webkit-gradient(linear, left top, right bottom, from("+this.getBgcolor()+"), to("+this.getBggradientcolor()+") )");
							}else if(jQuery.browser.msie){
								bgmod.css("background-image", "-ms-linear-gradient(left, "+this.getBgcolor()+", "+this.getBggradientcolor()+")");
							}
						}
					}
				}else{
					this._doBgimage(module);
				}
			},
			setBggradientdir : function(v){
				this._opt.bggradientdir = v;
				this._doBgcolor();
			},
			getBggradientdir : function(){
				return this._opt.bggradientdir;
			},
			_doBggradientdir: function(module){
				this._doBgcolor(module);
			},
			setBgimage : function(value){
				this._opt.bgimage = value;
				this._doBgimage();
			},
			_doBgimage : function(module){
				var bgMod = module;
				if (bgMod === undefined)
					bgMod = this._getModule("background");
				
				if (this.getBgimage()){
					bgMod.css("background-image", "url('"+this.getBgimage()+"')");
				} else {
					if(this.getBgcolor() && this.getBggradientcolor()){
						this._doBgcolor(module);
					}else if(this.getBgcolor()){
						bgMod.css("background-image", "none");
					}else{
						bgMod.css("background-image", "");
					}
				}
			},
			getBgimage : function(){
				return this._opt.bgimage;
			},
			setBgimagerepeat : function(v){
				this._opt.bgimagerepeat = v;
				this._doBgimagerepeat();
			},
			getBgimagerepeat : function(){
				return this._opt.bgimagerepeat;
			},
			_doBgimagerepeat : function(module){
				var bgmod = module;
				if (bgmod === undefined)
					bgmod = this._getModule("background"); 
				
				if(this.getBgimagerepeat()){
					bgmod.css("background-repeat", this.getBgimagerepeat()) ;
				}else{
					bgmod.css("background-repeat", "") ;
				}
			},
			setBgimagealign : function(v){
				this._opt.bgimagealign = v;
				this._doBgimagealign();
			},
			getBgimagealign : function(){
				return this._opt.bgimagealign;
			},
			_doBgimagealign : function(module){
				var bgmod = module;
				if (bgmod === undefined)
					bgmod = this._getModule("background"); 
				
				bgmod.css("background-position", this.getBgimagealign()) ;
			},
			setBordercolor : function(v){
				this._opt.bordercolor = v;
				this._doBorder();
			},
			getBordercolor : function(){
				return this._opt.bordercolor || '';
			},
			setBorderstyle : function(v){
				this._opt.borderstyle = v;
				this._doBorder();
			},
			getBorderstyle : function(){
				return this._opt.borderstyle;
			},
			setBorderwidth : function(v){
				this._opt.borderwidth = v;
				this._doBorder();
			},
			getBorderwidth : function(){
				return xwing.Util.parseInt(this._opt.borderwidth,0);
			},
			_doBorder : function(){
				var borderMod = this._getModule('border');
				if (borderMod){
					borderMod.css('border-style',this.getBorderstyle());
					if(this.getBorderstyle() == 'none'){
						borderMod.css('border-color','transparent');
						borderMod.css('border-width','0');
					}else{
						borderMod.css('border-color',this.getBordercolor());
						borderMod.css('border-width',this.getBorderwidth()+'px');						
					}
				}
			},
			_initStyleEvent : function() {
				if (window.xwingIDE) return;
				
				var ns = ".xw-style",
					classes = (Xwing.config.style && Xwing.config.style.classes) || {},
					pseudo = ['', ':hover', ':active', ':focus'],
					hasStyleEvent = false,
					that = this, 
					attrs = {};
	
				for ( var i = 0, l = pseudo.length; i < l; i++) {
					if (classes[this._opt.styles + pseudo[i]]) {
						jQuery.extend(attrs, classes[this._opt.styles + pseudo[i]]);
						pseudo[i] && (hasStyleEvent = true); 
					}
				}
	
				this._getJShell().unbind(ns);
				this._styleAttrs = [];
	
				for ( var k in attrs)
					this._styleAttrs.push(k);
	
				if (!this._opt.styles || !this._styleAttrs.length || !hasStyleEvent)
					return;
				
				this._getJShell().bind('mouseenter' + ns, function() {
					if (this._focused) return;
					that._setStyleClasses(':hover');
				}).bind('mousedown' + ns, function() {
					if (this._focused) return;
					that._setStyleClasses(':active');
				}).bind('mouseup' + ns, function() {
					if (this._focused) return;
					that._setStyleClasses(classes[that._opt.styles + ':hover'] ? ':hover' : '');
				}).bind('mouseleave' + ns, function() {
					if (this._focused) return;
					that._setStyleClasses('');
				}).bind('focusin' + ns, function(e) {
					classes[that._opt.styles + ':focus'] && (this._focused = true);
					that._setStyleClasses(':focus');
				}).bind('focusout' + ns, function(e) {
					classes[that._opt.styles + ':focus'] && (delete this._focused);
					that._setStyleClasses('');
				});
			},
			_setStyleClasses : function(pseudo) {
				var classes = (Xwing.config.style && Xwing.config.style.classes) || {},
					styleAttrs = this._styleAttrs || [],	
					style = this._opt.styles + pseudo;
				
				if (!this._opt.styles || !classes[style])
					return;
				
				var backupStyle = this._opt.styles;
				this._opt.styles = style;
				
				for (var i = 0, l = styleAttrs.length, key, value, funcName ; i < l ; i++) {
					key = styleAttrs[i];
					value = this._attr[key] || this._getDefault(key);
					funcName = "set" + key.replace(/^\w/, key.match(/^\w/)[0].toUpperCase());
					
					if (this[funcName]) {
						this[funcName](value);
					} else {
						Xwing.warn("_setStyleClasses:" + key + " doesnt have setter method.");
						this._opt[key] = value;
					}
				}
				
				this._opt.styles = backupStyle;
			},		
			_getCommEvent : function(){
				return xwing.widget.Widget._EVENT_COMM;
			},
			_initEvent : function() {
				if (this.model.events && this.model.events.length) {
					var commonEvent = this._getCommEvent(),//xwing.widget.Widget._EVENT_COMM, 
						thisObj = this, 
						eventName;
					
					for ( var i = 0, l = this.model.events.length; i < l; i++) {
						eventName = this.model.events[i];
						this._opt[eventName] && this.bind(eventName, this._opt[eventName]);

						if (jQuery.inArray(eventName, commonEvent) != -1) {
							this._getJShell().bind(eventName, function(event) {
								if (thisObj.getEnabled() && thisObj._opt[event.type]) {
									if( jQuery.browser.mozilla && parseFloat(jQuery.browser.version) >= 11 && (event.type == 'click' || event.type == 'dblclick') )
										thisObj._getJShell().focus();
									thisObj._fire(event.type, {
										type : event.type,
										source : thisObj,
										event : event
									});
								}
							});
						}
					}
				}
			},
			bind : function(type, func, obj){
				/*debug Xwing.debug("widget("+this.getAlias()+") bind request " + type +", "+ func + ","+obj); */
				if (type && this.model.events && (jQuery.inArray(type, this.model.events) != -1)) {
					for ( var i = 0, l = this._listeners.length, temp; i < l; i++) {
						temp = this._listeners[i];
						if (temp.type == type && temp.func == func && temp.obj == obj)
							return;
					}

					this._listeners.push({
						type : type,
						func : func,
						obj : obj
					});
				}
			},
			unbind : function(type, func){
				var arr = [];
				for ( var i = 0, l = this._listeners.length, temp; i < l; i++) {
					temp = this._listeners[i];
					if (func) {
						if (temp.func == func && temp.type == type) {
							arr.push(i);
						}
					} else {
						if (temp.type == type)
							arr.push(i);
					}
				}
				for ( var j = arr.length - 1; j == 0; j--) {
					this._listeners.splice(j, 1);
				}
			},
			_fire : function(type, event){
				if (!this.getEnabled())
					return false;
				
				for ( var i = 0, l = this._listeners.length, temp; i < l; i++) {
					if (type == this._listeners[i].type) {
						temp = this._listeners[i];
						
						try {
							var funcObj = temp.func;
							if (funcObj) {
								(typeof (funcObj) != "function") && (funcObj = eval(funcObj));
								funcObj.call(temp.obj || null, event);
							}
						} catch (e) {
							 /*debug
							 Xwing.debug("event fire call error : widget("+ this.getAlias()+") event type:"+ type +","+temp.func + "\n"+ e);
							 */
							return false;
						}
					}
				}
				
				return true;
			},
			show : function() {
				this._getJShell().css('display','block');
			},
			hide : function() {
				this._getJShell().css('display','none');
			},
			_doTabindex : function(){
				var mod = this._getModule("focus");
				if (mod.size()) {
					if (this.getEnabled()) {
						mod.attr("tabindex", this.getTabindex());
					} else {
						mod.attr("tabindex", '-1');
					}
				}
			},
			setTabindex : function(val){
				this._opt.tabindex = val;
				this._doTabindex();
			},
			getTabindex : function(){
				return this._opt.tabindex;
			},
			setStyles : function(v){
				this._opt.styles = v;
				this._doStyles();
			},
			_doStyles : function(){
				var styleNoneAttr = xwing.widget.Widget._STYLE_NONE;
				
				for ( var key in this.model.attributes) {
					if (jQuery.inArray(key, styleNoneAttr) != -1)
						continue;
					
					var value = this._attr[key] || this._getDefault(key);
					var funcName = "set" + key.replace(/^\w/, key.match(/^\w/)[0].toUpperCase());
					if (this[funcName]) {
						this[funcName](value);
					} else {
						Xwing.warn("_doStyles:" + key + " doesnt have setter method.");
						this._opt[key] = value;
					}
				}
				
				this._initStyleEvent();
			},
			getStyles : function(){
				return this._opt.styles;
			},
			setAnchor : function(v){
				this._opt.anchor = v;
				this._doAnchor();
			},
			getAnchor : function(){
				return this._opt.anchor;
			},
			calcAnchorOffset : function(v) {
				if (window.xwingIDE) return;
				if (this._opt.anchor == 'none') return;
				var direction = this._opt.anchor.split(' ');
				if (this._opt.anchor == 'all') 
					direction = ['top', 'right', 'bottom', 'left'];
				if (v) {
					v = v.split(' ');
					direction = jQuery.grep(v, function(n) {
						return jQuery.inArray(n, direction) != -1;
					});
				} else {
					this._anchoroffset = [null, null, null, null];
				}
				for ( var i = 0, l = direction.length; i < l; i++) {
					this._calcAnchorDirectionOffset(direction[i]);
				}
			},
			_doAnchor : function() {
				if (window.xwingIDE) return;
				if (this._opt.anchor == undefined) {
					this._opt.anchor = (this instanceof xwing.widget.Panel) ? 'all' : 'none';
				}
				this.calcAnchorOffset();
				if (this._opt.anchor != 'none') {
					this._parentWidget.addResizeEventListener(this);
				} else {
					this._parentWidget.removeResizeEventListener(this);
				}
			},
			_calcAnchorDirectionOffset : function(direction) {
				if (direction == 'top') {
					this._anchoroffset[0] = this.getTop();	
				} else if (direction == 'right') {
					this._anchoroffset[1] = this._parentWidget.getWidth() - this.getLeft() - this.getWidth();
				} else if (direction == 'bottom') {
					this._anchoroffset[2] = this._parentWidget.getHeight() - this.getTop() - this.getHeight();
				} else {
					this._anchoroffset[3] = this.getLeft();
				}
			},
			doAnchorListener : function(w, h) {
				if (this._anchoroffset[1] != null && this._anchoroffset[3] != null) {
					var width = w - (this._anchoroffset[1] + this._anchoroffset[3]);
					this._opt.width = width;
				} 
				if (this._anchoroffset[0] != null && this._anchoroffset[2] != null) {
					var height = h - (this._anchoroffset[0] + this._anchoroffset[2]);
					this._opt.height = height;
				}
				if (this._anchoroffset[3] == null && this._anchoroffset[1] != null) {
					var left = w - this._anchoroffset[1] - this._opt.width;
					this._opt.left = left;
				}
				if (this._anchoroffset[0] == null && this._anchoroffset[2] != null) {
					var top = h - this._anchoroffset[2] - this._opt.height;
					this._opt.top = top;
				}
				this._doBounds();
				this._fireResizeEvent();
			},
			addResizeEventListener : function(widget) {
				this._resizeEventHandler = xwing.Util.shift(this._resizeEventHandler||[], this);
				this._resizeEventHandler.push(widget);
			},
			removeResizeEventListener : function(widget) {
				this._resizeEventHandler = xwing.Util.shift(this._resizeEventHandler||[], this);
			},
			fireAnchorResizeEvent : function(w, h) {
				if (window.xwingIDE) return;
								
				if (this._resizeEventHandler) {
					for ( var i = 0, l = this._resizeEventHandler.length, target; i < l; i++) {
						target = this._resizeEventHandler[i];
						target.doAnchorListener(w, h);
						target.fireAnchorResizeEvent(target.getWidth(), target.getHeight());
					}
				}
			},
			getChildrenWidget : function(filter) {
				var widgets = [], widget;

				jQuery('>.xw-shell', this.getContainer()).each(function() {
					widget = Xwing.getWidget(jQuery(this).attr('id'));
					if (!widget) return;
					if (filter && filter.charAt(0) == ':') {
						if (widget.getAlias() != filter.substr(1))
							return;
					}
					widgets.push(widget);
				});

				return widgets;
			},			
			setParentWidget : function(w){
				this._parentWidget = w;
			},
			getParentWidget : function(){
				return this._parentWidget;
			},
			_fireResizeEvent : function() {
				this._fire('resize', {
					type : 'resize',
					source : this
				});
			},
			setVisible : function(v){
				this._opt.visible = v;
				this._doVisible();
			},
			getVisible : function(){
				return xwing.Util.parseBoolean(this._opt.visible);
			},
			_doVisible : function(){
				if (window.xwingIDE)
					return;

				if (this.getVisible()) {
					this.show();
					
					if( (jQuery.browser.msie || (jQuery.browser.mozilla && parseFloat(jQuery.browser.version) >= 11)) && window)
						window.focus();
				} else {
					this.hide();
				}
			},
			setTextpadding : function(v){
				this._opt.textpadding = v;
				this._doTextpadding();
			},
			getTextpadding : function(){
				return this._opt.textpadding;
			},
			_doTextpadding : function(){
				var fontMod = this._getModule('font'),
					paddings = (this.getTextpadding() == null ? "" : this.getTextpadding().trim().split(/\s+/)),
					result = "";

				if (fontMod) {
					for ( var i = 0, l = paddings.length; i < l; i++) {
						var tmp = paddings[i] + 'px ';
						result += tmp;
					}
					fontMod.css('padding', result);
				}
			},
			setCursor : function(v){
				this._opt.cursor;
				this._doCursor();
			},
			getCursor : function(){
				return this._opt.cursor;
			},
			_doCursor : function(){
				if (window.xwingIDE) return;
				
				var handle = jQuery('*[_xid=' + this.getXId() + ']', this._getJShell()).andSelf();
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				handle.css('cursor', cursor);
			},
			setTooltiptext : function(v){
				this._opt.tooltiptext = v;
				this._doTooltiptext();
			},
			getTooltiptext : function(){
				return this._opt.tooltiptext || '';
			},
			_doTooltiptext : function(){
				if (window.xwingIDE)
					return;
				
				if (this.getTooltiptext()) {
					var jTool = jQuery('span.xw-tooltiptext');
					if (jTool.length == 0) {
						jTool = jQuery("<span class='xw-mod xw-tooltiptext'/>").css('display','none');
						jQuery('body').append(jTool);
					}
					
					var clientArea = document.documentElement;					
					var thisObj = this;				
					this._getJShell().hover(function(event) {
						if (thisObj.getTooltiptext()) {
							jTool.text(thisObj.getTooltiptext());							
							var tt_w = jTool.outerWidth(), tt_h = jTool.outerHeight(), bd_w = clientArea.clientWidth, bd_h = clientArea.clientHeight;
							jTool.css({
								'top' : event.pageY + tt_h > bd_h ? event.pageY - tt_h : event.pageY,
								'left' : event.pageX + tt_w + 20 > bd_w ? event.pageX - tt_w - 10 : event.pageX + 15
							});
							jTool.show();
						}
					}, function() {
						if (thisObj.getTooltiptext()) {
							jTool.hide();
							jTool.text('');
						}
					});
				}
			},
			setOpacity : function(v){
				this._opt.opacity = v;
				this._doOpacity();
			},
			getOpacity : function(){
				return this._opt.opacity;
			},
			_doOpacity : function(){
				if (this._opt.opacity == "1") {
					this._getJShell().css('opacity', '');
					return;
				}
				
				if (jQuery.browser.msie && jQuery.browser.version == "8.0") {
					this._getJShell().css('filter', 'alpha(opacity=' + parseFloat(this.getOpacity()) * 100 + ')');
				} else {
					this._getJShell().css('opacity', this.getOpacity());
				}
			},
			setShadow : function(v){
				this._opt.shadow = v;
				this._doShadow();
			},
			getShadow : function(){
				return this._opt.shadow;
			},
			_doShadow: function(mod){
				var dom = (mod === undefined) ? this._getModule('border') : mod,
					shValue = '';

				if (this.getShadow()) {
					var shArr = this.getShadow().trim().split(/\s+/);
					for ( var i = 0, l = shArr.length; i < l; i++) {
						shValue += isNaN(shArr[i]) ? shArr[i] + ' ' : shArr[i] + 'px ';
					}
				}
				
				dom.css({
					'-moz-box-shadow' : shValue,
					'-webkit-box-shadow' : shValue,
					'box-shadow' : shValue
				});				
			},
			_doTextwrap : function(jEdit){
				if (jQuery.browser.webkit)
					jEdit.width('');
				
				switch (this.getTextwrap()) {
				case 'none':
					jEdit.css('white-space', 'nowrap');
					break;
				case 'pre':
					jEdit.css('white-space', 'pre');
					break;
				case 'prewrap':
					if (jQuery.browser.webkit && this.getAlias() != 'radio')
						jEdit.width(this.getWidth() + 'px');
					jEdit.css('white-space', 'pre-wrap');
					break;
				}
			},
			_bind : function(target, type, handler){
				if (!window.xwingIDE) {
					var widget = this;
					target.bind(type, function(event) {
						handler.call(widget, event);
					});
				}
			},
			_bindScrollPane : function(target, opts) {
				if (xwing.Util.isTouchDevice()) {
					this._resetWrapperSize = function() {
						this.wrapper.css('height', (this.getHeight() - this.getBorderwidth() * 2) + 'px');
					};

					var doBounds = this._doBounds || xwing.widget.Widget.prototype._doBounds;
					this._doBounds = function() {
						doBounds.call(this);
						this._refreshTouchScroll();
					};			
					
					this._refreshTouchScroll = function() {
						if (this.iSTimeout) return;
						this.iSTimeout = setTimeout(function() {
							try {
								thisObj._resetWrapperSize();
								thisObj.iScroll && thisObj.iScroll.refresh();
							} catch (e) {}
							thisObj.iSTimeout = null;
						}, 100);
					};
					
					var preventHandler = function(e) { e.preventDefault(); };
					
					target[0].addEventListener('touchstart', function(e) {
						document.addEventListener('touchmove', preventHandler, false);
					}, false);
					target[0].addEventListener('touchend', function(e) {
						document.removeEventListener('touchmove', preventHandler, false);
					}, false);
					
					target.parent().css('overflow', 'hidden');
					target.wrap("<div class='xw-scroll-wrapper' style='width:100%'></div>");
					this.wrapper = jQuery('.xw-scroll-wrapper', this._getJShell());
					
					var thisObj = this;
					setTimeout(function() {
						thisObj._resetWrapperSize();

						var options = {
							onBeforeScrollStart : function(e) {},
							zoom : false
						};
						
						options = jQuery.extend(options, opts);
						thisObj.iScroll = new iScroll(thisObj.wrapper[0], options);
					}, 100);
				}				
			},
			_refreshScrollPane : function() {
				this._refreshTouchScroll && this._refreshTouchScroll();
			},
			setDragIcon : function(v){
				this._opt.dragicon = v;
				this._doDragIcon();
			},
			getDragIcon : function(){
				return this._opt.dragicon;
			},
			_doDragIcon : function(){
				if(this.getDraggable()){
					var that = this;
					this._getJShell().draggable("option","helper",function(){
						return document.body.appendChild(that.getDragIcon()[0] ? that.getDragIcon()[0] : that.getDragIcon()); 
					});
				}
			},
			setDraggable : function(v){
				this._opt.draggable = v;
				this._doDraggable();
			},
			getDraggable : function(){
				return xwing.Util.parseBoolean(this._opt.draggable,false);
			},
			_doDraggable : function(){
				if( window.xwingIDE ) return;
				
				if(this.getDraggable()){
					var that = this;
					this._getJShell().draggable({
						helper : "clone",
						opacity : 0.5,
						start : function(event, ui){
							that._fire('dragstart',{
								source : that,
								event : event,
								position : ui.position,
								index : Xwing.getDataset(that._opt.binddataset) ? Xwing.getDataset(that._opt.binddataset) : -1
							});
						},
						drag : function(event, ui){
							ui.position.left = event.clientX - that.getParentWidget().getShell().offsetLeft;//event.clientX + 2- that.getParentWidget().getShell().offsetLeft;
							ui.position.top = event.clientY  - that.getParentWidget().getShell().offsetTop;//event.clientY + 15 - that.getParentWidget().getShell().offsetTop;
							
							that._fire("dragging",{
								source : that,
								event : event, 
								position : ui.position,
								index : Xwing.getDataset(that._opt.binddataset) ? Xwing.getDataset(that._opt.binddataset) : -1
							});
						},
						stop : function(event, ui){
							that._fire('dragend',{
								source : that, 
								event : event, 
								position : ui.position,
								index : Xwing.getDataset(that._opt.binddataset) ? Xwing.getDataset(that._opt.binddataset) : -1
							});
						}
					});
				}else {
					this._getJShell().draggable('destroy');
				}
			},
			setDroppable : function(v){
				this._opt.droppable = v;
				this._doDroppable();
			},
			getDroppable : function(){
				return xwing.Util.parseBoolean(this._opt.droppable,false);
			},
			_doDroppable : function(){
				if( window.xwingIDE ) return;
				if(this.getDroppable() ){
					var that = this;
					this._getJShell().droppable({
						tolerance : 'pointer',
						drop : function(event, ui){
							var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
							if(item.draggable){
								widget = item.draggable;
								widget._fire('dragend',{
									source : widget, 
									event : event, 
									position : ui.position,
									index : Xwing.getDataset(that._opt.binddataset) ? Xwing.getDataset(that._opt.binddataset) : -1
								});
								
								delete item.draggable;
							}
							that._fire('dropping',{
								dragicon : ui.helper,
								source : that,
								dragObj : widget,
								event : event, 
								position : ui.position
							});
						},
						over : function(event, ui){
							var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
							if(item.draggable){
								widget = item.draggable;
							}
							that._fire('dropin',{
								dragicon : ui.helper,
								source : that,
								dragObj : widget,
								event : event, 
								position : ui.position
							});
						},
						out : function(event, ui){
							var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
							if(item.draggable){
								widget = item.draggable;
							}
							that._fire('dropout',{
								dragicon : ui.helper,
								source : that,
								dragObj : widget,
								event : event, 
								position : ui.position
							});
						}
					});
				}else{
					this._getJShell().droppable('destroy');
				}
			}
		}
	}
});
