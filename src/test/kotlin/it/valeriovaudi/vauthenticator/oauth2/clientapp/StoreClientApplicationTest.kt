package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoreClientApplicationTest {

    @Mock
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @Mock
    lateinit var passwordEncoder: VAuthenticatorPasswordEncoder

    @Test
    fun `store a client application with password`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)

        storeClientApplication.store()
    }
}