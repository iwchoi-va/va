xwing.widget.Combo._model = {
	attributes : {
		width : {
			defaultValue : '100'
		},
		height : {
			defaultValue : '22'
		},
		size : {
			type : 'number',
			defaultValue : '0',
			valueDomain : '[0-9]+'
		},
		textpadding : {
			defaultValue : '0 5 0 5'
		},		
		editable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		itemall : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		itembordercolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		itemheight : {
			type : 'number',
			defaultValue : '20',
			valueDomain : '(-|)+[0-9]+(px|)'
		},
		itempadding : {
			type : 'string',
			defaultValue : '0 0 0 5',
			valueDomain : null
		},
		itemfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		itemfontdecoration : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|overline|underline|line-through'
		},
		itemfontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		itemfontsize : {
			type : 'number',
			defaultValue : '11',
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72'
		},
		itemfontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique'
		},
		itemfontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold|bolder|lighter'
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
		itembggradientdir : {
			type : 'string',
			defaultValue : 'vertical',
			valueDomain : 'vertical|horizontal'
		},
		inputmode : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|uppercase|lowercase|capitalize'
		},
		multiselectable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		placeholder : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		flexibleitem : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		mask : null,
		draggable : null
	},
	events : [ 'click', 'changing', 'change', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'show', 'hide', 'select', 'resize' ]
};
