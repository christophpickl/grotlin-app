package at.cpickl.grotlin.multi.service

import com.google.inject.AbstractModule
import java.util.Properties
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.ObjectifyService

public class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<VersionService>()).toInstance(PropertiesVersionService("/swirl.config.properties"))
        bind(javaClass<UserService>()).toInstance(ObjectifyUserService())
    }
}
