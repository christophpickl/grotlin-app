package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.service.ServiceModule
import at.cpickl.grotlin.multi.resource.ResourceModule
import javax.ws.rs.core.Response.Status
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext
import java.util.logging.Logger
import java.util.logging.Level


public class AppModule : AbstractModule() {
    override fun configure() {
        install(ResourceModule())
        install(ServiceModule())

        bind(javaClass<CorsInterceptor>())
    }
}
// http://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html/ch30.html
// ... You must allocate this and register it as a singleton provider from your Application class. See the javadoc or its various settings.

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

enum class FaultCode(public val label: String) {
    NOT_ALLOWED: FaultCode("NOT_ALLOWED")
    INVALID_PAYLOAD: FaultCode("INVALID_PAYLOAD")
    INVALID_LOGOUT: FaultCode("INVALID_LOGOUT")
    INVALID_CREDENTIALS: FaultCode("INVALID_CREDENTIALS")
    INTERNAL_ERROR: FaultCode("INTERNAL_ERROR")
}

open class FaultException(message: String, public val status: Status, public val fault: Fault) : RuntimeException(message) {
    override public fun toString(): String {
        return "FaultException[message='${getMessage()}', status='$status', fault=${fault}]"
    }
}


public fun Logger.exception(message: String, throwable: Throwable) {
    log(Level.SEVERE, message, throwable)
}
