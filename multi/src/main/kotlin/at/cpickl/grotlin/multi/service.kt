package at.cpickl.grotlin.multi

import com.google.inject.AbstractModule


public class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<Repo>()).toInstance(MyRepo())
    }
}

class MyRepo : Repo {
    override fun save(data: String) = "Hello ${data}!"
}

trait Repo {
    fun save(data: String): String
}
