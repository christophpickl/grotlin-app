package at.cpickl.grotlin.multi.webtests

import org.testng.annotations.Test
import org.hamcrest.Matchers
import org.jboss.resteasy.client.ClientRequest
import org.jboss.resteasy.client.ClientResponse
import java.util.logging.Logger
import at.cpickl.grotlin.multi.resource.VersionRto
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.equalTo

fun Response.assertStatusCode(expected: Status) {
    assertThat(getStatus(), equalTo(expected.getStatusCode()))
}

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
        request.accept(MediaType.APPLICATION_JSON)
        val response = request.get(entityType)
        assertThat(response.getStatus(), equalTo(expectedStatus))
        return response
    }

    fun getAny(endpointUrl: String): ClientResponse<out Any?> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.finer("GET ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        return request.get()
    }

    fun post(endpointUrl: String, body: Any): ClientResponse<out Any?> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.finer("POST ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        request.body(MediaType.APPLICATION_JSON, body)
        val response = request.post()
        return response
    }
}

class TestClient : Client() {
    fun get(url: String): Response {
        return getAny(url)
    }
}

Test(groups = array("WebTest")) public class MiscWebTest {
    public fun invalidUrlShouldReturn404NotFound() {
        assertThat(TestClient().getAny("/not_existing").getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()))
    }
}
