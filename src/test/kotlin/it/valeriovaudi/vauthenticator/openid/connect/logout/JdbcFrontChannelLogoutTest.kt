package it.valeriovaudi.vauthenticator.openid.connect.logout

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dataSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.jdbc.core.JdbcTemplate

@ExtendWith(MockKExtension::class)
class JdbcFrontChannelLogoutTest {

    lateinit var fontEndChannelLogout: JdbcFrontChannelLogout

    @MockK
    lateinit var clientApplicationRepository: DynamoDbClientApplicationRepository

    @BeforeEach
    fun setUp() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        fontEndChannelLogout =
            JdbcFrontChannelLogout("http://localhost/vauthenticator", clientApplicationRepository)
    }

    @Test
    fun `get all logout uri for a onlyone-portal federation`() {
        val federation = Federation("a_federation")

        every { clientApplicationRepository.findLogoutUriByFederation(federation) }
            .returns(
                listOf(
                    LogoutUri("http://an_uri123"),
                    LogoutUri("http://an_uri"),
                    LogoutUri("http://an_uri"),
                )
            )
        val idToken = TestingFixture.idTokenFor("a_federation")
        val federatedLogoutUrls = fontEndChannelLogout.getFederatedLogoutUrls(idToken)
        val expected = listOf(
            "http://localhost/vauthenticator/logout",
            "http://an_uri123",
            "http://an_uri",
            "http://an_uri"
        )
        Assertions.assertEquals(federatedLogoutUrls, expected)
    }
}