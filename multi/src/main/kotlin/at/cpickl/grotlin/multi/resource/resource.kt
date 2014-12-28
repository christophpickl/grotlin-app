package at.cpickl.grotlin.multi.resource

import com.google.inject.AbstractModule
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import at.cpickl.grotlin.multi.service.Role
import javax.ws.rs.core.Response
import at.cpickl.grotlin.multi.isDebugApp
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

        installFilters()
    }

    private fun installFilters() {
        // TODO tracking disabled, as i seem to be too dumb for google analytics report UI :-/
        //        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
        //            bind(javaClass<TrackingFilter>()) // first track, then secure ;)
        //        }
        bind(javaClass<SecuredFilter>())
    }

    private fun installResources() {
        bind(javaClass<VersionResource>())
        bind(javaClass<UserResource>())
        bind(javaClass<ChannelResource>())
        bind(javaClass<ChannelPresenceResource>())
        bind(javaClass<AdminResource>())
        bind(javaClass<GameResource>())

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

