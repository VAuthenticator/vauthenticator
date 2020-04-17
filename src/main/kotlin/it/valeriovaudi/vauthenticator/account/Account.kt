package it.valeriovaudi.vauthenticator.account

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

data class Account(var accountNonExpired: Boolean = true,
                   var accountNonLocked: Boolean = true,
                   var credentialsNonExpired: Boolean = true,
                   var enabled: Boolean = true,
                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   @Id
                   var sub: String,

        // needed for email oidc profile
                   var email: String,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String
)


interface AccountRepository {
    fun accountFor(username: String): Optional<Account>
}

class MongoAccountRepository(private val delegate: MongoAccountRepositoryDelegate) : AccountRepository {
    override fun accountFor(username: String) = delegate.findByUsername(username)
}

interface MongoAccountRepositoryDelegate : MongoRepository<Account, String> {
    fun findByUsername(username: String): Optional<Account>
}