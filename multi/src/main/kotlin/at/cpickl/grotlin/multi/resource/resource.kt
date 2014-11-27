package at.cpickl.grotlin.multi.resource

import com.google.inject.AbstractModule
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlElement
import javax.ws.rs.Path
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import at.cpickl.grotlin.multi.service.UserService
import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultCode
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import javax.inject.Inject
//import javax.inject.Inject

public class ResourceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionResource>())
        bind(javaClass<UserResource>())
        bind(javaClass<AdminResource>())

        bind(javaClass<FaultExceptionMapper>())
        bind(javaClass<UnrecognizedPropertyExceptionMapper>())
//        bind(javaClass<GeneralExceptionMapper>())
    }
}

Path("/admin") public class AdminResource [Inject] (private val userService: UserService) {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    Path("/resetDB") GET Produces(MediaType.APPLICATION_JSON)
    public fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!", Fault("You shall not pass!", FaultCode.NOT_ALLOWED))
        }
        // password == "foo"
        userService.saveOrUpdate(User("user1", "m@ail1.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33"))
        userService.saveOrUpdate(User("user2", "m@ail1.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33"))
        return """{ "success": true}"""
    }

}

class AdminException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault)
