package it.valeriovaudi.vauthenticator.openid.connect.logout

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dataSource
import it.valeriovaudi.vauthenticator.support.TestingFixture.initClientApplicationTests
import it.valeriovaudi.vauthenticator.support.TestingFixture.resetDatabase
import it.valeriovaudi.vauthenticator.time.Clock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.jdbc.core.JdbcTemplate

// fixme
class JdbcFrontChannelLogoutTest {

    val clock: Clock = Mockito.mock(Clock::class.java)

    lateinit var fontEndChannelLogout: JdbcFrontChannelLogout

//    @BeforeEach
    fun setUp() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        resetDatabase(jdbcTemplate)
        initClientApplicationTests(jdbcTemplate)
        fontEndChannelLogout = JdbcFrontChannelLogout("http://localhost/vauthenticator", jdbcTemplate)
    }

//    @Test
    fun `get all logout uri for a onlyone-portal federation`() {
        val content = KeyPairFixture.getFileContent("/keystore/keystore.jks")
        val keyPair = KeyPairFixture.keyPair(content = content)
        val clockTime: Long = 10000

        given(clock.nowInSeconds()).willReturn(clockTime)

        val idToken = ""
        /*= IdToken.createIdToken(iss = "AN_ISS", sub = "A_SUB",
                authentication = TestableOAuth2Authentication(),
                clock = clock)
*/
        val federatedLogoutUrls = "" //fontEndChannelLogout.getFederatedLogoutUrls(idToken.idTokenAsJwtSignedFor(keyPair))
        val expected = listOf(
                "http://localhost/vauthenticator/logout",
                "http://an_uri123",
                "http://an_uri",
                "http://an_uri"
        )
        Assertions.assertEquals(federatedLogoutUrls, expected)
    }
}