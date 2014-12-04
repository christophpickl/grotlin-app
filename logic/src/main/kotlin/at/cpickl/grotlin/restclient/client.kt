package at.cpickl.grotlin.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.map.ObjectMapper
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils
import java.net.URI
import org.codehaus.jackson.JsonProcessingException

public open class RestClient(private val baseUrl: String) {
    fun get(): GetRestClient = GetRestClientImpl(baseUrl)
    fun post(): PostRestClient = PostRestClientImpl(baseUrl)
}

public class RestResponse(private val response: CloseableHttpResponse) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<RestResponse>())
    }

    private val mapper = ObjectMapper()
    val status: Status
    {
        status = Status.byCode(response.getStatusLine().getStatusCode())
    }

    fun <T> unmarshallTo(javaClass: Class<T>): T {
        val entity = response.getEntity()
        val jsonString = EntityUtils.toString(entity)
        try {
            return mapper.readValue(jsonString, javaClass)
        } catch(e: JsonProcessingException) {
            LOG.error("Invalid JSON: '{}'", jsonString)
            throw e
        }
    }

}

public trait AnyRestClient<T : AnyRestClient<T>> {
    public fun header(name: String, value: String): T
    public fun queryParameter(name: String, value: String): T
    public fun accessToken(token: String): T
    public fun url(endpoint: String): RestResponse
}
public trait GetRestClient : AnyRestClient<GetRestClient> {

}
public trait PostRestClient : AnyRestClient<PostRestClient> {
    public fun body(entity: Any): PostRestClient

}
