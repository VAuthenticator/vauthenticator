package com.vauthenticator.server.events

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SpringEventsCollectorTest {

  /*  @MockK
    private lateinit var eventConsumer: EventConsumer

    @MockK
    private lateinit var anotherEventConsumer: EventConsumer

    @Test
    fun `when an event is received and processed from a list of event consumers`() {
        val underTest = SpringEventsCollector(listOf(eventConsumer, anotherEventConsumer))

        every { eventConsumer.accept(defaultSpringEvent) } just runs
        every { anotherEventConsumer.accept(defaultSpringEvent) } just runs

        underTest.accept(defaultSpringEvent)

        verify { eventConsumer.accept(defaultSpringEvent) }
        verify { anotherEventConsumer.accept(defaultSpringEvent) }
    }*/
}