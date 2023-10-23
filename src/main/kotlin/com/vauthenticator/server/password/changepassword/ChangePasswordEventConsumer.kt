package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.events.ChangePasswordEvent
import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.VAuthenticatorEvent
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordHistoryRepository
import org.springframework.stereotype.Service

//todo to be tested
@Service
class ChangePasswordEventConsumer(
    private val passwordHistoryRepository: PasswordHistoryRepository
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        println("ChangePasswordEventConsumer")
        passwordHistoryRepository.store(event.userName.content, event.payload as Password)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is ChangePasswordEvent
}