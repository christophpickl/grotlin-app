package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.service.ServiceModule
import at.cpickl.grotlin.multi.resource.ResourceModule
import javax.ws.rs.core.Response.Status
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext
import org.jboss.resteasy.spi.HttpRequest
import org.jboss.resteasy.core.ResourceMethodInvoker
import org.jboss.resteasy.core.ServerResponse


public class AppModule : AbstractModule() {
    override fun configure() {
        install(ResourceModule())
        install(ServiceModule())

        bind(javaClass<CorsInterceptor>())
    }
}

// https://code.google.com/r/sergiobossa-terrastore-kryo/source/browse/src/main/java/terrastore/server/impl/cors/CorsInterceptor.java
Provider ServerInterceptor public class CorsInterceptor: MessageBodyWriterInterceptor {
    override fun write(context: MessageBodyWriterContext?) {
        if (context == null) {
            return
        }
        context.getHeaders().add("Access-Control-Allow-Origin", "*");
        context.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,UPDATE,OPTIONS");
        context.getHeaders().add("Access-Control-Allow-Headers", "x-http-method-override");
        context.getHeaders().add("Access-Control-Max-Age", "");
        context.proceed()
    }
}

data class Fault(public val message: String, public val code: FaultCode)

enum class FaultCode(val label: String) {
    NOT_ALLOWED: FaultCode("NOT_ALLOWED")
}

abstract class FaultException(message: String, public val status: Status, public val fault: Fault) : RuntimeException(message)
