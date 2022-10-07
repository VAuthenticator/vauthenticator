package it.valeriovaudi.vauthenticator.document

interface DocumentRepository {

    fun loadDocument(type: String, path: String): ByteArray

}