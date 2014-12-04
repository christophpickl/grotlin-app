package at.cpickl.grotlin.restclient

import org.apache.http.client.methods.HttpRequestBase
import org.slf4j.LoggerFactory
import org.apache.http.impl.client.DefaultHttpClient
import java.net.URI
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.codehaus.jackson.map.ObjectMapper
import org.apache.http.entity.StringEntity
import java.net.URLEncoder


public abstract class BaseRestClientImpl <T : AnyRestClient<T>, R : HttpRequestBase> (private val baseUrl: String) : AnyRestClient<T> {
    class object {
        private val LOG = LoggerFactory.getLogger("at.cpickl.grotlin.restclient.BaseRestClientImpl")
    }

    private val headers = hashMapOf<String, String>()
    private val queryParams = hashMapOf<String, String>()

    override public fun url(endpoint: String): RestResponse {
        val client = DefaultHttpClient()
        val request = request()

        request.setURI(URI("${baseUrl}${endpoint}${buildQueryParams()}"))
        request.setHeader("Accept", "application/json")
        headers.forEach { (key, value) -> request.setHeader(key, value) }

        LOG.info("Executing request to: {}", request.getURI())
        val response = client.execute(request)
        return RestResponse(response)
    }

    override public fun header(name: String, value: String): T {
        headers.put(name, value)
        return self()
    }
    override public fun queryParameter(name: String, value: String): T {
        queryParams.put(name, value)
        return self()
    }
    override public fun accessToken(token: String): T {
        return header("X-access_token", token)
    }

    abstract protected fun self(): T
    abstract protected fun request(): R

    private fun buildQueryParams(): String {
        if (queryParams.empty) {
            return ""
        }
        return queryParams.keySet().fold("?", {(accumulator, key) ->
            "${accumulator}${key.urlEncode()}=${queryParams.get(key.urlEncode())}&"
        })
    }
}

private fun String.urlEncode(): String {
    return URLEncoder.encode(this, "ISO-8859-1")
}

class GetRestClientImpl(baseUrl: String) : BaseRestClientImpl<GetRestClient, HttpGet>(baseUrl), GetRestClient {
    override fun request(): HttpGet = HttpGet()
    override fun self(): GetRestClient = this

}

class PostRestClientImpl(baseUrl: String) : BaseRestClientImpl<PostRestClient, HttpPost>(baseUrl), PostRestClient {
    private var entity: Any? = null
    private val mapper = ObjectMapper()

    override fun request(): HttpPost {
        val request = HttpPost()
        if (entity != null) {
            val entityAsJson = mapper.writeValueAsString(entity)
            header("Content-Type", "application/json")
            request.setEntity(StringEntity(entityAsJson))
        }
        return request
    }

    override fun body(entity: Any): PostRestClient {
        this.entity = entity
        return self()
    }

    override fun self(): PostRestClient = this
}
