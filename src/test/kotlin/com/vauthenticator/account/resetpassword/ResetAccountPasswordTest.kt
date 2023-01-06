package com.vauthenticator.account.resetpassword

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.tiket.InvalidTicketException
import com.vauthenticator.account.tiket.TicketRepository
import com.vauthenticator.account.tiket.VerificationTicket
import com.vauthenticator.password.VAuthenticatorPasswordEncoder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.support.TicketFixture
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ResetAccountPasswordTest {

    lateinit var underTest: ResetAccountPassword

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketRepository: TicketRepository

    @MockK
    lateinit var vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder

    @BeforeEach
    internal fun setUp() {
        underTest = ResetAccountPassword(accountRepository, vAuthenticatorPasswordEncoder, ticketRepository)
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
        every { vAuthenticatorPasswordEncoder.encode("NEW_PSWD") } returns "NEW_PSWD"

        underTest.resetPasswordFromMailChallenge(verificationTicket, ResetPasswordRequest("NEW_PSWD"))
    }

    @Test
    internal fun `when a ticket was revoked`() {
        val verificationTicket = VerificationTicket("A_TIKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.empty()

        assertThrows(InvalidTicketException::class.java) {
            underTest.resetPasswordFromMailChallenge(
                verificationTicket,
                ResetPasswordRequest("NEW_PSWD")
            )
        }
    }
}