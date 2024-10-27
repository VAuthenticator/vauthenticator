package com.vauthenticator.server.password.domain.changepassword

import com.vauthenticator.server.events.ChangePasswordEvent
import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.VAuthenticatorEvent
import com.vauthenticator.server.password.domain.Password
import com.vauthenticator.server.password.domain.PasswordHistoryRepository
import org.springframework.stereotype.Service

class ChangePasswordEventConsumer(
    private val passwordHistoryRepository: PasswordHistoryRepository
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        passwordHistoryRepository.store(event.userName.content, event.payload as Password)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is ChangePasswordEvent
}