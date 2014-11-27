package at.cpickl.agrotlin.service

import android.os.AsyncTask
import at.cpickl.agrotlin.Logg
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import org.codehaus.jackson.map.ObjectMapper
import org.apache.http.util.EntityUtils


// class Params, class Progress, class Result
class VersionHttpRequest(private val resultHandler: (VersionRto) -> Unit,
                         private val exceptionHandler: (Exception) -> Unit)
: AsyncTask<Void, Void, VersionRto>() {
    class object {
        private val LOG: Logg = Logg(javaClass.getSimpleName())
    }
    // TODO inject that thing! make it configurable
    private val swirlEngineUrl = "http://10.0.1.12:8888"
    private var thrown: Exception? = null

    // http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
    override fun doInBackground(vararg params: Void?): VersionRto? {
        LOG.debug("doInBackground()")
        try {

            val client = DefaultHttpClient()
            val get = HttpGet("${swirlEngineUrl}/version")
            get.setHeader("Accept", "application/json")

            val response = client.execute(get)
            LOG.debug("response.getStatusLine().getStatusCode()=" + response.getStatusLine().getStatusCode())
            val statusCode = response.getStatusLine().getStatusCode()

            // http://stackoverflow.com/questions/6218143/how-to-send-post-request-in-json-using-httpclient
            if (statusCode == HttpStatus.SC_OK) {
                // httpPost.setEntity(new StringEntity(jsonAsString));
                val mapper = ObjectMapper()
                val entityOut = null // OutputStream
                //                    response.getEntity().writeTo(entityOut)
                val jsonAsString = EntityUtils.toString(response.getEntity())
                LOG.trace("Server call responded with body: '${jsonAsString}'")
                return mapper.readValue(jsonAsString, javaClass<VersionRto>())
            } else {
                // haha, nice hack ;)
                thrown = RuntimeException("GET ${get.getURI().toURL()} failed with status code: ${statusCode}")
                return null
            }
        } catch(e: Exception) {
            thrown = e
            return null
        }
    }
    override fun onPostExecute(result: VersionRto?) {
        if (thrown != null) {
            exceptionHandler(thrown!!)
        } else {
            resultHandler(result!!)
        }
    }

}

class VersionRto {
    // TODO use common module just containing Rto classes! avoid copy'n'paste
    public var artifactVersion: String? = null
    public var buildDate: String? = null
}
