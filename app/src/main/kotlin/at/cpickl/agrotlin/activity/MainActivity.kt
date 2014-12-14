package at.cpickl.agrotlin.activity

import android.app.Activity
import android.os.Bundle
import at.cpickl.grotlin.Player
import android.graphics.Color
import at.cpickl.grotlin.Game
import at.cpickl.grotlin.Map
import java.util.Arrays
import at.cpickl.grotlin.RealDice
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.widget.RelativeLayout
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import android.view.View
import at.cpickl.grotlin.BattleResult
import android.content.pm.ActivityInfo
import org.apache.http.client.HttpClient
import org.apache.http.impl.conn.DefaultClientConnection
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import android.os.AsyncTask
import org.apache.http.HttpStatus
import org.apache.http.util.EntityUtils
import android.content.Intent
import at.cpickl.agrotlin.BuildConfig
import at.cpickl.agrotlin.AndroidUtil
import at.cpickl.agrotlin.R
import at.cpickl.agrotlin.view.GameView
import at.cpickl.agrotlin.view.MiniMapView
import at.cpickl.agrotlin.service.AttackPhase
import at.cpickl.agrotlin.MiniMap
import at.cpickl.agrotlin.view.MapView
import at.cpickl.agrotlin.service.EndGamePhase
import at.cpickl.agrotlin.service.DistributionPhase
import roboguice.inject.ContentView
import roboguice.activity.RoboActivity
import roboguice.inject.InjectView
import android.widget.EditText
import at.cpickl.agrotlin.service.server.VersionHttpRequest
import javax.inject.Inject
import at.cpickl.agrotlin.service.VibrateService
import at.cpickl.agrotlin.showToast
import android.media.MediaPlayer
import at.cpickl.agrotlin.service.SoundPlayer
import at.cpickl.agrotlin.service.Sound
import android.view.Window
import android.view.WindowManager
import org.slf4j.LoggerFactory
import at.cpickl.agrotlin.service.AndroidOs
import at.cpickl.agrotlin.view.showAlertDialog

// NO!!! ContentView(R.layout.activity_main)
public class MainActivity : SwirlActivity() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<MainActivity>());
        {
            // val DEBUG: Boolean = java.lang.Boolean.parseBoolean("true")
            // val APPLICATION_ID: String = "at.cpickl.agrotlin"
            // val BUILD_TYPE: String = "debug"
            // val FLAVOR: String = ""
            // val VERSION_CODE: Int = 1
            // val VERSION_NAME: String = "1.0"
            LOG.info("Starting version ${BuildConfig.VERSION_NAME} (debug=${BuildConfig.DEBUG}, build type=${BuildConfig.BUILD_TYPE})")
            LOG.info("=======================================================================")
        }
    }

    [InjectView(R.id.btnRandomGame)] private var btnRandomGame: Button? = null
    [InjectView(R.id.btnLogin)] private var btnLogin: Button? = null
    [InjectView(R.id.btnDebug)] private var btnDebug: Button? = null
    [InjectView(R.id.btnSettings)] private var btnSettings: Button? = null

    Inject private var soundPlayer: SoundPlayer? = null
    Inject private var androidOs: AndroidOs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        println("===============================")
        // TODO how to configure logging? slf4j is set to info ATM...
        LOG.trace("SLF4J trace")
        LOG.debug("SLF4J debug")
        LOG.info("SLF4J info")
        LOG.warn("SLF4J warn")
        Log.v("TAG", "Android verbose")
        Log.d("TAG", "Android debug")
        Log.i("TAG", "Android info")
        Log.w("TAG", "Android warn")
        println("===============================")

        LOG.info("onCreate(savedInstanceState)")
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        if (!soundPlayer!!.isInit()) {
            LOG.debug("One time initialising sound player")
            soundPlayer!!.init(this)
        }
//        setTheme(android.R.style.Theme_Light);

//        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        // animFadein.setAnimationListener(this);
        // animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeIn);
        btnRandomGame!!.setOnClickListener { PlayGameActivity.start(this) }
        btnLogin!!.setOnClickListener { LoginActivity.start(this) }
        btnDebug!!.setOnClickListener { DebugActivity.start(this) }
        btnSettings!!.setOnClickListener { SettingsActivity.start(this) }

    }
    override fun onResume() {
        LOG.debug("onResume()")
        super.onResume()

        // TODO this check needs to be global, not just in MainActivity (e.g. if user is in login activity, need to check, and redirect here)
        if (!androidOs!!.isNetworkAvailable(this)) {
            enableAnyInteraction(false)
            showAlertDialog("Network Unavailable",
                    "This game needs a working internet connection! Please enable your network first.",
                    positiveButton = Pair("Shame on me", { dialog -> })
            )
        } else {
            enableAnyInteraction(true)
        }
    }


    private fun enableAnyInteraction(enable: Boolean) {
        LOG.info("enableAnyInteraction()")
        array(btnRandomGame, btnLogin, btnDebug).forEach { btn ->
            btn!!.setEnabled(enable)
            // TODO MINOR UI - visually indicate view is not enabled (grayed out buttons)
        }

    }

    /*
    its fullscreen, so we dont see it :)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        LOG.info("onOptionsItemSelected(item=${item})")
        val id = item.getItemId()

        when (id) {
            R.id.menu_about-> {
                LOG.info("menu about");
                showToast("About me...")
                soundPlayer!!.play(Sound.ROLL)
                vibrator!!.vibrate()
                return true;
            }
//            else -> throw IllegalArgumentException("Unhandled menu item ID: ${id} for menu item: ${item}")
        }

        return super.onOptionsItemSelected(item)
    }
    */

}
