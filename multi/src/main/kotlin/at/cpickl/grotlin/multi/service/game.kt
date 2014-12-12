package at.cpickl.grotlin.multi.service

import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.resource.AdminResource
import com.google.common.base.Objects
import com.google.common.base.MoreObjects
import javax.inject.Inject
import at.cpickl.grotlin.multi.resource.NotFoundException
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.multi.resource.UserException
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.WaitingGameNotification

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
            val game = WaitingRandomGame(idGenerator.generate(), maxPlayers)
            waitingGames.add(game)
            game.addWaitingUser(user)
            return game
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
            runningGameService.addNewGame(RunningGame(idGenerator.generate(), firstGame.users))

        }
        return firstGame
    }

}

public trait RunningGameService {
    public val runningGames: Collection<RunningGame>

    public fun addNewGame(game: RunningGame)
    public fun gameByIdForUser(gameId: String, user: User): RunningGame
}

class InMemoryRunningGameService [Inject] (private val channelApiService: ChannelApiService) : RunningGameService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<InMemoryRunningGameService>())
    }

    private val gamesById = hashMapOf<String, RunningGame>()
    override public val runningGames: Collection<RunningGame>
        get() = gamesById.values()

    override public fun addNewGame(game: RunningGame) {
        LOG.info("addNewGame(game=${game})")
        channelApiService.sendNotification(GameStartsNotification(game.id), game.users)
        gamesById.put(game.id, game)
    }

    override public fun gameByIdForUser(gameId: String, user: User): RunningGame {
        LOG.debug("gameByIdForUser(gameId='${gameId}', user=${user})")
        val game = gamesById.get(gameId)
        if (game == null) {
            throw NotFoundException("Game with ID '${gameId}' does not exist!", Fault("Game not found", FaultCode.GAME_NOT_FOUND))
        }
        if (!game.users.contains(user)) {
            throw UserException("${user} is not part of the ${game}!", Fault("Game not accessible", FaultCode.FORBIDDEN))
        }
        return game
    }

}

data class RunningGame(val id: String, val users: Collection<User>) {
    {
        if (users.empty) throw IllegalArgumentException("Users must not be empty!")
    }
    // currentPlayer's turn
    // map
}

data class WaitingRandomGame(val id: String, val usersMax: Int) {
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
