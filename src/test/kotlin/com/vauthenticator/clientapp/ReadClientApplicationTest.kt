package com.vauthenticator.clientapp

import com.vauthenticator.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.oauth2.clientapp.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
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
        Assertions.assertEquals(actual,Optional.of(aClientApp(clientAppId, Secret("*******"))))
    }

    @Test
    fun `find all happy path`() {
        val clientAppId = ClientAppId("AN_ID")
        val readClientApplication = ReadClientApplication(clientApplicationRepository)

        given(clientApplicationRepository.findAll())
                .willReturn(listOf(aClientApp(clientAppId)))

        val actual: List<ClientApplication> = readClientApplication.findAll()
        Assertions.assertEquals(actual, listOf(aClientApp(clientAppId, Secret("*******"))))
    }
}