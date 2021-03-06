package at.cpickl.grotlin

import java.util.HashSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Arrays

// TODO rename Map class as of name clash with kotlin stdlib Map<X, Y> datastructure :(
data open class Map(val regions: List<Region>) {

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

class Simple4RegionsMap(val r1: Region = Region(id = "r1"),
                        val r2: Region = Region(id = "r2"),
                        val r3: Region = Region(id = "r3"),
                        val r4: Region = Region(id = "r4")) :
        Map(listOf(r1, r2, r3, r4)) {
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

data class Region(public var owner: Player? = null, var armies: Int = 0, var id: String = "?") { // TODO make non-optional

//    public var owner: Player? = _owner
//        public get() = $owner
//        private set(value) { $owner = value}

    class object {
        private val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    // visible for testing
    var adjacent: MutableSet<Region> = HashSet()

    fun addBidirectional(vararg adj: Region): Region {
        LOG.trace("addBidirectional(adj=${Arrays.toString(adj.toArrayList().toArray())})")
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
        return adjacent.filter { it.owner != owner }
    }

    fun isPotentialAttackSource(): Boolean {
        if (owner == null) {
            return false
        }
        if (armies < 2) {
            return false
        }
        return !adjacentAttackables().isEmpty()
    }


}
