package at.cpickl.agrotlin.app

import org.junit.Test
import org.hamcrest.Matchers.*
import org.hamcrest.MatcherAssert.*

class SrcTestKotlinTest {

    Test fun myTest() {
        // TODO does not get runned... :(
        assertThat(2, equalTo(4))
    }

}

/*

RunWith(javaClass<RobolectricTestRunner>())
class MyActivityTest {
    Test fun clickSomeButton() {
        val activity = Robolectric.buildActivity(javaClass<MainActivity>()).create().get()
        val btnDebug = activity.findViewById(R.id.btnDebug)
        btnDebug.performClickl()
        // assert that
    }
}


 */