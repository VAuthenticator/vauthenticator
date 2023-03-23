package com.vauthenticator.server.document

data class Document(val contentType: String, val path: String, val content: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (contentType != other.contentType) return false
        if (path != other.path) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentType.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}