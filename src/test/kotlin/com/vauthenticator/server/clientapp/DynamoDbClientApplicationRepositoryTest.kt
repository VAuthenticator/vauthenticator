package com.vauthenticator.server.clientapp

import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplication
import com.vauthenticator.server.oauth2.clientapp.DynamoDbClientApplicationRepository
import com.vauthenticator.server.support.DynamoDbUtils.dynamoClientApplicationTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DynamoDbClientApplicationRepositoryTest {

    private lateinit var underTest: DynamoDbClientApplicationRepository

    @BeforeEach
    fun setUp() {
        underTest = DynamoDbClientApplicationRepository(dynamoDbClient, dynamoClientApplicationTableName)
        resetDynamoDb(dynamoDbClient)
    }

    @Test
    fun `when find one client application by empty client id`() {
        val clientApp: Optional<ClientApplication> = underTest.findOne(ClientAppId(""))
        val expected = Optional.empty<ClientApplication>()
        assertEquals(expected, clientApp)
    }

    @Test
    fun `when find one client application by client id that does not exist`() {
        val clientApp: Optional<ClientApplication> =
            underTest.findOne(ClientAppId("not-existing-one"))
        val expected = Optional.empty<ClientApplication>()
        assertEquals(expected, clientApp)
    }

    @Test
    fun `when store, check if it exist and then delete a client application by client`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        underTest.save(expected)
        var actual = underTest.findOne(clientAppId)

        assertEquals(Optional.of(expected), actual)

        underTest.delete(clientAppId)
        actual = underTest.findOne(clientAppId)

        assertEquals(Optional.empty<ClientApplication>(), actual)
    }

    @Test
    fun `when find all client applications`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        underTest.save(expected)
        val actual = underTest.findAll()

        assertEquals(listOf(expected), actual)
    }

    @Test
    fun `when find an client application with zero authorities`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        underTest.save(expected)
        val actual = underTest.findAll()

        assertEquals(listOf(expected), actual)
    }

}