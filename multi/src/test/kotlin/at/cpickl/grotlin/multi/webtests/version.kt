package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.multi.resource.VersionRto
import org.testng.annotations.Test
import at.cpickl.grotlin.multi.assertThat
import org.hamcrest.Matchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VersionClient : Client() {
    fun get(): VersionRto {
        return get("/version", javaClass<VersionRto>()).getEntity()
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
