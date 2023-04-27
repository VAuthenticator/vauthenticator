package com.vauthenticator.server.account.tiket

import com.vauthenticator.server.account.EMAIL
import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.support.DatabaseUtils
import com.vauthenticator.server.support.DatabaseUtils.dynamoTicketTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Duration
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DynamoDbTicketRepositoryTest {
    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }


    private lateinit var underTest: DynamoDbTicketRepository

    @BeforeEach
    internal fun setUp() {
        underTest = DynamoDbTicketRepository(DatabaseUtils.dynamoDbClient, dynamoTicketTableName)
        resetDatabase()
    }

    @Test
    internal fun `when a ticket is stored`() {
        val ticket = Ticket(
            VerificationTicket(ticketGenerator.invoke()),
            VerificationTicketFeatures(Duration.ofSeconds(100)),
            EMAIL,
            A_CLIENT_APP_ID
        )
        underTest.store(ticket)

        val item = DatabaseUtils.dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName(dynamoTicketTableName)
                .key(
                    mapOf(
                        "ticket" to ticketGenerator.invoke().asDynamoAttribute()
                    )
                )
                .build()
        ).item()

        val ticketFromDynamo = item["ticket"]!!.s()
        val ticketTTLFromDynamo = item["ttl"]!!.n()

        assertEquals(ticketFromDynamo, ticket.verificationTicket.content)
        assertEquals(ticketTTLFromDynamo, "100")
    }


    @Test
    internal fun `when a ticket is retrieved`() {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val expected = Ticket(
            verificationTicket,
            VerificationTicketFeatures(Duration.ofSeconds(200)),
            EMAIL,
            A_CLIENT_APP_ID
        )

        underTest.store(expected)

        val actual = underTest.loadFor(verificationTicket)

        assertEquals(Optional.of(expected), actual)
    }

    @Test
    internal fun `when a ticket is not present`() {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.loadFor(verificationTicket)
        assertEquals(Optional.empty<Ticket>(), actual)
    }

    @Test
    internal fun `when a ticket is delete`() {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val expected = Ticket(
            verificationTicket,
            VerificationTicketFeatures(Duration.ofSeconds(200)),
            EMAIL,
            A_CLIENT_APP_ID
        )

        underTest.store(expected)

        val actual = underTest.loadFor(verificationTicket)

        assertEquals(Optional.of(expected), actual)

        underTest.delete(verificationTicket)
        val actualAfterDeletion = underTest.loadFor(verificationTicket)

        assertEquals(Optional.empty<Ticket>(), actualAfterDeletion)
    }
}