String.prototype.trim = function(){
   this.replace(/^\s+|\s+$/g, "");
};


String.prototype.ltrim = function(){
   this.replace( /^\s+/, "" );
};


String.prototype.rtrim = function(){
   this.replace( /\s+$/, "" );
};


Element.prototype.remove = function() {
    this.parentElement.removeChild(this);
};


NodeList.prototype.remove = HTMLCollection.prototype.remove = function() {
    for(var i = this.length - 1; i >= 0; i--) {
        if(this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
        }
    }
};

function arrayObjectIndexOf(myArray, searchTerm, property) {
    for(var i = 0, len = myArray.length; i < len; i++) {
        if (myArray[i][property] === searchTerm) return i;
    }
    return -1;
}
