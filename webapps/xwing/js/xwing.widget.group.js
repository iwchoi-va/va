Class.define({
	Group : {
		alias : 'group',
		namespace : 'xwing.widget',
		extend : xwing.widget.Panel,
		Group : function(json){
			if(!arguments.length){
				 return;
			}else{			
				this._init(json);
			}
		},
		statics : {
			create : function(json){
				return new xwing.widget.Group(json);
			}
		},		
		prototypes : {
			_createPart :function(){
				this.jGroup = jQuery('<div class="xw-group"/>');
				this.area_title = jQuery('<div class="xw-group-area-title"/>');
				this.title = jQuery('<span class="xw-group-title xw-mod-font"></span>');
				this.area_body = jQuery('<div class="xw-group-area-body"/>');
				
				var container = jQuery('<div class="xw-container"/>');
				var groupContainer = jQuery('<div class="xw-group-container xw-mod-border xw-mod-background"/>').append(container);
				this.container = container[0];

				this.jGroup.append(this.area_title);
				this.jGroup.append(this.area_body);
				this.area_title.append(this.title);
				this.area_body.append(groupContainer);
				this._getJShell().append(this.jGroup);
				/*debug Xwing.debug("Widget(Group) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */				
			},
			_render : function(){
				xwing.widget.Panel.prototype._render.call(this);
				this._doTitle();
				this._doTitlealign();
				this._doTextpadding();
			},
			setTitle : function(title){
				this._opt.title = title;
				this._doTitle();
			},
			getTitle : function(){
				return this._opt.title;
			},
			_doTitle : function(){
				if(this.getTitle() == null || this.getTitle() == ""){
					this.area_title.css('display','none');
				}else{
					this.area_title.css('display','block');
					this.title.text(this.getTitle());
				}
				this._doSizing();
			},
			setTitlealign : function(v){
				this._opt.titlealign = v;
				this._doTitlealign();
			},
			getTitlealign : function(){
				return this._opt.titlealign;
			},
			_doTitlealign : function(){
				this.title.css('float',this.getTitlealign());
				if(this.getTitlealign() == 'right')	this.title.css('left','-20px');
				else this.title.css('left','20px');
			},
			_doBounds : function(){
				xwing.widget.Panel.prototype._doBounds.call(this);
				this._doSizing();
			},
			_doSizing : function(){
				if(this.getTitle()){
					this.area_body.css({"padding-top": "0.5em", "top": "-0.5em"});
				}else{
					this.area_body.css({"padding-top": "0", "top": "0"});
				}
			},
			_doBgcolor : function(){
				xwing.widget.Widget.prototype._doBgcolor.call(this);
				this.title.css('background-color',this.getTitlebgcolor());
			},
			setTitlebgcolor : function(v){
				this._opt.titlebgcolor = v;
				this._doBgcolor();
			},
			getTitlebgcolor : function(){
				return this._opt.titlebgcolor;
			},
			_doFontsize : function(){
				try {
					if (this.getFontsize()) {
						this.area_title.css("font-size", this.getFontsize() + 'px');
						this.title.css("font-size", this.getFontsize() + 'px');
						this.area_body.css("font-size", this.getFontsize() + 'px');
					}
				} catch (e) {
					Xwing.error("err on " + this.getAlias() + ".doFontsize:" + e);
				}
			},
			_doEnabled : function() {
				xwing.widget.Widget.prototype._doEnabled.call(this);
				this._getJShell().children(".xw-disabled").css('z-index', 1);
			}
		} 
	}
});