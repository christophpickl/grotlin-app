package at.cpickl.grotlin.multi.service

import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.FaultCode

public class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionService>()).toInstance(PropertiesVersionService("/swirl.config.properties"))
        bind(javaClass<UserService>()).toInstance(ObjectifyUserService())
    }
}

class TechException(message: String, faultMessage: String) : FaultException(message, Status.INTERNAL_SERVER_ERROR, Fault(faultMessage, FaultCode.INTERNAL_ERROR))
