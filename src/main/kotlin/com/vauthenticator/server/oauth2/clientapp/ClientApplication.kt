package com.vauthenticator.server.oauth2.clientapp

data class ClientApplication(
    val clientAppId: ClientAppId,
    val secret: Secret,
    val scopes: Scopes,
    val withPkce: WithPkce = WithPkce.disabled,
    val authorizedGrantTypes: AuthorizedGrantTypes,
    val webServerRedirectUri: CallbackUri,
    val authorities: Authorities,
    val accessTokenValidity: TokenTimeToLive,
    val refreshTokenValidity: TokenTimeToLive,
    val additionalInformation: Map<String, Any> = emptyMap(),
    val autoApprove: AutoApprove = AutoApprove.approve,
    val postLogoutRedirectUri: PostLogoutRedirectUri,
    val logoutUri: LogoutUri,
)

@JvmInline
value class WithPkce(val content: Boolean) {
    companion object {
        val enabled = WithPkce(true)
        val disabled = WithPkce(false)
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

data class Secret(val content: String)

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

        val MAIL_TEMPLATE_READER = Scope("admin:mail-template-reader")
        val MAIL_TEMPLATE_WRITER = Scope("admin:mail-template-writer")

        val KEY_READER = Scope("admin:key-reader")
        val KEY_EDITOR = Scope("admin:key-editor")

        val MFA_ALWAYS = Scope("mfa:always")

        val AVAILABLE_SCOPES = listOf(
            Scope("openid"),
            Scope("profile"),
            Scope("email"),

            Scope("admin:signup"),
            Scope("admin:welcome"),
            Scope("admin:mail-verify"),
            Scope("admin:reset-password"),

            Scope("admin:key-reader"),
            Scope("admin:key-editor"),

            Scope("admin:mail-template-reader"),
            Scope("admin:mail-template-writer"),

            Scope("mfa:always")
        )

    }
}

data class Authorities(val content: Set<Authority>) {
    companion object {
        fun empty() = Authorities(emptySet())
    }
}

data class Authority(val content: String)
data class TokenTimeToLive(val content: Long)

enum class ClientApplicationFeatures(val value: String) {
    SIGNUP("signup"),
    RESET_PASSWORD("reset-password")
}