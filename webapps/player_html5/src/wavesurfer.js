/**
 * wavesurfer.js
 *
 * https://github.com/katspaugh/wavesurfer.js
 *
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 */

'use strict';

var WaveSurfer = {
    defaultParams: {
        height        : 52, //이전버전 : 124
        waveColor     : '#999',
        progressColor : '#555',
        cursorColor   : '#333',
        cursorWidth   : 1,
        skipLength    : 2,
        minPxPerSec   : 20,
        pixelRatio    : window.devicePixelRatio,
        fillParent    : true,
        scrollParent  : false,
        hideScrollbar : false,
        normalize     : false,
        audioContext  : null,
        container     : null,
        dragSelection : true,
        loopSelection : true,
        audioRate     : 1,
        interact      : true,
        splitChannels : false,
        renderer      : 'Canvas',
        backend       : 'WebAudio',
        mediaType     : 'audio',
        expandWave    : false,
        initLog       : true        
    },

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
    txColor: '#0061c1',
    rxColor: '#dd0000',
    rtx: 'Y',
    
    init: function (params) {
        // Extract relevant parameters (or defaults)
        this.params = WaveSurfer.util.extend({}, this.defaultParams, params);

        this.container = 'string' == typeof params.container ?
            document.querySelector(this.params.container) :
            this.params.container;

        if (!this.container) {
            throw new Error('Container element not found');
        }

        if (typeof this.params.mediaContainer == 'undefined') {
            this.mediaContainer = this.container;
        } else if (typeof this.params.mediaContainer == 'string') {
            this.mediaContainer = document.querySelector(this.params.mediaContainer);
        } else {
            this.mediaContainer = this.params.mediaContainer;
        }

        if (!this.mediaContainer) {
            throw new Error('Media Container element not found');
        }

        // Used to save the current volume when muting so we can
        // restore once unmuted
        this.savedVolume = 0;
        // The current muted state
        this.isMuted = false;
        // Will hold a list of event descriptors that need to be
        // cancelled on subsequent loads of audio
        this.tmpEvents = [];
        
        this.createDrawer();
        this.createBackend();
        
       
    },
    
    createDrawer: function () {
        var my = this;

        this.drawer = Object.create(WaveSurfer.Drawer[this.params.renderer]);
        this.drawer.init(this.container, this.params);

        this.drawer.on('redraw', function () {
            my.drawBuffer();
            my.drawer.progress(my.backend.getPlayedPercents());
        });

        // Click-to-seek
        this.drawer.on('click', function (e, progress) {
            setTimeout(function () {
                my.seekTo(progress);
            }, 0);
        });

        // Relay the scroll event from the drawer
        this.drawer.on('scroll', function (e) {
            my.fireEvent('scroll', e);
        });
        
        // Relay the scroll event from the drawer
        this.drawer.on('mark', function (e) {
            my.fireEvent('mark', e);
        });
        
        
    },

    createBackend: function () {
        var my = this;
       
        if (this.backend) {
            this.backend.destroy();
        }

        // Back compat
        if (this.params.backend == 'AudioElement') {
            this.params.backend = 'MediaElement';
        }

        if (this.params.backend == 'WebAudio' && !WaveSurfer.WebAudio.supportsWebAudio()) {
            this.params.backend = 'MediaElement';
        }

        this.backend = Object.create(WaveSurfer[this.params.backend]);
        this.backend.init(this.params);

		this.backend.on('finish', function () {
	        my.fireEvent('finish');
	    });
       

        this.backend.on('audioprocess', function (time) {
            my.drawer.progress(my.backend.getPlayedPercents());
            my.fireEvent('audioprocess', time);
        });
        
        
        
    },

    getDuration: function () {
        return this.backend.getDuration();
    },

    getCurrentTime: function () {
        return this.backend.getCurrentTime();
    },

    play: function (start, end) {
        this.backend.play(start, end);
        this.fireEvent('play');
//        if($('.btn-play').is('.pause')){
//	    	 $('.btn-play').removeClass('pause');
//	    	 $('.btn-play').addClass('playing');
//	     }
        
        this.text_element.classList.add('speaking');
        this.selectCurrentWord();
        this.getTrackTime();
        if(rtx=="Y"){
        	//alert("!!");
	    	 this.text_element_tx.classList.add('speaking_t');
		     this.text_element_rx.classList.add('speaking_r');
	     }
        
       if(this.params.initLog){
        	this.params.initLog=false;
        	//saveLog();        
        }
    },

    pause: function () {
        this.backend.pause();
        this.fireEvent('pause');
//        if($('.btn-play').is('.playing')){
//	    	 $('.btn-play').removeClass('playing');
//	    	 $('.btn-play').addClass('pause');
//	     }
        
        this.text_element.classList.remove('speaking');
        this.selectCurrentWord();
    },

    pause: function () {
        this.pause();
    },
    play: function () {
        this.play();
    },


    isPlaying: function () {
        return !this.backend.isPaused();
    },

    skipBackward: function (seconds) {
        this.skip(-seconds || -this.params.skipLength);
        
        //this.text_element.classList.add('speaking');
        this.selectCurrentWord();
    },

    skipForward: function (seconds) {
        this.skip(seconds || this.params.skipLength);
        
        //this.text_element.classList.add('speaking');
        this.selectCurrentWord();
    },

    skip: function (offset) {
        var position = this.getCurrentTime() || 0;
        var duration = this.getDuration() || 1;
        position = Math.max(0, Math.min(duration, position + (offset || 0)));
        this.seekAndCenter(position / duration);
    },

    seekAndCenter: function (progress) {
        this.seekTo(progress);
        this.drawer.recenter(progress);
    },

    seekTo: function (progress) {
        var paused = this.backend.isPaused();
        // avoid small scrolls while paused seeking
        var oldScrollParent = this.params.scrollParent;
        
        if (paused) {
            this.params.scrollParent = false;
        }

        this.backend.seekTo(progress * this.getDuration());
        this.drawer.progress(this.backend.getPlayedPercents());

        if (!paused) {
            this.backend.pause();
            this.backend.play();
        }
        this.params.scrollParent = oldScrollParent;
        this.fireEvent('seek', progress);
        
        this.removeWordSelection();
        this.selectCurrentWord();
    },

    mark: function(percents) {
    	var total = document.getElementById("waveform").offsetWidth;
    	var left_position = total * percents * 100;
    	var obj = document.createElement('div');
    	obj.class = "mark";
    	obj.style.cssText = 'position:absolute;z-index:1;top:0px;left:'+ left_position +'px;width:1px;height:52px;background-color:#FFD33C;cursor:hand;';

    	document.getElementById("waveform").appendChild(obj);
    },
    
    removeMark: function(){
    	var mark_els = this.audio_element.querySelectorAll('div');
    	if(mark_els.length > 2){ // marked div! [1],[2]=>progress-bar
    		for(var i=2; i<mark_els.length; i++){
    			this.audio_element.removeChild(mark_els[i]);
    		}
    	}
    },
    
    stop: function () {
        this.pause();
        this.seekTo(0);
        this.drawer.progress(0);
        this.setInitPosition(); // STT/timer initiated!
    },

    setInitPosition: function () {
    	var current_word = this.words[0];
    	
    	this.removeWordSelection();
    	current_word.element.focus();
//    	current_word.element.style.border = this.borderStyle;
    	var size = parseFloat(window.getComputedStyle(document.getElementById('passage-text'), null).getPropertyValue('font-size'))+5;
    	current_word.element.style.fontSize = size+'px'; //this.fontSize;
    	current_word.element.style.fontWeight = this.fontWeight;
    	document.getElementById("srcKey").focus();
    	this.getTrackTime();
    	
    },
    
    /**
     * Set the playback volume.
     *
     * @param {Number} newVolume A value between 0 and 1, 0 being no
     * volume and 1 being full volume.
     */
    setVolume: function (newVolume) {
    	this.backend.setVolume(newVolume);
    },

    /**
     * Set the playback rate.
     *
     * @param {Number} rate A positive number. E.g. 0.5 means half the
     * normal speed, 2 means double speed and so on.
     */
    setPlaybackRate: function (rate) {
        this.backend.setPlaybackRate(rate);
    },

    /**
     * Toggle the volume on and off. It not currenly muted it will
     * save the current volume value and turn the volume off.
     * If currently muted then it will restore the volume to the saved
     * value, and then rest the saved value.
     */
    toggleMute: function () {
        if (this.isMuted) {
            // If currently muted then restore to the saved volume
            // and update the mute properties
            this.backend.setVolume(this.savedVolume);
            this.isMuted = false;
        } else {
            // If currently not muted then save current volume,
            // turn off the volume and update the mute properties
            this.savedVolume = this.backend.getVolume();
            this.backend.setVolume(0);
            this.isMuted = true;
        }
    },

    toggleScroll: function () {
        this.params.scrollParent = !this.params.scrollParent;
        this.drawBuffer();
    },

    toggleInteraction: function () {
        this.params.interact = !this.params.interact;
    },

    toggleExpand: function () {
//    	this.params.expandWave = !this.params.expandWave;
    	this.params.normalize = !this.params.normalize;
    	this.drawBuffer();
    },
    
    drawBuffer: function () {
        var nominalWidth = Math.round(
            this.getDuration() * this.params.minPxPerSec * this.params.pixelRatio
        );
        var parentWidth = this.drawer.getWidth();
        var width = nominalWidth;

        // Fill container
        if (this.params.fillParent && (!this.params.scrollParent || nominalWidth < parentWidth)) {
            width = parentWidth;
        }

      //load 시 시간 셋팅 추가(2016.04.08)
        var duration = this.backend.getDuration();
        var duration_time_m = Math.floor(duration/60)+'';
    	var duration_time_s = Math.floor(duration%60)+'';
        duration_time_m = duration_time_m.length<2?"0"+duration_time_m:duration_time_m;
   	 	duration_time_s = duration_time_s.length<2?"0"+duration_time_s:duration_time_s;
    	document.getElementById("track-start-time").innerHTML  = "00:00";
    	document.getElementById("track-end-time").innerHTML  = duration_time_m+":"+duration_time_s;
    	
 
    	this.drawer.setWidth(width);
    	
    	/**
    	 * 파형그리기 원하는 경우 this.drawer.setWidth(width); 주석처리 후 밑에있는 주석들 풀기
    	 * */
        //var peaks = this.backend.getPeaks(width);
        //this.drawer.drawPeaks(peaks, width);
        //this.fireEvent('redraw', peaks, width);
 
    },

    /**
     * Internal method.
     */
    loadArrayBuffer: function (arraybuffer) {
        this.decodeArrayBuffer(arraybuffer, function (data) {
            this.loadDecodedBuffer(data);
        }.bind(this));
    },

    /**
     * Directly load an externally decoded AudioBuffer.
     */
    loadDecodedBuffer: function (buffer) {
        this.backend.load(buffer);
        this.drawBuffer();
        this.fireEvent('ready');
    },

    /**
     * Loads audio data from a Blob or File object.
     *
     * @param {Blob|File} blob Audio data.
     */
    loadBlob: function (blob) {
        var my = this;
        // Create file reader
        var reader = new FileReader();
        reader.addEventListener('progress', function (e) {
            my.onProgress(e);
        });
        reader.addEventListener('load', function (e) {
            my.loadArrayBuffer(e.target.result);
        });
        reader.addEventListener('error', function () {
            my.fireEvent('error', 'Error reading file');
        });
        reader.readAsArrayBuffer(blob);
        this.empty();
    },

    /**
     * Loads audio and rerenders the waveform.
     */
    load: function (url, peaks) {
        switch (this.params.backend) {
            case 'WebAudio': return this.loadBuffer(url);
            case 'MediaElement': return this.loadMediaElement(url, peaks);
        }
    },

    /**
     * Loads audio using Web Audio buffer backend.
     */
    loadBuffer: function (url) {
        this.empty();
        
        // load via XHR and render all at once
        return this.getArrayBuffer(url, this.loadArrayBuffer.bind(this));
    },

    loadMediaElement: function (url, peaks) {
        this.empty();
        this.backend.load(url, this.mediaContainer, peaks);

        this.tmpEvents.push(
            this.backend.once('canplay', (function () {
                this.drawBuffer();
                this.fireEvent('ready');
            }).bind(this)),

            this.backend.once('error', (function (err) {
                this.fireEvent('error', err);
            }).bind(this))
        );


        // If no pre-decoded peaks provided, attempt to download the
        // audio file and decode it with Web Audio.
        if (!peaks && this.backend.supportsWebAudio()) {
            this.getArrayBuffer(url, (function (arraybuffer) {
                this.decodeArrayBuffer(arraybuffer, (function (buffer) {
                    this.backend.buffer = buffer;
                    this.drawBuffer();
                }).bind(this));
            }).bind(this));
        }
    },

    decodeArrayBuffer: function (arraybuffer, callback) {
        this.backend.decodeArrayBuffer(
            arraybuffer,
            this.fireEvent.bind(this, 'decoded'),
            this.fireEvent.bind(this, 'error', 'Error decoding audiobuffer')
        );
        this.tmpEvents.push(
            this.once('decoded', callback)
        );
    },

    getArrayBuffer: function (url, callback) {
        var my = this;
        var ajax = WaveSurfer.util.ajax({
            url: url,
            responseType: 'arraybuffer'
        });
        this.tmpEvents.push(
            ajax.on('progress', function (e) {
                my.onProgress(e);
            }),
            ajax.on('success', callback),
            ajax.on('error', function (e) {
                my.fireEvent('error', 'XHR error: ' + e.target.statusText);
            })
        );
        return ajax;
    },

    onProgress: function (e) {
        if (e.lengthComputable) {
            var percentComplete = e.loaded / e.total;
        } else {
            // Approximate progress with an asymptotic
            // function, and assume downloads in the 1-3 MB range.
            percentComplete = e.loaded / (e.loaded + 1000000);
        }
        this.fireEvent('loading', Math.round(percentComplete * 100), e.target);
    },

    /**
     * Exports PCM data into a JSON array and opens in a new window.
     */
    exportPCM: function (length, accuracy, noWindow) {
        length = length || 1024;
        accuracy = accuracy || 10000;
        noWindow = noWindow || false;
        var peaks = this.backend.getPeaks(length, accuracy);
        var arr = [].map.call(peaks, function (val) {
            return Math.round(val * accuracy) / accuracy;
        });
        var json = JSON.stringify(arr);
        if (!noWindow) {
            window.open('data:application/json;charset=utf-8,' +
                encodeURIComponent(json));
        }
        return json;
    },

    clearTmpEvents: function () {
        this.tmpEvents.forEach(function (e) { e.un(); });
    },

    /**
     * Display empty waveform.
     */
    empty: function () {
        if (!this.backend.isPaused()) {
            this.stop();
            this.backend.disconnectSource();
        }
        this.clearTmpEvents();
        this.drawer.progress(0);
        this.drawer.setWidth(0);
        this.drawer.drawPeaks({ length: this.drawer.getWidth() }, 0);
    },

    /**
     * Remove events, elements and disconnect WebAudio nodes.
     */
    destroy: function () {
        this.fireEvent('destroy');
        this.clearTmpEvents();
        this.unAll();
        this.backend.destroy();
        this.drawer.destroy();
    },
    
    
    
    
    
    /**
     * Build an index of all of the words that can be read along with their begin,
     * and end times, and the DOM element representing the word.
     * add on read-along.js!!!!
     */
    generateWordList: function (args) {
   
    	rtx = args.rtx;
    	var name;
        for (name in args) {
            this[name] = args[name];
            
        }
        
        var word_els = this.text_element.querySelectorAll('[data-start]');
        
        var word_els = this.text_element.querySelectorAll('[data-start]');
        
        this.words = Array.prototype.map.call(word_els, function (word_el, index) {
            var word = {
            		'begin': (parseFloat(word_el.getAttribute("data-start")))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
	                'dur': parseFloat(word_el.getAttribute("data-dur"))*0.01,     //0.001-> SYNC START TIME ms으로 제공
	                'gb': word_el.getAttribute("data-gb"),
	                'element': word_el
            };
            word_el.tabIndex = 0; // to make it focusable/interactive wow!!!! that's it!
            word.index = index;
            word.end = word.begin + word.dur;
            word_el.dataset.index = word.index;

            return word;
        });
   
        if(rtx=="Y"){
        	var word_els_tx = this.text_element_tx.querySelectorAll('[data-start]');
        	
 
        	this.words_tx = Array.prototype.map.call(word_els_tx, function (word_el, index) {
	            var word = {
	                'begin': (parseFloat(word_el.getAttribute("data-start")))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
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
			        'begin': (parseFloat(word_el.getAttribute("data-start")))*0.01, //0.01-> SYNC START TIME 10ms으로 제공
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
    
    _current_end_select_timeout_id: null,
    _current_next_select_timeout_id: null,
    
    /**
     * Select the current word and set timeout to select the next one if playing
     */
    selectCurrentWord: function() {
    	
        var that = this;
        var size = parseFloat(window.getComputedStyle(document.getElementById('passage-text'), null).getPropertyValue('font-size'))+3;
        var current_word = this.getCurrentWord();
        var is_playing = this.isPlaying();
    
        if (!current_word.element.classList.contains('speaking')) { // play time~ yeh!
        	
    		that.removeWordSelection();
            current_word.element.classList.add('speaking'); //들어오고,다시 play mode로 변경해주고..
            if (this.autofocus_current_word) {
                current_word.element.focus();
                current_word.element.style.fontSize = size+'px'; 
                current_word.element.style.fontWeight = this.fontWeight;
                if(current_word.gb=="0") current_word.element.style.color = this.txColor;
                else if(current_word.gb=="1") current_word.element.style.color = this.rxColor;
            	document.getElementById("srcKey").focus();
            }
        }else { // pause ! 
        	if (this.autofocus_current_word) {
                current_word.element.focus();
//            		current_word.element.style.border = this.borderStyle;
            	current_word.element.style.fontSize = size+'px'; 
                current_word.element.style.fontWeight = this.fontWeight;
                if(current_word.gb=="0") current_word.element.style.color = this.txColor;
                else if(current_word.gb=="1") current_word.element.style.color = this.rxColor;
            	document.getElementById("srcKey").focus();
            }
        }
        
      //화자분리 안된 것 먼저 칠하고,  tx/rx 중에 같은 word 찾아서 똑같이 칠해주는 과정 추가
    	var idx_t = WaveSurfer.util.arrayObjectIndexOf(this.words_tx, current_word.begin, "begin");
    	if(idx_t>-1){
    		this.words_tx[idx_t].element.classList.add('speaking_t');
    		this.words_tx[idx_t].element.focus();
    		this.words_tx[idx_t].element.style.fontSize = size+'px'; 
    		this.words_tx[idx_t].element.style.color = this.txColor;
    		this.words_tx[idx_t].element.style.fontWeight = this.fontWeight;
    	}
    	var idx_r = WaveSurfer.util.arrayObjectIndexOf(this.words_rx, current_word.begin, "begin");
    	
    	if(idx_r>-1){
    		
    		this.words_rx[idx_r].element.classList.add('speaking_r');
    		this.words_rx[idx_r].element.focus();
    		this.words_rx[idx_r].element.style.fontSize = size+'px';
    		this.words_rx[idx_r].element.style.color = this.rxColor;
    		this.words_rx[idx_r].element.style.fontWeight = this.fontWeight;
    	}
        	/**
             * The timeupdate Media event does not fire repeatedly enough to be
             * able to rely on for updating the selected word (it hovers around
             * 250ms resolution), so we add a setTimeout with the exact duration
             * of the word.
             */
            if (is_playing) {
                // Remove word selection when the word ceases to be spoken
            	
                var seconds_until_this_word_ends = current_word.end - this.getCurrentTime(); // Note: 'word' not 'world'! ;-)
                if (typeof this.audio_element === 'object'){ // && !isNaN(this.audio_element)) {
                    seconds_until_this_word_ends *= 1.0/this.backend.playbackRate;
                }
                clearTimeout(this._current_end_select_timeout_id);
                this._current_end_select_timeout_id = setTimeout(
                    function () {
                        if (!that.audio_element.paused) { // we always want to have a word selected while paused
                            current_word.element.classList.remove('speaking');
//                            current_word.element.style.border = "";
                            current_word.element.style.fontSize = "";
                        	current_word.element.style.fontWeight = "";
                        }
                    },
                    Math.max(seconds_until_this_word_ends * 1000, 0)
                );

                
                
                // Automatically trigger selectCurrentWord when the next word begins
                var next_word = this.words[current_word.index + 1];
                if (next_word) {
                	
                    var seconds_until_next_word_begins = next_word.begin - this.getCurrentTime();
                    //var orig_seconds_until_next_word_begins = seconds_until_next_word_begins; // temp
                    
                    if (typeof this.audio_element == 'object' ){  //&& !isNaN(this.audio_element)) {
                        seconds_until_next_word_begins *= 1.0/this.backend.playbackRate;
                    }
                    clearTimeout(this._current_next_select_timeout_id);
                    this._current_next_select_timeout_id = setTimeout(
                        function () {
                            that.selectCurrentWord();
                        },
                        Math.max(seconds_until_next_word_begins * 1000, 0)
                    );
                }
            }
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
        
        for (i = 0; i < this.words.length; i += 1) {
        	if(i<this.words.length-1){
	            is_current_word = (
//	                
	            	(
	            			this.getCurrentTime() >= this.words[i].begin
	                        &&
	                        this.getCurrentTime() < this.words[i+1].begin
	            	)
	            	||
	            	(this.getCurrentTime() < this.words[i].begin)
	            );
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
        Array.prototype.forEach.call(spoken_word_els, function (spoken_word_el) {
            spoken_word_el.classList.remove('speaking');
            //spoken_word_el.style.color = "";
            spoken_word_el.style.fontWeight = "";
            spoken_word_el.style.fontSize = size+"px";
        });
        
        if(rtx=="Y"){
	        var current_word = this.getCurrentWord();
        	var idx_t = WaveSurfer.util.arrayObjectIndexOf(this.words_tx, current_word.begin, "begin");
        	if(idx_t>-1){
	        	var spoken_word_els_tx = this.text_element_tx.querySelectorAll('span[data-start].speaking_t');
		        Array.prototype.forEach.call(spoken_word_els_tx, function (spoken_word_el) {
		        	spoken_word_el.classList.remove('speaking_t');
		        	 spoken_word_el.style.color = "";
		             spoken_word_el.style.fontWeight = "";
		             spoken_word_el.style.fontSize = size+"px";
		        });
        	}
        	var idx_r = WaveSurfer.util.arrayObjectIndexOf(this.words_rx, current_word.begin, "begin");
        	if(idx_r>-1){
		        var spoken_word_els_rx = this.text_element_rx.querySelectorAll('span[data-start].speaking_r');
		        Array.prototype.forEach.call(spoken_word_els_rx, function (spoken_word_el) {
		        	spoken_word_el.classList.remove('speaking_r');
		        	 spoken_word_el.style.color = "";
		             spoken_word_el.style.fontWeight = "";
		             spoken_word_el.style.fontSize = size+"px";
		        });
        	}
	        
	        
        }
    },
    
    getTrackTime: function() {
    	
    	var that = this;
    	var is_playing = this.isPlaying();
    	var duration = this.backend.getDuration();
    	var current_time = this.getCurrentTime();
    	
    	if(current_time == 0) document.getElementById("track-start-time").innerHTML  = "00:00";
    	
    	if(is_playing){
    		var duration_time_m = Math.floor(duration/60)+'';
        	var duration_time_s = Math.floor(duration%60)+'';
        	var current_time_m = Math.floor(current_time/60)+'';
        	var current_time_s = Math.floor(current_time%60)+'';
        	
        	duration_time_m = duration_time_m.length<2?"0"+duration_time_m:duration_time_m;
       	 	duration_time_s = duration_time_s.length<2?"0"+duration_time_s:duration_time_s;
       	 	current_time_m = current_time_m.length<2?"0"+current_time_m:current_time_m;
       	 	current_time_s = current_time_s.length<2?"0"+current_time_s:current_time_s;
       	 
       	 	var tmp = (current_time_m+":"+current_time_s) + " / " + (duration_time_m+":"+duration_time_s);
       	 	document.getElementById("track-start-time").innerHTML  = current_time_m+":"+current_time_s;
       	 	document.getElementById("track-end-time").innerHTML  = duration_time_m+":"+duration_time_s;
       	 
        	setTimeout(
                 function () {
                     that.getTrackTime();
                 }, 1000);
        	
    	}
    }
    
};

WaveSurfer.create = function (params) {
    var wavesurfer = Object.create(WaveSurfer);
    wavesurfer.init(params);
    return wavesurfer;
};
