package com.vauthenticator.server.password

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.clientapp.ClientAppFixture
import com.vauthenticator.server.events.SignUpEvent
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(MockKExtension::class)
class UpdatePasswordHistoryUponSignUpEventConsumerTest {

    @MockK
    lateinit var passwordHistoryRepository: PasswordHistoryRepository

    @Test
    fun `when upon a sign up event the password history is updated`() {
        val password = Password("A_PASSWORD")
        val event = SignUpEvent(Email("AN_EMAIL"), ClientAppFixture.aClientAppId(), Instant.now(), password)

        val uut = UpdatePasswordHistoryUponSignUpEventConsumer(passwordHistoryRepository)

        every { passwordHistoryRepository.store(password) } just runs

        uut.accept(event)

        verify { passwordHistoryRepository.store(password) }
    }
}