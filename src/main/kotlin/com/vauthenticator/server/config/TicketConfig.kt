package com.vauthenticator.server.config

import com.vauthenticator.server.account.ticket.DynamoDbTicketRepository
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.account.ticket.VerificationTicketFeatures
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Clock
import java.time.Duration
import java.util.*

@Configuration(proxyBeanMethods = false)
class TicketConfig {

    @Bean
    fun ticketRepository(
        @Value("\${vauthenticator.dynamo-db.ticket.table-name}") tableName: String,
        dynamoDbClient: DynamoDbClient
    ) =
        DynamoDbTicketRepository(dynamoDbClient, tableName)


    @Bean
    fun verificationTicketFactory(clock: Clock, ticketRepository: TicketRepository) =
        VerificationTicketFactory(
            { UUID.randomUUID().toString() },
            clock,
            ticketRepository,
            VerificationTicketFeatures(Duration.ofMinutes(5))
        )


}