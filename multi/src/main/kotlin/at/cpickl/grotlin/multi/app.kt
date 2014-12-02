package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.service.ServiceModule
import at.cpickl.grotlin.multi.resource.ResourceModule
import javax.ws.rs.core.Response.Status
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext


fun isDebugApp(): Boolean = System.getProperty("appDebug", "false").equals("true")

class AppModule : AbstractModule() {
    override fun configure() {
        install(ResourceModule())
        install(ServiceModule())

        bind(javaClass<CorsInterceptor>())
    }
}
// http://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html/ch30.html
// ... You must allocate this and register it as a singleton provider from your Application class. See the javadoc or its various settings.

// https://code.google.com/r/sergiobossa-terrastore-kryo/source/browse/src/main/java/terrastore/server/impl/cors/CorsInterceptor.java
Provider ServerInterceptor class CorsInterceptor: MessageBodyWriterInterceptor {
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

data class Fault(val message: String, val code: FaultCode)

enum class FaultCode(val label: String) {
    NOT_ALLOWED: FaultCode("NOT_ALLOWED")
    INVALID_PAYLOAD: FaultCode("INVALID_PAYLOAD")
    INVALID_PAGE: FaultCode("INVALID_PAGE")
    INVALID_LOGOUT: FaultCode("INVALID_LOGOUT")
    INVALID_CREDENTIALS: FaultCode("INVALID_CREDENTIALS")
    INTERNAL_ERROR: FaultCode("INTERNAL_ERROR")

    UNAUTHORIZED: FaultCode("UNAUTHORIZED")
    FORBIDDEN: FaultCode("FORBIDDEN")

}

open class FaultException(message: String, val status: Status, val fault: Fault) : RuntimeException(message) {
    override fun toString(): String {
        return "FaultException[message='${getMessage()}', status='$status', fault=${fault}]"
    }
}

