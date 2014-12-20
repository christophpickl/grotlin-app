
// https://cloud.google.com/appengine/docs/java/channel/javascript

var ChannelClient = function() {
	this.LOG = new Log("ChannelClient");
    this.token = null; // google channel token
	this.channel = null; // goog.appengine.Channel
	this.socket = null; // goog.appengine.Socket
};

ChannelClient.prototype.connect = function(token, onMessageFunction) {
    this.LOG.debug("connect(token=" + token + ", onMessageFunction)");
    this.channel = new goog.appengine.Channel(token);

    this.socket = this.channel.open();
    var self = this;
    this.socket.onopen = function () {
        self.LOG.debug("channel.onOpened()");
    };
    this.socket.onmessage = function (message) {
        self.LOG.debug("channel.onMessage(message.data=" + message.data+ ")");
        onMessageFunction(JSON.parse(message.data));
    };
    this.socket.onerror = function (error) {
        self.LOG.debug("channel.onError(error.description=" + error.description + ")");
        // error.description = 'Invalid+token.' or 'Token+timed+out.'
    };
    this.socket.onclose = function () {
        self.LOG.debug("channel.onClose()");
    };
    console.log("connectChannel(token=" + token + ") ... DONE");
};

ChannelClient.prototype.close = function() {
    this.LOG.debug("close()");
    this.socket.close();
};
