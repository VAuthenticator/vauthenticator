package com.vauthenticator.account.api

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.account.api.AccountConverter.fromDomainToAccountApiRepresentation
import com.vauthenticator.config.adminRole
import com.vauthenticator.role.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AccountConverterTest {

    @Test
    fun whenAccountViewIsForAdmin() {
        val anAccount = anAccount(listOf(Role(adminRole, "A ROLE DESCRIPTION")))
        UsernamePasswordAuthenticationToken(mock(Any::class.java), mock(Any::class.java), listOf(SimpleGrantedAuthority(
            adminRole
        )))
        val accountApiRepresentation = fromDomainToAccountApiRepresentation(anAccount)
        assertEquals(
            AdminAccountApiRepresentation(accountLocked = true, enabled = true, email = "email@domain.com", authorities = listOf(
                adminRole
            )), accountApiRepresentation)
    }
}