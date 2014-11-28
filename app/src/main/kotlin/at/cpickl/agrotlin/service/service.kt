package at.cpickl.agrotlin.service

import android.widget.Toast
import javax.inject.Inject
import android.os.Vibrator

trait LoginService {
    fun login(username: String, password: String): Boolean
}


class HttpLoginService : LoginService {
    class object {
        private val LOG: at.cpickl.agrotlin.Logg = at.cpickl.agrotlin.Logg("HttpLoginService")
    }
    override fun login(username: String, password: String): Boolean {
        LOG.info("login(username=${username}, password)")
        return true
    }

}


trait VibrateService {
    fun vibrate(milliseconds: Long = 500)
}

class AndroidVibrateService : VibrateService {
    // my girlfriend's one is better! peace.
    Inject var vibrator: Vibrator? = null

    override fun vibrate(milliseconds: Long) {
        vibrator!!.vibrate(milliseconds)
    }

}