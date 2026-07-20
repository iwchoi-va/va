xwing.widget.Tab._model = {
	attributes : {
		width : {
			defaultValue : '500'
		},
		height : {
			defaultValue : '300'
		},
		active : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '[0-9]'
		},
		handlealign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|right'
		},
		handlewidth : {
			type : 'string',
			defaultValue : null,
			valueDomain : '[0-9]'
		},
		handleheight : {
			type : 'string',
			defaultValue : '22',
			valueDomain : '[0-9]+'
		},
		multiline : {
			type : 'boolean',
			defaultValue : 'false',
			valueDomain : 'true|false'
		},	
		deactivefontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		deactivefontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		deactivefontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique'
		},
		deactivefontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold|bolder|lighter'
		},
		deactivefontsize : {
			type : 'number',
			defaultValue : '11',
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72'
		},
		deactivefontdecoration : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|overline|underline|line-through'
		},
		deactivebgimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		deactivebgimagerepeat : {
			type : 'string',
			defaultValue : 'no-repeat',
			valueDomain : 'no-repeat|repeat-x|repeat-y|repeat'
		},
		deactivebgimagealign : {
			type : 'string',
			defaultValue : 'left top',
			valueDomain : 'left top|left center|left bottom|right top|right center|right bottom|center top|center center|center bottom'
		},
		deactivebgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		deactivebggradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		deactivebggradientdir : {
			type : 'string',
			defaultValue : 'vertical',
			valueDomain : 'vertical|horizontal'
		},
		textpadding : null,
		tooltiptext : null
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'change', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};