package it.valeriovaudi.vauthenticator.oidc.userinfo

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext
import java.time.LocalDateTime
import java.time.ZoneOffset


open class UserInfoEnhancer(private val accountRepository: AccountRepository) {

    open fun oidcUserInfoFrom(principal: OidcUserInfoAuthenticationContext): OidcUserInfo =
        accountRepository.accountFor(userName(principal))
            .map { account ->
                val claims = mutableMapOf<String, Any>()

                claims["username"] = userName(principal)
                claims["authorities"] = authorities(principal)

                OpenIdClaimsProvider(account, claims)
                EmailClaimsProvider(account, claims)
                ProfileClaimsProvider(account, claims)
                OidcUserInfo(claims)
            }
            .orElseThrow()

}


typealias ClaimsProvider = (Account, MutableMap<String, Any>) -> MutableMap<String, Any>

object EmailClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, claims: MutableMap<String, Any>): MutableMap<String, Any> {
        claims["email"] = account.email
        claims["email_verified"] = true
        return claims
    }

}


object ProfileClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, claims: MutableMap<String, Any>): MutableMap<String, Any> {
        claims["name"] = "${account.firstName} ${account.lastName}"
        claims["given_name"] = account.firstName
        claims["family_name"] = account.lastName
        claims["middle_name"] = ""
        claims["updated_at"] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        account.phone.ifPresent {
            claims["phone_number"] = it.formattedPhone()
            claims["phone_number_verified"] = true
        }
        account.birthDate.ifPresent {
            claims["birthdate"] = it.iso8601FormattedDate()
        }
        return claims
    }

}

object OpenIdClaimsProvider : ClaimsProvider {
    override fun invoke(account: Account, claims: MutableMap<String, Any>): MutableMap<String, Any> {
        claims["sub"] = account.email
        return claims
    }

}


fun authorities(principal: OidcUserInfoAuthenticationContext) =
    principal.authorization.accessToken.claims!!["authorities"] as List<String>

fun userName(principal: OidcUserInfoAuthenticationContext) =
    principal.authorization.accessToken.claims!!["user_name"] as String
