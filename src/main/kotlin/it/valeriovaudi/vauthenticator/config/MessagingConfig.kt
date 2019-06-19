package it.valeriovaudi.vauthenticator.config

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.MessageChannels

@Configuration
class MessagingConfig {

    @Bean("authServerAccountServiceBridgeInboundChannel")
    fun authServerAccountServiceBridgeInboundChannel() =
            MessageChannels.direct().get()

    @Bean("authServerAccountServiceBridgeOutboundChannel")
    fun authServerAccountServiceBridgeOutboundChannel() =
            MessageChannels.direct().get()


    @Bean("authServerAccountServiceBridgeInboundQueue")
    fun authServerAccountServiceBridgeInboundQueue() =
            Queue("authServerAccountServiceBridgeInboundQueue", false)


    @Bean("authServerAccountServiceBridgeOutboundQueue")
    fun authServerAccountServiceBridgeOutboundQueue() =
            Queue("authServerAccountServiceBridgeOutboundQueue", false)

}
