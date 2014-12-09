package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.RestResponse
import at.cpickl.grotlin.restclient.Status
import org.hamcrest.MatcherAssert.assertThat
import at.cpickl.grotlin.endpoints.UserClient
import at.cpickl.grotlin.endpoints.VersionClient


fun baseUrl(): String {
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

class Clients {
    class object {
        fun user(): UserClient = UserClient(baseUrl())
        fun version(): VersionClient = VersionClient(baseUrl())
    }
}