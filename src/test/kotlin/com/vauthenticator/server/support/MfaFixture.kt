package com.vauthenticator.server.support

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod

object MfaFixture {

    fun accountMfaAssociatedMfaMethods(email : String) = listOf(
        MfaAccountMethod(email, Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD)
    )
}