var Grouping = function(dgObj, ds){
	this._datagrid = dgObj;
	this._dataset = ds;
	this._keyword = [];
	this._allwidth = 0;
	
	// row별로 edit를 다르게 하고 싶기 때문에..............하하하하하하ㅏ하하하하하하 
	this._iseditbyrow = false;
	this._celleditbyrow = [];
};

Grouping.prototype = {
		_revealSelectRow : function(row){
			var jTarget = jQuery('tr[cy=' + row + ']', this.body_tbody);

			if( jTarget.length != 0 ){
				// 해당 Index로 이동
				var clientHeight = xwing.Util.parseInt(this.body_area[0].clientHeight, 0),
					scrollTop = this.body_area.scrollTop(),
					offsetTop = jTarget[0].offsetTop + jTarget.height()*2;
				var that = this;
				jTarget.parents('td').each(function(idx, el){
					offsetTop += el.offsetTop;
				});
//				console.log(row+":"+ clientHeight+":"+scrollTop+":"+offsetTop)
				
				if( scrollTop > (offsetTop - jTarget.height() )){
					this.body_area.scrollTop(offsetTop - jTarget.height() );
				}
				else if( ( offsetTop - scrollTop) > clientHeight ){
//					console.log(offsetTop - clientHeight);
					this.body_area.scrollTop(offsetTop - clientHeight);
				}
			}
		},
		binddatasetListener : function(dsEvent){
			this._doDataMode();
			this._group && this._group._doRowDataMode();
			
			if (dsEvent.type == "cursor") {
				this.setSelection(dsEvent.rowIdx, true);
			} else if( dsEvent.type == "update"){
				this._group && this._group._updateRow(dsEvent);
//				this._doValue();
			}else{
				this._doValue();
			}
				
		},
		_initMethod : function(){
			this._datagrid._revealSelectRow = this._revealSelectRow;
			this._datagrid.binddatasetListener = this.binddatasetListener;
		},
		_init : function(){
			for(var i=0, l = this._datagrid._ast['body'].length ; i < l ; i++){
				var cells = this._datagrid._ast['body'][i].getCell();
				for(var j=0, k= cells.length; j < k ; j++){
					var cell = cells[j];
					if(cell.getGroupable() ) this._keyword.push(cell.getBindcolumn());
					if(cell.getChangeedit() ){
						this._iseditbyrow = true;
						this._celleditbyrow[j] = cell;
					}
				}
			}
			for(var i=0, l= this._datagrid._ast['column'].length; i < l ; i++){
				this._allwidth += this._datagrid._ast['column'][i].getWidth();
			}
			
			// datagrid에 존재하는 함수들 초기화
			this._initMethod();
			
			// editable 생성해야 함.............. 
			 this._createEditPart(); 
			 
			var that = this;
			this._datagrid.body.bind('mousedown',function(event){
				that._bindMousedown.call(that,event);
			});
			this._datagrid.body.bind('mouseup',function(event){
				that._bindMouseup.call(that,event);
			});
			this._datagrid.body.bind('keydown',function(event){
				that._bindKeydown.call(that, event);
			});
		},
		_createEditPart : function() {
			// GROUP에서도 이 것을 많이 사용한다. 
			if (window.xwingIDE) 
				return;

			var that = this._datagrid;				
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
		_doValue : function(init){
			var datagrid = this._datagrid
				, ds = this._dataset
				, _groups = {};
			
			datagrid.body_tbody.addClass('xw-datagrid-body-group');
			this._doRowDataMode();
			// new rendering
			for(var i=0, l=ds.size(); i < l ; i++){
				// depth 만들기 
				var step = _groups,
					parent, test;
				for(var j=0; j < this._keyword.length; j++){
					var keyval = ds.getValue(i,this._keyword[j]);
					if( !step[keyval] ){
						if(!step._td) parent = datagrid.body_tbody;
						else parent = step._td.find('tbody:first');
						step[keyval] = [];
						step[keyval]._td = this._makeGroupTd(j, this._keyword[j], keyval, parent);
					}
					step = step[keyval];
				}
				test = (!step._td) ? datagrid.body_tbody : step._td.find('tbody:first');
				tr = datagrid._makeRowHtml(ds, i );
				test.append(tr);
			}
			
			if (init) {
				var dsObj = Xwing.getDataset(datagrid.getBinddataset());
				// 다시 생각해보자 SORA
//				dsObj.setCursor(0);
				datagrid.setSelection(dsObj.getCursor(), true); // true 해야 함 ㅜㅜㅜㅜㅜㅜㅜ
			}
		},
		_makeGroupTd : function(depth, title, value, parent){
			var $td
				, column = this._datagrid._ast.column;
			var val = title+" : "+value
			    , v ;
			if(this._datagrid.getGroupheaderexpr()){
				try{
					v = eval(this._datagrid.getGroupheaderexpr());
					val = xwing.Util.is(v, 'function') ? v.call(this, title, value, depth) : v;
				}catch(e){
					Xwing.debug(e);
				}
				
			}
			var tdtmp = [
			    '<td ', '', ' _depth="'+depth+'" class="xw-datagrid-group-step" >',
			    	'<div class="xw-datagrid-group-text xw-datagrid-group-expand" ','',' >',val,'</div>',
			    	'<table class="xw-datagrid-group-table xw-datagrid-group-disabled" style="width:'+this._allwidth+'px;" >', // xw-datagrid-group-disabled
			    		'<colgroup>','','</colgroup>',
			    		'<tbody></tbody></table>',
			    	'</td>'
			             ];
			
			// style 신경 써야지.. expr의 .. 
			var height = 'width:'+this._allwidth+'px;height:'+this._datagrid.getRow('body',0).getHeight()+'px;line-height:'+this._datagrid.getRow('body',0).getHeight()+'px;';
			var style = "background-position-x:"+(depth*20+5)+'px;padding-left:'+(depth+1)*20+'px;'+height;
			var cols = [], colspan = 0;
			for(var i=0, l=column.length; i < l ; i++ ){
				colspan ++;
				cols.push('<col width="'+column[i].getWidth()+'px;" ></col>');
			}
			tdtmp[1] = 'colspan="'+colspan+'" ';
			tdtmp[4] = (depth == 0 ) ? 'style="'+height+'"' : 'style="'+style+'" ';
			tdtmp[10] = cols.join('');

			$td = jQuery(tdtmp.join(''));
			
			parent.append(jQuery('<tr style="height:'+this._datagrid.getRow('body',0).getHeight()+'px;"></tr>').append($td));
			return $td;
		},
		_updateRow : function(dsEvent){
			var idx = dsEvent.rowIdx,
				column = dsEvent.column,
				pv = dsEvent.value,
				datagrid = this._datagrid;
			
			var target = datagrid.body.find('tr[cy=' + idx + ']:not(.editable)')
				, cells , cell
				, td, value, style;
			if (target.length == 0) {
				delete datagrid._rowCache[idx];
				return;
			}
			
			for ( var i = 0; i < datagrid._ast.body.length; i++) {
				cells = datagrid._ast.body[i].getCell();
				
				for ( var j = 0; j < cells.length; j++) {
					cell = cells[j];
					td = target.find('td.' + cell.getId());
					value = undefined;
					style = {};
					if (cell.getExpr()) {
						style = cell._doExpr(idx);
						value = style.value;
					}
					
					// group 일 때, border 추가 
					if( datagrid._ast['body'].length == 1 && cell.getGroupborderright() ){
						if( !style.cell ) style.cell = "border-right:"+cell.getGroupborderright()+';';
						else style.cell += "border-right:"+cell.getGroupborderright()+';';
					}
					
					if (value === undefined)
						value = cell._getValue(this._dataset, idx);
					
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
		},
		_bindMousedown : function(event){
			// mousedown
		},
		_bindMouseup : function(event){
			var tdNode = jQuery(event.target).closest('td', event.currentTarget);
			if (!tdNode[0] || tdNode.hasClass('editable') || tdNode.hasClass('editbyrow'))   //|| (!this.getRightselection() && event.which == 3) editable 일 때, return 시킴
				return;
			
			var $div = jQuery(event.target);
			if( $div.hasClass('xw-datagrid-display-checkbox-item') ){
				
				// display checkbox 수정 할 때 문제 
				var $tr = $div.closest('tr',event.currentTarget)
					, cy = $tr.attr('cy')
					, cx = $div.closest('td',event.currentTarget).attr('cx')
					, cell = this._datagrid.getCell('body',0,cx);
				if( cy && cell && cx ){
					var checked = $div.hasClass('xw-checkbox-checked');
					if( checked ){
						$div.addClass('xw-checkbox-unchecked').removeClass('xw-checkbox-checked');
						this._dataset.setValue(cy, cell.getBindcolumn(), cell._opt.falsevalue, this._datagrid);
					}else{
						$div.addClass('xw-checkbox-checked').removeClass('xw-checkbox-unchecked');
						this._dataset.setValue(cy, cell.getBindcolumn(), cell._opt.truevalue, this._datagrid);
					}
					this._datagrid.setSelection(cy);
				}
				this._datagrid._doDataMode();
			}else if($div.hasClass('xw-datagrid-group-text')){
				// group의 head 부분 클릭 할 때 실행 
				var $chd = $div.siblings('table');
				if($chd.hasClass('xw-datagrid-group-disabled')){
					$div.addClass('xw-datagrid-group-collapse').removeClass('xw-datagrid-group-expand');
					$chd.removeClass('xw-datagrid-group-disabled');
				}else{
					$div.addClass('xw-datagrid-group-expand').removeClass('xw-datagrid-group-collapse');
					$chd.addClass('xw-datagrid-group-disabled'); 
				}
				this._datagrid._doDataMode();
				this._doRowDataMode();
			}else if( tdNode.attr('cx') && this._iseditbyrow && this._celleditbyrow[tdNode.attr('cx')]){
				// rowEditMode check 해야 함.... 
				var $tr = $div.closest('tr',event.currentTarget)
				 	, cy = $tr.attr('cy')
				 	, cx = tdNode.attr('cx');
				
				this._doRowEditMode(tdNode, cy,cx);
				this._dataset.setCursor($tr.attr('cy'), this._datagrid);
				this._datagrid.setSelection($tr.attr('cy'));
			}else{
				var $tr = $div.closest('tr',event.currentTarget);
				if( $tr.attr('cy') ){
					this._doRowDataMode();
					this._dataset.setCursor($tr.attr('cy'), this._datagrid);
					this._datagrid.setSelection($tr.attr('cy'));
					
					if( !$div.children().hasClass('xw-datagrid-display-checkbox') && this._datagrid.getEditwhen() != 'none')
						this._datagrid._doEditMode($div.closest('td',event.currentTarget));
				}
			}
		},
		_bindKeydown : function(event){
			if( xwing.widget.DataGrid._CTRL_KEY_SET.indexOf(event.keyCode) == -1) return;
			
			event.preventDefault();
			event.stopPropagation();
			
			// 먼저 check부터 한다..... 무슨 체크? 당근 해당 위치가 보여지는지 아니면 아닌지... 등등? ㅋㅋㅋ 
			var that = this;
			(function(keyCode) {
				that._dTimeout = setTimeout(function() {
					that._doNaviDataMode(keyCode);
					that._dTimeout = null;
				}, 10);
			})(event.keyCode);
			
		},
		_doNaviDataMode : function(keyCode){
			var dsObj = this._dataset
				, cursor = dsObj.getCursor()
				, $tr = jQuery('tr[cy='+cursor+']',this._datagrid.body_tbody);
			
			if(cursor < 0 || !$tr.hasClass('select') || $tr.parents('table.xw-datagrid-group-disabled').length != 0) 
				return false;
			
			switch(keyCode){
			case Xwing.key.DOWN_ARROW:
				var next = $tr.next();
				if(next.length != 0 ){
					dsObj.setCursor(next.attr('cy'), this._datagrid);
					this._datagrid.setSelection(next.attr('cy'));
				}
				break;
			case Xwing.key.UP_ARROW:
				var prev = $tr.prev();
				if(prev.length != 0 ){
					dsObj.setCursor(prev.attr('cy'), this._datagrid);
					this._datagrid.setSelection(prev.attr('cy'));
				}
				break;
			}
		},
		_doAutofit : function(widths) {
			var colgroups = this._datagrid.body_tbody.find('colgroup');
			colgroups.each(function(idx, ui){
				jQuery(ui).find('col').each(function(idx2,col){
					col.setAttribute('width',widths[idx2]);
				});
			});
			this._datagrid.body_tbody.find('div.xw-datagrid-group-text').width(this._allwidth+'px');
		},
		_doDataMode : function(){
			// editpart가 존재 할 경우 값 변경하기 
		},
		_doEditMode : function(td) {
//			console.log('edit mode')
			// editpart가 존재 할 경우 바꾸기 
			if(this._isEditMode() || this._datagrid.body.find('.empty').length != 0 ) return;
			var target = jQuery(td).closest('tr'),
				that = this._datagrid,
				editpart = that.body_table._editpart,
				cy = target.attr('cy');
			target = that.body.find('tr[cy=' + cy + ']:not(.editable)');
			for(var i=0; i < target.length ; i++){
				var height = target[i].clientHeight;
				jQuery(editpart[i]).height(height).find('td>div.xw-shell').each(function(i, e) {
					Xwing.getWidget(e.getAttribute('_xid')).setHeight(height - 1);
				});
			}
			
			editpart.$fixedTrs.insertBefore(target.filter('tr.fixed')[0]);
			editpart.$trs.insertBefore(target.filter('tr:not(.fixed)')[0]);
//			if (this._datagrid.getCheckbox()) {
//				var span = target.find('span.xw-datagrid-cell-checkbox');
//				editpart.$fixedTrs.find('span.xw-datagrid-cell-checkbox').attr('value', span.attr('value')).
//					css('background-position', span.css('background-position'));
//			}
			
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
					
					// display checkbox 이것 땜에.. 하하하하하ㅏ 
//					if( $td.children('span').hasClass('xw-datagrid-display-checkbox') ){
//						$td.children('span')[0].className = target.find('td[cx='+$td.attr('cx')+']').children()[0].className;
//					}
				}
			});
			
			// TODO
			//this._selectRow(cy, false);
			this._datagrid._selectedLastColIdx = (jQuery(td).attr('cx') ? jQuery(td).attr('cx') : this._datagrid._selectedLastColIdx);
			(this._datagrid.getSelectedrange() == 'row') && this._datagrid._selectRow(cy, false);
			
			if (jQuery.browser.webkit && this._datagrid._one === undefined) {
				editpart.css('display', 'none');
				setTimeout(function() { 
					editpart.css('display', ''); 
				}, 0);
				
				this._one = true;
			} 
			setTimeout(function() { target.hide(); }, 0);
			var id = this._datagrid._getIdByClass(td, 'body');
			id = editpart.find('td.' + id).children('div.xw-shell').attr('id');
			if (id) {
				var widget = Xwing.getWidget(id);
				widget && widget.focus && widget.focus();
			}
		},
		_isEditMode : function() {
			return this._datagrid.body_table._editpart && this._datagrid.body_table._editpart.is(":visible");
		},
		_isRowEditMode : function() {
			return this._datagrid.body_area.find('td.editbyrow') && this._datagrid.body_area.find('td.editbyrow').is(":visible");
		},
		_doRowDataMode : function(){
			var td = jQuery('td.editbyrow',this._datagrid.body_table);
			var that = this._datagrid;
			
			that._blurEditMode();
			if( td.length != 0 ){
				td.each(function(i, el){
					var cx = el.getAttribute('cx');
					if( jQuery(el).css('display') != 'none'){
						var widget = Xwing.getWidget(jQuery(el).children().attr('id'));
						if( widget && widget.getAlias() == 'combo' && widget.getMultiselectable() ){
							var ds = Xwing.getDataset(widget.getBinddataset())
								, colIdx= ds.getColumnIndex(widget.getBindcolumn());
							
							ds._rows[td.parent().attr('cy')][colIdx] = widget._opt.value;
							ds._phys._rows[td.parent().attr('cy')][colIdx] = widget._opt.value;
							jQuery('td[cx='+cx+']:not(.editbyrow) > span',jQuery(el).parent()).text(widget._opt.value);
						}else if ( widget && widget.getAlias() == 'spin' ){
							jQuery('td[cx='+cx+']:not(.editbyrow) > span',jQuery(el).parent()).text(widget._opt.value);
						}
						
						jQuery('td[cx='+cx+']:not(.editbyrow)',that.body_area).show();
						jQuery(el).hide();
						jQuery(el).insertAfter(that.body_table);
					}
				});
			}
		},
		_doRowEditMode : function(td, cy,cx){
			var cellObj = this._celleditbyrow[cx];
			if( td.hasClass('editbyrow') ) return;
			this._doRowDataMode();
			
			var width = td.width();
			var obj = cellObj._doChangeedit(cy);
			if( obj && obj.type ){
				if( !cellObj._isCreateRowEdit(obj.type) ){
					var newTd = jQuery('<td class="'+td[0].className+' editbyrow" cx="'+cx+'" />');
					newTd.append(cellObj._createRowEdit(obj.type, obj.opt));
					cellObj._grouprowedit[obj.type] = newTd[0];
				}else{
					var cls = (cellObj._grouprowedit[obj.type].className || '').split(' ');//datagrid0-b-r0c2_rowedit_edit
					var widget = Xwing.getWidget(cls[0]+"_rowedit"+'_'+obj.type);
					if( widget && obj.opt){
						for(var i in obj.opt){
							if( widget.getAttribute(i) != obj.opt[i] ) widget.setAttribute(i,obj.opt[i]);
						}
					}
				}
				
				jQuery(cellObj._grouprowedit[obj.type]).insertBefore(td.hide());
				jQuery(cellObj._grouprowedit[obj.type]).show();
				
				var cls = (cellObj._grouprowedit[obj.type].className || '').split(' ');//datagrid0-b-r0c2_rowedit_edit
				var id = cls[0]+"_rowedit"+'_'+obj.type;
				if (id) {
					var widget = Xwing.getWidget(id);
					if( widget && (widget.getAlias() == 'combo' || widget.getAlias() == 'spin') ) widget.setWidth(width);
					else if( widget ) widget._getJShell().css('width','100%');
					widget && widget.focus && widget.focus();
				}
			}
		}
};