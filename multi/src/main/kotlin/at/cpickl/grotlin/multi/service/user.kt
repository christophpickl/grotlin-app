package at.cpickl.grotlin.multi.service

import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id


trait UserService {
    fun saveOrUpdate(user: User)
    fun loadAll(): Collection<User>
}

class ObjectifyUserService : UserService {
    class object {
        {
            ObjectifyService.register(javaClass<UserDbo>())
        }
    }
    override public fun saveOrUpdate(user: User) {
        ObjectifyService.ofy().save().entity(toDbo(user)).now()
    }

    override public fun loadAll(): Collection<User> {
        return ObjectifyService.ofy().load().type(javaClass<UserDbo>()).list().map { fromDbo(it) }
    }

    private fun toDbo(user: User): UserDbo {
        val dbo = UserDbo()
        dbo.name = user.name
        return dbo
    }
    private fun fromDbo(dbo: UserDbo): User = User(dbo.name!!)
}

data public class User(public val name: String)

Entity data class UserDbo() {
    Id public var name: String? = null
}
