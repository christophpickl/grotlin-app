package at.cpickl.agrotlin

import android.app.Application
import roboguice.RoboGuice
import roboguice.AnnotationDatabaseImpl
import com.google.inject.AbstractModule
import at.cpickl.agrotlin.service.engine.UserEngine
import at.cpickl.agrotlin.service.engine.RestfulUserEngine
import at.cpickl.agrotlin.service.AndroidVibrateService
import at.cpickl.agrotlin.service.VibrateService
import at.cpickl.agrotlin.service.PooledSoundPlayer
import at.cpickl.agrotlin.service.SoundPlayer
import com.google.inject.Scope
import com.google.inject.Scopes
import at.cpickl.agrotlin.activity.JsInterfaceProvider
import at.cpickl.agrotlin.activity.JsInterface
import at.cpickl.agrotlin.service.NotificationDistributor
import at.cpickl.agrotlin.service.AndroidOs
import at.cpickl.agrotlin.service.AndroidOsImpl
import at.cpickl.agrotlin.service.SettingsManager
import at.cpickl.agrotlin.service.SettingsManagerViaSharedPreferences
import at.cpickl.agrotlin.service.engine.VersionEngine
import at.cpickl.grotlin.endpoints.VersionClient
import at.cpickl.grotlin.endpoints.ServerUrl
import org.slf4j.LoggerFactory
import at.cpickl.grotlin.endpoints.UserClient

// roboguice.application.RoboApplication
public class SwirlApplication : Application() {
    {
        // workaround for roboguice bug java.lang.ClassNotFoundException: AnnotationDatabaseImpl
        // see: https://github.com/roboguice/roboguice/issues/246
        RoboGuice.setUseAnnotationDatabases(false);
    }
}

class SwirlModule : AbstractModule() {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<SwirlModule>())
    }

    override fun configure() {
        bind(javaClass<UserEngine>()).to(javaClass<RestfulUserEngine>())
        bind(javaClass<VibrateService>()).to(javaClass<AndroidVibrateService>())
        bind(javaClass<SoundPlayer>()).to(javaClass<PooledSoundPlayer>())
        bind(javaClass<AndroidOs>()).to(javaClass<AndroidOsImpl>())
        bind(javaClass<SettingsManager>()).to(javaClass<SettingsManagerViaSharedPreferences>())
        bind(javaClass<VersionEngine>())

        val baseUrl = "http://swirl-engine.appspot.com" // TODO make runtime changeable via settings
        bind(javaClass<String>()).annotatedWith(javaClass<ServerUrl>()).toInstance(baseUrl)

        // comes from logic module
        bind(javaClass<VersionClient>())
        bind(javaClass<UserClient>())

        bind(javaClass<NotificationDistributor>()).`in`(Scopes.SINGLETON)
        bind(javaClass<JsInterfaceProvider>()).`in`(Scopes.SINGLETON)
    }

}

// actually the default cause is "this", is it bad to set it to null instead? (no overloaded ctors in kotlin as in java...?)
open class SwirlException(message: String, cause: Exception? = null) : RuntimeException(message, cause)
