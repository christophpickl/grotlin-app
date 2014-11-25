package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.service.ServiceModule
import at.cpickl.grotlin.multi.resource.ResourceModule
import javax.ws.rs.core.Response.Status

public class AppModule : AbstractModule() {
    override fun configure() {
        install(ResourceModule())
        install(ServiceModule())
    }
}

data class Fault(public val message: String, public val code: FaultCode)

enum class FaultCode(val label: String) {
    NOT_ALLOWED: FaultCode("NOT_ALLOWED")
}

abstract class FaultException(message: String, public val status: Status, public val fault: Fault) : RuntimeException(message)
