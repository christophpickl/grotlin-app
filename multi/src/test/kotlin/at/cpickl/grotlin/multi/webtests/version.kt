package at.cpickl.grotlin.multi.webtests

import at.cpickl.grotlin.multi.resource.VersionRto
import org.testng.annotations.Test
import java.util.logging.Logger
import at.cpickl.grotlin.multi.assertThat
import org.hamcrest.Matchers

class VersionClient : Client() {
    fun get(): VersionRto {
        return get("/version", javaClass<VersionRto>()).getEntity()
    }
}

Test(groups = array("WebTest")) public class VersionWebTest {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    public fun getVersion() {
        val version = VersionClient().get()
        LOG.fine("Returned version: ${version}")
        assertThat(version.artifactVersion, Matchers.notNullValue())
        assertThat(version.buildDate, Matchers.notNullValue())
    }

}