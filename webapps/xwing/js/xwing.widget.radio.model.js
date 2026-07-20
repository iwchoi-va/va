xwing.widget.Radio._model = {
	attributes : {
		columncount : {
			type : 'string',
			defaultValue : '0',
			valueDomain : '[0-9]'
		},
		width : {
			defaultValue : '50'
		},
		height : {
			defaultValue : '50'
		},	
		borderstyle : {
			defaultValue : 'none'
		},
		borderwidth : {
			defaultValue : '0'
		},		
		iconalign : {
			type : 'string',
			defaultValue : 'left',
			valueDomain : 'left|right'
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
		iconimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		iconmargin : {
			type : 'string',
			defaultValue : '0 0 0 0',
			valueDomain : null
		},
		textpadding : {
			type : 'string',
			defaultValue : '0 0 0 5',
			valueDomain : null
		},
		textwrap :{
			type:'string',
			defaultValue:'pre',
			valueDomain:'none|pre|prewrap'
		},
		itemheight : {
			type : 'string',
			defaultValue : null
		},
		mask : null,
		draggable : null
	},
	events : [ 'click', 'changing','change', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};