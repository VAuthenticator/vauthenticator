package com.vauthenticator.server.mfa.adapter.dynamodb

import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaAccountMethodsRepository
import com.vauthenticator.server.mfa.domain.MfaDeviceId
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDefaultMfaAccountMethodsTableName
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

    private val mfaDeviceId = MfaDeviceId("A_MFA_DEVICE_ID")
    private val masterKid = MasterKid("")
    private val email = anAccount().email
    private val key = Kid("")

    @MockK
    lateinit var keyRepository: KeyRepository

    lateinit var underTest: MfaAccountMethodsRepository

    @BeforeEach
    fun setUp() {

        resetDynamoDb()
        underTest = DynamoMfaAccountMethodsRepository(
            dynamoMfaAccountMethodsTableName,
            dynamoDefaultMfaAccountMethodsTableName,
            dynamoDbClient,
            keyRepository,
            masterKid
        ) { mfaDeviceId }
    }

    @Test
    fun `when a mfa account method is stored`() {
        every { keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA) } returns key

        underTest.save(email, MfaMethod.EMAIL_MFA_METHOD, email, true)
        val mfaAccountMethods = underTest.findAll(email)
        assertEquals(
            listOf(MfaAccountMethod(email, mfaDeviceId, key, MfaMethod.EMAIL_MFA_METHOD, email, true)),
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
        val mfaAccountMethods = underTest.findBy(email, MfaMethod.EMAIL_MFA_METHOD, email)
        assertEquals(
            Optional.of(MfaAccountMethod(email, mfaDeviceId, key, MfaMethod.EMAIL_MFA_METHOD, email, true)),
            mfaAccountMethods
        )
    }

    @Test
    fun `when one specific mfa account method is found by device id`() {
        every { keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA) } returns Kid("")

        val savedMfaAccountMethod = underTest.save(email, MfaMethod.EMAIL_MFA_METHOD, email, true)
        val mfaAccountMethods = underTest.findBy(savedMfaAccountMethod.mdaDeviceId)

        assertEquals(
            Optional.of(MfaAccountMethod(email, mfaDeviceId, key, MfaMethod.EMAIL_MFA_METHOD, email, true)),
            mfaAccountMethods
        )

    }

    @Test
    fun `when one specific enrolment association is not found`() {
        val mfaAccountMethods = underTest.findBy(email, MfaMethod.EMAIL_MFA_METHOD, email)
        val expected = Optional.empty<Any>()
        assertEquals(expected, mfaAccountMethods)
    }

    @Test
    fun `when decide what mfa use as default`() {
        val expected = Optional.of(mfaDeviceId)
        underTest.setAsDefault(email, mfaDeviceId)
        val defaultDevice = underTest.getDefaultDevice(email)

        assertEquals(expected, defaultDevice)
    }
}