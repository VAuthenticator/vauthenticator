package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.userdetails.AccountUserDetailsServiceAdapter
import it.valeriovaudi.vauthenticator.userdetails.NotParsableAccountDetails
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels

open class RabbitAccountRepository(private val getAccount: GetAccount) : AccountRepository {

    override fun accountFor(username: String) = getAccount.accountFor(username)
}

@MessagingGateway
interface GetAccount {

    @Gateway(requestChannel = "getAccountInboundChannel",
            replyChannel = "getAccountOutboundChannel",
            replyTimeout = (60 * 1000).toLong())
    fun accountFor(username: String): Account
}

class RabbitMessageAccountAdapter(private val objectMapper: ObjectMapper) {
    fun convert(securityAccountDetails: String) =
            try {
                objectMapper.readValue(securityAccountDetails, Account::class.java)
            } catch (e: Exception) {
                throw NotParsableAccountDetails(e.message!!, e)
            }
}