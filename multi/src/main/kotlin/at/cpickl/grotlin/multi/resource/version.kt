package at.cpickl.grotlin.multi.resource

import javax.inject.Inject
import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.VersionService
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.VersionRto

Path("/version") class VersionResource [Inject] (
        private val versionService: VersionService
) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<VersionResource>())
    }

    GET Produces(MediaType.APPLICATION_JSON) fun getVersion(): VersionRto {
        LOG.debug("getVersion()")

        val version = versionService.load()
        return VersionRto.build(version.artifactVersion, version.buildDate)
    }
}
