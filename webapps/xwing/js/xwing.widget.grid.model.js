xwing.widget.Grid._model=
{
	attributes:{
		width :{
			defaultValue:'500'
		},
		height:{
			defaultValue:'300'
		},
		headheight:{
			type : 'string',
			defaultValue : '25',
			valueDomain :'[0-9]+'
		},
		rowheight:{
			type: 'string',
			defaultValue : '22',
			valueDomain :'[0-9]+'
		},
		autofit : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		sortable: {
			type:'boolean',
			defaultValue : false,
			valueDomain :'true|false'
		},
		resizable:{
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		pagenavi:{
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		footerheight :{
			type : 'string',
			defaultValue : '30',
			valueDomain : '[0-9]+'
		},
		totalpage:{
			type : 'number',
			defaultValue :1,
			valueDomain :'[0-9]+'
		},
		currentpage:{
			type : 'number',
			defaultValue : 1,
			valueDomain :'[0-9]+'
		},
		emptymessage :{
			type :'string',
			defaultValue :'No data result.',
			valueDomain : null
		},
		editwhen :{
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|click|dblclick'
		},
		editselect :{
			type : 'string',
			defaultValue : 'row',
			valueDomain : 'row|cell'
		},
		editsuppress : {
			type : 'boolean',
			defaultValue : 'true',
			valueDomain : 'true|false'
		},
		oddcolor:{
			type:'string',
			defaultValue:null,
			valueDomain:null
		},
		evencolor:{
			type:'string',
			defaultValue:null,
			valueDomain:null
		},
		headcolor :{
			type:'string',
			defaultValue:null,
			valueDomain:null
		},
		headgradientcolor :{
			type:'string',
			defaultValue:null,
			valueDomain:null
		},
		headtextwrap : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false'
		},
		rownum :{
			type:'boolean',
			defaultValue:false,
			valueDomain:'true|false'
		},
		checkbox :{
			type:'boolean',
			defaultValue:false,
			valueDomain:'true|false'
		},
		checkboxfield:{
			type:'string',
			defaultValue:'cell',
			valueDomain:'cell|row'
		},
		expr:{
			type:'string',
			defaultValue:null,
			valueDomain:'rowIdx,colIdx,grid,summary'
		},
		summary :{
			type:'boolean',
			defaultValue:false,
			valueDomain:'true|false'
		},
		headfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		headfontdecoration : {
			type : 'string',
			defaultValue : 'none',
			valueDomain : 'none|overline|underline|line-through'
		},
		headfontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null
		},
		headfontsize : {
			type : 'number',
			defaultValue : '11',
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72'
		},
		headfontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique'
		},
		headfontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold|bolder|lighter'
		},
		textwrap :{
			type:'string',
			defaultValue:'pre',
			valueDomain:'none|pre|prewrap'
		},
		multiselectable : {
			type:'boolean',
			defaultValue:false,
			valueDomain:'true|false'
		},
		movecolumn :{
			type:'boolean',
			defaultValue:false,
			valueDomain:'true|false'
		},
		bindcolumn : null,
		value : null,
		domaindataset : null,
		domaincodecolumn :null,
		domaintextcolumn :null,
		mask : null,
		fontfamily : null,
		fontcolor : null,
		fontstyle: null,
		fontweight: null,
		fontsize : null,
		fontdecoration : null,
		textpadding : null,
		tooltiptext : null
	},
	events:['click', 'dblclick', 'dragstart', 'dragging', 'dragend', 'dropping', 'dropin', 'dropout', 'focusin', 'focusout', 'mouseenter', 'mousemove', 'mouseleave', 'pageclick', 'resize']	
};