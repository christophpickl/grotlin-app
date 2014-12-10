package at.cpickl.grotlin.channel

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

/** Marker interface only. */
trait ChannelNotification<D> {
    val type: String?
    fun toDomain(): D
}
//12-10 10:10:49.586    4049-4309/at.cpickl.agrotlin E/dalvikvm﹕ Unable to resolve Lat/cpickl/grotlin/channel/GameStartsNotificationRto; annotation class 3346
//12-10 10:10:49.586    4049-4309/at.cpickl.agrotlin E/dalvikvm﹕ Unable to resolve Lat/cpickl/grotlin/channel/GameStartsNotificationRto; annotation class 3348
XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class GameStartsNotificationRto (
        override var type: String? = null,
        var gameId: String? = null
) : ChannelNotification<GameStartsNotification> {
    class object {
        val TYPE: String = "gameStarts"
        fun build(gameId: String): GameStartsNotificationRto {
            return GameStartsNotificationRto(TYPE, gameId)
        }
    }

    override fun toDomain(): GameStartsNotification {
        return GameStartsNotification(gameId!!)
    }
}

data class GameStartsNotification(val gameId: String) {
    fun toRto(): GameStartsNotificationRto {
        return GameStartsNotificationRto.build(gameId)
    }
}
