package com.vauthenticator.document

interface DocumentRepository {

    fun loadDocument(type: String, path: String): ByteArray

}