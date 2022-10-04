package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.time.Duration

interface MailVerificationTicketFactory {
    fun createTicketFor(account: Account,
                        clientApplication: ClientApplication): VerificationTicket
}


class DynamoDbMailVerificationTicketFactory(private val tableName: String,
                                            private val dynamoDbClient: DynamoDbClient,
                                            private val ticketGenerator: () -> String) : MailVerificationTicketFactory {
    override fun createTicketFor(account: Account, clientApplication: ClientApplication): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
                verificationTicket,
                VerificationTicketFeatures(Duration.ZERO, false),
                account.email,
                clientApplication.clientAppId.content
        )
        store(ticket)
        return verificationTicket
    }

    private fun store(ticket: Ticket) {
        dynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(
                                mapOf(
                                        "ticket" to ticket.verificationTicket.content.asDynamoAttribute(),
                                        "fireAndForget" to ticket.features.fireAndForget.asDynamoAttribute(),
                                        "ttl" to ticket.features.ttl.seconds.asDynamoAttribute(),
                                        "user_name" to ticket.userName.asDynamoAttribute(),
                                        "client_application_id" to ticket.clientAppId.asDynamoAttribute()
                                )
                        )
                        .build()
        )
    }

}