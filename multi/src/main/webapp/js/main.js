
function getVersion() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8888/version",
        accept: "application/json",
        success: function(data) {
            console.log("SUCCESS version")
        },
        error: function(request, status, error) {
            console.log("ERROR: " + error)
        }
    })
}

onOpened = function () {
    console.log("onOpened()");
}
onMessage = function (message) {
    console.log("onMessage(message.data=" + message.data+ ")");
}
onError = function(error) {
    // error.code
    console.log("onError(error.description=" + error.description + ")");
}
onClose = function() {
    console.log("onClose()");
}

function connectChannel(token) {
    console.log("connectChannel(token=" + token + ")");
    channel = new goog.appengine.Channel(token);
    // channel.send_message(token, "hi from JS")

    socket = channel.open();
    socket.onopen = function () {
        console.log("onOpened()");
    }
    socket.onmessage = function (message) {
        console.log("onMessage(message.data=" + message.data+ ")");
    }
    socket.onerror = function (error) {
        console.log("onError(error.description=" + error.description + ")");
    }
    socket.onclose = function () {
        console.log("onClose()");
    }
    console.log("connectChannel(token=" + token + ") ... DONE");
}

function sendMessage() {
    console.log("sendMessage()");
    $.ajax({
        type: "POST",
        url: "http://localhost:8888/channel/push",
        dataType: 'json',
        // contentType : 'application/json',
        async: false,
        data: JSON.stringify({  }),
        success: function(data) {
            console.log("SUCCESS")
        },
        error: function(request, status, error) {
            console.log("ERROR: " + error)
        }
    })
}


$(document).ready(function() {
    console.log("Start it up, pump it up!");

    $("#btn_get_version").click(function() {
        getVersion()
    })
    $("#btn_connect_channel").click(function() {
        // token = JSON.parse(request.response)["token"];
        connectChannel($("#txt_token").val())
    })
});

