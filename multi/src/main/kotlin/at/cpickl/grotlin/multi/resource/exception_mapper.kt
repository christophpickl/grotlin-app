package at.cpickl.grotlin.multi.resource

import javax.ws.rs.core.Response
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.ws.rs.ext.Provider
import at.cpickl.grotlin.multi.FaultException
import at.cpickl.grotlin.multi.Fault
import java.util.logging.Logger
import at.cpickl.grotlin.multi.exception
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.FaultCode
import javax.ws.rs.ext.ExceptionMapper
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException

Provider class FaultExceptionMapper : ExceptionMapper<FaultException> {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    override fun toResponse(exception: FaultException): Response {
        LOG.exception("Fault exception!", exception)
        return Response.status(exception.status).entity(exception.fault.toRto()).build()
    }
}

Provider class UnrecognizedPropertyExceptionMapper : ExceptionMapper<UnrecognizedPropertyException> {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    override fun toResponse(exception: UnrecognizedPropertyException): Response {
        LOG.exception("JSON exception!", exception)
        return Response.status(Status.BAD_REQUEST).entity(Fault(exception.getMessage(), FaultCode.INVALID_PAYLOAD).toRto()).build()
    }
}

// javax.ws.rs.NotFoundException
//Provider class GeneralExceptionMapper : ExceptionMapper<Exception> {
//    class object {
//        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
//    }
//    override fun toResponse(exception: Exception): Response {
//        LOG.exception("Uncaught exception!", exception)
//        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Fault("Unknown error occured!", FaultCode.INTERNAL_ERROR).toRto()).build()
//    }
//}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement data class FaultRto {
    class object {
        public fun build(message: String, code: FaultCode): FaultRto {
            val rto = FaultRto()
            rto.message = message
            rto.code = code.label
            return rto
        }
    }
    public var message: String? = null
    public var code: String? = null

    // strangely the @data generated equals fails...
    override fun equals(other: Any?): Boolean {
        if (other is FaultRto) {
            return message == other.message && code == other.code
        }
        return false
    }

    override public fun toString(): String {
        return "FaultRto[message='${message}', code='${code}']"
    }
}

public fun Fault.toRto(): FaultRto {
    val rto = FaultRto()
    rto.message = message
    rto.code = code.label
    return rto
}
