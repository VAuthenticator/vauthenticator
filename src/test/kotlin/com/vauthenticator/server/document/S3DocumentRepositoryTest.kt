package com.vauthenticator.server.document

import com.vauthenticator.server.mail.MailType
import com.vauthenticator.server.support.DocumentUtils.documentBucket
import com.vauthenticator.server.support.DocumentUtils.initDocumentTests
import com.vauthenticator.server.support.DocumentUtils.s3Client
import com.vauthenticator.server.support.FileUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class S3DocumentRepositoryTest {

    @Test
    internal fun `load document from S3`() {
        initDocumentTests(s3Client)

        val documentRepository = S3DocumentRepository(s3Client, documentBucket)
        val document = documentRepository.loadDocument("mail", "templates/welcome.html")

        val expected = FileUtils.loadFileFor("index.html")
        assertEquals(expected, String(document.content))
    }

    @Test
    internal fun `store document on S3`() {
        initDocumentTests(s3Client)

        val documentFile = FileUtils.loadFileFor("index.html")
        val document = Document("", documentFile.toByteArray())

        val documentRepository = S3DocumentRepository(s3Client, documentBucket)

        documentRepository.saveDocument(MailType.RESET_PASSWORD.name, document)

        val actual =documentRepository.loadDocument("mail", "templates/welcome.html")
        assertEquals(actual, document)
    }
}