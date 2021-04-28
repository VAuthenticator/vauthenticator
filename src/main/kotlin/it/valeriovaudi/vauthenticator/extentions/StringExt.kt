package it.valeriovaudi.vauthenticator.extentions

import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

val logger = LoggerFactory.getLogger(StringExt::class.java.name)

class StringExt

fun String.toSha256(): String {
    var messageDigest: MessageDigest? = null
    try {
        messageDigest = MessageDigest.getInstance("SHA-256")
    } catch (e: NoSuchAlgorithmException) {
        logger.error(e.message, e)
    }

    return String(messageDigest!!.digest(this.toByteArray()))
}

fun String.asDynamoAttribute(): AttributeValue = AttributeValue.builder().s(this).build()
