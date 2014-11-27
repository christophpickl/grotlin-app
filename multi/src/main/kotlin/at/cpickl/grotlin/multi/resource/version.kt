package at.cpickl.grotlin.multi.resource

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.inject.Inject
import javax.ws.rs.Path
import at.cpickl.grotlin.multi.service.VersionService
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.util.logging.Level

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class VersionRto {
    class object {
        public fun build(artifactVersion: String, buildDate: String): VersionRto {
            val version = VersionRto()
            version.artifactVersion = artifactVersion
            version.buildDate = buildDate
            return version
        }
    }
    public var artifactVersion: String? = null
    public var buildDate: String? = null

    override public fun toString() = "VersionRto[artifactVersion='${artifactVersion}', buildDate='${buildDate}']"

}

Path("/version") public class VersionResource [Inject] (private val versionService: VersionService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    // Produces(MediaType.APPLICATION_JSON, "application/vnd.swirl.version+json")
    GET Produces(MediaType.APPLICATION_JSON) public fun getVersion(): VersionRto {
        LOG.log(Level.FINER, "getVersion()")
        val version = versionService.load()
        return VersionRto.build(version.artifactVersion, version.buildDate)
        //        return Response.status(200).entity("version=${repo.save("guice")}").build();
    }
}
