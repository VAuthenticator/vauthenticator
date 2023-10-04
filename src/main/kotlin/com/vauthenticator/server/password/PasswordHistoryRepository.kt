package com.vauthenticator.server.password

import com.vauthenticator.server.AuthenticationUserNameRepository
import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

interface PasswordHistoryRepository {

    fun store(password: Password)
    fun load(): List<Password>

}

class DynamoPasswordHistoryRepository(
    private val authenticationUserNameRepository: AuthenticationUserNameRepository,
    private val clock: Clock,
    private val dynamoPasswordHistoryTableName: String,
    private val dynamoDbClient: DynamoDbClient
) : PasswordHistoryRepository {
    override fun store(password: Password) {
        val userName = authenticationUserNameRepository.getCurrentAuthenticatedUserName()
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoPasswordHistoryTableName)
                .item(
                    mapOf(
                        "user_name" to userName.asDynamoAttribute(),
                        "created_at" to createdAt().asDynamoAttribute(),
                        "password" to password.content.asDynamoAttribute()
                    )
                )
                .build()
        )
    }

    private fun createdAt() =
        LocalDateTime.now(clock)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

    override fun load(): List<Password> {
        val userName = authenticationUserNameRepository.getCurrentAuthenticatedUserName()

        return dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoPasswordHistoryTableName)
                .scanIndexForward(false)
                .keyConditionExpression("user_name=:email")
                .expressionAttributeValues(mapOf(":email" to userName.asDynamoAttribute())).build()
        ).items()
            .map { Password(it.valueAsStringFor("password")) }
    }

}
