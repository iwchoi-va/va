Class.define({
	Edit : {
		alias : 'edit',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Edit : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Edit(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.jEdit = jQuery("<input type='"+this.getMode()+"' class='xw-edit xw-mod-background xw-mod-font xw-mod-border xw-mod-focus'/>");					
				
				var thisObj = this;

				this.jEdit.bind("focus", function(event){
					if (!window.xwingIDE) thisObj._startTimer();
				});
				
				this.jEdit.bind('blur', function(event){
					if (!window.xwingIDE) thisObj._endTimer();
				});

				this.jEdit.bind("keydown", function(event){
					thisObj.isHangulKeying = jQuery.browser.mozilla && event.keyCode == 229;
					
					event.stopPropagation();
					
					thisObj._fire('keydown', {
						type :'keydown',
						source : thisObj,
						event : event
					});
				});
				
				this.jEdit.bind("keyup", function(event){
					if (event.keyCode == 13 && !window.xwingIDE && (jQuery.browser.opera || jQuery.browser.msie || (jQuery.browser.mozilla && parseFloat(jQuery.browser.version) >= 11)))
						thisObj.updateDataset();
					
					event.stopPropagation();
					
					thisObj._fire('keyup', {
						type :'keyup',
						source : thisObj,
						event : event
					});
				});
				
				this.jEdit.bind('changing', function(event){
					var newValue = thisObj.getUnMaskedValue(thisObj.jEdit.val());
					
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
				});
				
				this.jEdit.bind('change', function(event) {
					thisObj._opt.value = thisObj.getUnMaskedValue(thisObj.jEdit.val());
					thisObj.updateDataset();
					
					thisObj._fire('change', {
						type : 'change',
						source : thisObj,
						event : event,
						value : thisObj._opt.value
					});
				});
				
				this._doReadonly();
				this._doInputmode();
				this._doImemode();
				this._doMaxlength();
				this._doHalign();
				
				this._getJShell().append(this.jEdit);
				/*debug Xwing.debug("Widget(Edit) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */			
			},
			_startTimer:function(){
				/*debug Xwing.debug("_startTimer"); */
				if (!this.getEnabled())
					return;
				
				window.clearInterval(this.timer);
				
				var oldValue = this.jEdit.val();				
				var thisObj = this;
				
				this.timer = window.setInterval(function() {
					var v = thisObj.jEdit.val();
					if (oldValue != v) {
						thisObj.jEdit.trigger('changing');
						thisObj.isHangulKeying && thisObj.jEdit.trigger('keyup');							
						oldValue = v;
					}
				}, 1);
			},
			_endTimer:function(){
				/*debug Xwing.debug("_endTimer"); */
				window.clearInterval(this.timer);
			},
			_doValue : function(){
				this.jEdit.val(this.getMaskedValue());
				if(this.getPlaceholder()) this._checkPlaceholder();
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);	
				if(Xwing.isIE()) this.jEdit.css('line-height',(this.getHeight() - this.getBorderwidth()*2)+"px");
				if(this.getPlaceholder()) this._resetPlaceholderheight();
			},
			setMode : function(v){
				this._opt.mode = v;
				this._doMode();
			},
			getMode : function(){
				return this._opt.mode;
			},
			_doMode : function(){
				!window.xwingIDE && this._endTimer();
				
				var val = this.jEdit.val();

				this._getJShell().empty();
				this._createPart();
				this._branding();
				this._render();

				this.jEdit.val(val);
				
				window.xwingIDE && this.jEdit.css("cursor", "default");
				
//				var val = this.jEdit.val();
//				this._getJShell().empty();
//				this.jEdit = jQuery("<input type='"+this.getMode()+"' class='xw-edit xw-mod-background xw-mod xw-mod-font xw-mod-border xw-mod-focus' _xid='"+this.getXId()+"' />");
//				this.jEdit.val(val);
//				this._getJShell().append(this.jEdit);
			},
			focus : function(){
				this.jEdit[0].focus();
			},
			setReadonly : function(v){
				this._opt.readonly = v;
				this._doReadonly();
			},
			getReadonly : function(){
				return xwing.Util.parseBoolean(this._opt.readonly);
			},
			_doReadonly : function(){
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
			_doMaxlength : function(){
				if(this.getMaxlength() != -1)	this.jEdit.attr('maxlength',this.getMaxlength());
				else this.jEdit.removeAttr('maxlength');
			},
			setHalign : function(v){
				this._opt.halign = v;
				this._doHalign();
			},
			getHalign : function(){
				return this._opt.halign;
			},
			_doHalign : function(){
				this.jEdit.css('text-align',this._opt.halign);
			}
//			setLengthunit : function(v){
//				this._opt.lengthunit = v;
//			},
//			getLengthunit : function(){
//				return this._opt.lengthunit;
//			}
//			},
//			_checkMaxlength : function(oldV,newV){
//				var maxbyte = this.getLengthunit() == 'ascii'? this.getMaxlength():(this.getLengthunit() == 'utf-8'? this.getMaxlength()*3:this.getMaxlength()*2);
//				if(maxbyte > 0){
//					var bytes = xwing.Util.getByteLength(newV);
//					if(bytes > maxbyte)	return oldV;
//				}
//				return newV;
//			}
		}
	}
});
