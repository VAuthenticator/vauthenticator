package com.vauthenticator.server.mfa.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.MfaMethod.EMAIL_MFA_METHOD
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.MfaFixture.accountMfaAssociatedMfaMethods
import com.vauthenticator.server.support.SecurityFixture.principalFor
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class MfaEnrolmentAssociationEndPointTest {

    lateinit var mokMvc: MockMvc

    private val objectMapper = ObjectMapper()

    @MockK
    private lateinit var sensitiveEmailMasker: SensitiveEmailMasker

    @MockK
    private lateinit var mfaAccountMethodsRepository: MfaAccountMethodsRepository

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaEnrolmentAssociationEndPoint(
                sensitiveEmailMasker,
                mfaAccountMethodsRepository
            )
        ).build()
    }


    @Test
    fun `when all enrolment are retrieved`() {
        val account = AccountTestFixture.anAccount()
        val email = account.email

        every { sensitiveEmailMasker.mask(email) } returns email
        every { mfaAccountMethodsRepository.findAll(email) } returns accountMfaAssociatedMfaMethods(email)

        mokMvc.perform(
            get("/api/mfa/enrollment")
                .principal(principalFor(email))
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    objectMapper.writeValueAsString(
                        listOf(
                            EmailMfaEnrolledDeviceResponse(email, EMAIL_MFA_METHOD.name)
                        )
                    )
                )
            )
    }
}