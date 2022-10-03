package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import org.springframework.web.bind.annotation.PathVariable

class MailVerificationUseCase(private val clientAccountRepository: ClientApplicationRepository,
                              private val accountRepository: AccountRepository,
                              private val mailVerificationTicketFactory: MailVerificationTicketFactory,
                              private val mailVerificationMailSender: MailVerificationMailSender) {

    fun sendVerifyMail(mail: String, clientAppId: ClientAppId) {
        clientAccountRepository.findOne(clientAppId)
                .map { clientApp ->
                    if (allowedOperationFor(clientApp)) {
                        accountRepository.accountFor(mail)
                                .map { account ->
                                    val verificationTicket = mailVerificationTicketFactory.createTicketFor(account, clientApp)
                                    mailVerificationMailSender.sendFor(account, verificationTicket)
                                }
                    }
                }

    }

    private fun allowedOperationFor(clientApp: ClientApplication) =
            clientApp.scopes.content.contains(Scope.MAIL_VERIFY)

    fun verifyMail(@PathVariable mail: String, clientAppId: ClientAppId): Unit = TODO()

}