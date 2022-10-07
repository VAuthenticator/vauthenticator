package it.valeriovaudi.vauthenticator.account.tiket

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.support.DatabaseUtils
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
internal class DynamoDbTicketRepositoryTest {
    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    @MockK
    lateinit var clocker: Clocker

    private lateinit var underTest: DynamoDbTicketRepository

    @BeforeEach
    internal fun setUp() {
        underTest = DynamoDbTicketRepository(DatabaseUtils.dynamoDbClient, clocker, dynamoMailVerificationTicketTableName)
        resetDatabase()
    }

    @Test
    internal fun `happy path`() {
        val now = Instant.ofEpochSecond(100)
        every { clocker.now() } returns now

        val ticket = Ticket(
                VerificationTicket(ticketGenerator.invoke()),
                VerificationTicketFeatures(Duration.ofSeconds(100), false),
                "email@domain.com",
                "A_CLIENT_APP_ID"
        )
        underTest.store(ticket)

        val item = DatabaseUtils.dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(dynamoMailVerificationTicketTableName)
                        .key(mapOf(
                                "ticket" to ticketGenerator.invoke().asDynamoAttribute()
                        ))
                        .build()
        ).item()

        val ticketFromDynamo = item["ticket"]!!.s()
        val ticketTTLFromDynamo = item["ttl"]!!.n()

        assertEquals(ticketFromDynamo, ticket.verificationTicket.content)
        assertEquals(ticketTTLFromDynamo, "200")
    }
}