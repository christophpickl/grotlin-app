package at.cpickl.agrotlin

import android.util.Log

public class Logg(_tag: String) {

    private val tag: String = "grotlin.${_tag}"

    public fun trace(message: String) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message)
        }
    }

    public fun debug(message: String) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message)
        }
    }

    public fun info(message: String) {
        if (Log.isLoggable(tag, Log.INFO)) {
            Log.i(tag, message)
        }
    }

    public fun warn(message: String) {
        if (Log.isLoggable(tag, Log.WARN)) {
            Log.w(tag, message)
        }
    }

    public fun error(message: String) {
        if (Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, message)
        }
    }
}

