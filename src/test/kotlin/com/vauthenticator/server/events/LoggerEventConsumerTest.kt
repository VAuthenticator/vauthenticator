package com.vauthenticator.server.events

import com.vauthenticator.server.events.EventFixture.userLoggedEvent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@ExtendWith(OutputCaptureExtension::class)
class LoggerEventConsumerTest {

    @Test
    fun `happy path`(output: CapturedOutput) {
        val underTest = LoggerEventConsumer()

        underTest.accept(userLoggedEvent)
        val message =
            "The user : A_CLIENT_APP_ID with the client id anemail@domain.com has done a UserLogged event at ${userLoggedEvent.timeStamp.epochSecond}"
        assertTrue(output.out.contains(message));
    }
}