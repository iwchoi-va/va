Class.define({
	Slider : {
		alias : 'slider',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Slider : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Slider(json);
			}
		},
		prototypes : {			
			_createPart : function(){
				this._jBar = jQuery("<div class='xw-slider xw-slider-center xw-mode xw-mod-border xw-mod-background' unselectable='on' />");
				this._jThumb = jQuery("<div class='xw-slider-thumb-h xw-mod' unselectable='on' />").appendTo(this._jBar);
				this._getJShell().append(this._jBar);
				
				this._doDirection();
				this._bind(this._jThumb,'mousedown',this._mouseDown);
				this._bind(this._jBar,'click',this._mouseDrag);
				this._bind(jQuery(document),'mousemove',this._mouseDrag);
				this._bind(jQuery(document),'mouseup',this._mouseUp);
			},
			_render : function(){
				this._doThumblabel();
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doSidemargin();
				this._doThumbimage();
				this._doThumbleft();
				this._doThumbtop();
				this._doPointlabel();
			},
			_mouseDown : function(event){
				if(!this.getEnabled()) return;
				this._slide = true;
				if(!this.getThumblabel()) this._displayMessage();
			},
			_mouseDrag : function(event){
				if(!this.getEnabled()) return;
				if(event.type == 'mousemove' && !this._slide) return;
				if((this.getMax() - this.getMin()) <= 0 ) return;

				var parent =( !this._parentWidget || this._parentWidget.getAlias() == 'page' ) ? 0 : (this.getDirection() == 'horizontal' ? this._parentWidget.getLeft() : this._parentWidget.getTop());
				var coordinate = ((this.getDirection() == 'horizontal') ? event.clientX  + this.getThumbleft(): event.clientY + this.getThumbtop()- xwing.Util.parseInt(this._jThumb.height(),0)/2) - parent;
				var offset =  coordinate - xwing.Util.parseInt(this._getJShell().css(this.direction), 0) + this.getSidemargin();
				var barSize = xwing.Util.parseInt(this._jBar.css(this.bound),0);
				if(offset < 0 ) offset = 0; 
				else if(offset > barSize) offset = barSize;
				if(this.getDirection() == 'vertical') offset = barSize - offset;
				
				var interval = barSize / ((this.getMax()-this.getMin()) / this.getStep());
				var newValue = (offset/interval)*this.getStep() + this.getMin();
				newValue = parseFloat(newValue.toFixed(this._getDecimalLength()));
				
				var opt = {type : 'changing',source : this,	event : event,value : newValue,	doit : true};
				this._fire("changing", opt);
				
				this.setValue(newValue);
				this._moveMessage();
				this._fire('change', {type : 'change',source : this,event : event,value : newValue});
				this._fire('slide',{type : 'slide', source: this, event : event});
			},
			_mouseUp : function(event){
				if(!this.getEnabled()) return;
				this._slide = false;
				if(!this.getThumblabel()) this._removeMessage();
			},
			_displayMessage : function(){
				var msg = jQuery('span.xw-slider-message',this.getShell());
				if(msg.length == 0){
					msg = jQuery("<span class='xw-slider-message'></span>").appendTo(this.getShell());
				}
				this._moveMessage();
			},
			_moveMessage : function(){
				var msg = jQuery('span.xw-slider-message',this.getShell()).text(this.getValue());
				if(this.getExprthumblabel()){
					var text = this._callExpr(this.getExprthumblabel(), [this.getValue(), this.getMax()]);
					msg.text(text || this.getValue());
				}
				if(this.getDirection() == 'horizontal'){
					msg.css({left : this._jThumb.offset().left - this._getJShell().offset().left + (this._jThumb.width()/2) - xwing.Util.parseFloat(msg.css('width'),0)/2, top : '-20px'});
				}else{
					msg.css({left : (this.getWidth() + 10), top : this._jThumb.offset().top - this._getJShell().offset().top + (this._jThumb.height()/2) - xwing.Util.parseFloat(msg.css('height'),0)/2});
				}
			},
			_callExpr : function(expr, opts){
				try{
					var f;
					if( typeof this.getExprthumblabel() == 'string'){
						f = window[this.getExprthumblabel()];
					}
					if(f === undefined || f == null) return;
					return f.apply(this, opts);
				}catch(e){
				}
			},
			_removeMessage : function(){
				jQuery('span.xw-slider-message',this.getShell()).remove();
			},
			_doValue:function(){
				var offset = (this.getValue()- this.getMin())/(this.getMax() - this.getMin())* 100;
				if(this.getDirection() == 'vertical') offset = 100 - offset; 
				this._jThumb.css(this.direction,offset+'%');
				this._moveMessage();
			},
			setDirection : function(v){
				this._opt.direction = v;
				this._doDirection();
			},
			getDirection : function(){
				return this._opt.direction;
			},
			_doDirection : function(){
				if(this.getDirection() == 'horizontal'){
					this.direction = 'left', this.bound = 'width' ;
					this._jThumb.addClass('xw-slider-thumb-h').removeClass('xw-slider-thumb-v');
				}else{
					this.direction = 'top', this.bound = 'height' ;
					this._jThumb.addClass('xw-slider-thumb-v').removeClass('xw-slider-thumb-h');
				}
				this._doValue();
				this._doSidemargin();
			},
			setMax : function(v){
				this._opt.max = v;
			},
			getMax : function(){
				return xwing.Util.parseFloat(this._opt.max,0);
			},
			setMin : function(v){
				this._opt.min = v;
			},
			getMin : function(){
				return xwing.Util.parseFloat(this._opt.min,0);
			},
			setSidemargin : function(v){
				this._opt.sidemargin = v;
				this._doSidemargin();
			},
			getSidemargin : function(){
				return xwing.Util.parseInt(this._opt.sidemargin,0);
			},
			_doSidemargin : function(){
				if(this.getDirection() == 'horizontal')
					this._getJShell().css('padding','0px '+this.getSidemargin()+'px');
				else
					this._getJShell().css('padding',this.getSidemargin()+'px 0px');
			},
			setStep : function(v){
				this._opt.step = v;
			},
			getStep : function(){
				return xwing.Util.parseFloat(this._opt.step,1);
			},
			nextStep : function(){
				if(!this.getEnabled()) return;
				this._executeStep(xwing.Util.parseFloat(this.getValue(),0) + xwing.Util.parseFloat(this.getStep(),0));
			},
			prevStep : function(){
				if(!this.getEnabled()) return;
				this._executeStep(xwing.Util.parseFloat(this.getValue(),0) - xwing.Util.parseFloat(this.getStep(),0));
			},
			_executeStep : function(val){
				if((this.getMax() - this.getMin()) <= 0 ) return;
				if(this.getMax() < val) val = this.getMax();
				else if(this.getMin() > val ) val = this.getMin();
				
				var opt = {type : 'changing',source : this,	event : null,value : val,	doit : true};
				this._fire("changing", opt);
				
				this.setValue(val);
				this._fire('change', {type : 'change',source : this,event : null,value : val});
				this._fire('slide',{type : 'slide', source: this, event : null});
			},
			setThumblabel : function(v){
				this._opt.thumblabel = v;
				this._doThumblabel();
			},
			getThumblabel : function(){
				return xwing.Util.parseBoolean(this._opt.thumblabel,false);
			},
			_doThumblabel : function(){
				if(this.getThumblabel()){
					this._displayMessage();
				}else this._removeMessage();
			},
			setThumbimage : function(v){
				this._opt.thumbimage = v;
				this._doThumbimage();
			},
			getThumbimage : function(){
				return this._opt.thumbimage;
			},
			_doThumbimage : function(){
				if(this.getThumbimage()){
					var img = new Image();
					var thisObj = this;
					img.onload = function(event){
						thisObj._jThumb.width(this.width);
						thisObj._jThumb.height(this.height);
					};
					img.src = this.getThumbimage();
					this._jThumb.css('background-image','url('+this.getThumbimage()+')');
				}else
					this._jThumb.css({
						width : '',
						height : '',
						'background-image' : ''
					});
				
			},
			setThumbleft : function(v){
				this._opt.thumbleft = v;
				this._doThumbleft();
			},
			getThumbleft : function(){
				return xwing.Util.parseInt(this._opt.thumbleft,0);
			},
			_doThumbleft : function(){
				this._jThumb.css('margin-left',this.getThumbleft()+"px");
			},
			setThumbtop : function(v){
				this._opt.thumbtop = v;
				this._doThumbtop();
			},
			getThumbtop : function(){
				return xwing.Util.parseInt(this._opt.thumbtop,0);
			},
			_doThumbtop : function(){
				this._jThumb.css('margin-top',this.getThumbtop()+'px');
			},
			setExprthumblabel : function(v){
				this._opt.exprthumblabel = v;
			},
			getExprthumblabel : function(){
				return this._opt.exprthumblabel;
			},
			setPointlabel : function(v){
				this._opt.pointlabel = v;
			},
			getPointlabel : function(){
				return xwing.Util.parseBoolean(this._opt.pointlabel,false);
			},
			_doPointlabel : function(){
				if(this.getPointlabel()){
					if(jQuery('span.xw-slider-min',this.getShell()).length == 0) jQuery("<span class='xw-slider-text xw-slider-min' />").text(this.getMin()).appendTo(this.getShell());
					if(jQuery('span.xw-slider-max',this.getShell()).length == 0) jQuery("<span class='xw-slider-text xw-slider-max' style='float:right;' />").text(this.getMax()).appendTo(this.getShell());
					this._setPosition();
				}else{
					jQuery('span.xw-slider-text',this.getShell()).remove();
				}
			},
			_setPosition : function(){
				var min = jQuery('span.xw-slider-min',this.getShell());
				var max = jQuery('span.xw-slider-max',this.getShell());
				if(this.getDirection() == 'horizontal'){
					min.removeClass('xw-slider-text-v');
					max.removeClass('xw-slider-text-v');
				}else{
					max.addClass('xw-slider-text-v').css('left',(this.getWidth()+20)*-1+'px');
					min.addClass('xw-slider-text-v').css({
						'left' : (this.getWidth()+20)*-1+'px',
						'margin-top' : this.getSidemargin()*-2+'px'
					});
				}
			},
			_doBounds : function(){
				xwing.widget.DataBindable.prototype._doBounds.call(this);
				
				var msg = jQuery('span.xw-slider-message',this.getShell());
				if(msg.length != 0 && this.getDirection() == 'vertical'){
					msg.css({top : this.getHeight() + this.getThumbtop()});
				}
			},
			_getDecimalLength : function(){
				var step = new String(this.getStep());
				var idx = step.indexOf('.');
				if(idx < 0 ) return 0;
				else return step.substring(idx+1).length;
			}
		}	
	}
});