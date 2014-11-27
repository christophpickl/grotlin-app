package at.cpickl.agrotlin

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

// good roboguice sample: https://github.com/roboguice/roboguice/tree/master/astroboy
ContentView(R.layout.activity_login)
public open class LoginActivity() : RoboActivity()  {
    class object {
        private val LOG: Logg = Logg("LoginActivity")
    }

    [InjectView(R.id.inp_username)] private var username: EditText? = null
    [InjectView(R.id.inp_password)] private var password: EditText? = null
    [InjectView(R.id.btn_login)] private var login: Button? = null
//    @InjectView(R.id.thumbnail)         thumbnail: ImageView? = null
//    @InjectResource(R.drawable.icon)    icon: Drawable? = null
//    @InjectResource(R.string.app_name)  myName: String? = null
//    Inject vibrator: Vibrator? = null

    override fun onCreate(saved: Bundle?) {
        super<RoboActivity>.onCreate(saved)
//        setContentView(R.layout.activity_login)
//        getIntent().getStringExtra()

        LOG.info("onCreate(..) login button = '${login}'")
        login!!.setOnClickListener({ onLogin() })
    }

    private fun onLogin() {
        Toast.makeText(this, "Login for user: ${username!!.getText()}", 5000).show()
    }

}
