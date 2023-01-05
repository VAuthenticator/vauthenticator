package it.valeriovaudi.vauthenticator.document

import it.valeriovaudi.vauthenticator.support.DocumentUtils.documentBucket
import it.valeriovaudi.vauthenticator.support.DocumentUtils.initDocumentTests
import it.valeriovaudi.vauthenticator.support.DocumentUtils.s3Client
import it.valeriovaudi.vauthenticator.support.SecurityFixture.loadFileFor
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

        val expected = loadFileFor("index.html")
        assertEquals(expected, String(document))
    }
}