package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoMfaAccountMethodsTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class DynamoMfaAccountMethodsRepositoryTest {

    private val masterKid = MasterKid("")
    private val email = anAccount().email

    @MockK
    lateinit var keyRepository: KeyRepository

    lateinit var underTest: MfaAccountMethodsRepository

    @BeforeEach
    fun setUp() {
        resetDynamoDb()
        underTest = DynamoMfaAccountMethodsRepository(
            dynamoMfaAccountMethodsTableName,
            dynamoDbClient,
            keyRepository,
            masterKid
        )
    }

    @Test
    fun `when a mfa account method is stored`() {
        every { keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA) } returns Kid("")

        underTest.save(email, MfaMethod.EMAIL_MFA_METHOD, email, true)
        val mfaAccountMethods = underTest.findAll(email)
        assertEquals(
            listOf(MfaAccountMethod(email, Kid(""), MfaMethod.EMAIL_MFA_METHOD, email)),
            mfaAccountMethods
        )
    }

    @Test
    fun `when a mfa account do not have method stored`() {
        val mfaAccountMethods = underTest.findAll(email)
        assertEquals(emptyList<MfaAccountMethod>(), mfaAccountMethods)
    }

    @Test
    fun `when try to get one specific enrolment association`() {
        every { keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA) } returns Kid("")

        underTest.save(email, MfaMethod.EMAIL_MFA_METHOD, email, true)
        val mfaAccountMethods = underTest.findOne(email, MfaMethod.EMAIL_MFA_METHOD, email)
        assertEquals(
            Optional.of(MfaAccountMethod(email, Kid(""), MfaMethod.EMAIL_MFA_METHOD, email)),
            mfaAccountMethods
        )
    }

    @Test
    fun `when one specific enrolment association is not found`() {
        val mfaAccountMethods = underTest.findOne(email, MfaMethod.EMAIL_MFA_METHOD, email)
        val expected = Optional.empty<Any>()
        assertEquals(expected, mfaAccountMethods)
    }
}