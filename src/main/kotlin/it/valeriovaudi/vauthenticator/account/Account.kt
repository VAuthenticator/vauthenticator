package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.extentions.toSha256

data class Account(var accountNonExpired: Boolean = false,
                   var accountNonLocked: Boolean = false,
                   var credentialsNonExpired: Boolean = false,
                   var enabled: Boolean = true,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   var email: String,
                   var emailVerified: Boolean = true,

                   var firstName: String,
                   var lastName: String
) {
    val sub: String
        get() = email.toSha256()
}