package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.ResetPasswordEvent
import com.vauthenticator.server.events.VAuthenticatorEvent
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordHistoryRepository
import org.springframework.stereotype.Service

//todo to be tested
@Service
class ResetPasswordEventConsumer(
    private val passwordHistoryRepository: PasswordHistoryRepository
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        println("ResetPasswordEventConsumer")
        passwordHistoryRepository.store(event.userName.content, event.payload as Password)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is ResetPasswordEvent
}