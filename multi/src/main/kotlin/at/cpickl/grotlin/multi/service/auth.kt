package at.cpickl.grotlin.multi.service

import javax.inject.Inject
import java.util.logging.Logger
import at.cpickl.grotlin.multi.isDebugApp

class AuthUserService [Inject] (
        private val userService: UserService,
        private val fakeUserReader: FakeUserReader
) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    fun authUser(token: String): User? {
        val foundUser = userService.userByToken(token)
        if (!isDebugApp()) {
            return foundUser
        }
        if (foundUser != null) {
            return foundUser
        }
        LOG.fine("Looking for fake access token because debug app is enabled")
        return fakeUserReader.read(token)
    }
}

class FakeUserReader [Inject] (private val userService: UserService) {
    private val nameByToken = mapOf(Pair("1", "user1"), Pair("2", "user2"))
    fun read(token: String): User = userService.userByName(nameByToken.get(token)!!)
}
