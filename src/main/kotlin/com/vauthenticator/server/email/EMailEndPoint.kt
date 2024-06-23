package com.vauthenticator.server.email

import com.vauthenticator.document.repository.Document
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.document.repository.DocumentType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class EMailEndPoint(private val documentRepository: DocumentRepository) {

    @GetMapping("/api/email-template/{EMailType}")
    fun getMailTemplate(@PathVariable EMailType: EMailType): ResponseEntity<EMailTemplate> {
        val document = documentRepository.loadDocument(DocumentType.MAIL.content, EMailType.path)
        return ResponseEntity.ok(EMailTemplate(EMailType, String(document.content)))
    }

    @PutMapping("/api/email-template")
    fun saveMailTemplate(@RequestBody request: EMailTemplate): ResponseEntity<Unit> {
        documentRepository.saveDocument(
            DocumentType.MAIL.content, //todo MAIL should be EMAIL
            Document(MediaType.TEXT_HTML_VALUE, request.emailType.path, request.body.toByteArray())
        )
        return ResponseEntity.noContent().build()
    }
}