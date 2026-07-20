Class.define({
	Radio : {
		alias : 'radio',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Radio : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Radio(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.jRadio = jQuery("<div class='xw-radio xw-mod-background xw-mod-border'/>");
				this.jCol_div = [];
				this._getJShell().append(this.jRadio);
			},
			_createItem : function(){
				var item = jQuery("<div class='xw-radio-item' />");
				if(this.getColumncount() == 0){
					item.addClass('xw-radio-item-zero');
				}
				var item_icon = jQuery("<span class='xw-radio-item-icon' unselectable='on' />").appendTo(item);
				var item_label = jQuery("<span class='xw-radio-item-label xw-mod-font' />").appendTo(item);
				item.attr("_xid",this.getId());
				
				return item;				
			},
			_render : function(){
				this._doColumncount();
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doItemheight();	
			},
			_doBounds : function(){
				xwing.widget.Widget.prototype._doBounds.call(this);
				var width = xwing.Util.parseInt((this.getWidth() - this.getBorderwidth() * 2) / this.getColumncount(), 0);
				for ( var i = 0; i < this.jCol_div.length; i++) {
					if (this.jCol_div[i].hasClass('xw-radio-zero'))
						break;
					this.jCol_div[i].width(width);
				}
			},
			_doValue : function() {
				if (this.isBinded("domaindataset") && jQuery(".xw-radio-item", this.getShell()).size()) {
					jQuery(".xw-radio-item-checked", this.jRadio).removeClass("xw-radio-item-checked").addClass("xw-radio-item-unchecked");

					var thisObj = this;
					jQuery(".xw-radio-item", this.getShell()).each(function() {
						if (this.code == thisObj.getValue()) {
							jQuery(".xw-radio-item-icon", this).addClass("xw-radio-item-checked").removeClass("xw-radio-item-unchecked-pressing").removeClass("xw-radio-item-unchecked");
							return true;
						}
					});
				}
				
				this._fire("change", {
					type : "change",
					source : this,
					value : this.getValue(),
					event : null
				});
			},
			_doDomain:function(){
				/* debug Xwing.debug("radio doDomain"); */
				this._clear();
				if(this.isBinded("domaindataset")){
					if(this.getDomaindataset() && Xwing.getDataset(this.getDomaindataset())){
						var dsObj = Xwing.getDataset(this.getDomaindataset());
						var size = dsObj.size();
						for(var i=0; i < size; i++){
							var code = dsObj.getValue(i, this.getDomaincodecolumn());
							var text = dsObj.getValue(i, this.getDomaintextcolumn());
							var radio = this._createItem();
							var icon = jQuery(".xw-radio-item-icon",radio);
							var label = jQuery(".xw-radio-item-label",radio);
							
							radio[0].code = code;
							radio[0].index = i;
							label.text(text);
							
							var col = this.getColumncount() == 0 ? 0 :i % this.getColumncount();
							this.jCol_div[col].append(radio);
							
							// action
							var thisObj = this;
							radio.bind('mousedown', function(event){
								if(!thisObj.getEnabled()) return;
								var cur_icon = jQuery(".xw-radio-item-icon",this);
								if(cur_icon.hasClass("xw-radio-item-checked")){
									cur_icon.addClass("xw-radio-item-checked-pressing");
								}else{
									cur_icon.addClass("xw-radio-item-unchecked-pressing");
								}
							});
							radio.bind('mouseup', function(event){
								if(!thisObj.getEnabled()) return;
								var cur_icon = jQuery(".xw-radio-item-icon",this);
								if(cur_icon.hasClass("xw-radio-item-checked")){
									cur_icon.removeClass("xw-radio-item-checked-pressing");
								}else{
									cur_icon.removeClass("xw-radio-item-unchecked-pressing");
									var opt = {type:'changing',source:thisObj,event:event,value:this.code,doit:true};
									thisObj._fire("changing",opt);
									if(opt.doit) thisObj.setValue(this.code);
								}
							});
							radio.bind('keypress',function(event){
								/*if(!thisObj.getEnabled()) return;
								var cur_icon = jQuery(".xw-radio-item-icon",this);
								if(event.keyCode == '13' || event.keyCode == '32' || event.keyCode == '0'){
									if(cur_icon.hasClass("xw-radio-item-checked")){
										cur_icon.removeClass("xw-radio-item-checked")
													.addClass("xw-radio-item-unchecked");
									}else{
										jQuery(".xw-radio-item-checked", thisObj.jRadio)
											.removeClass("xw-radio-item-checked")
											.addClass("xw-radio-item-unchecked");
										cur_icon.removeClass("xw-radio-item-unchecked")
													.addClass("xw-radio-item-checked");
									}
								}*/
							});
							if(this.getEnabled()){
								radio.attr("tabindex", this.getTabindex());
							}else{
								radio.attr('tabindex','-1');
							}
						}
					}
				}else{
					var radio = this._createItem();
					var label = jQuery(".xw-radio-item-label",radio);
					
					radio[0].code = code;
					label.text(this.getValue());
					
					this.jCol_div[0].append(radio);
				}
				this._postRender();
			},
			_postRender : function(){
				this._branding();
				this._doFont();
				this._doIconalign();
				this._doIconimage();
				this._doIconmargin();
				this._doValign();
				this._doHalign();
				this._doCursor();
				this._doTextwrap(jQuery('.xw-radio-item-label',this.getShell()));
			},
			setColumncount : function(cnt){
				this._opt.columncount = cnt ? cnt : 0;
				this._doColumncount();
			},
			_doColumncount : function() {
				var cnt = this.getColumncount();
				this._clearCol();
				if (cnt == 0) {
					this.jCol_div[0] = jQuery("<div class='xw-radio-zero xw-radio-col' />").css('position', 'static');
					this.jRadio.append(this.jCol_div[0]);
				} else {
					var width = xwing.Util.parseInt((this.getWidth() - this.getBorderwidth() * 2) / cnt, 0);
					for ( var i = 0; i < cnt; i++) {
						var tmp = jQuery("<div class='xw-radio-col' />").width(width);
						this.jCol_div.push(tmp);
						this.jRadio.append(tmp);
					}
				}
				if (this._dataset != undefined)
					this._doDomain();
			},
			getColumncount : function(){
				return xwing.Util.parseInt(this._opt.columncount, 0);
			},
			_clearCol : function(){
				delete this.jCol_div;
				this.jCol_div = [];
				this.jRadio.empty();
			},
			_clear : function(){				
				for(var i=0; i < this.jCol_div.length; i++){
					this.jCol_div[i].empty();
				}
			},
			domaindatasetListener : function(dsEvent){
				if(dsEvent.type=='cursor') return;
				
				this._doDomain();
			},
			setIconalign : function(v){
				this._opt.iconalign = v;
				this._doIconalign();
			},
			getIconalign : function(){
				return this._opt.iconalign;
			},
			_doIconalign : function(){
				var item = jQuery('.xw-radio-item',this.getShell());
				var thisObj = this;
				item.each(function(idx,el){
					var jIcon = jQuery('.xw-radio-item-icon',el);
					var jLabel = jQuery('.xw-radio-item-label',el);
					switch(thisObj.getIconalign()){
					case 'left' :
						jLabel.before(jIcon);
						break;
					case 'right' :
						jLabel.after(jIcon);
						break;
					}
				});
			},
			setIconimage : function(v){
				this._opt.iconimage = v;
				this._doIconimage();
			},
			getIconimage : function(){
				return this._opt.iconimage;
			},
			_doIconimage : function(){
				if(this.getIconimage()){
					jQuery('.xw-radio-item-icon',this.getShell()).css('background-image','url('+this.getIconimage()+')');
				}else{
					jQuery('.xw-radio-item-icon',this.getShell()).css('background-image','');
				}
			},
			setIconmargin : function(v){
				this._opt.iconmargin = v;
				this._doIconmargin();
			},
			getIconmargin : function(){
				return this._opt.iconmargin;
			},
			_doIconmargin : function(){
				var margins = (this.getIconmargin() == null ? "" : this.getIconmargin().trim().split(/\s+/));
				var result = "";
				for(var i =0 ; i < margins.length ; i++){
					var tmp = margins[i] + 'px ';
					result += tmp;
				}
				jQuery('.xw-radio-item-icon',this.getShell()).css('margin',result);
			},
			setValign : function(v){
				this._opt.valign = v;
				this._doValign();
			},
			getValign : function(){
				return this._opt.valign;
			},
			_doValign : function(){
				jQuery('.xw-radio-item-icon',this.getShell()).css('vertical-align',this._opt.valign);
				jQuery('.xw-radio-item-label',this.getShell()).css('vertical-align',this._opt.valign);				
			},
			setHalign : function(v){
				this._opt.halign = v;
				this._doHalign();
			},
			getHalign : function(){
				return this._opt.halign;
			},
			_doHalign : function(){
				jQuery('.xw-radio-item',this.getShell()).css('text-align',this._opt.halign);
			},
			_doCursor : function(){
				if(window.xwingIDE) return;			
				var icon = jQuery('.xw-radio-item-icon',this.getShell());
				var label = jQuery('.xw-radio-item-label',this.getShell());
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				icon.css('cursor',cursor);
				label.css('cursor',cursor);
			},
			setTextwrap : function(v){
				this._opt.textwrap = v;
				this._doTextwrap(jQuery('.xw-radio-item-label',this.getShell()));
			},
			getTextwrap : function(){
				return this._opt.textwrap;
			},
			setItemheight : function(v){
				this._opt.itemheight = v;
				this._doItemheight();
			},
			getItemheight : function(){
				return xwing.Util.parseInt(this._opt.itemheight,0);
			},
			_doItemheight : function(){
				if(this.getItemheight() != 0)
					this._getJShell().find('div.xw-radio-item').height(this.getItemheight()+'px');
			}
			
		}
	}
});