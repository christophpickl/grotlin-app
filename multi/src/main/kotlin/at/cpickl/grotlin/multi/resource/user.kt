package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.UserService
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
import at.cpickl.grotlin.multi.service.Role
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//import javax.inject.Inject

Path("/users")
class UserResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<UserResource>())
    }

    private val userTransformer: (User) -> UserResponseRto = {(user) ->
        val rto = UserResponseRto()
        rto.name = user.name
        rto.role = user.role.label
        rto
    }

    Secured(Role.ADMIN) GET Path("/")
    Produces(MediaType.APPLICATION_JSON)
    fun getUsers(pagination: Pagination): Collection<UserResponseRto> {
        LOG.debug("getUsers(pagination=${pagination})")
        return userService.loadAll(pagination).map(userTransformer)
    }

    POST Path("/login")
    Consumes(MediaType.APPLICATION_JSON) Produces(MediaType.APPLICATION_JSON)
    fun login(login: LoginRequestRto): LoginResponseRto {
        val user = userService.login(login.username!!, login.password!!)
        val rto = LoginResponseRto()
        rto.accessToken = user.accessToken
        return rto
    }

    Secured POST Path("/logout")
    fun logout(user: User): Response {
        userService.logout(user)
        return Response.status(Response.Status.NO_CONTENT).build()
    }

    Secured GET Path("/profile")
    Produces(MediaType.APPLICATION_JSON)
    fun getProfile(user: User): UserResponseRto {
        return userTransformer(user)
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginRequestRto {
    class object {
        fun build(username: String, password: String): LoginRequestRto {
            val rto = LoginRequestRto()
            rto.username = username
            rto.password = password
            return rto
        }
    }
    XmlElement(required = true, nillable = false)
    var username: String? = null
    XmlElement(required = true, nillable = false)
    var password: String? = null

    override fun toString() = "LoginRto[username='${username}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginResponseRto {
    var accessToken: String? = null
    override fun toString() = "LoginResultRto[accessToken='${accessToken}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserResponseRto {
    var name: String? = null
    var role: String? = null
    override fun toString() = "UserRto[name='${name}']"
}
