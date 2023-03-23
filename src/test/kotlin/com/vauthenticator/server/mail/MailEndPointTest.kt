package com.vauthenticator.server.mail

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.document.Document
import com.vauthenticator.server.document.DocumentRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class MailEndPointTest {

    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var documentRepository: DocumentRepository

    val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(MailEndPoint(documentRepository)).build()
    }

    @Test
    fun `when a new mail template is uploaded`() {
        val request = SaveMailTemplateRequest(
            MailType.WELCOME,
            "A_TEMPLATE"
        )

        every {
            documentRepository.saveDocument(
                MailType.WELCOME.name,
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