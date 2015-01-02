package at.cpickl.grotlin.multi.resource

import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import at.cpickl.grotlin.endpoints.Fault
import at.cpickl.grotlin.endpoints.FaultCode
import at.cpickl.grotlin.multi.service.WaitingRandomGameService
import at.cpickl.grotlin.multi.service.AdminService
import at.cpickl.grotlin.multi.service.Mail
import at.cpickl.grotlin.multi.service.MailAddress
import at.cpickl.grotlin.multi.service.MailSender

Path("/admin")
Produces(MediaType.APPLICATION_JSON)
class AdminResource [Inject] (
        private val adminService: AdminService,
        private val gameService: WaitingRandomGameService,
        private val mailSender: MailSender
) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AdminResource>())
    }

    GET Path("/dummy")
    fun dummy(): String {
        LOG.info("dummy()")
        mailSender.send(Mail("subject", "some text", MailAddress("christoph.pickl@gmail.com")))
        return """{ "success": "mail sent!" } """
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
