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
import org.apache.http.impl.client.HttpClients
import org.apache.http.conn.ssl.SSLContexts
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.HttpHost
import org.apache.http.impl.client.CloseableHttpClient


public abstract class BaseRestClientImpl <T : AnyRestClient<T>, R : HttpRequestBase> (private val baseUrl: String) : AnyRestClient<T> {
    class object {
        private val LOG = LoggerFactory.getLogger("at.cpickl.grotlin.restclient.BaseRestClientImpl")
    }

    private val headers = hashMapOf<String, String>()
    private val queryParams = hashMapOf<String, String>()

    private fun client(): CloseableHttpClient {
        val builder = HttpClients.custom()
                .setSSLSocketFactory(SSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
        if (System.getProperty("user.name") == "s6917" && !baseUrl.contains("localhost")) {
            LOG.info("Configuring sIT proxy.")
            builder.setProxy(HttpHost("proxy-sd.s-mxs.net", 8080))
        }
        return builder.build()
    }

    override public fun url(endpoint: String): RestResponse {
        val request = request()
        request.setURI(URI("${baseUrl}${endpoint}${buildQueryParams()}"))
        request.setHeader("Accept", "application/json")
        headers.forEach { (key, value) -> run {
            LOG.trace("Setting request header '{}' to '{}'.", key, value)
            request.setHeader(key, value)
        } }

        LOG.info("Executing request to: {}", request.getURI())
        val response = client().execute(request)
        return RestResponse(response, request.getURI().toString())
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
        return header(ACCESS_TOKEN_HEADER_NAME, token)
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
