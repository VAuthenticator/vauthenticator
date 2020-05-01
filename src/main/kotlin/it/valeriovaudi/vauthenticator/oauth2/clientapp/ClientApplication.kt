package it.valeriovaudi.vauthenticator.oauth2.clientapp

import java.util.*

data class ClientApplication(val clientAppId: ClientAppId,
                             val secret: Secret,
                             val scopes: Scopes,
                             val authorizedGrantTypes: AuthorizedGrantTypes,
                             val webServerRedirectUri: CallbackUri,
                             val authorities: Authorities,
                             val accessTokenValidity: TokenTimeToLive,
                             val refreshTokenValidity: TokenTimeToLive,
                             val additionalInformation: Map<String, Objects>,
                             val autoApprove: AutoApprove,
                             val postLogoutRedirectUri: PostLogoutRedirectUri,
                             val logoutUri: LogoutUri,
                             val federation: Federation,
                             val resourceIds: ResourceIds
)

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

sealed class Secret {
    fun content(): String = when (this) {
        is EmptySecret -> "*********"
        is FilledSecret -> this.content
    }
}

data class FilledSecret(val content: String) : Secret()
object EmptySecret : Secret()

data class ResourceId(val content: String)
data class ClientAppId(val content: String)
data class CallbackUri(val content: String)
data class PostLogoutRedirectUri(val content: String)
data class LogoutUri(val content: String)

data class Scopes(val content: List<Scope>) {
    companion object {
        fun from(vararg scope: Scope) = Scopes(listOf(*scope))
    }
}

data class Scope(val content: String) {
    companion object {
        val OPEN_ID = Scope("openid")
        val PROFILE = Scope("profile")
        val EMAIL = Scope("email")
    }
}

data class Authorities(val content: List<Authority>)
data class Authority(val content: String)
data class TokenTimeToLive(val content: Int)
data class Federation(val name: String)