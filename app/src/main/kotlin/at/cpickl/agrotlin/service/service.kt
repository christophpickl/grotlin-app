package at.cpickl.agrotlin.service

import android.widget.Toast
import javax.inject.Inject
import android.os.Vibrator
import at.cpickl.agrotlin.Logg
import javax.inject.Singleton

trait LoginService {
    fun login(username: String, password: String): Boolean
}

class HttpLoginService : LoginService {
    class object {
        private val LOG: Logg = Logg("HttpLoginService")
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
        private val LOG: Logg = Logg("AndroidVibrateService");
    }

    override fun vibrate(milliseconds: Long) {
        LOG.debug("vibrate(milliseconds=${milliseconds})")
        vibrator.vibrate(milliseconds)
    }

}