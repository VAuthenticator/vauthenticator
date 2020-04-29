package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
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
                aClientApp(ClientAppId("federated_client_id1"), Federation("ANOTHER_FEDERATION")),
                aClientApp(ClientAppId("federated_client_id2"), Federation("ANOTHER_FEDERATION"))
        )
        assertThat(actual, equalTo(clientApplications))
    }

    fun aClientApp(clientAppId: ClientAppId, federation: Federation = Federation("A_FEDERATION")) = ClientApplication(
            clientAppId,
            Secret("secret"),
            Scopes.from(Scope.OPEN_ID, Scope.PROFILE, Scope.EMAIL),
            AuthorizedGrantTypes.from(AuthorizedGrantType.PASSWORD),
            CallbackUri("http://an_uri"),
            Authorities(listOf(Authority("AN_AUTHORITY"))),
            TokenTimeToLive(10),
            TokenTimeToLive(10),
            emptyMap(),
            AutoApprove.approve,
            PostLogoutRedirectUri("http://an_uri"),
            LogoutUri("http://an_uri"),
            federation,
            ResourceIds.from(ResourceId("oauth2-resource"))
    )
}