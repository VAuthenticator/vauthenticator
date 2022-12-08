package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.account.Account

class MfaMethodsEnrolmentAssociation(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {

    fun associate(account: Account, emailMfaMethod: MfaMethod) {
        val email = account.email
        mfaAccountMethodsRepository.findAll(email)
            .filter { it.method == emailMfaMethod }
            .ifEmpty { mfaAccountMethodsRepository.save(email, emailMfaMethod) }
    }
}