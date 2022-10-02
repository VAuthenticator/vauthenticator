package it.valeriovaudi.vauthenticator.account.welcome

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockKExtension::class)
internal class WelcomeMailEndPointTest {
    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var welcomeMailSender: WelcomeMailSender

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(WelcomeMailEndPoint(accountRepository, welcomeMailSender)).build()
    }

    @Test
    internal fun `happy path`() {
        val anAccount = AccountTestFixture.anAccount()
        every { accountRepository.accountFor("email@domain.com") } returns Optional.of(anAccount)
        every { welcomeMailSender.sendFor(anAccount) } just runs

        mokMvc.perform(get("/sign-up/mail/email@domain.com/welcome"))
                .andExpect(status().isNoContent)

        verify { welcomeMailSender.sendFor(anAccount) }
    }
}