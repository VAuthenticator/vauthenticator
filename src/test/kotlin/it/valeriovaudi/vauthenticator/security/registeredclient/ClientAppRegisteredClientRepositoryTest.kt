package it.valeriovaudi.vauthenticator.security.registeredclient

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ClientAppRegisteredClientRepositoryTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @Test
    internal fun `when find a client app by id`() {
        val clientAppRegisteredClientRepository = ClientAppRegisteredClientRepository(clientApplicationRepository)

        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(aClientApplication())

        val actual = clientAppRegisteredClientRepository.findById("A_CLIENT_APP_ID")

        Assertions.assertEquals(aRegisteredClient(), actual)
    }

    @Test
    internal fun `when find a client app by client id`() {
        val clientAppRegisteredClientRepository = ClientAppRegisteredClientRepository(clientApplicationRepository)

        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(aClientApplication())

        val actual = clientAppRegisteredClientRepository.findByClientId("A_CLIENT_APP_ID")

        Assertions.assertEquals(aRegisteredClient(), actual)
    }

    @Test
    internal fun `when application is not foutn by id or clientId`() {
        val clientAppRegisteredClientRepository = ClientAppRegisteredClientRepository(clientApplicationRepository)

        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(Optional.empty())

        assertThrows(RegisteredClientAppNotFound::class.java,
            { clientAppRegisteredClientRepository.findByClientId("A_CLIENT_APP_ID") },
            "Application with id or client_id: A_CLIENT_APP_ID not found")

        assertThrows(RegisteredClientAppNotFound::class.java,
            { clientAppRegisteredClientRepository.findById("A_CLIENT_APP_ID") },
            "Application with id or client_id: A_CLIENT_APP_ID not found")
    }

    private fun aRegisteredClient() = RegisteredClient.withId("A_CLIENT_APP_ID")
        .clientId("A_CLIENT_APP_ID")
        .clientSecret("A_SECRET")
        .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .scope("A_SCOPE")
        .scope("ANOTHER_SCOPE")
        .redirectUri("http://a_call_back")
        .tokenSettings { tokenSettings: TokenSettings ->
            tokenSettings.accessTokenTimeToLive(Duration.ofSeconds(100))
            tokenSettings.refreshTokenTimeToLive(Duration.ofSeconds(200))
            tokenSettings.reuseRefreshTokens(false)
        }
        .build()

    private fun aClientApplication() = Optional.ofNullable(
        ClientApplication(
            clientAppId = ClientAppId("A_CLIENT_APP_ID"),
            secret = Secret("A_SECRET"),
            scopes = Scopes(listOf(Scope("A_SCOPE"), Scope("ANOTHER_SCOPE"))),
            authorizedGrantTypes = AuthorizedGrantTypes(
                listOf(
                    AuthorizedGrantType.AUTHORIZATION_CODE,
                    AuthorizedGrantType.REFRESH_TOKEN
                )
            ),
            webServerRedirectUri = CallbackUri("http://a_call_back"),
            authorities = Authorities(listOf(Authority("AN_AUTHORITY"), Authority("ANOTHER_AUTHORITY"))),
            accessTokenValidity = TokenTimeToLive(100),
            refreshTokenValidity = TokenTimeToLive(200),
            additionalInformation = emptyMap(),
            autoApprove = AutoApprove.approve,
            postLogoutRedirectUri = PostLogoutRedirectUri("http://post_logout_redirect_uri"),
            logoutUri = LogoutUri("http://logout_uri"),
            federation = Federation("federation"),
            resourceIds = ResourceIds(listOf(ResourceId("resource_id")))
        )
    )
}