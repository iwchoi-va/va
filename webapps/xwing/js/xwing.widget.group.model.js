xwing.widget.Group._model = {
	attributes : {
		width : {
			defaultValue : '400'
		},
		height : {
			defaultValue : '300'
		},
		title : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		titlealign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|right'
		},
		borderstyle : {
			defaultValue : 'solid'
		},
		borderwidth : {
			defaultValue : '1'
		},		
		titlebgcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
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
			defaultValue : '11',
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72'
		},
		fontdecoration : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|overline|underline|line-through'
		},
		textpadding : {
			type : 'string',
			defaultValue : '0 6 0 6',
			valueDomain : null
		},
		scroll : {
			defaultValue : false
		},
		url : null,
		tooltiptext : null
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
