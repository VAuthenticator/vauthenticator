package it.valeriovaudi.vauthenticator.extentions

import java.io.IOException
import java.io.InputStream

fun InputStream.toByteArray() = try {
    this.readAllBytes()
} catch (e: IOException) {
    ByteArray(0)
}
