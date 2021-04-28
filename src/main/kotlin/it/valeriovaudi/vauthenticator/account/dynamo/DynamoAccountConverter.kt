package it.valeriovaudi.vauthenticator.account.dynamo

import it.valeriovaudi.vauthenticator.account.Account
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object DynamoAccountConverter {
    fun fromDynamoToDomain(
        dynamoPayload: MutableMap<String, AttributeValue>,
        authorities: List<String>
    ) = Account(
        accountNonExpired = dynamoPayload["accountNonExpired"]?.bool()!!,
        accountNonLocked = dynamoPayload["accountNonLocked"]?.bool()!!,
        credentialsNonExpired = dynamoPayload["credentialsNonExpired"]?.bool()!!,
        enabled = dynamoPayload["enabled"]?.bool()!!,
        username = dynamoPayload["user_name"]?.s()!!,
        password = dynamoPayload["password"]?.s()!!,
        email = dynamoPayload["email"]?.s()!!,
        emailVerified = dynamoPayload["emailVerified"]?.bool()!!,
        firstName = dynamoPayload["firstName"]?.s()!!,
        lastName = dynamoPayload["lastName"]?.s()!!,
        authorities = authorities
    )

    fun fromDomainToDynamo(account: Account) = mutableMapOf(
        "accountNonExpired" to AttributeValue.builder().bool(account.accountNonExpired).build(),
        "accountNonLocked" to AttributeValue.builder().bool(account.accountNonLocked).build(),
        "credentialsNonExpired" to AttributeValue.builder().bool(account.credentialsNonExpired).build(),
        "enabled" to AttributeValue.builder().bool(account.enabled).build(),
        "user_name" to AttributeValue.builder().s(account.username).build(),
        "password" to AttributeValue.builder().s(account.password).build(),
        "email" to AttributeValue.builder().s(account.email).build(),
        "emailVerified" to AttributeValue.builder().bool(account.emailVerified).build(),
        "firstName" to AttributeValue.builder().s(account.firstName).build(),
        "lastName" to AttributeValue.builder().s(account.lastName).build()
    )
}