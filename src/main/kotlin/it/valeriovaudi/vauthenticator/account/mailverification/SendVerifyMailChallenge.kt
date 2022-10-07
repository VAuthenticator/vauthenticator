package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.AccountNotFoundException
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*

private const val LINK_KEY = "verificationMailLink"

class SendVerifyMailChallenge(private val clientAccountRepository: ClientApplicationRepository,
                              private val accountRepository: AccountRepository,
                              private val mailVerificationTicketFactory: MailVerificationTicketFactory,
                              private val mailVerificationMailSender: MailSenderService,
                              private val frontChannelBaseUrl: String) {

    fun sendVerifyMail(mail: String, clientAppId: ClientAppId) {
        clientAccountRepository.findOne(clientAppId)
                .map { clientApp ->
                    if (allowedOperationFor(clientApp)) {
                        sendVerificationTicketFor(mail, clientApp)
                    } else {
                        throw InsufficientClientApplicationScopeException("The client app ${clientAppId.content} does not support mail verification use case........ consider to add ${Scope.MAIL_VERIFY.content} as scope")
                    }
                }
    }

    private fun sendVerificationTicketFor(mail: String, clientApp: ClientApplication) =
            accountRepository.accountFor(mail)
                    .map { account ->
                        val verificationTicket = mailVerificationTicketFactory.createTicketFor(account, clientApp)
                        val mailContext = mailContextFrom(verificationTicket)
                        mailVerificationMailSender.sendFor(account, mailContext)
                    }.orElseThrow { AccountNotFoundException("account not found") }

    private fun mailContextFrom(verificationTicket: VerificationTicket): Map<String, String> {
        val verificationLink = "$frontChannelBaseUrl/verify-mail/${verificationTicket.content}"
        return mapOf(LINK_KEY to verificationLink)
    }

    private fun allowedOperationFor(clientApp: ClientApplication) =
            clientApp.scopes.content.contains(Scope.MAIL_VERIFY)

}