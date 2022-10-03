package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.AccountNotFoundException
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*

class MailVerificationUseCase(private val clientAccountRepository: ClientApplicationRepository,
                              private val accountRepository: AccountRepository,
                              private val mailVerificationTicketFactory: MailVerificationTicketFactory,
                              private val mailVerificationMailSender: MailVerificationMailSender) {

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
                        mailVerificationMailSender.sendFor(account, verificationTicket)
                    }.orElseThrow { AccountNotFoundException("account not found") }

    private fun allowedOperationFor(clientApp: ClientApplication) =
            clientApp.scopes.content.contains(Scope.MAIL_VERIFY)

    fun verifyMail(): Unit = TODO()

}