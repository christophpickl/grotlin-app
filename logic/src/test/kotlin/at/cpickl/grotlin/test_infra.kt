package at.cpickl.grotlin

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*



//fun <T> assertThat(message: String, actual: T, matcher: Matcher<T>) {
//    MatcherAssert.assertThat(message, actual, matcher)
//}
//
//fun <T> assertThat(actual: T, matcher: Matcher<T>) {
//    MatcherAssert.assertThat(actual, matcher)
//}
//
//fun <T> equalTo(operand: T): Matcher<T> {
//    return Matchers.equalTo(operand)
//}

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