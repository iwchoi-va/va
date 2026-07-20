(function($){
	var Renderer = function(canvas)
	{
	  var canvas = $(canvas).get(0);
	  var ctx = canvas.getContext("2d");
	  var particleSystem;
	
	  var that = {
	   init:function(system){
	    
	    particleSystem = system;
	    particleSystem.screenSize(canvas.width, canvas.height); 
	    particleSystem.screenPadding(80);
	    that.initMouseHandling();
	   },
	      
	   redraw:function(){
	    
	    ctx.fillStyle = "#e7ebee"; //background
	    ctx.fillRect(0,0, canvas.width, canvas.height); 
	   
	    particleSystem.eachEdge( 
	     function(edge, pt1, pt2){ 
	      ctx.strokeStyle = "rgba(0,0,0, .333)"; 
	      ctx.lineWidth = 1; 
	      ctx.beginPath();  
	      ctx.moveTo(pt1.x, pt1.y); 
	      ctx.lineTo(pt2.x, pt2.y);
	      ctx.stroke();
	    });
	 
	    var color = ['#F1552F','#FFC643','#1F366A','#E0347E','#B7C894','#827876','#976FAE','#7ECCBF','#B39976','#F1C5DC','#B7D261','#056BB7','#A9B2B1','#4B9479','#9ECEB4','#f9e1c9','#FEE05E','#01A58D','#EF3331','#154576'];
	    var idx=0;
	    
	    particleSystem.eachNode(
	     function(node, pt){ 
	      var w = 150;   
	      ctx.fillStyle = color[idx++]; 
		  ctx.fillRect(pt.x-w/2, pt.y-w/3/2, w, w/3);
		  ctx.fillStyle = "#e7ebee"; 
		  ctx.font = "20px Malgun Gothic"; 
		  ctx.textAlign = "center";  
		  ctx.textBaseline="middle"; 
	      ctx.fillText (node.name,pt.x,pt.y); 
	    });       
	   },
	  
	   initMouseHandling:function(){
	    var dragged = null;  
	    var dblclicked = null;
	    var handler = {
		     clicked:function(e){ 
			      var pos = $(canvas).offset(); 
			      _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top); 
			      dragged = particleSystem.nearest(_mouseP); 
			      if (dragged &&dragged.node !== null){
			       dragged.node.fixed = true; 
			      }
			      $(canvas).bind('mousemove', handler.dragged); 
			      $(window).bind('mouseup', handler.dropped);  
			      return false;
		     },
		     dblclicked:function(e){
	              var pos = $(canvas).offset();
	              _mouseP = arbor.Point(e.pageX - pos.left, e.pageY - pos.top)
	              dblclicked = particleSystem.nearest(_mouseP);

	              if (dblclicked === null || dblclicked.node === undefined) return
	              if (dblclicked.node !== null) {
	                  dblclicked.node.fixed = false
	                  var id = dblclicked.node.name;
	                  alert('Node selected: ' + id);

	                  if (id.indexOf("child") > -1) {
	                      //getChildInfo(id);
	                  }
	              }
	              return false;
	         },
		     dragged:function(e){ 
			      var pos = $(canvas).offset();
			      var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top);
			 
			      if (dragged &&dragged.node !== null){
			       var p = particleSystem.fromScreen(s);
			       dragged.node.p = p; 
			      }
			 
			      return false;
		     },
		     dropped:function(e){ 
			      if (dragged===null || dragged.node===undefined) return; 
			      if (dragged.node !== null) dragged.node.fixed = false; 
			      dragged = null; 
			      $(canvas).unbind('mousemove', handler.dragged);
			      $(window).unbind('mouseup', handler.dropped);
			      _mouseP = null;
			      return false;
		     }
		    
	    };
	    $(canvas).mousedown(handler.clicked);
	    $(canvas).dblclick(handler.dblclicked);
	   }
	      
	  };
	  return that;
	 };
	
	 $(document).ready(function(){
		  sys = arbor.ParticleSystem(1000);
		  sys.parameters({gravity:true});
		  sys.renderer = Renderer("#viewport"); 
		
		//  $.getJSON("data.json", 
		//   function(data){
		//    $.each(data.nodes, function(i,node){
		//     sys.addNode(node.name); 
		//    });
		//    
		//    $.each(data.edges, function(i,edge){
		//     sys.addEdge(sys.getNode(edge.src),sys.getNode(edge.dest));
		//    });
		//  });
		  var base = parent.keyword;
		  var relKey = setData();
		  
		  var baseNode = sys.addNode(base);
		  var childNode;
		  $.each(relKey, function (index, relKey) { 
			  childNode = sys.addNode(relKey.name, relKey.value);
			 
			  sys.addEdge(baseNode, childNode);
		  });
	    
	 });
	 
	 function setData(){
		var size=0; //max:20
		var keyMap = new Array();
		
		if( parent.$DS_REL_KEY.size()>20 ) size=20;
		else size = parent.$DS_REL_KEY.size();
	
		for(var i=0; i<size; i++){
			var sub = new Object();     
			sub['label'] = parent.$DS_REL_KEY.getValue(i,"keyword"); 
			sub['value'] = parent.$DS_REL_KEY.getValue(i,"cnt");
			//sub['size'] = round(parent.$DS_KEYWORDS.getValue(i,"VAL")/max*100, 0);// scale for font-size based on with the width
			keyMap[i] = sub;
		}
		
		return keyMap.map(function(d){ 
			return { "name":d.label, "value":d.value, "grade":d.value };
		  	});
	}
 

})(this.jQuery);
