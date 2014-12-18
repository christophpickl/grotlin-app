

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
		var fullMessage = nowFormatted() + " [" + level + "] " + source + " - " + message;
		$("#logText").val(fullMessage + "\n" + $("#logText").val());
		console.log(fullMessage);
	};
};

function nowFormatted() {
	var now = new Date();
	var hour = now.getHours();
	if (hour < 10) {
		hour = "0" + hour;
	}
	var min = now.getMinutes();
	if (min < 10) {
		min = "0" + min;
	}
	var sec = now.getSeconds();
	if (sec < 10) {
		sec = "0" + sec;
	}
	var ms = now.getMilliseconds();
	if (ms < 100) {
		ms = "0" + ms;
	}
	return hour + ":" + min + ":" + sec + "." + ms;
}


$(document).ready(function() {
    console.log("document ready ...");
    
    var controller = new AppController();
    controller.init();
});

