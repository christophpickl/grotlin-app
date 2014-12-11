package at.cpickl.grotlin.channel

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

trait ChannelNotification {
    fun actOn(responder: AllNotificationResponder)
    fun toRto(): NotificationRto
}

trait ChannelNotificationRto: NotificationRto {
    val type: String?
    fun toDomain(): ChannelNotification
}


/** Compound interface. */
trait AllNotificationResponder : GameStartsNotificationResponder, WaitingGameNotificationResponder

/** Marker interface only. */
trait NotificationRto

/** Marker interface only. */
trait NotificationResponder

object NotificationRegistry {
    private val registry: Map<String, Class<out ChannelNotificationRto>>
    {
        registry = hashMapOf(
            GameStartsNotificationRto.TYPE to javaClass<GameStartsNotificationRto>(),
            WaitingGameNotificationRto.TYPE to javaClass<WaitingGameNotificationRto>()
        )
    }

    fun byType(type: String): Class<ChannelNotificationRto> {
        val found = registry.get(type)
        if (found == null) {
            throw IllegalArgumentException("Unsupported notification type '${type}'!")
        }
        return found as Class<ChannelNotificationRto>
    }

}

// GAME STARTS NOTIFICATION
// =======================================================

data class GameStartsNotification(val gameId: String): ChannelNotification {
    override fun actOn(responder: AllNotificationResponder) {
        responder.onGameStarts(this)
    }

    override fun toRto(): GameStartsNotificationRto {
        return GameStartsNotificationRto.build(gameId)
    }
}

// Unable to resolve Lat/cpickl/grotlin/channel/GameStartsNotificationRto; annotation class 3xxx
XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class GameStartsNotificationRto (
        override var type: String? = null,
        var gameId: String? = null
) : ChannelNotificationRto {
    class object {
        val TYPE: String = "gameStarts"
        fun build(gameId: String): GameStartsNotificationRto {
            return GameStartsNotificationRto(TYPE, gameId)
        }
    }
    override fun toDomain(): ChannelNotification = GameStartsNotification(gameId!!)
}

trait GameStartsNotificationResponder: NotificationResponder {
    fun onGameStarts(notification: GameStartsNotification)
}

// WAITING GAME NOTIFICATION
// =======================================================

data class WaitingGameNotification(private val newUsersCount: Int): ChannelNotification {
    override fun actOn(responder: AllNotificationResponder) {
        responder.onWaitingGame(this)
    }

    override fun toRto(): WaitingGameNotificationRto {
        return WaitingGameNotificationRto.build(newUsersCount)
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class WaitingGameNotificationRto (
        override var type: String? = null,
        var newUsersCount: Int? = null
) : ChannelNotificationRto {
    class object {
        val TYPE: String = "waitingGame"
        fun build(newUsersCount: Int): WaitingGameNotificationRto {
            return WaitingGameNotificationRto(TYPE, newUsersCount)
        }
    }
    override fun toDomain(): ChannelNotification = WaitingGameNotification(newUsersCount!!)
}

trait WaitingGameNotificationResponder: NotificationResponder {
    fun onWaitingGame(notification: WaitingGameNotification)
}