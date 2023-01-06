package com.vauthenticator.server.account.repository

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.extentions.asDynamoAttribute
import software.amazon.awssdk.services.dynamodb.model.*

object DynamoAccountQueryFactory {

    fun findAllAccountQueryFor(table: String): ScanRequest = ScanRequest.builder().tableName(table).build()

    fun findAccountQueryForUserName(username: String, table: String): GetItemRequest = GetItemRequest.builder()
            .tableName(table)
            .key(
                    mutableMapOf(
                            "user_name" to username.asDynamoAttribute()
                    )
            )
            .build()

    fun storeAccountQueryFor(account: Account, table: String, withUpsert: Boolean = true): PutItemRequest = PutItemRequest.builder()
            .tableName(table)
            .item(DynamoAccountConverter.fromDomainToDynamo(account))
            .let {
                if (withUpsert)
                    it
                else
                    it.conditionExpression("user_name <> :username")
                            .expressionAttributeValues(mutableMapOf(":username" to account.username.asDynamoAttribute()))
            }
            .build()


    fun findAccountRoleByUserNameQueryFor(username: String, table: String): QueryRequest = QueryRequest.builder()
            .tableName(table)
            .keyConditionExpression("user_name = :username")
            .expressionAttributeValues(mutableMapOf(":username" to username.asDynamoAttribute()))
            .build()

    fun storeAccountRoleQueryFor(
            userName: String,
            authority: String,
            table: String
    ): PutItemRequest = PutItemRequest.builder()
            .tableName(table)
            .item(
                    mutableMapOf(
                            "user_name" to userName.asDynamoAttribute(),
                            "role_name" to authority.asDynamoAttribute()
                    )
            )
            .build()

    fun deleteAccountRoleQueryFor(
            username: String,
            roleName: String,
            table: String
    ): DeleteItemRequest = DeleteItemRequest.builder().tableName(table)
            .key(
                    mutableMapOf(
                            "user_name" to username.asDynamoAttribute(),
                            "role_name" to roleName.asDynamoAttribute()
                    )
            )
            .build()

}