package com.vauthenticator.server.account.tiket

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.filterEmptyAccountMetadata
import com.vauthenticator.server.extentions.valueAsLongFor
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
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
        )
                .flatMap { it.filterEmptyAccountMetadata() }
                .map {
                    Ticket(
                            VerificationTicket(it.valueAsStringFor("ticket")),
                            VerificationTicketFeatures(Duration.ofSeconds(it.valueAsLongFor("ttl"))),
                            it.valueAsStringFor("email"),
                            it.valueAsStringFor("client_application_id")
                    )
                }
    }

    override fun delete(verificationTicket: VerificationTicket) {
        dynamoDbClient.deleteItem(
                DeleteItemRequest.builder()
                        .tableName(tableName)
                        .key(mapOf("ticket" to verificationTicket.content.asDynamoAttribute()))
                        .build()
        )
    }

}