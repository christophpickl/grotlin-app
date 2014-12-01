package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.multi.resource.LogoutRequestRto
import javax.ws.rs.core.Response
import org.testng.annotations.Test
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.resource.FaultRto
import at.cpickl.grotlin.multi.equalTo
import at.cpickl.grotlin.multi.FaultCode
import org.jboss.resteasy.client.core.BaseClientResponse

class UserClient : Client() {
    fun <T> logout(logoutRequest: LogoutRequestRto): BaseClientResponse<T> {
        val response = post("/users/logout", logoutRequest)
        if(response is BaseClientResponse) {
            return response as BaseClientResponse<T>
        }
        throw RuntimeException("Invalid response type ${response} (expected: BaseClientResponse)")
    }
}

Test(groups = array("WebTest")) public class UserWebTest {
    fun logout_invalidToken_shouldReturn400BadRequest() {
        val response = UserClient().logout<FaultRto>(LogoutRequestRto.build("invalid_token"))
        response.assertStatusCode(Response.Status.BAD_REQUEST)
        assertThat(response.getEntity(javaClass<FaultRto>()), equalTo(FaultRto.build("No user session found", FaultCode.INVALID_LOGOUT)))
    }
}
