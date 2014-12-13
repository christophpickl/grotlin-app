package at.cpickl.agrotlin.service

import at.cpickl.grotlin.channel.ChannelNotification
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.channel.AllNotificationResponder
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.GameStartsNotificationResponder
import at.cpickl.grotlin.channel.WaitingGameNotification
import at.cpickl.grotlin.channel.WaitingGameNotificationResponder
import at.cpickl.grotlin.channel.NotificationResponder


class NotificationDistributor: AllNotificationResponder {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<NotificationDistributor>())
    }

    fun distribute(notification: ChannelNotification) {
        LOG.info("distribute(notification={})", notification)
        notification.actOn(this)
    }

    private val gameStartsNotificationResponders = hashSetOf<GameStartsNotificationResponder>()
    override fun onGameStarts(notification: GameStartsNotification) {
        LOG.info("onGameStarts(notification) gameStartsNotificationResponders.size=${gameStartsNotificationResponders.size()}")
        gameStartsNotificationResponders.forEach { it.onGameStarts(notification) }
    }
    fun register(responder: GameStartsNotificationResponder) = registerAny(responder, gameStartsNotificationResponders)
    fun unregister(responder: GameStartsNotificationResponder) = unregisterAny(responder, gameStartsNotificationResponders)

    private val waitingGameNotificationResponder = hashSetOf<WaitingGameNotificationResponder>()
    override fun onWaitingGame(notification: WaitingGameNotification) {
        LOG.info("onWaitingGame(notification) gameStartsNotificationResponders.size=${waitingGameNotificationResponder.size()}")
        waitingGameNotificationResponder.forEach { it.onWaitingGame(notification) }
    }
    fun register(responder: WaitingGameNotificationResponder) = registerAny(responder, waitingGameNotificationResponder)
    fun unregister(responder: WaitingGameNotificationResponder) = unregisterAny(responder, waitingGameNotificationResponder)


    private fun <R: NotificationResponder> registerAny(responder: R, responders: MutableCollection<R>) {
        LOG.debug("registerAny(responder={})", responder)
        if (responders.contains(responder)) {
            throw IllegalStateException("Responder already registered: ${responder}!")
        }
        responders.add(responder)
    }

    private fun <R: NotificationResponder> unregisterAny(responder: R, responders: MutableCollection<R>) {
        LOG.debug("unregisterAny(responder={})", responder)
        if (!responders.contains(responder)) {
            throw IllegalStateException("Responder not registered: ${responder}!")
        }
        responders.remove(responder)
    }

}

