package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService


interface OtpMfaSender {
    fun sendMfaChallengeFor(userName: String, mfaDeviceId: MfaDeviceId)
    fun sendMfaChallengeFor(userName: String)
}


class OtpMfaEmailSender(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaMailSender: EMailSenderService,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) : OtpMfaSender {

    override fun sendMfaChallengeFor(userName: String, mfaDeviceId: MfaDeviceId) {
        mfaAccountMethodsRepository.findBy(mfaDeviceId)
            .map {
                sendMfaChallengeFor(
                    userName,
                    it.mfaMethod,
                    it.mfaChannel
                )
            }
    }

    override fun sendMfaChallengeFor(userName: String) {
        mfaAccountMethodsRepository.getDefaultDevice(userName)
            .flatMap { mfaAccountMethodsRepository.findBy(it) }
            .map {
                sendMfaChallengeFor(
                    userName,
                    MfaMethod.EMAIL_MFA_METHOD,
                    it.mfaChannel
                )
            }
    }

    private fun sendMfaChallengeFor(userName: String, mfaMethod: MfaMethod, mfaChannel: String) {
        val account = accountRepository.accountFor(userName).get()
        val mfaSecret = otpMfa.generateSecretKeyFor(account, mfaMethod, mfaChannel)
        val mfaCode = otpMfa.getTOTPCode(mfaSecret).content()
        mfaMailSender.sendFor(account, mapOf("email" to mfaChannel, "mfaCode" to mfaCode))
    }
}