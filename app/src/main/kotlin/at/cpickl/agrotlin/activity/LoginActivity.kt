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

// good roboguice sample: https://github.com/roboguice/roboguice/tree/master/astroboy
ContentView(R.layout.activity_login)
public open class LoginActivity() : RoboActivity()  {
    class object {
        private val LOG: Logg = Logg("LoginActivity")
    }

    [InjectView(R.id.inp_username)] private var inpUsername: EditText? = null
    [InjectView(R.id.inp_password)] private var inpPassword: EditText? = null
    [InjectView(R.id.btn_login)] private var login: Button? = null
//    @InjectView(R.id.thumbnail)         thumbnail: ImageView? = null
//    @InjectResource(R.drawable.icon)    icon: Drawable? = null
//    @InjectResource(R.string.app_name)  myName: String? = null
//    Inject var vibrator: Vibrator? = null
    Inject private var loginService: LoginService? = null

    override fun onCreate(saved: Bundle?) {
        super<RoboActivity>.onCreate(saved)
//        setContentView(R.layout.activity_login)
//        getIntent().getStringExtra()

        login!!.setOnClickListener({ onLogin() })
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