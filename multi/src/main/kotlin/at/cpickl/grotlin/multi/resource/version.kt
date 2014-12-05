package at.cpickl.grotlin.multi.resource

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.inject.Inject
import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.VersionService
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class VersionRto {
    class object {
        fun build(artifactVersion: String, buildDate: String): VersionRto {
            val version = VersionRto()
            version.artifactVersion = artifactVersion
            version.buildDate = buildDate
            return version
        }
    }
    var artifactVersion: String? = null
    var buildDate: String? = null

    override fun toString() = "VersionRto[artifactVersion='${artifactVersion}', buildDate='${buildDate}']"

}

Path("/version") class VersionResource [Inject] (private val versionService: VersionService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<VersionResource>())
    }

    // Produces(MediaType.APPLICATION_JSON, "application/vnd.swirl.version+json")
    GET Produces(MediaType.APPLICATION_JSON) fun getVersion(): VersionRto {
        LOG.debug("getVersion()")
        val version = versionService.load()
        return VersionRto.build(version.artifactVersion, version.buildDate)
        //        return Response.status(200).entity("version=${repo.save("guice")}").build();
    }
}
