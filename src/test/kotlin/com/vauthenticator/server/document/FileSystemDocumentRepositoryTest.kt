package com.vauthenticator.server.document

import com.vauthenticator.server.support.FileUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FileSystemDocumentRepositoryTest {
    private val documentRepository = FileSystemDocumentRepository("src/test/resources/documents")

    @Test
    internal fun `load document from FileSystem`() {
        val document = documentRepository.loadDocument("mail", "templates/welcome.html")

        val expected = FileUtils.loadFileFor("index.html")
        assertEquals(expected, String(document.content))
    }

    @Test
    internal fun `store document on FileSystem`() {
        val documentFile = FileUtils.loadFileFor("index.html")
        val document = Document("text/html", "templates/new-welcome.html", documentFile.toByteArray())

        documentRepository.saveDocument(DocumentType.MAIL.content, document)

        val actual = documentRepository.loadDocument("mail", "templates/new-welcome.html")
        assertEquals(actual, document)
    }
}