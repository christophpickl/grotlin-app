package at.cpickl.grotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory


open class Game(val id: String, val map: Map, val players: List<Player>, val dice: Dice = RealDice()) {

    private val playersIterator = PlayerIterator(players)

    class object {
        private val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    public var currentPlayer: Player = playersIterator.currentPlayer
        get() = playersIterator.currentPlayer
        private set

    public fun attack(source: Region, target: Region): BattleResult {
        LOG.info("attack(source: {}, target: {})", source, target)

        verifyAttackable(source, target)
        if (!target.isOwned()) {
            source.attackAndOwn(target)
            return BattleResult(source.owner!!, source, target, -1, -1)
        }
        val battleResult = Battle(source, target, dice).fight()
        if (battleResult.winner == source.owner) {
            target.ownedBy(source.owner!!, source.armies - 1)
        }
        source.armies = 1 // in any case it's reduced to 1 ;)
        return battleResult
    }

    fun regionById(id: String): Region {
        val found = map.regions.firstOrNull { it.id == id }
        if (found != null) {
            return found
        }
        throw IllegalArgumentException("Not found region by ID '${id}' with regions: ${map.regions}!")
    }

    public fun nextPlayer() {
        currentPlayer = playersIterator.next()
        LOG.info("nextPlayer() is: {}", currentPlayer)
    }

    public fun sourceRegionsForCurrentPlayer(): Collection<Region> {
        return map.attackSourceRegionsFor(playersIterator.currentPlayer)
    }

    public fun isGameOver(): Boolean = map.regions.filter { it.owner != null }.groupBy { it.owner }.keySet().size == 1

    private fun verifyAttackable(source: Region, target: Region) {
        if (!source.isOwned()) {
            throw IllegalArgumentException("source.owner is not set")
        }
        if (source.armies < 2) {
            throw IllegalArgumentException("source.armies is < 2 (${source.armies}): ${source}")
        }
        // TODO check if target is reachable from source
    }

    fun biggestConnectedEmpireSize(currentPlayer: Player): Int {
        return EmpireFinder().biggestEmpire(currentPlayer, map).size()
    }

    fun distributableRegionsFor(player: Player): Collection<Region> = map.regions.filter { it.owner == player }

    override public fun toString() = "Game[map=${map}, players, dice]"
}

data class Player(val name: String, public val color: Int = 0) { // TODO make non-optional!

    var isAlive: Boolean = true // dirty design
        get() = $isAlive
        private set

    fun kill() {
        if (isAlive == false) throw IllegalStateException("Player already killed: ${name}")
        isAlive = false
    }

}
