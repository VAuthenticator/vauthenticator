package com.vauthenticator.server.account.api

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.api.AccountConverter.fromDomainToAccountApiRepresentation
import com.vauthenticator.server.config.adminRole
import com.vauthenticator.server.role.Role
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AccountConverterTest {

    @Test
    fun whenAccountViewIsForAdmin() {
        val anAccount = anAccount(listOf(Role(adminRole, "A ROLE DESCRIPTION")))
        UsernamePasswordAuthenticationToken(mockk(), mockk(), listOf(SimpleGrantedAuthority(
            adminRole
        )))
        val accountApiRepresentation = fromDomainToAccountApiRepresentation(anAccount)
        assertEquals(
            AdminAccountApiRepresentation(accountLocked = true, enabled = true, email = "email@domain.com", authorities = listOf(
                adminRole
            )), accountApiRepresentation)
    }
}