Class.define({
	Menu : {
		alias : 'menu',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Menu : function(json){
			this._init(json);
		},
		statics:{
			create : function(json){
				return new xwing.widget.Menu(json);
			}			
		},
		prototypes : {
			_createPart : function(){
				this.menu = jQuery("<div class='xw-menu xw-mod-border xw-mod-background'/>");				
				this.ul = jQuery("<ul class='xw-menu-bar'/>").appendTo(this.menu);
				this.menu.appendTo(this._getJShell());
				
				this._bindEventListener();
			},	
			_bindEventListener : function(){
				this._barItemClick = false;
				this._bind(this.ul,'mouseover',this._barOver);
				this._bind(this.ul,'click',this._barClick);
				var thisObj = this;
				jQuery('body').bind('click',function(event){
					thisObj._barItemClick = false;
					jQuery('body').find('li.xw-menu-bar-item-over').removeClass('xw-menu-bar-item-over');
					jQuery('body').find('div.xw-menu-submenu').empty().remove();
				});
				jQuery('body').bind('mouseover',function(event){
					!thisObj._barItemClick &&
						thisObj.ul.find('li.xw-menu-bar-item-over').removeClass('xw-menu-bar-item-over');
					jQuery('body').children('div.xw-menu-submenu').find('li.xw-menu-submenu-item-over:not([_leafyn=N])').removeClass('xw-menu-submenu-item-over');
				});
			},
			_barOver : function(event){
				event.stopPropagation();
				var jTargetLi = this._findjLi(event);
				if(!jTargetLi || jTargetLi.hasClass('xw-menu-item-disabled')) return;
				jTargetLi.siblings().removeClass('xw-menu-bar-item-over');
				jTargetLi.addClass("xw-menu-bar-item-over");
				
				(this._barItemClick && (jQuery('body').find('div.xw-menu-submenu[_pid='+jTargetLi.attr('_menuid')+']').length == 0) )
				&& this._controlSubmenu(jTargetLi);
			},
			_barClick : function(event){
				event.stopPropagation();
				jQuery('body').find('div.xw-menu-shell:not([id='+this.getId()+'])').find('li.xw-menu-bar-item-over').removeClass('xw-menu-bar-item-over');
				var jTargetLi = this._findjLi(event);
				if(!jTargetLi || jTargetLi.hasClass('xw-menu-item-disabled')) return;
				
				if(jTargetLi.attr("_leafyn") == 'N'){
					this._barItemClick = this._barItemClick ? false : true;
					this._controlSubmenu(jTargetLi);
				}else {
					this._menu = jTargetLi.attr("_menuid");
					jQuery('body').trigger('click');
					this._fire("select",{type:'select',source:this,event:event});
					jTargetLi.addClass("xw-menu-bar-item-over");
				}
			},
			_controlSubmenu : function(jTargetLi){
				//this._makeSubmenu(jTargetLi);
				var subMenu = jQuery('body').find('div.xw-menu-submenu[_pid='+jTargetLi.attr('_menuid')+']');
				jQuery('body').find('div.xw-menu-submenu').empty().remove();
				if(subMenu.size() == 0){
					var pid =jTargetLi.attr("_menuid");
					var dataset = Xwing.getDataset(this.getBinddataset());
					if((jQuery('div[id='+pid+'_submenu]').size() > 0) || (dataset.indexOfRow(this._opt.pidcolumn,pid) == -1)) return;
					
					this._makeSubmenu(jTargetLi);
				}
			},
			_findjLi : function(event){
				if(event.target.tagName.toLowerCase() == 'ul') return;
				var jTar = jQuery(event.target); 
				while(true){
					if(jTar.hasClass('xw-menu-bar-item') || jTar.hasClass('xw-menu-item-disabled') || jTar.hasClass('xw-menu-submenu-item')) break;
					else if(jTar[0].tagName.toLowerCase() == 'div') return;
					else jTar = jTar.parent();
				}
				return jTar;
			},
			_doValue:function(){
				var dataset = Xwing.getDataset(this.getBinddataset());
				this.ul.empty();

				if (!dataset || !dataset.hasColumn(this._opt.textcolumn) || !dataset.hasColumn(this._opt.idcolumn) || !dataset.hasColumn(this._opt.pidcolumn)) return;
				var cnt = dataset.size();
				for ( var i = 0; i < cnt; i++) {
					if(dataset.getValue(i,this._opt.pidcolumn) == ""){
						var item = jQuery("<li class='xw-menu-bar-item xw-mod-font' _topyn='Y' _leafyn='"+dataset.getValue(i,this._opt.leafyncolumn)+"' _menuid='"+dataset.getValue(i,this._opt.idcolumn)+"' style='text-align:center;height:100%;display:inline-block;'/>");
						item.css('line-height',(this.getHeight() - this.getBorderwidth()*2 - 6)+'px');
						if(dataset.hasColumn(this._opt.enabledcolumn)){
							var enabled = dataset.getValue(i,this._opt.enabledcolumn);
							if (enabled == 'true') {
								item.removeClass('xw-menu-item-disabled').addClass('xw-menu-bar-item').css('padding-right','5px');
							} else {
								item.addClass('xw-menu-item-disabled').removeClass('xw-menu-bar-item')
									.css('padding-right','5px');
							}
						}
						if(dataset.getValue(i,this._opt.iconcolumn))
							item.append(jQuery("<span class='xw-menu-bar-icon'/>").css('background-image','url('+dataset.getValue(i,this._opt.iconcolumn)+')'));
						
						this.ul.append(item.append(jQuery("<span class='xw-menu-bar-text' unselectable='on' />").text(dataset.getValue(i,this._opt.textcolumn))));
					}
				}
				this._branding();
				this._doCursor();
			},
			_makeSubmenu:function(pitem){
				var pid =pitem.attr("_menuid");
				var topyn = pitem.attr("_topyn");
				var x = pitem.offset().left;
				var y = pitem.offset().top + pitem.outerHeight();
				if(topyn != 'Y'){
					x =  pitem.offset().left + pitem.outerWidth() - 3;
					y =  pitem.offset().top -1;
				}
				var dataset = Xwing.getDataset(this.getBinddataset());
				var menu = jQuery("<div class='xw-menu-submenu xw-mod-background' id='"+pid+"_submenu' _pid='"+pid+"' style='left:"+x+"px;top:"+y+"px;' />")
				           .appendTo(jQuery('body'));
				var ul  = jQuery("<ul class='xw-menu-submenu-cnt' />").appendTo(menu);
				
				var cnt = dataset.size();
				for(var i=0;i<cnt;i++){
					if(dataset.getValue(i,this._opt.pidcolumn) == pid){
						var v = dataset.getValue(i,this._opt.idcolumn);
						var leaf = dataset.getValue(i,this._opt.leafyncolumn);
						var menuItem = jQuery("<li class='xw-menu-submenu-item xw-mod-font'  _leafyn='"+leaf+"' _pid='"+pid+"' _menuid='"+v+"' />").appendTo(ul);
						if(dataset.hasColumn(this._opt.enabledcolumn)){
							var enabled = dataset.getValue(i,this._opt.enabledcolumn);
							if (enabled == 'true') {
								menuItem.removeClass('xw-menu-item-disabled').addClass('xw-menu-submenu-item');
							} else {
								menuItem.addClass('xw-menu-item-disabled').removeClass('xw-menu-submenu-item')
										.css('padding-right','');
							}
						}
						menuItem.append(jQuery('<span class="xw-menu-submenu-icon" />').css('background-image','url('+dataset.getValue(i,this._opt.iconcolumn)+')'));
						menuItem.append(jQuery("<span class='xw-menu-submenu-text' />").text(dataset.getValue(i,this._opt.textcolumn)));
						(leaf == 'N') && 
						( menuItem.append(jQuery('<span class="xw-menu-submenu-next" />')));
						ul.append(menuItem);
						if(dataset.getValue(i,this._opt.separatorcolumn) || dataset.getValue(i,this._opt.separatorcolumn) == 'true'){
							menuItem.before(jQuery('<div class="xw-menu-submenu-separator" />'))
						}
					}
				}
				
				this._bindSubEventListener(ul);
				this._branding();
				this._doFont(menu);
				this._doBackground(menu);
			},
			_bindSubEventListener : function(jUl){
				this._bind(jUl,'mouseover',function(event){
					event.stopPropagation();
					var jTargetLi = this._findjLi(event);
					if(!jTargetLi || jTargetLi.hasClass('xw-menu-item-disabled')) return;
					// remove sibling menu 
					var id = [jTargetLi.siblings('.xw-menu-submenu-item-over').attr('_menuid')];
					while(true){
						var tmpid = [];
						for(var i=0, l=id.length; i<l; i++){
							var openSubmenu = jQuery('div.xw-menu-submenu[_pid='+id[i]+']','body');
							openSubmenu.find('li.xw-menu-submenu-item-over').each(function(idx,el){
								if(this.getAttribute('_leafyn') == 'N') tmpid.push(this.getAttribute('_menuid'));
							});
							openSubmenu.empty().remove();
						}
						if(tmpid.length == 0 ) break;
						id = tmpid;
					}
					jTargetLi.siblings().removeClass('xw-menu-submenu-item-over');
					jTargetLi.addClass("xw-menu-submenu-item-over");
					
					// open sumMenu
					if((jTargetLi.attr('_leafyn') == 'N') &&  jQuery('body').find('div.xw-menu-submenu[_pid='+jTargetLi.attr('_menuid')+']').length == 0) 
						this._makeSubmenu(jTargetLi); 
					
				});
				this._bind(jUl,'click',function(event){ 
					event.stopPropagation();
					var jTargetLi = this._findjLi(event);
					if(!jTargetLi || jTargetLi.hasClass('xw-menu-item-disabled')) return;
					if(jTargetLi.attr('_leafyn') == 'Y'){
						this._menu = jTargetLi.attr("_menuid");
						jQuery('body').trigger('click');
						this._fire("select",{type:'select',source:this,event:event});
					}
				});
			},
			getSelectedMenu :function(){
				return this._menu;
			},
			_doCursor : function(){
				if(window.xwingIDE) return;			
				var item = jQuery('li.xw-menu-bar-item',this.ul);
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				item.css('cursor',cursor);
			}
		}	
	}
});
