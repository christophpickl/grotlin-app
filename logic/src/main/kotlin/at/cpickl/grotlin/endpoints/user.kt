package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlElement
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.RestResponse
import at.cpickl.grotlin.restclient.Status
import javax.inject.Inject

class UserClient [Inject] (ServerUrl private val baseUrl: String) {

    /**
     * @throws LoginClientException on invalid login (403 FORBIDDEN)
     */
    fun login(request: LoginRequestRto): LoginResponseRto {
        val response = RestClient(baseUrl).post().body(request).url("/users/login")
        // response.onStatusUnmarshall<LoginResponseRto>(Status._200_OK, { return rto } )
        // response.onStatusWithFault(Status._403_FORBIDDEN, { throw LoginClientException ... })
        // somehow let the response object itself throw ClientException if no status was matched (?)
        if (response.status == Status._200_OK) {
            return response.unmarshallTo(javaClass<LoginResponseRto>())
        }
        if (response.status == Status._403_FORBIDDEN) {
            val fault = response.unmarshallTo(javaClass<FaultRto>()).toDomain()
            if (FaultCode.INVALID_CREDENTIALS == fault.code) {
                throw LoginClientException("Login failed for user '${request.username}'!")
            }
        }
        throw ClientException("Invalid login response status = ${response.status} (${response})")
    }

    fun logout(token: String): RestResponse {
        return RestClient(baseUrl).post().accessToken(token).url("/users/logout")
    }

    fun getProfile(accessToken: String): UserResponseRto {
        return RestClient(baseUrl).get().accessToken(accessToken).url("/users/profile").verifyStatusCode().unmarshallTo(javaClass<UserResponseRto>())
    }

}

class LoginClientException(message: String) : ClientException(message)

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class LoginRequestRto(
        XmlElement(required = true, nillable = false) var username: String? = null,
        XmlElement(required = true, nillable = false) var password: String? = null
) {
    class object {
        fun build(username: String, password: String): LoginRequestRto {
            val rto = LoginRequestRto()
            rto.username = username
            rto.password = password
            return rto
        }
    }
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

// TODO rename to UserPrivateProfileResponseRto
XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class UserResponseRto(var name: String? = null, var role: String? = null) {
    class object {
        fun build(name: String, role: String): UserResponseRto = UserResponseRto(name, role)
    }
}
