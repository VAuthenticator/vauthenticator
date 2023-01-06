package com.vauthenticator.extentions

import com.vauthenticator.account.Phone
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Phone.asDynamoAttribute() : AttributeValue = AttributeValue.builder().s(this.formattedPhone()).build()