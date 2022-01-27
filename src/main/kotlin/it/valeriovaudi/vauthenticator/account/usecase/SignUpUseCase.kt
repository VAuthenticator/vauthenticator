package it.valeriovaudi.vauthenticator.account.usecase

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope

class SignUpUseCase(
        private val clientAccountRepository: ClientApplicationRepository,
        private val accountRepository: AccountRepository
) {
    fun execute(clientAppId: ClientAppId, account: Account) {
        clientAccountRepository.findOne(clientAppId)
                .map {
                    if(it.scopes.content.contains(Scope.SIGN_UP)){
                        accountRepository.create(
                                account.copy(authorities = it.authorities.content.map { it.content })
                        )
                    } else {
                        throw UnsupportedSignUpUseCaseException("The client app ${clientAppId.content} does not support signup use case........ consider to add ${Scope.SIGN_UP.content} as scope")
                    }

                }
    }

}

class UnsupportedSignUpUseCaseException(message : String) : RuntimeException(message)