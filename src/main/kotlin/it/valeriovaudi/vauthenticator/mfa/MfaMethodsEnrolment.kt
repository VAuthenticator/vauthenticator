package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.account.Account

class MfaMethodsEnrolmentAssociation(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {

    fun associate(account: Account, emailMfaMethod: MfaMethod) {
        val email = account.email
        val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
        if (!mfaAccountMethods.containsKey(emailMfaMethod)) {
            mfaAccountMethodsRepository.save(email, emailMfaMethod)
        }
    }
}