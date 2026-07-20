xwing.widget.Label._model = {
	attributes : {
		width : {
			defaultValue : '100'
		},
		height : {
			defaultValue : '22'
		},
		value : {
			defaultValue : ''
		},
		borderstyle : {
			defaultValue : 'none'
		},
		borderwidth : {
			defaultValue : '0'
		},		
		bgimageedge : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
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
		icon : {
			type : 'string',
			defaultValue : '',
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
		textwrap :{
			type:'string',
			defaultValue:'pre',
			valueDomain:'none|pre|prewrap'
		}
	},
	events : [ 'click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
