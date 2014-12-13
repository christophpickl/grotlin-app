package at.cpickl.grotlin.multi

import at.cpickl.grotlin.multi.service.User
import at.cpickl.grotlin.multi.service.Role


//Test class EnableLogNonTest {
//    BeforeSuite fun initLogging() {
//        println("EnableLogNonTest#initLogging()")
//        val root = Logger.getLogger("")
//        val handler = ConsoleHandler()
//        handler.setLevel(Level.ALL)
//        root.addHandler(handler)
//    }
//}


class TestData {
    class object {
        val FAKE_TOKEN_ADMIN = "1"
        val FAKE_TOKEN_USER = "2"


        val USER1 = User("name1", "email1", "password1", Role.USER)
        val USER2 = User("name2", "email2", "password2", Role.USER)
        val USER3 = User("name3", "email3", "password3", Role.USER)
    }
}