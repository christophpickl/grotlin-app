package at.cpickl.grotlin.multi

import org.testng.annotations.Test
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

Test(groups = array("WebTest")) public class FancyWebTest {
    public fun request() {
        println("web test running ...")
        MatcherAssert.assertThat(2 + 2, Matchers.equalTo(3))
    }
}
