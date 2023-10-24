package com.vauthenticator.server.account.signup

import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEvent
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordHistoryRepository
import org.springframework.stereotype.Service

@Service
class SignUpEventConsumer(
    private val passwordHistoryRepository: PasswordHistoryRepository
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        println("UpdatePasswordHistoryUponSignUpEventConsumer")
        passwordHistoryRepository.store(event.userName.content, event.payload as Password)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is SignUpEvent
}