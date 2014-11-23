package at.cpickl.grotlin

import java.util.HashSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Arrays


data open class Map(val regions: Set<Region>) {

    fun attackSourceRegionsFor(player: Player): Collection<Region> {
        return regions.filter {
            it.owner == player &&
                    it.armies > 1 &&
                    it.adjacent.any { it.owner != player }
        }.toLinkedList()
    }

    // actually just used by CLI app
    open fun toPrettyString(): String = "N/A"

}

class Simple4RegionsMap(val r1: Region = Region(label = "r1"),
                        val r2: Region = Region(label = "r2"),
                        val r3: Region = Region(label = "r3"),
                        val r4: Region = Region(label = "r4")) :
        Map(hashSetOf(r1, r2, r3, r4)) {
    {
        r1.addBidirectional(r2, r3)
        r4.addBidirectional(r2, r3)
    }

    override fun toPrettyString(): String {
        val l1 = r1.toPrettyString()
        val l2 = r2.toPrettyString()
        val l3 = r3.toPrettyString()
        val l4 = r4.toPrettyString()
        return """
                /--------\
               | $l2  |
                \--------/
         /--------\         /--------\
        | $l1  |       | $l4  |
         \--------/         \--------/
                /--------\
               | $l3  |
                \--------/"""
    }
}

data class Region(var owner: Player? = null, var armies: Int = 0, var label: String = "?") {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    // visible for testing
    var adjacent: MutableSet<Region> = HashSet()

    fun addBidirectional(vararg adj: Region): Region {
        log.trace("addBidirectional(adj=${Arrays.toString(adj.toArrayList().toArray())})")
        adjacent.addAll(adj)
        adj.forEach { it.adjacent.add(this) }
        return this
    }

    fun ownedBy(newOwner: Player, armies: Int) {
        owner = newOwner
        this.armies = Contract.mustBePositive(armies)
    }

    fun isOwned() = owner != null

    fun attackAndOwn(to: Region) {
        to.ownedBy(owner!!, armies - 1)
        armies = 1
    }

    fun adjacentAttackables(): Collection<Region> {
        return adjacent.filter { it.owner != this.owner }
    }

}
