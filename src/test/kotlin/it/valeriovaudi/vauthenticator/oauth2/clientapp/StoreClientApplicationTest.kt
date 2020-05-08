package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class StoreClientApplicationTest {

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()

    @Mock
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @Mock
    lateinit var passwordEncoder: VAuthenticatorPasswordEncoder

    @Test
    fun `store a client application with password`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val aClientApp = ClientAppFixture.aClientApp(ClientAppId("AN_ID"))

        given(passwordEncoder.encode(aClientApp.secret.content))
                .willReturn(aClientApp.secret.content)

        storeClientApplication.store(aClientApp, true)
        verify(passwordEncoder).encode(aClientApp.secret.content)
        verify(clientApplicationRepository).save(aClientApp)
    }

    @Test
    fun `store a client application without password`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId)

        given(clientApplicationRepository.findOne(clientAppId))
                .willReturn(Optional.of(aClientApp))

        storeClientApplication.store(aClientApp, false)

        verify(passwordEncoder, times(0)).encode(aClientApp.secret.content)
        verify(clientApplicationRepository).findOne(clientAppId)
        verify(clientApplicationRepository).save(aClientApp)
    }

    @Test
    fun `reset password fot a client application`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId)
        val updatedClientApp = ClientAppFixture.aClientApp(clientAppId, password = Secret("A_NEW_PASSWORD"))

        given(clientApplicationRepository.findOne(clientAppId))
                .willReturn(Optional.of(aClientApp))

        given(passwordEncoder.encode("A_NEW_PASSWORD"))
                .willReturn("A_NEW_PASSWORD")

        storeClientApplication.resetPassword(clientAppId, Secret("A_NEW_PASSWORD"))

        verify(passwordEncoder).encode("A_NEW_PASSWORD")
        verify(clientApplicationRepository).findOne(clientAppId)
        verify(clientApplicationRepository).save(updatedClientApp)
    }

    @Test
    fun `reset password fot a not found client application`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")

        given(clientApplicationRepository.findOne(clientAppId))
                .willReturn(Optional.empty())

        expectedException.expect(ClientApplicationNotFound::class.java)

        storeClientApplication.resetPassword(clientAppId, Secret("A_NEW_PASSWORD"))
    }
}