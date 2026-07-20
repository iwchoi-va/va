xwing.widget.DataGridCell._model = {
	attributes : {
		displaytype : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|button|checkbox'
		},
		truevalue : {
			type : 'string',
			defaultValue : true,
			valueDomain : null
		},
		falsevalue : {
			type : 'string',
			defaultValue : false,
			valueDomain : null
		},
		checkboxcolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'BindData',
			control : 'dataset-column'
		},
		rowspan : {
			type : 'number',
			defaultValue : 1,
			valueDomain :'[0-9]+'
		},
		colspan : {
			type : 'number',
			defaultValue : 1,
			valueDomain :'[0-9]+'
		},
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
		expr : {
			type : 'string',
			defaultValue : null,
			valueDomain : 'cellObj,rowIdx'
		},
		text : {
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
		groupable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		exporttype : {
			type : 'string',
			defaultValue : 'string',
			valueDomain : 'string|number'
		},
		groupkey : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},
		groupsum : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},
		groupsumtype : {
			type : 'string',
			defaultValue : 'text',
			valueDomain : 'avg|count|sum|max|min|text'
		},
		groupsumrendering : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		changeedit : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		groupborderright : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		id : null,
		top : null,
		left : null,
		width : null,
		height : null,
		enabled : null,
		value : null,
		tabindex : null,
		bggradientdir : null,
		bordercolor : null,
		borderwidth : null,
		borderstyle : null,
		anchor : null,
		visible : null,
		tooltiptext : null,
		opacity : null,
		shadow : null,
		textpadding : null,
		draggable : null,
		droppable : null
	}
};
