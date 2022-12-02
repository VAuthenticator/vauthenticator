package it.valeriovaudi.vauthenticator.mfa

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil
import it.valeriovaudi.vauthenticator.account.Account
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex


interface OtpMfa {
    fun generateSecretKeyFor(account: Account): MfaSecret
    fun getTOTPCode(secretKey: MfaSecret): MfaChallenge
    fun verify(account: Account, optCode: MfaChallenge)
}

class TaimosOtpMfa(private val properties: OtpConfigurationProperties) : OtpMfa {
    private val tokenTimeWindow: Int = properties.otpTimeToLiveInSeconds
    private val tokenTimeWindowMillis: Long = (tokenTimeWindow * 1000).toLong()

    override fun generateSecretKeyFor(account: Account): MfaSecret {
        val base32 = Base32()
        return MfaSecret(base32.encodeToString(account.email.toByteArray()))
    }

    override fun getTOTPCode(secretKey: MfaSecret): MfaChallenge {
        println("properties")
        println(properties)
        val base32 = Base32()
        val bytes = base32.decode(secretKey.content())
        val hexKey: String = Hex.encodeHexString(bytes)
        return MfaChallenge(
            TimeBasedOneTimePasswordUtil.generateNumberStringHex(
                hexKey,
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

data class OtpConfigurationProperties(val otpLength: Int, val otpTimeToLiveInSeconds: Int)