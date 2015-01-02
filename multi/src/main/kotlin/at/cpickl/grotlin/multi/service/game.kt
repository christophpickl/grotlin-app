package at.cpickl.grotlin.multi.service

import org.slf4j.LoggerFactory
import com.google.common.base.MoreObjects
import javax.inject.Inject
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.WaitingGameNotification
import at.cpickl.grotlin.Simple4RegionsMap
import at.cpickl.grotlin.Region
import at.cpickl.grotlin.Game
import at.cpickl.grotlin.Map as Mapp
import at.cpickl.grotlin.Player
import at.cpickl.grotlin.Dice
import at.cpickl.grotlin.RealDice
import at.cpickl.grotlin.channel.AttackNotification
import at.cpickl.grotlin.multi.NotFoundException
import at.cpickl.grotlin.multi.UserException

class WaitingRandomGameService [Inject] (
        private val runningGameService: RunningGameService,
        private val channelApiService: ChannelApiService,
        private val idGenerator: IdGenerator
        ) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<WaitingRandomGameService>())
    }

    val waitingGames = linkedListOf<WaitingRandomGame>()
        get() = $waitingGames

    private val maxPlayers = 2

    fun getOrCreateRandomGame(user: User): WaitingRandomGame {
        LOG.debug("getOrCreateRandomGame(user=${user})")
        // check if user already in a waiting game

        if (waitingGames.empty) {
            return startNewWaitingGame(user)
        }
        val userGame = waitingGames.firstOrNull { it.containsWaiting(user) }
        if (userGame != null) {
            return userGame
        }
        val firstGame = waitingGames.first!!
        channelApiService.sendNotification(WaitingGameNotification(firstGame.users.size + 1), firstGame.users)
        firstGame.addWaitingUser(user)
        if (firstGame.isFull) {
            waitingGames.remove(firstGame)
            val runningGame = UserGame.build(idGenerator.generate(), firstGame.map, firstGame.users)
            pseudoInitGame(runningGame)
            runningGameService.addNewGame(runningGame)
        }
        return firstGame
    }

    private fun pseudoInitGame(game: UserGame) {
        LOG.trace("pseudoInitGame(game) ... r1.ownedBy=u1, r4.ownedBy=u2")
        val map = game.map as Simple4RegionsMap
        map.r1.ownedBy(game.asPlayer(game.users.get(0)), 2)
        map.r4.ownedBy(game.asPlayer(game.users.get(1)), 2)
    }

    private fun startNewWaitingGame(user: User): WaitingRandomGame {
        val map = Simple4RegionsMap() // for now only this map is supported
        val game = WaitingRandomGame(idGenerator.generate(), maxPlayers, map)
        waitingGames.add(game)
        game.addWaitingUser(user)
        return game
    }

}


public trait RunningGameService {
    public val runningGames: Collection<UserGame>

    public fun addNewGame(game: UserGame)
    public fun gameByIdForUser(gameId: String, user: User): UserGame

    public fun attackRegion(attack: AttackOrder): AttackNotification

    public fun endTurn(game: UserGame, user: User)
}

class InMemoryRunningGameService [Inject] (private val channelApiService: ChannelApiService) : RunningGameService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<InMemoryRunningGameService>())
    }

    private val gamesById = hashMapOf<String, UserGame>()
    override public val runningGames: Collection<UserGame>
        get() = gamesById.values()

    override public fun addNewGame(game: UserGame) {
        LOG.info("addNewGame(game=${game})")
        channelApiService.sendNotification(GameStartsNotification(game.id), game.users)
        gamesById.put(game.id, game)
    }

    override public fun gameByIdForUser(gameId: String, user: User): UserGame {
        LOG.debug("gameByIdForUser(gameId='${gameId}', user=${user})")
        val game = gamesById.get(gameId)
        if (game == null) {
            throw NotFoundException("Game with ID '${gameId}' does not exist for user ${user}!",
                    Fault("Unknown Game ID: '${gameId}'", FaultCode.GAME_NOT_FOUND))
        }
        if (!game.users.contains(user)) {
            throw UserException("${user} is not part of the ${game}!",
                    Fault("Game not accessible", FaultCode.FORBIDDEN))
        }
        return game
    }

    override fun attackRegion(attack: AttackOrder): AttackNotification {
        LOG.debug("attackRegion(attack=${attack})")
        //        val player = attack.game.asPlayer(attack.user)
        // TODO verify is attackable
        val battleResult = attack.game.attack(attack.sourceRegion, attack.targetRegion)
        val notification = AttackNotification.transform(battleResult)
        channelApiService.sendNotification(notification, attack.game.usersExcept(attack.user))
        return notification
    }

    override fun endTurn(game: UserGame, user: User) {
        LOG.debug("endTurn(game={}, user={})", game, user)
        if (!game.isCurrentUser(user)) {
            // TODO throw UserException instead
            throw RuntimeException("Nope, you are not the current user!")
        }
        // FIXME init distribution phase
        // return game.biggestConnectedEmpireSize(game.asPlayer(user))
        // game.nextPlayer()
    }


}

data class AttackOrder(val user: User, val game: UserGame, val sourceRegion: Region, val targetRegion: Region)


public class UserGame(id: String, map: Mapp, val users: List<User>, players: List<Player>, dice: Dice) : Game(id, map, players, dice) {
    public class object {
        public fun build(id: String, map: Mapp, users: List<User>, dice: Dice = RealDice()): UserGame {
            if (users.empty) throw IllegalArgumentException("Users must not be empty!")
            val color = 1 // TODO static color for player :-/
            return UserGame(id, map, users, users.map({ Player(it.name, color) }), dice)
        }
    }
    private val playerByName: Map<String, Player>
    {
        playerByName = players.toMap { it.name }
    }

    fun isCurrentUser(user: User): Boolean = currentPlayer == asPlayer(user)

    fun asPlayer(user: User) = playerByName.get(user.name)!!

    fun usersExcept(except: User): Collection<User> {
        return users.filter { it != except }
    }

}

data class WaitingRandomGame(val id: String, val usersMax: Int, val map: Mapp) {
    val isFull: Boolean
        get() = users.size == usersMax

    val users = linkedListOf<User>()

    fun addWaitingUser(user: User) {
        if (users.size == usersMax) {
            throw IllegalStateException("This game is already full of waiting users (${usersMax}): ${users}")
        }
        users.add(user)
    }

    fun containsWaiting(user: User) = users.contains(user)

    override fun toString() = MoreObjects.toStringHelper(this).add("id", id).add("usersMax", usersMax).toString()

}
