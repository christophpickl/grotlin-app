

// http://www.techumber.com/2013/08/javascript-object-oriented-programming-tutorial.html

var Log = function(source) {
	var source = source;
	this.debug = function (message) {
		_log("DEBUG", message);
	};
	this.info = function (message) {
		_log("INFO ", message);
	};
	this.error = function (message) {
		_log("ERROR", message);
	};
	var _log = function (level, message) {
		console.log("[" + level + "] " + source + " - " + message);
	};
};


$(document).ready(function() {
    console.log("document ready ...");
    
    var controller = new AppController();
    controller.init();
});

