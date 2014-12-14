package at.cpickl.agrotlin.service.server

import javax.inject.Inject
import android.os.AsyncTask
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.VersionRto
import at.cpickl.grotlin.endpoints.VersionClient


/** Is actually a provider factory. */
class VersionRequestor [Inject] (private val versionClient: VersionClient) {
    fun request(resultHandler: (VersionRto) -> Unit, exceptionHandler: (Exception) -> Unit) {
        VersionHttpRequest(versionClient, resultHandler, exceptionHandler).execute()
    }
}

// class Params, class Progress, class Result
class VersionHttpRequest(private val versionClient: VersionClient,
                         private val resultHandler: (VersionRto) -> Unit,
                         private val exceptionHandler: (Exception) -> Unit)
: AsyncTask<Void, Void, VersionRto>() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<VersionHttpRequest>())
    }

    private var thrown: Exception? = null

    // http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
    override fun doInBackground(vararg params: Void?): VersionRto? {
        LOG.debug("doInBackground()")
        try {
            // TODO do not instantiate manually, let inject (requires factory)
            val version = versionClient.get()
            LOG.debug("Got version: ${version}")
            return version
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

