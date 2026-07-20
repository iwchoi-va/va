xwing.Dialog._model = {
	attributes : {
		width : {
			defaultValue : '300'
		},
		height : {
			defaultValue : '200'
		},
		top : {
			defaultValue : '-1'
		},
		left : {
			defaultValue : '-1'
		},
		modal : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},
		animation : {
			type : 'boolean',
			defaultValue : 'true',
			valueDomain : 'true|false'
		},
		url : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		resizable : {
			type : 'boolean',
			defaultValue : 'true',
			valueDomain : 'true|false',
			control : 'combo'
		},
		draggable : {
			type : 'boolean',
			defaultValue : 'true',
			valueDomain : 'true|false'
		},
		title : {
			type : 'string',
			defaultValue : 'xwing dialog',
			valueDomain : null
		},
		content : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		buttonbarheight : {
			type : 'string',
			defaultValue : '25',
			valueDomain : '[0-9]+(px|)'
		},
		param : {
			type : 'string',
			valueDomain : null,
			defaultValue : null
		},
		tooltiptext : null,
		opacity : null,
		draggable : null,
		droppable : null
	},
	events : [ 'mouseenter', 'mouseleave', 'close', 'beforeclose', 'dragstart', 'drag', 'dragstop', 'resizestart', 'resize', 'resizestop' ]
};