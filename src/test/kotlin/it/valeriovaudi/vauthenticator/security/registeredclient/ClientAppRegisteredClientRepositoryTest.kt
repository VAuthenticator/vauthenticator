package it.valeriovaudi.vauthenticator.security.registeredclient

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.security.registeredclient.RegisteredClientRepositoryFixture.aClientApplication
import it.valeriovaudi.vauthenticator.security.registeredclient.RegisteredClientRepositoryFixture.aRegisteredClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ClientAppRegisteredClientRepositoryTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    lateinit var clientAppRegisteredClientRepository: ClientAppRegisteredClientRepository

    @BeforeEach
    fun setup() {
        clientAppRegisteredClientRepository =
            ClientAppRegisteredClientRepository(clientApplicationRepository)
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
}