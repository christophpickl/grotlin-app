package at.cpickl.grotlin.multi.resource

import javax.ws.rs.ext.Provider
import javax.ws.rs.Consumes
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.UserService
import javax.ws.rs.ext.MessageBodyReader
import at.cpickl.grotlin.multi.service.User
import java.lang.reflect.Type
import javax.ws.rs.core.MultivaluedMap
import java.io.InputStream
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerRequestContext
import org.jboss.resteasy.core.ResourceMethodInvoker
import org.jboss.resteasy.core.ServerResponse
import at.cpickl.grotlin.endpoints.FaultCode
import org.jboss.resteasy.core.Headers
import at.cpickl.grotlin.multi.isDebugApp
import at.cpickl.grotlin.multi.service.AuthUserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.FaultRto
import at.cpickl.grotlin.restclient.ACCESS_TOKEN_HEADER_NAME


Provider Consumes(MediaType.WILDCARD) class UserReader [Inject] (
        private val authUserService: AuthUserService
) : MessageBodyReader<User> {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<UserReader>())
    }
    override fun isReadable(type: Class<out Any?>?, genericType: Type?, annotations: Array<out Annotation>?, mediaType: MediaType?): Boolean {
        return type == javaClass<User>()
    }

    override fun readFrom(type: Class<User>?, genericType: Type?, annotations: Array<out Annotation>?, mediaType: MediaType?, httpHeaders: MultivaluedMap<String, String>?, entityStream: InputStream?): User? {
        var token: String? = httpHeaders!!.getFirst(ACCESS_TOKEN_HEADER_NAME)
        if (token == null) {
            return null
        }
        return authUserService.authUser(token!!)
    }
}

// PreMatching?
/** see http://howtodoinjava.com/2013/07/25/jax-rs-2-0-resteasy-3-0-2-final-security-tutorial/ */
Provider Secured class SecuredFilter [Inject] (private val authUserService: AuthUserService) : ContainerRequestFilter  {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<SecuredFilter>())
    }

    override fun filter(requestContext: ContainerRequestContext) {
        LOG.debug("secured working for context: method=${requestContext.getMethod()} URI=${requestContext.getUriInfo()}")
        var token: String? = requestContext.getHeaderString(ACCESS_TOKEN_HEADER_NAME)
        LOG.debug("Token from header '{}' has value: '{}'", ACCESS_TOKEN_HEADER_NAME, token)
        if (token == null) {
            requestContext.abortWithUnauthorized()
            return
        }
        val maybeUser = authUserService.authUser(token!!)
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
            LOG.debug("User has role '${maybeUser.role}' but role '${annotation.role()}' was required!")
            requestContext.abortWithForbidden()
            return
        }
        LOG.debug("User granted access.")
    }

}

fun ContainerRequestContext.abortWithUnauthorized() {
    abortWith(ServerResponse(FaultRto.build("Authentication required to access this resource!", FaultCode.UNAUTHORIZED), 401, Headers()))
}

fun ContainerRequestContext.abortWithForbidden() {
    abortWith(ServerResponse(FaultRto.build("You are not authorized to access this resource!", FaultCode.FORBIDDEN), 403, Headers()))
}
