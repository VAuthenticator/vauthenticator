package com.vauthenticator.account.welcome

import com.vauthenticator.account.AccountNotFoundException
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.mail.MailSenderService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class SayWelcomeTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var welcomeMailSender: MailSenderService

    lateinit var underTest: SayWelcome

    @BeforeEach
    internal fun setUp() {
        underTest = SayWelcome(accountRepository, welcomeMailSender)
    }

    @Test
    internal fun `happy path`() {
        val email = "email@domain.com"
        val anAccount = com.vauthenticator.account.AccountTestFixture.anAccount()
        every { accountRepository.accountFor(email) } returns Optional.of(anAccount)
        every { welcomeMailSender.sendFor(anAccount) } just runs

        underTest.welcome(email)

        verify { welcomeMailSender.sendFor(anAccount) }
    }

    @Test
    internal fun `sed mail for a non registered account`() {
        val email = "email@domain.com"
        val anAccount = com.vauthenticator.account.AccountTestFixture.anAccount()
        every { accountRepository.accountFor(email) } returns Optional.empty()

        assertThrows(AccountNotFoundException::class.java) { underTest.welcome(email) }

        verify(exactly = 0) { welcomeMailSender.sendFor(anAccount) }
    }
}