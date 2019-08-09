package it.valeriovaudi.vauthenticator.userdetails

data class SecurityAccountDetails(var username: String,
                                  var password: String,
                                  var accountNonExpired: Boolean = true,
                                  var accountNonLocked: Boolean = true,
                                  var credentialsNonExpired: Boolean = true,
                                  var enabled: Boolean = true,
                                  var authorities: List<String>)