package at.cpickl.grotlin

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test as test


class PlayerIteratorTest {

    val p1 = Player("1")
    val p2 = Player("2")
    val p3 = Player("3")

    test fun cycleEndlesslyThroughThreePlayers() {
        val testee = testee(p1, p2, p3)
        assertThat(testee.next(), equalTo(p1))
        assertThat(testee.next(), equalTo(p2))
        assertThat(testee.next(), equalTo(p3))
        assertThat(testee.next(), equalTo(p1))
        assertThat(testee.next(), equalTo(p2))
        assertThat(testee.next(), equalTo(p3))
    }

    test fun twoPlayersFirstOneDiesEndOfIteration() {
        val testee = testee(p1, p2)
        assertThat(testee.next(), equalTo(p1))
        assertThat(testee.next(), equalTo(p2))
        p1.kill()
        assertThat(testee.hasNext(), equalTo(false))
    }

    private fun testee(vararg players: Player) = PlayerIterator(players.toArrayList())

}
