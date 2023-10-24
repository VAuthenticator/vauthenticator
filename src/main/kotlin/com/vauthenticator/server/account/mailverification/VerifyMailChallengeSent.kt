package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.InvalidTicketException
import com.vauthenticator.server.account.ticket.Ticket
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.mfa.MfaMethod
import com.vauthenticator.server.mfa.MfaMethodsEnrolmentAssociation

class VerifyMailChallengeSent(
    private val accountRepository: AccountRepository,
    private val ticketRepository: TicketRepository,
    private val mfaMethodsEnrolmentAssociation: MfaMethodsEnrolmentAssociation
) {


    fun verifyMail(ticket: String) {
        ticketRepository.loadFor(VerificationTicket(ticket))
            .map { ticket ->
                enableAccountForEnabledClientAppFrom(ticket)
                revoke(ticket)
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket, it seems to be expired") }
    }


    private fun enableAccountForEnabledClientAppFrom(ticket: Ticket) {
        val account = enableAccountFrom(ticket)
        mfaMethodsEnrolmentAssociation.associate(account, MfaMethod.EMAIL_MFA_METHOD)
    }


    private fun enableAccountFrom(ticket: Ticket): Account =
        accountRepository.accountFor(ticket.email)
            .map { account ->
                val enabledAccount = makeAnAccountEnableForm(account)
                accountRepository.save(enabledAccount)
                enabledAccount
            }
            .orElseThrow { throw InvalidTicketException("The ticket ${ticket.verificationTicket.content} is not a valid ticket") }


    private fun makeAnAccountEnableForm(account: Account) =
        account.copy(accountNonLocked = true, enabled = true, emailVerified = true)

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.verificationTicket)

}

