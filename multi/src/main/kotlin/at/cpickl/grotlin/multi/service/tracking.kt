package at.cpickl.grotlin.multi.service

import com.google.appengine.api.urlfetch.HTTPHeader
import java.net.URL
import com.google.appengine.api.urlfetch.URLFetchService
import java.nio.charset.StandardCharsets
import java.net.URLEncoder
import com.google.appengine.api.urlfetch.HTTPRequest
import com.google.appengine.api.urlfetch.HTTPMethod
import org.slf4j.LoggerFactory

trait TrackingService {
    public fun track()
}


/**
 * @param trackingId Tracking ID / Web property / Property ID
 * see: https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#event
 * see: https://cloud.google.com/appengine/docs/google-analytics#app_engine_server-side_analytics_collection
 * see: https://developers.google.com/analytics/devguides/collection/protocol/v1/reference
 */
class GoogleAnalyticsTrackingService (
        private val trackingId: String, // e.g.: "UA-123456-1"
        private val urlFetchService: URLFetchService) : TrackingService {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<GoogleAnalyticsTrackingService>())
        private val CONTENT_TYPE_HEADER = HTTPHeader("Content-Type", "application/x-www-form-urlencoded")
        private val ANALYTICS_URL = URL("http", "www.google-analytics.com", "/collect")

        private val ANALYTICS_VERSION = "1"
    }

    /*
    This anonymously identifies a particular user, device, or browser instance.
    For the web, this is generally stored as a first-party cookie with a two-year expiration.
    For mobile apps, this is randomly generated for each particular instance of an application install.
    The value of this field should be a random UUID (version 4) as described in http://www.ietf.org/rfc/rfc4122.txt
     */
    private val clientId = "555"  // Anonymous Client ID.

    override public fun track() {
        LOG.info("track()")

        val hostname = "swirl-engine.appspot.com"
        val page = "/version"
        val title = "my title"
        val payload = linkedMapOf(
                "v" to ANALYTICS_VERSION, // The protocol version.
                "tid" to trackingId, // The ID that distinguishes to which Google Analytics property to send data.
                "cid" to clientId, // An ID unique to a particular user.
                // "uid" ... This is intended to be a known identifier for a user provided by the site owner/tracking library user. It may not itself be PII (personally identifiable information).

                "t" to "pageview", // Hit type: Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception', 'timing'.
                //            "dh" to hostname,  // Document hostname.
                "dp" to page
                //            "dt" to title
                // https://developers.google.com/analytics/devguides/collection/analyticsjs/events
                //                "t" to "event",
                //                "ec" to encode("category ?"), // Typically the object that was interacted with (e.g. button)
                //                "ea" to encode("action ?"), // The type of interaction (e.g. click)
                //                "el" to label, // Useful for categorizing events (e.g. nav buttons)
                //                "ev" to value // Number. Values must be non-negative. Useful to pass counts (e.g. 4 times)
        )
        val request = HTTPRequest(ANALYTICS_URL, HTTPMethod.POST)
        request.addHeader(CONTENT_TYPE_HEADER)
        //        request.addHeader(HTTPHeader("User-Agent", "Swirl-Engine v???"))
        val payloadEncoded = payloadToString(payload)
        request.setPayload(payloadEncoded.toByteArray(StandardCharsets.UTF_8))

        LOG.debug("Requesting Google Analytics: {}. payloadEncoded='{}'", request.getURL(), payloadEncoded)
        val response = urlFetchService.fetch(request)
        if (response.getResponseCode() != 200) {
            throw RuntimeException("Request to Google Analytics failed! (Response Code was: ${response.getResponseCode()})\n" +
                    "Response: [${String(response.getContent())}]")
        }
    }

    private fun payloadToString(payload: Map<String, String>) = payload.entrySet()
            .fold("", {(tmp, entry) -> "${tmp}&${entry.key}=${entry.value.toEncodedUtf8()}" }).substring(1)


}

public fun String.toEncodedUtf8(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.name())