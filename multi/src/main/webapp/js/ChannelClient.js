

var ChannelClient = function() {
	this.LOG = new Log("ChannelClient");
	this.socket = null;
	this.channel = null;
};

ChannelClient.prototype.connect = function(token, onMessageFunction) {
    this.LOG.debug("connectChannel(token=" + token + ")");
    this.channel = new goog.appengine.Channel(token);
    // channel.send_message(token, "hi from JS")
	
    this.socket = channel.open();
    var self = this;
    this.socket.onopen = function () {
        self.LOG.debug("onOpened()");
    };
    this.socket.onmessage = function (message) {
        self.LOG.debug("onMessage(message.data=" + message.data+ ")");
        onMessageFunction(JSON.parse(message.data));
    };
    this.socket.onerror = function (error) {
        self.LOG.debug("onError(error.description=" + error.description + ")");
    };
    this.socket.onclose = function () {
        self.LOG.debug("onClose()");
    };
    console.log("connectChannel(token=" + token + ") ... DONE");
};
