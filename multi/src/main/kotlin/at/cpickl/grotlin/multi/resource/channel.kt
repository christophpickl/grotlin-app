package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import com.google.appengine.api.channel.ChannelService
import com.google.appengine.api.channel.ChannelServiceFactory
import com.google.appengine.api.channel.ChannelMessage
import javax.ws.rs.core.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.ChannelApiService
import javax.ws.rs.GET
import com.google.appengine.api.channel.ChannelPresence
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import at.cpickl.grotlin.multi.service.Role
import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.GameStartsNotificationRto

Path("/channel") class ChannelResource [Inject] (private val channelApiService: ChannelApiService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelResource>())
    }

    Secured Path("/") POST Produces(MediaType.APPLICATION_JSON)
    fun createChannelToken(user: User): String {
        val channelToken = channelApiService.createToken(user)
        return """{ "channelToken": "${channelToken}" }"""
    }

    Secured Path("/push") POST Produces(MediaType.APPLICATION_JSON)
    fun pushMessage(user: User): String {
        channelApiService.sendNotification(user, GameStartsNotification("my game id"))
        return """{ "sent": true }"""
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
