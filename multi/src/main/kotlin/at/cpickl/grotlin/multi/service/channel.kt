
package at.cpickl.grotlin.multi.service

import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.resource.ChannelResource
import com.google.appengine.api.channel.ChannelService
import com.google.appengine.api.channel.ChannelServiceFactory
import com.google.appengine.api.channel.ChannelMessage
import javax.servlet.http.HttpServletRequest
import at.cpickl.grotlin.channel.ChannelNotificationRto
import at.cpickl.grotlin.JsonMarshaller
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.GameStartsNotificationRto
import at.cpickl.grotlin.channel.ChannelNotification

trait ChannelApiService {
    val connectionsCount: Int

    fun createToken(user: User): String

    fun sendNotification(notification: ChannelNotification, user: User)
    fun sendNotification(notification: ChannelNotification, users: Collection<User>)

    fun onConnected(request: HttpServletRequest)
    fun onDisconnected(request: HttpServletRequest)
}

class ChannelApiServiceImpl : ChannelApiService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ChannelResource>())
    }

    private val channelService: ChannelService  = ChannelServiceFactory.getChannelService()
    private val marshaller = JsonMarshaller()

    override var connectionsCount: Int = 0
        get() = $connectionsCount
        private set(value: Int) { $connectionsCount = value }

    override fun createToken(user: User): String {
        LOG.debug("createToken(user=${user})")
        // By default, tokens expire in two hours.
        // If a client remains connected to a channel for longer than the token duration, the sockets onerror() and onclose()
        // callbacks are called. At this point, the client can make an XHR request to the application to request a new token and
        // open a new channel.
        val token = channelService.createChannel(user.accessToken!!)
        return token
    }

    override fun sendNotification(notification: ChannelNotification, user: User) {
        sendNotification(notification, linkedListOf(user))
    }

    override fun sendNotification(notification: ChannelNotification, users: Collection<User>) {
        LOG.debug("sendNotification(notification={}, users={})", notification, users)
        val json = marshaller.toJson(notification.toRto())
        for (user in users) {
            channelService.sendMessage(ChannelMessage(user.accessToken!!, json))
        }
    }

    override fun onConnected(request: HttpServletRequest) {
        LOG.debug("onConnected(request) .. current connectionsCount=${connectionsCount}")
        connectionsCount++
        val presence = channelService.parsePresence(request) // isConnected:Boolean, clientId:String
        val message = channelService.parseMessage(request) // message:String, clientId:String
        println("presence=${presence}")
        println("message=${message}")

    }

    override fun onDisconnected(request: HttpServletRequest) {
        LOG.debug("onDisconnected(request) .. current connectionsCount=${connectionsCount}")
        connectionsCount--
    }

}