package at.cpickl.grotlin.multi

import org.hamcrest.MatcherAssert
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.testng.annotations.Test
import org.testng.annotations.BeforeSuite
import org.jboss.resteasy.client.ClientResponse
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description
import org.hamcrest.Factory
import javax.ws.rs.core.Response
import at.cpickl.grotlin.multi.webtests.TestClient

//Test class EnableLogNonTest {
//    BeforeSuite fun initLogging() {
//        println("EnableLogNonTest#initLogging()")
//        val root = Logger.getLogger("")
//        val handler = ConsoleHandler()
//        handler.setLevel(Level.ALL)
//        root.addHandler(handler)
//    }
//}

fun <T> assertThat(actual: T, matcher: Matcher<T>) {
    MatcherAssert.assertThat(actual, matcher)
}

fun <T> equalTo(operand: T): Matcher<T> {
    return Matchers.equalTo(operand)
}
