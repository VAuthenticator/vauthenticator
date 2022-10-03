package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

interface MailVerificationTicketFactory {
    fun createTicketFor(account: Account,
                        clientApplication: ClientApplication): MailVerificationTicket
}

class DynamoDbMailVerificationTicketFactory(private val dynamoDbClient: DynamoDbClient) :MailVerificationTicketFactory {
    override fun createTicketFor(account: Account, clientApplication: ClientApplication): MailVerificationTicket {
        TODO("Not yet implemented")
    }

}