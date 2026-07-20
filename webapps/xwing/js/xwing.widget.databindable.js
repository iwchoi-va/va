Class.define({
	DataBindable : {
		alias : "databindable",
		namespace : 'xwing.widget',
		extend : xwing.widget.Widget,
		DataBindable : function(json) {
			if (!arguments.length) return;
			this.name = "databindable";
			this._init(json);
		},
		statics : {	},
		prototypes : {
			_render : function() {
				this._initDataset();
				this._doPlaceholder();
				this._doDomain();
				this._doMask();
				
				xwing.widget.Widget.prototype._render.call(this);
			},
			_initDataset : function() {
				this._dataset = {};
				for ( var ds in this.model.attributes) {
					if (this.model.attributes[ds].control == "dataset") {
						this._dataset[ds] = [];
						this.connectDataset(ds);
						var category = this.model.attributes[ds].category;
						for ( var col in this.model.attributes) {
							if (this.model.attributes[col].category == category && this.model.attributes[col].control == "dataset-column") {
								this._dataset[ds].push(col);
							}
						}
					}
				}
				/*debug Xwing.debug("_initDataset : "+xwing.Util.toString(this._dataset)); */
			},
			connectDataset : function(datasetAttrName) {
				if (this._opt[datasetAttrName] != null && this._opt[datasetAttrName] != "") {
					var dataset = Xwing.getDataset(this._opt[datasetAttrName]);
					if (dataset) {
						dataset.bind('ALL', this._notify, this);
					}
				}
			},
			disconnectDataset : function(datasetAttrName) {
				if (this._opt[datasetAttrName] != null && this._opt[datasetAttrName] != "") {
					var oldDs = Xwing.getDataset(this._opt[datasetAttrName]);
					if (oldDs)
						oldDs.unbind("ALL", this._notify, this);
				}
			},
			isBinded : function(datasetAttrName) {
				if (this._opt[datasetAttrName] && this._dataset[datasetAttrName]) {
					var dsObj = Xwing.getDataset(this._opt[datasetAttrName]);
					if (dsObj) {
						for ( var i = 0; i < this._dataset[datasetAttrName].length; i++) {
							var colAttName = this._dataset[datasetAttrName][i];
							if (!this._opt[colAttName] || !dsObj.hasColumn(this._opt[colAttName])) {
								return false;
							}
						}
						return true;
					}
				}
				return false;
			},
			binddatasetListener : function(dsEvent) {
				if (dsEvent && dsEvent.type == "unbind") {
					if (this._attr.value)
						this._opt.value = this._attr.value;
				} else {
					if (this._opt.binddataset && this._opt.bindcolumn) {
						var dsObj = Xwing.getDataset(this._opt.binddataset);
						if (dsObj) {
							var value = dsObj.getValue(this._opt.bindcolumn);
							if (value !== null) {
								this._opt.value = value;
							}
						}
					}
				}
				this._doValue();
			},
			domaindatasetListener : function(dsEvent){
				this._doDomain();
			},
			setBinddataset : function(dataset) {
				/*debug Xwing.debug("setBind :" + dataset +", "+this._opt.binddataset); */
				if (dataset != this._opt.binddataset) {
					this.disconnectDataset("binddataset");
					this._opt.binddataset = dataset;
					if (dataset != null && dataset != "") {
						this.connectDataset("binddataset");
					}
					this._doValue();
				}
			},
			setBindcolumn : function(column) {
				this._opt.bindcolumn = column;
				this._doValue();
			},
			setDomaindataset : function(dataset) {
				if (dataset != this._opt.domaindataset) {
					this.disconnectDataset("domaindataset");
					this._opt.domaindataset = dataset;
					if (dataset != null && dataset != "") {
						this.connectDataset("domaindataset");
					}
					this._doDomain();
				}
			},
			getBinddataset : function() {
				return this._opt.binddataset;
			},
			getDomaindataset : function() {
				return this._opt.domaindataset;
			},
			setDomaincodecolumn : function(col) {
				this._opt.domaincodecolumn = col;
				this._doDomain();
			},
			getDomaincodecolumn : function() {
				return this._opt.domaincodecolumn;
			},
			setDomaintextcolumn : function(col) {
				this._opt.domaintextcolumn = col;
				this._doDomain();
			},
			getDomaintextcolumn : function() {
				return this._opt.domaintextcolumn;
			},
			getBindcolumn : function() {
				return this._opt.bindcolumn;
			},
			setValue : function(v) {
				this._opt.value = this.getUnMaskedValue(v);
				
				if (!window.xwingIDE) {
					this.updateDataset();
				}
				
				this._doValue();
			},
			_doValue : function() {
				// nothing to do here.
			},
			getValue : function() {
				var val = (this._opt.value == null ? "" : this._opt.value);
				if (this.isBinded("binddataset")) {
					if (Xwing.getDataset(this.getBinddataset())) {
						var ds = Xwing.getDataset(this.getBinddataset());
						if (ds.hasColumn(this.getBindcolumn())) {
							val = ds.getValue(this.getBindcolumn());
						}
					}
				}
				return val;
			},
			getDomainValue : function() {
				var val = this.getValue();
				if (this.isBinded("domaindataset")) {
					var domaindataset = Xwing.getDataset(this.getDomaindataset());
					if (domaindataset) {
						val = domaindataset.lookUp(this.getDomaincodecolumn(), this.getValue(), this.getDomaintextcolumn());
					}
				}
				return val;
			},
			updateDataset : function() {
				if (this.isBinded("binddataset")) {
					var ds = Xwing.getDataset(this.getBinddataset());
					if (ds.hasColumn(this.getBindcolumn())) {
						ds.setValue(this.getBindcolumn(), this._opt.value, this);
					}
				}
			},
			_notify : function(dsEvent){
				/*debug Xwing.debug(this.getAlias()+"("+this.getId()+")'s notify type:" + dsEvent.type +" "+ dsEvent.id); */
				if (dsEvent) {
					for ( var ds in this._dataset) {
						if (this._opt[ds] == dsEvent.id) {
							var listenerFunc = eval("this." + ds + "Listener");
							if (listenerFunc && typeof (listenerFunc) == "function")
								listenerFunc.call(this, dsEvent);
						}
					}
				}		
			},
			_doDomain : function(){
				//nothing to do...here..
			},
			setMask : function(mask) {
				this._opt.mask = mask;				
				this._doMask();
			},
			_doMask : function() {
				if (this._opt.mask != undefined) {
					if (this.getMask()) {
						jQuery.mask.options.textAlign = false;
						this._getJShell().find(":text").setMask(this.getMask());
					} else {
						this._getJShell().find(":text").unsetMask();
					}
				}
				this._doValue();
			},
			getMask : function() {
				return this._opt.mask;
			},
			getMaskedValue : function(v) {
				if (! arguments.length) {
					v = this.getValue();
				}		
				if (this.getMask() && v) {
					v = jQuery.mask.string(v, this.getMask());
				}
				return v;
			},
			getUnMaskedValue : function(v) {				
				if (this.getMask() && v) {
					var fixedCharsRegG = new RegExp(jQuery.mask.options.fixedChars, 'g');
					v = v.replace(fixedCharsRegG, '');
				}
				return v;
			},
			setPlaceholder : function(v) {
				this._opt.placeholder = v;
				this._doPlaceholder();
			},
			getPlaceholder : function() {
				return this._opt.placeholder;
			},
			_doPlaceholder : function() {
				if (this.getPlaceholder()) {
					var targetWidget = 'input:text,textarea';
					var placeh = jQuery('.xw-placeholder', this.getShell());
					if (placeh.length == 0) {
						placeh = jQuery("<div tabindex='-1' class='xw-placeholder xw-mod-font' style='line-height:" + this.getHeight() + "px;' ></div>");
						jQuery(targetWidget, this.getShell()).before(placeh);

						var thisObj = this;
						jQuery(targetWidget, this.getShell()).bind('focusin', function(event) {
							jQuery('.xw-placeholder', thisObj.getShell()).css('display', 'none');
						}).bind('focusout', function(event) {
							thisObj._checkPlaceholder();
						});
						
						if (jQuery.browser.msie || jQuery.browser.opera) {
							placeh.bind('focusin', function(event) {
								event.stopPropagation();
								jQuery(targetWidget, thisObj.getShell())[0].focus();
							});
							placeh.mousedown(function(event) {
								event.stopPropagation();
								jQuery(targetWidget, thisObj.getShell()).trigger('mousedown');
							});
							placeh.mouseup(function(event) {
								event.stopPropagation();
								jQuery(targetWidget, thisObj.getShell()).trigger('mouseup');
							});
						}
					}
					placeh.text(this.getPlaceholder());
					if (jQuery('input:text,textarea', this.getShell()).val() == "")
						placeh.css('display', 'block');
					else
						placeh.css('display', 'none');
					placeh = null;
				} else {
					jQuery('.xw-placeholder', this.getShell()).remove();
				}
			},
			_checkPlaceholder : function() {
				var placeh = jQuery('.xw-placeholder', this.getShell());
				if (jQuery('input:text,textarea', this.getShell()).val() == "") placeh.css('display', 'block');
				else placeh.css('display', 'none');
				placeh = null;
			},
			_resetPlaceholderheight : function() {
				jQuery('.xw-placeholder', this.getShell()).css('line-height', this.getHeight() + "px");
			}
		}
	}
});