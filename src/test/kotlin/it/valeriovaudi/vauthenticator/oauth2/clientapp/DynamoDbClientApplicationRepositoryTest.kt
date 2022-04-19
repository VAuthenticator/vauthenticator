package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoClientApplicationTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoDbClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

internal class DynamoDbClientApplicationRepositoryTest {

    lateinit var dynamoDbClientApplicationRepository: DynamoDbClientApplicationRepository

    object passwordEncoder : PasswordEncoder {
        override fun encode(p0: CharSequence): String =
                p0.toString()


        override fun matches(p0: CharSequence?, p1: String?): Boolean {
            TODO("Not yet implemented")
        }

    }

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