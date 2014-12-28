
var AppController = function() {
	var LOG = new Log("AppController");
	var restClient = new RestClient();
	var channelClient = new ChannelClient();
	var self = this;

	this._logOutput = function(message) {
		$("#outputText").val(message);
	};

	this._initByLocalStorage = function() {
		LOG.debug("initByLocalStorage()");
		var storedAccessToken = localStorage.getItem('accessToken');
		if (storedAccessToken != null) {
			$("#txt_access_token").val(storedAccessToken);
		}
		var storedChannelToken = localStorage.getItem('channelToken');
		if (storedChannelToken != null) {
			$("#txt_channel_token").val(storedChannelToken);
		}
		var storedEnvironment = localStorage.getItem('environment');
		if (storedEnvironment != null) {
			var environmentButton = $("#" + storedEnvironment);
			environmentButton.prop("checked", true);
		}
		LOG.debug("initByLocalStorage() DONE");
	};
	
	this._registerHandlers = function() {
		$("input:radio[name=inp_environment]").change(function() {
			localStorage.setItem('environment', $(this).attr("id"));
			restClient.setBaseUrl($(this).val());
		});
		$("#txt_access_token").keyup(function() {
			var newToken = $(this).val();
			localStorage.setItem('accessToken', newToken); 
			restClient.setAccessToken(newToken);
		});
		$("#txt_channel_token").keyup(function() {
			var newToken = $(this).val();
			localStorage.setItem('channelToken', newToken); 
		});

        $("#btn_get_version").click(function() {
            self._logOutput("Loading ...");
            restClient.getVersion(function(data) {
                LOG.debug("SUCCESS version");
                self._logOutput(JSON.stringify(data, null, "\t"));
            });
        });
        $("#btn_shutdown").click(function() {
            restClient.shutdown(function() {
                LOG.debug("SUCCESS shutdown");
            });
        });
	    $("#btn_join_game").click(function() {
	    	self._logOutput("Loading ...");
	        restClient.joinGame(function(data) {
	            LOG.debug("SUCCESS random game");
	            self._logOutput(JSON.stringify(data, null, "\t"));
	        });
	    });
	    
	    $("#btn_get_channel_token").click(function() {
	    	self._logOutput("Loading ...");
	        restClient.createChannelToken(function(data) {
	            LOG.debug("createChannelToken() SUCCESS result: channel token = " + data.channelToken);
	            $("#txt_channel_token").val(data.channelToken);
	            localStorage.setItem('channelToken', data.channelToken);
	            self._logOutput(JSON.stringify(data, null, "\t"));
	        });
	    });
	    $("#btn_connect_channel").click(function() {
	        channelClient.connect($("#txt_channel_token").val(), function (message) {
	        	// TODO do something with the received message.data
	        	LOG.debug("Received channel message of type '" + message.type + "'.");
                $("#txt_channel_messages").val("Message: " + message.type + "\n" + $("#txt_channel_messages").val());
	        	switch (message.type) {
	        		case "waitingGame":
	        			LOG.debug("Waiting game new users count: " + message.newUsersCount);
	        		break;
	        		case "gameStarts":
	        			LOG.debug("Game is starting! Game ID: " + message.gameId);
	        		break;
	        	}
	    	});

	    });

	};
	
	this.init = function() {
		this._initByLocalStorage();
		
		restClient.setBaseUrl($("input:radio[name=inp_environment]:checked").val());
		restClient.setAccessToken($("#txt_access_token").val());
		
		this._registerHandlers();
	};
};
