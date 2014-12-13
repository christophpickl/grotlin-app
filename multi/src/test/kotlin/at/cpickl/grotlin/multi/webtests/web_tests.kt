package at.cpickl.grotlin.multi.webtests

import org.testng.annotations.Test
import javax.ws.rs.core.MediaType
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description
import org.testng.annotations.BeforeSuite
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.TestData
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.restclient.assertStatusCode
import at.cpickl.grotlin.restclient.Status
import at.cpickl.grotlin.restclient.RestResponse

class AdminClient {
    fun resetDb() {
        RestClient(baseUrl()).get().queryParameter("secret", "hans").url("/admin/resetDB").assertStatusCode(Status._200_OK)
    }
}

class MiscTestClient {
    fun get(url: String): RestResponse {
        return RestClient(baseUrl()).get().url(url)
    }
}

Test(groups = array("WebTest")) class SecuredWebTest {

    fun securedEndpointWithoutAccessTokenShouldBeUnauthorized() {
        RestClient(baseUrl()).get().url("/test/secured").assertStatusCode(Status._401_UNAUTHORIZED)
    }

    fun securedEndpointWithHeaderFakeAccessTokenShouldBeOk() {
        RestClient(baseUrl()).get().accessToken(TestData.FAKE_TOKEN_USER).url("/test/secured").assertStatusCode(Status._200_OK)
    }

    fun securedAdminEndpointWithHeaderFakeUserAccessTokenShouldBeForbidden() {
        RestClient(baseUrl()).get().accessToken(TestData.FAKE_TOKEN_USER).url("/test/secured_admin").assertStatusCode(Status._403_FORBIDDEN)
    }

    fun securedAdminEndpointWithHeaderFakeAdminAccessTokenShouldBeOk() {
        RestClient(baseUrl()).get().accessToken(TestData.FAKE_TOKEN_ADMIN).url("/test/secured_admin").assertStatusCode(Status._200_OK)
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

    BeforeSuite fun initUsersForAllTestsBeforeAnyOtherTestIsExecuted_viaAdminResetDb() {
        AdminClient().resetDb()
    }

    fun invalidUrlShouldReturn404NotFound() {
        MiscTestClient().get("/not_existing").assertStatusCode(Status._404_NOT_FOUND)
    }

}
