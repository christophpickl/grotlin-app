package at.cpickl.agrotlin.activity

import roboguice.activity.RoboActivity
import android.os.Bundle
import at.cpickl.agrotlin.AndroidUtil
import org.slf4j.LoggerFactory
import at.cpickl.agrotlin.R
import android.preference.PreferenceActivity
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.SharedPreferences
import at.cpickl.agrotlin.service.PreferencesKeys
import at.cpickl.agrotlin.service.isAudioEnabled
import android.preference.Preference


open class SwirlActivity() : RoboActivity() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<SwirlActivity>())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<RoboActivity>.onCreate(savedInstanceState)

        AndroidUtil.fullscreen(this)
    }
}

// need to wire robo directly if want to have DI
class SettingsActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<SettingsActivity>())
        public fun start(callingActivity: Activity) {
            val intent = Intent(callingActivity, javaClass<SettingsActivity>())
            //        intent.putExtra()
            callingActivity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        AndroidUtil.fullscreen(this) // as we cant use SwirlActivity as a parent, redo this (if its getting too much, refactor!)

        super<PreferenceActivity>.onCreate(savedInstanceState)

        // i dont care about fragments :-p
        // https://developer.android.com/reference/android/preference/PreferenceActivity.html
        addPreferencesFromResource(R.xml.settings);

        val clearCachePref = findPreference(PreferencesKeys.CLEAR_CACHE_BUTTON) as Preference
        clearCachePref.setOnPreferenceClickListener(object: Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(pref: Preference): Boolean {
                LOG.info("Clearing the cache... noooooooot!")
                // write some code here
                return true
            }
        })

    }
    override fun onResume() {
        super<PreferenceActivity>.onResume()
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super<PreferenceActivity>.onPause()
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }
    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        LOG.debug("onSharedPreferenceChanged(preferences, key={})", key)
        if (key.equals(PreferencesKeys.ENABLE_AUDIO)) {
            val audioPref = findPreference(key);
            // Set summary to be the user-description for the selected value
//            connectionPref.setSummary(preferences.getString(key, ""));
            audioPref.setSummary("You have selected: ${preferences!!.isAudioEnabled()}")
        }

    }
}