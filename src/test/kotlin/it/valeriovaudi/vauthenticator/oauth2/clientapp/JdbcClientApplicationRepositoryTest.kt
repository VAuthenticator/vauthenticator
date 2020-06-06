package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.*

class JdbcClientApplicationRepositoryTest {

    companion object {
        @ClassRule
        @JvmField
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432)

    }

    lateinit var clientApplicationRepository: JdbcClientApplicationRepository

    @Before
    fun setUp() {
        val serviceHost = container.getServiceHost("postgres_1", 5432)
        val servicePort = container.getServicePort("postgres_1", 5432)
        val dataSource = DataSourceBuilder.create()
                .url("jdbc:postgresql://$serviceHost:$servicePort/vauthenticator?user=root&password=root")
                .build()
        clientApplicationRepository = JdbcClientApplicationRepository(JdbcTemplate(dataSource))
    }


    @Test
    fun `find a client application`() {
        val clientAppId = ClientAppId("client_id")
        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(clientAppId)
        assertThat(actual, equalTo(Optional.of(aClientApp(clientAppId))))
    }

    @Test
    fun `when try to find a client application that does not exist`() {
        val clientAppId = ClientAppId("a not exist client_id")
        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(clientAppId)
        assertThat(actual, equalTo(Optional.empty()))
    }

    @Test
    fun `when try to find a client application in a federation`() {
        val actual: Iterable<ClientApplication> = clientApplicationRepository.findByFederation(Federation("ANOTHER_FEDERATION"))
        val clientApplications: Iterable<ClientApplication> = listOf(
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        assertThat(actual, equalTo(clientApplications))
    }

    @Test
    fun `find all client applications in VAuthenticator`() {
        val actual: Iterable<ClientApplication> = clientApplicationRepository.findAll()
        val clientApplications: Iterable<ClientApplication> = listOf(
                aClientApp(ClientAppId("client_id")),
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        assertThat(actual, equalTo(clientApplications))
    }

    @Test
    fun `save a new applications in VAuthenticator`() {
        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret")))

        val actual: Optional<ClientApplication> = clientApplicationRepository.findOne(ClientAppId("a new client"))
        val clientApplications: Optional<ClientApplication> = Optional.of(aClientApp(ClientAppId("a new client")))
        assertThat(actual, equalTo(clientApplications))
    }

    @Test
    fun `when try to save more that onece a new applications in VAuthenticator`() {
        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret")))
        var actual: Optional<ClientApplication> = clientApplicationRepository.findOne(ClientAppId("a new client"))
        var clientApplications: Optional<ClientApplication> = Optional.of(aClientApp(ClientAppId("a new client")))
        assertThat(actual, equalTo(clientApplications))

        clientApplicationRepository.save(aClientApp(clientAppId = ClientAppId("a new client"), password = Secret("secret"), federation = Federation("a new federation")))
        actual = clientApplicationRepository.findOne(ClientAppId("a new client"))
        clientApplications = Optional.of(aClientApp(clientAppId = ClientAppId("a new client"), federation = Federation("a new federation")))
        assertThat(actual, equalTo(clientApplications))
    }

    @Test
    fun `delete an application in VAuthenticator`() {
        clientApplicationRepository.delete(ClientAppId("a new client"))
        clientApplicationRepository.delete(ClientAppId("client_id"))

        val actual: Iterable<ClientApplication> = clientApplicationRepository.findAll()
        val clientApplications: Iterable<ClientApplication> = listOf(
                aClientApp(clientAppId = ClientAppId("federated_client_id1"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("federated_client_id2"), federation = Federation("ANOTHER_FEDERATION")),
                aClientApp(clientAppId = ClientAppId("A_CLIENT_APPLICATION_ID"), federation = Federation("ANOTHER_FEDERATION"), logoutUri = LogoutUri("http://an_uri123"))
        )
        assertThat(actual, equalTo(clientApplications))
    }

}