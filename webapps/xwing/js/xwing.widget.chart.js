Class.define({
	Chart : {
		alias : 'chart',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Chart : function(json) {
			this._init(json);
		},
		statics : {
			create : function(json) {
				return new xwing.widget.Chart(json);
			}
		},
		prototypes : {
			_createPart : function() {
				this.holder = this._getJShell();
				this.holder.addClass('xw-chart xw-mod-border');
			},
			_initChart : function() {
				if (window.xwingIDE) {
					jQuery("<div class='xw-ide-chart'/>").appendTo(this.holder);
					this._branding();
				} else {
					this.chart = vchart.chart(this.holder, this.opts);
					
					if (this._opt.anchor != 'none') 
						return;
					
					var thisObj = this;
					this._initChartRender = function(e) {
						try {
							var test = thisObj.chart.paper.text(0, 0, "Loading..."), x = test.getBBox().x;
							thisObj.chart.redraw();
						} finally {
							test && test.remove();
							if (x && (x != NaN || x != 0)) {
								jQuery(window).unbind('resize', thisObj._initChartRender);
								delete thisObj._initChartRender;
							}
						}
					};
					jQuery(window).bind('resize', this._initChartRender);
				}
			},
			_initOption : function(){
				xwing.widget.Widget.prototype._initOption.call(this);
				this.opts = {};
				
				var thisObj = this;				
				for (var key in this._opt) {
					if (this.model.attributes[key] && this.model.attributes[key]['link']) {
						this._setLinkValue(key, this._opt[key]);
						
						(function(key) {
							var funcName = key.replace(/^\w/, key.match(/^\w/)[0].toUpperCase());
							thisObj["set" + funcName] = function(v) {
								thisObj._opt[key] = v;
								thisObj._setLinkValue(key, v);
							};
							thisObj["get" + funcName] = function() {
								return thisObj._opt[key];
							};
						})(key);
					}
				}
			},
			_render : function(){
				this._initDataset();
				this._doBounds();
				this._doBorder();
				this._doEnabled();
				this._doVisible();
				this._doOpacity();
				this._doBgcolor();
				this._initChart();
			},
			_doBounds : function() {
				xwing.widget.Widget.prototype._doBounds.call(this);
				if (this.chart) {
					this._showIndicator(true);
					this.chart.setSize(this._showIndicator, this);
				}
			},
			_showIndicator : function(visible) {
				if (visible) {
					this.holder.addClass('xw-disabled');
					xwing.widget.Widget.showIndicator("Loading chart. Please wait...", this);	
				} else {
					xwing.widget.Widget.hideIndicator(this);
					this.holder.removeClass('xw-disabled');
				}
			},
			_doBgcolor : function() {
				if (this._opt.bgcolor && this._opt.bggradientcolor) {
					var dir = this._opt.bggradientdir == 'vertical' ? '90' : '0';
					this._setLinkValue('bgcolor', dir + '-' + this._opt.bgcolor + '-' + this._opt.bggradientcolor, 'background.color');
				} else if (this._opt.bgcolor) {
					this._setLinkValue('bgcolor', this._opt.bgcolor, 'background.color');
				}
			},
			_doBggradientcolor : function() {
				this._doBgcolor();
			},
			_setLinkValue : function(k, v, link) {
				var attr = link || this.model.attributes[k]['link'];
				if (!attr) return;
				
				var holder = this.opts,
					ns = attr.split('.'),
					p = ns.pop(),					
					key;

				while (ns.length) {
					key = ns.shift();
					!holder[key] && (holder[key] = {});					
					holder = holder[key];
				}

				if (v == '' || v == null)
					delete holder[p];
				else {
					var type = this.model.attributes[k]['type'];

					if (type == 'boolean')
						holder[p] = xwing.Util.parseBoolean(v);
					else if (type == 'number')
						holder[p] = +v;
					else if (/\d+\s+\d+/.test(v)) {
						var pos = [ 'top', 'right', 'bottom', 'left' ],
							margin = v.split(/\s+/);
						
						holder[p] = {};
						for ( var i = 0, l = pos.length; i < l; i++) {
							margin[i] != undefined && (holder[p][pos[i]] = +margin[i]);
						}
					} else
						holder[p] = v;					
				}
			},
			draw : function(opts) {
				if (opts) {
					try {
						this.chart = this.chart || vchart.chart(this.holder);					
						opts = jQuery.extend(true, {}, this.opts, opts);
						this.chart.initOptions(opts);
						this._doValue();
					} catch (e) {
						Xwing.error(e);
					}
				}
			},
			redraw : function() {
				this._doValue();
			},
			remove : function() {
				this.chart && this.chart.remove();
				xwing.widget.Widget.prototype.remove.call(this);
			},
			clearData : function() {
				this.chart && this.chart.clearData();
			},
			binddatasetListener : function(dsEvent) {
				if (dsEvent) {
					if (dsEvent.id == this.getBinddataset()) {
						if (/^(bind|reset|sort|add|update|remove)$/.test(dsEvent.type)) {
							this._doValue();
						}
					}
				}
			},
			_doValue : function() {
				if (this.chart) {
					var ds = Xwing.getDataset(this.getBinddataset()),
						opts = this.chart.getProperty('opts');
					
					if (ds && opts) {
						var cols = ds.getColumnInfo(),
							rows = ds.getData();

						if (rows.length == 0) {
							this.chart.clearData();
							return;
						}
						
						if (opts.xAxis.label.bindcolumn) {
							opts.xAxis.label.value = this._getBindColumnValue(cols, rows, opts.xAxis.label.bindcolumn);						
						}
	
						for ( var i = 0, l = opts.series.length; i < l; i++) {
							if (opts.series[i].bindcolumn) {
								opts.series[i].value = this._getBindColumnValue(cols, rows, opts.series[i].bindcolumn, true);							
							}
						}						
					}
					
					this.chart.redraw();
				}
			},
			_getBindColumnValue : function(cols, rows, bindcolumn, isnum) {
				var v = [];
				for ( var i = 0, l = cols.length; i < l; i++) {
					if (cols[i] == bindcolumn) {
						var colIdx = i;
						for (i = 0, l = rows.length; i < l; i++) {
							v.push(isnum ? +rows[i][colIdx] : rows[i][colIdx]);
						}						
						return v;
					}
				}
				return v;
			}
		}
	}
});
