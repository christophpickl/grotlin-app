package at.cpickl.grotlin.multi.webtests

import org.testng.annotations.Test
import org.hamcrest.Matchers.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.restclient
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import at.cpickl.grotlin.restclient.RestClient
import at.cpickl.grotlin.endpoints.VersionClient

Test(groups = array("WebTest")) class VersionWebTest {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<VersionWebTest>())
    }

    fun getVersion() {
        val version = Clients.version().get()
        LOG.debug("Returned version: ${version}")
        assertThat(version.artifactVersion, notNullValue())
        assertThat(version.buildDate, notNullValue())
    }

}
