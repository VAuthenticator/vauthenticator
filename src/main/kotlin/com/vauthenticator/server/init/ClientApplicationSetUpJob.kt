package com.vauthenticator.server.init

import com.vauthenticator.server.oauth2.clientapp.adapter.cache.CachedClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.domain.*
import com.vauthenticator.server.oauth2.clientapp.domain.AuthorizedGrantType.*
import com.vauthenticator.server.oauth2.clientapp.domain.Scope.Companion.AVAILABLE_SCOPES
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service

@Service
class ClientApplicationSetUpJob(
    private val clientApplicationRepository: CachedClientApplicationRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        clientApplicationRepository.save(m2mDefaultAdminClientApp())
        clientApplicationRepository.save(managementUIDefaultClientApp())
    }

    private fun managementUIDefaultClientApp() = ClientApplication(
        clientAppId = ClientAppId("vauthenticator-management-ui"),
        secret = Secret("secret"),
        scopes = Scopes.from(*(AVAILABLE_SCOPES - Scope.MFA_ALWAYS).toTypedArray()),
        withPkce = WithPkce.disabled,
        authorizedGrantTypes = AuthorizedGrantTypes.from(AUTHORIZATION_CODE, REFRESH_TOKEN),
        webServerRedirectUri = CallbackUri("http://local.management.vauthenticator.com:8080/login/oauth2/code/client"),
        accessTokenValidity = TokenTimeToLive(3600),
        refreshTokenValidity = TokenTimeToLive(3600),
        additionalInformation = emptyMap(),
        autoApprove = AutoApprove.approve,
        postLogoutRedirectUri = PostLogoutRedirectUri("http://local.management.vauthenticator.com:8080/secure/admin/index"),
        logoutUri = LogoutUri("http://local.management.vauthenticator.com:8080/logout"),
    )

    private fun m2mDefaultAdminClientApp() = ClientApplication(
        clientAppId = ClientAppId("admin"),
        secret = Secret("secret"),
        scopes = Scopes.from(*(AVAILABLE_SCOPES - Scope.MFA_ALWAYS).toTypedArray()),
        withPkce = WithPkce.disabled,
        authorizedGrantTypes = AuthorizedGrantTypes.from(CLIENT_CREDENTIALS),
        webServerRedirectUri = CallbackUri(""),
        accessTokenValidity = TokenTimeToLive(3600),
        refreshTokenValidity = TokenTimeToLive(3600),
        additionalInformation = emptyMap(),
        autoApprove = AutoApprove.approve,
        postLogoutRedirectUri = PostLogoutRedirectUri(""),
        logoutUri = LogoutUri("")
    )
}