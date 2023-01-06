package com.vauthenticator.server.account.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class AdminAccountEndPointTest {

    lateinit var mokMvc: MockMvc

    @Mock
    lateinit var accountRepository: AccountRepository

    val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(AdminAccountEndPoint(accountRepository)).build()
    }

    @Test
    internal fun `find all accounts`() {
        val expectedRepresentation = listOf(
                AdminAccountApiRepresentation(email = "anemain@domain.com"),
                AdminAccountApiRepresentation(email = "anotheremain@domain.com")
        )
        val masterAccount = com.vauthenticator.server.account.AccountTestFixture.anAccount()

        given(accountRepository.findAll())
                .willReturn(
                        listOf(
                                masterAccount.copy(email = "anemain@domain.com"),
                                masterAccount.copy(email = "anotheremain@domain.com")
                        )
                )
        mokMvc.perform(get("/api/admin/accounts"))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedRepresentation)))
    }

    @Test
    internal fun `set an account as disabled`() {
        val representation = AdminAccountApiRepresentation(email = "anemail@domain.com", enabled = false)
        val masterAccount = com.vauthenticator.server.account.AccountTestFixture.anAccount().copy(enabled = false)

        given(accountRepository.accountFor("anemain@domain.com"))
                .willReturn(Optional.of(com.vauthenticator.server.account.AccountTestFixture.anAccount()))

        mokMvc.perform(put("/api/admin/accounts/anemain@domain.com/email")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(representation)))
                .andExpect(status().isNoContent)

        Mockito.verify(accountRepository).save(masterAccount)
    }
}