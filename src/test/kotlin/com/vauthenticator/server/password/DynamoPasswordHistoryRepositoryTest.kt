package com.vauthenticator.server.password

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.Email
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoPasswordHistoryTableName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Clock

class DynamoPasswordHistoryRepositoryTest {

    private val account = anAccount()

    @Test
    fun `when store a new password in an empty the history`() {
        val uut = DynamoPasswordHistoryRepository(Clock.systemUTC(), dynamoPasswordHistoryTableName, dynamoDbClient)

        val email = Email(account.email)

        uut.store(email, Password("A_PASSWORD"))
        val history = uut.load(email, 10)

        Assertions.assertEquals(1, history.size)
    }
}