package it.valeriovaudi.vauthenticator.extentions

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun MutableMap<String, AttributeValue>.valueAsStringFor(key: String): String =
    this[key]?.s()!!

fun MutableMap<String, AttributeValue>.valueAsBoolFor(key: String): Boolean =
    this[key]?.bool()!!