package at.cpickl.grotlin.multi.resource

import com.google.inject.AbstractModule
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.ws.rs.Path
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.ServiceModule
import at.cpickl.grotlin.multi.service.UserService
import at.cpickl.grotlin.multi.service.User

public class ResourceModule : AbstractModule() {
    override fun configure() {
        install(ServiceModule())
        bind(javaClass<VersionResource>())
        bind(javaClass<AdminResource>())
    }
}

Path("/admin") public class AdminResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    Path("/resetDB") GET Produces(MediaType.TEXT_PLAIN) public fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!")
        }
        userService.saveOrUpdate(User("user1"))
        userService.saveOrUpdate(User("user2"))
        return "DONE"
    }
    Path("/users") GET Produces(MediaType.APPLICATION_JSON) public fun getUsers(): Collection<UserRto> {
        return userService.loadAll().map {
            val rto = UserRto()
            rto.name = it.name
            rto
        }
    }
}
class AdminException(message: String) : RuntimeException(message) {
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserRto {
    public var name: String? = null

    override public fun toString() = "UserRto[name='${name}']"
}
