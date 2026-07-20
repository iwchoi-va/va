Class.define({
	File : {
		alias : 'file',
		namespace : 'xwing.widget',
		extend : xwing.widget.DataBindable,
		File : function(json) {
			this._init(json);
		},
		statics : {
			create : function(json){
				return new xwing.widget.File(json);
			}
		},
		prototypes : {
			_createPart : function() {
				this._Finfo = [];
				this._nextKey = 0;
				
				this.jBtn = jQuery("<button class='xw-file-upload-button'>upload</button>").addClass('xw-mod-font xw-mod-background xw-mod-border');
				this.form = jQuery("<form class='xw-file-form' target='" + ('i_' + this.getId()) + "' method='POST' enctype='multipart/form-data' />");
				var form_input = jQuery("<input tabindex='-1' class='xw-file-upload-file' type='file'  name='files' _key='" + this.getId() + "_" + this._nextKey + "' />");
				this.form.append(this.jBtn).append(form_input);
				this._getJShell().append(this.form);

				var thisObj = this;
				form_input.bind('change', function(event) {
					thisObj._change(event);
				});
				
				/*debug Xwing.debug("Widget(File) created : "+ this.getId() +", " + xwing.util.Util.obj2json(this._opt)); */
			},
			_render : function() {
				xwing.widget.DataBindable.prototype._render.call(this);
				this._doAction();
			},
			_change : function(event) {
				this.form.find('.xw-file-upload-file').css('display', 'none');

				var fileFirstNode = this.form.find('.xw-file-upload-file:first');
				this.form.append(fileFirstNode);

				this._addFileInfo(fileFirstNode[0]);
				
				var input = jQuery("<input tabindex='-1' class='xw-file-upload-file' type='file'  name='files' _key='" + this.getId() + "_" + this._nextKey + "' />");
				var thisObj = this;				
				input.bind('change', function(event) {
					thisObj._change();
				});
				this.jBtn.after(input);

				this._fire('select', {
					type : 'select',
					source : this,
					event : event
				});
			},
			_addFileInfo : function(file) {
				var dsObj = Xwing.getDataset(this._opt.binddataset);
				var fInfo = [];
				
				if (jQuery.browser.msie) {
					var arr = file.value.split(/\\/g);
					fInfo.push(arr[arr.length - 1], '', this.getId() + "_" + this._nextKey);
				} else if (jQuery.browser.mozilla) {
					fInfo.push(file.files[0].name, file.files[0].size, this.getId() + "_" + this._nextKey);
				} else if (jQuery.browser.webkit) {
					fInfo.push(file.files[0].name, file.files[0].size, this.getId() + "_" + this._nextKey);
				}

				/*debug Xwing.debug(fInfo); */
				
				this._nextKey++;
				this._Finfo.push(fInfo);
				
				if (dsObj) {
					var row = new Array(dsObj.getColumnInfo().length);
					if (dsObj.hasColumn(this._opt.filenamecolumn))
						row[dsObj.getColumnIndex(this._opt.filenamecolumn)] = fInfo[0];
					if (dsObj.hasColumn(this._opt.filesizecolumn))
						row[dsObj.getColumnIndex(this._opt.filesizecolumn)] = fInfo[1];
					
					dsObj.addRow(row);
				}
				
				dsObj = null;
				fInfo = null;
			},
			_doValue : function() {
				if (this._opt.value != undefined)
					this.jBtn.text(this._opt.value);
				else
					this.jBtn.text("");
			},
			send : function(param, callback) {
				var opt = param || {};
				this.callback = this.callback || callback || jQuery.noop;
				
				for ( var key in opt) {
					var input = jQuery("<input type='hidden' name='" + key + "' value='" + opt[key] + "' class='xw-file-param'/>");
					this.form.append(input);
				}
				
				this._tmpInput = this.form.children('input:file:first').detach();
				this._doUpload();
			},
			_doUpload : function() {
				xwing.widget.Widget.showIndicator();
				
				if (!this.iframe) {
					this.iframe = jQuery("<iframe id='" + ('i_' + this.getId()) + "' name='" + ('i_' + this.getId()) + "' />");
					this._getJShell().append(this.iframe);
					
					var thisObj = this;
					var argu = arguments;
					this.iframe.bind('load', function(event) {
						if(argu.length == 0 )
							thisObj._uploadResponse(event);
						else
							thisObj._uploadResponse(event, argu[0], argu[1], argu[2]);
					});
				}
				
				this.form.submit();
			},
			_uploadResponse : function(event) {
				xwing.widget.Widget.hideIndicator();

				var ifr = this.iframe;
				var res = this.iframe.contents().find('body').text();

				this.form.children('input.xw-file-param').remove();
				this._tmpInput && this.jBtn.after(this._tmpInput);
				this._tmpInput = null;
				this.iframe = null;

				if( arguments.length > 1 ) this._importDataset(arguments[2], res, arguments[3]);
				try {
					this.callback && this.callback(res);
				} catch (e) {
					Xwing.error("Error while executing callback function : " + e);
				}

				setTimeout(function() {
					ifr.remove();
				}, 3000);
			},
			setAction : function(action) {
				this._opt.action = action;
				this._doAction();
			},
			getAction : function() {
				return this._opt.action;
			},
			_doAction : function() {
				this.form.attr('action', this._opt.action);
			},
			size : function() {
				return this._Finfo.length;
			},
			removeFile : function(i) {
				if (arguments.length == 0) {
					this.form.children('input:file:not(:first)').unbind('change').remove();
					var dsObj = Xwing.getDataset(this._opt.binddataset);
					dsObj && dsObj.clearData();
					this._Finfo = [];
				} else {
					this.form.children('input[_key=' + this._Finfo[i][2] + ']').unbind('change').remove();
					this._Finfo.splice(i, 1);
					var dsObj = Xwing.getDataset(this._opt.binddataset);
					dsObj && dsObj.removeRow(i);
				}
			},
			addFile : function() {
				this.form.find('input:file:first').trigger('click');
			},
			binddatasetListener : function(dsEvent) {

			},
			importData : function(ds, useCol, sheetIdx){
				// importData를 위한 함수. 
				if( typeof ds == 'string'){
					ds = Xwing.getDataset(ds);
				}
				
				var colInfo = ds.getColumnInfo().join(',');
				useCol ? (colInfo += ":true") : ((colInfo += ":false"));
				
				var input = jQuery("<input type='hidden' name='config' value='" + colInfo + "' class='xw-file-param'/>");
				this.form.children('input:file:first').after(input);
				
				this._tmpInput = this.form.children('input:file:first').detach();
				this._doUpload(true, ds, (useCol ? useCol : false));
			},
			_importDataset : function(ds, res, useCol){
				try{
					var info = eval('('+res+')');
					if(useCol) ds.setData(info.rows);
					else ds.setData(info.cols, info.rows);
				}catch(e){
					/*debug Xwing.debug("Data Parse Error"); */
				}
				
			}
		}	
	}
});
