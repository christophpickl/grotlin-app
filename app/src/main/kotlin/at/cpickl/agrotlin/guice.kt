package at.cpickl.agrotlin

import com.google.inject.AbstractModule
import at.cpickl.agrotlin.service.HttpLoginService
import at.cpickl.agrotlin.service.LoginService

class SwirlModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<LoginService>()).toInstance(HttpLoginService())
    }

}