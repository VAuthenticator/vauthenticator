package com.vauthenticator.server.mail

import com.vauthenticator.document.repository.Document
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.document.DocumentType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MailEndPoint(private val documentRepository: DocumentRepository) {

    @GetMapping("/api/mail-template/{mailType}")
    fun getMailTemplate(@PathVariable mailType: MailType): ResponseEntity<MailTemplate> {
        val document = documentRepository.loadDocument(DocumentType.MAIL.content, mailType.path)
        return ResponseEntity.ok(MailTemplate(mailType, String(document.content)))
    }

    @PutMapping("/api/mail-template")
    fun saveMailTemplate(@RequestBody request: MailTemplate): ResponseEntity<Unit> {
        documentRepository.saveDocument(
            DocumentType.MAIL.content,
            Document(MediaType.TEXT_HTML_VALUE, request.mailType.path, request.body.toByteArray())
        )
        return ResponseEntity.noContent().build()
    }
}