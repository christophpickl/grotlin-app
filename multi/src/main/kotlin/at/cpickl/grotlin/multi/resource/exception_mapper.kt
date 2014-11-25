package at.cpickl.grotlin.multi.resource

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.ws.rs.ext.Provider
import at.cpickl.grotlin.multi.FaultException
import at.cpickl.grotlin.multi.Fault

Provider class FaultExceptionMapper : ExceptionMapper<FaultException> {
    override fun toResponse(exception: FaultException): Response {
        return Response.status(exception.status).entity(FaultRto.by(exception.fault)).build()
    }
}

XmlAccessorType(XmlAccessType.PROPERTY) XmlRootElement class FaultRto {
    class object {
        fun by(fault: Fault): FaultRto {
            val rto = FaultRto()
            rto.message = fault.message
            rto.code = fault.code.label
            return rto
        }
    }
    public var message: String? = null
    public var code: String? = null
}
