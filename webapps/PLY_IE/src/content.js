// create Element and load text

var stt = {
		
		text_element: null,
		text_element_tx: null,
		text_element_rx: null,
	    audio_element: null,
	    autofocus_current_word: true,
	    	
	    words: [],
	    words_tx: [],
	    words_rx: [],
	    
	    borderStyle: 'solid #aceceb',
	    fontSize: '18px',
	    fontWeight: 'bolder',
	    defColor: 'black', //#2c3d4f
	    selColor: '#D14A0E',
	    txColor: 'blue', //'#22aaad', //'#2c3d4f',
	    rxColor: 'black', //상담사'#dd0000',

		loadText: function(host, rec_key, start_time){

			var that = this;
			var xhr = new XMLHttpRequest();
			 //xhr.open("GET" , host + "player.getSTT_srt.do?contact_id="+contact_id+"&start_time="+start_time , true);
			xhr.open("GET" , host + "player.getSTT_srt.do?rec_key="+rec_key+"&start_time="+start_time , true);
			//xhr.open("GET" , host + "player.getSTT_srt.do?rec_key="+"300520160801153617"+"&start_time="+start_time , true);
			xhr.onreadystatechange = function() {
				   
		        if(xhr.readyState == 4) {
		        	if(xhr.status == 200) {
		        		var sttResult = JSON.parse(xhr.responseText);
			        	var str = JSON.stringify(sttResult);                       
			        	var new_sttResult = JSON.parse(str);   
			        	
			    //    	meta.rec_key = sttResult.REC_KEY; --20180327 지금현재가져오는 값없음.playerlog때문에 일단 주석처리.
			        	meta.file_len = sttResult.DURATION;
			        	meta.regist_no = sttResult.REGIST_NO;
			        	meta.agent_id = sttResult.AGENT_ID;
			        	meta.cust_name = sttResult.CUST_NAME;
			        	meta.incall_no = sttResult.INCALL_NO;
			        	meta.contact_id = sttResult.CONTACT_ID;
			        	
			        	rtx = sttResult.RTX;
						if(sttResult.ResultMessage=="fail"){
							//data = JSON.parse('[{"SYNC":"0","DUR":"0","CONTENT":"STT is processing."}]');
							data = JSON.parse('[{"SYNC":"0","DUR":"0","CONTENT":""}]');
			        	}else{
			        		data = JSON.parse(sttResult.DATA);
			        	}	
				        
			        	//STT text area
			        	var textarea = document.createElement('div');
			        	textarea.setAttribute("id", "passage-text");
			        	textarea.style.overflowY = 'scroll'; //해상도 안맞으면 스크롤 안보이는거 조정
			        	var textarea_tx = null, textarea_rx = null;
			        	if(rtx=="Y"){
			        		textarea_tx = document.createElement('div');
			        		textarea_tx.setAttribute("id", "passage-text-tx");
			        		textarea_rx = document.createElement('div');
			        		textarea_rx.setAttribute("id", "passage-text-rx");
			        	}
			        	var str="",str2="",str3="";
			        	
			        	//player.getSTT_srt.do
			        	for (var i=0; i<data.length; i++) {
			        		str += "<span id='"+i+"' ";
			        		str += "onClick=sttJump('"+ data[i].SYNC + "') ";
			        		str += "data-gb='"+ data[i].GB + "' ";
			        		str += "data-start='"+ data[i].SYNC + "' ";
			        		if(rtx=="Y" && data[i].GB=="TX") str += "style=color:"+that.txColor+";";
			        		else if(rtx=="Y" && data[i].GB=="RX") str += "style=color:"+that.rxColor+";";
			        		if(i<data.length-1) str += "data-dur='"+ data[i].DUR + "'>";
			        	    else str += "data-dur='"+ (meta.file_len-data[i].SYNC) + "'>";
			        	    str += data[i].CONTENT + "</span><br>";
			        	    
			        	    var span = document.createElement('span');
			        	    span.innerHTML = str;
			        	    textarea.appendChild(span);
			        	    
			        	    if(rtx=="Y"){ // 화자 분리
			        	        var span_cp = document.createElement('span');
			        	        str2 += "<span id='"+i+"' ";
			        	        str2 += "onClick=sttJump('"+ data[i].SYNC + "') ";
			        	        str2 += "data-gb='"+ data[i].GB + "' ";
			        	        str2 += "data-start='"+ data[i].SYNC + "' ";
				        	    if(i<data.length-1) str2 += "data-dur='"+ data[i].DUR + "'>";
				        	    else str2 += "data-dur='"+ (meta.file_len-data[i].SYNC) + "'>";
				        	    str2 += data[i].CONTENT + "</span><br>";
			        	        span_cp.innerHTML = str2;
			        	        
			        	        var span_empty = document.createElement('span');
			        	        str3 += "<span id='"+i+"_emp' ";
			        	        str3 += "data-gb='"+ data[i].GB + "' ";
			        	        str3 += "data-start='"+ data[i].SYNC + "'></span><br> ";
				        	    span_empty.innerHTML = str3;
				        	    
				        	    
			        	    	if(data[i].GB=="TX"){ 
					        	    textarea_tx.appendChild(span_cp);
//					        	    var br = document.createElement("br"); //대화형식으로 공백만드는 소스
//					        	    textarea_rx.appendChild(br);
					        	    //textarea_rx.appendChild(span_empty);
					        	    
				        		} else if(data[i].GB=="RX"){
//				        			var br = document.createElement("br"); //대화형식으로 공백만드는 소스
//					        	    textarea_tx.appendChild(br);
				        			
				        			//textarea_tx.appendChild(span_empty);
					        	    textarea_rx.appendChild(span_cp);
				        		}
			        	    }
			        	    
			        	    
			        	    str="",str2="",str3="";
			        	}

			        	document.getElementById("container").appendChild(textarea);
			        	//document.getElementById('passage-text').style.height = (height-340)+'px';
			        	document.getElementById('passage-text').style.height = (height-215)+'px';
			        	document.getElementById('passage-text').style.width = (width-35)+'px';
			        	//document.getElementById("passage-text").style.visibility = "hidden";
			        	if(rtx=="Y"){
			        		document.getElementById("container").appendChild(textarea_tx);
			        		document.getElementById("container").appendChild(textarea_rx);
			        		//document.getElementById('passage-text-tx').style.height = (height-340)+'px';
			        		document.getElementById('passage-text-tx').style.height = (height-215)+'px';
			        		document.getElementById('passage-text-tx').style.width = (width/2-10)+'px';
			        		document.getElementById('passage-text-tx').style.left = 0;
			        		//document.getElementById('passage-text-rx').style.height = (height-340)+'px';
			        		document.getElementById('passage-text-rx').style.height = (height-215)+'px';
			        		document.getElementById('passage-text-rx').style.width = (width/2-10)+'px';
			        		document.getElementById('passage-text-rx').style.left = (width/2)+'px';
			        		
			        		document.getElementById('toggleBtn').src = "images/split.png";
			        		//document.getElementById("passage-text").style.visibility = "hidden";
			        		document.getElementById("passage-text-rx").style.visibility = "hidden";
			        		document.getElementById("passage-text-tx").style.visibility = "hidden";
			        	}else{
			        		document.getElementById("passage-text").style.visibility = "visible";
			        	}
						
						// generate word list and auto play (init)
						// sync text
			        	
		        		var args = {
					            text_element: document.getElementById('passage-text'),
					            audio_element: audio
					        };
		        		if(rtx=="Y"){
		        			args.text_element_tx = document.getElementById('passage-text-tx');
		        			args.text_element_rx = document.getElementById('passage-text-rx');
			        	}
			        	that.generateWordList(args);
						
			        	if(parent.keyword != '' && typeof parent.keyword != 'undefined'){ //window.opener.keyword
			        		document.getElementById('srcKey').value = parent.keyword;
			        		setTimeout(function(){
			        			search();
			        		},300);
			        	}else if(parent.media_gb == 'voc'){
			        		var regkeyword = sttResult.REGKEYWORD.record;  
			        		setTimeout(function(){
			        			search_regkeyword(regkeyword);
			        		},300);
			        	}
			        	
		        	}
		        }
		    };
		    
		    xhr.send();
		},
		
		/**
	     * Build an index of all of the words that can be read along with their begin,
	     * and end times, and the DOM element representing the word.
	     * add on read-along.js!!!!
	     */
		generateWordList: function (args) {
	    	var name;
	        for (name in args) {
	            this[name] = args[name];
	        }
	        
	        var word_els = this.text_element.querySelectorAll('[data-start]');
	        this.words = Array.prototype.map.call(word_els, function (word_el, index) {
	        	
	            var word = {
	                'begin': parseFloat(word_el.getAttribute("data-start"))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
	                'dur': parseFloat(word_el.getAttribute("data-dur"))*0.01,     //0.001-> SYNC START TIME ms으로 제공
	                'gb': word_el.getAttribute("data-gb"),
	                'element': word_el
	            };
	            word_el.tabIndex = 0; // to make it focusable/interactive wow!!!! that's it!
	            word.index = index;
	            word.end = word.begin + word.dur;
	            word_el.setAttribute("index",word.index);
	            //word_el.dataset.index = word.index;

	            return word;
	        });
	        
	        if(rtx=="Y"){
	        	var word_els_tx = this.text_element_tx.querySelectorAll('[data-start]');
	        	this.words_tx = Array.prototype.map.call(word_els_tx, function (word_el, index) {
		            var word = {
		                'begin': parseFloat(word_el.getAttribute("data-start"))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
		                'dur': parseFloat(word_el.getAttribute("data-dur"))*0.01,     //0.001-> SYNC START TIME ms으로 제공
		                'gb': word_el.getAttribute("data-gb"),
		                'element': word_el
		            };
		            word_el.tabIndex = 0; // to make it focusable/interactive wow!!!! that's it!
		            word.index = index;
		            word.end = word.begin + word.dur;
		            word_el.setAttribute("index",word.index);
		            //word_el.dataset.index = word.index;

		            return word;
		        });
	        	
	        	var word_els_rx = this.text_element_rx.querySelectorAll('[data-start]');
				this.words_rx = Array.prototype.map.call(word_els_rx, function (word_el, index) {
				    var word = {
				        'begin': parseFloat(word_el.getAttribute("data-start"))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
				        'dur': parseFloat(word_el.getAttribute("data-dur"))*0.01,     //0.001-> SYNC START TIME ms으로 제공
				        'gb': word_el.getAttribute("data-gb"),
				        'element': word_el
				    };
				    word_el.tabIndex = 0; // to make it focusable/interactive wow!!!! that's it!
				    word.index = index;
				    word.end = word.begin + word.dur;
				    word_el.setAttribute("index",word.index);
				    //word_el.dataset.index = word.index;
				
				    return word;
				});
	        }
	    },
		
	    
	    /**
	     * Select the current word and set timeout to select the next one if playing
	     */
	    selectCurrentWord: function() {
	    	
	    	var that = this;

	    	var size = parseFloat(window.getComputedStyle(document.getElementById('passage-text'), null).getPropertyValue('font-size'))+3;
	        var current_word = this.getCurrentWord();
	        var is_playing = wmp.isPlaying();


//	        if(current_word!=null){

	        
	        	if (!current_word.element.classList.contains('speaking')) { // play time~ yeh!
	        		that.removeWordSelection();
	                current_word.element.classList.add('speaking'); //들어오고,다시 play mode로 변경해주고..
	                if (this.autofocus_current_word) {
	                	
	                    current_word.element.focus();
	                	
		                current_word.element.style.fontSize = size+'px'; 
		                current_word.element.style.fontWeight = this.fontWeight;
		                current_word.element.style.color = this.selColor;
	                    if(rtx=="Y" && current_word.gb=="TX") current_word.element.style.color = this.selColor; //this.txColor;
	                    else if(rtx=="Y" && current_word.gb=="RX") current_word.element.style.color = this.selColor; //this.rxColor;
	//                	document.getElementById("srcKey").focus();
	                }
	            }else { // pause !
	
	            	if (this.autofocus_current_word) {
	 
	            		current_word.element.focus();
	//	            	current_word.element.style.border = this.borderStyle;
		            	
	                    if(rtx=="Y" && current_word.gb=="TX") current_word.element.style.color = this.selColor; //this.txColor;
	                    else if(rtx=="Y" && current_word.gb=="RX") current_word.element.style.color = this.selColor; //this.rxColor;
	//                	document.getElementById("srcKey").focus();
	                }
	            }
	        	
	        	//화자분리 안된 것 먼저 칠하고,  tx/rx 중에 같은 word 찾아서 똑같이 칠해주는 과정 추가
	        	var idx_t = arrayObjectIndexOf(this.words_tx, current_word.begin, "begin");
	        	if(idx_t>-1){
	        		this.words_tx[idx_t].element.classList.add('speaking_t');
	        		this.words_tx[idx_t].element.focus();
	        		this.words_tx[idx_t].element.style.fontSize = size+'px'; 
	        		this.words_tx[idx_t].element.style.color = this.selColor; //this.txColor;
	        		this.words_tx[idx_t].element.style.fontWeight = this.fontWeight;
	        	}
	        	var idx_r = arrayObjectIndexOf(this.words_rx, current_word.begin, "begin");
	        	if(idx_r>-1){
	        		this.words_rx[idx_r].element.classList.add('speaking_r');
	        		this.words_rx[idx_r].element.focus();
	        		this.words_rx[idx_r].element.style.fontSize = size+'px';
	        		this.words_rx[idx_r].element.style.color = this.selColor; //this.rxColor;
	        		this.words_rx[idx_r].element.style.fontWeight = this.fontWeight;
	        	}
	        	
	        	var sub_obj = document.getElementById("sub-container");
	        	var passage = document.getElementById("passage-text");
	        	if(sub_obj!=null){
	        		if(parent.document.title=="녹취리스트") passage.style.height='330px';
	        		else passage.style.height='470px';
	        	} else {
	        		if(parent.document.title=="녹취리스트") passage.style.height='510px'; 
	        		else passage.style.height='645px';
	        	}

		        
	        	/**
	             * The timeupdate Media event does not fire repeatedly enough to be
	             * able to rely on for updating the selected word (it hovers around
	             * 250ms resolution), so we add a setTimeout with the exact duration
	             * of the word.
	             */
	                
                // Automatically trigger selectCurrentWord when the next word begins
                var next_word = this.words[current_word.index + 1];
                if (next_word) {
                	
                    var seconds_until_next_word_begins = next_word.begin - wmp.getCurrentTime();
                    //var orig_seconds_until_next_word_begins = seconds_until_next_word_begins; // temp
                    
                    if (typeof this.audio_element == 'object' ){  //&& !isNaN(this.audio_element)) {
                        seconds_until_next_word_begins *= 1.0 / wmp.getPlayBackRate();
                    }
//	                    clearTimeout(this._current_next_select_timeout_id);
//	                    this._current_next_select_timeout_id = 
                    if (is_playing) {
                    	setTimeout(
	                        function () {
	                            that.selectCurrentWord();
	                        }, Math.max(seconds_until_next_word_begins * 100, 0)
                    	);
                    }
	             }
//	        }
	    },
	    
	    /**
	     * From the audio's currentTime, find the word that is currently being played
	     * @todo this would better be implemented as a binary search
	     */
	    getCurrentWord: function () {
	        var i;
	        var len;
	        var is_current_word;
	        var word = null;
	        var cur_begin, next_begin;
	        for (i = 0; i < this.words.length; i += 1) {
	        	if(i<this.words.length-1){
//	        		cur_begin = wmp.getCurrentTime() >= this.words[i].begin;
//	        		next_begin = wmp.getCurrentTime() >= this.words[i+1].begin;
//	        		if(next_begin - cur_begin ==1){ //STT믄징븐리 케이스만!
//	        			
//	        		}else{
	        			is_current_word = (
	    		            	(
	    		            		wmp.getCurrentTime() >= this.words[i].begin
	    			            	&&
	    			            	wmp.getCurrentTime() < this.words[i+1].begin
	    		            	)
	    		            	||
	    		            	(wmp.getCurrentTime() < this.words[i].begin)
	    		            );
//	        		}
		            
	        	 }else{
	        		 is_current_word = true;
	        	 }
	            if (is_current_word) {
	                word = this.words[i];
	                break;
	            }
	        }
	        
	        if (!word) {
	            throw Error('Unable to find current word and we should always be able to.');
	        }

	        return word;
	    },
	    
	    removeWordSelection: function() {
	        // There should only be one element with .speaking, but selecting all for good measure
	    	var size = parseFloat(window.getComputedStyle(document.getElementById('passage-text'), null).getPropertyValue('font-size'));
	    	var that = this;
	        var spoken_word_els = this.text_element.querySelectorAll('span[data-start].speaking');
	        
	        if(spoken_word_els.length>0){
	        	Array.prototype.forEach.call(spoken_word_els, function (spoken_word_el) {
	 	           spoken_word_el.classList.remove('speaking');
	 	           if(rtx=="Y"){
	 	        	  var gb = document.getElementById(spoken_word_el.id).getAttribute("data-gb");
	 	        	  if(gb=="RX") spoken_word_el.style.color = that.rxColor;
	 	        	  else if (gb=="TX") spoken_word_el.style.color = that.txColor;
	 	           }else{
	 	        	  spoken_word_el.style.color = that.defColor;
	 	           }
	 	           spoken_word_el.style.fontWeight = "";
	 	           spoken_word_el.style.fontSize = size+"px";
	 	        });
	        }
	       

	        if(rtx=="Y"){
		        var current_word = this.getCurrentWord();
	        	var idx_t = arrayObjectIndexOf(this.words_tx, current_word.begin, "begin");
	        	if(idx_t>-1){
		        	var spoken_word_els_tx = this.text_element_tx.querySelectorAll('span[data-start].speaking_t');
			        Array.prototype.forEach.call(spoken_word_els_tx, function (spoken_word_el) {
			        	spoken_word_el.classList.remove('speaking_t');
			        	spoken_word_el.style.color = that.txColor;
			            spoken_word_el.style.fontWeight = "";
			            spoken_word_el.style.fontSize = size+"px";
			        });
	        	}
	        	var idx_r = arrayObjectIndexOf(this.words_rx, current_word.begin, "begin");
	        	if(idx_r>-1){
			        var spoken_word_els_rx = this.text_element_rx.querySelectorAll('span[data-start].speaking_r');
			        Array.prototype.forEach.call(spoken_word_els_rx, function (spoken_word_el) {
			        	spoken_word_el.classList.remove('speaking_r');
			        	spoken_word_el.style.color = that.rxColor;
			            spoken_word_el.style.fontWeight = "";
			            spoken_word_el.style.fontSize = size+"px";
			        });
	        	}
	        }
	        
	    },
	    
	    initSelection: function() {
	        // There should only be one element with .speaking, but selecting all for good measure
	    	var size = parseFloat(window.getComputedStyle(document.getElementById('passage-text'), null).getPropertyValue('font-size'));
	    	
	        var spoken_word_els = this.text_element.querySelectorAll('span[data-start].speaking');
	        Array.prototype.forEach.call(spoken_word_els, function (spoken_word_el) {
	            spoken_word_el.classList.remove('speaking');
	            //spoken_word_el.style.color = "";
	            spoken_word_el.style.fontWeight = "";
	            spoken_word_el.style.fontSize = size+"px";
	        });
	        
	        if(rtx=="Y"){
	        	var spoken_word_els_tx = this.text_element_tx.querySelectorAll('span[data-start].speaking_t');
		        Array.prototype.forEach.call(spoken_word_els_tx, function (spoken_word_el) {
		        	spoken_word_el.classList.remove('speaking_t');
		        	 spoken_word_el.style.color = "";
		             spoken_word_el.style.fontWeight = "";
		             spoken_word_el.style.fontSize = size+"px";
		        });
		        var spoken_word_els_rx = this.text_element_rx.querySelectorAll('span[data-start].speaking_r');
		        Array.prototype.forEach.call(spoken_word_els_rx, function (spoken_word_el) {
		        	spoken_word_el.classList.remove('speaking_r');
		        	 spoken_word_el.style.color = "";
		             spoken_word_el.style.fontWeight = "";
		             spoken_word_el.style.fontSize = size+"px";
		        });
	        }
	    },
	    
	    mark: function(percents) {
	    	
	    	//if(document.getElementById('waveform').querySelectorAll('canvas').length != 0 ){
	    	var total = document.getElementById("waveform").offsetWidth;
	    	var left_position = total * percents;
	    	
	    	var obj = document.createElement('div');
	    	
	    	obj.id = left_position;
	    	obj.class = "mark";
	    	obj.style.cssText = 'position:absolute;z-index:0;top:0px;left:'+ left_position +'px;width:1px;height:50px;background-color:#FFD33C;cursor:pointer;';

	    	document.getElementById("waveform").appendChild(obj);
	    	//}
	    },
	    
	    removeMark: function(){
	    	var mark_els = document.getElementById("waveform").querySelectorAll('div');
	    	
    		for(var i=0; i<mark_els.length; i++){
    			if(mark_els[i].id == playedId || mark_els[i].id == barId){
    				continue;
	    		}else{
	    			document.getElementById("waveform").removeChild(mark_els[i]);
	    		}
	    	}
	    }
	    
		
};
	
