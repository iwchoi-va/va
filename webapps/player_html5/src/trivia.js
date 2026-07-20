var GLOBAL_ACTIONS = {
    'play': function () {
    	//loadAudio();
    	if(log_gb == 'S'){
    		loadAudio(); //file download
			saveLog('L'); //listen log
			log_gb = 'L';
    	}else{
	    	if(typeof wavesurfer.backend.buffer != "object"){
	    		alert("파일을 다운로드 하는 중입니다.");
	    	}
        	wavesurfer.play();
    	}
    },
    'pause': function () {
    	//loadAudio();
        wavesurfer.pause();
    },

    'stop': function () {
        wavesurfer.stop();
    },
    
    'back': function () {
        wavesurfer.skipBackward();		
    },

    'forth': function () {
        wavesurfer.skipForward();
    },

    'toggle-mute': function () {
        wavesurfer.toggleMute();
    },
    
    'toggleExpand': function () {
        wavesurfer.toggleExpand();
    }
    
};


// Bind actions to buttons and keypresses
document.addEventListener('DOMContentLoaded', function () {
    document.addEventListener('keydown', function (e) {
        var map = {
            32: 'play',       // space
            37: 'back',       // left
            39: 'forth'       // right
        };
        var action = map[e.keyCode];
        if (action in GLOBAL_ACTIONS) {
            if (document == e.target || document.body == e.target) {
                e.preventDefault();
            }
            GLOBAL_ACTIONS[action](e);
        }
    });

    [].forEach.call(document.querySelectorAll('[data-action]'), function (el) {
        el.addEventListener('click', function (e) {
            var action = e.currentTarget.dataset.action;
            if (action in GLOBAL_ACTIONS) {
                e.preventDefault();
                GLOBAL_ACTIONS[action](e);
            }
        });
    });
});


// Misc
document.addEventListener('DOMContentLoaded', function () {
    // Web Audio not supported
    if (!window.AudioContext && !window.webkitAudioContext) {
        var demo = document.querySelector('#demo');
        if (demo) {		
            demo.innerHTML = '<img src="/example/screenshot.png" />';
        }
    }

    /*
    // Navbar links
    var ul = document.querySelector('.nav-pills');
    var pills = ul.querySelectorAll('li');
    var active = pills[0];
    if (location.search) {
        var first = location.search.split('&')[0];
        var link = ul.querySelector('a[href="' + first + '"]');
        if (link) {
            active =  link.parentNode;
        }
    }
    active && active.classList.add('active');
    */
});
