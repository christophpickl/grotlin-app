package at.cpickl.agrotlin.service.engine

import org.slf4j.LoggerFactory
import at.cpickl.agrotlin.service.AndroidVibrateService
import at.cpickl.agrotlin.SwirlException
import at.cpickl.grotlin.endpoints.UserClient
import at.cpickl.grotlin.endpoints.LoginRequestRto
import javax.inject.Inject
import at.cpickl.grotlin.endpoints.LoginClientException


trait UserEngine {
    /** @return access token or throws LoginException */
    fun login(username: String, password: String, resultHandler: (String) -> Unit)
}

class RestfulUserEngine [Inject] (private val userClient: UserClient) : UserEngine {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AndroidVibrateService>())
    }
    override fun login(username: String, password: String, resultHandler: (String) -> Unit) {
        ServerAsyncTask({ userClient.login(LoginRequestRto.build(username, password)).accessToken!! }, resultHandler).execute()
    }

//    override fun login(, ): String {
//        ServerAsyncTask({ versionClient.get()}, resultHandler, exceptionHandler).execute()
//
//        LOG.info("login(username=${username}, password)")
//        try {
//            val client: UserClient = UserClient("TODO!!!! inject") // TODO fixme here!!!
//            val response = client.login(LoginRequestRto.build(username, password))
//            return response.accessToken!!
//        } catch(e: ClientFaultException) {
//            // TODO process this here: e.fault
//            throw LoginException("Could not log in user '${username}'.", e)
//        }
//    }
}

