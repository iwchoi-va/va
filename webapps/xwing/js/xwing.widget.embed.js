Class.define({
	Embed : {
		alias : 'embed',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		Embed : function(json){
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.Embed(json);
			}
		},		
		prototypes : {
			_createPart : function(){
				this.jCont = jQuery("<div class='xw-embed xw-mod-background xw-mod-border xw-mod-focus'/>");
				this._getJShell().append(this.jCont);
				
				if(window.xwingIDE) {
					this.jCont.text("Embed").css({
						'text-align' : 'center',
						'vertical-align' : 'center',
						'line-height' : this.getHeight()+'px',
						'background-color' : '#fff'
					});
					return;
				}
				
				if(Xwing.isIE()){
					this.object = this._createObject();
					this.jCont.html(this.object);
				}else{
					this.object = jQuery("<embed class='xw-embed-object xw-mod' id='"+('x_'+this.getId())+"' />");
					if(this.getSrc() != null) this.object.attr("src",this.getSrc());
					if(this.getType() != null) this.object.attr("type",this.getType());
					this._setParam();
					this.jCont.append(this.object);
				}
			},
			_doBounds : function(){			
				xwing.widget.Widget.prototype._doBounds.call(this);
				if(window.xwingIDE) this.jCont.css('line-height',this.getHeight()+'px');
			},
			_doClassid : function(){
				if(Xwing.isIE()){
					this.object.attr('classid',this.getClassid());
				}
			},
			_doCodebase : function(){
				if(Xwing.isIE()){
					this.object.attr('codebase',this.getCodebase());
				}
			},
			setClassid : function(value){
				this._opt.classid = value;
				this._doClassid();
			},
			getClassid : function(){
				return this._opt.classid;
			},
			setCodebase : function(value){
				this._opt.codebase = value;
				this._doCodebase();
			},
			getCodebase : function(){
				return this._opt.codebase;
			},
			setSrc : function(v){
				this._opt.src = v;
			},
			getSrc : function(){
				return this._opt.src;
			},
			setType : function(v){
				this._opt.type = v;
			},
			getType : function(){
				return this._opt.type;
			},
			getActiveXObject : function(){
				var actX = document.getElementById('x_'+this.getId());
				return actX;
			},
			_createObject : function(){				
				var objStr ='<object';
				var embStr = "<embed class='xw-embed' ";
				objStr += (" class='xw-object-object xw-mod' id='"+('x_'+this.getId())+"'");
				objStr += (" style='width:"+this.getWidth()+"px;height:"+this.getHeight()+"px;' ");

				if(this.getClassid() != null ) objStr += (" classid='"+this.getClassid()+"'");
				if(this.getCodebase() != null) objStr += (" codebase='"+this.getCodebase()+"'");
				if(this.getSrc() != null) embStr += (" src='"+this.getSrc()+"'");
				if(this.getType() != null) embStr += (" type='"+this.getType()+"'");
				var ds = Xwing.getDataset(this.getBinddataset());
				
				if(ds){
					if(ds.hasColumn('name') && ds.hasColumn('value')){
						objStr +='>';
						for(var i=0; i < ds.size() ; i++){
							var name = ds.getValue(i,'name');
							var value = ds.getValue(i,'value');
							
							var param = "<param name='"+name+"' value='"+value+"' ></param>";
							objStr += param;
							embStr += " "+name+"='"+value+"'";
						}
						objStr += (embStr+" />");
					}
				}else{
					objStr += '>';
				}
				objStr += '</object>';
				
				return objStr;				
			},
			_setParam : function(){
				var ds = Xwing.getDataset(this.getBinddataset());
				if(ds){
					if(ds.hasColumn('name') || ds.hasColumn('value')){
						for(var i=0; i < ds.size() ; i++){
							var name = ds.getValue(i,'name');
							var value = ds.getValue(i,'value');
							this.object.attr(name,value);
						}
					}					
				}
			}				
		}
	}
});
