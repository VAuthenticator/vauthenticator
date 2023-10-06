package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.events.EventFixture.signUpEvent
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock

@ExtendWith(MockKExtension::class)
class SendWelcomeMailUponSignUpEventConsumerTest {

    @Mock
    lateinit var sayWelcome: SayWelcome

    @Test
    fun `when a welcome mail is sent`() {
        val uut = SendWelcomeMailUponSignUpEventConsumer(sayWelcome)

        every { sayWelcome.welcome("AN_EMAIL") } just runs

        uut.accept(signUpEvent)
        assertEquals(true, uut.handleable(signUpEvent))

        verify { sayWelcome.welcome("AN_EMAIL") }
    }
}