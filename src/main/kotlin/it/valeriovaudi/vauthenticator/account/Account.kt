package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.extentions.toSha256

data class  Account(var accountNonExpired: Boolean = false,
                   var accountNonLocked: Boolean = false,
                   var credentialsNonExpired: Boolean = false,
                   var enabled: Boolean = true,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

        // needed for email oidc profile
                   var email: String,
        //todo should be changed when account
                   var emailVerified: Boolean = true,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String
) {
    val sub: String
        get() = email.toSha256()
}


object AccountConverter {
    fun fromDomainToAccountApiRepresentation(domain: Account): AccountApiRepresentation =
            AccountApiRepresentation(!domain.accountNonLocked, domain.enabled, domain.email, domain.authorities)

    fun fromRepresentationToDomain(representation: AccountRepresentation): Account = Account(
            email = representation.email,
            password = representation.password,
            username = representation.email,
            firstName = representation.firstName,
            lastName = representation.lastName,
            authorities = listOf("ROLE_USER"),
            accountNonExpired = false,
            accountNonLocked = false,
            credentialsNonExpired = false
    )


}