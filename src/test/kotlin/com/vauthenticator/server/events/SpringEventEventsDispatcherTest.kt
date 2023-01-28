package com.vauthenticator.server.events

import com.vauthenticator.server.events.EventFixture.userLoggedEvent
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class SpringEventEventsDispatcherTest {

    @MockK
    private lateinit var publisher: ApplicationEventPublisher

    @Test
    fun `when an event is published`() {
        val underTest = SpringEventEventsDispatcher(publisher)

        every { publisher.publishEvent(userLoggedEvent) } just runs

        underTest.dispatch(userLoggedEvent)

        verify { publisher.publishEvent(userLoggedEvent) }
    }
}