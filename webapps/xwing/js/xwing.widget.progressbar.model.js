xwing.widget.Progressbar._model = {
	attributes : {
		width : {
			defaultValue : '200'
		},
		height : {
			defaultValue : '22'
		},
		bordercolor : {
			defaultValue : '#D0D0D0'
		},
		max : {
			type : 'number',
			defaultValue : 100,
			valueDomain : '[0-9]+'
		},
		bgcolor : {
			defaultValue : 'white'
		},
		barbgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		barbggradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		barbgimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		animation : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		direction : {
			type : 'string',
			defaultValue : 'horizontal',
			valueDomain : 'horizontal|vertical'
		},
		expr : {
			type : 'string',
			defaultValue : null,
			valueDomain : 'value,maxValue'
		},
		step : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+'
		},
		label : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		mask : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		draggable : null,
		droppable : null
	},
	events : [ 'click', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};