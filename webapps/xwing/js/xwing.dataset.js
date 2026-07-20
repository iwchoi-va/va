Class.define({
	Dataset :{
		alias: 'dataset',
		namespace : 'xwing',
		Dataset : function(json, column, record){
			if (!arguments.length)
				return;

			this._opt = {};
			
			if (arguments.length == 1 && xwing.Util.is(json, 'object')) {
				(column = json['column']) && (delete json['column']);
				(record = json['record']) && (delete json['record']);
				jQuery.extend(this._opt, json);
			} else {
				this._opt.id = json;
			}

			this.id = this._opt.id;

			if (!this.id)
				throw new Error("To create Dataset, id is needed.");

			this._cols = column || [];
			this._rows = record || [];
			this._rowsRemoved = [];
			
			// TODO sora 1. Make variable in order that it make the Logical Dataset.
			this._phys = {};
			this._phys._cols = column ? column.concat([]) : [];
			this._phys._rows = record ? record.concat([]) : [];
			this._phys._rowsRemoved = [];
			
			this._cursor = this._rows.length > 0 ? 0 : -1;			
			this._listeners = [];
			
			this.model = Class.getClass(this.getAlias()).getModel();
			
			this._initOption();
			this._initEvent();
			
			Xwing._addDataset(this.id, this);			
			/*debug Xwing.debug("DataSet:"+ this.id +" create"); */
		},
		statics : {
			_FILTER_PTN : /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=|\S?>|\S?<)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/,
			create : function(json){
				return new xwing.Dataset(json);
			},
			parse : function(xNode){
				var opts = xwing.widget.Widget._parseXJson(xNode);
				if (!opts['id'])
					return;
				opts['column'] = xwing.Dataset._parseColumn(xNode);
				opts['record'] = xwing.Dataset._parseRow(xNode);
				
				new xwing.Dataset(opts);

				jQuery(xNode).remove();
			},
			_parseColumn : function(xNode) {
				var cols = jQuery("xwing|dataset-column", xNode);
				var colArray = [];
				
				cols.each(function() {
					colArray.push(jQuery(this).attr("id"));
				});
				
				return colArray;
			},
			_parseRow : function(xNode) {
				var rows = jQuery("xwing|dataset-row", xNode);
				var rowArray = [];

				rows.each(function() {
					var cells = jQuery("xwing|dataset-cell", this);
					var cellArray = [];
					cells.each(function() {
						cellArray.push(jQuery(this).text());
					});
					rowArray.push(cellArray);
				});
				
				return rowArray;
			}
		},
		prototypes : {
			_compareToValue : function(type, value, check){
				return type === '==' ? value === check :
					type === '*=' ? value.indexOf(check) >= 0 :
					type === '~=' ? (" " + value + " ").indexOf(check) >= 0 :
					type === '!=' ? value !== check :
					type === '^=' ? value.indexOf(check) === 0 :
					type === '$=' ? value.substr(value.length - check.length) === check : 
					type === '>'  ? xwing.Util.parseInt(value, -1) > xwing.Util.parseInt(check,false) : 
					type === '<'  ? xwing.Util.parseInt(value, -1) < xwing.Util.parseInt(check,false):
					type === '>=' ? xwing.Util.parseInt(value, -1) >= xwing.Util.parseInt(check, false):
					type === '<=' ? xwing.Util.parseInt(value, -1) <= xwing.Util.parseInt(check,false) : false;
			},
			_createPattern : function(filter){
				if (!filter || xwing.Util.is(filter, 'function'))
					return false;
				
				filter = String(filter).trim();
				var row = [], state;
				
				if (filter.charAt(0) == ':') {
					if ((state = filter.charAt(1).toUpperCase()) == 'D') {
						for ( var i = 0, l = this._rowsRemoved.length; i < l; i++) {
							row.push(this._rowsRemoved[i].concat([]));
						}
					} else {
						for ( var i = 0, l = this._rows.length; i < l; i++) {
							(state == '[' || this._rows[i]._ST == state)
													&& row.push(this._rows[i].concat([]));
						}
					}
					filter = (state == '[') ? filter.substr(1) : filter.substr(2);
					if (filter.length == 0) return {
														row : row,
														pattern : -1
													};
				} else {
					row = this._rows || [];
				}
				var ptnExpr = xwing.Dataset._FILTER_PTN;
				var pattern = [], v;
				while(v = filter.match(ptnExpr)) {
					pattern.push(v);
				    filter = filter.replace(ptnExpr, '');
				}
				
				return {
					row : row,
					pattern : pattern
				}
			},
			_initOption : function() {
				for ( var key in this.model.attributes) {
					this._opt[key] == undefined && (this._opt[key] = this.model.attributes[key].defaultValue);
				}
			},
			_initEvent : function() {
				for ( var i = 0, l = this.model.events.length; i < l; i++) {
					var eventName = this.model.events[i];
					this._opt[eventName] && this.bind(eventName, this._opt[eventName], this);
				}
			},		
			setEnabled : function(flag) {
				this._opt.enabled = flag;
				this._doEnabled();
			},
			_doEnabled : function() {
				this._fireAll(new xwing.DatasetEvent(this.id, 'reset'));
			},
			getEnabled : function() {
				return xwing.Util.parseBoolean(this._opt.enabled);
			},
			getAlias : function(){
				return this.alias;
			},			
			getId : function(){
				return this.id;	
			},
			setData : function(){
				if (arguments.length == 2) {
					var columns = arguments[0].concat([]);
					var rows = arguments[1].concat([]);

					this._rows = rows;
					this._cols = columns;
					this._cursor = this._rows.length > 0 ? 0 : -1;
					
					//TODO sora. Set Physical Dataset.
					this._phys._rows = rows.concat([]);
					this._phys._cols = columns.concat([]);
					this._fireAll(new xwing.DatasetEvent(this.id, 'reset'));
					
					return true;
				} else if (arguments.length == 1 && this._cols) {
					this._rows = arguments[0].concat([]);
					this._cursor = this._rows.length > 0 ? 0 : -1;
					
					//TODO sora. Set Physical Dataset.
					this._phys._rows = arguments[0].concat([]);
					this._fireAll(new xwing.DatasetEvent(this.id, 'reset'));
					
					return true;
				}
				
				return false;
			},
			getData : function(filter) {
				var patrn = this._createPattern(filter);
				if (!patrn){
					if (xwing.Util.is(filter, 'function')) 
						return jQuery.grep(this._rows, filter);
					else 
						return this._rows || [];
				}
				
				var row = patrn.row
					, pattern = patrn.pattern, that = this;
				
				for ( var i = 0, l = pattern.length; i < l; i++) {
					var idx = this._cols.indexOf(pattern[i][1]);
					if (idx == -1) continue;
					
					var type = pattern[i][2], 
						check = pattern[i][4],
						value;
					
					row = jQuery.grep(row, function(v) {
						value = String(v[idx]);
						return that._compareToValue(type, value, check);
					});
				}
				
				return row;
			}, 
			getPhysicalData : function(filter) {
				var patrn = this._createPattern(filter);
				if (!patrn){
					if (xwing.Util.is(filter, 'function')) 
						return jQuery.grep(this._phys._rows, filter);
					else 
						return this._phys._rows || [];
				}
				
				var row = patrn.row
					, pattern = patrn.pattern, that = this;
				
				for ( var i = 0, l = pattern.length; i < l; i++) {
					var idx = this._cols.indexOf(pattern[i][1]);
					if (idx == -1) continue;
					
					var type = pattern[i][2], 
						check = pattern[i][4],
						value;
					
					row = jQuery.grep(row, function(v) {
						value = String(v[idx]);
						return that._compareToValue(type, value, check);
					});
				}
				
				return row;
			}, 
			searchRow : function (filter){
				// Row Index... return;
				var patrn = this._createPattern(filter);
				if (!patrn){
					if (xwing.Util.is(filter, 'function')) 
						return jQuery.grep(this._rows, filter);
					else 
						return this._rows || [];
				}
				
				var row = patrn.row
					, pattern = patrn.pattern
					, rowIdx = [], that = this;
				
				jQuery.grep(row, function(v, i) {
					var push = true;
					for ( var j = 0, l = pattern.length; j < l; j++){
						var idx = that._cols.indexOf(pattern[j][1]);
						if (idx == -1) continue;
						
						var type = pattern[j][2], 
							check = pattern[j][4],
							value = String(v[idx]);
						
						result = that._compareToValue(type, value, check);
						if( !result ){
							push = false;
							break;
						}
					} 
					
					push && rowIdx.push(i);
				});
				return rowIdx;
			},
			setColumnInfo : function(colInfo) {
				if (colInfo && colInfo instanceof Array) {
					this._cols = colInfo;
					
					//TODO sora. Set physical cols.
					this._phys._cols = colInfo.concat([]);
					
					this._fireAll(new xwing.DatasetEvent(this.id, 'reset'));
					return true;
				}

				return false;
			},
			getColumnInfo : function(){
				return this._cols;
			},
			getColumnIndex : function(columnName){
				var colIdx = -1;
				if(typeof columnName == 'string'){
					for( var i=0; i < this._cols.length ; i++){
						if(this._cols[i] == columnName){
							colIdx = i;
							break;
						}
					}
				}else if(this.size() > colummName ) colIdx = columnName;
				return colIdx;
			},
			clear : function(){
				this._rows = new Array();
				this._cols = new Array();
				this._cursor = -1;					
				this._fireAll(new xwing.DatasetEvent(this.id, 'reset', this._rows.length));	
				
				//TODO sora. Clear physical Dataset
				this._phys._rows = [];
				this._phys._cols = [];
			},
			clearData : function(){
				this._rows = new Array();
				this._cursor = -1;			
				this._fireAll(new xwing.DatasetEvent(this.id, 'reset', this._rows.length));
				
				//TODO sora. Clear physical Row;
				this._phys._rows = [];
			},
			copyFrom : function(srcDs){
				if (srcDs == undefined)
					throw new Error("Dataset.copyFrom():Must specify dataset object or ID");
				
				if (typeof (srcDs) == "string") {
					if (Xwing.getDataset(srcDs)) {
						srcDs = Xwing.getDataset(srcDs);
					} else {
						throw new Error("Dataset.copyFrom():Can not be found specified dataset(" + srcDs + ")");
					}
				}
				if (srcDs._cols && srcDs._rows) {
					var rows = new Array();
					var rowlist = srcDs._rows.concat([]);
					for ( var i = 0; i < rowlist.length; i++) {
						rows.push(srcDs._rows[i].concat([]));
					}					
					this.setData(srcDs._cols.concat([]), rows);
				} else {
					throw new Error("");
				}
			},
			clone : function(targetDs){
				if (targetDs == undefined)
					throw new Error("Dataset.clone():Must specify dataset object or ID for creating clone");

				if (typeof (targetDs) == "string") {
					targetDs = Xwing.getDataset(targetDs) || (new xwing.Dataset(targetDs));
				}
				if (this._cols && this._rows) {
					var rows = new Array();
					var rowlist = this._rows.concat([]);
					for ( var i = 0; i < rowlist.length; i++) {
						rows.push(this._rows[i].concat([]));
					}
					targetDs.setData(this._cols.concat([]), rows);
				}

				return targetDs;		
			},
			appendDataset : function(srcDs, from , to){
				if (srcDs == undefined)
					throw new Error("Dataset.appendDataset():Must specify dataset object or ID for creating clone");

				if (typeof (srcDs) == "string") {
					if (Xwing.getDataset(srcDs)) {
						srcDs = Xwing.getDataset(srcDs);
					} else {
						throw new Error("Dataset.copyFrom():Can not be found specified dataset(" + srcDs + ")");
					}
				}
				
				var stIdx = 0;
				var enIdx = srcDs.size();
				if( from != undefined  && !isNaN(from) &&  parseInt(from) > 0 )
					stIdx = parseInt(from);
				if( to != undefined  && !isNaN(to)  &&  parseInt(to) < srcDs.size() )
					enIdx = parseInt(to)+1;
				
				if (srcDs._cols && srcDs._rows) {
					var rows = this._phys._rows.concat([]);
					var srcRows = srcDs._rows.concat([]);
					for( var i = stIdx; i < enIdx ; i++){
						rows.push(srcRows[i].concat([]));
					}
					this.setData(srcDs._cols.concat([]), rows);
				}
			},
			copyRowFrom : function(targetRowIdx, srcDs, srcRowIdx){
				if (typeof (srcDs) == "string") {
					if (Xwing.getDataset(srcDs))
						srcDs = Xwing.getDataset(srcDs);
					else
						throw new Error("Dataset.copyRowFrom():Can not be found specified srcDs(" + srcDs + ").");
				}
				if (this.size() > targetRowIdx) {
					if (this._cols.length == 0)
						this.setColumnInfo(srcDs.getColumnInfo().concat([]));
					this.setRow(targetRowIdx, srcDs.getRow(srcRowIdx).concat([]));
				} else {
					throw new Error("Dataset.copyRowFrom():Target Dataset has no such targetRowIdx(" + targetRowIdx + ").");
				}
			},
			addRow : function(row){
				if (!this._cols.length)
					throw new Error("Dataset.addRow():This dataset has no ColumnInfo.");
				
				if (row == undefined) {
					row = new Array(this._cols.length);
					row._ST = 'C';
					this._rows.push(row);
					
					//TODO sora. Add Physical row
					this._phys._rows.push(new Array(this._phys._cols.length));
				} else if (xwing.Util.is(row, 'array')) {
					row._ST = 'C';
					this._rows.push(row);
					
					//TODO sora. Add Physical row
					this._phys._rows.push(row.concat([]));
				} else {
					throw new Error("Dataset.addRow():Invalid argument(must be Array)");
				}
				
				this._cursor = this._rows.length - 1;				
				this._fireAll(new xwing.DatasetEvent(this.id, 'add', this._rows.length - 1));
			},
			appendRow : function(rows){
				if (!this._cols.length)
					throw new Error("Dataset.addRow():This dataset has no ColumnInfo.");
				
				if (xwing.Util.is(rows, 'array') && xwing.Util.is(rows[0], 'array')) {
					rows = jQuery.grep(rows, function(row,i){
						row._ST = 'C';
						return row;
					});
					this._rows = this._rows.concat(rows);
					
					//TODO sora. Append Physical row
					this._phys._rows = this._rows.concat([]);
				}else {
					throw new Error("Dataset.addRow():Invalid argument(must be Array)");
				}
				this._cursor = this._rows.length - 1;				
				this._fireAll(new xwing.DatasetEvent(this.id, 'add', this._rows.length - 1));
			},
			setRow : function(idx, row) {
				if (!this._cols.length)
					throw new Error("Dataset.setRow():This dataset has no ColumnInfo.");
				else if (this.size() <= idx)
					throw new Error("Dataset.setRow():Index(" + idx + ") out of range");
				
				var oldValue = this._rows[idx];
				
				if (row == undefined) {
					this._rows[idx] = new Array(this._cols.length);
					
					// TODO sora. Set physical rows
					this._phys._rows[idx] = new Array(this._phys._cols.length);
				} else if (xwing.Util.is(row, 'array')) {
					this._rows[idx] = row;
					
					// TODO sora. Set physical rows
					this._phys._rows[idx] = row.concat([]);
				} else {
					throw new Error("Dataset.setRow():Argument row must be Array.");
				}
				
				this._cursor = idx;
				this._fireAll(new xwing.DatasetEvent(this.id, 'update', idx, null, null, oldValue));
			},
			insertRow : function(idx, row, frontObj){
				if (!this._cols.length)
					throw new Error("Dataset.insertRow():This dataset has no ColumnInfo.");
				else if (this.size() <= idx)
					throw new Error("Dataset.insertRow():Index(" + idx + ") out of range");
				
				if (row == undefined) {
					this._rows.splice(idx, 0, new Array(this._cols.length));
					
					// TODO sora. Insert physical rows
					this._phys._rows.splice(idx, 0, new Array(this._cols.length));
				} else if (xwing.Util.is(row, 'array')) {
					// sora. multirow insert
					if( xwing.Util.is(row[0], 'array')){
						var front = this._rows.slice(0,idx+1)
							, back = this._rows.slice(idx+1, this._rows.length);
						
						this._rows = front.concat(row.concat(back));
						this._phys._rows = this._rows.concat([]);
					}else{
						this._rows.splice(idx, 0, row);
						
						// TODO sora. Insert physical rows
						this._phys._rows.splice(idx, 0, row.concat([]));
					}
				} else {
					throw new Error("Dataset.insertRow():Argument row must be Array");
				}
				
				this._cursor = idx;
				this._fireAll(new xwing.DatasetEvent(this.id, 'add', idx, null, frontObj));
			},
			hasColumn : function(colName){
				for ( var i in this._cols) {
					if (colName == this._cols[i])
						return true;
				}
				return false;
			},
			getRow : function(idx){
				if (this.size() > idx) {
					return this._rows[idx];
				} else {
					throw new Error("Dataset.getRow():Index(" + idx + ") out of range.");
				}
			},
			removeRow : function(idx, fromObj){
				if (idx < 0 || idx >= this._rows.length) {
					throw new Error("Dataset.removeRow():Index(" + idx + ") out of range.");
				}
				
				var tempRow = this._rows[idx];					
				this._rowsRemoved.push(this._rows.splice(idx, 1)[0]);
				
				this._cursor = idx;
				
				// TODO sora. Insert physical rows
				this._phys._rowsRemoved.push(this._phys._rows.splice(idx, 1)[0]);
				
				if (this._cursor >= this.size()) this._cursor = this.size() - 1;
				else if (this.size() == 0) this._cursor = -1;

				this._fireAll(new xwing.DatasetEvent(this.id, 'remove', idx, null, fromObj));					
				return tempRow;
			},
			getValue : function(){
				var rowIdx = this._cursor;
				var colInfo = "";
				var result = null;
				
				if (!arguments || arguments.length == 0) {
					throw new Error("Dataset.getValue():Invalid arguments.");
				} else if (arguments.length == 1) {
					colInfo = arguments[0];
					rowIdx = this._cursor;
				} else if (arguments.length == 2) {
					rowIdx = arguments[0];
					colInfo = arguments[1];
				}
				
				if (rowIdx >= 0 && this._rows[rowIdx]) {
					if (typeof (colInfo) == "number") {
						result = this._rows[rowIdx][colInfo];
					} else if (typeof (colInfo) == "string") {
						colInfo = jQuery.trim(colInfo);
						var colIdx;
						for ( var i = 0; i < this._cols.length; i++) {
							if (this._cols[i] == colInfo) {
								colIdx = i;
								break;
							}
						}
						result = this._rows[rowIdx][colIdx];
					}
				}
				
				if (typeof result == 'undefined')
					result = "";
				return result;
			},
			indexOfRow : function(columnName, columnValue){
				var idx = -1;
				if (this._cols.length == 0 || this.size() == 0)
					return idx;
				
				var colIdx = 0;
				for ( var i = 0; i < this._cols.length; i++) {
					if (this._cols[i] == columnName) {
						colIdx = i;
						break;
					}
				}
				for ( var i = 0; i < this._rows.length; i++) {
					if (this._rows[i][colIdx] == columnValue) {
						idx = i;
						break;
					}
				}
				return idx;
			},
			lookUp : function(columnName, columnValue, lookupColumnName) {
				var idx = this.indexOfRow(columnName, columnValue);
				if (idx != -1)
					return this.getValue(idx, lookupColumnName);
				return "";
			},
			setCursor : function(idx, fromObj) {
				if (this.size() <= idx) 
					throw new Error("Dataset.setCursor():Index(" + idx + ") out of range.");
				
				this._cursor = +idx;
				this._fireAll(new xwing.DatasetEvent(this.id, 'cursor', this._cursor, null, fromObj));
				return this._cursor;
			},
			getCursor : function(){
				return this._cursor;	
			},
			setValue : function(){
				var rowIdx = this._cursor;
				var colInfo = "";
				var value = null;
				var fromObj = null;
				var oldValue;
				
				if (!arguments || arguments.length < 2) {
					throw new Error("Dataset.setValue():Invalid arguments.");
				} else if (arguments.length == 2) {
					rowIdx = this._cursor;
					colInfo = arguments[0];
					value = arguments[1];
				} else if (arguments.length == 3) {
					if (typeof (arguments[2]) == "object" && arguments[2] instanceof xwing.widget.Widget) {
						rowIdx = this._cursor;
						colInfo = arguments[0];
						value = arguments[1];
						fromObj = arguments[2];
					} else {
						rowIdx = arguments[0];
						colInfo = arguments[1];
						value = arguments[2];
					}
				} else if (arguments.length == 4) {
					rowIdx = arguments[0];
					colInfo = arguments[1];
					value = arguments[2];
					fromObj = arguments[3];
				}

				if (xwing.Util.is(colInfo, "string")) {
					var colIdx;
					for ( var i in this._cols) {
						if (this._cols[i] == colInfo) {
							colIdx = i;
						}
					}
					oldValue = this._rows[rowIdx][colIdx];
					this._rows[rowIdx][colIdx] = value;
					
					// TODO sora. set value
					this._phys._rows[rowIdx][colIdx] = value;
				} else if (xwing.Util.is(colInfo, "number")) {
					oldValue = this._rows[rowIdx][colInfo];
					this._rows[rowIdx][colInfo] = value;
					
					// TODO sora. set value
					this._phys._rows[rowIdx][colInfo] = value;
				}
				
				this._fireAll(new xwing.DatasetEvent(this.id, 'update', rowIdx, colInfo, fromObj, oldValue));
			},
			bind : function(type, func, obj) {
				var aNew = {
					type : type,
					func : func,
					obj : obj
				};
				for ( var i = 0; i < this._listeners.length; i++) {
					var temp = this._listeners[i];
					if (temp.type == aNew.type && temp.func == aNew.func && temp.obj == aNew.obj)
						return;
				}
				this._listeners.push(aNew);
			},
			unbind : function(type, func, obj) {
				var temp = null;
				for ( var i = 0; i < this._listeners.length; i++) {
					temp = this._listeners[i];
					if (temp.type == type && temp.func == func && temp.obj == obj) {
						this._listeners.splice(i, 1);
						break;
					}
				}
				return temp;
			},
			fire : function(type, rowIdx, columnName, source, value) {
				this._fireAll(new xwing.DatasetEvent(this.id, type, rowIdx, columnName, source, value));
			},
			_fire : function(target, evt) {
				try {
					var funcObj = target.func;
					if (funcObj) {
						(typeof (funcObj) != "function") && (funcObj = eval(funcObj));
						funcObj.call(target.obj || null, evt);
					}					
				} catch (e) {
					/*debug
					Xwing.debug("dataset event fire call error : (" + this.id + ") event type:" + target.type + "," + target.func + "\n" + e);
					 */
				}
			},
			_setDataState : function(evt) {
				if (evt.type == 'reset') {
					this._rowsRemoved = [];
					
					// TODO sora. set value
					this._phys._rowsRemoved = [];
				} else if (this._rows[evt.rowIdx]) {
					if (this._rows[evt.rowIdx]._ST == 'C') return;
					this._rows[evt.rowIdx]._ST = evt.type == 'add' ? 'C' : 'U';
					
					// TODO sora. set value
					this._phys._rows[evt.rowIdx]._ST = evt.type == 'add' ? 'C' : 'U';
				}
			},
			_fireAll : function(evt) {
				if (/^(add|update|reset)$/.test(evt.type)) {
					this._setDataState(evt);
				}
				
				if (this.getEnabled()) {
					for ( var i = 0, l = this._listeners.length; i < l; i++) {
						if (evt.source && evt.source == this._listeners[i].obj) {
							continue;
						}
						if (this._listeners[i].type == "ALL" || evt.type == this._listeners[i].type) {
							this._fire(this._listeners[i], evt);
						}
					}
				}
			},
			size : function() {
				return this._rows.length;
			},
			getDataString : function() {
				return '{' + 'column : ' + JSON.stringify(this._cols||[]) + ',' + 'record : ' + JSON.stringify(this._rows||[]) + '}';
				/*
				var str = "{";
				str += "column : [";
				for ( var i = 0; i < this._cols.length; i++) {
					str += "'" + this._cols[i] + "'";
					if (i < this._cols.length - 1)
						str += ", ";
				}
				str += "], record : [";
				for ( var i = 0; i < this._rows.length; i++) {
					str += '[';
					for ( var j = 0; j < this._rows[i].length; j++) {
						str += "'" + this._rows[i][j] + "'";
						if (j < this._rows[i].length - 1) {
							str += ", ";
						}
					}
					str += "]";
					if (i < this._rows.length - 1) {
						str += ", ";
					}
				}
				str += "] }";
				return str;*/
			},
			toString : function() {
				var str = "";
				str += "Dataset(" + this.id + "), ";
				str += "cols:[";
				for ( var i = 0; i < this._cols.length; i++) {
					str += this._cols[i];
					if (i < this._cols.length - 1) {
						str += ", ";
					}
				}
				str += "], rows:[";
				for ( var i = 0; i < this._rows.length; i++) {
					str += '[' + i + ':';
					for ( var j = 0; j < this._rows[i].length; j++) {
						str += this._rows[i][j];
						if (j < this._rows[i].length - 1) {
							str += ", ";
						}
					}
					str += "]";
					if (i < this._rows.length - 1) {
						str += ", ";
					}
				}
				str += "]";
				return str;
			},
			sort : function(col, desc, domainDSObj,domainCode,domainText){
				/*debug Xwing.debug("old cursor :"+this._cursor); */
				if (typeof (col) == "string") {
					col = jQuery.trim(col);
					for ( var i = 0; i < this._cols.length; i++) {
						if (this._cols[i] == col) {
							col = i;
							break;
						}
					}
				}
				/*debug Xwing.debug(col); */	
				if (typeof (col) != "number") {
					/*debug Xwing.debug('NAN'); */
					/*debug if(col <0 || col >= this._cols.length)  Xwing.debug('index out of bounds'); */
					return false;
				}
				var cursorRow = this._rows[this._cursor];
				if (desc) {
					this._rows.sort(function(a, b) {
						var pre = a[col];
						var next = b[col];
						if(domainDSObj && domainCode && domainText){
							if(typeof domainDSObj ==  "string") domainDSObj = Xwing.getDataset(domainDSObj);
							pre = domainDSObj.lookUp(domainCode,a[col],domainText);
							next = domainDSObj.lookUp(domainCode,b[col],domainText);
						}
						if( pre == undefined || pre == null ) pre = '';
						if( next == undefined || next == null ) next = '';
						
						if (!parseFloat(pre) || !parseFloat(next)) {
							return (pre > next) ? -1 : (pre < next) ? 1 : 0;
						} else{
							return ((parseFloat(pre) > parseFloat(next)) ? -1 : (parseFloat(pre) < parseFloat(next) ? 1 : 0));
						}
						
							
					});
				} else {
					this._rows.sort(function(a, b) {
						var pre = a[col];
						var next = b[col];
						if(domainDSObj && domainCode && domainText){
							if(typeof domainDSObj ==  "string") domainDSObj = Xwing.getDataset(domainDSObj);
							pre = domainDSObj.lookUp(domainCode,a[col],domainText);
							next = domainDSObj.lookUp(domainCode,b[col],domainText);
						}
						if( pre == undefined || pre == null ) pre = '';
						if( next == undefined || next == null ) next = '';
						
						if (!parseFloat(pre) || !parseFloat(next))
							return (pre < next) ? -1 : (pre > next) ? 1 : 0;
						else
							return ((parseFloat(pre) < parseFloat(next)) ? -1 : ((parseFloat(pre) > parseFloat(next)) ? 1 : 0));
					});
				}
				for ( var i = 0; i < this._rows.length; i++) {
					if (this._rows[i] === cursorRow) {
						this._cursor = i;
						break;
					}
				}
				
				this._phys._rows = this._rows.concat([]);
				/*debug Xwing.debug("new cursor:"+this._cursor); */
				this._fireAll(new xwing.DatasetEvent(this.id, 'sort', null, null, null));
				return true;
			},
			multiSort : function(cols, desc) {
				if (typeof (cols) == "object" || cols instanceof Array) {
					for ( var i = 0; i < cols.length; i++) {
						if (typeof (cols[i]) == "string") {
							var col = jQuery.trim(cols[i]);
							for ( var k = 0; k < this._cols.length; k++) {
								if (this._cols[k] == col) {
									cols[i] = k;
									break;
								}
							}
						}
					}
				}
				if (desc) {
					this._rows.sort(function(a, b) {
						for ( var i = 0; i < cols.length; i++) {
							var y = cols[i]
							, left = isNaN(a[y]) ? a[y] : parseFloat(a[y])
							, right = isNaN(b[y]) ? b[y] : parseFloat(b[y]);
							if (left == right)
								continue;
							else if (left > right) {
								return -1;
							} else if (left < right) {
								return 1;
							}
						}
						return 0;
					});
				} else {
					this._rows.sort(function(a, b) {
						for ( var i = 0; i < cols.length; i++) {
							var y = cols[i]
								, left = isNaN(a[y]) ? a[y] : parseFloat(a[y])
								, right = isNaN(b[y]) ? b[y] : parseFloat(b[y]);
							if (left == right)
								continue;
							else if (left < right) {
								return -1;
							} else if (left > right) {
								return 1;
							}
						}
						return 0;
					});
				}
				
				this._phys._rows = this._rows.concat([]);
				/*debug Xwing.debug("new cursor:"+this._cursor); */
				this._fireAll(new xwing.DatasetEvent(this.id, 'sort', null, null, null));
				return true;
			},
			min : function(col, filter){
				/*
				 *  Dataset에서 요청한 Column, Record 범위 내에서 값에 대한 최소값을 구하는 Method입니다.  
				 */
				var colIdx = this.getColumnIndex(col)
					, rows = this.getData(filter);
				if(rows.length == 0 || colIdx == -1) return;
				
				rows.sort(function(pre, next){
					var preval = pre[colIdx]
						, nextval = next[colIdx];
					if (isNaN(parseFloat(preval)) || isNaN(parseFloat(nextval))) 
						return (preval < nextval) ? -1 : (pre > nextval) ? 1 : 0;
					else 
						return ((parseFloat(preval) < parseFloat(nextval)) ? -1
								: ((parseFloat(preval) > parseFloat(nextval)) ? 1 : 0));
				});
				return rows[0][colIdx];
			},
			max : function(col, filter){
				/*
				 *  Dataset에서 요청한 Column, Record 범위 내에서 값에 대한 최대값을 구하는 Method입니다.  
				 */
				var colIdx = this.getColumnIndex(col)
				, rows = this.getData(filter);
				if(rows.length == 0 || colIdx == -1) return;
				
				rows.sort(function(pre, next){
					var preval = pre[colIdx]
						, nextval = next[colIdx];
					if (isNaN(parseFloat(preval)) || isNaN(parseFloat(nextval))) 
						return (preval > nextval) ? -1 : (pre < nextval) ? 1 : 0;
					else 
						return ((parseFloat(preval) > parseFloat(nextval)) ? -1
								: ((parseFloat(preval) < parseFloat(nextval)) ? 1 : 0));
				});
				return rows[0][colIdx];
			},
			sum : function(col, filter){
				var colIdx = this.getColumnIndex(col)
				, rows = this.getData(filter)
				, sum = 0, val;
				if(rows.length == 0 || colIdx == -1) return;
				
				jQuery.grep(rows,function(row, i){
					val = parseFloat(row[colIdx]);
					if(!isNaN(val)) sum += val;
				});
				return sum;
			},
			avg : function(col, filter){
				var colIdx = this.getColumnIndex(col)
				, rows = this.getData(filter)
				, sum = 0, val, cnt=0;
				if(rows.length == 0 || colIdx == -1) return;
				
				jQuery.grep(rows,function(row, i){
					val = parseFloat(row[colIdx]);
					if(!isNaN(val)){
						sum += val;
						cnt++;
					}
				});
				return sum/cnt;
			},
			filter : function(filter) {
				if (!filter) return ;
				this._rows = this._phys._rows.concat([]);
				if (xwing.Util.is(filter, 'function')) 
					this._rows = jQuery.grep(this._rows, filter);
				
				var patrn = this._createPattern(filter);
				var row = patrn.row
				, pattern = patrn.pattern
				, that = this;
				
				for ( var i = 0, l = pattern.length; i < l; i++) {
					var idx = this._cols.indexOf(pattern[i][1]);
					if (idx == -1) continue;
					
					var type = pattern[i][2], 
						check = pattern[i][4],
						value;
					
					row = jQuery.grep(row, function(v) {
						value = String(v[idx]);
						return that._compareToValue(type, value, check);
					});
				}
				
				this._rows = row;
				this._fireAll(new xwing.DatasetEvent(this.id, 'filter'));
			},
			unfilter : function(){
				this._rows = this._phys._rows.concat([]);
				
				this._fireAll(new xwing.DatasetEvent(this.id, 'filter'));
			},
			_groupsum : function(key, type, suppress){
				if( key.length == 0 || type.length == 0 || this._phys._rows.length == 0) return;
				var depth = key.length,
					result = [],
					calType , cal ;
				
					for( var ridx=0, l= this._phys._rows.length ; ridx <= l ; ridx++){
						var row
						, cnt = result.length;
						if( ridx != l ) row = this._phys._rows[ridx].concat([]);
						// group 찾으면서 계산 
						for( var gidx = 0; gidx < depth; gidx++ ){
							var newrow = null;
							// newRow 만들기 
							if( ridx != 0 ){
								var gcol = this.getColumnIndex(key[gidx].bindcolumn);
								if( gcol == -1) continue;
								if( (this._phys._rows[ridx - 1][gcol] != row[gcol]) || ridx == l) newrow = new Array(this._phys._cols.length);
							}
							
							// 현 grouping에 관한 cell 계산 
							for( var cidx = 0, l3 = type.length ; cidx < l3; cidx++ ){
								var ccol = this.getColumnIndex(type[cidx].bindcolumn);
								calType = type[cidx].type;
								if( ccol == -1) continue;
								if( ridx == 0 ){
									if( !type[cidx].cal )
										type[cidx].cal = new Array(depth);
									cal = type[cidx].cal;
									cal[gidx] =  parseFloat(row[ccol]);
									if( calType == 'avg' ) cal[gidx] = row[ccol]+":1";
									else if( calType == 'count' ) cal[gidx] = 1;
									else if( calType == 'text') cal[gidx] = '';
								}else{
									cal = type[cidx].cal;
									if( !newrow ){
										switch( calType ){
										case "avg":
											var acc = parseFloat(cal[gidx].split(":")[0])
												, count = parseInt(cal[gidx].split(":")[1]);
											cal[gidx] = (acc+ parseFloat(row[ccol]))+":"+(count+1);
											break;
										case "count":
											cal[gidx] += 1;
											break;
										case "sum":
											cal[gidx] += parseFloat(row[ccol]);
	//										console.log(ridx+":"+gidx+":"+cidx+":"+ccol+":"+cal[gidx]+":"+ parseFloat(row[ccol]))
											break;
										case "max":
											if( cal[gidx] < parseFloat(row[ccol])) cal[gidx] = parseFloat(row[ccol]);
											break;
										case "min":
											if( cal[gidx] > parseFloat(row[ccol])) cal[gidx] = parseFloat(row[ccol]);
											break;
										}
									}else{
										newrow[ccol] = cal[gidx];
										if( calType == 'avg'){
											var acc = parseFloat(cal[gidx].split(":")[0])
											, count = parseInt(cal[gidx].split(":")[1]);
											newrow[ccol] = parseFloat(acc/count);
										}
										
										cal[gidx] =  parseFloat(row[ccol]);
										if( calType == 'avg' ) cal[gidx] = row[ccol]+":1";
										else if( calType == 'count' ) cal[gidx] = 1;
										else if( calType == 'text') cal[gidx] = '';
									}
								}
							}
							if( newrow ){
								for( var g2idx = 0; g2idx < (key[gidx].depth+1); g2idx++){
									var g2col =  this.getColumnIndex(suppress[g2idx]);
									newrow[g2col] = this._phys._rows[ridx - 1][g2col];
								}
								newrow._GROUP = key[gidx].depth;
								result.splice(cnt, 0, newrow);
							}
						}
						if( ridx != l ) result.push(row);
				}
				
				this._rows = result;
			}
		}	
	}
});

/**
 * dataset id
 * type : {  'reset', 'add', 'remove', 'update', 'cursor', 'sort', 'filter' }
 */
Class.define({
	DatasetEvent : {
		namespace : "xwing",
		DatasetEvent : function(id, type, rowIdx, columnName, source, value) {
			this.id = id;
			this.type = type;
			this.rowIdx = rowIdx;
			this.column = columnName || null;
			this.source = source;
			this.value = value;
		},
		prototypes : {
			toString : function() {
				return "id : " + this.id + ", type : " + this.type + ", rowIdx : " + this.rowIdx + ", column :" + this.column + ", source :" + this.source;
			}
		}
	}
});
