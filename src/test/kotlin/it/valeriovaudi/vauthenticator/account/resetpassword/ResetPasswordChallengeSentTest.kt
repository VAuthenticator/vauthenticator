package it.valeriovaudi.vauthenticator.account.resetpassword

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.support.TicketFixture
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ResetPasswordChallengeSentTest {

    lateinit var underTest: ResetPasswordChallengeSent

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketRepository: TicketRepository

    @BeforeEach
    internal fun setUp() {
        underTest = ResetPasswordChallengeSent(accountRepository, ticketRepository)
    }

    @Test
    internal fun `happy path`() {
        val anAccount = anAccount()
        val email = anAccount.email

        val verificationTicket = VerificationTicket("A_TIKET")
        val ticket = TicketFixture.ticketFor(verificationTicket.content, email, "")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(ticket)
        every { ticketRepository.delete(verificationTicket) } just runs
        every { accountRepository.accountFor(email) } returns Optional.of(anAccount)
        every { accountRepository.save(anAccount.copy(password = "NEW_PSWD")) } just runs

        underTest.resetPassword(verificationTicket, ResetPasswordRequest("NEW_PSWD"))
    }
}