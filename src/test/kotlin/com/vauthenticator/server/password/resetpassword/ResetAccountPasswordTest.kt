package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.InvalidTicketException
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.events.ResetPasswordEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.support.TicketFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertThrows
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
    lateinit var passwordPolicy: PasswordPolicy

    @MockK
    lateinit var vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder

    @MockK
    lateinit var eventsDispatcher : VAuthenticatorEventsDispatcher

    @BeforeEach
    internal fun setUp() {
        underTest =
            ResetAccountPassword(eventsDispatcher,accountRepository, vAuthenticatorPasswordEncoder, passwordPolicy, ticketRepository)
    }

    @Test
    internal fun `happy path`() {
        val anAccount = anAccount()
        val email = anAccount.email

        val verificationTicket = VerificationTicket("A_TICKET")
        val ticket = TicketFixture.ticketFor(verificationTicket.content, email, "")

        every { passwordPolicy.accept(email,"NEW_PSWD") } just runs
        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(ticket)
        every { ticketRepository.delete(verificationTicket) } just runs
        every { accountRepository.accountFor(email) } returns Optional.of(anAccount)
        every { accountRepository.save(anAccount.copy(password = "NEW_PSWD")) } just runs
        every { vAuthenticatorPasswordEncoder.encode("NEW_PSWD") } returns "NEW_PSWD"
        every { eventsDispatcher.dispatch(any<ResetPasswordEvent>()) } just runs

        underTest.resetPasswordFromMailChallenge(verificationTicket, ResetPasswordRequest("NEW_PSWD"))
    }

    @Test
    internal fun `when a ticket was revoked`() {
        val verificationTicket = VerificationTicket("A_TICKET")

        every { passwordPolicy.accept("A_USERNAME", "NEW_PSWD") } just runs
        every { ticketRepository.loadFor(verificationTicket) } returns Optional.empty()

        assertThrows(InvalidTicketException::class.java) {
            underTest.resetPasswordFromMailChallenge(
                verificationTicket,
                ResetPasswordRequest("NEW_PSWD")
            )
        }
    }
}