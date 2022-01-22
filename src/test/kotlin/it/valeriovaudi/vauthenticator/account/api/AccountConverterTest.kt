package it.valeriovaudi.vauthenticator.account.api

import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.api.AccountConverter.fromDomainToAccountApiRepresentation
import it.valeriovaudi.vauthenticator.config.adminRole
import it.valeriovaudi.vauthenticator.role.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class AccountConverterTest {

    @Test
    fun whenAccountViewIsForAdmin() {
        val anAccount = anAccount(listOf(Role(adminRole, "A ROLE DESCRIPTION")))
        val authentication = UsernamePasswordAuthenticationToken(mock(Any::class.java), mock(Any::class.java), listOf(SimpleGrantedAuthority(adminRole)))
        val accountApiRepresentation = fromDomainToAccountApiRepresentation(anAccount, authentication)
        assertEquals(AccountApiAdminRepresentation(accountLocked = true, enabled = true, email = "email@domail.com", authorities = listOf(adminRole)), accountApiRepresentation)
    }

    @Test
    fun whenAccountViewIsForFinalUser() {
        val anAccount = anAccount(listOf(Role("A_ROLE", "A ROLE DESCRIPTION")))
        val authentication = UsernamePasswordAuthenticationToken(mock(Any::class.java), mock(Any::class.java), listOf(SimpleGrantedAuthority("A_ROLE")))
        val accountApiRepresentation = fromDomainToAccountApiRepresentation(anAccount, authentication)
        assertEquals(AccountApiUserRepresentation(email = "email@domail.com", firstName = "A First Name", lastName = "A Last Name", authorities = listOf("A_ROLE")), accountApiRepresentation)
    }
}