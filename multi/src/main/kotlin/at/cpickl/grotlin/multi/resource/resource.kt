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
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultCode
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status

public class ResourceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionResource>())
        bind(javaClass<AdminResource>())
        bind(javaClass<FaultExceptionMapper>())
    }
}

Path("/admin") public class AdminResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    Path("/resetDB") GET Produces(MediaType.APPLICATION_JSON) public fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!", Fault("You shall not pass!", FaultCode.NOT_ALLOWED))
        }
        userService.saveOrUpdate(User("user1"))
        userService.saveOrUpdate(User("user2"))
        return """{ "success": true}"""
    }

    Path("/users") GET Produces(MediaType.APPLICATION_JSON) public fun getUsers(): Collection<UserRto> {
        return userService.loadAll().map {
            val rto = UserRto()
            rto.name = it.name
            rto
        }
    }
}
class AdminException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault) {
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserRto {
    public var name: String? = null

    override public fun toString() = "UserRto[name='${name}']"
}
