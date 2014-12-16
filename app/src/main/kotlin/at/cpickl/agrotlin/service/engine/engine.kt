package at.cpickl.agrotlin.service.engine

import org.slf4j.LoggerFactory
import javax.inject.Inject
import at.cpickl.grotlin.endpoints.VersionClient
import android.os.AsyncTask
import at.cpickl.grotlin.endpoints.VersionRto
import at.cpickl.grotlin.endpoints.ClientFaultException
import at.cpickl.grotlin.endpoints.UserClient
import at.cpickl.grotlin.endpoints.LoginRequestRto
import at.cpickl.agrotlin.SwirlException
import at.cpickl.agrotlin.service.AndroidVibrateService

val DEFAULT_ENGINE_EXCEPTION_HANDLER: (Exception) -> Unit = { e -> throw e }//throw EngineException("Engine request failed!", e) }

class EngineException(message: String, cause: Exception) : SwirlException(message, cause)

class ServerAsyncTask<R/*esult*/> (
        /** executed on background thread */
        private val clientFun: () -> R,
        /** executed on UI thread */
        private val resultHandler: (R) -> Unit,
        private val exceptionHandler: (Exception) -> Unit = DEFAULT_ENGINE_EXCEPTION_HANDLER
)

// class Params, class Progress, class Result
: AsyncTask<Void, Void, R>() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ServerAsyncTask<*>>())
    }

    private var thrown: Exception? = null

    // http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
    override fun doInBackground(vararg params: Void?): R? {
        LOG.debug("doInBackground()")
        try {
            val result = clientFun()
            LOG.debug("Got client result: ${result}")
            return result
        } catch (e: Exception) {
            thrown = e
            return null
        }
    }
    override fun onPostExecute(result: R?) {
        if (thrown != null) {
            exceptionHandler(thrown!!)
        } else {
            resultHandler(result!!)
        }
    }

}




/** Is actually a provider factory. */
class VersionEngine [Inject] (private val versionClient: VersionClient) {
    fun getVersion(resultHandler: (VersionRto) -> Unit) {
        ServerAsyncTask({ versionClient.get()}, resultHandler).execute()
    }
}
