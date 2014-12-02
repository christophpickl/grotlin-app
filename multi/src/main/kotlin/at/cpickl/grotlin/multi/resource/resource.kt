package at.cpickl.grotlin.multi.resource

import com.google.inject.AbstractModule
import javax.ws.rs.Path
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import at.cpickl.grotlin.multi.service.UserService
import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultCode
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import javax.inject.Inject
import org.jboss.resteasy.spi.interception.PreProcessInterceptor
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerRequestContext
import org.jboss.resteasy.core.ServerResponse
import org.jboss.resteasy.core.Headers
import at.cpickl.grotlin.multi.service.Role
import org.jboss.resteasy.core.ResourceMethodInvoker
import javax.ws.rs.Consumes
import javax.ws.rs.ext.MessageBodyReader
import java.lang.reflect.Type
import javax.ws.rs.core.MultivaluedMap
import java.io.InputStream

//import javax.inject.Inject

// https://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html/Validation.html

public class ResourceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionResource>())
        bind(javaClass<UserResource>())
        bind(javaClass<ChannelResource>())
        bind(javaClass<ChannelPresenceResource>())
        bind(javaClass<AdminResource>())

        bind(javaClass<FaultExceptionMapper>())
        bind(javaClass<UnrecognizedPropertyExceptionMapper>())
//        bind(javaClass<GeneralExceptionMapper>())

        bind(javaClass<PaginationReader>())
        bind(javaClass<SecuredFilter>())
        bind(javaClass<UserReader>())
    }
}

Provider Consumes(MediaType.WILDCARD) public class UserReader [Inject] (private val userService: UserService) : MessageBodyReader<User> {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    override fun isReadable(type: Class<out Any?>?, genericType: Type?, annotations: Array<out Annotation>?, mediaType: MediaType?): Boolean {
        return type == javaClass<User>()
    }

    override fun readFrom(type: Class<User>?, genericType: Type?, annotations: Array<out Annotation>?, mediaType: MediaType?, httpHeaders: MultivaluedMap<String, String>?, entityStream: InputStream?): User? {
        // or read from query param "access_token" and support fake tokens
        var token: String? = httpHeaders!!.getFirst("token")
        if (token == null) {
            return null
        }
        return userService.userByToken(token!!)
    }
}

// PreMatching?
/** see http://howtodoinjava.com/2013/07/25/jax-rs-2-0-resteasy-3-0-2-final-security-tutorial/ */
Provider Secured class SecuredFilter [Inject] (private val userService: UserService) : ContainerRequestFilter  {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    override fun filter(requestContext: ContainerRequestContext) {
        LOG.fine("secured working for context: method=${requestContext.getMethod()} URI=${requestContext.getUriInfo()}")
        var token: String? = requestContext.getHeaderString("token")
        if (token == null) {
            requestContext.abortWithUnauthorized()
            return
        }
        val maybeUser = userService.userByToken(token!!)
        if (maybeUser == null) {
            requestContext.abortWithUnauthorized()
            return
        }
        val methodInvoker = requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker")
        if (methodInvoker !is ResourceMethodInvoker) {
            throw RuntimeException("Expected property to be of type ResourceMethodInvoker but was: ${methodInvoker.javaClass.getName()}")
        }
        val annotation = methodInvoker.getMethod().getAnnotation(javaClass<Secured>())
        if (!maybeUser.role.isAtLeast(annotation.role())) {
            LOG.fine("User has role '${maybeUser.role}' but role '${annotation.role()}' was required!")
            requestContext.abortWithForbidden()
            return
        }
    }

}

fun ContainerRequestContext.abortWithUnauthorized() {
    abortWith(ServerResponse(FaultRto.build("Authentication required to access this resource!", FaultCode.UNAUTHORIZED), 401, Headers()))
}

fun ContainerRequestContext.abortWithForbidden() {
    abortWith(ServerResponse(FaultRto.build("You are not authorized to access this resource!", FaultCode.FORBIDDEN), 403, Headers()))
}

Path("/admin")
Produces(MediaType.APPLICATION_JSON)
public class AdminResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    GET Path("/resetDB")
    public fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!", Fault("You shall not pass!", FaultCode.NOT_ALLOWED))
        }
        // password == "foo"
        userService.saveOrUpdate(User("user1", "m@ail1.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.ADMIN))
        userService.saveOrUpdate(User("user2", "m@ail2.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        userService.saveOrUpdate(User("user3", "m@ail3.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        return """{ "success": true}"""
    }

}

class AdminException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault)
