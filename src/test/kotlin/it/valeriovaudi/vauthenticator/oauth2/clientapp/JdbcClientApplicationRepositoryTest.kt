package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.support.TestingFixture.dataSource
import it.valeriovaudi.vauthenticator.support.TestingFixture.initClientApplicationTests
import it.valeriovaudi.vauthenticator.support.TestingFixture.resetDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*

class JdbcClientApplicationRepositoryTest {

    lateinit var clientApplicationRepository: JdbcClientApplicationRepository

    @BeforeEach
    fun setUp() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        clientApplicationRepository = JdbcClientApplicationRepository(jdbcTemplate)
        resetDatabase(jdbcTemplate)
        initClientApplicationTests(jdbcTemplate)
    }

    @Test
    fun `find a client application`() {
        val clientAppId = ClientAppId("client_id")
        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(clientAppId)
        Assertions.assertEquals(actual, Optional.of(aClientApp(clientAppId)))
    }

    @Test
    fun `when try to find a client application that does not exist`() {
        val clientAppId = ClientAppId("a not exist client_id")
        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(clientAppId)
        val expected: Optional<ClientApplication> = Optional.empty()
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when try to find a client application in a federation`() {
        val actual: Iterable<ClientApplication> = clientApplicationRepository.findByFederation(Federation("ANOTHER_FEDERATION"))
        val expected: Iterable<ClientApplication> = listOf(
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `find all client applications in VAuthenticator`() {
        val actual: Iterable<ClientApplication> = clientApplicationRepository.findAll()
        val expected: Iterable<ClientApplication> = listOf(
                aClientApp(ClientAppId("client_id")),
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `save a new applications in VAuthenticator`() {
        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret")))

        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(ClientAppId("a new client"))
        val expected: Optional<ClientApplication> = Optional.of(aClientApp(ClientAppId("a new client")))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when try to save more that onece a new applications in VAuthenticator`() {
        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret")))
        var actual: Optional<ClientApplication> = clientApplicationRepository.findOne(ClientAppId("a new client"))
        var expected: Optional<ClientApplication> = Optional.of(aClientApp(ClientAppId("a new client")))
        Assertions.assertEquals(expected, actual)

        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret"), federation = Federation("a new federation")))
        actual = clientApplicationRepository.findOne(ClientAppId("a new client"))
        expected = Optional.of(aClientApp(clientAppId = ClientAppId("a new client"), federation = Federation("a new federation")))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `delete an application in VAuthenticator`() {
        clientApplicationRepository.delete(ClientAppId("a new client"))
        clientApplicationRepository.delete(ClientAppId("client_id"))

        val actual: Iterable<ClientApplication> = clientApplicationRepository.findAll()
        val expected: Iterable<ClientApplication> = listOf(
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        Assertions.assertEquals(expected, actual)
    }

}