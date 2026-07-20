/*******************************************************************************
 * Global Variable
 ******************************************************************************/
//setTimeout(function(){
var _slide_loop = null;

var DRAWGRID_COLUMN = {
	id : new Array(),
	title : new Array(),
	bindColumn : new Array(),
	edittype : new Array()
};
var xr_log_flag = false;
/*******************************************************************************
 * Object
 *
 * @original IsDialer Developer
 * @improved IsForce Developer
 ******************************************************************************/
/**
 * 프로세스 처리 시간 확인!
 *
 * @param processId
 * @returns {Processing}
 */
Processing = function(processId) {
	var d = null;
	this.start = function() {
		d = new Date().getTime();
	};
	this.stop = function() {
		Xwing.notify(processId + " : "
				+ (new Date().getTime() - d) / 1000 + " sec");
		d = null;
	};
};

/**
 * 버튼 활성여부 제어.
 *
 * @param ObjectArray
 * @returns
 */
Buttonx = function(ObjectArray) {
	if (typeof ObjectArray == "string") {
		Xwing.notify("Error : ObjectArray는 배열이어야 합니다.");
		return false;
	}
	var btn = ObjectArray;
	this.activity = function(code) {
		var c = new String(code);
		var cd = [];
		if (btn.length != c.length) {
			Xwing.notify("Error : 버튼개수와 코드길이가 같지 않습니다.");
			return false;
		}
		if (new RegExp(/^([^0-1]{0,})$/).test(c)) {
			Xwing.notify("Error : 코드에는 0(true) 1(false)만 들어갈 수있습니다.");
			return false;
		}
		for ( var i = 0; i < c.length; i++) {
			cd.push(c.charAt(i));
		}
		for ( var i = 0; i < c.length; i++) {
			btn[i].setAttribute("enabled", cd[i] == 1 ? true : false);
		}
	};
};

/**
 * 위젯 활성여부 제어.
 *
 * @param ObjectArray
 * @returns
 */
Widgetx = function(ObjectArray) {
	if (typeof ObjectArray == "string") {
		Xwing.notify("Error : ObjectArray는 배열이어야 합니다.");
		return false;
	}
	var btn = ObjectArray;
	this.activity = function(code) {
		var c = new String(code);
		var cd = [];
		if (btn.length != c.length) {
			Xwing.notify("Error : 버튼개수와 코드길이가 같지 않습니다.");
			return false;
		}
		if (new RegExp(/^([^0-1]{0,})$/).test(c)) {
			Xwing.notify("Error : 코드에는 0(true) 1(false)만 들어갈 수있습니다.");
			return false;
		}
		for ( var i = 0; i < c.length; i++) {
			cd.push(c.charAt(i));
		}
		for ( var i = 0; i < c.length; i++) {
			btn[i].setAttribute("enabled", cd[i] == 1 ? true : false);
		}
	};
};

/**
 * 입력박스 스타일 설정 및 유효성검사
 *
 * @param Exception
 * @param ButtonId
 * @returns {StyleValidation}
 */
StyleValidation = function(Exception, ButtonId) {
	var object = [];
	var objectStyles = [];
	var exception = Exception || "";
	exception = exception.replace(/ /g, "").split(",");
	var expr = {
		widget : [],
		expr : [],
		msg : []
	};
	var btn_save = Xwing.getButton(ButtonId || "btn_save");
	var btn_click = btn_save.getAttribute("click");
	var that = this;

	this._errMsg = function(m) {
		alert(m || "필수입력 항목을 모두 입력해 주세요.");
	};
	this._schLabel = function(o) {
		for ( var i = 0 ; i < FUNC_XOBJECT.length ; i++) {
			if (FUNC_XOBJECT[i].getId().split("_")[1] == o.getId().split("_")[1]
					&& /^(stc|lbl)/.test(FUNC_XOBJECT[i].getId())) {
				return FUNC_XOBJECT[i].getValue();
			}
		}
		return false;
	};
	this._initStyles = function() {
		for ( var i = 0 ; i < FUNC_XOBJECT.length ; i++) {
			if (/^(edt|cmb|dpk)/.test(FUNC_XOBJECT[i].getId())) {
				object.push(FUNC_XOBJECT[i]);
				objectStyles.push(FUNC_XOBJECT[i].getStyles());
			}
		}
	};
	this._s = function(o, s) {
		var str = o.replace(/ /gim, "").split(",");
		for ( var i =0 ; i <  str.length ; i++) {
			var t = null;
			if (/^edt_/.test(str[i])) {
				t = "edit_";
			} else if (/^cmb_/.test(str[i])) {
				t = "combo_";
			} else {
				continue;
			}
			eval("$" + str[i]).setStyles(t + s);
		}
	};
	this.setNone = function(o) {
		this._s(o, "");
	};
	this.setMand = function(o) {
		this._s(o, "mand");
	};
	this.setReadOlny = function(o) {

		this._s(o, "readonly");
	};
	this.setMandReadOnly = function(o) {

		this._s(o, "mand_readonly");
	};
	this.setDefault = function() {
		for ( var i = 0 ; i <  objectStyles.length; i++) {
			object[i].setStyles(objectStyles[i]);
		}
	};
	this._initStyles();
	btn_save.unbind("click");
	btn_save.bind("click", function() {
		e: for ( var i =0 ; i < object.length ; i++) {
			for ( var j = 0 ; j < expr.widget.length ; j++) {
				if (expr.widget[j].getId() == object[i].getId()) {
					if (eval(expr.expr[j])) {
						that._errMsg(expr.msg[j]);
						object[i].focus();
						return false;
					}
				}
			}
			switch (object[i].getStyles()) {
			case "combo_mand":
			case "combo_mand_readonly":
			case "edit_mand":
			case "edit_mand_readonly":
				if (!object[i].getValue()) {
					for ( var j = 0; j < exception.length ; j++) {
						if (exception[j] == object[i].getId()) {
							continue e;
						}
					}
					var stc = that._schLabel(object[i]);
					var inp = "입력";
					var msg = null;
					if (stc) {
						if (/^[^edt]/.test(object[i].getId())) {
							inp = "선택";
						}
						msg = stc + " 항목을 " + inp + "해 주세요.";
					}
					that._errMsg(msg);
					object[i].focus();
					return false;
				}
			}
		}
		eval(btn_click)();
	});
};

/**
 * Page에 주기적으로 Event가 발생되는 Timer를 설정하는 Object
 *
 * ex)
 * var timer = new Timer(); //Timer Object 생성
 * timer.setInterval(10); // 반복할 시간(초) - Integer 단위 초
 * timer.setfunction(function or "function"); // 주기적으로 발생할 Event - function & string 함수 또는 문자
 * timer.start(); //Timer start
 * timer.stop(); //Timer stop
 *
 * @returns {Timer}
 */
Timer = function() {
	this._interval = 1000;
	this._timer = null;
	this._function = null;
};

Timer.prototype = {
	start : function() {
		this._timer = setInterval((this._function == null ? this._func
				: this._function), this._interval);
	},
	stop : function() {
		clearInterval(this._timer);
	},
	setfunction : function(func) {
		this._function = (typeof (func) == "string") ? eval(func)
				: (typeof (func) == "function") ? func : null;
	},
	setInterval : function(interval) {
		this._interval = (isNaN(interval) ? 1 : (interval == 0) ? 1 : interval) * 1000;
	},
	_func : function() {
		Xwing.notify("The function is not defined.");
	}
};

/*******************************************************************************
 * Script
 * @original IsDialer Developer
 * @improved IsForce Developer
 ******************************************************************************/
/**
 * 최상위 부모까지 해당 객체(obj)를 찾아서 리턴한다.
 *
 * @param obj
 * @returns
 */
function global(obj) {
	var MAX_DEPTH = 10;
	var o = obj;
	var p = "parent.";
	var g = "global.";
	var r = null;

	for ( var i = 0; i < MAX_DEPTH; i++) {
		if ((r = eval(p + o)) != undefined) {
			break;
		}

		if ((r = (eval(p+'global') ? eval(p + g + o) : undefined ) ) != undefined) {
			break;
		}
		p += "parent.";
	}
	return r;
}

/**
 * Xwing모든 위젯을 $Id명으로 위젯을 담음!
 */
function XwingObject() {
	try{loadDataset();}catch(e){}
	var o = Xwing.getWidgetList().keys();
	var n = Xwing.getWidgetList().keys();

	FUNC_XOBJECT = [];

	for ( var i = 0 ; i < o.length ; i++) {

		if (Xwing.getWidget(o[i])._attr == undefined
				|| Xwing.getWidget(o[i])._attr.xw_type == undefined)
			continue;
		n[i] = n[i].replace(/-/gim, '_');

		switch (Xwing.getWidget(o[i])._attr.xw_type.toUpperCase()) {
		case "BUTTON":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getButton('" + o[i]
					+ "')"));
			break;
		case "CHART":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getChart('" + o[i]
					+ "')"));
			break;
		case "CHECKBOX":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getCheckbox('" + o[i]
					+ "')"));
			break;
		case "COMBO":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getCombo('" + o[i]
					+ "')"));
			break;
		case "DATEPICKER":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getDatepicker('"
					+ o[i] + "')"));
			break;
		case "EDIT":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getEdit('" + o[i]
					+ "')"));
			break;
		case "EMBED":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getEmbed('" + o[i]
					+ "')"));
			break;
		case "FILE":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getFile('" + o[i]
					+ "')"));
			break;
		case "DATAGRID":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getDataGrid('" + o[i]
					+ "')"));
			break;
		case "GRID":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getGrid('" + o[i]
					+ "')"));
			break;
		case "GRID-COLUMN":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getGridcolumn('"
					+ o[i] + "')"));
			break;
		case "GROUP":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getGroup('" + o[i]
					+ "')"));
			break;
		case "LABEL":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getLabel('" + o[i]
					+ "')"));
			break;
		case "LIST":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getList('" + o[i]
					+ "')"));
			break;
		case "MENU":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getMenu('" + o[i]
					+ "')"));
			break;
		case "PANEL":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getPanel('" + o[i]
					+ "')"));
			break;
		case "PROGRESSBAR":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getProgressbar('"
					+ o[i] + "')"));
			break;
		case "RADIO":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getRadio('" + o[i]
					+ "')"));
			break;
		case "RICHTEXT":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getRichtext('" + o[i]
					+ "')"));
			break;
		case "SLIDER":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getSlider('" + o[i]
					+ "')"));
			break;
		case "SPIN":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getSpin('" + o[i]
					+ "')"));
			break;
		case "SPLITBAR":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getSplitbar('" + o[i]
					+ "')"));
			break;
		case "TAB":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getTab('" + o[i]
					+ "')"));
			break;
		case "TAB-PAGE":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getTabpage('" + o[i]
					+ "')"));
			break;
		case "TEXT":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getText('" + o[i]
					+ "')"));
			break;
		case "TEXTAREA":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getTextArea('" + o[i]
					+ "')"));
			break;
		case "TREE":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getTree('" + o[i]
					+ "')"));
			break;
		case "PAGE":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getPage('" + o[i]
					+ "')"));
			break;
		case "WIDGET":
			FUNC_XOBJECT.push(eval("$" + n[i] + "= Xwing.getWidget('" + o[i]
					+ "')"));
			break;
		}
	}

	n = null;
	o = Xwing.getDatasetList().keys();

	for ( var i = 0 ; i < o.length ; i++) {

		FUNC_XOBJECT.push(eval("$" + o[i] + "= Xwing.getDataset('" + o[i]
				+ "')"));
	}

	// CREATE COPY PARENT DATASET.
	n = [ "add", "remove", "reset", "sort", "update" ];
	o = parent.Xwing.getDatasetList().keys();

	for ( var i = 0; i < o.length; i++) {

		if (typeof _IS_FORCE == "string")
			break;

		var p = parent.Xwing.getDataset(o[i]);

		FUNC_XOBJECT.push(eval("$_" + o[i] + " = Xwing.createDataset('_" + o[i]
				+ "')"));

		eval("$_" + o[i] + ".setData(p.getColumnInfo(), p.getData())");
		eval("$_" + o[i] + ".setCursor(p.getCursor())");

		for ( var j = 0 ; j < n.length ; j++) {

			eval("$_"
					+ o[i]
					+ ".bind('"
					+ n[j]
					+ "', function() { Xwing.notify('수정된 데이터셋 결과는 부모 데이터셋에 반영되지 않습니다!'); })");
		}
	}

	xwing.widget.DataGrid.prototype._createXmlString = function(){
		var columns = this._ast.column
			, c_arr = ['<datagrid-colgroup>'];

		var zerowidth = [];
		for(var i=0, l=columns.length, col ; i<l ; i++){
			col = columns[i];
			if( col._opt.width != 0)
				c_arr.push('<datagrid-column '
					+ (col._opt.width ? 'width="'+col._opt.width+'" ' : ' ')
					+'></datagrid-column>');
			else
				zerowidth.push(i);
		}
		c_arr.push('</datagrid-colgroup>');

		var parts = ['head','body','summary']
		, p_arr = [];
		for(var i=0, l=parts.length ; i<l ; i++){
			var part = this._ast[parts[i]];
			if(part.length == 0 ) continue;

			var arr = ['<datagrid-'+parts[i]+'>'];
			var colspan = 0;
			for(var j=0, rows = part.length; j<rows ; j++){
				arr.push('<datagrid-row>');
				var cells = part[j].getCell();
				for(var k=0; k<cells.length; k++){
					var cell = cells[k]
						, attr = ''
						, text = cell._opt.text;
					if( zerowidth.indexOf(cell._opt.colindex) != -1 && parseInt(cell._opt.colspan) == 1) continue;
					// text...
					if(cell.getExpr()){
						var val = cell._doExpr(0);
						if(val && (val.value || val.value ==0)) text = val.value;
						else if(typeof val != 'object' || val == 0) text = val;
					}
					var zero = false;
					if(parseInt(cell._opt.colspan) > 1){
						for( var z=0; z < zerowidth.length ; z++){
							var tmp= (parseInt(cell._opt.colspan) > 1 ? 1 : 0);
							if((cell._opt.colindex - parseInt(cell._opt.colspan) + tmp) <=  zerowidth[z] && zerowidth[z] <= cell._opt.colindex){
								zero = true;
								colspan++;
								continue;
							}
						}
					}
					// attribute
					attr = (cell._opt.bindcolumn ? 'bindcolumn="'+cell._opt.bindcolumn+'" ' : '')
						+ (cell._opt.colspan != 1 ? 'colspan="'+(zero ? parseInt(cell._opt.colspan) - colspan : cell._opt.colspan)+'" ' : '')
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
	};

	constContext();
}

/**
 * 기존에 정의된 column 정보와 record data를 초기화 한 후, 새로 지정합니다.
 *
 * @param datasetId
 * @param columns
 * @param rows
 */
function g_setData(datasetId, columns, rows) {
	var d = Xwing.getDataset(datasetId);
	d.setData(columns, rows);
};

/**
 * XwingObject로 생성된 객체를 지움.
 */
function XwingObject_destroy() {
	var ObjectId = null;
	for ( var i = 0 ; i < FUNC_XOBJECT.length ; i++) {
		ObjectId = FUNC_XOBJECT[i].getId().replace(/-/gim, "_");
		eval("$" + ObjectId + "= null");
	}
}

/**
 * 자식객체를 배열로 반환.
 *
 * @param parentId
 * @returns {Array}
 */
function g_getChildNodes(parentId) {
	var b = false;
	var n = false;
	var r = [];
	for (  var i = 0 ; i < FUNC_XOBJECT.length ; i++) {
		try {
			if (b == true && n == false)
				break;
			if (n = (parentId == FUNC_XOBJECT[i]._parentWidget.getId())) {
				b = true;
				r.push(FUNC_XOBJECT[i]);
			}
		} catch (e) {
		}
	}
	return r;
}

/**
 * 그리드 컬럼을 동적으로 그림! ※권장하지 않음
 *
 * @param gridId
 * @param dynamic
 * @returns {Boolean}
 */
function g_drawGridcolumn(gridId, dynamic) {
	var grd = Xwing.getGrid(gridId);

	if (dynamic) {

		grd.addColumn(Xwing.createGridcolumn({
			id : DRAWGRID_COLUMN.id.shift(),
			title : DRAWGRID_COLUMN.title.shift(),
			bindcolumn : DRAWGRID_COLUMN.bindColumn.shift(),
			edittype : DRAWGRID_COLUMN.edittype.shift()
		}));
		Xwing.createGridcolumn();

		if (DRAWGRID_COLUMN.id.length == 0) {

			return true;
		}

		setTimeout("g_drawGridcolumn(\"" + gridId + "\", " + true + ")", 5);
	} else {

		for ( var i = 0; i < DRAWGRID_COLUMN.id.length; i++) {

			grd.addColumn(Xwing.createGridcolumn({
				id : DRAWGRID_COLUMN.id[i],
				title : DRAWGRID_COLUMN.title[i],
				bindcolumn : DRAWGRID_COLUMN.bindColumn[i],
				edittype : DRAWGRID_COLUMN.edittype[i]
			}));
		}
	}
}

/**
 * 패널 slide left/right 개발 및 테스트완료 up/down 개발 시 수정..
 *
 * @param sPanelId
 * @param sPageId
 * @param sMove
 * @param nPos
 * @param fCallBack
 * @param sCtrlId
 */
function g_slidePanel(sPanelId, sPageId, sMove, nPos, fCallBack, sCtrlId) {
	if (_slide_loop != null) {
		clearTimeout(_slide_loop);
	}
	sCtrlId = g_nvl(sCtrlId, "");
	var nSpeed = 60;
	var nMotion = 4;
	var oPanel = Xwing.getWidget(sPanelId);
	var oPage = Xwing.getWidget(sPageId);
	var oController = Xwing.getWidget(sCtrlId);
	var nCurr;
	var nAfterCurr = 0;

	if (oPage.getScroll())
		oPage.setScroll(false);

	if (sMove == "UP") {
		nCurr = oPanel.getTop();
		nAfterCurr = nCurr - (nCurr - nPos) / nMotion - 1;
		if (nAfterCurr <= nPos) {
			if (!g_isNull(oController)) {
				oController.setTop(nPos - oController.getHeight());
			}
			oPanel.setTop(nPos);
			fCallBack();
			return;
		}
		if (!g_isNull(oController)) {
			oController.setTop(nAfterCurr - oController.getHeight());
		}
		oPanel.setTop(nAfterCurr);
	} else if (sMove == "LEFT") {
		nCurr = oPanel.getLeft();
		nAfterCurr = nCurr + (nPos - nCurr) / nMotion + 1;
		if (nAfterCurr <= nPos) {
			if (!g_isNull(oController)) {
				oController.setLeft(nPos - oController.getWidth());
			}
			oPanel.setLeft(nPos);
			fCallBack();
			return;
		}
		oController.setLeft(nAfterCurr - oController.getWidth());
		oPanel.setLeft(nAfterCurr);
	} else if (sMove == "RIGHT") {
		nCurr = oPanel.getLeft();
		nAfterCurr = nCurr + (nPos - nCurr) / nMotion + 1;
		if (nAfterCurr >= nPos) {
			if (!g_isNull(oController)) {
				oController.setLeft(nPos - oController.getWidth());
			}
			oPanel.setLeft(nPos);
			fCallBack();
			return;
		}
		if (!g_isNull(oController)) {
			oController.setLeft(nAfterCurr - oController.getWidth());
		}
		oPanel.setLeft(nAfterCurr);
	} else {
		nAfterCurr = nCurr + (nPos - nCurr) / nMotion + 1;
		if (nAfterCurr >= nPos) {
			if (!g_isNull(oController)) {
				oController.setTop(nPos - oController.getHeight());
			}
			oPanel.setTop(nPos);
			fCallBack();
			return;
		}
		if (!g_isNull(oController)) {
			oController.setTop(nAfterCurr - oController.getHeight());
		}
		oPanel.setTop(nAfterCurr);
	}
	_slide_loop = setTimeout("g_slidePanel('" + sPanelId + "', '" + sPageId
			+ "', '" + sMove + "', " + nPos + ", " + fCallBack + ", '"
			+ sCtrlId + "')", nSpeed);
}

/**
 * 각 Object의 값을 빈값(' ')또는 null 인지 체크!
 *
 * @param objs
 * @param names
 * @returns {Boolean}
 */
function g_checkEmptyObjects(objs, names) {
	for ( var i = 0; i < objs.length; i++) {
		if (objs[i].getAttribute('value') == ''
				|| objs[i].getAttribute('value') == null) {
			alert(names[i] + ' 정보를 입력해 주세요!');
			if (objs[i].getAttribute('enabled') != false) {
				objs[i].focus();
			}
			return true;
		}
	}
	return false;
}

/**
 * 각 Object의 값이 0~9사이 숫자인지 체크!
 * @param objs
 * @param names
 * @returns {Boolean}
 */
function g_checkNumberObjects(objs, names) {
	var f = '';
	f = new RegExp('[^0-9]');

	for ( var i = 0; i < objs.length; i++) {
		if (f.test(objs[i].getAttribute('value'))) {
			alert(names[i] + ' 항목은 숫자만 입력 가능합니다!');

			objs[i].focus();

			return true;
		}
	}
	return false;
}

/**
 * 각 Object의 값중 원하는 문자열이 들어갔는지 체크!
 * @param objs
 * @param str
 * @returns {Boolean}
 */
function g_checkStringObjects(objs, str) {
	for ( var i = 0; i < objs.length; i++) {
		if (objs[i].getAttribute('value').indexOf(str) == -1) {
			objs[i].focus();

			return true;
		}
	}
	return false;
}

/**
 * 각 Object의 값길이를 원하는 길이인지 아닌지 체크!
 * @param objs
 * @param names
 * @param length
 * @returns {Boolean}
 */
function g_checkLengthObjects(objs, names, length) {
	var str = '';
	for ( var i = 0; i < objs.length; i++) {
		str = new String(objs[i].getAttribute('value'));
		if (str.length < length[i]) {
			objs[i].focus();
			alert(names[i] + '항목은 최소' + length[i] + '자 이상 입력하셔야 합니다!');
			return true;
		}
	}
	return false;
}

/**
 * Object들의 속성값을 일괄적으로 처리!
 * @param objs
 * @param type
 * @param value
 */
function g_setAttributeObjects(objs, type, value) {
	for ( var i = 0; i < objs.length; i++) {
		objs[i].setAttribute(type, value);
	}
}

/**
 * Object들의 visible상태를 일괄적으로 처리!
 * @param objs
 * @param b
 */
function g_visibleObjects(objs, b) {
	for ( var i = 0; i < objs.length; i++) {
		objs[i].setAttribute('visible', b);
	}
}

/**
 * Object들의 enable상태를 일괄적으로 처리!
 * @param objs
 * @param b
 */
function g_enabledObjects(objs, b) {
	for ( var i = 0; i < objs.length; i++) {
		objs[i].setAttribute('enabled', b);
	}
}

/**
 * 각 Object의 값을 빈값으로 바꿈
 * @param objs
 */
function g_emptyObjects(objs) {
	for ( var i = 0; i < objs.length; i++) {
		objs[i].setAttribute('value', '');
	}
}

/**
 * 프로세스를 delay시킴!
 * @param millisecond
 */
function g_sleep(millisecond) {
	var sec = new Date().getTime() + millisecond;
	while (true) {
		if (new Date().getTime() > sec) {
			break;
		}
	}
}

/**
 * IN쿼리안에 들어갈 값을 추출.
 *
 * @param dataset
 * @param column
 * @returns {String}
 */
function g_getInQuery(dataset, column) {
	var v_result = "";
	for ( var i = 0; i < dataset.size(); i++) {
		v_result += dataset.getValue(i, column);
		v_result += i < dataset.size() - 1 ? ", " : "";
	}
	return v_result;
}

/**
 * 문자열을 콤마단위로 자른 후 다시 붙임('str1, str2' => \'str1\', \'str2\').
 * @param str
 * @returns
 */
function g_getStringFormat(str) {
	var o = str.replace(/ /gim, "");
	o = o.split(",");
	for ( var i = 0 ; i < o.length ; i++){
		o[i] = "'" + o[i] + "'";
	}
	return o.join(",");
}

/**
 * 사용자 입력조건을 쿼리로 파싱함.
 * @param dataset
 * @returns {string}
 */
function g_parsingQuery(dataset) {
	var d = {
		condGroup : [],
		condQuery : [],
		condOp : []
	};
	var expr = [];
	var q = "";
	expr[0] = /^[^(]((\w|<|<=|>|=>){0,},(\w|<|<=|>|=>){0,}){0,}[^(,|))]$/;
	expr[1] = /^\w{1,}$/;
	expr[2] = /^>=((\w){0,})$/;
	expr[3] = /^<=((\w){0,})$/;
	expr[4] = /^>((\w){0,})$/;
	expr[5] = /^<((\w){0,})$/;
	expr[6] = /^(\w{1,})-(\w{1,})$/;
	expr[7] = /^~(\w{0,})$/;
	expr[8] = /^~[(]((,)|(\w)){0,}[^,][)]$/;
	expr[9] = /^[(]((,)|(\w)){0,}[^,][)]$/;
	expr[10] = /^~[*]$/;
	expr[11] = /^[*]$/;
	expr[12] = /^[*]\w{0,}$/;
	expr[13] = /^\w{0,}[*]$/;
	expr[14] = /^(\w{0,}[?]{1,}\w{0,})$/;
	expr[15] = /^[?]\w{0,}$/;
	expr[16] = /^\w{0,}[?]$/;
	expr[17] = /^$/;
	for ( var i = 0; i < dataset.size(); i++) {
		var c = dataset.getValue(i, "COND_COL") + " ";
		var v = dataset.getValue(i, "COND_VALUE").replace(/ /g, "");
		var r = c;
		d.condOp[i] = dataset.getValue(i, "COND_OP");
		d.condGroup[i] = dataset.getValue(i, "COND_GROUP");
		for ( var j = 0; j < expr.length; j++) {
			d.condQuery[i] = null;
			if (!expr[j].test(v)) {
				continue;
			}
			switch (j) {
			case 0:
				v = v.split(",");
				r = "";
				for ( var z = 0 ; z <  v.length ; z++) {
					r += c + " = '" + v[z] + "'";
					r += (v.length - 1) != z ? " OR " : "";
				}
				break;
			case 1:
				r += "= '" + v + "'";
				break;
			case 2:
				v = v.replace(">=", "");
				r += ">= '" + v + "'";
				break;
			case 3:
				v = v.replace("<=", "");
				r += "<= '" + v + "'";
				break;
			case 4:
				v = v.replace(">", "");
				r += "> '" + v + "'";
				break;
			case 5:
				v = v.replace("<", "");
				r += "< '" + v + "'";
				break;
			case 6:
				v = v.split("-");
				r += "BETWEEN '" + v[0] + "' AND '" + v[1] + "'";
				break;
			case 7:
				v = v.replace("~", "");
				r += "!= '" + v + "'";
				break;
			case 8:
				v = (v.replace(/[^\w|,]{0,}/g, "")).split(",");
				r += "NOT IN (";
				for ( var z = 0 ; z < v.length; z++) {
					r += "'" + v[z] + "'";
					r += (v.length - 1) != z ? ", " : "";
				}
				r += ")";
				break;
			case 9:
				v = (v.replace(/[^\w|,]{0,}/g, "")).split(",");
				r += "IN (";
				for ( var z = 0 ; z < v.length; z++) {
					r += "'" + v[z] + "'";
					r += (v.length - 1) != z ? ", " : "";
				}
				r += ")";
				break;
			case 10:
				r += "is null";
				break;
			case 11:
				r += "like '%'";
				break;
			case 12:
				v = v.replace(/^\*/g, "%");
				r += "like '" + v + "'";
				break;
			case 13:
				v = v.replace(/\*/g, "%");
				r += "like '" + v + "'";
				break;
			case 14:
				v = v.replace(/\?/g, "_");
				r += "like '" + v + "'";
				break;
			case 15:
				v = v.replace(/\?/g, "_");
				r += "like '" + v + "'";
				break;
			case 16:
				v = v.replace(/\?/g, "_");
				r += "like '" + v + "'";
				break;
			case 17:
				r = "";
				break;
			}
			if (r != c) {
				d.condQuery[i] = r;
				break;
			}
		}
	}

	// [!] SORTING CHECK.
	if (d.condGroup.join(", ") != (d.condGroup.sort().join(", "))) {
		alert("ERR: 쿼리변환 실패!(그룹 시퀀스 오류.)");
		return false;
	}
	s = "1";
	// [!] EXPRESSION CHECK.
	for ( var i = 0 ; i < d.condQuery.length ; i++) {
		if (d.condQuery[i] == null) {
			alert("ERR: 쿼리변환 실패!(존재하지 않는 표현식.)\n[ROW(" + i + "), COND_VALUE("
					+ dataset.getValue(i, "COND_VALUE") + ")]");
			return false;
		}
		if (d.condQuery[i] == "") {
			continue;
		}
		q += "(";
		q += i == 0 || d.condGroup[parseInt(i, 10) - 1] != d.condGroup[i] ? "("
				: "";
		q += d.condQuery[i];
		q += d.condGroup[parseInt(i, 10) + 1] != d.condGroup[i] ? ")" : "";
		q += ")";
		// q += (d.condQuery.length-1) != i ? " "+d.condOp[i]+" " : "";
		q += " " + d.condOp[i];
	}
	q += " 1=1";
	var l = q.replace(/[^(]/gim, '');
	var r = q.replace(/[^)]/gim, '');
	for ( var i = r.length; i < l.length; i++) {
		q += ")";
	}
	return q;
}

/**
 * 그리드에 bind된 데이터셋을 엑셀파일로 내려받는다.
 * @param grdObj
 * @param filename
 */
function g_exportData(grdObj, filename) {
	var column = '', row = '';
	if (grdObj._opt.binddataset) {
		column = Xwing.getDataset(grdObj._opt.binddataset)._cols.join(']');
		row = grdObj._join(Xwing.getDataset(grdObj._opt.binddataset)._rows);
	}
	var URL = global("G_URL") + "jsp/export.jsp";
	var PARAMS = {
		datagrid : grdObj._datagridXml,
		column : column,
		row : row
	};
	if (filename)
		PARAMS['filename'] = filename;
	grdObj._postSend(URL, PARAMS);
}

/**
 *
 * 그리드 내용을 엑셀파일로 내려받는다.
 * @param grdObj
 * @param filename
 * @param nRows : 데이터셋 row 수
 * @param numYN : 그리드 앞에 rownum 가 붙으면 "Y", 아니면 "N"
 * @PARAM checkYN : 그리드 앞에 checkbox가 붙으면 "Y", 아니면 "N"
 * @param left_fixYN : 그리드에 left fix가 있으면 "Y", 아니면 "N"
 * 그리드의 셀을 ']'으로 구분한다. 따라서 그리드 안의 내용에 ']'이 들어가면 엑셀이 어그러져서 나온다.
 * 그리드 속성에서 virtual이 'false'로 해야한다.
 *
 */

function g_list_exportData(grdObj, filename, nRows, numYN, checkYN, left_fixYN){

	var exportGrd = grdObj;
	var grdType = exportGrd._attr.xw_type;	//그리드 type

	if( grdType == "datagrid"){
		//그리드 종류가 dataGrid 일 경우..
		var nHeadColumn=0;

		//그리드에 head가 존재하는 경우에만 head[0]._cell에 접근 가능.
		if( exportGrd._ast.head.length !=0 ){
			nHeadColumn = exportGrd._ast.head[0]._cell.length;
		}

		var nGrdColumn = exportGrd._ast.column.length;	//그리드 컬럼 갯수.

		var columns = "";	//head의 text. '['로 구분함.
		var rows = "";		//body의 text. '['로 구분함.
		var dataSetRows = nRows;	//data row 수.

		for(var i=0; i<nHeadColumn; i++){
			//head text 값 가져오는 것.
			columns += exportGrd._ast.head[0]._cell[i]._opt.text +"]";
		}

		if(nHeadColumn == 0){
			//head가 없을 경우.
			for (var i=0; i<nGrdColumn; i++){
				columns += " ]";
			}
		}

		var fixSize = 0;	//fix된 column size.

		if(left_fixYN=="Y"){
			//fix된  컬럼이 얼마나 되는지..
			fixSize = exportGrd.body_fix_table[0].childNodes[1].childNodes[0].childNodes.length;
		}

		var cntFixStart = 0;	//fix된 body text값의 start 부분을 알기 위한 변수.
		var cntFix = fixSize;	//num, check를 제외한 fix된 컬럼 수를 알기 위한 변수.


		if (left_fixYN=="Y" && numYN == "Y"){
			cntFixStart = cntFixStart + 1;
			cntFix = cntFix - 1;
			if(checkYN=="Y"){
				cntFixStart = cntFixStart + 1;
				cntFix = cntFix - 1;
			}

		}

		var columnCnt = 0;

		for ( var j=0; j<dataSetRows; j++){

			for( var k=cntFixStart; k < fixSize ; k++ ){
				//fix된 body 값 가져오는 것.
				if(exportGrd._ast.body[0]._cell[columnCnt]._opt.bindcolumn != null){
					rows+= exportGrd.body_fix_table[0].childNodes[1].childNodes[j].childNodes[k].innerText+"]";
				}
				columnCnt++;
			}

			for(var z=0; z<(nGrdColumn-cntFix); z++){
				//fix되지 않은 body 값 가져오는 것.
				if(exportGrd._ast.body[0]._cell[columnCnt]._opt.bindcolumn != null){
					rows+= exportGrd.body_table[0].childNodes[1].childNodes[j].childNodes[z].innerText+"]";
				}
				columnCnt++;
			}
			columnCnt=0;
		}

		var URL = global("G_URL") + "jsp/export.jsp";
		var PARAMS = {
			datagrid : "",
			column : columns,
			row : rows
		};

		if (filename)
			PARAMS['filename'] = filename;
		grdObj._postSend(URL, PARAMS);

	}else if(grdType == "grid"){
		//그리드 타입이 grid 일 경우.

		var nColumn = 0;

		//컬럼 갯수.
		nColumn = exportGrd.header_table[0].childNodes[1].childNodes[0].childNodes.length;

		var columns = "";	//head의 text. '['로 구분함.
		var rows = "";		//body의 text. '['로 구분함.
		var dataSetRows = nRows;	//data row 수.


		for(var i=0; i<nColumn; i++){
			//Header text. ']' 로 구분.
			columns += exportGrd.header_table[0].childNodes[1].childNodes[0].childNodes[i].innerText+"]";
		}

		for(var j=0; j<dataSetRows; j++){
			for(var i=0; i<nColumn; i++){
				//Body text. ']' 로 구분.
				rows += exportGrd.body_table[0].childNodes[1].childNodes[j].childNodes[i].innerText+"]";
			}
		}

		var URL = global("G_URL") + "jsp/export.jsp";
		var PARAMS = {
			datagrid : "",
			column : columns,
			row : rows
		};
		if (filename)
			PARAMS['filename'] = filename;
		grdObj._postSend(URL, PARAMS);


	}


}

/**
 * xwing.Dialog를 띄운다. oOpt의 id값이 없으면 Dialog의 id는 파일명으로 최상의 부모창에 hash값에 담긴다.
 *
 * @param oOpt
 * @param bMain
 */
function g_dialog(oOpt, bMain) {
	var MAX_DEPTH = 10;
	var o = "_IS_FORCE";
	var p = "parent.";
	var r = null;
	for ( var i = 0; i < MAX_DEPTH; i++) {
		if ((r = eval(p + o)) != undefined) {
			break;
		}
		p += "parent.";
	}
	// if(!g_isNull(oOpt.url)) oOpt["url"] = "../" + oOpt.url;
	if (!g_isNull(oOpt.width))
		oOpt["width"] = oOpt.width + 16;
	if (!g_isNull(oOpt.height))
		oOpt["height"] = oOpt.height + 46;

	var dialog = null;
	eval("dialog = new " + ((bMain) ? p : "") + "xwing.Dialog(oOpt)");
	eval("dialog.open()");

	if (p._dialog == null || typeof (p._dialog) == "undefined") {
		eval("__dialog = " + p + "_dialog = new xwing.util.Hash()");
	} else {
		eval("__dialog = " + p + "_dialog");
	}
	__dialog.set(
			(oOpt.id == null || typeof (oOpt.id) == "undefined") ? (oOpt.url)
					.split("/")[(oOpt.url).split("/").length - 1] : oOpt.id,
			dialog);

}

/**
 * g_dialog로 띄운 해당 id(default는 해당파일명)의 dialog Object를 return
 *
 * @param id
 * @returns
 */
function g_getDialog(id) {
	var MAX_DEPTH = 10;
	var o = "_IS_FORCE";
	var p = "parent.";
	var r = null;
	for ( var i = 0; i < MAX_DEPTH; i++) {
		if ((r = eval(p + o)) != undefined) {
			break;
		}
		p += "parent.";
	}
	eval("__dialog = " + p + "_dialog");
	return __dialog.get(id);
}

/**
 * processing error messages at Callback Parameter
 *
 * success - Output Dataset : {column : [column info], record : [rows info]}
 * Error : "", ResultCode : "", ResultMessage : ""
 *
 * fail - Error : "", ResultCode: "", ResultMessage: "", input의 param 정보들.
 *
 * ---------------------------------------------------------------------------
 * ErrorCode ErrorMessage Description
 * ---------------------------------------------------------------------------
 *
 * 0 'OK' 정상
 *
 * 1 HTTP Transfer Error 서버로 부터 HTTP 오류 코드를 응답으로 받은경우 예) 404 NOT FOUND
 *
 * -1 TIMEOUT 지정한 시간이내에 응답을 받지 못한경우 Default 30초, xwing.config파일의 URL prefix를
 * 지정하면서 timeout을 지정할 수 있습니다.
 *
 * -2 'Xwing Response Parse Error' 서버로 부터 응답을 받았으나 그 내용이 Xwing JSON 규격을 만족하지 않아
 * 해석이 불가능한경우
 *
 * @param res
 * @param bMsgYN
 *            'Done' Message Popup Y/N Return Value
 * @returns {Boolean} res : response Data
 */
function g_message(res, bMsgYN) {
	// 임시 적인 처리 추후 수정필요

	if (res.ResultCode != '0') {
		xwing.Dialog.alert(
				'ResultCode : ' + res.ResultCode + '<br/>' + 'ResultMessage : ' + res.ResultMessage
				, 'Error'
				, function() {

					if( res.ResultCode == 'SessionTimeOut')
					{
						//로그아웃 처리 추가 필요
						window.open("../common/login.xhtml", "ISFORCE_LOGIN",
						"width=620, height=520");

						top.window.open("about:_blank", "_self").close();
					}
				}
		);

		return false;
	} else {
		if (bMsgYN) {
			xwing.Dialog.alert('처리되었습니다.');
		}
		return true;
	}
}

/**
 * Xwing.request
 * ----------------------------------------------------------------------------
 * error메시지 추가 - opt.msgyn
 *
 * @param opt
 *            option parameter
 * @param callback
 *            callback function
 */
function g_xr(opt, callback) {
	var _process = null;

	if (xr_log_flag) {
		_process = new Processing(g_nvl(opt.reqId, "Processing"));
		_process.start();
		xr_log(opt);
	}

	Xwing.request(opt, function(reqId, res, err, xhr) {
		if (!g_message(res, (opt.msgyn || false))) {
			reqId = reqId + "_error";
		}
		if (typeof (callback) == "function") {
			eval("callback(reqId, res, err, xhr)");
		}
		if (xr_log_flag)
			_process.stop();
	});

	if (window.event.ctrlKey && window.event.shiftKey) {
		var opt = {
			modal : false,
			resizable : true,
			url : global("G_URL")+ "common/dv.xhtml",
			width : 600,
			height : 450,
			title : "DataSet View",
			param : {
				p_target : false
			}
		};
		var dialog = new xwing.Dialog(opt);
		dialog.open();
	}
}

var xr_log = function(o) {
	for ( var i = 0 ; i < o.length ; i++) {
		if (o[i] == null || o[i] == undefined) {
		} else if (typeof (o[i]) == "boolean") {
		} else if (typeof (o[i]) == "function") {
		} else if (typeof (o[i]) == "string") {
		} else {
			if (o[i] instanceof xwing.Dataset) {

			} else {
				xr_log(o[i]);
			}
		}
	}
};


/**
 * Dataset의 값을 Json 현식의 Param에 추가한다.
 *
 * @param strPtn
 * @param dsObj
 * @param nRow
 * @param objData
 * @returns
 */
function g_dsToParam(strPtn, dsObj, nRow, objData) {
	var v_col_info = dsObj.getColumnInfo();
	for ( var i = 0; i < v_col_info.length; i++) {
		var v_col_id = v_col_info[i];
		var v_tmp = dsObj.getValue(nRow, v_col_id);
		objData[(strPtn + v_col_id + "").toUpperCase()] = ((v_tmp == null || typeof (v_tmp) == "undefined") ? ""
				: v_tmp);
	}
	return objData;
}

/**
 * File Upload
 *
 * Error : 1 파일업로드 실패(size) -1 파일업로드 실패(other) 0 파일업로드 성공
 *
 * @param oFile
 *            File Object
 * @param callback
 *            Call Back Function
 * @param sFilePath
 *            File Write Path
 * @param oOpt
 *            User Parameter
 *            oOpt 안에 security = "N" 으로 넘겨주면 파일명 암호화 안됨.
 */
function g_fileupload(oFile, callback, sFilePath, oOpt) {
	if (g_isNull(oFile) || !(oFile instanceof xwing.widget.File)) {
		Xwing.notify("File object Not find. upload fail!");
		return false;
	}
	sFilePath = sFilePath || "";
	oOpt = oOpt || {};

	oFile.setAction(g_nvl(global("G_SERVERURL"), "../") + "/jsp/fileUpload.jsp");
	oOpt["filepath"] = sFilePath;

	oFile.send(oOpt, function(sRes) {

		var oRes = JSON.parse(sRes);
		if (oRes.Error == 1) {
			xwing.Dialog.alert("파일업로드를 실패하였습니다. <br/>업로드 가능한 최대 파일사이즈는 "
					+ oRes.maxsize + "MB 입니다.");
			return false;
		} else if (oRes.Error == 0) {
			var __fileinfo = Xwing.getDataset("__fileinfo");
			if (g_isNull(__fileinfo)) {
				__fileinfo = new xwing.Dataset("__fileinfo",
						oRes.fileinfo.column, oRes.fileinfo.record);
			} else {
				__fileinfo.setData(oRes.fileinfo.column, oRes.fileinfo.record);
			}

			if (__fileinfo.getColumnInfo().join().indexOf("REG_ID") == -1) {
				__fileinfo.setColumnInfo(__fileinfo.getColumnInfo().concat("REG_ID"));
			}

			for ( var i = 0; i < __fileinfo.size(); i++) {
				__fileinfo.setValue(i, "REG_ID", global("G_USER_ID"));
			}

			if (__fileinfo.size() != 0) {
				Xwing.request({
					reqId : "__fileinfo",
					url : "service::cs.com.fileupload.do",
					param : {
						_sqlName : "cs.com.uploadfile.ins",
						batchlist : __fileinfo
					},
					indicator : false,
					showError : true
				}, function(reqId, res, err, xhr) {
					__fileinfo.clearData();
				});
			}
		}
		oFile.removeFile();
		if (typeof (callback) == "function") {
			eval("callback(oRes)");
		}
	});
}

/**
 * Excel Upload
 *
 * @param oFile
 *            File Object
 * @param callback
 *            Call Back Function
 * @returns
 */
function g_uploadexcel(oFile, callback) {
	var binddataset = oFile.getBinddataset();
	if (g_isNull(binddataset) || g_isEmpty(binddataset)) {
		return false;
	}
	var opt = {};
	oFile.setAction(g_nvl(global("G_URL"), "../") + "jsp/excelUpload.jsp");
	oFile.send(opt, function(sRes) {
		oFile.removeFile();
		var oRes = JSON.parse(sRes);
		if (oRes.Error != '0') {
			// xwing.Dialog.alert('ResultCode : ' + oRes.ResultCode + '<br/>' +
			// 'ResultMessage : ' + oRes.ResultMessage);
			xwing.Dialog.alert(oRes.ResultMessage);
			return false;
		}
		if (typeof (callback) == "function") {
			eval("callback(oRes)");
		}
	});
}

/**
 * 쿠키를 만드는 함수입니다.
 *
 * @param cName
 *            쿠키이름 : 쿠키이름을 영문으로 넣어주세요.
 * @param cValue
 *            쿠키값 : 쿠키의 값을 문자열로 넣어주세요.
 * @param cDay
 *            만료일 : 쿠키의 만료일을 숫자로 넣어주세요(-1 : 삭제)
 */
function g_setCookie(cName, cValue, cDay) {
	var expire = new Date();
	expire.setDate(expire.getDate() + cDay);
	// 한글 깨짐을 막기위해 escape(cValue)를 합니다.
	cookies = cName + '=' + escape(cValue) + '; path=/ ';
	if (typeof cDay != 'undefined')
		cookies += ';expires=' + expire.toGMTString() + ';';
	document.cookie = cookies;
}

/**
 * 쿠키값을 가져오는 함수입니다.
 *
 * @param cName
 *            쿠키이름 : 쿠키이름을 영문으로 넣어주세요.
 */
function g_getCookie(cName) {
	cName = cName + '=';
	var cookieData = document.cookie;
	var start = cookieData.indexOf(cName);
	var cValue = '';
	if (start != -1) {
		start += cName.length;
		var end = cookieData.indexOf(';', start);
		if (end == -1)
			end = cookieData.length;
		cValue = cookieData.substring(start, end);
	}
	return unescape(cValue);
}

/**
 * 콘솔로그를 남긴다.
 *
 * @param o
 *            Object
 */
function g_log(o) {
}


/**
 * Dataset 에서 지정한 Row를 추가 후 Column 에 값을 변경.
 *
 * @param arguments
 * @returns {Boolean}
 */
function g_mulitSetValue() {
	var argLen = arguments.length;
	if (argLen % 2 != 0) {
		return false;
	}
	if (argLen < 4) {
		return false;
	}

	var dsObj = arguments[0];
	if (!(dsObj instanceof xwing.Dataset)) {
		return false;
	}

	var nRow = arguments[1];
	if (typeof (nRow) != "number") {
		return false;
	}

	var colInfo = dsObj.getColumnInfo();
	var array = colInfo.join().split(",");
	for ( var i = 0 ; i < array.length ; i++) {
		array[i] = "";
	}

	if (dsObj.size() <= 0 || (dsObj.size() - 1) < nRow) {
		dsObj.addRow(array);
		nRow = (dsObj.size()) - 1;
	} else {
		dsObj.insertRow(nRow, array);
	}

	for ( var i = 2; i < argLen - 1; i += 2) {
		var colId = arguments[i];
		if (colInfo.indexOf(colId) > 0) {
			var value = arguments[i + 1];
			value = ((value == null || value == undefined) ? "" : value);
			dsObj.setValue(nRow, colId, value);
		}
	}
	return true;
}

/**
 * Dataset의 마지막에 새로운 레코드를 추가
 *
 * @param dsObj
 * @returns
 */
function g_addRow(dsObj) {
	if (!(dsObj instanceof xwing.Dataset)) {
		return -1;
	}

	var colInfo = dsObj.getColumnInfo();
	var array = colInfo.join().split(",");
	for ( var i = 0 ; i < array.length ; i++) {
		array[i] = "";
	}

	dsObj.addRow(array);
	return dsObj.size() - 1;
}

/**
 * Dataset의 해당 컬럼의 값들을 문자로 리턴
 *
 * @param dataset
 * @param column
 * @returns {String}
 */
function g_getValues(dataset, column) {
	var v_result = "";
	for ( var i = 0; i < dataset.size(); i++) {
		v_result += dataset.getValue(i, column);
		v_result += i < dataset.size() - 1 ? "," : "";
	}
	return v_result;
}

function a_isNull(value) {
	return (value == null || typeof (value) == "undefined");
}

function a_nvl() {
	if (a_isNull(arguments) == true)
		return null;
	for ( var i = 0; i < arguments.length; i++) {
		if (a_isNull(arguments[i]) == false)
			return arguments[i];
	}

	return null;
}

/* **********************************************************
 * tenant column for datagrid
 * 전체 테넌트 권한이 아닌 사용자에게 그리드에서 테넌트 컬럼을 보이지 않게 하기 위해 호출
 * 테넌트 컬럼은 서머리에서 머지되어있어야 함
 *************************************************************/
function g_checkTenant(datagrid){
	var hasSum = eval(datagrid).getCell('summary',0);
	var sumMerge = (typeof hasSum != 'undefined'?eval(datagrid).getCell('summary',0,0).getAttribute('colspan'):0);
	allTenant = global('G_TENANT_ID')==0;
	if(!allTenant){
		for(var idx=0; idx<eval(datagrid).getColumn().length; idx++){
			if(eval(datagrid).getCell('body',0,idx).getBindcolumn()=='tenant'){
				eval(datagrid).removeColumn(idx);
				sumMerge--;
				break;
			}
		}

		if(typeof hasSum != 'undefined') eval(datagrid).getCell('summary',0,0).setAttribute('colspan',sumMerge);
	}
	return (allTenant=allTenant?0:1);
}

/* **********************************************************
 * tenant column for datagrid
 * 전체 테넌트 권한이 아닌 사용자에게 그리드에서 테넌트 컬럼을 보이지 않게 하기 위해 호출
 * 테넌트 컬럼은 서머리에서 머지되어있어야 함
 *************************************************************/
function checkTenant(datagrid){
	var hasSum = eval(datagrid).getCell('summary',0);
	var sumMerge = (typeof hasSum != 'undefined'?eval(datagrid).getCell('summary',0,0).getAttribute('colspan'):0);
	allTenant = global('G_TENANT_ID')==0;
	if(!allTenant){
		for(var idx=0; idx<eval(datagrid).getColumn().length; idx++){
			if(eval(datagrid).getCell('body',0,idx).getBindcolumn()=='tenant'){
				eval(datagrid).removeColumn(idx);
				sumMerge--;
				break;
			}
		}

		if(typeof hasSum != 'undefined') eval(datagrid).getCell('summary',0,0).setAttribute('colspan',sumMerge);
	}
	return (allTenant=allTenant?0:1);
}


//Xwing._getURL = function(url){ //Xwing이 정의도지 않았습니다 에러. 20180518
//    var baseurl = url;
//    var urlArray = url.split("::");
//
//    if (urlArray.length == 2) {
//        var prefix = urlArray[0]
//            , cnt = 1;
//
//        baseurl = "";
//        baseurl += xwing.Xwing.config.transaction.url[prefix].baseUrl || document.location.pathname;
//        baseurl += "/" + urlArray[1];
//
//        while(true){
//            if(xwing.Xwing.config.transaction.url[prefix]){
//                var tmpurl = xwing.Xwing.config.transaction.url[prefix].baseUrl || document.location.pathname;
//                if( tmpurl.indexOf(window.location.hostname) != -1){
//                    baseurl = "";
//                    baseurl += xwing.Xwing.config.transaction.url[prefix].baseUrl || document.location.pathname;
//                    baseurl += "/" + urlArray[1];
//                    break;
//                }else{
//                    prefix += cnt;
//                    cnt++;
//                }
//            }else
//                break;
//        }
//    }
//    return baseurl;
//
//};

function g_getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function constContext(){
	//document.getElementById(wId).oncontextmenu = function(){return false;};
	document.body.oncontextmenu = function(){return false;};
}

//엑셀버튼아이디, visible여부, main의 top볼수있는지 여부
function excelDownAuth(btnId,v,main){
	v = v == undefined? true : v;
	main = main == undefined? true : main;
	if(main){
		var downPossible = top.$DS_GRADEAUTH.getValue(top.$DS_GRADEAUTH.indexOfRow("CODEID",global("G_USER_GRADE")),"ETC4");
	}else{
		var downPossible = parent.$DS_GRADEAUTH.getValue(parent.$DS_GRADEAUTH.indexOfRow("CODEID",parent.user_grade),"ETC4");
	}
	if(downPossible == "Y"){
		btnId.setEnabled(true);
	}else{
		btnId.setEnabled(false);
		if(!v){
			btnId.setVisible(false);
		}
	}

}
//},500);
