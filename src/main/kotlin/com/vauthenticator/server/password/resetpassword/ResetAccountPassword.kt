package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.InvalidTicketException
import com.vauthenticator.server.account.tiket.Ticket
import com.vauthenticator.server.account.tiket.TicketRepository
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import java.util.*

class ResetAccountPassword(
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val passwordPolicy: PasswordPolicy,
    private val ticketRepository: TicketRepository
) {
    fun resetPasswordFromMailChallenge(verificationTicket: VerificationTicket, request: ResetPasswordRequest) {
        ticketRepository.loadFor(verificationTicket).map {
            passwordPolicy.accept(it.email, request.newPassword)
            passwordResetFor(it, request)
            ticketRepository.delete(verificationTicket)
        }
            .orElseThrow { throw InvalidTicketException("The ticket ${verificationTicket.content} is not a valid ticket it seems to be used or expired") }
    }

    private fun passwordResetFor(it: Ticket, request: ResetPasswordRequest): Optional<Unit> =
        accountRepository.accountFor(it.email).map {
            val newAccount = it.copy(password = vAuthenticatorPasswordEncoder.encode(request.newPassword))
            accountRepository.save(newAccount)
        }

}