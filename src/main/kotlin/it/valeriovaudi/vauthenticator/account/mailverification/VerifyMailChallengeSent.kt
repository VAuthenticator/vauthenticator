package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.InvalidTicketException
import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.InsufficientClientApplicationScopeException
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope

class VerifyMailChallengeSent(private val clientAccountRepository: ClientApplicationRepository,
                              private val accountRepository: AccountRepository,
                              private val ticketRepository: TicketRepository) {


    fun verifyMail(ticket: String)  {
        ticketRepository.loadFor(VerificationTicket(ticket))
                .map { ticket ->
                    enableAccountForEnabledClientAppFrom(ticket)
                    revoke(ticket)
                }.orElseThrow { throw InvalidTicketException("Te ticket $ticket is not a valid ticket it seems to be expired") }
    }


    private fun enableAccountForEnabledClientAppFrom(ticket: Ticket) {
        clientAccountRepository.findOne(ClientAppId(ticket.clientAppId))
                .map { clientApplication ->
                    if (clientApplication.scopes.content.contains(Scope.MAIL_VERIFY)) {
                        enableAccountFrom(ticket)
                    } else {
                        throw InsufficientClientApplicationScopeException("The client app ${ticket.clientAppId} does not support signup use case........ consider to add ${Scope.MAIL_VERIFY.content} as scope")
                    }
                }.orElseThrow { throw InvalidTicketException("Te ticket ${ticket.verificationTicket.content} is not a valid ticket it seems to be assigned to a client app that does not exist") }

    }


    private fun enableAccountFrom(ticket: Ticket) {
        accountRepository.accountFor(ticket.email)
                .map { account -> accountRepository.save(makeAnAccountEnableForm(account)) }
                .orElseThrow { throw InvalidTicketException("Te ticket ${ticket.verificationTicket.content} is not a valid ticket") }

    }

    private fun makeAnAccountEnableForm(account: Account) =
            account.copy(accountNonLocked = true, enabled = true, emailVerified = true)

    private fun revoke(ticket: Ticket) =
            ticketRepository.delete(ticket.verificationTicket)

}

