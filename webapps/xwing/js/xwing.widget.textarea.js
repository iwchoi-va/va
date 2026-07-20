Class.define({
	TextArea : {
		alias : 'textarea',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		TextArea : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.TextArea(json);
			}
		},			
		prototypes : {
			_createPart : function(){
				this.jEdit = jQuery("<textarea class='xw-textarea xw-mod-background xw-mod-font xw-mod-border xw-mod-focus'/>");
				this._getJShell().append(this.jEdit);
				this.chageFlag = true;
				this._bind(this.jEdit, 'focus', this._onFocus);
				this._bind(this.jEdit, 'blur', this._onBlur);
				this._bind(this.jEdit, 'change', this._onChange);
				this._bind(this.jEdit, 'select', this._onSelect);
				this._bind(this.jEdit, 'keydown', this._onKeydown);
				this._bind(this.jEdit, 'keyup', this._onKeyup);
				this._bind(this.jEdit, 'focusin', function(event){
					event.stopPropagation();
					if(!this.chageFlag) return;
					this._fire("focusin", {
						type : 'focusin',
						source : this,
						event : event
					});
				});
				this._bind(this.jEdit, 'focusout', function(event){
					event.stopPropagation();
					if(!this.chageFlag) return;
					this._fire("focusout", {
						type : 'focusout',
						source : this,
						event : event
					});
				});

				this._doReadonly();
				this._doImemode();
				this._doInputmode();
				/*debug Xwing.debug("Widget(TextArea) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_onFocus : function(event){
				if(!window.xwingIDE && this.getEnabled()){
					window.clearInterval(this.timer);
					
					var oldValue = this.jEdit.val();				
					var thisObj = this;
					
					this.timer = window.setInterval(function() {
						var v = thisObj.jEdit.val();
						if (oldValue != v) {
							var newValue = thisObj.getUnMaskedValue(thisObj.jEdit.val());
							if(thisObj.getMaxlength() != -1 && newValue.length > thisObj.getMaxlength()){
								newValue = newValue.substr(0,thisObj.getMaxlength());
								thisObj.chageFlag = false;
								thisObj.jEdit.blur();
								thisObj.setValue(newValue);
								thisObj.jEdit[0].focus();
								oldValue = newValue;
								thisObj.chageFlag = true;
								return;
							}
							
							var opt = {
								type : 'changing',
								source : thisObj,
								event : event,
								value : newValue,
								doit : true
							};
							
							thisObj._fire("changing", opt);
							if (opt.doit) {						
								thisObj._opt.value = newValue;						
							} else {
								thisObj._doValue();
							}					
							thisObj.isHangulKeying && thisObj.jEdit.trigger('keyup');							
							oldValue = v;
						}
					}, 1);
				}
				
			},
			_onBlur : function(event){
				window.clearInterval(this.timer);
			},
			_onChange : function(event) {
				if(!this.chageFlag) return;
				this.setValue(this.jEdit.val());

				this._fire('change', {
					'type' : event.type,
					'source' : this,
					'value' : this.jEdit.val(),
					'event' : event
				});
			},
			_onSelect : function(event) {
				this._fire('select', {
					'type' : event.type,
					'source' : this,
					'event' : event
				});
			},
			_onKeydown : function(event) {
				this.isHangulKeying = jQuery.browser.mozilla && event.keyCode == 229;
				this._fire('keydown', {
					'type' : event.type,
					'source' : this,
					'event' : event
				});
			},
			_onKeyup : function(event) {
				this._fire('keyup', {
					'type' : event.type,
					'source' : this,
					'event' : event
				});
			},
			_doValue : function(){
				/*debug Xwing.debug("textarea dovalue..."); */
				this.jEdit.val(this.getValue()||'');
				if(this.getPlaceholder()) this._checkPlaceholder();
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);	
				if(this.getPlaceholder()) this._resetPlaceholderheight();
			},
			_doEnabled : function(){
				if(this.getEnabled()){
					this.$.addClass("xw-textarea-shell-enabled").removeClass("xw-textarea-shell-disabled");
					this.jEdit.addClass("xw-textarea-enabled").removeClass("xw-textarea-disabled");
					if(!window.xwingIDE ){
						this.jEdit.removeAttr("disabled");
					}
				}else{
					this.$.addClass("xw-textarea-shell-disabled").removeClass("xw-textarea-shell-enabled");
					this.jEdit.addClass("xw-textarea-disabled").removeClass("xw-textarea-enabled");
					if(!window.xwingIDE){
						this.jEdit.attr("disabled", true);
					}
				}
			},
			setReadonly : function(v){
				this._opt.readonly = v;
				this._doReadonly();
			},
			getReadonly : function(){
				return xwing.Util.parseBoolean(this._opt.readonly);
			},
			_doReadonly : function(){
				/*debug Xwing.debug("_doReadonly"); */
				if(this.getReadonly()){
					this.jEdit.attr('readonly','readonly');
				}else{
					this.jEdit.removeAttr('readonly');
				}
			},
			setImemode : function(v){
				this._opt.imemode = v;
				this._doImemode();
			},
			getImemode : function(){
				return this._opt.imemode;
			},
			_doImemode : function(){
				this.jEdit.css('ime-mode',this.getImemode());
			},
			setInputmode : function(v){
				this._opt.inputmode = v;
				this._doInputmode();
			},
			getInputmode : function(){
				return this._opt.inputmode;
			},
			_doInputmode : function(){
				this.jEdit.css('text-transform',this.getInputmode());
			},
			setMaxlength : function(v){
				this._opt.maxlength = v;
			},
			getMaxlength : function(){
				return xwing.Util.parseInt(this._opt.maxlength,-1);
			},
			focus : function(){
				this.jEdit[0].focus();
			}
		}
	}

});
