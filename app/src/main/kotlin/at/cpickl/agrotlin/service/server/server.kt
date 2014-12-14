package at.cpickl.agrotlin.service.server

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



trait LoginService {
    /** @return access token or throws LoginException */
    fun login(username: String, password: String): String
}

class HttpLoginService : LoginService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AndroidVibrateService>())
    }

    override fun login(username: String, password: String): String {
        LOG.info("login(username=${username}, password)")
        try {
            val client: UserClient = UserClient("TODO!!!! inject") // TODO fixme here!!!
            val response = client.login(LoginRequestRto.build(username, password))
            return response.accessToken!!
        } catch(e: ClientFaultException) {
            // TODO process this here: e.fault
            throw LoginException("Could not log in user '${username}'.", e)
        }
    }
}

class LoginException(message: String, cause: Exception) : SwirlException(message, cause)
