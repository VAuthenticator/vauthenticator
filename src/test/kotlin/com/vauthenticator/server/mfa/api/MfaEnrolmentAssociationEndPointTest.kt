package com.vauthenticator.server.mfa.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.EmailMfaDevice
import com.vauthenticator.server.mfa.domain.MfaAccountMethodsRepository
import com.vauthenticator.server.mfa.domain.MfaMethod.EMAIL_MFA_METHOD
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollment
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.oauth2.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.Scope
import com.vauthenticator.server.oauth2.clientapp.domain.Scopes
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.MfaFixture.accountMfaAssociatedMfaMethods
import com.vauthenticator.server.support.SecurityFixture.principalFor
import com.vauthenticator.server.ticket.TicketId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    @MockK
    private lateinit var mfaMethodsEnrollment: MfaMethodsEnrollment

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var mfaMethodsEnrolmentAssociation: MfaMethodsEnrollmentAssociation

    @MockK
    private lateinit var permissionValidator: PermissionValidator

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(
            MfaEnrolmentAssociationEndPoint(
                sensitiveEmailMasker,
                mfaAccountMethodsRepository,
                mfaMethodsEnrollment,
                mfaMethodsEnrolmentAssociation,
                permissionValidator
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
                            EmailMfaDevice(email, EMAIL_MFA_METHOD)
                        )
                    )
                )
            )
    }

    @Test
    fun `when a new mfa channel is enrolled`() {
        val account = AccountTestFixture.anAccount()
        val email = account.email

        val authentication = principalFor(
            clientAppId = A_CLIENT_APP_ID,
            email = email,
            authorities = listOf("USER"),
            scopes = listOf(Scope.MFA_ENROLLMENT.content)
        )
        every { permissionValidator.validate(authentication, any(), Scopes.from(Scope.MFA_ENROLLMENT)) } just runs
        every {
            mfaMethodsEnrollment.enroll(
                account.email,
                EMAIL_MFA_METHOD,
                account.email,
                ClientAppId(A_CLIENT_APP_ID),
                true
            )
        } returns TicketId("A_TICKET")

        mokMvc.perform(
            post("/api/mfa/enrollment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MfaEnrollmentRequest(account.email, EMAIL_MFA_METHOD)))
                .principal(authentication)
        )
            .andExpect(status().isCreated)
            .andExpect(
                content().json(
                    objectMapper.writeValueAsString(
                        MfaEnrollmentResponse("A_TICKET")
                    )
                )
            )

    }


    @Test
    fun `when a new enrolled mfa is associated`() {
        val account = AccountTestFixture.anAccount()
        val email = account.email

        val authentication = principalFor(
            clientAppId = A_CLIENT_APP_ID,
            email = email,
            authorities = listOf("USER"),
            scopes = listOf(Scope.MFA_ENROLLMENT.content)
        )
        every { permissionValidator.validate(authentication, any(), Scopes.from(Scope.MFA_ENROLLMENT)) } just runs
        every { mfaMethodsEnrolmentAssociation.associate("A_TICKET", "A_CODE") } just runs

        mokMvc.perform(
            post("/api/mfa/associate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MfaEnrollmentAssociationRequest("A_TICKET", "A_CODE")))
                .principal(authentication)
        )
            .andExpect(status().isNoContent)
    }
}