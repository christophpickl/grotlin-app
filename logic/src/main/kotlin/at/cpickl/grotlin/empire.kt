package at.cpickl.grotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EmpireFinder {
    class object {
        val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    fun biggestEmpire(player: Player, map: Map): Collection<Region> {
        val regions = map.regions.filter({ it.owner == player }).map({ Marked(it) })
        val markedByRegion = regions.toMap { it.item }

        var empire: Collection<Region> = linkedListOf()
        regions.forEach {
            val currentEmpire = deepSearch(player, it, markedByRegion)
            if (currentEmpire.size() > empire.size()) {
                empire = currentEmpire
            }
        }
        LOG.trace("biggestEmpire(player={}, map) ... returning empire.size={}", player, empire.size())
        return empire
    }

    private fun deepSearch(player: Player, marked: Marked<Region>, markedByRegion: kotlin.Map<Region, Marked<Region>>): Collection<Region> {
        if (marked.marked) {
            return linkedListOf()
        }
        marked.marked = true
        val empire = linkedListOf<Region>()
        empire.add(marked.item)
        for (adjacentRegion in marked.item.adjacent.filter { it.owner == player }) {
            val adjacentMarkedRegion = markedByRegion.get(adjacentRegion)
            if (adjacentMarkedRegion == null) {
                throw RuntimeException("Not found adjacentRegion=${adjacentRegion} in map: ${markedByRegion}")
            }
            empire.addAll(deepSearch(player, adjacentMarkedRegion, markedByRegion))
        }
        return empire
    }

}

data class Marked<T>(val item: T, var marked: Boolean = false)
