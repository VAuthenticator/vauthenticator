package com.vauthenticator.server.document

import com.vauthenticator.server.support.DocumentUtils.documentBucket
import com.vauthenticator.server.support.DocumentUtils.initDocumentTests
import com.vauthenticator.server.support.DocumentUtils.s3Client
import com.vauthenticator.server.support.FileUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class S3DocumentRepositoryTest {

    @Test
    @Disabled
    internal fun `load document from S3`() {
        initDocumentTests(s3Client)

        val documentRepository = S3DocumentRepository(s3Client, documentBucket)
        val document = documentRepository.loadDocument("mail", "templates/welcome.html")

        val expected = FileUtils.loadFileFor("index.html")
        assertEquals(expected, String(document))
    }
}