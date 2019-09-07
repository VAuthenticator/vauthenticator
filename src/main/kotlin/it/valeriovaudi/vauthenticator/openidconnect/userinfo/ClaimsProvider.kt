package it.valeriovaudi.vauthenticator.openidconnect.userinfo

import it.valeriovaudi.vauthenticator.account.Account
import java.util.*

typealias ClaimsProvider = (Account, UserInfo) -> UserInfo

object EmailClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {
        return userInfo.copy(email = account.mail, email_verified = true);
    }

}


object ProfileClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {

        return userInfo.copy(
                name = "${account.firstName} ${account.lastName}",
                given_name = account.firstName,
                family_name = account.lastName,
                middle_name = ""
        )

    }

}

object OpenIdClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, userInfo: UserInfo): UserInfo {
        return userInfo.copy(
                sub = UUID.randomUUID().toString())

    }

}
