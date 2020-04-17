package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import it.valeriovaudi.vauthenticator.account.Account
import java.time.LocalDateTime
import java.time.ZoneOffset

typealias ClaimsProvider = (Account, UserInfo) -> UserInfo

object EmailClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {
        return userInfo.copy(email = account.email, email_verified = true);
    }

}


object ProfileClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {

        return userInfo.copy(
                name = "${account.firstName} ${account.lastName}",
                given_name = account.firstName,
                family_name = account.lastName,
                middle_name = "",
                updated_at = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )

    }

}

object OpenIdClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {
        return userInfo.copy(
                sub = account.email
        )

    }

}
