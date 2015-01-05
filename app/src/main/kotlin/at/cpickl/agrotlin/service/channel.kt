package at.cpickl.agrotlin.service

import at.cpickl.grotlin.channel.ChannelNotification
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.channel.AllNotificationResponder
import at.cpickl.grotlin.channel.GameStartsNotification
import at.cpickl.grotlin.channel.GameStartsNotificationResponder
import at.cpickl.grotlin.channel.WaitingGameNotification
import at.cpickl.grotlin.channel.WaitingGameNotificationResponder
import at.cpickl.grotlin.channel.NotificationResponder
import at.cpickl.grotlin.channel.AttackNotification
import at.cpickl.grotlin.channel.AttackNotificationResponder


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

    private val waitingGameNotificationResponders = hashSetOf<WaitingGameNotificationResponder>()
    override fun onWaitingGame(notification: WaitingGameNotification) {
        LOG.info("onWaitingGame(notification) gameStartsNotificationResponders.size=${waitingGameNotificationResponders.size()}")
        waitingGameNotificationResponders.forEach { it.onWaitingGame(notification) }
    }
    fun register(responder: WaitingGameNotificationResponder) = registerAny(responder, waitingGameNotificationResponders)
    fun unregister(responder: WaitingGameNotificationResponder) = unregisterAny(responder, waitingGameNotificationResponders)


    private val attackNotificationResponders = hashSetOf<AttackNotificationResponder>()
    override fun onAttacked(notification: AttackNotification) {
        LOG.info("onAttacked(notification) attackNotificationResponders.size=${attackNotificationResponders.size()}")
        attackNotificationResponders.forEach { it.onAttacked(notification) }
    }
    fun register(responder: AttackNotificationResponder) = registerAny(responder, attackNotificationResponders)
    fun unregister(responder: AttackNotificationResponder) = unregisterAny(responder, attackNotificationResponders)


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

