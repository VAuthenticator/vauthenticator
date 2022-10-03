package it.valeriovaudi.vauthenticator.security.registeredclient

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration

class ClientAppRegisteredClientRepository(private val clientApplicationRepository: ClientApplicationRepository) :
        RegisteredClientRepository {

    val logger: Logger = LoggerFactory.getLogger(ClientAppRegisteredClientRepository::class.java)

    override fun save(registeredClient: RegisteredClient) {
        throw UnsupportedActionException("store client application from ClientAppRegisteredClientRepository is not currently allowed");
    }

    override fun findById(id: String): RegisteredClient =
            registeredClient(id)

    override fun findByClientId(clientId: String): RegisteredClient =
            registeredClient(clientId)


    private fun registeredClient(id: String) = clientApplicationRepository.findOne(ClientAppId(id))
            .map { clientApp ->
                RegisteredClient.withId(id)
                        .clientId(id)
                        .clientSecret(clientApp.secret.content)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantTypes { authorizationGrantTypes ->
                            authorizationGrantTypes.addAll(clientApp.authorizedGrantTypes.content.map {
                                AuthorizationGrantType(
                                        it.name.lowercase()
                                )
                            })
                        }
                        .scopes { scopes -> scopes.addAll(clientApp.scopes.content.map { it.content }) }
                        .redirectUri(clientApp.webServerRedirectUri.content)
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofSeconds(clientApp.accessTokenValidity.content.toLong()))
                                .refreshTokenTimeToLive(Duration.ofSeconds(clientApp.refreshTokenValidity.content.toLong()))
                                .reuseRefreshTokens(true)
                                .build())
                        .build()
            }.orElseThrow {
                logger.error("Application with id or client_id: $id not found")
                RegisteredClientAppNotFound("Application with id or client_id: $id not found")
            }


}