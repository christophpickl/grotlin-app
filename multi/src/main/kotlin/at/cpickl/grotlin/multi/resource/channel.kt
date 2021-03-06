// google app engine Channel API endpoints

package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import org.slf4j.LoggerFactory
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.ChannelApiService
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import at.cpickl.grotlin.multi.service.Role
import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.channel.GameStartsNotification
import javax.ws.rs.Consumes
import com.google.common.io.CharStreams
import java.io.InputStreamReader

Path("/channel") class ChannelResource [Inject] (private val channelApiService: ChannelApiService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelResource>())
    }

    Secured Path("/") POST Produces(MediaType.APPLICATION_JSON)
    fun createChannelToken(user: User): String {
        val channelToken = channelApiService.createToken(user)
        return """{ "channelToken": "${channelToken}" }"""
    }

    Secured Path("/push") POST Produces(MediaType.APPLICATION_JSON) Consumes(MediaType.APPLICATION_JSON)
    fun pushMessage(Context request: HttpServletRequest, user: User): String {
//        request.getReader()
        val requestBody = CharStreams.toString(InputStreamReader(request.getInputStream()))
        println("XXXXXXXXXXXXXXX requestBody ='${requestBody}'")
        channelApiService.sendNotification(GameStartsNotification("my game id"), user)
        // TODO consumes Rto containing: targetUser, clientBody==notification instance
        return """{ "sent": true }"""
    }

    private fun transformPushRequest() {

    }

    Secured(Role.ADMIN) Path("/connections") GET Produces(MediaType.APPLICATION_JSON)
    fun listConnections(): String {
        return """{ "connectionsCount": "${channelApiService.connectionsCount}" }"""
    }

}

Path("/_ah/channel") class ChannelPresenceResource [Inject] (private val channelApiService: ChannelApiService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelPresenceResource>())
    }

    Path("/connected") POST
    fun userConnected([Context] request: HttpServletRequest): Response {
        LOG.debug("userConnected(request)")
        channelApiService.onConnected(request)
        return Response.ok().build()
    }

    Path("/disconnected") POST
    fun userDisconnected([Context] request: HttpServletRequest): Response {
        LOG.debug("userDisconnected(request)")
        channelApiService.onDisconnected(request)
        return Response.ok().build()
    }

}
