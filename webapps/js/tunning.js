
var rec_key_tunning,
	contact_id_tunning,
	start_time_tunning,
	user_id_tunning,
	gubun_tunning,
	s_CNSL_CD_tunning
	;
var dialog_tunning = null;

function tunning_popup(param){
	
	this.rec_key_tunning = param.rec_key_tunning;
	this.contact_id_tunning = param.contact_id_tunning;
	this.start_time_tunning = param.start_time_tunning;
	this.user_id_tunning = param.user_id_tunning;
	this.gubun_tunning = param.gubun_tunning;
	this.s_CNSL_CD_tunning = param.s_CNSL_CD_tunning;
	
	var opt = {
				modal: false,
				resizable: false,
				draggable:true,
				id: 'RSK080P1',
				url: '../RSK/RSK080P1.xhtml',
				width: 806, //408
				height: 454, //790
				title : '정정요청',
				param : {   rec_key: rec_key_tunning
							, contact_id: contact_id_tunning
							, start_time: start_time_tunning
							, user_id: user_id_tunning
						    , gubun: gubun_tunning
						    , s_CNSL_CD: s_CNSL_CD_tunning
						  }
				, close : tunning_unLoad
	};

	eval("dialog_tunning = new top.xwing.Dialog(opt)");
	eval("dialog_tunning.open()");
}

function tunning_unLoad(){
	
	var v_body = eval("top.window.document.body");
	v_body.removeChild(eval("top.window.document.getElementById('RSK080P1')"));
	dialog_tunning = null;
}
