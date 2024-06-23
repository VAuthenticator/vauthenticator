package com.vauthenticator.server.email

import com.vauthenticator.document.repository.Document
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.document.repository.DocumentType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class EMailEndPoint(private val documentRepository: DocumentRepository) {

    @GetMapping("/api/email-template/{mailType}")
    fun getMailTemplate(@PathVariable mailType: MailType): ResponseEntity<MailTemplate> {
        val document = documentRepository.loadDocument(DocumentType.MAIL.content, mailType.path)
        return ResponseEntity.ok(MailTemplate(mailType, String(document.content)))
    }

    @PutMapping("/api/email-template")
    fun saveMailTemplate(@RequestBody request: MailTemplate): ResponseEntity<Unit> {
        documentRepository.saveDocument(
            DocumentType.MAIL.content, //todo MAIL should be EMAIL
            Document(MediaType.TEXT_HTML_VALUE, request.mailType.path, request.body.toByteArray())
        )
        return ResponseEntity.noContent().build()
    }
}