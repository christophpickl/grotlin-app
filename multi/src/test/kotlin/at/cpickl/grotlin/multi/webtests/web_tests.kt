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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.TestData


class AdminClient : Client() {
    fun resetDb() {
        getAny("/admin/resetDB").queryParameter("secret", "hans").get().assertStatusCode(Response.Status.OK)
    }
}

class TestClient : Client() {
    fun get(url: String): Response {
        return getAny(url).get()
    }
}

Test(groups = array("WebTest")) class SecuredWebTest {

    fun securedEndpointWithoutAccessTokenShouldBeUnauthorized() {
        TestClient().get("/test/secured").assertStatusCode(Response.Status.UNAUTHORIZED)
    }

    fun securedEndpointWithHeaderFakeAccessTokenShouldBeOk() {
        TestClient().getAny("/test/secured").header("X-access_token", TestData.FAKE_TOKEN_USER).get().assertStatusCode(Response.Status.OK)
    }

    fun securedAdminEndpointWithHeaderFakeUserAccessTokenShouldBeForbidden() {
        TestClient().getAny("/test/secured_admin").header("X-access_token", TestData.FAKE_TOKEN_USER).get().assertStatusCode(Response.Status.FORBIDDEN)
    }

    fun securedAdminEndpointWithHeaderFakeAdminAccessTokenShouldBeOk() {
        TestClient().getAny("/test/secured_admin").header("X-access_token", TestData.FAKE_TOKEN_ADMIN).get().assertStatusCode(Response.Status.OK)
    }

    // gnah, resteasy's message body reader doesnt provide access to query params :-/
    //    fun securedEndpointWithQueryParamFakeAccessTokenShouldBeOk() {
    //        TestClient().getAny("/test/secured").queryParameter("X-access_token", "1").get().assertStatusCode(Response.Status.OK)
    //    }
}

Test(groups = array("WebTest")) class MiscWebTest {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<MiscWebTest>())
    }

    BeforeSuite fun resetDB() {
        AdminClient().resetDb()
    }

    fun invalidUrlShouldReturn404NotFound() {
        TestClient().get("/not_existing").assertStatusCode(Response.Status.NOT_FOUND)
    }

}

