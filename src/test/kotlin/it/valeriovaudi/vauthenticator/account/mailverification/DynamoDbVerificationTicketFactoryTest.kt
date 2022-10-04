package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoMailVerificationTicketTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DynamoDbVerificationTicketFactoryTest {

    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    private lateinit var underTest: DynamoDbMailVerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        underTest = DynamoDbMailVerificationTicketFactory(
                dynamoMailVerificationTicketTableName,
                dynamoDbClient,
                ticketGenerator
        )
        resetDatabase()
    }

    @Test
    internal fun `happy path`() {
        val account = anAccount()

        val clientApplication = aClientApp(ClientAppId("A_CLIENT_APP_ID"))

        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, clientApplication)

        assertEquals(expected, actual)

        val ticketFromDynamo = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(dynamoMailVerificationTicketTableName)
                        .key(mapOf(
                                "ticket" to ticketGenerator.invoke().asDynamoAttribute()
                        ))
                        .build()
        ).item()["ticket"]!!.s()

        assertEquals(ticketFromDynamo, actual.content)
    }
}