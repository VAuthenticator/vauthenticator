package it.valeriovaudi.vauthenticator.account

object AccountTestFixture {

    fun anAccount() = Account(
            username = "email@domail.com",
            password = "secret",
            authorities = emptyList(),
            email = "email@domail.com",
            firstName = "A First Name",
            lastName = "A Last Name"
    )
}