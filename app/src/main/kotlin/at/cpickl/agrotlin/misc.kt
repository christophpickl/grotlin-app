package at.cpickl.agrotlin

import android.util.Log
import android.app.Activity
import android.widget.Toast
import android.content.Context

fun Context.showToast(message: String, duration: Int = 3000) {
    Toast.makeText(this, message, duration).show()
}

public class FloatPoint(public val x: Float, public val y: Float) {
    override fun toString(): String {
        return "FloatPoint[" + x + "/" + y + "]"
    }
}
