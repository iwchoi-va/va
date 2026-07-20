/**
 * [ Dependency List ]
 * 
 * xwing.js
 * xwing.core.js
 * xwing.util.js
 * jquery.mousewheel.js
 */
Class.define({
	DataGrid : {
		alias : "datagrid",
		extend : xwing.widget.DataBindable,
		namespace : "xwing.widget",
		DataGrid : function(json, node){
			if (!arguments.length) return;

			node && (json.xw_node = node);			
			this._init(json);
		},
		statics : {
			_CTRL_KEY_SET : [ Xwing.key.DOWN_ARROW, Xwing.key.UP_ARROW, Xwing.key.SPACE, Xwing.key.ENTER, Xwing.key.LEFT_ARROW, Xwing.key.RIGHT_ARROW ],
			_EVENT_COMM : ["mouseenter", "mousemove", "mouseleave", "focusin", "focusout" ],
			create : function(json){
				return new xwing.widget.DataGrid(json);
			}
		},		
		prototypes :{
			_getCommEvent : function(){
				return xwing.widget.DataGrid._EVENT_COMM;
			},
			_parse : function(node) {
				this._isgroupsum = false;
				this._groupkey = [];
				this._grouptype = [];
				this._groupsuppress = [];
				var getAttrList = xwing.widget.Widget._parseXJson, 
					areaType = ['head', 'body', 'summary'],
					that = this,
					structure = {
						column : [],
						head : [],
						body : [],
						summary : []
					};
				jQuery('xwing|datagrid-column', jQuery('xwing|datagrid-colgroup', node)).each(function() {
					var v = new xwing.widget.DataGridColumn(getAttrList(this), that);
					structure.column.push(v);
				});
				
				var suppress = false
				, group = false;
				for ( var a = 0, l = areaType.length; a < l; a++) {
					var area = areaType[a];
					var height = 0;
					
					jQuery('xwing|datagrid-row', jQuery('xwing|datagrid-' + area, node)).each(function(i, e) {
						var json = getAttrList(this);
						var id = that._getCssId() + '-' + area.charAt(0) + '-r';
						json['id'] = id + i;
						
						var v = new xwing.widget.DataGridRow(json, that);
						structure[area].push(v);
						
						height += v.getHeight();
						
						var depth = 0;
						jQuery('xwing|datagrid-cell', this).each(function(j) {
							json = getAttrList(this);
							json['id'] = id + i + 'c' + j;
							
							// grouping 순서. 
							if( json['suppress'] && json['suppress'] == 'true'  ){
								that._groupsuppress.push(json['bindcolumn']);
								// 실제 만들어야 할 row들 
								if(json['groupkey'] == true || json['groupkey'] == 'true'){
									suppress = true;
									that._groupkey.push({
										bindcolumn : json['bindcolumn'],
										depth : depth
									});
								}
								depth++;
							}
							
							// 실제 계산해야 할 cell들
							if(json['groupsum'] == true || json['groupsum'] == 'true'){
								groupsum = true;
								that._grouptype.push({
									bindcolumn : json['bindcolumn'],
									type : ( json['groupsumtype']? json['groupsumtype'] :'text')
								});
							}
							v.addCell(new xwing.widget.DataGridCell(json, that, area));
						});
					});
					
					structure[area]._height = height;
				}
				
				if( suppress && groupsum ) this._isgroupsum = true;
				else this._groupkey = [];
				return structure;
			},
			_setRowHeight : function(indices) {
				if (this.getTextwrap() == 'none')
					return;
				
				var fixedTrs, trs,
					$fixedTr, $tr,
					fixedTrHeight, trHeight,
					that = this;

				if (indices) {
					fixedTrs = [];
					trs = [];
					
					for ( var i = 0, l = indices.length; i < l; i++) {
						that.body_fix_tbody.find('>tr[cy=' + indices[i] + ']').each(function() {
							fixedTrs.push(this);
						});
						that.body_tbody.find('>tr[cy=' + indices[i] + ']').each(function() {
							trs.push(this);
						});
					}
				} else {
					fixedTrs = that.body_fix_tbody.find('>tr');
					trs = that.body_tbody.find('>tr');
				}
				
				for ( var i = 0; i < trs.length; i++) {
					$fixedTr = jQuery(fixedTrs[i]);
					$tr = jQuery(trs[i]);
					
					fixedTrHeight = $fixedTr.height();
					trHeight = $tr.height();

					if (fixedTrHeight == trHeight)
						continue;

					if (fixedTrHeight > trHeight) {
						$tr.height(fixedTrHeight);
						$fixedTr.height(fixedTrHeight);
					} else {
						$fixedTr.height(trHeight);
						$tr.height(trHeight);
					}
				}
			},
			_getFixedColIndex : function() {
				var v = -1;
				
				for ( var i = 0, l = this._ast.column.length; i < l; i++) {
					this._ast.column[i].getFixed() && (v = i);
				}
				
				return v;
			},
			_setCellColIndex : function(persist) {
				var areas = ['head', 'body', 'summary'];
				
				for ( var i = 0, area; i < areas.length; i++) {
					area = areas[i];
					for ( var j = 0, cells; j < this._ast[area].length; j++) {
						cells = this._ast[area][j].getCell();
						for ( var k = 0; k < cells.length; k++) {
							cells[k]._setColIndex();
							if (persist && area != 'body') {		
								this[area.substring(0, 4)].find('.' + cells[k].getId()).attr('cx', cells[k]._getColIndex());
							}
						}
					}
				}
			},
			_render : function() {
				this._doHeadcolor();
				this._doSummarycolor();

				xwing.widget.DataBindable.prototype._render.call(this);
			},
			_getCssId : function() {
				return window.xwingIDE ? 'X' + this.getXId() : this.getXId();
			},
			_appendCompleted : function(){
				this._replace();
			},
			_replace : function(){
				var that = this;
				setTimeout(function(){
					that._setRowHeight();
					that._fitBottomScrollArea();
				},1000);
			},
			_initDataGridEvent : function(){
				this._bind(this._getJShell(),'click',this._handleClickEvent);
				this._bind(this._getJShell(),'dblclick',this._handleClickEvent);
				this._bind(this.head,'mouseup',this._clickDisplayCheckbox);
				
				var that = this;
				jQuery(document.documentElement).bind('mouseup',function(event){
					if( jQuery(event.target).parents('table[_xid='+that.getId()+']').length == 0  ){
						that._doDataMode();
					}
				});
			},
			_handleClickEvent : function(event){
				var tdNode = jQuery(event.target).closest('td',event.currentTarget)
				, firstcx = xwing.Util.parseInt(this.body.find('tr[cy='+tdNode.parent().attr('cy')+'][ri='+tdNode.parent().attr('ri')+']').children(':first').attr('cx'),0)
				, cx = xwing.Util.parseInt(tdNode.attr('cx'),0)
				, ri = tdNode.parent().attr('ri')
				, cy = tdNode.parent().attr('cy');
				if( !ri || !cy) return;
				if (this.getEnabled() && this._opt[event.type]) {
					var opt = {
							type : event.type,
							source : this,
							event : event
						};
					if(cx != undefined){
						opt.cell = this._ast.body[ri].getCell(cx - firstcx); 
						opt.rowIdx = cy;
					}
					this._fire(event.type, opt);
				}
			},
			_clickDisplayCheckbox : function(event){
				// HEAD checkbox SORA
				var display = jQuery(event.target);
				if( this.getEnabled() && display.hasClass('xw-datagrid-head-display-checkbox') ){
					var checked = display.hasClass('xw-checkbox-checked');
					if( checked ){
						display.addClass('xw-checkbox-unchecked').removeClass('xw-checkbox-checked');
					}else{
						display.addClass('xw-checkbox-checked').removeClass('xw-checkbox-unchecked');
					}
					// click checkbox event
					var opt = {
						type : 'changeheadercheckbox',
						source : Xwing.getWidget(jQuery(event.target).closest('th',event.currentTarget).attr('class').split(' ')[0]),
						checked : checked ? false : true,
						event : event
					};
					this._fire(opt.type, opt);
				}
			},
			_createPart : function(){
				this.grid = jQuery("<div class='xw-datagrid xw-mod-border xw-mod-focus'/>");
				this.grid.addClass(this._getCssId());
				this._ast = this._parse(this._json.xw_node);
				this._setCellColIndex();
				this._cssList = {};

				this._rownumWidth = 30; // 22
				this._pageRowCnt = 30;
				this._rowCache = [];
				this._cacheSelect = {
					standard : ''
					, preselect : ''
					, selected : []
				};
				this._selectedLastColIdx = 0; 
				this._keystand = false;
				this._createCssRules();
				this._createHeaderPart();
				this._createBodyPart();
				this._createSummaryPart();
				this._createPagePart();
				 
				this._createFixedAreaPart();
				this.getGroupable() && this._ast['body'].length == 1 ? this._createGrouping() : this._createDataGrid();
				this._getJShell().append(this.grid);
				
				this._initDataGridEvent();
				if (window.xwingIDE) {
					jQuery("<div class='xw-mod xw-ide-wrap'/>").appendTo(this.body);
				}
			},
			_createDataGrid : function(){
				this._createEditPart();
				
				this._doRownum();
				this._doCheckbox();
				this._doResizable();
				this._doSortable();
				this._doMovecolumn();
				
				this._json.xw_node && jQuery(this._json.xw_node).empty();
				
				this._bindDraggbleEvent();
				this._bindKeyEvent();
				this._bindColShowHide();
			},
			_createGrouping : function(){
				this._json.xw_node && jQuery(this._json.xw_node).empty();
				
				this._group = new Grouping(this, Xwing.getDataset(this._opt.binddataset));
				this._group._init();
			},
			_bindDraggbleEvent : function(){
				var that = this
					, dsObj = Xwing.getDataset(this.getBinddataset());
				this._flag = false, this.move = false;
				this._isRowspan = false;
				for(var i=0; i < this._ast['body'].length; i++){
					var cells = this._ast['body'][i].getCell();
					for(var j=0; j < cells.length; j++){
						if(cells[j].getRowspan() > 1){
							this._isRowspan = true;
							break;
						}
					}
				}
				
				this.body.bind('mousedown',function(event){
					if(that.getRightselection() || event.which != 3){
						that._keystand = false;
						var tdNode = jQuery(event.target).closest('td',event.currentTarget)
						, tagName = event.target.tagName.toLowerCase();
						
						if(!tdNode.hasClass('xw-datagrid-rownum' || !tdNode.hasClass('xw-datagrid-checkbox'))
							&& !(tagName == 'div' || tagName == 'table')){
							
							if( that.getSelectedrange() == 'cell' ){
								
								if(!event.ctrlKey || !that.getMultiselectable()) that._initCacheSelect();
								that._cacheSelect.standard = tdNode[0], that._cacheSelect.preselect = tdNode[0]
								, that._flag = true, that.move = false;
								that._setCacheSelect(+tdNode.parent().attr('cy'), +tdNode.attr('cx'), (that.getMultiselectable() ? event.ctrlKey : false)  ,true);
								dsObj.setCursor(tdNode.parent().attr('cy'), that);
								
							}else if( that.getSelectedrange() == 'row' && !that._isRowspan){
								var trNode = jQuery(event.target).closest('tr',event.currentTarget)
									, cy = parseInt(trNode.attr('cy'));
								
								if(!event.ctrlKey || !that.getMultiselectable()) that._initCacheSelect();
								that._cacheSelect.standard = trNode[0], that._cacheSelect.preselect = trNode[0]
								, that._flag = true, that.move = false;
								
								jQuery('tr[cy='+cy+']:not(editable)',that.body).each(function(idx,el){
									if(event.ctrlKey){
										jQuery(el).toggleClass('select');
										var index = that._cacheSelect.selected.indexOf(el);
										if(index != -1){
											that._cacheSelect.selected.splice(index,1);
										}else{
											that._cacheSelect.selected.push(el);
										}
									}else{
										jQuery(el).addClass('select');
										that._cacheSelect.selected.push(el);
									}
								});
								if( that._isMouseup() ) return;
								if(trNode.attr('cy') != dsObj.getCursor()){
									that._doDataMode(); 
									// TODO SORA TEST
									//if(that.getMultiselectable()) dsObj.setCursor(trNode.attr('cy'), that);
									dsObj.setCursor(trNode.attr('cy'), that);
								}
								
							}
						}
					}
				});
				this.body.bind('mousemove',function(event){
					event.preventDefault();
					var tdNode = jQuery(event.target).closest('td',event.currentTarget)
					, tagName = event.target.tagName.toLowerCase();
					if(that.getMultiselectable() && that._flag 
							&& !(tagName == 'div' || tagName == 'table')){
						if( that.getSelectedrange() == 'cell' ){
							var x = +tdNode.attr('cx'), y = +tdNode.parent().attr('cy');
							if( (that._cacheSelect.standard != tdNode[0] && that._cacheSelect.preselect != tdNode[0])){
								that.move = true;
								that._setCacheSelect(y, x, false, true);
								that._cacheSelect.preselect = tdNode[0];
							}else if(that._cacheSelect.standard == tdNode[0] && that.move){
								that._setCacheSelect(y, x, false, true);
								that._cacheSelect.preselect = tdNode[0];
							}
							dsObj.setCursor(y, that);
						}else if ( that.getSelectedrange() == 'row' && !that._isRowspan){
							var trNode = jQuery(event.target).closest('tr',event.currentTarget);
							if( that._cacheSelect.standard.getAttribute('cy') != trNode.attr('cy') 
								&&  that._cacheSelect.preselect.getAttribute('cy') != trNode.attr('cy')){
								that.move = true;
								that._setCacheRowSelect(parseInt(trNode.attr('cy')));
								that._cacheSelect.preselect = trNode[0];
							}else if( that._cacheSelect.standard.getAttribute('cy') == trNode.attr('cy') && that.move){
								that._setCacheRowSelect(parseInt(trNode.attr('cy')));
								that._cacheSelect.preselect = trNode[0];
							}
						}
					}
				});
				jQuery(document.documentElement).bind('mouseup',function(event){
					that._flag = false, that.move = false;
					if(that.getSelectedrange() == 'cell'){
						that._flag = false, that.move = false;
					}
					if(!jQuery(event.target).hasClass('xw-datagrid-column-menu') ){
						if(!jQuery(event.target).hasClass('xw-datagrid-menu')){
							that._colmenu && that._colmenu.hide();
							that.clickCol && that.clickCol.children('.xw-datagrid-column-menu').hide();
							that.clickCol = null;
						}
					}
				});
			},
			_bindScrollEvent : function() {
				var prevTop = 0,
					left, top,
					that = this,
					t_render;

				this._bind(this.body_area, 'scroll', function(event) {
					left = this.body_area.scrollLeft();
					top = this.body_area.scrollTop();

					this.body_fix_div.scrollTop(top);
					
					this.head_area.scrollLeft(left);
					this.summ_area.scrollLeft(left);
					
					if (prevTop != top) {
						if (t_render) {
							clearTimeout(t_render);
						}
						
						(function(v) {
							t_render = setTimeout(function() {
							//	console.time('_row render');
								
								try {
									that.getGroupable() || that._renderRows(v <= 0);
								} catch (e) {}
								
								//console.timeEnd('_row render');
								t_render = null;
							}, 50);
						})(prevTop - top);
						
						prevTop = top;
					}
				});
			},
			_handleMouseUpEvent : function(event) {
				var tdNode = jQuery(event.target).closest('td', event.currentTarget);
				if (!tdNode[0] || tdNode.hasClass('editable') || (!this.getRightselection() && event.which == 3))  
					return;
				
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (!dsObj) return;
				this._selectedLastColIdx = ((+tdNode.attr('cx') == 0 || +tdNode.attr('cx') )? +tdNode.attr('cx') : this._selectedLastColIdx);
				var cy = tdNode.parent().attr('cy');
				
				// mouseup fire 
				this._fire('mouseup',{
					type:'mouseup',
					source: this,
					event: event
				});	
				// Start....display checkbox 값 변경 및 dataset 값 변경 
				var display = jQuery(event.target)
					, cell = this.getCell('body',0, this._selectedLastColIdx);
				if( display.hasClass('xw-datagrid-display-checkbox-item') && cell){//xw-datagrid-display-checkbox
					var checked = display.hasClass('xw-checkbox-checked');
					if( checked ){
						display.addClass('xw-checkbox-unchecked').removeClass('xw-checkbox-checked');
						dsObj.setValue(cy, cell.getBindcolumn(), cell._opt.falsevalue, this);
					}else{
						display.addClass('xw-checkbox-checked').removeClass('xw-checkbox-unchecked');
						dsObj.setValue(cy, cell.getBindcolumn(), cell._opt.truevalue, this);
					}
				}
				// END......
				
				if (this.getMultiselectable() && event.ctrlKey) {
					this._doDataMode();
					//TODO Row or Cell 그리고 range가 rowspan, colspan 없는 경우에만 함... 
					if(this.getSelectedrange() == 'row' && that._isRowspan) {
						this._selectRow(cy, true);
					}
					dsObj.setCursor(cy, this);
				} else  if(this.getSelectedrange() == 'row'){
					if( this.getMultiselectable() && this._flag && !this._isRowspan){
						this._flag = false, this.move = false;
						if( this._isMouseup()){
							dsObj.setCursor(cy, this);
						}
					}else if (this.getEditwhen() == 'click' && this.getSelectedrange() == 'row') {
						var oldcy = dsObj.getCursor();
						dsObj.setCursor(cy, this);
						this._selectRow(cy);
						
						if(!(this._isEditMode() && cy == oldcy)){
							this._doDataMode();
							this._doEditMode(tdNode);
						}
					}else
						dsObj.setCursor(cy);
				}
			},
			_handleDblClickEvent : function(event) {
				if (this.getEditwhen() != 'dblclick' || this.getSelectedrange() == 'cell') return;
				if (this.getMultiselectable() && event.ctrlKey) return;
				
				var tdNode = jQuery(event.target).closest('td', event.currentTarget);
				if (!tdNode[0] || tdNode.hasClass('editable')) 
					return;
				
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (!dsObj) return;
				
				var cy = tdNode.parent().attr('cy');
				dsObj.setCursor(cy);
				
				this._doEditMode(tdNode);
			},
			_bindColShowHide : function(){
				/*
				 * sora Column Show/Hide
				 */
				if( !this.getVisiblecontextmenu() ) return;
				this.overCol, this.clickCol;
				this._createColumnMenu();
				if( !this._colmenu ) return;
				
				var that = this;
				this._colmenu.find('ul:first').bind('mouseup',function(event){
					var li = jQuery(event.target).closest('li',event.currentTarget)
						, check = li.find('.xw-datagrid-menu-item-check');
					if(check.attr('multicheck') == 'true'){
						check.attr('multicheck','fale').css('background-position','0 0');
						
						// ?ㅻⅨ li?ㅼ쓽 媛믩룄 ??false???뚮쭔. 遺紐⑤룄 false... 
						if(li.parents('li').length != 0 ){
							var hide = true;
							li.siblings().each(function(idx, ui){
								if( jQuery(ui).children('span.xw-datagrid-menu-item-check').attr('multicheck') == 'true'){
									hide = false;
									return;
								}
							});
							hide && li.parents('li:last').children('span.xw-datagrid-menu-item-check').attr('multicheck','fale').css('background-position','0 0');
						}
						
						that._triggerColumn(false,li, event);
					}else{
						check.attr('multicheck','true').css('background-position','0 -40px');
						li.parents('li').children('.xw-datagrid-menu-item-check').attr('multicheck','true').css('background-position','0 -40px');
						
						that._triggerColumn(true,li, event);
					}
				});
				
				this.head.bind('mouseover',function(event){
					var tdNode = jQuery(event.target).closest('th',event.currentTarget);
					tdNode.children('.xw-datagrid-column-menu').show();
					if(!that.overCol) that.overCol = tdNode;
					else if(tdNode[0] != that.overCol[0]){
						(!that.clickCol || that.clickCol[0] != that.overCol[0]) && that.overCol.children('.xw-datagrid-column-menu').hide();
						that.overCol = tdNode;
					}
				});
				
				this.head.bind('mouseleave',function(event){
					(that.overCol && ((that.clickCol ? that.clickCol[0] : null) != that.overCol[0])) && that.overCol.children('.xw-datagrid-column-menu').hide();
				});
				var that = this;
				this.head.bind('mouseup',function(event){
					that._bindMouseup(event);
				});
			},
			_bindMouseup : function(event){
				var target = jQuery(event.target);
				if(target.hasClass('xw-datagrid-column-menu')){
					this._colmenu.css('left',event.clientX+'px')
								 .css('top',target.offset().top+target.height() - 4+'px')
								 .show();
					this.clickCol && this.clickCol.children('.xw-datagrid-column-menu').hide();
					this.clickCol = target.parent();
				}
			},
			_createColumnMenu : function(){
				if(!this._opt.binddataset || !Xwing.getDataset(this._opt.binddataset)) return;
				//check Upper List Item. 
				var upper = [];
				if(!this._colmenu)
					this._colmenu = jQuery('<div class="xw-datagrid-menu xw-datagrid-menu-contextmenu" style="display:none;"/>').css('top',(this.getTop()+this.head.height() -2)+'px');
				
				var ui = this._colmenu.children('ul.xw-datagrid-menu');
				if( ui.length != 0 )
					ui.children('li').remove(); 
				else 
					ui = jQuery('<ul class="xw-datagrid-menu" style="margin:0px;padding:0px;list-style:none;" />').appendTo(this._colmenu);
				
				var row = this._ast.head
					, that = this;
				for(var i=0, l=row.length; i < l; i++){
					var a = row[i].getCell()
						, tmpUpper = [];
					for(var k=0; k < a.length; k++){
						var cell = a[k]
							, colspan = cell.getColspan()
							, colindex = cell._opt.colindex;
						var li = jQuery('<li class="xw-datagrid-menu xw-datagrid-menu-col" cx="'+colindex+'" ri="'+i+'" ></li>');
						jQuery("<span class='xw-datagrid-menu xw-datagrid-menu-item xw-datagrid-menu-item-check' multicheck='true' style='background-position:0 -40px;' ></span>").appendTo(li);
						jQuery("<span class='xw-datagrid-menu xw-datagrid-menu-item' style='line-height:20px;margin:1px 10px 1px 3px;' ></span>").text(cell.getText()).appendTo(li);
						tmpUpper.push([(colindex - colspan + 1),cell._opt.colindex,li]);
						
						// create Depth
						for(var j=0; j < upper.length; j++){
							var info = upper[j];
							if(info[0] <= colindex && colindex <= info[1]){
								if(!info[2].children('ul')[0]){
									info[2].append('<span class="xw-datagrid-menu xw-datagrid-menu-item xw-datagrid-menu-item-arrow" ></span>');
									ui = jQuery('<ul class="xw-datagrid-menu xw-datagrid-menu-contextmenu" style="margin:0px;padding:0px;list-style:none;position:relative;top:-12px;" />').appendTo(info[2]);
									info[2].bind('mouseover',function(event){
										$(event.target).closest('li',event.srcElement).children('ul').show();
									});
									info[2].bind('mouseleave',function(event){
										$(event.target).closest('li',event.srcElement).children('ul').hide();
									});
								}else
									ui = info[2].children('ul');
								break;
							}
						}
						ui.append(li);
						
					}
					upper = tmpUpper;
				}
				setTimeout(function(){
					that._colmenu.width((that._colmenu.width()+2)+'px');
					that._colmenu.find('ul:not(:first)').css('left',that._colmenu.width()+'px').hide();
					that._colmenu.hide();
				},0);
				
				this._colmenu.appendTo('body');
			},
			_triggerColumn : function(show, li, event){
				var cx = li.attr('cx'),
					ri = parseInt(li.attr('ri')),
					col, classname, cell
					pre = li.prev()[0],
					prev_cx = pre ? parseInt(pre.getAttribute('cx')) + 1 : cx,
					that = this;
				var licx = cx
					, liri = ri;
				li.find('li').each(function(idx, ui){
					licx = ui.getAttribute('cx'),
					liri = parseInt(ui.getAttribute('ri'));
					
					cell = that.head_thead.children('tr:eq('+liri+')').children('th[cx='+licx+']');
					show ? cell.show() : cell.hide();
				});
				li.parents('li').each(function(idx, ui){
					licx = ui.getAttribute('cx'),
					liri = parseInt(ui.getAttribute('ri'));
					
					
					cell = that.head_thead.children('tr:eq('+liri+')').children('th[cx='+licx+']');
					colspan = parseInt(cell.attr('colspan'));
					
					if( colspan && colspan != 1){
						show ? cell.attr('colspan', colspan+1) : cell.attr('colspan', colspan-1);
					}else{
						show ? (cell.css('display') == 'none' ? cell.show() : cell.attr('colspan',colspan+1))  : cell.hide();
					}
				});
				cell = that.head_thead.children('tr:eq('+ri+')').children('th[cx='+cx+']');
				show ? (li.find('li').length != 0 ? cell.show().attr('colspan',li.find('li').length) : cell.show()) : cell.hide();
				for(; prev_cx <= cx ; prev_cx++){
					classname = that.body_area.find('td[cx='+prev_cx+']:first')[0] ? that.body_area.find('td[cx='+prev_cx+']:first')[0].className : '';
					classname = classname.replace(' select','');
					
					cell = Xwing.getWidget(classname)
					, col = this._ast.column[prev_cx];
					// TODO
					this._setCssRules('.'+classname, cell._getCss()+(show ? '' : 'display:none;'));
					jQuery.each(col._columns, function(idx, ui){
						show ? jQuery(ui).show() : jQuery(ui).hide();
					});
				}
			},
			/**
			 * update dataset and set key focus
			 */
			_blurEditMode : function() {
				this.body.focus();
			},
			_isEditMode : function() {
				//return this.body_table._editpart && this.body_table._editpart.is(":visible");
				return this.body_table._editpart && (this.body_table._editpart.css('display') != 'none');
			},
			_doDataMode : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (!dsObj || !this._isEditMode()) 
					return;

				this._blurEditMode();
				
				var editpart = this.body_table._editpart,
					cy = editpart.attr('cy'),
					target = this.body.find('tr[cy=' + cy + ']:not(.editable)');
				
				target.css('display', '');
				
				// display checkbox 값 변경 
				editpart.find('div.xw-datagrid-display-checkbox').each(function(i, e){
					target.children('td[cx='+e.parentElement.getAttribute('cx')+']').find('.xw-datagrid-display-checkbox-item')[0].className = e.children[0].className;
				});
				
				if (this.getCheckbox()) {
					var span = editpart.find('span.xw-datagrid-cell-checkbox');
					target.find('span.xw-datagrid-cell-checkbox').attr('value', span.attr('value')).
						css('background-position', span.css('background-position'));
				}
				
				editpart.hide();
				editpart.insertAfter(this.body_table);																						
			},
			_doEditMode : function(td) {
				if(this._isEditMode() || this.body.find('.empty').length != 0 ) return;
				
				var target = jQuery(td).closest('tr'),
					editpart = this.body_table._editpart,
					cy = target.attr('cy'),
					that = this;
				target = this.body.find('tr[cy=' + cy + ']:not(.editable)');
				
				if (this.getTextwrap() != 'none') {
					for(var i=0; i < target.length ; i++){
						var height = target[i].clientHeight;					
						jQuery(editpart[i]).height(height).find('td>div.xw-shell').each(function(i, e) {
							Xwing.getWidget(e.getAttribute('_xid')).setHeight(height - 1);
						});
					}
				}
				editpart.$fixedTrs.insertBefore(target.filter('tr.fixed')[0]);
				editpart.$trs.insertBefore(target.filter('tr:not(.fixed)')[0]);
				if (this.getCheckbox()) {
					var span = target.find('span.xw-datagrid-cell-checkbox');
					editpart.$fixedTrs.find('span.xw-datagrid-cell-checkbox').attr('value', span.attr('value')).
						css('background-position', span.css('background-position'));
				}
				
				if (this.getRownum()) {
					editpart.$fixedTrs.find('span.xw-datagrid-cell-rownum').text(target.find('span.xw-datagrid-cell-rownum').text());
				}
				
				editpart.attr('cy', cy).css('display', '');
				jQuery('td:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)', editpart).each(function(i, e) {
					var $td = jQuery(this),
						editCell = jQuery('>div.xw-shell', $td);
					
					if (editCell.length) {
						if (that._keyNavi) 
							return;
						
						var w = Xwing.getWidget(editCell.attr('id'));
						if (w) {					
							$td.innerWidth() != w.getWidth() && w.setWidth($td.innerWidth());
							$td.innerHeight() != w.getHeight() && w.setHeight($td.innerHeight());
							
							// checkbox일 떄..... setLabel에 관한 얘기들 허허허허허허...........
							if( w.getAlias() == 'checkbox' && w._opt.label && typeof w._opt.label == 'object'){
								if( w._opt.label.domaindataset ){
									var domaindataset = Xwing.getDataset(w._opt.domaindataset)
										, binddataset = Xwing.getDataset( w._opt.binddataset);
									if( domaindataset )
										w.setLabel(domaindataset.lookUp(w._opt.domaincodecolumn, binddataset.getValue(cy, w._opt.bindcolumn), w._opt.domaintextcolumn));
								}else if ( w._opt.label.bindcolumn ){
									var binddataset = Xwing.getDataset( w._opt.binddataset);
									w.setLabel(binddataset.getValue(cy,w._opt.label.bindcolumn));
								}
							}
						}
					} else {
						var id = that._getIdByClass($td, 'body');
						jQuery('>span', $td).text(jQuery('td.' + id + '>span', target).text());

						// display checkbox 
						if( $td.children('div').hasClass('xw-datagrid-display-checkbox') || $td.children('span').hasClass('xw-datagrid-display-checkbox')){ //xw-datagrid-display-checkbox를 변경함 
							$td.find('.xw-datagrid-display-checkbox-item')[0].className = target.find('td[cx='+$td.attr('cx')+']').find('.xw-datagrid-display-checkbox-item')[0].className;
						}
					}
				});
				
				
				// TODO
				//this._selectRow(cy, false);
				this._selectedLastColIdx = (jQuery(td).attr('cx') ? jQuery(td).attr('cx') : this._selectedLastColIdx);
				(this.getSelectedrange() == 'row') && this._selectRow(cy, false);
				
				if (jQuery.browser.webkit && this._one === undefined) {
					editpart.css('display', 'none');
					setTimeout(function() { 
						editpart.css('display', ''); 
					}, 0);
					
					this._one = true;
				} 
				setTimeout(function() { target.hide(); }, 0);
				var id = this._getIdByClass(td, 'body');
				id = editpart.find('td.' + id).children('div.xw-shell').attr('id');
				if (id) {
					var widget = Xwing.getWidget(id);
					widget && widget.focus && widget.focus();
				}
			},
			_bindKeyEvent : function() {
				if (window.xwingIDE) 
					return;

				var that = this;		
				this.body.bind('keydown', function(event) {
					if (xwing.widget.DataGrid._CTRL_KEY_SET.indexOf(event.keyCode) == -1)
						return;
					
					event.preventDefault();
					event.stopPropagation();
					
					switch(event.keyCode) {
						case Xwing.key.SPACE:
							if (that.getCheckbox()) {
								var dsObj = Xwing.getDataset(that.getBinddataset());
								if (dsObj) {
									var cursor = dsObj.getCursor();
									var chk = jQuery('tr[cy=' + cursor +']', that.body_fix_tbody).find('span.xw-datagrid-cell-checkbox');								
									that._toggleCheckbox(chk);
								}
							}
							return;
						case Xwing.key.ENTER:
							that._doDataMode();
							return;
					}
					
					if (that._dTimeout) {
						clearTimeout(that._dTimeout);
					}
					
					(function(keyCode) {
						that._dTimeout = setTimeout(function() {
							that._doNaviDataMode(keyCode,event.shiftKey);
							that._dTimeout = null;
						}, 10);
					})(event.keyCode);
				});
			},
			_doNaviDataMode : function(keyCode, shiftKey) {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				var cursor = dsObj.getCursor();
				if(shiftKey && this.getMultiselectable() && !this._keystand){
					var $cell = jQuery('tr[cy='+cursor+'] > td[cx='+this._selectedLastColIdx+']',this.body);
					this._keystand = true;
					this._cacheSelect.standard =  $cell[0];
					this._cacheSelect.preselect =  $cell[0];
				}
				
				switch (keyCode) {
					case Xwing.key.DOWN_ARROW:
						if (cursor + 1 < dsObj.size()) {
							if(shiftKey && this.getMultiselectable()){
								if(this.getSelectedrange() == 'row'){
									if(jQuery('tr[cy='+(cursor + 1)+']',this.body).hasClass('select')){
										this._doDataMode();
										this._selectRow(cursor, true);
										this._revealSelectRow(cursor+1);
									}else this.setSelection(cursor + 1, true,false,true);
								}else{
									this._setCacheSelect(cursor + 1, this._selectedLastColIdx, false);
									this._cacheSelect.preselect =  jQuery('tr[cy='+(cursor+1)+'] > td[cx='+this._selectedLastColIdx+']',this.body)[0];
									this._revealSelectRow(cursor + 1);
								}
								dsObj.setCursor(cursor + 1, this);
							}else{
								this._keystand = false;
								this._initCacheSelect();
								dsObj.setCursor(cursor + 1);
							}
						}
						break;
					case Xwing.key.UP_ARROW:
						if (cursor - 1 >= 0) {
							if(shiftKey && this.getMultiselectable()){
								if(this.getSelectedrange() == 'row'){
									if(jQuery('tr[cy='+(cursor - 1)+']',this.body).hasClass('select')){
										this._selectRow(cursor, true);
										this._revealSelectRow(cursor-1);
									}else
										this.setSelection(cursor - 1, true,false,true);
								}else{
									if((cursor-1) < 0 ) return;
									this._setCacheSelect(cursor - 1, this._selectedLastColIdx, false);
									this._cacheSelect.preselect =  jQuery('tr[cy='+(cursor-1)+'] > td[cx='+this._selectedLastColIdx+']',this.body)[0];
									this._revealSelectRow(cursor - 1);
								}
								dsObj.setCursor(cursor - 1, this);
							}else{
								this._keystand = false;
								this._initCacheSelect();
								dsObj.setCursor(cursor - 1);
							}
						}
						break;
					case Xwing.key.LEFT_ARROW: case Xwing.key.RIGHT_ARROW:
						if(this.getSelectedrange() == 'row' ) return;
						var forward;
						if(Xwing.key.LEFT_ARROW == keyCode){
							forward = false;
							if( xwing.Util.parseInt(this._selectedLastColIdx) - 1 < 0) return;
							this._selectedLastColIdx = xwing.Util.parseInt(this._selectedLastColIdx) - 1;
						}else{
							var cnt = 0
							, multiIdx = +jQuery('td[cx='+this._selectedLastColIdx+']',this.body).parent().attr('ri');
							for(var i=0,l=this._ast.body; i<l.length ; i++){
								cnt += l[i].getCell().length;
							}
							forward = true;
							if(xwing.Util.parseInt(this._selectedLastColIdx) + 1 > (cnt-1)) return;
							this._selectedLastColIdx = xwing.Util.parseInt(this._selectedLastColIdx) + 1;
							if(multiIdx < +jQuery('td[cx='+this._selectedLastColIdx+']',this.body).parent().attr('ri')) forward = false;
						}
						if(shiftKey && this.getMultiselectable()){
							this._setCacheSelect(cursor, this._selectedLastColIdx, false);
							this._cacheSelect.preselect =  jQuery('tr[cy='+cursor+'] > td[cx='+this._selectedLastColIdx+']',this.body)[0];
							
						}else{
							this._keystand = false;
							this._initCacheSelect();
							this._cacheSelect.standard = '';
							this._setCacheSelect(cursor, this._selectedLastColIdx );
						}
						this._setSideScrollbar(forward,cursor , this._selectedLastColIdx);
						break;
//					case Xwing.key.SPACE:
//						if (this.getCheckbox()) {
//							var chk = jQuery('tr[cy=' + cursor +']', this.body_fix_tbody).find('span.xw-datagrid-cell-checkbox');
//							that._toggleCheckbox(chk);
//						}
//						break;
//					case Xwing.key.ENTER:
//						that._doDataMode();
//						break;
				}
			},
			_doNaviEditMode : function(keyCode, xid) {
				try {
					this._keyNavi = true;
					
					var rendered = this._getRenderedRange();
					var widget = Xwing.getWidget(xid); 

					switch (keyCode) {
						case Xwing.key.DOWN_ARROW:
							var cy = widget._getJShell().closest('tr', this.body).attr('cy');
							this._blurEditMode();
							this.setSelection(++cy, true, true);
							
							if (jQuery.browser.msie) setTimeout(function() { widget.focus && widget.focus(); }, 0); 
							else widget.focus && widget.focus();
							
							break;
						case Xwing.key.UP_ARROW:
							var cy = widget._getJShell().closest('tr', this.body).attr('cy');
							this._blurEditMode();
							
							this.setSelection(--cy, true, true);
							
							if (jQuery.browser.msie) setTimeout(function() { widget.focus && widget.focus(); }, 0); 
							else widget.focus && widget.focus();
							
							break;
						case Xwing.key.TAB:
							var editpart = this.body_table._editpart;
							if (editpart.$trs.find('td:last>.xw-shell').is(widget.getShell())) {
								var cy = widget._getJShell().closest('tr', this.body_tbody).attr('cy');
								this.setSelection(++cy, true, true);
							}
							
							break;
						case Xwing.key.ENTER:
							if (widget.getAlias() == 'combo') {
								if (!widget.isDropdown()) return;
								widget._unexpand();
							}
							
							this._doDataMode();
							break;
					}					
				} catch (e) {
					Xwing.error(e);
				} finally {
					this._keyNavi = false;
				}
			},
			_getNode : function(selector) {
				return jQuery(selector, this.grid);
			},
			_createFixedAreaPart : function() {
				var fixedColIndex = this._getFixedColIndex();
				
				this.head_fix_div = jQuery("<div class='xw-datagrid-header-fix'/>");
				this.head_fix_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.head_fix_colgroup = jQuery("<colgroup/>");
				this.head_fix_thead = jQuery("<thead/>");
				
				var thead = this.head_fix_thead;
				this.head_thead.find('tr').each(function(i, e) {
					thead.append(e.cloneNode(false));
				});
				
				if (fixedColIndex != -1) {
					var colgroup = jQuery(this.head_colgroup.children().get(fixedColIndex)).prevAll().andSelf();
					this.head_fix_colgroup.append(colgroup);
					var $trs = thead.find('tr');
					
					this.head_thead.find('tr').each(function(i, e) {
						var thgroup = jQuery(this).find('th[cx=' + fixedColIndex + ']').prevAll().andSelf();
						jQuery($trs[i]).append(thgroup);
					});
				}
				
				
				this.head.prepend(this.head_fix_div.append(this.head_fix_table.append(this.head_fix_colgroup).append(this.head_fix_thead)));
				
				this.body_fix_div = jQuery("<div class='xw-datagrid-body-fix'/>");
				this.body_fix_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.body_fix_colgroup = jQuery("<colgroup/>");
				this.body_fix_tbody = jQuery("<tbody/>");
		
				if (fixedColIndex != -1) {
					var colgroup = jQuery(this.body_colgroup.children().get(fixedColIndex)).prevAll().andSelf();
					this.body_fix_colgroup.append(colgroup);
				}

				this.body_fix_div.append(this.body_top.clone());
				this.body_fix_div.append(this.body_fix_table.append(this.body_fix_colgroup).append(this.body_fix_tbody));
				this.body_fix_div.append(this.body_bottom.clone());
				
				this.body.prepend(this.body_fix_div);
				
				this.body_top = jQuery('div.xw-datagrid-body-top', this.body);
				this.body_bottom = jQuery('div.xw-datagrid-body-bottom', this.body);
				
				this.summ_fix_div = jQuery("<div class='xw-datagrid-summary-fix'/>");
				this.summ_fix_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.summ_fix_colgroup = jQuery("<colgroup/>");
				this.summ_fix_tbody = jQuery("<tbody/>");
				
				var tbody = this.summ_fix_tbody;
				this.summ_tbody.find('tr').each(function(i, e) {
					tbody.append(e.cloneNode(false));
				});
				
				if (fixedColIndex != -1) {
					var colgroup = jQuery(this.summ_colgroup.children().get(fixedColIndex)).prevAll().andSelf();
					this.summ_fix_colgroup.append(colgroup);
					var $trs = tbody.find('tr');
					
					this.summ_tbody.find('tr').each(function(i, e) {
						var tdgroup = jQuery(this).find('td[cx=' + fixedColIndex + ']').prevAll().andSelf();
						jQuery($trs[i]).append(tdgroup);
					});
				}
				
				this.summ.prepend(this.summ_fix_div.append(this.summ_fix_table.append(this.summ_fix_colgroup).append(this.summ_fix_tbody)));
				
				this._bind(this.body_fix_table, 'mousedown', function(event) {
					if (this.getCheckbox()) {
						var target = jQuery(event.target);
						this.body_fix_table._check = target.hasClass('xw-datagrid-cell-checkbox');
						this.body_fix_table._check && this._activeCheckbox(target);
					}
				});
				
				this._bind(this.body_fix_table, 'mouseup', function(event) {
					if (this.getCheckbox() && this.body_fix_table._check) {
						var target = jQuery(event.target);
						target.hasClass('xw-datagrid-cell-checkbox') && this._toggleCheckbox(target);
					}
				});
				
				if( !this.getGroupable()){
					this._bind(this.body_fix_table, 'dblclick', this._handleDblClickEvent);
					this._bind(this.body_fix_table, 'mouseup', this._handleMouseUpEvent);
				}
				
				var that = this;
				this.body_fix_div.mousewheel(function(event, delta, deltaX, deltaY) {
					var top = that.body_area.scrollTop();
					
					top -= delta * 30;
					that.body_area.scrollTop(top);
					
					event.preventDefault();
				});
			},
			_createHeaderPart : function() {
				this.head = jQuery("<div class='xw-datagrid-area-header'/>");
				this.head_area = jQuery("<div class='xw-datagrid-header-area'/>");
				this.head_resize = jQuery("<div class='xw-datagrid-header-resize' style='padding-right: 20px;'/>");
				this.head_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.head_colgroup = jQuery("<colgroup/>");

				for ( var i = 0, l = this._ast.column.length, c; i < l; i++) {
					c = jQuery('<col/>').attr('width', this._ast.column[i].getWidth() + 'px');					
					c.appendTo(this.head_colgroup);
					this._ast.column[i].addColumn(c);
				}
				
				this.head_thead = jQuery("<thead/>");
				
				var heightSum = 0,
					height,
					id = this._getCssId() + '-h-r';

				var thTemplate = [
				    '<th ', '', '', 'style="padding: 0;">',
				    '',//'	<div class="xw-datagrid-column-resizer xw-datagrid-row"/>',
				    '',//'	<div class="xw-datagrid-column-menu xw-datagrid-row" style="display:none;"/>',
					'	<div class="xw-datagrid-head-title">',
					'',//'		<div class="xw-datagrid-head-contents xw-datagrid-row" style="width:10000px;"></div>',
					'	</div>',
					'</th>'
				];
				
				for ( var i = 0, l = this._ast.head.length; i < l; i++) {
					height = this._ast.head[i].getHeight();
					
					var $tr = jQuery('<tr class="' + id + i + '"/>');					
					var ths = this._ast.head[i].getCell();
					var thStr = '';
					var width = -1;
					
					for (var j = 0, k = ths.length , rowspan, _height; j < k; j++) {
						rowspan = ths[j].getRowspan();
						_height = height*rowspan - 4;
						if( jQuery.browser.msie || (jQuery.browser.mozilla && $.browser.version == '11.0')){
							width = this._ast.column[ths[j]._getColIndex()].getWidth();
							//alert(width);
						}
						
						thTemplate[1] = ths[j]._getSpan();
						thTemplate[2] = ' class="' + id + i + 'c' + j + '" cx="' + ths[j]._getColIndex() + '" ';
						thTemplate[4] = '	<div class="xw-datagrid-column-resizer xw-datagrid-row" style="height:'+_height+'px;'+(width == 0 ? 'width:0px;' : '')+'" />'
						thTemplate[5] = '	<div class="xw-datagrid-column-menu xw-datagrid-row" style="display:none;height:'+height*rowspan+'px;" />'
						thTemplate[7] = '		<div class="xw-datagrid-head-contents xw-datagrid-row" style="width:10000px;height:'+_height+'px;"></div>'
						thStr += thTemplate.join('');
					};
					
					$tr.append(jQuery(thStr));
					
					var $contents = jQuery('.xw-datagrid-head-contents', $tr);
					
					for (var j = 0, k = ths.length ; j < k; j++) {						
						var $td = jQuery($contents[j]);
						$td.html('<span class="xw-datagrid-font '+(ths[j]._opt.displaytype == 'checkbox' ? 'xw-datagrid-head-display-checkbox xw-datagrid-display-checkbox xw-checkbox-item xw-checkbox-unchecked' : '')+'">'+(ths[j]._opt.displaytype == 'checkbox' ? '' : xwing.Util.encodeHtml(ths[j].getText()))+'</span>');
						$td.append(jQuery('<span class="xw-datagrid-column-sort"></span>'));
					}
					
					this.head_thead.append($tr);
					heightSum += height;
					
					$tr.children('th').each(function(i, e) {
						var $node = jQuery(this);
						if (xwing.Util.parseInt($node.attr('colspan')) > 1) {
							$node.children('.xw-datagrid-column-resizer').remove();
						}					
					});
				}
				
				this.head.height(heightSum);				
				this.head.append(this.head_area.append(this.head_resize.append(this.head_table.append(this.head_colgroup).append(this.head_thead))));
				this.grid.append(this.head);
				
				// tomcat에 올릴 때 이것 때문에 rendering이 안됨 -> textwrap이 pre || prewrap 인 경우를 대비해서 만든 것이기 때문에.. 
//				setTimeout(function() {
//					jQuery('.xw-datagrid-row', this.head_thead).each(function(i, e) {
//						jQuery(this).height(jQuery(this).closest('th').height());
//					});					
//				}, 10);				
			},
			_createBodyPart : function() {
				this.body = jQuery("<div class='xw-datagrid-area-body xw-mod-background' tabindex='-1' />");
				this.body_area = jQuery("<div class='xw-datagrid-body-area' tabindex='-1' />");
				this.body_top = jQuery('<div class="xw-datagrid-body-top" tabindex="-1" />');
				this.body_bottom = jQuery('<div class="xw-datagrid-body-bottom" tabindex="-1" />');
				this.body_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.body_colgroup = jQuery("<colgroup/>");
				
				var colgroup = this.head_colgroup.children().clone();
				colgroup.appendTo(this.body_colgroup);
				
				for ( var i = 0, l = this._ast.column.length; i < l; i++) {
					this._ast.column[i].addColumn(jQuery(colgroup.get(i)));
				}
				
				this.body_tbody = jQuery("<tbody/>");
				this.body_area.append(this.body_top);
				this.body_area.append(this.body_table.append(this.body_colgroup).append(this.body_tbody));
				this.body_area.append(this.body_bottom);
				
				this.grid.append(this.body.append(this.body_area));
				
				if( !this.getGroupable()){
					this._bind(this.body_table, 'dblclick', this._handleDblClickEvent);
					this._bind(this.body_table, 'mouseup', this._handleMouseUpEvent);
				}
				this._bindScrollEvent();
			},
			_getIdByClass : function(node, area) {
				var cls = (node.attr('class') || '').split(' ');
				var p = this.getId() + '-' + area.charAt(0);
				
				for ( var i = 0; i < cls.length; i++) {
					if (cls[i].indexOf(p) != -1) 
						return cls[i];
				}
			},
			_createEditPart : function() {
				if (window.xwingIDE) 
					return;

				var that = this;				
				setTimeout(function() {				
					var editpart = jQuery(that._makeRowHtml(null, -100, true));
					
					editpart.addClass('editable');			
					editpart.find('td:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)').each(function(i, e) {
						var td = jQuery(this);
						var w = Xwing.getWidget(that._getIdByClass(td, 'body'));
						if (!w) return;
						
						var shell = w._getEditCell(td);
						// display checkbox인 경우 
						if( w._opt.dispalytype == 'checkbox'){
							td[0].innerHTML = '<span class="xw-datagrid-font xw-datagrid-display-checkbox xw-checkbox-item xw-checkbox-checked"></span>';
						}
						shell && td.addClass('editable').empty().append(shell); 
					});
					
					editpart.hide();
					
					editpart.$fixedTrs = editpart.filter('tr.fixed');
					editpart.$trs = editpart.filter('tr:not(.fixed)');
					
					that.body.append(editpart);
					that.body_table._editpart = editpart;
				}, 0);		
			},
			_getEditPart : function() {
				return this.body_table._editpart;
			},
			_createSummaryPart : function() {
				this.summ = jQuery("<div class='xw-datagrid-area-summary'/>");
				this.summ_area = jQuery("<div class='xw-datagrid-summary-area'/>");
				this.summ_resize = jQuery("<div class='xw-datagrid-summary-resize' style='height: 100%;padding-right: 20px;'/>");
				this.summ_table = jQuery("<table cellspacing='0' cellpadding='0' border='0' style='width:0;'/>");
				this.summ_colgroup = jQuery("<colgroup/>");
				
				var colgroup = this.head_colgroup.children().clone();
				colgroup.appendTo(this.summ_colgroup);
				
				for ( var i = 0, l = this._ast.column.length; i < l; i++) {
					this._ast.column[i].addColumn(jQuery(colgroup.get(i)));
				}
				
				this.summ_tbody = jQuery("<tbody/>");

				var heightSum = 0,
					height,
					id = this._getCssId() + '-s-r';
				
				for ( var i = 0, l = this._ast.summary.length; i < l; i++) {
					height = this._ast.summary[i].getHeight();
					
					var $tr = jQuery('<tr class="' + id + i + '"/>');				
					var ths = this._ast.summary[i].getCell();
					var thArray = [];
					var style = {};
					
					for (var j = 0, k = ths.length ; j < k; j++) {	
						if( ths[j].getExpr() ){
							style = ths[j]._doExpr(-1);
							value = style.value;
						}else{
							value = ths[j].getText();
						}
						thArray.push('<td ' + ths[j]._getSpan() + ' class="' + id + i + 'c' + j + '" cx="' + ths[j]._getColIndex() + '" '+((style && style.cell) ? 'style="'+style.cell+'"' : '')+' >');
						thArray.push('<span class="xw-datagrid-font" '+((style && style.text) ? 'style="'+style.text+'"' : '')+'>');
						thArray.push(xwing.Util.encodeHtml(value));
						thArray.push('</span></td>');
					}
					
					$tr.append(jQuery(thArray.join('')));
					this.summ_tbody.append($tr);
					heightSum += height;
				}
				
				this.summ.height(heightSum);		
				this.summ.append(this.summ_area.append(this.summ_resize.append(this.summ_table.append(this.summ_colgroup).append(this.summ_tbody))));
				// SORA topsummary
				if( this.getTopsummary() )
					this.body.before(this.summ.addClass('xw-datagrid-summary-topsummary'));
				else 
					this.grid.append(this.summ);
			},			
			_createPagePart : function() {
				// Create page part
				this.page =  jQuery("<div class='xw-datagrid-area-footer'/>");
				var that= this;
				this._key = {
						 value : ''
						, array : []
						, index : 0
					};
				if(!this.getPageable()) this.page.hide();
				var searchpart = jQuery("<div class='xw-datagrid-page-search' style='width:240px;float:left;' />").bind('click',function(event){
					var target = jQuery(event.target);
					if(target.hasClass('xw-datagrid-page-refresh')){
						that._searchKey('refresh');
					}else if(target.hasClass('xw-datagrid-page-search-left')){
						that._searchKey('prev');
					}else if(target.hasClass('xw-datagrid-page-search-right')){
						that._searchKey('next');
					}
					
				});
				var search = jQuery('<input class="xw-datagrid-page-search-input" style="width:165px;height:19px;margin:2px 10px 0px 2px;" />').bind('keyup',function(event){
					if(event.keyCode == Xwing.key.ENTER){
						that._searchKey("next");
					}else{
						(this.value) && that._resetSearchValues(this.value);
					}
				});
				searchpart.append(jQuery('<div class="xw-datagrid-page-refresh" />'))
				   		  .append(search)
						  .append(jQuery('<div class="xw-datagrid-page-search-left" />'))
						  .append(jQuery('<div class="xw-datagrid-page-search-right" />'));
				
				this.pagepart = jQuery("<div style='width:170px;float:right;margin-top:2px;' />");
				var pageStr = [];
				pageStr.push('<span class="xw-datagrid-page-icon xw-datagrid-page-icon-first" style="vertical-align:middle" />');
				pageStr.push('<span class="xw-datagrid-page-icon xw-datagrid-page-icon-prev" style="vertical-align:middle"/>');
				pageStr.push("<input typte='text' class='xw-datagrid-page-input' />");
				pageStr.push('<span class="xw-datagrid-page-text" style="vertical-align:middle">'+' of 50'+'</span>');
				pageStr.push('<span class="xw-datagrid-page-icon xw-datagrid-page-icon-next" style="vertical-align:middle"/>');
				pageStr.push('<span class="xw-datagrid-page-icon xw-datagrid-page-icon-last" style="vertical-align:middle"/>');
				
				this.page.append(searchpart)
						 .append(this.pagepart.append(jQuery(pageStr.join(''))));
				
				this._bind(this.pagepart,'click',this._clickPage);
				this.grid.append(this.page);
				this._doPageable();
				this._doPagenum();
				this._doPagesize();
			},
			_resetSearchValues : function(value){
				if(this._key.value != value){
					this._key = {
						 value : value
						, array : []
						, index : 0
					};
					var dsObj = Xwing.getDataset(this.getBinddataset());
					if(!dsObj) return;
					for(var row=0; row < dsObj.size(); row++){
						for(var col = 0, l = this._ast.column.length; col < l; col++){
							var key = dsObj.getValue(row, col);
							if(key.indexOf(value) != -1 ){
								if(this.getSelectedrange() == 'row'){
									if(this._key.array.indexOf(row) == -1 ) this._key.array.push(row);
								}else{
									this._key.array.push([row,col]);
								}
							}
						}
					}
				}
			},
			_searchKey : function(type){
				var dsObj = Xwing.getDataset(this.getBinddataset());
				switch(type){
				case 'prev':
					var index = this._key.array[(this._key.index-1)];
					if(index != undefined){
						(this._key.index != 0 ) && (this._key.index -= 1);
						if(this.getSelectedrange() == 'row'){
							dsObj.setCursor(index);
						}else{
							this._selectedLastColIdx = index[1];
							this._initCacheSelect();
							dsObj.setCursor(index[0]);
							this._setSideScrollbar(false,index[0], this._selectedLastColIdx);
						}
					}
					break;
				case 'next':
					var index = this._key.array[(this._key.index+1)];
					if(index != undefined){
						(this._key.index != (this._key.array.length -1) ) && (this._key.index += 1);
						if(this.getSelectedrange() == 'row'){
							dsObj.setCursor(index);
						}else{
							this._selectedLastColIdx = index[1];
							this._initCacheSelect();
							dsObj.setCursor(index[0]);
							this._setSideScrollbar(false,index[0], this._selectedLastColIdx);
						}
					}
					break;
				case 'refresh':
					this._doValue();
					break;
				}
			},
			_clickPage : function(event){
				var $target = jQuery(event.target);
				if($target.hasClass('xw-datagrid-page-icon-disabled') || !$target.hasClass('xw-datagrid-page-icon')) return;
				if($target.hasClass('xw-datagrid-page-icon-first')) this.setPagenum(1);
				else if($target.hasClass('xw-datagrid-page-icon-prev')) this.setPagenum(this.getPagenum() - 1);
				else if($target.hasClass('xw-datagrid-page-icon-next')) this.setPagenum(this.getPagenum() + 1);
				else if($target.hasClass('xw-datagrid-page-icon-last')) this.setPagenum(this.getTotalpage());
				
				this._fire('pageclick',{
					type:'pageclick',
					source: this,
					event: event,
					index : this.getPagenum()
				});				
			},
			setPageable : function(v){
				this._opt.pageable = v;
				this._doPageable();
			},
			getPageable : function(){
				return xwing.Util.parseBoolean(this._opt.pageable);
			},
			_doPageable : function(){
				(this.getPageable()) ? this.page.height("25px").show() : this.page.height("0").hide();
			},
			setPagenum : function(v){
				this._opt.pagenum = v;
				this._doPagenum();
			},
			getPagenum : function(){
				return xwing.Util.parseInt(this._opt.pagenum,1);
			},
			_doPagenum : function(){
				this.pagepart.find('input.xw-datagrid-page-input').val(this.getPagenum());
				this._resetPageicon();
			},
			setPagesize : function(totPage){
				this._opt.pagesize = totPage;
				this._doPagesize();
			},
			getPagesize : function(){
				return xwing.Util.parseInt(this._opt.pagesize,1);
			},
			_doPagesize : function(){
				this.pagepart.find('span.xw-datagrid-page-text').text("of "+this.getPagesize());
				this._resetPageicon();
			},
			_resetPageicon : function(){
				this.pagepart.find('span.xw-datagrid-page-icon-disabled').removeClass('xw-datagrid-page-icon-disabled');
				if(this.getPagenum() == 1)
					this.pagepart.find('span.xw-datagrid-page-icon-first, span.xw-datagrid-page-icon-prev').addClass('xw-datagrid-page-icon-disabled');
				if(this.getPagenum() == this.getPagesize())
					this.pagepart.find('span.xw-datagrid-page-icon-last, span.xw-datagrid-page-icon-next').addClass('xw-datagrid-page-icon-disabled');
			},
			_getCss : function() {
				var rules = [], opt = this._opt;
				return rules.join('');
			},
			_getCssFont : function() {
				var rules = [], opt = this._opt;
				
				opt.textpadding && rules.push('padding:' + xwing.Util.parseShorthand(opt.textpadding) + ';');
				opt.textwrap && rules.push('white-space:' + (opt.textwrap == 'pre' ? 'pre' : (opt.textwrap == 'prewrap' ? 'pre-wrap' : 'nowrap')) + ';');
				(opt.textwrap == 'prewrep') &&rules.push('word-break:break-word;');
				
				return rules.join('');
			},
			_createCssRules : function() {
				this.style = jQuery('<style type="text/css" rel="stylesheet" />');

				var id = this._getCssId(),
					rules = [],
					cssOdd = '', cssEven = '',
					setCssRules = function(rules, area, key) {
						for ( var i = 0, l = area.length; i < l; i++) {
							appendCssRule(rules, area[i], key + i);
							area[i].getCell && setCssRules(rules, area[i].getCell(), key + i + 'c');
						}
					},
					appendCssRule = function(rules, obj, key) {
						if (obj._getCss) {
							rules.push('.' + key + ' { ');
							rules.push(obj._getCss());
							rules.push(' } ');
						}
	
						if (obj._getCssFont) {
							rules.push('.' + key + ' .xw-datagrid-font { ');
							rules.push(obj._getCssFont());
							rules.push(' } ');
						}
					};
				
				appendCssRule(rules, this, id);
				
				cssEven = this._opt.evencolor && !this.getGroupable() ? 'background-color:' + this._opt.evencolor + ';' : '';
				cssOdd = this._opt.oddcolor && !this.getGroupable() ? 'background-color:' + this._opt.oddcolor + ';' : '';
				rules.push('.' + id + ' .even { '+cssEven+' }');
				rules.push('.' + id + ' .odd { '+cssOdd+' }');
				this.getSelectcolor && this.getSelectcolor && rules.push('.' + id + ' .select {'+(this.getSelectcolor() ? this.getSelectcolor() : 'background:#d8e9ee;')+' }');
				
				setCssRules(rules, this._ast.head, id + '-h-r');
				setCssRules(rules, this._ast.body, id + '-b-r');
				setCssRules(rules, this._ast.summary, id + '-s-r');
				if (this.style[0].styleSheet) {
					this.style[0].styleSheet.cssText = rules.join(' ');
					
					!jQuery.browser.msie && (this.style = this.style.clone());
					this.style.appendTo(jQuery('head'));
				} else {
					!jQuery.browser.msie && (this.style = this.style.clone());
					this.style.appendTo(jQuery('head'));
					
					this.style[0].appendChild(document.createTextNode(rules.join(' ')));
				}
			},
			_doBounds : function() {
				xwing.widget.Widget.prototype._doBounds.call(this);

				var bw = this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth() * 2;
				var bodyHeight = this.getHeight() - bw - this.summ.height() - this.head.height() - this.page.height(); // page도 추가 
				this.body.height(bodyHeight);
				
				this._doAutofit();
			},
			setAutofit : function(v) {
				this._opt.autofit = v;
				this._doAutofit();
			},	
			_doAutofit : function() {
				if (this._aTimeout) 
					return;
				
				var that = this;
				this._aTimeout = setTimeout(function() {
					try {
						if (!window.xwingIDE && that.getAutofit()) {							
							var sum = that.head_table.width();
							var initWidth = that._getNotFixWidth();
							if (sum <= 0){
								that._fitColumnWidth();
								jQuery.browser.msie && that._fitFixColumnWidth();	
								that._aTimeout = null;
								return;
							}
							if( sum + 100 <  initWidth || initWidth < sum + 100 ) sum = that._getNotFixWidth();
							var viewWidth = that.getWidth() - that.body_fix_div.width() - that.getBorderwidth() * 2 - 20;
							var cols = that.head_colgroup.find('col:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)');
							var widths = [];
							that._group && (that._group._allwidth = 0);
							cols.each(function(i) {
								var w = viewWidth * xwing.Util.parseInt(jQuery(this).attr('width'), 0) / sum,
									c = that.getColumn(this);
								if ( w <=0 || viewWidth <= 0 ){
									w = (that.getWidth() - that._getFixWidth() - that.getBorderwidth() * 2 - 20) * c.getWidth() / sum;
									if( w < 0 ) w = c.getWidth();
								}
								that._group && (that._group._allwidth += Math.round(w));
								widths.push(Math.round(w));
								c && c.setWidth(Math.round(w));
							});
							that._group && that._group._doAutofit(widths);
						}
						
						that._fitColumnWidth();
						jQuery.browser.msie && that._fitFixColumnWidth();	
					} catch (e) {}
					
					that._aTimeout = null;
				}, 0);
			},
			getAutofit : function() {
				return xwing.Util.parseBoolean(this._opt.autofit);
			},
			_fitColumnWidth : function() {
				var w = 0
				, that = this;

				jQuery('col', this.head_colgroup).each(function(idx, ui) {
					var width = xwing.Util.parseInt(jQuery(this).attr('width'));
					w += width;
					
					if( jQuery.browser.msie || (jQuery.browser.mozilla && $.browser.version == '11.0')){
//						that.head.find('th[cx='+idx+']').children('div.xw-datagrid-column-resizer, div.xw-datagrid-head-title').css('width',(width == 0 ? '0px' : ''));
					}
				});
				this.head_table.width(w);
				this.head_resize.width(w + 20);
				this.body_table.width(w);
				this.summ_table.width(w);
				this.summ_resize.width(w + 20);

				this._fitBottomScrollArea();
			},
			_fitFixColumnWidth : function(){
				var w = 0;
				
				jQuery('col', this.head_fix_colgroup).each(function() {
					w += xwing.Util.parseInt(jQuery(this).attr('width'));
				});
				this.head_fix_table.width(w);
				this.body_fix_table.width(w);
				this.summ_fix_table.width(w);
			},
			_makeRowHtml : function(dsObj, di, template) {
				var fixedColIndex = this._getFixedColIndex();
				var fixedTrArray = [];
				var trArray = [];
				var html = '';
				var tstyle = '';
				
				for ( var i = 0, l = this._ast.body.length, cells, style; i < l; i++) {
					cells = this._ast.body[i].getCell();
					style = this._ast.body[i].getId() + ' ' + (di % 2 ? 'even' : 'odd');
					if(this._isGroupSum() && dsObj && dsObj.getRow(di) && dsObj.getRow(di)._GROUP != undefined){
						style+=' xw-datagrid-groupsum'; 
						if( this._ast.body[i].getGroupsumstylerendering() ){
							tstyle = 'style="'+this._ast.body[i]._doGroupsumstylerendering( dsObj.getRow(di)._GROUP)+'"';
						}
					}
					fixedTrArray.push('<tr cy="' + di + '" ri="'+i+'" class="' + style + ' fixed" '+(tstyle != '' ? tstyle : '')+'>');
					trArray.push('<tr cy="' + di + '" ri="'+i+'" class="' + style + '" '+(tstyle != '' ? tstyle : '')+'>');
					
					if (i == 0 && this.getRownum() && !this.getGroupable()) {
						fixedTrArray.push('<td class="xw-datagrid-rownum" align="center" valign="center" rowspan="' + l + '">');
						fixedTrArray.push('<span class="xw-datagrid-cell-rownum">' + (di + 1) + '</span>');
						fixedTrArray.push('</td>');
					}
					
					if (i == 0 && this.getCheckbox() && !this.getGroupable()) {
						fixedTrArray.push('<td class="xw-datagrid-checkbox" align="center" valign="center" rowspan="' + l + '">');
						fixedTrArray.push('<span class="xw-datagrid-cell-checkbox" ');
						dsObj && this._getRowState(dsObj, di).checked && fixedTrArray.push('style="background-position: 0px -40px;" value="checked" ');
						fixedTrArray.push('/></td>');
					}
					
					for ( var j = 0, k = cells.length, temp, cell, value, style; j < k; j++) {
						cell = cells[j];
						temp = fixedColIndex == -1 ? trArray : (cell._getColIndex() > fixedColIndex ? trArray : fixedTrArray);
						value = undefined;
						style = {};
						
						if (!template) {
							if (cell.getExpr() && !(this._isGroupSum() && dsObj && dsObj.getRow(di) && dsObj.getRow(di)._GROUP != undefined)) {
								style = cell._doExpr(di);
								value = style.value;
							}
							
							if (value === undefined && dsObj) {
								value = cell._getValue(dsObj, di);
							}
							
							if(this._isGroupSum() && cell.getGroupsum() && cell.getGroupsumrendering() && (dsObj.getRow(di)._GROUP != undefined)){
								var tmp = cell._doGroupsumrendering(dsObj.getValue(di,cell._opt.bindcolumn), dsObj.getRow(di)._GROUP);
								if( tmp.value || tmp.value == "") value = tmp.value;
								if( tmp.cell ) style.cell = tmp.cell;
								if( tmp.text ) style.text = tmp.text;
							}
						}			
						// group 일 때, border 추가 
						if( this.getGroupable() && this._ast['body'].length == 1 && cell.getGroupborderright() ){
							if( !style.cell ) style.cell = "border-right:"+cell.getGroupborderright()+';';
							else style.cell += "border-right:"+cell.getGroupborderright()+';';
						}
						
						// display checkbox
						var cx = j+(this._ast.column.length*i);
						temp.push('<td ' + cell._getSpan() + ' class="' + cell.getId() + '" cx="'+cx+'" ');
						style.cell && temp.push('style="' + style.cell + '" ');			
						
						// display checkbox
						if( cell._opt.displaytype == 'checkbox' && cell._opt.checkboxcolumn ){
								temp.push('><div class="xw-datagrid-display-checkbox xw-checkbox-item-div" unselectable="on" style="vertical-align:middle;">'
											+'<div class="xw-datagrid-display-checkbox-item  xw-checkbox-item '+(value ==  cell._opt.truevalue ? 'xw-checkbox-checked' : 'xw-checkbox-unchecked')+'" style="margin:0px;"></div></div>'
											+'<span class="xw-datagrid-display-checkbox xw-datagrid-display-checkbox-label">'
											+( !dsObj ? '' : dsObj.getValue(di,cell._opt.checkboxcolumn ))
											+'</span>'
										+'</td>');
						}else{
							var checkbox = '';
							if(cell._opt.displaytype == 'checkbox')
								checkbox = 'xw-datagrid-display-checkbox xw-datagrid-display-checkbox-item xw-checkbox-item '+(value ==  cell._opt.truevalue ? 'xw-checkbox-checked' : 'xw-checkbox-unchecked');
							
							temp.push('><span class="xw-datagrid-font '+(checkbox ? checkbox : '')+'" ');
							(style.text && !checkbox) && temp.push('style="' + style.text + '"');						
							temp.push('>', (!checkbox ? value : '') , '</span></td>');
						} 
					}

					fixedTrArray.push('</tr>');
					trArray.push('</tr>');
				}

				fixedTrArray.length > 2 && (html = fixedTrArray.join(''));
				return html + trArray.join('');
			},
			_getRowState : function(dsObj, i) {
				var id = '_' + this.getId(),
					row = dsObj.getRow(i);

				!row[id] && (row[id] = {});
				return row[id];
			},
			_getRenderedRange : function() {
				return {
					top : +this.body_tbody.find('>tr:not(.editable):first').attr('cy'),
					bottom : +this.body_tbody.find('>tr:not(.editable):last').attr('cy') 
				};
			},			
			_getVisibleRange : function() {
				var scrollTop = this.body_area.scrollTop(),
					clientHeight = xwing.Util.parseInt(this.body_area[0].clientHeight, 0);
				return {
					top : Math.floor(scrollTop / this._ast.body._height),
					bottom : Math.ceil((scrollTop + clientHeight) / this._ast.body._height)
				};
			},			
			_getRenderRange : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				var range = this._getVisibleRange();
//				if(this.getVirtual() && this.getTextwrap() != 'prewrap'){
				if(this.getVirtual()){
					range.top -= this._pageRowCnt;
					range.bottom += this._pageRowCnt;
					
					if (range.top < 0) range.top = 0;
					if (range.bottom > dsObj.size() - 1) range.bottom = dsObj.size() - 1;
				}else{
					range.top = 0;
					range.bottom = dsObj.size() - 1;
				}
				return range;
			},
			_cleanUpRows : function(rendered, forward) {
				for ( var i = rendered.top, trs, l = rendered.bottom; i <= l; i++) {
					trs = this.body.find('tr[cy=' + i + ']');
					if (trs.hasClass('editable')) {
						this._doDataMode();
						trs = this.body.find('tr[cy=' + i + ']:not(.editable)');
					}					
					trs.detach();
				}
			},
			_renderRows : function(forward, init, rdTop, rdBottom) {
				var rendered = this._getRenderedRange();
				var render = (rdTop != null && rdBottom != null) ? { top : rdTop, bottom : rdBottom} :this._getRenderRange();
				var isEmpty = isNaN(rendered.top);
				if (! isEmpty) {
					if (rendered.top > rendered.bottom) {
						return;
					} else if (forward) {
						if (rendered.top == render.top) return;
						var limit = rendered.bottom - parseInt(this._pageRowCnt * 0.3);
						if (limit > render.bottom) 
							return;
					} else {
						if (rendered.bottom == render.bottom) return;
						var limit = rendered.top + parseInt(this._pageRowCnt * 0.3);
						if (limit < render.top) 
							return;
					}
				}
				
				try {
					var cleanRange = jQuery.extend({}, rendered),
						indices = [],
						nodes = [];
					
					if (forward) {
						cleanRange.bottom = render.top - 1;
					} else {
						cleanRange.top = render.bottom + 1;
					}
					for ( var i = render.top, l = render.bottom; i <= l; i++) {
						!this._rowCache[i] && this._cachePageRows(i);

						if (this._rowCache[i] && (isEmpty || i < rendered.top || i > rendered.bottom)) {
							indices.push(i);

							this._rowCache[i].each(function(i, e) {
								nodes.push(e);
							});
						}
					}
					if (nodes.length == 0)
						return;
					
					var $nodes = jQuery(nodes);
					if (isEmpty || rendered.bottom < indices[0]) {
						this.body_fix_tbody.append($nodes.filter('tr.fixed'));
						this.body_tbody.append($nodes.filter('tr:not(.fixed)'));
					} else {
						this.body_fix_tbody.prepend($nodes.filter('tr.fixed'));
						this.body_tbody.prepend($nodes.filter('tr:not(.fixed)'));
					}
					
					this._cleanUpRows(cleanRange, forward);
					delete nodes;

					for ( var i = 0, l = indices.length; i < l; i++) {
						this._suppressRow(indices[i]);
					}
					
					this._setRowHeight(isEmpty ? null : indices);
				} catch (e) {
					Xwing.error(e);
				}

				this._fitScrollArea();
				
				if (init) {
					var dsObj = Xwing.getDataset(this.getBinddataset());
					// 다시 생각해보자 SORA
					this.setSelection( (  this._keepscroll ?  this._keepscroll.cursor : dsObj.getCursor() ), true); // true 해야 함 ㅜㅜㅜㅜㅜㅜㅜ
				}
			},
			_cachePageRows : function(n) {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				var trArray = [];
				var range = {
					top : n < dsObj.size() ? n : dsObj.size(),
					bottom : (n + this._pageRowCnt) < dsObj.size() ? n + this._pageRowCnt : dsObj.size()
				};
				for ( var i = range.top, l = range.bottom; i < l; i++) {
					!this._rowCache[i] && trArray.push(this._makeRowHtml(dsObj, i));
				}
				
				var rows = jQuery(trArray.join(''));
				for ( var i = range.top, l = range.bottom; i < l; i++) {
					var row = rows.filter('tr[cy=' + i + ']');
					if (row.length) {
						this._rowCache[i] = row;
					}
				}
			},
			_fitScrollArea : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset()),
					rendered = this._getRenderedRange(),
					top_h = 0,
					bottom_h = 0;
				
				if (!isNaN(rendered.top)) {
					rendered.top != 0 && (top_h = rendered.top * this._ast.body._height);
					dsObj.size() != (rendered.bottom + 1) && (bottom_h = ((dsObj.size() - 1 - rendered.bottom) * this._ast.body._height));
				}
				
				this.body_top.height(top_h);
				this.body_bottom.height(bottom_h);

				this._fitBottomScrollArea();
			},
			_fitBottomScrollArea : function() {
				if( this.getVisible() ){
					var that = this;
					setTimeout(function() {
						that.body_area.css('width',( that.body.width() != 0 && that.body_fix_div.width() != 0 && that.body_fix_div.width() >= that.body.width())  ? '0' : '');
						that.body_fix_div.height(that.body_area[0].clientHeight == 0 ? '' : that.body_area[0].clientHeight);
					}, 0);
				}
			},
			setMultiselectable: function(v){
				this._opt.multiselectable = v;
			},
			getMultiselectable : function(){
				return xwing.Util.parseBoolean(this._opt.multiselectable);
			},		
			_getFixWidth : function(){
				var _fixwidth = 0;
				for( var i=0; i <= this._getFixedColIndex(); i++){
					var col = this._ast.column[i];
					_fixwidth += col.getWidth();
				}
				if( this.getCheckbox() ) _fixwidth += 22;
				if( this.getRownum() ) _fixwidth += 30;
				return _fixwidth;
			},
			_getNotFixWidth : function(){
				var cols = this.head_colgroup.find('col:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)');
				var width = 0;
				cols.each(function(i,el){
					width +=jQuery(el).width();
				})
				return width;
			},
			_selectRow : function(idx, multi) {
				// set selected index
				var rows = jQuery('tr[cy=' + idx + ']', this.body);
				if (multi) {
					(rows.length != 0) && rows.toggleClass('select');
					var selects = jQuery('tr[cy=' + idx + ']:not(.editable)', this.body)
					, that = this;
					selects.each(function(i,el){
						var index = that._cacheSelect.selected.indexOf(el);
						if(index == -1){
							that._cacheSelect.selected.push(el);
						}else{
							that._cacheSelect.selected.splice(index,1);
						}
					});
				} else {
					if (rows.length == 0) return;
					var selects = jQuery('tr[cy=' + idx + ']:not(.editable)', this.body)
					, that = this;
					this._initCacheSelect();
					selects.each(function(i,el){
						that._cacheSelect.selected.push(el);
					});
					rows.addClass('select');
				}
			},		
			_initCacheSelect : function(){
				for(var i=0,l=this._cacheSelect.selected.length; i<l ; i++){
					var $cell = jQuery(this._cacheSelect.selected[i]);
					$cell.removeClass('select');
				}
				this._cacheSelect.selected = [];
			},
			_setCacheSelect : function(rIdx, cIdx, toggle, mouse){
				var $cell =  jQuery('tr[cy='+rIdx+'] > td[cx='+cIdx+']',this.body);
				(!this._cacheSelect.standard) && (this._cacheSelect.standard = $cell[0]);
				(!this._cacheSelect.preselect) && (this._cacheSelect.preselect = $cell[0]);
				
				var stand = {row : +this._cacheSelect.standard.parentNode.getAttribute('cy') , col : +this._cacheSelect.standard.getAttribute('cx') }
				, pre = {row : +this._cacheSelect.preselect.parentNode.getAttribute('cy'), col : +this._cacheSelect.preselect.getAttribute('cx')}
				, cur = {row : rIdx, col : cIdx};
				var index = this._cacheSelect.selected.indexOf(this._cacheSelect.standard);
				var $stand =  jQuery(this._cacheSelect.standard);
				if( index == -1){
					this._cacheSelect.selected.push(this._cacheSelect.standard);
					$stand.addClass('select');
				}else if(toggle){
					$stand.removeClass('select');
					this._cacheSelect.selected.splice(index,1);
				}
				
				if($cell[0] == this._cacheSelect.standard && $cell[0] == this._cacheSelect.preselect ) return;
				this._addCasheSelect(stand, pre, cur, mouse, 'row');
				this._addCasheSelect(stand, pre, cur, mouse, 'col');
				this._removeCacheSelect(stand, pre, cur, mouse, 'row');
				this._removeCacheSelect(stand, pre, cur, mouse, 'col');
			},
			_addCasheSelect : function(stand, pre, cur, mouse, type){
				var add = {starty : stand.row, endy : stand.row, startx : stand.col, endx : stand.col}
				, area = 'col'
				, $tr, $td, addSel = [];
				if(stand[type] <= pre[type] && pre[type] < cur[type]){
					(type == 'row') ? (add.starty = pre[type] + 1) : ( add.startx = pre[type] + 1 );
					(type == 'row') ? (add.endy = cur[type]) : (add.endx = cur[type]);
				}else if(stand[type] >= pre[type] && pre[type] > cur[type]){
					(type == 'row') ? (add.starty = cur[type]) : (add.startx = cur[type]);
					(type == 'row') ? (add.endy = pre[type] - 1) : (add.endx = pre[type] - 1);
				}else return;
				
				if(type == 'col') area = 'row';
				if(stand[area] < cur[area] ){
					(area == 'col') ? (add.startx = stand[area]) : (add.starty = stand[area]);
					(area == 'col') ? (add.endx = cur[area]) : (add.endy = cur[area]);
				} else  {
					(area == 'col') ? (add.startx = cur[area]) : (add.starty = cur[area]);
					(area == 'col') ? (add.endx = stand[area]) : (add.endy = stand[area]);
				}
				for(var y = add.starty ; y <= add.endy ; y++){
					$tr = jQuery('tr[cy='+y+']',this.body);
					for(var x = add.startx ; x <= add.endx ; x++){
						//$td = $tr.children('td[cx='+x+']').addClass('select');
						$td = $tr.children('td[cx='+x+']');
						var index = this._cacheSelect.selected.indexOf($td[0]);
						if( index == -1){
							this._cacheSelect.selected.push($td[0]);
							addSel.push($td[0]);
						}
					}
				}
				jQuery(addSel).addClass('select');
			},
			_removeCacheSelect : function(stand, pre, cur, mouse, type){
				var remove = {starty : -1, endy : -1, startx : -1, endx : -1}
				, area = 'col'
				, $tr, $td, remSel = [];
				if(stand[type] <=  cur[type] &&  cur[type] < pre[type]){
					(type == 'row') ? (remove.starty =  cur[type] + 1) : (remove.startx = cur[type] + 1);
					(type == 'row') ? (remove.endy = pre[type]) : (remove.endx = pre[type]);
				}else if(stand[type] >=  cur[type] &&  cur[type] > pre[type]){
					(type == 'row') ? (remove.starty = pre[type]) : (remove.startx = pre[type]);
					(type == 'row') ? (remove.endy =  cur[type] - 1) : (remove.endx = cur[type] - 1);
				}else return;
				
				if(type == 'col') area = 'row';
				if(stand[area] < pre[area] ){
					(area == 'col') ? (remove.startx = stand[area]) : (remove.starty = stand[area]);
					(area == 'col') ? (remove.endx = pre[area]) : (remove.endy = pre[area]);
				} else  {
					(area == 'col') ? (remove.startx = pre[area]) : (remove.starty = pre[area]);
					(area == 'col') ? (remove.endx = stand[area]) : (remove.endy = stand[area]);
				}
				for(var y = remove.starty ; y <= remove.endy ; y++){
					if(y == -1 ) break;
					$tr = jQuery('tr[cy='+y+']',this.body);
					if($tr.length == 0 ) $tr = this._rowCache[y];
					for(var x = remove.startx ; x <= remove.endx ; x++){
						$td = $tr.children('td[cx='+x+']');
						var index = this._cacheSelect.selected.indexOf($td[0]);
						//if( index != -1 && !$td.hasClass('select')){
						if( index != -1 ){
							this._cacheSelect.selected.splice(index,1);
							remSel.push($td[0]);
						}
					}
				}
				jQuery(remSel).removeClass('select');
			},
			_setCacheRowSelect : function(cur_cy){
				if( !this._cacheSelect.standard) return;
				var stand_cy = parseInt(this._cacheSelect.standard.getAttribute('cy'))
					, pre_cy = parseInt(this._cacheSelect.preselect.getAttribute('cy'));
				
				var add = {start : -1, end : -1}
					, remove = {start : -1, end : -1}
					, that = this
					, $tr;
				// add 조건 
				(stand_cy <= pre_cy && pre_cy < cur_cy) && (add = { start : (pre_cy+1) , end : cur_cy});
				(cur_cy < pre_cy && pre_cy <= stand_cy) && (add = { start : cur_cy, end : (pre_cy-1)}); 
				// remove 조건 
				(stand_cy <= cur_cy && cur_cy < pre_cy) && (remove = { start : (cur_cy+1), end : pre_cy});
				(pre_cy < cur_cy && cur_cy <= stand_cy) && (remove = { start : pre_cy, end: (cur_cy -1)});
				// add & remove 조건 
				if(pre_cy <= stand_cy && stand_cy < cur_cy) {
					add = { start : stand_cy , end : cur_cy};
					(pre_cy != stand_cy ) && (remove = { start : pre_cy, end : stand_cy});
				}
				if(cur_cy < stand_cy && stand_cy <= pre_cy ) {
					add = { start : cur_cy , end : stand_cy};
					(stand_cy != pre_cy ) && (remove = { start : stand_cy, end : pre_cy});
				}
				
				for(var i = add.start; i <= add.end; i++){
					$tr = jQuery('tr[cy='+i+']',this.body);
					$tr.each(function(idx, el){
						var index = that._cacheSelect.selected.indexOf(el);
						if( index == -1){
							that._cacheSelect.selected.push(el);
						}
					});
					$tr.addClass('select');
				}
				
				for(var i = remove.start; i <= remove.end; i++){
					$tr = jQuery('tr[cy='+i+']',this.body);
					$tr.each(function(idx, el){
						var index = that._cacheSelect.selected.indexOf(el);
						if( index == -1){
							that._cacheSelect.selected.splice(index,1);
						}
					});
					$tr.removeClass('select');
				}
			},
			_setSideScrollbar : function(forward,rIdx, cIdx){
				// if Exist h-sideScroll, move scrollbar
				/*
				 * 1. check current location
				 * 2. check scrollbar & 
				 * 3. move bar
				 */
				var cells = jQuery('tr[cy=' + rIdx + ']', this.body).children('td:not(.xw-datagrid-rownum, .xw-datagrid-checkbox)').eq(cIdx);
				var that = this, width = 0, left = 0, right = 0,
					clientWidth = xwing.Util.parseInt(this.body_area[0].clientWidth,0),
					scrollLeft = this.body_area.scrollLeft(),
					fixed = this.body_fix_colgroup.children(':not(.xw-datagrid-rownum, .xw-datagrid-checkbox)').length;
				jQuery('col',this.head_colgroup).each(function(idx,el){
					var flag = ((idx+fixed) == cIdx );
					//left
					if( width < scrollLeft  && flag && !forward){
						that.body_area.scrollLeft( width ) ;
						return false;
					}
					//right
					width += jQuery(el).width();
					if((scrollLeft + clientWidth - 1) <= width && flag && forward){
						that.body_area.scrollLeft(width - clientWidth + 1);
						return false;
					}
					if(flag) return false;
				});
				
			},
			_revealSelectRow : function(row){
				// keep 
				if( this._keepscroll && row != 0){
					var dsObj = Xwing.getDataset(this.getBinddataset());
					if (!dsObj || row >= dsObj.size()) 
						return;
					
					var range = {
							top : Math.floor(this._keepscroll.top / this._ast.body._height),
							bottom : Math.ceil((this._keepscroll.top + xwing.Util.parseInt(this.body_area[0].clientHeight, 0)) / this._ast.body._height)
					};
					if( range.top <= row && row <= range.bottom ){
						this.body_area.scrollTop(this._keepscroll.top);
						if( this._keepscroll.left != 0 )this.body_area.scrollLeft( this._keepscroll.left ) ;
						
						var forward = true;
						if(range.top > row) forward = false; // Check Up or Down Direction
						this._renderRows(forward, false, range.top, range.bottom);
						this._keepscroll = false;
						dsObj.setCursor(row,this);
						return;
					}
					this._keepscroll = false;
				}
				var forward = true;
				var jTarget = jQuery('tr[cy=' + row + ']', this.body_tbody);
				if (!jTarget.length) {
					// 해당 Index가 view part에 존재하지 않을 경우 
					// 1.Create Visible Tr
					var dsObj = Xwing.getDataset(this.getBinddataset());
					if (!dsObj || row >= dsObj.size()) 
						return;
					
					var range = this._getVisibleRange();
					if(range.top > row) forward = false; // Check Up or Down Direction
					
					var visibleFloat = Math.ceil(xwing.Util.parseInt(this.body_area[0].clientHeight, 0) / this._ast.body._height);
					var render = {
							top : row
						  , bottom : (row + visibleFloat +  this._pageRowCnt) < dsObj.size() ? (row + visibleFloat +  this._pageRowCnt) : dsObj.size()
					};
					dsObj.setCursor(row,this);
					this._renderRows(forward, false, render.top, render.bottom);
					jTarget = jQuery('tr[cy=' + row + ']', this.body_tbody);
				}
				// 해당 Index로 이동
				var clientHeight = xwing.Util.parseInt(this.body_area[0].clientHeight, 0),
					scrollTop = this.body_area.scrollTop(),
					offsetTop = jTarget[0].offsetTop + this.body_top.height(),
					height = offsetTop + jTarget.height() * jTarget.length;
				if (scrollTop > offsetTop) {
					this.body_area.scrollTop(offsetTop);
				} else if ((scrollTop + clientHeight) < height) {
					if(!forward) this.body_area.scrollTop(height - clientHeight);
					else this.body_area.scrollTop(height - (jTarget.height() * jTarget.length));
				}
			},
			setSelection : function(idx, reveal, editable, multi) {
				!xwing.Util.is(idx, 'array') && (idx = [ idx || 0 ]);
				if (idx.length == 0) 
					return;
				if (reveal) 
					this._revealSelectRow(idx[0]);
				
				if (editable && (this.getEditwhen() != 'none') && this.getSelectedrange() == 'row') {
					var dsObj = Xwing.getDataset(this.getBinddataset());
					if (!dsObj || idx[0] >= dsObj.size()) 
						return;
					
					dsObj.setCursor(idx[0]);
					if (idx[0] < 0) 
						return;
					
					var jTd = jQuery('tr[cy=' + idx[0] + ']>td:first', this.body_tbody);
					this._doEditMode(jTd);
				} else {
					this._doDataMode();
					
					//if(!multi) this._initSelected();
					for ( var i = 0, l = idx.length, m = this.getMultiselectable(); i < l; i++) {
						// TODO Select Row or Cell
						if(this.getSelectedrange() == 'row'){
							//this._selectRow(idx[i], m);
							this._selectRow(idx[i], (multi?true:false));
						}else{
							if(!multi) this._initCacheSelect();
							this._cacheSelect.standard = '';
							this._cacheSelect.preselect= '';
							this._setCacheSelect(idx[i], this._selectedLastColIdx); //TODO
						}
					}
				}
			},	
			getSelection : function() {
				var array = [], tmpList = {}
				, that = this;
				jQuery.each(this._cacheSelect.selected,function(idx,el){
					if(that.getSelectedrange() == 'row'){
						var cy = +el.getAttribute('cy');
						array.indexOf(cy) == -1 && array.push(cy);
					}else{
						var cx = +el.getAttribute('cx')
						, cy = +el.parentNode.getAttribute('cy');
						tmpList[cy] || (tmpList[cy] = {});
						tmpList[cy][cx] = true;
					}
				});

				for(var row in tmpList){
					for(var col in tmpList[row]){
						array.push([row,col]);
					}
				}
				
				return array;
			},
			getSelectionCnt : function() {
				if(this.getSelectedrange() == 'row' && (this.getCheckbox() || this.getRownum())){
					return Math.ceil(this._cacheSelect.selected.length/2);
				}else{
					return this._cacheSelect.selected.length;
				}
			},
			setEmptymessage : function(msg){
				this._opt.emptymessage = msg;
			},
			getEmptymessage : function(){
				return this._opt.emptymessage;
			},
			setResizable : function(v){
				this._opt.resizable = v;
				return this._doResizable();
			},
			getResizable : function(){
				return xwing.Util.parseBoolean(this._opt.resizable);
			},
			_doResizable : function(){
				if (window.xwingIDE) 
					return;

				if (this.getResizable()) {
					var resizer = this.head.find('.xw-datagrid-column-resizer'),
						that = this;
					
					resizer.draggable({
						cursor: 'e-resize',
						axis: "x",
						helper : function(event){
							return jQuery("<div class='xw-datagrid-column-resizer-ghost'/>").
								height(that.getHeight()).
//								appendTo(jQuery(document.documentElement));
								appendTo(that._getJShell());
						},
						stop : function(event, ui){
							var node = jQuery(event.target.parentNode),
								column = that.getColumn(+node.attr('cx'));
								w = ui.position.left - node.position().left;

							if (w < 0) w = 0;
							column && column.setWidth(w);
							
							if( that._isEditMode() ){
								var $td = that.body_table._editpart.children('td[cx='+node.attr('cx')+']');
								var editCell = $td.children();
								var w = Xwing.getWidget(editCell.attr('id'));
								if( w ){
									$td.innerWidth() != w.getWidth() && w.setWidth($td.innerWidth());
								}
							}
							
							that._setRowHeight();
						}
					});
				} else {
					this.head.find('.xw-datagrid-column-resizer').
						draggable('destroy').
						hide();
				}
			},
			binddatasetListener : function(dsEvent) {
				if (dsEvent && dsEvent.id == this.getBinddataset()) {
					if (/^(reset|sort|add|remove|filter)$/.test(dsEvent.type)) {
						this._clearRowCache();
						this._doDataMode();
					} else if (dsEvent.type == 'update' && dsEvent.source && dsEvent.source.getParentWidget && dsEvent.source.getParentWidget() != this) {
						this._doDataMode();
					}
					
					if (dsEvent.type == "reset" || dsEvent.type == "sort" || dsEvent.type == "filter") {
						this._doValue();
					} else if (dsEvent.type == "cursor") {
						if( dsEvent.rowIdx >= 0 ) this.setSelection(dsEvent.rowIdx, true);
					} else if (dsEvent.type == "add") {
						this._insertRow(dsEvent.rowIdx);
					} else if (dsEvent.type == "update") {
						this._updateRow(dsEvent);
					} else if (dsEvent.type == "remove") {
						this._removeRow(dsEvent.rowIdx);
					}
					
					if (/^(reset|add|update|remove)$/.test(dsEvent.type)) {
						this._doSummaryValue();
					}
					
					if (/^(reset|add|remove)$/.test(dsEvent.type)) {
						this._fitScrollArea();
					}
				}		
			},
			_doValue : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				this.body_fix_tbody.empty();
				this.body_tbody.empty();
				this._initSelected();
				
				if( this.getGroupable() && this._ast['body'].length == 1 ){
					this._group._doValue(true);
				}else{
					if( this._isGroupSum && dsObj ) dsObj._groupsum(this._groupkey, this._grouptype, this._groupsuppress);
					this._clearRowCache();
					if (dsObj && dsObj.size() > 0) {
						// 2016.02.16 sorting 시 해당 스크롤로 이동해야하는데 안돼서 수정
						var scrollTop = this.body_area.scrollTop();
                        if( scrollTop != 0 ) {
                          var height = dsObj.getCursor() * this._ast['body']._height ;
                          if( scrollTop > height )  this._renderRows(false, true);
                          else this._renderRows(true, true);
                        }else this._renderRows(true, true);

						//this._renderRows(true, true);
					} else {
						this._renderEmptyMessage();
					}
				}
			},
			_renderEmptyMessage : function() {
				var emptyRow = jQuery("<tr class='empty'><td/></tr>");
				var td = jQuery('td', emptyRow);
				
				td.attr("colspan", this.head_colgroup.find('col').length).css("text-align", "center");
				td.html(xwing.Util.encodeHtml(this.getEmptymessage()));
				
				this.body_tbody.append(emptyRow);
				if(this.body_fix_tbody){
					var emptyFixRow = jQuery("<tr class='empty'><td/></tr>");
					jQuery('td', emptyFixRow).attr('colspan', this.head_fix_colgroup.find('col').length);
					this.body_fix_tbody.append(emptyFixRow);
				}
			},
			_setRowInfo : function(trs, inc) {
				var isRowNum = this.getRownum();
				
				trs.removeClass('even odd').each(function(i, e) {
					var tr = jQuery(this),
						cy = +tr.attr('cy') + inc || 0;
					
					tr.attr('cy', cy).addClass(cy % 2 ? 'even' : 'odd');
					isRowNum && tr.find('td.xw-datagrid-rownum>span').text(cy + 1);
				});
			},
			_insertRow : function(idx) {
				idx == 0 && jQuery('tr.empty', this.body).empty().remove();
				
				var rendered = this._getRenderedRange(),
					dsObj = Xwing.getDataset(this.getBinddataset()),
					$row = jQuery(this._makeRowHtml(dsObj, idx)),
					$fixedTr = $row.filter('tr.fixed'),
					$tr = $row.filter('tr:not(.fixed)');
				
				if (isNaN(rendered.bottom) || (idx == rendered.bottom + 1)) {
					this.body_fix_tbody.append($fixedTr);
					this.body_tbody.append($tr);	
				} else if (idx >= rendered.top && idx <= rendered.bottom) {
					this.body_fix_tbody.find('tr[cy=' + idx + ']:first').before($fixedTr);
					this.body_tbody.find('tr[cy=' + idx + ']:first').before($tr);
					
					this._setRowInfo($row.nextAll(), 1);
				}
				
				this._suppressRow(idx);
				this.setSelection(idx, true);
			},
			_updateRow : function(dsEvent) {
				var idx = dsEvent.rowIdx,
					column = dsEvent.column,
					pv = dsEvent.value;
				
				var dsObj = Xwing.getDataset(this.getBinddataset()),
					editRows = [ idx ],
					cells, cell, target;
				
				if (xwing.Util.is(column, 'string') && this.getEditsuppress()) {
					var cv = dsObj.getValue(idx, column);
					var l = dsObj.size();

					// 부모 cell들과 비교도 해야 한다. 
					var parent = true
						, parents = {
							cells : [],
							values : []
						};
					for ( var i = 0; i < this._ast.body.length; i++) {
						cells = this._ast.body[i].getCell();
						parent = true;
						for ( var j = 0; j < cells.length; j++) {
							cell = cells[j];
							if (cell.getSuppress() && cell.getBindcolumn() == column) {
								parent = false;
								for ( var k = idx - 1; k >= 0; k--) {
									// 부모 cell들과 비교해야 함 
									var pass = true;
									for( var m = 0; m < parents.cells.length ; m++){
										var parent_v = parents.values[m]
											, parent_pv = dsObj.getValue(k, parents.cells[m].getBindcolumn());
										if( parent_pv != parent_v){
											pass = false;
											break;
										}
									}
									if (dsObj.getValue(k, column) != pv || !pass)
										break;
									dsObj.setValue(k, column, cv, this);
									editRows.push(k);
								}
								for (k = idx + 1; k < l; k++) {
									// 부모 cell들과 비교해야 함 
									var pass = true;
									for( var m = 0; m < parents.cells.length ; m++){
										var parent_v = parents.values[m]
											, parent_pv = dsObj.getValue(k, parents.cells[m].getBindcolumn());
										if( parent_pv != parent_v){
											pass = false;
											break;
										}
									}
									if (dsObj.getValue(k, column) != pv || !pass)
										break;
									dsObj.setValue(k, column, cv, this);
									editRows.push(k);
								}
							}else if( cell.getSuppress()  && parent ){
								parents.cells.push(cell);
								parents.values.push(dsObj.getValue(idx,cell.getBindcolumn()));
							}
						}
					}
				}

				var td, value, style;				
				for ( var k = 0; k < editRows.length; k++) {
					idx = editRows[k];
					target = this.body.find('tr[cy=' + idx + ']:not(.editable)');
					if (target.length == 0) {
						delete this._rowCache[idx];
						continue;
					}

					for ( var i = 0; i < this._ast.body.length; i++) {
						cells = this._ast.body[i].getCell();
						
						for ( var j = 0; j < cells.length; j++) {
							cell = cells[j];
							
							td = target.find('td.' + cell.getId());
							value = undefined;
							style = {};
							
							if (cell.getExpr()) {
								style = cell._doExpr(idx);
								value = style.value;
							}
							
							if (value === undefined)
								value = cell._getValue(dsObj, idx);
							
							if (style.cell) td.attr('style', style.cell);
							else td.removeAttr('style');
							
							if (style.text) td.find('>span').attr('style', style.text);
							else td.find('>span').removeAttr('style');
							
							// display checkbox
							if( cell._opt.displaytype == 'checkbox'){
								var checked = (cell._opt.truevalue == value);
								if( checked )
									td.find('>span').addClass('xw-checkbox-checked').removeClass('xw-checkbox-unchecked');
								else
									td.find('>span').addClass('xw-checkbox-unchecked').removeClass('xw-checkbox-checked');
							}else
								td.find('>span').html(value);
						}
					}

					this._suppressRow(idx);
				}
				
				if (this.getTextwrap() == 'prewrap') {
					var that = this;
					setTimeout(function() {
						that._setRowHeight([ dsEvent.rowIdx ]);
					}, 0);
				}
			},
			_removeRow : function(idx) {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (dsObj.size() == 0) {
					this._doValue();
					return;
				}
				
				var target = this.body.find('tr[cy=' + idx + ']:not(.editable)');
				if (target.length == 0) return;
				
				var nextNodes = target.nextAll();
				target.empty().remove();
				
				this._setRowInfo(nextNodes, -1);
				this._suppressRow(idx == dsObj.size() ? --idx : idx);
			},
			_doSummaryValue : function() {
				var td, value, style, display;
				
				for ( var i = 0, cells; i < this._ast.summary.length; i++) {
					cells = this._ast.summary[i].getCell();
					
					for ( var j = 0, cell; j < cells.length; j++) {
						cell = cells[j];
						
						td = this.summ.find('td.' + cell.getId()); 
						display = td.css('display');
						value = undefined;
						style = {};
						
						if (cell.getExpr()) {
							style = cell._doExpr(0);
							value = style.value;
						}
						
						if (value === undefined && cell.getBindcolumn()) {
							var dsObj = Xwing.getDataset(this.getBinddataset());
							value = cell._getValue(dsObj, 0);
						}
						
						if (style.cell) td.attr('style', style.cell);
						else td.removeAttr('style');
						
						if (style.text) td.find('>span').attr('style', style.text);
						else td.find('>span').removeAttr('style');
						
						if( display == 'none') td.css('display','none');
						td.find('>span').html(value);
					}
				}
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				this._doBounds();
			},
			remove : function() {
				xwing.widget.Widget.prototype.remove.call(this);
				this.style.remove();
			},
			setTextwrap : function(wrap) {
				this._opt.textwrap = wrap;
				this._doTextwrap();
			},
			getTextwrap : function() {
				return this._opt.textwrap;
			},
			_doTextwrap : function() {
				this._setCssRules('.' + this._getCssId() + ' .xw-datagrid-font', this._getCssFont());
				
				if (this.getTextwrap() != 'prewrap') {
					this.body.find('tr').css('height', '');
				} else {
					this._setRowHeight();					
				}
			},
			_doTextpadding : function() {
				this._setCssRules('.' + this._getCssId() + ' .xw-datagrid-font', this._getCssFont());
			},
			setHeadcolor : function(v) {
				this._opt.headcolor = v;
				this._doHeadcolor();
			},
			_doHeadcolor : function() {
				this._setAreaColor(this.head_fix_div, this.getHeadcolor(), this.getHeadgradientcolor());
				this._setAreaColor(this.head_area, this.getHeadcolor(), this.getHeadgradientcolor());
			},		
			getHeadcolor : function() {
				return this._opt.headcolor;
			},
			setHeadgradientcolor : function(v) {
				this._opt.headgradientcolor = v;
				this._doHeadcolor();
			},
			getHeadgradientcolor : function() {
				return this._opt.headgradientcolor;
			},
			setSummarycolor : function(v) {
				this._opt.summarycolor = v;
				this._doSummarycolor();
			},
			_doSummarycolor : function() {
				this._setAreaColor(this.summ_fix_div, this.getSummarycolor(), this.getSummarygradientcolor());
				this._setAreaColor(this.summ_area, this.getSummarycolor(), this.getSummarygradientcolor());
			},
			getSummarycolor : function() {
				return this._opt.summarycolor;
			},
			setSummarygradientcolor : function(v) {
				this._opt.summarygradientcolor = v;
				this._doSummarycolor();
			},
			getSummarygradientcolor : function() {
				return this._opt.summarygradientcolor;
			},
			_setAreaColor : function(area, color, gradientcolor) {
				if (gradientcolor && color) {
					if (jQuery.browser.msie) {
						area.css('filter', "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + color + "', endColorstr='" + gradientcolor + "')");
					} else {
						if (jQuery.browser.mozilla) {
							area.css("background-image", "-moz-linear-gradient(top, " + color + ", " + gradientcolor + ")");
						} else if (jQuery.browser.webkit) {
							area.css("background-image", "-webkit-gradient(linear, left top, left bottom, from(" + color + "), to(" + gradientcolor + ") )");
						} else if (jQuery.browser.opera) {
							area.css("background-image", "-o-linear-gradient(top, " + color + ", " + gradientcolor + ")");
						}
					}
				} else {
					area.css('background', color || '');
				}
			},
			_setCssRules : function(selector, cssRules) {
				var rules = this._getCssRules(selector);
				rules && (rules.style['cssText'] = cssRules);
			},
			_getCssRules : function(selector) {
				if (!this._cssList[selector]) {
					if (!this.stylesheet) {
						var sheets = document.styleSheets;
						for ( var i = 0, l = sheets.length; i < l; i++) {
							if ((sheets[i].ownerNode || sheets[i].owningElement) == this.style[0]) {
								this.stylesheet = sheets[i];
								break;
							}
						}
					}
					
					if (!this.stylesheet) {
//						throw new Error('DataGrid._getCssRules(): Can not find styleSheet node');
						return;
					}
					
					var cssRules = this.stylesheet.cssRules || this.stylesheet.rules;
					for ( var i = 0, l = cssRules.length; i < l; i++) {
						if (cssRules[i].selectorText == selector) {
							this._cssList[selector] = cssRules[i];
							break;
						}
					}
				} 
				
				return this._cssList[selector]; 
			},
			_addCssRules : function(selector, cssRules){
				if (!this.stylesheet) {
					var sheets = document.styleSheets;
					for ( var i = 0, l = sheets.length; i < l; i++) {
						if ((sheets[i].ownerNode || sheets[i].owningElement) == this.style[0]) {
							this.stylesheet = sheets[i];
							break;
						}
					}
				}
				
				if (!this.stylesheet) {
					return;
				}
				this.stylesheet.addRule(selector,cssRules);
			},
			getColumn : function(v) {
				if (v === undefined)
					return this._ast.column;
				else if (typeof v == 'number')
					return this._ast.column[v];
				else {
					for ( var i = 0, l = this._ast.column.length; i < l; i++) {
						if (this._ast.column[i].hasColumn(v))
							return this._ast.column[i];
					}
				}
			},
			getRow : function(area, rowIdx) {
				if (!/^(head|body|summary)$/.test(area)) return;
				return this._ast[area][rowIdx];
			},
			getCell : function(area, rowIdx, colIdx) {
				if (!/^(head|body|summary)$/.test(area)) return;
				return this._ast[area][rowIdx] && this._ast[area][rowIdx].getCell(colIdx);
			},
			_getCellLocation : function(cell) {
				var coords = xwing.Util.getCellCoords(cell[0]);
				var table = cell.closest('table');
				
				if (this.head_table.is(table)) {
					coords.x += this.head_fix_colgroup.find('col:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)').length;
				} else {
					table.find('span.xw-datagrid-cell-checkbox').length && (coords.x -= 1);
					table.find('span.xw-datagrid-cell-rownum').length && (coords.x -= 1);
				}
				
				return {
					rows : coords.y,
					cols : coords.x
				};
			},
			_getCellNode : function(area, colidx) {
				var cell = null;
				
				for ( var i = 0, cells; i < this._ast[area].length; i++) {
					cells = this._ast[area][i].getCell();
					
					for ( var j = 0; j < cells.length; j++) {
						if (cells[j]._getColIndex() == colidx) {
							cell = cells[j];
							break;
						}
					}
				}
				
				return cell;
//				var matrix = [], result, 
//					row, cells, cell, 
//					colspan, rowspan, 
//					colIndex;
//
//				for ( var i = 0; i < this._ast[area].length; i++) {
//					matrix[i] = matrix[i] || [];
//					row = this._ast[area][i];
//					cells = row.getCell();
//
//					for ( var j = 0; j < cells.length; j++) {
//						cell = cells[j];
//						colspan = cell.getColspan() || 1;
//						rowspan = cell.getRowspan() || 1;
//
//						colIndex = null;
//						for ( var l = 0; l <= matrix[i].length && colIndex === null; l++) {
//							if (!matrix[i][l])
//								colIndex = l;
//						}
//
//						if (colIndex == columnIdx) {
//							result = cell;
//							break;
//						}
//
//						for ( var k = i; k < i + rowspan; k++) {
//							for ( var l = colIndex; l < colIndex + colspan; l++) {
//								matrix[k] = matrix[k] || [];
//								matrix[k][l] = 1;
//							}
//						}
//					}
//
//					if (result)
//						break;
//				}
//				
//				return result;
			},			
			setEvencolor : function(v) {
				this._opt.evencolor = v;
				this._doEvencolor();
			},
			getEvencolor : function() {
				return this._opt.evencolor;
			},
			_doEvencolor : function() {
				var cssRule = this._opt.evencolor ? 'background-color:' + this._opt.evencolor + ';' : '';
				this._setCssRules('.' + this._getCssId() + ' .even', cssRule);
			},
			setOddcolor : function(v) {
				this._opt.oddcolor = v;
				this._doOddcolor();
			},
			getOddcolor : function() {
				return this._opt.oddcolor;
			},
			_doOddcolor : function() {
				var cssRule = this._opt.oddcolor ? 'background-color:' + this._opt.oddcolor + ';' : '';
				this._setCssRules('.' + this._getCssId() + ' .odd', cssRule);
			},		
			setCheckbox : function(v) {
				this._opt.checkbox = v;
				this._doCheckbox();
			},
			getCheckbox : function() {
				return xwing.Util.parseBoolean(this._opt.checkbox);
			},
			_clearRowCache : function() {
				delete this._rowCache;
				this._rowCache = [];
			},
			_doCheckbox : function(add) {
				if (this.getCheckbox()) { 
					if (jQuery('.xw-datagrid-checkbox', this.head_fix_table).length == 0) {
						var rw = 22;
						
						var headCol = jQuery("<col class='xw-mod xw-datagrid-checkbox xw-datagrid-checkbox-head'/>").attr({'_xid' : this._opt._xid, 'width' : rw + 'px'});
						var headTh = jQuery("<th class='xw-mod xw-datagrid-checkbox'/>").attr('_xid', this._opt._xid);
						var headCheck = jQuery("<span class='xw-datagrid-cell-checkbox' value='unchecked'/>").appendTo(headTh);
						var that = this;
						
						if (!window.xwingIDE) {
							headCheck.mousedown(function(event) {
								that._activeCheckbox(headCheck);
							}).mouseup(function(event) {
								that._toggleCheckbox(headCheck, null, true);
								
								var bodyCheck = jQuery('span.xw-datagrid-cell-checkbox', that.body_fix_tbody);
								var v = headCheck.attr('value');
								
								that._toggleCheckbox(bodyCheck, v);
							});
						}
						
						if (this._ast.head.length > 1) 
							headTh.attr('rowspan', this._ast.head.length);
						
						var bodyCol = jQuery("<col class='xw-mod xw-datagrid-checkbox'/>").width(rw).attr('_xid', this._opt._xid);
						var summCol = jQuery("<col class='xw-mod xw-datagrid-checkbox xw-datagrid-checkbox-summary'/>").width(rw).attr('_xid', this._opt._xid);
						var summTh = jQuery("<td class='xw-mod xw-datagrid-checkbox xw-datagrid-checkbox-summary'/>").attr('_xid', this._opt._xid);
						
						if (this._ast.summary.length > 1) 
							summTh.attr('rowspan', this._ast.summary.length);
						
						if (this.getRownum()) {
							this.head_fix_colgroup.find('.xw-datagrid-rownum').after(headCol);
							this.head_fix_thead.find('.xw-datagrid-rownum').after(headTh);
							this.body_fix_colgroup.find('.xw-datagrid-rownum').after(bodyCol);
							this.summ_fix_colgroup.find('.xw-datagrid-rownum').after(summCol);
							this.summ_fix_tbody.find('.xw-datagrid-rownum').after(summTh);
						} else {
							this.head_fix_colgroup.prepend(headCol);
							jQuery('tr:first', this.head_fix_table).prepend(headTh);							
							this.body_fix_colgroup.prepend(bodyCol);							
							this.summ_fix_colgroup.prepend(summCol);
							jQuery('tr:first', this.summ_fix_table).prepend(summTh);
						}

						!add && this._doBodyCheckbox();						
					}
				} else {
					jQuery('.xw-datagrid-checkbox', this.getShell()).empty().remove();
				}
				
				this._clearRowCache();
				this._doAutofit();
			},
			_cloneFixedBodyRow : function() {
				var that = this;
				
				this.body_tbody.find('tr:not(.editable)').each(function(i, e) {
					that.body_fix_tbody.append(e.cloneNode(false));
				});
			},
			_doBodyCheckbox : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (dsObj && dsObj.size() > 0) {
					var rendered = this._getRenderedRange(),
						id = '_' + this.getId();
					
					if (this.body_fix_tbody.find('>tr').length == 0) 
						this._cloneFixedBodyRow();
					
					for ( var i = rendered.top, tr; i <= rendered.bottom; i++) {
						tr = jQuery('tr[cy="' + i + '"]:not(.editable):first', this.body_fix_tbody);
						this._appendCheckbox(tr);
					}
					
					for (var i = 0, l = dsObj.size(), row; i < l; i++) {
						row = dsObj.getRow(i);
						if (row && row[id]) delete row[id];
					}
				}
				
				var fixeditpart = this.body_table._editpart;
				if( this.body_table._editpart ){
					jQuery.each(this.body_table._editpart,function(idx,el){
						if( el.className.indexOf('fixed') > -1)	fixeditpart = jQuery(el);
					});
				}
				this._appendCheckbox(fixeditpart);
//				this._appendCheckbox(this.body_table._editpart);
			},
			_appendCheckbox : function(tr) {
				if (!tr || tr.find('td.xw-datagrid-checkbox').length) 
					return;
				
				var td = jQuery("<td class='xw-datagrid-checkbox'/>").attr({align : 'center', valign : 'center', rowspan : this._ast.body.length});
				var span = jQuery('<span class="xw-datagrid-cell-checkbox" value="unchecked"/>');
				
				if (this.getRownum()) {
					tr.find('td:first').after(td.append(span));
				} else {
					tr.prepend(td.append(span));
				}
			},
			setRownum : function(v) {
				this._opt.rownum = v;
				this._doRownum();
			},
			getRownum : function() {
				return xwing.Util.parseBoolean(this._opt.rownum);
			},
			_doRownum : function(base) {
				if (this.getRownum()) {
					if (jQuery('.xw-datagrid-rownum', this.head_fix_table).length == 0) {
						var rw = this._rownumWidth;
						
						var headCol = jQuery("<col class='xw-mod xw-datagrid-rownum xw-datagrid-rownum-head'/>").attr({'_xid' : this._opt._xid, 'width' : rw + 'px'});
						var headTh = jQuery("<th class='xw-mod xw-datagrid-rownum'/>").html("No").attr('_xid', this._opt._xid);
						if (this._ast.head.length > 1)
							headTh.attr('rowspan', this._ast.head.length);
						
						var bodyCol = jQuery("<col class='xw-mod xw-datagrid-rownum'/>").width(rw).attr('_xid', this._opt._xid);
						
						var summCol = jQuery("<col class='xw-mod xw-datagrid-rownum xw-datagrid-rownum-summary'/>").width(rw).attr('_xid', this._opt._xid);
						var summTh = jQuery("<td class='xw-mod xw-datagrid-rownum xw-datagrid-rownum-summary'/>").attr('_xid', this._opt._xid);
						if (this._ast.summary.length > 1)
							summTh.attr('rowspan', this._ast.summary.length);
						
						this.head_fix_colgroup.prepend(headCol);
						jQuery('tr:first', this.head_fix_table).prepend(headTh);
						this.body_fix_colgroup.prepend(bodyCol);
						
						!base && this._doBodyRownum();
						
						this.summ_fix_colgroup.prepend(summCol);
						jQuery('tr:first', this.summ_fix_table).prepend(summTh);
					}
				} else {
					jQuery('.xw-datagrid-rownum', this.getShell()).empty().remove();
				}
				
				this._clearRowCache();
				this._doAutofit();
			},
			_doBodyRownum : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (dsObj && dsObj.size() > 0 ) {
					var rendered = this._getRenderedRange();
					
					if (this.body_fix_tbody.find('>tr').length == 0) 
						this._cloneFixedBodyRow();
					
					for ( var i = rendered.top, tr; i <= rendered.bottom; i++) {
						tr = jQuery('tr[cy="' + i + '"]:not(.editable):first', this.body_fix_tbody);
						this._appendRownum(tr);
					}
				}
				
				this._appendRownum(this.body_table._editpart);
			},
			_appendRownum : function(tr) {
				if (!tr || tr.find('td.xw-datagrid-rownum').length) 
					return;
				
				var td = jQuery("<td class='xw-datagrid-rownum'/>").attr({align : 'center', valign : 'center', rowspan : this._ast.body.length});
				var span = jQuery('<span class="xw-datagrid-cell-rownum" >' + (+tr[0].getAttribute('cy') + 1) + '</span>');
				
				tr.prepend(td.append(span));			
			},
			_activeCheckbox : function(chk) {
				chk.css('background-position', '0 ' + (chk.attr('value') == 'checked' ? '-60px' : '-20px'));
			},
			_toggleCheckbox : function(chk, value, checkAll) {
				if (value) {
					chk.attr('value', value).css('background-position', '0 ' + (value == 'checked' ? '-40px' : '0'));
				} else if (chk.attr('value') == 'checked') {
					chk.attr('value', 'unchecked').css('background-position', '0 0');
				} else {
					chk.attr('value', 'checked').css('background-position', '0 -40px');
				}
				
				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (dsObj) {
					value = chk.attr('value');
					
					if (checkAll) {
						this._clearRowCache();
						
						for ( var i = 0; i < dsObj.size(); i++) {
							this._getRowState(dsObj, i).checked = value == 'checked';
						}
					} else {
						var that = this, cy;
						chk.each(function() {
							cy = jQuery(this).closest('tr').attr('cy');
							if (cy && +cy >= 0) {
								that._getRowState(dsObj, +cy).checked = value == 'checked';
							}
						});
					}
				}
			},
			getCheckedCnt : function() {
				return this.getCheckedIdx().length;
			},
			getCheckedIdx : function() {
				var dsObj = Xwing.getDataset(this.getBinddataset()),
					v = [];
				
				if (this.getCheckbox() && dsObj) {
					for ( var i = 0, l = dsObj.size(); i < l; i++) {
						this._getRowState(dsObj, i).checked && v.push(i); 
					}					
				}
				
				return v;
			},			
			setSortable : function(v) {
				this._opt.sortable = v;
				return this._doSortable();
			},
			getSortable : function() {
				return xwing.Util.parseBoolean(this._opt.sortable);
			},
			_doSortable : function() {
				if (window.xwingIDE) 
					return;
				
				var that = this;
				jQuery('.xw-datagrid-head-title', this.head).bind('click', function(event) {
					if( !that.getSortable() ) return;
					var node = jQuery(event.currentTarget);
					
					var sorter = node.find('.xw-datagrid-column-sort');
					if (sorter.size() == 0) return;
					
					var colidx = +node.parent().attr('cx');
					if (isNaN(colidx)) return;
				
					var cell = that._getCellNode('body', colidx);
					if (!cell) return;

					var bindcolumn = cell.getBindcolumn();
					Xwing.debug('dataset bindcolumn = ' + bindcolumn);

					var dataset = Xwing.getDataset(that.getBinddataset());
					var domaindataset = Xwing.getDataset(cell.getDomaindataset());
					var domaincodecolumn = cell.getDomaincodecolumn();
					var domaintextcolumn = cell.getDomaintextcolumn();

					if (dataset && bindcolumn && dataset.hasColumn(bindcolumn)) {
						var result = false;
						
						if (!sorter.hasClass('xw-datagrid-sort-icon-up')) {
							if (domaindataset && domaincodecolumn && domaintextcolumn)
								result = dataset.sort(bindcolumn, false, domaindataset, domaincodecolumn, domaintextcolumn);
							else
								result = dataset.sort(bindcolumn, false);

							if (result) {
								jQuery('.xw-datagrid-column-sort', that.head).hide();
								sorter.show().addClass('xw-datagrid-sort-icon-up').removeClass('xw-datagrid-sort-icon-down');
							}
						} else {
							if (domaindataset && domaincodecolumn && domaintextcolumn)
								result = dataset.sort(bindcolumn, true, domaindataset, domaincodecolumn, domaintextcolumn);
							else
								result = dataset.sort(bindcolumn, true);

							if (result) {
								jQuery('.xw-datagrid-column-sort', that.head).hide();
								sorter.show().addClass('xw-datagrid-sort-icon-down').removeClass('xw-datagrid-sort-icon-up');
							}
						}
					}						
				});
			},
			setMovecolumn : function(v) {
				this._opt.movecolumn = v;
				this._doMovecolumn();
			},
			getMovecolumn : function() {
				return xwing.Util.parseBoolean(this._opt.movecolumn);
			},
			_doMovecolumn : function() {
				if (window.xwingIDE) 
					return;

				if (this.getMovecolumn()) {
					if (this._ast.head.length > 1 || this._ast.body.length > 1 || this._ast.summary.length > 1)
						return;
					
					var reorder = {	};
					var that = this;
					
					jQuery('tr', this.head_table).sortable({
						handle : '.xw-datagrid-head-title',
						axis: 'x',
						items: 'th',
						helper: 'clone',
						containment: 'parent',
						placeholder: 'xw-datagrid-placeholder',
						start : function(event, ui) {
							var rowspan = jQuery(ui.item[0]).attr('rowspan') || 1;
							if (rowspan > 1) {
								ui.placeholder.attr('rowspan', rowspan);
							}
							
							var coords = that._getCellLocation(ui.item);
							
							reorder = {};
							reorder['from'] = coords; 
						},
						stop : function(event, ui) {
							var coords = that._getCellLocation(ui.item);
							if (reorder['from'].cols == coords.cols)
								return;

							var colspan = jQuery(ui.item[0]).attr('colspan') || 1;
							if (coords.cols < 0 || colspan > 1) {
								jQuery(this).sortable('cancel');
								return;
							}

							reorder['to'] = coords;
							that._reorderColumns(reorder);
							that._createColumnMenu();
						}
					}).disableSelection();
				}
			},
			_reorderColumns : function(reorder) {
				var areaType = ['body', 'summ'],
					from = reorder['from'].cols,
					to = reorder['to'].cols,
					eFrom = this.getColumn(from).getColumn(),
					eTo = this.getColumn(to).getColumn(),
					cells;
				
				// Change structure of AST Model
				for ( var area in this._ast) {
					if (area == 'column') cells = this._ast[area];
					else if (this._ast[area][0]) cells = this._ast[area][0].getCell();
					else cells = null;
					
					cells && cells.splice(to, 0, cells.splice(from, 1)[0]);		
				}
				
				// Change <col/> element position
				for ( var i = 0; i < eFrom.length; i++) {
					if (from > to) eFrom[i].insertBefore(eTo[i]);
					else eFrom[i].insertAfter(eTo[i]);
				}

				this._setCellColIndex(true);

				// if data mode, change editpart node's position to table body
				if (!this._isEditMode()) {
					var isModeChanged = true;
					this.body_tbody.append(this.body_table._editpart.filter('tr:not(.fixed)'));
				}
				
				if (this.head_fix_colgroup) {
					var offset = this.head_fix_colgroup.find('col:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)').length;				
					from -= offset;
					to -= offset;
				}
				
				// Change <td/> element position
				for ( var i = 0; i < areaType.length; i++) {
					this[areaType[i] + '_tbody'].children('tr').each(function(i, e) {
						var tds = jQuery(this).find('td:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)');
						var to_tds = tds.get(to);
						
						if (from > to) jQuery(tds.get(from)).insertBefore(to_tds);
						else jQuery(tds.get(from)).insertAfter(to_tds);
						
						tds = jQuery(this).find('td:not(.xw-datagrid-rownum,.xw-datagrid-checkbox)');
						tds.each(function(idx, el){
							el.setAttribute('cx',idx);
						});
					});
				}
				
				// restore editpart's position
				if (isModeChanged) {
					this.body_table._editpart.insertAfter(this.body_table);
				}
				
				this._clearRowCache();
			},
			setEditwhen : function(v) {
				this._opt.editwhen = v;
			},
			getEditwhen : function() {
				return this._opt.editwhen;
			},
			_suppressRow : function(nRow) {
				if (this._ast.body.length != 1) 
					return;

				var dsObj = Xwing.getDataset(this.getBinddataset());
				if (!dsObj) return;
				
				var cells = this._ast.body[0].getCell(), 
					pSeparate = false,
					nSeparate = false,
					id, bindColumn, 
					pv, cv, nv, pe, ce, ne,
					s = dsObj.size(),
					pr, cr, nr;
				
				for ( var i = 0, l = cells.length, cell; i < l; i++) {
					if (cells[i].getSuppress()) {
						!pr && (pr = this.body.find('tr[cy=' + (nRow - 1) + ']:not(.editable)'));
						!cr && (cr = this.body.find('tr[cy=' + nRow + ']:not(.editable)'));
						!nr && (nr = this.body.find('tr[cy=' + (nRow + 1) + ']:not(.editable)'));

						cell = cells[i];
						
						id = cell.getId();
						bindColumn = cell.getBindcolumn();
						
						pv = nRow - 1 < 0 ? null : dsObj.getValue(nRow - 1, bindColumn);
						cv = dsObj.getValue(nRow, bindColumn);
						nv = nRow + 1 < s ? dsObj.getValue(nRow + 1, bindColumn) : null;

						pe = pr.find('>td.' + id);
						ce = cr.find('>td.' + id); 
						ne = nr.find('>td.' + id);
				
						// GROUPING 
						if( this._isGroupSum() ){
							var grow = dsObj.getRow(nRow)
								, grouptext = ce.find('>span').text()
								, pretext = pe.find('>span').text();
							if( grow._GROUP != undefined && (pretext != grouptext)){
								cv = grouptext;
							}
						}
						if (pv != cv || pSeparate) {
							pe.css('border-bottom') && pe.css('border-bottom', '');
							ce.find('>span').css('display', '');
							pSeparate = true;
						} else {
							pe.css('border-bottom', '0px none');
							ce.find('>span').css('display', 'none');
						}
						
						if (cv != nv || nSeparate) {
							ce.css('border-bottom') && ce.css('border-bottom', '');
							ne.find('>span').css('display', '');
							nSeparate = true;
						} else {
							ce.css('border-bottom', '0px none');
							ne.find('>span').css('display', 'none');
						}
					}
				}
			},
			setEditsuppress : function(v) {
				this._opt.editsuppress;
			},
			getEditsuppress : function() {
				return xwing.Util.parseBoolean(this._opt.editsuppress);
			},
			_initSelected : function(){
				this._cacheSelect = {
						standard : ''
						, preselect : ''
						, selected : []
					};
				jQuery('.select', this.body).removeClass('select');
			},
			setSelectedrange : function(v){
				this._opt.selectedrange = v;
				// TODO init
				this._initSelected();
			},
			getSelectedrange : function(){
				return this._opt.selectedrange;
			},
			setVirtual : function(v){
				this._opt.virtual = v;
				this._doValue();
			},
			getVirtual : function(){
				return xwing.Util.parseBoolean(this._opt.virtual,false);
			},
			_createXmlString : function(){
				var columns = this._ast.column
					, c_arr = ['<datagrid-colgroup>'];
				for(var i=0, l=columns.length, col ; i<l ; i++){
					col = columns[i];
					c_arr.push('<datagrid-column '
							+ (col._opt.width ? 'width="'+col._opt.width+'" ' : ' ')
							+'></datagrid-column>');
				}
				c_arr.push('</datagrid-colgroup>');
				
				
				var parts = ['head','body','summary']
					, p_arr = [];
				for(var i=0, l=parts.length ; i<l ; i++){
					var part = this._ast[parts[i]];
					if(part.length == 0 ) continue;
					
					var arr = ['<datagrid-'+parts[i]+'>'];
					for(var j=0, rows = part.length; j<rows ; j++){
						arr.push('<datagrid-row>');
						var cells = part[j].getCell();
						for(var k=0; k<cells.length; k++){
							var cell = cells[k]
								, attr = ''
								, text = cell._opt.text;
							// text... 
							if(cell.getExpr()){
								var val = cell._doExpr(0);
								if(val && (val.value || val.value ==0)) text = val.value;
								else if(val || val == 0) text = val;
							}
							// attribute
							attr = (cell._opt.bindcolumn ? 'bindcolumn="'+cell._opt.bindcolumn+'" ' : '')
								+ (cell._opt.colspan != 1 ? 'colspan="'+cell._opt.colspan+'" ' : '')
								+ (cell._opt.rowspan ? 'rowspan="'+cell._opt.rowspan+'" ' : '')
								+ ((text || text == 0) ? 'text="'+text+'" ' : '')
								+ (cell._opt.fontcolor ? 'fontcolor="'+cell._opt.fontcolor+'" ' : '')
								+ (cell._opt.fontfamily ? 'fontfamily="'+cell._opt.fontfamily+'" ' : '')
								+ (cell._opt.fontsize ? 'fontsize="'+cell._opt.fontsize+'" ' : '')
								+ (cell._opt.fontweight ? 'fontweight="'+cell._opt.fontweight+'" ' : '')
								+ (cell._opt.exporttype ? 'exporttype="'+cell._opt.exporttype+'" ' : '')
								;
							arr.push('<datagrid-cell '+ attr 
								+ '></datagrid-cell>');
						}
						arr.push('</datagrid-row>');
					}
					arr.push('</datagrid-'+parts[i]+'>');
					p_arr.push(arr.join(''));
				}
				return '<datagrid>'+ c_arr.join('')+p_arr.join('') + '</datagrid>';
			},
			exportData : function(filename ,url, expr){
				// 임시..
				var column = '', row = '';
				this._datagridXml = this._createXmlString();
				if(this._opt.binddataset){
					column = Xwing.getDataset(this._opt.binddataset)._cols.join('`');
					row = this._join(Xwing.getDataset(this._opt.binddataset)._rows, expr);
				}
				var URL = url //"http://127.0.0.1:8080/TestInServer/export.jsp"
					, PARAMS = {
						datagrid : this._datagridXml,
						column : column,
						row : row
					};
				if(filename) PARAMS['filename'] = filename;
				this._postSend(URL, PARAMS);
			},
			_postSend : function(URL, PARAMS) {
				var temp;
				if(jQuery('form',this.grid).length != 0) tmp = jQuery('form',this.grid)[0];
				else temp = document.createElement("form");
				temp.action=URL;
				temp.method="POST";
				temp.acceptCharset = "utf-8";
				temp.style.display="none";
				for(var x in PARAMS) {
					var opt=document.createElement("textarea");
					opt.name=x;
					opt.value=PARAMS[x];
					temp.appendChild(opt);
				}
				this.grid.append(temp);
				temp.submit();
				delete jQuery(temp).empty().remove();
			},
			_removeContent : function(xmlStr){
				var start = xmlStr.indexOf('<!--')
					, end = xmlStr.indexOf('-->') + 3;
				var slice = xmlStr.slice(start, end);
				while(0 < slice.length){
					xmlStr = xmlStr.replace(slice,'');
					
					start = xmlStr.indexOf('<!--')
					, end = xmlStr.indexOf('-->') + 3;
					if(start == -1) break;
					else slice = xmlStr.slice(start, end);
				}
//				xmlStr = xmlStr.replace(/\n|(\/>)\s+(<)|(>)\s+(<)/g,'');
				xmlStr = xmlStr.replace(/\n|xwing:/g,'');
				return xmlStr;
			},
			_join : function(arr, expr){
				var str = ''
					, exprIdxs = []
					, groupIdxs = [];
				if(expr){
					var ds = Xwing.getDataset(this._opt.binddataset);
					for( var i = 0, l = this._ast.body.length, cells; i < l; i++){
						cells = this._ast.body[i].getCell();
						for( var j=0, k= cells.length,cell, colIdx; j < k ; j++){
							cell = cells[j];
							colIdx = ds.getColumnIndex(cell._opt.bindcolumn);
							if(cell.getExpr() && colIdx != -1) exprIdxs.push({ cell : cell, idx : colIdx});
							if( this._isGroupSum() && cell.getGroupsum() && cell.getGroupsumrendering() ){
								groupIdxs.push({ cell : cell, idx : colIdx});
							}
						}
					}
				}
				for(var i=0, row; i < arr.length ; i++){
					row = arr[i].concat([]);
					if(expr && this._isGroupSum() && arr[i]._GROUP != undefined ){
						// group sum을 위한 작업
						for( var j=0, cell, val, idx; j < groupIdxs.length ; j++){
							idx = groupIdxs[j].idx;
							val = groupIdxs[j].cell._doGroupsumrendering( arr[i][idx], arr[i]._GROUP);
							if( val.value || val.value == "") row[idx] = val.value;
						}
					}else{
						// group sum이 아닐 때 기존처럼 작업 
						for(var j=0 , idx, val, cell; j < exprIdxs.length ; j++){
							idx = exprIdxs[j].idx;
							cell = exprIdxs[j].cell;
							val = cell._doExpr(i);
							if(val && (val.value || val.value == 0)) row[idx] = val.value;
							else if(typeof val != 'object' || val == 0) row[idx] = val;
						}
					}
					
					if( i == (arr.length - 1 ) ) str += row.join('`');
					else str += row.join('`')+'`';
				}
				
				return str;
			},
			setRightselection : function(v){
				this._opt.rightselection = v;
			},
			getRightselection : function(){
				return xwing.Util.parseBoolean(this._opt.rightselection,false);
			},
			_reParse : function($node, remake){
				if(remake){
					for( var key in this._ast){
						var part = this._ast[key];
						if( key != 'column'){
							for( var i = 0 ; i < part.length ; i++){
								var cells = part[i].getCell();
								for( var j = 0 ; j < cells.length; j++){
									if( cells[j].getEdittype() != 'none' ){
										Xwing.removeWidget(this.getId()+'_'+cells[j].getId());
									}
									Xwing.removeWidget(cells[j].getId());
								}
							}
						}
					}
					this._ast = { column : [], head : [], body : [], summary : []};
				}
				
				var getAttrList = xwing.widget.Widget._parseXJson
				, areaType = ['head', 'body', 'summary']
				, that = this
				, column = false
				, structure = {
						column : [],
						head : [],
						body : [],
						summary : []
					}
				, fixcol = -1
				, cIdx = 0;
				
				 if(!remake) cIdx = this._ast.column.length;
				jQuery('datagrid-column',$node).each(function(i,e){
					column = true;
					var v = new xwing.widget.DataGridColumn(getAttrList(this), that);
					structure.column.push(v);
					if(v.getFixed()) fixcol = i + cIdx;
				});
				
				for ( var a = 0, l = areaType.length; a < l; a++) {
					var area = areaType[a];
					var height = 0;
					jQuery('datagrid-row', $node.filter('datagrid-' + area)).each(function(i, e) {
						var json = getAttrList(this);
						var id = that._getCssId() + '-' + area.charAt(0) + '-r';
						json['id'] = id + i;
						
						var v = new xwing.widget.DataGridRow(json, that);
						structure[area].push(v);
						height += v.getHeight();
						if(!remake && that._ast[area][i]) cIdx = that._ast[area][i]._cell.length;
						jQuery('datagrid-cell', this).each(function(j) {
							json = getAttrList(this);
							json['id'] = id + i + 'c' + (j+cIdx);
							v.addCell(new xwing.widget.DataGridCell(json, that, area));
						});
					});
					
					this._ast[area]._height = height;
					structure[area]._height = height;
				}
				
				return { _ast : structure, _fixIdx : fixcol};
			},
			_resetCssRules : function(){
				// reset 전에 기존 style 삭제 
				if (this.style[0].styleSheet) 
					this.style[0].styleSheet.cssText = '';
				 else if(  this.style[0].innerHTML )
					 this.style[0].innerHTML = '';
				 else if( this.style[0].innerText )
					 this.style[0].innerText = '';
				
				var id = this._getCssId()
				, rules = []
				, cssOdd = '', cssEven = ''
				, setCssRules = function(rules, area, key){
					for(var i=0,l = area.length ; i < l; i++){
						appendCssRule(rules, area[i], key + i);
						area[i].getCell && setCssRules(rules, area[i].getCell(), key + i + 'c');
					}
				}
				, appendCssRule = function(rules, obj, key){
					if( obj._getCss ){
						rules.push('.'+key+' { ');
						rules.push(obj._getCss());
						rules.push(' } ');
					}
					
					if(obj._getCssFont){
						rules.push('.' + key + ' .xw-datagrid-font { ');
						rules.push(obj._getCssFont());
						rules.push(' } ');
					}
				};
				
				appendCssRule(rules, this, id);
				cssEven = this._opt.evencolor ? 'background-color:' + this._opt.evencolor + ';' : '';
				cssOdd = this._opt.oddcolor ? 'background-color:' + this._opt.oddcolor + ';' : '';
				rules.push('.' + id + ' .even { ' + cssEven+' } ');
				rules.push('.' + id + ' .odd { ' + cssOdd + ' }');
				rules.push('.' + id + ' .select {'+(this.getSelectcolor() ? this.getSelectcolor() : 'background:#d8e9ee;')+' }');
				
				setCssRules(rules, this._ast.head, id + '-h-r');
				setCssRules(rules, this._ast.body, id + '-b-r');
				setCssRules(rules, this._ast.summary, id + '-s-r');
				
				if (this.style[0].styleSheet) 
					this.style[0].styleSheet.cssText = rules.join(' ');
				 else 
					 this.style[0].appendChild(document.createTextNode(rules.join(' ')));
				
				var sheets = document.styleSheets;
				for ( var i = 0, l = sheets.length; i < l; i++) {
					if ((sheets[i].ownerNode || sheets[i].owningElement) == this.style[0]) {
						this.stylesheet = sheets[i];
						break;
					}
				}
			},
			_resetBodyPart : function(){
				this._clearRowCache();
				this._doDataMode();
				this._doValue();
			},
			addContent : function(content, remake){
				content = content.replace(/xwing:/g,'');
				var _tmp  = this._reParse(jQuery(content), remake);
				if(remake){
					this._ast = _tmp._ast;
					this._removeDom(_tmp._ast);
					this._setCellColIndex();
					this._remakeDataGrid(_tmp._ast, _tmp._fixIdx);
				}else if(_tmp._fixIdx > -1){
					this._removeDom(_tmp._ast);
					this._appendAst(_tmp._ast);
					this._setCellColIndex();
					this._remakeDataGrid(this._ast, _tmp._fixIdx);
				}else{
					this._appendAst(_tmp._ast);
					this._setCellColIndex();
					this._appendDataGrid(_tmp._ast);
				}
				
				if(this._isEditMode()) this._doDataMode();
				if( this.body.find('tr.editable').length != 0 )
					this.body.find('tr.editable').remove();
				this._createEditPart();
				
				this._resetCssRules();
				this._doRownum(true);
				this._doCheckbox(true);
				this._doBounds();
				this._resetBodyPart();
				this._doResizable();
			},
			_removeDom : function(_ast){
				// header
				this.head.find('col, th').empty().remove();
				this.head_thead.find('tr:gt('+(_ast.head.length-1)+')').empty().remove();
				this.head_fix_thead.find('tr:gt('+(_ast.head.length-1)+')').empty().remove();
				// body
				this.body_colgroup.children('col').empty().remove();
				//this.body_fix_colgroup.children('col:not(.xw-datagrid-checkbox, .xw-datagrid-rownum)').empty().remove();
				this.body_fix_colgroup.children('col').empty().remove();
				this.body.find('tr.empty').empty().remove();
				// summary
				this.summ_tbody.find('tr:gt('+(_ast.summary.length-1)+')').empty().remove();
				this.summ_fix_tbody.find('tr:gt('+(_ast.summary.length-1)+')').empty().remove();
				this.summ.find('col, td').empty().remove();
			},
			_remakeDataGrid : function(_ast, _fixIdx){
				this._appendCol(_ast.column, _fixIdx);
				this._appendHead(_ast.head, _fixIdx);
				this._appendSumm(_ast.summary, _fixIdx);
			},
			_appendCol : function(column, _fixIdx){
				var tmp = "";
				for(var i=0; i < column.length ; i++){
					(_fixIdx >= i ) ? (tmp = "fix_") : (tmp = "");
					var ch = jQuery('<col/>').attr('width', column[i].getWidth() + 'px');	
					ch.appendTo(this["head_"+tmp+"colgroup"]);
					this._ast.column[i].addColumn(ch);
					this._ast.column[i].addColumn(ch.clone(false).appendTo(this["body_"+tmp+"colgroup"]));
					this._ast.column[i].addColumn(ch.clone(false).appendTo(this["summ_"+tmp+"colgroup"]));
				}
			},
			_appendHead : function(rows, _fixIdx){
				var tmp = ""
				, heightSum = 0, height , id = this._getCssId() + '-h-r';
				var thTemplate = [
				    '<th ', '', '', 'style="padding: 0;">',
				    '	<div class="xw-datagrid-column-resizer xw-datagrid-row"/>',
					'	<div class="xw-datagrid-column-menu xw-datagrid-row" style="display:none;"/>',
					'	<div class="xw-datagrid-head-title">',
					'		<div class="xw-datagrid-head-contents xw-datagrid-row" style="width:10000px;"></div>',
					'	</div>',
					'</th>'
				];
				for(var i=0; i < rows.length ; i++){
					height = rows[i].getHeight();
					
					var $tr
					, ths = rows[i].getCell();
					for (var j = 0, k = ths.length ; j < k; j++) {
						( _fixIdx >= ths[j]._getColIndex() ) ? (tmp = "fix_") : (tmp = "");
						$tr = this["head_"+tmp+"thead"].find('tr:eq('+i+')');
						if($tr.length == 0 ) $tr = jQuery('<tr class="' + id + i + '"/>').appendTo(this["head_"+tmp+"thead"]);
						thTemplate[1] = ths[j]._getSpan();
						thTemplate[2] = ' class="' + id + i + 'c' + j + '" cx="' + ths[j]._getColIndex() + '" ';
//						thStr += thTemplate.join('');  id + i + 'c' + ($tr.children().length+j) 
						var $td = jQuery(thTemplate.join(''));
						$td.find('.xw-datagrid-column-resizer').height(height+'px');
						$td.find('.xw-datagrid-head-contents').height(height*ths[j].getRowspan() - 4+'px');
						this._addCssRules('.' + id + i, 'height : '+height+'px;');
						$tr.append($td);
						
						var $contents = jQuery('.xw-datagrid-head-contents', $td);
						$contents.html('<span class="xw-datagrid-font">'+xwing.Util.encodeHtml(ths[j].getText())+'</span>');
						$contents.append(jQuery('<span class="xw-datagrid-column-sort"></span>'));
					}
					
					heightSum += height;

					if( this.getRownum()){
						var headTh = jQuery("<th class='xw-mod xw-datagrid-rownum'/>").html("No").attr('_xid', this._opt._xid);
						if (this._ast.head.length > 1)
							headTh.attr('rowspan', this._ast.head.length);
						if( jQuery('tr:first', this.head_fix_table).length == 0 ){
							for( var i = 0 ; i < this._ast.head.length ; i++ ){
								this.head_fix_thead.append(jQuery('<tr class="' +this._getCssId() + '-h-r' + i + '"/>'));	
							}
						}
					}
					
					$tr.children('th').each(function(i, e) {
						var $node = jQuery(this);
						if (xwing.Util.parseInt($node.attr('colspan')) > 1) {
							$node.children('.xw-datagrid-column-resizer').remove();
						}					
					});
					
				}
				this.head.height(heightSum);	
			},
			_appendSumm : function(rows, _fixIdx){
				if(rows.length == 0 ) return;
				var tmp = ""
				, heightSum = 0, height , id = this._getCssId() + '-s-r';
				for(var i=0; i < rows.length ; i++){
					height = rows[i].getHeight();
					
					var $tr , thStr 
					, ths = rows[i].getCell();
					var style = {}, value;
					
					for (var j = 0, k = ths.length ; j < k; j++) {
						if(  _fixIdx >= ths[j]._getColIndex() )  tmp = "fix_"; else tmp = "";
						if( ths[j].getExpr() ){
							style = ths[j]._doExpr(-1);
							value = style.value;
						}else{
							value = ths[j].getText();
						}
						
						thStr = '<td ' + ths[j]._getSpan() + ' class="' + id + i + 'c' + j + '" cx="' + ths[j]._getColIndex() + '" '+((style && style.cell) ? 'style="'+style.cell+'"' : '')+' >'
						 + '<span class="xw-datagrid-font" '+((style && style.text) ? 'style="'+style.text+'"' : '')+'>'
						 + xwing.Util.encodeHtml(value)
						 + '</span></td>';
						$tr = this["summ_"+tmp+"tbody"].find('tr:eq('+i+')');
						if($tr.length == 0 ) $tr = jQuery('<tr class="' + id + i + '"/>').appendTo(this["summ_"+tmp+"tbody"]);
						this._addCssRules('.' + id + i, 'height : '+height+'px;');
						$tr.append(jQuery(thStr));
					}
					
					heightSum += height;
				}
				this.summ.height(heightSum);	
			},
			_appendAst : function(_ast){
				var tmp = ['head','body','summary'];
				for(var i=0; i < _ast.column.length ; i++){
					this._ast.column.push(_ast.column[i]);
				}
				for(var i=0; i < tmp.length ; i++){
					var part = _ast[tmp[i]];
					for(var j=0, l= part.length; j < l ; j++){
						var a = -1;
						if(part[j]._cell) a = part[j]._cell.length;
						if(this._ast[tmp[i]][j] ) this._ast[tmp[i]][j]._cell = this._ast[tmp[i]][j]._cell.concat(part[j]._cell);
						else this._ast[tmp[i]][j] = part[j];
					}
				}
			},
			_appendDataGrid : function(_ast){
				this._appendCol(_ast.column);
				this._appendHead(_ast.head);
				this._appendSumm(_ast.summary);
			},
			removeRow : function(part, idxs){
				var arr = [];
				if(arguments.length == 0 || arguments.length == 1) return;
				else if(typeof idxs == 'object' || typeof idxs == 'array')	arr = idxs;
				else if(!isNaN(idxs)) arr = [idxs];
				arr.sort();
				
				var _part = this._ast[part]
				, heightSum = _part._height || 0;
				for(var x = 0 ; x < _part.length ; x++){
					var _cells = _part[x].getCell();
					for(var y = 0 ; y < _cells.length ; y++){
						var rowspan = _cells[y].getRowspan();
						for(var i=0; i<arr.length ; i++){
							var idx = parseInt(arr[i]);
							if(x < idx && idx < (x + rowspan)){
								_cells[y].setRowspan(rowspan - 1);
								this._setRowspan( _cells[y],rowspan - 1);
							}
						}
					}
				}
				for(var i=(arr.length-1); i >=0 ; i-- ){
					if ( this._ast[part][arr[i]] ){
						heightSum -= this._ast[part][arr[i]].getHeight();
						this._removeRowTr(part,this._ast[part][arr[i]]);
						this._ast[part].splice(i,1);
						
					}
				}
				
				if(part == 'body'){
					this._resetBodyPart();
				}else {
					_part._height = heightSum;
					this[part.substr(0,4)].height(heightSum);
					this._doBounds();
				}
			},
			_setRowspan : function(cell, rowspan){
				if(cell._area != 'body'){
					var $td = this[cell._area.substr(0,4)].find('.'+cell.getId());
					if(rowspan == 1 ) $td.attr('rowspan','');
					else $td.attr('rowspan',rowspan);
				}
			},
			_setColspan : function(cell, colspan){
				if(cell._area != 'body'){
					var $td = this[cell._area.substr(0,4)].find('.'+cell.getId());
					if(colspan == 1 ) $td.attr('colspan','');
					else $td.attr('colspan',colspan);
				}
			},
			_removeCellTd : function(part, cell){
				if(part!= 'body'){
					var $td = this[part.substr(0,4)].find('.'+cell.getId());
					$td.empty().remove();
				}
			},
			_removeRowTr : function(part, row){
				if(part!= 'body'){
					var $tr = this[part.substr(0,4)].find('.'+row.getId());
					$tr.empty().remove();
				}
			},
			_removeCol : function(i){
				this.head.find('col:not(.xw-datagrid-checkbox, .xw-datagrid-rownum):eq('+i+')').empty().remove();
				this.body.find('col:not(.xw-datagrid-checkbox, .xw-datagrid-rownum):eq('+i+')').empty().remove();
				this.summ.find('col:not(.xw-datagrid-checkbox, .xw-datagrid-rownum):eq('+i+')').empty().remove();
			},
			removeColumn : function(idxs){
				var arr = []
				, _removeArr = []
				, fix = this._getFixedColIndex();
				if(arguments.length == 0) return;
				else if(typeof idxs == 'object' || typeof idxs == 'array')	arr = idxs;
				else if(!isNaN(idxs)) arr = [idxs];
				arr.sort();
				// remove
				for(var key in this._ast){
					var _part = this._ast[key];
					if(key == 'column'){
						for(var i=(arr.length-1); i >=0 ; i-- ){
							if(_part[parseInt(arr[i])]){
								this._removeCol(arr[i]);
								_part.splice(parseInt(arr[i]),1);
							}
						}
					}else {
						var matrix = [], colIndex = null;
						for(var x=0; x < _part.length ; x++){
							matrix[x] = matrix[x] || [];
							var _cells = _part[x].getCell();
							for(var y=0; y < _cells.length ; y++){
								var _cell = _cells[y];
								var colspan = _cell.getColspan()
									, rowspan = _cell.getRowspan();
								colIndex = null;
								for ( var l = 0; l <= matrix[x].length && colIndex === null; l++) {
									if (!matrix[x][l])
										colIndex = l;
								}
								
								var fix_colspan = colspan;
								for(var k=x; k < x + rowspan; k++){
									for(var l=colIndex; l < colIndex + fix_colspan ; l++){
										matrix[k] = matrix[k] || [];
										matrix[k][l] = 1;
										
										if( k == x && (arr.indexOf(l) > -1 || arr.indexOf(String(l)) > -1)){
											if(colspan > 1){
												colspan--;
												this._setColspan(_cell, colspan);
												_cell.setColspan(colspan);
											}else if(colspan <= 1){
												this._removeCellTd(key, _cell);
												_removeArr.push(_cell);
											}
										}
									}
								}
							}
						}
					}
				}
				for(var i = (_removeArr.length -1); i >=0 ; i--){
					var cell = _removeArr[i]
					, _part = this._ast[cell._area];
					for(var j = 0 ; j < _part.length ; j++){
						var removeidx = _part[j].getCell().indexOf(_removeArr[i]);
						if(removeidx > -1 ){
							_part[j].getCell().splice(removeidx,1);
							break;
						}
					}
				}
				
				this._setCellColIndex(true);
				if( (this._getFixedColIndex() == -1 && fix != -1) || (this._getFixedColIndex() != -1 && fix == -1)){
					this._removeAll(this._ast);
					this._appendAst(this._ast);
					this._remakeDataGrid(this._ast,this._getFixedColIndex());
				}
				this._doBounds();
				this._resetBodyPart();
			},
			setChecking : function(idx){
				var ds = Xwing.getDataset(this._opt.binddataset);
				if(arguments.length == 1 && (!ds || ds.size() <= idx)) return;
				
				if( arguments.length == 1){
					var chk = jQuery('tr[cy=' + idx +']', this.body_fix_tbody).find('span.xw-datagrid-cell-checkbox');
					if(chk.length != 0)
						this._toggleCheckbox(chk);
					else if( this._rowCache[idx]){
						this._toggleCheckbox(this._rowCache[idx].find('span.xw-datagrid-cell-checkbox'));
					}else{
						var chek = this._getRowState(ds , idx).checked;
						this._getRowState(ds , idx).checked = (chek ? false : true);
					}
				}else{
					this.head_fix_thead.find('.xw-datagrid-cell-checkbox').trigger('mouseup');
				}
			},
			setDragIcon : function(v){
				this._opt.dragicon = v;
//				this._doDragIcon();
			},
			getDragIcon : function(){
				return this._opt.dragicon
			},
//			_doDragIcon : function(){
//				var that = this;
//				setTimeout(function(){
//					that._getJShell().draggable('option','helper',function(){return jQuery('<div/>');});
//				},10);
//			},
			setDataDraggable : function(v){
				this._opt.datadraggable = v;
				this._doDataDragDrop();
			},
			getDataDraggable : function(){
				return xwing.Util.parseBoolean(this._opt.datadraggable,false);
			},
			setDataDroppable : function(v){
				this._opt.datadroppale = v;
				this._doDataDragDrop();
			},
			getDataDroppable : function(){
				return xwing.Util.parseBoolean(this._opt.datadroppale,false);
			},
			_doDataDragDrop : function(){
				if( this.getDataDraggable() || this.getDataDroppable() ){
					if( !this._dragdrop ) this._dragdrop = new item(this);
					else
						this._dragdrop.setProperty();
				}else if( this._dragdrop ){
					delete this._dragdrop ;
				}
			},
			_getDragIndex : function(event){
				// Drag ???대떦 ?뱀젙 INDEX瑜?媛?몄삤???⑥닔 
				var col = $(event.srcElement).closest('td', this.getShell());
				if( col.length != 0 ) col =  xwing.Util.parseInt(col.attr('cx'),1);
				else col = -1;
				
				var row = $(event.srcElement).closest('tr', this.getShell());
				if( row.length != 0 ) row =  xwing.Util.parseInt(row.attr('cy'),1);
				else row = -1;
				
				return [row, col];
			},
			_mousemoveShell : function(event){
				// ??widget???곸뿭?먯꽌 drag媛 ?쇱뼱??寃쎌슦??. 
				if( this.getDataDroppable() ){
					
					var helper = $('.xw-draggable-helper'),
						ds = Xwing.getDataset(this._opt.binddataset)
						, dragWG = this._dragdrop;
					
					if( helper && helper.length != 0 && ds != null ){
						
						var boundary = dragWG._createBoundary(this.body);
						
						var viewPort = this._getVisibleRange(); 
						var boundaryWhich = viewPort.top + Math.round((event.clientY- (this.getTop() + this._ast.head._height))/this._ast.body._height)
							, targetWhich = viewPort.top + Math.floor((event.clientY- (this.getTop() + this._ast.head._height) )/this._ast.body._height); // 72 = top + headHeight
						
						var tr = this.body_area.find("tr[cy="+boundaryWhich+']')
							, target = this.body_area.find("tr[cy="+targetWhich+']');
						
						// body ?곸뿭???덉쓣 ??
						if( tr[0]){
							var offsetTop = tr[0].offsetTop + this._ast.head._height -  this.body_area.scrollTop();
							boundary.css('top',offsetTop+'px');
						}else{
							 tr = this.body_area.find("tr:last");
							 var offsetTop = tr[0].offsetTop + tr[0].offsetHeight + this._ast.head._height -  this.body_area.scrollTop();
								boundary.css('top',offsetTop+'px');
						}
						
						var col = $(event.srcElement).closest('td', this.getShell());
						if( col.length != 0 ) col = xwing.Util.parseInt(col.attr('cx'),-1);
						else col = -1;
						
						item.dropIdx = [boundaryWhich, col]; // [row, col]
						if( ds.size() ==0 ) item.dropIdx = [-1, -1]; // [row, col]
						else if( ds.size() < boundaryWhich)  item.dropIdx = [ds.size(), col]; // [row, col]
						
						
						$('.xw-draggable-over').removeClass("xw-draggable-over");
						target.addClass('xw-draggable-over');
					}
					
				}
			},
			_mouseupShell: function(event){
				if( item.draggable ){
					item.draggable._fire('dragend',{
						source : item.draggable,
						event : event,
						position : {
							'top' : event.clientY,//(event.clientY+15)+'px',
							'left' : event.clientX//(event.clientX+2)+'px'
						},
						index : item.dragIdx.concat([])
					});
					this._fire('dropping',{
						source : this,
						dragObj : item.draggable,
						event : event, 
						position : {
							'top' : event.clientY,//(event.clientY+15)+'px',
							'left' : event.clientX//(event.clientX+2)+'px'
						},
						dragIndex : item.dragIdx && item.dragIdx.concat([]),
						dropIndex : item.dropIdx && item.dropIdx.concat([])
					});
					
					delete item.draggable;
				}
			},
			setGroupable : function(v){
				this._opt.groupable = v;
			},
			getGroupable : function(){
				return xwing.Util.parseBoolean(this._opt.groupable,false);
			},
			setGroupheaderexpr : function(v){
				this._opt.groupheaderexpr = v;
			},
			getGroupheaderexpr : function(){
				return this._opt.groupheaderexpr;
			},
			_doVisible : function(){
				if (window.xwingIDE)
					return;

				if (this.getVisible()) {
					this.show();
					
					this._doAutofit();
				} else {
					this.hide();
				}
			},
			setVisiblecontextmenu : function(v){
				this._opt.visiblecontextmenu = v;
			},
			getVisiblecontextmenu : function(){
				return xwing.Util.parseBoolean(this._opt.visiblecontextmenu,false);
			},
			setTopsummary : function(v){
				this._opt.topsummary = v;
			},
			getTopsummary : function(){
				return xwing.Util.parseBoolean(this._opt.topsummary, false);
			},
			keepScroll : function(v){
				this._keepscroll = false;
				if( v == true || v == 'true'){
					var ds = Xwing.getDataset(this._opt.binddataset);
					if( ds.size() == 0 ) return;
					this._keepscroll = {
							top : this.body_area.scrollTop()
							, left : this.body_area.scrollLeft()
							, cursor : ds.getCursor()
					};
				}
			},
			_isGroupSum : function(){
				return xwing.Util.parseBoolean(this._isgroupsum,false);
			},
			DataMode : function(){
				this._doDataMode();
			},
			expandAll : function() {
				if( this._group){
					var divs = this.body_table.find('.xw-datagrid-group-expand');
					divs.addClass('xw-datagrid-group-collapse').removeClass('xw-datagrid-group-expand');
					var $chd = this.body_table.find('table.xw-datagrid-group-disabled');
					$chd.removeClass('xw-datagrid-group-disabled');
				}
			},
			collapseAll : function() {
				if( this._group){
					if( this._group){
						var divs = this.body_table.find('.xw-datagrid-group-collapse');
						divs.addClass('xw-datagrid-group-expand').removeClass('xw-datagrid-group-collapse');
						var $chd = this.body_table.find('table.xw-datagrid-group-disabled');
						$chd.addClass('xw-datagrid-group-disabled');
					}
				}
			},
			_isMouseup : function(){
				var type = 'mouseup';
			
				for ( var i = 0, l = this._listeners.length, temp; i < l; i++) {
					if (type == this._listeners[i].type) {
						temp = this._listeners[i];
						if ( temp.func ) {
							return true;
						}
					}
				}
				return false;
			},
			setSelectcolor : function(v){
				this._opt.selectcolor = v;
				this._doSelectcolor();
			},
			getSelectcolor : function (){
				return this._opt.selectcolor;
			},
			_doSelectcolor : function(){
				this._setCssRules('.' + this.getId()+' .select', 'background:'+(this.getSelectcolor() ? this.getSelectcolor() : '#d8e9ee')+";");
			}
		}
	}
});
