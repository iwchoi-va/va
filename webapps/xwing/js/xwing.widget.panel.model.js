xwing.widget.Panel._model = {
	attributes : {
		width : {
			defaultValue : '300'
		},
		height : {
			defaultValue : '200'
		},
		url : {
			type : 'string',
			valueDomain : null,
			defaultValue : null
		},
		scroll : {
			type : 'boolean',
			valueDomain : 'true|false',
			defaultValue : true
		},
		borderstyle : {
			defaultValue : 'none'
		},
		borderwidth : {
			defaultValue : '0'
		},			
		fontfamily : null,
		fontsize : null,
		fontstyle : null,
		fontweight : null,
		fontdecoration : null,
		fontcolor : null,
		textpadding : null,
		tooltiptext : null
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
