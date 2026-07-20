xwing.menu = {};
xwing.menu.ContextMenu = function(menu, target, option) {
	// 초기화 
	this._initOption(menu, target, option);
	
	// binding
	this._connectDataset(menu);
	var items = this._parsingDataset(menu);
	this._opt.items = this._opt.items ? jQuery.extend({},this._opt.items,items) : items;
	this._createPart();
};

xwing.menu.ContextMenu.prototype = {
		_initOption : function(menu, target, option){
			this._opt = {};
			if(typeof menu == 'string'){
				this._opt.parent = target;
			}else{
				this._opt = menu;
				this._opt.parent = target;
			}
			if(option && option.beforeShow)
				this._opt.events = {
					show : function(){
						option.beforeShow.call(this);
					}
				};
		},
		_connectDataset : function(p_ds){
			var ds = Xwing.getDataset(p_ds);
			if(ds){
				this._opt.binddataset = p_ds;
				ds.bind('ALL',this._notify,this);
			}
		},
		_notify : function(dsEvent){
			if (dsEvent && (this._opt.binddataset == dsEvent.id)) 
				this.binddatasetListener.call(this,dsEvent);
		},
		_parsingDataset : function(p_ds){
			var items = []
				, callback = ''
				, ds;
			if(typeof p_ds == 'string')
				ds = Xwing.getDataset(p_ds);
			
			if(ds){
				for(var i=0, l=ds.size(), item=''; i < l; i++){
					var sepr = ds.getValue(i,'separator')
						, label = ds.getValue(i,'label')
						, icon = ds.getValue(i,'icon')
						, onclick = ds.getValue(i,'onclick')
						, disabled = ds.getValue(i,'disabled');
					if(sepr || sepr == 'true')
						item = i +': "-----------------"';
					else{
						item = i+': {'
						+ 'name : "'+ label+'"'
						+ (disabled == 'true' ? ', disabled : true' : '')
						+ '}';
						if(onclick || onclick == 'true')
							callback += 'if( key == '+i+') '+onclick+' && ' + onclick+'();';
					}
						
					items.push(item);
				}
			}
			if( jQuery.browser.msie && parseFloat(jQuery.browser.version) <= 8){
				var a;
				eval('(a = function(key, optoins) {'+callback+' } )');
				this._opt.callback = a;
			}else{
				this._opt.callback = eval('(function(key, optoins) {'+callback+'})');
			}
			
			return eval("({"+items.join(',')+"})");
		},
		_createPart : function(){
			var parent = Xwing.getWidget(this._opt.parent);
			if(!this._opt.parent || !parent) return;
			var selector = parent.getId()
				, items = this._opt.items
				, callback = this._opt.callback;
			
			$.contextMenu({
				selector : '#'+selector,
				callback : callback,
				items : items,
				events : this._opt.events
			},this);
		}, 
		binddatasetListener : function(dsEvent){
			if(dsEvent.type == 'update' && dsEvent.column == 'disabled'){
				var ds = Xwing.getDataset(dsEvent.id)
					, row = dsEvent.rowIdx
				 	, newVal = ds.getValue(row, 'disabled');
				if( newVal == true || newVal == 'true'){
					this._opt.items[row].disabled = true;
					this._opt._context.items[row].disabled = true;
				}else if((newVal == false || newVal == 'false') && this._opt.items[row].disabled){
					delete this._opt.items[row].disabled;
					delete this._opt._context.items[row].disabled;
					this._opt._context.$menu.children(':eq('+row+')').removeClass('disabled');
				}
			}else if(dsEvent.type == 'reset'){
				var items = this._parsingDataset(dsEvent.id);
				if(items){
					this._opt.items = items;
					var parent = Xwing.getWidget(this._opt.parent);
					$.contextMenu('destroy','#'+parent.getId());
					this._createPart();
				}
			}
		},
		setItems : function(v){
			this._opt.items = v;
			var parent = Xwing.getWidget(this._opt.parent);
			$.contextMenu('destroy','#'+parent.getId());
			this._createPart();
		},
		setDisabled : function(key,value){
			if(value == true || value == 'true'){
				this._opt.iemts[key].disabled = true;
				this._opt._context.items[key].disabled = true;
			}else{
				delete this._opt.items[key].disabled;
				delete this._opt._context.items[key].disabled;
			}
		}
};
xwing.Xwing.createContextMenu = function(menu, target, option){
	return new xwing.menu.ContextMenu(menu, target, option);
};