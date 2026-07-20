/**
 * 데이터셋 컬럼값 추가.
 * 
 * @param sColumn
 * @returns
 */

//setTimeout
function loadDataset(){
xwing.Dataset.prototype.addColumn = function(sColumn) {
	this.setColumnInfo(this.getColumnInfo().concat(sColumn));
	return this.getColumnInfo();
};


/**
 * 데이터셋 컬럼값을 일괄적으로 변경.
 * 
 * @param sColumn
 * @param oValue
 * @returns {Boolean}
 */
xwing.Dataset.prototype.setColumnData = function(sColumn, oValue) {
	if (typeof oValue == "string") {
		for ( var i = 0; i < this.size(); i++) {
			this.setValue(i, sColumn, oValue);
		}
		return true;
	}

	if (this.size() > oValue.length) {
		Xwing.notify("value길이가 dataset길이보다 짧습니다.");
		return false;
	}

	for ( var i = 0; i < this.size(); i++) {
		this.setValue(i, sColumn, oValue[i]);
	}

	return true;
};

/**
 * 지정한 Dataset에서 지정한 Record 전체를 복사해 DataSet의 지정한 Record를 변경하는 Method 입니다. Column
 * 명이 서로 다를경우, 복사할 Column이 저장될 Column을 지정할 수 있습니다. copyToRow Method는 지정한
 * DataSet에서 값을 가져오지만, copyToRow Method는 지정한 DataSet으로 값을 내보냅니다.
 * 
 * @param nFromRow
 *            Integer Source Dataset의 복사 할 Record Index.
 * @param oDataset
 *            Object/String Target이 되는 dataset object/Dataset id string.
 * @param nToRow
 *            Integer Target Dataset의 복사되어 변경 될 Row Index.
 * @param strColInfo
 *            String 복사할 조건. 생략 가능합니다. 생략시에는 같은 필드명의 값을 복사합니다.
 *            ToColumnID=FromColumnID,ToColumnID1=FromColumnID1 형식으로 입력합니다.
 */
xwing.Dataset.prototype.copyRow = function(nToRow, oDataset, nFromRow, strColInfo) {
	if (typeof (oDataset) == "string") {
		oDataset = Xwing.getDataset(oDataset);
		if (_dataset_isNull(oDataset)) {
			throw new Error("Dataset.copyRow():This is not dataset");
		}
	}

	var _to_columns_join = this.getColumnInfo().join(",");
	var _from_columns = oDataset.getColumnInfo();
	var _to_columns = this.getColumnInfo();
	var _from_hash = new xwing.util.Hash();
	var _to_hash = new xwing.util.Hash();
	var _from_rows = oDataset.getRow(nFromRow);
	var _to_rows = [];

	for ( var i = 0; i < _from_columns.length; i++) {
		_from_hash.set(_to_columns[i], i);
		var _column = _from_columns[i];
		if (_to_columns_join.indexOf(_column) == -1) {
			_to_columns_join += "," + _column;
		}
	}
	this.setColumnInfo(_to_columns_join.split(","));

	_to_columns = this.getColumnInfo();
	for ( var i = 0; i < _to_columns.length; i++) {
		_to_hash.set(_to_columns[i], i);
	}

	for ( var i = 0; i < _from_columns.length; i++) {
		_to_rows[_to_hash.get(_from_columns[i])] = oDataset.getValue(nFromRow,
				_from_columns[i]);
	}
	if (!_dataset_isNull(strColInfo) && !_dataset_isEmpty(strColInfo)) {
		var _coninfos = strColInfo.split(",");
		for ( var i = 0; i < _coninfos.length; i++) {
			if (_coninfos[i].indexOf("=") == -1)
				continue;
			var _infos = _coninfos[i].split("=");
			for ( var j = 0; j < _infos.length; j++) {
				_to_rows[_to_hash.get(String(_infos[0]).trim())] = oDataset.getValue(nFromRow, String(_infos[1]).trim());
			}
		}
	}
	this.setRow(nToRow, _to_rows);	
};


/**
 * 지정한 Dataset에서 지정한 Record 전체를 복사해 DataSet의 지정한 Record를 변경하는 Method 입니다. Column
 * 명이 서로 다를경우, 복사할 Column이 저장될 Column을 지정할 수 있습니다. copyToRow Method는 지정한
 * DataSet에서 값을 가져오지만, copyToRow Method는 지정한 DataSet으로 값을 내보냅니다.
 * 
 * @param nFromRow
 *            Integer Source Dataset의 복사 할 Record Index.
 * @param oDataset
 *            Object/String Target이 되는 dataset object/Dataset id string.
 * @param nToRow
 *            Integer Target Dataset의 복사되어 변경 될 Row Index.
 * @param strColInfo
 *            String 복사할 조건. 생략 가능합니다. 생략시에는 같은 필드명의 값을 복사합니다.
 *            ToColumnID=FromColumnID,ToColumnID1=FromColumnID1 형식으로 입력합니다.
 */
xwing.Dataset.prototype.copyToRow = function(nFromRow, oDataset, nToRow, strColInfo) {
	if (typeof (oDataset) == "string") {
		oDataset = Xwing.getDataset(oDataset);
		if (_dataset_isNull(oDataset)) {
			throw new Error("Dataset.copyToRow():This is not dataset");
		}
	}

	var _to_columns_join = oDataset.getColumnInfo().join(",");
	var _from_columns = this.getColumnInfo();
	var _to_columns = oDataset.getColumnInfo();
	var _from_hash = new xwing.util.Hash();
	var _to_hash = new xwing.util.Hash();
	var _from_rows = this.getRow(nFromRow);
	var _to_rows = [];

	for ( var i = 0; i < _from_columns.length; i++) {
		_from_hash.set(_to_columns[i], i);
		var _column = _from_columns[i];
		if (_to_columns_join.indexOf(_column) == -1) {
			_to_columns_join += "," + _column;
		}
	}
	oDataset.setColumnInfo(_to_columns_join.split(","));

	_to_columns = oDataset.getColumnInfo();
	for ( var i = 0; i < _to_columns.length; i++) {
		_to_hash.set(_to_columns[i], i);
	}

	for ( var i = 0; i < _from_columns.length; i++) {
		_to_rows[_to_hash.get(_from_columns[i])] = this.getValue(nFromRow,
				_from_columns[i]);
	}
	if (!_dataset_isNull(strColInfo) && !_dataset_isEmpty(strColInfo)) {
		var _coninfos = strColInfo.split(",");
		for ( var i = 0; i < _coninfos.length; i++) {
			if (_coninfos[i].indexOf("=") == -1)
				continue;
			var _infos = _coninfos[i].split("=");
			for ( var j = 0; j < _infos.length; j++) {
				_to_rows[_to_hash.get(String(_infos[0]).trim())] = this.getValue(nFromRow, String(_infos[1]).trim());
			}
		}
	}
	oDataset.setRow(nToRow, _to_rows);	
};

/**
 * 
 * @param filter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다.             
 *
 * @returns
 */
xwing.Dataset.prototype.getData2 = function(filter) {
	if (!filter)
		return this._rows || [];
	if (xwing.Util.is(filter, 'function')) 
		return jQuery.grep(this._rows, filter);

	filter = String(filter).trim();
	var row = [], state;

	if (filter.charAt(0) == ':') {
		if ((state = filter.charAt(1).toUpperCase()) == 'D') {
			for ( var i = 0, l = this._rowsRemoved.length; i < l; i++) {
				row.push(this._rowsRemoved[i].concat([]));
			}
		} else {
			for ( var i = 0, l = this._rows.length; i < l; i++) {
				this._rows[i]._ST == state && row.push(this._rows[i].concat([]));
			}
		}
		filter = filter.substr(2);
		if (filter.length == 0) return row;
	} else {
		row = this._rows || [];
	}
	
	var pattern = [], ptnExpr, v;
	
	ptnExpr = xwing.Dataset._FILTER_PTN;
	while (v = filter.match(ptnExpr)) {
		pattern.push(v);
		filter = filter.replace(ptnExpr, '');
	}

	ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?>)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
	while (v = filter.match(ptnExpr)) {
		pattern.push(v);
		filter = filter.replace(ptnExpr, '');
	}

	ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?<)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
	while (v = filter.match(ptnExpr)) {
		pattern.push(v);
		filter = filter.replace(ptnExpr, '');
	}

	for ( var i = 0, l = pattern.length; i < l; i++) {
		var idx = this._cols.indexOf(pattern[i][1]);
		if (idx == -1) continue;
		var type = pattern[i][2], check = pattern[i][4], value;
		row = jQuery.grep( row,
				function(v) {
					value = String(v[idx]);
					return type === '==' ? value === check : 
						   type === '*=' ? value.indexOf(check) >= 0 : 
						   type === '~=' ? (" " + value + " ").indexOf(check) >= 0 : 
						   type === '!=' ? value !== check : 
						   type === '^=' ? value.indexOf(check) === 0 : 
						   type === '$=' ? value.substr(value.length - check.length) === check : 
						   type === '<=' ? parseFloat(value) <= parseFloat(check) :
						   type === '>=' ? parseFloat(value) >= parseFloat(check) :
						   type === '>>' ? parseFloat(value) > parseFloat(check) :
						   type === '<<' ? parseFloat(value) < parseFloat(check) :
						   false;
		});
	}
	
	return row;
}

/**
 * Dataset에서 요청한 Record 범위에 대해 Column ID의 값에 대한 최대값이나 레코드별 수식 수행 결과에 대한 최대값을 구하는
 * Method 입니다.
 * 
 * @param sColumn
 *            최대값을 구할 Column ID.
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다. 
 * @returns min value
 */
xwing.Dataset.prototype.min = function(sColumn, sFilter) {
	sColumn = String(sColumn).trim();
	sFilter = String(_dataset_nvl(sFilter, "")).trim();
	var _idx = this.getColIndex(sColumn);
	if (_idx == -1)
		return "";

	var __rows = this.getData2(sFilter);
	if (__rows.length == 0)
		return "";

	var _array = new Array();
	for ( var i = 0; i < __rows.length; i++) {
		_array[i] = __rows[i][_idx];
	}

	_array.sort(function(pre, next) {
		if (isNaN(parseFloat(pre)) || isNaN(parseFloat(next))) {
			return (pre < next) ? -1 : (pre > next) ? 1 : 0;
		} else {
			return ((parseFloat(pre) < parseFloat(next)) ? -1
					: ((parseFloat(pre) > parseFloat(next)) ? 1 : 0));
		}
	});

	return _array[0];
};

/**
 * Dataset에서 요청한 Record 범위에 대해 Column ID의 값에 대한 최소값이나 레코드별 수식 수행 결과에 대한 최소값을 구하는
 * Method 입니다.
 * 
 * @param sColumn
 *            최소값을 구할 Column ID.
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다. 
 * @returns max value
 */
xwing.Dataset.prototype.max = function(sColumn, sFilter) {
	sColumn = String(sColumn).trim();
	sFilter = String(_dataset_nvl(sFilter, "")).trim();
	var _idx = this.getColIndex(sColumn);
	if (_idx == -1) return "";

	var __rows = this.getData2(sFilter);
	if (__rows.length == 0)
		return "";

	var _array = new Array();
	for ( var i = 0; i < __rows.length; i++) {
		_array[i] = __rows[i][_idx];
	}

	_array.sort(function(pre, next) {
		if (isNaN(parseFloat(pre)) || isNaN(parseFloat(next))) {
			return (pre < next) ? -1 : (pre > next) ? 1 : 0;
		} else {
			return ((parseFloat(pre) < parseFloat(next)) ? -1
					: ((parseFloat(pre) > parseFloat(next)) ? 1 : 0));
		}
	});

	return _array[_array.length - 1];
};

/**
 * Dataset에서 요청한 Record 범위에 대해 Column ID의 값에 대한 합계나 Record 별 수식 수행 결과에 대한 합계를
 * 구하는 Method입니다.
 * 
 * @param sColumn
 *            합계를 구할 Column ID.
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다. 
 * @returns sum value
 */
xwing.Dataset.prototype.sum = function(sColumn, sFilter) {
	sColumn = String(sColumn).trim();
	sFilter = String(_dataset_nvl(sFilter, "")).trim();
	var _idx = this.getColIndex(sColumn);
	if (_idx == -1)
		return null;

	var __rows = this.getData2(sFilter);
	if (__rows.length == 0)
		return 0;
	var _sum = 0, _val;
	var _flag = false;
	for ( var i = 0; i < __rows.length; i++) {
		_val = __rows[i][_idx];
		if (isNaN(parseFloat(_val))) {
			_flag = true;
			break;
		}
		_sum += parseFloat(_val);
	}
	return _flag ? null : _sum;
};

/**
 * Dataset에서 요청한 Record 범위에 대해 Column ID의 값에 대한 평균값이나 Record 별 수식 수행 결과에 대한 평균값을
 * 구하는 Method 입니다.
 * 
 * @param sColumn
 *            평균값을 구할 Column ID.
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다. 
 * @returns avg value
 */
xwing.Dataset.prototype.avg = function(sColumn, sFilter) {
	sColumn = String(sColumn).trim();
	sFilter = String(_dataset_nvl(sFilter, "")).trim();
	var _idx = this.getColIndex(sColumn);
	if (_idx == -1)
		return null;

	var __rows = this.getData2(sFilter);
	if (__rows.length == 0)
		return 0;
	var _sum = 0, _val, _cnt = 0;
	var _flag = false;
	for ( var i = 0; i < __rows.length; i++) {
		_val = __rows[i][_idx];
		if (!_dataset_isNull(_val) && !_dataset_isEmpty(_val)) {
			if (isNaN(parseFloat(_val))) {
				_flag = true;
				break;
			}
			_sum += parseFloat(_val);
			_cnt++;
		}
	}
	return _flag ? null : _sum / _cnt;
};

/**
 * 조건식에 맞는 Record의 갯수를 구하는 Method 입니다.
 * 
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $=, >>, >> 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다. 
 * @returns 
 */
xwing.Dataset.prototype.count = function(sFilter) {
	sFilter = String(_dataset_nvl(sFilter, "")).trim();
	var __rows = this.getData2(sFilter);
	return __rows.length;
};

/**
 * Dataset의 Column들의 갯수의 합계를 얻어오는 Property 입니다.
 * 
 * @returns 
 */
xwing.Dataset.prototype.colCount = function() {
	return this.getColumnInfo().length;
};

/**
 * 지정한 Column ID에 해당하는 Dataset의 Column Index를 가지고 오는 Method 입니다.
 * @param sColumn
 * @returns {Number}
 */
xwing.Dataset.prototype.getColIndex = function(sColumn) {
	var _columns = this.getColumnInfo();
	var i = 0;
	var idx = -1;
	for (i in _columns) {
		if (_columns[i] == sColumn) {
			idx = i;
			break;
		}
	}
	return idx;
};

/**
 * 지정한 Column Index에 해당하는 Dataset의 Column ID를 가지고 오는 Method 입니다.
 * @param nIndex
 * @returns
 */
xwing.Dataset.prototype.getColId = function(nIndex) {
	var _columns = this.getColumnInfo();
	return _columns[nIndex];
};

/**
 * Dataset에서 해당 레코드를 원하는 위치로 이동하는 Method입니다.
 * 
 * @param nOldRow
 *            이동 할 Record Index.
 * @param nNewRow
 *            이동 후의 Record Index.
 */
xwing.Dataset.prototype.moveRow = function(nOldRow, nNewRow) {
	var _oldRow = this._rows[nOldRow];
	var _newRow = this._rows[nNewRow];
	
	this.setRow(nOldRow, _newRow);
	this.setRow(nNewRow, _oldRow);
//	this._rows[nOldRow] = _newRow;
//	this._rows[nNewRow] = _oldRow;
};

/**
 * Dataset에 존재하는 첫번째 Record를 가져옵니다.
 * @returns
 */
xwing.Dataset.prototype.getFirstRows = function() {
	return _dataset_nvl(this._rows[0], []);
};

/**
 * Dataset에 존재하는 마지막 Record를 가져옵니다.
 * @returns
 */
xwing.Dataset.prototype.getLastRows = function() {
	return _dataset_nvl(this._rows[this._rows.length - 1], []);
};

/**
 * Dataset에 존재하는 첫번째 Record를 가져옵니다.
 * @returns
 */
xwing.Dataset.prototype.getFirstRow = function() {
	return this._rows.length > 0 ? 0 : -1;
};

/**
 * Dataset에 존재하는 마지막 Record를 가져옵니다.
 * @returns
 */
xwing.Dataset.prototype.getLastRow = function() {
	return this._rows.length > 0 ? (this._rows.length - 1) : -1;
};

/**
 * Dataset에서 지정된 조건이 참인 첫번째 Record를 찾는 Method 입니다.
 * 
 * @param sFilter
 *            :상태[컬럼0 operator '비교값'] 
 *            상태는 C:생성, D:삭제, U:갱신으로 나뉩니다. 
 *            operator는 ==, =, ~=, !=, ^=, $= 이있습니다.
 *             
 *            == 는 비교값이 컬럼값과 일치하는 경우, 
 *            *= 는 비교값이 포함된 경우, 
 *            ~= 는 비교값이 한 단어로 분리된 경우, 
 *            != 는 비교값이 컬럼값과 다른 경우, 
 *            ^= 는컬럼값이 비교값으로 시작하는 경우, 
 *            $= 는 컬럼값이 비교값으로 끝나는 경우를 말합니다.
 *            <= 는 컬럼값이 비교값보다 작거나 작은 경우를 말합니다.
 *            >= 는 컬럼값이 비교값보다 크거나 큰 경우를 말합니다. 
 *            << 는 컬럼값이 비교값보다 작은 경우를 말합니다.
 *            >> 는 컬럼값이 비교값보다 큰 경우를 말합니다.             
 */
/*xwing/js/xwing.dataset.js 파일에서 searchRow함수 사용됨. 첫번째 record가 아닌 모든 record를 array로 받음. 
 
xwing.Dataset.prototype.searchRow = function(sFilter) {
	
	sFilter = String(_dataset_nvl(sFilter, "")).trim();

	var __cols = this._cols || [];
	if (__cols.length == 0) return -1;

	var __rows = this._rows || [];
	if (__rows.length == 0) return -1;

	__cols = __cols.concat("_rowidx");
	var _max_col_idx = (__cols.length - 1);

	for ( var i = 0; i < __rows.length; i++) {
		__rows[i][_max_col_idx] = i;
	}

	var _pattern = [], _ptnExpr, v;

	_ptnExpr = xwing.Dataset._FILTER_PTN;
	while (v = sFilter.match(_ptnExpr)) {
		_pattern.push(v);
		sFilter = sFilter.replace(_ptnExpr, '');
	}

	_ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?>)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
	while (v = sFilter.match(_ptnExpr)) {
		_pattern.push(v);
		sFilter = sFilter.replace(_ptnExpr, '');
	}

	_ptnExpr = /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?<)\s*(?:(['"])(.*?)\3|(#?)|(\S*))|)\s*\]/;
	while (v = sFilter.match(_ptnExpr)) {
		_pattern.push(v);
		sFilter = sFilter.replace(_ptnExpr, '');
	}

	if (_pattern.length == 0) return -1;

	for ( var i = 0, l = _pattern.length; i < l; i++) {
		var idx = this._cols.indexOf(_pattern[i][1]);
		if (idx == -1)
			continue;
		var type = _pattern[i][2], check = _pattern[i][4], value;
		__rows = jQuery.grep( __rows,
				function(v) {
					value = String(v[idx]);
					return type === '==' ? value === check : 
						   type === '*=' ? value.indexOf(check) >= 0 : 
						   type === '~=' ? (" " + value + " ").indexOf(check) >= 0 : 
						   type === '!=' ? value !== check : 
						   type === '^=' ? value.indexOf(check) === 0 : 
						   type === '$=' ? value.substr(value.length - check.length) === check : 
						   type === '<=' ? parseFloat(value) <= parseFloat(check) :
						   type === '>=' ? parseFloat(value) >= parseFloat(check) :
						   type === '>>' ? parseFloat(value) > parseFloat(check) :
						   type === '<<' ? parseFloat(value) < parseFloat(check) :
						   false;
		});
	}

	return (_dataset_isNull(__rows) || (__rows.length == 0) || 
			_dataset_isNull(__rows[0][_max_col_idx]) ||
			_dataset_isEmpty(__rows[0][_max_col_idx])) ? -1 : __rows[0][_max_col_idx];
			
};
*/
/**
 * 기존 Dataset에 새로운 Dataset을 통합하는 Method 입니다. 
 * 
 * Merge Method가 호출되면 기존 Dataset의 마지막 부분에 Merge 될 Dataset의 Record 갯수만큼 Record가 추가됩니다. 
 * Merge될 Dataset과 기존 Dataset의 동일한 Column ID에 대해서만 Record가 합해집니다. 
 * Merge될 Datsaet과 기존 Dataset의 다른 Column ID들은 무시 됩니다.
 * 
 * @param oDataset
 */
xwing.Dataset.prototype.merge = function(oDataset) {

};

/*******************************************************************************
 * UTIL
 ******************************************************************************/
function _dataset_nvl() {
	if (_dataset_isNull(arguments) == true)
		return null;
	for ( var i = 0; i < arguments.length; i++) {
		if (_dataset_isNull(arguments[i]) == false)
			return arguments[i];
	}
	return null;
}

function _dataset_evl() {
	if (_dataset_isNull(arguments) == true)
		return "";
	for ( var i = 0; i < arguments.length; i++) {
		if (_dataset_isEmpty(arguments[i]) == false)
			return arguments[i];
	}

	return "";
}

function _dataset_decode() {
	var i = 1;
	for (; i < arguments.length - 1;) {
		if (arguments[0] == arguments[i])
			return arguments[i + 1];
		i += 2;
	}
	return arguments[i];
}

function _dataset_substr() {
	var arg = arguments;

	var val = arg[0];
	if (typeof (val) != "string") {
		val = val.toString();
	}

	var s = arg[1];
	var e = _dataset_nvl(arg[2], val.length);
	return val.substr(s, e);
}

function _dataset_isNull(obj) {
	return (obj == null || typeof (obj) == "undefined");
}

function _dataset_isEmpty(obj) {
	return (_dataset_isNull(obj) == true || (typeof (obj) == "string" && obj.length == 0));
}  
};
