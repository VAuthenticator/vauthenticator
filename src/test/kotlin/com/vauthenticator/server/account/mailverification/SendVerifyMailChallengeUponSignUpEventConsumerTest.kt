package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.clientapp.ClientAppFixture
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.password.Password
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(MockKExtension::class)
class SendVerifyMailChallengeUponSignUpEventConsumerTest {

    @MockK
    lateinit var mailChallenge: SendVerifyMailChallenge

    @Test
    fun `when the verify mail challenge is sent`() {
        val event = SignUpEvent(
            Email("AN_EMAIL"),
            ClientAppFixture.aClientAppId(),
            Instant.now(),
            Password("A_PASSWORD")
        )

        val uut = SendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge)

        every { mailChallenge.sendVerifyMail("AN_EMAIL") } just runs

        uut.accept(event)
        assertEquals(true, uut.handleable(event))

        verify { mailChallenge.sendVerifyMail("AN_EMAIL") }
    }
}