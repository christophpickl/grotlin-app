package at.cpickl.grotlin.multi.webtests

import javax.ws.rs.core.Response
import org.testng.annotations.Test
import org.jboss.resteasy.client.core.BaseClientResponse
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.Status
import at.cpickl.grotlin.restclient.RestResponse
import at.cpickl.grotlin.multi.assertStatusCode
import at.cpickl.grotlin.endpoints.UserClient
import at.cpickl.grotlin.endpoints.LoginRequestRto
import at.cpickl.grotlin.multi.TestData
import at.cpickl.grotlin.endpoints.UserResponseRto
import at.cpickl.grotlin.endpoints.FaultRto
import at.cpickl.grotlin.endpoints.FaultCode


Test(groups = array("WebTest")) class UserWebTest {

    fun login_user1_success() {
        val response = Clients.user().login(LoginRequestRto.build("user1", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33"))
        assertThat(response.accessToken, notNullValue())
    }

    fun profile_user1_success() {
        assertThat(Clients.user().getProfile(TestData.FAKE_TOKEN_ADMIN),
            equalTo(UserResponseRto.build("user1", "Admin")))
    }

    fun logout_invalidToken_shouldReturn400BadRequest() {
        val response = Clients.user().logout("invalid")
        response.assertStatusCode(Status._401_UNAUTHORIZED)
        assertThat(response.unmarshallTo(javaClass<FaultRto>()),
            equalTo(FaultRto.build("Authentication required to access this resource!", FaultCode.UNAUTHORIZED)))
    }
}
