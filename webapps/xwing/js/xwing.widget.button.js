Class.define({
	Button : {
		alias : 'button',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Button : function(json) {
			this._init(json);
		},
		statics : {
			create : function(json) {
				return new xwing.widget.Button(json);
			}
		},
		prototypes : {
			_createPart : function() {
				this.jBtn = jQuery("<div class='xw-button xw-mod-background xw-mod-focus'/>");
				
				if (jQuery.browser.msie && parseInt(jQuery.browser.version) == 8) {
					this.jImg = jQuery("<img class='xw-align-image' />").appendTo(this.jBtn);
					this.jBtn.append(" ");
					this.jImg.css('margin-left','-4px');
				}

				this.jIcon = jQuery("<span class='xw-button-icon xw-mod xw-align'/>").appendTo(this.jBtn);
				this.jEdit = jQuery("<span class='xw-button-label xw-mod xw-align xw-mod-font' unselectable='on' />").appendTo(this.jBtn);
				
				var interdiv = jQuery("<div class='xw-button-shell-inner xw-mod-border'/>");
				interdiv.append(this.jBtn);
			    
				if (jQuery.browser.msie) {
					var that = this;
					
					interdiv.bind('mousedown', function() {
						jQuery(this).removeClass('xw-button-shell-inner').addClass('xw-button-click');
						that._getJShell().focus();
					}).bind('mouseleave mouseup', function() {
						jQuery(this).addClass('xw-button-shell-inner').removeClass('xw-button-click');
					});
				}
				
			    this._getJShell().append(interdiv);
			},
			_render : function() {
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doIcon();
				this._doIconmargin();
				this._doIconalign();
				this._doTextwrap(this.jEdit);
				
				// 임시
				if( !jQuery.browser.webkit && !this.getFontfamily() ){
					this.jEdit.css('padding-top','1px');
				}
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);
				var borderwidth = (this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth());
				this.jBtn.css('line-height',(this.getHeight() - borderwidth*2)+'px');
				if( !jQuery.browser.webkit ){
					this.jEdit.css('line-height',(this.getHeight() - borderwidth*2)+'px');
				}
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				var borderMod = this._getModule('border');
				if(this.getBorderstyle() == 'none') borderMod.css('line-height',this.getHeight()+'px');
				else borderMod.css('line-height',(this.getHeight() - this.getBorderwidth()*2)+'px');
			},
			_doValue : function() {
				var val = this.getDomainValue();
				this.jEdit.text(val);
			},
			_doDomain : function() {
				this._doValue();
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
					var thisObj = this;
					var image = new Image();
					image.onload = function() {
						thisObj.jIcon.width(this.width);
						thisObj.jIcon.height(this.height);
					};
					image.src = this._opt.icon;
					this.jIcon.css("background-image", "url('" + this._opt.icon + "')");
				} else {
					this.jIcon.width(0);
					this.jIcon.height(0);
					this.jIcon.css("background-image", "");
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
				var jIcon = this.jIcon.remove();
				
				switch (this.getIconalign()) {
				case 'left':
					this.jEdit.before(jIcon);
					break;
				case 'right':
					this.jEdit.after(jIcon);
					break;
				}
				
				this.jIcon = jIcon;
			},
			setIconmargin : function(v) {
				this._opt.iconmargin = v;
				this._doIconmargin();
			},
			getIconmargin : function() {
				return this._opt.iconmargin;
			},
			_doIconmargin : function() {
				var margins = (this.getIconmargin() == null ? "" : this.getIconmargin().trim().split(/\s+/)),
					result = "";

				for ( var i = 0, l = margins.length; i < l; i++) {
					result += margins[i] + 'px ';
				}
				
				this.jIcon.css('margin', result);
			},
			setTextwrap : function(v) {
				this._opt.textwrap = v;
				this._doTextwrap(this.jEdit);
			},
			getTextwrap : function() {
				return this._opt.textwrap;
			},
			_doEnabled : function(){
				this._getJShell().children(".xw-disabled").remove();

				if(!this.getEnabled()){
					var interceptor = jQuery("<div class='xw-disabled xw-button-disabled xw-mod'/>");
					if(this._opt._xid) interceptor.attr("_xid", this._opt._xid);
					this._getJShell().append(interceptor);
					interceptor.width("100%");
					interceptor.height("100%");
				}
				this._doTabindex();
			}
		}
	}
});
