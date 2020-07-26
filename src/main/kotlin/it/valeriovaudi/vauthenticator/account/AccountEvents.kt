package it.valeriovaudi.vauthenticator.account

sealed class AccountEvents
data class AccountCreated(val email: String, val firstName: String, val lastName: String) : AccountEvents()
data class AccountCreationErrorOnAuthSystem(val email: String, val firstName: String, val lastName: String, val error: AccountRegistrationError) : AccountEvents()
data class AccountRegistrationError(val message: String) : AccountEvents()

data class AccountUpdated(val email: String, val firstName: String, val lastName: String) : AccountEvents()

