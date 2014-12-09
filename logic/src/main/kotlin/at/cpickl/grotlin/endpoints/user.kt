package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlElement
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.RestResponse

class UserClient(private val baseUrl: String) {

    fun login(request: LoginRequestRto): LoginResponseRto {
        return RestClient(baseUrl).post().body(request).url("/users/login").unmarshallTo(javaClass<LoginResponseRto>())
    }

    fun logout(token: String): RestResponse {
        return RestClient(baseUrl).post().accessToken(token).url("/users/logout")
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginRequestRto {
    class object {
        fun build(username: String, password: String): LoginRequestRto {
            val rto = LoginRequestRto()
            rto.username = username
            rto.password = password
            return rto
        }
    }
    XmlElement(required = true, nillable = false)
    var username: String? = null
    XmlElement(required = true, nillable = false)
    var password: String? = null

    override fun toString() = "LoginRto[username='${username}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginResponseRto {
    var accessToken: String? = null
    override fun toString() = "LoginResultRto[accessToken='${accessToken}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserResponseRto {
    var name: String? = null
    var role: String? = null
    override fun toString() = "UserRto[name='${name}']"
}
