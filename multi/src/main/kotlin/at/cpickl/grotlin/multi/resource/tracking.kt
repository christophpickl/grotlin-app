package at.cpickl.grotlin.multi.resource

import javax.ws.rs.ext.Provider
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.TrackingService
import javax.ws.rs.container.ContainerRequestFilter
import org.slf4j.LoggerFactory
import javax.ws.rs.container.ContainerRequestContext
import at.cpickl.grotlin.multi.service.PageTrack

Provider class TrackingFilter [Inject] (private val trackingService: TrackingService) : ContainerRequestFilter {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<TrackingFilter>())
    }

    override fun filter(requestContext: ContainerRequestContext) {
        LOG.trace("filter(request)")
        // TODO store "some" client ID
        trackingService.track(PageTrack(requestContext.getUriInfo().getPath(), requestContext.getMethod()))
        // TODO also track the outcome of a request (wrap around the whole request-response)
    }

}
