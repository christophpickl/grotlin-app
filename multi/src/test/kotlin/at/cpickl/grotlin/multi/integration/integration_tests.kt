package at.cpickl.grotlin.multi.integration

import org.testng.annotations.Test
import at.cpickl.grotlin.multi.service.ServiceModule
import javax.inject.Inject
import at.cpickl.grotlin.multi.AppModule
import at.cpickl.grotlin.multi.resource.GameResource
import at.cpickl.grotlin.multi.TestData
import org.hamcrest.Matchers
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import com.google.inject.AbstractModule
import at.cpickl.grotlin.multi.service.RunningGameService
import at.cpickl.grotlin.multi.service.InMemoryRunningGameService
import com.google.inject.Scopes
import at.cpickl.grotlin.multi.service.RunningGame
import at.cpickl.grotlin.multi.service.User
import com.google.inject.util.Modules
import org.testng.IModuleFactory
import org.testng.ITestContext
import com.google.inject.Module
import org.testng.annotations.BeforeMethod
import com.google.inject.Guice


// no such thing as @DirtiesContext
//Guice(moduleFactory = javaClass<GameResourceIntegrationTestModuleFactory>())
//(modules = array(javaClass<AppModule>(), javaClass<FakeModule>())) ... will fail to override :-(
Test(groups = array("IntegrationTest"))
class GameResourceIntegrationTest {

    Inject private var testee: GameResource? = null

    BeforeMethod fun initGuice() {
        Guice.createInjector(Modules.override(AppModule()).with(FakeModule())).injectMembers(this)
    }

    fun createNewGameAndListWaitingShouldReturnOneGame() {
        testee!!.getExistingOrCreateNewRandomGame(TestData.USER1)
        val list = testee!!.listWaitingRandomGames()
        assertThat(list, Matchers.hasSize(1))
        val game = list.first()
        assertThat(game.usersMax!!, equalTo(2))
        assertThat(game.usersWaiting!!, equalTo(1))
    }

}
//class GameResourceIntegrationTestModuleFactory : IModuleFactory {
//    override fun createModule(context: ITestContext?, testClass: Class<out Any?>?): Module? =
//        Modules.override(AppModule()).with(object : AbstractModule() {
//            override fun configure() {
//                bind(javaClass<RunningGameService>()).to(javaClass<TestRunningGameService>()).`in`(Scopes.SINGLETON)
//            }
//        })
//}

class FakeModule : AbstractModule() {
    override fun configure() {
        bind(javaClass<RunningGameService>()).to(javaClass<TestRunningGameService>()).`in`(Scopes.SINGLETON)
    }
}

class TestRunningGameService : RunningGameService {
    override val runningGames: Collection<RunningGame> = linkedListOf()
    override fun addNewGame(game: RunningGame) {
        throw UnsupportedOperationException()
    }

    override fun gameByIdForUser(gameId: String, user: User): RunningGame {
        throw UnsupportedOperationException()
    }

}