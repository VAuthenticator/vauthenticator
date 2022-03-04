package it.valeriovaudi.vauthenticator.openid.connect.logout

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoDbClientApplicationRepository
import it.valeriovaudi.vauthenticator.support.TestingFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class JdbcFrontChannelLogoutTest {

    lateinit var fontEndChannelLogout: JdbcFrontChannelLogout

    @MockK
    lateinit var clientApplicationRepository: DynamoDbClientApplicationRepository

    @BeforeEach
    fun setUp() {
        fontEndChannelLogout =
            JdbcFrontChannelLogout("http://localhost/vauthenticator", clientApplicationRepository)
    }

    @Test
    fun `get all logout uri for a onlyone-portal federation`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        every { clientApplicationRepository.findOne(clientAppId) }
            .returns(
                Optional.of(ClientAppFixture.aClientApp(clientAppId))
            )
        val idToken = TestingFixture.simpleJwtFor("A_CLIENT_APP_ID")
        val federatedLogoutUrls = fontEndChannelLogout.getFederatedLogoutUrls(idToken)
        val expected = listOf(
            "http://localhost/vauthenticator/logout",
            "http://an_uri"
        )
        Assertions.assertEquals(federatedLogoutUrls, expected)
    }
}