package at.cpickl.agrotlin.activity

import roboguice.activity.RoboActivity
import android.os.Bundle
import at.cpickl.agrotlin.AndroidUtil
import org.slf4j.LoggerFactory


open class SwirlActivity() : RoboActivity() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<SwirlActivity>())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.info("onCreate(savedInstanceState)")
        super<RoboActivity>.onCreate(savedInstanceState)

        AndroidUtil.fullscreen(this)
    }
}
