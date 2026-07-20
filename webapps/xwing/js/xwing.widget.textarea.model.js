xwing.widget.TextArea._model = {
	attributes : {
		width : {
			defaultValue : '120'
		},
		height : {
			defaultValue : '80'
		},
		imemode : {
			type : 'string',
			defaultValue : 'auto',
			valueDomain : 'auto|active|inactive|disabled'
		},
		inputmode : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|capital|lowercase|uppercase'
		},
		maxlength : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		placeholder : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		readonly : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		textpadding : {
			defaultValue : '5 5 5 5'
		},
		mask : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		cursor : null,
		draggable : null
	},
	events : [ 'click', 'dblclick', 'changing', 'change', 'focusin', 'focusout', 'keydown', 'keyup', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
