package it.valeriovaudi.vauthenticator.account.welcome

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpConfirmationMailSender
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class WelcomeMailEndPointTest {
    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var signUpConfirmationMailSender: SignUpConfirmationMailSender

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(WelcomeMailEndPoint(accountRepository, signUpConfirmationMailSender)).build()
    }

    @Test
    internal fun `happy path`() {
        mokMvc.perform(get("/sign-up/mail/mail@test.com/welcome"))
                .andExpect(status().isOk())
    }
}