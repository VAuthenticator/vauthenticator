package com.vauthenticator.server.document

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class S3DocumentRepository(
    private val s3Client: S3Client,
    private val buketName: String
) : DocumentRepository {

    private val logger: Logger = LoggerFactory.getLogger(S3DocumentRepository::class.java)
    override fun loadDocument(type: String, path: String): Document {
        val request = GetObjectRequest.builder().bucket(buketName).key(documentKeyFor(type, path)).build()
        val response = s3Client.getObject(request)
        return Document(
            path = path,
            contentType = response.response().contentType(),
            content = response.readAllBytes()
        )

    }


    override fun saveDocument(type: String, document: Document) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(buketName)
                .key(documentKeyFor(type, document.path))
                .contentType(document.contentType)
                .build(),
            RequestBody.fromBytes(document.content)
        )
    }

    private fun documentKeyFor(type: String, path: String) = "$type/$path"
}