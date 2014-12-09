package at.cpickl.grotlin.multi.service

import javax.inject.Inject
import at.cpickl.grotlin.multi.isDebugApp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthUserService [Inject] (
        private val userService: UserService,
        private val fakeUserReader: FakeUserReader
) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AuthUserService>())
    }
    fun authUser(token: String): User? {
        val foundUser = userService.userByToken(token)
        // TODO LATER ... re-enable debug-app check again when going to PROD some time later!
//        if (!isDebugApp()) {
//            return foundUser
//        }
        if (foundUser != null) {
            return foundUser
        }
        LOG.debug("Looking for fake access token because debug app is enabled")
        if (!fakeUserReader.isFakeToken(token)) {
            return null
        }
        return fakeUserReader.read(token)
    }
}

class FakeUserReader [Inject] (private val userService: UserService) {

    private val nameByToken = mapOf(Pair("1", "user1"), Pair("2", "user2"))

    fun isFakeToken(token: String): Boolean = nameByToken.containsKey(token)
    fun read(token: String): User {
        val name = nameByToken.get(token)!!
        val user = userService.userByName(name)
        if (user == null) {
            throw IllegalStateException("Could not find user by name '${name}' for fake token '${token}'! (try resetDB)")
        }
        user.accessToken = token
        userService.saveOrUpdate(user)
        return user
    }

}
