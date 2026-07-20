Class.define({
	Combo : {
		alias : 'combo',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Combo : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Combo(json);
			}
		},
		prototypes : {			
			_createPart : function(){
				this.varray = new Array();
				this.selectidx = '';
				this.combo = jQuery("<div class='xw-combo xw-mod-border'/>");
				this.comboInput = jQuery("<input type='text' class='xw-combo-input xw-mod-font xw-mod-background xw-combo-focus xw-mod-focus' />").appendTo(this.combo);
				this.imgDiv = jQuery("<div class='xw-combo-img-cnt ' />").appendTo(this.combo);
				this.img = jQuery("<div class='xw-combo-img '/>").appendTo(this.imgDiv);
				this.itemCnt = jQuery("<div class='xw-combo-item-cnt xw-mod-cnt' unselectable='on' />");
				this.ul = jQuery("<ul style='margin:0px;padding:0px;list-style:none outside none;'/>").appendTo(this.itemCnt);
				this._bindEventListener();
				
				this.combo.appendTo(this._getJShell());
				this.itemCnt.appendTo(jQuery("body"));
				/*debug Xwing.debug("Widget(Combo) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_bindEventListener : function(){
				this._bind(jQuery(document),'mousedown',this._clickout);
				this._bind(this.itemCnt, "mousedown", function(event){event.stopPropagation();});
				this._bind(this.itemCnt, "mouseup", function(event){event.stopPropagation();});
				this._bind(this.itemCnt,'mouseenter',function(event){
					this._getSelectingItem().removeClass("xw-combo-item-selecting");
					this._doItembg(this._getSelectingItem());
				});
				this._bind(this.imgDiv,'mousedown',this._toggleExpand);
				this._bind(this.comboInput,'mousedown',this._toggleExpand);
				this._bind(this.comboInput,'blur',this._blured);
				this._bind(this.comboInput,'focus',this._focused);			
				this._bind(this.comboInput,'keydown',this._keyProcess);
				
				this._bind(this.ul,'mousedown',function(event){
					this._mousedown = true;
				});
				this._bind(this.ul, "mouseup", function(event){// 아래 다른 widget이 있는 경우 event 발생을 막기 위해
					event.stopPropagation();
					if(!this._mousedown) return;
					this._mousedown = false;
					if(this.getMultiselectable()) this._multiselected(event);
					else this._selected(event);
				});
			},
			_render : function(){
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doEditable();
				this._doInputmode();
			},
			_doBounds:function(){
				xwing.widget.DataBindable.prototype._doBounds.call(this);
				
				var width = this._opt.width
				  , dsObj = Xwing.getDataset(this._opt.domaindataset);
				this.comboInput.css("width", (width - 18 - this.getBorderwidth() * 2) + "px");
				if( !this.getFlexibleitem() ) this.itemCnt.css("width", width + "px");
				else this.itemCnt.css("width", "");
				if (Xwing.isIE()) this.comboInput.css('line-height', (this.getHeight() - this.getBorderwidth() * 2) + "px");
				if(this.getPlaceholder()) this._resetPlaceholderheight();
				
				if (parseInt(this._opt.size) > 0 && dsObj && dsObj.size() > parseInt(this._opt.size))
					this.itemCnt.css("height", (((parseInt(this._opt.size) * (parseInt(this.getItemheight())) + this.getBorderwidth() * 2))) + "px");
				else if(parseInt(this._opt.size) > 0 && dsObj && dsObj.size() > 0)
					this.itemCnt.css("height", ((((this._getUseItemall() ? dsObj.size() + 1 : dsObj.size())* (parseInt(this.getItemheight())) + this.getBorderwidth() * 2))) + "px");
				else 
					this.itemCnt.css('height','');
			},
			_doBorder : function(){
				xwing.widget.Widget.prototype._doBorder.call(this);
				
				var borderwidth = (this.getBorderstyle() == 'none' ? 0 : this.getBorderwidth());
				var width = this._opt.width;
				this.comboInput.css("width", (width - 18 - borderwidth * 2) + "px");
				if (Xwing.isIE()) this.comboInput.css('line-height', (this.getHeight() - borderwidth * 2) + "px");
			},
			_doValue:function(){
				var value = (this.getValue() || this.getValue() == 0 ) ? new String(this.getValue()).split(',') : '';
				if (!this.getMultiselectable() && value.length > 1) value = value.slice(0,1);
				if(this.ul.parent().css('display') != 'none' && this.getMultiselectable()){
					// check 해제
					this.ul.find('span.xw-combo-check[multicheck=true]').attr('multicheck','false').css('background-position','0 0');
				}
				this.comboInput.val('');
				var valueStr = '';
				var cnt = 0;
				this.selectidx = '';
				for(var i = 0 ; i < this.varray.length ; i++) {
					if (value.indexOf(String(this.varray[i][0])) != -1) {
						if(this.selectidx == '') this.selectidx = String(i);
						else this.selectidx += ','+i;
						if(valueStr == '') valueStr = this.varray[i][1];
						else valueStr += ','+ this.varray[i][1];
						
						if(this.ul.parent().css('display') != 'none' && this.getMultiselectable()){
							// check
							this.ul.children('li[_idx='+i+']').find('span.xw-combo-check').attr('multicheck','true').css('background-position','0 -40px');
						}
						if( this._getUseItemall() ){
							cnt++;
						}
					}
					this.oldText = valueStr;
				}
				if( cnt == this.varray.length ){
					this.ul.children('li.xw-combo-all').find('span.xw-combo-check').attr('multicheck','true').css('background-position','0 -40px');
					this.comboInput.val('전체');
				}else this.comboInput.val(valueStr);
				
				if(this.getPlaceholder()) this._checkPlaceholder();
			},
			_doDomain:function(){
				this.comboInput.val('');
				this.ul.empty();
				this.varray = new Array();
				var dsObj = Xwing.getDataset(this._opt.domaindataset);
				if (!dsObj)	return;
				for ( var i = 0; i < dsObj.size(); i++) {
					this.varray[i] = [ dsObj.getValue(i, this._opt.domaincodecolumn), dsObj.getValue(i, this._opt.domaintextcolumn) ];
					var li = jQuery("<li class='xw-combo-li' _idx='"+i+"'/>").appendTo(this.ul)
								.css("height",this._opt.itemheight+"px");
					jQuery("<span class='xw-combo-item' multicheck='false' ></span>").appendTo(li);
					jQuery("<span class='xw-combo-item' style='line-height:"+this._opt.itemheight+"px;margin:1px;' ></span>").text(this.varray[i][1]).appendTo(li);
				}
				if(this.varray.length == 0 ) this._opt.value = '';
				this._doItemall();
				this._doMultiselectable();
				this._doItemfont();
				this._doItempadding();
				this._doItembg();
				this._doItembordercolor();
				this._doValue();
				
				if (parseInt(this._opt.size) > 0 && dsObj.size() > parseInt(this._opt.size))
					this.itemCnt.css("height", (((parseInt(this._opt.size) * (parseInt(this.getItemheight())) + this.getBorderwidth() * 2))) + "px");
				else if(parseInt(this._opt.size) > 0 && dsObj.size() > 0)
					this.itemCnt.css("height", ((((this._getUseItemall() ? dsObj.size() + 1 : dsObj.size())* (parseInt(this.getItemheight())) + this.getBorderwidth() * 2))) + "px");
				else this.itemCnt.css('height','');
			},
			setEditable: function(value){
				this._opt.editable = value;
				this._doEditable();
			},
			getEditable:function(){
				return xwing.Util.parseBoolean(this._opt.editable);
			},
			_doEditable:function(){
				if(this.getEditable()) this.comboInput.removeAttr("readonly");
				else this.comboInput.attr("readonly", "readonly");
			},
			setSize:function(value){
				this._opt.size = value;
				this._doBounds();
			},
			_isExpanded:function(){
				return this.itemCnt.css("display") == 'block';
			},
			_getSelectingItem:function(){
				return this.itemCnt.find("li.xw-combo-li:not(.xw-filter-none)").filter(":eq("+this._idx+")");
			},
			_keyProcess : function(event){
//				if(event.keyCode != 9) event.preventDefault();
				switch(event.keyCode){
				case 13: //enter
					if(!this._isExpanded()){ this._expand();
					}else{
						if(this.getMultiselectable()){
							this.comboInput.val(this.oldText);
							this._unexpand();
						}else{
							if(this._idx != "-1"){
								var v = this._getSelectingItem().find('span:last').text();
								var dsIdx =this._getSelectingItem().attr('_idx');
								var value = String(this.varray[dsIdx][0]);
								var oldValue = this.getValue();
								var opt = {type:'changing',source:this,event:event,doit:true,value:value};
								if(oldValue != value) this._fire("changing",opt);
								this._endTimer();
								this._unexpand();
								if(opt.doit){
									this.comboInput.val(v);
									this.oldText = this.varray[dsIdx][1];
									this.setValue(value);
									this._fire("select",{type:'select',source:this,event:event});
									if(oldValue != value) this._fire("change",{type:'change',source:this,event:event,value:value});
								}
								
							}else{
								this._idx ="0";
								this._getSelectingItem().addClass("xw-combo-item-selecting");
								this._getSelectingItem().css('background-color','');
								this._getSelectingItem().css('background-image','');
							}
						}
					}
					break;
				case 32: // space(multi를 위해)
					if(this.getMultiselectable() && this._isExpanded()){
						this._getSelectingItem().addClass("xw-combo-item-selecting");
						this._getSelectingItem().css('background-color','');
						this._getSelectingItem().css('background-image','');
						var opt = {
							target : this._getSelectingItem().children('.xw-combo-check')[0],
							stopPropagation : function(){
								event.stopPropagation();
							}
						};
						this._multiselected(opt);
					}
					break;
				case 40: // down
					if(!this._isExpanded())
						this._expand();
					if(parseInt(this._idx) == parseInt(this._maxidx)) return;					
					
					this._getSelectingItem().removeClass("xw-combo-item-selecting");
					this._doItembg(this._getSelectingItem());
					this._idx = parseInt(this._idx) + 1;
					this._getSelectingItem().addClass("xw-combo-item-selecting");
					this._getSelectingItem().css('background-color','');
					this._getSelectingItem().css('background-image','');
					if(this._idx >= parseInt(this._opt.size)){
						var oldTop = parseInt(this.itemCnt.scrollTop());
						var height = parseInt(this.getItemheight());
						this.itemCnt.scrollTop(oldTop+height+2);
					}
					break;
				case 38: // up
					if(parseInt(this._idx) == 0) return;
					
					this._getSelectingItem().removeClass("xw-combo-item-selecting");
					this._doItembg(this._getSelectingItem());
					this._idx = parseInt(this._idx) -1;
					this._getSelectingItem().addClass("xw-combo-item-selecting");
					this._getSelectingItem().css('background-color','');
					this._getSelectingItem().css('background-image','');
					if(this._idx >= parseInt(this._opt.size)){
						var oldTop = parseInt(this.itemCnt.scrollTop());
						var height = parseInt(this.getItemheight());
						this.itemCnt.scrollTop(oldTop-(height+2));
					}else{
						this.itemCnt.scrollTop(0);
					}
					break;
				case 27: case 9:// exe
					this._unexpand();
					break;
				}
			},
			_toggleExpand:function(event){
				if(this._isExpanded()){
					this._unexpand();
				}else{
					jQuery('div.xw-combo-item-cnt','body').hide();
					this._expand();
				}
				event.stopPropagation();
			},
			_focused:function(event){
				/*debug Xwing.debug('_focused'); */
				this._startTimer();
				this.oldText = this.comboInput.val();
				this.combo.addClass('xw-combo-focus');
				event.stopPropagation();
			},
			_blured:function(event){
				/*debug Xwing.debug('_blured'); */
				this._endTimer();
//				if(!this.getMultiselectable()) this._unexpand();
				this.comboInput.val(this.oldText); 
				this.combo.removeClass('xw-combo-focus');
				event.stopPropagation();
			},
			_clickout:function(event){
				/*debug Xwing.debug('_clickouted'); */
				this._unexpand();
			},
			_unexpand:function(){
				this._mousedown = false;
				var isExpand = this._isExpanded();
				this._idx = "-1";
				this.itemCnt.find("li.xw-combo-li").removeClass("xw-combo-item-selecting xw-filter-none");
				this._doItembg();
				this.itemCnt.css("display","none");
				
				if(isExpand)this._fire('hide',{type:'hide',source:this,event:null});
			},
			_expand:function(){
				if(this.getMultiselectable()){
					var thisObj = this;
					this.itemCnt.find("li.xw-combo-li").each(function(idx,el){
						var li = jQuery(this);
						var idx = li.attr('_idx');
						if( idx == 'all' ) return 1;
						var value = String(thisObj.varray[idx][0]);
						var vals = thisObj.getValue().split(',');
						if(vals.indexOf(value) != -1) jQuery('span',li).css('background-position','0 -40px').attr('multicheck','true');
						else jQuery('span',li).css('background-position','0 0').attr('multicheck','false');
					});
				}
				
				this.itemCnt.css("display","block");
				this.itemCnt.scrollTop(0);
				var w_width = document.documentElement.clientWidth;
				var w_height = document.documentElement.clientHeight;				
				var top = this.combo.offset().top + this.combo.height() + this.getBorderwidth() * 2;
				var c_width = (this.getFlexibleitem() ? this.itemCnt.width() : this.combo.width());
				
				if(w_height < (top + this.itemCnt.height())){
					top = w_height - this.itemCnt.height();
				}
				var left = this.combo.offset().left;
				if(left < 0 ){
					left = 0;
				}else if(w_width < (left + c_width)){
					left = w_width - c_width - 2;
				}

				this.itemCnt.css("left", left + "px");
				this.itemCnt.css("top", top + "px");
				this.itemCnt.find("li.xw-combo-li").css("display","block")
												.removeClass("xw-combo-item-selecting");
				this._idx="-1";
				this._maxidx = this.itemCnt.find("li.xw-combo-li").length - 1;
				
				this._fire('show',{type:'show',source:this,event:null});
			},
			_findLi : function(jNode) {
				var tagNodeName = '';
				while(true){
					tagNodeName = jNode[0].tagName.toLowerCase();
					if (tagNodeName == 'li') break;
					else if (tagNodeName == 'ul') return;
					else jNode = jNode.parent();
				}			
				return jNode;
			},
			_selected:function(event){
				event.stopPropagation();
				
				var t = this._findLi(jQuery(event.target));
				if(!t) return;
				var oldValue = this.getValue();
				var value = this.varray[t.attr('_idx')][0];
				var opt = {type:'changing',source:this,event:event,value:value,doit:true};
				if(oldValue != value) this._fire("changing",opt);
				if(opt.doit){
					this._unexpand();
					this.setValue(value);
					this._fire("select",{type:'select',source:this,event:event});
					if(oldValue != value) this._fire("change",{type:'change',source:this,value:value,event:event});
				}
			},
			_multiselected : function(event){
				event.stopPropagation();
				
				var check = jQuery(event.target);
				if(check.hasClass('xw-combo-check')){
					var li = this._findLi(jQuery(event.target));
					var idx = li.attr('_idx');
					var value = '';
					var oldValue = this.getValue();
					var newValue = oldValue ? oldValue.split(',') : [];
					if(idx == 'all'){
						var opt = {type:'changing',source:this,event:event,value:'all',index:idx,doit:true};
						this._fire("changing",opt);
						if(!opt.doit) return;
						if(check.attr('multicheck') != 'true'){
							this.ul.find('span.xw-combo-check').attr('multicheck','true').css('background-position','0 -40px');
							this.comboInput.val('전체');
							// value 가져와야 함.. 
							this.selectidx = '';
							for(var i=0; i < this.varray.length ; i++){
								if(this.selectidx == '') this.selectidx = String(i);
								else this.selectidx += ','+i;
								if(value == '' ) value = String(this.varray[i][0]);
								else value += ','+ this.varray[i][0];
							}
						}else{
							this.selectidx = '';
							this.ul.find('span.xw-combo-check').attr('multicheck','false').css('background-position','0 0');
							this.comboInput.val('');
						}
					}else{
						value = String(this.varray[idx][0]);
						if(check.attr('multicheck') == 'true'){						
							if(newValue.indexOf(value) != -1)
								newValue.splice(newValue.indexOf(value), 1);						
						}else {
							newValue.push(value);
						}
						value = newValue.join(',');
						var opt = {type:'changing',source:this,event:event,value:value,index:idx,doit:true};
						this._fire("changing",opt);
						if(!opt.doit) return;
						
						if(check.attr('multicheck') == 'true'){
							check.attr('multicheck','false').css('background-position','0 0');
						}else{
							check.attr('multicheck','true').css('background-position','0 -40px');
						}
						if( this.getItemall() && this.varray.length == this.ul.find('span.xw-combo-check:not(.xw-combo-all)[multicheck=true]').length){
							this.selectidx = '';
							value = '';
							for(var i=0; i < this.varray.length ; i++){
								if(this.selectidx == '') this.selectidx = String(i);
								else this.selectidx += ','+i;
								if(value == '' ) value = String(this.varray[i][0]);
								else value += ','+ this.varray[i][0];
							}
							this.ul.find('span.xw-combo-all').attr('multicheck','true').css('background-position','0 -40px');
							this.comboInput.val('전체');
						}else{
							var valueStr = '';
							this.selectidx = '';
							value = '';
							for(var i=0; i < this.varray.length ; i++){
								if (newValue.indexOf(String(this.varray[i][0])) != -1) {
									if(this.selectidx == '') this.selectidx = String(i);
									else this.selectidx += ','+i;
									if(value == '' ) value = String(this.varray[i][0]);
									else value += ','+ this.varray[i][0];
									if(valueStr == '' ) valueStr = String(this.varray[i][1]);
									else valueStr += ','+ this.varray[i][1];
								}
							}
							this.ul.find('span.xw-combo-all').attr('multicheck','false').css('background-position','0 0');
							this.comboInput.val(valueStr);
						}
					}
					this._opt.value = value;
					if( this.getBinddataset() && this.getBindcolumn()){
						var ds = Xwing.getDataset(this.getBinddataset())
						, colIdx= ds.getColumnIndex(this.getBindcolumn());
					
						ds._rows[ds.getCursor()][colIdx] = this._opt.value;
						ds._phys._rows[ds.getCursor()][colIdx] = this._opt.value;
					}
					this._fire("select",{type:'select',source:this,index:idx,selected:check.attr('multicheck'),event:event});
					if( oldValue!= value) this._fire("change",{type:'change',source:this,value:value,index:idx,selected:check.attr('multicheck'),event:event});
				}
			},
			_startTimer:function(){
				Xwing.debug("_startTimer");
				if( ! this.getEditable() ) return;
				
				var oldInput = this.comboInput.val();
				window.clearInterval(this.timer);
				var sss = this;
				this.timer = window.setInterval(function(){
					var vv = sss.comboInput.val();
					
					if(oldInput!= vv){
						if(! sss._isExpanded()) sss._expand();
						if(!sss.getMultiselectable()) sss._filter();
						oldInput = vv;
					}
					
				},50);
			},
			_endTimer:function(){
				Xwing.debug("_endTimer");
				window.clearInterval(this.timer);
			},
			_filter:function(){
				this._getSelectingItem().removeClass("xw-combo-item-selecting");
				this._doItembg(this._getSelectingItem());
				var value = this.comboInput.val();
				var items = this.itemCnt.find("li.xw-combo-li");
				var ff = this._hangulToJaso;
				var i = 0;
				items.each(function(){
					var item = jQuery(this);
					var v= item.find('span.xw-combo-item:last').html();
					if(ff(v).search(ff(value)) != 0){
						item.addClass('xw-filter-none');
						item.css("display","none");
//						item.attr("_idx",-1);
					}else {
						item.removeClass('xw-filter-none');
						item.css("display","block");
						i++;
//						item.attr("_idx",(i++));
						/*debug Xwing.debug(item); */
					}
				});
				this._idx = "-1";
				this.itemCnt.find("div.xw-combo-item").removeClass("xw-combo-item-selecting");
				this._maxidx = --i;
			},
			_hangulToJaso : function(s){
				var ChoSung = [ 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146,
	 			   0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e ];
	 			 // ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ ㅙ ㅚ ㅛ ㅜ ㅝ ㅞ ㅟ ㅠ ㅡ ㅢ ㅣ
	 			var JwungSung = [ 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159,
	 			   0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 ];
	 			 // ㄱ ㄲ ㄳ ㄴ ㄵ ㄶ ㄷ ㄹ ㄺ ㄻ ㄼ ㄽ ㄾ ㄿ ㅀ ㅁ ㅂ ㅄ ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
	 			var JongSung = [ 0, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
	 			   0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c,
	 			   0x314d, 0x314e ];
	 			  	
	 			  var a, b, c; // 자소 버퍼: 초성/중성/종성 순
	 			  var result = "";
	 			  for (var i = 0; i < s.length; i++) {
	 			   var ch = s.charCodeAt(i);
	 			   
	 			   if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
	 			    c = ch - 0xAC00; 
	 			    a = Math.floor(c / (21 * 28));
	 			    c = c % (21 * 28); 
	 			    b = Math.floor(c / 28);      
	 			    c = c % 28;    
	 			    
	 			    result = result + new String(ChoSung[a]) + new String(JwungSung[b]);
	 			    if (c != 0)
	 			     result = result + new String(JongSung[c]); // c가 0이 아니면, 즉 받침이 있으면
	 			   } else {
	 			    result = result + new String(ch);
	 			   }
	 			}
	 			return result;
			},
			domaindatasetListener : function(dsEvent){
				if(dsEvent.type=='cursor') return;
				this._doDomain();
			},
			getText : function(){
				return this.comboInput[0].value;
			},
			_doCursor : function(){
				if(window.xwingIDE) return;			
				var cursor = this.getCursor();
				switch(cursor){
				case "default":
					this.imgDiv.css('cursor','');
					break;
				default:
					this.imgDiv.css('cursor',cursor);
				}
			},
			focus : function(){
				this.comboInput[0].focus();
			},
			setItemfontcolor : function(v){
				this._opt.itemfontcolor = v;
				this._doItemfont();
			},
			getItemfontcolor : function(){
				return this._opt.itemfontcolor;
			},
			setItemfontdecoration : function(v){
				this._opt.itemfontdecoration = v;
				this._doItemfont();
			},
			getItemfontdecoration : function(){
				return this._opt.itemfontdecoration;
			},
			setItemfontfamily : function(v){
				this._opt.itemfontfamily = v;
				this._doItemfont();
			},
			getItemfontfamily : function(){
				return this._opt.itemfontfamily;
			},
			setItemfontsize : function(v){
				this._opt.itemfontsize = v;
				this._doItemfont();
			},
			getItemfontsize : function(){
				return this._opt.itemfontsize;
			},
			setItemfontstyle : function(v){
				this._opt.itemfontstyle = v;
				this._doItemfont();
			},
			getItemfontstyle : function(){
				return this._opt.itemfontstyle;
			},
			setItemfontweight : function(v){
				this._opt.itemfontweight = v;
				this._doItemfont();
			},
			getItemfontweight : function(){
				return this._opt.itemfontweight;
			},
			_doItemfont : function(){
				var items = jQuery('span.xw-combo-item',this.itemCnt);
				items.css('color' , this.getItemfontcolor());
				items.css('text-decoration' , this.getItemfontdecoration());
				items.css('font-size' , this.getItemfontsize()+'px');
				items.css('font-style' , this.getItemfontstyle());
				items.css('font-weight' , this.getItemfontweight());
				
				try {
					if (this.getItemfontfamily && Xwing.config.fonts && Xwing.config.fonts[this.getItemfontfamily()]) {
						items.css("font-family", Xwing.config.fonts[this.getItemfontfamily()]);
					}
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontfamily" + +":" + e);
				}
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
			setItembggradientdir : function(v){
				this._opt.itembggradientdir = v;
				this._doItembg();
			},
			getItembggradientdir : function(){
				return this._opt.itembggradientdir
			},
			_doItembg : function(mod){
				var items = mod;
				if(items === undefined) items = jQuery('li.xw-combo-li',this.itemCnt);
				
				items.css('background-color',this.getItembgcolor());
				if(this.getItembggradientcolor() && this.getItembgcolor()){					
					items.css('background-color','');
					if(Xwing.isIE()){
						var gradientType = (this.getItembggradientdir() == "vertical"? "GradientType=0" : "GradientType=1" ); 						
						items.css('filter',"progid:DXImageTransform.Microsoft.gradient("+gradientType+",startColorstr='"+this.getItembgcolor()+"', endColorstr='"+this.getItembggradientcolor()+"')");
					}else{
						if(this.getItembggradientdir() == "vertical"){
							if(jQuery.browser.mozilla){
								items.css("background-image", "-moz-linear-gradient(top, "+this.getItembgcolor()+", "+this.getItembggradientcolor()+")");
							}else if(jQuery.browser.webkit){
								items.css("background-image", "-webkit-gradient(linear, left top, left bottom, from("+this.getItembgcolor()+"), to("+this.getItembggradientcolor()+") )");
							}
						}else{
							if(jQuery.browser.mozilla){							
								items.css("background-image", "-moz-linear-gradient(left,"+this.getItembgcolor()+", "+this.getItembggradientcolor());
							}else if(jQuery.browser.webkit){							
								items.css("background-image", "-webkit-gradient(linear, left top, right bottom, from("+this.getItembgcolor()+"), to("+this.getItembggradientcolor()+") )");
							}
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
				jQuery('li.xw-combo-li',this.itemCnt).not(':last').css('border-bottom-color',this.getItembordercolor());
			},
			setItemheight : function(v){
				this._opt.itemheight = v;
				this._doItemheight();
			},
			getItemheight : function(){
				return xwing.Util.parseInt(this._opt.itemheight,0);
			},
			_doItemheight : function(){
				var itemh = this.getItemheight()+'px';
				jQuery('li.xw-combo-li',this.itemCnt).height(itemh).css('line-height',itemh);
				
				if(parseInt(this._opt.size) > 0 )
					this.itemCnt.css("height",(( parseInt(this._opt.size) *(parseInt(itemh)+this.getBorderwidth()*2)))+"px");
			},
			setItempadding : function(v){
				this._opt.itempadding = v;
				this._doItempadding();
			},
			getItempadding : function(){
				return this._opt.itempadding;
			},
			_doItempadding : function(){
				var fontMod = jQuery('span.xw-combo-item:odd',this.itemCnt);
				var paddings = (this.getItempadding() == null ? "" : this.getItempadding().trim().split(/\s+/));
				var result = "";
				if(fontMod){
					for(var i =0 ; i < paddings.length ; i++){
						var tmp = paddings[i]+'px ';
						result += tmp;
					}
					fontMod.css('padding',result);
				}
			},
			setInputmode : function(v){
				this._opt.inputmode = v;
				this._doInputmode();
			},
			getInputmode : function(){
				return this._opt.inputmode;
			},
			_doInputmode : function(){
				this.comboInput.css('text-transform',this.getInputmode());
			},
			setItemindex : function(v){
				this._opt.itemindex = v;
				this._doItemindex();
			},
			getItemindex : function(){
				return this.selectidx;
			},
			_doItemindex : function(){
				var value = this._getValue(this._opt.itemindex);
				this.setValue(value);
			},
			setMultiselectable : function(v){
				this._opt.multiselectable = v;
				this._doMultiselectable();
			},
			getMultiselectable : function(){
				return xwing.Util.parseBoolean(this._opt.multiselectable,false);
			},
			_doMultiselectable : function(){
				if(this.getMultiselectable()){
					jQuery('span[multicheck]',this.itemCnt).addClass('xw-combo-check');
				}else{
					jQuery('span[multicheck]',this.itemCnt).removeClass('xw-combo-check');
				}
			},
			_getValue : function(idx){
				var domainDS = Xwing.getDataset(this._opt.domaindataset);
				if (domainDS == undefined || this._opt.itemindex == undefined || domainDS.size()-1 < this._opt.itemindex ) return this.getValue();
				var idxs = this._opt.itemindex.split(',');
				if(!this.getMultiselectable()) idxs = idxs.slice(0,1);
				var value = "";
				for(var i=0; i < idxs.length;i++){
					var val = domainDS.getValue(idxs[i], this._opt.domaincodecolumn);
					value == "" ? value += val : value += "," + val;
				}
				return value;

			},
			isDropdown : function(){
				return this._isExpanded();
			},
			dropDown : function(){
				if(!this._isExpanded()){
					this._expand();
				}
			},
			setItemall : function(v){
				this._opt.itemall = v;
				this._doItemall();
			},
			getItemall : function(){
				return xwing.Util.parseBoolean(this._opt.itemall);
			},
			_getUseItemall : function(){
				if( this.getMultiselectable() && this.getItemall() )return true;
				else return false;
			},
			_doItemall : function(){
				if( this._getUseItemall()){
					var li = jQuery("<li class='xw-combo-li xw-combo-all' _idx='all'/>").prependTo(this.ul)
					.css("height",this._opt.itemheight+"px");
					jQuery("<span class='xw-combo-item xw-combo-check xw-combo-all' multicheck='false' ></span>").appendTo(li);
					jQuery("<span class='xw-combo-item' style='line-height:"+this._opt.itemheight+"px;margin:1px;' >전체</span>").appendTo(li);
				}
			},
			setMultiall : function(v){
				if( this._getUseItemall()){
					var val = '';
					this.selectidx = '';
					if(v == 'true' || v == true){
						for(var i=0; i < this.varray.length ; i++){
							if(this.selectidx == '') this.selectidx = String(i);
							else this.selectidx += ','+i;
							if(val == '' ) val = String(this.varray[i][0]);
							else val += ','+ this.varray[i][0];
						}
						this.ul.find('span.xw-combo-check').attr('multicheck','true').css('background-position','0 -40px');
						this.comboInput.val('전체');
					}else{
						this.ul.find('span.xw-combo-check').attr('multicheck','false').css('background-position','0 0');
						this.comboInput.val('');
					}
						
					
					this._opt.value = val;
				}
			},
			getAllCheck : function(){
				var res = false;
				if( this._getUseItemall()){
					if( this.ul.find('li.xw-combo-all span.xw-combo-check').attr('multicheck') == 'true') res = true;
				}
				return res;
			},
			setFlexibleitem : function(v){
				this._opt.flexibleitem = v;
//				this._doBounds();
			},
			getFlexibleitem : function(){
				return xwing.Util.parseBoolean(this._opt.flexibleitem, false);
			}
		}	
	}
});
