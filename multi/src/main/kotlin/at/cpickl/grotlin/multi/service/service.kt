package at.cpickl.grotlin.multi.service

import com.google.inject.AbstractModule
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import com.google.inject.Scopes
import java.util.UUID

fun randomUUID() = UUID.randomUUID().toString()

class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionService>()).toInstance(PropertiesVersionService("/swirl.config.properties"))
        bind(javaClass<UserService>()).toInstance(ObjectifyUserService())
        bind(javaClass<AuthUserService>())
        bind(javaClass<FakeUserReader>()) // needs to be in context, but will not be used if debug app is not enabled
        bind(javaClass<WaitingRandomGameService>()).`in`(Scopes.SINGLETON)
        bind(javaClass<RunningGameService>()).to(javaClass<InMemoryRunningGameService>()).`in`(Scopes.SINGLETON)
        bind(javaClass<AdminService>())
        bind(javaClass<ChannelApiService>()).to(javaClass<ChannelApiServiceImpl>()).`in`(Scopes.SINGLETON)
    }
}

class TechException(message: String, faultMessage: String) : FaultException(message, Status.INTERNAL_SERVER_ERROR, Fault(faultMessage, FaultCode.INTERNAL_ERROR))
