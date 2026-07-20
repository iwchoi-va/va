xwing.Dataset._model = {
	attributes : {
		id : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		enabled : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false'
		},
		description : {
			type : 'string',
			defaultValue : '',
			valueDomain : null
		},
		minicon : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		toogleicon : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		}
	},
	events : [ 'add', 'cursor', 'remove', 'reset', 'sort', 'update' ]
};
