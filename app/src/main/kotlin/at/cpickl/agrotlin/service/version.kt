package at.cpickl.agrotlin.service

import android.os.AsyncTask
import at.cpickl.agrotlin.Logg
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import org.codehaus.jackson.map.ObjectMapper
import org.apache.http.util.EntityUtils
import at.cpickl.agrotlin.SwirlException
import at.cpickl.grotlin.restclient.RestClient


// class Params, class Progress, class Result
class VersionHttpRequest(private val resultHandler: (VersionRto) -> Unit,
                         private val exceptionHandler: (Exception) -> Unit)
: AsyncTask<Void, Void, VersionRto>() {
    class object {
        private val LOG: Logg = Logg(javaClass.getSimpleName())
    }

    // TODO inject that thing! make it configurable. inject provider
    private val swirlEngineUrl: String;
    {
        val user = System.getProperty("user.name")
        if (user == "s6917") {
//            swirlEngineUrl = "http://10.18.101.204:8888"
            swirlEngineUrl = "http://http://swirl-engine.appspot.com"
        } else {
            swirlEngineUrl = "http://10.0.1.12:8888"
        }
    }

    private var thrown: Exception? = null

    // http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
    override fun doInBackground(vararg params: Void?): VersionRto? {
        LOG.debug("doInBackground()")
        try {
            val rest = RestClient(swirlEngineUrl) // TODO do not instantiate manually, let inject (requires factory)
            val response = rest.get().url("/version")
            val c = DefaultHttpClient()

            return response.unmarshallTo(javaClass<VersionRto>())
        } catch (e: Exception) {
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
