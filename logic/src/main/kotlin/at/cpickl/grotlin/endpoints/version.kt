package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import com.google.common.base.MoreObjects
import at.cpickl.grotlin.restclient.RestClient


// make it a bean with injected "@ServerUrl baseUrl"
class VersionClient(private val baseUrl: String) {
    fun get(): VersionRto {
        return RestClient(baseUrl).get().url("/version").unmarshallTo(javaClass<VersionRto>())
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class VersionRto(
        var artifactVersion: String? = null,
        var buildDate: String? = null) {
    class object {
        fun build(artifactVersion: String, buildDate: String) = VersionRto(artifactVersion, buildDate)
    }
}
