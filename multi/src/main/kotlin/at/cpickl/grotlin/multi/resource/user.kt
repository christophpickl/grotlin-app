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
import at.cpickl.grotlin.multi.service.User
import javax.ws.rs.core.Response
import javax.inject.Inject
import java.lang.annotation.Retention
import java.lang.annotation.Target

//import javax.inject.Inject

Path("/users")
public class UserResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    private val userTransformer: (User) -> UserResponseRto = {(user) ->
        val rto = UserResponseRto()
        rto.name = user.name
        rto.token = user.token
        rto
    }

    GET Path("/")
    Produces(MediaType.APPLICATION_JSON)
    public fun getUsers(pagination: Pagination): Collection<UserResponseRto> {
        LOG.fine("getUsers(pagination=${pagination})")
        return userService.loadAll(pagination).map(userTransformer)
    }

    POST Path("/login")
    Consumes(MediaType.APPLICATION_JSON) Produces(MediaType.APPLICATION_JSON)
    public fun login(login: LoginRequestRto): LoginResponseRto {
        val user = userService.login(login.username!!, login.password!!)
        val rto = LoginResponseRto()
        rto.token = user.token
        return rto
    }

    POST Path("/logout")
    Consumes(MediaType.APPLICATION_JSON)
    public fun logout(logout: LogoutRequestRto): Response {
        userService.logout(logout.token!!)
        return Response.status(Response.Status.NO_CONTENT).build()
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginRequestRto {
    XmlElement(required = true, nillable = false)
    public var username: String? = null
    XmlElement(required = true, nillable = false)
    public var password: String? = null

    override public fun toString() = "LoginRto[username='${username}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginResponseRto {
    public var token: String? = null
    override public fun toString() = "LoginResultRto[token='${token}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LogoutRequestRto {
    class object {
        public fun build(token: String): LogoutRequestRto {
            val rto = LogoutRequestRto()
            rto.token = token
            return rto
        }
    }
    XmlElement(required = true, nillable = false)
    public var token: String? = null

    override public fun toString() = "LogoutRequestRto[token='${token}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserResponseRto {
    public var name: String? = null
    public var token: String? = null // TODO delete me
    override public fun toString() = "UserRto[name='${name}']"
}
