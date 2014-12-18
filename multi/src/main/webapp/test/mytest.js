'use strict';

// http://jasmine.github.io/2.1/introduction.html

describe('RestClient shall do what I want him to do', function() {
	
	// var baseUrl = "http://swirl-engine.appspot.com";
	var baseUrl = "http://localhost:8888";
	
	xit('should retrieve the current version asynchronously', function(done) {
		var testee = new RestClient(baseUrl);
		console.log("test running ...");
		
		testee.getVersion(function(data) {
			console.log("got version: ", data);
			expect(data.artifactVersion).toBeDefined();
			expect(data.buildDate).toBeDefined();
			done();
		});
	});
	
	xit('should return user profile', function(done) {
		var testee = new RestClient(baseUrl);
		testee.setAccessToken("1");
		testee.getProfile(function (data) {
			console.log("got profile: ", data);
			expect(data.name).toBe("user1");
			expect(data.role).toBe("Admin");
			done();
		});
	});
	
});
