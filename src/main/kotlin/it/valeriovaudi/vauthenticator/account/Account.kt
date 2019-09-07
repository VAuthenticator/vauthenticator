package it.valeriovaudi.vauthenticator.account

data class Account(var accountNonExpired: Boolean = true,
                   var accountNonLocked: Boolean = true,
                   var credentialsNonExpired: Boolean = true,
                   var enabled: Boolean = true,

                   var userRoles: List<String>,

        // needed for email oidc profile
                   var mail: String,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String

)

