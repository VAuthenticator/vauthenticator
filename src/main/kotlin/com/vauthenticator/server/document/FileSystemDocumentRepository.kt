package com.vauthenticator.server.document

import java.nio.file.Files
import java.nio.file.Files.probeContentType
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths

class FileSystemDocumentRepository(private val basePath: String) : DocumentRepository {

    override fun loadDocument(type: String, path: String): Document {
        val filePath = Paths.get(basePath, documentKeyFor(type, path))
        val contentType = probeContentType(filePath)
        val content = readAllBytes(filePath)
        return Document(
            path = path,
            contentType = contentType,
            content = content
        )
    }


    override fun saveDocument(type: String, document: Document) {
        val filePath = Paths.get(basePath, documentKeyFor(type, document.path))
        try {
            Files.createFile(filePath)
            Files.write(filePath, document.content)
        } catch (e: Exception) {

        }
    }

}