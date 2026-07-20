Class.define({
	Progressbar : {
		alias : 'progressbar',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Progressbar : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Progressbar(json);
			}
		},
		prototypes : {
			_createPart : function(){
				//Xwing.debug("[creatPart]============================================");
				this.jBar = jQuery("<div class='xw-progressbar xw-mod-border'/>");
				this.barDiv = jQuery("<div class='xw-mod '></div>").appendTo(this.jBar);
				this.barText = jQuery("<span class='xw-mod xw-mod-font '></span>").appendTo(this.jBar);
				this._getJShell().append(this.jBar);
			},
			_initBg : function(){
				var bgcss = {
					'background-image' : '',
					'background-color' : '',
					'background-position' : ''
				};
				this.barDiv.css(bgcss);
				this.jBar.css(bgcss);
			},
			_render : function(){
				this._doDirection();
				this._doLabel();
				xwing.widget.DataBindable.prototype._render.call(this);
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);
				this._resetBounds();
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				this._resetBounds();
			},
			_resetBounds : function(){
				this.barText.width(this.getWidth()-this.getBorderwidth()*2);
				this.barText.css('line-height',(this.getHeight()-this.getBorderwidth()*2)+'px');
				this._doValue();
			},
			setBarbgcolor : function(v){
				this._opt.barbgcolor = v;
				this._doBarbg();
			},
			getBarbgcolor : function(){
				return this._opt.barbgcolor;
			},
			setBarbggradientcolor : function(v){
				this._opt.barbggradientcolor = v;
				this._doBarbg();
			},
			getBarbggradientcolor : function(){
				return this._opt.barbggradientcolor;
			},
			setBarbgimage : function(v){
				this._opt.barbgimage = v;
				this._doBarbg();
			},
			getBarbgimage : function(){
				return this._opt.barbgimage;
			},
			_doBarbg : function(){
				// color -> bgimage -> gradation 
				var module = (this.getDirection() == 'horizontal') ? this.barDiv : this.jBar;
				 if(this.getBarbggradientcolor() && this.getBarbgcolor()){
					 if(this.getDirection() == 'horizontal'){
						 if(jQuery.browser.msie){
							 module.css("filter","progid:DXImageTransform.Microsoft.gradient(enabled='true',startColorstr="+this.getBarbggradientcolor()+", endColorstr="+this.getBarbgcolor()+")")
						 }else if(jQuery.browser.mozilla){
							 module.css('background',"-moz-linear-gradient("+this.getBarbggradientcolor()+", "+this.getBarbgcolor()+" 50%, "+this.getBarbggradientcolor()+")")
						 }else if(jQuery.browser.opera){
							 module.css('background',"-o-linear-gradient("+this.getBarbggradientcolor()+", "+this.getBarbgcolor()+" 50%, "+this.getBarbggradientcolor()+")");
						 }else if(jQuery.browser.webkit){
							 module.css('background',"-webkit-gradient(linear, 0 0, 0 bottom, from("+this.getBarbggradientcolor()+"), color-stop(.5, "+this.getBarbgcolor()+"), to("+this.getBarbggradientcolor()+") )")
						 }
					 }else{
						 if(jQuery.browser.msie){
							 module.css("filter","progid:DXImageTransform.Microsoft.gradient(GradientType=1,startColorstr="+this.getBarbggradientcolor()+", endColorstr="+this.getBarbgcolor()+")")
						 }else if(jQuery.browser.mozilla){
							 module.css("background","-moz-linear-gradient(left center,"+this.getBarbggradientcolor()+", "+this.getBarbgcolor()+" 50%, "+this.getBarbggradientcolor()+")");
						 }else if(jQuery.browser.opera){
							 module.css("background","-o-linear-gradient(360deg,"+this.getBarbggradientcolor()+", "+this.getBarbgcolor()+" 50%, "+this.getBarbggradientcolor()+")");
						 }else if(jQuery.browser.webkit){
							 module.css("background","-webkit-gradient(linear, 0 0, 100% 0%, from("+this.getBarbggradientcolor()+"), color-stop(.5, "+this.getBarbgcolor()+"), to("+this.getBarbggradientcolor()+") )");
						 }
					 }
				 }else if(this.getBarbgimage() ){
					 module.css({
							'background-image':'url('+this.getBarbgimage()+')',
							'background-color':''
						});
					if(jQuery.browser.msie) module.css('filter','');
				 }else if(this.getBarbgcolor() ){
					 module.css({
							'background-image':'none',
							'filter' : 'none',
							'background-color' : this.getBarbgcolor()
						});
				 }
			},
			setLabel : function(v){
				this._opt.label = v;
				this._doLabel();
			},
			getLabel : function(){
				return this._opt.label;
			},
			_doLabel : function(){
				this.barText.text(this.getLabel());
			},
			setMax : function(v){
				this._opt.max = v;
			},
			getMax : function(){
				return xwing.Util.parseInt(this._opt.max,0);
			},
			setStep : function(v){
				this._opt.step = v;
			},
			getStep : function(){
				return xwing.Util.parseInt(this._opt.step,0);
			},
			_calcBarSize : function(type){
				if(this.getValue() < 0 ) this._opt.value = 0;
				if(this.getValue() > this.getMax()) this._opt.value = this.getMax();
				
				var curValue = this.getValue();
				if(this.getDirection() == 'vertical') curValue = this.getMax() - this.getValue();
				
				var size = curValue/this.getMax();
				size = xwing.Util.parseInt(size*(this.getAttribute(type) - this.getBorderwidth()*2),0);
				return size;
			},
			_doValue : function(){
				var type = this.getDirection() == 'horizontal'? 'width' : 'height'; 
				var size = this._calcBarSize(type);
				
				var obj = {};
				obj[type] = size;
				
				if(this.getAnimation()){
					this.barDiv.animate(obj, 1000);
				}else{
					this.barDiv.css(obj);
				}				
				
				if(this._opt.expr) {
					var text = this._callExpr(this.getExpr(), [this.getValue(), this.getMax()]);
					this.barText.text(text || this.getLabel());
				}
			},
			_callExpr : function(expr, opts){
				try{
					var f;
					if( typeof this.getExpr() == 'string'){
						f = window[this.getExpr()];
					}
					if(f === undefined || f == null) return;
					return f.apply(this, opts);
				}catch(e){
				}
			},			
			setDirection : function(v){
				this._opt.direction = v;
				this._doDirection();
			},
			getDirection : function(){
				return this._opt.direction;
			},
			_doDirection : function(){
				this._initBg();
				
				if(this.getDirection() == 'horizontal'){
					this.barDiv.addClass('xw-progressbar-h xw-progress-bar-hcolor').removeClass('xw-progressbar-v xw-mod-background');
					this.barText.addClass('xw-progressbar-h').removeClass('xw-progressbar-v');
					this.jBar.addClass('xw-mod-background').removeClass('xw-progress-bar-vcolor');
				}else{
					this.barDiv.addClass('xw-progressbar-v xw-mod-background').removeClass('xw-progressbar-h xw-progress-bar-hcolor');
					this.barText.addClass('xw-progressbar-v').removeClass('xw-progressbar-h');
					this.jBar.addClass('xw-progress-bar-vcolor').removeClass('xw-mod-background');
				}
				
				this._doBarbg();
				this._doBackground();				
			},
			setExpr : function(v){
				this._opt.expr = v;
			},
			getExpr : function(){
				return this._opt.expr;
			},
			setAnimation : function(v){
				this._opt.animation = v;
			},
			getAnimation : function(){
				return xwing.Util.parseBoolean(this._opt.animation, false);
			},
			nextStep : function(){
				if(!this.getEnabled()) return;
				var val = xwing.Util.parseInt(this.getValue(),0)+this.getStep();
				this.setValue(val);
			},
			prevStep : function(){
				if(!this.getEnabled()) return;
				var val = xwing.Util.parseInt(this.getValue(),0)-this.getStep();
				this.setValue(val);
			}
		}
	}
});