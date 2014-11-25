package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import java.util.Properties


public class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<Repo>()).toInstance(MyRepo())
    }
}

class MyRepo : Repo {
    override fun loadVersion(): Version {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("/swirl.config.properties"))
        val artifactVersion = properties.get("artifact.version") as String
        val buildDate = properties.get("build.date") as String
        return Version(artifactVersion, buildDate)
    }
}

trait Repo {
    fun loadVersion(): Version
}

data class Version(public val artifactVersion: String, public val buildDate: String)
