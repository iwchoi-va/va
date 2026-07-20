Class.define({
	Tree : {
		alias : 'tree',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Tree : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Tree(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.tree = jQuery("<div class='xw-tree xw-mod-border xw-mod-background' tabindex='-1' />");
				this.ul = jQuery("<ul style='margin:0px;padding:0px;list-style:none outside none;'/>").appendTo(this.tree);
				this.tree.appendTo(this._getJShell());
				
				this._bindEventListener();
				this._bindScrollPane(this.ul);
			},
			_bindEventListener : function(){
				this._bind(this.tree, "scroll", this._scroll);
				this._bind(this.tree, "keydown", this._keydown);
				
				this._bind(this.ul, 'mousedown', this._mousedown);
				this._bind(this.ul, 'mouseup', this._click);
				this._bind(this.ul, 'dblclick', this._dblclick);				
			},
			_mousedown : function(event) {
				var jTar = jQuery(event.target);
				if (jTar.hasClass('xw-tree-handle'))
					this._handleClicked(event);

				while (true) {
					if (jTar[0].tagName.toLowerCase() == 'div' && jTar.hasClass('xw-tree-item')) {
						break;
					} else if (jTar[0].tagName.toLowerCase() == 'li' || jTar[0].tagName.toLowerCase() == 'ul') {
						jTar = jTar.children(":first");
					} else {
						jTar = jTar.parent();
					}
				}
				event.currentTarget = jTar[0];
				this._selecting(event);
			},
			_click : function(event) {
				var jTar = jQuery(event.target);

				while (true) {
					if (jTar[0].tagName.toLowerCase() == 'div' && jTar.hasClass('xw-tree-item')) {
						break;
					} else if (jTar[0].tagName.toLowerCase() == 'li' || jTar[0].tagName.toLowerCase() == 'ul') {
						jTar = jTar.children(":first");
					} else {
						jTar = jTar.parent();
					}
				}
				event.target = jTar[0];
				this._selected(event);
			},
			_dblclick : function(event) {
				event.target = jQuery(event.target).children('img.xw-tree-handle');
				this._handleClicked(event);
			},
			_scroll : function(event) {
				if (this.tree.scrollTop() == 0) {
					this.scrollBottom = "top";
					this._fire("scrolltop", {
						type : "scrolltop",
						source : this,
						event : event
					});
				}
				if ((this.ul.outerHeight() - this.tree.innerHeight()) <= this.tree.scrollTop()) {
					if (this.scrollBottom != "bottom") {
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

				this._fire("scroll", {
					type : "scroll",
					source : this,
					event : event
				});
			},
			_keydown : function(event) {
				if (event.keyCode != 9)
					event.preventDefault();
				var dsObj = Xwing.getDataset(this._opt.binddataset);
				if (!dsObj) return;
				var curDiv = jQuery(event.currentTarget).find('div.xw-tree-item-selected[_nid=' + dsObj.getValue(this._opt.idcolumn) + ']');
				if (!curDiv[0]) return;
				
				switch (event.keyCode) {
				case 38:
				case 40: // up, down arrow key
					var nextItem = (event.keyCode == 38 ? this._findNextItem('up', curDiv) : this._findNextItem('down', curDiv));
					if (!nextItem)
						return;

					this._moveSelect((event.keyCode == 38 ? 'up' : 'down'), nextItem, event);
					break;
				case 39: // right arrow key
					if (curDiv.attr('leafyn') == 'Y')
						return;
					if (curDiv.next().css('display') == 'none') {
						event.target = curDiv.children('img.xw-tree-handle');
						this._handleClicked(event);
					} else {
						var nextItem = this._findNextItem('down', curDiv);
						nextItem && this._moveSelect('down', nextItem, event);
					}
					break;
				case 37: // left arrow key
					if (curDiv.attr('leafyn') == 'N') {
						if (curDiv.next().css('display') == 'block') {
							event.target = curDiv.children('img.xw-tree-handle');
							this._handleClicked(event);
						} else {
							var nextItem = curDiv.parent().parent().prev();
							if (nextItem.length != 0) {
								this._moveSelect('up', nextItem, event);
							}
						}
					} else {
						var nextItem = curDiv.parent().parent().prev();
						var curDepth = parseInt(curDiv.attr('_depth'));
						while (true) {
							var nextDepth = parseInt(nextItem.attr('_depth'));
							if (nextItem.attr('leafyn') == 'N' && nextDepth == (curDepth - 1)) {
								break;
							} else {
								curDepth = nextDepth;
								nextItem = nextItem.parent().parent().prev();
								if (nextItem.length == 0)
									break;
							}
						}
						this._moveSelect('up', nextItem, event);
					}
					break;
				case 32:
					event.target = curDiv.children('img.xw-tree-check');
					this._toggleCheck(event);
					break;
				}
			},
			_findNextItem : function(type, curDiv) {
				if (type == 'up') {
					var curLi = curDiv.parent();
					if (curLi.prev().length != 0) {
						var jul = curLi.prev().children('ul');
						while (true) {
							if (jul.css('display') == 'block') {
								jul = jul.children('li:last').children('ul');
								if (jul.length == 0)
									return;
							} else {
								return (jul.prev().length == 0 ? undefined : jul.prev());
							}
						}
					} else if (curDiv.parent().parent().prev().length != 0) {
						return curDiv.parent().parent().prev();
					}
				} else {
					if (curDiv.next().css('display') == 'none') {
						var curLi = curDiv.parent();
						while (curLi) {
							if (curLi.next().length == 0) {
								curLi = curDiv.parent().parent().parent();
								curDiv = curLi.children('div');
								if (curLi.length == 0)
									return;
							} else {
								return (curLi.next().children('div').length == 0 ? undefined : curLi.next().children('div'));
							}
						}
					} else {
						return curDiv.next().children('li:first').children('div');
					}
				}
			},
			_moveSelect : function(type, nextItem, event) {
				event.currentTarget = nextItem[0];
				this._selecting(event);
				
				event.target = nextItem.find('span.xw-tree-span:first');
				this._selected(event);

				if (type == 'up') {
					var interval = nextItem.offset().top - this.tree.offset().top - this.getBorderwidth() * 2 - 4;
					if (interval <= 0)
						this.tree.scrollTop((this.tree.scrollTop() + interval) < 0 ? 0 : (this.tree.scrollTop() + interval));
				} else {
					var interval = (nextItem.offset().top + nextItem.height()) - (this.tree.offset().top + this.getBorderwidth() * 2 + this.tree.height());
					if (interval >= 0)
						this.tree.scrollTop((this.tree.scrollTop() + interval) < 0 ? 0 : (this.tree.scrollTop() + interval));
				}
			},
			_doFont : function(){
				if( !this.getExpr() )
					xwing.widget.DataBindable.prototype._doFont.call(this);
			},
			_doValue : function() {
				try {
					this.tree.children("ul").empty();
					this.dataset = Xwing.getDataset(this.getBinddataset());
					if (!this.dataset)
						return;
					
					for ( var i = 0, len = this.dataset.size(); i < len; i++) {
						this._addRow(i);
					}

					this._doTreeline(0, this.ul);
					this._branding();
					
					if( !this.getExpr() ){
						this._doFont();
						this._doTextpadding();
						this._doCursor();
					}
					
					this._refreshScrollPane();
					this._doDataDragDrop();
				} catch (e) {
					/*debug Xwing.debug(e); */
				}
			},
			_addRowByEvent : function(i) {
				this._addRow(i);
				this._doTreeline(0, this.ul);
				
				if( !this.getExpr() ){
					this._doFont();
					this._doTextpadding();
					this._doCursor();
				}
			},
			_addRow:function(i){
				var depth = 0; 
				var value = this.dataset.getValue(i,this._opt.textcolumn);
				var id = this.dataset.getValue(i,this._opt.idcolumn);
				var pid = this.dataset.getValue(i,this._opt.pidcolumn);
				var leaf_yn = this.dataset.getValue(i,this._opt.leafyncolumn);
				var close = this.dataset.getValue(i,this._opt.nodeiconcolumn) || Xwing.XWING_HOME+"css/themes/"+xwing.Xwing.config.theme+"/img/tree/folder.gif";
				var leaf = this.dataset.getValue(i,this._opt.nodeiconcolumn) || Xwing.XWING_HOME+"css/themes/"+xwing.Xwing.config.theme+"/img/tree/page.gif";
				if(id == null || id == "" || pid === undefined) id = i;
				if(leaf_yn == null || leaf_yn == "" || leaf_yn === undefined) leaf_yn = "N";
				var pnode = this.tree.find('li.xw-tree-li[_nid='+pid+']:last');
				
				if (pnode.size() == 0) {
					if (xwing.Util.isTouchDevice()) pnode = this.wrapper || this.tree;
					else pnode = this.tree;
				} else depth = parseInt(pnode.attr('_depth'))+1;
				
				var li = jQuery("<li class='xw-tree-li' _nid='"+id+"' _depth='"+depth+"' />").appendTo(pnode.children("ul"));
				var item = jQuery("<div class='xw-tree-item' leafyn='"+leaf_yn+"' _nid='"+id+"' _depth='"+depth+"' _dsid='"+i+"' />").appendTo(li).css({ 'height' : this.getRowheight() + 'px', 'line-height' : this.getRowheight() + 'px'});
				jQuery("<ul class='xw-tree-ul' />").appendTo(li);
				
				// Make Font Style 
				var style = this._makeStyle(depth, i);
				var span = jQuery("<span unselectable='on' class='xw-tree-span xw-mod-font' "+(style ? ("style='"+style+"' ") : '')+" ></span>").text(value).appendTo(item).attr("_xid", this.getXId());
				var base = Xwing.XWING_HOME + 'css/images/base.gif';
				
				for(var j=0; j < depth; j++){
					 jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='"+j+"' />").insertBefore(span);
				}
				if(leaf_yn == 'Y'){
					jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent' _line='"+depth+"' />").insertBefore(span);
					if(leaf) jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent xw-tree-leaf' _line='"+depth+"' />").css('background-image','url('+leaf+')').insertBefore(span).css('margin-left',(this.getLeafmargin() == 0 ? '' : this.getLeafmargin()+'px'));
				}else{
					jQuery("<img src='"+base+"' onselectable='on' _nid='"+id+"' class='xw-tree-handle xw-tree-plus' />").insertBefore(span);
					var icon = jQuery("<img src='"+close+"' onselectable='on' class='xw-tree-icon' />").insertBefore(span);
					if(!close) icon.css('width',0);
					else icon.css('width','');
					this._bind(icon,"dblclick",this._handleClicked);
				}
				this._bind(span,"dblclick",this._handleClicked);
				
				if(this._opt.checkable == 'true') jQuery("<img src='"+base+"' class='xw-tree-check xw-tree-check-unchecked' mode='edit' />").insertBefore(span);
				
				return;
			},
			setEditable: function(value){
				this._opt.editable = (value == true||value=='true')?true:false;
				if(this._opt.editable) this.tree.sortable();
			},
			setCheckable:function(value){
				this._opt.checkable = value;
				this._doValue();
			},
			getCheckable : function(){
				return xwing.Util.parseBoolean(this._opt.checkable,false);
			},
			setIdcolumn:function(value){
				this._opt.idcolumn = value;
				this._doValue();
			},
			setPidcolumn:function(value){
				this._opt.pidcolumn = value;
				this._doValue();
			},
			setLeafyncolumn:function(value){
				this._opt.leafyncolumn = value;
				this._doValue();
			},
			setTextcolumn:function(value){
				this._opt.textcolumn = value;
				this._doValue();
			},
			_selecting : function(event) {
				if (this._opt.checkable == 'true') {
					jQuery(event.target).hasClass('xw-tree-check') && this._toggleCheck(event);
				}
				
				var t = jQuery(event.currentTarget);
				if (event.ctrlKey && this._opt.multiselectable == 'true') {
					if (t.hasClass("xw-tree-item-selected")) {
						t.removeClass("xw-tree-item-selected");
					} else {
						t.addClass("xw-tree-item-selected");
					}
				} else {
					this.tree.find("div.xw-tree-item").removeClass("xw-tree-item-selected");
					t.addClass("xw-tree-item-selected");
				}
			},
			_selected : function(event) {
				try {
					var t = jQuery(event.target).parent();
					var oldidx = this.dataset.getCursor();
					var idx = this.dataset.indexOfRow(this._opt.idcolumn, t.attr("_nid"));
					this.dataset.setCursor(idx, this);

					this._fire("select", {
						type : 'select',
						source : this,
						event : event
					});
					
					if (oldidx != idx) 
						this._fire("change", {
							type : 'change',
							source : this,
							event : event
						});
				} catch (e) {
					/*debug Xwing.debug(e); */
				}
			},
			_toggleCheck : function(event, first) {
				var t = jQuery(event.target).parent().parent().find("img.xw-tree-check"+(first ? ':first' : ''));
				if (this._opt.multiselectable == 'false') {
					this.tree.find("img.xw-tree-check").removeClass("xw-tree-check-checked");
					this.tree.find("img.xw-tree-check").addClass("xw-tree-check-unchecked");
				}
				if (t.hasClass("xw-tree-check-checked")) {
					t.removeClass("xw-tree-check-checked");
					t.addClass("xw-tree-check-unchecked");
				} else {
					t.removeClass("xw-tree-check-unchecked");
					t.addClass("xw-tree-check-checked");
				}
			},
			_handleClicked:function(event){
				var target = jQuery(event.target);
				var div = target.parent();
				if( event.target.tagName == 'div') div = jQuery(event.target);
				if(div.attr("leafyn") =='Y') return;
				var ds = Xwing.getDataset(this._opt.binddataset);

				var close = ds.getValue(target.parent().attr("_dsid"),this._opt.nodeiconcolumn) || Xwing.XWING_HOME+"css/themes/"+xwing.Xwing.config.theme+"/img/tree/folder.gif";
				var open = ds.hasColumn(this._opt.foldericoncolumn)?ds.getValue(target.parent().attr("_dsid"),this._opt.foldericoncolumn):Xwing.XWING_HOME+"css/themes/"+xwing.Xwing.config.theme+"/img/tree/folderopen.gif";
				
				var ul = div.siblings();
				var handle = div.children('img.xw-tree-handle');
				var img = div.children('img.xw-tree-icon');
				var last = div.attr('_last');
				if(ul.css('display') == 'none'){
					ul.show();
					handle.removeClass("xw-tree-plus").addClass("xw-tree-minus");
					if(this.getTreeline() && !this.getTreelineimage()){
						if(last == 'Y'){
							if(handle.hasClass('xw-tree-line-plus-end-first'))
								handle.addClass('xw-tree-line-minus-end-first').removeClass('xw-tree-line-plus-end-first');
							else handle.addClass('xw-tree-line-minus-end').removeClass('xw-tree-line-plus-end');
						}else{
							if(handle.hasClass('xw-tree-line-plus-first'))
								handle.addClass('xw-tree-line-minus-first').removeClass('xw-tree-line-plus-first');
							else handle.addClass('xw-tree-line-minus').removeClass('xw-tree-line-plus');
						}
					}
					
					if(open) img.attr('src',open).removeClass('xw-tree-close-icon').addClass('xw-tree-open-icon').css('width','');
					
					this._fire("expand",{type:'expand',source:this,event:event});
				}else{
					ul.hide();
					handle.removeClass("xw-tree-minus").addClass("xw-tree-plus");
					if(this.getTreeline() && !this.getTreelineimage()){
						if(last == 'Y'){
							if(handle.hasClass('xw-tree-line-minus-end-first'))
								handle.addClass('xw-tree-line-plus-end-first').removeClass('xw-tree-line-minus-end-first');
							else handle.addClass('xw-tree-line-plus-end').removeClass('xw-tree-line-minus-end');
						}else{
							if(handle.hasClass('xw-tree-line-minus-first'))
								handle.addClass('xw-tree-line-plus-first').removeClass('xw-tree-line-minus-first');
							else handle.addClass('xw-tree-line-plus').removeClass('xw-tree-line-minus');
						}
					}
					if(close) img.attr('src',close).removeClass('xw-tree-open-icon').addClass('xw-tree-close-icon').css('width','');
					
					this._fire("collapse",{type:'collapse',source:this,event:event});
				}
				
				this._refreshScrollPane();
				event.stopPropagation();
			},
			getSelectionCnt : function() {
				return this.tree.find("div.xw-tree-item").filter(".xw-tree-item-selected").size();
			},
			getSelection : function() {
				var items = this.tree.find("div.xw-tree-item");
				var selectedItem = [];

				for ( var i = 0, size = items.size(); i < size; i++) {
					var condition = (this.getCheckable() ? jQuery(items[i]).children('img.xw-tree-check').hasClass('xw-tree-check-checked') : jQuery(items[i]).hasClass("xw-tree-item-selected"));
					condition && selectedItem.push(jQuery(items[i]).attr('_dsid'));
				}

				return selectedItem;
			},
			setSelection : function(idx, reveal) {
				!xwing.Util.is(idx, 'array') && (idx = [ idx || 0 ]);
				if (idx.length == 0) return;
				
				var items = this.tree.find("div.xw-tree-item").removeClass('xw-tree-item-selected');
				
				for ( var i = 0, l = idx.length, item, parents; i < l; i++) {
					item = items.eq(idx[i]);
					
					parents = item.parentsUntil(this.ul, 'ul');
					parents.css('display', 'block');
					
					parents.prev().children('img.xw-tree-plus').removeClass('xw-tree-plus').addClass('xw-tree-minus');
					this._doTreeline(0, this.ul);

					item.addClass("xw-tree-item-selected");
				}
				
				if (reveal) {
					var jTarget = items.eq(idx[0]),
						clientHeight = xwing.Util.parseInt(this.tree[0].clientHeight, 0),
						scrollTop = this.tree.scrollTop(),
						offsetTop = jTarget[0].offsetTop,
						height = offsetTop + jTarget.height();
					
					if (scrollTop > height) {
						this.tree.scrollTop(offsetTop);
					} else if ((scrollTop + clientHeight) < height) {
						this.tree.scrollTop(height - clientHeight);
					}
				}
			},
			binddatasetListener :function(event){				
				if(event.type=='binded' || event.type=='init' || event.type=='reset'){
					this._doValue();
				}
				if(event.fromObj == this) return;
				if(event.type=='update'){
					var nid = this.dataset.getValue(event.rowIdx,this._opt.idcolumn);
					var text = this.dataset.getValue(event.rowIdx,this._opt.textcolumn);
					var item = this.tree.find("div.xw-tree-item[_nid="+nid+"]");
					try{
						item.children("span").html(text);
					}catch(e){
						item.children("span").text(text);
					}
				}else if(event.type=='add'){					
					this._addRowByEvent(event.rowIdx);
				}else if(event.type=='remove'){
					this._removeRow(event.rowIdx);
				}
			},
			_removeRow : function(i) {
				this.tree.find("li.xw-tree-li").eq(i).remove();
			},
			_doCursor : function(){
				if(window.xwingIDE) return;			
				var item = jQuery('div.xw-tree-item',this.ul);
				var cursor = this.getCursor() == 'default' ? '' : this.getCursor();
				item.css('cursor',cursor);
			},
			setNodeiconcolumn : function(v){
				this._opt.nodeiconcolumn = v;
			},
			getNodeiconcolumn : function(){
				return this._opt.nodeiconcolumn;
			},
			setFoldericoncolumn : function(v){
				this._opt.foldericoncolumn = v;
			},
			getFoldericoncolumn : function(){
				return this._opt.foldericoncolumn;
			},
			setTreeline : function(v){
				this._opt.treeline = v;
				this._doTreeline(0,this.ul);
			},
			getTreeline : function(){
				return xwing.Util.parseBoolean(this._opt.treeline,false);
			},
			_doTreeline : function(depth, area) {
				if (this.getTreeline()) {
					var lis = jQuery('li[_depth=' + depth + ']', area);
					var lastIdx = lis.length - 1;
					var thisObj = this;
					lis.each(function(idx, el) {
						var div = jQuery(el.children[0]);
						if (div.attr('leafyn') == 'Y') {
							jQuery('img.xw-tree-leaf', this).css('margin-left', '');
							var indent = jQuery('img[_line=' + depth + ']:not(.xw-tree-leaf)', div).addClass('xw-tree-line');
							if (thisObj.getTreelineimage())
								indent.css('background-image', 'url(' + thisObj.getTreelineimage() + ')');

							indent.removeClass('xw-tree-indent-joinbottom xw-tree-indent-join');
							if (idx == lastIdx)
								indent.addClass('xw-tree-indent-joinbottom');
							else
								indent.addClass((div.attr('_dsid') == '0' ? 'xw-tree-indent-join-first' : 'xw-tree-indent-join'));
						} else {
							var handle = div.find('img.xw-tree-handle').addClass('xw-tree-line');
							if (idx == lastIdx) {
								div.attr("_last", "Y");
								handle.removeClass('xw-tree-line-plus-end xw-tree-line-minus-end');
								
								if (handle.hasClass('xw-tree-plus'))
									handle.addClass((div.attr('_dsid') == '0' ? 'xw-tree-line-plus-end-first': 'xw-tree-line-plus-end'));
								else
									handle.addClass((div.attr('_dsid') == '0' ? 'xw-tree-line-minus-end-first': 'xw-tree-line-minus-end'));
							} else {
								handle.removeClass('xw-tree-line-plus xw-tree-line-minus');
								
								if (handle.hasClass('xw-tree-plus'))
									handle.addClass((div.attr('_dsid') == '0' ? 'xw-tree-line-plus-first' : 'xw-tree-line-plus'));
								else
									handle.addClass((div.attr('_dsid') == '0' ? 'xw-tree-line-minus-first' : 'xw-tree-line-minus'));
								
								var indent = jQuery('img[_line=' + depth + ']', el);
								indent.addClass('xw-tree-line').addClass('xw-tree-line-indent');
								if (thisObj.getTreelineimage())
									indent.css('background-image', 'url(' + thisObj.getTreelineimage() + ')');
							}
							if (thisObj.getTreelineimage())
								handle.css('background-image', 'url(' + thisObj.getTreelineimage() + ')');
							thisObj._doTreeline(depth + 1, this);
						}
					});
				} else {
					if (this.getTreelineimage())
						jQuery('img.xw-tree-line', this.ul).css('background-image', '');
					
					this._doLeafmargin();
					
					jQuery('img.xw-tree-line',this.ul).
						removeClass('xw-tree-line').
						removeClass("xw-tree-line-plus").
						removeClass('xw-tree-line-minus').
						removeClass('xw-tree-line-indent').
						removeClass("xw-tree-line-plus-end").
						removeClass('xw-tree-line-minus-end');
				}
			},
			setTreelineimage : function(v) {
				this._opt.treelineimage = v;
				this._doTreeline(0, this.ul);
			},
			getTreelineimage : function() {
				return this._opt.treelineimage;
			},
			setRowheight : function(v) {
				this._opt.rowheight = v;
				this._doRowheight();
			},
			getRowheight : function() {
				return xwing.Util.parseInt(this._opt.rowheight, 0);
			},
			_doRowheight : function() {
				this.ul.find('div.xw-tree-item').css('height', this.getRowheight() + 'px').css('line-height', this.getRowheight() + 'px');
			},
			setLeafmargin : function(v) {
				this._opt.leafmargin = v;
				this._doLeafmargin();
			},
			getLeafmargin : function() {
				return xwing.Util.parseInt(this._opt.leafmargin, 0);
			},
			_doLeafmargin : function() {
				jQuery('img.xw-tree-leaf', this.ul).css('margin-left', (this.getLeafmargin() == 0 ? '' : this.getLeafmargin() + 'px'));
			},
			expandAll : function() {
				this.ul.find('ul').css('display', 'block');
				this.ul.find('img.xw-tree-plus').removeClass('xw-tree-plus').addClass('xw-tree-minus');
				this._doTreeline(0, this.ul);
			},
			collapseAll : function() {
				this.ul.find('ul').css('display', 'none');
				this.ul.find('img.xw-tree-minus').removeClass('xw-tree-minus').addClass('xw-tree-plus');
				this._doTreeline(0, this.ul);
			},
			setAutoupdate : function(v){
				this._opt.autoupdate = v;
			},
			getAutoupdate : function(){
				return xwing.Util.parseBoolean(this._opt.autoupdate,false);
			},
			setDataDraggable : function(v){
				this._opt.datadraggable = v;
				this._doDataDragDrop();
			},
			getDataDraggable : function(){
				return xwing.Util.parseBoolean(this._opt.datadraggable, false);
			},
			setDataDroppable : function(v){
				this._opt.datadroppable = v;
				
				this._doDataDragDrop();
			},
			getDataDroppable : function(){
				return xwing.Util.parseBoolean(this._opt.datadroppable,false);
			},
			_doDataDragDrop : function(){
				if( this.getDataDraggable() || this.getDataDroppable() ){
					if( !this._dragdrop ) this._dragdrop = new item(this);
					else
						this._dragdrop.setProperty();
					
					this._dragdrop.t_render = null;
				}else if( this._dragdrop ){
					delete this._dragdrop ;
				}
					
			},
			_getDragIndex : function(event){
				// Drag 시 해당 특정 INDEX를 가져오는 함수 
				var div =  $(event.srcElement).closest('div.xw-tree-item', this.getShell());
				var row = -1;
				if( div.length != 0 )  row  =  xwing.Util.parseInt(div.attr('_dsid'),-1);
				
				return [row, -1];
			},
			_mousemoveShell : function(event){
				// 현 widget의 영역에서 drag가 일어난 경우임.. 
				if( this.getDataDroppable() ){
					var helper = $('.xw-draggable-helper')
						, ds = Xwing.getDataset(this._opt.binddataset)
						, dragWG = this._dragdrop;
					
					if( helper && helper.length != 0 && ds != null ){
						var boundary = dragWG._createBoundary(this._getJShell());
						
						var div =  $(event.srcElement).closest('div.xw-tree-item', this.getShell());
						
						if( div[0]){
							if (this._dragdrop.t_render) {
								clearTimeout(this._dragdrop.t_render);
							}
							var index = event.clientY - this.getTop() - div[0].offsetTop;
							if( parseInt(this.getRowheight()/2)  > index  ){
								boundary.css("top",div[0].offsetTop+'px');
								
								item.dropIdx = [parseInt(div.attr('_dsid')),-1];
								item.dropIdx._up =  true;
							}else{
								boundary.css("top",div[0].offsetTop + this.getRowheight()+'px');
								item.dropIdx = [parseInt(div.attr('_dsid')),-1];
								item.dropIdx._up =  false;
								
								var that = this;
								item.test = event.srcElement;
								
								(function(v) {
									that._dragdrop.t_render = setTimeout(function(){
										if( item.test == event.srcElement){
											var aa =  $(event.srcElement).closest('div.xw-tree-item', that.getShell());
											if(aa.attr("leafyn") =='N' && aa.siblings('ul').css('display') == 'none'){
												// 이 안에서만 collapse 기능 추가하면 될 듯 
												that._handleClicked(event);
											}
										}
										
									},1000);
								})(event);
								
							}
							$('.xw-draggable-over').removeClass("xw-draggable-over");
							div.addClass('xw-draggable-over');
						}
						
						
					}
				}
			},
			_mouseupShell: function(event){
				if( item.draggable ){
					item.draggable._fire('dragend',{
						source : item.draggable,
						event : event,
						position : {
							'top' : event.clientY+15, 
							'left' : event.clientX+2
						},
						index : item.dragIdx.concat([])
					});
					
					if(this.getDataDroppable()){
						this._fire('dropping',{
							source : this,
							dragObj : item.draggable,
							event : event, 
							position : {
								'top' : event.clientY+15, 
								'left' : event.clientX+2
							},
							dragIndex : item.dragIdx && item.dragIdx.concat([]),
							dropIndex : item.dropIdx && item.dropIdx.concat([])
						});
						if( this.getAutoupdate() ){
							this._autoAupdate();
						}
					}
					delete item.draggable;
				}
			},
			_autoAupdate : function(){
				if( (item.draggable == this) && item.dragIdx && item.dropIdx && item.dragIdx[0] > -1 && item.dropIdx[0] > -1 && item.dragIdx[0] != item.dropIdx[0]){
					var pre_rowIdx = item.dragIdx[0]
						, rowIdx = item.dropIdx[0];
					
					var pre_row = this._getJShell().find('div[_dsid='+pre_rowIdx+']')
						, row = this._getJShell().find('div[_dsid='+rowIdx+']')
						, nid = row.attr('_nid'), pid 
						, pre_depth = parseInt(pre_row.attr('_depth'))
						, depth = parseInt(row.attr('_depth'));
					
					var removeIdx = []
						, pre_li = pre_row.parent('li').detach()
						, ds = Xwing.getDataset(this._opt.binddataset);
					//tobe 현재 ds는 다 신경 안 쓰고 ui만 신경쓰고 있음.... 
					
					// set removeIdx 
					pre_li.find('div.xw-tree-item').each(function(idx,el){
						removeIdx.push(parseInt(el.getAttribute('_dsid')));
					});
					removeIdx.sort(function(a,b){return a > b ? -1 : (a < b) ? 1 : 0;});
					
					var removeRows = []
						, that = this;
					jQuery.each(removeIdx,function(i,el){
						removeRows.splice(0,0,ds.removeRow(el,that));
					});
					
					rowIdx = ds.indexOfRow(this._opt.idcolumn,nid);
					if(item.dropIdx._up ){ // boundary가 현재 위치로 위에 존재 
						row.parent().before(pre_li);
						// 해당 row 바로 위에 위치 같은 li ( row - 1 , 부모 존재하면 부모 넣어주기 )
						pid = ds.lookUp(this._opt.idcolumn,nid,this._opt.pidcolumn);
						rowIdx = rowIdx -1;
					}else{
						if( row.attr('leafyn') == 'N' ){ // boundary가 현재 위치로 아래에 존재 
							row.siblings('ul').prepend(pre_li);
							// 해당 row 자식으로 삽입 , 한단계 아래 li ( row + 1, row 값을 부모로 넣기 )
							pid = nid;
						}else{
							row.parent().after(pre_li);
							// 해당 row 바로 아래에 위치 같은 li ( row + 1 , 부모 존재하면 부모 넣어주기 ) 
							pid = ds.lookUp(this._opt.idcolumn,nid,this._opt.pidcolumn);
						}
					}
					
					// setting ds
					removeRows[0][ds.getColumnIndex(this._opt.pidcolumn)] = pid;
					ds.insertRow(rowIdx, removeRows, that);
					
					// depth에 따른 base image plus , minus
					var base = Xwing.XWING_HOME + 'css/images/base.gif'
					, depthInfo = {
						add : (pre_depth > depth ? false : true),
						interval : (pre_depth > depth ? (pre_depth-depth) : (depth - pre_depth))
					};
				if( row.attr('leafyn') == 'N' ){
					if( !item.dropIdx._up ){
						var curdepth = depth + 1;
						if( curdepth != pre_depth){
							depthInfo = { add : true, interval : ( pre_depth ==  depth ? 1 :  depthInfo.interval + 1 )};
						}else{
							depthInfo = { add : false, interval : 0 };
						}
					}
				}
				for(var i=0, l = depthInfo.interval ; i  < l ; i++){
					pre_li.attr('_depth',( depthInfo.add ? pre_depth+1+i : pre_depth-1-i ) );
					pre_li.children('div').attr('_depth', ( depthInfo.add ? pre_depth+1+i : pre_depth-1-i ) );
					
					var indent = pre_li.children('div').find('img.xw-tree-indent:not(.xw-tree-leaf)');
					if(indent.length != 0 ){
						depthInfo.add ? indent.last().after(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='"+(parseInt(indent.last().attr('_line'))+1)+"' />")) : indent.last().remove();
					}else if ( depthInfo.add )
						pre_li.children('div').prepend(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='0' />"));
					
					pre_li.find('li.xw-tree-li').each(function(idx, el){
						var li = jQuery(el);
						var curdepth = parseInt(li.attr('_depth'));
						li.attr('_depth', ( depthInfo.add ? curdepth+1+i : curdepth-1-i )  );
						li.children('div').attr('_depth',( depthInfo.add ? curdepth+1+i : curdepth-1-i ) );
						
						var indent = li.children('div').find('img.xw-tree-indent:not(.xw-tree-leaf)');
						if(indent.length != 0 ){
							depthInfo.add ? indent.last().after(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='"+(parseInt(indent.last().attr('_line'))+1)+"' />")) : indent.last().remove();
						}else if ( depthInfo.add )
							li.children('div').prepend(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='0' />"));
						
					});
				}
					
//					var base = Xwing.XWING_HOME + 'css/images/base.gif'
//						, depthInfo = {
//							add : (pre_depth > depth ? false : true),
//							interval : (pre_depth > depth ? (pre_depth-depth) : (depth - pre_depth))
//						};
//					
//					if( !item.dropIdx._up && row.attr('leafyn') == 'N' ) depthInfo = { add : true, interval : ( pre_depth ==  depth ? 1 :  depthInfo.interval + 1 )};
//					if( pre_depth != depth || (!item.dropIdx._up && row.attr('leafyn') == 'N') ){
//						for(var i=0, l = depthInfo.interval ; i  < l ; i++){
//							pre_li.attr('_depth',( depthInfo.add ? pre_depth+1+i : pre_depth-1-i ) );
//							pre_li.children('div').attr('_depth', ( depthInfo.add ? pre_depth+1+i : pre_depth-1-i ) );
//							
//							var indent = pre_li.children('div').find('img.xw-tree-indent:not(.xw-tree-leaf)');
//							if(indent.length != 0 ){
//								depthInfo.add ? indent.last().after(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='"+(parseInt(indent.last().attr('_line'))+1)+"' />")) : indent.last().remove();
//							}else if ( depthInfo.add )
//								pre_li.children('div').prepend(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='0' />"));
//							
//							pre_li.find('li.xw-tree-li').each(function(idx, el){
//								var li = jQuery(el);
//								li.attr('_depth', ( depthInfo.add ? pre_depth+1+i : pre_depth-1-i )  );
//								li.children('div').attr('_depth',( depthInfo.add ? pre_depth+1+i : pre_depth-1-i ) );
//								
//								var indent = li.children('div').find('img.xw-tree-indent:not(.xw-tree-leaf)');
//								if(indent.length != 0 ){
//									depthInfo.add ? indent.last().after(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='"+(parseInt(indent.last().attr('_line'))+1)+"' />")) : indent.last().remove();
//								}else if ( depthInfo.add )
//									li.children('div').prepend(jQuery("<img src='"+base+"' onselectable='on' class='xw-tree-indent ' _line='0' />"));
//								
//							});
//						}
//					}
					
					this._resetDatasetIndex(); // 새로 index를 재정의한다. 
					var pretree = this.getTreeline();
					this.setTreeline( pretree ? false : true);
					this.setTreeline( pretree ? true : false);
				}
			},
			_resetDatasetIndex : function(){
				var ds = this.dataset
					, nid;
				for( var i=0, l= ds.size(); i < l ; i++){
					nid = ds.getValue(i,this._opt.idcolumn);
					this._getJShell().find('div[_nid='+nid+']').attr('_dsid',i);
				}
				delete ds, nid;
			},
			setChecking : function(idx, first){
				var ds = Xwing.getDataset(this._opt.binddataset); 
				if( this.getCheckable()){
					if( typeof idx == 'number' && ds && idx < ds.size()){
						var a = { target : this._getJShell().find('[_dsid='+idx+']').children(".xw-tree-check") };
				    	this._toggleCheck(a, first);
					}else if( typeof idx == 'boolean' && xwing.Util.parseBoolean(this._opt.multiselectable, false)){
						if(idx){
							this._getJShell().find('img.xw-tree-check').removeClass('xw-tree-check-unchecked');
							this._getJShell().find('img.xw-tree-check').addClass('xw-tree-check-checked');
						}else{
							this._getJShell().find('img.xw-tree-check').removeClass('xw-tree-check-checked');
							this._getJShell().find('img.xw-tree-check').addClass('xw-tree-check-unchecked');
						}
					}
				}
			},
			setExpr : function(v){
				this._opt.expr = v;
			},
			getExpr : function(){
				return this._opt.expr;
			},
			_makeStyle : function(depth, i){
				var style = "";
				if( this.getExpr()){
					try {
						var v = eval(this.getExpr());
						var obj = xwing.Util.is(v, 'function') ? v.call(null, depth, i) : v;
						if (obj !== undefined && xwing.Util.is(obj, 'object')) {
							var fontfamily = obj.fontfamily;
							if( obj.fontfamily && Xwing.config.fonts && Xwing.config.fonts[obj.fontfamily] ){
								fontfamily = Xwing.config.fonts[obj.fontfamily];
							}else if ( this.getFontfamily() && Xwing.config.fonts && Xwing.config.fonts[this.getFontfamily()]){
								fontfamily = Xwing.config.fonts[this.getFontfamily()];
							}
							
							fontfamily && (style += 'font-family:'+fontfamily+';');
							obj.fontcolor ? (style += 'color:'+obj.fontcolor +';') : (style += 'color:'+this.getFontcolor() +';');
							obj.fontstyle ? (style += 'font-style:'+obj.fontstyle +';') : (style += 'font-style:'+ this.getFontstyle() +';');
							obj.fontweight ? (style += 'font-weight:'+obj.fontweight+';') : (style += 'font-weight:'+this.getFontweight()+';');
							obj.fontsize ? (style += 'font-size:'+obj.fontsize+'px;') : (style += 'font-size:'+this.getFontsize()+'px;');
							obj.fontdecoration ? (style += 'text-decoration:'+obj.fontdecoration+';') : (style += 'text-decoration:'+this.getFontdecoration()+';');
						}
					}catch (e) {
						Xwing.debug(e);
					}
				}
				return style;
			}
		}	
	}
});

