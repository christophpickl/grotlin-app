package at.cpickl.grotlin.multi.service

import javax.inject.Inject
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.multi.resource.AdminResource
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode

class AdminService [Inject] (private val userService: UserService) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AdminResource>())
    }

    fun resetDatabase(secret: String): Boolean {
        if (secret != "hans") {
            throw AdminException("Invalid secret '${secret}'!", Fault("You shall not pass!", FaultCode.NOT_ALLOWED))
        }
        if (!userService.loadAll().empty) {
            return false
        }
        // password == "foo"
        userService.saveOrUpdate(User("user1", "m@ail1.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.ADMIN))
        userService.saveOrUpdate(User("user2", "m@ail2.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        userService.saveOrUpdate(User("user3", "m@ail3.at", "0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", Role.USER))
        return true
    }

}

class AdminException(message: String, fault: Fault) : FaultException(message, Status.FORBIDDEN, fault)
