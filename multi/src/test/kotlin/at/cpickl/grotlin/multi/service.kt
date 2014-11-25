package at.cpickl.grotlin.multi

import org.testng.annotations.Test
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

Test public class PropertiesVersionServiceTest {
    public fun loadTestPropertiesFileShouldReturnContent() {
        MatcherAssert.assertThat(PropertiesVersionService("/swirltest/test_version.properties").load(),
                Matchers.equalTo(Version("testArtifactVersion", "testBuildDate")))
    }
}
