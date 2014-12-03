package at.cpickl.grotlin.multi.webtests

import org.jboss.resteasy.client.ClientResponse
import org.jboss.resteasy.client.ClientRequest
import javax.ws.rs.core.MediaType
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.equalTo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import at.cpickl.grotlin.multi.resource.VersionRto
import at.cpickl.grotlin.multi.resource.LoginRequestRto
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

class RestClient {
    fun get(): GetRestClient = GetRestClientImpl()
    fun post(): PostRestClient = PostRestClientImpl()
}

class RestResponse(private val clientResponse: ClientResponse<out Any?>) {
    fun <T> unmarshallTo(javaClass: Class<T>): T {
        return clientResponse.getEntity(javaClass)
    }

    fun assertStatusCode(expected: Status) {
        assertThat(clientResponse, hasStatus(expected))
    }

}

trait AnyRestClient<T : AnyRestClient<T>> {
    fun header(name: String, value: String): T
    fun accessToken(token: String): T
    fun url(endpoint: String): RestResponse
}
trait GetRestClient : AnyRestClient<GetRestClient> {

}
trait PostRestClient : AnyRestClient<PostRestClient> {
    fun body(entity: Any): PostRestClient

}
abstract class BaseRestClientImpl<T : AnyRestClient<T>> : AnyRestClient<T> {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<Client>())
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
        LOG.debug("Client base url: '${baseUrl}'")
    }

    private val headers = hashMapOf<String, String>()

    override fun url(endpoint: String): RestResponse {
        val request = ClientRequest("${baseUrl}${endpoint}")
        request.accept(MediaType.APPLICATION_JSON)
        return RestResponse(execute(request))
    }
    override fun header(name: String, value: String): T {
        headers.put(name, value)
        return self()
    }
    override fun accessToken(token: String): T {
        return header("X-access_token", token)
    }

    abstract protected fun self(): T
    abstract protected fun execute(request: ClientRequest): ClientResponse<out Any?>
}
class GetRestClientImpl : BaseRestClientImpl<GetRestClient>(), GetRestClient {
    override fun execute(request: ClientRequest): ClientResponse<out Any?> {
        return request.get()
    }
    override fun self(): GetRestClient = this

}
class PostRestClientImpl : BaseRestClientImpl<PostRestClient>(), PostRestClient {
    private var entity: Any? = null

    override fun body(entity: Any): PostRestClient {
        this.entity = entity
        return this
    }

    override fun execute(request: ClientRequest): ClientResponse<out Any?> {
        if (entity != null) {
            request.body(MediaType.APPLICATION_JSON, entity)
        }
        return request.post()
    }
    override fun self(): PostRestClient = this
}

Test class Foo {
    fun foo() {
        val response = RestClient().get().url("/version")
        println(response.unmarshallTo(javaClass<VersionRto>()))
    }
}

/**
 * @deprecated use the RestClient infrastructure instead!
 */
Deprecated abstract class Client {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<Client>())
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
        LOG.debug("Test client base url: '${baseUrl}'")
    }

    fun <T> get(endpointUrl: String, entityType: Class<T>, expectedStatus: Int = 200): ClientResponse<T> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.debug("GET ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        val response = request.get(entityType)
        assertThat(response.getStatus(), equalTo(expectedStatus))
        return response
    }

    fun getAny(endpointUrl: String): ClientRequest {
        val url = "${baseUrl}${endpointUrl}"
        LOG.debug("GET ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        return request
    }

    fun post(endpointUrl: String, body: Any? = null): ClientResponse<out Any?> {
        val url = "${baseUrl}${endpointUrl}"
        LOG.debug("POST ${url}")
        val request = ClientRequest(url)
        request.accept(MediaType.APPLICATION_JSON)
        if (body != null) {
            request.body(MediaType.APPLICATION_JSON, body)
        }
        val response = request.post()
        return response
    }
}
