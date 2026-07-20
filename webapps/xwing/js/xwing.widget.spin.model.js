xwing.widget.Spin._model = {
	attributes : {
		width : {
			defaultValue : '100'
		},
		height : {
			defaultValue : '22'
		},
		value : {
			defaultValue : '0'
		},
		circulation : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : "true|false"
		},
		comma : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		editable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : "true|false"
		},
		max : {
			type : 'string',
			defaultValue : '100',
			valueDomain : null
		},
		min : {
			type : 'string',
			defaultValue : '0',
			valueDomain : null
		},
		step : {
			type : 'string',
			defaultValue : '1',
			valueDomain : null
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},		
		mask : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		draggable : null
	},
	events : [ 'click', 'changing', 'change', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
