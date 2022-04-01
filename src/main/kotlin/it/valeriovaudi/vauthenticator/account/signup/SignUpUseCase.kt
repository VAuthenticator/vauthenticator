package it.valeriovaudi.vauthenticator.account.signup

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailMessage
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope

open class SignUpUseCase(
        private val clientAccountRepository: ClientApplicationRepository,
        private val accountRepository: AccountRepository,
        private val signUpConfirmationMailSender: SignUpConfirmationMailSender
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        clientAccountRepository.findOne(clientAppId)
                .map {
                    if (it.scopes.content.contains(Scope.SIGN_UP)) {
                        val registeredAccount = account.copy(authorities = it.authorities.content.map { it.content })
                        accountRepository.create(registeredAccount)
                        signUpConfirmationMailSender.sendConfirmation(registeredAccount)
                    } else {
                        throw UnsupportedSignUpUseCaseException("The client app ${clientAppId.content} does not support signup use case........ consider to add ${Scope.SIGN_UP.content} as scope")
                    }

                }
    }

}

open class SignUpConfirmationMailSender(
        private val mailSenderService: MailSenderService,
        private val mailConfiguration: SignUpConfirmationMailConfiguration) {

    open fun sendConfirmation(account: Account) {
        mailSenderService.send(MailMessage(account.email,
                mailConfiguration.from,
                mailConfiguration.subject,
                mailConfiguration.bodyTemplate.format(account.firstName)))
    }
}

class UnsupportedSignUpUseCaseException(message: String) : RuntimeException(message)