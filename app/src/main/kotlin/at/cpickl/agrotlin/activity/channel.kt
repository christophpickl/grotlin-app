package at.cpickl.agrotlin.activity

import android.content.Context
import android.webkit.WebView
import at.cpickl.agrotlin.Logg
import javax.inject.Inject
import org.slf4j.LoggerFactory
import android.webkit.JavascriptInterface
import org.json.JSONObject
import at.cpickl.grotlin.JsonMarshaller
import at.cpickl.grotlin.channel.GameStartsNotificationRto
import at.cpickl.agrotlin.service.NotificationDistributor
import at.cpickl.grotlin.channel.NotificationRegistry
import at.cpickl.grotlin.channel.ChannelNotificationRto


class ChannelWebView(context: Context, jsInterface: JsInterface) : WebView(context) {
    class object {
        private val LOG: Logg = Logg("ChannelWebView")
    }

    {
        getSettings().setJavaScriptEnabled(true)
        addJavascriptInterface(jsInterface, JsConstants.INTERFACE_NAME)
        // http://stackoverflow.com/questions/4325639/android-calling-javascript-functions-in-webview
        loadData("""
        <html>
        <head>
            <script type="text/javascript" src="${JsConstants.CHANNEL_JS_SRC}"></script>
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
    }

    public fun connectToChannel(channelToken: String) {
        LOG.info("connectToChannel(channelToken=${channelToken})")
        // this can be created by swirl-engine POST /channel ... this is the most famous "channel token"
        loadUrl("javascript:${JsConstants.CONNECT_METHOD}('${channelToken}')")
    }
}

object JsConstants {
    val INTERFACE_NAME: String = "JSInterface"
    val CONNECT_METHOD: String = "connectChannel"
    // https://talkgadget.google.com/talkgadget/channel.js
    val CHANNEL_JS_SRC = "http://swirl-engine.appspot.com/_ah/channel/jsapi"
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
        LOG.debug("debug(message='${message}')")
    }

    JavascriptInterface fun onOpen() {
        LOG.debug("onOpen()")
    }

    JavascriptInterface fun onMessage(messageBody: String) {
        LOG.debug("onMessage(messageBody)")
        val json = JSONObject(messageBody)
        val notificationType = json.getString("type")
        val notificationClass:Class<ChannelNotificationRto> = NotificationRegistry.byType(notificationType)
        val notification = marshaller.fromJson(messageBody, notificationClass).toDomain()
        distributor.distribute(notification)
    }

    JavascriptInterface fun onError(message: String) {
        LOG.debug("onError(message={})", message)
    }

    JavascriptInterface fun onClose() {
        LOG.debug("onClose()")
    }
}
