package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ReadClientApplicationTest {

    @Mock
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @Test
    fun `find one happy path`() {
        val clientAppId = ClientAppId("AN_ID")
        val readClientApplication = ReadClientApplication(clientApplicationRepository)

        given(clientApplicationRepository.findOne(clientAppId))
                .willReturn(Optional.of(aClientApp(clientAppId)))

        val actual: Optional<ClientApplication> = readClientApplication.findOne(clientAppId)
        assertThat(actual, equalTo(Optional.of(aClientApp(clientAppId, Secret("*******")))))
    }

    @Test
    fun `find all happy path`() {
        val clientAppId = ClientAppId("AN_ID")
        val readClientApplication = ReadClientApplication(clientApplicationRepository)

        given(clientApplicationRepository.findAll())
                .willReturn(listOf(aClientApp(clientAppId)))

        val actual: List<ClientApplication> = readClientApplication.findAll()
        assertThat(actual, equalTo(listOf(aClientApp(clientAppId, Secret("*******")))))
    }
}