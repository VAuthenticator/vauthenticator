package it.valeriovaudi.vauthenticator.account.api

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class AccountEndPointTest {

    lateinit var mokMvc: MockMvc

    @Mock
    lateinit var accountRepository: AccountRepository

    val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(AccountEndPoint(accountRepository)).build()
    }


    @Test
    internal fun `sign up a new account`() {
        val representation = FinalAccountRepresentation(email = "email@domain.com", password = "secret", firstName = "A First Name", lastName = "A Last Name", authorities = emptyList())
        val masterAccount = AccountTestFixture.anAccount().copy(accountNonExpired = true, emailVerified = true, accountNonLocked = true, credentialsNonExpired = true, enabled = true)


        mokMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(MockMvcResultMatchers.status().isCreated)

        assertEquals(true, masterAccount.accountNonLocked)
        assertEquals(true, masterAccount.emailVerified)
        assertEquals(true, masterAccount.accountNonExpired)
        assertEquals(true, masterAccount.credentialsNonExpired)
        assertEquals(true, masterAccount.enabled)

        Mockito.verify(accountRepository).create(masterAccount)
    }
}