package com.vauthenticator.server.account.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.Phone
import com.vauthenticator.server.account.SaveAccount
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.signup.SignUpUse
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.SecurityFixture.principalFor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class AccountEndPointTest {

    lateinit var mokMvc: MockMvc

    @Mock
    lateinit var signUpUse: SignUpUse

    @Mock
    lateinit var accountRepository: AccountRepository

    private val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(AccountEndPoint(signUpUse, SaveAccount(accountRepository)))
            .build()
    }

    @Test
    internal fun `sign up a new account`() {
        val representation = FinalAccountRepresentation(
            email = "email@domain.com",
            password = "secret",
            firstName = "A First Name",
            lastName = "A Last Name",
            authorities = emptyList(),
            birthDate = "",
            phone = Phone.nullValue().formattedPhone()
        )
        val masterAccount = com.vauthenticator.server.account.AccountTestFixture.anAccount().copy(
            accountNonExpired = true,
            emailVerified = false,
            accountNonLocked = false,
            credentialsNonExpired = true,
            enabled = false,
        )

        val clientAppId = "A_CLIENT_APP_ID"
        mokMvc.perform(
            MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principalFor(clientAppId, "email@domain.com", listOf("VAUTHENTICATOR_ADMIN")))
                .content(objectMapper.writeValueAsString(representation))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

        Mockito.verify(signUpUse).execute(ClientAppId(clientAppId), masterAccount)

        assertEquals(false, masterAccount.accountNonLocked)
        assertEquals(false, masterAccount.emailVerified)
        assertEquals(true, masterAccount.accountNonExpired)
        assertEquals(true, masterAccount.credentialsNonExpired)
        assertEquals(false, masterAccount.enabled)
    }

    @Test
    internal fun `sign up a new account like form ui with client app id in the session`() {
        val representation = FinalAccountRepresentation(
            email = "email@domain.com",
            password = "secret",
            firstName = "A First Name",
            lastName = "A Last Name",
            authorities = emptyList(),
            birthDate = "",
            phone = Phone.nullValue().formattedPhone()
        )
        val masterAccount = com.vauthenticator.server.account.AccountTestFixture.anAccount().copy(
            accountNonExpired = true,
            emailVerified = false,
            accountNonLocked = false,
            credentialsNonExpired = true,
            enabled = false,
        )

        val clientAppId = "A_CLIENT_APP_ID"
        mokMvc.perform(
            MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("clientId", "A_CLIENT_APP_ID")
                .content(objectMapper.writeValueAsString(representation))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

        Mockito.verify(signUpUse).execute(ClientAppId(clientAppId), masterAccount)

        assertEquals(false, masterAccount.accountNonLocked)
        assertEquals(false, masterAccount.emailVerified)
        assertEquals(true, masterAccount.accountNonExpired)
        assertEquals(true, masterAccount.credentialsNonExpired)
        assertEquals(false, masterAccount.enabled)
    }

    @Test
    internal fun `update account details`() {
        val representation = FinalAccountRepresentation(
            email = "email@domain.com",
            password = "secret",
            firstName = "A First Name",
            lastName = "A Last Name",
            authorities = emptyList(),
            birthDate = "",
            phone = Phone.nullValue().formattedPhone()
        )
        val masterAccount = com.vauthenticator.server.account.AccountTestFixture.anAccount().copy(
            accountNonExpired = true,
            emailVerified = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
        )
        val clientAppId = "A_CLIENT_APP_ID"

        given(accountRepository.accountFor("email@domain.com"))
            .willAnswer { Optional.of(masterAccount) }

        mokMvc.perform(
            MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principalFor(clientAppId, "email@domain.com", listOf("VAUTHENTICATOR_ADMIN")))
                .content(objectMapper.writeValueAsString(representation))
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        Mockito.verify(accountRepository).save(masterAccount)
    }

}