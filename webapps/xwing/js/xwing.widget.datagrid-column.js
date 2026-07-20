Class.define({
	DataGridColumn : {
		alias : "datagrid-column",
		namespace : "xwing.widget",
		DataGridColumn : function(json, obj) {
			this._datagrid = obj;
			this._init(json);
		},
		statics : {

		},
		prototypes : {
			_init : function(json) {
				this.model = Class.getClass(this.getAlias()).getModel();
				this._opt = {};
				this._columns = [];
				this._zerowidth = [];
				this._attr = {};

				jQuery.extend(this._opt, json || {});
				for ( var key in this.model.attributes) {
					this._opt[key] == undefined && (this._opt[key] = this.model.attributes[key].defaultValue);
				}

				var methods = [ 'hasAttribute', 'setAttribute', 'getAttribute', 'removeAttribute', 'getAttributes', '_getDefault' ];
				for ( var i = 0, l = methods.length; i < l; i++) {
					this[methods[i]] = xwing.widget.Widget.prototype[methods[i]];
				}
			},
			addColumn : function(v) {
				this._columns.push(v);
			},
			hasColumn : function(v) {
				for ( var i = 0, l = this._columns.length; i < l; i++) {
					if (this._columns[i].is(v))
						return true;
				}
				return false;
			},
			getColumn : function() {
				return this._columns;
			},
			getAlias : function() {
				return this.alias;
			},
			setWidth : function(v) {
				this._opt.width = v;
				this._doWidth();
			},
			getWidth : function() {
				return xwing.Util.parseInt(this._opt.width, 0);
			},
			_doWidth : function() {
				for ( var i = 0, l = this._columns.length, w = xwing.Util.parseInt(this._opt.width); i < l; i++) {
					var col = this._columns[i];
					col.attr('width', w + 'px');
					if( i == 1 ) continue;
					if( w == 0 && (col.css('display') != 'none')){
						col.css('display','none');
						var cursor = col.prevAll().length
						, trs =  col.parents('table:first').find('tr')
						, that = this;
						if( col.parents('table:first').parent().parent().hasClass('xw-datagrid-'+(i == 0 ? 'header' : 'summary')+'-area')){
							cursor += this._datagrid[(i == 0 ? 'head' : 'summ' ) +'_fix_colgroup'].children('col:not(.xw-datagrid-rownum, .xw-datagrid-checkbox)').length;
						}
						trs.each(function(trI, tr){
							var jTr = jQuery(tr)
							, jTds = jTr.children(':not(.xw-datagrid-rownum, .xw-datagrid-checkbox)')
							, idx = cursor
							, jTd = jTds.filter('[cx='+cursor+']')
							, colspan;
							
							if( jTd.length == 0 ){
								while(true){
									idx++;
									if( jTds.filter('[cx='+idx+']').length != 0 ){
										jTd = jTds.filter('[cx='+idx+']');
										break;
									}
									if( idx >= that._columns[i].parents('table:first').find('col:not(.xw-datagrid-rownum, .xw-datagrid-checkbox)').length) break;
								}
								if( !jTd.attr('colspan') || jTd.attr('colspan') == '0') return;
							}
							colspan = !jTd.attr('colspan') ? 1 : parseInt(jTd.attr('colspan'));
							jTd[0] && that._zerowidth.push(jTd[0]);
							if( colspan != 0 ){
								jTd.attr('colspan',colspan - 1);
								if( colspan == 1 ) jTd.css('display','none');
							}
						});
					}else if( w != 0 && this._columns[i].css('display') == 'none'){
						this._columns[i].css('display','');
						for(var j=0, k = this._zerowidth.length; j < k ; j++){
							var td = this._zerowidth.pop();
							var colspan = ( !td.getAttribute('colspan') ? 0 : parseInt(td.getAttribute('colspan')));
							td.setAttribute('colspan', colspan + 1 );
							td.style.display = '';
						}
					}
				}

				this._datagrid._fitColumnWidth();
				if( parseFloat(jQuery.browser.version) <= 8 && jQuery.browser.msie)
					this._datagrid._fitFixColumnWidth();
			},
			setFixed : function(v) {
				this._opt.fixed = v;
				this._doFixed();
			},
			getFixed : function() {
				return xwing.Util.parseBoolean(this._opt.fixed);
			},
			_doFixed : function(){
				this._datagrid._remakeDataGrid(this._datagrid._ast,this._datagrid._getFixedColIndex());
				this._datagrid._doBounds();
				this._datagrid._resetBodyPart();
			},
			getXId : function() {
				return this._opt._xid;
			}
		}
	}
});
