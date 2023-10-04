package com.vauthenticator.server.password

import com.vauthenticator.server.AuthenticationUserNameRepository
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoPasswordHistoryTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock

@ExtendWith(MockKExtension::class)
class DynamoPasswordHistoryRepositoryTest {

    lateinit var uut: PasswordHistoryRepository

    @MockK
    lateinit var authenticationUserNameRepository: AuthenticationUserNameRepository

    @BeforeEach
    fun setUp() {
        uut = DynamoPasswordHistoryRepository(
            authenticationUserNameRepository,
            Clock.systemUTC(),
            dynamoPasswordHistoryTableName,
            dynamoDbClient
        )
        every { authenticationUserNameRepository.getCurrentAuthenticatedUserName() } returns "A_TEST_MAIL"

        resetDatabase()
    }

    @Test
    fun `when store a new password in an empty the history`() {

        uut.store(Password("A_PASSWORD"))
        val history = uut.load()

        Assertions.assertEquals(1, history.size)
    }

    @Test
    fun `when store a new password in an non empty the history`() {
        uut.store(Password("A_PASSWORD 3"))
        uut.store(Password("A_PASSWORD 1"))
        uut.store(Password("A_PASSWORD 2"))
        val history = uut.load()

        println(history)
        val expected = listOf(
            Password("A_PASSWORD 2"),
            Password("A_PASSWORD 1"),
            Password("A_PASSWORD 3")
        )
        Assertions.assertEquals(expected, history)
    }
}