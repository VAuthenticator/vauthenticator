package it.valeriovaudi.vauthenticator.openid.connect.logout

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture
import it.valeriovaudi.vauthenticator.openid.connect.idtoken.IdToken
import it.valeriovaudi.vauthenticator.openid.connect.idtoken.TestableOAuth2Authentication
import it.valeriovaudi.vauthenticator.time.Clock
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class JdbcFrontChannelLogoutTest {
    companion object {
        @ClassRule
        @JvmField
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432)

    }

    val clock: Clock = Mockito.mock(Clock::class.java)

    lateinit var fontEndChannelLogout: JdbcFrontChannelLogout

    @Before
    fun setUp() {
        val serviceHost = container.getServiceHost("postgres_1", 5432)
        val servicePort = container.getServicePort("postgres_1", 5432)
        val dataSource = DataSourceBuilder.create()
                .url("jdbc:postgresql://$serviceHost:$servicePort/vauthenticator?user=root&password=root")
                .build()
        fontEndChannelLogout = JdbcFrontChannelLogout("http://localhost/vauthenticator",JdbcTemplate(dataSource))
    }

    @Test
    fun `get all logout uri for a onlyone-portal federation`() {
        val content = KeyPairFixture.getFileContent("/keystore/keystore.jks")
        val keyPair = KeyPairFixture.keyPair(content = content)
        val clockTime: Long = 10000

        given(clock.nowInSeconds()).willReturn(clockTime)

        val idToken = IdToken.createIdToken(iss = "AN_ISS", sub = "A_SUB",
                authentication = TestableOAuth2Authentication(),
                clock = clock)

        val federatedLogoutUrls = fontEndChannelLogout.getFederatedLogoutUrls(idToken.idTokenAsJwtSignedFor(keyPair))
        val expected = listOf(
                "http://localhost/vauthenticator/logout",
                "http://an_uri",
                "http://an_uri",
                "http://an_uri"
        )
        assertThat(federatedLogoutUrls, equalTo(expected))
    }
}