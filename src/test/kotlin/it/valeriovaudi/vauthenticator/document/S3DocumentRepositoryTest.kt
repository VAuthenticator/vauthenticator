package it.valeriovaudi.vauthenticator.document

import it.valeriovaudi.vauthenticator.support.TestingFixture.loadFileFor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client.builder
import java.net.URI

internal class S3DocumentRepositoryTest {

    @Test
    internal fun `load document from S3`() {

        val s3Client = builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("xxxx", "xxx")))
                .endpointOverride(URI.create("http://localhost:4566"))
                .build()

        val documentRepository = S3DocumentRepository(s3Client, "bucket")
        val document = documentRepository.loadDocument("mail", "templates/sign-up.html")

        val expected = loadFileFor("index.html")
        Assertions.assertEquals(expected, String(document))
    }
}