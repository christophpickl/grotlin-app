package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import at.cpickl.grotlin.multi.service.UserService
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultCode
import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.multi.service.Role
import at.cpickl.grotlin.multi.FaultException
import javax.ws.rs.core.Response.Status
import at.cpickl.grotlin.multi.service.WaitingRandomGameService
import at.cpickl.grotlin.multi.service.AdminService

Path("/admin")
Produces(MediaType.APPLICATION_JSON)
class AdminResource [Inject] (
        private val adminService: AdminService,
        private val gameService: WaitingRandomGameService
) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AdminResource>())
    }

    GET Path("/resetDB")
    fun resetDatabase(QueryParam("secret") secret: String?): String {
        if (secret == null) {
            throw UserException("Query param 'secret' missing!", Fault("What are you trying?", FaultCode.FORBIDDEN))
        }
        val result = adminService.resetDatabase(secret)
        return """{ "success": ${result}}"""
    }

}
