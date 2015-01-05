// TODO this file is copied from logic module (see travis-ci test dependency issue)
package at.cpickl.grotlin.multi

import at.cpickl.grotlin.restclient.RestResponse
import at.cpickl.grotlin.restclient.Status
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description
import at.cpickl.grotlin.Dice
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.lessThan


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



// For now Kotlin supports only primary constructors (secondary constructors may be supported later).
// http://stackoverflow.com/questions/19299525/kotlin-secondary-constructor
//fun testDice(vararg rolls: Int): Dice = TestDice(rolls)
class TestDice(_rolls: IntArray) : Dice {
    class object {
        fun byRolls(vararg rolls: Int) = TestDice(rolls)
    }
    private val rolls: IntArray = _rolls
    private var current: Int = 0

    {
        assertThat("All rolls must be between 1-6!",
                rolls.all { it >= 1 && it <= 6 }, equalTo(true))
    }

    override fun roll(): Int {
        assertThat("Already consumed all rolls: ${rolls.toString()}", current, lessThan(rolls.size()))
        return rolls[current++]
    }

    fun verify() {
        // TODO how to format IntArray to pretty string?
        assertThat("Not all rolls have been consumed, only ${current} out of ${rolls.size}: ${rolls}",
                current, equalTo(rolls.size()))
    }

}