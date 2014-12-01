package at.cpickl.grotlin.multi.service


import org.testng.annotations.Test
import org.testng.annotations.BeforeMethod
import org.testng.annotations.AfterMethod
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.googlecode.objectify.ObjectifyService
import at.cpickl.grotlin.multi.assertThat
import at.cpickl.grotlin.multi.equalTo
import at.cpickl.grotlin.multi.resource.Pagination

Test public class PropertiesVersionServiceTest {
    public fun loadTestPropertiesFileShouldReturnContent() {
        MatcherAssert.assertThat(PropertiesVersionService("/swirltest/test_version.properties").load(),
                Matchers.equalTo(Version("testArtifactVersion", "testBuildDate")))
    }
}

Test public class ObjectifyUserServiceTest {

    private var testee: ObjectifyUserService = ObjectifyUserService()
    private var engineHelper: LocalServiceTestHelper? = null

    BeforeMethod public fun setUp() {
        testee = ObjectifyUserService()
        engineHelper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())
        engineHelper!!.setUp()
    }

    AfterMethod public fun tearDown() {
        engineHelper!!.tearDown()
    }

    public fun loadAll() {
        ObjectifyService.run({
            assertThat(testee.loadAll().size, equalTo(0))
        })
    }

    Test(dependsOnMethods = array("loadAll")) public fun saveOrUpdate() {
        ObjectifyService.run({
            assertThat(testee.loadAll().size, equalTo(0))
            val user = User("cpi", "email", "password")
            testee.saveOrUpdate(user)
            val actual = testee.loadAll()
            assertThat(actual.size, equalTo(1))
            assertThat(actual.first(), equalTo(user))
        })
    }
}


