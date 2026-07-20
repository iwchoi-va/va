xwing.widget.Splitbar._model = {
	attributes : {
		width : {
			defaultValue : '10'
		},
		height : {
			defaultValue : '200'
		},
		value : {
			defaultValue : '0'
		},
		bindwidget : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		bartype : {
			type : 'string',
			defaultValue : 'horizontal',
			valueDomain : 'horizontal|vertical'
		},
		icon : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		iconcursor : {
			type : 'string',
			defaultValue : 'default',
			valueDomain : 'default|auto|crosshair|e-resize|help|move|n-resize|ne-resize|nw-resize|pointer|progress|s-resize|se-resize|sw-resize|text|w-resize|wait'
		},
		gutter : {
			type : 'string',
			defaultValue : '0 0 0 0',
			valuDomain : null
		},
		mask : null,
		binddataset : null,
		bindcolumn : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		draggable : null,
		droppable : null
	},
	events : [ 'click', 'dblclick','dragstart', 'drag', 'dragstop', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'select', 'resize' ]
};
