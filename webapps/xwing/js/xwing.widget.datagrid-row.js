Class.define({
	DataGridRow : {
		alias : "datagrid-row",
		namespace : "xwing.widget",
		DataGridRow : function(json, obj) {
			this._datagrid = obj;
			this._init(json);
		},
		statics : {

		},
		prototypes : {
			_init : function(json) {
				this.model = Class.getClass(this.getAlias()).getModel();
				this._opt = {};
				this._attr = {};
				this._cell = [];

				jQuery.extend(this._opt, json || {});
				for ( var key in this.model.attributes) {
					this._opt[key] == undefined && (this._opt[key] = this.model.attributes[key].defaultValue);
				}

				var methods = [ 'hasAttribute', 'setAttribute', 'getAttribute', 'removeAttribute', 'getAttributes', '_getDefault' ];
				for ( var i = 0, l = methods.length; i < l; i++) {
					this[methods[i]] = xwing.widget.Widget.prototype[methods[i]];
				}
			},
			getId : function() {
				return this._opt.id;
			},
			_setCss : function() {
				this._datagrid._setCssRules('.' + this.getId(), this._getCss());
			},
			_getCss : function() {
				return 'height: ' + this.getHeight() + 'px;';
			},
			_addCss : function(){
				this._datagrid._addCssRules('.' + this.getId(), this._getCss());
			},
			addCell : function(v) {
				this._cell.push(v);
			},
			getCell : function(i) {
				return i === undefined ? this._cell : this._cell[i];
			},
			_removeCell : function(i){
				if(arguments.length == 0 ){
					this._cell = [];
				}else{
					this._cell.splice(i,1);
				}
			},
			setHeight : function(v) {
				this._opt.height = v;
				this._doHeight();
			},
			getHeight : function() {
				return xwing.Util.parseInt(this._opt.height, 0);
			},
			_doHeight : function() {
				this._setCss();
			},
			getAlias : function() {
				return this.alias;
			},
			getXId : function() {
				return this._opt._xid;
			},
			setGroupsumstylerendering : function(v){
				this._opt.groupsumstylerendering = v;
			},
			getGroupsumstylerendering : function(){
				return this._opt.groupsumstylerendering;
			},
			_doGroupsumstylerendering : function(depth){
				try{
					var v = eval(this.getGroupsumstylerendering());
					var obj = xwing.Util.is(v,'function') ? v.call(null, this, depth) : v;
					if( obj !== undefined ){
						return this._getBGCss(obj);
					}
				}catch(e){
					Xwing.debug(e);
				}
				return '';
			},
			_getBGCss : function(v) {
				var rules = [], opt = v || this._opt;

				if (opt.bgimage) {
					rules.push("background-image:" + "url('" + opt.bgimage + "');");
					opt.bgimagerepeat && rules.push("background-repeat:" + opt.bgimagerepeat + ";");
					opt.bgimagealign && rules.push("background-position:" + opt.bgimagealign + ";");
				} else if (opt.bgcolor && opt.bggradientcolor) {
					rules.push("background-image:" + "linear-gradient(to top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					if (jQuery.browser.mozilla) {
						rules.push("background-image:" + "-moz-linear-gradient(top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					} else if (jQuery.browser.opera) {
						rules.push("background-image:" + "-o-linear-gradient(top, " + opt.bgcolor + ", " + opt.bggradientcolor + ");");
					} else if (jQuery.browser.webkit) {
						rules.push("background-image:" + "-webkit-gradient(linear, left top, left bottom, from(" + opt.bgcolor + "), to(" + opt.bggradientcolor + "));");
					}
				} else if (opt.bgcolor) {
					rules.push('background-color:' + opt.bgcolor + ';');
				}
				
				opt.valign && rules.push("vertical-align:" + opt.valign + ';');
				opt.halign && rules.push("text-align:" + opt.halign + ';');
				opt.cursor && (opt.cursor != 'default') && rules.push("cursor:" + opt.cursor + ';');
				
				return rules.join('');
			}
		}
	}
});
