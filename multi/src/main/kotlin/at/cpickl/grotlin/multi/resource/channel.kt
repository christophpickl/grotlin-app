package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import java.util.logging.Logger
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import com.google.appengine.api.channel.ChannelService
import com.google.appengine.api.channel.ChannelServiceFactory
import com.google.appengine.api.channel.ChannelMessage
import javax.ws.rs.core.Response

Path("/channel") public class ChannelResource {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    private val channelKey = "useUserTokenInstead"

    Path("/") POST Produces(MediaType.APPLICATION_JSON)
    public fun createChannelToken(): String {
        // By default, tokens expire in two hours.
        // If a client remains connected to a channel for longer than the token duration, the socket’s onerror() and onclose()
        // callbacks are called. At this point, the client can make an XHR request to the application to request a new token and
        // open a new channel.
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        val token = channelService.createChannel(channelKey)
        return """{ "token": "${token}" }"""
    }

    Path("/push") POST Produces(MediaType.APPLICATION_JSON)
    public fun pushMessage(): String {
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        val message = "my server message"
        channelService.sendMessage(ChannelMessage(channelKey, message))
        return """{ "sent": "${message}" }"""
    }

}

Path("/_ah/channel") public class ChannelPresenceResource {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    Path("/connected") POST
    public fun userConnected(): Response {
        LOG.fine("userConnected()")
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        //        val presence = channelService.parsePresence(request)
        return Response.ok().build()
    }
    Path("/disconnected") POST
    public fun userDisconnected(): Response {
        LOG.fine("userDisconnected()")
        val channelService: ChannelService  = ChannelServiceFactory.getChannelService();
        //        val presence = channelService.parsePresence(request)
        return Response.ok().build()
    }

}
