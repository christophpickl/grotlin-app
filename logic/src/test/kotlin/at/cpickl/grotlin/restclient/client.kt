package at.cpickl.grotlin.restclient

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description


public fun RestResponse.assertStatusCode(expected: Status) {
    assertThat(this, hasStatus(expected))
}

public fun hasStatus(expected: Status): Matcher<RestResponse> = ResponseStatusMatcher(expected)

public class ResponseStatusMatcher(private val expected: Status) : TypeSafeMatcher<RestResponse>() {
    override fun matchesSafely(actual: RestResponse): Boolean {
        return actual.status == expected
    }
    override fun describeTo(description: Description) {
        description.appendText("Response with status ${expected} (${expected})")
    }
    override fun describeMismatchSafely(actual: RestResponse, mismatchDescription: Description) {
        // actual.getLocation() ... throws org.jboss.resteasy.spi.NotImplementedYetException
        mismatchDescription.appendText("was ${actual.status}")
    }
}
