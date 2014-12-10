package at.cpickl.grotlin.channel

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

/** Marker interface only. */
trait ChannelNotificationRto {
    val type: String?
    fun toDomain(): ChannelNotification
}

trait GameStartsNotificationResponder {
    fun onGameStarts(notification: GameStartsNotification)
}

trait NotificationResponder: GameStartsNotificationResponder {
}

trait ChannelNotification {
    fun actOn(responder: NotificationResponder)
}

object NotificationRegistry {
    private val registry: Map<String, Class<out ChannelNotificationRto>>
    {
        registry = hashMapOf(
            GameStartsNotificationRto.TYPE to javaClass<GameStartsNotificationRto>()
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

//12-10 10:10:49.586    4049-4309/at.cpickl.agrotlin E/dalvikvm﹕ Unable to resolve Lat/cpickl/grotlin/channel/GameStartsNotificationRto; annotation class 3346
//12-10 10:10:49.586    4049-4309/at.cpickl.agrotlin E/dalvikvm﹕ Unable to resolve Lat/cpickl/grotlin/channel/GameStartsNotificationRto; annotation class 3348
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

data class GameStartsNotification(val gameId: String): ChannelNotification {
    override fun actOn(responder: NotificationResponder) {
        responder.onGameStarts(this)
    }

    fun toRto(): GameStartsNotificationRto {
        return GameStartsNotificationRto.build(gameId)
    }
}
