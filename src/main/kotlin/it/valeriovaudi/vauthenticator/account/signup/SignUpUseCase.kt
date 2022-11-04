package it.valeriovaudi.vauthenticator.account.signup

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.mailverification.SendVerifyMailChallenge
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.InsufficientClientApplicationScopeException
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.password.VAuthenticatorPasswordEncoder

open class SignUpUseCase(
        private val clientAccountRepository: ClientApplicationRepository,
        private val accountRepository: AccountRepository,
        private val welcomeMailSender: MailSenderService,
        private val sendVerifyMailChallenge: SendVerifyMailChallenge,
        private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        clientAccountRepository.findOne(clientAppId)
                .map {
                    if(hasScopes(it.scopes.content)) {
                        val registeredAccount = account.copy(
                                authorities = it.authorities.content.map { it.content },
                                password = vAuthenticatorPasswordEncoder.encode(account.password))
                        accountRepository.create(registeredAccount)
                        welcomeMailSender.sendFor(registeredAccount)
                        sendVerifyMailChallenge.sendVerifyMail(account.email, clientAppId)
                    } else {
                        throw InsufficientClientApplicationScopeException("The client app ${clientAppId.content} does not support signup use case........ consider to add ${Scope.SIGN_UP.content} as scope")
                    }

                }
    }

    private fun hasScopes(scope : Set<Scope>) = scope.stream().anyMatch(::hasScope)
    private fun hasScope(scope : Scope) = setOf(Scope.SIGN_UP, Scope.MAIL_VERIFY).contains(scope)
}

