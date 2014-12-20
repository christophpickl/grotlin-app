

var RestClient = function(baseUrl) {
	this.LOG = new Log("RestClient");
	this.baseUrl = baseUrl;
	this.accessToken = null;
	
	$.ajaxSetup({
		accept: "application/json",
		dataType: 'json',
		error: this._onAjaxError
	});
};

RestClient.prototype.setBaseUrl = function (value) {
	this.LOG.debug("setBaseUrl(value=" + value + ")");
	this.baseUrl = value;
};
RestClient.prototype.setAccessToken = function (value) {
	this.LOG.debug("setAccessToken(value=" + value + ")");
	this.accessToken = value;
};


/** @param onSuccessFunction takes single ajax data object as a parameter */
RestClient.prototype.getVersion = function (onSuccessFunction) {
    this.LOG.debug("getVersion()");
    this._get("/version", onSuccessFunction);
};

RestClient.prototype.getProfile = function (onSuccessFunction) {
    this.LOG.debug("getProfile()");
    this._get("/users/profile", onSuccessFunction);
};

RestClient.prototype.joinGame = function (onSuccessFunction) {
    this.LOG.debug("joinGame()");
    this._post("/game/random", {}, onSuccessFunction);
};

RestClient.prototype.createChannelToken = function(onSuccessFunction) {
    this.LOG.debug("createChannelToken()");
    this._post("/channel", {}, onSuccessFunction);
};

RestClient.prototype.pushMessage = function(body, onSuccessFunction) {
    this.LOG.debug("pushMessage()");
    this._post("/channel/push", { text: body }, onSuccessFunction);
};
RestClient.prototype.shutdown = function(onSuccessFunction) {
    this.LOG.debug("shutdown()");
    this._post("/_ah/admin/quit", { }, onSuccessFunction);
};

RestClient.prototype._executeAny = function(urlPart, onSuccessFunction, ajaxCall) {
	$.ajax(ajaxCall);
};

RestClient.prototype._get = function(urlPart, onSuccessFunction) {
	$.ajax({
        type: "GET",
        url: this._fullUrl(urlPart),
        headers: {
            "X-access_token": this.accessToken
        },
        success: onSuccessFunction
    });
};
RestClient.prototype._post = function(urlPart, payloadAsJson, onSuccessFunction /*onErrorFunction = null*/) {
    var payloadAsString = JSON.stringify(payloadAsJson, null, 2);
    this.LOG.debug("post() ... payloadAsString='" + payloadAsString + "'");
	$.ajax({
        type: "POST",
        url: this._fullUrl(urlPart),
        headers: {
            "X-access_token": this.accessToken
        },
        // contentType : 'application/json',
        data: payloadAsString,
        success: onSuccessFunction
        // TODO error: onErrorFunction ? onErrorFunction : this._onAjaxError (and remove global setting from above)
    });
};

RestClient.prototype._fullUrl = function(urlPart) {
    var fullUrl = this.baseUrl + urlPart;
    this.LOG.debug("Requesting URL: " + fullUrl);
    return fullUrl;
};

RestClient.prototype._onAjaxError = function(request, status, error) {
	console.log("RestClient.prototype._onAjaxError(request="+request+", status="+status+", error="+error+")");
	// this.LOG == undefined?!?!?
	
	// this.LOG.debug("ERROR: " + error + " (status=" + status + ") request=" + request);
    // alert("ERROR: " + error + " (status=" + status + ") request=" + request);
    // request.responseJSON.message ... how to access those? seen in debugger, but not accessible this way.
    // request.responseJSON.code
};
