xwing.widget.DataGrid._model = {
	attributes : {
		width : {
			defaultValue : '500'
		},
		height : {
			defaultValue : '300'
		},
		autofit : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		sortable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		resizable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		pageable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		pagesize : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+'
		},
		pagenum : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+'
		},
		emptymessage : {
			type : 'string',
			defaultValue : 'No data available.',
			valueDomain : null
		},
		editwhen : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|click|dblclick'
		},
		editsuppress : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		oddcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		evencolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		rownum : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		rightselection : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		checkbox : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		multiselectable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		movecolumn : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		selectedrange : {
			type : 'string',
			defaultValue : 'row',
			valueDomain : 'row|cell'
		},
		textwrap : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|pre|prewrap'
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},
		headcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		headgradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		summarycolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		summarygradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		virtual : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		groupable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		groupheaderexpr : {
			type : 'string',
			defaultValue : null,
			valueDomain : 'columnTitle, groupTitle, depth'
		},
		datadraggable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		datadroppable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		visiblecontextmenu : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		topsummary : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		selectcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		bindcolumn : null,
		value : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		mask : null,
		fontfamily : null,
		fontcolor : null,
		fontstyle : null,
		fontweight : null,
		fontsize : null,
		fontdecoration : null,
		tooltiptext : null
	},
	events : [ 'mouseup', 'click', 'changeheadercheckbox', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'pageclick', 'resize' ]
};