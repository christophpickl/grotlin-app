package at.cpickl.agrotlin.activity

import roboguice.activity.RoboActivity
import android.os.Bundle
import at.cpickl.agrotlin.Logg
import at.cpickl.agrotlin.AndroidUtil


open class SwirlActivity() : RoboActivity() {
    class object {
        private val LOG: Logg = Logg("SwirlActivity");
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<RoboActivity>.onCreate(savedInstanceState)

        AndroidUtil.fullscreen(this)
    }
}
