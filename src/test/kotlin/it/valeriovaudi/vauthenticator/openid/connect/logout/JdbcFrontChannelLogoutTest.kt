package it.valeriovaudi.vauthenticator.openid.connect.logout

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture
import it.valeriovaudi.vauthenticator.openid.connect.idtoken.IdToken
import it.valeriovaudi.vauthenticator.openid.connect.idtoken.TestableOAuth2Authentication
import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.time.Clock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate

class JdbcFrontChannelLogoutTest {

    val clock: Clock = Mockito.mock(Clock::class.java)

    lateinit var fontEndChannelLogout: JdbcFrontChannelLogout

    @BeforeEach
    fun setUp() {
        val serviceHost = TestingFixture.postGresHost
        val servicePort = TestingFixture.postGresPort
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
                "http://an_uri123",
                "http://an_uri",
                "http://an_uri"
        )
        Assertions.assertEquals(federatedLogoutUrls, expected)
    }
}