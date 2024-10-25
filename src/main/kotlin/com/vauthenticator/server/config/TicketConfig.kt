package com.vauthenticator.server.config

import com.vauthenticator.server.ticket.adapter.dynamodb.DynamoDbTicketRepository
import com.vauthenticator.server.ticket.domain.TicketCreator
import com.vauthenticator.server.ticket.domain.TicketFeatures
import com.vauthenticator.server.ticket.domain.TicketRepository
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
        TicketCreator(
            { UUID.randomUUID().toString() },
            clock,
            ticketRepository,
            TicketFeatures(Duration.ofMinutes(5))
        )


}