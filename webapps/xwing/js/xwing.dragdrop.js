var item =  function(widget){
	this._wobj = widget;
	this._in = false;
	this._out = false;
	
	var that = this;
	setTimeout(function(){
		if( widget != null ) that._init();
	},0);
	
};

// 공통적인 변수 
item.draggable;
item.droppable;
item.dragIdx;
item.dropIdx;
item.test;
item.dropin;
item.prototype = {
	_init : function(){
		var wg = this._wobj;
		this.setProperty();
		
		var body = (wg.getAlias() == 'datagrid') ? wg.body[0] : wg.getShell();
		
		body.onmousemove = function(event){
			if(wg.getDataDraggable() || wg.getDataDroppable()){
				wg._mousemoveShell.call(wg, event);
			}
		};
		body.onmouseup = function(event){
			if(wg.getDataDroppable())
				wg._mouseupShell.call(wg, event);
		};
	},
	setProperty : function(){
		var wg = this._wobj;
		
		if(wg.getDataDraggable()){
			this._createDraggable();
		}else{
			wg._getJShell().draggable('destroy');
		}
		if(wg.getDataDroppable()){
			this._createDroppable();
		}else{
			wg._getJShell().droppable('destroy');
		}
	},
	_createDraggable : function(){
		var wg = this._wobj
			, that = this;
		wg._getJShell().draggable({
			helper : function(){return document.createElement('div')}, // jQuery('<span>dfsddf</span>')
			start : function(event, ui){
				that._mousedown.call(that,event);
			},
			drag : function(event, ui){
				ui.position.left = event.clientX; //event.clientX + 2 ;
				ui.position.top = event.clientY; //event.clientY + 15;
				that._mousemove.call(that,event);
				
			},
			stop : function(event, ui){
				item.dropable = that._wobj;
				var helper = $('.xw-draggable-helper');
				var boundary = $('.xw-droppable-which');
				if( helper && helper.length != 0 ){
					helper.empty().remove();
				}
				
				$('.xw-draggable-over').removeClass("xw-draggable-over");
				if( boundary && boundary.length != 0 ){
					boundary.empty().remove();
				}
			}
		});
	},
	_createDroppable : function(){
		var wg = this._wobj
		, that = this;
		wg._getJShell().droppable({
			tolerance : 'pointer',
			drop : function(event, ui){
				// drag -> itemdrop
				var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
				if( widget && widget.getDraggable()){
					wg._fire('dropping',{
						dragicon : ui.helper,
						source : that,
						dragObj : widget,
						event : event, 
						position : ui.position
					});
				}
			},
			over : function(event, ui){
				var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
				if(item.draggable){
					widget = item.draggable;
				}
				wg._fire('dropin',{
					dragicon : ui.helper,
					source : wg,
					dragObj : widget,
					event : event, 
					position : ui.position
				});
			},
			out : function(event, ui){
				var widget = Xwing.getWidget(ui.draggable.attr('id') || ui.draggable[0].offsetParent.getAttribute('id'));
				if(item.draggable){
					widget = item.draggable;
				}
				wg._fire('dropout',{
					dragicon : ui.helper,
					source : wg,
					dragObj : widget,
					event : event, 
					position : ui.position
				});
			}
		});
	},
	_createHelper : function(){
		var wg = this._wobj;
		var helper = $('.xw-draggable-helper');
		if(helper.length != 0 ) return;
		if( !wg.getDragIcon() ){
			helper = $('<span class="xw-draggable-helper" >Select Row</span>');
			$('body').append(helper);
		}else{
			
			var  html = wg.getDragIcon();
			if( typeof  html == "string"){
				html = jQuery(html);
			}else if(html instanceof HTMLElement){
				html = jQuery(html);
			}
			
			html.addClass('xw-draggable-helper');
			$('body').append(html);
		}
		
		
	},
	_createBoundary : function(jshell){
		var boundary = jshell.find('.xw-droppable-which');
		if(boundary.length == 0 ){
			$('.xw-droppable-which').empty().remove();
			boundary =  $('<span class="xw-droppable-which" style="width:200px;" ></span>');
			// boundary width 
			boundary.css('width',jshell.width()+'px');
			jshell.append(boundary);
		}
		return boundary;
	},
	_mousedown : function(event){
		var wg = this._wobj, 
			ds = Xwing.getDataset(wg._opt.binddataset);
		
		if ( wg.getDataDraggable() && ds && ds.size() != 0 ) {
			
			item.draggable = wg;
			item.dragIdx = wg._getDragIndex(event); // [row, col] 
			this._createHelper();
			
			var helper = $('.xw-draggable-helper');
			helper.css({
				'top' : event.clientY,//(event.clientY+15)+'px',
				'left' : event.clientX//(event.clientX+2)+'px'
			});
			wg._fire('dragstart',{
				source : wg,
				event : event,
				position : {
					'top' : event.clientY,//(event.clientY+15)+'px',
					'left' : event.clientX//(event.clientX+2)+'px'
				},
				index : item.dragIdx.concat([])
			});
			
			delete helper;
		} 
		delete wg, ds;
	},
	_mousemove : function(event){
		var helper = $('.xw-draggable-helper');
		if( helper && helper.length != 0 ){
			// helper 있으면....... draggable true
			helper.css({
				'top' : event.clientY,//(event.clientY+15)+'px',
				'left' : event.clientX//(event.clientX+2)+'px'
			});
			var wg = this._wobj;
			if( wg.getDataDraggable() ){
				wg._fire("dragging",{
					source : wg,
					event : event, 
					position :{
						'top' : event.clientY,//(event.clientY+15)+'px',
						'left' : event.clientX//(event.clientX+2)+'px'
					},
					index : item.dragIdx && item.dragIdx.concat([])
				});
			}
		}
	}
};
