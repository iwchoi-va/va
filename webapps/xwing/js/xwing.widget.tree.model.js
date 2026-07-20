xwing.widget.Tree._model = {
	attributes : {
		width : {
			defaultValue : '150'
		},
		height : {
			defaultValue : '200'
		},
		multiselectable : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},
		checkable : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},
		rowheight : {
			type : 'number',
			defaultValue : '20',
			valueDomain : null
		},
		idcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		pidcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		leafyncolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		textcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		nodeiconcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		foldericoncolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		textpadding : {
			defaultValue : '0 0 0 5'
		},
		leafmargin : {
			type : 'number',
			defaultValue : 0,
			valueDomain : null
		},
		treeline : {
			type : 'boolean',
			dafultValue : false,
			valueDomain : 'true|false'
		},
		treelineimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
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
		autoupdate : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		expr : {
			type : 'string',
			defaultValue : null,
			valueDomain : 'depth, rowIdx'
		},
		bindcolumn : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		value : null,
		mask : null,
		tooltiptext : null
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout','change', 'focusin', 'focusout', 'expand', 'collapse', 'mouseenter', 'mousemove', 'mouseleave', 'select', 'scroll', 'scrolltop', 'scrollbottom', 'resize' ]
};
