package com.vauthenticator.server.mail

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.document.repository.Document
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.document.DocumentType
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class MailEndPointTest {

    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var documentRepository: DocumentRepository

    private val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(MailEndPoint(documentRepository)).build()
    }

    @Test
    fun `when an mfa mail template is retrieved`() {
        val response = MailTemplate(
            MailType.MFA,
            "A_TEMPLATE"
        )

        every {
            documentRepository.loadDocument(
                DocumentType.MAIL.content,
                MailType.MFA.path
            )
        } returns Document("", MailType.MFA.path, "A_TEMPLATE".toByteArray())

        mockMvc.perform(
            get("/api/mail-template/${MailType.MFA}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `when a new mail template is uploaded`() {
        val request = MailTemplate(
            MailType.WELCOME,
            "A_TEMPLATE"
        )

        every {
            documentRepository.saveDocument(
                DocumentType.MAIL.content,
                Document("text/html", "templates/welcome.html", "A_TEMPLATE".toByteArray())
            )
        } just runs

        mockMvc.perform(
            put("/api/mail-template")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNoContent)
    }
}