package at.cpickl.grotlin.multi

import java.util.logging.Logger
import java.util.logging.Level


public fun Logger.exception(message: String, throwable: Throwable) {
    log(Level.SEVERE, message, throwable)
}

public fun String.toIntOrThrow(toThrow: Exception): Int {
    try {
        return toInt()
    } catch(e: NumberFormatException) {
        throw toThrow
    }
}
