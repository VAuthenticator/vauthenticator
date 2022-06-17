package it.valeriovaudi.vauthenticator.account.api

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.account.Date
import it.valeriovaudi.vauthenticator.account.Phone
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.support.TestingFixture
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
    lateinit var signUpUseCase: SignUpUseCase

    @Mock
    lateinit var accountRepository: AccountRepository

    private val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(AccountEndPoint(signUpUseCase, accountRepository))
                .build()
    }

    @Test
    internal fun `sign up a new account`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())
        val masterAccount = AccountTestFixture.anAccount().copy(accountNonExpired = true, emailVerified = true, accountNonLocked = true, credentialsNonExpired = true, enabled = true,)

        val clientAppId = "A_CLIENT_APP_ID"
        mokMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("clientId", "clientId")
                .header("Authorization", "Bearer ${TestingFixture.simpleJwtFor(clientAppId)}")
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isCreated)

        Mockito.verify(signUpUseCase).execute(ClientAppId(clientAppId), masterAccount)

        assertEquals(true, masterAccount.accountNonLocked)
        assertEquals(true, masterAccount.emailVerified)
        assertEquals(true, masterAccount.accountNonExpired)
        assertEquals(true, masterAccount.credentialsNonExpired)
        assertEquals(true, masterAccount.enabled)
    }

    @Test
    internal fun `update account details`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())
        val masterAccount = AccountTestFixture.anAccount().copy(accountNonExpired = true, emailVerified = true, accountNonLocked = true, credentialsNonExpired = true, enabled = true,)
        val clientAppId = "A_CLIENT_APP_ID"

        given(accountRepository.accountFor("email@domain.com"))
                .willAnswer { Optional.of(masterAccount) }

        mokMvc.perform(MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${TestingFixture.simpleJwtFor(clientAppId, "email@domain.com")}")
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        Mockito.verify(accountRepository).save(masterAccount)
    }

    @Test
    internal fun `when update account details fails due to token without user_name claim in access token`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())
        val clientAppId = "A_CLIENT_APP_ID"


        mokMvc.perform(MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${TestingFixture.simpleJwtFor(clientAppId)}")
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isForbidden)

        Mockito.verifyNoInteractions(accountRepository)
    }
    @Test
    internal fun `when update account details fails due to no token in the request`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())


        mokMvc.perform(MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        Mockito.verifyNoInteractions(accountRepository)
    }

    @Test
    internal fun `when update account details fails due to empty token in the request`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())


        mokMvc.perform(MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ")
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        Mockito.verifyNoInteractions(accountRepository)
    }

    @Test
    internal fun `when update account details fails due to empty Authorization header`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList(), birthDate = Date.nullValue().formattedDate(), phone = Phone.nullValue().formattedPhone())


        mokMvc.perform(MockMvcRequestBuilders.put("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", " ")
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)

        Mockito.verifyNoInteractions(accountRepository)
    }
}