Class.define({
	Checkbox : {
		alias : 'checkbox',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Checkbox : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Checkbox(json);
			}
		},			
		prototypes : {
			_createPart : function(){
				this.shellDiv = jQuery("<div class='xw-checkbox xw-mod-background xw-mod-focus'/>");
				
				this.checkbox_item_div = jQuery("<div class='xw-checkbox-item-div' unselectable='on' />").appendTo(this.shellDiv);
				this.checkbox_item = jQuery("<div class='xw-checkbox-item'/>").appendTo(this.checkbox_item_div);
				this.checkbox_label = jQuery("<span class='xw-checkbox-label xw-mod-font'/>").appendTo(this.shellDiv);

				if(this._opt.label) {
					this.setLabel(this._opt.label);
				}
				
				this._bind(this.shellDiv, 'mousedown', this._onMousedown);
				this._bind(this.shellDiv, 'mouseup', this._onMouseup);
				this._bind(this.shellDiv, 'keypress', this._keypress);
				
				this.shellDiv.appendTo(this._getJShell().addClass('xw-mod-border'));
				/*debug Xwing.debug("Widget(Checkbox) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_render : function(){
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doIconalign();
				this._doIconmargin();
				this._doIconimage();
				this._doHalign();
				this._doValign();
				this._doTextwrap(this.checkbox_label);
			},
			_keypress : function(event){
				if( event.keyCode == '13' || event.keyCode == '32' || event.keyCode == '0'){
					this._setCheckedValue(event);
				}
			},
			_onMousedown : function(event) {
				if (this.checkbox_label.attr("disabled"))
					return;
				
				if (this.checkbox_label.attr("value") == "checked") {
					this.checkbox_item.addClass("xw-checkbox-checked-pressing");
				} else {
					this.checkbox_item.addClass("xw-checkbox-unchecked-pressing");
				}
			},
			_onMouseup : function(event) {
				if (this.checkbox_label.attr("disabled"))
					return;
				
				this._setCheckedValue(event);
			},
			_setCheckedValue : function(event) {
				if (!this.getEnabled()||!this.getTruevalue())
					return;
				
				/*debug Xwing.debug("isChecked() "+this.isChecked()); */
				var opt = {type:'changing',source:this,event:event,value : '',doit:true};
				if (this.isChecked()) {
					/*debug Xwing.debug("setvalue :"+this._opt.falsevalue); */
					opt.value = this._opt.falsevalue;
					if(this.getValue() != this._opt.falsevalue) this._fire("changing",opt);
					if(!opt.doit){
						this.checkbox_item.removeClass("xw-checkbox-checked-pressing");
						return;
					}
					this.setValue(this._opt.falsevalue);
				} else {
					/*debug Xwing.debug("setvalue: "+this._opt.truevalue); */
					opt.value = this._opt.truevalue;
					if(this.getValue() != this._opt.truevalue) this._fire("changing",opt);
					if(!opt.doit) {
						this.checkbox_item.removeClass("xw-checkbox-unchecked-pressing");
						return;
					}
					this.setValue(this._opt.truevalue);
				}
				this._fire('change', {
					type : 'change',
					source : this,
					value : this.getValue()
				});
			},		
			_doValue : function() {
				this._setChecked(this.getValue() == this._opt.truevalue);
			},
			setLabel : function(labelText) {
				this.checkbox_label.text(labelText);
			},
			_setChecked : function(check) {
				if (check) {
					this.checkbox_label.attr("value", "checked");
					this.checkbox_item.removeClass("xw-checkbox-unchecked-pressing").
					removeClass("xw-checkbox-unchecked").
					addClass("xw-checkbox-checked");
				} else {
					this.checkbox_label.attr("value", "unchecked");
					this.checkbox_item.removeClass("xw-checkbox-checked-pressing").
					removeClass("xw-checkbox-checked").
					addClass("xw-checkbox-unchecked");
				}
				/*debug Xwing.debug("getValue :" + this.getValue()); */
			},
			isChecked : function() {
				return this.checkbox_label.attr("value") == "checked";
			},
			setTruevalue : function(value) {
				this._opt.truevalue = value;
				this._doTruevalue();
			},
			getTruevalue : function() {
				return this._opt.truevalue;
			},
			_doTruevalue : function() {
				this._doValue();
			},
			setFalsevalue : function(value) {
				this._opt.falsevalue = value;
				this._doFalsevalue();
			},
			getFalsevalue : function() {
				return this._opt.falsevalue;
			},
			_doFalsevalue : function() {
				this._doValue();
			},
			setValign : function(v){
				this._opt.valign = v;
				this._doValign();
			},
			getValign : function(){
				return this._opt.valign;
			},
			_doValign : function(){
				this.checkbox_item_div.css('vertical-align', this._opt.valign);
				this.checkbox_label.css('vertical-align',this._opt.valign);
			},
			setHalign : function(v){
				this._opt.halign = v;
				this._doHalign();
			},
			getHalign : function(){
				return this._opt.halign;
			},
			_doHalign : function(){
				this.checkbox_label.css('text-align',this._opt.halign);
			},
			setIconalign : function(v){
				this._opt.iconalign = v;
				this._doIconalign();
			},
			getIconalign : function(){
				return this._opt.iconalign;
			},
			_doIconalign : function(){
				var checkbox_item = this.checkbox_item_div.remove();
				switch(this.getIconalign()){
				case 'left' :
						this.checkbox_label.before(checkbox_item);
					break;
				case 'right' :
						this.checkbox_label.after(checkbox_item);
					break;
				}
				this.checkbox_item_div = checkbox_item;
				checkbox_item = null;
			},			
			setIconmargin : function(v){
				this._opt.iconmargin = v;
				this._doIconmargin();
			},
			getIconmargin : function(){
				return this._opt.iconmargin;
			},
			_doIconmargin : function(){
				var margins = (this.getIconmargin() == null ? "" : this.getIconmargin().trim().split(/\s+/));
				var result = "";
				for(var i in margins){
					result += margins[i] + 'px ';
				}
				this.checkbox_item.css('margin',result);
			},
			setIconimage : function(v){
				this._opt.iconimage = v;
				this._doIconimage();
			},
			getIconimage : function(){
				return this._opt.iconimage;
			},
			_doIconimage : function(){
				if(this.getIconimage()){
					this.checkbox_item.css('background-image','url('+this.getIconimage()+')');
				}else{
					this.checkbox_item.css('background-image','');
				}
			},
			_doCursor : function(){
				if(window.xwingIDE) return;
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				this.checkbox_item.css('cursor',cursor);
				this.checkbox_label.css('cursor',cursor);
			},
			setTextwrap : function(v){
				this._opt.textwrap = v;
				this._doTextwrap(this.checkbox_label);
			},
			getTextwrap : function(){
				return this._opt.textwrap;
			}
		}
	}

});
