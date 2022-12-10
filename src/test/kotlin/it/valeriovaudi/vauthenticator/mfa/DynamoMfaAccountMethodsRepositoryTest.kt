package it.valeriovaudi.vauthenticator.mfa

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.keys.*
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoMfaAccountMethodsTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DynamoMfaAccountMethodsRepositoryTest {

    @MockK
    lateinit var keyRepository: KeyRepository

    @BeforeEach
    fun setUp() {
        resetDatabase()
    }

    @Test
    fun `when a mfa account method is stored`() {
        val masterKid = MasterKid("")
        val email = anAccount().email

        val underTest = DynamoMfaAccountMethodsRepository(
            dynamoMfaAccountMethodsTableName,
            dynamoDbClient,
            keyRepository,
            masterKid
        )
        every { keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA) } returns Kid("")

        underTest.save(email, MfaMethod.EMAIL_MFA_METHOD)
        val mfaAccountMethods = underTest.findAll(email)
        Assertions.assertEquals(
            MfaAccountMethod(email, Kid(""), MfaMethod.EMAIL_MFA_METHOD),
            mfaAccountMethods[MfaMethod.EMAIL_MFA_METHOD]
        )
    }
}