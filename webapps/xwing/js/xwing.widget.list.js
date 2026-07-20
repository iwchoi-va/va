Class.define({
	List : {
		alias : 'list',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		List : function(json){
			this._init(json);
		},
		statics:{
			create : function(json){
				return new xwing.widget.List(json);
			}			
		},
		prototypes : {
			_createPart : function(){				
				this.list = jQuery("<div class='xw-list xw-mod-background xw-mod-border'/>");
				this._bind(this.list, "scroll", this._checkscroll);				
				this.ul = jQuery("<ul style='width:100%;margin:0px;padding:0px;list-style:none outside none;' tabindex='-1' />").appendTo(this.list);
				this.list.appendTo(this._getJShell());
				this.doit = true;
				
				this._bind(this.ul, 'keydown', this._keyDown);
				
				this._bindScrollPane(this.ul);
				this._bindTouchEvent();	
				
				/*debug Xwing.debug("Widget(List) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},	
			_bindTouchEvent : function() {
				if (xwing.Util.isTouchDevice()) {
					var thisObj = this;
					this.ul[0].currY = 0;
					
					this.ul.live('touchstart', function(event){
						event.preventDefault();
						this.preY = event.targetTouches[0].pageY;
					}, false);
					
					this.ul.live('touchmove', function(event){
						event.preventDefault();
						var y = event.targetTouches[0].pageY,
							diff = (y - this.preY),
							oldY = this.currY;
						
						this.currY = (this.currY + diff) > 0 ? 0 : (this.currY + diff);
						jQuery(this).css('top', this.currY + "px");

						var thisTop = thisObj._getJShell().offset().top,
							lastTop = jQuery("li:last", this).offset().top,
							lastHeight = jQuery("li:last", this).height(),
							end = lastTop - thisTop + lastHeight;
						
						if(end < jQuery(this).height()){
							this.currY = oldY;
							jQuery(this).css('top', this.currY + "px");						
							thisObj._checkscroll(event);
						} 
					}, false);
				}
			},
			_keyDown : function(event){
				if (event.keyCode != 9) event.preventDefault();
				var dsObj = Xwing.getDataset(this._opt.binddataset);
				if(!dsObj) return;
				
				var idx = dsObj.getCursor();
				switch(event.keyCode) {
					case 40: //down
						if(idx == (dsObj.size()-1)) return;
						var height = this.list.height();
						event.currentTarget = jQuery(event.target).find("li:eq("+(idx+1)+") > div")[0];
						var rowOffset = event.currentTarget.offsetTop + jQuery(event.currentTarget).height() - this.getBorderwidth() + 1;
						if((rowOffset - this.list.scrollTop()) > height ) this.list.scrollTop((rowOffset -height ));
						
						this._selecting(event);
						this._selected(event);
						break;
					case 38: //up
						if(idx ==0) return;
						var height = this.list.height();
						event.currentTarget = jQuery(event.target).find("li:eq("+(idx-1)+") > div")[0];
						var rowOffset = event.currentTarget.offsetTop - this.getBorderwidth() - 1;
						if(rowOffset < this.list.scrollTop() ) this.list.scrollTop(rowOffset);
						
						this._selecting(event);
						this._selected(event);
						break;
				}
			},	
			_doValue:function(){
				try{
					this.ul.empty();
					this.dataset = Xwing.getDataset(this.getBinddataset());				
					if(!this.dataset) return;
					
					var len = this.dataset.size();
					for ( var i = 0; i < len; i++) {						
						var li = jQuery("<li class='xw-list-li' />");
						var item  = jQuery("<div class='xw-list-item ' />").appendTo(li);
						if (i%2 == 1){
							item.addClass("xw-list-item-odd");
						} else {
							item.addClass("xw-list-item-even");
						}
						
						if(this._opt.iconcolumn){
							var icon = this.dataset.getValue(i,this._opt.iconcolumn);
							var img  =jQuery("<img src='"+this._doExpr(i,icon,"icon")+"' style='padding:2px;float:left;width:30px;height:30px'/>").appendTo(item);
						}
						
						var rightArea = jQuery("<div style='overflow:hidden'/>").appendTo(item);
						if(this._opt.titlecolumn){
							var title = this.dataset.getValue(i,this._opt.titlecolumn);
							var tit = jQuery("<span style='font-weight:bold;padding:2px;display:block'/>").appendTo(rightArea);
							try{
								tit.html(this._doExpr(i,title,"title"));
							}catch(e){
								tit.text(this._doExpr(i,title,"title"));
							}
						}
						
						var content = this.dataset.getValue(i,this._opt.contentcolumn);
						/*debug Xwing.debug(content); */
						var con = jQuery("<span  onselectable='on' style='padding:2px;word-wrap:break-word;display:block' class='xw-list-span xw-mod-font'/>").appendTo(rightArea);
						try{
							con.html(this._doExpr(i,content,"content"));
						}catch(e){
							con.text(this._doExpr(i,content,"content"));
						}
								
						if(this._opt.footercolumn){
							var footer = this.dataset.getValue(i,this._opt.footercolumn);
							var foo = jQuery("<span style='padding:2px;display:block'/>").appendTo(rightArea);
							try{
								foo.html(this._doExpr(i,footer,"footer"));
							}catch(e){
								foo.text(this._doExpr(i,footer,"footer"));
							}
						}	
						
						li.appendTo(this.ul);
					}
					
					this._branding();
					this._doTextpadding();
					this._doItemheight();
					this._doFont();
					this._doItembg();
					this._doItembordercolor();
					
					var items = this.list.find("div.xw-list-item");
					this._bind(items, "mousedown", this._selecting);
					this._bind(items, "mouseup", this._selected);
					
					var cur = this.dataset.getCursor();
					this.setSelection([cur]);
					
					this._refreshScrollPane();
				}catch(e){
					/*debug Xwing.debug(e); */
				}
			},
			setItemheight : function(value) {
				this._opt.itemheight = value;
				this._doItemheight();
			},
			_doItemheight : function() {
				var items = this.list.find("div.xw-list-item");
				if (this._opt.itemheight) {
					items.css("min-height", this._opt.itemheight + "px");
				} else {
					items.css("height", "auto");
				}
			},
			getItemheight : function() {
				return this._opt.itemheight;
			},
			setIconcolumn : function(value) {
				this._opt.iconcolumn = value;
				this._doValue();
			},
			getIconcolumn : function() {
				return this._opt.iconcolumn;
			},
			setTitlecolumn : function(value) {
				this._opt.titlecolumn = value;
				this._doValue();
			},
			getTitlecolumn : function() {
				return this._opt.titlecolumn;
			},
			setContentcolumn : function(value) {
				this._opt.contentcolumn = value;
				this._doValue();
			},
			getContentcolumn : function() {
				return this._opt.contentcolumn;
			},
			setFootercolumn : function(value) {
				this._opt.footercolumn = value;
				this._doValue();
			},
			getFootercolumn : function() {
				return this._opt.footercolumn;
			},
			setMultiselectable : function(value) {
				this._opt.multiselectable = value;
			},
			getMultiselectable : function() {
				return xwing.Util.parseBoolean(this._opt.multiselectable, false);
			},
			setExpr : function(value) {
				this._opt.expr = value;
			},
			_doExpr : function(rowIdx, value, gubun) {
				if (this._opt.expr === undefined || this._opt.expr == null)
					return value;
				var f = window[this._opt.expr];
				if (jQuery.isFunction(f))
					return f(rowIdx, value, gubun);
				else
					return value;
			},
			getExpr : function() {
				return this._opt.expr;
			},
			setItembgcolor : function(v){
				this._opt.itembgcolor = v;
				this._doItembg();
			},
			getItembgcolor : function(){
				return this._opt.itembgcolor;
			},
			setItembggradientcolor : function(v){
				this._opt.itembggradientcolor = v;
				this._doItembg();
			},
			getItembggradientcolor : function(){
				return this._opt.itembggradientcolor;
			},
			_doItembg : function(mod){
				var items = mod;
				if(items === undefined) items = this.list.find("div.xw-list-item");

				items.css('background-color',this.getItembgcolor());
				if(this.getItembggradientcolor() && this.getItembgcolor()){					
					items.css('background-color','');
					if(Xwing.isIE()){
						items.css('filter',"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='"+this.getItembgcolor()+"', endColorstr='"+this.getItembggradientcolor()+"')");
					}else{
						if(jQuery.browser.mozilla){
							items.css("background-image", "-moz-linear-gradient(top, "+this.getItembgcolor()+", "+this.getItembggradientcolor()+")");
						}else if(jQuery.browser.opera){
							items.css("background-image", "-o-linear-gradient(top, "+this.getItembgcolor()+", "+this.getItembggradientcolor()+")");
						}else if(jQuery.browser.webkit){
							items.css("background-image", "-webkit-gradient(linear, left top, left bottom, from("+this.getItembgcolor()+"), to("+this.getItembggradientcolor()+") )");
						}
					}
				}
			},
			setItembordercolor : function(v){
				this._opt.itembordercolor = v;
				this._doItembordercolor();
			},
			getItembordercolor : function(){
				return this._opt.itembordercolor;
			},
			_doItembordercolor : function(){
				var items = this.list.find("div.xw-list-item").not(':last');
				if(this.getItembordercolor()) items.css('border-bottom-color',this.getItembordercolor());
			},
			_selecting : function(event) {
				var t = jQuery(event.currentTarget);
				var oldidx = this.dataset.getCursor();					
				var idx = this.list.find("div.xw-list-item").index(event.currentTarget);
				var opt = {type:'changing',source:this,event:event,doit:true, value:idx};
				if(this.dataset.getCursor != idx) this._fire("changing",opt);
				this.doit = opt.doit;
				if(!this.doit) return;
				
				if (event.ctrlKey && this._opt.multiselectable == 'true') {
					if (t.hasClass("xw-list-item-selected")) {
						t.removeClass("xw-list-item-selected");
					} else {
						t.addClass("xw-list-item-selected");
					}
				} else {
					var items = this.list.find("div.xw-list-item");
					items.removeClass("xw-list-item-selected");

					t.addClass("xw-list-item-selected");
				}
			},
			_selected : function(event) {
				try{
					if(!this.doit){
						this.doit = true;
						return;
					}
					
					var oldidx = this.dataset.getCursor();					
					var idx = this.list.find("div.xw-list-item").index(event.currentTarget);
					/*debug Xwing.debug("oldidx >> "+oldidx+","+idx); */
					
					this.dataset.setCursor(idx, this);
					this._fire("select", {type:'select', source:this, event:event});
					if (oldidx != idx) this._fire("change", {type:'change', source:this, event:event, value:idx});
				}catch(e){
					/*debug Xwing.debug(e); */
				}
			},		
			_checkscroll : function(event) {
				try{
					if (this.list.scrollTop() == 0) {
						/*debug Xwing.debug("top"); */
						this.scrollBottom = "top";
						this._fire("scrolltop", {
							type : "scrolltop",
							source : this,
							event : event
						});
					}
					if ((this.ul.outerHeight() - this.list.innerHeight()) <= this.list.scrollTop()) {
						if (this.scrollBottom != "bottom"){
							this.scrollBottom = "bottom";
							this._fire("scrollbottom", {
								type : "scrollbottom",
								source : this,
								event : event
							});
						}
						return;
					}

					if (this.scrollBottom == "bottom" || this.scrollBottom == "top")
						this.scrollBottom = "";
					
					this._fire("scroll",{type:"scroll",source:this,event:event});
				}catch(e){
					/*debug Xwing.debug(e); */
				}
			},
			getSelectionCnt : function() {
				return this.list.find("div.xw-list-item").filter(".xw-list-item-selected").size();
			},
			getSelection : function() {
				var items = this.list.find("div.xw-list-item");
				var selectedItem = [];

				for ( var i = 0, size = items.size(); i < size; i++) {
					if (jQuery(items[i]).hasClass("xw-list-item-selected"))
						selectedItem.push(i);
				}

				return selectedItem;
			},
			setSelection : function(idx, reveal) {
				!xwing.Util.is(idx, 'array') && (idx = [ idx || 0 ]);
				if (idx.length == 0) return;

				var items = this.list.find("div.xw-list-item").removeClass('xw-list-item-selected');

				for ( var i = 0, l = idx.length; i < l; i++) {
					items.eq(idx[i]).addClass("xw-list-item-selected");
				}
				
				if (reveal) {
					var jTarget = items.eq(idx[0]),
						clientHeight = xwing.Util.parseInt(this.list[0].clientHeight, 0),
						scrollTop = this.list.scrollTop(),
						offsetTop = jTarget[0].offsetTop,
						height = offsetTop + jTarget.height();
					
					if (scrollTop > height) {
						this.list.scrollTop(offsetTop);
					} else if ((scrollTop + clientHeight) < height) {
						this.list.scrollTop(height - clientHeight);
					}
				}
			},
			binddatasetListener : function(dsEvent){
				/*debug Xwing.debug('start'); */
				if(dsEvent && dsEvent.type == "unbind" ){
					if(this._attr.value) this._opt.value = this._attr.value;
				}else{
					if(this._opt.binddataset && this._opt.bindcolumn){
						var dsObj = Xwing.getDataset(this._opt.binddataset);
						if(dsObj){
							var value = dsObj.getValue(this._opt.bindcolumn);
							if(value !== null){
								this._opt.value = value;
							}
						}
					}
				}
				if(dsEvent && dsEvent.type=='cursor') {					
					var cur = this.dataset.getCursor();					
					this.setSelection([cur]);		
					/*debug Xwing.debug('end'); */
					return;
				}
				
				this._doValue();
			}			
		}	
	}
});
