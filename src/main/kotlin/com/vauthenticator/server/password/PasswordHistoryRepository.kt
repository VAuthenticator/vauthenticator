package com.vauthenticator.server.password

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

interface PasswordHistoryRepository {

    fun store(email: Email, password: Password)
    fun load(email: Email, until: Long): List<Password>

}

class DynamoPasswordHistoryRepository(
    private val clock: Clock,
    private val dynamoPasswordHistoryTableName: String,
    private val dynamoDbClient: DynamoDbClient
) : PasswordHistoryRepository {
    override fun store(email: Email, password: Password) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoPasswordHistoryTableName)
                .item(
                    mapOf(
                        "user_name" to email.content.asDynamoAttribute(),
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

    override fun load(email: Email, until: Long): List<Password> {
        return dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoPasswordHistoryTableName)
                .keyConditionExpression("user_name=:email")
                .expressionAttributeValues(mapOf(":email" to email.content.asDynamoAttribute()))                .build()
        ).items()
            .map { Password(it.valueAsStringFor("password")) }
    }

}
