package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import java.util.Properties


public class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionService>()).toInstance(PropertiesVersionService("/swirl.config.properties"))
    }
}

class PropertiesVersionService(private val propertiesClasspath: String) : VersionService {
    override fun load(): Version {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream(propertiesClasspath))
        val artifactVersion = properties.get("artifact.version") as String
        val buildDate = properties.get("build.date") as String
        return Version(artifactVersion, buildDate)
    }
}

trait VersionService {
    fun load(): Version
}

data class Version(public val artifactVersion: String, public val buildDate: String)
