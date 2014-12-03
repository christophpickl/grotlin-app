package at.cpickl.grotlin.multi.resource

import javax.ws.rs.core.Response
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.ws.rs.ext.Provider
import at.cpickl.grotlin.multi.FaultException
import at.cpickl.grotlin.multi.Fault
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.FaultCode
import javax.ws.rs.ext.ExceptionMapper
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

Provider class FaultExceptionMapper : ExceptionMapper<FaultException> {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<FaultExceptionMapper>())
    }
    override fun toResponse(exception: FaultException): Response {
        LOG.warn("Fault exception!", exception)
        return Response.status(exception.status).entity(exception.fault.toRto()).build()
    }
}

Provider class UnrecognizedPropertyExceptionMapper : ExceptionMapper<UnrecognizedPropertyException> {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<UnrecognizedPropertyExceptionMapper>())
    }
    override fun toResponse(exception: UnrecognizedPropertyException): Response {
        LOG.warn("JSON exception!", exception)
        return Response.status(Status.BAD_REQUEST).entity(Fault(exception.getMessage(), FaultCode.INVALID_PAYLOAD).toRto()).build()
    }
}

// KotlinNullPointerExceptionMapper -> internal server error

// javax.ws.rs.NotFoundException
//Provider class GeneralExceptionMapper : ExceptionMapper<Exception> {
//    override fun toResponse(exception: Exception): Response {
//        LOG.exception("Uncaught exception!", exception)
//        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Fault("Unknown error occured!", FaultCode.INTERNAL_ERROR).toRto()).build()
//    }
//}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class FaultRto {
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

    // strangely the @data generated equals fails...
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

class UserException(message: String, fault: Fault) : FaultException(message, Response.Status.BAD_REQUEST, fault)

class NotFoundException(message: String, fault: Fault) : FaultException(message, Response.Status.NOT_FOUND, fault)

fun Fault.toRto(): FaultRto {
    val rto = FaultRto()
    rto.message = message
    rto.code = code.label
    return rto
}
