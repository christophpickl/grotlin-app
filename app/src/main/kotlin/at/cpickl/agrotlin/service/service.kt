package at.cpickl.agrotlin.service

import android.widget.Toast
import javax.inject.Inject
import android.os.Vibrator
import javax.inject.Singleton
import org.slf4j.LoggerFactory
import android.content.Context
import android.net.ConnectivityManager

trait LoginService {
    fun login(username: String, password: String): Boolean
}

class HttpLoginService : LoginService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AndroidVibrateService>())
    }
    override fun login(username: String, password: String): Boolean {
        LOG.info("login(username=${username}, password)")
        return true
    }
}


trait VibrateService {
    fun vibrate(milliseconds: Long = 500)
}

Singleton class AndroidVibrateService [Inject] (private val vibrator: Vibrator) : VibrateService {
    // my girlfriend's one is better! peace out.
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AndroidVibrateService>())
    }

    override fun vibrate(milliseconds: Long) {
        LOG.debug("vibrate(milliseconds=${milliseconds})")
        vibrator.vibrate(milliseconds)
    }

}

trait AndroidOs {
    fun isNetworkAvailable(context: Context): Boolean
}


class AndroidOsImpl: AndroidOs {
    override fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}