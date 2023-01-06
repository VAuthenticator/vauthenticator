package com.vauthenticator.account.resetpassword

import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.tiket.InvalidTicketException
import com.vauthenticator.account.tiket.Ticket
import com.vauthenticator.account.tiket.TicketRepository
import com.vauthenticator.account.tiket.VerificationTicket
import com.vauthenticator.password.VAuthenticatorPasswordEncoder
import java.util.*

class ResetAccountPassword(
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,

    private val ticketRepository: TicketRepository
) {
    fun resetPasswordFromMailChallenge(verificationTicket: VerificationTicket, request: ResetPasswordRequest) {
        ticketRepository.loadFor(verificationTicket).map {
            passwordResetFor(it, request)
            ticketRepository.delete(verificationTicket)
        }
            .orElseThrow { throw InvalidTicketException("Te ticket ${verificationTicket.content} is not a valid ticket it seems to be used or expired") }
    }

    private fun passwordResetFor(it: Ticket, request: ResetPasswordRequest): Optional<Unit> =
        accountRepository.accountFor(it.email).map {
            val newAccount = it.copy(password = vAuthenticatorPasswordEncoder.encode(request.newPassword))
            accountRepository.save(newAccount)
        }

}