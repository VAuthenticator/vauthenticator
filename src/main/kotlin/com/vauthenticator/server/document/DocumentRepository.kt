package com.vauthenticator.server.document

interface DocumentRepository {

    fun loadDocument(type: String, path: String): Document
    fun saveDocument(type: String, document: Document)

    fun documentKeyFor(type: String, path: String) = "$type/$path"
}