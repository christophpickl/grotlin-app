package at.cpickl.grotlin.multi.webtests

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.assertThat
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description

fun Response.assertStatusCode(expected: Status) {
    assertThat(this, hasStatus(expected))
}

fun hasStatus(expected: Response.Status): Matcher<Response> = ResponseStatusMatcher(expected)
class ResponseStatusMatcher(private val expected: Response.Status) : TypeSafeMatcher<Response>() {
    override fun matchesSafely(actual: Response): Boolean {
        return actual.getStatus() == expected.getStatusCode()
    }
    override fun describeTo(description: Description) {
        description.appendText("Response with status ${expected} (${expected.getStatusCode()})")
    }
    override fun describeMismatchSafely(actual: Response, mismatchDescription: Description) {
        // actual.getLocation() ... throws org.jboss.resteasy.spi.NotImplementedYetException
        mismatchDescription.appendText("was ${actual.getStatus()}")
    }
}
