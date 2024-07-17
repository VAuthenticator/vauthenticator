package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository

class MfaMethodsEnrolmentAssociation(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {

    fun associate(account: Account, emailMfaMethod: MfaMethod) {
        val email = account.email
        val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
        if (!mfaAccountMethods.any { it.method == emailMfaMethod}) {
            mfaAccountMethodsRepository.save(email, emailMfaMethod)
        }
    }
}
class MfaMethodsEnrollment(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {

    fun enroll(account: Account, emailMfaMethod: MfaMethod) {
        TODO()
    }
}