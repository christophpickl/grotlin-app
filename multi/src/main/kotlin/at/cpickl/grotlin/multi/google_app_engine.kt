package at.cpickl.grotlin.multi

import java.util.logging.Logger
import java.util.logging.Level
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import com.google.inject.AbstractModule
import javax.inject.Inject

class MyRepo : Repo {
    override fun save(data: String) = "Hello ${data}!"
}
trait Repo {
    fun save(data: String): String
}

public class ResourceModule : AbstractModule() {

    override fun configure() {
        println("ResourceModule configuring repo ...")
        bind(javaClass<Repo>()).toInstance(MyRepo())
        bind(javaClass<VersionResource>()) // is it even necessary?
    }
}

Path("/version") public class VersionResource [Inject] (private val repo: Repo) {

    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    GET Produces(MediaType.TEXT_PLAIN) public fun getVersion(): Response {
        LOG.log(Level.FINER, "getVersion()")
        return Response.status(200).entity("version=${repo.save("guice")}").build();
    }
}
