package at.cpickl.agrotlin

import android.app.Application
import roboguice.RoboGuice
import roboguice.AnnotationDatabaseImpl
import com.google.inject.AbstractModule
import at.cpickl.agrotlin.service.LoginService
import at.cpickl.agrotlin.service.HttpLoginService
import at.cpickl.agrotlin.service.AndroidVibrateService
import at.cpickl.agrotlin.service.VibrateService
import at.cpickl.agrotlin.service.PooledSoundPlayer
import at.cpickl.agrotlin.service.SoundPlayer
import com.google.inject.Scope
import com.google.inject.Scopes

// roboguice.application.RoboApplication
public class SwirlApplication : Application() {
    {
        // workaround for roboguice bug java.lang.ClassNotFoundException: AnnotationDatabaseImpl
        // see: https://github.com/roboguice/roboguice/issues/246
        RoboGuice.setUseAnnotationDatabases(false);
    }
}

class SwirlModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<LoginService>()).toInstance(HttpLoginService())
        bind(javaClass<VibrateService>()).to(javaClass<AndroidVibrateService>())
        bind(javaClass<SoundPlayer>()).to(javaClass<PooledSoundPlayer>())
    }
}

open class SwirlException(message: String) : RuntimeException(message) {

}
