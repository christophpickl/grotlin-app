package at.cpickl.grotlin.multi.service

import java.util.Properties

data class Version(public val artifactVersion: String, public val buildDate: String)

trait VersionService {
    fun load(): Version
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