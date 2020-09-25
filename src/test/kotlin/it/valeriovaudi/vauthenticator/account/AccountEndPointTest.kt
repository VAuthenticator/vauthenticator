package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
    internal fun `find all accounts`() {
        val expectedRepresentation = listOf(
                AccountApiRepresentation(email = "anemail@domain.com"),
                AccountApiRepresentation(email = "anotheremail@domain.com")
        )
        val masterAccount = AccountTestFixture.anAccount()

        given(accountRepository.findAll())
                .willReturn(
                        listOf(
                                masterAccount.copy(email = "anemail@domain.com"),
                                masterAccount.copy(email = "anotheremail@domain.com")
                        )
                )
        mokMvc.perform(get("/api/accounts"))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedRepresentation)))
    }
}