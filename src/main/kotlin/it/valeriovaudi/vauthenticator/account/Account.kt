package it.valeriovaudi.vauthenticator.account


import java.util.*

data class Account(var accountNonExpired: Boolean = true,
                   var accountNonLocked: Boolean = true,
                   var credentialsNonExpired: Boolean = true,
                   var enabled: Boolean = true,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   var sub: String,

        // needed for email oidc profile
                   var email: String,
        //todo should be changed when account
                   var emailVerified: Boolean = true,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String
)

interface AccountRepository {
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)
}

interface AccountRegistration {
    fun execute(account: Account)
}