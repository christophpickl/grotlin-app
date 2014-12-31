package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.WaitingRandomGameService
import javax.ws.rs.POST
import at.cpickl.grotlin.multi.service.User
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import at.cpickl.grotlin.multi.service.WaitingRandomGame
import at.cpickl.grotlin.multi.service.RunningGameService
import at.cpickl.grotlin.multi.service.Role
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Consumes
import at.cpickl.grotlin.multi.service.AttackOrder
import at.cpickl.grotlin.Map as Mapp
import at.cpickl.grotlin.multi.service.UserGame
import at.cpickl.grotlin.channel.AttackNotificationRto
import javax.ws.rs.core.Response

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

    Secured POST Path("/runningGames/{gameId}/attack") Consumes(MediaType.APPLICATION_JSON)
    fun attackRegion(
            PathParam("gameId") gameId: String,
            attackRto: AttackOrderRto,
            user: User): AttackNotificationRto {
        val game = runningGameService.gameByIdForUser(gameId, user)
        val notification = runningGameService.attackRegion(AttackOrder(user, game,
                game.regionById(attackRto.sourceRegionId!!),
                game.regionById(attackRto.targetRegionId!!)))
        // result to other players will be pushed via channel API
        return notification.toRto()
    }

    // TODO create own resource which has a field property $gameId
    Secured POST Path("/runningGames/{gameId}/endTurn")
    fun endTurn(PathParam("gameId") gameId: String, user: User): Response {
        runningGameService.endTurn(runningGameService.gameByIdForUser(gameId, user), user)
        return Response.ok().build()
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class AttackOrderRto (
        var sourceRegionId: String? = null,
        var targetRegionId: String? = null
) {
    class object {
        //        val transform: (String, String) -> AttackOrderRto =
        //            { (sourceRegionId, targetRegionId) -> AttackOrderRto(sourceRegionId, targetRegionId)}
        fun build(sourceRegionId: String, targetRegionId: String): AttackOrderRto =
                AttackOrderRto(sourceRegionId, targetRegionId)
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class WaitingRandomGameRto (
        var usersMax: Int? = null,
        var usersWaiting: Int? = null,
        var waitingGameId: String? = null // running game will have different ID, pushed via Channel API
) {
    class object {
        val transform: (WaitingRandomGame) -> WaitingRandomGameRto  =
            { (game) -> WaitingRandomGameRto(game.usersMax, game.users.size, game.id) }
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class RunningGameRto(
        var players: Collection<PlayerRto>? = null,
        var map: MappRto? = null
) {
    class object {
        val transform: (UserGame) -> RunningGameRto =
                { (game) ->
                    val rto = RunningGameRto()
                    rto.players = game.users.map(PlayerRto.transform)
                    rto.map = MappRto.transform(game.map)
                    rto
                }
    }

}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class PlayerRto (
        var name: String? = null
) {
    class object {
        val transform: (User) -> PlayerRto =
                {(user) -> PlayerRto(user.name) }
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class MappRto (
        var regions: Collection<RegionRto>? = null
) {
    class object {
        val transform: (Mapp) -> MappRto  =
                { (map) ->
                    val rto = MappRto()
                    rto.regions = map.regions.map { RegionRto(it.id, it.armies, it.owner?.name) }
                    rto
                }
    }
}


XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class RegionRto(
        var id: String? = null,
        var armies: Int? = 0,
        var ownerName: String? = null // could be really null, if not owned by anyone
) {

}