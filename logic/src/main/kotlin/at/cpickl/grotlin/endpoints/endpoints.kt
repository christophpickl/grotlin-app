package at.cpickl.grotlin.endpoints

import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlRootElement


open class ClientException(message: String, cause: Exception? = null) : RuntimeException(message, cause)
