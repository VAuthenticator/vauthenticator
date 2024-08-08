package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService


interface OtpMfaSender {
    fun sendMfaChallenge(userName: String, mfaMethod: MfaMethod, mfaChannel: String)
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