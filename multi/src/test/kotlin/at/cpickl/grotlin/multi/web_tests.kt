package at.cpickl.grotlin.multi

import org.testng.annotations.Test
import org.hamcrest.Matchers
import org.jboss.resteasy.client.ClientRequest
import org.jboss.resteasy.client.ClientResponse
import java.util.logging.Logger
import at.cpickl.grotlin.multi.resource.VersionRto

abstract class Client {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    private val baseUrl: String
    {
        val target = System.getProperty("testTarget", "UNDEFINED")
        when (target) {
            "LOCAL" -> baseUrl = "http://localhost:8888"
            "PROD" -> baseUrl = "http://swirl-engine.appspot.com"
            "UNDEFINED" -> {
                println("No system property 'testTarget' defined, using default LOCAL target instead.")
                baseUrl = "http://localhost:8888"
            }
            else -> throw IllegalArgumentException("Invalid testTarget: '${target}'!")
        }
        LOG.fine("Test client base url: '${baseUrl}'")
    }

    fun <T> get(endpointUrl: String, entityType: Class<T>, expectedStatus: Int = 200): ClientResponse<T> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.finer("GET ${url}")
        val request = ClientRequest(url)
        request.accept("application/json")
        val response = request.get(entityType)
        assertThat(response.getStatus(), equalTo(expectedStatus))
        return response
    }
}

class VersionClient : Client() {
    fun get(): VersionRto {
        return get("/version", javaClass<VersionRto>()).getEntity()
    }
}

Test(groups = array("WebTest")) public class VersionWebTest {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    public fun getVersion() {
        val version = VersionClient().get()
        LOG.fine("Returned version: ${version}")
        assertThat(version.artifactVersion, Matchers.notNullValue())
        assertThat(version.buildDate, Matchers.notNullValue())
    }

}
