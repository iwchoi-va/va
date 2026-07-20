var Class = (function(window){
	var Class = function(){};
	Class._map = {};
	Class.getClass = function(alias){
		return Class._map[alias];
	};
	
	Class.define = function(prop){
		var propCnt = 0;
		for(var className in prop){
			if(prop.hasOwnProperty(className)) propCnt++;
		}
		if(propCnt != 1) throw new Error("Class.define(). not valid class defines.");
		var classSpec = prop[className];
		
		var clazz = classSpec[className] || function(){};

		if(!classSpec.alias){
			classSpec.alias = "no-alias";
		}
		Class._map[classSpec.alias] = clazz;
		clazz.getAlias = function(){ return classSpec.alias; };

		clazz.seq = 0;
		clazz.getName = function(){
			return className;
		};
		clazz.getModel = function(){
			if(clazz.model){
				return clazz.model;
			}else{
				clazz.model = clazz.extendsModel();
			}
			return clazz.model;
		};
		clazz.extendsModel = function(){
			var result = {};
			
			if(this.prototype.parent && this.prototype.parent.getAlias() &&  Class.getClass(this.prototype.parent.getAlias())){
				var superModel = Class.getClass(this.prototype.parent.getAlias()).getModel();
				jQuery.extend(true, result, superModel);// deep copy from superModel to result
				var myModel = jQuery.extend(true, {}, Class.getClass(this.getAlias())._model);
				
				for(var i in myModel){
					if(i == "attributes"){
						if(!result.attributes){
							 result.attributes = {};

						}
						jQuery.extend(result.attributes, myModel.attributes);
						for(var att in result.attributes){
							if(result.attributes[att] === null) delete result.attributes[att];
						}
					}else if(i == "events"){
						result.events =  myModel.events.slice();
					}
				} 	
			}else{
				jQuery.extend(true, result, this._model);
			}
			return result;
		},
		clazz.getNextId = function(){
			return this.getAlias() + clazz.seq++;
		};

		if(classSpec.extend){
			var superClass = classSpec.extend; 
			clazz.prototype = new superClass;
			clazz.prototype.constructor = clazz;
			clazz.prototype.parent = superClass.prototype;		
		}
		clazz.prototype.alias = classSpec.alias;
		
		for(var statics in classSpec.statics){
			clazz[statics] = classSpec.statics[statics];
		}
		for(var proto in classSpec.prototypes){
			clazz.prototype[proto] = classSpec.prototypes[proto];
		}
		if(classSpec.namespace){
			var namespace = classSpec.namespace.split(".");
			if(namespace.length > 0){
				var temp = window[namespace[0]] || (function(){ window[namespace[0]]={}; return window[namespace[0]]})();
				for(var i=1; i < namespace.length; i++){
					temp = temp[namespace[i]] || (function(){ temp[namespace[i]] = {}; return temp[namespace[i]]})();
				}
				temp[className] = clazz;
			}else{
				window[classSpec.namespace][className] = clazz;
			}	
		}else{
			window[className] = clazz;
		}
	};
	return Class;
})(window);