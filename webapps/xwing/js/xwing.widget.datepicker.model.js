xwing.widget.Datepicker._model = {
	attributes : {
		width : {
			defaultValue : '100'
		},
		height : {
			defaultValue : '22'
		},
		format : {
			type : 'string',
			defaultValue : 'yyyy-mm-dd',
			valueDomain : 'yyyy-mm-dd|yyyy/mm/dd|mm/dd/yyyy|dd/mm/yyyy'
		},
		value : {
			defaultValue : ''
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},
		daybgcolor : {
			value : 'string',
			defaultValue : null,
			valueDomain : null
		},
		daybordercolor : {
			value : 'string',
			defaultValue : null,
			valueDomin : null
		},
		dayfontcolor : {
			value : 'string',
			defaultValue : null,
			valueDomain : null
		},
		showothermonth : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		othermonthbgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		othermonthbordercolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		othermonthfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		datecolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		bgcolorcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		bordercolorcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		fontcolorcolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		placeholder : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		domaincodecolumn : null,
		domaintextcolumn : null,
		mask : null,
		draggable : null
	},
	events : [ 'click',  'changing', 'change', 'focusin', 'focusout', 'keydown', 'keyup', 'mouseenter','mousemove', 'mouseleave',  'show', 'hide', 'select', 'resize' ]
};
