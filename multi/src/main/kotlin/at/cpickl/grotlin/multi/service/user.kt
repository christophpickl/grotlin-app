package at.cpickl.grotlin.multi.service

import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import java.security.MessageDigest
import java.util.logging.Logger
import com.googlecode.objectify.Key

fun main(args: Array<String>) {
    println(hash("foo"))
    println(hash("foo"))
}

trait UserService {
    fun saveOrUpdate(user: User)
    fun loadAll(): Collection<User>
    fun login(username: String, password: String): User?
}

class ObjectifyUserService : UserService {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName());
        {
            ObjectifyService.register(javaClass<UserDbo>())
        }
    }
    override public fun saveOrUpdate(user: User) {
        LOG.info("saveOrUpdate(user)")
        ObjectifyService.ofy().save().entity(toDbo(user)).now()
    }

    override public fun loadAll(): Collection<User> {
        LOG.info("loadAll()")
        return ObjectifyService.ofy().load().type(javaClass<UserDbo>()).list().map { fromDbo(it) }
    }
    override public fun login(username: String, password: String): User? {
        LOG.info("login(username=${username}, password)")
        val foundUser = ObjectifyService.ofy().load().type(javaClass<UserDbo>()).id(username).now()
        if (foundUser == null) {
            return null
        }

        val user = fromDbo(foundUser)
        if (user.password != password) {
            return null
        }
        return user
    }

    private fun toDbo(user: User): UserDbo {
        val dbo = UserDbo()
        dbo.name = user.name
        dbo.email = user.email
        dbo.password = user.password
        return dbo
    }

    private fun fromDbo(dbo: UserDbo): User = User(dbo.name!!, dbo.email!!, dbo.password!!)
}

data public class User(public val name: String, public val email: String, public val password: String)

Entity data class UserDbo() {
    Id public var name: String? = null
    public var email: String? = null
    public var password: String? = null
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
