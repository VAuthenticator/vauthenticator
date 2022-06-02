package it.valeriovaudi.vauthenticator.extentions

import it.valeriovaudi.vauthenticator.account.Date
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Date.asDynamoAttribute() : AttributeValue = AttributeValue.builder().s(this.formattedDate()).build()