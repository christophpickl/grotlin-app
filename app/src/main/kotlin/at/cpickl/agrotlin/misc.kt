package at.cpickl.agrotlin

import android.util.Log
import android.app.Activity
import android.widget.Toast
import android.content.Context

fun Context.showToast(message: String, duration: Int = 3000) {
    Toast.makeText(this, message, duration).show()
}

public class Logg(val tag: String) {

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


public class FloatPoint(public val x: Float, public val y: Float) {
    override fun toString(): String {
        return "FloatPoint[" + x + "/" + y + "]"
    }
}
