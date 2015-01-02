package at.cpickl.grotlin.multi.service

import java.util.Properties
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import javax.mail.Message
import javax.mail.Transport
import javax.mail.Address
import org.slf4j.LoggerFactory

data class MailAddress(val email: String, val name: String = "")
data class Mail(val subject: String, val body: String, vararg val receivers: MailAddress)

trait MailSender {
    fun send(mail: Mail)
}

class LocalDummyMailSender : MailSender {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<LocalDummyMailSender>())
    }
    override fun send(mail: Mail) {
        LOG.info("send(mail={}) ... NOT going to send this mail.", mail)
    }
}

// or receive emails: https://cloud.google.com/appengine/docs/java/mail/
class AppEngineMailSender : MailSender {

    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AppEngineMailSender>())
        private val SENDER = MailAddress("foobar@swirl-engine.appspot.com", "swirl faker")
    }

    override fun send(mail: Mail) {
        LOG.debug("send(mail={})", mail)
        val message = MimeMessage(Session.getDefaultInstance(Properties()))
        message.setFrom(InternetAddress(SENDER.email, SENDER.name))
        message.addRecipients(Message.RecipientType.TO, mail.receiversAsJavaxArray())
        message.setSubject(mail.subject)
        message.setText(mail.body)
        Transport.send(message)
    }

    private fun Mail.receiversAsJavaxArray(): Array<Address> {
        return receivers.map({ InternetAddress(it.email, it.name) }).copyToArray()
    }

}
