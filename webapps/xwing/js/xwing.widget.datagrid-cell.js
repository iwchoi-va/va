Class.define({
	DataGridCell : {
		alias : "datagrid-cell",
		namespace : "xwing.widget",
		extend : xwing.widget.Widget,
		DataGridCell : function(json, obj, area) {
			this._datagrid = obj;
			this._area = area;
			this._init(json);
			
			// rowedit
			this._grouprowedit = {};
		},
		statics : {
			_NAVI_KEY_SET : [ Xwing.key.DOWN_ARROW, Xwing.key.UP_ARROW, Xwing.key.TAB, Xwing.key.ENTER ],
			create : function(json) {
				return new xwing.widget.DataGridCell(json);
			}
		},
		prototypes : {
			_init : function(json) {
				this.model = Class.getClass(this.getAlias()).getModel();
				this._opt = {};
				this._attr = {};
				
				jQuery.extend(this._opt, json || {});
				xwing.widget.Widget.prototype._initOption.call(this);
				
				Xwing.addWidget(this._opt.id, this);
			},
			getId : function() {
				return this._opt.id;
			},
			_getEditCell : function(td) {
				if (this.getEdittype() == 'none')
					return;
				
				var that = this,
					opt = {
						id : this._datagrid.getId() + '_' + this.getId(),
						width : td.width() - 2,
						height : td.height() - 2,
						binddataset : this._datagrid.getBinddataset(),
						bindcolumn : this.getBindcolumn()
					};
				
				if (this.getDomaindataset() && this.getDomaincodecolumn() && this.getDomaintextcolumn()) {
					opt.domaindataset = this.getDomaindataset();
					opt.domaincodecolumn = this.getDomaincodecolumn();
					opt.domaintextcolumn = this.getDomaintextcolumn();
				}
				
				if (this.getEditoption()) {
					try {
						options = eval('({' + (this.getEditoption() || '') + '})');
						opt = jQuery.extend(opt, options);
					} catch (e) {
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
						opt.halign || (opt.halign = 'center');
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
				}
				
				widget._getJShell().find('.xw-mod-border').css('border-radius', 0);
				widget._getJShell().find('input.xw-mod-focus').bind('keydown', function(event) {
					if (xwing.widget.DataGridCell._NAVI_KEY_SET.indexOf(event.keyCode) == -1)
						return;

					event.stopPropagation();
					
					var xid = event.currentTarget.getAttribute('_xid');
					var widget = Xwing.getWidget(xid);
					var rendered = that._datagrid._getRenderedRange();
					
					switch (event.keyCode) {
						case Xwing.key.DOWN_ARROW:
							event.preventDefault();
							if (widget.getAlias() == 'combo') {
								return;
							}
							
							var cy = widget._getJShell().closest('tr', that._datagrid.body).attr('cy');
							if (cy == rendered.bottom) return;
							
							break;
						case Xwing.key.UP_ARROW:
							event.preventDefault();
							if (widget.getAlias() == 'combo') {
								return;
							}
							
							var cy = widget._getJShell().closest('tr', that._datagrid.body).attr('cy');
							if (cy == rendered.top) return;
							
							break;
						case Xwing.key.TAB:
							var editpart = that._datagrid.body_table._editpart;
							if (editpart.$trs.find('td>.xw-shell:last').is(widget.getShell())) {
								event.preventDefault();
								var cy = widget._getJShell().closest('tr', that._datagrid.body_tbody).attr('cy');
								if (cy == rendered.bottom) 
									return;
							}
							break;
					}
					
					if (that.t_render) {
						clearTimeout(that.t_render);
					}
					
					(function(keyCode, xid) {
						that.t_render = setTimeout(function() {
							that._datagrid._doNaviEditMode(keyCode, xid);
							that.t_render = null;
						}, 10);							
					})(event.keyCode, xid);					
				});
				
				widget.setParentWidget(this._datagrid);
				return widget.getShell();
			},
			_bindEditKeyEvent : function(event) {
				
			},
			_getValue : function(dataset, rowIdx) {
				var value = dataset.getValue(rowIdx, this.getBindcolumn());

				if (this.getDomaindataset() && this.getDomaincodecolumn() && this.getDomaintextcolumn()) {
					var domainDsObj = Xwing.getDataset(this.getDomaindataset());
					var codeCol = this.getDomaincodecolumn();
					var textCol = this.getDomaintextcolumn();
					if (domainDsObj) {
						value = domainDsObj.lookUp(codeCol, value, textCol);
					}
				}

				this.getMask() && (value = xwing.widget.DataBindable.prototype.getMaskedValue.call(this, value));
				return xwing.Util.encodeHtml(value);
			},
			_getSpan : function() {
				var span = '';
				if (this.getColspan() > 1)
					span += 'colspan="' + this.getColspan() + '" ';
				if (this.getRowspan() > 1)
					span += 'rowspan="' + this.getRowspan() + '" ';
				return span;
			},
			_setCss : function() {
				this._datagrid._setCssRules('.' + this.getId(), this._getCss());
			},
			_getCss : function(v) {
				var rules = [], opt = v || this._opt;

				if (opt.bgimage) {
					rules.push("background-image:" + "url('" + opt.bgimage + "');");
					opt.bgimagerepeat && rules.push("background-repeat:" + opt.bgimagerepeat + ";");
					opt.bgimagealign && rules.push("background-position:" + opt.bgimagealign + ";");
				} else if (opt.bgcolor && opt.bggradientcolor) {
					rules.push("background-image:" + "linear-gradient(to top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					if (jQuery.browser.mozilla) {
						rules.push("background-image:" + "-moz-linear-gradient(top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					} else if (jQuery.browser.opera) {
						rules.push("background-image:" + "-o-linear-gradient(top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					} else if (jQuery.browser.webkit) {
						rules.push("background-image:" + "-webkit-gradient(linear, left top, left bottom, from(" + opt.bgcolor + "), to(" + opt.bggradientcolor + "));");
					}
				} else if (opt.bgcolor) {
					rules.push('background-color:' + opt.bgcolor + ';');
				}
				
				opt.valign && rules.push("vertical-align:" + opt.valign + ';');
				opt.halign && rules.push("text-align:" + opt.halign + ';');
				opt.cursor && (opt.cursor != 'default') && rules.push("cursor:" + opt.cursor + ';');
				
				return rules.join('');
			},
			_addCss : function(){
				this._datagrid._addCssRules('.' + this.getId(), this._getCss());
			},
			_doBackground : function() { },
			_doBgcolor : function() {
				this._setCss();
			},
			_doBggradientcolor : function() {
				this._setCss();
			},
			_doBgimage : function() {
				this._setCss();
			},
			_doBgimagerepeat : function() {
				this._setCss();
			},
			_doBgimagealign : function() {
				this._setCss();
			},
			_getCssFont : function(v) {
				var rules = [], opt = v || this._opt;
				var fontfamily = opt.fontfamily ;
				if (opt.fontfamily && Xwing.config.fonts && Xwing.config.fonts[opt.fontfamily]) {
					fontfamily = Xwing.config.fonts[opt.fontfamily];
				}
				
				opt.fontfamily && rules.push('font-family:' + fontfamily + ';');
				opt.fontcolor && rules.push('color:' + opt.fontcolor + ';');
				opt.fontstyle && rules.push('font-style:' + opt.fontstyle + ';');
				opt.fontweight && rules.push('font-weight:' + opt.fontweight + ';');
				opt.fontsize && rules.push('font-size:' + opt.fontsize + 'px;');
				opt.fontdecoration && rules.push('text-decoration:' + opt.fontdecoration + ';');
				
				return rules.join('');
			},
			_doFont : function() { 
				this._datagrid._setCssRules('.' + this.getId() + ' .xw-datagrid-font', this._getCssFont());
			},
			_doFontfamily : function() {
				this._doFont();
			},
			_doFontcolor : function() {
				this._doFont();
			},
			_doFontstyle : function() {
				this._doFont();
			},
			_doFontweight : function() {
				this._doFont();
			},
			_doFontsize : function() {
				this._doFont();
			},
			_doFontdecoration : function() {
				this._doFont();
			},
			setColspan : function(v) {
				this._opt.colspan = v;
			},
			getColspan : function() {
				return xwing.Util.parseInt(this._opt.colspan);
			},
			setRowspan : function(v) {
				this._opt.rowspan = v;
			},
			getRowspan : function() {
				return xwing.Util.parseInt(this._opt.rowspan);
			},
			setMask : function(v){
				this._opt.mask = v;
			},
			getMask : function(){
				return this._opt.mask;
			},			
			setText : function(v) {
				this._opt.text = v;
				this._doText();
			},
			getText : function() {
				return this._opt.text;
			},
			_doText : function() {
				var text = xwing.Util.encodeHtml(this.getText());
				this._datagrid.grid.find('.' + this.getId() + ' .xw-datagrid-font').html(text);
			},
			setValign : function(v) {
				this._opt.valign = v;
				this._doValign();
			},
			getValign : function() {
				return this._opt.valign;
			},
			_doValign : function() {
				this._setCss();
			},
			setHalign : function(v) {
				this._opt.halign = v;
				this._doHalign();
			},
			getHalign : function() {
				return this._opt.halign;
			},
			_doHalign : function() {
				this._setCss();
			},
			setBindcolumn : function(column) {
				this._opt.bindcolumn = column;
			},
			getBindcolumn : function() {
				return this._opt.bindcolumn;
			},
			setDomaindataset : function(v) {
				this._opt.domaindataset = v;
			},
			getDomaindataset : function() {
				return this._opt.domaindataset;
			},
			setDomaincodecolumn : function(v) {
				this._opt.domaincodecolumn = v;
			},
			getDomaincodecolumn : function() {
				return this._opt.domaincodecolumn;
			},
			setDomaintextcolumn : function(v) {
				this._opt.domaintextcolumn = v;
			},
			getDomaintextcolumn : function() {
				return this._opt.domaintextcolumn;
			},
			setEdittype : function(v) {
				var oldv = this._opt.edittype;
				this._opt.edittype = v;
				this._doEdittype(oldv, v);
			},
			getEdittype : function() {
				return this._opt.edittype;
			},
			_doEdittype : function(oldv, newv){
				if( oldv != newv){
					var rows = this._datagrid._ast[this._area], cells
						, coord
						, that = this;
					for(var i=0, l=rows.length; i<l ; i++){
						cells = rows[i].getCell();
						for(var j=0, k=cells.length; j < k; j++){
							if(cells[j] == this){
								coord = {rowIdx : i, colIdx : j};
								break;
							} 
						}
					}
					var edit = this._datagrid.body_table._editpart
						, td = edit.filter('[ri='+coord.rowIdx+']').children('td[cx='+coord.colIdx+']');
					
					// width & height || data
					setTimeout(function(){
						var shell, value, style = {}
							, dsObj = Xwing.getDataset(that._datagrid._opt.binddataset)
							, di = dsObj.getCursor();
						if(newv == 'none'){
							if (that.getExpr()) {
								style = that._doExpr(di);
								value = style.value;
							}
							if (value === undefined && dsObj) value = that._getValue(dsObj, di);
							
							shell = jQuery('<span class="xw-datagrid-font" '
									+ (style.text ? 'style="' + style.text + '"' : '')
									+ '>'+value+'</span>' 
									);
						}else 
							shell = that._getEditCell(td);
						
						td.empty().append(shell);
					},0);
				}
			},
			setEditoption : function(v) {
				this._opt.editoption = v;
			},
			getEditoption : function() {
				return this._opt.editoption;
			},
			setSuppress : function(v) {
				this._opt.suppress = v;
			},
			getSuppress : function() {
				return xwing.Util.parseBoolean(this._opt.suppress);
			},
			_doSuppress : function() {
				
			},
			_setColIndex : function(v) {
				if (v === undefined) v = this._getCellColIndex();
				this._opt.colindex = v;
			},
			_getColIndex : function() {
				return this._opt.colindex;
			},
			_getCellColIndex : function() {
				var matrix = [],
					row = null, col = null, 
					cells = null, cell = null, 
					colspan = null, rowspan = null, 
					rowIndex = null, colIndex = null,
					area = this._area;
				
				for ( var i = 0; i < this._datagrid._ast[area].length && col === null; i++) {
					matrix[i] = matrix[i] || [];
					row = this._datagrid._ast[area][i];
					cells = row.getCell();
					
					for ( var j = 0; j < cells.length && col === null; j++) {
						cell = cells[j];
						colspan = cell.getColspan() || 1;
						rowspan = cell.getRowspan() || 1;
	
						rowIndex = i;
						matrix[rowIndex] = matrix[rowIndex] || [];
						colIndex = null;
	
						for ( var l = 0; l <= matrix[rowIndex].length && colIndex === null; l++) {
							if (!matrix[rowIndex][l])
								colIndex = l;
						}
	
						if (this === cell) {
							col = colIndex;
							break;
						}
	
						for ( var k = rowIndex; k < rowIndex + rowspan; k++) {
							for ( var l = colIndex; l < colIndex + colspan; l++) {
								matrix[k] = matrix[k] || [];
								matrix[k][l] = 1;
							}
						}
					}
				}
				
				return col + this.getColspan() - 1;
			},
			setExpr : function(v) {
				this._opt.expr = v;
				this._datagrid._resetBodyPart();
			},
			getExpr : function() {
				return this._opt.expr;
			},
			_doExpr : function(rowIdx) {
				var opt = {};

				try {
					var v = eval(this.getExpr());
					var obj = xwing.Util.is(v, 'function') ? v.call(null, this, rowIdx) : v;
					
					if (obj !== undefined) {
						if (xwing.Util.is(obj, 'object')) {
							opt.value = obj.value;
							opt.text = this._getCssFont(obj);
							opt.cell = this._getCss(obj);
						} else {
							opt.value = obj;
						}
						
						if (!(opt.value === undefined || opt.value === null)) {
							this.getMask() && (opt.value = xwing.widget.DataBindable.prototype.getMaskedValue.call(this, opt.value));
							opt.value = xwing.Util.encodeHtml(opt.value);
						}
					}
				} catch (e) {
					Xwing.debug(e);
				}

				return opt;
			},
			setGroupable : function(v){
				this._opt.groupable = v;
			},
			getGroupable : function(){
				return xwing.Util.parseBoolean(this._opt.groupable, false);
			},
			setDisplayCheckbox : function(v){
				if( this._area == 'head' && this._opt.displaytype == 'checkbox'){
					if( v == true || v == 'true'){
						this._datagrid.head.find('th.'+this.getId()).find('span.xw-datagrid-display-checkbox').removeClass("xw-checkbox-unchecked").addClass('xw-checkbox-checked');
					}else{
						this._datagrid.head.find('th.'+this.getId()).find('span.xw-datagrid-display-checkbox').removeClass("xw-checkbox-checked").addClass('xw-checkbox-unchecked');
					}
				}
			},
			getDisplayCheckbox : function(){
				if( this._area == 'head' && this._opt.displaytype == 'checkbox'){
					if( this._datagrid.head.find('th.'+this.getId()).find('span.xw-datagrid-display-checkbox').hasClass('xw-checkbox-checked')){
						return this._opt.truevalue;
					}else
						return this._opt.falsevalue;
				}
				return false;
			},
			setExporttype : function(v){
				this._opt.exporttype = v;
			},
			getExporttype : function(){
				return this._opt.exporttype;
			},
			getDatagridIdx : function(){
				// 일반 본인의 인덱스 반환
				var ast = this._datagrid._ast[this._area]
					, idx = [-1,-1];
				for(var r = 0, l = ast.length; r < l ; r++){
					var row = ast[r]
						, cells = row._cell;
					idx[0] = r;
					for( var c = 0, l2 = cells.length; c< l2 ; c++){
						var cell = cells[c];
						if( cell == this){
							idx[1] = c;
							return idx;
						}
					}
				}
				return idx;
			},
			setGroupsum : function(v){
				this._opt.groupsum = v;
			},
			getGroupsum : function(){
				return xwing.Util.parseBoolean(this._opt.groupsum,false);
			},
			setGroupsumtype : function(v){
				this._opt.groupsumtype = v;
			},
			getGroupsumtype : function(){
				return this._opt.groupsumtype;
			},
			setGroupsumrendering : function(v){
				this._opt.groupsumrendering = v;
			},
			getGroupsumrendering : function(){
				return this._opt.groupsumrendering;
			},
			_doGroupsumrendering : function(value, depth){
				var opt = {};
				try{
					var v = eval(this.getGroupsumrendering());
					var obj = xwing.Util.is(v,'function') ? v.call(null, this, value, depth) : v;
					
					if( obj !== undefined ){
						if (xwing.Util.is(obj, 'object')) {
							opt.value = obj.value;
							opt.text = this._getCssFont(obj);
							opt.cell = this._getCss(obj);
						} else {
							opt.value = obj;
						}
						
						if (!(opt.value === undefined || opt.value === null)) {
							this.getMask() && (opt.value = xwing.widget.DataBindable.prototype.getMaskedValue.call(this, opt.value));
							opt.value = xwing.Util.encodeHtml(opt.value);
						}
					}
				}catch(e){
					Xwing.debug(e);
				}
				return opt;
			},
			setGroupborderright : function(v){
				this._opt.groupborderright = v;
			},
			getGroupborderright : function(){
				return this._opt.groupborderright;
			},
			setChangeedit : function(v){
				this._opt.changeedit = v;
			},
			getChangeedit : function(){
				return this._opt.changeedit;
			},
			_doChangeedit : function(rowIdx) {
				try {
					var v = eval(this.getChangeedit());
					var obj = xwing.Util.is(v, 'function') ? v.call(null, this, rowIdx) : v;
					
					return obj;
				} catch (e) {
					Xwing.debug(e);
				}
			},
			_isCreateRowEdit : function(type){
				return (this._grouprowedit[type] ? true : false);
			},
			_createRowEdit : function(type, opt){
				if( !opt ) opt = {};
				
				opt['id'] = this.getId()+'_rowedit_'+type;
				opt['binddataset'] = this._datagrid.getBinddataset();
				opt['bindcolumn'] = this.getBindcolumn();
				
				var widget = null;
				switch(type){
				case 'combo' : 
					widget = new xwing.widget.Combo(opt);
					break;
				case 'edit' :
					widget = new xwing.widget.Edit(opt);
					break;
				case 'spin' :
					widget = new xwing.widget.Spin(opt);
					widget._setValue = function(val){
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
						
						if( this._parentWidget && this._parentWidget.getAlias() == 'datagrid'){
							this._opt.value = val;
							if( this.getBinddataset() && this.getBindcolumn() ){
								var ds = Xwing.getDataset(this.getBinddataset())
									, colIdx = ds.getColumnIndex(this.getBindcolumn());
								ds._rows[ds.getCursor()][colIdx] = val;
								ds._phys._rows[ds.getCursor()][colIdx] = val;
							}
							this._jinput.val(val);
						}else
							this.setValue(val);
						this._fire('change',{type:'change',source:this,event:null,value:this.getValue()});
					};
					break;
				}
				
				var that = this;
				widget._getJShell().find('input.xw-mod-focus').bind('keydown', function(event) {
					if (xwing.widget.DataGridCell._NAVI_KEY_SET.indexOf(event.keyCode) == -1)
						return;
					
					event.stopPropagation();
					var xid = event.currentTarget.getAttribute('_xid');
					var widget = Xwing.getWidget(xid);
					
					switch (event.keyCode) {
						case Xwing.key.DOWN_ARROW:
							event.preventDefault();
							if (widget.getAlias() == 'combo') {
								return;
							}
							
							var cy = widget._getJShell().closest('tr', that._datagrid.body).attr('cy');
							if (cy == rendered.bottom) return;
							
							break;
						case Xwing.key.UP_ARROW:
							event.preventDefault();
							if (widget.getAlias() == 'combo') {
								return;
							}
							
							var cy = widget._getJShell().closest('tr', that._datagrid.body).attr('cy');
							if (cy == rendered.top) return;
							
							break;
						case Xwing.key.TAB:
							var editpart = that._datagrid.body_table._editpart;
							if (editpart.$trs.find('td>.xw-shell:last').is(widget.getShell())) {
								event.preventDefault();
								var cy = widget._getJShell().closest('tr', that._datagrid.body_tbody).attr('cy');
								if (cy == rendered.bottom) 
									return;
							}
							break;
					}
					
					if (that.t_render) {
						clearTimeout(that.t_render);
					}
					
					(function(keyCode, xid) {
						that.t_render = setTimeout(function() {
//							that._datagrid._doNaviEditMode(keyCode, xid);
//							that._datagrid._group && that._datagrid._doValue();
							that._datagrid._group && that._datagrid._group._doRowDataMode();
							that.t_render = null;
						}, 10);							
					})(event.keyCode, xid);	
				});
				widget._getJShell().find('.xw-mod-border').css('border-radius', 0);
				widget.setParentWidget(this._datagrid);
				return widget.getShell();
			}
		}
	}
});
