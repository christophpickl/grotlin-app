package at.cpickl.grotlin.multi.webtests

import javax.ws.rs.core.Response
import org.testng.annotations.Test
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.resource.FaultRto
import at.cpickl.grotlin.multi.equalTo
import at.cpickl.grotlin.multi.FaultCode
import org.jboss.resteasy.client.core.BaseClientResponse
import at.cpickl.grotlin.multi.resource.LoginRequestRto
import at.cpickl.grotlin.multi.resource.LoginResponseRto
import at.cpickl.grotlin.multi.resource.VersionRto

class UserClient {

    fun login(request: LoginRequestRto): LoginResponseRto {
        return RestClient().post().body(request).url("/users/login").unmarshallTo(javaClass<LoginResponseRto>())
    }

    fun logout(token: String): RestResponse {
        return RestClient().post().accessToken(token).url("/users/logout")
    }

}

Test(groups = array("WebTest")) class UserWebTest {

    fun login_user1_success() {
        println(UserClient().login(LoginRequestRto.build("user1", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33")))
    }

    fun logout_invalidToken_shouldReturn400BadRequest() {
        val response = UserClient().logout("invalid")
        response.assertStatusCode(Response.Status.UNAUTHORIZED)
        assertThat(response.unmarshallTo(javaClass<FaultRto>()),
            equalTo(FaultRto.build("Authentication required to access this resource!", FaultCode.UNAUTHORIZED)))
    }
}
