package at.cpickl.grotlin.multi.webtests

import java.util.logging.Logger
import org.jboss.resteasy.client.ClientResponse
import org.jboss.resteasy.client.ClientRequest
import javax.ws.rs.core.MediaType
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.equalTo

abstract class Client {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    private val baseUrl: String
    {
        val target = System.getProperty("testTarget", "UNDEFINED")
        // or use ClientRequestFactory
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

    fun getAny(endpointUrl: String): ClientRequest {
        val url = "${baseUrl}${endpointUrl}"
        LOG.finer("GET ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        return request
    }

    fun post(endpointUrl: String, body: Any? = null): ClientResponse<out Any?> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.finer("POST ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        if (body != null) {
            request.body(MediaType.APPLICATION_JSON, body)
        }
        val response = request.post()
        return response
    }
}
