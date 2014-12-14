package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import com.google.common.base.MoreObjects
import at.cpickl.grotlin.restclient.RestClient
import javax.inject.Inject
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention
import com.google.inject.BindingAnnotation
import java.lang.annotation.Target
import java.lang.annotation.ElementType


/** AppEngine base url or localhost or IP or whatever*/
Retention(RetentionPolicy.RUNTIME)
BindingAnnotation
Target(ElementType.PARAMETER)
annotation class ServerUrl// (val foobar: String)

class VersionClient [Inject] (ServerUrl private val baseUrl: String) {
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
