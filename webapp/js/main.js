
function getVersion() {
    $.ajax({
        type: "GET",
        url: "http://localhost:8888/version",
        accept: "application/json",
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
});

