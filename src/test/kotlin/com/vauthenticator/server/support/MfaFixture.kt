package com.vauthenticator.server.support

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaChallenge
import com.vauthenticator.server.mfa.domain.MfaDeviceId
import com.vauthenticator.server.mfa.domain.MfaMethod
import java.util.*

object MfaFixture {

    fun accountMfaAssociatedMfaMethods(email: String) = listOf(
        MfaAccountMethod(email, MfaDeviceId("A_MFA_DEVICE_ID"), Kid("A_KID"), MfaMethod.EMAIL_MFA_METHOD, email, true)
    )

    fun associatedMfaAccountMethod(userName: String, email: String) =
       Optional.of(MfaAccountMethod(userName, mfaDeviceId, keyId, MfaMethod.EMAIL_MFA_METHOD, email, true))

    fun notAssociatedMfaAccountMethod(userName: String, email: String) =
       Optional.of(MfaAccountMethod(userName, mfaDeviceId, keyId, MfaMethod.EMAIL_MFA_METHOD, email, false))

    val challenge = MfaChallenge("AN_MFA_CHALLENGE")
    val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
    val keyId = Kid("A_KID")


}