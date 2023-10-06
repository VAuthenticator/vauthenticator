package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEvent

class SendVerifyMailChallengeUponSignUpEventConsumer(
    private val mailChallenge: SendVerifyMailChallenge
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        mailChallenge.sendVerifyMail(event.userName.content)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is SignUpEvent

}