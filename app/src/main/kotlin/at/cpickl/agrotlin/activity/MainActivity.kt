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
import at.cpickl.agrotlin.Logg
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
import at.cpickl.agrotlin.service.VersionHttpRequest
import javax.inject.Inject
import at.cpickl.agrotlin.service.VibrateService
import at.cpickl.agrotlin.showToast
import android.media.MediaPlayer
import at.cpickl.agrotlin.service.SoundPlayer
import at.cpickl.agrotlin.service.Sound
import android.view.Window
import android.view.WindowManager

// NO!!! ContentView(R.layout.activity_main)
public class MainActivity : SwirlActivity() {
    class object {
        private val LOG: Logg = Logg("MainActivity");
        {
            // val DEBUG: Boolean = java.lang.Boolean.parseBoolean("true")
            // val APPLICATION_ID: String = "at.cpickl.agrotlin"
            // val BUILD_TYPE: String = "debug"
            // val FLAVOR: String = ""
            // val VERSION_CODE: Int = 1
            // val VERSION_NAME: String = "1.0"
            LOG.info("Starting version ${BuildConfig.VERSION_NAME} (debug=${BuildConfig.DEBUG}, build type=${BuildConfig.BUILD_TYPE})")
        }
    }

    [InjectView(R.id.btnRandomGame)] private var btnRandomGame: Button? = null
    [InjectView(R.id.btnLogin)] private var btnLogin: Button? = null
    [InjectView(R.id.btnDebug)] private var btnDebug: Button? = null

    Inject private var soundPlayer: SoundPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main);

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
