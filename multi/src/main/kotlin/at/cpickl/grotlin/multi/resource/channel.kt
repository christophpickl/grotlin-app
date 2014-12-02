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

Path("/channel") class ChannelResource {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelResource>())
    }

    private val channelKey = "useUserTokenInstead"

    Path("/") POST Produces(MediaType.APPLICATION_JSON)
    fun createChannelToken(): String {
        // By default, tokens expire in two hours.
        // If a client remains connected to a channel for longer than the token duration, the sockets onerror() and onclose()
        // callbacks are called. At this point, the client can make an XHR request to the application to request a new token and
        // open a new channel.
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        val token = channelService.createChannel(channelKey)
        return """{ "token": "${token}" }"""
    }

    Path("/push") POST Produces(MediaType.APPLICATION_JSON)
    fun pushMessage(): String {
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        val message = "my server message"
        channelService.sendMessage(ChannelMessage(channelKey, message))
        return """{ "sent": "${message}" }"""
    }

}

Path("/_ah/channel") class ChannelPresenceResource {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelPresenceResource>())
    }
    Path("/connected") POST
    fun userConnected(): Response {
        LOG.debug("userConnected()")
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        //        val presence = channelService.parsePresence(request)
        return Response.ok().build()
    }
    Path("/disconnected") POST
    fun userDisconnected(): Response {
        LOG.debug("userDisconnected()")
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        //        val presence = channelService.parsePresence(request)
        return Response.ok().build()
    }

}
