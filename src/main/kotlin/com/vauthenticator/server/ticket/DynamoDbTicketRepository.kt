package com.vauthenticator.server.ticket

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.filterEmptyMetadata
import com.vauthenticator.server.extentions.valueAsLongFor
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

class DynamoDbTicketRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String
) : TicketRepository {

    override fun store(ticket: Ticket) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(
                    mapOf(
                        "ticket" to ticket.verificationTicket.content.asDynamoAttribute(),
                        "ttl" to (ticket.ttl).asDynamoAttribute(),
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
            .flatMap { it.filterEmptyMetadata() }
            .map {
                Ticket(
                    VerificationTicket(it.valueAsStringFor("ticket")),
                    it.valueAsStringFor("email"),
                    it.valueAsStringFor("client_application_id"),
                    it.valueAsLongFor("ttl")
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