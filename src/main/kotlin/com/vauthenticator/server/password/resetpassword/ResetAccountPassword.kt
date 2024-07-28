package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.events.ResetPasswordEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.ticket.InvalidTicketException
import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketId
import com.vauthenticator.server.ticket.TicketRepository
import java.time.Instant
import java.util.*

class ResetAccountPassword(
    private val eventsDispatcher: VAuthenticatorEventsDispatcher,
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val passwordPolicy: PasswordPolicy,
    private val ticketRepository: TicketRepository
) {
    fun resetPasswordFromMailChallenge(ticketId: TicketId, request: ResetPasswordRequest) {
        ticketRepository.loadFor(ticketId).map {
            passwordPolicy.accept(it.userName, request.newPassword)
            val encodedNewPassword = vAuthenticatorPasswordEncoder.encode(request.newPassword)
            passwordResetFor(it, request.copy(newPassword = encodedNewPassword))
            ticketRepository.delete(ticketId)
            eventsDispatcher.dispatch(
                ResetPasswordEvent(
                    Email(it.userName),
                    ClientAppId.empty(),
                    Instant.now(),
                    Password(encodedNewPassword)
                )
            )
        }
            .orElseThrow { throw InvalidTicketException("The ticket ${ticketId.content} is not a valid ticket it seems to be used or expired") }
    }

    private fun passwordResetFor(it: Ticket, request: ResetPasswordRequest): Optional<Unit> =
        accountRepository.accountFor(it.userName).map {
            val newAccount = it.copy(password = request.newPassword)
            accountRepository.save(newAccount)
        }

}