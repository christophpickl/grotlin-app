package at.cpickl.grotlin

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test as test

class RealDiceTest {

    test fun roll_should_return_numbers_from_1_to_6() {
        100.times { assertThat(roll(), allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(6))) }
    }

    test fun roll_should_return_all_numbers_at_least_once() {
        val markers = hashMapOf(1 to false, 2 to false, 3 to false,
                4 to false, 5 to false, 6 to false)
        100.times { markers.put(roll(), true) }
        assertThat("All 6 numbers should have been marked! Rolled: ${markers}",
                markers.values().all { it == true }, equalTo(true))
    }

    private fun roll(): Int = RealDice().roll()

}
