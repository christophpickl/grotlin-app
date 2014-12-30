package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.UserService
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import at.cpickl.grotlin.multi.service.User
import javax.ws.rs.core.Response
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.Role
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.LoginRequestRto
import at.cpickl.grotlin.endpoints.UserResponseRto
import at.cpickl.grotlin.endpoints.LoginResponseRto
import javax.validation.Valid

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

    /** Lists all user profiles (admin only). */
    Secured(Role.ADMIN) GET Path("/")
    Produces(MediaType.APPLICATION_JSON)
    fun getUsers(pagination: Pagination): Collection<UserResponseRto> {
        LOG.info("getUsers(pagination=${pagination})")
        return userService.loadAll(pagination).map(userTransformer)
    }

    // on invalid login: returns FORBIDDEN(403), FaultCode.INVALID_CREDENTIALS
    POST Path("/login")
    Consumes(MediaType.APPLICATION_JSON) Produces(MediaType.APPLICATION_JSON)
    fun login(Valid loginRequest: LoginRequestRto): LoginResponseRto {
        LOG.info("login(loginRequest=${loginRequest})")
        val user = userService.login(loginRequest.username!!, loginRequest.password!!)
        return LoginResponseRto.build(user.accessToken!!)
    }

    Secured POST Path("/logout")
    fun logout(user: User): Response {
        LOG.info("logout(user=${user})")
        userService.logout(user)
        return Response.status(Response.Status.NO_CONTENT).build()
    }

    /** Returns the private profile for "this" user. */
    Secured GET Path("/profile")
    Produces(MediaType.APPLICATION_JSON)
    fun getProfile(user: User): UserResponseRto {
        LOG.info("getProfile(user=${user})")
        return userTransformer(user)
    }

}
