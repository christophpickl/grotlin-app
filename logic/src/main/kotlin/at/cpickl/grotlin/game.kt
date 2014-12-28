package at.cpickl.grotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory


//data class RunningGame(val id: String, val users: Collection<Player>, val map: Map) {

//
//    fun asPlayer(user: User): Player {
//        val found = playersByName.get(user.name)
//        if (found != null) {
//            return found
//        }
//        throw IllegalArgumentException("Not found player by user instance: {$user}! Registered players: ${playersByName.values()}")
//    }
// currentPlayer's turn
//}

open class Game(val id: String, val map: Map, val players: List<Player>, val dice: Dice = RealDice()) {

    private val playersIterator = PlayerIterator(players)

    class object {
        val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    public var currentPlayer: Player = playersIterator.currentPlayer
        get() = playersIterator.currentPlayer
        private set

    public fun attack(source: Region, target: Region): BattleResult {
        LOG.info("attack(source: {}, target: {})", source, target)

        verifyAttackable(source, target)
        if (!target.isOwned()) {
            source.attackAndOwn(target)
            return BattleResult(source.owner!!)
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
        // FIXME test & implement
        return 1
    }

    fun distributableRegionsFor(player: Player): Collection<Region> = map.regions.filter { it.owner == player }

    override public fun toString() = "Game[map=${map}, players, dice]"
}

class GameEngine(private val game: Game, private val listener: GameListener) {
    fun start() {
        while (true) {
            listener.onPlayersTurn(game.currentPlayer)
            attackPhase()
            if (game.isGameOver()) {
                break;
            }
            distributionPhase()
            game.nextPlayer()
        }
    }

    private fun attackPhase() {
        while (!game.sourceRegionsForCurrentPlayer().empty &&
                listener.continueAttacking(game.currentPlayer)) {
            val result = attackPhaseStep()
            listener.onBattleResult(result)
        }
    }

    private fun attackPhaseStep(): BattleResult {
        val (source, target) = listener.doAttack(game.currentPlayer)
        return game.attack(source, target)
    }

    private fun distributionPhase() {
        val spawn = game.biggestConnectedEmpireSize(game.currentPlayer)
        val target = listener.doDistribute(game.currentPlayer, spawn)
        if (target.owner != game.currentPlayer) throw IllegalArgumentException("target.owner != currentPlayer")
        target.armies += spawn
    }
}

trait GameListener {
    fun onPlayersTurn(player: Player)
    fun doAttack(player: Player): Pair<Region, Region>
    fun continueAttacking(player: Player): Boolean
    // only distributing all at once into one region supported right now ;)
    fun doDistribute(player: Player, biggestConnectedEmpireSize: Int): Region

    fun onBattleResult(result: BattleResult)

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
