xwing.widget.Chart._model = {
	attributes : {
		bgimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'background.image.src'
		},
		bgimagealign : {
			type : 'string',
			defaultValue : 'left top',
			valueDomain : 'left top|left center|left bottom|right top|right center|right bottom|center top|center center|center bottom',
			link : 'background.image.align'
		},
		fontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'font.family'
		},
		fontcolor : {
			type : 'string',
			defaultValue : '#555',
			valueDomain : null,
			link : 'font.color'
		},
		fontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'font.style'
		},
		fontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'font.weight'
		},
		fontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'font.size'
		},
		fontdecoration : null,
		titletext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'title.text'
		},
		titlefontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'title.font.family'
		},
		titlefontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'title.font.color'
		},
		titlefontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'title.font.style'
		},
		titlefontweight : {
			type : 'string',
			defaultValue : 'bold',
			valueDomain : 'normal|bold',
			link : 'title.font.weight'
		},
		titlefontsize : {
			type : 'number',
			defaultValue : 13,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'title.font.size'
		},
		titlemargin : {
			type : 'string',
			defaultValue : '0 0 20 0',
			valueDomain : null,
			link : 'title.margin'
		},
		subtitletext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'subtitle.text'
		},		
		subtitlefontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'subtitle.font.family'
		},
		subtitlefontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'subtitle.font.color'
		},
		subtitlefontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'subtitle.font.style'
		},
		subtitlefontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'subtitle.font.weight'
		},
		subtitlefontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'subtitle.font.size'
		},
		chartgutter : {
			type : 'string',
			defaultValue : '10 10 10 10',
			valueDomain : null,
			link : 'gutter'
		},
		galleryshadow : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false',
			link : 'shadow.visible'
		},		
		legend : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false',
			link : 'legend.visible'
		},
		legendalign : {
			type : 'string',
			defaultValue : 'bottom',
			valueDomain : 'top|right|bottom|left',
			link : 'legend.align'
		},
		legendbordercolor : {
			type : 'string',
			defaultValue : '#000',
			valueDomain : null,
			link : 'legend.border.color'
		},
		legendborderwidth : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+',
			link : 'legend.border.width'			
		},
		legendbgcolor : {
			type : 'string',
			defaultValue : '#fff',
			valueDomain : null,
			link : 'legend.background.color'
		},
		legendfontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'legend.font.family'
		},
		legendfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'legend.font.color'
		},
		legendfontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'legend.font.style'
		},
		legendfontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'legend.font.weight'
		},
		legendfontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'legend.font.size'
		},
		legendmargin : {
			type : 'string',
			defaultValue : '10 10 10 10',
			valueDomain : null,
			link : 'legend.margin'
		},
		axistitlefontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'axis.title.font.family'
		},
		axistitlefontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'axis.title.font.color'
		},
		axistitlefontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'axis.title.font.style'
		},
		axistitlefontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'axis.title.font.weight'
		},
		axistitlefontsize : {
			type : 'number',
			defaultValue : 12,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'axis.title.font.size'
		},		
		axistitlemargin : {
			type : 'string',
			defaultValue : '10 10 10 10',
			valueDomain : null,
			link : 'axis.title.margin'
		},		
		axislabelfontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'axis.label.font.family'
		},
		axislabelfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'axis.label.font.color'
		},
		axislabelfontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'axis.label.font.style'
		},
		axislabelfontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'axis.label.font.weight'
		},
		axislabelfontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'axis.label.font.size'
		},		
		axislabelmargin : {
			type : 'string',
			defaultValue : '5 5 5 5',
			valueDomain : null,
			link : 'axis.label.margin'
		},
		tickmark : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false',
			link : 'axis.tickmark.visible'
		},
		xaxistitletext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'xAxis.title.text'
		},
		xaxisrotate : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'xAxis.label.rotate'
		},
		yaxistitletext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'yAxis.title.text'			
		},
		yaxislabeldecimals : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '[0-9]+',
			link : 'yAxis.label.decimals'
		},
		y2axistitletext : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'y2Axis.title.text'			
		},
		y2axislabeldecimals : {
			type : 'number',
			defaultValue : 0,
			valueDomain : '[0-9]+',
			link : 'y2Axis.label.decimals'
		},		
		flexiblemin : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false',
			link : 'flexiblemin'
		},
		gridbordercolor : {
			type : 'string',
			defaultValue : '#666',
			valueDomain : null,
			link : 'grid.border.color'
		},
		gridborderwidth : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+',
			link : 'grid.border.width'	
		},
		gridbgcolor : {
			type : 'string',
			defaultValue : '#fff',
			valueDomain : null,
			link : 'grid.background.color'
		},
		gridbgopacity : {
			type : 'number',
			defaultValue : 1,
			valueDomain: null,
			link : 'grid.background.opacity'
		},
		gridbgimage : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'grid.background.image.src'
		},
		gridbgimagealign : {
			type : 'string',
			defaultValue : 'center center',
			valueDomain : 'left top|left center|left bottom|right top|right center|right bottom|center top|center center|center bottom',
			link : 'grid.background.image.align'
		},
		gridlineborderstyle : {
			type : 'string',
			defaultValue : 'dot',
			valueDomain : 'none|shortdash|shortdot|shortdashdot|shortdashdotdot|dot|dash|longdash|dashdot|longdashdot|longdashdotdot',
			link : 'grid.line.border.style'
		},
		gridlinebordercolor : {
			type : 'string',
			defaultValue : '#000',
			valueDomain : null,
			link : 'grid.line.border.color'
		},
		gridlineborderwidth : {
			type : 'number',
			defaultValue : 1,
			valueDomain : '[0-9]+',
			link : 'grid.line.border.width'
		},
		gridlinecolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'grid.line.color'
		},
		gridlineopacity : {
			type : 'number',
			defaultValue : 1,
			valueDomain: null,
			link : 'grid.line.opacity'
		},
		gridlinedirection : {
			type : 'string',
			defaultValue : 'all',
			valueDomain : 'none|vertical|horizontal|all',
			link : 'grid.line.direction'
		},
		pointlabel : {
			type : 'boolean',
			defaultValue : true,
			valueDomain : 'true|false',
			link : 'pointlabel.visible'
		},
		innerpointlabel : {
			type : 'boolean',
			defaultValue : false,
			valueDomain : 'true|false',
			link : 'pointlabel.innerpointlabel'
		},
		pointlabelfontfamily : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'pointlabel.font.family'
		},
		pointlabelfontcolor : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			link : 'pointlabel.font.color'
		},
		pointlabelfontstyle : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|italic|oblique',
			link : 'pointlabel.font.style'
		},
		pointlabelfontweight : {
			type : 'string',
			defaultValue : 'normal',
			valueDomain : 'normal|bold',
			link : 'pointlabel.font.weight'
		},
		pointlabelfontsize : {
			type : 'number',
			defaultValue : 11,
			valueDomain : '8|9|10|11|12|13|15|16|18|20|22|24|26|28|36|48|72',
			link : 'pointlabel.font.size'
		},
		categorycolumn : {
			type : 'string',
			defaultValue : null,
			valueDomain : null,
			category : 'BindData',
			control : 'dataset-column',
			link : 'xAxis.label.bindcolumn'
		},		
		bgimagerepeat : null,
		tabindex : null,
		textpadding : null,
		tooltiptext : null,
		cursor : null,
		value : null,
		mask : null,
		bindcolumn : null,
		domaindataset : null,
		domaincodecolumn : null,
		domaintextcolumn : null
	},
	events : [ 'click', 'mouseenter', 'mouseleave', 'focusin', 'focusout', 'resize' ]
};
