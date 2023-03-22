package com.vauthenticator.server.mail

import com.vauthenticator.server.document.Document
import com.vauthenticator.server.document.DocumentRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class SaveMailTemplateRequest(val mailType: MailType, val body: String)

@RestController
class MailEndPoint(private val documentRepository: DocumentRepository) {

    @PutMapping("/api/mail-template")
    fun saveMailTemplate(@RequestBody request: SaveMailTemplateRequest) : ResponseEntity<Unit> {
        documentRepository.saveDocument(request.mailType.name, Document(MediaType.TEXT_HTML_VALUE, request.body.toByteArray()))
        return ResponseEntity.noContent().build()
    }
}