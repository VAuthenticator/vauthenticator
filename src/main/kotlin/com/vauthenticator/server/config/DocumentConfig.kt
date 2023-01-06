package com.vauthenticator.server.config

import com.vauthenticator.server.document.S3DocumentRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

@Configuration(proxyBeanMethods = false)
class DocumentConfig {

    @Bean
    fun documentRepository(@Value("\${document.bucket-name}") documentBucketName: String, s3Client: S3Client) =
            S3DocumentRepository(s3Client, documentBucketName)

}

