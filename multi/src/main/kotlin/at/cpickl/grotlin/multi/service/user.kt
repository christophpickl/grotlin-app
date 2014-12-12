package at.cpickl.grotlin.multi.service

import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import java.security.MessageDigest
import java.util.UUID
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.resource.Pagination
import at.cpickl.grotlin.multi.resource.paginate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

fun main(args: Array<String>) {
    println(hash("foo"))
    println(hash("foo"))
}

trait UserService {
    fun saveOrUpdate(user: User)
    fun loadAll(pagination: Pagination = Pagination.ALL): Collection<User>
    /** Throws exception on invalid login. */
    fun login(username: String, password: String): User
    fun logout(user: User)

    fun userByToken(token: String): User?

    // only used by fake access token support
    fun userByName(name: String): User?
}

// https://code.google.com/p/objectify-appengine/
class ObjectifyUserService [Inject] (private val idGenerator: IdGenerator) : UserService {

    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ObjectifyUserService>());
        {
            ObjectifyService.register(javaClass<UserDbo>())
        }
    }
    override fun saveOrUpdate(user: User) {
        LOG.info("saveOrUpdate(user)")
        save(user)
    }

    override fun loadAll(pagination: Pagination): Collection<User> {
        LOG.info("loadAll(pagination=${pagination})")
        return ObjectifyService.ofy().load().type(javaClass<UserDbo>()).paginate(pagination).list().map { it.toUser() }
    }

    override fun userByToken(token: String): User? {
        LOG.info("userByToken(token)")
        return ObjectifyService.ofy().load().type(javaClass<UserDbo>()).filter({ it.accessToken == token }).first?.toUser()
    }

    override fun userByName(name: String): User? {
        LOG.info("userByName(name='${name}')")
        return ObjectifyService.ofy().load().type(javaClass<UserDbo>()).filter({ it.name == name }).first?.toUser()
    }

    override fun login(username: String, password: String): User {
        LOG.info("login(username=${username}, password)")
        val foundUser = ObjectifyService.ofy().load().type(javaClass<UserDbo>()).id(username).now()
        if (foundUser == null) {
            throw LoginException("No user found by username '${username}'!", Fault("Invalid username/password", FaultCode.INVALID_CREDENTIALS))
        }
        val user = foundUser.toUser()
        if (user.password != password) {
            throw LoginException("Invalid password for user '${username}'!", Fault("Invalid username/password", FaultCode.INVALID_CREDENTIALS))
        }
        user.accessToken = idGenerator.generate()
        save(user) // update token
        return user
    }

    override fun logout(user: User) {
        LOG.info("logout(user=${user})")
        if (user.accessToken == null) {
            throw RuntimeException("Can not logout a not logged in user: ${user}")
        }
        val result = ObjectifyService.ofy().load().type(javaClass<UserDbo>()).filter { it.accessToken == user.accessToken }
        if (result.size > 1) {
            throw TechException("There were more than one entries (${result.size}) found for token '${user.accessToken}'!", "Invalid token")
        }
        if (result.size == 0) {
            throw FaultException("No user found with token '${user.accessToken}'!", Status.BAD_REQUEST, Fault("No user session found", FaultCode.INVALID_LOGOUT))
        }
        val found = result.first()
        found.accessToken = null
        save(found)
    }

    private fun save(user: User) {
        save(toDbo(user))
    }
    private fun save(user: UserDbo) {
        LOG.debug("save(user=${user})")
        ObjectifyService.ofy().save().entity(user).now()
    }

    private fun toDbo(user: User): UserDbo {
        val dbo = UserDbo()
        dbo.name = user.name
        dbo.email = user.email
        dbo.password = user.password
        dbo.role = user.role
        dbo.accessToken = user.accessToken
        return dbo
    }

}

class LoginException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault)

data class User(
        val name: String,
        val email: String,
        val password: String,
        val role: Role,
        var accessToken: String? = null)

enum class Role(val label: String, private val level: Int) {
    USER: Role("User", 40)
    ADMIN: Role("Admin", 80)

    fun isAtLeast(other: Role): Boolean = this.level >= other.level
}

Entity data class UserDbo() {
    Id var name: String? = null
    var email: String? = null
    var password: String? = null
    var role: Role? = null
    var accessToken: String? = null

    fun toUser(): User = User(name!!, email!!, password!!, role!!, accessToken)
}

// TODO move this to the central game logic module (use it on server side and within android)
fun hash(input: String): String {
    // http://www.rgagnon.com/javadetails/java-0400.html
    val digest = MessageDigest.getInstance("SHA-1")
    digest.reset()
    digest.update(input.getBytes())
    val bytes = digest.digest()

    val sb = StringBuffer(bytes.size * 2)
    for (b in bytes) {
        val masked = b.toInt() and 0xff
        if (masked < 16) {
            sb.append('0')
        }
        sb.append(Integer.toHexString(masked))
    }
    return sb.toString().toUpperCase()
}
