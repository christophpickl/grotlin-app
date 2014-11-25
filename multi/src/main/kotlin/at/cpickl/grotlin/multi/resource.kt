package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import javax.ws.rs.Path
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.util.logging.Level
import javax.inject.Inject
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

public class ResourceModule : AbstractModule() {
    override fun configure() {
        install(ServiceModule())
        bind(javaClass<VersionResource>())
    }
}

Path("/version") public class VersionResource [Inject] (private val repo: Repo) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    GET Produces(MediaType.APPLICATION_JSON, "application/vnd.swirl.version+json") public fun getVersion(): Version {
        LOG.log(Level.FINER, "getVersion()")
        return Version.build("1.0-SNAPSHOT", "2042-12-12")
        //        return Response.status(200).entity("version=${repo.save("guice")}").build();
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class Version {
    class object {
        public fun build(artifactVersion: String, buildDate: String): Version {
            val version = Version()
            version.artifactVersion = artifactVersion
            version.buildDate = buildDate
            return version
        }
    }
    public var artifactVersion: String? = null
    public var buildDate: String? = null
}
