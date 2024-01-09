package com.vauthenticator.server.security.registeredclient

import com.vauthenticator.server.oauth2.clientapp.*
import com.vauthenticator.server.oauth2.registeredclient.ClientAppRegisteredClientRepository
import com.vauthenticator.server.oauth2.registeredclient.RegisteredClientAppNotFound
import com.vauthenticator.server.security.registeredclient.RegisteredClientRepositoryFixture.aClientApplication
import com.vauthenticator.server.security.registeredclient.RegisteredClientRepositoryFixture.aRegisteredClient
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ClientAppRegisteredClientRepositoryTest {

    @MockK
    lateinit var storeClientApplication: StoreClientApplication

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    lateinit var clientAppRegisteredClientRepository: ClientAppRegisteredClientRepository

    @BeforeEach
    fun setup() {
        clientAppRegisteredClientRepository =
            ClientAppRegisteredClientRepository(storeClientApplication, clientApplicationRepository)
    }

    @Test
    internal fun `when find a client app by id`() {
        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(aClientApplication())

        val actual = clientAppRegisteredClientRepository.findById("A_CLIENT_APP_ID")

        Assertions.assertEquals(aRegisteredClient(), actual)
    }

    @Test
    internal fun `when find a client app by client id`() {
        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(aClientApplication())

        val actual = clientAppRegisteredClientRepository.findByClientId("A_CLIENT_APP_ID")

        Assertions.assertEquals(aRegisteredClient(), actual)
    }

    @Test
    internal fun `when application is not foutn by id or clientId`() {
        every { clientApplicationRepository.findOne(ClientAppId("A_CLIENT_APP_ID")) }
            .returns(Optional.empty())

        assertThrows(
            RegisteredClientAppNotFound::class.java,
            { clientAppRegisteredClientRepository.findByClientId("A_CLIENT_APP_ID") },
            "Application with id or client_id: A_CLIENT_APP_ID not found"
        )

        assertThrows(
            RegisteredClientAppNotFound::class.java,
            { clientAppRegisteredClientRepository.findById("A_CLIENT_APP_ID") },
            "Application with id or client_id: A_CLIENT_APP_ID not found"
        )
    }

    @Test
    internal fun `when a new client app is registered`() {
        every {
            storeClientApplication.store(
                aClientApplication().get()
                    .copy(
                        postLogoutRedirectUri = PostLogoutRedirectUri("http://post_logout_redirect_uri"),
                        logoutUri = LogoutUri(""),
                        authorities = Authorities.empty(),
                        scopes = Scopes(setOf(Scope("A_SCOPE"), Scope("ANOTHER_SCOPE")))
                    ), true
            )
        } just runs

        clientAppRegisteredClientRepository.save(
            RegisteredClient.from(aRegisteredClient()).build()
        )

    }
}