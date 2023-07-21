package com.vauthenticator.server.oauth2.registeredclient

import com.vauthenticator.server.oauth2.clientapp.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient.withId
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration

class ClientAppRegisteredClientRepository(
    private val storeClientApplication: StoreClientApplication,
    private val clientApplicationRepository: ClientApplicationRepository
) :
    RegisteredClientRepository {

    private val logger: Logger = LoggerFactory.getLogger(ClientAppRegisteredClientRepository::class.java)

    override fun save(registeredClient: RegisteredClient) {
        storeClientApplication.store(
            ClientApplication(
                clientAppId = ClientAppId(registeredClient.clientId),
                authorities = Authorities.empty(),
                logoutUri = LogoutUri(""),
                postLogoutRedirectUri = PostLogoutRedirectUri(registeredClient.postLogoutRedirectUris.first()),
                scopes = Scopes(registeredClient.scopes.map { Scope(it) }.toSet()),
                accessTokenValidity = TokenTimeToLive(registeredClient.tokenSettings.accessTokenTimeToLive.toSeconds()),
                refreshTokenValidity = TokenTimeToLive(registeredClient.tokenSettings.refreshTokenTimeToLive.toSeconds()),
                additionalInformation = emptyMap(),
                authorizedGrantTypes = AuthorizedGrantTypes(registeredClient.authorizationGrantTypes.map {
                    AuthorizedGrantType.valueOf(
                        it.value.uppercase()
                    )
                }),
                secret = Secret(registeredClient.clientSecret!!),
                withPkce = WithPkce(registeredClient.clientSettings.isRequireProofKey),
                webServerRedirectUri = CallbackUri(registeredClient.redirectUris.first()),
                autoApprove = AutoApprove(registeredClient.clientSettings.isRequireAuthorizationConsent.not())
            ), true
        )
    }

    override fun findById(id: String): RegisteredClient =
        registeredClient(id)

    override fun findByClientId(clientId: String): RegisteredClient =
        registeredClient(clientId)


    private fun registeredClient(id: String) = clientApplicationRepository.findOne(ClientAppId(id))
        .map { clientApp ->
            val registeredClientAppDefinition = withId(id)
                .clientId(id)
                .clientSecret(clientApp.secret.content)
                .authorizationGrantTypes { authorizationGrantTypes ->
                    authorizationGrantTypes.addAll(clientApp.authorizedGrantTypes.content.map {
                        AuthorizationGrantType(
                            it.name.lowercase()
                        )
                    })
                }
                .clientAuthenticationMethods {
                    ClientAuthenticationMethod.NONE
                    ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                    ClientAuthenticationMethod.CLIENT_SECRET_POST
                }
                .scopes { scopes -> scopes.addAll(clientApp.scopes.content.map { it.content }) }
                .redirectUri(clientApp.webServerRedirectUri.content)
                .clientSettings(
                    ClientSettings.builder().requireProofKey(clientApp.withPkce.content).build()
                )
                .tokenSettings(
                    TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(clientApp.accessTokenValidity.content))
                        .refreshTokenTimeToLive(Duration.ofSeconds(clientApp.refreshTokenValidity.content))
                        .reuseRefreshTokens(true)
                        .build()
                )

            registeredClientAppDefinition.build()
        }.orElseThrow {
            logger.error("Application with id or client_id: $id not found")
            RegisteredClientAppNotFound("Application with id or client_id: $id not found")
        }


}