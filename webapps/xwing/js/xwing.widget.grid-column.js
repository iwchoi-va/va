Class.define({
	Gridcolumn : {
		alias : "grid-column",
		namespace : "xwing.widget",
		extend : xwing.widget.Widget,
		Gridcolumn :function(json){
			this._init(json);
			this.containerWidget = this;
			/*debug Xwing.debug("Widget(Grid-column) created : "); */
		},
		statics : {
			create : function(json){
				return new xwing.widget.Gridcolumn(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.resizable = false;
				this._opt.anchor = 'none';
				this.headCol = jQuery("<col class='xw-mod'/>");
				this.bodyCol = jQuery("<col class='xw-mod'/>");
				this.summaryCol = jQuery("<col class='xw-mod'/>");
				this.th = jQuery("<th class='xw-mod' />"); 
			},
			_getHeadCol : function(){
				return this.headCol;
			},
			_getBodyCol : function(){
				return this.bodyCol;
			},
			_getSummaryCol : function(){
				return this.summaryCol;
			},
			_getTh : function(){
				return this.th;
			},
			_createEditCell : function(dsObj,height){
				var cell;
				if(this.getBindcolumn()){
					cell = this.editCell(dsObj, height);
				}else{
					cell = jQuery('<span style="display:inline;" class="xw-mod-font" />');
				}
				return cell;
			},
			editCell : function(dsObj,height){ 
				var w = xwing.Util.parseInt(this.th.width(), 0) - 2,
					h = height - 2,
					opt = {}, 
					options;
				
				opt.id = this.grid.getId() + "_" + this.getId();
				opt.width = w;
				opt.height = h;
				
				if(dsObj && this.getBindcolumn()){
					opt.binddataset = this.grid.getBinddataset();
					opt.bindcolumn = this.getBindcolumn();
				}
				
				if(this.getDomaindataset() && this.getDomaincodecolumn() && this.getDomaintextcolumn()){
					opt.domaindataset = this.getDomaindataset();
					opt.domaincodecolumn = this.getDomaincodecolumn();
					opt.domaintextcolumn = this.getDomaintextcolumn();
				}

				if (this.getEditoption()) {
					try {
						options = eval('({' + (this.getEditoption() || '') + '})');
						opt = jQuery.extend(opt, options);
					} catch (e) {
						Xwing.error("Error on " + opt.id + " while parsing editoption : " + e);
						
						// because of a previous version compatibility
						if (this.getEdittype() == 'checkbox') {
							var eOpt = this.getEditoption().split(",");
							if (eOpt.length == 2) {
								opt.truevalue = eOpt[0];
								opt.falsevalue = eOpt[1];
							}
						} else if (this.getEdittype() == 'edit') {
							opt.mode = this.getEditoption();
						}
					}
				}
				
				var widget = null;
				switch (this.getEdittype()) {
					case 'combo' :
						widget = new xwing.widget.Combo(opt);
						break;
					case 'checkbox':
						opt.width = 14;
						opt.halign = 'center';
						widget = new xwing.widget.Checkbox(opt);
						break;
					case 'radio':
						widget = new xwing.widget.Radio(opt);
						break;
					case 'datepicker' :
						widget = new xwing.widget.Datepicker(opt);
						break;
					case 'edit':
						widget = new xwing.widget.Edit(opt);
						break;
					case 'none':
						return jQuery(this._makeTextCell(dsObj, '0', null, {})).attr('_edittype', 'none');
				}
				
				widget.setParentWidget(this.grid);
				return widget.getShell();
			},
			_getValue : function(dataset, rowIdx){
				var opt = [];
				opt.value = dataset.getValue(rowIdx,this.getBindcolumn());
				if(this.getDomaindataset() && this.getDomaincodecolumn() && this.getDomaintextcolumn()){
					opt.domaindataset = this.getDomaindataset();
					opt.domaincodecolumn = this.getDomaincodecolumn();
					opt.domaintextcolumn = this.getDomaintextcolumn();
					var domainValue = Xwing.getDataset(opt.domaindataset).lookUp(opt.domaincodecolumn,opt.value,opt.domaintextcolumn);
					opt.value = domainValue;
				}
				opt.mask = this.getMask();
				var maskValue = xwing.widget.DataBindable.prototype.getMaskedValue.call(this,opt.value);
				 /(&|>|<|"|')/.test(maskValue) && (maskValue = maskValue.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;"));
				return maskValue;
			},
			setTitle : function(title){
				this._opt.title = title;
				this._doTitle();
			}, 
			_doTitle : function(){
				this.th.html(this.getTitle());
				this._doSortable();
				this._doResizable();
			},
			getTitle : function(){
				return this._opt.title;
			},
			_appended : function(){
				this._doTitle();
			},
			_setParentWidget : function(p){
				this.grid = p;
			},
			getShell : function(){
				return this.grid.getShell();
			},
			_render : function(){
				this._doBounds();
			},
			_doBounds : function(){
				this.headCol.width(this.getWidth());
				this.bodyCol.width(this.getWidth());
				this.summaryCol.width(this.getWidth());
				if(this.grid) this.grid._resetColRate();
				if(this.grid) this.grid._doAutofit();
			},
			_branding : function(){
				jQuery("*", this.headCol).andSelf().addClass("xw-mod");
				jQuery("*", this.bodyCol).andSelf().addClass("xw-mod");
				jQuery("*", this.summaryCol).andSelf().addClass("xw-mod");
				if(this._opt._xid){
					jQuery("*", this.headCol).andSelf().attr("_xid", this._opt._xid);
					jQuery("*", this.bodyCol).andSelf().attr("_xid", this._opt._xid);
					jQuery("*", this.summaryCol).andSelf().attr("_xid", this._opt._xid);
				}
			},
			setBindcolumn : function(column) {
					this._opt.bindcolumn = column;
					this.grid._doValue();
			},		
			getBindcolumn : function(){
				return this._opt.bindcolumn;
			},
			setDomaindataset : function(v){
				this._opt.domaindataset = v;
				this.grid._doValue();
			},
			getDomaindataset : function(){
				return this._opt.domaindataset;
			},
			setDomaincodecolumn: function(v){
				this._opt.domaincodecolumn = v;
				this.grid._doValue();
			},
			getDomaincodecolumn : function(){
				return this._opt.domaincodecolumn;
			},
			setDomaintextcolumn : function(v){
				this._opt.domaintextcolumn = v;
				this.grid._doValue();
			},
			getDomaintextcolumn : function(){
				return this._opt.domaintextcolumn;
			},
			setEdittype : function(v){
				this._opt.edittype = v;
			},
			getEdittype : function(){
				return this._opt.edittype;
			},
			setEditoption : function(v){
				this._opt.editoption = v;
			},
			getEditoption : function(){
				return this._opt.editoption;
			},
			_doSortable : function(){
				if(window.xwingIDE) return false;
				var thisObj = this;
				var sorter = jQuery(".xw-grid-sort-icon",  thisObj.th);				
				if(thisObj.grid.getSortable() && !sorter.size()){
					this.th.sorter = jQuery("<span class='xw-grid-sort-icon' style='display:none;' />");
					this.th.append(this.th.sorter);
					this._bind(this.th, 'click', function(event){
						/*debug Xwing.debug(thisObj.grid.getBinddataset() + ", " + thisObj.getBindcolumn()); */
						if(Xwing.isIE() && thisObj.resizable){
							thisObj.resizable = false;
							return false;
						}
						if(!thisObj.grid.sortFlag){
							thisObj.grid.sortFlag = true;
							return false;
						}
						var dataset = Xwing.getDataset(thisObj.grid.getBinddataset());
						if(dataset && thisObj.getBindcolumn() && dataset.hasColumn(thisObj.getBindcolumn()) ){
							var result = false;
							if(!thisObj.th.sorter.hasClass('xw-grid-sort-icon-up')){
								if(thisObj.getDomaindataset() && thisObj.getDomaincodecolumn() && thisObj.getDomaintextcolumn())result = dataset.sort(thisObj.getBindcolumn(),false,thisObj.getDomaindataset(),thisObj.getDomaincodecolumn(),thisObj.getDomaintextcolumn()); 
								else result = dataset.sort(thisObj.getBindcolumn(),false);
								if(result)	thisObj.th.sorter.addClass('xw-grid-sort-icon-up').removeClass('xw-grid-sort-icon-down');							
							}else{
								if(thisObj.getDomaindataset() && thisObj.getDomaincodecolumn() && thisObj.getDomaintextcolumn())result = dataset.sort(thisObj.getBindcolumn(),true,thisObj.getDomaindataset(),thisObj.getDomaincodecolumn(),thisObj.getDomaintextcolumn()); 
								else result = dataset.sort(thisObj.getBindcolumn(),true);
								if(result)	thisObj.th.sorter.addClass('xw-grid-sort-icon-down').removeClass('xw-grid-sort-icon-up');
							}
							if(result){
								for(var i=0; i < thisObj.grid.columnList.length; i++){
									if(thisObj.grid.columnList[i]._getTh().sorter){
										thisObj.grid.columnList[i]._getTh().sorter.hide();
									}
								}
								thisObj.th.sorter.show();
							}
						}				
					});
					return true;
				}else if(!thisObj.grid.getSortable() && sorter.size()){
					sorter.empty().remove();
					return true;
				}
			},
			_doResizable : function(){
				if(window.xwingIDE) return false;
				var thisObj = this;
				var resizer = jQuery(".xw-grid-column-resizer",  thisObj.th);				
				if(thisObj.grid.getResizable() && !resizer.size()){
					this.th.append(
						jQuery("<div class='xw-grid-column-resizer'/>").height(
							this.grid.getHeadheight()
						).draggable({
								cursor: 'e-resize',
								axis: "x" ,
								helper : function(){
									return jQuery("<div class='xw-grid-column-resizer-ghost'/>").height(thisObj.grid.getHeight());
								},
								stop : function(event, ui){
									if(Xwing.isIE()) thisObj.resizable = true;
									var w = ui.position.left - thisObj.th.position().left;
									var diff = w - thisObj.th.width();
									var gridw = thisObj.grid.header_table.width() + diff;
									/*debug Xwing.debug("diff:"+diff); */
									/*debug Xwing.debug("gridw w:"+ thisObj.grid.header_table.width() + ">"+gridw); */
									/*debug Xwing.debug('cell w:'+ thisObj.th.width() +">" + w); */
									thisObj.setWidth(w);
								}
						})
					);
					return true;
				}else if(!thisObj.grid.getResizable() && resizer.size()){
					resizer.draggable('destroy');
					resizer.empty().remove();
					return true;
				}
			},
			remove : function(){
				/*debug Xwing.debug('grid-column remove invoked..'); */
				this.grid.removeColumn(this);
			},
			setMask : function(v){
				this._opt.mask = v;
				this._doMask();
			},
			getMask : function(){
				return this._opt.mask;
			},
			_doMask : function(){
				// TODO 
			},
			setSuppress : function(v){
				this._opt.suppress = v;
			},
			getSuppress : function(){
				return xwing.Util.parseBoolean(this._opt.suppress,false);
			},
			setTooltip : function(v){
				this._opt.tooltiptext = v;
				this._doTooltip();
			},
			getTooltip : function(){
				return xwing.Util.parseBoolean(this._opt.tooltip);
			},
			_doTooltip : function(){
				var jTool = jQuery('span.xw-tooltiptext[_xid='+this.getXId()+']');
				if(jTool.length == 0 && this.getTooltip()) {
					jTool = jQuery("<span class='xw-mod xw-tooltiptext' _xid='"+this.getXId()+"' />").hide();
					jQuery('body').append(jTool);
				}else{
					jTool.empty().remove();
					delete jTool;
				}
			},
			setHalign : function(v) {
				this._opt.halign = v;
				this._doAlign();
			},
			getHalign : function() {
				return this._opt.halign;
			},
			setValign : function(v) {
				this._opt.valign = v;
				this._doAlign();
			},
			getValign : function() {
				return this._opt.valign;
			},
			_doAlign : function(){
				var tds = jQuery('td[_xid='+this.getXId()+']',this.grid.body_tbody).not('.xw-grid-checkbox, .xw-grid-rownum');
				tds.attr('align',this._opt.halign);
				tds.attr('valign',this._opt.valign);
			},
			_makeTextCell : function(dsObj,rowIdx,exprValue, exprStyle){
				(exprStyle['bgcolor'] || this._opt.bgcolor) && (this.grid.tdStyle+="background-color:"+(exprStyle['bgcolor'] || this._opt.bgcolor)+";");
				(exprStyle['bgimage'] || this._opt.bgimage) && (this.grid.tdStyle+="background-image:"+'url('+(exprStyle['bgimage'] ||this._opt.bgimage) +");");
				(exprStyle['bgimagerepeat'] || this._opt.bgimagerepeat) && (this.grid.tdStyle+="background-repeat:"+(exprStyle['bgimagerepeat'] ||this._opt.bgimagerepeat) +";");
				(exprStyle['bgimagealign'] || this._opt.bgimagealign) && (this.grid.tdStyle+="background-position:"+(exprStyle['bgimagealign'] ||this._opt.bgimagealign) +";");
				if(this.getBindcolumn()){	
					var maskValue = (exprValue || exprValue == 0 ) ? exprValue : this._getValue(dsObj, rowIdx);
					var style = 'display:inline;';
					if(this.getSuppress()){
						var passcomfirm = this._doSuppress(dsObj,rowIdx);
						if(passcomfirm == false) {
							style = 'display:none;';
						}
					}
					style += this._textStyle(exprStyle);
					if(this.grid.getTextwrap() == 'none')  style += 'white-space:nowrap;';
					else if(this.grid.getTextwrap() == 'pre') style += 'white-space:pre;';
					else if(this.grid.getTextwrap() == 'prewrap') style += 'white-space:pre-wrap;';
					
					var span_start = "<span class='xw-mod-font xw-mod' _xid='"+this._opt._xid+"' ";
					return span_start+"style='"+style+"' >"+maskValue+"</span>";
				}
				return "";
			},
			_textStyle : function(exprStyle){
				var style="";
				var fontfamily = this._opt.fontfamily ;
				if (this._opt.fontfamily && Xwing.config.fonts && Xwing.config.fonts[this._opt.fontfamily]) {
					fontfamily = Xwing.config.fonts[this._opt.fontfamily];
				}
				
				(exprStyle['fontfamily'] || this._opt.fontfamily) && (style += "font-family:"+(exprStyle['fontfamily'] || fontfamily)+";");
				(exprStyle['fontcolor'] || this._opt.fontcolor) && (style += "color:"+(exprStyle['fontcolor'] ||this._opt.fontcolor)+";");
				(exprStyle['fontstyle'] || this._opt.fontstyle) && (style += "font-style:"+(exprStyle['fontstyle'] ||this._opt.fontstyle)+";");
				(exprStyle['fontweight'] || this._opt.fontweight) && (style += "font-weight:"+(exprStyle['fontweight'] ||this._opt.fontweight)+";");
				(exprStyle['fontsize'] || this._opt.fontsize) && (style+= "font-size:"+(exprStyle['fontsize'] ||this._opt.fontsize)+";");
				(exprStyle['fontdecoration'] || this._opt.fontdecoration) && (style+="text-decoration:"+(exprStyle['fontdecoration'] ||this._opt.fontdecoration)+";");				
				
				if(exprStyle['textpadding'] || this._opt.textpadding){
					var paddings = exprStyle['textpadding'] || this._opt.textpadding;
					paddings = paddings.trim().split(/\s+/);
					var result = "";
					for(var i=0, l=paddings.length; i < l ; i++) result += paddings[i]+'px ';
					style +="padding:"+result+";";
				}
				return style;
			},
			_doSuppress : function(dataset,y){
				var result = false;
				y = xwing.Util.parseInt(y,0);
				var preV = dataset.getValue((y-1),this.getBindcolumn());
				var curV = dataset.getValue(y,this.getBindcolumn());
				var nextV = dataset.getValue(((y+1)>= dataset.size()?y:(y+1)),this.getBindcolumn());
				
				if(preV != curV){
					this.grid.valueflag = true;
					result = true;
				}else{
					if(this.grid.valueflag == true){
						result = true;
					}
				}
				
				if(curV == nextV && y!=(dataset.size() -1)){
					if(this.grid.lineflag != true) this.grid.tdStyle +='border-bottom:0px none;';
				}else if(y != (dataset.size() -1)){
					this.grid.lineflag = true;
				}
				return result;
			},
			setDisplaytype : function(v){
				this._opt.displaytype = v;
			},
			getDisplaytype : function(){
				return this._opt.displaytype;
			},
			setFixed : function(v){
				this._opt.fixed = v;
				if(this.grid) this.grid._resetColRate();
			},
			getFixed : function(){
				return xwing.Util.parseBoolean(this._opt.fixed);
			}
		}
	}
});
