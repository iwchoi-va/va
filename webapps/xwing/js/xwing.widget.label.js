Class.define({
	Label : {
		alias : 'label',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Label : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Label(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.label_div = jQuery("<div class='xw-label xw-mod-focus xw-mod-cursor'/>");
				this.label_div_cell = jQuery("<div class='xw-label-cell xw-mod-background'/>").appendTo(this.label_div);
				this.icon = jQuery("<span class='xw-label-icon xw-mode xw-align' />").appendTo(this.label_div_cell);
				this.label = jQuery("<label class='xw-label-label xw-mod-font'/>").appendTo(this.label_div_cell);
				
				this.label_div.appendTo(this._getJShell().addClass('xw-mod-border'));
				/*debug Xwing.debug("Widget(Label) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_doEnabled : function(){
				if(this.getEnabled()){
					this._getJShell().addClass("xw-label-shell-enabled").removeClass("xw-label-shell-disabled");
					this.label.addClass("xw-label-enabled").removeClass("xw-label-disabled");
					
					if(! window.xwingIDE) {
						this.label.removeAttr('disabled');
					}
				}else{
					this._getJShell().addClass("xw-label-shell-disabled").removeClass("xw-label-shell-enabled");
					this.label.addClass("xw-label-disabled").removeClass("xw-label-enabled");
					
					if(! window.xwingIDE) {
						this.label.attr('disabled', 'disabled');
					}
				}		
			},
			_doValue : function(){
				var val = this.getDomainValue();
				this.label.text(this.getMaskedValue(val));
			},
			_doDomain : function() {
				this._doValue();
			},
			_render : function() {
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doAlign();
				this._doIcon();
				this._doIconalign();
				this._doIconmargin();
				this._doTextwrap(this.label);
				this._doBgimageedge();
			},
			setValign : function(v) {
				this._opt.valign = v;
				this._doAlign();
			},
			getValign : function() {
				return this._opt.valign;
			},
			_doValign : function(){
				this._doAlign();
			},
			setHalign : function(v) {
				this._opt.halign = v;
				this._doAlign();
			},
			getHalign : function() {
				return this._opt.halign;
			},
			_doAlign : function() {
				this.label_div_cell.css('text-align',this.getHalign());
				this.label_div_cell.css('vertical-align',this.getValign());
				this.icon.css('vertical-align',this.getValign());
				this.label.css('vertical-align',this.getValign());
			},
			setIcon : function(value){
				this._opt.icon = value;
				this._doIcon();
			},
			getIcon : function(){
				return this._opt.icon;
			},
			_doIcon : function(){
				if (this._opt.icon) {
					this.icon.css('background', "url('" + this._opt.icon + "') 0% 50% no-repeat");
					var img = new Image();
					var thisObj = this;
					img.onload = function() {
						thisObj.icon.width(this.width);
						thisObj.icon.height(this.height);
					};
					img.src = this._opt.icon;
				} else {
					this.icon.width(0);
					this.icon.height(0);
					this.icon.css("background-image", "");
				}
			},
			setIconalign : function(v) {
				this._opt.iconalign = v;
				this._doIconalign();
			},
			getIconalign : function() {
				return this._opt.iconalign;
			},
			_doIconalign : function() {
				var jIcon = this.icon.remove();
				switch (this.getIconalign()) {
				case 'left':
					this.label.before(jIcon);
					break;
				case 'right':
					this.label.after(jIcon);
					break;
				}
				this.icon = jIcon;
			},
			setIconmargin : function(v) {
				this._opt.iconmargin = v;
				this._doIconmargin();
			},
			getIconmargin : function() {
				return this._opt.iconmargin;
			},
			_doIconmargin : function() {
				var margins = (this.getIconmargin() == null ? "" : this.getIconmargin().trim().split(" "));
				var result = "";
				for ( var i in margins) {
					var tmp = margins[i] + 'px ';
					result += tmp;
				}
				this.icon.css('margin', result);
			},
			_doFontsize : function() {
				xwing.widget.Widget.prototype._doFontsize.call(this);
				this.label.css('line-height', this.getFontsize() + 'px');
			},
			setTextwrap : function(v) {
				this._opt.textwrap = v;
				this._doTextwrap(this.label);
			},
			getTextwrap : function() {
				return this._opt.textwrap;
			},
			setBgimageedge : function(v) {
				this._opt.bgimageedge = v;
				this._doBgimageedge();
			},
			getBgimageedge : function() {
				return this._opt.bgimageedge;
			},
			_doBgimageedge : function() {
				var jEdge = jQuery('div.xw-label-edge', this.getShell());
				if (this.getBgimageedge()) {
					if (jEdge.length == 0) {
						this.label_div_cell.before(jQuery("<div class='xw-label-edge xw-label-edge-left' />"));
						this.label_div_cell.after(jQuery("<div class='xw-label-edge xw-label-edge-right' />"));
						jEdge = jQuery('div.xw-label-edge', this.getShell()).addClass('xw-mod').attr("_xid", this.getXId());
					}
					var img = new Image();
					img.onload = function(event) {
						var width = xwing.Util.parseInt(this.width, 0) / 2;
						jEdge.width(width);
					};
					img.src = this.getBgimageedge();
					jEdge.css({
						'background-image' : 'url(' + this.getBgimageedge() + ')'
					});
					
					jQuery.browser.msie && jEdge.css({
						'height' : (this.getHeight() - this.getBorderwidth() * 2) + 'px'
					});
				} else if (jEdge.length != 0) {
					jEdge.css('background-image', '').remove();
				}
			}
		}
	}

});
