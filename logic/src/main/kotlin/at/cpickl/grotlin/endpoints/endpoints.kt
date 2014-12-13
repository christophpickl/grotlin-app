package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement

data class Fault(val message: String, val code: FaultCode) {
    fun toRto(): FaultRto {
        val rto = FaultRto()
        rto.message = message
        rto.code = code.label
        return rto
    }
}


enum class FaultCode(val label: String) {
    NOT_ALLOWED: FaultCode("NOT_ALLOWED")
    INVALID_PAYLOAD: FaultCode("INVALID_PAYLOAD")
    INVALID_PAGE: FaultCode("INVALID_PAGE")
    INVALID_LOGOUT: FaultCode("INVALID_LOGOUT")
    INVALID_CREDENTIALS: FaultCode("INVALID_CREDENTIALS")
    INTERNAL_ERROR: FaultCode("INTERNAL_ERROR")

    UNAUTHORIZED: FaultCode("UNAUTHORIZED")
    FORBIDDEN: FaultCode("FORBIDDEN")

    GAME_NOT_FOUND: FaultCode("GAME_NOT_FOUND")
}


XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement class FaultRto {
    class object {
        fun build(message: String, code: FaultCode): FaultRto {
            val rto = FaultRto()
            rto.message = message
            rto.code = code.label
            return rto
        }
    }
    var message: String? = null
    var code: String? = null

    override fun equals(other: Any?): Boolean {
        if (other is FaultRto) {
            return message == other.message && code == other.code
        }
        return false
    }

    override fun toString(): String {
        return "FaultRto[message='${message}', code='${code}']"
    }
}
