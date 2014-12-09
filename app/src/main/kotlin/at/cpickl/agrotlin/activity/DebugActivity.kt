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

public open class DebugActivity : SwirlActivity() {
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

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwirlActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_debug)

        btnLoadVersion!!.setOnClickListener({ onLoadVersion() })
        btnFoobar!!.setOnClickListener({ onFoobar() })

        webView = WebView(this)
        // http://stackoverflow.com/questions/4325639/android-calling-javascript-functions-in-webview
        val jsInterfaceName = "JSInterface"
        val jsInterface = JsInterface(this)
        webView!!.getSettings().setJavaScriptEnabled(true)
        webView!!.addJavascriptInterface(jsInterface, jsInterfaceName)
        webView!!.loadData("""
        <html>
        <head>
            <!-- https://talkgadget.google.com/talkgadget/channel.js -->
            <script type="text/javascript" src="http://swirl-engine.appspot.com/_ah/channel/jsapi"></script>
            <script type="text/javascript">
            function connectChannel(token) {
                window.${jsInterfaceName}.doEchoTest("connectChannel(token=" + token + ")");
                var channel = new goog.appengine.Channel(token);

                var socket = channel.open();
                socket.onopen = function () {
                    window.${jsInterfaceName}.doEchoTest("onOpen");
                };
                socket.onmessage = function (myMessage) {
                    window.${jsInterfaceName}.doEchoTest("onMessage: " + myMessage.data);
                };
                socket.onerror = function (error) {
                    window.${jsInterfaceName}.doEchoTest("onError: " + error.description);
                };
                socket.onclose = function () {
                    window.${jsInterfaceName}.doEchoTest("onClose");
                };
                window.${jsInterfaceName}.doEchoTest("Async connection opening ...");
            }
            </script>
        </head>
        <body>
            <h1>hello webview</h1>
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
        webView!!.loadUrl("javascript:connectChannel('AHRlWrqXEgyuYEgHgfe7Zqf_aKHM1jW_xOPDdjIl2MpThmYwIpvfG7kK1B7OkOwSCSMA5fx2LdEG51t8W4EX3_Ejx4Fy3JCxgpUnwUa3tbBGT6vfOFk9tSk')")
    }

}

class JsInterface(private val context: Context) {
    JavascriptInterface fun doEchoTest(message: String) {
        println("JS says ................ '${message}'")
    }
}
