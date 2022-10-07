package it.valeriovaudi.vauthenticator.account.tiket

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsBoolFor
import it.valeriovaudi.vauthenticator.extentions.valueAsLongFor
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Duration
import java.util.*

interface TicketRepository {
    fun store(ticket: Ticket)
    fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket>
    fun delete(verificationTicket: VerificationTicket)
}

class DynamoDbTicketRepository(private val dynamoDbClient: DynamoDbClient,
                               private val tableName: String) : TicketRepository {

    override fun store(ticket: Ticket) {
        dynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(
                                mapOf(
                                        "ticket" to ticket.verificationTicket.content.asDynamoAttribute(),
                                        "fireAndForget" to ticket.features.fireAndForget.asDynamoAttribute(),
                                        "ttl" to (ticket.features.ttl.seconds).asDynamoAttribute(),
                                        "email" to ticket.email.asDynamoAttribute(),
                                        "client_application_id" to ticket.clientAppId.asDynamoAttribute()
                                )
                        )
                        .build()
        )
    }


    override fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket> {
        return Optional.ofNullable(
                dynamoDbClient.getItem(
                        GetItemRequest.builder()
                                .tableName(tableName)
                                .key(mapOf("ticket" to verificationTicket.content.asDynamoAttribute()))
                                .build()
                ).item()
        ).map {
            Ticket(
                    VerificationTicket(it.valueAsStringFor("ticket")),
                    VerificationTicketFeatures(Duration.ofSeconds(it.valueAsLongFor("ttl")), it.valueAsBoolFor("fireAndForget")),
                    it.valueAsStringFor("email"),
                    it.valueAsStringFor("client_application_id")
            )
        }

    }

    override fun delete(verificationTicket: VerificationTicket) {
        TODO("Not yet implemented")
    }

}