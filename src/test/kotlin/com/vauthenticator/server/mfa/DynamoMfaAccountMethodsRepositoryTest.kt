package com.vauthenticator.server.mfa

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.keys.*
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoMfaAccountMethodsTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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

    @Test
    fun `when a mfa account do not have method stored`() {
        val masterKid = MasterKid("")
        val email = anAccount().email

        val underTest = DynamoMfaAccountMethodsRepository(
            dynamoMfaAccountMethodsTableName,
            dynamoDbClient,
            keyRepository,
            masterKid
        )

        val mfaAccountMethods = underTest.findAll(email)
        Assertions.assertEquals(emptyMap<MfaMethod, MfaAccountMethod>(), mfaAccountMethods)
    }
}