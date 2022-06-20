package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.Date
import it.valeriovaudi.vauthenticator.account.Phone
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsBoolFor
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object DynamoAccountConverter {
    fun fromDynamoToDomain(
        dynamoPayload: MutableMap<String, AttributeValue>,
        authorities: List<String>
    ) = Account(
        accountNonExpired = dynamoPayload.valueAsBoolFor("accountNonExpired"),
        accountNonLocked = dynamoPayload.valueAsBoolFor("accountNonLocked"),
        credentialsNonExpired = dynamoPayload.valueAsBoolFor("credentialsNonExpired"),
        enabled = dynamoPayload.valueAsBoolFor("enabled"),
        username = dynamoPayload.valueAsStringFor("user_name"),
        password = dynamoPayload.valueAsStringFor("password"),
        email = dynamoPayload.valueAsStringFor("email"),
        emailVerified = dynamoPayload.valueAsBoolFor("emailVerified"),
        firstName = dynamoPayload.valueAsStringFor("firstName"),
        lastName = dynamoPayload.valueAsStringFor("lastName"),
        authorities = authorities,
        birthDate = Date.isoDateFor(dynamoPayload.valueAsStringFor("birthDate")),
        phone = Phone.phoneFor(dynamoPayload.valueAsStringFor("phone"))
    )

    fun fromDomainToDynamo(account: Account) =
        mutableMapOf(
            "accountNonExpired" to account.accountNonExpired.asDynamoAttribute(),
            "accountNonLocked" to account.accountNonLocked.asDynamoAttribute(),
            "credentialsNonExpired" to account.credentialsNonExpired.asDynamoAttribute(),
            "enabled" to account.enabled.asDynamoAttribute(),
            "user_name" to account.username.asDynamoAttribute(),
            "password" to account.password.asDynamoAttribute(),
            "email" to account.email.asDynamoAttribute(),
            "emailVerified" to account.emailVerified.asDynamoAttribute(),
            "firstName" to account.firstName.asDynamoAttribute(),
            "lastName" to account.lastName.asDynamoAttribute(),
            "birthDate" to account.birthDate.asDynamoAttribute(),
            "phone" to account.phone.asDynamoAttribute()
        )
}