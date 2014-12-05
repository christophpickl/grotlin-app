
package at.cpickl.grotlin.multi.service

import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.resource.ChannelResource
import com.google.appengine.api.channel.ChannelService
import com.google.appengine.api.channel.ChannelServiceFactory
import com.google.appengine.api.channel.ChannelMessage
import javax.servlet.http.HttpServletRequest

class ChannelApiService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelResource>())
    }

    private val channelService: ChannelService  = ChannelServiceFactory.getChannelService()

    var connectionsCount: Int = 0
        get() = $connectionsCount
        private set(value: Int) { $connectionsCount = value }

    fun createToken(user: User): String {
        LOG.debug("createToken(user=${user})")
        // By default, tokens expire in two hours.
        // If a client remains connected to a channel for longer than the token duration, the sockets onerror() and onclose()
        // callbacks are called. At this point, the client can make an XHR request to the application to request a new token and
        // open a new channel.
        val token = channelService.createChannel(user.accessToken!!)
        return token

    }

    fun sendFoobarMessage(user: User, message: String) {
        channelService.sendMessage(ChannelMessage(user.accessToken!!, message))
    }

    fun onConnected(request: HttpServletRequest) {
        LOG.debug("onConnected(request) .. current connectionsCount=${connectionsCount}")
        connectionsCount++
        val presence = channelService.parsePresence(request) // isConnected:Boolean, clientId:String
        val message = channelService.parseMessage(request) // message:String, clientId:String
        println("presence=${presence}")
        println("message=${message}")

    }

    fun onDisconnected(request: HttpServletRequest) {
        LOG.debug("onDisconnected(request) .. current connectionsCount=${connectionsCount}")
        connectionsCount--
    }

}