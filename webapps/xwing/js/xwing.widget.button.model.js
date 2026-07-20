xwing.widget.Button._model = {
	attributes : {
		height : {
			defaultValue : '22'
		},
		fontweight : {
			defaultValue : 'normal'
		},
		fontcolor : {
			defaultValue : null
		},
		icon : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		iconalign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|right'
		},
		iconmargin : {
			type : 'string',
			defaultValue : '0 0 0 0',
			valueDomain : null
		},
		textwrap : {
			type : 'string',
			defaultValue : 'pre',
			valueDomain : 'none|pre|prewrap'
		},
		mask : null,
		shadow : {
			defaultValue : '1 1 2 0 #d0d0d0'
		}
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
