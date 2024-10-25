package com.vauthenticator.server.password.adapter.dynamodb

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.password.domain.Password
import com.vauthenticator.server.password.domain.PasswordHistoryRepository
import com.vauthenticator.server.support.DynamoDbUtils
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Clock

private const val A_USERNAME = "A_USERNAME"

@ExtendWith(MockKExtension::class)
class DynamoPasswordHistoryRepositoryTest {

    lateinit var uut: PasswordHistoryRepository

    @BeforeEach
    fun setUp() {
        uut = DynamoPasswordHistoryRepository(
            2,
            3,
            Clock.systemUTC(),
            DynamoDbUtils.dynamoPasswordHistoryTableName,
            DynamoDbUtils.dynamoDbClient
        )

        DynamoDbUtils.resetDynamoDb()
    }

    @Test
    fun `when store a new password in an empty the history`() {

        uut.store(A_USERNAME, Password("A_PASSWORD"))
        val history = uut.load(A_USERNAME)

        Assertions.assertEquals(1, history.size)
    }

    @Test
    fun `when store a new password in an non empty the history`() {
        uut.store(A_USERNAME, Password("A_PASSWORD 3"))
        uut.store(A_USERNAME, Password("A_PASSWORD 1"))
        uut.store(A_USERNAME, Password("A_PASSWORD 2"))
        uut.store(A_USERNAME, Password("A_PASSWORD 10"))
        uut.store(A_USERNAME, Password("A_PASSWORD 12"))
        uut.store(A_USERNAME, Password("A_PASSWORD 22"))
        uut.store(A_USERNAME, Password("A_PASSWORD 32"))
        uut.store(A_USERNAME, Password("A_PASSWORD 42"))
        val history = uut.load(A_USERNAME)

        val expected = listOf(
            Password("A_PASSWORD 42"),
            Password("A_PASSWORD 32"),
        )
        Assertions.assertEquals(expected, history)
        Assertions.assertEquals(3, loadActualDynamoSizeFor(A_USERNAME).size)
    }

    @Test
    fun `when store a new password in an non empty the history with less item then requested as limit`() {
        uut.store(A_USERNAME, Password("A_PASSWORD 3"))
        val history = uut.load(A_USERNAME)

        val expected = listOf(
            Password("A_PASSWORD 3"),
        )
        Assertions.assertEquals(expected, history)
        Assertions.assertEquals(1, loadActualDynamoSizeFor(A_USERNAME).size)
    }

    private fun loadActualDynamoSizeFor(userName: String): MutableList<MutableMap<String, AttributeValue>> {
        return DynamoDbUtils.dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(DynamoDbUtils.dynamoPasswordHistoryTableName)
                .scanIndexForward(false)
                .keyConditionExpression("user_name=:email")
                .expressionAttributeValues(mapOf(":email" to userName.asDynamoAttribute())).build()
        ).items()
    }

}