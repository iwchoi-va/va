xwing.menu = {
	option : {
		theme : 'vista'
	}
};

xwing.menu.ContextMenu = function(menu, target, option) {
	function getMenuByDataset() {
		var ds = Xwing.getDataset(menu),
			separator = "",
			label = "",
			onclick = "",
			icon = "",
			str = "[";
		
		for(var i = 0 ; i < ds.size() ; i++) {
			separator = ds.getValue(i, "separator");

			if (separator && separator.toLowerCase() == "true") {
				str += "xwing.menu.ContextMenu.SEPERATOR";
			} else {
				label = ds.getValue(i, "label");
				onclick = ds.getValue(i, "onclick");
				icon = ds.getValue(i, "icon");
				disabled = ds.getValue(i, "disabled");

				str += "{'"+ label + "':{";				
				onclick && (onclick = "function(){try{" + onclick + "();}catch(e){Xwing.error(e);}}") ;
				onclick && (str += "onclick:" + onclick) && (icon || disabled) && (str += ",");
				icon && (str += "icon:'" + icon +"'") && disabled && (str += ",");			
				disabled && (str += "disabled:" + disabled.toLowerCase());
				str += "}}";
			}
			
			if(i < (ds.size() - 1)) {
				str += ",";
			}
		}
		
		return eval(str += "]");
	}
	
	if (menu) {
		if (typeof menu == 'string' && Xwing.hasDataset(menu)) {
			this.menu = getMenuByDataset;
		} else {
			this.menu = menu;
		}
		this.option = jQuery.extend({}, xwing.menu.option, option);
	} else {
		return;
	}
	
	if (target) {
		this.appendTo(target);
	}
}

xwing.menu.ContextMenu.prototype.appendTo = function(target) {
	if (typeof target == 'string') {
		this.node = jQuery('#' + target)[0];
	} else if (target instanceof Obj && !(target.nodeName || target.tagName)) {
		this.node = target;
	} else {
		return;
	}
	
	jQuery(this.node).contextMenu(this.menu, this.option);
}

xwing.menu.ContextMenu.SEPERATOR = jQuery.contextMenu.separator;

xwing.Xwing.createContextMenu = function(menu, target, option) {
	return new xwing.menu.ContextMenu(menu, target, option);
}