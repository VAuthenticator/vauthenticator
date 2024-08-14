package com.vauthenticator.server.oauth2.clientapp.domain

import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.support.ClientAppFixture
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class StoreClientApplicationTest {

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockK
    lateinit var passwordEncoder: VAuthenticatorPasswordEncoder

    @Test
    fun `store a client application with password`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val aClientApp = ClientAppFixture.aClientApp(ClientAppId("AN_ID"))

        every { passwordEncoder.encode(aClientApp.secret.content) } returns aClientApp.secret.content
        every { clientApplicationRepository.save(aClientApp) } just runs

        storeClientApplication.store(aClientApp, true)
        verify { passwordEncoder.encode(aClientApp.secret.content) }
        verify { clientApplicationRepository.save(aClientApp) }
    }

    @Test
    fun `store a client application without password`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId)

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp)
        every { clientApplicationRepository.save(aClientApp) } just runs

        storeClientApplication.store(aClientApp, false)

        verify(exactly = 0) { passwordEncoder.encode(aClientApp.secret.content) }
        verify { clientApplicationRepository.findOne(clientAppId) }
        verify { clientApplicationRepository.save(aClientApp) }
    }

    @Test
    fun `reset password fot a client application`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId)
        val updatedClientApp = ClientAppFixture.aClientApp(clientAppId, password = Secret("A_NEW_PASSWORD"))

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(aClientApp)
        every { passwordEncoder.encode("A_NEW_PASSWORD") } returns "A_NEW_PASSWORD"
        every { clientApplicationRepository.save(updatedClientApp) } just runs

        storeClientApplication.resetPassword(clientAppId, Secret("A_NEW_PASSWORD"))

        verify { passwordEncoder.encode("A_NEW_PASSWORD") }
        verify { clientApplicationRepository.findOne(clientAppId) }
        verify { clientApplicationRepository.save(updatedClientApp) }
    }

    @Test
    fun `reset password fot a not found client application`() {
        val storeClientApplication = StoreClientApplication(clientApplicationRepository, passwordEncoder)
        val clientAppId = ClientAppId("AN_ID")

        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.empty()

        assertThrows(ClientApplicationNotFound::class.java) {
            storeClientApplication.resetPassword(clientAppId, Secret("A_NEW_PASSWORD"))
        }
    }
}