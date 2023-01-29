package com.vauthenticator.server.events

import com.vauthenticator.server.events.EventFixture.defaultSpringEvent
import com.vauthenticator.server.events.EventFixture.mfaEvent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@ExtendWith(OutputCaptureExtension::class)
class LoggerEventConsumerTest {

    @Test
    fun `when a non Default Spring event is fired line MFA event`(output: CapturedOutput) {
        val underTest = LoggerEventConsumer()

        underTest.accept(mfaEvent)
        val message = """
             The user : A_CLIENT_APP_ID 
             with the client id anemail@domain.com 
             has done a VAuthenticatorMFAEvent 
             event at ${mfaEvent.timeStamp.epochSecond}
            """.trimIndent()
        assertTrue(output.out.contains(message));
    }

    @Test
    fun `when a Default Spring event is fired`(output: CapturedOutput) {
        val underTest = LoggerEventConsumer()

        underTest.accept(defaultSpringEvent)
        val message = """
             The user : A_CLIENT_APP_ID 
             with the client id anemail@domain.com 
             has done a AuthenticationSuccessEvent 
             event at ${defaultSpringEvent.timeStamp.epochSecond}
            """.trimIndent()
        assertTrue(output.out.contains(message));
    }
}