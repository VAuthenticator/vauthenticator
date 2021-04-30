package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.role.Role

object AccountTestFixture {

    fun anAccount() = Account(
            username = "email@domail.com",
            password = "secret",
            authorities = emptyList(),
            email = "email@domail.com",
            firstName = "A First Name",
            lastName = "A Last Name"
    )

    fun anAccount(roles:List<Role>) = Account(
            username = "email@domail.com",
            password = "secret",
            authorities = roles.map { it.name },
            email = "email@domail.com",
            firstName = "A First Name",
            lastName = "A Last Name"
    )
}