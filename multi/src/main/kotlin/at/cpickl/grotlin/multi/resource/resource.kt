package at.cpickl.grotlin.multi.resource

import com.google.inject.AbstractModule
import javax.ws.rs.Path
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
import javax.ws.rs.core.Response
import at.cpickl.grotlin.multi.isDebugApp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//import javax.inject.Inject

// https://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html/Validation.html

class ResourceModule : AbstractModule() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ResourceModule>())
    }

    override fun configure() {
        installResources()

        bind(javaClass<FaultExceptionMapper>())
        bind(javaClass<UnrecognizedPropertyExceptionMapper>())

        bind(javaClass<PaginationReader>())
        bind(javaClass<UserReader>())
        bind(javaClass<SecuredFilter>())
    }

    private fun installResources() {
        bind(javaClass<VersionResource>())
        bind(javaClass<UserResource>())
        bind(javaClass<ChannelResource>())
        bind(javaClass<ChannelPresenceResource>())
        bind(javaClass<AdminResource>())

        if (isDebugApp()) {
            LOG.debug("Registering additional test resources because debug app is enabled")
            bind(javaClass<TestResource>())
        }
    }
}

Path("/test")
Produces(MediaType.APPLICATION_JSON)
class TestResource {

    Secured GET Path("/secured")
    fun secured(): Response {
        return Response.ok().build()
    }

    Secured(role = Role.ADMIN) GET Path("/secured_admin")
    fun securedAdmin(): Response {
        return Response.ok().build()
    }

}

Path("/admin")
Produces(MediaType.APPLICATION_JSON)
class AdminResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AdminResource>())
    }

    GET Path("/resetDB")
    fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!", Fault("You shall not pass!", FaultCode.NOT_ALLOWED))
        }
        if (!userService.loadAll().empty) {
            return """{ "success": false}"""
        }
        // password == "foo"
        userService.saveOrUpdate(User("user1", "m@ail1.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.ADMIN))
        userService.saveOrUpdate(User("user2", "m@ail2.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        userService.saveOrUpdate(User("user3", "m@ail3.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        return """{ "success": true}"""
    }

}

class AdminException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault)
