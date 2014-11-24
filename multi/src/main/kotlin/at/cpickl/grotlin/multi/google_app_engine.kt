package at.cpickl.grotlin.multi

import java.util.logging.Logger
import java.util.logging.Level
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import org.glassfish.jersey.server.ResourceConfig

Path("/version") public class VersionResource {

    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    GET Produces(MediaType.TEXT_PLAIN) public fun getVersion(): Response {
        LOG.log(Level.FINER, "getVersion()")
        return Response.status(200).entity("version=jersey").build();
    }
}
