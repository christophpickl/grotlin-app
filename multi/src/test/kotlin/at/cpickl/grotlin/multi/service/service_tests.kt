package at.cpickl.grotlin.multi.service


import org.testng.annotations.Test
import org.testng.annotations.BeforeMethod
import org.testng.annotations.AfterMethod
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.googlecode.objectify.ObjectifyService
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import at.cpickl.grotlin.multi.resource.Pagination
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.mockito.Mockito
import org.mockito.ArgumentMatcher
import at.cpickl.grotlin.multi.TestData

Test class PropertiesVersionServiceTest {
    fun loadTestPropertiesFileShouldReturnContent() {
        assertThat(PropertiesVersionService("/swirltest/test_version.properties").load(),
                equalTo(Version("testArtifactVersion", "testBuildDate")))
    }
}

Test class ObjectifyUserServiceTest {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<ObjectifyUserServiceTest>())
    }

    private var testee: ObjectifyUserService = ObjectifyUserService()
    private var engineHelper: LocalServiceTestHelper? = null

    BeforeMethod fun setUp() {
        LOG.debug("setUp()")
        testee = ObjectifyUserService()
        engineHelper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())
        engineHelper!!.setUp()
    }

    AfterMethod fun tearDown() {
        engineHelper!!.tearDown()
    }

    fun loadAll() {
        ObjectifyService.run({
            assertThat(testee.loadAll().size, equalTo(0))
        })
    }

    Test(dependsOnMethods = array("loadAll")) fun saveOrUpdate() {
        ObjectifyService.run({
            assertThat(testee.loadAll().size, equalTo(0))
            val user = User("cpi", "email", "password", Role.USER)
            testee.saveOrUpdate(user)
            val actual = testee.loadAll()
            assertThat(actual.size, equalTo(1))
            assertThat(actual.first(), equalTo(user))
        })
    }
}

Test class GameServiceTest {


    private var runningGameService: RunningGameService = Mockito.mock(javaClass<RunningGameService>())
    private var testee = WaitingRandomGameService(runningGameService)

    BeforeMethod fun init() {
        runningGameService: RunningGameService = Mockito.mock(javaClass<RunningGameService>())
        testee = WaitingRandomGameService(runningGameService)
    }
    AfterMethod fun verifyMocks() {
//        Mockito.verifyNoMoreInteractions(runningGameService)
    }

    fun getOrCreateRandomGame_initCreate_shouldReturnNewGame() {
        assertThat(testee.waitingGames, empty<WaitingRandomGame>())
        val actual = testee.getOrCreateRandomGame(TestData.USER1)
        assertThat(testee.waitingGames, hasSize<WaitingRandomGame>(1))
        assertThat(testee.waitingGames.first!!, equalTo(actual))
        assertThat(actual.usersMax, equalTo(2)) // this will break in future
        assertThat(actual.usersWaiting, equalTo(1))
    }

    fun getOrCreateRandomGame_secondTimeForSameUser_shouldReturnSameGame() {
        val game1 = testee.getOrCreateRandomGame(TestData.USER1)
        assertThat(testee.getOrCreateRandomGame(TestData.USER1), sameInstance(game1))
        assertThat(game1.usersWaiting, equalTo(1)) // same user must not increase waiting count
    }

    fun getOrCreateRandomGame_twoUsers_shouldBeTheSameGameAndBeFull() {
        val game1 = testee.getOrCreateRandomGame(TestData.USER1)
        val game2 = testee.getOrCreateRandomGame(TestData.USER2)
        assertThat(game1, sameInstance(game2))
        assertThat(game1.usersWaiting, equalTo(2)) // game is full
    }

    fun getOrCreateRandomGame_threeDifferentGames_shouldCreateOneNewGame() {
        val game1 = testee.getOrCreateRandomGame(TestData.USER1)
        testee.getOrCreateRandomGame(TestData.USER2)
        val game3 = testee.getOrCreateRandomGame(TestData.USER3)

        assertThat(game1, not(sameInstance(game3)))
        assertThat(game3.usersWaiting, equalTo(1))
        // TODO mockito pisses me off
//        Mockito.verify(runningGameService, Mockito.times(2)).addNewGame(org.mockito.Matchers.any<RunningGame>(javaClass<RunningGame>()))
    }

}


