package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.UserService
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlElement
import javax.inject.Inject
//import javax.inject.Inject


Path("/users") public class UserResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    Path("/") GET Produces(MediaType.APPLICATION_JSON)
    public fun getUsers(): Collection<UserRto> {
        return userService.loadAll().map {
            val rto = UserRto()
            rto.name = it.name
            rto
        }
    }
    Path("/login") POST Consumes(MediaType.APPLICATION_JSON) Produces(MediaType.APPLICATION_JSON)
    public fun login(login: LoginRto): LoginResultRto {
        val maybeUser = userService.login(login.username!!, login.password!!)
        val rto = LoginResultRto()
        rto.success = maybeUser != null
        return rto
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginRto {
    XmlElement(required = true, nillable = false)
    public var username: String? = null
    public var password: String? = null
    override public fun toString() = "LoginRto[username='${username}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginResultRto {
    public var success: Boolean? = null
    override public fun toString() = "LoginResultRto[success='${success}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserRto {
    public var name: String? = null
    override public fun toString() = "UserRto[name='${name}']"
}
