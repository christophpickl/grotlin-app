package at.cpickl.agrotlin

import java.util.Collection
import java.util.LinkedHashSet

data public class Player(public val name: String, public val color: Int)

data public class Game(public val players: List<Player>, public val map: Map)

data public class Map(private val regions: List<Region>)

public class Region {
    public val outs: MutableCollection<Region> = linkedListOf()
    public var owner: Player? = null
        private set
    public var armies: Int = 0

    public fun addOuts(vararg out: Region): Region {
        for (o in out) {
            outs.add(o)
            o.outs.add(this) // do it bi-directional!
        }
        return this
    }

    public fun ownedBy(newOwner: Player, newArmies: Int): Region {
        owner = newOwner
        armies = newArmies
        return this
    }

    override fun toString(): String {
        return "Region[owner=" + owner + "]"
    }

}
