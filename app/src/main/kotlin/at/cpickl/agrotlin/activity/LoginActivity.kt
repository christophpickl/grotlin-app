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

// good roboguice sample: https://github.com/roboguice/roboguice/tree/master/astroboy
ContentView(R.layout.activity_login)
public open class LoginActivity() : RoboActivity()  {
    class object {
        private val LOG: Logg = Logg("LoginActivity")

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
    Inject private var loginService: LoginService? = null

    override fun onCreate(saved: Bundle?) {
        super<RoboActivity>.onCreate(saved)
//        setContentView(R.layout.activity_login)
//        getIntent().getStringExtra()

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
        val loggedIn = loginService!!.login(username, password)
        val message = if(loggedIn) "Successfully logged in as ${username}." else "Login failed!"
        Toast.makeText(this, message, 5000).show()
    }

}
