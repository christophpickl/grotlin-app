package at.cpickl.grotlin.multi.resource

import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import javax.ws.rs.ext.ExceptionMapper
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.multi.isDebugApp
import javax.validation.ConstraintViolationException
import org.jboss.resteasy.api.validation.ResteasyViolationException

Provider class GeneralExceptionMapper : ExceptionMapper<Exception> {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<GeneralExceptionMapper>())
    }
    override fun toResponse(exception: Exception): Response {
        // TODO MINOR: enhance FaultRto for debug stuff, exception message + stacktrace
        val additionalMessage = if (isDebugApp()) " (exception: ${exception.getMessage()})" else " (please contact admin/review logs)"
        LOG.warn("Unhandled generic exception!", exception)
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(Fault("Server could not process request!${additionalMessage}", FaultCode.INTERNAL_ERROR).toRto()).build()
    }
}

// TODO how to hook into the default violation exception mapper?!
//Provider class ConstraintViolationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
Provider class ResteasyViolationExceptionMapper : ExceptionMapper<ResteasyViolationException> {

    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ResteasyViolationExceptionMapper>())
    }
    override fun toResponse(exception: ResteasyViolationException): Response {
        LOG.warn("Validation failed!!", exception)
        exception.getParameterViolations().forEach { println("Violation: ${it}"); }
        return Response.status(Status.BAD_REQUEST)
                .entity(Fault("Validation failed", FaultCode.INVALID_PAYLOAD).toRto()).build()
    }
}

class ViolationMapper {
    fun foo(exception: ResteasyViolationException): Fault {

        return Fault("", FaultCode.INVALID_PAYLOAD)
    }
}

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

class UserException(message: String, fault: Fault) : FaultException(message, Response.Status.BAD_REQUEST, fault)

class NotFoundException(message: String, fault: Fault) : FaultException(message, Response.Status.NOT_FOUND, fault)
