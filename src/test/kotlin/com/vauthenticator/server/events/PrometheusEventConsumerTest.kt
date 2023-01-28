package com.vauthenticator.server.events

import io.micrometer.prometheus.PrometheusMeterRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PrometheusEventConsumerTest {
    @Test
    fun `happy path`() {
        val registry = PrometheusMeterRegistry { null }
        val underTest = PrometheusEventConsumer(registry)

        val event = EventFixture.userLoggedEvent
        underTest.accept(event)

        registry.prometheusRegistry.metricFamilySamples()
            .asIterator()
            .forEach {
                Assertions.assertEquals("UserLogged", it.name)
                Assertions.assertEquals(listOf("A_CLIENT_APP_ID", "${event.timeStamp.epochSecond}", "anemail@domain.com"), it.samples[0].labelValues.toList())
            }
    }

}