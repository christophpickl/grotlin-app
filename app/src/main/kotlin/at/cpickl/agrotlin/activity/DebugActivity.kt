package at.cpickl.agrotlin.activity

import android.app.Activity
import roboguice.inject.ContentView
import roboguice.activity.RoboActivity
import roboguice.inject.InjectView
import android.widget.EditText
import android.os.Bundle
import android.widget.Button
import android.view.View.OnClickListener
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
import at.cpickl.grotlin.channel.ChannelNotification
import at.cpickl.grotlin.channel.GameStartsNotification

public open class DebugActivity: SwirlActivity() {
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

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_debug)

        btnLoadVersion!!.setOnClickListener({ onLoadVersion() })
        btnFoobar!!.setOnClickListener({ onFoobar() })

        webView = WebView(this)
        // http://stackoverflow.com/questions/4325639/android-calling-javascript-functions-in-webview

        val jsApiSource = "http://swirl-engine.appspot.com/_ah/channel/jsapi"
        val jsInterface = jsInterfaceProvider!!.create(this)
        webView!!.getSettings().setJavaScriptEnabled(true)
        webView!!.addJavascriptInterface(jsInterface, JsConstants.INTERFACE_NAME)
        webView!!.loadData("""
        <html>
        <head>
            <!-- https://talkgadget.google.com/talkgadget/channel.js -->
            <script type="text/javascript" src="${jsApiSource}"></script>
            <script type="text/javascript">
                function ${JsConstants.CONNECT_METHOD}(token) {
                    window.${JsConstants.INTERFACE_NAME}.debug('Connecting to GAE channel API via token: ' + token);
                    var channel = new goog.appengine.Channel(token);
                    var socket = channel.open();
                    socket.onopen = function () {
                        window.${JsConstants.INTERFACE_NAME}.onOpen();
                    };
                    socket.onmessage = function (message) {
                        window.${JsConstants.INTERFACE_NAME}.onMessage(message.data);
                    };
                    socket.onerror = function (error) {
                        window.${JsConstants.INTERFACE_NAME}.onError("onError: " + error.description);
                    };
                    socket.onclose = function () {
                        window.${JsConstants.INTERFACE_NAME}.onClose();
                    };
                    window.${JsConstants.INTERFACE_NAME}.debug('Waiting for async callback function invocations ...');
                }
            </script>
        </head>
        <body>
            <p>webview loaded</p>
        </body>
        </html>
        """, "text/html", "UTF-8")
        myContainer!!.addView(webView)
    }

    private fun onLoadVersion() {
        LOG.info("onLoadVersion()")
        VersionHttpRequest({ showToast("Version received: ${it}")}, { showToast("Fail: ${it}"); it.printStackTrace(); }).execute()
    }

    private fun onFoobar() {
        LOG.info("onFoobar()")
        val channelToken = "AHRlWrojcP1vYE5_S13ZZhmsvPxWQ6OYRTzLAK7zH8iEoLgH8HYTbaFQ8B0vE-fKlP0N15UAO3qRP-ceiVqyTWjINPdKkFjI_467OI_vo06r2MYjCyJphPA"
        // this can be created by swirl-engine POST /channel ... this is the most famous "channel token"
        webView!!.loadUrl("javascript:${JsConstants.CONNECT_METHOD}('${channelToken}')")
    }

}

class JsConstants {
    class object {
        val INTERFACE_NAME: String = "JSInterface"
        val CONNECT_METHOD: String = "connectChannel"
    }
}

class NotificationDistributor {
    fun distribute(notification: GameStartsNotification) { // change type to: ChannelNotification!!!!!!!!!
        println("yesssssssss! distributing channel notification .............. " + notification)
    }
}


class JsInterfaceProvider [Inject] (private val distributor: NotificationDistributor) {
    fun create(context: Context): JsInterface = JsInterface(distributor, context)
}

// not a bean, see: JsInterfaceProvider
class JsInterface (private val distributor: NotificationDistributor, private val context: Context) {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<JsInterface>())
    }

    private val marshaller = JsonMarshaller()

    JavascriptInterface fun debug(message: String) {
        println("XXXXXXXXXXXXXXXXXXXXXXXXXX debug(message='${message}')")
    }

    JavascriptInterface fun onOpen() {
        LOG.debug("onOpen()")
    }

    JavascriptInterface fun onMessage(messageBody: String) {
        LOG.debug("onMessage(messageBody)")
        val json = JSONObject(messageBody)
        val notificationType = json.getString("type")
        val notification = marshaller.fromJson(messageBody, javaClass<GameStartsNotificationRto>()).toDomain()
        distributor.distribute(notification)
    }

    JavascriptInterface fun onError(message: String) {
        LOG.debug("onError(message={})", message)
    }

    JavascriptInterface fun onClose() {
        LOG.debug("onClose()")
    }
}
