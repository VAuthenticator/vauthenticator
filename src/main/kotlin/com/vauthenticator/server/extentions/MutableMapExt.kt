package com.vauthenticator.server.extentions

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

fun MutableMap<String, AttributeValue>.valueAsStringFor(key: String): String =
        this[key]?.s()!!

fun MutableMap<String, AttributeValue>.valuesAsListOfStringFor(key: String): List<String> =
        Optional.ofNullable(this[key]).map { it.ss() }.orElse(emptyList())

fun MutableMap<String, AttributeValue>.valueAsBoolFor(key: String): Boolean =
        this[key]?.bool()!!

fun MutableMap<String, AttributeValue>.valueAsIntFor(key: String): Int =
        this[key]?.n()!!.toInt()

fun MutableMap<String, AttributeValue>.valueAsLongFor(key: String): Long =
        this[key]?.n()!!.toLong()

fun MutableMap<String, AttributeValue>.filterEmptyAccountMetadata() =
        if (this.isEmpty()) {
            Optional.empty()
        } else {
            Optional.of(this)
        }