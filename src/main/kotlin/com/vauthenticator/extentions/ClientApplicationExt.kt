package com.vauthenticator.extentions

import com.vauthenticator.oauth2.clientapp.Authorities
import com.vauthenticator.oauth2.clientapp.AuthorizedGrantTypes
import com.vauthenticator.oauth2.clientapp.Scopes
import com.vauthenticator.oauth2.clientapp.TokenTimeToLive
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Scopes.asDynamoAttribute(): AttributeValue = AttributeValue.builder().ss(this.content.map { it.content }).build()

fun AuthorizedGrantTypes.asDynamoAttribute(): AttributeValue =
    AttributeValue.builder().ss(this.content.map { it.name }).build()

fun Authorities.asDynamoAttribute(): AttributeValue =
    AttributeValue.builder().ss(this.content.map { it.content }).build()

fun TokenTimeToLive.asDynamoAttribute(): AttributeValue =
    AttributeValue.builder().n(this.content.toString()).build()
