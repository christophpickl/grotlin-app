'use strict';

// http://jasmine.github.io/2.1/introduction.html

describe('Channel Token stuff', function() {

	// var baseUrl = "http://swirl-engine.appspot.com";
	var baseUrl = "http://localhost:8888";
	
	it('create channel token', function(done) {
		var testee = new RestClient(baseUrl);
		testee.setAccessToken("1");
		testee.createChannelToken(function(data) {
			console.log("channel token: ", data);
			var channelToken = data.channelToken;
			
			var channel = new ChannelClient();
			channel.connect(channelToken, function(message) {
				console.log("channel message: ", message);
			});
			done();
		});
	});
	
	
});
