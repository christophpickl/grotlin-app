package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.multi.resource.VersionRto
import org.testng.annotations.Test
import org.hamcrest.Matchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.restclient
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import at.cpickl.grotlin.restclient.RestClient

class VersionClient {
    fun get(): VersionRto {
        return TestClient().get().url("/version").unmarshallTo(javaClass<VersionRto>())
    }
}

Test(groups = array("WebTest")) class VersionWebTest {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<VersionWebTest>())
    }

    fun getVersion() {
        val version = VersionClient().get()
        LOG.debug("Returned version: ${version}")
        assertThat(version.artifactVersion, Matchers.notNullValue())
        assertThat(version.buildDate, Matchers.notNullValue())
    }

}
