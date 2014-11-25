package at.cpickl.grotlin.multi

import org.hamcrest.MatcherAssert
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.testng.annotations.Test
import org.testng.annotations.BeforeSuite
import java.util.logging.Logger
import java.util.logging.ConsoleHandler
import java.util.logging.Level

Test public class EnableLogNonTest {

    BeforeSuite public fun initLogging() {
        println("EnableLogNonTest#initLogging()")
        val root = Logger.getLogger("")
        val handler = ConsoleHandler()
        handler.setLevel(Level.ALL)
        root.addHandler(handler)
    }
}

fun <T> assertThat(actual: T, matcher: Matcher<T>) {
    MatcherAssert.assertThat(actual, matcher)
}

fun <T> equalTo(operand: T): Matcher<T> {
    return Matchers.equalTo(operand)
}
