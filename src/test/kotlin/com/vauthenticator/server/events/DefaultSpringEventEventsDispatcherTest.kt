package com.vauthenticator.server.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.login.LoginPageController
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.role.RoleEndPoint
import com.vauthenticator.server.role.RoleRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class DefaultSpringEventEventsDispatcherTest {
    lateinit var mokMvc: MockMvc
/*
    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @BeforeEach
    internal fun setUp() {
        mokMvc =
            MockMvcBuilders.standaloneSetup(
                LoginPageController(clientApplicationRepository, ObjectMapper())
            ).build()
    }

    @MockK
    private lateinit var publisher: ApplicationEventPublisher

    @Test
    fun `happy path`() {
        val underTest = DefaultSpringEventEventsDispatcher(publisher)
        underTest.handle(EventFixture.defaultSpringEvent.source)
    }*/
}