package com.vauthenticator.server.ticket

import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.support.DynamoDbUtils
import com.vauthenticator.server.support.DynamoDbUtils.dynamoTicketTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import com.vauthenticator.server.support.EMAIL
import com.vauthenticator.server.support.TicketFixture.ticketFor
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DynamoDbTicketRepositoryTest {
    private val ticketValue = UUID.randomUUID().toString()
    private val ticketGenerator = { ticketValue }


    private lateinit var underTest: DynamoDbTicketRepository
    private val ticket = ticketFor(ticketGenerator.invoke(), EMAIL, A_CLIENT_APP_ID)

    @BeforeEach
    internal fun setUp() {
        underTest = DynamoDbTicketRepository(DynamoDbUtils.dynamoDbClient, dynamoTicketTableName)
        resetDynamoDb()
    }

    @Test
    internal fun `when a ticket is stored`() {
        underTest.store(ticket)

        val item = DynamoDbUtils.dynamoDbClient.getItem(
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

        assertEquals(ticketFromDynamo, ticket.ticketId.content)
        assertEquals(ticketTTLFromDynamo, "200")
    }


    @Test
    internal fun `when a ticket is retrieved`() {
        val ticketId = TicketId(ticketGenerator.invoke())
        val expected = ticket

        underTest.store(expected)

        val actual = underTest.loadFor(ticketId)

        assertEquals(Optional.of(expected), actual)
    }

    @Test
    internal fun `when a ticket is not present`() {
        val ticketId = TicketId(ticketGenerator.invoke())
        val actual = underTest.loadFor(ticketId)
        assertEquals(Optional.empty<Ticket>(), actual)
    }

    @Test
    internal fun `when a ticket is delete`() {
        val ticketId = TicketId(ticketGenerator.invoke())
        val expected = ticket

        underTest.store(expected)

        val actual = underTest.loadFor(ticketId)

        assertEquals(Optional.of(expected), actual)

        underTest.delete(ticketId)
        val actualAfterDeletion = underTest.loadFor(ticketId)

        assertEquals(Optional.empty<Ticket>(), actualAfterDeletion)
    }
}