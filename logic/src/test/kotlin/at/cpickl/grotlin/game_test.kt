package at.cpickl.grotlin

import org.testng.annotations.Test as test
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod


abstract class GameableTest {

    val p1 = Player("Player 1")
    val p2 = Player("Player 2")
    var map = Simple4RegionsMap()
    var dice = TestDice(intArray())

    BeforeMethod fun setUp() {
        map = Simple4RegionsMap()
        dice = TestDice(intArray())
    }

    AfterMethod fun tearDown() {
        dice.verify()
    }

}

class GameTest : GameableTest() {

    test fun attackNeutralRegionWith3ArmiesShouldSucceedAndAllUnitsMoveIn() {
        val sourceArmies = 3
        val sourceRegion = map.r1
        val targetRegion = map.r2
        sourceRegion.ownedBy(p1, sourceArmies)

        game(p1).attack(sourceRegion, targetRegion)

        assertThat(targetRegion.owner, equalTo(p1))
        assertAllUnitsMovedIn(sourceRegion, sourceArmies, targetRegion)
    }

    test fun attackOccupiedRegionAndWinAllDicesShouldSucceedAndAllUnitsMoveIn() {
        attackOnDefaultMapWithDices(6, 6, 1, 1)

        assertThat(map.r2.owner, equalTo(p1))
        assertAllUnitsMovedIn(map.r1, 2, map.r2)
    }

    test fun attackOccupiedRegionAndLoseAllDicesShouldFail() {
        attackOnDefaultMapWithDices(1, 1, 6, 6)

        assertThat(map.r2.owner, equalTo(p2))
        assertThat(map.r1.armies, equalTo(1))
        assertThat(map.r2.armies, equalTo(2))
    }

    private fun attackOnDefaultMapWithDices(vararg dices: Int) {
        dice = TestDice(dices)
        val game = game(p1, p2)
        map.r1.ownedBy(p1, 2)
        map.r2.ownedBy(p2, 2)

        game.attack(source = map.r1, target = map.r2)
    }

    private fun assertAllUnitsMovedIn(source: Region, sourceArmies: Int, target: Region) {
        assertThat(source.armies, equalTo(1))
        assertThat(target.armies, equalTo(sourceArmies - 1))
    }

    private fun game(vararg players: Player) = Game(map, players.toLinkedList(), dice)

}
