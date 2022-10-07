package it.valeriovaudi.vauthenticator.account.tiket

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.time.Clocker
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

interface TicketRepository {
    fun store(ticket: Ticket)
    fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket>
    fun delete(verificationTicket: VerificationTicket)
}

class DynamoDbTicketRepository(private val dynamoDbClient: DynamoDbClient,
                               private val clocker: Clocker,
                               private val tableName: String) : TicketRepository {

    override fun store(ticket: Ticket) {
        dynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(
                                mapOf(
                                        "ticket" to ticket.verificationTicket.content.asDynamoAttribute(),
                                        "fireAndForget" to ticket.features.fireAndForget.asDynamoAttribute(),
                                        "ttl" to (clocker.now().epochSecond + ticket.features.ttl.seconds).asDynamoAttribute(),
                                        "email" to ticket.email.asDynamoAttribute(),
                                        "client_application_id" to ticket.clientAppId.asDynamoAttribute()
                                )
                        )
                        .build()
        )
    }


    override fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket> {
        TODO("Not yet implemented")
    }

    override fun delete(verificationTicket: VerificationTicket) {
        TODO("Not yet implemented")
    }

}