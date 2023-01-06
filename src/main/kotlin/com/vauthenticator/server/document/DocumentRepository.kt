package com.vauthenticator.server.document

interface DocumentRepository {

    fun loadDocument(type: String, path: String): ByteArray

}