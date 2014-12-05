
function getVersion() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8888/version",
        accept: "application/json",
        success: function(data) {
            console.log("SUCCESS version")
            alert('Server version is: ' + data.artifactVersion + "\nBuild date: " + data.buildDate)
        },
        error: function(request, status, error) {
            alert("ERROR: " + error + " (status=" + status + ")")
        }
    })
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
            console.log("SUCCESS: data=" + data)
        },
        error: function(request, status, error) {
            alert("ERROR: " + error + " (status=" + status + ")")
        }
    })
}

function getChannelToken() {
    console.log("getChannelToken()");
    $.ajax({
        type: "POST",
        url: "http://localhost:8888/channel",
        dataType: 'json',
        headers: {
            "X-access_token": $("#txt_access_token").val()
        },
        // contentType : 'application/json',
        // noooooo ... async: false,
        data: JSON.stringify({  }),
        success: function(data) {
            console.log("SUCCESS: channel token = " + data.token)
            $("#txt_channel_token").val(data.token)
        },
        error: function(request, status, error) {
            alert("ERROR: " + error + " (status=" + status + ")")
            // request.responseJSON.message ... how to access those? seen in debugger, but not accessible this way.
            // request.responseJSON.code
        }
    })
}

$(document).ready(function() {
    console.log("Start it up, pump it up!");

    $("#btn_get_version").click(function() {
        getVersion()
    })
    $("#btn_get_channel_token").click(function() {
        getChannelToken()
    })

    $("#btn_connect_channel").click(function() {
        connectChannel($("#txt_channel_token").val())
    })
});

