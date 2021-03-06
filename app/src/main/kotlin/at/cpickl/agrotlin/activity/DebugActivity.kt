package at.cpickl.agrotlin.activity

import android.app.Activity
import roboguice.inject.ContentView
import roboguice.activity.RoboActivity
import roboguice.inject.InjectView
import android.widget.EditText
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.view.View
import android.os.Vibrator
import javax.inject.Inject
import at.cpickl.agrotlin.service.engine.UserEngine
import at.cpickl.agrotlin.R
import android.content.Context
import android.content.Intent
import android.widget.TextView
import at.cpickl.agrotlin.showToast
import android.widget.LinearLayout
import android.webkit.WebView
import at.cpickl.agrotlin.service.VibrateService
import android.webkit.JavascriptInterface
import org.slf4j.LoggerFactory
import org.json.JSONObject
import at.cpickl.grotlin.JsonMarshaller
import at.cpickl.grotlin.channel.GameStartsNotificationRto
import at.cpickl.grotlin.channel.ChannelNotificationRto
import at.cpickl.grotlin.channel.GameStartsNotification
import android.app.AlertDialog
import android.content.DialogInterface.OnClickListener
import android.content.DialogInterface
import android.text.InputType
import at.cpickl.agrotlin.service.NotificationDistributor
import at.cpickl.grotlin.channel.GameStartsNotificationResponder
import at.cpickl.agrotlin.view.showAlertDialog
import at.cpickl.agrotlin.view.showAlertOkCancelDialog
import at.cpickl.agrotlin.service.SettingsManagerViaSharedPreferences
import android.graphics.Color
import at.cpickl.agrotlin.service.engine.VersionEngine

public open class DebugActivity: SwirlActivity(), GameStartsNotificationResponder {

    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<DebugActivity>())

        public fun start(callingActivity: Activity) {
            val intent = Intent(callingActivity, javaClass<DebugActivity>())
            //        intent.putExtra()
            callingActivity.startActivity(intent)
        }
    }

    [InjectView(R.id.btnLoadVersion)] private var btnLoadVersion: Button? = null
    [InjectView(R.id.btnFoobar)] private var btnFoobar: Button? = null
    [InjectView(R.id.myContainer)] private var myContainer: LinearLayout? = null
    Inject private var jsInterfaceProvider: JsInterfaceProvider? = null
    Inject private var notificationDistributor: NotificationDistributor? = null
    Inject private var versionEngine: VersionEngine? = null

    private var webView: ChannelWebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_debug)

        btnLoadVersion!!.setOnClickListener({
            LOG.info("onLoadVersion()")
            showToast("Loading server version ...", duration = 1000)
            versionEngine!!.getVersion({ showToast("Version received: ${it}")})
        })

        btnFoobar!!.setOnClickListener({
            val input = EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT)

            showAlertOkCancelDialog("Channel Connect", "Enter channel token:", input,
                    { dialog -> webView!!.connectToChannel(input.getText().toString()) })
        })

        notificationDistributor!!.register(this)
//        notificationDistributor!!.unregister(this)

        // TODO do not re-create webView here, because this will reset stuff, e.g. on orientation change... :-/
        webView = ChannelWebView(this, jsInterfaceProvider!!.create(this))
        myContainer!!.addView(webView)
    }

    override fun onGameStarts(notification: GameStartsNotification) {
        println("DebugActivity ... onGameStarts")
        showToast("game starts: " + notification)
    }

}

