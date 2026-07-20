xwing.widget.File._model = {
	attributes : {
		height : {
			defaultValue : '22'
		},
		action : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		filenamecolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		filesizecolumn : {
			type : 'string',
			defaultValue : '',
			valueDomain : null,
			control : 'dataset-column',
			category : 'BindData'
		},
		mask : null,
		bindcolumn : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null,
		draggable : null
	},
	events:['click', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'select', 'resize']
};
