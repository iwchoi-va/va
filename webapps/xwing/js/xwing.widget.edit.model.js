xwing.widget.Edit._model = {
	attributes : {
		height : {
			defaultValue : '22'
		},
		imemode : {
			type : 'string',
			defaultValue : 'auto',
			valueDomain : 'auto|active|inactive|disabled'
		},
		inputmode : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|capitalize|lowercase|uppercase'
		},
		halign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|center|right'
		},
		maxlength : {
			type : 'number',
			defaultValue : null,
			valueDomain : null
		},
		mode : {
			type : 'string',
			defaultValue : 'text',
			valueDomain : 'text|password'
		},
		readonly : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		placeholder : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},	
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		cursor : null,
		draggable : null
	},
	events : [ 'click', 'dblclick', 'changing', 'change',  'focusin', 'focusout', 'keydown', 'keyup', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
