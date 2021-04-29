package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoClientApplicationTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoDbClient
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
        TestingFixture.resetDatabase(dynamoDbClient)
    }

    @Test
    fun `when find one client application by client id that does not exist`() {
        val clientApp: Optional<ClientApplication> = dynamoDbClientApplicationRepository.findOne(ClientAppId(""))
        Assertions.assertEquals(clientApp, Optional.empty<ClientApplication>())
    }

    @Test
    fun `when find one client application by client id after save it`() {
        val expected = ClientAppFixture.aClientApp(ClientAppId("client_id"))
        dynamoDbClientApplicationRepository.save(expected)
        val actual = dynamoDbClientApplicationRepository.findOne(ClientAppId("client_id"))

        Assertions.assertEquals(actual, Optional.of(expected))
    }

    @Test
    fun `when find federated client applications by federation`() {
        TODO("Not yet implemented")
    }

    @Test
    fun `when find all client applications`() {
        TODO("Not yet implemented")
    }


    @Test
    fun `when delete a client application`() {
        TODO("Not yet implemented")
    }
}