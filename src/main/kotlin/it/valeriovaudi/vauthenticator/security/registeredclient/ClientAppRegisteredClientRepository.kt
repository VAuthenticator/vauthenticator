package it.valeriovaudi.vauthenticator.security.registeredclient

import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration

class ClientAppRegisteredClientRepository(private val clientApplicationRepository: ClientApplicationRepository) :
    RegisteredClientRepository {

    val logger = LoggerFactory.getLogger(ClientAppRegisteredClientRepository::class.java)

    override fun save(registeredClient: RegisteredClient) {
        clientApplicationRepository.save(
                ClientApplication(
                        clientAppId = ClientAppId(registeredClient.clientId),
                        secret = Secret(registeredClient.clientSecret),
                        accessTokenValidity = TokenTimeToLive(registeredClient.tokenSettings.accessTokenTimeToLive().toSeconds().toInt()),
                        refreshTokenValidity = TokenTimeToLive(registeredClient.tokenSettings.refreshTokenTimeToLive().toSeconds().toInt()),
                        additionalInformation = emptyMap(),
                        authorities = Authorities.empty(),

                )
        );
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
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantTypes { authorizationGrantTypes ->
                    authorizationGrantTypes.addAll(clientApp.authorizedGrantTypes.content.map {
                        AuthorizationGrantType(
                            it.name.toLowerCase()
                        )
                    })
                }
                .scopes { scopes -> scopes.addAll(clientApp.scopes.content.map { it.content }) }
                .redirectUri(clientApp.webServerRedirectUri.content)
                .tokenSettings { tokenSettings: TokenSettings ->
                    tokenSettings.accessTokenTimeToLive(Duration.ofSeconds(clientApp.accessTokenValidity.content.toLong()))
                    tokenSettings.refreshTokenTimeToLive(Duration.ofSeconds(clientApp.refreshTokenValidity.content.toLong()))
                    tokenSettings.reuseRefreshTokens(true)
                }
                .build()
        }.orElseThrow {
            logger.error("Application with id or client_id: $id not found")
            RegisteredClientAppNotFound("Application with id or client_id: $id not found")
        }


}