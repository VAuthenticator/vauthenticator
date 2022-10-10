package it.valeriovaudi.vauthenticator.oauth2.clientapp

import com.nimbusds.jose.JWSObject
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import java.util.*

data class ClientApplication(
        val clientAppId: ClientAppId,
        val secret: Secret,
        val scopes: Scopes,
        val authorizedGrantTypes: AuthorizedGrantTypes,
        val webServerRedirectUri: CallbackUri,
        val authorities: Authorities,
        val accessTokenValidity: TokenTimeToLive,
        val refreshTokenValidity: TokenTimeToLive,
        val additionalInformation: Map<String, Objects> = emptyMap(),
        val autoApprove: AutoApprove = AutoApprove.approve,
        val postLogoutRedirectUri: PostLogoutRedirectUri,
        val logoutUri: LogoutUri,
        val resourceIds: ResourceIds
) {
    companion object {
        fun clientAppIdFrom(jwtToken: String) =
                ClientAppId(JWSObject.parse(jwtToken).payload.toJSONObject().get(IdTokenClaimNames.AZP) as String)

        fun userNameFrom(jwtToken: String): String =
                Optional.ofNullable(JWSObject.parse(jwtToken).payload.toJSONObject().get("user_name") as String?)
                        .orElse("")
    }
}

data class AutoApprove(val content: Boolean) {
    companion object {
        val approve = AutoApprove(true)
        val disapprove = AutoApprove(false)
    }
}

data class AuthorizedGrantTypes(val content: List<AuthorizedGrantType>) {
    companion object {
        fun from(vararg authorizedGrantType: AuthorizedGrantType) = AuthorizedGrantTypes(listOf(*authorizedGrantType))
    }
}

enum class AuthorizedGrantType { CLIENT_CREDENTIALS, PASSWORD, AUTHORIZATION_CODE, IMPLICIT, REFRESH_TOKEN }

data class ResourceIds(val content: List<ResourceId>) {
    companion object {
        fun from(vararg resourceId: ResourceId) = ResourceIds(listOf(*resourceId))
    }
}

data class Secret(val content: String)

data class ResourceId(val content: String)
data class ClientAppId(val content: String) {
    companion object {
        fun empty(): ClientAppId = ClientAppId("")
    }
}

data class CallbackUri(val content: String)
data class PostLogoutRedirectUri(val content: String)
data class LogoutUri(val content: String)

data class Scopes(val content: Set<Scope>) {
    companion object {
        fun from(vararg scope: Scope) = Scopes(setOf(*scope))
    }
}

data class Scope(val content: String) {
    companion object {
        val OPEN_ID = Scope("openid")
        val PROFILE = Scope("profile")
        val EMAIL = Scope("email")

        val SIGN_UP = Scope("admin:signup")
        val WELCOME = Scope("admin:welcome")
        val MAIL_VERIFY = Scope("admin:mail-verify")
        val RESET_PASSWORD = Scope("admin:reset-password")
    }
}

data class Authorities(val content: List<Authority>) {
    companion object {
        fun empty() = Authorities(emptyList())
    }
}

data class Authority(val content: String)
data class TokenTimeToLive(val content: Int)

enum class ClientApplicationFeatures(val value: String) {
    SIGNUP("signup"),
    RESET_PASSWORD("reset-password")
}