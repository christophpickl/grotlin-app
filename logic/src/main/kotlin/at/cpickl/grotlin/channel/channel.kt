package at.cpickl.grotlin.channel

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import at.cpickl.grotlin.BattleResult

trait ChannelNotification {
    fun actOn(responder: AllNotificationResponder)
    fun toRto(): NotificationRto
}

trait ChannelNotificationRto: NotificationRto {
    val type: String?
    fun toDomain(): ChannelNotification
}


/** Compound interface. */
trait AllNotificationResponder :
        GameStartsNotificationResponder,
        WaitingGameNotificationResponder,
        AttackNotificationResponder

/** Marker interface only. */
trait NotificationRto

/** Marker interface only. */
trait NotificationResponder

object NotificationRegistry {
    private val registry: Map<String, Class<out ChannelNotificationRto>>
    {
        registry = hashMapOf(
            GameStartsNotificationRto.TYPE to javaClass<GameStartsNotificationRto>(),
                WaitingGameNotificationRto.TYPE to javaClass<WaitingGameNotificationRto>(),
                AttackNotificationRto.TYPE to javaClass<AttackNotificationRto>()
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

// ATTACK NOTIFICATION
// =======================================================

data class AttackNotification(
        val wonUserName: String,
        val regionIdAttacker: String, // TODO MINOR should be of type Region, but would need complex toDomain transformer (from Id to Region)
        val regionIdDefender: String,
        val diceRollAttacker: Int,
        val diceRollDefender: Int
) : ChannelNotification {
    class object {
        val transform: (BattleResult) -> AttackNotification =
                {(result) ->
                    AttackNotification(result.winner.name,
                            result.attackerRegion.id, result.defenderRegion.id,
                            result.attackerDiceRoll, result.defenderDiceRoll)
                }
    }
    override fun actOn(responder: AllNotificationResponder) {
        responder.onAttacked(this)
    }

    override fun toRto(): AttackNotificationRto {
        return AttackNotificationRto.build(wonUserName, regionIdAttacker, regionIdDefender,
                diceRollAttacker, diceRollDefender)
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class AttackNotificationRto (
        override var type: String? = null,
        var wonUserName: String? = null,
        var regionIdAttacker: String? = null,
        var regionIdDefender: String? = null,
        var diceAttacker: Int? = null,
        var diceDefender: Int? = null
) : ChannelNotificationRto {
    class object {
        val TYPE: String = "attack"
        fun build(wonUserName: String, regionIdAttacker: String, regionIdDefender: String,
                  diceAttacker: Int, diceDefender: Int): AttackNotificationRto {
            return AttackNotificationRto(TYPE, wonUserName, regionIdAttacker, regionIdDefender,
                    diceAttacker, diceDefender)
        }
    }
    override fun toDomain(): ChannelNotification = AttackNotification(wonUserName!!,
            regionIdAttacker!!, regionIdDefender!!, diceAttacker!!, diceDefender!!)
}

trait AttackNotificationResponder : NotificationResponder {
    fun onAttacked(notification: AttackNotification)
}
