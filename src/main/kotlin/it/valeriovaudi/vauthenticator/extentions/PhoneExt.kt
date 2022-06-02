package it.valeriovaudi.vauthenticator.extentions

import it.valeriovaudi.vauthenticator.account.Phone
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Phone.asDynamoAttribute() : AttributeValue = AttributeValue.builder().s(this.formattedPhone()).build()