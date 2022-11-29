package it.valeriovaudi.vauthenticator.mfa

import de.taimos.totp.TOTP
import it.valeriovaudi.vauthenticator.account.Account
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex


interface OtpMfa {
    fun generateSecretKeyFor(account: Account): MfaSecret
    fun getTOTPCode(secretKey: MfaSecret): MfaChallenge
    fun verify(account: Account, optCode: MfaChallenge)
}

class TaimosOtpMfa : OtpMfa {

    override fun generateSecretKeyFor(account: Account): MfaSecret {
        val base32 = Base32()
        return MfaSecret(base32.encodeToString(account.email.toByteArray()))
    }

    override fun getTOTPCode(secretKey: MfaSecret): MfaChallenge {
        val base32 = Base32()
        val bytes = base32.decode(secretKey.content())
        val hexKey: String = Hex.encodeHexString(bytes)
        return MfaChallenge(TOTP.getOTP(hexKey))
    }

    override fun verify(account: Account, optCode: MfaChallenge) {
        val secretKey = generateSecretKeyFor(account)
        val code = getTOTPCode(secretKey)
        if (optCode != code) {
            throw MfaException("Customer Code does not match with system code")
        }
    }

}