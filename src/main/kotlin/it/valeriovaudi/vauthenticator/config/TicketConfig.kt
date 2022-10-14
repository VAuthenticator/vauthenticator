package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.tiket.DynamoDbTicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFactory
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.time.UtcClocker
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration
import java.util.*

@Configuration(proxyBeanMethods = false)
class TicketConfig {

    @Bean
    fun ticketRepository(@Value("\${vauthenticator.dynamo-db.ticket.table-name}") tableName: String, dynamoDbClient: DynamoDbClient) =
            DynamoDbTicketRepository(dynamoDbClient, tableName)


    @Bean
    fun verificationTicketFactory(ticketRepository: TicketRepository) =
            VerificationTicketFactory({ UUID.randomUUID().toString() }, UtcClocker(), ticketRepository,
                    VerificationTicketFeatures(Duration.ofMinutes(5))
            )


}