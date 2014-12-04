package at.cpickl.agrotlin.service

import at.cpickl.agrotlin.Logg
import org.codehaus.jackson.map.ObjectMapper
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import org.apache.http.util.EntityUtils
import at.cpickl.agrotlin.SwirlException

/*
class RestClient {
    class object {
        private val LOG: Logg = Logg(javaClass.getSimpleName())
    }

    private val mapper = ObjectMapper()
    // TODO inject that thing! make it configurable
    private val swirlEngineUrl = "http://10.0.1.12:8888"

    public fun <T> get(endpoint: String, type: Class<T>): T {

        val client = DefaultHttpClient()
        val get = HttpGet("${swirlEngineUrl}${endpoint}")
        get.setHeader("Accept", "application/json")

        val response = client.execute(get)
        LOG.debug("response.getStatusLine().getStatusCode()=" + response.getStatusLine().getStatusCode())
        val statusCode = response.getStatusLine().getStatusCode()

        // http://stackoverflow.com/questions/6218143/how-to-send-post-request-in-json-using-httpclient
        if (statusCode != HttpStatus.SC_OK) {
            throw RestException("GET ${get.getURI().toURL()} failed with status code: ${statusCode}")
        }
        // httpPost.setEntity(new StringEntity(jsonAsString));
        //                    response.getEntity().writeTo(entityOut)
        val jsonAsString = EntityUtils.toString(response.getEntity())
        LOG.trace("Server call responded with body: '${jsonAsString}'")
        return mapper.readValue(jsonAsString, type)
    }
}

class RestException(message: String) : SwirlException(message) {

}
*/