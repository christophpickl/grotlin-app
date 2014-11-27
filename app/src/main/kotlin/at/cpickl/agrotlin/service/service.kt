package at.cpickl.agrotlin.service

import android.widget.Toast

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