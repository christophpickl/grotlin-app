package at.cpickl.grotlin.multi.webtests

import org.testng.annotations.Test
import org.hamcrest.Matchers
import org.jboss.resteasy.client.ClientRequest
import org.jboss.resteasy.client.ClientResponse
import at.cpickl.grotlin.multi.resource.VersionRto
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.equalTo
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description
import org.testng.annotations.BeforeSuite


class TestClient : Client() {
    fun get(url: String): Response {
        return getAny(url).get()
    }
}

Test(groups = array("WebTest")) public class SecuredWebTest {

    public fun securedEndpointWithoutAccessTokenShouldBeUnauthorized() {
        TestClient().get("/test/secured").assertStatusCode(Response.Status.UNAUTHORIZED)
    }

    public fun securedEndpointWithHeaderFakeAccessTokenShouldBeOk() {
        TestClient().getAny("/test/secured").header("X-access_token", FAKE_TOKEN_USER).get().assertStatusCode(Response.Status.OK)
    }

    public fun securedAdminEndpointWithHeaderFakeUserAccessTokenShouldBeForbidden() {
        TestClient().getAny("/test/secured_admin").header("X-access_token", FAKE_TOKEN_USER).get().assertStatusCode(Response.Status.FORBIDDEN)
    }

    public fun securedAdminEndpointWithHeaderFakeAdminAccessTokenShouldBeOk() {
        TestClient().getAny("/test/secured_admin").header("X-access_token", FAKE_TOKEN_ADMIN).get().assertStatusCode(Response.Status.OK)
    }

    // gnah, resteasy's message body reader doesnt provide access to query params :-/
    //    public fun securedEndpointWithQueryParamFakeAccessTokenShouldBeOk() {
    //        TestClient().getAny("/test/secured").queryParameter("X-access_token", "1").get().assertStatusCode(Response.Status.OK)
    //    }
}

Test(groups = array("WebTest")) public class MiscWebTest {

//    BeforeSuite
//    public fun resetDB() {
// TODO invoke resetDB, otherwise there will be no users
//    }

    public fun invalidUrlShouldReturn404NotFound() {
        TestClient().get("/not_existing").assertStatusCode(Response.Status.NOT_FOUND)
    }

}

