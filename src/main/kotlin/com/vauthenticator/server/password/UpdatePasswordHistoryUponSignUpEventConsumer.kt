package com.vauthenticator.server.password

import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEvent
import org.springframework.stereotype.Service

@Service
class UpdatePasswordHistoryUponSignUpEventConsumer(
    private val passwordHistoryRepository: PasswordHistoryRepository
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        passwordHistoryRepository.store(event.userName.content, event.payload as Password)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean =
        event::class.isInstance(SignUpEvent::class)

}