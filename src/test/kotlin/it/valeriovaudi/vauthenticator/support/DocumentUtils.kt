package it.valeriovaudi.vauthenticator.support

import org.springframework.core.io.ClassPathResource
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI


object DocumentUtils {
    private const val documentBucket: String = "document-bucket"

    val s3Client: S3Client = S3Client.builder()
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("ACCESS_KEY_ID", "SECRET_ACCESS_KEY"))
            ).region(Region.US_EAST_1)
            .endpointOverride(URI.create("http://localhost:4566"))
            .build()

    fun initDocumentTests(client: S3Client) {
        try {
            client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(documentBucket)
                            .build()
            )
        }catch (e : Exception){

        }

        client.putObject(
                PutObjectRequest.builder()
                        .bucket(documentBucket)
                        .key("mail/templates/welcome.html")
                        .build(),
                RequestBody.fromFile(ClassPathResource("index.html").file)
        )
    }

}