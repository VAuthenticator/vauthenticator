package com.vauthenticator.server.config

import com.vauthenticator.server.account.mailverification.SendVerifyMailChallengeUponSignUpEventConsumer
import com.vauthenticator.server.account.signup.SignUpEventConsumer
import com.vauthenticator.server.account.welcome.SendWelcomeMailUponSignUpEventConsumer
import com.vauthenticator.server.events.*
import com.vauthenticator.server.password.changepassword.ChangePasswordEventConsumer
import com.vauthenticator.server.password.resetpassword.ResetPasswordEventConsumer
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class EventsConfig(private val eventConsumerConfig: EventConsumerConfig) {

    @Bean
    fun eventsDispatcherAdapter(publisher: ApplicationEventPublisher) =
        SpringEventEventsDispatcher(publisher)

    @Bean
    fun eventsDispatcher(publisher: ApplicationEventPublisher) =
        VAuthenticatorEventsDispatcher(publisher)

    @Bean
    fun eventsCollector(
        changePasswordEventConsumer: ChangePasswordEventConsumer,
        resetPasswordEventConsumer: ResetPasswordEventConsumer,
        signUpEventConsumer: SignUpEventConsumer,
        sendWelcomeMailUponSignUpEventConsumer: SendWelcomeMailUponSignUpEventConsumer,
        sendVerifyMailChallengeUponSignUpEventConsumer: SendVerifyMailChallengeUponSignUpEventConsumer,
        loggerEventConsumer: EventConsumer
    ) =
        SpringEventsCollector(
            listOf(
                loggerEventConsumer,
                sendWelcomeMailUponSignUpEventConsumer,
                sendVerifyMailChallengeUponSignUpEventConsumer,
                signUpEventConsumer,
                resetPasswordEventConsumer,
                changePasswordEventConsumer
            )
        )

    @Bean
    fun loggerEventConsumer() = LoggerEventConsumer(eventConsumerConfig)

}