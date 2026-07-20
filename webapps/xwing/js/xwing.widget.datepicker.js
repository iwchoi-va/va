Class.define({
	Datepicker : {
		alias : 'datepicker',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Datepicker : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Datepicker(json);
			}
		},			
		prototypes : {
			_createPart : function(){
				this.datepicker = jQuery("<div class='xw-datepicker xw-mod-border'/>").appendTo(this._getJShell());
				this.datepicker_input = jQuery("<input type='text' class='xw-datepicker-input xw-mod-font xw-mod-background xw-mod-focus'/>").appendTo(this.datepicker);
				this.datepicker_div = jQuery("<div class='xw-datepicker-div'/>").appendTo(this.datepicker);
				this.datepicker_div_img = jQuery("<div class='xw-datepicker-div-img'/>").appendTo(this.datepicker_div);				
				
				// domain 
				this.specialDate = {};
				var thisObj = this;
				
				this.datepicker_input.datepicker({
					dateFormat : "yy-mm-dd",
					monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
					dayNamesMin : ['일','월','화','수','목','금','토'],
					showButtonPanel : true,
					closeText : '닫기',
					currentText : '오늘',
					gotoCurrent : false,
					changeMonth : true,
					changeYear : true,
					showOn : '',
					onSelect : function(dateText, inst) {
						thisObj._onSelect();
					},
					beforeShow : function(input, inst) {
						thisObj._onShow();
					},
					onClose : function(dateText, inst) {
						thisObj._onHide();
					},
					onChangeMonthYear : function(year,month,inst){
						window.setTimeout(function() {
							thisObj._setDayStyle(year,month,inst.dpDiv);
						}, 1);
					}
				});
				this._bind(this.datepicker_input, 'change', this._onChange);
				this._bind(this.datepicker_input, 'keydown', this._onKeydown);
				this._bind(this.datepicker_input, 'keyup', this._onKeyup);
				this._bind(this.datepicker_div, 'click', this._showDatepicker);
				this._bind(this.datepicker_input, 'focus', this._onFocus);
				this._bind(this.datepicker_input,'blur', this._onBlur);
				
				window.setTimeout(function() {
					thisObj.datepicker_input.datepicker("widget").find('a,select,button').attr('tabindex', '-1');
				}, 1000);
				
				/*debug Xwing.debug("Widget(Datepicker) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_onKeydown : function(event) {
				this._fire('keydown', {type:'keydown',source:this,event:event});
			},			
			_onKeyup : function(event) {
				if( jQuery.browser.opera || jQuery.browser.msie || (jQuery.browser.mozilla && parseFloat(jQuery.browser.version) >= 11) ) {
					// ie 11 
					var v = jQuery.datepicker.formatDate('yymmdd', this.datepicker_input.datepicker('getDate'));
					if( event.keyCode == 13 && this.getValue() != v) {
						this._onChange();
					}
				}
				this._fire('keyup', {type:'keyup',source:this,event:event});
			},
			_onSelect : function() {
				this._fire('select', {type:'select',source:this,event:null});

				var v = jQuery.datepicker.formatDate('yymmdd', this.datepicker_input.datepicker('getDate'));
				if (this.getValue() != v) {
					this._onChange();
				}
			},
			_onShow : function() {
				this._fire('show', {type:'show',source:this,event:null});
			},
			_onHide : function() {
				this._fire('hide', {type:'hide',source:this,event:null});
			},
			_onFocus : function() {
				if(this.getEnabled()){
					this.datepicker.addClass('xw-datepicker-focus');
				}else{
					this.datepicker.removeClass('xw-datepicker-focus');
				}
				
			},
			_onBlur : function(){
				this.datepicker.removeClass('xw-datepicker-focus');
			},
			setFormat : function(format) {
				if (arguments.length) {
					this._opt.format = format;
				}
				this._doFormat();
			},
			_doFormat : function() {
				var format = this._opt.format.replace('yyyy', 'yy');
				this.datepicker_input.datepicker("option", "dateFormat", format);

				var mask = this._opt.format.replace('yyyy', '9999').replace('mm', '19').replace('dd', '39');
				this.datepicker_input.setMask(mask);
			},
			getFormat : function() {
				return this._opt.format;
			},
			_doValue : function() {
				if (this.getValue()) {
				     try {
				      var dateObj = jQuery.datepicker.parseDate("yymmdd", this.getValue());
				      this.datepicker_input.datepicker("setDate", dateObj);
				     } catch (e) {
				      this.datepicker_input.val(""); 
				     }
			    } else {
			     this.datepicker_input.val(this.getValue());
			    }
				if(this.getPlaceholder()) this._checkPlaceholder();
			},
			_doDomain : function(){
				// style of special day
				var domainDS = Xwing.getDataset(this._opt.domaindataset);
				if(domainDS){
					for(var i=0; i < domainDS.size(); i++){
						var full = domainDS.getValue(i,this._opt.datecolumn);
						var font = domainDS.getValue(i,this._opt.fontcolorcolumn);
						var bg = domainDS.getValue(i,this._opt.bgcolorcolumn);
						var border = domainDS.getValue(i,this._opt.bordercolorcolumn);
						if(full && full.length == 8){
							var style = "";
							if(font) style +="color:"+font+" !important;";
							if(bg) style += "background-color:"+bg+" !important;";
							if(border) style += "border-color:"+border+" !important;";
							var year = full.substr(0,4);
							var mon = parseFloat(full.substr(4,2));
							var day = parseFloat(full.substr(6,2));
							if(this.specialDate[year] == undefined) this.specialDate[year] = {};
							if(this.specialDate[year][mon] == undefined) this.specialDate[year][mon] = [];
							this.specialDate[year][mon].push([day,style]);
						}
					}
				}
				
			},
			_onChange : function(event) {
				var v = jQuery.datepicker.formatDate('yymmdd', this.datepicker_input.datepicker('getDate'));
				var opt = {type:'changing',source:this,event:event,doit:true,value:v};
				if(this.getValue() != v) this._fire("changing",opt);
				if(!opt.doit){
					this._doValue();
					return;
				}
				this.setValue(v);				
				this._fire('change',  {type:'change',source:this,event:null,value:v});
			},
			_showDatepicker : function() {
				if (this.getEnabled()) {
					this.datepicker_input.datepicker('option','showOtherMonths',this.getShowothermonth());
					this.datepicker_input.datepicker('option','selectOtherMonths',this.getShowothermonth());
					this.datepicker_input.datepicker('show');
					var year;
					var mon;
					if(this.getValue()){
						year = this.getYear();
						mon = parseFloat(this.getMonth());
					}else{
						var date = new Date();
						year = date.getFullYear();
						mon = parseFloat(date.getMonth()+1);
					}
					this._setDayStyle(year,mon,this.datepicker_input.datepicker('widget'));
				}
			},
			_setDayStyle : function(year,mon,jDiv){
				var others = jDiv.find('.ui-datepicker-other-month').children();
				var days = jDiv.find('td:not(.ui-datepicker-other-month)').children();
				var style = '';
				if(this.getOthermonthbgcolor())	style += "background-color:"+this.getOthermonthbgcolor()+' !important;';
				if(this.getOthermonthbordercolor()) style += "border-color:"+this.getOthermonthbordercolor()+' !important;';
				if(this.getOthermonthfontcolor()) style += "color:"+this.getOthermonthfontcolor()+' !important;';
				others.css('cssText',style);
				
				style = '';
				if(this.getDaybgcolor()) style += "background-color:"+this.getDaybgcolor()+' !important;';
				if(this.getDaybordercolor()) style += "border-color:"+this.getDaybordercolor()+' !important;';
				if(this.getDayfontcolor()) style += "color:"+this.getDayfontcolor()+' !important;';
				days.css('cssText',style);
				
				if(typeof(this.specialDate) == 'object'){
					if(this.specialDate[year]){
						if(this.specialDate[year][mon]){
							var days = this.specialDate[year][mon];
							for(var i=0; i < days.length; i++){
								jDiv.find("td:not(.ui-datepicker-other-month):eq("+(days[i][0]-1)+")").children().css('cssText',days[i][1]);
							}
						}
					}					
				}
			},
			_render : function() {
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doBounds();
				this.setFormat();
			},
			_doBounds : function() {	
				xwing.widget.Widget.prototype._doBounds.call(this);		
				var borderwidth = (this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth());
				this.datepicker_input.width(this.getWidth() - 18 - borderwidth*2);
				this.datepicker_input.height(this.getHeight() - borderwidth*2);
				if(Xwing.isIE()) this.datepicker_input.css('line-height',(this.getHeight() -borderwidth*2) +"px");
				if(this.getPlaceholder()) this._resetPlaceholderheight();
			},
			_doBorder : function(){
				var borderMod = this._getModule('border');
				xwing.widget.Widget.prototype._doBorder.call(this);
				
				var borderwidth = (this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth());
				this.datepicker_input.width(this.getWidth() - 18 - borderwidth*2);
				this.datepicker_input.height(this.getHeight() - borderwidth*2);
				if(Xwing.isIE()) this.datepicker_input.css('line-height',(this.getHeight() -borderwidth*2) +"px");
			},
			getFormatedValue : function(v) {
				if (! arguments.length) {
					v = this.getValue();
				}		
				if (this.getFormat() && v) {
					var format = this._opt.format.replace('yyyy', 'yy');
					v = jQuery.datepicker.formatDate(format,this.datepicker_input.datepicker('getDate'));
				}
				return v;
			},
			_doCursor : function(){
				if(window.xwingIDE) return;			
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				this.datepicker_div_img.css('cursor',cursor);
			},
			setDaybgcolor : function(v){
				this._opt.daybgcolor = v;
			},
			getDaybgcolor : function(){
				return this._opt.daybgcolor;
			},
			setDaybordercolor : function(v){
				this._opt.daybordercolor = v;
			},
			getDaybordercolor : function(){
				return this._opt.daybordercolor;
			},
			setDayfontcolor : function(v){
				this._opt.dayfontcolor = v;
			},
			getDayfontcolor : function(){
				return this._opt.dayfontcolor;
			},
			setShowothermonth : function(v){
				this._opt.showothermonth = v;
			},
			getShowothermonth : function(){
				return xwing.Util.parseBoolean(this._opt.showothermonth,false);
			},
			setOthermonthbgcolor : function(v){
				this._opt.othermonthbgcolor = v;
			},
			getOthermonthbgcolor : function(){
				return this._opt.othermonthbgcolor;
			},
			setOthermonthbordercolor : function(v){
				this._opt.othermonthbordercolor = v;
			},
			getOthermonthbordercolor : function(){
				return this._opt.othermonthbordercolor;
			},
			setOthermonthfontcolor : function(v){
				this._opt.othermonthfontcolor = v;
			},
			getOthermonthfontcolor : function(){
				return this._opt.othermonthfontcolor;
			},
			getYear : function(){
				var date = this.datepicker_input.datepicker('getDate');
				if(date) return date.getFullYear();
				return '';
			},
			getMonth : function(){
				var date = this.datepicker_input.datepicker('getDate');
				if(date) return parseFloat(date.getMonth()) + 1;
				return "";
			},
			getDate : function(){
				var date = this.datepicker_input.datepicker('getDate');
				if(date) return date.getDate();
				return '';
			},
			getDayOfWeek : function(){
				var date = this.datepicker_input.datepicker('getDate');
				var weeks = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
				if(date) return weeks[date.getDay()];
				return "";
			},
			setDatecolumn : function(v){
				this._opt.datecolumn = v;
			},
			getDatecolumn : function(){
				return this._opt.datecolumn;
			},
			setBgcolorcolumn : function(v){
				this._opt.bgcolorcolumn = v;
			},
			getBgcolorcolumn : function(){
				return this._opt.bgcolorcolumn;
			},
			setBordercolorcolumn : function(v){
				this._opt.bordercolorcolumn = v;
			},
			getBordercolorcolumn : function(){
				return this._opt.bordercolorcolumn;
			},
			setFontcolorcolumn : function(v){
				this._opt.fontcolorcolumn = v;
			},
			getFontcolorcolumn : function(){
				return this._opt.fontcolorcolumn;
			},
			focus : function(){
				this.datepicker_input[0].focus();
			}
		}
	}
});
