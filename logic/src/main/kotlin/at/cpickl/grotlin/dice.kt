package at.cpickl.grotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory


trait Dice {
    // postcondition: return 1-6
    fun roll(): Int
}

class RealDice : Dice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun roll(): Int {
        val number = (Math.random() * 6).toInt() + 1
        log.trace("rolled: {}", number)
        return number
    }
}
