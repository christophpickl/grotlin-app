
var AppController = function() {
	var LOG = new Log("AppController");
	var restClient = new RestClient();
	var channelClient = new ChannelClient();
	var self = this;

	this._logOutput = function(message) {
		$("#outputText").val(message);
	};
    this._gameId = function () {
        return $("#txt_gameId").val();
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
        $("#btn_resetDB").click(function() {
            restClient.resetDB(function() {
                LOG.debug("SUCCESS reset DB");
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
                self._logResponseData(data);
            });
        });
        $("#btn_attack").click(function() {
            self._logOutput("Attacking ...");
            var regionIdSource = $("#txt_attack_source").val();
            var regionIdTarget = $("#txt_attack_target").val();
            restClient.attack(self._gameId(), regionIdSource, regionIdTarget, function(data) {
                LOG.debug("SUCCESS attacking");
                self._logResponseData(data);
            });
        });

        $("#btn_show_game").click(function() {
            restClient.showGame(self._gameId(), function(data) {
                self._logResponseData(data);
            });
        });
        $("#btn_end_turn").click(function() {
            restClient.endTurn(self._gameId(), function(data) {
                self._logOutput("Next player's turn. (actually distribution phase should start. response should contain units to deploy)")
            });
        });

        $("#btn_channel_token_and_connect").click(function() {
            self._logOutput("Establishing channel... creating token.");
            restClient.createChannelToken(function(data) {

                LOG.debug("createChannelToken() SUCCESS result: channel token = " + data.channelToken);
                $("#txt_channel_token").val(data.channelToken);
                localStorage.setItem('channelToken', data.channelToken);
                self._logResponseData(data);
                channelClient.connect($("#txt_channel_token").val(), function (message) {

                    LOG.debug("Received channel message of type '" + message.type + "'.");
                    $("#txt_channel_messages").val("Message: " + message.type + "\n" + $("#txt_channel_messages").val());
                    switch (message.type) {
                        case "waitingGame":
                            LOG.debug("Waiting game new users count: " + message.newUsersCount);
                            break;
                        case "gameStarts":
                            LOG.debug("Game is starting! Game ID: " + message.gameId);
                            $("#txt_gameId").val(message.gameId);
                            break;
                        case "attack":
                            LOG.debug("Attacked! Player won: " + message.wonUserName);
                            // regionIdAttacker, regionIdDefender, diceAttacker, diceDefender
                            break;
                    }
                });
            });
        });

	};
	
	this.init = function() {
		this._initByLocalStorage();
		
		restClient.setBaseUrl($("input:radio[name=inp_environment]:checked").val());
		restClient.setAccessToken($("#txt_access_token").val());
		
		this._registerHandlers();
	};

    this._logResponseData = function(data) {
        this._logOutput(JSON.stringify(data, null, "\t"));
    };
};
