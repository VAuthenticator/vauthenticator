package com.vauthenticator.document

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

class S3DocumentRepository(
        private val s3Client: S3Client,
        private val buketName: String) : DocumentRepository {
    override fun loadDocument(type: String, path: String): ByteArray {
        val request = GetObjectRequest.builder().bucket(buketName).key("$type/$path").build()
        return s3Client.getObject(request)
                .readAllBytes()
    }
}