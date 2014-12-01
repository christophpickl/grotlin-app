package at.cpickl.grotlin.multi.resource

import javax.ws.rs.ext.Provider
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.ext.MessageBodyReader
import java.lang.reflect.Type
import javax.ws.rs.core.MultivaluedMap
import java.io.InputStream
import com.googlecode.objectify.cmd.LoadType
import at.cpickl.grotlin.multi.Fault
import at.cpickl.grotlin.multi.FaultCode
import com.googlecode.objectify.cmd.Query
import at.cpickl.grotlin.multi.toIntOrThrow
import java.util.logging.Logger

//Provider Produces(MediaType.WILDCARD) public class InjectPaginationInterceptor : ContextResolver<Pagination> {
//    Context private var request: HttpServletRequest? = null
//    override fun getContext(type: Class<out Any?>?): Pagination? {
//        return Pagination(0, 100)
//    }
//}

Provider Consumes(MediaType.WILDCARD) public class PaginationReader : MessageBodyReader<Pagination> {
    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }
    Context private var request: HttpServletRequest? = null

    override fun readFrom(type: Class<Pagination>?,
                          genericType: Type?,
                          annotations: Array<out Annotation>?,
                          mediaType: MediaType?,
                          httpHeaders: MultivaluedMap<String, String>?,
                          entityStream: InputStream?): Pagination? {
        LOG.finest("readFrom(type=${type}) ... request=${request}")
        var pageNumberString = request!!.getParameter("page")
        var pageSizeString = request!!.getParameter("size")
        if (pageNumberString == null && pageSizeString == null) {
            return Pagination.all()
        }
        if (pageNumberString == null) {
            throw UserException("pageNumberString == null", Fault("Page size was defined but not page number!", FaultCode.INVALID_PAGE))
        }
        if (pageSizeString == null) {
            throw UserException("pageSizeString == null", Fault("Page number was defined but not page size!", FaultCode.INVALID_PAGE))
        }
        val pageNumber: Int = pageNumberString.toIntOrThrow(UserException("Non number given '${pageNumberString}'!", Fault("Invalid characters for page number!", FaultCode.INVALID_PAGE)))
        val pageSize: Int = pageSizeString.toIntOrThrow(UserException("Non number given '${pageSizeString}'!", Fault("Invalid characters for page size!", FaultCode.INVALID_PAGE)))
        if (pageNumber < 0) {
            throw UserException("pageNumber (${pageNumber}) < 0", Fault("Page number must not be negative!", FaultCode.INVALID_PAGE))
        }
        if (pageSize < 0) {
            throw UserException("pageSize (${pageSize}) < 0", Fault("Page size must not be negative!", FaultCode.INVALID_PAGE))
        }
        return Pagination(pageNumber, pageSize)
    }

    override fun isReadable(type: Class<out Any?>?, genericType: Type?, annotations: Array<out Annotation>?, mediaType: MediaType?): Boolean {
        return type == javaClass<Pagination>()
    }

}


fun <T> LoadType<T>.paginate(pagination: Pagination): Query<T> = limit(pagination.size).offset(pagination.number * pagination.size)

public data class Pagination(public val number: Int, public val size: Int) {
    class object {
        fun all(): Pagination = Pagination(0, Integer.MAX_VALUE)
    }
}
