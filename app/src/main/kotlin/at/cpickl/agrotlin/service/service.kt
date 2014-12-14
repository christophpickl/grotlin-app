package at.cpickl.agrotlin.service

import android.widget.Toast
import javax.inject.Inject
import android.os.Vibrator
import javax.inject.Singleton
import org.slf4j.LoggerFactory
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.content.SharedPreferences


trait VibrateService {
    fun vibrate(milliseconds: Long = 500)
}

Singleton class AndroidVibrateService [Inject] (private val vibrator: Vibrator) : VibrateService {
    // my girlfriend's one is better! peace out.
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<AndroidVibrateService>())
    }

    override fun vibrate(milliseconds: Long) {
        LOG.debug("vibrate(milliseconds=${milliseconds})")
        vibrator.vibrate(milliseconds)
    }

}

trait AndroidOs {
    fun isNetworkAvailable(context: Context): Boolean
}


class AndroidOsImpl: AndroidOs {
    override fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
trait SettingsManager {
    // actually this is a weird API, requiring any context. i dont care which context, it just needs to be one (MainActivity to init this thing)
    fun isAudioEnabled(context: Context): Boolean
}

class SettingsManagerViaSharedPreferences : SettingsManager {
    override fun isAudioEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).isAudioEnabled()
    }
}

class PreferencesKeys {
    class object {
        val ENABLE_AUDIO: String = "prefEnableAudio"

        // does not hold a value, but is clickable
        val CLEAR_CACHE_BUTTON: String = "prefClearCacheButton"
    }
}
fun SharedPreferences.isAudioEnabled(): Boolean = getBoolean(PreferencesKeys.ENABLE_AUDIO, true)