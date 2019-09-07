package it.valeriovaudi.vauthenticator.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.boot.context.properties.PropertyMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
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

    @Autowired
    lateinit var properties: RabbitProperties

    @Autowired
    lateinit var messageConverter: ObjectProvider<MessageConverter>

    @Bean
    @Scope("prototype")
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val map = PropertyMapper.get()
        val template = RabbitTemplate(connectionFactory)
        val messageConverter = this.messageConverter.getIfUnique()
        if (messageConverter != null) {
            template.messageConverter = messageConverter
        }
        return template
    }
}
