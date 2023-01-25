package com.vauthenticator.server.account

import com.vauthenticator.server.role.Role

object AccountTestFixture {

    fun anAccount() = Account(
        enabled = true,
        username = "email@domain.com",
        password = "secret",
        authorities = emptyList(),
        email = "email@domain.com",
        firstName = "A First Name",
        lastName = "A Last Name",
        birthDate = Date.empty(),
        phone = Phone.empty(),
        locale = UserLocale.empty()
    )

    fun anAccount(roles: List<Role>) = Account(
        enabled = true,
        username = "email@domain.com",
        password = "secret",
        authorities = roles.map { it.name },
        email = "email@domain.com",
        firstName = "A First Name",
        lastName = "A Last Name",
        birthDate = Date.empty(),
        phone = Phone.empty(),
        locale = UserLocale.empty()
    )
}