package at.cpickl.grotlin

import org.testng.annotations.Test as test
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MapTest {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val p1 = Player("p1")
    val p2 = Player("p2")
    var r1 = Region(id = "r1")
    var r2 = Region(id = "r2")
    var r3 = Region(id = "r3")

    BeforeMethod fun init() {
        r1 = Region(id = "r1")
        r2 = Region(id = "r2")
        r3 = Region(id = "r3")
    }


    test fun sourceRegions_oneNeutralRegion() {
        log.info("sourceRegions_oneNeutralRegion()")
        r1.ownedBy(p1, 2)
        r1.addBidirectional(r2)

        val actual = attackSourceRegionsForMap(p1, r1, r2)

        assertThat(actual.size, equalTo(1))
        assertThat(actual.first(), equalTo(r1))
    }

    test fun sourceRegions_oneSelfAndAnEnemy() {
        log.info("sourceRegions_oneSelfAndAnEnemy()")
        r1.ownedBy(p1, 2)
        r2.ownedBy(p1, 2)
        r3.ownedBy(p2, 1)
        r1.addBidirectional(r2)
        r1.addBidirectional(r3)

        val actual = attackSourceRegionsForMap(p1, r1, r2, r3)

        assertThat(actual.size, equalTo(1))
        assertThat(actual.first(), equalTo(r1))
    }

    private fun attackSourceRegionsForMap(player: Player, vararg regions: Region): Collection<Region> {
        return Map(regions.toList()).attackSourceRegionsFor(player)
    }

}