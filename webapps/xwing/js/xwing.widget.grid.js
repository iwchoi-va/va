Class.define({
	Grid : {
		alias : "grid",
		extend : xwing.widget.DataBindable,
		namespace : "xwing.widget",
		Grid : function(json){
			this._init(json);
			/*debug Xwing.debug("Widget(Grid) created : "); */
		},
		statics : {
			create : function(json){
				return new xwing.widget.Grid(json);
			}
		},		
		prototypes :{
			_handleClickEvent : function(event){
				var tdNode = jQuery(event.target).closest('td',event.currentTarget)
				, cx = tdNode.attr("coordx");
				var opt =  {
						type: event.type,
						source : this,
						event : event										
						};
				if(cx != undefined ) opt.column = this.columnList[cx];
				if(this.cellFlag) this._fire(event.type,opt);
				this.cellFlag = false;
			},
			_createPart : function(){
				this._initValue();
				this.grid = jQuery("<div class='xw-grid xw-mod-border xw-mod-focus'/>");
				
				this._createHeaderPart();
				this._createBodyPart();
				this._createSummaryPart();
				this._createPagePart();
				this._getJShell().append(this.grid);
				
				this._doHeadrownum();
				this._doHeadcheckbox();
				
				this._bind(this.body, 'scroll', function(event){
					this.header.scrollLeft(this.body.scrollLeft());
					this.summary.scrollLeft(this.body.scrollLeft());					
				});
				this._getJShell().unbind('click').unbind('dblclick');
				this._bind(this._getJShell(), 'click', this._handleClickEvent);
				this._bind(this._getJShell(), 'dblclick', this._handleClickEvent);
				
				var thisObj = this;
				var scrollHandler = function() {
					thisObj.header.scrollLeft(-this.x);
					thisObj.summary.scrollLeft(-this.x);					
				};
				this._bindScrollPane(this.body_table, {
					onBeforeScrollStart : function(e) {
						var jTd = thisObj._findTd(jQuery(e.target));
						if (!jTd || (jTd.attr('mode') != 'edit')) {
							e.preventDefault();
						}
					},
					onScrollMove : scrollHandler,
					onScrollEnd : scrollHandler,
					zoom: false
				});
			},
			_initValue : function(){
				this.columnList = [];
				this.selectedRowIdx = "";
				this.setectedColIdx = "";
				this.cellFlag = false;
				this.multiFlag = false;
				this.changeChk = false;
				this.move = {};
				this.move.changing = undefined;
				this.sortFlag = true;
				this.editYN = false;
				this.columnFixed = [];
			},
			_createHeaderPart : function(){
				this.header = jQuery("<div class='xw-grid-area-header'/>");
				this.header_resize = jQuery("<div class='xw-grid-header-resize'/>");
				this.header_colgroup = jQuery("<colgroup/>"); 
				this.header_table = jQuery("<table cellspacing='0' cellpadding='0' border='0'/>");
				this.header_thead = jQuery("<thead/>");
				this.header_tr = jQuery("<tr/>").height(this._opt.headheight);
				this.grid.append(
					this.header.append(
							this.header_resize.append(
								this.header_table.append(
									this.header_colgroup
								).append(
									this.header_thead.append(
										this.header_tr
									)
								)
							)
				    )
				);
				this._doHeadcolor();
			},
			_createBodyPart : function(){
				this.body = jQuery("<div class='xw-grid-area-body xw-mod-background' tabindex='-1' />");
				this.body_table =jQuery("<table cellspacing='0' cellpadding='0' border='0' />");
				this.body_colgroup = jQuery("<colgroup/>");
				this.body_tbody =  jQuery("<tbody/>");
				this.grid.append(
					this.body.append(
						this.body_table.append(
							this.body_colgroup
						).append(
							this.body_tbody
						)
					)
				);
				this._keydownBind();
				this._createBasicActionEvent();
			},
			_createSummaryPart : function(){
				this.summary = jQuery("<div class='xw-grid-area-summary'/>");
				this.summary_resize = jQuery("<div class='xw-grid-summary-resize'/>").css('height','100%');
				this.summary_colgroup = jQuery("<colgroup/>");
				this.summary_table = jQuery("<table/>");
				this.summary_tbody = jQuery("<tbody/>");
				this.summary_tr = jQuery("<tr/>").height(this.getRowheight());
				this.summary.append(
					this.summary_resize.append(
						this.summary_table.append(
							this.summary_colgroup
						).append(
							this.summary_tbody.append(
								this.summary_tr
							)
						)
					)
				);
				this.grid.append(this.summary);
			},
			_createPagePart : function(){
				this.footer = jQuery("<div class='xw-grid-area-footer'/>");
				this.grid.append(this.footer);
				
				this._doPage();
			},
			_keydownBind : function(){
				var thisObj = this;
				
				this.body.bind('keydown', function(e){
					if(thisObj.editYN) return;
					e.preventDefault();
					e.stopPropagation();
					
					var ds = Xwing.getDataset(thisObj.getBinddataset());
					var old_cursor = xwing.Util.parseInt(ds.getCursor());
					var div_height = xwing.Util.parseInt(this.clientHeight,0);
					
					switch(e.keyCode){
					case 40: //down
							var new_cursor = old_cursor + 1;
							if(new_cursor != ds.size() ){
								thisObj.selectedRowIdx = new_cursor;
								ds.setCursor(new_cursor);
								var cur_row = jQuery('tr[coordy='+new_cursor+']',this);
								var offsetTop = parseInt(cur_row[0].offsetTop); 
								var scrollTop = jQuery(this).scrollTop();
								var height = offsetTop+parseInt(cur_row.height());
								if(scrollTop > height){
									jQuery(this).scrollTop(offsetTop);
								}else if((div_height+scrollTop) < height){
									var top = height - div_height;
									jQuery(this).scrollTop(top);
								}
								delete cur_row, offsetTop, scrollTop, height;
							}
						break;
					case 38: //up
						var new_cursor = old_cursor -1;
						if(new_cursor >= 0 ){
							thisObj.selectedRowIdx = new_cursor;
							ds.setCursor(new_cursor);
							var cur_row = jQuery('tr[coordy='+new_cursor+"]",this);
							var offsetTop = parseInt(cur_row[0].offsetTop); 
							
							var scrollTop = jQuery(this).scrollTop();
							var height = offsetTop;
							if(scrollTop >= height){
								jQuery(this).scrollTop(height); 
							}else if((scrollTop+div_height) < (height+parseInt(cur_row.height()))){
								var top =height+parseInt(cur_row.height()) - div_height;
								jQuery(this).scrollTop(top);
							}
							delete cur_row, offsetTop, scrollTop, height;
						}
						break;
					}
					delete ds,old_cursor, div_height;
					if(e.keyCode == 32 && thisObj.getCheckbox()){
						// checkbox 클릭 
						var chk = jQuery('.xw-grid-cell-checkbox[coordy='+thisObj.selectedRowIdx+']',this.body_tbody);
						thisObj._mouseupCheckbox(chk);
					}
				});
			},
			appendChild : function(child){
				/*debug Xwing.debug("grid append " + child); */
				if(!this.isAppendable(child)) return;				
				this.addColumn(child);
				return child;
			},
			insertChild : function(child, index){
				/*debug Xwing.debug("grid column insert " + child + ', '+index); */
				if(index < 0 || index >= this.columnList.length){
					return this.appendChild(child);
				}else{
					return this.insertColumn(child, index);
				}
			},
			addColumn : function(columnObj){
				columnObj._setParentWidget(this);
				this.columnList[this.columnList.length] = columnObj;
				this.columnFixed[this.columnFixed.length] = (columnObj.getFixed() ? 'fixed' : (columnObj.getWidth()/this.getWidth()*100).toFixed(2)); 
				this.header_colgroup.append(columnObj._getHeadCol());
				this.header_tr.append(columnObj._getTh());
				this.body_colgroup.append(columnObj._getBodyCol());
				columnObj._appended();
				this._doValue();
				if(this.childCompleted){
					this._doHeadfont();
					this._doHeadtextwrap();
					this._doAutofit();
				}
			},
			insertColumn : function(columnObj, index){
				columnObj._setParentWidget(this);
				this.columnList.splice(index, 0, columnObj);
				this.columnFixed.splice(index, 0, (columnObj.getFixed() ? 'fixed' : (columnObj.getWidth()/this.getWidth()*100).toFixed(2)));
				jQuery(this.header_colgroup.children('col.xw-mod')[index]).before(columnObj._getHeadCol());
				jQuery(this.header_tr.children('th.xw-mod')[index]).before(columnObj._getTh());
				jQuery(this.body_colgroup.children('col.xw-mod')[index]).before(columnObj._getBodyCol());
				
				columnObj._appended();
				this._doValue();
				if(this.childCompleted){
					this._doHeadfont();
					this._doHeadtextwrap();
					this._doAutofit();
				}
			},
			removeColumn : function(columnObj){
				var index = null;
				for(var i=0; i < this.columnList.length; i++){
					if(this.columnList[i] === columnObj){
						index = i;
						break;
					}
				}
				/*debug Xwing.debug('grid removecolumn invoked.' + index); */
				if(index != null){
					this.columnList.splice(index,1);
					this.columnFixed.splice(index,1);
					jQuery('td[coordx='+index+']', this.body_tbody).empty().remove();
				}
				columnObj._getHeadCol().empty().remove();
				columnObj._getTh().empty().remove();
				columnObj._getBodyCol().empty().remove();
//				columnObj._removeTds();				
				
				Xwing.removeWidget(columnObj.getId());
				if(this.childCompleted)	this._doAutofit();
				if(columnObj._xid && xwingIDE && xwingIDE.addWidget){
					xwingIDE.removeWidget(columnObj._opt._xid, this);
				}
				delete index;
			},
			_appendCompleted : function(){
				this._branding();
				this.childCompleted = true;
				this._doValue();
				this._doHeadfont();
				this._doHeadtextwrap();
				this._resetColRate();
				this._doAutofit();
				this._createEditTag();
				this._doMovecolumn();
			},
			_resetWidth : function(){
				var w = 0;
				for(var i=0; i < this.columnList.length ; i++){
					w += this.columnList[i].getWidth();
				}
				if( this.getRownum()) w = w + 20;
				if(this.getCheckbox()) w = w + 22;
				w = w || this.getWidth();
				this.header_table.width(w);
				this.header_resize.width(w+20);
				this.body_table.width(w);
				this.summary_table.width(w);
				this.summary_resize.width(w+20);
				delete w;
			},
			_resetColRate : function(){
				var w = this.body_table.width();
				if(this.getCheckbox()) w -= 22;
				if(this.getRownum()) w -= 20;
				for(var i=0; i < this.columnList.length ; i++){
					if(this.columnList[i].getFixed()) w -= this.columnList[i].getWidth();
				}
				for(var i=0; i<this.columnList.length ; i++){
					this.columnFixed[i] = (this.columnList[i].getFixed() ? 'fixed' : (this.columnList[i].getWidth()/(w == 0 ? this.getWidth() : w)*100).toFixed(2));
				}
			},
			_createEditTag : function(){
				this._doDataMode();

				jQuery('tr[mode=edit][_xid=' + this.getXId() + ']', this.grid).empty().remove();
				jQuery('td[mode=edit][_xid]', this.grid).empty().remove();
				
				var tr = jQuery('<tr mode="edit" _xid="'+this.getXId()+'" />').hide();
				var ds = Xwing.getDataset(this._opt.binddataset);
				if(this.getEditwhen() != 'none' && this.getEditselect() == 'row'){
					if( this.getRownum()){
						var td = jQuery("<td class='xw-grid-rownum'/>").attr({align:'center',valign:'center'});
						td.append(jQuery('<span class="xw-grid-cell-rownum" >edit</span>').addClass("xw-mod-font"));
						tr.append(td);
					}
					if( this.getCheckbox()){
						var td = jQuery("<td class='xw-grid-checkbox xw-grid-checkbox-body'/>").attr({align:'center',valign:'center'});
						td.append(jQuery("<span coordy='edit' class='xw-grid-cell-checkbox' value='unchecked' />"));
						tr.append(td);
					}
					
					for(var x=0; x < this.columnList.length ; x++){
						var td = jQuery('<td mode="edit" class="xw-mod-background" coordx="'+x+'" _xid="'+this.columnList[x].getXId()+'" style="height:'+this.getRowheight()+'px;" />')
								 .attr('align',this.columnList[x]._opt.halign)
								 .attr('valign',this.columnList[x]._opt.valign);
						var cell = this.columnList[x]._createEditCell(ds,this.getRowheight());
						tr.append(td.append(cell));
					}
					this.body.append(tr);
					delete tr, ds;
				}else if(this.getEditwhen() != 'none' && this.getEditselect() == 'cell'){
					for(var x=0; x < this.columnList.length ; x++){
						if(this.columnList[x]._opt.edittype == 'none') continue;
						var td = jQuery('<td mode="edit" class="xw-mod-background" coordx="'+x+'" _xid="'+this.columnList[x].getXId()+'" style="height:'+this.getRowheight()+'px;" />').hide();
						td.attr('valign',this.columnList[x].getValign()).attr('align',this.columnList[x].getHalign());
						var cell = this.columnList[x]._createEditCell(ds,this.getRowheight());
						td.append(cell);
						this.body.append(td);
						delete  td, cell;
					}
				}
			},
			binddatasetListener : function(dsEvent){
				if(dsEvent){
					if(dsEvent.id == this.getBinddataset()){
						this._doDataMode();
						var that = this;
						setTimeout(function(){
							if(dsEvent.type == "reset" || dsEvent.type =="sort"){
								/*startwatch "row value"*/	
								that._doValue();
								/*stopwatch "row value"*/
							}else if(dsEvent.type == "cursor"){
								that._doSelect(false,dsEvent.rowIdx);
							}else if ( dsEvent.type=="add"){
								var idx = dsEvent.rowIdx;
								var tr = jQuery('tr.empty',this.body_tbody).empty().remove();
								/*startwatch "row add"*/
								if( dsEvent.rowIdx == (Xwing.getDataset(dsEvent.id).size()-1)){
									that._addRow(idx); 
								}else{
									that._insertRow(idx);
								}
								that._doSummaryValue();
								/*stopwatch "row add"*/
							}else if(dsEvent.type=="update"){
								/*startwatch "row update"*/
								that._updateRow(dsEvent.rowIdx);
								
								if (that.getEditwhen() != 'none' && that.getEditselect() == 'row' && dsEvent.source && 
										dsEvent.source.getAlias() != 'edit') {
									if (dsEvent.source.getParentWidget && dsEvent.source.getParentWidget() == this) {
										that.setSelection(dsEvent.rowIdx, false, true);
									}
								}
								
								/*stopwatch "row update"*/
								that._doSummaryValue();
							}else if(dsEvent.type=="remove"){
								/*startwatch "row remove"*/
								that._removeRow(dsEvent.rowIdx);
								that._doSuppress(dsEvent.rowIdx);
								that._doSelect(false, Xwing.getDataset(dsEvent.id).getCursor());
								that._doSummaryValue();
								/*stopwatch "row remove"*/
							}
						},1);
					}
				}		
				
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);
				
				this.header.height(this.getHeadheight());
				this.header_table.height(this.getHeadheight());
				this.footer.height(this.getFooterheight())
							.css("line-height",(this.getFooterheight()-1)+"px");
				this.summary.height(this.getRowheight());
				this.summary_tr.height(this.getRowheight());
				jQuery('tr',this.body_tbody).height(this.getRowheight());
				
				var virticalBorderSize = (this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth()*2); 
				var bodyHeight = this.getHeight() - virticalBorderSize - this.getHeadheight();
				
				if(this.getPagenavi()){
					bodyHeight -= this.getFooterheight();
					this.footer.show();
				}else{
					this.footer.hide();
				}
				
				if( this.getSummary()){
					bodyHeight -= this.getRowheight();
					this.summary.show();
				}else{
					this.summary.hide();
				}
				this.body.height(bodyHeight);
				this._doAutofit();
				delete virticalBorderSize, bodyHeight;
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				this._doBounds();
			},
			_doValue : function(){
				this.editYN = false;
				var dsObj = Xwing.getDataset(this.getBinddataset());
				this.body_tbody.empty();

				if(dsObj && (this.childCompleted === true)){
					if(dsObj && dsObj.size() > 0){
						var all = "";
						for ( var y = 0, l = dsObj.size(); y < l; y++) {
							all += this._makeRow(y);
						}
						this.body_tbody.html(all);
						delete all;
						this._doEvencolor();
						this._doOddcolor();
						
//						this._doSelect(false,dsObj.getCursor());						
						this.setSelection(dsObj.getCursor(), true);
					}else{
						this.emptyMessage = jQuery("<tr class='empty xw-mod' _xid='"+this.getXId()+"'/>");
						var td = jQuery("<td class='xw-mod' _xid='"+this.getXId()+"'/>");
						this.emptyMessage.append(td);
						td.attr("colspan" , ((this.getCheckbox() ? 1 : 0) + (this.getRownum() ? 1 : 0) + this.columnList.length)).css("text-align", "center");
						td.html(this.getEmptymessage());
						this.body_tbody.append(this.emptyMessage);
						delete td;
					}
					this._doSummaryValue();
					delete dsObj; 
				}
				
				this._refreshScrollPane();
			},
			_createBasicActionEvent : function(jTr){
				this._bind(this.body_table,'mousedown',function(event){
					var chk = jQuery(event.target);
					if(chk.hasClass('xw-grid-checkbox')) chk = chk.children();
					else if(!chk.hasClass('xw-grid-cell-checkbox')) return;
					this._mousedownCheckbox(chk);
				});
				this._bind(this.body_table,'mouseup',function(event){
					var chk = jQuery(event.target);
					if(chk.hasClass('xw-grid-checkbox')) chk = chk.children();
					else if(!chk.hasClass('xw-grid-cell-checkbox')) return;
					this._mouseupCheckbox(chk);
				});
				this._bind(this.body_table,'mouseup',function(event){
					var jTd = this._findTd(jQuery(event.target));
					if (!jTd) return;
					this._clickBasicAction(event, jTd);
					this._clickTdAction(event, jTd);
				});
				this._bind(this.body_table,'dblclick',function(event){
					this.cellFlag = true;
					var jTd = this._findTd(jQuery(event.target));
					if (!jTd || this.getEditwhen() != 'dblclick' || (this.getMultiselectable() && event.ctrlKey)) return;
					this._doEditMode(jTd.attr('mode'),jTd.attr("coordy"),jTd.attr("coordx"));
				});
			},
			_findTd : function(jNode) {
				var tagNodeName = '';
				while(true){
					tagNodeName = jNode[0].tagName.toLowerCase();
					if (tagNodeName == 'td') break;
					else if (tagNodeName == 'table') return;
					else jNode = jNode.parent();
				}			
				return jNode;
			},
			_clickBasicAction : function(e,jTd){
				// set index & setCurser & doSelect
				this.cellFlag = true;
				if(jTd.hasClass('xw-grid-rownum') || jTd.hasClass('xw-grid-checkbox')){
					this.selectedRowIdx = jTd.parent().attr("coordy");
					this.selectedColIdx = "-1";
				}else{
					this.selectedRowIdx = jTd.attr("coordy");
					this.selectedColIdx = jTd.attr("coordx");
				}
				var ds = Xwing.getDataset(this._opt.binddataset);
				if(ds && this.selectedRowIdx != ds.getCursor()) ds.setCursor(this.selectedRowIdx, this);
				if(this.getMultiselectable() && e.ctrlKey)
					this._doSelect(true, this.selectedRowIdx);
				else
					this._doSelect(false, this.selectedRowIdx);
				
				//checkbox 
				if(this.getCheckbox() && this.getCheckboxfield() == 'row' && !this.changeChk){
					var chk = jQuery('.xw-grid-cell-checkbox[coordy='+this.selectedRowIdx+']',this.body_tbody);
					this._mouseupCheckbox(chk);
				}
				this.changeChk = false;	
			},
			_clickTdAction : function(e,jTd){
				var isCell = !(jTd.hasClass('xw-grid-rownum') || jTd.hasClass('xw-grid-checkbox'));
				if( (!isCell && this.getEditwhen() != 'none') || jTd.attr('mode') != 'edit')
					this._doDataMode();
				
				if(isCell && this.getEditwhen() == 'click' && !(this.getMultiselectable() && e.ctrlKey)){
					var that = this;
					setTimeout(function(){
						that._doEditMode(jTd.attr('mode'),jTd.attr("coordy"),jTd.attr("coordx"));
					}, 1);
					
					if(Xwing.isIE()) 
						jQuery('tr[coordy='+this.selectedRowIdx+'][_xid='+this.getXId()+']',this.body_tbody).trigger('click');
				}
			},
			_mousedownCheckbox : function(chk){
				this.changeChk = true;
				if(this.getEditwhen() != 'none') this._doDataMode();
				if( chk.attr('value') == 'checked' ){
					chk.css('background-position','0 -60px');
				}else{
					chk.css('background-position','0 -20px');
				}
			},
			_mouseupCheckbox : function(chk){
				if( chk.attr('value') == 'checked' ){
					chk.attr('value','unchecked');
					chk.css('background-position','0 0');
				}else{
					chk.attr('value','checked');
					chk.css('background-position','0 -40px');
				}
				//this._resetTrColor(jQuery('tr[coordy='+this.selectedRowIdx+"]",this.body_tbody));
			},
			_addRow : function(rowIdx){
				var tr = this._makeRow(rowIdx);
				var jTr = jQuery(tr);
				this.body_tbody.append(jTr);
				this._resetTrColor(jTr);
				this._doSuppress(xwing.Util.parseInt(rowIdx,0));
				delete tr, jTr;
			},
			_insertRow : function(rowIdx){
				/*startwatch "create tr" */
				var tr = this._makeRow(rowIdx);
				var jTr = jQuery(tr);
				/*stopwatch "create tr" */
				this.body_tbody.find('tr[coordy='+rowIdx+']').before(jTr);
				/*startwatch "reset style" */
				this._resetRowIdx();
				this._doSuppress(xwing.Util.parseInt(rowIdx,0));
				delete tr, jTr;
				/*stopwatch "reset style" */
			},
			_updateRow : function(rowIdx){
				var tr = jQuery('tr[coordy='+rowIdx+']',this.body_tbody);
				tr.attr('mode','data');

				if(tr.length != 0){
					var dsObj = Xwing.getDataset(this._opt.binddataset);
					for(var x = 0 ; x < this.columnList.length ; x++){
						var td = jQuery('td[coordx='+x+']',tr);
						var value = this._doCallExpr(rowIdx, x, this, false);
						if( !(value == undefined || value == null) ){
							if(typeof value == 'object'){
								var style = value;
								value = style.value;
								delete style.value;
								this._setCellStyle(td, x, style);
							}
							td.find('span').html(value || "");
						}else{
							td.find('span').html(this.columnList[x]._getValue(dsObj,rowIdx));
						}
						delete td, value;
					}
				}
				this._doSuppress(xwing.Util.parseInt(rowIdx,0));
				delete tr;
			},
			_removeRow : function(rowIdx){
				jQuery('tr[coordy='+rowIdx+']',this.body_tbody).empty().remove();
				this._resetRowIdx();
			},
			_makeRow : function(rowIdx){
				var tr = ["<tr ","","style='height:"+this.getRowheight()+"px;' >",""," </tr>"];
				var trClass = "";
				tr[1] += "coordy='"+rowIdx+"' mode='data' _xid='"+this.getXId()+"' ";
				
				(rowIdx%2 == 0)?trClass +="odd ":trClass +="even ";
				
				if(this.getRownum()){
					var td_start = "<td class='xw-grid-rownum' align='center' valign='center' mode='data' >";
					var span_num = "<span class='xw-grid-cell-rownum xw-mod-font' >"+(rowIdx+1)+"</span>";
					var total =td_start+span_num+"</td>";
					tr[3]+= total;
					delete td_start, span_num, total;
				}
				if(this.getCheckbox()){
					var td_start = "<td class='xw-grid-checkbox xw-grid-checkbox-body' align='center' valign='center' mode='edit' >";
					var chk = "<span coordy='"+rowIdx+"' class='xw-grid-cell-checkbox' value='unchecked' />";
					var total = td_start+chk+"</td>";
					tr[3]+= total;
					delete td_start, chk, total;
				}
				
				this.lineflag = false;
				this.valueflag = false;
				this.tdStyle = "";
				for(var colIdx=0; colIdx <this.columnList.length; colIdx++){
					tr[3] += this._makeCell(rowIdx,colIdx);
				}
				var tot_tr = tr[0]+tr[1]+"class='"+trClass+"' "+tr[2]+tr[3]+tr[4];
				delete tr, trClass;
				return tot_tr;
			},
			_makeCell : function(rowIdx,colIdx){
				var td,cell, exprValue = null, style = {};;
				var dsObj = Xwing.getDataset(this._opt.binddataset);
				var td_start = "<td class='xw-mod-background "+((this.columnList[colIdx]._opt.displaytype == 'button') && 'xw-grid-cell-button')+"' mode='data' coordx='"+colIdx+"' coordy='"+rowIdx+"' "
							+ " _xid='"+this.columnList[colIdx].getXId()+"' ";
				this.columnList[colIdx]._opt.halign && (td_start += "align='"+this.columnList[colIdx]._opt.halign+"' ");
				this.columnList[colIdx]._opt.valign && (td_start += "valign='"+this.columnList[colIdx]._opt.valign+"' ");
				this.tdStyle = "height:"+(this.getRowheight() -1)+"px;";
				(this.columnList[colIdx]._opt.cursor != 'none') && (this.tdStyle += ('cursor:'+this.columnList[colIdx]._opt.cursor+';'));
				
				if(this.getExpr()){
					exprValue = this._doCallExpr(rowIdx, colIdx, this, false);
					if(typeof exprValue == 'object'){
						var style = exprValue;
						exprValue = style.value;
						delete style.value;
					}
				}
				cell = this.columnList[colIdx]._makeTextCell(dsObj,rowIdx,exprValue,style);
				td_start += "style='"+this.tdStyle+"' >";
				td  = td_start + cell+ "</td>";
				delete dsObj, td_start, cell, exprValue;
				return td;
			},
			_resetRowIdx : function(){
				var thisObj = this;
				jQuery('tr', this.body_tbody).each(function(row, cont) {
					jQuery(this).attr('coordy', row);
					jQuery(this).removeClass('even').removeClass('odd');
					(row % 2 == 0 ? jQuery(this).addClass('even') : jQuery(this).addClass('odd'));
					thisObj._resetTrColor(jQuery(this));
					var cnt = 0;
					jQuery('td', this).each(function(col, cont) {
						if (jQuery(this).hasClass('xw-grid-checkbox')) {
							cnt++;
							jQuery('*', this).attr('coordy', row);
						} else if (jQuery(this).hasClass('xw-grid-rownum')) {
							cnt++;
							jQuery('span', this).text((row + 1));
						} else {
							jQuery(this).attr('coordy', row);
							jQuery(this).attr('coordx', col - cnt);
						}
					});
					delete cnt;
				});
				delete thisObj;
			},
			_resetTrColor : function(jTr){
				if(jQuery('td.xw-grid-checkbox-body [value=checked]',jTr).length != '0'){
					jTr.css('background-color','#D9E4F6');
				}else if(jTr.hasClass('odd')){
					var odd = this.getOddcolor() == null ? '' : this.getOddcolor();
					jTr.css('background-color',odd);
				}else if(jTr.hasClass('even')){
					var even = this.getEvencolor() == null ? '' : this.getEvencolor();
					jTr.css('background-color',even);
				}else{
					jTr.css('background-color','');
				}
			},
			_doSummaryValue : function(){
				jQuery('td',this.summary_colgroup).not('.xw-grid-checkbox, .xw-grid-rownum').empty().remove();
				jQuery('td',this.summary_tr).not('.xw-grid-checkbox, .xw-grid-rownum').empty().remove();
				for(var x=0; x < this.columnList.length; x++){
					var td = jQuery("<td/>");
					td.attr("summary","true").attr("coordx",x);
					td.attr("align",this.columnList[x]._opt.halign);
					td.attr("valign",this.columnList[x]._opt.valign);
					this.summary_colgroup.append(this.columnList[x]._getSummaryCol());
					this.summary_tr.append(td);
					
					if(this.getExpr()){
						var exprValue = this._doCallExpr(null, x, this, true);
						if(typeof exprValue == 'object'){
							var style = exprValue;
							exprValue = style.value;
							delete style.value;
							this._setCellStyle(td, x, style);
						}
						td.html(exprValue);
					}
					delete td;
				}
			},
			_doSelect : function(multi,idx){
				if(window.xwingIDE) return;
				var select = jQuery('tr[coordy='+idx+']',this.body);
				if(select.length == 0) return;
				if(!multi){//single
					select.siblings().removeClass('select');
					select.addClass('select');
				}else{//multi
					if(select.hasClass('select')) select.removeClass('select');
					else select.addClass('select');
				}
				this.selectedRowIdx = idx;
				delete select;
			},
			_doEditMode : function(mode,rowIdx,colIdx){
				if(mode == 'data' && this.isBinded('binddataset')) {
					this._doDataMode();
					if(this.columnList[colIdx]._opt.edittype == 'none' && this.getEditselect() == 'cell' ) return;
					this.editYN = true;
					var jCur,jRpc;
					if( this.getEditselect() == 'row'){
						jCur = jQuery('tr[coordy='+rowIdx+']',this.body_tbody);
						jRpc = jQuery('tr[mode=edit][_xid='+this.getXId()+']',this.body).detach().css('display','');
						this._doEditsuppress('row',jCur,jRpc);
						
						var chk = jQuery('span.xw-grid-cell-checkbox',jCur);
						if(this.getCheckbox())
							jQuery('span.xw-grid-cell-checkbox',jRpc).attr('value',chk.attr('value'))
																	 .css('background-position',chk.css('background-position'));
						
						var text = jQuery('span.xw-grid-cell-rownum',jCur).text();
						if(this.getRownum())
							jQuery('span.xw-grid-cell-rownum',jRpc).text(text);
						
						var thisObj = this;
						jQuery('td:not(.xw-grid-rownum,.xw-grid-checkbox)',jRpc).each(function(idx,el){
							if(jQuery('span[_edittype=none]',this).length != 0){
								var td = jQuery(this);
								var cur_td = jQuery('td[coordx='+td.attr('coordx')+'][coordy='+rowIdx+']',jCur);
								if(cur_td.children().css('display') == 'none') td.children().css('display','none');
								else td.children().css('display','');
								jQuery('span[_edittype=none]',this).text(cur_td.text());
							}else{
								var id = jQuery(this).children().attr('_xid');
								var cur_td = jQuery('td[coordx='+this.getAttribute('coordx')+'][coordy='+rowIdx+']',jCur);
								var wdg = Xwing.getWidget(id);
								if(wdg && wdg.getAlias() != 'checkbox' && wdg.getWidth() != cur_td.width())
									wdg.setWidth(cur_td.width());
									
							}
						});
						jCur.hide();
//						jQuery('span[_edittype=none]',jRpc).each(function(idx,el){
//							var td = thisObj._findTd(jQuery(this));
//							var cur_td = jQuery('td[coordx='+td.attr('coordx')+'][coordy='+rowIdx+']',jCur);
//							if(cur_td.children().css('display') == 'none') td.children().css('display','none');
//							else td.children().css('display','');
//							jQuery(this).text(cur_td.text());
//						});
					}else {
						jCur = jQuery('td[coordy='+rowIdx+'][coordx='+colIdx+']',this.body_tbody);
//						jRpc = jQuery('td[mode=edit][_xid='+this.columnList[colIdx].getXId()+']',this.body);
						jRpc = jQuery('td[mode=edit][coordx=' + colIdx + ']', this.body);
						if(jCur.length == 0 || this._doEditsuppress('cell',jCur,jRpc)) return;
						if(!jRpc.children().hasClass('xw-checkbox-shell'))  Xwing.getWidget(jRpc.children().attr('_xid')).setWidth(jCur.width());
						jCur.hide();
						jRpc.detach().css('display','');
					}
					jQuery('*',jRpc).andSelf().attr('coordy',rowIdx);
					if(rowIdx == this.selectedRowIdx) jRpc.addClass('select');
					else jRpc.removeClass('select');
					
					jCur.attr('change','true').before(jRpc);
					this.body.append(jCur.detach());
				}
			},
			_doDataMode : function(){
				this.editYN = false;
				var jCur, jRpc
				,that = this;
				if (this.getEditselect() == 'row') {
					jRpc = jQuery('tr[mode=data][_xid=' + this.getXId() + '][change=true]', this.body).detach().css('display', '');
					jCur = jQuery('tr[mode=edit][_xid=' + this.getXId() + ']', this.body_tbody);
					if (jCur.length == 0)
						return;
					this._doEditsuppress('row', jCur, jRpc);
					var checked = jQuery('span.xw-grid-cell-checkbox', jCur).attr('value');
					if (this._opt.checkbox && checked) {
						var position = (checked == 'checked' ? "0px -40px" : "0 0");
						jQuery('span.xw-grid-cell-checkbox', jRpc).attr('value', checked).css('background-position', position);
					}
				} else {
					jRpc = jQuery('td[mode=data][change=true]', this.body);
					jCur = jQuery('td[mode=edit][_xid=' + jRpc.attr('_xid') + ']', this.body_tbody);
					if (jCur.length == 0 || this._doEditsuppress('cell', jRpc, jCur))
						return;
					jRpc.detach().css('display', '');
				}

				if (jRpc.attr('coordy') == this.selectedRowIdx)
					jRpc.addClass('select');
				else
					jRpc.removeClass('select');

				jRpc.attr('change', '');
				jCur.before(jRpc).hide();
				setTimeout(function(){that.body.append(jCur.detach());},0);
			},
			setEditwhen : function(v){
				this._opt.editwhen = v;
				this._createEditTag();
			},
			getEditwhen : function(){
				return this._opt.editwhen;
			},
			setEditselect : function(select){
				this._opt.editselect = select;
				this._createEditTag();
			},
			getEditselect : function(){
				return this._opt.editselect;
			},
			setEditsuppress : function(v){
				this._opt.editsuppress;
			},
			getEditsuppress : function(){
				return xwing.Util.parseBoolean(this._opt.editsuppress,false);
			},
			_doEditsuppress : function(type,jCur,jRpc){
				if(this.getEditsuppress()) return;
				if(type == 'row'){
					jCur.children(':not(.xw-grid-checkbox,.xw-grid-rownum').each(function(idx,el){
						if(el.children[0].style.display == 'none'){
							var tmpTd = jQuery(this).clone();
							var replacedTd = jRpc.children('[coordx='+el.getAttribute('coordx')+']');
							replacedTd.before(tmpTd);
							jQuery(this).replaceWith(replacedTd.detach());
						}
					});
				}else if(type == 'cell'){
					if( jCur.children().css('display') == 'none' )return true;
					else return false;
				}
			},
			setEmptymessage : function(msg){
				this._opt.emptymessage = msg;
			},
			getEmptymessage : function(){
				return this._opt.emptymessage;
			},
			setEvencolor : function(v){
				this._opt.evencolor =v;
				this._doEvencolor();
			},
			getEvencolor : function(){
				return this._opt.evencolor;
			},
			_doEvencolor : function(){
				if(this._opt.evencolor){ 
					this.body_tbody.find('tr.even').css('background-color', this._opt.evencolor);
				}else{
					this.body_tbody.find('tr.even').css('background-color', "");
				}
			},
			setOddcolor : function(v){
				this._opt.oddcolor = v;
				this._doOddcolor();
			},
			getOddcolor : function(){
				return this._opt.oddcolor;
			},
			_doOddcolor : function(){
				if(this._opt.oddcolor){
					this.body_tbody.find('tr.odd').css('background-color', this._opt.oddcolor);
				}else{
					this.body_tbody.find('tr.odd').css('background-color', "");
				}
			},
			setFooterheight : function(h){
				this._opt.footerheight = h;
				this._doBounds();
			},
			getFooterheight : function(){
				return xwing.Util.parseInt(this._opt.footerheight);
			},
			setHeadcolor : function(v){
				this._opt.headcolor = v;
				this._doHeadcolor();
			},
			getHeadcolor : function(){
				return this._opt.headcolor;
			},
			setHeadgradientcolor : function(v){
				this._opt.headgradientcolor = v;
				this._doHeadcolor();
			},
			getHeadgradientcolor : function(){
				return this._opt.headgradientcolor;
			},
			_doHeadcolor : function(){
				/*debug Xwing.debug("grid.doHeadcolor " + this.getHeadcolor() + ", "+ this.getHeadgradientcolor()); */
				if(this.getHeadgradientcolor() && this.getHeadcolor()){
					this.header_table.css("background-image", "linear-gradient(to top, "+this.getHeadcolor()+", "+this.getHeadgradientcolor()+")");
					if(jQuery.browser.msie){
						this.header_table.css('filter',"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='"+this.getHeadcolor()+"', endColorstr='"+this.getHeadgradientcolor()+"')");									
					}else{
						if(jQuery.browser.mozilla){
							this.header_table.css("background-image", "-moz-linear-gradient(top, "+this.getHeadcolor()+", "+this.getHeadgradientcolor()+")");
						}else if(jQuery.browser.webkit){
							this.header_table.css("background-image", "-webkit-gradient(linear, left top, left bottom, from("+this.getHeadcolor()+"), to("+this.getHeadgradientcolor()+") )");
						}
					}
				}else{
					if(this.getHeadcolor()){
						this.header_table.css('background', this.getHeadcolor());
					}else{
						this.header_table.css('background', "");
					}	
				}
			},
			setHeadheight : function(h){
				this._opt.headheight = h;
				this._doBounds();
			},
			getHeadheight : function(){
				return xwing.Util.parseInt(this._opt.headheight, 25);
			},
			setPagenavi : function(flag){
				this._opt.pagenavi = flag;
				this._doBounds();
			},
			getPagenavi : function(){
				return xwing.Util.parseBoolean(this._opt.pagenavi, false);
			},
			setTotalpage : function(totPage){
				this._opt.totalpage = totPage;
				this._doTotalpage();
			},
			getTotalpage : function(){
				return xwing.Util.parseInt(this._opt.totalpage,1);
			},
			_doTotalpage : function(){
				this.footer.find('span.xw-grid-page-text-last').text("of "+this.getTotalpage());
				this._resetPageicon();
			},
			setCurrentpage : function(currPage){
				this._opt.currentpage = currPage;
				this._doCurrentpage();
			},
			getCurrentpage : function(){
				return xwing.Util.parseInt(this._opt.currentpage,1);
			},
			_doCurrentpage : function(){
				this.footer.find('input.xw-grid-page-input').val(this.getCurrentpage());
				this._resetPageicon();
			},
			_doPage : function(){
				var textSpace = jQuery("<div class='xw-grid-page-text-space' />");
				jQuery("<span class='xw-grid-page-text' >Page</span>").appendTo(textSpace);
				this._bind(jQuery("<input typte='text' class='xw-grid-page-input' />").val(this.getCurrentpage()).appendTo(textSpace),'change',this._changePage);
				jQuery("<span class='xw-grid-page-text-last' >of "+this.getTotalpage()+"</span>").appendTo(textSpace);
				this.footer.append(jQuery('<span class="xw-grid-page-icon xw-grid-page-icon-first" style="vertical-align:middle" />'))
				   		   .append(jQuery('<span class="xw-grid-page-icon xw-grid-page-icon-prev" style="vertical-align:middle"/>'))
				   		   .append(jQuery('<div class="xw-grid-page-bar" style="vertical-align:middle"/>'))
				   		   .append(textSpace)
				   		   .append(jQuery('<div class="xw-grid-page-bar" style="vertical-align:middle"/>'))
				   		   .append(jQuery('<span class="xw-grid-page-icon xw-grid-page-icon-next" style="vertical-align:middle"/>'))
				   		   .append(jQuery('<span class="xw-grid-page-icon xw-grid-page-icon-last" style="vertical-align:middle"/>'));
				
				this._bind(this.footer,'click',this._clickPage);
				this._resetPageicon();				
			},
			_changePage : function(event){
				var value = event.currentTarget.value;
				if(isNaN(value) ) event.currentTarget.value = this.getCurrentpage();
				else if(value > this.getTotalpage()) this.setCurrentpage(this.getTotalpage());
				else if( value < 1 ) this.setCurrentpage(1);
				else this.setCurrentpage(value);
				
				this._fire('pageclick',{
					type:'pageclick',
					source: this,
					event: event,
					index : this.getCurrentpage()
				}); 
			},
			_clickPage : function(event){
				var target = jQuery(event.target);
				if(target.hasClass('xw-grid-page-icon-disabled') || !target.hasClass('xw-grid-page-icon')) return;
				
				if(target.hasClass('xw-grid-page-icon-first')) this.setCurrentpage(1);
				else if(target.hasClass('xw-grid-page-icon-prev')) this.setCurrentpage(this.getCurrentpage() - 1);
				else if(target.hasClass('xw-grid-page-icon-next')) this.setCurrentpage(this.getCurrentpage() + 1);
				else if(target.hasClass('xw-grid-page-icon-last')) this.setCurrentpage(this.getTotalpage());
				
				this._fire('pageclick',{
					type:'pageclick',
					source: this,
					event: event,
					index : this.getCurrentpage()
				});				
			},
			_resetPageicon : function(){
				if(this.getCurrentpage() == 1)
					this.footer.find('span.xw-grid-page-icon-first, span.xw-grid-page-icon-prev').addClass('xw-grid-page-icon-disabled');
				else
					this.footer.find('span.xw-grid-page-icon-first, span.xw-grid-page-icon-prev').removeClass('xw-grid-page-icon-disabled');
				
				if(this.getCurrentpage() == this.getTotalpage())
					this.footer.find('span.xw-grid-page-icon-last, span.xw-grid-page-icon-next').addClass('xw-grid-page-icon-disabled');
				else
					this.footer.find('span.xw-grid-page-icon-last, span.xw-grid-page-icon-next').removeClass('xw-grid-page-icon-disabled');
			},
			setResizable : function(v){
				this._opt.resizable = v;
				return this._doResizable();
			},
			getResizable : function(){
				return xwing.Util.parseBoolean(this._opt.resizable);
			},
			_doResizable : function(){
				var result = false;
				for( var i  in this.columnList){
					var column = this.columnList[i];
					result = column._doResizable();
				}
				return result;
			},
			setSelection : function(idx, reveal, editable) {
				!xwing.Util.is(idx, 'array') && (idx = [ idx || 0 ]);
				if (idx.length == 0) return;
				
				if (editable && (this.getEditwhen() != 'none')) {
					var jTr = jQuery('tr[coordy=' + idx[0] + '][_xid=' + this.getXId() + ']', this.body_tbody);
					this._doEditMode(jTr.attr('mode'), jTr.attr("coordy"), 0);
					this._doSelect(false, idx[0]);
				} else {
					this._doDataMode();
					jQuery('.select', this.body).removeClass('select');
					for ( var i = 0, l = idx.length, m = this.getMultiselectable(); i < l; i++) {
						this._doSelect(m, idx[i]);
					}
				}
				
				if (reveal) {
					var jTarget = jQuery('tr[coordy=' + idx[0] + ']', this.body),
						clientHeight = xwing.Util.parseInt(this.body[0].clientHeight, 0),
						scrollTop = this.body.scrollTop(),
						offsetTop = jTarget[0].offsetTop,
						height = offsetTop + jTarget.height();
						
					if (scrollTop > height) {
						this.body.scrollTop(offsetTop);
					} else if ((scrollTop + clientHeight) < height) {
						this.body.scrollTop(height - clientHeight);
					}
				}
			},	
			getSelection : function(editable) {
				var array = [];
				
				jQuery('tr.select', this.body_table).each(function(idx, ele) {
					if (editable && jQuery(ele).find('[mode=edit][_xid]').length == 0) return;
					array.push(+ele.getAttribute('coordy'));
				});
				
				return array;
			},
			getSelectionCnt : function() {
				return jQuery('tr.select', this.body_table).size();
			},
			getSelectedColIdx : function(){
				return +this.selectedColIdx;
			},
			setSortable : function(v){
				this._opt.sortable = v;
				return this._doSortable();
			},
			getSortable: function(){
				return xwing.Util.parseBoolean(this._opt.sortable);
			},
			_doSortable : function(){
				var result = false;
				for( var i  in this.columnList){
					var column = this.columnList[i];
					result = column._doSortable();
				}
				return result;
			},
			setRownum : function(rownum){
				this._opt.rownum = rownum;
				this._doHeadrownum();
				this._doBodyRownum();
				this._doAutofit();
			},
			getRownum : function(){
				return xwing.Util.parseBoolean(this._opt.rownum);
			},
			_doHeadrownum : function(){
				if(this.getRownum() && jQuery('.xw-grid-rownum',this.getShell()).length == 0){
					var headCol = jQuery("<col class='xw-mod xw-grid-rownum xw-grid-rownum-head'/>").width('20')
																 .attr('_xid',this._opt._xid);
					var headTh = jQuery("<th class='xw-mod xw-grid-rownum'/>").html("No")
															   .attr('_xid',this._opt._xid);
					var bodyCol = jQuery("<col class='xw-mod xw-grid-rownum'/>").width('20')
																 .attr('_xid',this._opt._xid);
					var summCol = jQuery("<col class='xw-mod xw-grid-rownum xw-grid-rownum-summary'/>").width('20')
									.attr('_xid',this._opt._xid);
					var summTh = jQuery("<td class='xw-mod xw-grid-rownum xw-grid-rownum-summary'/>").attr('_xid',this._opt._xid);
					
					this.header_colgroup.prepend(headCol); 
					this.header_tr.prepend(headTh);
					this.body_colgroup.prepend(bodyCol);
					this.summary_colgroup.prepend(summCol);
					this.summary_tr.prepend(summTh);
				}else if(!this.getRownum()){
					jQuery('.xw-grid-rownum',this.getShell()).empty().remove();
					jQuery('tr[mode=edit][_xid='+this.getXId()+']','body').find('td.xw-grid-rownum').empty().remove();
				}
			},
			_doBodyRownum : function(){
				var ds = Xwing.getDataset(this._opt.binddataset);
				if(ds && this.getRownum() && jQuery('.xw-grid-rownum',this.body_tbody).length == 0){
					for(var i=0; i < ds.size() ; i++){
						var td = jQuery("<td class='xw-grid-rownum'/>").attr({align:'center',valign:'center'});
						var span = jQuery('<span class="xw-grid-cell-rownum" >'+i+'</span>').addClass("xw-mod-font");
						var tr = jQuery('tr[coordy='+i+']',this.body_tbody);
						
						td.append(span);
						tr.prepend(td);
					}
					
					if(this.getEditwhen() != 'none' && this.getEditselect() == 'row'){
						var tr = jQuery('tr[mode=edit][_xid='+this.getXId()+']','body');
						var td = jQuery("<td class='xw-grid-rownum'/>").attr({align:'center',valign:'center'});
						var span = jQuery('<span class="xw-grid-cell-rownum" >'+i+'</span>').addClass("xw-mod-font");
						
						td.append(span);
						tr.prepend(td);
					}
				}
			},
			setCheckbox : function(checkbox){
				this._opt.checkbox = checkbox;
				this._doHeadcheckbox();
				this._doBodyCheckbox();
				this._doAutofit();
			},
			getCheckbox : function(){
				return xwing.Util.parseBoolean(this._opt.checkbox);
			},
			_doHeadcheckbox : function(){
				var thisObj = this;
				if(this.getCheckbox() && jQuery('.xw-grid-checkbox',this.getShell()).length == 0){
					var headCol = jQuery("<col class='xw-mod xw-grid-checkbox xw-grid-checkbox-head'/>").width('22px')
																 .attr('_xid',this._opt._xid);
					var headTh = jQuery("<th class='xw-mod xw-grid-checkbox xw-grid-checkbox-head'/>").attr('_xid',this._opt._xid);
					var bodyCol = jQuery("<col class='xw-mod xw-grid-checkbox xw-grid-checkbox-head'/>").width('22px')
																 .attr('_xid',this._opt._xid);
					
					var summCol = jQuery("<col class='xw-mod xw-grid-checkbox xw-grid-checkbox-summary'/>").width('22px')
									.attr('_xid',this._opt._xid);
					var summTd = jQuery("<td class='xw-mod xw-grid-checkbox xw-grid-checkbox-summary'/>").attr('_xid',this._opt._xid)
								.attr('summary','true');
					
					var chk = this._makeHeadCheckbox();
					headTh.append(chk);
					
					if(this.getRownum()){
						this.header_colgroup.find(':first').after(headCol); 
						this.header_tr.find(':first').after(headTh);
						this.body_colgroup.find(':first').after(bodyCol);
						this.summary_colgroup.find(':first').after(summCol);
						this.summary_tr.find(':first').after(summTd);
					}else{
						this.header_colgroup.prepend(headCol); 
						this.header_tr.prepend(headTh);
						this.body_colgroup.prepend(bodyCol);
						this.summary_colgroup.prepend(summCol);
						this.summary_tr.prepend(summTd);
					}
					
				}else if(!this.getCheckbox()){
					jQuery('.xw-grid-checkbox',this.getShell()).empty().remove();
					jQuery('tr[mode=edit][_xid='+this.getXId()+']','body').find('td.xw-grid-checkbox').empty().remove();
				}
			},
			_doBodyCheckbox : function(){
				var ds = Xwing.getDataset(this._opt.binddataset);
				if(ds && this.getCheckbox() && jQuery('.xw-grid-checkbox',this.body_tbody).length == 0){
					for(var i=0; i < ds.size() ; i++){
						var td = jQuery("<td class='xw-grid-checkbox xw-grid-checkbox-body'/>").attr({align:'center',valign:'center'});
						var chk = jQuery("<span coordy='"+i+"' class='xw-grid-cell-checkbox' value='unchecked' />");;
						var tr = jQuery('tr[coordy='+i+']',this.body_tbody);
						
						td.append(chk);
						if(this.getRownum()){
							tr.find(':first').after(td);
						}else{
							tr.prepend(td);
						}
					}
					
					if(this.getEditwhen() != 'none' && this.getEditselect() == 'row'){
						var tr = jQuery('tr[mode=edit][_xid='+this.getXId()+']','body');
						var td = jQuery("<td class='xw-grid-checkbox xw-grid-checkbox-body'/>").attr({align:'center',valign:'center'});
						var chk = jQuery("<span coordy='"+i+"' class='xw-grid-cell-checkbox' value='unchecked' />");;
						td.append(chk);
						
						if(this.getRownum()){
							tr.find(':first').after(td);
						}else{
							tr.prepend(td);
						}
					}
				}
			},
			_makeHeadCheckbox : function(){
				var chk = jQuery("<span coordy='head' class='xw-grid-cell-checkbox' value='unchecked' />");
				var thisObj = this;
				this._bind(chk, 'mousedown', function(e){
					thisObj.changeChk = true;
					if(thisObj.getEditwhen() != 'none') thisObj._doDataMode();
					if( chk.attr('value') == 'checked' ){
						chk.css('background-position','0 -60px');
					}else{
						chk.css('background-position','0 -20px');
					}
				});
				this._bind(chk, 'mouseup', function(e){
					if( chk.attr('value') == 'checked' ){
						chk.attr('value','unchecked');
						chk.css('background-position','0 0');
					}else{
						chk.attr('value','checked');
						chk.css('background-position','0 -40px');
					}
					thisObj.chk_headCheckboxClick(chk.attr('value'));
				});
				return chk;
			},
			chk_headCheckboxClick : function(value){
				this.changeChk = false;
				var chk = jQuery('.xw-grid-cell-checkbox',this.body_tbody);
				if(value == 'checked'){
					chk.attr('value','checked').css('background-position','0 -40px');
				}else{
					chk.attr('value','unchecked').css('background-position','0 0');
					var thisObj = this;
					jQuery("tr",this.body_tbody).each(function(){
						thisObj._resetTrColor(jQuery(this));
					});
				}
			},
			getCheckedCnt : function() {
				if (this.getCheckbox()) {
					return jQuery('.xw-grid-checkbox-body [value=checked]', this.getShell()).length;
				} else
					return 0;
			},
			getCheckedIdx : function() {
				var array = [];

				jQuery('.xw-grid-checkbox-body [value=checked]', this.getShell()).each(function(idx, ele) {
					array.push(ele.getAttribute('coordy'));
				});

				return array;
			},
			setExpr : function(expr){
				this._opt.expr = expr;
			},
			getExpr : function(){
				return this._opt.expr;
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
			_doCallExpr : function(rowIdx,colIdx,grid,summary){
				try{
					var f;
					if( typeof this.getExpr() == 'string'){
						f = window[this.getExpr()];
					}
					if(f === undefined || f == null) return;
					return f.call(null,rowIdx,colIdx,grid,summary);
				}catch(e){
				}
			},
			_setCellStyle : function(jTd,colIdx,style){
				/*for(var i in style){
					var child = jTd.children().size() == 0 ? jTd : jTd.children();
					(i == 'fontfamily') && child.css('font-family',style[i]);
					(i == 'fontcolor') && child.css('color',style[i]);
					(i == 'fontstyle') && child.css('font-style',style[i]);
					(i == 'fontweight') && child.css('font-weight',style[i]);
					(i == 'fontsize') && child.css('font-size',style[i]);
					(i == 'fontdecoration') && td.css('text-decoration',style[i]);

					(i == 'bgcolor') && jTd.css('background-color',style[i]);
					(i == 'bgimage') && jTd.css('background-image',style[i]);
				}*/
				var child = jTd.children().size() == 0 ? jTd : jTd.children();
				var col = this.columnList[colIdx];
				var textStyle = col._textStyle(style);
				child.css('cssText',textStyle);
				jTd.css('background-color',(style['bgcolor'] || this._opt.bgcolor || ''));
				jTd.css('background-image',(style['bgimage'] || this._opt.bgimage || ''));
			},
			setSummary : function(value){
				this._opt.summary = value;
				this._doBounds();
			},
			getSummary : function(){
				return xwing.Util.parseBoolean(this._opt.summary);
			},
			getColumnList : function(){
				return this.columnList;
			},
			setHeadfontsize : function(v){
				this._opt.headfontsize = v;
				this._doHeadfont();
			},
			getHeadfontsize : function(){
				return this._opt.headfontsize;
			},
			setHeadfontcolor : function(v){
				this._opt.headfontcolor = v;
				this._doHeadfont();
			},
			getHeadfontcolor : function(){
				return this._opt.headfontcolor;
			},
			setHeadfontdecoration : function(v){
				this._opt.headfontdecoration = v;
				this._doHeadfont();
			},
			getHeadfontdecoration : function(){
				return this._opt.headfontdecoration;
			},
			setHeadfontfamily : function(v){
				this._opt.headfontfamily = v;
				this._doHeadfont();
			},
			getHeadfontfamily : function(){
				return this._opt.headfontfamily;
			},
			setHeadfontstyle : function(v){
				this._opt.headfontstyle = v;
				this._doHeadfont();
			},
			getHeadfontstyle : function(){
				return this._opt.headfontstyle;
			},
			setHeadfontweight : function(v){
				this._opt.headfontweight = v;
				this._doHeadfont();
			},
			getHeadfontweight : function(){
				return this._opt.headfontweight;
			},
			_doHeadfont : function(){
				var ths = this.header_tr.find('th');
				ths.css('font-size',this.getHeadfontsize() + 'px');
				ths.css('color' , this.getHeadfontcolor());
				ths.css('text-decoration' , this.getHeadfontdecoration());
				ths.css('font-style' , this.getHeadfontstyle());
				ths.css('font-weight' , this.getHeadfontweight());
				
				try {
					if (this.getHeadfontfamily && Xwing.config.fonts && Xwing.config.fonts[this.getHeadfontfamily()]) {
						ths.css("font-family", Xwing.config.fonts[this.getHeadfontfamily()]);
					}
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontfamily" + +":" + e);
				}
			},
			setHeadtextwrap : function(v){
				this._opt.headtextwrap = v;
				this._doHeadtextwrap();
			},
			getHeadtextwrap : function(){
				return xwing.Util.parseBoolean(this._opt.headtextwrap,false);
			},
			_doHeadtextwrap : function(){
				var ths = jQuery('th',this.header_tr).not('.xw-grid-rownum, .xw-grid-checkbox');
				if(this.getHeadtextwrap()){
					ths.css('white-space','pre');
				}else{
					ths.css('white-space','');
				}
			},
			setTextwrap : function(wrap){
				this._opt.textwrap = wrap;
				this._doTextwrap();
			},
			getTextwrap : function(){
				return this._opt.textwrap;
			},
			_doTextwrap : function(){
				var tds = jQuery('td',this.body_table).not('.xw-grid-rownum, .xw-grid-checkbox');
				var span = jQuery('span',tds);
				switch(this.getTextwrap()){
				case 'none':
						span.css('white-space','nowrap');
					break;
				case 'pre':
						span.css('white-space','pre');
					break;
				case 'prewrap':
						span.css('white-space','pre-wrap');
					break;
				}
			},
			setMultiselectable: function(v){
				this._opt.multiselectable = v;
			},
			getMultiselectable : function(){
				return xwing.Util.parseBoolean(this._opt.multiselectable,false);
			},
			setMovecolumn : function(v){
				this._opt.movecolumn = v;
				this._doMovecolumn();
			},
			getMovecolumn : function(){
				return xwing.Util.parseBoolean(this._opt.movecolumn,false);
			},
			_doMovecolumn : function(){
				this._bind(this.header_tr,'mousedown',function(e){
					var jTh = jQuery(e.target);
					if(jTh.hasClass('xw-grid-column-resizer') || !this.getMovecolumn()) return;
					if(jTh.hasClass('xw-grid-rownum') || jTh.hasClass('xw-grid-checkbox') || jTh.hasClass('xw-grid-cell-checkbox')) return;
					this.move.cur = this._findColIdx(jTh);
				});
				this._bind(this.header_tr,'mousemove',function(e){
					if(this.move.cur == undefined) return;
					this.sortFlag = false;
					var jTh = this._findjTh(jQuery(e.target));
					var tarIdx = this._findColIdx(jTh);
					if(this.move.cur != tarIdx){
						if(jTh[0] !== this.move.tarDom) this._changeColumn(this.move.cur,tarIdx);
					}
				});
				this._bind(this.header_tr,'mouseup',function(e){
					this.move.cur = undefined;
					this.move.tarDom = undefined;
					if(this.move.changing){
						this.move.changing = false;
						this._doDataMode();
						this._doValue();
					}
					
				});
				this._bind(this.header_tr,'mouseleave',function(e){
					this.move.cur = undefined;
					this.move.tarDom = undefined;
					if(this.move.changing){
						this.move.changing = false;
						this._doDataMode();
						this._doValue();
					}
				});
			},
			_findjTh : function(jTh){
				while(true){
					if(jTh[0].tagName.toLowerCase() == 'th') break;
					else if(jTh[0].tagName.toLowerCase() == 'tr') return;
					else jTh = jTh.parent();
				}
				return jTh;
			},
			_findColIdx : function(jTh){
				var etcIdx = 0;
				if(this.getRownum()) etcIdx++;
				if(this.getCheckbox()) etcIdx++;
				return jTh.prevAll().length - etcIdx;
			},
			_changeColumn : function(curIdx,tarIdx){
				this.move.changing = true;
				var tmp = this.columnList[curIdx];
				this.columnList[curIdx] = this.columnList[tarIdx];
				this.columnList[tarIdx] = tmp;
				
				tmp = this.columnFixed[curIdx];
				this.columnFixed[curIdx] = this.columnFixed[tarIdx];
				this.columnFixed[tarIdx] = tmp;
				
				//th change
				var ths = this.header_tr.find('th').not('.xw-grid-checkbox,.xw-grid-rownum');
				var hcols = this.header_colgroup.find('col').not('.xw-grid-checkbox,.xw-grid-rownum');
				var bcols = this.body_colgroup.find('col').not('.xw-grid-checkbox,.xw-grid-rownum');
				var scols = this.summary_colgroup.find('col').not('.xw-grid-checkbox,.xw-grid-rownum');
				var removeth = ths.filter(':eq('+curIdx+')');
				var removehcol = hcols.filter(':eq('+curIdx+')');
				var removebcol = bcols.filter(':eq('+curIdx+')');
				var removescol = scols.filter(':eq('+curIdx+')');
				var th = ths.filter(':eq('+tarIdx+')');
				var hcol = hcols.filter(':eq('+tarIdx+')');
				var bcol = bcols.filter(':eq('+tarIdx+')');
				var scol = scols.filter(':eq('+tarIdx+')');
				
				if( curIdx < tarIdx){
					removeth.insertAfter(th);
					removehcol.insertAfter(hcol);
					removebcol.insertAfter(bcol);
					removescol.insertAfter(scol);
				}else{
					removeth.insertBefore(th);
					removehcol.insertBefore(hcol);
					removebcol.insertBefore(bcol);
					removescol.insertBefore(scol);
				}
				
				//edit th change
				if(this.getEditwhen() != 'none' && this.getEditselect() == 'row'){
					var edit_ths = jQuery('tr[mode=edit][_xid='+this.getXId()+']',this.body).children(':not(.xw-grid-checkbox,.xw-grid-rownum)');
					var edit_rTh = edit_ths.filter(':eq('+curIdx+')');
					var edit_th = edit_ths.filter(':eq('+tarIdx+')');
					
					edit_rTh.attr('coordx',tarIdx);
					edit_th.attr('coordx',curIdx);
					if(curIdx < tarIdx) edit_rTh.insertAfter(edit_th);
					else edit_rTh.insertBefore(edit_th);
				}
				
				this.move.cur = tarIdx;
				this.move.tarDom = th[0];
			},
			setRowheight : function(h){
				this._opt.rowheight = h;
				this._doBounds();
			},
			getRowheight : function(){
				return xwing.Util.parseInt(this._opt.rowheight);
			},
			setCheckboxfield : function(v){
				this._opt.checkboxfield = v;
			},
			getCheckboxfield : function(){
				return this._opt.checkboxfield;
			},
			setAutofit : function(v){
				this._opt.autofit = v;
			},
			getAutofit : function(){
				return xwing.Util.parseBoolean(this._opt.autofit,false);
			},
			_doAutofit : function(){
				if(!window.xwingIDE && this.getAutofit()){
					var visibleWidth = this.getWidth() - this.getBorderwidth() * 2 - (this.body_tbody[0].rows.length * this.getRowheight() > this.body.height() ? 20 : 0);
					var preWidth = 0;
					for(var i=0; i < this.columnList.length ; i++){
						if(this.columnList[i].getFixed())
							visibleWidth = visibleWidth - this.columnList[i].getWidth();
					}
					if(this.getRownum()) visibleWidth -= 20;
					if(this.getCheckbox()) visibleWidth -= 22;
					for(var i=0; i < this.columnList.length ; i++){
						if(this.columnList[i].getFixed()) continue;
						var width = parseInt(this.columnFixed[i]*visibleWidth/100);
						if((this.columnList.length -1) != i)preWidth += width;
						else width = visibleWidth - preWidth;
						this.columnList[i].headCol.width(width);
						this.columnList[i].bodyCol.width(width);
						this.columnList[i].summaryCol.width(width);
						this.columnList[i]._opt.width = width;
						
						if(this.columnList[i].getEdittype() == 'checkbox') continue;
						if(this.getEditwhen() != 'none' && this.getEditselect() == 'row'){
							var editTr = jQuery('tr[mode=edit][_xid='+this.getXId()+']',this.body);
							editTr.children('td[_xid='+this.columnList[i].getId()+']').children().width(width); 
						}else if(this.getEditwhen() != 'none' && this.getEditselect() == 'cell'){
							jQuery('td[mode=edit][_xid='+this.columnList[i].getId()+']',this.body).children().width(width); 
						}
						
					}
				}
				this._resetWidth();
			},
			_doSuppress : function(rowIdx){
				var lineflag = false;
				var valueflag = false;
				var dataset = Xwing.getDataset(this._opt.binddataset);
				for(var x=0; x < this.columnList.length; x++){
					if(this.columnList[x].getSuppress()){
						var preV = dataset.getValue((rowIdx-1),this.columnList[x].getBindcolumn());
						var curV = dataset.getValue(rowIdx,this.columnList[x].getBindcolumn());
						var nextV = dataset.getValue(((rowIdx+1)>= dataset.size()?rowIdx:(rowIdx+1)),this.columnList[x].getBindcolumn());
						if(preV != curV){
							jQuery('td[coordx='+x+'][coordy='+(rowIdx-1)+']',this.body_tbody).css('border-bottom','');
							jQuery('td[coordx='+x+'][coordy='+rowIdx+'] > span',this.body_tbody).css('display','inline');
							valueflag = true;
						}else{
							jQuery('td[coordx='+x+'][coordy='+(rowIdx-1)+']',this.body_tbody)
							.css('border-bottom',valueflag ? '' : '0px none');
							jQuery('td[coordx='+x+'][coordy='+rowIdx+'] > span',this.body_tbody)
							.css('display',valueflag ? 'inline' : 'none');
						}
						
						var curTd = jQuery('td[coordx='+x+'][coordy='+rowIdx+']',this.body_tbody);
						var nextTd = jQuery('td[coordx='+x+'][coordy='+((rowIdx+1)>= dataset.size()?rowIdx:(rowIdx+1))+'] > span',this.body_tbody);
						if(curV == nextV && rowIdx!=(dataset.size() -1)){
							curTd.css('border-bottom',lineflag ? '' : '0px none');
							nextTd.css('display',lineflag ? 'inline' : 'none');
						}else if(rowIdx != (dataset.size() -1)){
							curTd.css('border-bottom','');
							nextTd.css('display','inline');
							lineflag  = true;
						}
					}
				}
			},
			exportData : function(filename,url){
				if(location.protocol != 'http:'){
					alert("Web 환경에서만 이용 할 수 있습니다.");
					return;
				}
				if(filename == undefined) filename = "";
				 var param = {
						 filename : filename,
						 data : this._makeTable()
				 };
				 this._postSend(url,param);
			},
			_makeTable : function(){
				var tmpThead = this.header_thead.clone();
				var tmpTbody = this.body_tbody.clone();
				var summary;
				if(this.getSummary()){
					summary = this.summary_tr.clone();
					if(this.getCheckbox()) summary.find('td.xw-grid-checkbox').empty().remove();
				}
				var th = tmpThead.find("th");
				if(this.getCheckbox()){
					tmpThead.find('th.xw-grid-checkbox').empty().remove();
					tmpTbody.find('td.xw-grid-checkbox').empty().remove();
				}
				if(this.getHeadcolor()) th.css('background-color',this.getHeadcolor());
				else th.css('background-color','#b8c6d8');
				th.attr('align','center');
				th.css('font-size',this.getHeadfontsize()+'pt');
				tmpThead.find('tr').css('height','');
				tmpTbody.find('tr').css('background-color','');
				
				for(var i=0; i < this.columnList.length; i++){
					var td = jQuery('td[coordx='+i+']',tmpTbody);
					td.find('span').css('font-size',this.columnList[i].getFontsize()+'pt');
					if(this.getOddcolor()) jQuery('tr.odd',tmpTbody).find('td').css('background-color',this.getOddcolor());
					if(this.getEvencolor()) jQuery('tr.even',tmpTbody).find('td').css('background-color',this.getEvencolor());
					td.css('height','');
					
					var halign = this.columnList[i].getHalign();
					var valign = this.columnList[i].getValign();
					td.attr('align',halign);
					td.attr('valign',valign);
					if(this.getSummary()) {
						summary.find('td:not(.xw-grid-rownum):eq('+i+')').attr('align',halign);
						summary.find('td:not(.xw-grid-rownum):eq('+i+')').attr('valign',valign);
					}
				}
				var result;
				if(this.getSummary()){
					result = "<table border=1>"+tmpThead[0].outerHTML+tmpTbody[0].outerHTML+summary[0].outerHTML+"</table>";
					delete summary.empty().remove();
				}else
					result = "<table border=1>"+tmpThead[0].outerHTML+tmpTbody[0].outerHTML+"</table>";
				delete tmpThead.empty().remove();
				delete tmpTbody.empty().remove();
				result = result.replace(/\n/gi, "<br>");
				return result;
				
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
			}
		}
	}
});
