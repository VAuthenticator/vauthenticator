package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.ticket.InvalidTicketException
import com.vauthenticator.server.ticket.TicketId
import com.vauthenticator.server.ticket.TicketRepository

class VerifyEMailChallenge(
    private val ticketRepository: TicketRepository,
    private val accountRepository: AccountRepository,
    private val mfaMethodsEnrollmentAssociation: MfaMethodsEnrollmentAssociation
) {

    fun verifyMail(ticket: String) {
        ticketRepository.loadFor(TicketId(ticket))
            .map {
                mfaMethodsEnrollmentAssociation.associate(ticket, true)
                enableAccountFrom(it.userName)
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket") }

    }

    private fun enableAccountFrom(email: String): Account =
        accountRepository.accountFor(email)
            .map { account ->
                val enabledAccount = makeAnAccountEnableForm(account)
                accountRepository.save(enabledAccount)
                enabledAccount
            }
            .orElseThrow { throw InvalidTicketException("The ticket associated with the username $email is not a valid ticket") }


    private fun makeAnAccountEnableForm(account: Account) =
        account.copy(accountNonLocked = true, enabled = true, emailVerified = true)

}

