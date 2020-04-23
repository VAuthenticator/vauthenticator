package it.valeriovaudi.vauthenticator.account

object AccountTestFixture {

    fun anAccount(sub: String) = Account(
            username = "email@domail.com",
            password = "secret",
            authorities = emptyList(),
            sub = sub,
            email = "email@domail.com",
            firstName = "A First Name",
            lastName = "A Last Name"
    )
}