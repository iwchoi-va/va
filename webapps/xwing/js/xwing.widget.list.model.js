xwing.widget.List._model = {
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
		itemheight : {
			type : 'number',
			defaultValue : '22',
			valueDomain : null
		},
		iconcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		titlecolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		contentcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		footercolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		expr : {
			type : 'string',
			defaultValue : null,
			valueDomain : 'rowIdx,gubun,value'
		},
		textpadding : {
			defaultValue : '4 4 4 4'
		},	
		itembgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		itembggradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		itembordercolor : {
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
		tooltiptext : null,
		draggable : null,
		droppable : null
	},
	events : [ 'click', 'dblclick', 'changing', 'change', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'select', 'scroll', 'scrolltop', 'scrollbottom', 'resize' ]

};
