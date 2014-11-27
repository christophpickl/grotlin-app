package at.cpickl.agrotlin

import android.app.Application
import roboguice.RoboGuice
import roboguice.AnnotationDatabaseImpl
import com.google.inject.AbstractModule
import at.cpickl.agrotlin.service.LoginService
import at.cpickl.agrotlin.service.HttpLoginService

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
    }
}
