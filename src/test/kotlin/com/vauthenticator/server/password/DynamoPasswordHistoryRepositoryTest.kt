package com.vauthenticator.server.password

import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoPasswordHistoryTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock

private const val A_USERNAME = "A_USERNAME"

@ExtendWith(MockKExtension::class)
class DynamoPasswordHistoryRepositoryTest {

    lateinit var uut: PasswordHistoryRepository

    @BeforeEach
    fun setUp() {
        uut = DynamoPasswordHistoryRepository(
            2,
            Clock.systemUTC(),
            dynamoPasswordHistoryTableName,
            dynamoDbClient
        )

        resetDatabase()
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
        val history = uut.load(A_USERNAME)

        println(history)
        val expected = listOf(
            Password("A_PASSWORD 2"),
            Password("A_PASSWORD 1"),
        )
        Assertions.assertEquals(expected, history)
    }
}