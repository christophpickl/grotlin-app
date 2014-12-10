package at.cpickl.agrotlin.service

import at.cpickl.grotlin.channel.ChannelNotification
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.channel.NotificationResponder
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.GameStartsNotificationResponder


class NotificationDistributor: NotificationResponder {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<NotificationDistributor>())
    }

    private val gameStartsNotificationResponders = hashSetOf<GameStartsNotificationResponder>()

    fun distribute(notification: ChannelNotification) {
        LOG.info("distribute(notification={})", notification)
        notification.actOn(this)
    }

    override fun onGameStarts(notification: GameStartsNotification) {
        LOG.info("onGameStarts(notification) gameStartsNotificationResponders.size=${gameStartsNotificationResponders.size()}")
        gameStartsNotificationResponders.forEach { it.onGameStarts(notification) }
    }

    fun register(responder: GameStartsNotificationResponder) {
        LOG.debug("register(responder={})", responder)
        if (gameStartsNotificationResponders.contains(responder)) {
            throw IllegalStateException("Responder already registered: ${responder}!")
        }
        gameStartsNotificationResponders.add(responder)
    }

    fun unregister(responder: GameStartsNotificationResponder) {
        LOG.debug("unregister(responder={})", responder)
        if (!gameStartsNotificationResponders.contains(responder)) {
            throw IllegalStateException("Responder not registered: ${responder}!")
        }
        gameStartsNotificationResponders.remove(responder)
    }

}

