package com.vauthenticator.server.mfa.domain

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil
import com.vauthenticator.server.account.Account
import com.vauthenticator.server.extentions.decoder
import com.vauthenticator.server.keys.KeyDecrypter
import com.vauthenticator.server.keys.KeyPurpose
import com.vauthenticator.server.keys.KeyRepository
import com.vauthenticator.server.mfa.OtpConfigurationProperties
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import org.apache.commons.codec.binary.Hex

//todo the interface has to take in account the enrolled method
interface OtpMfa {
    fun generateSecretKeyFor(account: Account): MfaSecret
    fun getTOTPCode(secretKey: MfaSecret): MfaChallenge
    fun verify(account: Account, optCode: MfaChallenge)
}

class TaimosOtpMfa(
    private val keyDecrypter: KeyDecrypter,
    private val keyRepository: KeyRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val properties: OtpConfigurationProperties
) : OtpMfa {
    private val tokenTimeWindow: Int = properties.timeToLiveInSeconds
    private val tokenTimeWindowMillis: Long = (tokenTimeWindow * 1000).toLong()

    // todo to be improved
    override fun generateSecretKeyFor(account: Account): MfaSecret {
        //todo
        val mfatMethod =
            mfaAccountMethodsRepository.findOne(account.email, MfaMethod.EMAIL_MFA_METHOD, account.email).orElseGet { null }
        val encryptedSecret = keyRepository.keyFor(mfatMethod.key, KeyPurpose.MFA)
        val decryptKeyAsByteArray = keyDecrypter.decryptKey(encryptedSecret.dataKey.encryptedPrivateKeyAsString())
        val decryptedKey = Hex.encodeHexString(decoder.decode(decryptKeyAsByteArray))
        return MfaSecret(decryptedKey)
    }

    override fun getTOTPCode(secretKey: MfaSecret): MfaChallenge {
        return MfaChallenge(
            TimeBasedOneTimePasswordUtil.generateNumberStringHex(
                secretKey.content(),
                System.currentTimeMillis(),
                tokenTimeWindow,
                properties.length
            )
        )
    }

    override fun verify(account: Account, optCode: MfaChallenge) {
        val mfaSecret = generateSecretKeyFor(account)
        try {
            val validated =
                TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(
                    mfaSecret.content(),
                    optCode.content().toInt(),
                    tokenTimeWindowMillis,
                    System.currentTimeMillis(),
                    tokenTimeWindow,
                    properties.length
                );
            if (!validated) {
                throw MfaException("Customer Code does not match with system code")
            }
        } catch (e: RuntimeException) {
            throw MfaException("Customer Code does not match with system code")
        }
    }

}

