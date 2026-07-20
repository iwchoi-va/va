xwing.widget.Gridcolumn._model = {
	attributes : {
		edittype : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|edit|combo|checkbox|radio|datepicker'
		},
		editoption : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		displaytype : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|button'
		},
		fixed : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		title : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		bindcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'BindData',
			control : 'parent-dataset-column'
		},
		domaindataset : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset'
		},
		domaincodecolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		domaintextcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'DomainData',
			control : 'dataset-column'
		},
		valign : {
			type : 'string',
			defaultValue : 'middle',
			valueDomain : 'top|middle|bottom'
		},
		halign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|center|right'
		},
		mask : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		suppress : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},
		top : null,
		left : null,
		height : null,
		enabled : null,
		value : null,
		tabindex : null,
		bggradientcolor : null,
		bggradientdir : null,
		bordercolor : null,
		borderwidth : null,
		borderstyle : null,
		anchor : null,
		visible : null,
		tooltiptext : null,
		opacity : null,
		shadow : null
	}
};
