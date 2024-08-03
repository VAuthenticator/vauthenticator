package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService


interface OtpMfaSender {
    fun sendMfaChallenge(userName: String, mfaMethod: MfaMethod, mfaChannel: String)
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeToBeAssociatedFor(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        challenge: MfaChallenge
    )

    fun verifyAssociatedMfaChallengeFor(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        challenge: MfaChallenge
    )
}

class OtpMfaEmailSender(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaMailSender: EMailSenderService
) : OtpMfaSender {

    override fun sendMfaChallenge(userName: String, mfaMethod: MfaMethod, mfaChannel: String) {
        val account = accountRepository.accountFor(userName).get()
        val mfaSecret = otpMfa.generateSecretKeyFor(account, mfaMethod, mfaChannel)
        val mfaCode = otpMfa.getTOTPCode(mfaSecret).content()
        mfaMailSender.sendFor(account, mapOf("email" to mfaChannel, "mfaCode" to mfaCode))
    }
}

class AccountAwareOtpMfaVerifier(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) : OtpMfaVerifier {

    override fun verifyMfaChallengeToBeAssociatedFor(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        challenge: MfaChallenge
    ) {
        mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, mfaChannel)
            .map {
                val account = accountRepository.accountFor(userName).get()
                if (!it.associated) {
                    otpMfa.verify(account, mfaMethod, mfaChannel, challenge)
                } else {
                    throw AssociatedMfaVerificationException("Mfa Challenge verification failed: this mfa method is already associated")
                }
            }
    }

    override fun verifyAssociatedMfaChallengeFor(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        challenge: MfaChallenge
    ) {
        mfaAccountMethodsRepository.findBy(userName, MfaMethod.EMAIL_MFA_METHOD, mfaChannel)
            .map {
                val account = accountRepository.accountFor(userName).get()
                if (it.associated) {
                    otpMfa.verify(account, mfaMethod, mfaChannel, challenge)
                } else {
                    throw UnAssociatedMfaVerificationException("Mfa Challenge verification failed: this mfa method has to be associated")
                }
            }
    }

}