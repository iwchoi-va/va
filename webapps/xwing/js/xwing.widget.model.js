xwing.widget.Widget._model = {
	attributes : {
		id : {
			type : 'string',
			defaultValue : 'anonymous',
			valueDomain : null
		},
		top : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '(-|)+[0-9]+(px|)'
		},
		left : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '(-|)+[0-9]+(px|)'
		},
		width : {
			type : 'number',
			defaultValue : 100,
			valueDomain : '(-|)+[0-9]+(px|)'
		},
		height : {
			type : 'number',
			defaultValue : 100,
			valueDomain : '(-|)+[0-9]+(px|)'
		},
		enabled : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		fontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		fontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		fontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique'
		},
		fontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold|bolder|lighter'
		},
		fontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72'
		},
		fontdecoration : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|overline|underline|line-through'
		},
		bgimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		bgimagerepeat : {
			type : 'string',
			defaultValue : 'no-repeat',
			valueDomain : 'no-repeat|repeat-x|repeat-y|repeat'
		},
		bgimagealign : {
			type : 'string',
			defaultValue : 'left top',
			valueDomain : 'left top|left center|left bottom|right top|right center|right bottom|center top|center center|center bottom'
		},
		bgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		bggradientcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		bggradientdir : {
			type : 'string',
			defaultValue : 'vertical',
			valueDomain : 'vertical|horizontal'
		},
		bordercolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		borderstyle : {
			type : 'string',
			defaultValue : 'solid',
			valueDomain : 'none|dotted|dashed|solid|double'
		},
		tabindex : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '[0-9]+'
		},
		styles : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		anchor : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|all|top|top right|top right bottom|top right left|top bottom|top bottom left|top left|right|right bottom|right bottom left|right left|bottom|bottom left|left'
		},
		visible : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		borderwidth : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+'
		},
		textpadding : {
			type : 'string',
			defaultValue : '0 0 0 0',
			valueDomain : null
		},
		cursor : {
			type : 'string',
			defaultValue : 'default',
			valueDomain : 'default|auto|crosshair|e-resize|help|move|n-resize|ne-resize|nw-resize|pointer|progress|s-resize|se-resize|sw-resize|text|w-resize|wait'
		},
		tooltiptext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		opacity : {
			type : 'string',
			defaultValue : 1,
			valueDomain: null
		},
		shadow : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		draggable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		droppable : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		}
	},
	events : [ 'click', 'mouseenter', 'mouseleave', 'focusin', 'focusout' ]
};
