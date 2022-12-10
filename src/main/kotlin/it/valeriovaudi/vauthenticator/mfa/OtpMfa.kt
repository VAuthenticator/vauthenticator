package it.valeriovaudi.vauthenticator.mfa

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil
import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.keys.KeyDecrypter
import it.valeriovaudi.vauthenticator.keys.KeyPurpose
import it.valeriovaudi.vauthenticator.keys.KeyRepository
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex
import org.springframework.boot.context.properties.ConfigurationProperties


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
    private val tokenTimeWindow: Int = properties.otpTimeToLiveInSeconds
    private val tokenTimeWindowMillis: Long = (tokenTimeWindow * 1000).toLong()

    override fun generateSecretKeyFor(account: Account): MfaSecret {
        val associatedMfa = mfaAccountMethodsRepository.findAll(account.email)
        val mfatMethod = associatedMfa[MfaMethod.EMAIL_MFA_METHOD]!!
        val encryptedSecret = keyRepository.keyFor(mfatMethod.key, KeyPurpose.MFA)
        val decryptedKey = keyDecrypter.decryptKey(encryptedSecret.dataKey.encryptedPrivateKeyAsString())
        return MfaSecret(decryptedKey)
    }

    override fun getTOTPCode(secretKey: MfaSecret): MfaChallenge {
        return MfaChallenge(
            TimeBasedOneTimePasswordUtil.generateNumberStringHex(
                secretKey.content(),
                System.currentTimeMillis(),
                tokenTimeWindow,
                properties.otpLength
            )
        )
    }

    override fun verify(account: Account, optCode: MfaChallenge) {
        val secretKey = generateSecretKeyFor(account).content()
        val base32 = Base32()
        val bytes = base32.decode(secretKey)
        val hexKey: String = Hex.encodeHexString(bytes)
        try {
            val validated =
                TimeBasedOneTimePasswordUtil.validateCurrentNumberHex(
                    hexKey,
                    optCode.content().toInt(),
                    tokenTimeWindowMillis,
                    System.currentTimeMillis(),
                    tokenTimeWindow,
                    properties.otpLength
                );
            if (!validated) {
                throw MfaException("Customer Code does not match with system code")
            }
        } catch (e: RuntimeException) {
            throw MfaException("Customer Code does not match with system code")
        }
    }

}

@ConfigurationProperties("mfa.otp")
data class OtpConfigurationProperties(val otpLength: Int, val otpTimeToLiveInSeconds: Int)