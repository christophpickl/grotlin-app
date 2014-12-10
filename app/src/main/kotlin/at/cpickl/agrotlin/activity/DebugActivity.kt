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
import at.cpickl.agrotlin.service.LoginService
import at.cpickl.agrotlin.Logg
import at.cpickl.agrotlin.R
import android.content.Context
import android.content.Intent
import android.widget.TextView
import at.cpickl.agrotlin.showToast
import at.cpickl.agrotlin.service.VersionHttpRequest
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

public open class DebugActivity: SwirlActivity(), GameStartsNotificationResponder {

    class object {
        private val LOG: Logg = Logg("DebugActivity")

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

    private var webView: ChannelWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_debug)

        btnLoadVersion!!.setOnClickListener({
            LOG.info("onLoadVersion()")
            VersionHttpRequest({ showToast("Version received: ${it}")}, { showToast("Fail: ${it}"); it.printStackTrace(); }).execute()
        })

        btnFoobar!!.setOnClickListener({
            val input = EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT)
            AlertDialog.Builder(this)
                .setTitle("Channel Connect")
                .setMessage("Enter channel token:")
                .setView(input)
                    .setPositiveButton("OK", object : OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            webView!!.connectToChannel(input.getText().toString())
                        }
                    })
                    .setNegativeButton("Cancel", object : OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            dialog.cancel()
                        }
                    })
                .show()
        })

        // TODO do not re-create webView here, because this will reset stuff, e.g. on orientation change... :-/
        webView = ChannelWebView(this, jsInterfaceProvider!!.create(this))
        myContainer!!.addView(webView)
    }

    override fun onResume() {
        super<SwirlActivity>.onResume()
        notificationDistributor!!.register(this)
    }

    override fun onPause() {
        super<SwirlActivity>.onPause()
        notificationDistributor!!.unregister(this)
    }

    override fun onGameStarts(notification: GameStartsNotification) {
        println("DebugActivity ... onGameStarts")
        showToast("game starts: " + notification)
    }

}
