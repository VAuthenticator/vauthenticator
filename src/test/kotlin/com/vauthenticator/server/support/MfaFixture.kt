package com.vauthenticator.server.support

import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import java.util.*

object MfaFixture {

    fun accountMfaAssociatedMfaMethods(email: String) = listOf(
        MfaAccountMethod(email, mfaDeviceId, keyId, MfaMethod.EMAIL_MFA_METHOD, email, true)
    )

    fun associatedMfaAccountMethod(userName: String, email: String) =
       Optional.of(MfaAccountMethod(userName, mfaDeviceId, keyId, MfaMethod.EMAIL_MFA_METHOD, email, true))

    fun notAssociatedMfaAccountMethod(userName: String, email: String) =
       Optional.of(MfaAccountMethod(userName, mfaDeviceId, keyId, MfaMethod.EMAIL_MFA_METHOD, email, false))

    val account = anAccount()
    val userName = account.email
    const val email = "a_new_email@email.com"
    val challenge = MfaChallenge("AN_MFA_CHALLENGE")
    val mfaDeviceId = MfaDeviceId("AN_MFA_DEVICE_ID")
    val mfaSecret = MfaSecret("AN_MFA_SECRET")
    val keyId = Kid("A_KID")

}