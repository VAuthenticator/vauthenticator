package com.vauthenticator.server.account

import com.vauthenticator.server.role.Role

const val EMAIL = "email@domain.com"

object AccountTestFixture {

    fun anAccount() = Account(
        enabled = true,
        username = EMAIL,
        password = "secret",
        authorities = emptySet(),
        email = EMAIL,
        firstName = "A First Name",
        lastName = "A Last Name",
        birthDate = Date.empty(),
        phone = Phone.empty(),
        locale = UserLocale.empty()
    )

    fun anAccount(roles: List<Role>) = Account(
        enabled = true,
        username = EMAIL,
        password = "secret",
        authorities = roles.map { it.name }.toSet(),
        email = EMAIL,
        firstName = "A First Name",
        lastName = "A Last Name",
        birthDate = Date.empty(),
        phone = Phone.empty(),
        locale = UserLocale.empty()
    )
}