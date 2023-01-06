package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.InvalidTicketException
import com.vauthenticator.server.account.tiket.Ticket
import com.vauthenticator.server.account.tiket.TicketRepository
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.mfa.MfaMethod
import com.vauthenticator.server.mfa.MfaMethodsEnrolmentAssociation
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.server.oauth2.clientapp.Scope

class VerifyMailChallengeSent(
    private val clientAccountRepository: ClientApplicationRepository,
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
            .orElseThrow { throw InvalidTicketException("Te ticket $ticket is not a valid ticket it seems to be expired") }
    }


    private fun enableAccountForEnabledClientAppFrom(ticket: Ticket) {
        clientAccountRepository.findOne(ClientAppId(ticket.clientAppId))
            .map { clientApplication ->
                if (clientApplication.scopes.content.contains(Scope.MAIL_VERIFY)) {
                    val account = enableAccountFrom(ticket)
                    mfaMethodsEnrolmentAssociation.associate(account, MfaMethod.EMAIL_MFA_METHOD)
                } else {
                    throw InsufficientClientApplicationScopeException("The client app ${ticket.clientAppId} does not support signup use case........ consider to add ${Scope.MAIL_VERIFY.content} as scope")
                }
            }
            .orElseThrow { throw InvalidTicketException("Te ticket ${ticket.verificationTicket.content} is not a valid ticket it seems to be assigned to a client app that does not exist") }

    }


    private fun enableAccountFrom(ticket: Ticket): Account =
        accountRepository.accountFor(ticket.email)
            .map { account ->
                val enabledAccount = makeAnAccountEnableForm(account)
                accountRepository.save(enabledAccount)
                enabledAccount
            }
            .orElseThrow { throw InvalidTicketException("Te ticket ${ticket.verificationTicket.content} is not a valid ticket") }


    private fun makeAnAccountEnableForm(account: Account) =
        account.copy(accountNonLocked = true, enabled = true, emailVerified = true)

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.verificationTicket)

}

