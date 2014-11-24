package at.cpickl.grotlin.multi

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.logging.Logger
import java.util.logging.Level

fun main(args: Array<String>) {
    println("app engine ahoi")
}

public class VersionServlet : HttpServlet() {

    class object {
        private val LOG: Logger = Logger.getLogger(javaClass.getSimpleName())
    }

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        LOG.log(Level.FINER, "doGet()")
        response.getWriter().write("version=foobar")
    }

}
