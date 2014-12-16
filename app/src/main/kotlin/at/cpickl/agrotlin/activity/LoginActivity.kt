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
import at.cpickl.agrotlin.service.engine.UserEngine
import at.cpickl.agrotlin.R
import android.content.Context
import android.content.Intent
import android.widget.TextView
import at.cpickl.agrotlin.showToast
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.LoginClientException

// good roboguice sample: https://github.com/roboguice/roboguice/tree/master/astroboy

public open class LoginActivity : SwirlActivity() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<LoginActivity>())

        public fun start(callingActivity: Activity) {
            val intent = Intent(callingActivity, javaClass<LoginActivity>())
            //        intent.putExtra()
            callingActivity.startActivity(intent)
        }
    }

    [InjectView(R.id.inpUsername)] private var inpUsername: EditText? = null
    [InjectView(R.id.inpPassword)] private var inpPassword: EditText? = null
    [InjectView(R.id.btnLogin)] private var btnLogin: Button? = null
    [InjectView(R.id.btnForgotPassword)] private var btnForgotPassword: TextView? = null
//    @InjectView(R.id.thumbnail)         thumbnail: ImageView? = null
//    @InjectResource(R.drawable.icon)    icon: Drawable? = null
//    @InjectResource(R.string.app_name)  myName: String? = null
    Inject private var userEngine: UserEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwirlActivity>.onCreate(savedInstanceState)
//        getIntent().getStringExtra()

        setContentView(R.layout.activity_login)

        btnLogin!!.setOnClickListener({ onLogin() })
        btnForgotPassword!!.setOnClickListener({ onForgotPassword() })
    }

    private fun onForgotPassword() {
        LOG.info("onForgotPassword()")
        showToast("Not yet implemented!")
    }

    private fun onLogin() {
        LOG.info("onLogin()")
        val username = inpUsername!!.getText().toString()
        val password = inpPassword!!.getText().toString()

        // FIXME catch exception doesnt work, needs passing a custom exception handler and check via instanceof for LoginClientException
//        try {
            userEngine!!.login(username, password, {
                token ->
                LOG.debug("Logged in as 'username' and got token '${token}'.")
                // store token in global data storage
                // request /user/profile_justMyOwnBecauseRegularProfileIsThePubliclyVisible endpoint

                showToast("Successfully logged in as ${username}.")
            })
//        } catch (e: LoginClientException) {
//            LOG.error("Ups!", e)
//            showToast("Login failed!")
//        }
    }

}
