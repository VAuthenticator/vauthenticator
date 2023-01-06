package com.vauthenticator.server.mfa

import com.vauthenticator.server.account.Account

class MfaMethodsEnrolmentAssociation(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {

    fun associate(account: Account, emailMfaMethod: MfaMethod) {
        val email = account.email
        val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
        if (!mfaAccountMethods.containsKey(emailMfaMethod)) {
            mfaAccountMethodsRepository.save(email, emailMfaMethod)
        }
    }
}