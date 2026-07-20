xwing.widget.Slider._model = {
	attributes : {
		width : {
			defaultValue : '200'
		},
		height : {
			defaultValue : '5'
		},
		value : {
			defaultValue : '0'
		},
		direction : {
			type : 'string',
			defaultValue : 'horizontal',
			valueDomain : 'horizontal|vertical'
		},
		exprthumblabel:{
			type:'string',
			defaultValue:null,
			valueDomain:'value,maxValue'
		},
		max : {
			type : 'number',
			defaultValue : 100,
			valueDomain : null
		},
		min : {
			type : 'number',
			defaultValue : 0,
			valueDomain : null
		},
		sidemargin : {
			type : 'number',
			defaultValue : 7,
			valueDomain : null
		},
		step : {
			type : 'number',
			defaultValue : 1,
			valueDomain : null
		},
		pointlabel : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		thumblabel : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		thumbimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		thumbleft : {
			type : 'number',
			defaultValue : -10,
			valueDomain : null
		},
		thumbtop : {
			type : 'number',
			defaultValue : -6,
			valueDomain : null
		},
		mask : null,
		binddataset : null,
		bindcolumn : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		fontfamily : null,
		fontcolor : null,
		fontstyle : null,
		fontweight : null,
		fontsize : null,
		fontdecoration : null,
		shadow : null,
		textpadding : null,
		draggable : null,
		droppable : null
	},
	events : [ 'click', 'changing','change', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'slide', 'resize' ]
};
