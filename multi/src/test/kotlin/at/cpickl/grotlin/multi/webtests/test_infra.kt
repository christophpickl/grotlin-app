package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.restclient.RestClient


private fun baseUrl(): String {
    // i am a too stupid kotlin dev, i dont know how to init this property as a static constant :(
    val target = System.getProperty("testTarget", "UNDEFINED")
    when (target) {
        "LOCAL" -> return "http://localhost:8888"
        "PROD" -> return "http://swirl-engine.appspot.com"
        "UNDEFINED" -> {
            println("No system property 'testTarget' defined, using default LOCAL target instead.")
            return "http://localhost:8888"
        }
        else -> throw IllegalArgumentException("Invalid testTarget: '${target}'!")
    }
}

class TestClient : RestClient(baseUrl())
