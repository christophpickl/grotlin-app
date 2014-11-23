package at.cpickl.grotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Game(val map: Map, val players: List<Player>, val dice: Dice = RealDice()) {

    private val playersIterator = PlayerIterator(players)


    //    class object {
    //        fun create() = C()
    //    }
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    public var currentPlayer: Player = playersIterator.currentPlayer
        get() = playersIterator.currentPlayer
        private set

    public fun attack(source: Region, target: Region): BattleResult {
        log.info("attack(source: {}, target: {})", source, target)
        println("source: ${source}")
        verifyAttackable(source, target)
        if (!target.isOwned()) {
            source.attackAndOwn(target)
            return BattleResult(source.owner!!)
        }
        val battleResult = Battle(source, target, dice).fight()
        if (battleResult.winner == source.owner) {
            target.owner = source.owner
            target.armies = source.armies - 1
            source.armies = 1
        } else {
            // attack lost
            source.armies = 1
        }
        return battleResult
    }

    public fun nextPlayer() {
        currentPlayer = playersIterator.next()
        log.info("nextPlayer() is: {}", currentPlayer)
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
            throw IllegalArgumentException("source.armies is < 2 (${source.armies})")
        }
        // TODO check if target is reachable from source
    }

    fun biggestConnectedEmpireSize(currentPlayer: Player): Int {
        // FIXME test & implement
        return 1
    }

    fun distributableRegionsFor(player: Player): Collection<Region> = map.regions.filter { it.owner == player }

}

class GameEngine(private val game: Game, private val listener: GameListener) {
    fun start() {
        while (true) {
            attackPhase()
            if (game.isGameOver()) {
                break;
            }
            distributionPhase()
            game.nextPlayer()
        }
    }

    private fun attackPhase() {
        listener.onPlayersTurn(game.currentPlayer)
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


data class Player(val name: String) {

    var isAlive: Boolean = true // dirty design
        get() = $isAlive
        private set

    fun kill() {
        if (isAlive == false) throw IllegalStateException("Player already killed: ${name}")
        isAlive = false
    }

}
