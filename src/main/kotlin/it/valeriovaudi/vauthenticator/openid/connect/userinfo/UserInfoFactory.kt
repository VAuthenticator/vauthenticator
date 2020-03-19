package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import it.valeriovaudi.vauthenticator.account.AccountRepository
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

open class UserInfoFactory(private val accountRepository: AccountRepository) {

    open fun newUserInfo(principal: JwtAuthenticationToken) =
            accountRepository.accountFor(userName(principal))
                    .let { account ->
                        var userInfo = UserInfo(username = userName(principal), authorities = authorities(principal))

                        userInfo = OpenIdClaimsProvider(account, userInfo)
                        userInfo = EmailClaimsProvider(account, userInfo)
                        ProfileClaimsProvider(account, userInfo)
                    }


    private fun authorities(principal: JwtAuthenticationToken) =
            principal.token.claims["authorities"] as List<String>

    private fun userName(principal: JwtAuthenticationToken) =
            principal.token.claims["user_name"] as String
}