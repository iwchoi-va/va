xwing.widget.Checkbox._model = {
	attributes : {
		width : {
			defaultValue : '120'
		},
		height : {
			defaultValue : '22'
		},
		borderstyle : {
			defaultValue : 'none'
		},
		borderwidth : {
			defaultValue : '0'
		},
		value : {
			defaultValue : 'true'
		},
		truevalue : {
			type : 'string',
			defaultValue : 'true',
			valueDomain : null
		},
		falsevalue : {
			type : 'string',
			defaultValue : 'false',
			valueDomain : null
		},
		label : {
			type : 'string',
			defaultValue : '',
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
		iconalign : {
			type: 'string',
			defaultValue : 'left',
			valueDomain : 'left|right'
		},
		iconimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		iconmargin : {
			type:'string',
			defaultValue:'0 0 0 0',
			valueDomain:null
		},
		textpadding : {
			type : 'string',
			defaultValue : '0 5 0 5',
			valueDomain : null
		},
		textwrap :{
			type:'string',
			defaultValue:'pre',
			valueDomain:'none|pre|prewrap'
		},
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		mask : null
	},
	events : [ 'click',  'changing', 'change', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'resize' ]
};
