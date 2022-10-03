package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DynamoDbMailVerificationTicketFactoryTest {

    lateinit var underTest: DynamoDbMailVerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        underTest = DynamoDbMailVerificationTicketFactory(dynamoDbClient)
    }

    @Test
    internal fun `happy path`() {


    }
}