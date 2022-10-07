package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoMailVerificationTicketTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import it.valeriovaudi.vauthenticator.time.Clocker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DynamoDbMailVerificationTicketFactoryTest {

    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    @MockK
    lateinit var clocker : Clocker

    private lateinit var underTest: DynamoDbMailVerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        DynamoDbMailVerificationTicketFactory(
                dynamoMailVerificationTicketTableName,
                dynamoDbClient,
                ticketGenerator,
                clocker,
                VerificationTicketFeatures(Duration.ofSeconds(100), false)
        ).also { underTest = it }
        resetDatabase()
    }

    @Test
    internal fun `happy path`() {
        val account = anAccount()

        val now = Instant.ofEpochSecond(100)
        every { clocker.now() } returns now

        val clientApplication = aClientApp(ClientAppId("A_CLIENT_APP_ID"))

        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, clientApplication)

        assertEquals(expected, actual)

        val item = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(dynamoMailVerificationTicketTableName)
                        .key(mapOf(
                                "ticket" to ticketGenerator.invoke().asDynamoAttribute()
                        ))
                        .build()
        ).item()

        val ticketFromDynamo = item["ticket"]!!.s()
        val ticketTTLFromDynamo = item["ttl"]!!.n()

        assertEquals(ticketFromDynamo, actual.content)
        assertEquals(ticketTTLFromDynamo, "200")
    }
}