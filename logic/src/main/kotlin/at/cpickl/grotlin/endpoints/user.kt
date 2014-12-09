package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlElement
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.RestResponse
import com.google.common.base.MoreObjects
import at.cpickl.grotlin.restclient.Status

class UserClient(private val baseUrl: String) {

    fun login(request: LoginRequestRto): LoginResponseRto {
        return RestClient(baseUrl).post().body(request).url("/users/login").verifyStatusCode().unmarshallTo(javaClass<LoginResponseRto>())
    }

    fun logout(token: String): RestResponse {
        return RestClient(baseUrl).post().accessToken(token).url("/users/logout")
    }

    fun getProfile(accessToken: String): UserResponseRto {
        return RestClient(baseUrl).get().accessToken(accessToken).url("/users/profile").verifyStatusCode().unmarshallTo(javaClass<UserResponseRto>())
    }

}

class ClientFaultException(message: String, val response: RestResponse, val fault: FaultRto) : RuntimeException(message) {
    override fun toString(): String {
        return "Fault was thrown: ${fault} (status code = ${response.status}) with message: ${getMessage()}"
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
    class object {
        fun build(accessToken: String): LoginResponseRto {
            val rto = LoginResponseRto()
            rto.accessToken = accessToken
            return rto
        }
    }
    var accessToken: String? = null
    override fun toString() = "LoginResultRto[accessToken='${accessToken}']"
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserResponseRto(var name: String? = null, var role: String? = null) {
    class object {
        fun build(name: String, role: String): UserResponseRto = UserResponseRto(name, role)
    }
}
