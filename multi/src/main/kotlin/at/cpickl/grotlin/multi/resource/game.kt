package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.WaitingRandomGameService
import javax.ws.rs.POST
import javax.ws.rs.core.Response
import at.cpickl.grotlin.multi.service.User
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import at.cpickl.grotlin.multi.service.WaitingRandomGame
import at.cpickl.grotlin.multi.service.RunningGameService
import at.cpickl.grotlin.multi.service.Role
import javax.ws.rs.GET
import com.google.common.base.MoreObjects
import at.cpickl.grotlin.multi.service.RunningGame
import javax.ws.rs.PathParam


Path("/game")
Produces(MediaType.APPLICATION_JSON)
class GameResource [Inject](
        private val waitingRandomGameService: WaitingRandomGameService,
        private val runningGameService: RunningGameService
) {

    Secured POST Path("/random")
    fun getExistingOrCreateNewRandomGame(user: User): WaitingRandomGameRto {
        val game = waitingRandomGameService.getOrCreateRandomGame(user)
        return WaitingRandomGameRto.transform(game)
    }

    Secured(Role.ADMIN) GET Path("/waitingRandomGames")
    fun listWaitingRandomGames(): Collection<WaitingRandomGameRto> {
        return waitingRandomGameService.waitingGames.map(WaitingRandomGameRto.transform)
    }

    Secured(Role.ADMIN) GET Path("/runningGames")
    fun listRunningGames(): Collection<RunningGameRto> {
        return runningGameService.runningGames.map(RunningGameRto.transform)
    }

    Secured GET Path("/runningGames/{gameId}")
    fun getRunningGames(PathParam("gameId") gameId: String, user: User): RunningGameRto {
        return RunningGameRto.transform(runningGameService.gameByIdForUser(gameId, user))
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class WaitingRandomGameRto {
    class object {
        val transform: (WaitingRandomGame) -> WaitingRandomGameRto  =
            { (game) ->
                val rto = WaitingRandomGameRto()
                rto.usersMax = game.usersMax
                rto.usersWaiting = game.users.size
                rto
            }
    }
    var usersMax: Int? = null
    var usersWaiting: Int? = null
    override fun toString() = MoreObjects.toStringHelper(this).add("usersMax", usersMax).add("usersWaiting", usersWaiting).toString()
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class RunningGameRto {
    class object {
        val transform: (RunningGame) -> RunningGameRto  =
                { (game) ->
                    val rto = RunningGameRto()
                    rto.users = game.users
                    rto
                }
    }
    var users: Collection<User>? = null
    override fun toString() = MoreObjects.toStringHelper(this).add("users", users).toString()
}
