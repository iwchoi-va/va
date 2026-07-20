Class.define({
	Spin : {
		alias : 'spin',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Spin : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Spin(json);
			}
		},
		prototypes : {			
			_createPart : function(){
				this._jSpin = jQuery("<div class='xw-spin xw-mod-border'/>");
				this._jinput = jQuery("<input type='text' class='xw-spin-input xw-mod-font xw-mod-background xw-mod-focus' />").appendTo(this._jSpin);
				this._jBtn = jQuery("<div class='xw-spin-button'/>").appendTo(this._jSpin);
				this._jBtn_up = jQuery("<div class='xw-spin-up'/>").appendTo(this._jBtn);
				this._jBtn_down = jQuery("<div class='xw-spin-down'/>").appendTo(this._jBtn);
				
				this._bind(this._jBtn_up,'click',this._upValue);
				this._bind(this._jBtn_down,'click',this._downValue);
				this._bind(this._jinput,'keydown',this._keypress);
				this._bind(this._jinput,'change',this._change);
				
				this._jSpin.appendTo(this._getJShell());
				/*debug Xwing.debug("Widget(Combo) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_render : function(){
				xwing.widget.DataBindable.prototype._render.call(this);
				
				this._doEditable();
			},
			_doBounds:function(){
				xwing.widget.DataBindable.prototype._doBounds.call(this);
				this._jinput.width(this.getWidth() - this.getBorderwidth()*2 - 18);
				if (Xwing.isIE()) this._jinput.css('line-height', (this.getHeight() - this.getBorderwidth() * 2) + "px");
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				this._jinput.width(this.getWidth() - this.getBorderwidth()*2 - 18);
				if (Xwing.isIE()) this._jinput.css('line-height', (this.getHeight() - this.getBorderwidth() * 2) + "px");
			},
			_change : function(event){
				var val = this._jinput.val().replace(/,/g,'') || 0;
				
				if(val == this.getValue()) return;
				if(isNaN(val)) val = this._opt.value;
				
				this._setValue(val);
			},
			_keypress : function(event){
				if(event.keyCode == 38) this._upValue();
				else if(event.keyCode == 40) this._downValue();
			},
			_upValue : function(){
				var val = this.getValue();
				if(!val) val = this.getMin();
				else if(!isNaN(this._jinput.val())) val = this._jinput.val().replace(/,/g,'') || 0;
				var T=Number('1e'+1);
				var val = Math.round((parseFloat(val)+parseFloat(this.getStep()))*T)/T;
				this._setValue(val);
			},
			_downValue : function(){
				var val = this.getValue();
				if(!val) val = this.getMin();
				else if(!isNaN(this._jinput.val())) val = this._jinput.val().replace(/,/g,'') || 0;
				var T=Number('1e'+1);
				var val = Math.round((parseFloat(val) - parseFloat(this.getStep()))*T)/T;
				this._setValue(val);
			},
			_setValue : function(val){
				var realVal = this.getValue();
				if(val > parseFloat(this.getMax())){
					val = (this.getCirculation() ? this.getMin() : this.getMax());
				}else if(val < parseFloat(this.getMin())){
					val = (this.getCirculation() ? this.getMax() : this.getMin());
				}
				if(realVal == val) return;
				var opt = {type:'changing',source:this,event:null,value:val,doit:true};
				this._fire('changing',opt);
				if(!opt.doit){
					this._jinput.val(this._getCommaValue());
					return;
				}
				
				this.setValue(val);
				this._fire('change',{type:'change',source:this,event:null,value:this.getValue()});
			},
			_doValue:function(){
				this._jinput.val(this._getCommaValue()); 
			},
			setEditable : function(v){
				this._opt.editable = v;
				this._doEditable();
			},
			getEditable : function(){
				return xwing.Util.parseBoolean(this._opt.editable,false);
			},
			_doEditable : function(){
				if(this.getEditable()) this._jinput.removeAttr("readonly");
				else this._jinput.attr("readonly", "readonly");
			},
			setCirculation : function(v){
				this._opt.circulation = v;
			},
			getCirculation : function(){
				return xwing.Util.parseBoolean(this._opt.circulation,false);
			},
			setMax : function(v){
				this._opt.max = v;
			},
			getMax : function(){
				return this._opt.max;
			},
			setMin : function(v){
				this._opt.min = v;
			},
			getMin : function(){
				return this._opt.min;
			},
			setStep : function(v){
				this._opt.step = v;
			},
			getStep : function(){
				return this._opt.step;
			},
			focus : function(){
				this._jinput[0].focus();
			},
			setComma : function(v){
				this._opt.comma = v;
			},
			getComma : function(){
				return xwing.Util.parseBoolean(this._opt.comma,false);
			},
			_getCommaValue : function(val){
				 var v = val || this.getValue();
				 if(this.getComma()){
					 var reg = /(^[+-]?\d+)(\d{3})/;
				     v +='';
				     while(reg.test(v)){
				         v = v.replace(reg,'$1'+','+'$2');
				     }
				 }
			     return v;
			}
		}	
	}
});
