package com.vauthenticator.server.clientapp

import com.vauthenticator.server.oauth2.clientapp.Authorities
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplication
import com.vauthenticator.server.oauth2.clientapp.DynamoDbClientApplicationRepository
import com.vauthenticator.server.support.DatabaseUtils.dynamoClientApplicationTableName
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DynamoDbClientApplicationRepositoryTest {

    lateinit var dynamoDbClientApplicationRepository: DynamoDbClientApplicationRepository

    @BeforeEach
    fun setUp() {
        dynamoDbClientApplicationRepository =
                DynamoDbClientApplicationRepository(dynamoDbClient, dynamoClientApplicationTableName)
    }

    @AfterEach
    fun tearDown() {
        resetDatabase()
    }

    @Test
    fun `when find one client application by empty client id`() {
        val clientApp: Optional<ClientApplication> = dynamoDbClientApplicationRepository.findOne(ClientAppId(""))
        Assertions.assertEquals(clientApp, Optional.empty<ClientApplication>())
    }

    @Test
    fun `when find one client application by client id that does not exist`() {
        val clientApp: Optional<ClientApplication> =
                dynamoDbClientApplicationRepository.findOne(ClientAppId("not-existing-one"))
        Assertions.assertEquals(clientApp, Optional.empty<ClientApplication>())
    }

    @Test
    fun `when store, check if it exist and then delete a client application by client`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        dynamoDbClientApplicationRepository.save(expected)
        var actual = dynamoDbClientApplicationRepository.findOne(clientAppId)

        Assertions.assertEquals(actual, Optional.of(expected))

        dynamoDbClientApplicationRepository.delete(clientAppId)
        actual = dynamoDbClientApplicationRepository.findOne(clientAppId)

        Assertions.assertEquals(actual, Optional.empty<ClientApplication>())
    }

    @Test
    fun `when find all client applications`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        dynamoDbClientApplicationRepository.save(expected)
        val actual = dynamoDbClientApplicationRepository.findAll()

        Assertions.assertEquals(actual, listOf(expected))
    }

    @Test
    fun `when find an client application with zero authorities`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId, authorities = Authorities.empty())
        dynamoDbClientApplicationRepository.save(expected)
        val actual = dynamoDbClientApplicationRepository.findAll()

        Assertions.assertEquals(actual, listOf(expected))
    }

}